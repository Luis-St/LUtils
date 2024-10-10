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
 *
 * @author Luis-St
 *
 */

public class DataInput implements AutoCloseable {
	
	private final InputStream stream;
	
	public DataInput(@NotNull String file) {
		this(new File(Objects.requireNonNull(file, "File must not be null")));
	}
	
	public DataInput(@NotNull String path, @NotNull String fileName) {
		this(new File(Objects.requireNonNull(path, "Path must not be null"), Objects.requireNonNull(fileName, "File name must not be null")));
	}
	
	public DataInput(@NotNull Path path) {
		this(Objects.requireNonNull(path, "Path must not be null").toFile());
	}
	
	public DataInput(@NotNull File file) {
		Objects.requireNonNull(file, "File must not be null");
		try {
			this.stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new UncheckedIOException("File not found: " + file, e);
		}
	}
	
	public DataInput(@NotNull InputStream stream) {
		Objects.requireNonNull(stream, "Input stream must not be null");
		this.stream = stream;
	}
	
	public @NotNull InputStream getStream() {
		return this.stream;
	}
	
	@Override
	public void close() throws IOException {
		this.stream.close();
	}
}
