/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.util.unsafe.classpath;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utility class for classpath related operations.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings("ErrorNotRethrown")
public final class ClassPathUtils {
	
	/**
	 * The logger for this class.<br>
	 */
	private static final Logger LOGGER = LogManager.getLogger(ClassPathUtils.class);
	/**
	 * An empty array of fields, used to return an empty array when a class cannot be linked.<br>
	 */
	public static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
	/**
	 * An empty array of methods, used to return an empty array when a class cannot be linked.<br>
	 */
	public static final Method[] EMPTY_METHOD_ARRAY = new Method[0];
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private ClassPathUtils() {}
	
	/**
	 * Gets all classes from the classpath.<br>
	 * If an error occurs, the error will be logged and an empty list will be returned.<br>
	 *
	 * @return A list of all classes from the classpath
	 */
	public static @NonNull List<Class<?>> getAllClasses() {
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
	 * By default, the depth of the package which is used to filter the classes is 3.<br>
	 * <p>
	 *     For example, if the caller of this method is in the package "net.luis.utils.util",<br>
	 * 	   all classes in the packages "net.luis.utils" will be returned,<br>
	 * 	   because the depth is 3 which means that the package will be cut after the third dot.<br>
	 * 	   The depth can be changed by using the system property 'unsafe.package.depth'.
	 * </p>
	 * <p>
	 *     If an error occurs, the error will be logged and an empty list will be returned.
	 * </p>
	 *
	 * @return A list of all classes from the classpath which are related to the project
	 * @see ClassPathHelper
	 */
	public static @NonNull List<Class<?>> getProjectClasses() {
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
	 *
	 * @param packageName The package name in which the classes should be
	 * @return A list of all classes
	 */
	public static @NonNull List<Class<?>> getClasses(@Nullable String packageName) {
		return getClasses(packageName, Mode.INCLUDE);
	}
	
	/**
	 * Gets all classes from the given package.<br>
	 * The mode determines if the classes should be included or excluded<br>
	 * if the package name of the class starts with the given package name.<br>
	 * <p>
	 *     If the given package name is null, all classes will be returned.<br>
	 *     If the mode is null, the classes will be included.
	 * </p>
	 *
	 * @param packageName The package name to filter the classes
	 * @param mode Whether the classes should be included or excluded
	 * @return A list of all classes
	 */
	public static @NonNull List<Class<?>> getClasses(@Nullable String packageName, @Nullable Mode mode) {
		return Lists.newArrayList(ClassPathHelper.getClasses(true, clazz -> {
			return packageName == null || (mode == Mode.EXCLUDE) != clazz.startsWith(packageName);
		}));
	}
	
	/**
	 * Gets all classes from the classpath which are annotated with the given annotation.<br>
	 *
	 * @param annotation The annotation which should be present on the classes
	 * @return A list of all classes
	 * @throws NullPointerException If the given annotation is null
	 */
	public static @NonNull List<Class<?>> getAnnotatedClasses(@NonNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getAllClasses().stream().filter(clazz -> clazz.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets all classes from the given package which are annotated with the given annotation.<br>
	 *
	 * @param packageName The package name in which the classes should be
	 * @param annotation The annotation which should be present on the classes
	 * @return A list of all classes
	 * @throws NullPointerException If the given annotation is null
	 */
	public static @NonNull List<Class<?>> getAnnotatedClasses(@Nullable String packageName, @NonNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getClasses(packageName).stream().filter(clazz -> clazz.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets all methods from the classpath which are annotated with the given annotation.<br>
	 *
	 * @param annotation The annotation which should be present on the methods
	 * @return A list of all methods
	 * @throws NullPointerException If the given annotation is null
	 */
	public static @NonNull List<Method> getAnnotatedMethods(@NonNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getAllClasses().stream().map(ClassPathUtils::getDeclaredMethodsSafely).flatMap(methods -> Lists.newArrayList(methods).stream()).filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets all methods from the classes in the given package which are annotated with the given annotation.<br>
	 *
	 * @param packageName The package name in which the classes should be
	 * @param annotation The annotation which should be present on the methods
	 * @return A list of all methods
	 * @throws NullPointerException If the given annotation is null
	 */
	public static @NonNull List<Method> getAnnotatedMethods(@Nullable String packageName, @NonNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getClasses(packageName).stream().map(ClassPathUtils::getDeclaredMethodsSafely).flatMap(methods -> Lists.newArrayList(methods).stream()).filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets all fields from the classpath which are annotated with the given annotation.<br>
	 *
	 * @param annotation The annotation which should be present on the fields
	 * @return A list of all fields
	 * @throws NullPointerException If the given annotation is null
	 */
	public static @NonNull List<Field> getAnnotatedFields(@NonNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getAllClasses().stream().map(ClassPathUtils::getDeclaredFieldsSafely).flatMap(fields -> Lists.newArrayList(fields).stream()).filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	/**
	 * Gets all fields from the classes in the given package which are annotated with the given annotation.<br>
	 *
	 * @param packageName The package name in which the classes should be
	 * @param annotation The annotation which should be present on the fields
	 * @return A list of all fields
	 * @throws NullPointerException If the given annotation is null
	 */
	public static @NonNull List<Field> getAnnotatedFields(@Nullable String packageName, @NonNull Class<? extends Annotation> annotation) {
		Objects.requireNonNull(annotation, "Annotation must not be null");
		return getClasses(packageName).stream().map(ClassPathUtils::getDeclaredFieldsSafely).flatMap(fields -> Lists.newArrayList(fields).stream()).filter(field -> field.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}
	
	//region Internal
	
	/**
	 * Gets the declared methods of the given class, tolerating classes that cannot be linked.<br>
	 * Reflective access to a class whose referenced types are missing (e.g. an optional dependency) throws a {@link LinkageError},<br>
	 * such classes are skipped by returning an empty array so classpath scanning does not fail.<br>
	 *
	 * @param clazz The class to inspect
	 * @return The declared methods, or an empty array if the class cannot be linked
	 * @throws NullPointerException If the given class is null
	 */
	private static @NonNull Method[] getDeclaredMethodsSafely(@NonNull Class<?> clazz) {
		Objects.requireNonNull(clazz, "Class must not be null");
		
		try {
			return clazz.getDeclaredMethods();
		} catch (LinkageError e) {
			return EMPTY_METHOD_ARRAY;
		}
	}
	
	/**
	 * Gets the declared fields of the given class, tolerating classes that cannot be linked.<br>
	 * Reflective access to a class whose referenced types are missing (e.g. an optional dependency) throws a {@link LinkageError},<br>
	 * such classes are skipped by returning an empty array so classpath scanning does not fail.<br>
	 *
	 * @param clazz The class to inspect
	 * @return The declared fields, or an empty array if the class cannot be linked
	 * @throws NullPointerException If the given class is null
	 */
	private static @NonNull Field[] getDeclaredFieldsSafely(@NonNull Class<?> clazz) {
		Objects.requireNonNull(clazz, "Class must not be null");
		
		try {
			return clazz.getDeclaredFields();
		} catch (LinkageError e) {
			return EMPTY_FIELD_ARRAY;
		}
	}
	
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
