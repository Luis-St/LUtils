/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.util.unsafe;

import net.luis.utils.util.unsafe.reflection.ReflectionHelper;
import net.luis.utils.util.unsafe.reflection.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Utility class for stack trace related operations.<br>
 * Provides methods to get the calling class and method.<br>
 *
 * @author Luis-St
 */
public class StackTraceUtils {
	
	/**
	 * Constant for the system property 'unsafe.calls.main'.<br>
	 * If this property is {@code true}, the methods in this class can be called from the {@code main} method.<br>
	 */
	private static final String UNSAFE_CALLS_MAIN = "unsafe.calls.main";
	
	/**
	 * Constant for the system property 'unsafe.offset.base'.<br>
	 * The base offset for the stack trace elements is {@code 3}.<br>
	 * Used internally in {@link StackTraceUtils#getStackTrace(int)} to get the correct stack trace element.<br>
	 */
	private static final String UNSAFE_OFFSET_BASE = "unsafe.offset.base";
	
	/**
	 * Constant for the system property 'unsafe.offset.default'.<br>
	 * <p>
	 *     The default offset for the stack trace elements is {@code 1}.<br>
	 *     The value must be greater than or equal to {@code 1}.<br>
	 *     The value will be added to the base offset to get the correct stack trace element.<br>
	 * </p>
	 */
	private static final String UNSAFE_OFFSET_DEFAULT = "unsafe.offset.default";
	
	//region Calling class
	
	/**
	 * Gets the calling class from the stack trace with an offset of:<br>
	 * <pre>base + default</pre>
	 * <p>
	 *     The base offset will be get from the system property 'unsafe.offset.base'.<br>
	 *     If the property is not set, the default value {@code 3} will be used.<br>
	 * </p>
	 * <p>
	 *     The default offset will be get from the system property 'unsafe.offset.default'.<br>
	 *     If the property is not set, the default value {@code 1} will be used.<br>
	 * </p>
	 * <ul>
	 *     <li>...</li>
	 *     <li>2: {@link StackTraceUtils#getCallingClass()}</li>
	 *     <li>3: The method which requests the calling class</li>
	 *     <li>4: The calling class</li>
	 * </ul>
	 * @return The calling class
	 * @throws IndexOutOfBoundsException If the specified position is out of bounds
	 * @throws IllegalCallerException If called from the {@code main} method and 'unsafe.calls.main' is {@code false}
	 * @see StackTraceUtils#getCallingClass(int)
	 * @see StackTraceUtils#getStackTrace(int)
	 */
	public static Class<?> getCallingClass() {
		return ReflectionHelper.getClassForName(getStackTrace(getDefaultOffset()).getClassName());
	}
	
	/**
	 * Gets the calling class from the stack trace with an offset of:<br>
	 * <pre>base + default + callsBefore</pre>
	 * <p>
	 *     The base offset will be get from the system property 'unsafe.offset.base'.<br>
	 *     If the property is not set, the default value {@code 3} will be used.<br>
	 * </p>
	 * <p>
	 *     The default offset will be get from the system property 'unsafe.offset.default'.<br>
	 *     If the property is not set, the default value {@code 1} will be used.<br>
	 * </p>
	 * <ul>
	 *     <li>...</li>
	 *     <li>2: {@link StackTraceUtils#getCallingClass(int)}</li>
	 *     <li>3: The class with the method which calls this method</li>
	 *     <li>4: The calling class</li>
	 *     <li>...</li>
	 * </ul>
	 * @param callsBefore The number of calls before the method which requests the calling class
	 * @return The calling class at the specified position
	 * @throws IllegalArgumentException If the specified calls before value is less than or equal to 0
	 * @throws IndexOutOfBoundsException If the specified position is out of bounds
	 * @throws IllegalCallerException If called from the {@code main} method and 'unsafe.calls.main' is {@code false}
	 * @see StackTraceUtils#getCallingClass()
	 * @see StackTraceUtils#getStackTrace(int)
	 */
	public static Class<?> getCallingClass(int callsBefore) {
		return ReflectionHelper.getClassForName(getStackTrace(getDefaultOffset() + callsBefore).getClassName());
	}
	//endregion
	
	//region Calling method
	
	/**
	 * Gets the calling method from the stack trace with an offset of:<br>
	 * <pre>base + default</pre>
	 * <p>
	 *     The base offset will be get from the system property 'unsafe.offset.base'.<br>
	 *     If the property is not set, the default value {@code 3} will be used.<br>
	 * </p>
	 * <p>
	 *     The default offset will be get from the system property 'unsafe.offset.default'.<br>
	 *     If the property is not set, the default value {@code 1} will be used.<br>
	 * </p>
	 * <ul>
	 *     <li>...</li>
	 *     <li>2: {@link StackTraceUtils#getCallingMethod()}</li>
	 *     <li>3: The method which requests the calling method</li>
	 *     <li>4: The calling method</li>
	 * </ul>
	 * @return The calling method
	 * @throws IndexOutOfBoundsException If the specified position is out of bounds
	 * @throws IllegalStateException If the calling method could not be identified
	 * @throws IllegalCallerException If called from the {@code main} method and 'unsafe.calls.main' is {@code false}
	 * @see StackTraceUtils#getCallingMethod(int)
	 * @see StackTraceUtils#getStackTrace(int)
	 */
	public static @NotNull Method getCallingMethod() {
		StackTraceElement element = getStackTrace(getDefaultOffset());
		Method method = getCallingMethod(element);
		if (method != null) {
			return method;
		} else {
			throw new IllegalStateException("Could not identify the exact calling method");
		}
	}
	
	/**
	 * Gets the calling method from the stack trace with an offset of:<br>
	 * <pre>base + default + callsBefore</pre>
	 * <p>
	 *     The base offset will be get from the system property 'unsafe.offset.base'.<br>
	 *     If the property is not set, the default value {@code 3} will be used.<br>
	 * </p>
	 * <p>
	 *     The default offset will be get from the system property 'unsafe.offset.default'.<br>
	 *     If the property is not set, the default value {@code 1} will be used.<br>
	 * </p>
	 * <ul>
	 *     <li>...</li>
	 *     <li>2: {@link StackTraceUtils#getCallingMethod(int)}</li>
	 *     <li>3: The method which calls this method</li>
	 *     <li>4: The calling method</li>
	 *     <li>...</li>
	 * </ul>
	 * @param callsBefore The number of calls before the method which requests the calling method
	 * @return The calling method at the specified position
	 * @throws IllegalArgumentException If the specified calls before value is less than or equal to 0
	 * @throws IndexOutOfBoundsException If the specified position is out of bounds
	 * @throws IllegalStateException If the calling method could not be identified
	 * @throws IllegalCallerException If called from the {@code main} method and 'unsafe.calls.main' is {@code false}
	 * @see StackTraceUtils#getCallingMethod()
	 * @see StackTraceUtils#getStackTrace(int)
	 */
	public static @NotNull Method getCallingMethod(int callsBefore) {
		StackTraceElement element = getStackTrace(getDefaultOffset() + callsBefore);
		Method method = getCallingMethod(element);
		if (method != null) {
			return method;
		} else {
			throw new IllegalStateException("Could not identify the exact calling method");
		}
	}
	
	/**
	 * Gets the calling method safe from the stack trace with an offset of:<br>
	 * <pre>base + default</pre>
	 * <p>
	 *     The base offset will be get from the system property 'unsafe.offset.base'.<br>
	 *     If the property is not set, the default value {@code 3} will be used.<br>
	 * </p>
	 * <p>
	 *     The default offset will be get from the system property 'unsafe.offset.default'.<br>
	 *     If the property is not set, the default value {@code 1} will be used.<br>
	 * </p>
	 * <ul>
	 *     <li>...</li>
	 *     <li>2: {@link StackTraceUtils#getCallingMethodSafe()}</li>
	 *     <li>3: The method which requests the calling method</li>
	 *     <li>4: The calling method</li>
	 *     <li>...</li>
	 * </ul>
	 * @return An optional containing the calling method or an empty optional if the calling method could not be identified or an error occurred
	 * @throws IndexOutOfBoundsException If the specified position is out of bounds
	 * @throws IllegalCallerException If called from the {@code main} method and 'unsafe.calls.main' is {@code false}
	 * @see StackTraceUtils#getCallingMethodSafe(int)
	 * @see StackTraceUtils#getStackTrace(int)
	 */
	public static @NotNull Optional<Method> getCallingMethodSafe() {
		try {
			StackTraceElement element = getStackTrace(getDefaultOffset());
			return Optional.ofNullable(getCallingMethod(element));
		} catch (Exception ignored) {
			return Optional.empty();
		}
	}
	
	/**
	 * Gets the calling method safe from the stack trace with an offset of:<br>
	 * <pre>base + default + callsBefore</pre>
	 * <p>
	 *     The base offset will be get from the system property 'unsafe.offset.base'.<br>
	 *     If the property is not set, the default value {@code 3} will be used.<br>
	 * </p>
	 * <p>
	 *     The default offset will be get from the system property 'unsafe.offset.default'.<br>
	 *     If the property is not set, the default value {@code 1} will be used.<br>
	 * </p>
	 * <ul>
	 *     <li>...</li>
	 *     <li>2: {@link StackTraceUtils#getCallingMethodSafe(int)}</li>
	 *     <li>3: The method which calls this method</li>
	 *     <li>4: The calling method</li>
	 *     <li>5: The method which calls the method which requests the calling method</li>
	 *     <li>...</li>
	 * </ul>
	 * @param callsBefore The number of calls before the method which requests the calling method
	 * @return An optional containing the calling method or an empty optional if the calling method could not be identified or an error occurred
	 * @throws IllegalArgumentException If the specified calls before value is less than or equal to 0
	 * @throws IndexOutOfBoundsException If the specified position is out of bounds
	 * @throws IllegalCallerException If called from the {@code main} method and 'unsafe.calls.main' is {@code false}
	 * @see StackTraceUtils#getCallingMethodSafe()
	 * @see StackTraceUtils#getStackTrace(int)
	 */
	public static @NotNull Optional<Method> getCallingMethodSafe(int callsBefore) {
		try {
			StackTraceElement element = getStackTrace(getDefaultOffset() + callsBefore);
			return Optional.ofNullable(getCallingMethod(element));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	//endregion
	
	//region Internal
	
	/**
	 * Gets the default offset for the stack trace elements.<br>
	 * <p>
	 *     The default offset will be get from the system property 'unsafe.offset.default'.<br>
	 *     If the property is not set, the default value {@code 1} will be used.<br>
	 * </p>
	 * @return The default offset
	 * @see StackTraceUtils#getCallingClass()
	 * @see StackTraceUtils#getCallingClass(int)
	 * @see StackTraceUtils#getCallingMethod()
	 * @see StackTraceUtils#getCallingMethod(int)
	 * @see StackTraceUtils#getCallingMethodSafe()
	 * @see StackTraceUtils#getCallingMethodSafe(int)
	 */
	private static int getDefaultOffset() {
		return Integer.parseInt(System.getProperty(UNSAFE_OFFSET_DEFAULT, "1"));
	}
	
	/**
	 * Gets the stack trace element at the specified position.<br>
	 * This method is used internally to get the correct stack trace element.<br>
	 * <p>
	 *     The base offset will be get from the system property 'unsafe.offset.base'.<br>
	 *     If the property is not set, the default value {@code 3} will be used.<br>
	 * </p>
	 * <ul>
	 *     <li>0: {@link Thread#getStackTrace()}</li>
	 *     <li>1: {@link StackTraceUtils#getStackTrace(int)}</li>
	 *     <li>2: The overloading method in this class</li>
	 *     <li>3: The method which requests the calling stack trace element</li>
	 *     <li>4: The calling method or class</li>
	 * </ul>
	 * @param callsBefore The number of calls before the overloading method in this class
	 * @return The stack trace element at the specified position
	 * @throws IllegalArgumentException If the specified calls before value is less than or equal to 0
	 * @throws IndexOutOfBoundsException If the specified position is out of bounds
	 * @throws IllegalCallerException If called from the {@code main} method and 'unsafe.calls.main' is {@code false}
	 */
	private static @NotNull StackTraceElement getStackTrace(int callsBefore) {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		if (0 >= callsBefore) {
			throw new IllegalArgumentException("The calls before value must be greater than 0");
		}
		int base = Integer.parseInt(System.getProperty(UNSAFE_OFFSET_BASE, "3"));
		if (elements.length < base + callsBefore) {
			throw new IndexOutOfBoundsException("The specified position is out of bounds");
		}
		boolean preventMainCalls = !Boolean.parseBoolean(System.getProperty(UNSAFE_CALLS_MAIN, "false"));
		if (elements.length - 1 == base + callsBefore && preventMainCalls) {
			throw new IllegalCallerException("Could not identify the calling stack trace element for the main method");
		}
		return elements[base + callsBefore];
	}
	
	/**
	 * Gets the calling method from the specified stack trace element.<br>
	 * The calling method will be identified by the class name and the method name.<br>
	 * If there are multiple methods with the same name in the same class, the method will not be identified.<br>
	 * @param element The stack trace element
	 * @return The calling method or {@code null} if the method could not be identified
	 * @throws NullPointerException If the specified stack trace element is {@code null}
	 */
	private static @Nullable Method getCallingMethod(@NotNull StackTraceElement element) {
		Objects.requireNonNull(element, "Stack trace element must not be null");
		Class<?> clazz = Objects.requireNonNull(ReflectionHelper.getClassForName(element.getClassName()));
		List<Method> methods = ReflectionUtils.getMethodsForName(clazz, element.getMethodName());
		return methods.size() == 1 ? methods.get(0) : null;
	}
	//endregion
}
