/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.resources;

import net.luis.utils.function.FunctionUtils;
import net.luis.utils.io.FileUtils;
import net.luis.utils.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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
	 * The (file) name of the resource.<br>
	 */
	private final String file;
	
	/**
	 * Constructs a new resource location with the given path and name.<br>
	 * The path will be stripped and then modified by {@link #modifyPath(String)}.<br>
	 *
	 * @param path The path of the resource
	 * @param file The name of the resource
	 * @throws NullPointerException If the file is null
	 */
	ResourceLocation(@Nullable String path, @NonNull String file) {
		this.path = this.modifyPath(StringUtils.stripToEmpty(path));
		this.file = Objects.requireNonNull(file, "File must not be null").strip();
	}
	
	//region Static factory methods
	
	/**
	 * Creates a new resource location for a resource on the classpath.<br>
	 *
	 * @param file The file of the resource
	 * @return A new resource location
	 * @throws NullPointerException If the file is null
	 */
	public static @NonNull ResourceLocation internal(@NonNull String file) {
		return new InternalResourceLocation(splitPath(file));
	}
	
	/**
	 * Creates a new resource location for a resource on the classpath.<br>
	 *
	 * @param path The path of the resource
	 * @param name The name of the resource
	 * @return A new resource location
	 * @throws NullPointerException If the name is null
	 */
	public static @NonNull ResourceLocation internal(@Nullable String path, @NonNull String name) {
		return new InternalResourceLocation(path, name);
	}
	
	/**
	 * Creates a new resource location for a resource on the filesystem.<br>
	 *
	 * @param file The file of the resource
	 * @return A new resource location
	 * @throws NullPointerException If the file is null
	 */
	public static @NonNull ResourceLocation external(@NonNull String file) {
		return new ExternalResourceLocation(splitPath(file));
	}
	
	/**
	 * Creates a new resource location for a resource on the filesystem.<br>
	 *
	 * @param path The path of the resource
	 * @param name The name of the resource
	 * @return A new resource location
	 * @throws NullPointerException If the name is null
	 */
	public static @NonNull ResourceLocation external(@Nullable String path, @NonNull String name) {
		return new ExternalResourceLocation(path, name);
	}
	
	/**
	 * Gets a resource from the classpath or from the filesystem.<br>
	 * Trys to load the resource from the filesystem first.<br>
	 * If the resource was not found in the filesystem, then the classpath will be tried.<br>
	 * If the resource was not found, an exception will be thrown.<br>
	 *
	 * @param path The path of the resource
	 * @param name The name of the resource
	 * @return The resource location
	 * @throws NullPointerException If the path is null
	 * @throws IllegalArgumentException If the resource was not found
	 * @see #getResource(String, String, Type)
	 */
	public static @NonNull ResourceLocation getResource(@Nullable String path, @NonNull String name) {
		return getResource(path, name, Type.EXTERNAL);
	}
	
	/**
	 * Gets a resource from the classpath or from the filesystem.<br>
	 * Trys to load the resource from the preferred type first.<br>
	 * If the preferred type is null, then the resource will be tried to load from the classpath first.<br>
	 * If the resource was not found in the preferred type, then the other type will be tried.<br>
	 * If the resource wa not found an exception will be thrown.<br>
	 *
	 * @param path The path of the resource
	 * @param name The name of the resource
	 * @param preferredType The preferred type of the resource
	 * @return The resource location
	 * @throws NullPointerException If the path is null
	 * @throws IllegalArgumentException If the resource was not found
	 */
	public static @NonNull ResourceLocation getResource(@Nullable String path, @NonNull String name, @Nullable Type preferredType) {
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
		String location = (!path.isEmpty() && path.charAt(path.length() - 1) == '/' ? path : path + "/") + name;
		throw new IllegalArgumentException("Resource '" + location + "' was not found in any location");
	}
	//endregion
	
	//region Static helper methods
	
	/**
	 * Splits a file into a path and a name.<br>
	 *
	 * @param file The file to split
	 * @return A pair of the path and the name
	 * @throws NullPointerException If the file is null
	 */
	static @NonNull Pair<String, String> splitPath(@NonNull String file) {
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
	
	/**
	 * Modifies the path of the resource on construction.<br>
	 *
	 * @param path The path to modify
	 * @return The modified path
	 */
	protected abstract @NonNull String modifyPath(@Nullable String path);
	
	/**
	 * Returns the type of the resource.<br>
	 * @return The type
	 */
	public abstract @NonNull Type getType();
	
	/**
	 * Returns the path of the resource.<br>
	 * @return The path
	 */
	public final @NonNull String getPath() {
		return this.path;
	}
	
	/**
	 * Returns the (file) name of the resource as a string.<br>
	 * @return The (file) name
	 */
	public final @NonNull String getFile() {
		return this.file;
	}
	
	/**
	 * Constructs a new {@link File} from the resource.<br>
	 * Resources on the classpath can not be converted into a file.<br>
	 *
	 * @return The resource as a {@link File}
	 * @throws UnsupportedOperationException If the resource is on the classpath
	 */
	public abstract @NonNull File asFile();
	
	/**
	 * Constructs a new {@link Path} from the resource.<br>
	 * Resources on the classpath cannot be converted into a path.<br>
	 *
	 * @return The resource as a {@link Path}
	 * @throws UnsupportedOperationException If the resource is on the classpath
	 */
	public abstract @NonNull Path asPath();
	
	/**
	 * Checks if the resource exists.<br>
	 * @return True if the resource exists, otherwise false
	 */
	public abstract boolean exists();
	
	/**
	 * Creates an input stream to the resource.<br>
	 *
	 * @return An input stream
	 * @throws IOException If an I/O error occurs
	 */
	public abstract @NonNull InputStream getStream() throws IOException;
	
	/**
	 * Reads the content of the resource as a byte array.<br>
	 *
	 * @return The bytes of the resource
	 * @throws IOException If an I/O error occurs
	 */
	public abstract byte @NonNull [] getBytes() throws IOException;
	
	/**
	 * Reads the content of the resource as a single string.<br>
	 *
	 * @return The resource content
	 * @throws IOException If an I/O error occurs
	 */
	public abstract @NonNull String getString() throws IOException;
	
	/**
	 * Reads the content of the resource as a stream of lines.<br>
	 *
	 * @return The resource content
	 * @throws IOException If an I/O error occurs
	 */
	public abstract @NonNull Stream<String> getLines() throws IOException;
	
	/**
	 * Copies the resource to the {@link #TEMP temporary directory}.<br>
	 * The copied resource will be deleted on program exit.<br>
	 *
	 * @return The path of the copied resource
	 * @throws IOException If an I/O error occurs
	 */
	public abstract @NonNull Path copy() throws IOException;
	
	/**
	 * Copies the resource to the given target path.<br>
	 * The copied resource will stay on the filesystem and will not be deleted on program exit.<br>
	 * If a temporary copy of the resource is needed use {@link #copy()} instead.<br>
	 *
	 * @param target The target path
	 * @return The path of the copied resource
	 * @throws IOException If an I/O error occurs
	 */
	public abstract @NonNull Path copy(@NonNull Path target) throws IOException;
	
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
		TEMP = FunctionUtils.memorize(() -> {
			try {
				return FileUtils.createSessionDirectory("resources");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
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
		EXTERNAL
	}
}
