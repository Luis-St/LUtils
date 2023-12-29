package net.luis.utils.io;

import net.luis.utils.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class FileUtils {
	
	public static @NotNull Pair<String, String> split(@Nullable String file) {
		String str = StringUtils.stripToEmpty(file).replace("\\", "/");
		int index = str.lastIndexOf("/");
		if (index == -1) {
			return Pair.of("", str);
		} else if (getExtension(file).isEmpty()) {
			return Pair.of(str, "");
		} else {
			return Pair.of(str.substring(0, index + 1), str.substring(index + 1));
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
}
