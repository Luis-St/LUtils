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

import net.luis.utils.io.data.OutputProvider;
import net.luis.utils.util.ValueConverter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;

/**
 * Represents a writer for properties.<br>
 *
 * @author Luis-St
 */
public class PropertiesWriter implements AutoCloseable {
	
	/**
	 * The configuration for the properties writer.<br>
	 */
	private final PropertyConfig config;
	/**
	 * The internal io writer for writing the properties.<br>
	 */
	private final BufferedWriter writer;
	
	/**
	 * Constructs a new properties writer for the given output with the default property configuration.<br>
	 * @param output The output provider to create the writer for
	 * @throws NullPointerException If the output is null
	 */
	public PropertiesWriter(@NotNull OutputProvider output) {
		this(output, PropertyConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new properties writer for the given output with the given property configuration.<br>
	 * @param output The output to create the writer for
	 * @param config The configuration for the properties writer
	 * @throws NullPointerException If the output or the configuration is null
	 */
	public PropertiesWriter(@NotNull OutputProvider output, @NotNull PropertyConfig config) {
		this.config = Objects.requireNonNull(config, "Property config must not be null");
		this.writer = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(output, "Output must not be null").getStream(), config.charset()));
	}
	
	/**
	 * Writes the given raw property to the underlying output.<br>
	 * @param key The key of the property
	 * @param value The value of the property
	 * @throws NullPointerException If the key or the value is null
	 * @throws IOException If an I/O error occurs (optional)
	 */
	public void write(@NotNull String key, @NotNull Object value) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		this.write(Property.of(key, value.toString()));
	}
	
	/**
	 * Writes the given raw property to the underlying output.<br>
	 * The value is converted to a string using the given converter.<br>
	 * @param key The key of the property
	 * @param value The value of the property
	 * @param converter The converter to convert the value to a string
	 * @param <T> The type of the value
	 * @throws NullPointerException If the key, the value or the converter is null
	 * @throws IOException If an I/O error occurs (optional)
	 */
	public <T> void write(@NotNull String key, @NotNull T value, @NotNull ValueConverter<String, T> converter) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(converter, "Converter must not be null");
		this.write(Property.of(key, converter.convert(value)));
	}
	
	/**
	 * Writes the given properties to the underlying output.<br>
	 * @param properties The properties to write
	 * @throws NullPointerException If the properties are null
	 * @throws IOException If an I/O error occurs (optional)
	 */
	public void write(@NotNull Properties properties) {
		Objects.requireNonNull(properties, "Properties must not be null");
		properties.getProperties().forEach(this::write);
	}
	
	/**
	 * Writes the given property to the underlying output.<br>
	 * @param property The property to write
	 * @throws NullPointerException If the property is null
	 * @throws IOException If an I/O error occurs (optional)
	 */
	public void write(@NotNull Property property) {
		Objects.requireNonNull(property, "Property must not be null");
		try {
			this.config.ensureKeyMatches(property.getKey());
			this.config.ensureValueMatches(property.getRawValue());
			
			this.writer.write(property.toString(this.config));
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e) {
			this.config.errorAction().handle(e);
		}
	}
	
	@Override
	public void close() throws IOException {
		this.writer.close();
	}
}
