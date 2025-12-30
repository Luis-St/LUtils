/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.data.yaml;

import net.luis.utils.io.data.OutputProvider;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;

/**
 * A yaml writer for writing yaml elements to an output.<br>
 * The writer can be used to write yaml mappings, sequences, scalars and null values.<br>
 * The writer expects only one yaml element per output.<br>
 *
 * @author Luis-St
 */
public class YamlWriter implements AutoCloseable {

	/**
	 * The yaml config used by the writer.<br>
	 */
	private final YamlConfig config;
	/**
	 * The internal writer used to write the yaml elements.<br>
	 */
	private final BufferedWriter writer;

	/**
	 * Constructs a new yaml writer with the default configuration.<br>
	 *
	 * @param output The output to create the writer for
	 * @throws NullPointerException If the output is null
	 */
	public YamlWriter(@NotNull OutputProvider output) {
		this(output, YamlConfig.DEFAULT);
	}

	/**
	 * Constructs a new yaml writer with the given configuration.<br>
	 *
	 * @param output The output to create the writer for
	 * @param config The configuration to use for the writer
	 * @throws NullPointerException If the output or the configuration is null
	 */
	public YamlWriter(@NotNull OutputProvider output, @NotNull YamlConfig config) {
		this.config = Objects.requireNonNull(config, "Yaml config must not be null");
		this.writer = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(output, "Output must not be null").getStream(), config.charset()));
	}

	/**
	 * Writes the given yaml element to the output.<br>
	 * The yaml element is written as a string with the configuration of the writer.<br>
	 *
	 * @param yaml The yaml element to write
	 * @throws NullPointerException If the yaml element is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeYaml(@NotNull YamlElement yaml) {
		Objects.requireNonNull(yaml, "Yaml element must not be null");
		try {
			if (this.config.useDocumentMarkers()) {
				this.writer.write("---");
				this.writer.newLine();
			}
			this.writer.write(yaml.toString(this.config));
			if (this.config.useDocumentMarkers()) {
				this.writer.newLine();
				this.writer.write("...");
			}
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the yaml element", e);
		}
	}

	@Override
	public void close() throws IOException {
		this.writer.close();
	}
}
