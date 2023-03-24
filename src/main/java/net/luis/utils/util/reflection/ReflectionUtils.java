package net.luis.utils.util.reflection;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Luis-st
 *
 */

public class ReflectionUtils {
	
	public static @NotNull Nullability getNullability(@NotNull AnnotatedType type) {
		if (type.isAnnotationPresent(Nullable.class)) {
			return Nullability.NULLABLE;
		} else if (type.isAnnotationPresent(NotNull.class)) {
			return Nullability.NOT_NULL;
		}
		return Nullability.UNKNOWN;
	}
	
	public static @NotNull String getRawName(@NotNull Method method, String... prefixes) {
		String name = method.getName();
		if (name.startsWith("get")) {
			return name.substring(3);
		} else if (name.startsWith("is")) {
			return name.substring(2);
		} else if (name.startsWith("has")) {
			return name.substring(3);
		}
		for (String prefix : prefixes) {
			if (name.startsWith(prefix)) {
				return name.substring(prefix.length());
			}
		}
		return name;
	}
	
}
