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
	
	//region Calling class
	
	/**
	 * Gets the calling class from the stack trace with an offset of {@code 4}.<br>
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
		return ReflectionHelper.getClassForName(getStackTrace(1).getClassName());
	}
	
	/**
	 * Gets the calling class from the stack trace with an offset of {@code 4 + callsBefore}.<br>
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
		return ReflectionHelper.getClassForName(getStackTrace(1 + callsBefore).getClassName());
	}
	//endregion
	
	//region Calling method
	
	/**
	 * Gets the calling method from the stack trace with an offset of {@code 4}.<br>
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
		StackTraceElement element = getStackTrace(1);
		Method method = getCallingMethod(element);
		if (method != null) {
			return method;
		} else {
			throw new IllegalStateException("Could not identify the exact calling method");
		}
	}
	
	/**
	 * Gets the calling method from the stack trace with an offset of {@code 4 + callsBefore}.<br>
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
		StackTraceElement element = getStackTrace(1 + callsBefore);
		Method method = getCallingMethod(element);
		if (method != null) {
			return method;
		} else {
			throw new IllegalStateException("Could not identify the exact calling method");
		}
	}
	
	/**
	 * Gets the calling method safe from the stack trace with an offset of {@code 4}.<br>
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
			StackTraceElement element = getStackTrace(1);
			return Optional.ofNullable(getCallingMethod(element));
		} catch (Exception ignored) {
			return Optional.empty();
		}
	}
	
	/**
	 * Gets the calling method safe from the stack trace with an offset of {@code 4 + callsBefore}.<br>
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
			StackTraceElement element = getStackTrace(callsBefore);
			return Optional.ofNullable(getCallingMethod(element));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	//endregion
	
	//region Internal
	
	/**
	 * Gets the stack trace element at the specified position.<br>
	 * This method is used internally to get the correct stack trace element.<br>
	 * The base offset is {@code 3} and the default offset of any calling method is {@code 1}.<br>
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
		if (elements.length < 3 + callsBefore) {
			throw new IndexOutOfBoundsException("The specified position is out of bounds");
		}
		boolean preventMainCalls = !Boolean.parseBoolean(System.getProperty(UNSAFE_CALLS_MAIN, "false"));
		if (elements.length - 1 == 3 + callsBefore && preventMainCalls) {
			throw new IllegalCallerException("Could not identify the calling stack trace element for the main method");
		}
		return elements[3 + callsBefore];
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
