package net.luis.utils.util.unsafe.classpath;

import com.google.common.collect.Lists;
import net.luis.utils.util.unsafe.reflection.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("ErrorNotRethrown")
public class ClassPathUtils {
	
	private static final Logger LOGGER = LogManager.getLogger(ClassPathUtils.class);
	
	//region Classes
	public static @NotNull List<Class<?>> getAllClasses() {
		List<Class<?>> classes = Lists.newArrayList();
		try {
			classes.addAll(ClassPathHelper.getClasses(true, clazz -> true));
		} catch (Exception | Error e) {
			LOGGER.error("An error occurred while trying to get all classes from the classpath", e);
		}
		return classes;
	}
	
	public static @NotNull List<Class<?>> getProjectClasses() {
		List<Class<?>> classes = Lists.newArrayList();
		try {
			classes.addAll(ClassPathHelper.getClasses(false, clazz -> true));
		} catch (Exception | Error e) {
			LOGGER.error("An error occurred while trying to get all classes from the classpath", e);
		}
		return classes;
	}
	
	public static @NotNull List<Class<?>> getClasses(@Nullable String packageName) {
		return getClasses(packageName, Mode.INCLUDE);
	}
	
	public static @NotNull List<Class<?>> getClasses(@Nullable String packageName, @Nullable Mode mode) {
		return Lists.newArrayList(ClassPathHelper.getClasses(true, clazz -> {
			return packageName == null || (mode == Mode.INCLUDE) == clazz.startsWith(packageName);
		}));
	}
	
	public static @NotNull List<Class<?>> getAnnotatedClasses(@NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getAllClasses().stream().filter(clazz -> clazz.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static @NotNull List<Class<?>> getAnnotatedClasses(@Nullable String packageName, @NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getClasses(packageName).stream().filter(clazz -> clazz.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	//endregion
	
	//region Methods
	public static @NotNull List<Method> getAnnotatedMethods(@NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getAllClasses().stream().map(Class::getDeclaredMethods).flatMap(methods -> Lists.newArrayList(methods).stream()).filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static @NotNull List<Method> getAnnotatedMethods(@Nullable String packageName, @NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getClasses(packageName).stream().map(Class::getDeclaredMethods).flatMap(methods -> Lists.newArrayList(methods).stream()).filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static @NotNull List<Method> getAnnotatedMethods(@NotNull Class<?> clazz, @NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return Lists.newArrayList(clazz.getDeclaredMethods()).stream().filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	//endregion
	
	//region Fields
	public static @NotNull List<Field> getAnnotatedFields(@NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getAllClasses().stream().map(Class::getDeclaredFields).flatMap(fields -> Lists.newArrayList(fields).stream()).filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static @NotNull List<Field> getAnnotatedFields(@Nullable String packageName, @NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getClasses(packageName).stream().map(Class::getDeclaredFields).flatMap(fields -> Lists.newArrayList(fields).stream()).filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static @NotNull List<Field> getAnnotatedFields(@NotNull Class<?> clazz, @NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return Lists.newArrayList(clazz.getDeclaredFields()).stream().filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	public static @NotNull Optional<Field> getAnnotatedField(@NotNull Class<?> clazz, @NotNull String name, @NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(annotation, "Annotation must not be null");
		if (ReflectionHelper.hasField(clazz, name, f -> f.isAnnotationPresent(annotation))) {
			return Optional.of(ReflectionHelper.getField(clazz, name));
		}
		return Optional.empty();
	}
	//endregion
	
	//region Internal
	public enum Mode {
		INCLUDE, EXCLUDE
	}
	//endregion
}
