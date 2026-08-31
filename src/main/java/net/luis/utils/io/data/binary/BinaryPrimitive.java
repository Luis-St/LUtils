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

package net.luis.utils.io.data.binary;

import net.luis.utils.io.data.binary.exception.BinaryTypeException;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a binary primitive value.<br>
 * A primitive value can be a boolean, number or string.<br>
 * <p>
 *     The type of a primitive is determined by the value it holds.<br>
 *     The type is written to the output in front of the value and is used to read the value back.
 * </p>
 *
 * @author Luis-St
 */
public class BinaryPrimitive implements BinaryElement {
	
	/**
	 * The value of this binary primitive.<br>
	 */
	private final Object value;
	/**
	 * The type of this binary primitive.<br>
	 */
	private final BinaryType type;
	
	/**
	 * Constructs a new binary primitive with the given boolean value.<br>
	 * @param value The boolean value
	 */
	public BinaryPrimitive(boolean value) {
		this.value = value;
		this.type = BinaryType.BOOLEAN;
	}
	
	/**
	 * Constructs a new binary primitive with the given byte value.<br>
	 * @param value The byte value
	 */
	public BinaryPrimitive(byte value) {
		this.value = value;
		this.type = BinaryType.BYTE;
	}
	
	/**
	 * Constructs a new binary primitive with the given short value.<br>
	 * @param value The short value
	 */
	public BinaryPrimitive(short value) {
		this.value = value;
		this.type = BinaryType.SHORT;
	}
	
	/**
	 * Constructs a new binary primitive with the given integer value.<br>
	 * @param value The integer value
	 */
	public BinaryPrimitive(int value) {
		this.value = value;
		this.type = BinaryType.INTEGER;
	}
	
	/**
	 * Constructs a new binary primitive with the given long value.<br>
	 * @param value The long value
	 */
	public BinaryPrimitive(long value) {
		this.value = value;
		this.type = BinaryType.LONG;
	}
	
	/**
	 * Constructs a new binary primitive with the given float value.<br>
	 * @param value The float value
	 */
	public BinaryPrimitive(float value) {
		this.value = value;
		this.type = BinaryType.FLOAT;
	}
	
	/**
	 * Constructs a new binary primitive with the given double value.<br>
	 * @param value The double value
	 */
	public BinaryPrimitive(double value) {
		this.value = value;
		this.type = BinaryType.DOUBLE;
	}
	
	/**
	 * Constructs a new binary primitive with the given character value.<br>
	 * The character is stored as a string.<br>
	 *
	 * @param value The character value
	 */
	public BinaryPrimitive(char value) {
		this.value = String.valueOf(value);
		this.type = BinaryType.STRING;
	}
	
	/**
	 * Constructs a new binary primitive with the given string value.<br>
	 *
	 * @param value The string value
	 * @throws NullPointerException If the value is null
	 */
	public BinaryPrimitive(@NonNull String value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
		this.type = BinaryType.STRING;
	}
	
	/**
	 * Constructs a new binary primitive with the given number value.<br>
	 * The type of the primitive is determined by the type of the given number.<br>
	 *
	 * @param value The number value
	 * @throws NullPointerException If the value is null
	 * @throws BinaryTypeException If the type of the number is not supported
	 */
	public BinaryPrimitive(@NonNull Number value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
		this.type = switch (value) {
			case Byte _ -> BinaryType.BYTE;
			case Short _ -> BinaryType.SHORT;
			case Integer _ -> BinaryType.INTEGER;
			case Long _ -> BinaryType.LONG;
			case Float _ -> BinaryType.FLOAT;
			case Double _ -> BinaryType.DOUBLE;
			default -> throw new BinaryTypeException("Unsupported number type: " + value.getClass().getSimpleName());
		};
	}
	
	@Override
	public @NonNull BinaryType getType() {
		return this.type;
	}
	
	@Override
	public boolean isBinaryBoolean() {
		return this.type == BinaryType.BOOLEAN;
	}
	
	@Override
	public boolean getAsBoolean() {
		if (this.value instanceof Boolean bool) {
			return bool;
		}
		throw new BinaryTypeException("Expected a binary boolean, but found: " + this.type);
	}
	
	@Override
	public boolean isBinaryNumber() {
		return this.value instanceof Number;
	}
	
	@Override
	public @NonNull Number getAsNumber() {
		if (this.value instanceof Number number) {
			return number;
		}
		throw new BinaryTypeException("Expected a binary number, but found: " + this.type);
	}
	
	@Override
	public boolean isBinaryByte() {
		return this.type == BinaryType.BYTE;
	}
	
	@Override
	public byte getAsByte() {
		return this.getAsNumber().byteValue();
	}
	
	@Override
	public boolean isBinaryShort() {
		return this.type == BinaryType.SHORT;
	}
	
	@Override
	public short getAsShort() {
		return this.getAsNumber().shortValue();
	}
	
	@Override
	public boolean isBinaryInteger() {
		return this.type == BinaryType.INTEGER;
	}
	
	@Override
	public int getAsInteger() {
		return this.getAsNumber().intValue();
	}
	
	@Override
	public boolean isBinaryLong() {
		return this.type == BinaryType.LONG;
	}
	
	@Override
	public long getAsLong() {
		return this.getAsNumber().longValue();
	}
	
	@Override
	public boolean isBinaryFloat() {
		return this.type == BinaryType.FLOAT;
	}
	
	@Override
	public float getAsFloat() {
		return this.getAsNumber().floatValue();
	}
	
	@Override
	public boolean isBinaryDouble() {
		return this.type == BinaryType.DOUBLE;
	}
	
	@Override
	public double getAsDouble() {
		return this.getAsNumber().doubleValue();
	}
	
	@Override
	public boolean isBinaryString() {
		return this.type == BinaryType.STRING;
	}
	
	@Override
	public @NonNull String getAsString() {
		if (this.value instanceof String string) {
			return string;
		}
		throw new BinaryTypeException("Expected a binary string, but found: " + this.type);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BinaryPrimitive that)) return false;
		
		if (this.type != that.type) return false;
		return this.value.equals(that.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.value, this.type);
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.value);
	}
	//endregion
}
