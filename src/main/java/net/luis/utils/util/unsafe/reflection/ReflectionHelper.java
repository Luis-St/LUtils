package net.luis.utils.util.unsafe.reflection;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.*;

import java.lang.reflect.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public class ReflectionHelper {
	
	private static final Logger LOGGER = LogManager.getLogger(ReflectionHelper.class);
	//region Debugging
	private static boolean logExceptions = false;
	private static boolean throwExceptions = false;
	
	public static void enableExceptionLogging() {
		logExceptions = true;
	}
	
	public static void disableExceptionLogging() {
		logExceptions = false;
	}
	
	public static void enableExceptionThrowing() {
		throwExceptions = true;
	}
	
	public static void disableExceptionThrowing() {
		throwExceptions = false;
	}
	//endregion
	
	public static @UnknownNullability Class<?> getClassForName(@NotNull String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			//region Exception handling
			LOGGER.warn("The type {} could not be found", className);
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
			//endregion
		}
		return null;
	}
	
	public static boolean hasInterface(@NotNull Class<?> clazz, @NotNull Class<?> iface) {
		Objects.requireNonNull(iface, "Interface must not be null");
		if (iface.isInterface()) {
			return Lists.newArrayList(clazz.getInterfaces()).contains(iface);
		}
		return false;
	}
	
	//region Internal helper methods
	private static @NotNull List<String> getSimpleNames(Class<?> @NotNull ... classes) {
		return Lists.newArrayList(classes).stream().map(Class::getSimpleName).collect(Collectors.toList());
	}
	
	private static @NotNull List<String> getSimpleNames(Object @NotNull ... objects) {
		return Lists.newArrayList(objects).stream().map(Object::getClass).map(Class::getSimpleName).collect(Collectors.toList());
	}
	//endregion
	
	//region Constructor methods
	public static <T> @UnknownNullability Constructor<T> getConstructor(@NotNull Class<T> clazz, Class<?> @NotNull ... parameters) {
		Constructor<T> constructor = null;
		try {
			constructor = Objects.requireNonNull(clazz, "Class must not be null").getDeclaredConstructor(parameters);
		} catch (NoSuchMethodException e) {
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
			LOGGER.warn("Error retrieving the constructor for parameter {} in type '{}'", getSimpleNames(parameters), clazz.getSimpleName());
		} catch (SecurityException e) {
			LOGGER.warn("No permission to retrieve constructor of type '{}'", clazz.getSimpleName());
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (NullPointerException e) {
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw e;
			}
		}
		return constructor;
	}
	
	public static boolean hasConstructor(@NotNull Class<?> clazz, Class<?> @NotNull ... parameters) {
		return hasConstructor(clazz, null, parameters);
	}
	
	public static <T> boolean hasConstructor(@NotNull Class<T> clazz, @Nullable Predicate<Constructor<T>> predicate, Class<?> @NotNull ... parameters) {
		Constructor<T> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(parameters);
		} catch (Exception ignored) {
		
		}
		return constructor != null && (predicate == null || predicate.test(constructor));
	}
	
	public static <T> T newInstance(@NotNull Constructor<T> constructor, Object @NotNull ... parameters) {
		T instance = null;
		try {
			Objects.requireNonNull(constructor, "Constructor must not be null");
			if (constructor.trySetAccessible()) {
				instance = constructor.newInstance(parameters);
			} else {
				LOGGER.warn("The package of type '{}' is not accessible for the caller", constructor.getDeclaringClass().getSimpleName());
			}
		} catch (InstantiationException e) {
			LOGGER.warn("Cannot create a new instance of type '{}' with arguments {}", constructor.getDeclaringClass().getSimpleName(), getSimpleNames(parameters));
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (IllegalAccessException e) {
			LOGGER.warn("Access to the constructor of type '{}' not possible", constructor.getDeclaringClass().getSimpleName());
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (IllegalArgumentException e) {
			LOGGER.warn("The parameters {} do not match those of the constructor in type '{}', expected parameters {}", getSimpleNames(parameters), constructor.getDeclaringClass().getSimpleName(), getSimpleNames(constructor.getParameterTypes()));
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (InvocationTargetException e) {
			LOGGER.warn("Something went wrong when invoking constructor of type '{}' with parameters {}", constructor.getDeclaringClass().getSimpleName(), getSimpleNames(parameters));
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (NullPointerException e) {
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw e;
			}
		}
		return instance;
	}
	
	public static <T> T newInstance(@NotNull Class<T> clazz, Object @NotNull ... parameters) {
		return newInstance(Objects.requireNonNull(getConstructor(clazz, Lists.newArrayList(parameters).stream().map(Object::getClass).toArray(Class<?>[]::new))), parameters);
	}
	//endregion
	
	//region Method methods
	public static @UnknownNullability Method getMethod(@NotNull Class<?> clazz, @NotNull String name, Class<?> @NotNull ... parameters) {
		Method method = null;
		try {
			method = Objects.requireNonNull(clazz, "Class must not be null").getDeclaredMethod(name, parameters);
		} catch (NoSuchMethodException e) {
			LOGGER.warn("Error retrieving method for name '{}' and parameter {} in type '{}'", name, getSimpleNames(parameters), clazz.getSimpleName());
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (SecurityException e) {
			LOGGER.warn("No permission to retrieve method with name '{}' and parameters {} in type '{}'", name, getSimpleNames(parameters), clazz.getSimpleName());
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (NullPointerException e) {
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw e;
			}
		}
		return method;
	}
	
	public static boolean hasMethod(@NotNull Class<?> clazz, @NotNull String name, Class<?> @NotNull ... parameters) {
		return hasMethod(clazz, name, null, parameters);
	}
	
	public static boolean hasMethod(@NotNull Class<?> clazz, @NotNull String name, @Nullable Predicate<Method> predicate, Class<?> @NotNull ... parameters) {
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(name, parameters);
		} catch (Exception ignored) {
		
		}
		return method != null && (predicate == null || predicate.test(method));
	}
	
	public static @UnknownNullability Object invoke(@NotNull Method method, @Nullable Object instance, Object @NotNull ... parameters) {
		Object returnValue = null;
		try {
			Objects.requireNonNull(method, "Method must not be null");
			if (method.trySetAccessible()) {
				returnValue = method.invoke(instance, parameters);
			} else {
				LOGGER.warn("The package of type '{}' is not accessible for the caller", method.getDeclaringClass().getSimpleName());
			}
		} catch (IllegalAccessException e) {
			LOGGER.warn("Access to the method '{}' in type '{}' and parameter {} not possible", method.getName(), method.getDeclaringClass().getSimpleName(), getSimpleNames(parameters));
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (IllegalArgumentException e) {
			LOGGER.warn("The parameters {} do not match those of the method '{}' in type '{}', expected parameters {}", getSimpleNames(parameters), method.getName(), method.getDeclaringClass().getSimpleName(), getSimpleNames(method.getParameterTypes()));
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (InvocationTargetException e) {
			LOGGER.warn("Something went wrong when invoking method '{}' in type '{}' and parameters {}", method.getName(), method.getDeclaringClass().getSimpleName(), getSimpleNames(parameters));
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (NullPointerException e) {
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw e;
			}
		}
		return returnValue;
	}
	
	public static @UnknownNullability Object invoke(@NotNull Class<?> clazz, @NotNull String name, @Nullable Object instance, Object @NotNull ... parameters) {
		return invoke(Objects.requireNonNull(getMethod(clazz, name, Lists.newArrayList(parameters).stream().map(Object::getClass).toArray(Class<?>[]::new))), instance, parameters);
	}
	//endregion
	
	//region Field methods
	public static @UnknownNullability Field getField(@NotNull Class<?> clazz, @NotNull String name) {
		Field field = null;
		try {
			field = Objects.requireNonNull(clazz, "Class must not be null").getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			LOGGER.warn("Fail to get field '{}' in type '{}'", name, clazz.getSimpleName());
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (SecurityException e) {
			LOGGER.warn("No permission to get field '{}' in type '{}'", name, clazz.getSimpleName());
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (NullPointerException e) {
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw e;
			}
		}
		return field;
	}
	
	public static boolean hasField(@NotNull Class<?> clazz, @NotNull String name) {
		return hasField(clazz, name, null);
	}
	
	public static boolean hasField(@NotNull Class<?> clazz, @NotNull String name, @Nullable Predicate<Field> predicate) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (Exception ignored) {
		
		}
		return field != null && (predicate == null || predicate.test(field));
	}
	
	public static @UnknownNullability Object get(@NotNull Field field, @Nullable Object instance) {
		Object value = null;
		try {
			Objects.requireNonNull(field, "Field must not be null");
			if (field.trySetAccessible()) {
				value = field.get(instance);
			} else {
				LOGGER.warn("The package of type '{}' is not accessible for the caller", field.getDeclaringClass().getSimpleName());
			}
		} catch (IllegalArgumentException e) {
			LOGGER.warn("Value of the field '{}' in type '{}' cannot be determined", field.getName(), field.getDeclaringClass().getSimpleName());
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (IllegalAccessException e) {
			LOGGER.warn("Access to field '{}' in type '{}' not possible", field.getName(), field.getDeclaringClass().getSimpleName());
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (NullPointerException e) {
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw e;
			}
		}
		return value;
	}
	
	public static @UnknownNullability Object get(@NotNull Class<?> clazz, @NotNull String name, @Nullable Object instance) {
		return get(getField(clazz, name), instance);
	}
	
	public static void set(@NotNull Field field, @Nullable Object instance, @Nullable Object value) {
		try {
			Objects.requireNonNull(field, "Field must not be null");
			if (field.trySetAccessible()) {
				field.set(instance, value);
			} else {
				LOGGER.warn("The package of type '{}' is not accessible for the caller", field.getDeclaringClass().getSimpleName());
			}
		} catch (IllegalArgumentException e) {
			LOGGER.warn("Value of the field with the name '{}' in type '{}' could not be set", field.getName(), field.getDeclaringClass().getSimpleName());
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (IllegalAccessException e) {
			LOGGER.warn("Can not access field '{}' in type '{}'", field.getName(), field.getDeclaringClass().getSimpleName());
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw new RuntimeException(e);
			}
		} catch (NullPointerException e) {
			if (logExceptions) {
				LOGGER.error(e);
			}
			if (throwExceptions) {
				throw e;
			}
		}
	}
	
	public static void set(@NotNull Class<?> clazz, @NotNull String name, @Nullable Object instance, @Nullable Object value) {
		set(getField(clazz, name), instance, value);
	}
	//endregion
}
