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

import net.luis.utils.io.data.yaml.exception.YamlTypeException;
import net.luis.utils.io.reader.StringReader;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a yaml scalar value.<br>
 * A scalar value can be a boolean, number or string.<br>
 *
 * @author Luis-St
 */
public class YamlScalar implements YamlElement {
	
	/**
	 * The value of this yaml scalar.<br>
	 */
	private final Object value;
	
	/**
	 * Constructs a new yaml scalar with the given boolean value.<br>
	 * @param value The boolean value
	 */
	public YamlScalar(boolean value) {
		this.value = value;
	}
	
	/**
	 * Constructs a new yaml scalar with the given number value.<br>
	 *
	 * @param value The number value
	 * @throws NullPointerException If the value is null
	 */
	public YamlScalar(@NonNull Number value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new yaml scalar with the given character value.<br>
	 * @param value The character value
	 */
	public YamlScalar(char value) {
		this.value = String.valueOf(value);
	}
	
	/**
	 * Constructs a new yaml scalar with the given string value.<br>
	 *
	 * @param value The string value
	 * @throws NullPointerException If the value is null
	 */
	public YamlScalar(@NonNull String value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	//region Static helper methods
	
	/**
	 * Tries to parse the given string to a boolean or number.<br>
	 *
	 * @param string The string
	 * @return The parsed value or the string if it could not be parsed
	 */
	private static @NonNull Object tryParse(@NonNull String string) {
		if ("true".equalsIgnoreCase(string) || "false".equalsIgnoreCase(string)) {
			return Boolean.parseBoolean(string);
		}
		if (".inf".equalsIgnoreCase(string) || "+.inf".equalsIgnoreCase(string)) {
			return Double.POSITIVE_INFINITY;
		}
		if ("-.inf".equalsIgnoreCase(string)) {
			return Double.NEGATIVE_INFINITY;
		}
		if (".nan".equalsIgnoreCase(string)) {
			return Double.NaN;
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
	
	/**
	 * Checks if the given string needs to be quoted in YAML output.<br>
	 *
	 * @param value The string value to check
	 * @return True if the string needs to be quoted, false otherwise
	 */
	private static boolean needsQuoting(@NonNull String value) {
		if (value.isEmpty()) {
			return true;
		}
		
		String lower = value.toLowerCase();
		if ("true".equals(lower) || "false".equals(lower) || "null".equals(lower) || ".inf".equals(lower) || "+.inf".equals(lower) || "-.inf".equals(lower) || ".nan".equals(lower) || "~".equals(lower)) {
			return false;
		}
		
		char first = value.charAt(0);
		if (
			first == '#' || first == '&' || first == '*' || first == '!' || first == '|' || first == '>' || first == '\'' || first == '"' ||
				first == '%' || first == '@' || first == '`' || first == '{' || first == '[' || first == '-' || first == '?' || first == ':'
		) {
			return true;
		}
		
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == ':' || c == '#' || c == '\n' || c == '\r' || c == '\t') {
				return true;
			}
		}
		return false;
	}
	//endregion
	
	/**
	 * Escapes special characters in a string for YAML double-quoted output.<br>
	 *
	 * @param string The string to escape
	 * @return The escaped string
	 */
	private static @NonNull String escapeString(@NonNull String string) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
				case '"' -> builder.append("\\\"");
				case '\\' -> builder.append("\\\\");
				case '\n' -> builder.append("\\n");
				case '\r' -> builder.append("\\r");
				case '\t' -> builder.append("\\t");
				default -> builder.append(c);
			}
		}
		return builder.toString();
	}
	
	/**
	 * Returns the name of the type of this yaml scalar in a human-readable format.<br>
	 * Used for debugging and error messages.<br>
	 *
	 * @return The name of the type of this yaml scalar in a human-readable format
	 * @throws IllegalStateException If the type of this yaml scalar is unknown
	 */
	private @NonNull String getName() {
		if (this.isBoolean()) {
			return "yaml boolean";
		} else if (this.isNumber()) {
			return "yaml number";
		} else if (this.isString()) {
			return "yaml string";
		}
		throw new IllegalStateException("Unknown yaml scalar type");
	}
	
	/**
	 * Checks if this yaml scalar is a boolean.<br>
	 * @return True if this yaml scalar is a boolean, false otherwise
	 */
	public boolean isBoolean() {
		return this.value instanceof Boolean;
	}
	
	/**
	 * Checks if this yaml scalar is a number.<br>
	 * @return True if this yaml scalar is a number, false otherwise
	 */
	public boolean isNumber() {
		return this.value instanceof Number;
	}
	
	/**
	 * Checks if this yaml scalar is a byte.<br>
	 * @return True if this yaml scalar is a byte, false otherwise
	 */
	public boolean isByte() {
		return this.value instanceof Byte;
	}
	
	/**
	 * Checks if this yaml scalar is a short.<br>
	 * @return True if this yaml scalar is a short, false otherwise
	 */
	public boolean isShort() {
		return this.value instanceof Short;
	}
	
	/**
	 * Checks if this yaml scalar is an integer.<br>
	 * @return True if this yaml scalar is an integer, false otherwise
	 */
	public boolean isInteger() {
		return this.value instanceof Integer;
	}
	
	/**
	 * Checks if this yaml scalar is a long.<br>
	 * @return True if this yaml scalar is a long, false otherwise
	 */
	public boolean isLong() {
		return this.value instanceof Long;
	}
	
	/**
	 * Checks if this yaml scalar is a float.<br>
	 * @return True if this yaml scalar is a float, false otherwise
	 */
	public boolean isFloat() {
		return this.value instanceof Float;
	}
	
	/**
	 * Checks if this yaml scalar is a double.<br>
	 * @return True if this yaml scalar is a double, false otherwise
	 */
	public boolean isDouble() {
		return this.value instanceof Double;
	}
	
	/**
	 * Checks if this yaml scalar is a string.<br>
	 * @return True if this yaml scalar is a string, false otherwise
	 */
	public boolean isString() {
		return this.value instanceof String;
	}
	
	/**
	 * Converts this yaml scalar to a boolean.<br>
	 * @return This yaml scalar as a boolean
	 * @throws YamlTypeException If this yaml scalar is not a boolean
	 */
	public boolean getAsBoolean() {
		if (this.isBoolean()) {
			return (boolean) this.value;
		}
		throw new YamlTypeException("Expected a yaml boolean, but found: " + this.getName());
	}
	
	/**
	 * Converts this yaml scalar to a number.<br>
	 * @return This yaml scalar as a number
	 * @throws YamlTypeException If this yaml scalar is not a number
	 */
	public @NonNull Number getAsNumber() {
		if (this.isNumber()) {
			return (Number) this.value;
		}
		throw new YamlTypeException("Expected a yaml number, but found: " + this.getName());
	}
	
	/**
	 * Converts this yaml scalar to a byte.<br>
	 * @return This yaml scalar as a byte
	 * @throws YamlTypeException If this yaml scalar is not a number
	 */
	public byte getAsByte() {
		if (this.isByte()) {
			return (byte) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().byteValue();
		}
		throw new YamlTypeException("Expected a yaml byte, but found: " + this.getName());
	}
	
	/**
	 * Converts this yaml scalar to a short.<br>
	 * @return This yaml scalar as a short
	 * @throws YamlTypeException If this yaml scalar is not a number
	 */
	public short getAsShort() {
		if (this.isShort()) {
			return (short) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().shortValue();
		}
		throw new YamlTypeException("Expected a yaml short, but found: " + this.getName());
	}
	
	/**
	 * Converts this yaml scalar to an integer.<br>
	 * @return This yaml scalar as an integer
	 * @throws YamlTypeException If this yaml scalar is not a number
	 */
	public int getAsInteger() {
		if (this.isInteger()) {
			return (int) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().intValue();
		}
		throw new YamlTypeException("Expected a yaml integer, but found: " + this.getName());
	}
	
	/**
	 * Converts this yaml scalar to a long.<br>
	 * @return This yaml scalar as a long
	 * @throws YamlTypeException If this yaml scalar is not a number
	 */
	public long getAsLong() {
		if (this.isLong()) {
			return (long) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().longValue();
		}
		throw new YamlTypeException("Expected a yaml long, but found: " + this.getName());
	}
	
	/**
	 * Converts this yaml scalar to a float.<br>
	 * @return This yaml scalar as a float
	 * @throws YamlTypeException If this yaml scalar is not a number
	 */
	public float getAsFloat() {
		if (this.isFloat()) {
			return (float) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().floatValue();
		}
		throw new YamlTypeException("Expected a yaml float, but found: " + this.getName());
	}
	
	/**
	 * Converts this yaml scalar to a double.<br>
	 * @return This yaml scalar as a double
	 * @throws YamlTypeException If this yaml scalar is not a number
	 */
	public double getAsDouble() {
		if (this.isDouble()) {
			return (double) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().doubleValue();
		}
		throw new YamlTypeException("Expected a yaml double, but found: " + this.getName());
	}
	
	/**
	 * Converts this yaml scalar to a string.<br>
	 * @return This yaml scalar as a string
	 */
	public @NonNull String getAsString() {
		if (this.isString()) {
			return (String) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().toString();
		} else if (this.isBoolean()) {
			return String.valueOf(this.getAsBoolean());
		}
		throw new YamlTypeException("Expected a yaml string, but found: " + this.getName());
	}
	
	/**
	 * If this yaml scalar is a string, tries to parse it to a boolean or number and returns a new yaml scalar with the parsed value.<br>
	 * If the parsing fails, returns this yaml scalar.<br>
	 *
	 * @return A yaml scalar with the parsed value or this yaml scalar if the parsing fails
	 */
	public @NonNull YamlScalar getAsParsedYamlScalar() {
		if (this.isString()) {
			Object parsed = tryParse(this.getAsString());
			
			return switch (parsed) {
				case Boolean bool -> new YamlScalar(bool);
				case Number number -> new YamlScalar(number);
				case String string -> new YamlScalar(string);
				default -> this;
			};
		}
		return this;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof YamlScalar that)) return false;
		
		return this.value.equals(that.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.value);
	}
	
	@Override
	public String toString() {
		return this.toString(YamlConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull YamlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		
		if (this.value instanceof String string) {
			if (needsQuoting(string)) {
				// Use double quotes and escape special characters
				return "\"" + escapeString(string) + "\"";
			}
			return string;
		} else if (this.value instanceof Double d) {
			if (d.isInfinite()) {
				return d > 0 ? ".inf" : "-.inf";
			} else if (d.isNaN()) {
				return ".nan";
			}
		} else if (this.value instanceof Float f) {
			if (f.isInfinite()) {
				return f > 0 ? ".inf" : "-.inf";
			} else if (f.isNaN()) {
				return ".nan";
			}
		}
		return String.valueOf(this.value);
	}
	//endregion
}
