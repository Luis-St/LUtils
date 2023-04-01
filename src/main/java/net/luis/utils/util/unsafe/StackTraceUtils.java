package net.luis.utils.util.unsafe;

import net.luis.utils.util.unsafe.reflection.ReflectionHelper;
import net.luis.utils.util.unsafe.reflection.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public class StackTraceUtils {
	
	private static @NotNull StackTraceElement getStackTrace(int callsBefore) {
		return Thread.currentThread().getStackTrace()[4 + callsBefore];
	}
	
	public static Class<?> getCallingClass(int callsBefore) {
		return ReflectionHelper.getClassForName(getStackTrace(callsBefore).getClassName());
	}
	
	public static Class<?> getCallingClass() {
		return getCallingClass(1);
	}
	
	public static @NotNull Optional<Method> getCallingMethodSafe(int callsBefore) {
		StackTraceElement element = getStackTrace(callsBefore);
		Class<?> clazz = Objects.requireNonNull(ReflectionHelper.getClassForName(element.getClassName()));
		List<Method> methods = ReflectionUtils.getMethodsForName(clazz, element.getMethodName());
		if (methods.size() == 1) {
			return Optional.of(methods.get(0));
		} else {
			return Optional.empty();
		}
	}
	
	public static @NotNull Optional<Method> getCallingMethodSafe() {
		return getCallingMethodSafe(1);
	}
	
	public static @NotNull Method getCallingMethod(int callsBefore) {
		StackTraceElement element = getStackTrace(callsBefore);
		Class<?> clazz = Objects.requireNonNull(ReflectionHelper.getClassForName(element.getClassName()));
		List<Method> methods = ReflectionUtils.getMethodsForName(clazz, element.getMethodName());
		if (methods.size() == 1) {
			return methods.get(0);
		} else {
			throw new IllegalStateException("Could not identify the exact calling method");
		}
	}
	
	public static @NotNull Method getCallingMethod() {
		return getCallingMethod(1);
	}
	
	
}
