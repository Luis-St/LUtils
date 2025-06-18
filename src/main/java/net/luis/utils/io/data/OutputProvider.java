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

package net.luis.utils.io.data;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Output provider for writing data to a file or stream.<br>
 * This class provides several constructors for different types of output sources.<br>
 * <p>
 *     Primary usage of this class is to provide an output stream for different output sources.<br>
 *     This can be useful for other writers or formatters to keep their constructors clean and simple.
 * </p>
 * The class is also {@link AutoCloseable}, so it can be used in try-with-resources statements.<br>
 *
 * @author Luis-St
 */
public class OutputProvider implements AutoCloseable {
	
	/**
	 * The output stream for writing data.<br>
	 */
	private final OutputStream stream;
	
	/**
	 * Constructs a new output provider for the given file.<br>
	 *
	 * @param file The file to write data to
	 * @throws NullPointerException If the file is null
	 * @throws UncheckedIOException If the file is not found
	 */
	public OutputProvider(@NotNull String file) {
		this(new File(Objects.requireNonNull(file, "File must not be null")));
	}
	
	/**
	 * Constructs a new output provider for the given path and file name.<br>
	 * The path and file name are concatenated to a file.<br>
	 *
	 * @param path The path to the file
	 * @param fileName The name of the file
	 * @throws NullPointerException If the path or file name is null
	 * @throws UncheckedIOException If the file is not found
	 */
	public OutputProvider(@NotNull String path, @NotNull String fileName) {
		this(new File(Objects.requireNonNull(path, "Path must not be null"), Objects.requireNonNull(fileName, "File name must not be null")));
	}
	
	/**
	 * Constructs a new output provider for the given path.<br>
	 *
	 * @param path The path to the file
	 * @throws NullPointerException If the path is null
	 * @throws UncheckedIOException If the file is not found
	 */
	public OutputProvider(@NotNull Path path) {
		this(Objects.requireNonNull(path, "Path must not be null").toFile());
	}
	
	/**
	 * Constructs a new output provider for the given file.<br>
	 *
	 * @param file The file to write data to
	 * @throws NullPointerException If the file is null
	 * @throws UncheckedIOException If the file is not found
	 */
	public OutputProvider(@NotNull File file) {
		Objects.requireNonNull(file, "File must not be null");
		try {
			this.stream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new UncheckedIOException("File not found: " + file, e);
		}
	}
	
	/**
	 * Constructs a new output provider for the given output stream.<br>
	 *
	 * @param stream The output stream to write data to
	 * @throws NullPointerException If the stream is null
	 */
	public OutputProvider(@NotNull OutputStream stream) {
		Objects.requireNonNull(stream, "Output stream must not be null");
		this.stream = stream;
	}
	
	/**
	 * Returns the internal output stream for writing data.<br>
	 * The output stream should not be used directly, it is intended to be passed to other writers or formatters.<br>
	 *
	 * @return The output stream
	 */
	public @NotNull OutputStream getStream() {
		return this.stream;
	}
	
	@Override
	public void close() throws IOException {
		this.stream.close();
	}
}
