package net.luis.utils.util.reflection;

import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-st
 *
 */

public class ClassPathUtils {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static List<Class<?>> getAllClasses() {
		List<Class<?>> classes = Lists.newArrayList();
		try {
			ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClasses().forEach(classInfo -> {
				try {
					classes.add(Class.forName(classInfo.getName()));
				} catch (ClassNotFoundException e) {
					LOGGER.error("The class for the name {} cannot be found because it does not exist", classInfo.getName());
				} catch (NoClassDefFoundError ignored) {
				
				}
			});
		} catch (Exception e) {
			LOGGER.error("An error occurred while trying to get all classes from the classpath", e);
		}
		return classes;
	}
	
	public static List<Class<?>> getClasses(String packageName) {
	    return getAllClasses().stream().filter(clazz -> clazz.getPackageName().startsWith(packageName)).collect(Collectors.toList());
	}
	
	public static List<Class<?>> getAnnotatedClasses(Class<? extends Annotation> annotation) {
	    return getAllClasses().stream().filter(clazz -> clazz.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static List<Method> getAnnotatedMethods(Class<? extends Annotation> annotation) {
	    return getAllClasses().stream().map(Class::getDeclaredMethods).flatMap(methods -> Lists.newArrayList(methods).stream()).filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static List<Method> getAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotation) {
	    return Lists.newArrayList(clazz.getDeclaredMethods()).stream().filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static List<Field> getAnnotatedFields(Class<? extends Annotation> annotation) {
	    return getAllClasses().stream().map(Class::getDeclaredFields).flatMap(fields -> Lists.newArrayList(fields).stream()).filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static List<Field> getAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotation) {
	    return Lists.newArrayList(clazz.getDeclaredFields()).stream().filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
}
