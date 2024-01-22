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
 * Represents a resource which can be loaded from the classpath or from the filesystem.<br>
 *
 * @author Luis-St
 */
public abstract sealed class ResourceLocation permits ExternalResourceLocation, InternalResourceLocation {
	
	/**
	 * The temporary directory for resources which are copied from the classpath.<br>
	 */
	protected static final Supplier<Path> TEMP;
	
	/**
	 * The path of the resource.<br>
	 */
	private final String path;
	/**
	 * The name of the resource.<br>
	 */
	private final String file;
	
	/**
	 * Constructs a new {@link ResourceLocation} with the given path and name.<br>
	 * The path will be stripped and then modified by {@link #modifyPath(String)}.<br>
	 *
	 *
	 * @param path The path of the resource
	 * @param file The name of the resource
	 * @throws NullPointerException If the file is null
	 */
	ResourceLocation(@Nullable String path, @NotNull String file) {
		this.path = this.modifyPath(StringUtils.stripToEmpty(path));
		this.file = Objects.requireNonNull(file, "File must not be null").strip();
	}
	
	//region Static factory methods
	
	/**
	 * Creates a new {@link ResourceLocation} for a resource on the classpath.<br>
	 * @param file The file of the resource
	 * @return A new resource location
	 * @throws NullPointerException If the file is null
	 */
	public static @NotNull ResourceLocation internal(@NotNull String file) {
		return new InternalResourceLocation(splitPath(file));
	}
	
	/**
	 * Creates a new {@link ResourceLocation} for a resource on the classpath.<br>
	 * @param path The path of the resource
	 * @param name The name of the resource
	 * @return A new resource location
	 * @throws NullPointerException If the name is null
	 */
	public static @NotNull ResourceLocation internal(@Nullable String path, @NotNull String name) {
		return new InternalResourceLocation(path, name);
	}
	
	/**
	 * Creates a new {@link ResourceLocation} for a resource on the filesystem.<br>
	 * @param file The file of the resource
	 * @return A new resource location
	 * @throws NullPointerException If the file is null
	 */
	public static @NotNull ResourceLocation external(@NotNull String file) {
		return new ExternalResourceLocation(splitPath(file));
	}
	
	/**
	 * Creates a new {@link ResourceLocation} for a resource on the filesystem.<br>
	 * @param path The path of the resource
	 * @param name The name of the resource
	 * @return A new resource location
	 * @throws NullPointerException If the name is null
	 */
	public static @NotNull ResourceLocation external(@Nullable String path, @NotNull String name) {
		return new ExternalResourceLocation(path, name);
	}
	
	/**
	 * Gets a resource from the classpath or from the filesystem.<br>
	 * Trys to load the resource from the filesystem first.<br>
	 * If the resource was not found in the filesystem then the classpath will be tried.<br>
	 * If the resource was not found an exception will be thrown.<br>
	 * @param path The path of the resource
	 * @param name The name of the resource
	 * @return The resource location
	 * @throws NullPointerException If the path is null
	 * @throws IllegalArgumentException If the resource was not found
	 * @see #getResource(String, String, Type)
	 */
	public static @NotNull ResourceLocation getResource(@Nullable String path, @NotNull String name) {
		return getResource(path, name, Type.EXTERNAL);
	}
	
	/**
	 * Gets a resource from the classpath or from the filesystem.<br>
	 * Trys to load the resource from the preferred type first.<br>
	 * If the preferred type is null then the resource will be tried to load from the classpath first.<br>
	 * If the resource was not found in the preferred type then the other type will be tried.<br>
	 * If the resource wa not found an exception will be thrown.<br>
	 * @param path The path of the resource
	 * @param name The name of the resource
	 * @param preferredType The preferred type of the resource
	 * @return The resource location
	 * @throws NullPointerException If the path is null
	 * @throws IllegalArgumentException If the resource was not found
	 */
	public static @NotNull ResourceLocation getResource(@Nullable String path, @NotNull String name, @Nullable Type preferredType) {
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
		Objects.requireNonNull(file, "File must not be null");
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
	
	/**
	 * Represents the type of a {@link ResourceLocation}.<br>
	 */
	public enum Type {
		/**
		 * Represents a resource on the classpath.<br>
		 */
		INTERNAL,
		/**
		 * Represents a resource on the filesystem.<br>
		 */
		EXTERNAL;
	}
}
