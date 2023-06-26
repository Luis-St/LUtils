package net.luis.utils.resources;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * @author Luis-St
 *
 */

public abstract class ResourceLocation {

	public static @NotNull ResourceLocation internal(String path, String name) {
		return new InternalResourceLocation();
	}
	
	public static @NotNull ResourceLocation external(String path, String name) {
		return new ExternalResourceLocation();
	}
	
/*	private final String path;
	private final String name;
	
	public ResourceLocation(String name) {
		String str = Objects.requireNonNull(name, "Name must not be null").strip().replace("\\", "/");
		int index = str.lastIndexOf("/");
		if (index == -1) {
			this.path = "";
			this.name = str;
		} else {
			this.path = str.substring(0, index + 1);
			this.name = str.substring(index + 1);
		}
	}
	
	public ResourceLocation(String path, String name) {
		this.path = StringUtils.trimToEmpty(path);
		this.name = Objects.requireNonNull(name, "Name must not be null").strip();
	}*/
	
	public abstract Type getType();
	
	public abstract File getFile();
	
	public abstract Path getPath();
	
	public abstract Stream<String> getLines();
	
	public abstract String getString();
	
	public abstract byte[] getBytes();
	
	public abstract InputStream getStream();
	
	public abstract boolean exists();
	
	public static enum Type {
		INTERNAL, EXTERNAL;
	}
}
