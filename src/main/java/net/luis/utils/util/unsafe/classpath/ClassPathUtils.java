package net.luis.utils.util.unsafe.classpath;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
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
	
	//region Classes
	public static @NotNull List<Class<?>> getClasses(String packageName) {
		Objects.requireNonNull(packageName, "Package name must not be null");
		return getAllClasses().stream().filter(clazz -> clazz.getPackageName().startsWith(packageName)).collect(Collectors.toList());
	}
	
	public static @NotNull List<Class<?>> getAnnotatedClasses(Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getAllClasses().stream().filter(clazz -> clazz.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	//endregion
	
	//region Methods
	public static @NotNull List<Method> getAnnotatedMethods(Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getAllClasses().stream().map(Class::getDeclaredMethods).flatMap(methods -> Lists.newArrayList(methods).stream()).filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static @NotNull List<Method> getAnnotatedMethods(@NotNull Class<?> clazz, Class<? extends Annotation> annotation) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return Lists.newArrayList(clazz.getDeclaredMethods()).stream().filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	//endregion
	
	//region Fields
	public static @NotNull List<Field> getAnnotatedFields(Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getAllClasses().stream().map(Class::getDeclaredFields).flatMap(fields -> Lists.newArrayList(fields).stream()).filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static @NotNull List<Field> getAnnotatedFields(@NotNull Class<?> clazz, Class<? extends Annotation> annotation) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return Lists.newArrayList(clazz.getDeclaredFields()).stream().filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	//endregion
}
