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

package net.luis.utils.io;

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Utility class for file operations.
 *
 * @author Luis-St
 */
public final class FileUtils {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private FileUtils() {}
	
	/**
	 * Splits the given file into a pair of the folder and the file name.<br>
	 * The folder is the part before the last slash, the name is the part after the last slash.<br>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * split(null) -> ("", "")
	 * split("") -> ("", "")
	 * split(".") -> (".", "")
	 * split("./") -> ("./", "")
	 * split("foo") -> ("foo", "")
	 * split("/foo/") -> ("/foo"/, "")
	 * split("foo.json") -> ("", "foo.json")
	 * split("/foo.json") -> ("/", "foo.json")
	 * split("/bar/foo.json") -> ("/bar"/, "foo.json")
	 * }</pre>
	 *
	 * @param file The file to split as a string
	 * @return The pair of the folder and the file name
	 */
	public static @NotNull Pair<String, String> split(@Nullable String file) {
		String str = StringUtils.stripToEmpty(file).replace("\\", "/");
		int dash = str.lastIndexOf("/");
		int dot = str.lastIndexOf(".");
		
		boolean hasDotAfterSlash = dot > dash;
		if (dash == -1 && dot == -1) {
			return Pair.of(str, "");
		} else if (!hasDotAfterSlash || ".".equals(str)) {
			return Pair.of(str, "");
		} else if (dash == -1) {
			return Pair.of("", str);
		} else {
			return Pair.of(str.substring(0, dash + 1), str.substring(dash + 1));
		}
	}
	
	/**
	 * Gets the name of the given file.<br>
	 * The file can be a path or a file name.<br>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * getName(null) -> ""
	 * getName("") -> ""
	 * getName("/") -> ""
	 * getName("/foo.json") -> "foo"
	 * getName("/bar/foo.json") -> "foo"
	 * }</pre>
	 *
	 * @param file The file to get the name of
	 * @return The name of the file or an empty string if the file has no name
	 */
	public static @NotNull String getName(@Nullable String file) {
		String str;
		if (Strings.CS.containsAny(file, "/", "\\")) {
			str = split(file).getSecond();
		} else {
			str = StringUtils.stripToEmpty(file);
		}
		
		int index = str.lastIndexOf(".");
		if (index == -1) {
			return str;
		} else {
			return str.substring(0, index);
		}
	}
	
	/**
	 * Gets the extension of the given file.<br>
	 * The file can be a path or a file name.<br>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * getExtension(null) -> ""
	 * getExtension("") -> ""
	 * getExtension("/") -> ""
	 * getExtension("/foo.json") -> "json"
	 * getExtension("/bar/foo.json") -> "json"
	 * }</pre>
	 *
	 * @param file The file to get the extension of
	 * @return The extension of the file or an empty string if the file has no extension
	 */
	public static @NotNull String getExtension(@Nullable String file) {
		String str = StringUtils.stripToEmpty(file);
		int index = str.lastIndexOf(".");
		if (index == -1) {
			return "";
		} else {
			return str.substring(index + 1);
		}
	}
	
	/**
	 * Gets the relative path of the given file.<br>
	 * The file can be a path or a file name.
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * getRelativePath(null) -> "./"
	 * getRelativePath("") -> "./"
	 * getRelativePath("/") -> "./"
	 * getRelativePath("foo") -> "./foo/"
	 * getRelativePath("./foo/") -> "./foo/"
	 * getRelativePath("/foo.json") -> "./foo.json"
	 * getRelativePath("/bar/foo.json") -> "./bar/foo.json"
	 * }</pre>
	 *
	 * @param file The file to get the relative path of
	 * @return The relative path of the file
	 */
	public static @NotNull String getRelativePath(@Nullable String file) {
		String str = Strings.CS.contains(file, ".") ? split(file).getFirst() : StringUtils.stripToEmpty(file).replace("\\", "/");
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
	
	/**
	 * Gets the relative path of the given file.<br>
	 * The file can be a path or a file name.
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * normalizeDirectoryPath(null) -> "./"
	 * normalizeDirectoryPath("") -> "./"
	 * normalizeDirectoryPath("/") -> "./"
	 * normalizeDirectoryPath("foo") -> "./foo/"
	 * normalizeDirectoryPath("./foo/") -> "./foo/"
	 * normalizeDirectoryPath("/foo.json") -> "./"
	 * normalizeDirectoryPath("/bar/foo.json") -> "./bar/"
	 * }</pre>
	 *
	 * @param file The file to get the relative path of
	 * @return The relative path of the file
	 */
	public static @NotNull String normalizeDirectoryPath(@Nullable String file) {
		String original = StringUtils.stripToEmpty(file).replace("\\", "/");
		if (original.isEmpty() || "/".equals(original) || "./".equals(original)) {
			return "/";
		}
		
		Pair<String, String> parts = split(original);
		boolean isFile = !parts.getSecond().isEmpty();
		String dir = parts.getFirst();
		
		if (isFile) {
			if (original.startsWith("./")) {
				String result = original.substring(1);
				if (!result.startsWith("/")) {
					return "/" + result;
				}
				return result;
			} else if (original.startsWith("/")) {
				if ("/".equals(dir)) {
					return "/";
				} else {
					return original;
				}
			}
		}
		
		String result = original;
		if (!result.endsWith("/")) {
			result += "/";
		}
		if (result.startsWith("./")) {
			result = result.substring(1);
		}
		if (!result.startsWith("/")) {
			return "/" + result;
		}
		return result;
	}
	
	/**
	 * Creates the given file and all parent directories.<br>
	 *
	 * @param file The file to create
	 * @throws NullPointerException If the file is null
	 * @throws FileAlreadyExistsException If the file already exists
	 * @throws IOException If an I/O error occurs
	 */
	@Blocking
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
	
	/**
	 * Creates the given file and all parent directories if they do not exist.<br>
	 *
	 * @param file The file to create
	 * @throws NullPointerException If the file is null
	 * @throws IOException If an I/O error occurs
	 * @see #create(Path)
	 */
	@Blocking
	public static void createIfNotExists(@NotNull Path file) throws IOException {
		Objects.requireNonNull(file, "File must not be null");
		if (Files.notExists(file)) {
			create(file);
		}
	}
	
	/**
	 * Creates a temporary directory for the current session.<br>
	 * The directory and all its content will be deleted when the JVM shuts down.<br>
	 *
	 * @param prefix The prefix of the directory name
	 * @return The path of the created directory
	 * @throws IOException If an I/O error occurs
	 * @see Files#createTempDirectory(String, FileAttribute[])
	 */
	@Blocking
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
	
	/**
	 * Deletes the given file or directory recursively.<br>
	 *
	 * @param path The path to delete
	 * @throws NullPointerException If the path is null
	 * @throws IOException If an I/O error occurs
	 */
	@Blocking
	public static void deleteRecursively(@NotNull Path path) throws IOException {
		Objects.requireNonNull(path, "Path must not be null");
		if (!Files.exists(path)) {
			throw new FileNotFoundException("Path '" + path + "' does not exist");
		}
		try (Stream<Path> files = Files.walk(path)) {
			files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}
	}
	
	/**
	 * Reads the content of the given input provider as a string.<br>
	 *
	 * @param provider The input provider to read from
	 * @return The content of the input provider
	 * @throws NullPointerException If the provider is null
	 * @throws IOException If an I/O error occurs
	 */
	@Blocking
	public static @NotNull String readString(@NotNull InputProvider provider) throws IOException {
		Objects.requireNonNull(provider, "Provider must not be null");
		return readString(new InputStreamReader(provider.getStream()));
	}
	
	/**
	 * Reads the content of the given input provider as a string using the given charset.<br>
	 *
	 * @param provider The input provider to read from
	 * @param charset The charset to use
	 * @return The content of the input provider
	 * @throws NullPointerException If the provider or charset is null
	 * @throws IOException If an I/O error occurs
	 */
	@Blocking
	public static @NotNull String readString(@NotNull InputProvider provider, @NotNull Charset charset) throws IOException {
		Objects.requireNonNull(provider, "Provider must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
		return readString(new InputStreamReader(provider.getStream(), charset));
	}
	
	/**
	 * Reads the content of the given reader as a string.<br>
	 *
	 * @param reader The reader to read from
	 * @return The content of the reader
	 * @throws NullPointerException If the reader is null
	 * @throws IOException If an I/O error occurs
	 */
	@Blocking
	@SuppressWarnings("NestedAssignment")
	public static @NotNull String readString(@NotNull Reader reader) throws IOException {
		Objects.requireNonNull(reader, "Reader must not be null");
		try (BufferedReader buffer = new BufferedReader(reader)) {
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = buffer.readLine()) != null) {
				builder.append(line).append("\n");
			}
			return builder.toString().stripTrailing();
		}
	}
}
