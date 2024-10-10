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

import net.luis.utils.io.data.json.exception.JsonSyntaxException;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringReader;
import net.luis.utils.io.data.DataInput;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class JsonReader implements AutoCloseable {
	
	private final JsonConfig config;
	private final ScopedStringReader reader;
	
	public JsonReader(@NotNull String string) {
		this(string, JsonConfig.DEFAULT);
	}
	
	public JsonReader(@NotNull String string, @NotNull JsonConfig config) {
		this.config = Objects.requireNonNull(config, "Json config must not be null");
		this.reader = new ScopedStringReader(Objects.requireNonNull(string, "String must not be null"));
	}
	
	public JsonReader(@NotNull DataInput input) {
		this(input, JsonConfig.DEFAULT);
	}
	
	public JsonReader(@NotNull DataInput input, @NotNull JsonConfig config) {
		this.config = Objects.requireNonNull(config, "Json config must not be null");
		this.reader = new ScopedStringReader(new InputStreamReader(Objects.requireNonNull(input, "Input must not be null").getStream(), config.charset()));
	}
	
	public @NotNull JsonElement readJson() {
		return this.readJsonElement(this.reader);
	}
	
	private @NotNull JsonElement readJsonElement(@NotNull ScopedStringReader reader) {
		this.reader.skipWhitespaces();
		char next = reader.peek();
		if (next == '{') {
			return this.readJsonObject(reader);
		} else if (next == '[') {
			return this.readJsonArray(reader);
		} else {
			return this.readJsonValue(reader);
		}
	}
	
	private @NotNull JsonObject readJsonObject(@NotNull ScopedStringReader jsonReader) {
		if (jsonReader.peek() != '{') {
			throw new JsonSyntaxException("Invalid start of json object, expected '{' but got: '" + jsonReader.peek() + "'");
		}
		String jsonObjectScope = jsonReader.readScope(ScopedStringReader.CURLY_BRACKETS);
		jsonReader.skipWhitespaces();
		if (jsonReader.canRead()) {
			throw new JsonSyntaxException("Invalid end of json object, expected '}' but got: '" + jsonReader.peek() + "'");
		}
		if (2 > jsonObjectScope.length()) {
			throw new JsonSyntaxException("Invalid json object, expected format: '{\"key\": \"value\"}' but got: '" + jsonObjectScope + "'");
		}
		JsonObject jsonObject = new JsonObject();
		if (jsonObjectScope.substring(1, jsonObjectScope.length() - 1).isBlank()) {
			return jsonObject;
		}
		
		ScopedStringReader objectReader = new ScopedStringReader(jsonObjectScope.substring(1, jsonObjectScope.length() - 1));
		while (objectReader.canRead()) {
			objectReader.skipWhitespaces();
			String key = objectReader.readString();
			objectReader.skipWhitespaces();
			if (objectReader.peek() != ':') {
				throw new JsonSyntaxException("Invalid json object, expected ':' but got: '" + objectReader.peek() + "'");
			}
			objectReader.skip();
			
			objectReader.skipWhitespaces();
			jsonObject.add(key, this.readJsonElement(new ScopedStringReader(objectReader.readUntil(','))));
			objectReader.skipWhitespaces();
			if (objectReader.canRead() && objectReader.peek() == ',') {
				objectReader.skip();
			}
		}
		return jsonObject;
	}
	
	private @NotNull JsonArray readJsonArray(@NotNull ScopedStringReader jsonReader) {
		if (jsonReader.peek() != '[') {
			throw new JsonSyntaxException("Invalid start of json array, expected '[' but got: '" + jsonReader.peek() + "'");
		}
		String jsonArrayScope = jsonReader.readScope(ScopedStringReader.SQUARE_BRACKETS);
		jsonReader.skipWhitespaces();
		if (jsonReader.canRead()) {
			throw new JsonSyntaxException("Invalid end of json array, expected ']' but got: '" + jsonReader.peek() + "'");
		}
		if (2 > jsonArrayScope.length()) {
			throw new JsonSyntaxException("Invalid json array, expected format: '[\"value\"]' but got: '" + jsonArrayScope + "'");
		}
		JsonArray jsonArray = new JsonArray();
		if (jsonArrayScope.substring(1, jsonArrayScope.length() - 1).isBlank()) {
			return jsonArray;
		}
		
		ScopedStringReader arrayReader = new ScopedStringReader(jsonArrayScope.substring(1, jsonArrayScope.length() - 1));
		while (arrayReader.canRead()) {
			arrayReader.skipWhitespaces();
			jsonArray.add(this.readJsonElement(new ScopedStringReader(arrayReader.readUntil(','))));
			arrayReader.skipWhitespaces();
			if (arrayReader.canRead() && arrayReader.peek() == ',') {
				arrayReader.skip();
			}
		}
		return jsonArray;
	}
	
	private @NotNull JsonElement readJsonValue(@NotNull ScopedStringReader reader) {
		char next = reader.peek();
		if (Character.isWhitespace(next)) {
			throw new JsonSyntaxException("Invalid json value, expected value but got: '" + next + "'");
		}
		if (next == '"') {
			return new JsonPrimitive(reader.readQuotedString());
		}
		String value = reader.readRemaining().strip();
		if ("null".equalsIgnoreCase(value)) {
			return JsonNull.INSTANCE;
		} else if ("true".equalsIgnoreCase(value)) {
			return new JsonPrimitive(true);
		} else if ("false".equalsIgnoreCase(value)) {
			return new JsonPrimitive(false);
		}
		StringReader valueReader = new StringReader(value);
		try {
			return new JsonPrimitive(valueReader.readNumber());
		} catch (Exception e) {
			throw new JsonSyntaxException("Invalid json primitive, expected a number but got: '" + value + "'");
		}
	}
	
	@Override
	public void close() throws IOException {}
}
