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

package net.luis.utils.io.data.toon;

import net.luis.utils.io.data.toon.exception.ToonTypeException;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a TOON scalar value.<br>
 * A TOON value can be a boolean, number, or string.<br>
 *
 * @author Luis-St
 */
public class ToonValue implements ToonElement {
	
	/**
	 * The value of this TOON value.<br>
	 */
	private final Object value;
	
	/**
	 * Constructs a new TOON value with the given boolean value.<br>
	 * @param value The boolean value
	 */
	public ToonValue(boolean value) {
		this.value = value;
	}
	
	/**
	 * Constructs a new TOON value with the given number value.<br>
	 *
	 * @param value The number value
	 * @throws NullPointerException If the value is null
	 */
	public ToonValue(@NonNull Number value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Constructs a new TOON value with the given character value.<br>
	 * @param value The character value
	 */
	public ToonValue(char value) {
		this.value = String.valueOf(value);
	}
	
	/**
	 * Constructs a new TOON value with the given string value.<br>
	 *
	 * @param value The string value
	 * @throws NullPointerException If the value is null
	 */
	public ToonValue(@NonNull String value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Returns the name of the type of this TOON value in a human-readable format.<br>
	 * Used for debugging and error messages.<br>
	 *
	 * @return The name of the type of this TOON value in a human-readable format
	 * @throws IllegalStateException If the type of this TOON value is unknown
	 */
	private @NonNull String getName() {
		if (this.isTomlBoolean()) {
			return "toon boolean";
		} else if (this.isTomlNumber()) {
			return "toon number";
		} else if (this.isTomlString()) {
			return "toon string";
		}
		throw new IllegalStateException("Unknown TOON value type");
	}
	
	/**
	 * Checks if this value is a boolean.<br>
	 * @return True if this value is a boolean, false otherwise
	 */
	private boolean isTomlBoolean() {
		return this.value instanceof Boolean;
	}
	
	/**
	 * Checks if this value is a number.<br>
	 * @return True if this value is a number, false otherwise
	 */
	private boolean isTomlNumber() {
		return this.value instanceof Number;
	}
	
	/**
	 * Checks if this value is a string.<br>
	 * @return True if this value is a string, false otherwise
	 */
	private boolean isTomlString() {
		return this.value instanceof String;
	}
	
	@Override
	public boolean isToonBoolean() {
		return this.value instanceof Boolean;
	}
	
	@Override
	public boolean getAsBoolean() {
		if (this.isToonBoolean()) {
			return (boolean) this.value;
		}
		throw new ToonTypeException("Expected a TOON boolean, but found: " + this.getName());
	}
	
	@Override
	public boolean isToonNumber() {
		return this.value instanceof Number;
	}
	
	@Override
	public @NonNull Number getAsNumber() {
		if (this.isToonNumber()) {
			return (Number) this.value;
		}
		throw new ToonTypeException("Expected a TOON number, but found: " + this.getName());
	}
	
	@Override
	public boolean isToonByte() {
		return this.value instanceof Byte;
	}
	
	@Override
	public byte getAsByte() {
		if (this.isToonByte()) {
			return (byte) this.value;
		} else if (this.isToonNumber()) {
			return this.getAsNumber().byteValue();
		}
		throw new ToonTypeException("Expected a TOON byte, but found: " + this.getName());
	}
	
	@Override
	public boolean isToonShort() {
		return this.value instanceof Short;
	}
	
	@Override
	public short getAsShort() {
		if (this.isToonShort()) {
			return (short) this.value;
		} else if (this.isToonNumber()) {
			return this.getAsNumber().shortValue();
		}
		throw new ToonTypeException("Expected a TOON short, but found: " + this.getName());
	}
	
	@Override
	public boolean isToonInteger() {
		return this.value instanceof Integer;
	}
	
	@Override
	public int getAsInteger() {
		if (this.isToonInteger()) {
			return (int) this.value;
		} else if (this.isToonNumber()) {
			return this.getAsNumber().intValue();
		}
		throw new ToonTypeException("Expected a TOON integer, but found: " + this.getName());
	}
	
	@Override
	public boolean isToonLong() {
		return this.value instanceof Long;
	}
	
	@Override
	public long getAsLong() {
		if (this.isToonLong()) {
			return (long) this.value;
		} else if (this.isToonNumber()) {
			return this.getAsNumber().longValue();
		}
		throw new ToonTypeException("Expected a TOON long, but found: " + this.getName());
	}
	
	@Override
	public boolean isToonFloat() {
		return this.value instanceof Float;
	}
	
	@Override
	public float getAsFloat() {
		if (this.isToonFloat()) {
			return (float) this.value;
		} else if (this.isToonNumber()) {
			return this.getAsNumber().floatValue();
		}
		throw new ToonTypeException("Expected a TOON float, but found: " + this.getName());
	}
	
	@Override
	public boolean isToonDouble() {
		return this.value instanceof Double;
	}
	
	@Override
	public double getAsDouble() {
		if (this.isToonDouble()) {
			return (double) this.value;
		} else if (this.isToonNumber()) {
			return this.getAsNumber().doubleValue();
		}
		throw new ToonTypeException("Expected a TOON double, but found: " + this.getName());
	}
	
	@Override
	public boolean isToonString() {
		return this.value instanceof String;
	}
	
	@Override
	public @NonNull String getAsString() {
		if (this.isToonString()) {
			return (String) this.value;
		} else if (this.isToonNumber()) {
			return this.getAsNumber().toString();
		} else if (this.isToonBoolean()) {
			return String.valueOf(this.getAsBoolean());
		}
		throw new ToonTypeException("Expected a TOON string, but found: " + this.getName());
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ToonValue that)) return false;
		
		return this.value.equals(that.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.value);
	}
	
	@Override
	public String toString() {
		return this.toString(ToonConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull ToonConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		
		if (this.isToonBoolean()) {
			return this.getAsBoolean() ? "true" : "false";
		} else if (this.isToonNumber()) {
			return ToonHelper.formatNumber(this.getAsNumber());
		} else if (this.isToonString()) {
			String str = (String) this.value;
			if (ToonHelper.needsQuoting(str, config.delimiter().getChar())) {
				return "\"" + ToonHelper.escapeString(str) + "\"";
			}
			return str;
		}
		return String.valueOf(this.value);
	}
	//endregion
}
