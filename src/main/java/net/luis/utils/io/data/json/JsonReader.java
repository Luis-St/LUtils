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
import net.luis.utils.io.data.InputProvider;
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
	
	public JsonReader(@NotNull InputProvider input) {
		this(input, JsonConfig.DEFAULT);
	}
	
	public JsonReader(@NotNull InputProvider input, @NotNull JsonConfig config) {
		this.config = Objects.requireNonNull(config, "Json config must not be null");
		this.reader = new ScopedStringReader(new InputStreamReader(Objects.requireNonNull(input, "Input must not be null").getStream(), config.charset()));
	}
	
	private static char lastNoneWhitespaceChar(@NotNull String string) {
		for (int i = string.length() - 1; i >= 0; i--) {
			char c = string.charAt(i);
			if (!Character.isWhitespace(c)) {
				return c;
			}
		}
		return 0;
	}
	
	public @NotNull JsonElement readJson() {
		if (!this.reader.canRead()) {
			throw new JsonSyntaxException("Invalid json, expected content but got nothing");
		}
		return this.readJsonElement(this.reader);
	}
	
	private @NotNull JsonElement readJsonElement(@NotNull ScopedStringReader reader) {
		this.reader.skipWhitespaces();
		char next = reader.peek();
		JsonElement element;
		if (next == '{') {
			element = this.readJsonObject(reader);
		} else if (next == '[') {
			element = this.readJsonArray(reader);
		} else {
			element = this.readJsonValue(reader);
		}
		reader.skipWhitespaces();
		if (this.config.strict() && reader.canRead()) {
			throw new JsonSyntaxException("Invalid json element, expected end of input but got: '" + reader.peek() + "'");
		}
		return element;
	}
	
	private @NotNull JsonObject readJsonObject(@NotNull ScopedStringReader jsonReader) {
		String jsonObjectScope;
		try {
			jsonObjectScope = jsonReader.readScope(ScopedStringReader.CURLY_BRACKETS);
		} catch (Exception e) {
			throw new JsonSyntaxException("Invalid json object, missing closing bracket '}'", e);
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
			String key;
			if (this.config.strict()) {
				key = objectReader.readQuotedString();
			} else {
				key = objectReader.readString();
				objectReader.skipWhitespaces();
			}
			if (objectReader.peek() != ':') {
				throw new JsonSyntaxException("Invalid json object, expected ':' but got: '" + objectReader.peek() + "'");
			}
			objectReader.skip();
			objectReader.skipWhitespaces();
			
			String value = objectReader.readUntilInclusive(',');
			if (value.charAt(value.length() - 1) == ',') {
				objectReader.skipWhitespaces();
				if (this.config.strict() && !objectReader.canRead()) {
					throw new JsonSyntaxException("Invalid json object, expected another entry but got nothing");
				}
				value = value.substring(0, value.length() - 1);
			}
			jsonObject.add(key, this.readJsonElement(new ScopedStringReader(value)));
		}
		return jsonObject;
	}
	
	private @NotNull JsonArray readJsonArray(@NotNull ScopedStringReader jsonReader) {
		String jsonArrayScope;
		try {
			jsonArrayScope = jsonReader.readScope(ScopedStringReader.SQUARE_BRACKETS);
		} catch (Exception e) {
			throw new JsonSyntaxException("Invalid json array, missing closing bracket ']'", e);
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
			String element = arrayReader.readUntilInclusive(',');
			arrayReader.skipWhitespaces();
			if (element.charAt(element.length() - 1) == ',') {
				if (this.config.strict() && !arrayReader.canRead()) {
					throw new JsonSyntaxException("Invalid json array, expected another element but got nothing");
				}
				element = element.substring(0, element.length() - 1);
			}
			jsonArray.add(this.readJsonElement(new ScopedStringReader(element)));
		}
		return jsonArray;
	}
	
	private @NotNull JsonElement readJsonValue(@NotNull ScopedStringReader reader) {
		reader.skipWhitespaces();
		char next = reader.peek();
		if (next == '"') {
			return new JsonPrimitive(reader.readQuotedString());
		}
		String value = reader.readRemaining().strip();
		if (this.config.strict() ? "null".equals(value) : "null".equalsIgnoreCase(value)) {
			return JsonNull.INSTANCE;
		} else if (this.config.strict() ? "true".equals(value) : "true".equalsIgnoreCase(value)) {
			return new JsonPrimitive(true);
		} else if (this.config.strict() ? "false".equals(value) : "false".equalsIgnoreCase(value)) {
			return new JsonPrimitive(false);
		}
		StringReader valueReader = new StringReader(value);
		try {
			return new JsonPrimitive(valueReader.readNumber());
		} catch (Exception e) {
			if (this.config.strict()) {
				throw new JsonSyntaxException("Invalid json primitive, expected a number but got: '" + value + "'", e);
			}
			return new JsonPrimitive(value);
		}
	}
	
	@Override
	public void close() throws IOException {
		this.reader.readRemaining(); // Assert that there is no remaining content
	}
}
