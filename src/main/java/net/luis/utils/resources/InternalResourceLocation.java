package net.luis.utils.resources;

import com.google.common.collect.Lists;
import net.luis.utils.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Luis-St
 *
 */

final class InternalResourceLocation extends ResourceLocation {
	
	private final URL url;
	
	InternalResourceLocation(Pair<String, String> pair) {
		this(pair.getFirst(), pair.getSecond());
	}
	
	InternalResourceLocation(String path, String name) {
		super(path, name);
		this.url = this.getClass().getResource(this.getPath() + this.getFile());
	}
	
	//region Constructor modifications
	@Override
	protected @NotNull String modifyPath(@Nullable String path) {
		String strPath = StringUtils.stripToEmpty(path);
		if (strPath.isEmpty()) {
			return "/";
		}
		if (!strPath.startsWith("/")) {
			strPath = "/" + strPath;
		}
		return !strPath.endsWith("/") ? strPath + "/" : strPath;
	}
	//endregion
	
	@Override
	public @NotNull Type getType() {
		return Type.INTERNAL;
	}
	
	@Override
	public @NotNull File asFile() {
		throw new UnsupportedOperationException("Internal resources can not be converted into a file");
	}
	
	@Override
	public @NotNull Path asPath() {
		throw new UnsupportedOperationException("Internal resources can not be converted into a path");
	}
	
	@Override
	public boolean exists() {
		return this.url != null;
	}
	
	@Override
	public @NotNull InputStream getStream() throws IOException {
		return this.url.openStream();
	}
	
	@Override
	public byte[] getBytes() throws IOException {
		try (InputStream stream = this.getStream()) {
			return stream.readAllBytes();
		}
	}
	
	@Override
	public @NotNull String getString() throws IOException {
		return this.getLines().collect(Collectors.joining(System.lineSeparator()));
	}
	
	@Override
	public @NotNull Stream<String> getLines() throws IOException {
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
}
