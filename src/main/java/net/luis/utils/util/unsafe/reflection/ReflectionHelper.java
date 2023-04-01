package net.luis.utils.util.unsafe.reflection;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public class ReflectionHelper {
	
	private static final Logger LOGGER = LogManager.getLogger(ReflectionHelper.class);
	//region Debugging
	private static boolean LOG_EXCEPTIONS = false;
	private static boolean THROW_EXCEPTIONS = false;
	
	public static void enableExceptionLogging() {
		LOG_EXCEPTIONS = true;
	}
	
	public static void disableExceptionLogging() {
		LOG_EXCEPTIONS = false;
	}
	
	public static void enableExceptionThrowing() {
		THROW_EXCEPTIONS = true;
	}
	
	public static void disableExceptionThrowing() {
		THROW_EXCEPTIONS = false;
	}
	//endregion
	
	public static Class<?> getClassForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			//region Exception handling
			LOGGER.warn("The type {} could not be found", className);
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
			//endregion
		}
		return null;
	}
	
	public static boolean hasInterface(Class<?> clazz, @NotNull Class<?> iface) {
		if (iface.isInterface()) {
			return Lists.newArrayList(clazz.getInterfaces()).contains(iface);
		}
		return false;
	}
	
	//region Internal helper methods
	private static List<String> getSimpleNames(Class<?>... classes) {
		return Lists.newArrayList(classes).stream().map(Class::getSimpleName).collect(Collectors.toList());
	}
	
	private static List<String> getSimpleNames(Object... objects) {
		return Lists.newArrayList(objects).stream().map(Object::getClass).map(Class::getSimpleName).collect(Collectors.toList());
	}
	//endregion
	
	//region Constructor methods
	public static <T> Constructor<T> getConstructor(@NotNull Class<T> clazz, Class<?>... parameters) {
		Constructor<T> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(parameters);
		} catch (NoSuchMethodException e) {
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
			LOGGER.warn("Error retrieving the constructor for parameter {} in type {}", getSimpleNames(parameters), clazz.getSimpleName());
		} catch (SecurityException e) {
			LOGGER.warn("No permission to retrieve constructor of type {}", clazz.getSimpleName());
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		}
		return constructor;
	}
	
	public static boolean hasConstructor(Class<?> clazz, Class<?>... parameters) {
		Constructor<?> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(parameters);
		} catch (Exception ignored) {
		
		}
		return constructor != null;
	}
	
	public static <T> T newInstance(@NotNull Constructor<T> constructor, Object... parameters) {
		T instance = null;
		try {
			if (constructor.trySetAccessible()) {
				instance = constructor.newInstance(parameters);
			} else {
				LOGGER.warn("The package of type {} is not accessible for the caller", constructor.getDeclaringClass().getSimpleName());
			}
		} catch (InstantiationException e) {
			LOGGER.warn("Cannot create a new instance of type {} with arguments {}", constructor.getDeclaringClass().getSimpleName(), getSimpleNames(parameters));
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		} catch (IllegalAccessException e) {
			LOGGER.warn("Access to the constructor of type {} not possible", constructor.getDeclaringClass().getSimpleName());
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		} catch (IllegalArgumentException e) {
			LOGGER.warn("The parameters {} do not match those of the constructor in type {}, expected parameters {}", getSimpleNames(parameters), constructor.getClass().getSimpleName(), getSimpleNames(constructor.getParameterTypes()));
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		} catch (InvocationTargetException e) {
			LOGGER.warn("Something went wrong when invoking constructor of type {} with parameters {}", constructor.getDeclaringClass().getSimpleName(), getSimpleNames(parameters));
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}
	
	public static <T> T newInstance(Class<T> clazz, Object... parameters) {
		return newInstance(Objects.requireNonNull(getConstructor(clazz, Lists.newArrayList(parameters).stream().map(Object::getClass).toArray(Class<?>[]::new))), parameters);
	}
	//endregion
	
	//region Method methods
	public static Method getMethod(@NotNull Class<?> clazz, String name, Class<?>... parameters) {
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(name, parameters);
		} catch (NoSuchMethodException e) {
			LOGGER.warn("Error retrieving method for name {} and parameter {} in type {}", name, getSimpleNames(parameters), clazz.getSimpleName());
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		} catch (SecurityException e) {
			LOGGER.warn("No permission to retrieve method with name {} and parameters {} in type {}", name, getSimpleNames(parameters), clazz.getSimpleName());
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		}
		return method;
	}
	
	public static boolean hasMethod(Class<?> clazz, String name, Class<?>... parameters) {
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(name, parameters);
		} catch (Exception ignored) {
		
		}
		return method != null;
	}
	
	public static Object invoke(@NotNull Method method, Object instance, Object... parameters) {
		Object returnValue = null;
		try {
			if (method.trySetAccessible()) {
				returnValue = method.invoke(instance, parameters);
			} else {
				LOGGER.warn("The package of type {} is not accessible for the caller", method.getDeclaringClass().getSimpleName());
			}
		} catch (IllegalAccessException e) {
			LOGGER.warn("Access to the method {} of type {} and parameter {} not possible", method.getName(), method.getDeclaringClass().getSimpleName(), getSimpleNames(parameters));
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		} catch (IllegalArgumentException e) {
			LOGGER.warn("The parameters {} do not match those of the method {} in type {}, expected parameters {}", getSimpleNames(parameters), method.getName(), method.getDeclaringClass().getSimpleName(), getSimpleNames(method.getParameterTypes()));
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		} catch (InvocationTargetException e) {
			LOGGER.warn("Something went wrong when invoking method {} in class {} and parameters {}", method.getName(), method.getDeclaringClass().getSimpleName(), getSimpleNames(parameters));
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		}
		return returnValue;
	}
	
	public static Object invoke(Class<?> clazz, String name, Object instance, Object... parameters) {
		return invoke(Objects.requireNonNull(getMethod(clazz, name, Lists.newArrayList(parameters).stream().map(Object::getClass).toArray(Class<?>[]::new))), instance, parameters);
	}
	//endregion
	
	//region Field methods
	public static Field getField(@NotNull Class<?> clazz, String name) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			LOGGER.warn("Fail to get field {} in class {}", name, clazz.getSimpleName());
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		} catch (SecurityException e) {
			LOGGER.warn("No permission to get field {} in class {}", name, clazz.getSimpleName());
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		}
		return field;
	}
	
	public static boolean hasField(Class<?> clazz, String name) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (Exception ignored) {
		
		}
		return field != null;
	}
	
	public static Object get(@NotNull Field field, Object instance) {
		Object value = null;
		try {
			if (field.trySetAccessible()) {
				value = field.get(instance);
			} else {
				LOGGER.warn("The package of type {} is not accessible for the caller", field.getDeclaringClass().getSimpleName());
			}
		} catch (IllegalArgumentException e) {
			LOGGER.warn("Value of the field {} in class {} cannot be determined", field.getName(), instance.getClass().getSimpleName());
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		} catch (IllegalAccessException e) {
			LOGGER.warn("Access to field {} of type {} not possible", field.getName(), field.getDeclaringClass().getSimpleName());
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		}
		return value;
	}
	
	public static Object get(Class<?> clazz, String name, Object instance) {
		return get(getField(clazz, name), instance);
	}
	
	public static void set(@NotNull Field field, Object instance, Object value) {
		try {
			if (field.trySetAccessible()) {
				field.set(instance, value);
			} else {
				LOGGER.warn("The package of type {} is not accessible for the caller", field.getDeclaringClass().getSimpleName());
			}
		} catch (IllegalArgumentException e) {
			LOGGER.warn("Value of the field with the name {} in type {} could not be set", field.getName(), instance.getClass().getSimpleName());
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		} catch (IllegalAccessException e) {
			LOGGER.warn("Can not access field {} in class {}", field.getName(), field.getDeclaringClass().getSimpleName());
			if (LOG_EXCEPTIONS && !THROW_EXCEPTIONS) {
				LOGGER.error(e);
			} else if (THROW_EXCEPTIONS) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static void set(Class<?> clazz, String name, Object instance, Object value) {
		set(getField(clazz, name), instance, value);
	}
	//endregion
	
}
