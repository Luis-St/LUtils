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

package net.luis.utils.io.data.json;

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.json.exception.JsonSyntaxException;
import net.luis.utils.io.reader.StringReader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * A json reader for reading json elements from a {@link String string} or {@link InputProvider input provider}.<br>
 * The reader can be used to read json arrays, objects, primitives and null values.<br>
 * The reader expects only one json element per input.<br>
 *
 * @author Luis-St
 */
public class JsonReader implements AutoCloseable {
	
	/**
	 * The scope stack used to keep track of the current json scope.<br>
	 */
	private final Deque<Character> scope = new ArrayDeque<>();
	/**
	 * The json config used by this reader.<br>
	 */
	private final JsonConfig config;
	/**
	 * The internal reader used to read the json content.<br>
	 */
	private final StringReader reader;
	
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
		this.reader = new StringReader(Objects.requireNonNull(string, "String must not be null"));
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
		this.reader = new StringReader(new InputStreamReader(Objects.requireNonNull(input, "Input must not be null").getStream(), config.charset()));
	}
	
	/**
	 * Reads the next json element from the input.<br>
	 * <p>
	 *     In strict mode, this reader only accepts one json element per input.<br>
	 * </p>
	 * @return The next json element
	 * @throws JsonSyntaxException If the json is invalid
	 * @see #readJsonElement()
	 */
	public @NotNull JsonElement readJson() {
		if (!this.reader.canRead()) {
			throw new JsonSyntaxException("Invalid json, expected content but got nothing");
		}
		this.scope.clear();
		JsonElement element = this.readJsonElement();
		if (!this.scope.isEmpty()) {
			throw new JsonSyntaxException("Invalid json structure, some elements are not closed properly");
		}
		this.reader.skipWhitespaces();
		if (this.config.strict() && this.reader.canRead()) {
			throw new JsonSyntaxException("Invalid json element, expected end of input but got: '" + this.reader.peek() + "'");
		}
		return element;
	}
	
	/**
	 * Reads the next json element from the input.<br>
	 * Supported json elements are objects, arrays, primitives and null values.<br>
	 * @return The read json element
	 * @throws JsonSyntaxException If the json is invalid (depends on the configuration)
	 * @see #readJsonArray()
	 * @see #readJsonObject()
	 * @see #readJsonValue()
	 */
	private @NotNull JsonElement readJsonElement() {
		this.reader.skipWhitespaces();
		char next = this.reader.peek();
		JsonElement element;
		if (next == '{') {
			element = this.readJsonObject();
		} else if (next == '[') {
			element = this.readJsonArray();
		} else {
			element = this.readJsonValue();
		}
		this.reader.skipWhitespaces();
		return element;
	}
	
	/**
	 * Reads a json array from the underlying reader.<br>
	 * The json array is expected to be formatted as follows:<br>
	 * <ul>
	 *     <li>Empty array: {@code []}</li>
	 *     <li>Array with one element: {@code ["value"]}</li>
	 *     <li>Array with multiple elements: {@code ["value1", "value2", "value3"]}</li>
	 * </ul>
	 * <p>
	 *     In strict mode, the reader expects no trailing comma after the last element.<br>
	 * </p>
	 * @return The read json array
	 * @throws JsonSyntaxException If the json array is invalid
	 */
	private @NotNull JsonArray readJsonArray() {
		if (this.reader.peek() != '[') {
			throw new JsonSyntaxException("Invalid json array, expected opening bracket '[' but got: '" + this.reader.peek() + "'");
		}
		this.reader.skip();
		this.scope.push('[');
		
		JsonArray array = new JsonArray();
		int currentScope = this.scope.size();
		
		this.reader.skipWhitespaces();
		while (this.reader.canRead() && this.reader.peek() != ']') {
			array.add(this.readJsonElement());
			if (currentScope > this.scope.size()) {
				throw new JsonSyntaxException("Invalid json structure, some elements are not closed properly");
			}
			this.reader.skipWhitespaces();
			if (this.reader.peek() == ',') {
				this.reader.skip();
				this.reader.skipWhitespaces();
				if (this.config.strict() && this.reader.peek() == ']') {
					throw new JsonSyntaxException("Invalid json array, expected another element but got closing bracket ']'");
				}
			}
		}
		if (!this.reader.canRead()) {
			throw new JsonSyntaxException("Invalid json array, expected closing bracket ']' but got nothing");
		}
		if (this.scope.size() == currentScope) {
			if (this.reader.peek() != ']') {
				throw new JsonSyntaxException("Invalid json array, expected closing bracket ']' but got: '" + this.reader.peek() + "'");
			}
			if (this.scope.pop() != '[') {
				throw new JsonSyntaxException("Invalid json structure, the scope stack is corrupted: " + this.scope);
			}
			this.reader.skip();
		} else {
			throw new JsonSyntaxException("Invalid json array, expected closing bracket ']' but got nothing");
		}
		return array;
	}
	
	/**
	 * Reads a json object from the underlying reader<br>
	 * The json object is expected to be formatted as follows:<br>
	 * <ul>
	 *     <li>Empty object: {@code {}}</li>
	 *     <li>Object with one entry: {@code {"key": "value"}}</li>
	 *     <li>Object with multiple entries: {@code {"key1": "value1", "key2": "value2", "key3": "value3"}'}/li>
	 * </ul>
	 * <p>
	 *     In strict mode, the reader expects the keys to be quoted and no trailing comma after the last entry.<br>
	 * </p>
	 * @return The read json object
	 * @throws JsonSyntaxException If the json object is invalid
	 */
	private @NotNull JsonObject readJsonObject() {
		if (this.reader.peek() != '{') {
			throw new JsonSyntaxException("Invalid json object, expected opening bracket '{' but got: '" + this.reader.peek() + "'");
		}
		this.reader.skip();
		this.scope.push('{');
		
		JsonObject object = new JsonObject();
		int currentScope = this.scope.size();
		
		this.reader.skipWhitespaces();
		while (this.reader.canRead() && this.reader.peek() != '}') {
			this.reader.skipWhitespaces();
			String key;
			if (this.config.strict()) {
				key = this.reader.readQuotedString();
			} else {
				key = this.reader.readString();
				this.reader.skipWhitespaces();
			}
			if (this.reader.peek() != ':') {
				throw new JsonSyntaxException("Invalid json object, expected ':' but got: '" + this.reader.peek() + "'");
			}
			this.reader.skip();
			this.reader.skipWhitespaces();
			
			object.add(key, this.readJsonElement());
			if (currentScope > this.scope.size()) {
				throw new JsonSyntaxException("Invalid json structure, some elements are not closed properly");
			}
			this.reader.skipWhitespaces();
			if (this.reader.peek() == ',') {
				this.reader.skip();
				this.reader.skipWhitespaces();
				if (this.config.strict() && this.reader.peek() == '}') {
					throw new JsonSyntaxException("Invalid json object, expected another entry but got closing bracket '}'");
				}
			}
		}
		if (!this.reader.canRead()) {
			throw new JsonSyntaxException("Invalid json object, expected closing bracket '}' but got nothing");
		}
		if (this.scope.size() == currentScope) {
			if (this.reader.peek() != '}') {
				throw new JsonSyntaxException("Invalid json object, expected closing bracket '}' but got: '" + this.reader.peek() + "'");
			}
			if (this.scope.pop() != '{') {
				throw new JsonSyntaxException("Invalid json structure, the scope stack is corrupted: " + this.scope);
			}
			this.reader.skip();
		} else {
			throw new JsonSyntaxException("Invalid json object, expected closing bracket '}' but got nothing");
		}
		return object;
	}
	
	/**
	 * Reads a json value from the underlying reader.<br>
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
	 *     In non-strict mode, the following values are also accepted:<br>
	 * </p>
	 * <ul>
	 *     <li>{@code NULL} (upper or mixed case)</li>
	 *     <li>{@code TRUE} (upper or mixed case)</li>
	 *     <li>{@code FALSE} (upper or mixed case)</li>
	 * </ul>
	 * @return The read json value
	 * @throws JsonSyntaxException If the json value is invalid (depends on the configuration)
	 */
	private @NotNull JsonElement readJsonValue() {
		this.reader.skipWhitespaces();
		char next = Character.toLowerCase(this.reader.peek());
		if (next == '"') {
			return new JsonPrimitive(this.reader.readQuotedString());
		}
		if (next == 'n') {
			return this.readJsonNull();
		} else if (next == 't' || next == 'f') {
			return this.readJsonBoolean();
		}
		try {
			return new JsonPrimitive(this.reader.readNumber());
		} catch (Exception e) {
			throw new JsonSyntaxException("Invalid json primitive, expected a number but got: '" + next + "'", e);
		}
	}
	
	/**
	 * Reads a json null value from the underlying reader.<br>
	 * The reader expects the next four characters to be 'null'.<br>
	 * <p>
	 *     In strict mode, the reader will throw an exception if the value is not 'null'.<br>
	 *     In non-strict mode, the reader will accept any case of 'null' including mixed-case.<br>
	 * </p>
	 * @return The read json null value (always {@link JsonNull#INSTANCE})
	 * @throws JsonSyntaxException If the json null value is invalid (depends on the configuration)
	 */
	private @NotNull JsonNull readJsonNull() {
		if (Character.toLowerCase(this.reader.peek()) != 'n') {
			throw new JsonSyntaxException("Invalid json null, expected 'null' but got: '" + this.reader.peek() + "'");
		}
		String value = this.reader.read(4);
		if (this.config.strict() ? !"null".equals(value) : !"null".equalsIgnoreCase(value)) {
			throw new JsonSyntaxException("Invalid json null, expected 'null' but got: '" + value + "'");
		}
		return JsonNull.INSTANCE;
	}
	
	/**
	 * Reads a json boolean value from the underlying reader.<br>
	 * The reader expects the next four or five characters to be 'true' or 'false'.<br>
	 * <p>
	 *     In strict mode, the reader will throw a exception if the value is not 'true' or 'false'.<br>
	 *     In non-strict mode, the reader will accept any case of 'true' or 'false' including mixed-case.<br>
	 * </p>
	 * @return The read json boolean value
	 * @throws JsonSyntaxException If the json boolean value is invalid (depends on the configuration)
	 */
	private @NotNull JsonPrimitive readJsonBoolean() {
		char next = Character.toLowerCase(this.reader.peek());
		if (next != 't' && next != 'f') {
			throw new JsonSyntaxException("Invalid json boolean, expected 'true' or 'false' but got: '" + next + "'");
		}
		if (next == 't') {
			String value = this.reader.read(4);
			if (this.config.strict() ? !"true".equals(value) : !"true".equalsIgnoreCase(value)) {
				throw new JsonSyntaxException("Invalid json boolean, expected 'true' but got: '" + value + "'");
			}
			return new JsonPrimitive(true);
		} else {
			String value = this.reader.read(5);
			if (this.config.strict() ? !"false".equals(value) : !"false".equalsIgnoreCase(value)) {
				throw new JsonSyntaxException("Invalid json boolean, expected 'false' but got: '" + value + "'");
			}
			return new JsonPrimitive(false);
		}
	}
	
	@Override
	public void close() throws IOException {
		this.reader.readRemaining(); // Assert that there is no remaining content
	}
}
