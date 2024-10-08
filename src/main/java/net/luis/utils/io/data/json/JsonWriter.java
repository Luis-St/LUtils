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

import net.luis.utils.io.stream.DataOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class JsonWriter implements AutoCloseable {
	
	private final JsonConfig config;
	private final BufferedWriter writer;
	
	public JsonWriter(@NotNull DataOutputStream stream) {
		this(stream, JsonConfig.DEFAULT);
	}
	
	public JsonWriter(@NotNull DataOutputStream stream, @NotNull JsonConfig config) {
		this.config = Objects.requireNonNull(config, "Json config must not be null");
		this.writer = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(stream, "Stream must not be null").getStream(), config.charset()));
	}
	
	public void writeJson(@NotNull JsonElement json) {
		try {
			this.writer.write(json.toString(this.config));
		} catch (IOException e) {
			this.config.errorAction().handle(e);
		}
	}
	
	@Override
	public void close() throws IOException {
		this.writer.close();
	}
}
