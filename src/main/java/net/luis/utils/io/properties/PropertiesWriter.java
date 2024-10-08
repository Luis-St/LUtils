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

package net.luis.utils.io.properties;

import net.luis.utils.io.stream.DataOutputStream;
import net.luis.utils.util.ValueConverter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class PropertiesWriter implements AutoCloseable {
	
	private final PropertyConfig config;
	private final BufferedWriter writer;
	
	public PropertiesWriter(@NotNull DataOutputStream stream) {
		this(stream, PropertyConfig.DEFAULT);
	}
	
	public PropertiesWriter(@NotNull DataOutputStream stream, @NotNull PropertyConfig config) {
		this.config = Objects.requireNonNull(config, "Config must not be null");
		this.writer = new BufferedWriter(new OutputStreamWriter(stream.getStream(), config.charset()));
	}
	
	public void write(@NotNull Property property) {
		this.write(property.getKey(), property.getRawValue());
	}
	
	public void write(@NotNull Properties properties) {
		properties.getProperties().forEach(this::write);
	}
	
	public void write(@NotNull String key, @NotNull Object value) {
		this.write(key, value.toString());
	}
	
	public <T> void write(@NotNull String key, @NotNull T value, @NotNull ValueConverter<String, T> converter) {
		this.write(key, converter.convert(value));
	}
	
	private void write(@NotNull String key, @NotNull String value) {
		try {
			this.config.ensureKeyMatches(key);
			this.config.ensureValueMatches(value);
			
			this.writer.write(key);
			this.writer.write(" ".repeat(this.config.alignment()));
			this.writer.write(this.config.separator());
			this.writer.write(" ".repeat(this.config.alignment()));
			this.writer.write(value);
			this.writer.newLine();
		} catch (IOException e) {
			this.config.errorAction().handle(e);
		}
	}
	
	@Override
	public void close() throws Exception {
		this.writer.close();
	}
}
