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

package net.luis.utils.io.data.ini;

import net.luis.utils.io.data.DataHelper;
import net.luis.utils.io.data.ini.exception.IniTypeException;
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
	 * Returns the name of the type of this ini value in a human-readable format.<br>
	 * Used for debugging and error messages.<br>
	 *
	 * @return The name of the type of this ini value in a human-readable format
	 * @throws IllegalStateException If the type of this ini value is unknown
	 */
	private @NonNull String getName() {
		if (this.isIniBoolean()) {
			return "Ini boolean";
		} else if (this.isIniNumber()) {
			return "Ini number";
		} else if (this.isIniString()) {
			return "Ini string";
		}
		throw new IllegalStateException("Unknown ini value type");
	}
	
	@Override
	public boolean isIniBoolean() {
		return this.value instanceof Boolean;
	}
	
	@Override
	public boolean getAsBoolean() {
		if (this.isIniBoolean()) {
			return (boolean) this.value;
		}
		throw new IniTypeException("Expected an ini boolean, but found: " + this.getName());
	}
	
	@Override
	public boolean isIniNumber() {
		return this.value instanceof Number;
	}
	
	@Override
	public @NonNull Number getAsNumber() {
		if (this.isIniNumber()) {
			return (Number) this.value;
		}
		throw new IniTypeException("Expected an ini number, but found: " + this.getName());
	}
	
	@Override
	public boolean isIniByte() {
		return this.value instanceof Byte;
	}
	
	@Override
	public byte getAsByte() {
		if (this.isIniByte()) {
			return (byte) this.value;
		} else if (this.isIniNumber()) {
			return this.getAsNumber().byteValue();
		}
		throw new IniTypeException("Expected an ini byte, but found: " + this.getName());
	}
	
	@Override
	public boolean isIniShort() {
		return this.value instanceof Short;
	}
	
	@Override
	public short getAsShort() {
		if (this.isIniShort()) {
			return (short) this.value;
		} else if (this.isIniNumber()) {
			return this.getAsNumber().shortValue();
		}
		throw new IniTypeException("Expected an ini short, but found: " + this.getName());
	}
	
	@Override
	public boolean isIniInteger() {
		return this.value instanceof Integer;
	}
	
	@Override
	public int getAsInteger() {
		if (this.isIniInteger()) {
			return (int) this.value;
		} else if (this.isIniNumber()) {
			return this.getAsNumber().intValue();
		}
		throw new IniTypeException("Expected an ini integer, but found: " + this.getName());
	}
	
	@Override
	public boolean isIniLong() {
		return this.value instanceof Long;
	}
	
	@Override
	public long getAsLong() {
		if (this.isIniLong()) {
			return (long) this.value;
		} else if (this.isIniNumber()) {
			return this.getAsNumber().longValue();
		}
		throw new IniTypeException("Expected an ini long, but found: " + this.getName());
	}
	
	@Override
	public boolean isIniFloat() {
		return this.value instanceof Float;
	}
	
	@Override
	public float getAsFloat() {
		if (this.isIniFloat()) {
			return (float) this.value;
		} else if (this.isIniNumber()) {
			return this.getAsNumber().floatValue();
		}
		throw new IniTypeException("Expected an ini float, but found: " + this.getName());
	}
	
	@Override
	public boolean isIniDouble() {
		return this.value instanceof Double;
	}
	
	@Override
	public double getAsDouble() {
		if (this.isIniDouble()) {
			return (double) this.value;
		} else if (this.isIniNumber()) {
			return this.getAsNumber().doubleValue();
		}
		throw new IniTypeException("Expected an ini double, but found: " + this.getName());
	}
	
	@Override
	public boolean isIniString() {
		return this.value instanceof String;
	}
	
	@Override
	public @NonNull String getAsString() {
		if (this.isIniString()) {
			return (String) this.value;
		} else if (this.isIniNumber()) {
			return this.getAsNumber().toString();
		} else if (this.isIniBoolean()) {
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
		if (this.isIniString()) {
			Object parsed = DataHelper.tryParse(this.getAsString());
			
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
