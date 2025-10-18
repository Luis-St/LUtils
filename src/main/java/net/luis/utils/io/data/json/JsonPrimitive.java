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

import net.luis.utils.io.data.json.exception.JsonTypeException;
import net.luis.utils.io.reader.StringReader;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a json primitive value.<br>
 * A primitive value can be a boolean, number or string.<br>
 *
 * @author Luis-St
 */
public class JsonPrimitive implements JsonElement {
	
	/**
	 * The value of this json primitive.<br>
	 */
	private final Object value;
	
	/**
	 * Constructs a new json primitive with the given boolean value.<br>
	 * @param value The boolean value
	 */
	public JsonPrimitive(boolean value) {
		this.value = value;
	}
	
	/**
	 * Constructs a new json primitive with the given number value.<br>
	 *
	 * @param value The number value
	 * @throws NullPointerException If the value is null
	 */
	public JsonPrimitive(@NotNull Number value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new json primitive with the given character value.<br>
	 * @param value The character value
	 */
	public JsonPrimitive(char value) {
		this.value = String.valueOf(value);
	}
	
	/**
	 * Constructs a new json primitive with the given string value.<br>
	 *
	 * @param value The string value
	 * @throws NullPointerException If the value is null
	 */
	public JsonPrimitive(@NotNull String value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	//region Static helper methods
	
	/**
	 * Tries to parse the given string to a boolean or number.<br>
	 *
	 * @param string The string
	 * @return The parsed value or the string if it could not be parsed
	 */
	private static @NotNull Object tryParse(@NotNull String string) {
		if ("true".equalsIgnoreCase(string) || "false".equalsIgnoreCase(string)) {
			return Boolean.parseBoolean(string);
		}
		StringReader reader = new StringReader(string);
		try {
			Number number = reader.readNumber();
			reader.skipWhitespaces();
			if (reader.canRead()) {
				return string;
			}
			return number;
		} catch (Exception e) {
			return string;
		}
	}
	//endregion
	
	/**
	 * Returns the name of the type of this json primitive in a human-readable format.<br>
	 * Used for debugging and error messages.<br>
	 *
	 * @return The name of the type of this json primitive in a human-readable format
	 * @throws IllegalStateException If the type of this json primitive is unknown
	 */
	private @NotNull String getName() {
		if (this.isJsonBoolean()) {
			return "json boolean";
		} else if (this.isJsonNumber()) {
			return "json number";
		} else if (this.isJsonString()) {
			return "json string";
		}
		throw new IllegalStateException("Unknown json primitive type");
	}
	
	/**
	 * Checks if this json primitive is a json boolean.<br>
	 * @return True if this json primitive is a json boolean, false otherwise
	 */
	public boolean isJsonBoolean() {
		return this.value instanceof Boolean;
	}
	
	/**
	 * Converts this json primitive to a boolean.<br>
	 * @return This json primitive as a boolean
	 * @throws JsonTypeException If this json primitive is not a json boolean
	 */
	public boolean getAsBoolean() {
		if (this.isJsonBoolean()) {
			return (boolean) this.value;
		}
		throw new JsonTypeException("Expected a json boolean, but found: " + this.getName());
	}
	
	/**
	 * Checks if this json primitive is a json number.<br>
	 * @return True if this json primitive is a json number, false otherwise
	 */
	public boolean isJsonNumber() {
		return this.value instanceof Number;
	}
	
	/**
	 * Converts this json primitive to a number.<br>
	 * @return This json primitive as a number
	 * @throws JsonTypeException If this json primitive is not a json number
	 */
	public @NotNull Number getAsNumber() {
		if (this.isJsonNumber()) {
			return (Number) this.value;
		}
		throw new JsonTypeException("Expected a json number, but found: " + this.getName());
	}
	
	/**
	 * Checks if this json primitive is a json byte.<br>
	 * @return True if this json primitive is a json byte, false otherwise
	 */
	public boolean isJsonByte() {
		return this.value instanceof Byte;
	}
	
	/**
	 * Converts this json primitive to a byte.<br>
	 * @return This json primitive as a byte
	 * @throws JsonTypeException If this json primitive is not a json byte
	 */
	public byte getAsByte() {
		if (this.isJsonByte()) {
			return (byte) this.value;
		} else if (this.isJsonNumber()) {
			return this.getAsNumber().byteValue();
		}
		throw new JsonTypeException("Expected a json byte, but found: " + this.getName());
	}
	
	/**
	 * Checks if this json primitive is a json short.<br>
	 * @return True if this json primitive is a json short, false otherwise
	 */
	public boolean isJsonShort() {
		return this.value instanceof Short;
	}
	
	/**
	 * Converts this json primitive to a short.<br>
	 * @return This json primitive as a short
	 * @throws JsonTypeException If this json primitive is not a json short
	 */
	public short getAsShort() {
		if (this.isJsonShort()) {
			return (short) this.value;
		} else if (this.isJsonNumber()) {
			return this.getAsNumber().shortValue();
		}
		throw new JsonTypeException("Expected a json short, but found: " + this.getName());
	}
	
	/**
	 * Checks if this json primitive is a json integer.<br>
	 * @return True if this json primitive is a json integer, false otherwise
	 */
	public boolean isJsonInteger() {
		return this.value instanceof Integer;
	}
	
	/**
	 * Converts this json primitive to an integer.<br>
	 * @return This json primitive as an integer
	 * @throws JsonTypeException If this json primitive is not a json integer
	 */
	public int getAsInteger() {
		if (this.isJsonInteger()) {
			return (int) this.value;
		} else if (this.isJsonNumber()) {
			return this.getAsNumber().intValue();
		}
		throw new JsonTypeException("Expected a json integer, but found: " + this.getName());
	}
	
	/**
	 * Checks if this json primitive is a json long.<br>
	 * @return True if this json primitive is a json long, false otherwise
	 */
	public boolean isJsonLong() {
		return this.value instanceof Long;
	}
	
	/**
	 * Converts this json primitive to a long.<br>
	 * @return This json primitive as a long
	 * @throws JsonTypeException If this json primitive is not a json long
	 */
	public long getAsLong() {
		if (this.isJsonLong()) {
			return (long) this.value;
		} else if (this.isJsonNumber()) {
			return this.getAsNumber().longValue();
		}
		throw new JsonTypeException("Expected a json long, but found: " + this.getName());
	}
	
	/**
	 * Checks if this json primitive is a json float.<br>
	 * @return True if this json primitive is a json float, false otherwise
	 */
	public boolean isJsonFloat() {
		return this.value instanceof Float;
	}
	
	/**
	 * Converts this json primitive to a float.<br>
	 * @return This json primitive as a float
	 * @throws JsonTypeException If this json primitive is not a json float
	 */
	public float getAsFloat() {
		if (this.isJsonFloat()) {
			return (float) this.value;
		} else if (this.isJsonNumber()) {
			return this.getAsNumber().floatValue();
		}
		throw new JsonTypeException("Expected a json float, but found: " + this.getName());
	}
	
	/**
	 * Checks if this json primitive is a json double.<br>
	 * @return True if this json primitive is a json double, false otherwise
	 */
	public boolean isJsonDouble() {
		return this.value instanceof Double;
	}
	
	/**
	 * Converts this json primitive to a double.<br>
	 * @return This json primitive as a double
	 * @throws JsonTypeException If this json primitive is not a json double
	 */
	public double getAsDouble() {
		if (this.isJsonDouble()) {
			return (double) this.value;
		} else if (this.isJsonNumber()) {
			return this.getAsNumber().doubleValue();
		}
		throw new JsonTypeException("Expected a json double, but found: " + this.getName());
	}
	
	/**
	 * Checks if this json primitive is a json string.<br>
	 * @return True if this json primitive is a json string, false otherwise
	 */
	public boolean isJsonString() {
		return this.value instanceof String;
	}
	
	/**
	 * Converts this json primitive to a string.<br>
	 * @return This json primitive as a string
	 * @throws JsonTypeException If this json primitive is not a json string
	 */
	public @NotNull String getAsString() {
		if (this.isJsonString()) {
			return (String) this.value;
		} else if (this.isJsonNumber()) {
			return this.getAsNumber().toString();
		} else if (this.isJsonBoolean()) {
			return String.valueOf(this.getAsBoolean());
		}
		throw new JsonTypeException("Expected a json string, but found: " + this.getName());
	}
	
	/**
	 * If this json primitive is a json string, tries to parse it to a boolean or number and returns a new json primitive with the parsed value.<br>
	 * If the parsing fails, returns this json primitive.<br>
	 *
	 * @return A json primitive with the parsed value or this json primitive if the parsing fails
	 */
	public @NotNull JsonPrimitive getAsParsedJsonPrimitive() {
		if (this.isJsonString()) {
			Object parsed = tryParse(this.getAsString());
			
			return switch (parsed) {
				case Boolean bool -> new JsonPrimitive(bool);
				case Number number -> new JsonPrimitive(number);
				case String string -> new JsonPrimitive(string);
				default -> this;
			};
		}
		return this;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof JsonPrimitive that)) return false;
		
		return this.value.equals(that.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.value);
	}
	
	@Override
	public String toString() {
		return this.toString(JsonConfig.DEFAULT);
	}
	
	@Override
	public @NotNull String toString(@NotNull JsonConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		
		if (this.value instanceof String string) {
			return "\"" + string + "\"";
		}
		return String.valueOf(this.value);
	}
	//endregion
}
