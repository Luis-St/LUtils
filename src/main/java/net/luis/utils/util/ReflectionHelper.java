package net.luis.utils.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

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
			LOGGER.warn("Fail to find class for name " + className, e);
		}
		return null;
	}
	
	public static boolean hasInterface(Class<?> clazz, Class<?> iface) {
		if (iface.isInterface()) {
			return Lists.newArrayList(clazz.getInterfaces()).contains(iface);
		}
		return false;
	}
	
	@Nullable
	public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameters) {
		Constructor<T> constructor = null;
		try {
			constructor = clazz.getDeclaredConstructor(parameters);
		} catch (NoSuchMethodException e) {
			LOGGER.warn("Fail to get constructor for parameters " + Lists.newArrayList(parameters).stream().map(Class::getSimpleName).collect(Collectors.toList()) + " in class " + clazz.getSimpleName(), e);
		} catch (SecurityException e) {
			LOGGER.warn("No permisson to get the constructor of class " + clazz.getSimpleName(), e);
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
		List<String> parameterNames = Lists.newArrayList(parameters).stream().map(Object::getClass).map(Class::getSimpleName).collect(Collectors.toList());
		T instance = null;
		try {
			constructor.setAccessible(true);
			instance = constructor.newInstance(parameters);
		} catch (InstantiationException e) {
			LOGGER.warn("Can not create a new instance of class " + constructor.getDeclaringClass().getSimpleName() + " with arguments " + parameterNames, e);
		} catch (IllegalAccessException e) {
			LOGGER.warn("Can not access the constructor of class " + constructor.getDeclaringClass().getSimpleName(), e);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("The arguments " + parameterNames + " does not match with those of the constructor");
		} catch (InvocationTargetException e) {
			LOGGER.warn("Something went wrong while invoke the constructor of class " + constructor.getDeclaringClass().getSimpleName(), e);
		}
		return instance;
	}
	
	@Nullable
	public static <T> T newInstance(Class<T> clazz, Object... parameters) {
		return newInstance(getConstructor(clazz, Lists.newArrayList(parameters).stream().map(Object::getClass).toArray(Class<?>[]::new)), parameters);
	}
	
	@Nullable
	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameters) {
		List<String> parameterNames = Lists.newArrayList(parameters).stream().map(Object::getClass).map(Class::getSimpleName).collect(Collectors.toList());
		Method method = null;
		try {
			method = clazz.getDeclaredMethod(name, parameters);
		} catch (NoSuchMethodException e) {
			LOGGER.warn("Fail to get method for name " + name + " and parameters " + parameterNames + " in class " + clazz.getSimpleName(), e);
		} catch (SecurityException e) {
			LOGGER.warn("No permisson to get the method with name " + name + " and parameters " + parameterNames + " in class " + clazz.getSimpleName(), e);
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
		List<String> parameterNames = Lists.newArrayList(parameters).stream().map(Object::getClass).map(Class::getSimpleName).collect(Collectors.toList());
		Object returnValue = null;
		try {
			method.setAccessible(true);
			returnValue = method.invoke(instance, parameters);
		} catch (IllegalAccessException e) {
			LOGGER.warn("Can not access the method in class " + method.getDeclaringClass().getSimpleName() + " with name " + method.getName() + " and parameters " + parameterNames, e);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("The parameters " + parameterNames + " does not match with those of the method");
		} catch (InvocationTargetException e) {
			LOGGER.warn("Something went wrong while invoke the method in class " + method.getDeclaringClass().getSimpleName() + " with name " + method.getName() + " and parameters " + parameterNames, e);
		}
		return returnValue;
	}
	
	@Nullable
	public static Object invoke(Class<?> clazz, String name, @Nullable Object instance, Object... parameters) {
		return invoke(getMethod(clazz, name, Lists.newArrayList(parameters).stream().map(Object::getClass).toArray(Class<?>[]::new)), instance, parameters);
	}
	
	@Nullable
	public static Field getField(Class<?> clazz, String name) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			LOGGER.warn("Fail to get field for name " + name + " in class " + clazz.getSimpleName(), e);
		} catch (SecurityException e) {
			LOGGER.warn("No permisson to get the field with name " + name + " in class " + clazz.getSimpleName(), e);
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
			LOGGER.warn("Fail to get value of field with name " + field.getName() + " in class " + instance.getClass().getSimpleName() + " ", e);
		} catch (IllegalAccessException e) {
			LOGGER.warn("Can not access the field in class " + field.getDeclaringClass().getSimpleName() + " with name " + field.getName() + " ", e);
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
			LOGGER.warn("Fail to set value of type " + value.getClass().getSimpleName() + " to field with name " + field.getName() + " in class " + instance.getClass().getSimpleName() + " ", e);
		} catch (IllegalAccessException e) {
			LOGGER.warn("Can not access the field in class " + field.getDeclaringClass().getSimpleName() + " with name " + field.getName() + " ", e);
		}
	}
	
	public static void set(Class<?> clazz, String name, @Nullable Object instance, Object value) {
		set(getField(clazz, name), instance, value);
	}
	
}
