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

package net.luis.utils.io.data.json;

import net.luis.utils.io.data.OutputProvider;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;

/**
 * A json writer for writing json elements to an output.<br>
 * The writer can be used to write json arrays, objects, values and null values.<br>
 * The writer expects only one json element per output.<br>
 *
 * @author Luis-St
 */
public class JsonWriter implements AutoCloseable {
	
	/**
	 * The json config used by the writer.<br>
	 */
	private final JsonConfig config;
	/**
	 * The internal writer used to write the json elements.<br>
	 */
	private final BufferedWriter writer;
	
	/**
	 * Constructs a new json writer with the default configuration.<br>
	 * @param output The output to create the writer for
	 * @throws NullPointerException If the output is null
	 */
	public JsonWriter(@NotNull OutputProvider output) {
		this(output, JsonConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new json writer with the given configuration.<br>
	 * @param output The output to create the writer for
	 * @param config The configuration to use for the writer
	 * @throws NullPointerException If the output or the configuration is null
	 */
	public JsonWriter(@NotNull OutputProvider output, @NotNull JsonConfig config) {
		this.config = Objects.requireNonNull(config, "Json config must not be null");
		this.writer = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(output, "Output must not be null").getStream(), config.charset()));
	}
	
	/**
	 * Writes the given json element to the output.<br>
	 * The json element is written as a string with the configuration of the writer.<br>
	 * @param json The json element to write
	 * @throws NullPointerException If the json element is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeJson(@NotNull JsonElement json) {
		Objects.requireNonNull(json, "Json element must not be null");
		try {
			this.writer.write(json.toString(this.config));
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the json element", e);
		}
	}
	
	@Override
	public void close() throws IOException {
		this.writer.close();
	}
}
