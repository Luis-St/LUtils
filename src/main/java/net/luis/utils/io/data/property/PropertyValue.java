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

package net.luis.utils.io.data.property;

import net.luis.utils.io.data.property.exception.PropertyTypeException;
import net.luis.utils.io.reader.StringReader;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a property primitive value.<br>
 * A primitive value can be a boolean, number or string.<br>
 *
 * @author Luis-St
 */
public class PropertyValue implements PropertyElement {
	
	/**
	 * The value of this property value.<br>
	 */
	private final Object value;
	
	/**
	 * Constructs a new property value with the given boolean value.<br>
	 * @param value The boolean value
	 */
	public PropertyValue(boolean value) {
		this.value = value;
	}
	
	/**
	 * Constructs a new property value with the given number value.<br>
	 *
	 * @param value The number value
	 * @throws NullPointerException If the value is null
	 */
	public PropertyValue(@NonNull Number value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new property value with the given character value.<br>
	 * @param value The character value
	 */
	public PropertyValue(char value) {
		this.value = String.valueOf(value);
	}
	
	/**
	 * Constructs a new property value with the given string value.<br>
	 *
	 * @param value The string value
	 * @throws NullPointerException If the value is null
	 */
	public PropertyValue(@NonNull String value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
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
	 * Returns the name of the type of this property value in a human-readable format.<br>
	 * Used for debugging and error messages.<br>
	 *
	 * @return The name of the type of this property value in a human-readable format
	 * @throws IllegalStateException If the type of this property value is unknown
	 */
	private @NonNull String getName() {
		if (this.isBoolean()) {
			return "property boolean";
		} else if (this.isNumber()) {
			return "property number";
		} else if (this.isString()) {
			return "property string";
		}
		throw new IllegalStateException("Unknown property value type");
	}
	
	@Override
	public boolean isBoolean() {
		return this.value instanceof Boolean;
	}
	
	@Override
	public boolean getAsBoolean() {
		if (this.isBoolean()) {
			return (boolean) this.value;
		}
		throw new PropertyTypeException("Expected a property boolean, but found: " + this.getName());
	}
	
	@Override
	public boolean isNumber() {
		return this.value instanceof Number;
	}
	
	@Override
	public @NonNull Number getAsNumber() {
		if (this.isNumber()) {
			return (Number) this.value;
		}
		throw new PropertyTypeException("Expected a property number, but found: " + this.getName());
	}
	
	@Override
	public boolean isByte() {
		return this.value instanceof Byte;
	}
	
	@Override
	public byte getAsByte() {
		if (this.isByte()) {
			return (byte) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().byteValue();
		}
		throw new PropertyTypeException("Expected a property byte, but found: " + this.getName());
	}
	
	@Override
	public boolean isShort() {
		return this.value instanceof Short;
	}
	
	@Override
	public short getAsShort() {
		if (this.isShort()) {
			return (short) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().shortValue();
		}
		throw new PropertyTypeException("Expected a property short, but found: " + this.getName());
	}
	
	@Override
	public boolean isInteger() {
		return this.value instanceof Integer;
	}
	
	@Override
	public int getAsInteger() {
		if (this.isInteger()) {
			return (int) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().intValue();
		}
		throw new PropertyTypeException("Expected a property integer, but found: " + this.getName());
	}
	
	@Override
	public boolean isLong() {
		return this.value instanceof Long;
	}
	
	@Override
	public long getAsLong() {
		if (this.isLong()) {
			return (long) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().longValue();
		}
		throw new PropertyTypeException("Expected a property long, but found: " + this.getName());
	}
	
	@Override
	public boolean isFloat() {
		return this.value instanceof Float;
	}
	
	@Override
	public float getAsFloat() {
		if (this.isFloat()) {
			return (float) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().floatValue();
		}
		throw new PropertyTypeException("Expected a property float, but found: " + this.getName());
	}
	
	@Override
	public boolean isDouble() {
		return this.value instanceof Double;
	}
	
	@Override
	public double getAsDouble() {
		if (this.isDouble()) {
			return (double) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().doubleValue();
		}
		throw new PropertyTypeException("Expected a property double, but found: " + this.getName());
	}
	
	@Override
	public boolean isString() {
		return this.value instanceof String;
	}
	
	@Override
	public @NonNull String getAsString() {
		if (this.isString()) {
			return (String) this.value;
		} else if (this.isNumber()) {
			return this.getAsNumber().toString();
		} else if (this.isBoolean()) {
			return String.valueOf(this.getAsBoolean());
		}
		throw new PropertyTypeException("Expected a property string, but found: " + this.getName());
	}
	
	/**
	 * If this property value is a string, tries to parse it to a boolean or number
	 * and returns a new property value with the parsed value.<br>
	 * If the parsing fails, returns this property value.<br>
	 *
	 * @return A property value with the parsed value or this property value if the parsing fails
	 */
	public @NonNull PropertyValue getAsParsedPropertyValue() {
		if (this.isString()) {
			Object parsed = tryParse(this.getAsString());
			
			return switch (parsed) {
				case Boolean bool -> new PropertyValue(bool);
				case Number number -> new PropertyValue(number);
				case String string -> new PropertyValue(string);
				default -> this;
			};
		}
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PropertyValue that)) return false;
		
		return this.value.equals(that.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.value);
	}
	
	@Override
	public String toString() {
		return this.toString(PropertyConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull PropertyConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		return String.valueOf(this.value);
	}
}
