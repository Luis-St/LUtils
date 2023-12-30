package net.luis.utils.resources;

import net.luis.utils.io.FileUtils;
import net.luis.utils.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 *
 * @author Luis-St
 *
 */

public abstract sealed class ResourceLocation permits ExternalResourceLocation, InternalResourceLocation {
	
	protected static final Supplier<Path> TEMP;
	
	private final String path;
	private final String file;
	
	ResourceLocation(@Nullable String path, @NotNull String file) {
		String strPath = StringUtils.stripToEmpty(path);
		this.path = this.modifyPath(strPath);
		this.file = file.strip();
	}
	
	//region Static factory methods
	public static @NotNull ResourceLocation internal(@NotNull String file) {
		return new InternalResourceLocation(splitPath(file));
	}
	
	public static @NotNull ResourceLocation internal(@Nullable String path, @NotNull String name) {
		return new InternalResourceLocation(path, name);
	}
	
	public static @NotNull ResourceLocation external(@NotNull String file) {
		return new ExternalResourceLocation(splitPath(file));
	}
	
	public static @NotNull ResourceLocation external(@Nullable String path, @NotNull String name) {
		return new ExternalResourceLocation(path, name);
	}
	
	public static @NotNull ResourceLocation getResource(@Nullable String path, @NotNull String name) {
		return getResource(path, name, Type.EXTERNAL);
	}
	
	public static @NotNull ResourceLocation getResource(@Nullable String path, @NotNull String name, @NotNull Type preferredType) {
		Objects.requireNonNull(preferredType, "Preferred type must not be null");
		ResourceLocation internal = internal(path, name);
		ResourceLocation external = external(path, name);
		if (preferredType == Type.INTERNAL && internal.exists()) {
			return internal;
		} else if (preferredType == Type.EXTERNAL && external.exists()) {
			return external;
		}
		if (internal.exists()) {
			return internal;
		} else if (external.exists()) {
			return external;
		}
		if (path == null) {
			throw new IllegalArgumentException("Resource '" + name + "' was not found in any location");
		}
		String location = (path.charAt(path.length() - 1) == '/' ? path : path + "/") + name;
		throw new IllegalArgumentException("Resource '" + location + "' was not found in any location");
	}
	//endregion
	
	//region Static helper methods
	static @NotNull Pair<String, String> splitPath(@NotNull String file) {
		String str = file.strip().replace("\\", "/");
		int index = str.lastIndexOf("/");
		if (index == -1) {
			return Pair.of("", str);
		} else {
			return Pair.of(str.substring(0, index + 1), str.substring(index + 1));
		}
	}
	//endregion
	
	protected abstract @NotNull String modifyPath(@Nullable String path);
	
	public abstract @NotNull Type getType();
	
	public final @NotNull String getFile() {
		return this.file;
	}
	
	public final @NotNull String getPath() {
		return this.path;
	}
	
	public abstract @NotNull File asFile();
	
	public abstract @NotNull Path asPath();
	
	public abstract boolean exists();
	
	public abstract @NotNull InputStream getStream() throws IOException;
	
	public abstract byte @NotNull [] getBytes() throws IOException;
	
	public abstract @NotNull String getString() throws IOException;
	
	public abstract @NotNull Stream<String> getLines() throws IOException;
	
	public abstract @NotNull Path copy() throws IOException;
	
	public abstract @NotNull Path copy(@NotNull Path target) throws IOException;
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ResourceLocation that)) return false;
		
		if (!this.path.equals(that.path)) return false;
		return this.file.equals(that.file);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.path, this.file);
	}
	
	@Override
	public String toString() {
		return this.path + this.file;
	}
	//endregion
	
	//region Static initializer
	static {
		TEMP = () -> {
			try {
				return FileUtils.createSessionDirectory("resources");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};
	}
	//endregion
	
	public enum Type {
		INTERNAL, EXTERNAL
	}
}
