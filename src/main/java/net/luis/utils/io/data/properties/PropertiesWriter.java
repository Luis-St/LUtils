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

package net.luis.utils.io.data.properties;

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
		this.config = Objects.requireNonNull(config, "Property config must not be null");
		this.writer = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(stream, "Stream must not be null").getStream(), config.charset()));
	}
	
	public void write(@NotNull String key, @NotNull Object value) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		this.write(Property.of(key, value.toString()));
	}
	
	public void write(@NotNull Properties properties) {
		Objects.requireNonNull(properties, "Properties must not be null");
		properties.getProperties().forEach(this::write);
	}
	
	public <T> void write(@NotNull String key, @NotNull T value, @NotNull ValueConverter<String, T> converter) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(converter, "Converter must not be null");
		this.write(Property.of(key, converter.convert(value)));
	}
	
	public void write(@NotNull Property property) {
		Objects.requireNonNull(property, "Property must not be null");
		try {
			this.config.ensureKeyMatches(property.getKey());
			this.config.ensureValueMatches(property.getRawValue());
			
			this.writer.write(property.toString(this.config));
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
