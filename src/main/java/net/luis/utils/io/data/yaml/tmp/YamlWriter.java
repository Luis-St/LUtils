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

package net.luis.utils.io.data.yaml.tmp;

import net.luis.utils.io.data.OutputProvider;
import net.luis.utils.io.data.yaml.YamlStruct;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class YamlWriter implements AutoCloseable {
	
	private final YamlConfig config;
	private final BufferedWriter writer;
	
	public YamlWriter(@NotNull OutputProvider output) {
		this(output, YamlConfig.DEFAULT);
	}
	
	public YamlWriter(@NotNull OutputProvider output, @NotNull YamlConfig config) {
		this.config = Objects.requireNonNull(config, "Yaml config must not be null");
		this.writer = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(output, "Output must not be null").getStream(), config.charset()));
	}
	
	public void writeYaml(@NotNull YamlStruct struct) {
		Objects.requireNonNull(struct, "yaml struct must not be null");
		try {
			this.writer.write(struct.toString(this.config));
			this.writer.newLine();
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the yaml struct", e);
		}
	}
	
	public void writeYaml(@NotNull YamlDocument document) {
		Objects.requireNonNull(document, "Yaml document must not be null");
		for (String key : document.keySet()) {
			this.writeYaml(Objects.requireNonNull(document.get(key), "yaml struct must not be null"));
		}
	}
	
	@Override
	public void close() throws IOException {
		this.writer.close();
	}
}
