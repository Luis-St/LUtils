package net.luis.utils.util.unsafe;

import net.luis.utils.util.unsafe.reflection.ReflectionHelper;
import net.luis.utils.util.unsafe.reflection.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class StackTraceUtils {
	
	private static final String UNSAFE_CALLS_MAIN = "unsafe.calls.main";
	
	public static Class<?> getCallingClass() {
		return ReflectionHelper.getClassForName(getStackTrace(1).getClassName());
	}
	
	public static Class<?> getCallingClass(int callsBefore) {
		return ReflectionHelper.getClassForName(getStackTrace(1 + callsBefore).getClassName());
	}
	
	public static @NotNull Method getCallingMethod() {
		StackTraceElement element = getStackTrace(1);
		Method method = getCallingMethod(element);
		if (method != null) {
			return method;
		} else {
			throw new IllegalStateException("Could not identify the exact calling method");
		}
	}
	
	public static @NotNull Method getCallingMethod(int callsBefore) {
		StackTraceElement element = getStackTrace(1 + callsBefore);
		Method method = getCallingMethod(element);
		if (method != null) {
			return method;
		} else {
			throw new IllegalStateException("Could not identify the exact calling method");
		}
	}
	
	public static @NotNull Optional<Method> getCallingMethodSafe() {
		try {
			StackTraceElement element = getStackTrace(1);
			return Optional.ofNullable(getCallingMethod(element));
		} catch (Exception ignored) {
			return Optional.empty();
		}
	}
	
	public static @NotNull Optional<Method> getCallingMethodSafe(int callsBefore) {
		try {
			StackTraceElement element = getStackTrace(callsBefore);
			return Optional.ofNullable(getCallingMethod(element));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
	//region Internal
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
	
	private static @Nullable Method getCallingMethod(@NotNull StackTraceElement element) {
		Objects.requireNonNull(element, "Stack trace element must not be null");
		Class<?> clazz = Objects.requireNonNull(ReflectionHelper.getClassForName(element.getClassName()));
		List<Method> methods = ReflectionUtils.getMethodsForName(clazz, element.getMethodName());
		return methods.size() == 1 ? methods.get(0) : null;
	}
	//endregion
}
