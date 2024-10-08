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

public class DataOutputStream implements AutoCloseable {
	
	private final OutputStream stream;
	
	public DataOutputStream(@NotNull String file) {
		this(new File(Objects.requireNonNull(file, "File must not be null")));
	}
	
	public DataOutputStream(@NotNull String path, @NotNull String fileName) {
		this(new File(Objects.requireNonNull(path, "Path must not be null"), Objects.requireNonNull(fileName, "File name must not be null")));
	}
	
	public DataOutputStream(@NotNull Path path) {
		this(Objects.requireNonNull(path, "Path must not be null").toFile());
	}
	
	public DataOutputStream(@NotNull File file) {
		Objects.requireNonNull(file, "File must not be null");
		try {
			this.stream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new UncheckedIOException("File not found: " + file, e);
		}
	}
	
	public DataOutputStream(@NotNull OutputStream stream) {
		Objects.requireNonNull(stream, "Output stream must not be null");
		this.stream = stream;
	}
	
	public @NotNull OutputStream getStream() {
		return this.stream;
	}
	
	@Override
	public void close() throws IOException {
		this.stream.close();
	}
}
