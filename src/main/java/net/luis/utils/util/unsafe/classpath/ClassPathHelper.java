package net.luis.utils.util.unsafe.classpath;

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
class ClassPathHelper {
	
	/**
	 * Constant for the system property 'unsafe.package.depth'.<br>
	 * <p>
	 *     The property is used in {@link #getClasses(boolean, Predicate)} to filter the classes<br>
	 *     from the classpath by the package depth of the calling class.<br>
	 * </p>
	 * <p>
	 *     The default value is {@code 2}.<br>
	 * </p>
	 */
	private static final String UNSAFE_PACKAGE_DEPTH = "unsafe.package.depth";
	
	/**
	 * Gets all classes from the classpath.<br>
	 * The classes will be filtered by the given condition.<br>
	 * <p>
	 *     If dependency classes should not be included, an 'and' clause is added to the condition.<br>
	 *     The clause checks uses the package name of the calling class to determine if the class is a dependency or not.<br>
	 * </p>
	 * <p>
	 *     For example, if the caller of this method is in the package "net.luis.utils.util",<br>
	 * 	   all classes in the packages "net.luis.utils" will be returned,<br>
	 * 	   because the depth is 3 which means that the package will be cut after the third dot.<br>
	 * 	   The depth can be changed by using the system property 'unsafe.package.depth'.<br>
	 * </p>
	 * <p>
	 *     Any exceptions which will be thrown while trying to get the classes will be ignored.<br>
	 * </p>
	 * @param includeDependencies Whether to include dependency classes or not
	 * @param condition A condition to filter the classes
	 * @return A list of all classes
	 */
	static @NotNull List<Class<?>> getClasses(boolean includeDependencies, @NotNull Predicate<String> condition) {
		List<Class<?>> classes = new ArrayList<>();
		for (File file : getClassPathFiles()) {
			if (file.isDirectory()) {
				classes.addAll(getClassesFromDirectory(file, condition));
			} else {
				String caller = StackTraceUtils.getCallingClass(1).getPackageName();
				int packageDepth = Integer.parseInt(System.getProperty("unsafe.package.depth", "2"));
				String pack = Arrays.stream(caller.split("\\.")).limit(packageDepth).collect(Collectors.joining("."));
				classes.addAll(getClassesFromJar(file, includeDependencies ? condition : condition.and(clazz -> clazz.startsWith(pack))));
			}
		}
		return classes;
	}
	
	//region Internal helper methods
	
	/**
	 * Gets all classes from the given jar file.<br>
	 * Any exceptions which will be thrown while trying to get the classes will be ignored.<br>
	 * @param file The jar file
	 * @param condition A condition to filter the classes
	 * @return A list of all classes in the given jar file
	 * @throws NullPointerException If the given file is null
	 */
	private static @NotNull List<Class<?>> getClassesFromJar(@NotNull File file, @NotNull Predicate<String> condition) {
		Objects.requireNonNull(file, "File must not be null");
		List<Class<?>> classes = new ArrayList<>();
		if (file.exists() && file.canRead()) {
			try (JarFile jar = new JarFile(file)) {
				Enumeration<JarEntry> enumeration = jar.entries();
				while (enumeration.hasMoreElements()) {
					JarEntry entry = enumeration.nextElement();
					if (entry.getName().endsWith("class")) {
						String className = convertToClass(entry.getName());
						if (!condition.test(className)) {
							continue;
						}
						Class<?> clazz = Class.forName(convertToClass(entry.getName()));
						classes.add(clazz);
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
	 *     This method will be mainly used to get all classes in ide environments, from the output directory.<br>
	 * </p>
	 * @param directory The directory to get the classes from
	 * @param condition A condition to filter the classes
	 * @return A list of all classes in the given directory
	 * @throws NullPointerException If the given directory is null
	 */
	private static @NotNull List<Class<?>> getClassesFromDirectory(@NotNull File directory, @NotNull Predicate<String> condition) {
		Objects.requireNonNull(directory, "Path must not be null");
		List<Class<?>> classes = new ArrayList<>();
		for (File file : listFiles(directory, (dir, name) -> name.endsWith(".jar"), false)) {
			classes.addAll(getClassesFromJar(file, condition));
		}
		for (File classfile : listFiles(directory, (dir, name) -> name.endsWith(".class"), true)) {
			String className = convertToClass(classfile.getAbsolutePath().substring(directory.getAbsolutePath().length() + 1));
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
	 * @param directory The directory to get the files from
	 * @param filter A filter to filter the files
	 * @param recurse If true, subdirectories will be included
	 * @return A list of all files in the given directory
	 * @throws NullPointerException If the given directory is null
	 */
	private static @NotNull List<File> listFiles(@NotNull File directory, @Nullable FilenameFilter filter, boolean recurse) {
		Objects.requireNonNull(directory, "Directory must not be null");
		List<File> files = new ArrayList<>();
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
	 * @return A list of all files from the classpath
	 */
	private static @NotNull List<File> getClassPathFiles() {
		List<File> files = new ArrayList<>();
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
