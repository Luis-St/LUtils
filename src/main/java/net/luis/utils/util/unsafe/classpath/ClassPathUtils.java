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
 * Utility class for classpath related operations.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings("ErrorNotRethrown")
public class ClassPathUtils {
	
	/**
	 * The logger for this class.<br>
	 */
	private static final Logger LOGGER = LogManager.getLogger(ClassPathUtils.class);
	
	//region Classes
	
	/**
	 * Gets all classes from the classpath.<br>
	 * If an error occurs, the error will be logged and an empty list will be returned.<br>
	 * @return A list of all classes from the classpath
	 */
	public static @NotNull List<Class<?>> getAllClasses() {
		List<Class<?>> classes = Lists.newArrayList();
		try {
			classes.addAll(ClassPathHelper.getClasses(true, clazz -> true));
		} catch (Exception | Error e) {
			LOGGER.error("An error occurred while trying to get all classes from the classpath", e);
		}
		return classes;
	}
	
	/**
	 * Gets all classes from the classpath which are related to the project.<br>
	 * Related classes are classes that are in the same package as the caller of this method.<br>
	 * By default, the depth of which the package is used to filter the classes is 3.<br>
	 * <p>
	 *     For example, if the caller of this method is in the package "net.luis.utils.util",<br>
	 * 	   all classes in the packages "net.luis.utils" will be returned,<br>
	 * 	   because the depth is 3 which means that the package will be cut after the third dot.<br>
	 * 	   The depth can be changed by using the system property 'unsafe.package.depth'.<br>
	 * </p>
	 * <p>
	 *     If an error occurs, the error will be logged and an empty list will be returned.<br>
	 * </p>
	 * @return A list of all classes from the classpath which are related to the project
	 * @see ClassPathHelper
	 */
	public static @NotNull List<Class<?>> getProjectClasses() {
		List<Class<?>> classes = Lists.newArrayList();
		try {
			classes.addAll(ClassPathHelper.getClasses(false, clazz -> true));
		} catch (Exception | Error e) {
			LOGGER.error("An error occurred while trying to get all classes from the classpath", e);
		}
		return classes;
	}
	
	/**
	 * Gets all classes from the given package.<br>
	 * If the given package name is null, all classes will be returned.<br>
	 * @param packageName The package name in which the classes should be
	 * @return A list of all classes
	 */
	public static @NotNull List<Class<?>> getClasses(@Nullable String packageName) {
		return getClasses(packageName, Mode.INCLUDE);
	}
	
	/**
	 * Gets all classes from the given package.<br>
	 * The mode determines if the classes should be included or excluded<br>
	 * if the package name of the class starts with the given package name.<br>
	 * <p>
	 *     If the given package name is null, all classes will be returned.<br>
	 *     If the mode is null, the classes will be included.<br>
	 * </p>
	 * @param packageName The package name to filter the classes
	 * @param mode Whether the classes should be included or excluded
	 * @return A list of all classes
	 */
	public static @NotNull List<Class<?>> getClasses(@Nullable String packageName, @Nullable Mode mode) {
		return Lists.newArrayList(ClassPathHelper.getClasses(true, clazz -> {
			return packageName == null || (mode == Mode.EXCLUDE) != clazz.startsWith(packageName);
		}));
	}
	
	/**
	 * Gets all classes from the classpath which are annotated with the given annotation.<br>
	 * @param annotation The annotation which should be present on the classes
	 * @return A list of all classes
	 * @throws NullPointerException If the given annotation is null
	 */
	public static @NotNull List<Class<?>> getAnnotatedClasses(@NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getAllClasses().stream().filter(clazz -> clazz.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets all classes from the given package which are annotated with the given annotation.<br>
	 * @param packageName The package name in which the classes should be
	 * @param annotation The annotation which should be present on the classes
	 * @return A list of all classes
	 * @throws NullPointerException If the given annotation is null
	 */
	public static @NotNull List<Class<?>> getAnnotatedClasses(@Nullable String packageName, @NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getClasses(packageName).stream().filter(clazz -> clazz.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	//endregion
	
	//region Methods
	
	/**
	 * Gets all methods from the classpath which are annotated with the given annotation.<br>
	 * @param annotation The annotation which should be present on the methods
	 * @return A list of all methods
	 * @throws NullPointerException If the given annotation is null
	 */
	public static @NotNull List<Method> getAnnotatedMethods(@NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getAllClasses().stream().map(Class::getDeclaredMethods).flatMap(methods -> Lists.newArrayList(methods).stream()).filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets all methods from the classes in the given package which are annotated with the given annotation.<br>
	 * @param packageName The package name in which the classes should be
	 * @param annotation The annotation which should be present on the methods
	 * @return A list of all methods
	 * @throws NullPointerException If the given annotation is null
	 */
	public static @NotNull List<Method> getAnnotatedMethods(@Nullable String packageName, @NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getClasses(packageName).stream().map(Class::getDeclaredMethods).flatMap(methods -> Lists.newArrayList(methods).stream()).filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets all methods from the given class that are annotated with the given annotation.<br>
	 * @param clazz The class in which the methods should be
	 * @param annotation The annotation which should be present on the methods
	 * @return A list of all methods
	 * @throws NullPointerException If the given class or annotation is null
	 */
	public static @NotNull List<Method> getAnnotatedMethods(@NotNull Class<?> clazz, @NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return Lists.newArrayList(clazz.getDeclaredMethods()).stream().filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets the method with the given name from the given class which is annotated with the given annotation.<br>
	 * @param clazz The class in which the method should be
	 * @param name The name of the method
	 * @param annotation The annotation which should be present on the method
	 * @param parameters The parameters of the method
	 * @return An optional containing the method or an empty optional if the method was not found
	 * @throws NullPointerException If the given class, name or annotation is null
	 */
	public static @NotNull Optional<Method> getAnnotatedMethod(@NotNull Class<?> clazz, @NotNull String name, @NotNull Class<? extends Annotation> annotation, Class<?> @Nullable ... parameters) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(annotation, "Annotation must not be null");
		if (ReflectionHelper.hasMethod(clazz, name, m -> m.isAnnotationPresent(annotation), parameters)) {
			return Optional.of(ReflectionHelper.getMethod(clazz, name, parameters));
		}
		return Optional.empty();
	}
	//endregion
	
	//region Fields
	
	/**
	 * Gets all fields from the classpath which are annotated with the given annotation.<br>
	 * @param annotation The annotation which should be present on the fields
	 * @return A list of all fields
	 * @throws NullPointerException If the given annotation is null
	 */
	public static @NotNull List<Field> getAnnotatedFields(@NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getAllClasses().stream().map(Class::getDeclaredFields).flatMap(fields -> Lists.newArrayList(fields).stream()).filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets all fields from the classes in the given package which are annotated with the given annotation.<br>
	 * @param packageName The package name in which the classes should be
	 * @param annotation The annotation which should be present on the fields
	 * @return A list of all fields
	 * @throws NullPointerException If the given annotation is null
	 */
	public static @NotNull List<Field> getAnnotatedFields(@Nullable String packageName, @NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getClasses(packageName).stream().map(Class::getDeclaredFields).flatMap(fields -> Lists.newArrayList(fields).stream()).filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets all fields from the given class that are annotated with the given annotation.<br>
	 * @param clazz The class in which the fields should be
	 * @param annotation The annotation which should be present on the fields
	 * @return A list of all fields
	 * @throws NullPointerException If the given class or annotation is null
	 */
	public static @NotNull List<Field> getAnnotatedFields(@NotNull Class<?> clazz, @NotNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(clazz, "Class must not be null");
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return Lists.newArrayList(clazz.getDeclaredFields()).stream().filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets the field with the given name from the given class which is annotated with the given annotation.<br>
	 * If the field does not exist or is not annotated with the given annotation, an empty optional will be returned.<br>
	 * @param clazz The class in which the field should be
	 * @param name The name of the field
	 * @param annotation The annotation which should be present on the field
	 * @return An optional containing the field or an empty optional if the field was not found
	 */
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
	
	/**
	 * The mode which determines if the classes should be included or excluded.<br>
	 */
	public enum Mode {
		/**
		 * The object will be included if the underlying predicate returns true.<br>
		 */
		INCLUDE,
		/**
		 * The object will be excluded if the underlying predicate returns true.<br>
		 */
		EXCLUDE
	}
	//endregion
}
