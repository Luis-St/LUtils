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
import net.luis.utils.exception.ReflectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ArrayUtils.*;

/**
 * Helper class for reflection-related operations.<br>
 * Provides methods to:
 * <ul>
 *     <li>check if a class has a constructor, method or field</li>
 *     <li>get constructors, methods and fields</li>
 *     <li>invoke constructors, methods and fields</li>
 * </ul>
 *
 * @author Luis-St
 */
public final class ReflectionHelper {
	
	/**
	 * The logger for this class.<br>
	 * Used for logging errors and warnings.<br>
	 */
	private static final Logger LOGGER = LogManager.getLogger(ReflectionHelper.class);
	/**
	 * Constant for the system property 'reflection.exceptions.throw'.<br>
	 * If this property is {@code true}, exceptions will be thrown when an error occurs.<br>
	 */
	private static final String REFLECTION_EXCEPTIONS_THROW = "reflection.exceptions.throw";
	/**
	 * Constant for the system property 'reflection.exceptions.log'.<br>
	 * <p>
	 *     If this property is {@code true}, exceptions will be logged when an error occurs.<br>
	 *     If the property is {@code false}, the logging is not completely disabled<br>
	 *     only exceptions will not be logged.<br>
	 * </p>
	 */
	private static final String REFLECTION_EXCEPTIONS_LOG = "reflection.exceptions.log";
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private ReflectionHelper() {}
	
	/**
	 * Gets the class for the given name.<br>
	 * @param className The name of the class
	 * @return The class for the given name or null if the class could not be found
	 * @throws NullPointerException If the given class name is null
	 * @throws RuntimeException If the class could not be found and throwing exceptions is enabled
	 */
	public static @Nullable Class<?> getClassForName(@NotNull String className) {
		Objects.requireNonNull(className, "Class name must not be null");
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			LOGGER.warn("The type {} could not be found", className);
			handleException(e);
		}
		return null;
	}
	
	/**
	 * Check if the given class is an instance of the given interface.<br>
	 * @param clazz The class to check
	 * @param iface The interface to check
	 * @return True if the given class is an instance of the given interface, otherwise false
	 * @throws NullPointerException If the given class or interface is null
	 */
	public static boolean hasInterface(@NotNull Class<?> clazz, @NotNull Class<?> iface) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(iface, "Interface must not be null");
		if (iface.isInterface()) {
			return contains(clazz.getInterfaces(), iface);
		}
		return false;
	}
	
	//region Constructor methods
	
	/**
	 * Gets the constructor from the given class with the given parameters.<br>
	 * Exceptions will not be logged or thrown by default.<br>
	 * @param clazz The class to get the constructor from
	 * @param parameters The parameters of the constructor
	 * @return The constructor as an optional or an empty optional if the constructor could not be found
	 * @param <T> The type of the class
	 * @throws NullPointerException If the given class is null
	 * @see #handleException(Exception)
	 */
	public static <T> @NotNull Optional<Constructor<T>> getConstructor(@NotNull Class<T> clazz, Class<?> @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		try {
			return Optional.of(clazz.getDeclaredConstructor(nullToEmpty(parameters)));
		} catch (NoSuchMethodException e) {
			LOGGER.warn("Error retrieving the constructor for parameter {} in type '{}'", getSimpleNames(parameters), clazz.getSimpleName());
			handleException(e);
		} catch (SecurityException e) {
			LOGGER.warn("No permission to retrieve constructor of type '{}'", clazz.getSimpleName());
			handleException(e);
		}
		return Optional.empty();
	}
	
	/**
	 * Check if the given class has a constructor with the given parameters.<br>
	 * Exceptions which occur during reflection will be ignored.<br>
	 * @param clazz The class to check
	 * @param parameters The parameters of the constructor
	 * @return True if the given class has a constructor with the given parameters, otherwise false
	 * @throws NullPointerException If the given class is null
	 * @see #hasConstructor(Class, Predicate, Class[])
	 */
	public static boolean hasConstructor(@NotNull Class<?> clazz, Class<?> @Nullable ... parameters) {
		return hasConstructor(clazz, null, parameters);
	}
	
	/**
	 * Check if the given class has a constructor with the given parameters.<br>
	 * Exceptions which occur during reflection will be ignored.<br>
	 * @param clazz The class to check
	 * @param predicate A predicate with an additional condition the constructor must meet
	 * @param parameters The parameters of the constructor
	 * @return True if the given class has a constructor with the given parameters, otherwise false
	 * @param <T> The type of the class
	 * @throws NullPointerException If the given class is null
	 */
	public static <T> boolean hasConstructor(@NotNull Class<T> clazz, @Nullable Predicate<Constructor<T>> predicate, Class<?> @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Constructor<T> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(nullToEmpty(parameters));
		} catch (Exception ignored) {}
		return constructor != null && (predicate == null || predicate.test(constructor));
	}
	
	/**
	 * Creates a new instance from the given constructor and parameters.<br>
	 * Exceptions will not be logged or thrown by default.<br>
	 * @param constructor The constructor to create the instance from
	 * @param parameters The parameters of the constructor
	 * @return The new instance as an optional or an empty optional if the instance could not be created
	 * @param <T> The type of the instance
	 * @throws NullPointerException If the given constructor is null
	 * @see #handleException(Exception)
	 */
	public static <T> @NotNull Optional<T> newInstance(@NotNull Constructor<T> constructor, Object @Nullable ... parameters) {
		Objects.requireNonNull(constructor, "Constructor must not be null");
		try {
			if (constructor.trySetAccessible()) {
				return Optional.of(constructor.newInstance(nullToEmpty(parameters)));
			} else {
				LOGGER.warn("The package of type '{}' is not accessible for the caller", constructor.getDeclaringClass().getSimpleName());
			}
		} catch (InstantiationException e) {
			LOGGER.warn("Cannot create a new instance of type '{}' with parameters {}", constructor.getDeclaringClass().getSimpleName(), getSimpleNames(parameters));
			handleException(e);
		} catch (IllegalAccessException e) {
			LOGGER.warn("Access to the constructor of type '{}' not possible", constructor.getDeclaringClass().getSimpleName());
			handleException(e);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("The parameters {} do not match those of the constructor in type '{}', expected parameters {}", getSimpleNames(parameters), constructor.getDeclaringClass().getSimpleName(), getSimpleNames(constructor.getParameterTypes()));
			handleException(e);
		} catch (InvocationTargetException e) {
			LOGGER.warn("Something went wrong when invoking constructor of type '{}' with parameters {}", constructor.getDeclaringClass().getSimpleName(), getSimpleNames(parameters));
			handleException(e);
		}
		return Optional.empty();
	}
	
	/**
	 * Creates a new instance from the given class and parameters.<br>
	 * @param clazz The class to create the instance from
	 * @param parameters The parameters of the constructor
	 * @return The new instance as an optional or an empty optional if the instance could not be created
	 * @param <T> The type of the instance
	 * @throws NullPointerException If the given class is null
	 * @throws IllegalStateException If no constructor for the given parameters could be found
	 * @see #getConstructor(Class, Class[])
	 * @see #newInstance(Constructor, Object...)
	 */
	public static <T> @NotNull Optional<T> newInstance(@NotNull Class<T> clazz, Object @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Object[] params = nullToEmpty(parameters);
		Optional<Constructor<T>> constructor = getConstructor(clazz, Lists.newArrayList(params).stream().map(Object::getClass).toArray(Class<?>[]::new));
		if (constructor.isEmpty()) {
			throw new IllegalStateException("No constructor for parameters " + getSimpleNames(params) + " in type '" + clazz.getSimpleName() + "' found");
		}
		return newInstance(constructor.orElseThrow(), params);
	}
	//endregion
	
	//region Method methods
	
	/**
	 * Gets the method from the given class with the given name and parameters.<br>
	 * Exceptions will not be logged or thrown by default.<br>
	 * @param clazz The class to get the method from
	 * @param name The name of the method
	 * @param parameters The parameters of the method
	 * @return The method as an optional or an empty optional if the method could not be found
	 * @throws NullPointerException If the given class or name is null
	 * @see #handleException(Exception)
	 */
	public static @NotNull Optional<Method> getMethod(@NotNull Class<?> clazz, @NotNull String name, Class<?> @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		try {
			return Optional.of(clazz.getDeclaredMethod(name, nullToEmpty(parameters)));
		} catch (NoSuchMethodException e) {
			LOGGER.warn("Error retrieving method for name '{}' and parameter {} in type '{}'", name, getSimpleNames(parameters), clazz.getSimpleName());
			handleException(e);
		} catch (SecurityException e) {
			LOGGER.warn("No permission to retrieve method with name '{}' and parameters {} in type '{}'", name, getSimpleNames(parameters), clazz.getSimpleName());
			handleException(e);
		}
		return Optional.empty();
	}
	
	/**
	 * Check if the given class has a method with the given name and parameters.<br>
	 * Exceptions which occur during reflection will be ignored.<br>
	 * @param clazz The class to check for the method
	 * @param name The name of the method
	 * @param parameters The parameters of the method
	 * @return True if the given class has a method with the given name and parameters, otherwise false
	 * @throws NullPointerException If the given class or name is null
	 */
	public static boolean hasMethod(@NotNull Class<?> clazz, @NotNull String name, Class<?> @Nullable ... parameters) {
		return hasMethod(clazz, name, null, parameters);
	}
	
	/**
	 * Check if the given class has a method with the given name and parameters.<br>
	 * Exceptions which occur during reflection will be ignored.<br>
	 * @param clazz The class to check for the method
	 * @param name The name of the method
	 * @param predicate A predicate with an additional condition the method must meet
	 * @param parameters The parameters of the method
	 * @return True if the given class has a method with the given name and parameters, otherwise false
	 * @throws NullPointerException If the given class or name is null
	 */
	public static boolean hasMethod(@NotNull Class<?> clazz, @NotNull String name, @Nullable Predicate<Method> predicate, Class<?> @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(name, nullToEmpty(parameters));
		} catch (Exception ignored) {}
		return method != null && (predicate == null || predicate.test(method));
	}
	
	/**
	 * Invokes the given method with the given parameters on the given instance.<br>
	 * <p>
	 *     If the invocation is successful, the return value will be returned.<br>
	 *     If the invocation fails, the return value will be null.<br>
	 * </p>
	 * <p>
	 *     Exceptions will not be logged or thrown by default.<br>
	 * </p>
	 * @param method The method to invoke
	 * @param instance The instance to invoke the method on
	 * @param parameters The parameters of the method
	 * @return The return value of the method as an optional or an empty optional if the invocation failed
	 * @throws NullPointerException If the given method is null
	 * @see #handleException(Exception)
	 */
	public static @NotNull Optional<Object> invoke(@NotNull Method method, @Nullable Object instance, Object @Nullable ... parameters) {
		Objects.requireNonNull(method, "Method must not be null");
		try {
			if (method.trySetAccessible()) {
				return Optional.ofNullable(method.invoke(instance, nullToEmpty(parameters)));
			} else {
				LOGGER.warn("The package of type '{}' is not accessible for the caller", method.getDeclaringClass().getSimpleName());
			}
		} catch (IllegalAccessException e) {
			LOGGER.warn("Access to the method '{}' in type '{}' and parameter {} not possible", method.getName(), method.getDeclaringClass().getSimpleName(), getSimpleNames(parameters));
			handleException(e);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("The parameters {} do not match those of the method '{}' in type '{}', expected parameters {}", getSimpleNames(parameters), method.getName(), method.getDeclaringClass().getSimpleName(), getSimpleNames(method.getParameterTypes()));
			handleException(e);
		} catch (InvocationTargetException e) {
			LOGGER.warn("Something went wrong when invoking method '{}' in type '{}' and parameters {}", method.getName(), method.getDeclaringClass().getSimpleName(), getSimpleNames(parameters));
			handleException(e);
		}
		return Optional.empty();
	}
	
	/**
	 * Invokes the method with the given name and parameters on the given instance.<br>
	 * <p>
	 *     If the invocation is successful, the return value will be returned.<br>
	 *     If the invocation fails, the return value will be null.<br>
	 * </p>
	 * @param clazz The class which contains the method
	 * @param name The name of the method
	 * @param instance The instance to invoke the method on
	 * @param parameters The parameters of the method
	 * @return The return value of the method as an optional or an empty optional if the invocation failed
	 * @throws NullPointerException If the given class or name is null
	 * @throws IllegalStateException If no method for the given name and parameters could be found
	 * @see #getMethod(Class, String, Class[])
	 * @see #invoke(Method, Object, Object...)
	 */
	public static @NotNull Optional<Object> invoke(@NotNull Class<?> clazz, @NotNull String name, @Nullable Object instance, Object @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Object[] params = nullToEmpty(parameters);
		Optional<Method> method = getMethod(clazz, name, Lists.newArrayList(params).stream().map(Object::getClass).toArray(Class<?>[]::new));
		if (method.isEmpty()) {
			throw new IllegalStateException("No method for name '" + name + "' and parameters " + getSimpleNames(params) + " in type '" + clazz.getSimpleName() + "' found");
		}
		return invoke(method.orElseThrow(), instance, params);
	}
	//endregion
	
	//region Field methods
	
	/**
	 * Gets the field from the given class with the given name.<br>
	 * Exceptions will not be logged or thrown by default.<br>
	 * @param clazz The class to get the field from
	 * @param name The name of the field
	 * @return The field as an optional or an empty optional if the field could not be found
	 * @throws NullPointerException If the given class or name is null
	 * @see #handleException(Exception)
	 */
	public static @NotNull Optional<Field> getField(@NotNull Class<?> clazz, @NotNull String name) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		try {
			return Optional.of(clazz.getDeclaredField(name));
		} catch (NoSuchFieldException e) {
			LOGGER.warn("Fail to get field '{}' in type '{}'", name, clazz.getSimpleName());
			handleException(e);
		} catch (SecurityException e) {
			LOGGER.warn("No permission to get field '{}' in type '{}'", name, clazz.getSimpleName());
			handleException(e);
		}
		return Optional.empty();
	}
	
	/**
	 * Check if the given class has a field with the given name.<br>
	 * Exceptions which occur during reflection will be ignored.<br>
	 * @param clazz The class to check for the field
	 * @param name The name of the field
	 * @return True if the given class has a field with the given name, otherwise false
	 * @throws NullPointerException If the given class or name is null
	 */
	public static boolean hasField(@NotNull Class<?> clazz, @NotNull String name) {
		return hasField(clazz, name, null);
	}
	
	/**
	 * Check if the given class has a field with the given name.<br>
	 * Exceptions which occur during reflection will be ignored.<br>
	 * @param clazz The class to check for the field
	 * @param name The name of the field
	 * @param predicate A predicate with an additional condition the field must meet
	 * @return True if the given class has a field with the given name, otherwise false
	 * @throws NullPointerException If the given class or name is null
	 */
	public static boolean hasField(@NotNull Class<?> clazz, @NotNull String name, @Nullable Predicate<Field> predicate) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (Exception ignored) {}
		return field != null && (predicate == null || predicate.test(field));
	}
	
	/**
	 * Gets the value of the given field from the given instance.<br>
	 * Exceptions will not be logged or thrown by default.<br>
	 * @param field The field to get the value from
	 * @param instance The instance to get the value from
	 * @return The value of the field as an optional or an empty optional if the value could not be determined
	 * @throws NullPointerException If the given field is null
	 * @see #handleException(Exception)
	 */
	public static @NotNull Optional<Object> get(@NotNull Field field, @Nullable Object instance) {
		Objects.requireNonNull(field, "Field must not be null");
		try {
			if (field.trySetAccessible()) {
				return Optional.ofNullable(field.get(instance));
			} else {
				LOGGER.warn("The package of type '{}' is not accessible for the caller", field.getDeclaringClass().getSimpleName());
			}
		} catch (IllegalArgumentException e) {
			LOGGER.warn("Value of the field '{}' in type '{}' cannot be determined", field.getName(), field.getDeclaringClass().getSimpleName());
			handleException(e);
		} catch (IllegalAccessException e) {
			LOGGER.warn("Access to field '{}' in type '{}' not possible", field.getName(), field.getDeclaringClass().getSimpleName());
			handleException(e);
		}
		return Optional.empty();
	}
	
	/**
	 * Gets the value from the field with the given name from the given class and instance.<br>
	 * @param clazz The class to get the field from
	 * @param name The name of the field
	 * @param instance The instance to get the value from
	 * @return The value of the field as an optional or an empty optional if the value could not be determined
	 * @throws NullPointerException If the given class or name is null
	 * @throws IllegalStateException If no field with the given name could be found
	 * @see #getField(Class, String)
	 * @see #get(Field, Object)
	 */
	public static @NotNull Optional<Object> get(@NotNull Class<?> clazz, @NotNull String name, @Nullable Object instance) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Optional<Field> field = getField(clazz, name);
		if (field.isEmpty()) { // Only thrown when 'reflection.exceptions.throw' is false
			throw new IllegalStateException("No field with the name '" + name + "' in type '" + clazz.getSimpleName() + "' found");
		}
		return get(field.orElseThrow(), instance);
	}
	
	/**
	 * Sets the value of the given field in the given instance to the given value.<br>
	 * Exceptions will not be logged or thrown by default.<br>
	 * @param field The field to set the value to
	 * @param instance The instance to set the value to
	 * @param value The value to set
	 * @throws NullPointerException If the given field is null
	 * @see #handleException(Exception)
	 */
	public static void set(@NotNull Field field, @Nullable Object instance, @Nullable Object value) {
		Objects.requireNonNull(field, "Field must not be null");
		try {
			if (field.trySetAccessible()) {
				field.set(instance, value);
			} else {
				LOGGER.warn("The package of type '{}' is not accessible for the caller", field.getDeclaringClass().getSimpleName());
			}
		} catch (IllegalArgumentException e) {
			LOGGER.warn("Value of the field with the name '{}' in type '{}' could not be set", field.getName(), field.getDeclaringClass().getSimpleName());
			handleException(e);
		} catch (IllegalAccessException e) {
			LOGGER.warn("Can not access field '{}' in type '{}'", field.getName(), field.getDeclaringClass().getSimpleName());
			handleException(e);
		}
	}
	
	/**
	 * Sets the value of the field with the given name in the given class and instance to the given value.<br>
	 * @param clazz The class to set the field in
	 * @param name The name of the field
	 * @param instance The instance to set the value to
	 * @param value The value to set
	 * @throws NullPointerException If the given class or name is null
	 * @throws IllegalStateException If no field with the given name could be found
	 * @see #getField(Class, String)
	 * @see #set(Field, Object, Object)
	 */
	public static void set(@NotNull Class<?> clazz, @NotNull String name, @Nullable Object instance, @Nullable Object value) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Optional<Field> field = getField(clazz, name);
		if (field.isEmpty()) { // Only thrown when 'reflection.exceptions.throw' is false
			throw new IllegalStateException("No field with the name '" + name + "' in type '" + clazz.getSimpleName() + "' found");
		}
		set(field.orElseThrow(), instance, value);
	}
	//endregion
	
	//region Internal
	
	/**
	 * Handles the given exception.<br>
	 * If the system property 'reflection.exceptions.log' is {@code true}, the exception will be logged.<br>
	 * If the system property 'reflection.exceptions.throw' is {@code true}, the exception will be thrown.<br>
	 * @param e The exception to handle
	 */
	private static void handleException(@NotNull Exception e) {
		if (Boolean.parseBoolean(System.getProperty(REFLECTION_EXCEPTIONS_LOG, "false"))) {
			LOGGER.error(e);
		}
		if (Boolean.parseBoolean(System.getProperty(REFLECTION_EXCEPTIONS_THROW, "false"))) {
			throw new ReflectionException(e);
		}
	}
	
	/**
	 * Maps the given classes to their simple names.<br>
	 * If the given array is null or empty, an empty list will be returned.<br>
	 * @param classes The list of classes
	 * @return A list containing the simple names
	 */
	private static @NotNull List<String> getSimpleNames(Class<?> @Nullable ... classes) {
		if (classes == null || classes.length == 0) {
			return Lists.newArrayList();
		}
		return Arrays.stream(classes).map(Class::getSimpleName).collect(Collectors.toList());
	}
	
	/**
	 * Maps the given objects to their simple names by converting them to their classes first.<br>
	 * If the given array is null or empty, an empty list will be returned.<br>
	 * @param objects The list of objects
	 * @return A list containing the simple names
	 */
	private static @NotNull List<String> getSimpleNames(Object @Nullable ... objects) {
		if (objects == null || objects.length == 0) {
			return Lists.newArrayList();
		}
		return Arrays.stream(objects).map(Object::getClass).map(Class::getSimpleName).collect(Collectors.toList());
	}
	//endregion
}
