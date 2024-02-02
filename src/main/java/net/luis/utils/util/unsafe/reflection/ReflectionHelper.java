package net.luis.utils.util.unsafe.reflection;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ArrayUtils.*;

/**
 *
 * @author Luis-St
 *
 */

public class ReflectionHelper {
	
	private static final Logger LOGGER = LogManager.getLogger(ReflectionHelper.class);
	private static final String REFLECTION_EXCEPTIONS_THROW = "reflection.exceptions.throw";
	private static final String REFLECTION_EXCEPTIONS_LOG = "reflection.exceptions.log";
	
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
	
	public static boolean hasInterface(@NotNull Class<?> clazz, @NotNull Class<?> iface) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(iface, "Interface must not be null");
		if (iface.isInterface()) {
			return Lists.newArrayList(clazz.getInterfaces()).contains(iface);
		}
		return false;
	}
	
	//region Constructor methods
	public static <T> Constructor<T> getConstructor(@NotNull Class<T> clazz, Class<?> @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Constructor<T> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(nullToEmpty(parameters));
		} catch (NoSuchMethodException e) {
			LOGGER.warn("Error retrieving the constructor for parameter {} in type '{}'", getSimpleNames(parameters), clazz.getSimpleName());
			handleException(e);
		} catch (SecurityException e) {
			LOGGER.warn("No permission to retrieve constructor of type '{}'", clazz.getSimpleName());
			handleException(e);
		}
		return constructor;
	}
	
	public static boolean hasConstructor(@NotNull Class<?> clazz, Class<?> @Nullable ... parameters) {
		return hasConstructor(clazz, null, parameters);
	}
	
	public static <T> boolean hasConstructor(@NotNull Class<T> clazz, @Nullable Predicate<Constructor<T>> predicate, Class<?> @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Constructor<T> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(nullToEmpty(parameters));
		} catch (Exception ignored) {}
		return constructor != null && (predicate == null || predicate.test(constructor));
	}
	
	public static <T> T newInstance(@NotNull Constructor<T> constructor, Object @Nullable ... parameters) {
		Objects.requireNonNull(constructor, "Constructor must not be null");
		T instance = null;
		try {
			if (constructor.trySetAccessible()) {
				instance = constructor.newInstance(nullToEmpty(parameters));
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
		return instance;
	}
	
	public static <T> T newInstance(@NotNull Class<T> clazz, Object @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Object[] params = nullToEmpty(parameters);
		Constructor<T> constructor = getConstructor(clazz, Lists.newArrayList(params).stream().map(Object::getClass).toArray(Class<?>[]::new));
		if (constructor == null) {
			throw new IllegalStateException("No constructor for parameters " + getSimpleNames(params) + " in type '" + clazz.getSimpleName() + "' found");
		}
		return newInstance(constructor, params);
	}
	//endregion
	
	//region Method methods
	public static Method getMethod(@NotNull Class<?> clazz, @NotNull String name, Class<?> @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(name, nullToEmpty(parameters));
		} catch (NoSuchMethodException e) {
			LOGGER.warn("Error retrieving method for name '{}' and parameter {} in type '{}'", name, getSimpleNames(parameters), clazz.getSimpleName());
			handleException(e);
		} catch (SecurityException e) {
			LOGGER.warn("No permission to retrieve method with name '{}' and parameters {} in type '{}'", name, getSimpleNames(parameters), clazz.getSimpleName());
			handleException(e);
		}
		return method;
	}
	
	public static boolean hasMethod(@NotNull Class<?> clazz, @NotNull String name, Class<?> @Nullable ... parameters) {
		return hasMethod(clazz, name, null, parameters);
	}
	
	public static boolean hasMethod(@NotNull Class<?> clazz, @NotNull String name, @Nullable Predicate<Method> predicate, Class<?> @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(name, nullToEmpty(parameters));
		} catch (Exception ignored) {}
		return method != null && (predicate == null || predicate.test(method));
	}
	
	public static Object invoke(@NotNull Method method, @Nullable Object instance, Object @Nullable ... parameters) {
		Objects.requireNonNull(method, "Method must not be null");
		Object returnValue = null;
		try {
			if (method.trySetAccessible()) {
				returnValue = method.invoke(instance, nullToEmpty(parameters));
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
		return returnValue;
	}
	
	public static Object invoke(@NotNull Class<?> clazz, @NotNull String name, @Nullable Object instance, Object @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Object[] params = nullToEmpty(parameters);
		Method method = getMethod(clazz, name, Lists.newArrayList(params).stream().map(Object::getClass).toArray(Class<?>[]::new));
		if (method == null) {
			throw new IllegalStateException("No method for name '" + name + "' and parameters " + getSimpleNames(params) + " in type '" + clazz.getSimpleName() + "' found");
		}
		return invoke(method, instance, params);
	}
	//endregion
	
	//region Field methods
	public static Field getField(@NotNull Class<?> clazz, @NotNull String name) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			LOGGER.warn("Fail to get field '{}' in type '{}'", name, clazz.getSimpleName());
			handleException(e);
		} catch (SecurityException e) {
			LOGGER.warn("No permission to get field '{}' in type '{}'", name, clazz.getSimpleName());
			handleException(e);
		}
		return field;
	}
	
	public static boolean hasField(@NotNull Class<?> clazz, @NotNull String name) {
		return hasField(clazz, name, null);
	}
	
	public static boolean hasField(@NotNull Class<?> clazz, @NotNull String name, @Nullable Predicate<Field> predicate) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (Exception ignored) {}
		return field != null && (predicate == null || predicate.test(field));
	}
	
	public static Object get(@NotNull Field field, @Nullable Object instance) {
		Objects.requireNonNull(field, "Field must not be null");
		Object value = null;
		try {
			if (field.trySetAccessible()) {
				value = field.get(instance);
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
		return value;
	}
	
	public static Object get(@NotNull Class<?> clazz, @NotNull String name, @Nullable Object instance) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Field field = getField(clazz, name);
		if (field == null) {
			throw new IllegalStateException("No field with the name '" + name + "' in type '" + clazz.getSimpleName() + "' found");
		}
		return get(field, instance);
	}
	
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
	
	public static void set(@NotNull Class<?> clazz, @NotNull String name, @Nullable Object instance, @Nullable Object value) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Field field = getField(clazz, name);
		if (field == null) {
			throw new IllegalStateException("No field with the name '" + name + "' in type '" + clazz.getSimpleName() + "' found");
		}
		set(field, instance, value);
	}
	//region Internal
	private static void handleException(@NotNull Exception e) {
		if (Boolean.parseBoolean(System.getProperty(REFLECTION_EXCEPTIONS_LOG, "false"))) {
			LOGGER.error(e);
		}
		if (Boolean.parseBoolean(System.getProperty(REFLECTION_EXCEPTIONS_THROW, "false"))) {
			throw new RuntimeException(e);
		}
	}
	
	private static @NotNull List<String> getSimpleNames(Class<?> @Nullable ... classes) {
		return Lists.newArrayList(nullToEmpty(classes)).stream().map(Class::getSimpleName).collect(Collectors.toList());
	}
	
	private static @NotNull List<String> getSimpleNames(Object @Nullable ... objects) {
		return Lists.newArrayList(nullToEmpty(objects)).stream().map(Object::getClass).map(Class::getSimpleName).collect(Collectors.toList());
	}
	//endregion
}
