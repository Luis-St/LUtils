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

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.json.exception.JsonSyntaxException;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringReader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * A json reader for reading json elements from a {@link String string} or {@link InputProvider input provider}.<br>
 * The reader can be used to read json arrays, objects, primitives and null values.<br>
 * The reader expects only one json element per input.<br>
 *
 * @author Luis-St
 */
public class JsonReader implements AutoCloseable {
	
	/**
	 * The json config used by this reader.<br>
	 */
	private final JsonConfig config;
	/**
	 * The internal reader used to read the json content.<br>
	 */
	private final ScopedStringReader reader;
	
	/**
	 * Constructs a new json reader with the given string and the default configuration.<br>
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public JsonReader(@NotNull String string) {
		this(string, JsonConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new json reader with the given string and configuration.<br>
	 * @param string The string to read from
	 * @param config The configuration to use
	 * @throws NullPointerException If the string or configuration is null
	 */
	public JsonReader(@NotNull String string, @NotNull JsonConfig config) {
		this.config = Objects.requireNonNull(config, "Json config must not be null");
		this.reader = new ScopedStringReader(Objects.requireNonNull(string, "String must not be null"));
	}
	
	/**
	 * Constructs a new json reader with the given input and the default configuration.<br>
	 * @param input The input to create the reader for
	 * @throws NullPointerException If the input is null
	 */
	public JsonReader(@NotNull InputProvider input) {
		this(input, JsonConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new json reader with the given input and configuration.<br>
	 * @param input The input to create the reader for
	 * @param config The configuration to use
	 * @throws NullPointerException If the input or configuration is null
	 */
	public JsonReader(@NotNull InputProvider input, @NotNull JsonConfig config) {
		this.config = Objects.requireNonNull(config, "Json config must not be null");
		this.reader = new ScopedStringReader(new InputStreamReader(Objects.requireNonNull(input, "Input must not be null").getStream(), config.charset()));
	}
	
	/**
	 * Gets the last none whitespace character of the given string.<br>
	 * The whitespace characters a determined by {@link Character#isWhitespace(char)}.<br>
	 * @param string The string to get the last none whitespace character of
	 * @return The last none whitespace character of the string or 0 if the string is empty
	 * @throws NullPointerException If the string is null
	 */
	private static char lastNoneWhitespaceChar(@NotNull String string) {
		Objects.requireNonNull(string, "String must not be null");
		for (int i = string.length() - 1; i >= 0; i--) {
			char c = string.charAt(i);
			if (!Character.isWhitespace(c)) {
				return c;
			}
		}
		return 0;
	}
	
	/**
	 * Reads the next json element from the input.<br>
	 * @return The next json element
	 * @throws JsonSyntaxException If the json is invalid
	 * @see #readJsonElement(ScopedStringReader)
	 */
	public @NotNull JsonElement readJson() {
		if (!this.reader.canRead()) {
			throw new JsonSyntaxException("Invalid json, expected content but got nothing");
		}
		return this.readJsonElement(this.reader);
	}
	
	/**
	 * Reads the next json element from the input.<br>
	 * The reader supports reading of json objects, arrays, primitives and null values.<br>
	 * <p>
	 *     In strict mode, the reader only accepts one json element per input.<br>
	 * </p>
	 * @param reader The reader to read the json element from
	 * @return The read json element
	 * @throws JsonSyntaxException If the json is invalid (depends on the configuration)
	 * @see #readJsonArray(ScopedStringReader)
	 * @see #readJsonObject(ScopedStringReader)
	 * @see #readJsonValue(ScopedStringReader)
	 */
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
	
	/**
	 * Reads a json array from the given reader.<br>
	 * The reader expects the json array to be formatted as follows:<br>
	 * <ul>
	 *     <li>Empty array: {@code []}</li>
	 *     <li>Array with one element: {@code ["value"]}</li>
	 *     <li>Array with multiple elements: {@code ["value1", "value2", "value3"]}</li>
	 * </ul>
	 * <p>
	 *     In strict mode, the reader expects no trailing comma after the last element.<br>
	 * </p>
	 * @param jsonReader The reader to read the json array from
	 * @return The read json array
	 * @throws JsonSyntaxException If the json array is invalid
	 */
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
	
	/**
	 * Reads a json object from the given reader.<br>
	 * The reader expects the json object to be formatted as follows:<br>
	 * <ul>
	 *     <li>Empty object: {@code {}}</li>
	 *     <li>Object with one entry: {@code {"key": "value"}}</li>
	 *     <li>Object with multiple entries: {@code {"key1": "value1", "key2": "value2", "key3": "value3"}'}/li>
	 * </ul>
	 * <p>
	 *     In strict mode, the reader expects the keys to be quoted and no trailing comma after the last entry.<br>
	 * </p>
	 * @param jsonReader The reader to read the json object from
	 * @return The read json object
	 * @throws JsonSyntaxException If the json object is invalid
	 */
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
	
	/**
	 * Reads a json value from the given reader.<br>
	 * A json value can be either a string, number, boolean or null.<br>
	 * <p>
	 *     In strict mode, the reader will throw a exception if the value is not a valid json primitive.<br>
	 *     It also accepts only the following values:<br>
	 * </p>
	 * <ul>
	 *     <li>{@code null} (lower-case)</li>
	 *     <li>{@code true} (lower-case)</li>
	 *     <li>{@code false} (lower-case)</li>
	 * </ul>
	 * <p>
	 *     In non-strict mode, the reader will convert the value to a string if it is not a valid json primitive.<br>
	 *     If the value is a valid json primitive, the reader will return it as a json primitive.<br>
	 *     It also accepts the following values in addition to the strict values:<br>
	 * </p>
	 * <ul>
	 *     <li>{@code NULL} (upper or mixed case)</li>
	 *     <li>{@code TRUE} (upper or mixed case)</li>
	 *     <li>{@code FALSE} (upper or mixed case)</li>
	 * </ul>
	 * @param reader The reader to read the json value from
	 * @return The read json value
	 * @throws JsonSyntaxException If the json value is invalid (depends on the configuration)
	 */
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
