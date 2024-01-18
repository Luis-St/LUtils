package net.luis.utils.io;

import net.luis.utils.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.*;

import java.io.*;
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
public class FileUtils {
	
	/**
	 * Splits the given file into a pair of the folder and the file name.<br>
	 * The folder is the part before the last slash, the name is the part after the last slash.<br>
	 * Examples:
	 * <ul>
	 *     <li><pre>null -> ("", "")</pre></li>
	 *     <li><pre>"" -> ("", "")</pre></li>
	 *     <li><pre>"." -> (".", "")</pre></li>
	 *     <li><pre>"./" -> ("./", "")</pre></li>
	 *     <li><pre>"test" -> ("test", "")</pre></li>
	 *     <li><pre>"/test/" -> ("/test"/, "")</pre></li>
	 *     <li><pre>"/test.json" -> ("/", "test.json")</pre></li>
	 *     <li><pre>"/test/test.json" -> ("/test"/, "test.json")</pre></li>
	 * </ul>
	 *
	 * @param file The file to split as a string
	 * @return The pair of the folder and the file name
	 */
	public static @NotNull Pair<String, String> split(@Nullable String file) {
		String str = StringUtils.stripToEmpty(file).replace("\\", "/");
		int dash = str.lastIndexOf("/");
		int dot = str.lastIndexOf(".");
		if (dash == -1 && dot == -1) {
			return Pair.of(str, "");
		} else if (dash == -1 || dot == -1) {
			return Pair.of(str, "");
		} else {
			return Pair.of(str.substring(0, dash + 1), str.substring(dash + 1));
		}
	}
	
	/**
	 * Gets the name of the given file.<br>
	 * The file can be a path or a file name.<br>
	 * Examples:
	 * <ul>
	 *     <li><pre>null -> ""</pre></li>
	 *     <li><pre>"" -> ""</pre></li>
	 *     <li><pre>"/" -> ""</pre></li>
	 *     <li><pre>"/test.json" -> "test"</pre></li>
	 *     <li><pre>"/test/test.json" -> "test"</pre></li>
	 * </ul>
	 * @param file The file to get the name of
	 * @return The name of the file or an empty string if the file has no name
	 */
	public static @NotNull String getName(@Nullable String file) {
		String str = split(file).getSecond();
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
	 * Examples:
	 * <ul>
	 *     <li><pre>null -> ""</pre></li>
	 *     <li><pre>"" -> ""</pre></li>
	 *     <li><pre>"/" -> ""</pre></li>
	 *     <li><pre>"/test.json" -> ".json"</pre></li>
	 *     <li><pre>"/test/test.json" -> ".json"</pre></li>
	 * </ul>
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
	 * The file will be relativized.<br>
	 * The file can be a path or a file name.<br>
	 * Examples:
	 * <ul>
	 *     <li><pre>null -> "./"</pre></li>
	 *     <li><pre>"" -> "./"</pre></li>
	 *     <li><pre>"/" -> "./"</pre></li>
	 *     <li><pre>"test" -> "./test/"</pre></li>
	 *     <li><pre>"./test/" -> "./test/"</pre></li>
	 *     <li><pre>"/test.json" -> "./test.json"</pre></li>
	 *     <li><pre>"/test/test.json" -> "./test/test.json"</pre></li>
	 * </ul>
	 * @param file The file to get the relative path of
	 * @return The relative path of the file
	 */
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
	
	/**
	 * Creates the given file and all parent directories.<br>
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
}
