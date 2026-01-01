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

import com.google.common.collect.Lists;
import net.luis.utils.io.FileUtils;
import net.luis.utils.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Internal implementation of {@link ResourceLocation}.<br>
 * This class is used to load resources from the classpath.<br>
 *
 * @author Luis-St
 */
final class InternalResourceLocation extends ResourceLocation {
	
	/**
	 * The url of the resource on the classpath or null if the resource does not exist.<br>
	 */
	private final URL url;
	
	/**
	 * Constructs a new internal resource location from the given {@link Pair}.<br>
	 * The first value of the pair is the path, and the second value is the name of the resource.<br>
	 *
	 * @param pair The pair
	 * @throws NullPointerException If the pair is null
	 */
	InternalResourceLocation(@NonNull Pair<String, String> pair) {
		this(pair.getFirst(), pair.getSecond());
	}
	
	/**
	 * Constructs a new internal resource location with the given path and name.<br>
	 *
	 * @param path The path of the resource
	 * @param name The name of the resource
	 * @throws NullPointerException If the name is null
	 */
	InternalResourceLocation(@Nullable String path, @NonNull String name) {
		super(path, name);
		this.url = this.getClass().getResource(this.getPath() + this.getFile());
	}
	
	//region Constructor modifications
	@Override
	protected @NonNull String modifyPath(@Nullable String path) {
		String strPath = StringUtils.stripToEmpty(path);
		if (strPath.isEmpty()) {
			return "/";
		}
		if (strPath.charAt(0) != '/') {
			strPath = "/" + strPath;
		}
		return strPath.charAt(strPath.length() - 1) != '/' ? strPath + "/" : strPath;
	}
	//endregion
	
	@Override
	public @NonNull Type getType() {
		return Type.INTERNAL;
	}
	
	@Override
	public @NonNull File asFile() {
		throw new UnsupportedOperationException("Internal resources can not be converted into a file");
	}
	
	@Override
	public @NonNull Path asPath() {
		throw new UnsupportedOperationException("Internal resources can not be converted into a path");
	}
	
	@Override
	public boolean exists() {
		return this.url != null;
	}
	
	@Override
	public @NonNull InputStream getStream() throws IOException {
		return this.url.openStream();
	}
	
	@Override
	public byte @NonNull [] getBytes() throws IOException {
		try (InputStream stream = this.getStream()) {
			return stream.readAllBytes();
		}
	}
	
	@Override
	public @NonNull String getString() throws IOException {
		return this.getLines().collect(Collectors.joining(System.lineSeparator()));
	}
	
	@Override
	public @NonNull Stream<String> getLines() throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.getStream()))) {
			List<String> lines = Lists.newArrayList();
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				lines.add(line);
			}
			return lines.stream();
		}
	}
	
	@Override
	public @NonNull Path copy() throws IOException {
		String path = this.getPath();
		Path target = TEMP.get().resolve(path.startsWith("/") ? path.substring(1) : path).resolve(this.getFile());
		FileUtils.createIfNotExists(target);
		InputStream stream = this.getStream();
		Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
		stream.close();
		return target;
	}
	
	@Override
	public @NonNull Path copy(@NonNull Path target) throws IOException {
		FileUtils.createIfNotExists(target);
		InputStream stream = this.getStream();
		Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
		stream.close();
		return target;
	}
}
