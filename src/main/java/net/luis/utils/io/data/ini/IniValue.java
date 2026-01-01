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

package net.luis.utils.io.data.ini;

import net.luis.utils.io.data.ini.exception.IniTypeException;
import net.luis.utils.io.reader.StringReader;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents an ini primitive value.<br>
 * A primitive value can be a boolean, number or string.<br>
 *
 * @author Luis-St
 */
public class IniValue implements IniElement {
	
	/**
	 * The value of this ini value.<br>
	 */
	private final Object value;
	
	/**
	 * Constructs a new ini value with the given boolean value.<br>
	 * @param value The boolean value
	 */
	public IniValue(boolean value) {
		this.value = value;
	}
	
	/**
	 * Constructs a new ini value with the given number value.<br>
	 *
	 * @param value The number value
	 * @throws NullPointerException If the value is null
	 */
	public IniValue(@NonNull Number value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new ini value with the given character value.<br>
	 * @param value The character value
	 */
	public IniValue(char value) {
		this.value = String.valueOf(value);
	}
	
	/**
	 * Constructs a new ini value with the given string value.<br>
	 *
	 * @param value The string value
	 * @throws NullPointerException If the value is null
	 */
	public IniValue(@NonNull String value) {
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
	 * Returns the name of the type of this ini value in a human-readable format.<br>
	 * Used for debugging and error messages.<br>
	 *
	 * @return The name of the type of this ini value in a human-readable format
	 * @throws IllegalStateException If the type of this ini value is unknown
	 */
	private @NonNull String getName() {
		if (this.isBoolean()) {
			return "Ini boolean";
		} else if (this.isNumber()) {
			return "Ini number";
		} else if (this.isString()) {
			return "Ini string";
		}
		throw new IllegalStateException("Unknown ini value type");
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
		throw new IniTypeException("Expected an ini boolean, but found: " + this.getName());
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
		throw new IniTypeException("Expected an ini number, but found: " + this.getName());
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
		throw new IniTypeException("Expected an ini byte, but found: " + this.getName());
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
		throw new IniTypeException("Expected an ini short, but found: " + this.getName());
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
		throw new IniTypeException("Expected an ini integer, but found: " + this.getName());
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
		throw new IniTypeException("Expected an ini long, but found: " + this.getName());
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
		throw new IniTypeException("Expected an ini float, but found: " + this.getName());
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
		throw new IniTypeException("Expected an ini double, but found: " + this.getName());
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
		throw new IniTypeException("Expected an ini string, but found: " + this.getName());
	}
	
	/**
	 * If this ini value is a string, tries to parse it to a boolean or number
	 * and returns a new ini value with the parsed value.<br>
	 * If the parsing fails, returns this ini value.<br>
	 *
	 * @return An ini value with the parsed value or this ini value if the parsing fails
	 */
	public @NonNull IniValue getAsParsedIniValue() {
		if (this.isString()) {
			Object parsed = tryParse(this.getAsString());
			
			return switch (parsed) {
				case Boolean bool -> new IniValue(bool);
				case Number number -> new IniValue(number);
				case String string -> new IniValue(string);
				default -> this;
			};
		}
		return this;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IniValue that)) return false;
		
		return this.value.equals(that.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.value);
	}
	
	@Override
	public String toString() {
		return this.toString(IniConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull IniConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		return String.valueOf(this.value);
	}
	//endregion
}
