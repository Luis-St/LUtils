package net.luis.utils.resources;

import net.luis.utils.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 *
 * @author Luis-St
 *
 */

final class ExternalResourceLocation extends ResourceLocation {
	
	private final File file;
	private final Path path;
	
	ExternalResourceLocation(@NotNull Pair<String, String> pair) {
		this(pair.getFirst(), pair.getSecond());
	}
	
	ExternalResourceLocation(@Nullable String path, @NotNull String name) {
		super(path, name);
		this.file = new File(this.getPath() + this.getFile());
		this.path = Paths.get(this.getPath() + this.getFile());
	}
	
	//region Constructor modifications
	@Override
	protected @NotNull String modifyPath(@Nullable String path) {
		String strPath = StringUtils.stripToEmpty(path);
		if (strPath.isEmpty()) {
			return "./";
		}
		if (!strPath.startsWith("./") && strPath.charAt(0) != '/') {
			strPath = "./" + strPath;
		} else if (strPath.charAt(0) == '.') {
			strPath = "./" + strPath.substring(1);
		} else if (strPath.charAt(0) == '/') {
			strPath = "." + strPath;
		}
		return strPath.charAt(strPath.length() - 1) != '/' ? strPath + "/" : strPath;
	}
	//endregion
	
	@Override
	public @NotNull Type getType() {
		return Type.EXTERNAL;
	}
	
	@Override
	public @NotNull File asFile() {
		return this.file;
	}
	
	@Override
	public @NotNull Path asPath() {
		return this.path;
	}
	
	@Override
	public boolean exists() {
		return this.file.exists();
	}
	
	@Override
	public @NotNull InputStream getStream() throws IOException {
		return Files.newInputStream(this.path);
	}
	
	@Override
	public byte @NotNull [] getBytes() throws IOException {
		return Files.readAllBytes(this.path);
	}
	
	@Override
	public @NotNull String getString() throws IOException {
		return Files.readString(this.path);
	}
	
	@Override
	public @NotNull Stream<String> getLines() throws IOException {
		return Files.readAllLines(this.path).stream();
	}
	
	@Override
	public @NotNull Path copy() throws IOException {
		String path = this.getPath();
		Path target = TEMP.get().resolve(path.startsWith("./") ? path.substring(2) : path).resolve(this.getFile());
		return Files.copy(this.path, target, StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Override
	public @NotNull Path copy(@NotNull Path target) throws IOException {
		return Files.copy(this.path, target, StandardCopyOption.REPLACE_EXISTING);
	}
}
