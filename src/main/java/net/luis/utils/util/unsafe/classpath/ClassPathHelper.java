package net.luis.utils.util.unsafe.classpath;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author Luis-St
 *
 */

class ClassPathHelper {
	
	static @NotNull List<Class<?>> getClasses() {
		List<Class<?>> classes = new ArrayList<>();
		for (File file : getClassPathFiles()) {
			if (file.isDirectory()) {
				classes.addAll(getClassesFromDirectory(file));
			} else {
				classes.addAll(getClassesFromJar(file));
			}
		}
		return classes;
	}
	
	//region Internal helper methods
	private static @NotNull String getClassName(@NotNull String fileName) {
		return fileName.substring(0, fileName.length() - 6).replace("/", ".").replace("\\", ".");
	}
	
	private static @NotNull List<Class<?>> getClassesFromJar(@NotNull File file) {
		List<Class<?>> classes = new ArrayList<>();
		if (file.canRead()) {
			try (JarFile jar = new JarFile(file)) {
				Enumeration<JarEntry> enumeration = jar.entries();
				while (enumeration.hasMoreElements()) {
					JarEntry entry = enumeration.nextElement();
					if (entry.getName().endsWith("class")) {
						Class<?> clazz = Class.forName(getClassName(entry.getName()));
						classes.add(clazz);
					}
				}
			} catch (Exception | Error ignored) {
			
			}
		}
		return classes;
	}
	
	private static @NotNull List<Class<?>> getClassesFromDirectory(@NotNull File path) {
		List<Class<?>> classes = new ArrayList<>();
		for (File file : listFiles(path, (dir, name) -> name.endsWith(".jar"), false)) {
			classes.addAll(getClassesFromJar(file));
		}
		for (File classfile : listFiles(path, (dir, name) -> name.endsWith(".class"), true)) {
			String className = getClassName(classfile.getAbsolutePath().substring(path.getAbsolutePath().length() + 1));
			try {
				classes.add(Class.forName(className));
			} catch (Exception | Error ignored) {
			
			}
		}
		return classes;
	}
	
	private static @NotNull List<File> listFiles(@NotNull File directory, FilenameFilter filter, boolean recurse) {
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
	//endregion
	
}
