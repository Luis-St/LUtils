/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

import net.luis.utils.io.data.DataHelper;
import net.luis.utils.io.data.yaml.exception.YamlTypeException;
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
	 * @throws NullPointerException If the string is null
	 */
	private static @NonNull Object tryParse(@NonNull String string) {
		Objects.requireNonNull(string, "String must not be null");
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
		
		return DataHelper.tryParseNumber(string);
	}
	
	/**
	 * Checks if the given string needs to be quoted in YAML output.<br>
	 *
	 * @param value The string value to check
	 * @return True if the string needs to be quoted, false otherwise
	 * @throws NullPointerException If the value is null
	 */
	private static boolean needsQuoting(@NonNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		if (value.isEmpty()) {
			return true;
		}
		
		String lower = value.toLowerCase();
		if ("true".equals(lower) || "false".equals(lower) || "null".equals(lower) || ".inf".equals(lower) || "+.inf".equals(lower) || "-.inf".equals(lower) || ".nan".equals(lower) || "~".equals(lower)) {
			return false;
		}
		
		char first = value.charAt(0);
		if (YamlHelper.isYamlSpecialCharacter(first) || first == ':') {
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
	 * Returns the name of the type of this yaml scalar in a human-readable format.<br>
	 * Used for debugging and error messages.<br>
	 *
	 * @return The name of the type of this yaml scalar in a human-readable format
	 * @throws IllegalStateException If the type of this yaml scalar is unknown
	 */
	private @NonNull String getName() {
		if (this.isYamlBoolean()) {
			return "yaml boolean";
		} else if (this.isYamlNumber()) {
			return "yaml number";
		} else if (this.isYamlString()) {
			return "yaml string";
		}
		throw new IllegalStateException("Unknown yaml scalar type");
	}
	
	@Override
	public boolean isYamlBoolean() {
		return this.value instanceof Boolean;
	}
	
	@Override
	public boolean isYamlNumber() {
		return this.value instanceof Number;
	}
	
	@Override
	public boolean isYamlByte() {
		return this.value instanceof Byte;
	}
	
	@Override
	public boolean isYamlShort() {
		return this.value instanceof Short;
	}
	
	@Override
	public boolean isYamlInteger() {
		return this.value instanceof Integer;
	}
	
	@Override
	public boolean isYamlLong() {
		return this.value instanceof Long;
	}
	
	@Override
	public boolean isYamlFloat() {
		return this.value instanceof Float;
	}
	
	@Override
	public boolean isYamlDouble() {
		return this.value instanceof Double;
	}
	
	@Override
	public boolean isYamlString() {
		return this.value instanceof String;
	}
	
	@Override
	public boolean getAsBoolean() {
		if (this.isYamlBoolean()) {
			return (boolean) this.value;
		}
		throw new YamlTypeException("Expected a yaml boolean, but found: " + this.getName());
	}
	
	@Override
	public @NonNull Number getAsNumber() {
		if (this.isYamlNumber()) {
			return (Number) this.value;
		}
		throw new YamlTypeException("Expected a yaml number, but found: " + this.getName());
	}
	
	@Override
	public byte getAsByte() {
		if (this.isYamlByte()) {
			return (byte) this.value;
		} else if (this.isYamlNumber()) {
			return this.getAsNumber().byteValue();
		}
		throw new YamlTypeException("Expected a yaml byte, but found: " + this.getName());
	}
	
	@Override
	public short getAsShort() {
		if (this.isYamlShort()) {
			return (short) this.value;
		} else if (this.isYamlNumber()) {
			return this.getAsNumber().shortValue();
		}
		throw new YamlTypeException("Expected a yaml short, but found: " + this.getName());
	}
	
	@Override
	public int getAsInteger() {
		if (this.isYamlInteger()) {
			return (int) this.value;
		} else if (this.isYamlNumber()) {
			return this.getAsNumber().intValue();
		}
		throw new YamlTypeException("Expected a yaml integer, but found: " + this.getName());
	}
	
	@Override
	public long getAsLong() {
		if (this.isYamlLong()) {
			return (long) this.value;
		} else if (this.isYamlNumber()) {
			return this.getAsNumber().longValue();
		}
		throw new YamlTypeException("Expected a yaml long, but found: " + this.getName());
	}
	
	@Override
	public float getAsFloat() {
		if (this.isYamlFloat()) {
			return (float) this.value;
		} else if (this.isYamlNumber()) {
			return this.getAsNumber().floatValue();
		}
		throw new YamlTypeException("Expected a yaml float, but found: " + this.getName());
	}
	
	@Override
	public double getAsDouble() {
		if (this.isYamlDouble()) {
			return (double) this.value;
		} else if (this.isYamlNumber()) {
			return this.getAsNumber().doubleValue();
		}
		throw new YamlTypeException("Expected a yaml double, but found: " + this.getName());
	}
	
	@Override
	public @NonNull String getAsString() {
		if (this.isYamlString()) {
			return (String) this.value;
		} else if (this.isYamlNumber()) {
			return this.getAsNumber().toString();
		} else if (this.isYamlBoolean()) {
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
		if (this.isYamlString()) {
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
				return "\"" + YamlHelper.escapeString(string) + "\"";
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
