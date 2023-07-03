package net.luis.utils.util.unsafe.reflection;

import com.google.common.collect.Lists;
import net.luis.utils.util.Utils;
import net.luis.utils.util.unsafe.Nullability;
import net.luis.utils.util.unsafe.info.ValueInfo;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class ReflectionUtils {
	
	public static @NotNull Nullability getNullability(AnnotatedType type) {
		Objects.requireNonNull(type, "Annotated type must not be null");
		if (type.isAnnotationPresent(Nullable.class)) {
			return Nullability.NULLABLE;
		} else if (type.isAnnotationPresent(NotNull.class)) {
			return Nullability.NOT_NULL;
		}
		return Nullability.UNKNOWN;
	}
	
	public static @NotNull String getRawName(@NotNull Method method, String... prefixes) {
		String name = Objects.requireNonNull(method, "Method must not be null").getName();
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
	
	public static @NotNull List<Method> getMethodsForName(Class<?> clazz, String name) {
		Objects.requireNonNull(clazz, "Class must not be null");
		return Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.getName().equals(name)).toList();
	}
	
	public static Object[] getParameters(Executable executable, Object... values) {
		return getParameters(Objects.requireNonNull(executable, "Executable must not be null"), Utils.mapList(Lists.newArrayList(values), value -> {
			String name = value.getClass().getSimpleName();
			return new ValueInfo(value, name.substring(0, 1).toLowerCase() + name.substring(1), Nullability.UNKNOWN);
		}));
	}
	
	public static Object[] getParameters(Executable executable, List<ValueInfo> values) {
		Parameter[] parameters = Objects.requireNonNull(executable, "Method must not be null").getParameters();
		Object[] arguments = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			arguments[i] = findParameter(parameters[i], values);
		}
		return arguments;
	}
	
	//region Internal
	private static @NotNull Object findParameter(Parameter parameter, List<ValueInfo> values) {
		Executable executable = Objects.requireNonNull(parameter, "Parameter must not be null").getDeclaringExecutable();
		for (ValueInfo value : Objects.requireNonNull(values, "Values must not be null")) {
			Object object = value.value();
			boolean duplicate = Utils.hasDuplicates(object, Utils.mapList(Lists.newArrayList(values), ValueInfo::value));
			if (parameter.getType().isInstance(object) || ClassUtils.primitiveToWrapper(parameter.getType()).isInstance(object)) {
				if (!duplicate) {
					return object;
				} else if (!parameter.isNamePresent()) {
					throw new IllegalArgumentException("The parameter " + parameter.getName() + " of method " + executable.getDeclaringClass().getSimpleName() + "#" + executable.getName() + " is ambiguous");
				} else if (parameter.getName().equalsIgnoreCase(value.name())) {
					return object;
				}
			}
		}
		throw new IllegalArgumentException("No value for parameter " + parameter.getName() + " of method " + executable.getDeclaringClass().getSimpleName() + "#" + executable.getName() + " found");
	}
	//endregion
}
