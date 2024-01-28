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

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("ErrorNotRethrown")
class ClassPathHelper {
	
	static @NotNull List<Class<?>> getClasses(boolean includeDependencies, @NotNull Predicate<String> condition) {
		List<Class<?>> classes = new ArrayList<>();
		for (File file : getClassPathFiles()) {
			if (file.isDirectory()) {
				classes.addAll(getClassesFromDirectory(file, condition));
			} else {
				String caller = StackTraceUtils.getCallingClass(1).getPackageName();
				classes.addAll(getClassesFromJar(file, includeDependencies ? condition : condition.and(clazz -> clazz.startsWith(caller))));
			}
		}
		return classes;
	}
	
	//region Internal helper methods
	
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
	
	private static @NotNull List<Class<?>> getClassesFromDirectory(@NotNull File path, @NotNull Predicate<String> condition) {
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
	
	private static @NotNull String convertToClass(@NotNull String fileName) {
		Objects.requireNonNull(fileName, "File name must not be null");
		return fileName.substring(0, fileName.length() - 6).replace("/", ".").replace("\\", ".");
	}
	//endregion
}
