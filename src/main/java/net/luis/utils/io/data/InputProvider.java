/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.data;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Input provider for reading data from a file or stream.<br>
 * This class provides several constructors for different input sources.<br>
 * <p>
 *     Primary usage of this class is to provide an input stream for different input sources.<br>
 *     This can be useful for other readers or parsers to keep their constructors clean and simple.<br>
 * </p>
 * The class is also {@link AutoCloseable}, so it can be used in try-with-resources statements.<br>
 *
 * @author Luis-St
 */
public class InputProvider implements AutoCloseable {
	
	/**
	 * The input stream for reading data.<br>
	 */
	private final InputStream stream;
	
	/**
	 * Constructs a new input provider for the given file.<br>
	 * @param file The file to read data from
	 * @throws NullPointerException If the file is null
	 * @throws UncheckedIOException If the file is not found
	 */
	public InputProvider(@NotNull String file) {
		this(new File(Objects.requireNonNull(file, "File must not be null")));
	}
	
	/**
	 * Constructs a new input provider for the given path and file name.<br>
	 * The path and file name are concatenated to a file.<br>
	 * @param path The path to the file
	 * @param fileName The name of the file
	 * @throws NullPointerException If the path or file name is null
	 * @throws UncheckedIOException If the file is not found
	 */
	public InputProvider(@NotNull String path, @NotNull String fileName) {
		this(new File(Objects.requireNonNull(path, "Path must not be null"), Objects.requireNonNull(fileName, "File name must not be null")));
	}
	
	/**
	 * Constructs a new input provider for the given path.<br>
	 * @param path The path to the file
	 * @throws NullPointerException If the path is null
	 * @throws UncheckedIOException If the file is not found
	 */
	public InputProvider(@NotNull Path path) {
		this(Objects.requireNonNull(path, "Path must not be null").toFile());
	}
	
	/**
	 * Constructs a new input provider for the given file.<br>
	 * @param file The file to read data from
	 * @throws NullPointerException If the file is null
	 * @throws UncheckedIOException If the file is not found
	 */
	public InputProvider(@NotNull File file) {
		Objects.requireNonNull(file, "File must not be null");
		try {
			this.stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new UncheckedIOException("File not found: " + file, e);
		}
	}
	
	/**
	 * Constructs a new input provider for the given input stream.<br>
	 * @param stream The input stream to read data from
	 * @throws NullPointerException If the input stream is null
	 */
	public InputProvider(@NotNull InputStream stream) {
		Objects.requireNonNull(stream, "Input stream must not be null");
		this.stream = stream;
	}
	
	/**
	 * Returns the internal input stream for reading data.<br>
	 * The input stream should not be used directly, it is intended to be passed to other readers or parsers.<br>
	 * @return The input stream
	 */
	public @NotNull InputStream getStream() {
		return this.stream;
	}
	
	@Override
	public void close() throws IOException {
		this.stream.close();
	}
}
