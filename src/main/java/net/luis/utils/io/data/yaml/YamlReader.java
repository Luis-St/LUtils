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

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.reader.StringReader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class YamlReader implements AutoCloseable {
	
	private final YamlConfig config;
	private final StringReader reader;
	
	public YamlReader(@NotNull String string) {
		this(string, YamlConfig.DEFAULT);
	}
	
	public YamlReader(@NotNull String string, @NotNull YamlConfig config) {
		this.config = Objects.requireNonNull(config, "Json config must not be null");
		this.reader = new StringReader(Objects.requireNonNull(string, "String must not be null"));
	}
	
	public YamlReader(@NotNull InputProvider input) {
		this(input, YamlConfig.DEFAULT);
	}
	
	public YamlReader(@NotNull InputProvider input, @NotNull YamlConfig config) {
		this.config = Objects.requireNonNull(config, "Json config must not be null");
		this.reader = new StringReader(new InputStreamReader(Objects.requireNonNull(input, "Input must not be null").getStream(), config.charset()));
	}
	
	@Override
	public void close() throws IOException {
		this.reader.readRemaining(); // Assert that there is no remaining content
	}
	
	//region Internal
	private record Line(@NotNull String line, int indent) {
		
		public @NotNull String indentedLine(@NotNull YamlConfig config) {
			return config.indent().repeat(this.indent) + this.line;
		}
	}
	//endregion
}
