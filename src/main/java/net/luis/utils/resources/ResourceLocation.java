package net.luis.utils.resources;

import net.luis.utils.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * @author Luis-St
 *
 */

public abstract sealed class ResourceLocation permits ExternalResourceLocation, InternalResourceLocation {
	
	private final String path;
	private final String file;
	
	ResourceLocation(String path, String file) {
		String strPath = StringUtils.stripToEmpty(path);
		this.path = this.modifyPath(strPath);
		this.file = StringUtils.stripToEmpty(file);
	}
	
	//region Static factory methods
	public static @NotNull ResourceLocation internal(String file) {
		return new InternalResourceLocation(splitPath(file));
	}
	
	public static @NotNull ResourceLocation internal(String path, String name) {
		return new InternalResourceLocation(path, name);
	}
	
	public static @NotNull ResourceLocation external(String file) {
		return new ExternalResourceLocation(splitPath(file));
	}
	
	public static @NotNull ResourceLocation external(String path, String name) {
		return new ExternalResourceLocation(path, name);
	}
	
	
	public static @NotNull ResourceLocation getResource(String path, String name) {
		return getResource(path, name, Type.EXTERNAL);
	}

	public static @NotNull ResourceLocation getResource(String path, String name, Type preferredType) {
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
		String location = (path.endsWith("/") ? path : path + "/") + name;
		throw new IllegalArgumentException("Resource '" + location + "' was not found in any location");
	}
	//endregion
	
	//region Static helper methods
	static @NotNull Pair<String, String> splitPath(String file) {
		String str = Objects.requireNonNull(file, "File must not be null").strip().replace("\\", "/");
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
	
	public abstract byte[] getBytes() throws IOException;
	
	public abstract @NotNull String getString() throws IOException;
	
	public abstract @NotNull Stream<String> getLines() throws IOException;
	
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
	
	public static enum Type {
		INTERNAL, EXTERNAL;
	}
}