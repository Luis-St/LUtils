package net.luis.utils.io;

import net.luis.utils.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * @author Luis-St
 *
 */

public class FileUtils {
	
	public static @NotNull Pair<String, String> split(@Nullable String file) {
		String str = StringUtils.stripToEmpty(file).replace("\\", "/");
		int dash = str.lastIndexOf("/");
		int dot = str.lastIndexOf(".");
		if (dash == -1 && dot == -1) {
			return Pair.of(str, "");
		} else if (dash == -1) {
			return Pair.of("", str);
		} else if (dot == -1) {
			return Pair.of(str, "");
		} else {
			return Pair.of(str.substring(0, dash + 1), str.substring(dash + 1));
		}
	}
	
	public static @NotNull String getName(@Nullable String file) {
		String str = split(file).getSecond();
		int index = str.lastIndexOf(".");
		if (index == -1) {
			return str;
		} else {
			return str.substring(0, index);
		}
	}
	
	public static @NotNull String getExtension(@Nullable String file) {
		String str = StringUtils.stripToEmpty(file);
		int index = str.lastIndexOf(".");
		if (index == -1) {
			return "";
		} else {
			return str.substring(index + 1);
		}
	}
	
	public static @NotNull String getRelativePath(@Nullable String file) {
		String str = StringUtils.contains(file, ".") ? split(file).getFirst() : StringUtils.stripToEmpty(file).replace("\\", "/");
		if (str.isEmpty() || "/".equals(str) || "./".equals(str)) {
			return "./";
		}
		int last = str.length() - 1;
		if (str.charAt(last) != '/') {
			str += "/";
		}
		if (str.startsWith("./") && str.charAt(last) == '/') {
			return str;
		} else if (!str.startsWith("./") && str.charAt(0) != '/') {
			return "./" + str;
		} else if (str.charAt(0) == '.') {
			return "./" + str.substring(1);
		} else if (str.charAt(0) == '/') {
			return "." + str;
		}
		return str;
	}
	
	public static void create(@NotNull Path file) throws IOException {
		Objects.requireNonNull(file, "File must not be null");
		if (Files.exists(file)) {
			throw new FileAlreadyExistsException("File '" + file + "' already exists");
		}
		Pair<String, String> pair = split(file.toString());
		String folders = pair.getFirst();
		String name = pair.getSecond();
		if (!".".equals(folders) && !"./".equals(folders)) {
			Files.createDirectories(Paths.get(folders));
		}
		if (!name.isBlank()) {
			Files.createFile(file);
		}
	}
	
	public static void createIfNotExists(@NotNull Path file) throws IOException {
		Objects.requireNonNull(file, "File must not be null");
		if (Files.notExists(file)) {
			create(file);
		}
	}
	
	public static @NotNull Path createSessionDirectory(@Nullable String prefix) throws IOException {
		Path path = Files.createTempDirectory(prefix == null ? "temp" : prefix);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				deleteRecursively(path.toFile().toPath());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));
		return path;
	}
	
	public static void deleteRecursively(@NotNull Path path) throws IOException {
		Objects.requireNonNull(path, "Path must not be null");
		if (!Files.exists(path)) {
			throw new FileNotFoundException("Path '" + path + "' does not exist");
		}
		try (Stream<Path> files = Files.walk(path)) {
			files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}
	}
}
