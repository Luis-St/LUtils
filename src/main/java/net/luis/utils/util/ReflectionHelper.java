package net.luis.utils.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

/**
 *
 * @author Luis-st
 *
 */

public class ReflectionHelper {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Nullable
	public static Class<?> getClassForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			LOGGER.warn("The type {} could not be found", className);
		}
		return null;
	}
	
	public static boolean hasInterface(Class<?> clazz, Class<?> iface) {
		if (iface.isInterface()) {
			return Lists.newArrayList(clazz.getInterfaces()).contains(iface);
		}
		return false;
	}
	
	private static Stream<Class<?>> streamParameters(Class<?>... parameters) {
		return Lists.newArrayList(parameters).stream();
	}
	
	private static Stream<Class<?>> streamObjects(Object... parameters) {
		return Lists.newArrayList(parameters).stream().map(Object::getClass);
	}
	
	@Nullable
	public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameters) {
		Constructor<T> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(parameters);
		} catch (NoSuchMethodException e) {
			LOGGER.warn("Error retrieving the constructor for parameter {} in type {}", streamParameters(parameters).map(Class::getSimpleName).collect(Collectors.toList()), clazz.getSimpleName());
		} catch (SecurityException e) {
			LOGGER.warn("No permission to retrieve the constructor of type {}", clazz.getSimpleName());
		}
		return constructor;
	}
	
	public static boolean hasConstructor(Class<?> clazz, Class<?>... parameters) {
		Constructor<?> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(parameters);
		} catch (Exception e) {
			
		}
		return constructor != null;
	}
	
	@Nullable
	public static <T> T newInstance(Constructor<T> constructor, Object... parameters) {
		List<String> parameterNames = streamObjects(parameters).map(Class::getSimpleName).collect(Collectors.toList());
		T instance = null;
		try {
			constructor.setAccessible(true);
			instance = constructor.newInstance(parameters);
		} catch (InstantiationException e) {
			LOGGER.warn("Cannot create a new instance of type {} with arguments {}", constructor.getDeclaringClass().getSimpleName(), parameterNames);
		} catch (IllegalAccessException e) {
			LOGGER.warn("Access to the constructor of type {} not possible", constructor.getDeclaringClass().getSimpleName());
		} catch (IllegalArgumentException e) {
			LOGGER.warn("The parameters {} do not match those of the constructor", parameterNames);
		} catch (InvocationTargetException e) {
			LOGGER.warn("Something went wrong when invoking the constructor of type {}", constructor.getDeclaringClass().getSimpleName());
		}
		return instance;
	}
	
	@Nullable
	public static <T> T newInstance(Class<T> clazz, Object... parameters) {
		return newInstance(getConstructor(clazz, streamObjects(parameters).toArray(Class<?>[]::new)), parameters);
	}
	
	@Nullable
	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameters) {
		List<String> parameterNames = streamParameters(parameters).map(Object::getClass).map(Class::getSimpleName).collect(Collectors.toList());
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(name, parameters);
		} catch (NoSuchMethodException e) {
			LOGGER.warn("Error retrieving method for name {} and parameter {} in type {}", name, parameterNames, clazz.getSimpleName());
		} catch (SecurityException e) {
			LOGGER.warn("No permission to retrieve the method with name {} and parameters {} in type {}", name, parameterNames, clazz.getSimpleName());
		}
		return method;
	}
	
	public static boolean hasMethod(Class<?> clazz, String name, Class<?>... parameters) {
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(name, parameters);
		} catch (Exception e) {
			
		}
		return method != null;
	}
	
	@Nullable
	public static Object invoke(Method method, @Nullable Object instance, Object... parameters) {
		List<String> parameterNames = streamObjects(parameters).map(Class::getSimpleName).collect(Collectors.toList());
		Object returnValue = null;
		try {
			method.setAccessible(true);
			returnValue = method.invoke(instance, parameters);
		} catch (IllegalAccessException e) {
			LOGGER.warn("Access to the method of type {} with name {} and parameter {} not possible", method.getDeclaringClass().getSimpleName(), method.getName(), parameterNames);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("The parameters {} do not match the parameters of the method", parameterNames);
		} catch (InvocationTargetException e) {
			LOGGER.warn("Something went wrong when invoking the method in class {} with the name {} and parameters {}", method.getDeclaringClass().getSimpleName(), method.getName(), parameterNames);
		}
		return returnValue;
	}
	
	@Nullable
	public static Object invoke(Class<?> clazz, String name, @Nullable Object instance, Object... parameters) {
		return invoke(getMethod(clazz, name, streamObjects(parameters).toArray(Class<?>[]::new)), instance, parameters);
	}
	
	@Nullable
	public static Field getField(Class<?> clazz, String name) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			LOGGER.warn("Fail to get field for name {} in class {}", name, clazz.getSimpleName());
		} catch (SecurityException e) {
			LOGGER.warn("No permisson to get the field with name {} in class {}", name, clazz.getSimpleName());
		}
		return field;
	}
	
	public static boolean hasField(Class<?> clazz, String name) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (Exception e) {
			
		}
		return field != null;
	}
	
	@Nullable
	public static Object get(Field field, @Nullable Object instance) {
		Object value = null;
		try {
			field.setAccessible(true);
			value = field.get(instance);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("Value of the field with the name {} in the class {} cannot be determined", field.getName(), instance.getClass().getSimpleName());
		} catch (IllegalAccessException e) {
			LOGGER.warn("Access to the field of type {} with name {} not possible", field.getDeclaringClass().getSimpleName(), field.getName());
		}
		return value;
	}
	
	@Nullable
	public static Object get(Class<?> clazz, String name, @Nullable Object instance) {
		return get(getField(clazz, name), instance);
	}
	
	public static void set(Field field, @Nullable Object instance, Object value) {
		try {
			field.setAccessible(true);
			field.set(instance, value);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("Value of the field with the name {} in type {} could not be set", field.getName(), instance.getClass().getSimpleName());
		} catch (IllegalAccessException e) {
			LOGGER.warn("Can not access the field in class {} with name {}", field.getDeclaringClass().getSimpleName(), field.getName());
		}
	}
	
	public static void set(Class<?> clazz, String name, @Nullable Object instance, Object value) {
		set(getField(clazz, name), instance, value);
	}
	
}
