package net.luis.utils.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;

/**
 *
 * @author Luis-st
 *
 */

public class ClasspathInspector {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	@NotNull
	public static List<Class<?>> getClasses() {
		List<Class<?>> classes = Lists.newArrayList();
		for (File file : getClasspathClasses()) {
			if (file.isDirectory()) {
				classes.addAll(getClasses(file));
			}
		}
		classes.removeIf(Class::isAnonymousClass);
		return classes;
	}
	
	@NotNull
	private static List<Class<?>> getClasses(File path) {
		List<Class<?>> classes = Lists.newArrayList();
		for (File file : getFiles(path, (directory, name) -> name.endsWith(".class"))) {
			String className = getClassName(file.getAbsolutePath().substring(path.getAbsolutePath().length() + 1));
			try {
				classes.add(Class.forName(className));
			} catch (ClassNotFoundException e) {
				LOGGER.error("The class for the name {} cannot be found because it does not exist", className);
				throw new RuntimeException(e);
			}
		}
		return classes;
	}
	
	@NotNull
	private static List<File> getFiles(File directory, FilenameFilter filter) {
		List<File> files = Lists.newArrayList();
		for (File file : directory.listFiles()) {
			if (filter.accept(directory, file.getName())) {
				files.add(file);
			}
			if (file.isDirectory()) {
				files.addAll(getFiles(file, filter));
			}
		}
		return files;
	}
	
	@NotNull
	private static String getClassName(final String fileName) {
		return fileName.substring(0, fileName.length() - 6).replaceAll("/|\\\\", "\\.");
	}
	
	@NotNull
	private static List<File> getClasspathClasses() {
		List<File> files = Lists.newArrayList();
		if (System.getProperty("java.class.path") != null) {
			for (String path : System.getProperty("java.class.path").split(File.pathSeparator)) {
				files.add(new File(path));
			}
		}
		return files;
	}
	
}
