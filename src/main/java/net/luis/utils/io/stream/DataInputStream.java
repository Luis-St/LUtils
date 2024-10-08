package net.luis.utils.io.stream;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class DataInputStream implements AutoCloseable {
	
	private final InputStream stream;
	
	public DataInputStream(@NotNull String file) {
		this(new File(Objects.requireNonNull(file, "File must not be null")));
	}
	
	public DataInputStream(@NotNull String path, @NotNull String fileName) {
		this(new File(Objects.requireNonNull(path, "Path must not be null"), Objects.requireNonNull(fileName, "File name must not be null")));
	}
	
	public DataInputStream(@NotNull Path path) {
		this(Objects.requireNonNull(path, "Path must not be null").toFile());
	}
	
	public DataInputStream(@NotNull File file) {
		Objects.requireNonNull(file, "File must not be null");
		try {
			this.stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new UncheckedIOException("File not found: " + file, e);
		}
	}
	
	public DataInputStream(@NotNull InputStream stream) {
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
