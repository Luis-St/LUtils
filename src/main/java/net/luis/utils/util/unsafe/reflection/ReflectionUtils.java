/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.util.unsafe.reflection;

import com.google.common.collect.Lists;
import net.luis.utils.util.Pair;
import net.luis.utils.util.Utils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ArrayUtils.*;

/**
 * Utility class for reflection-related operations.<br>
 *
 *
 * @author Luis-St
 */
public final class ReflectionUtils {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private ReflectionUtils() {}
	
	//region Methods
	
	/**
	 * Gets the raw name of the given method.<br>
	 * The raw name is the name of the method without prefixes like "get", "set", "is" or "has".<br>
	 *
	 * @param method The method to get the raw name of
	 * @param prefixes Additional prefixes to remove
	 * @return The raw name of the given method or {@link Method#getName()} as fallback
	 * @throws NullPointerException If the given method is null
	 */
	public static @NonNull String getRawName(@NonNull Method method, String @Nullable ... prefixes) {
		String name = Objects.requireNonNull(method, "Method must not be null").getName();
		if (name.startsWith("get")) {
			return name.substring(3);
		} else if (name.startsWith("set")) {
			return name.substring(3);
		} else if (name.startsWith("is")) {
			return name.substring(2);
		} else if (name.startsWith("has")) {
			return name.substring(3);
		}
		for (String prefix : nullToEmpty(prefixes)) {
			if (prefix == null || prefix.isEmpty()) {
				continue;
			}
			if (name.startsWith(prefix)) {
				return name.substring(prefix.length());
			}
		}
		return name;
	}
	
	/**
	 * Gets all methods from the given class that are annotated with the given annotation.<br>
	 *
	 * @param clazz The class in which the methods should be
	 * @param annotation The annotation which should be present on the methods
	 * @return A list of all methods
	 * @throws NullPointerException If the given class or annotation is null
	 */
	public static @NonNull List<Method> getAnnotatedMethods(@NonNull Class<?> clazz, @NonNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return Lists.newArrayList(clazz.getDeclaredMethods()).stream().filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets the method with the given name from the given class which is annotated with the given annotation.<br>
	 *
	 * @param clazz The class in which the method should be
	 * @param name The name of the method
	 * @param annotation The annotation which should be present on the method
	 * @param parameters The parameters of the method
	 * @return An optional containing the method or an empty optional if the method was not found
	 * @throws NullPointerException If the given class, name or annotation is null
	 */
	public static @NonNull Optional<Method> getAnnotatedMethod(@NonNull Class<?> clazz, @NonNull String name, @NonNull Class<? extends Annotation> annotation, Class<?> @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(annotation, "Annotation must not be null");
		if (ReflectionHelper.hasMethod(clazz, name, m -> m.isAnnotationPresent(annotation), parameters)) {
			return ReflectionHelper.getMethod(clazz, name, parameters);
		}
		return Optional.empty();
	}
	
	/**
	 * Gets all methods of the given class for the given name (case-sensitive).<br>
	 * If the given name is null, all methods of the given class will be returned.<br>
	 * The methods will be returned in the order of their parameter count.<br>
	 *
	 * @param clazz The class to get the methods of
	 * @param name The name of the methods to get
	 * @return A list of all methods from the given class for the given name
	 * @throws NullPointerException If the given class is null
	 */
	public static @NonNull List<Method> getMethodsForName(@NonNull Class<?> clazz, @Nullable String name) {
		Objects.requireNonNull(clazz, "Class must not be null");
		return Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.getName().equals(name) || name == null).sorted(Comparator.comparingInt(Method::getParameterCount)).collect(Collectors.toList());
	}
	
	/**
	 * Gets the method with the given name from the given class.<br>
	 * If the given name is null or there are multiple methods with the given name, an empty optional will be returned.<br>
	 *
	 * @param clazz The class to get the method from
	 * @param name The name of the method
	 * @return An optional containing the method or an empty optional if the method was not found
	 * @throws NullPointerException If the given class is null
	 */
	public static @NonNull Optional<Method> getMethodForName(@NonNull Class<?> clazz, @Nullable String name) {
		Objects.requireNonNull(clazz, "Class must not be null");
		List<Method> methods = getMethodsForName(clazz, name);
		if (methods.size() == 1) {
			return Optional.of(methods.getFirst());
		}
		return Optional.empty();
	}
	
	/**
	 * Finds the parameters in the correct order for the given executable.<br>
	 * The parameters will be searched by their type, and if the type is ambiguous, by their name.<br>
	 * <p>
	 *     In the case the compiler has not included the parameter names,<br>
	 *     and the types are ambiguous, an exception will be thrown.<br>
	 *     If the given executable has no parameters, an empty array will be returned.
	 * </p>
	 *
	 * @param executable The executable to find the parameters for
	 * @param values The possible values for the parameters
	 * @return An array of the found parameters in the correct order
	 * @throws NullPointerException If the given executable is null
	 * @throws IllegalArgumentException If the type of the parameter is ambiguous and the name is not present
	 * @throws IllegalArgumentException If no value for a parameter could be found
	 * @see #findParameter(Parameter, List)
	 */
	public static Object @NonNull [] findParameters(@NonNull Executable executable, Object @Nullable ... values) {
		Objects.requireNonNull(executable, "Executable must not be null");
		return findParameters(executable, Utils.mapList(Lists.newArrayList(nullToEmpty(values)), value -> {
			String name = value.getClass().getSimpleName();
			return Pair.of(value, StringUtils.uncapitalize(name));
		}));
	}
	
	/**
	 * Finds the parameters in the correct order for the given executable.<br>
	 * The parameters will be searched by their type, and if the type is ambiguous, by their name.<br>
	 * <p>
	 *     In the case the compiler has not included the parameter names,<br>
	 *     and the types are ambiguous, an exception will be thrown.<br>
	 *     If the given executable has no parameters, an empty array will be returned.
	 * </p>
	 *
	 * @param executable The executable to find the parameters for
	 * @param values The possible values for the parameters
	 * @return An array of the found parameters in the correct order
	 * @throws NullPointerException If the given executable is null
	 * @throws IllegalArgumentException If the type of the parameter is ambiguous and the name is not present
	 * @throws IllegalArgumentException If no value for a parameter could be found
	 * @see #findParameter(Parameter, List)
	 */
	public static Object @NonNull [] findParameters(@NonNull Executable executable, @Nullable List<Pair<Object, String>> values) {
		Parameter[] parameters = Objects.requireNonNull(executable, "Executable must not be null").getParameters();
		Object[] arguments = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			arguments[i] = findParameter(parameters[i], values == null ? Lists.newArrayList() : values);
		}
		return arguments;
	}
	//endregion
	
	//region Fields
	
	/**
	 * Gets all fields from the given class that are annotated with the given annotation.<br>
	 *
	 * @param clazz The class in which the fields should be
	 * @param annotation The annotation which should be present on the fields
	 * @return A list of all fields
	 * @throws NullPointerException If the given class or annotation is null
	 */
	public static @NonNull List<Field> getAnnotatedFields(@NonNull Class<?> clazz, @NonNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return Lists.newArrayList(clazz.getDeclaredFields()).stream().filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets the field with the given name from the given class which is annotated with the given annotation.<br>
	 * If the field does not exist or is not annotated with the given annotation, an empty optional will be returned.<br>
	 *
	 * @param clazz The class in which the field should be
	 * @param name The name of the field
	 * @param annotation The annotation which should be present on the field
	 * @return An optional containing the field or an empty optional if the field was not found
	 * @throws NullPointerException If the given class, name or annotation is null
	 */
	public static @NonNull Optional<Field> getAnnotatedField(@NonNull Class<?> clazz, @NonNull String name, @NonNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(annotation, "Annotation must not be null");
		if (ReflectionHelper.hasField(clazz, name, f -> f.isAnnotationPresent(annotation))) {
			return ReflectionHelper.getField(clazz, name);
		}
		return Optional.empty();
	}
	
	/**
	 * Gets all fields from the given class for the given type.<br>
	 * If the given type is null, an empty list will be returned.<br>
	 *
	 * @param clazz The class to get the fields of
	 * @param type The type of the fields to get
	 * @return A list of all fields from the given class for the given type
	 * @throws NullPointerException If the given class is null
	 */
	public static @NonNull List<Field> getFieldsForType(@NonNull Class<?> clazz, @Nullable Class<?> type) {
		Objects.requireNonNull(clazz, "Class must not be null");
		return Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.getType().equals(type)).collect(Collectors.toList());
	}
	//endregion
	
	//region Internal
	
	/**
	 * Finds the value for the given parameter.<br>
	 * The value will be searched by its type, and if the type is ambiguous, by its name.<br>
	 * An exception will be thrown if no value for the parameter could be found.<br>
	 *
	 * @param parameter The parameter to find the value for
	 * @param values The possible values for the parameter
	 * @return The value found for the given parameter
	 * @throws NullPointerException If the given parameter or values are null
	 * @throws IllegalArgumentException If the type of the parameter is ambiguous and the name is not present
	 * @throws IllegalStateException If no value for the parameter could be found
	 */
	private static @NonNull Object findParameter(@NonNull Parameter parameter, @NonNull List<Pair<Object, String>> values) {
		Objects.requireNonNull(parameter, "Parameter must not be null");
		Objects.requireNonNull(values, "Values must not be null");
		Executable executable = parameter.getDeclaringExecutable();
		for (Pair<Object, String> value : values) {
			Object object = value.getFirst();
			boolean noDuplicates = !Utils.hasDuplicates(object, Utils.mapList(Lists.newArrayList(values), Pair::getFirst));
			if (parameter.getType().isInstance(object) || ClassUtils.primitiveToWrapper(parameter.getType()).isInstance(object)) {
				if (noDuplicates) {
					return object;
				} else if (!parameter.isNamePresent()) {
					throw new IllegalArgumentException("The parameter '" + parameter.getName() + "' of method '" + executable.getDeclaringClass().getSimpleName() + "#" + executable.getName() + "' is ambiguous");
				} else if (parameter.getName().equalsIgnoreCase(value.getSecond())) {
					return object;
				}
			}
		}
		throw new IllegalStateException("No value for parameter '" + parameter.getName() + "' of method '" + executable.getDeclaringClass().getSimpleName() + "#" + executable.getName() + "' found");
	}
	//endregion
}
