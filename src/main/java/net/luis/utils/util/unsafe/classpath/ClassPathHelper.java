/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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
import net.luis.utils.util.unsafe.StackTraceUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Internal helper class to get all classes from the classpath.<br>
 *
 * @see ClassPathUtils
 *
 * @author Luis-St
 */
@SuppressWarnings("ErrorNotRethrown")
final class ClassPathHelper {
	
	/**
	 * Constant for the system property 'unsafe.package.depth'.<br>
	 * <p>
	 *     The property is used in {@link #getClasses(boolean, Predicate)} to filter the classes<br>
	 *     from the classpath by the package depth of the calling class.
	 * </p>
	 * <p>
	 *     The default value is {@code 2}.
	 * </p>
	 */
	private static final String UNSAFE_PACKAGE_DEPTH = "unsafe.package.depth";
	
	/**
	 * Constant for the system property 'unsafe.classes.ignored'.<br>
	 * <p>
	 *     The property is used in to filter classes which should be fully ignored.<br>
	 *     All classes which are listed will not be loaded.
	 * </p>
	 * <p>
	 *     The value of the property is a comma or semicolon separated list of fully qualified class names.<br>
	 *     If you want to ignore all classes from a package, you can use a wildcard at the end of the package name.<br>
	 *     Wildcard entries are only allowed at the end of the package name, the entry must end with {@code .*}.
	 * </p>
	 * <p>
	 *     The default value is an empty string.
	 * </p>
	 */
	private static final String UNSAFE_CLASSES_IGNORED = "unsafe.classes.ignored";
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private ClassPathHelper() {}
	
	/**
	 * Gets all classes from the classpath.<br>
	 * The classes will be filtered by the given condition.<br>
	 * <p>
	 *     If dependency classes should not be included, an 'and' clause is added to the condition.<br>
	 *     The clause checks uses the package name of the calling class to determine if the class is a dependency or not.
	 * </p>
	 * <p>
	 *     For example, if the caller of this method is in the package "net.luis.utils.util",<br>
	 * 	   all classes in the packages "net.luis.utils" will be returned,<br>
	 * 	   because the depth is 3 which means that the package will be cut after the third dot.<br>
	 * 	   The depth can be changed by using the system property 'unsafe.package.depth'.
	 * </p>
	 * <p>
	 *     Any exceptions which will be thrown while trying to get the classes will be ignored.
	 * </p>
	 *
	 * @param includeDependencies Whether to include dependency classes or not
	 * @param condition A condition to filter the classes
	 * @return A list of all classes
	 */
	static @NotNull List<Class<?>> getClasses(boolean includeDependencies, @NotNull Predicate<String> condition) {
		List<Class<?>> classes = Lists.newArrayList();
		for (File file : getClassPathFiles()) {
			if (file.isDirectory()) {
				classes.addAll(getClassesFromDirectory(file, condition));
			} else {
				String caller = StackTraceUtils.getCallingClass(1).getPackageName();
				int packageDepth = Integer.parseInt(System.getProperty(UNSAFE_PACKAGE_DEPTH, "2"));
				String pack = Arrays.stream(caller.split("\\.")).limit(packageDepth).collect(Collectors.joining("."));
				classes.addAll(getClassesFromJar(file, includeDependencies ? condition : condition.and(clazz -> clazz.startsWith(pack))));
			}
		}
		return classes;
	}
	
	//region Internal helper methods
	
	/**
	 * Gets a condition to filter the classes which should be ignored.<br>
	 * The condition is based on the system property 'unsafe.classes.ignored'.<br>
	 * <p>
	 *     The value of the property is a comma or semicolon separated list of fully qualified class names.<br>
	 *     If you want to ignore all classes from a package, you can use a wildcard at the end of the package name.<br>
	 *     Wildcard entries are only allowed at the end of the package name, the entry must end with {@code .*}.
	 * </p>
	 * <p>
	 *     If the property is not set, the condition will always return {@code false}.<br>
	 *     This means that no classes will be ignored.
	 * </p>
	 *
	 * @return A condition to filter the classes which should be ignored
	 */
	private static @NotNull Predicate<String> getIgnoreCondition() {
		String property = System.getProperty(UNSAFE_CLASSES_IGNORED, "");
		List<String> classes = Arrays.stream(property.split("[,;]")).map(String::trim).filter(s -> !s.isEmpty()).toList();
		if (classes.isEmpty()) {
			return clazz -> false;
		}
		List<String> ignored = classes.stream().filter(s -> !s.endsWith(".*")).toList();
		List<String> patterns = ignored.stream().filter(s -> s.endsWith(".*")).map(s -> s.substring(0, s.length() - 1)).toList();
		return clazz -> {
			if (ignored.contains(clazz)) {
				return true;
			}
			for (String pattern : patterns) {
				if (clazz.startsWith(pattern.substring(0, pattern.length() - 2))) {
					return true;
				}
			}
			return false;
		};
	}
	
	/**
	 * Gets all classes from the given jar file.<br>
	 * Any exceptions which will be thrown while trying to get the classes will be ignored.<br>
	 *
	 * @param file The jar file
	 * @param condition A condition to filter the classes
	 * @return A list of all classes in the given jar file
	 * @throws NullPointerException If the given file is null
	 */
	private static @NotNull List<Class<?>> getClassesFromJar(@NotNull File file, @NotNull Predicate<String> condition) {
		Objects.requireNonNull(file, "File must not be null");
		List<Class<?>> classes = Lists.newArrayList();
		Predicate<String> ignore = getIgnoreCondition();
		if (file.exists() && file.canRead()) {
			try (JarFile jar = new JarFile(file)) {
				Enumeration<JarEntry> enumeration = jar.entries();
				while (enumeration.hasMoreElements()) {
					JarEntry entry = enumeration.nextElement();
					if (entry.getName().endsWith(".class")) {
						String className = convertToClass(entry.getName());
						if (ignore.test(className)) {
							continue;
						}
						if (!condition.test(className)) {
							continue;
						}
						classes.add(Class.forName(className));
					}
				}
			} catch (Exception | Error ignored) {}
		}
		return classes;
	}
	
	/**
	 * Gets all classes from the given directory.<br>
	 * This method will be called recursively if the given directory contains subdirectories.<br>
	 * <p>
	 *     If a jar file is found, {@link #getClassesFromJar(File, Predicate)} will be called.<br>
	 *     Any exceptions which will be thrown while trying to get the classes will be ignored.<br>
	 *     This method will be mainly used to get all classes in ide environments, from the output directory.
	 * </p>
	 *
	 * @param directory The directory to get the classes from
	 * @param condition A condition to filter the classes
	 * @return A list of all classes in the given directory
	 * @throws NullPointerException If the given directory is null
	 */
	private static @NotNull List<Class<?>> getClassesFromDirectory(@NotNull File directory, @NotNull Predicate<String> condition) {
		Objects.requireNonNull(directory, "Path must not be null");
		List<Class<?>> classes = Lists.newArrayList();
		for (File file : listFiles(directory, (dir, name) -> name.endsWith(".jar"), false)) {
			classes.addAll(getClassesFromJar(file, condition));
		}
		Predicate<String> ignore = getIgnoreCondition();
		for (File classfile : listFiles(directory, (dir, name) -> name.endsWith(".class"), true)) {
			String className = convertToClass(classfile.getAbsolutePath().substring(directory.getAbsolutePath().length() + 1));
			if (ignore.test(className)) {
				continue;
			}
			if (!condition.test(className)) {
				continue;
			}
			try {
				classes.add(Class.forName(className));
			} catch (Exception | Error ignored) {}
		}
		return classes;
	}
	
	/**
	 * Gets all files from the given directory.<br>
	 * If the given directory contains subdirectories, this method will be called recursively.<br>
	 *
	 * @param directory The directory to get the files from
	 * @param filter A filter to filter the files
	 * @param recurse If true, subdirectories will be included
	 * @return A list of all files in the given directory
	 * @throws NullPointerException If the given directory is null
	 */
	private static @NotNull List<File> listFiles(@NotNull File directory, @Nullable FilenameFilter filter, boolean recurse) {
		Objects.requireNonNull(directory, "Directory must not be null");
		List<File> files = Lists.newArrayList();
		for (File entry : Objects.requireNonNull(directory.listFiles())) {
			if (filter == null || filter.accept(directory, entry.getName())) {
				files.add(entry);
			}
			if (recurse && entry.isDirectory()) {
				files.addAll(listFiles(entry, filter, true));
			}
		}
		return files;
	}
	
	/**
	 * Gets all files from the classpath.<br>
	 * The files will be extracted from the system property 'java.class.path'.<br>
	 * The system path separator will be used to split the classpath into single files.<br>
	 *
	 * @return A list of all files from the classpath
	 */
	private static @NotNull List<File> getClassPathFiles() {
		List<File> files = Lists.newArrayList();
		String classPath = System.getProperty("java.class.path");
		if (classPath != null) {
			for (String path : classPath.split(File.pathSeparator)) {
				files.add(new File(path));
			}
		}
		return files;
	}
	
	/**
	 * Converts the given file name to a class name.<br>
	 *
	 * @param fileName The file name to convert
	 * @return The converted class name
	 * @throws NullPointerException If the given file name is null
	 */
	private static @NotNull String convertToClass(@NotNull String fileName) {
		Objects.requireNonNull(fileName, "File name must not be null");
		return fileName.substring(0, fileName.length() - 6).replace("/", ".").replace("\\", ".");
	}
	//endregion
}
