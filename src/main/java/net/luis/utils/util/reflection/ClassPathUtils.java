package net.luis.utils.util.reflection;

import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public class ClassPathUtils {
	
	private static final Logger LOGGER = LogManager.getLogger(ClassPathUtils.class);
	
	public static @NotNull List<Class<?>> getAllClasses() {
		List<Class<?>> classes = Lists.newArrayList();
		try {
			classes.addAll(ClassPathHelper.getClasses());
		} catch (Exception | Error e) {
			LOGGER.error("An error occurred while trying to get all classes from the classpath", e);
		}
		return classes;
	}
	
	public static @NotNull List<Class<?>> getClasses(@NotNull String packageName) {
	    return getAllClasses().stream().filter(clazz -> clazz.getPackageName().startsWith(packageName)).collect(Collectors.toList());
	}
	
	public static @NotNull List<Class<?>> getAnnotatedClasses(@NotNull Class<? extends Annotation> annotation) {
	    return getAllClasses().stream().filter(clazz -> clazz.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static @NotNull List<Method> getAnnotatedMethods(@NotNull Class<? extends Annotation> annotation) {
	    return getAllClasses().stream().map(Class::getDeclaredMethods).flatMap(methods -> Lists.newArrayList(methods).stream()).filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static @NotNull List<Method> getAnnotatedMethods(@NotNull Class<?> clazz, @NotNull Class<? extends Annotation> annotation) {
	    return Lists.newArrayList(clazz.getDeclaredMethods()).stream().filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static @NotNull List<Field> getAnnotatedFields(@NotNull Class<? extends Annotation> annotation) {
	    return getAllClasses().stream().map(Class::getDeclaredFields).flatMap(fields -> Lists.newArrayList(fields).stream()).filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static @NotNull List<Field> getAnnotatedFields(@NotNull Class<?> clazz, @NotNull Class<? extends Annotation> annotation) {
	    return Lists.newArrayList(clazz.getDeclaredFields()).stream().filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
}
