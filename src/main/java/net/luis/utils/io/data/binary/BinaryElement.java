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

/**
 * A generic interface representing a binary element.<br>
 * A binary element is the in-memory representation of a value of the binary format.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface BinaryElement {
	
	/**
	 * Returns the type of this binary element.<br>
	 * @return The type of this element
	 */
	@NonNull BinaryType getType();
	
	/**
	 * Checks if this binary element is a binary null value.<br>
	 * @return True if this element is a binary null value, false otherwise
	 */
	default boolean isBinaryNull() {
		return this instanceof BinaryNull;
	}
	
	/**
	 * Checks if this binary element is an absent value.<br>
	 * @return True if this element is an absent value, false otherwise
	 */
	default boolean isBinaryAbsent() {
		return this instanceof BinaryAbsent;
	}
	
	/**
	 * Checks if this binary element is a binary array.<br>
	 * @return True if this element is a binary array, false otherwise
	 */
	default boolean isBinaryArray() {
		return this instanceof BinaryArray;
	}
	
	/**
	 * Checks if this binary element is a binary struct.<br>
	 * @return True if this element is a binary struct, false otherwise
	 */
	default boolean isBinaryStruct() {
		return this instanceof BinaryStruct;
	}
	
	/**
	 * Checks if this binary element is a binary map.<br>
	 * @return True if this element is a binary map, false otherwise
	 */
	default boolean isBinaryMap() {
		return this instanceof BinaryMap;
	}
	
	/**
	 * Checks if this binary element is a binary primitive value.<br>
	 * @return True if this element is a binary primitive value, false otherwise
	 */
	default boolean isBinaryPrimitive() {
		return this instanceof BinaryPrimitive;
	}
	
	/**
	 * Checks if this binary element is a binary boolean value.<br>
	 * @return True if this element is a binary boolean value, false otherwise
	 */
	default boolean isBinaryBoolean() {
		return this.isBinaryPrimitive() && this.getAsBinaryPrimitive().isBinaryBoolean();
	}
	
	/**
	 * Checks if this binary element is a binary number value.<br>
	 * @return True if this element is a binary number value, false otherwise
	 */
	default boolean isBinaryNumber() {
		return this.isBinaryPrimitive() && this.getAsBinaryPrimitive().isBinaryNumber();
	}
	
	/**
	 * Checks if this binary element is a binary byte value.<br>
	 * @return True if this element is a binary byte value, false otherwise
	 */
	default boolean isBinaryByte() {
		return this.isBinaryPrimitive() && this.getAsBinaryPrimitive().isBinaryByte();
	}
	
	/**
	 * Checks if this binary element is a binary short value.<br>
	 * @return True if this element is a binary short value, false otherwise
	 */
	default boolean isBinaryShort() {
		return this.isBinaryPrimitive() && this.getAsBinaryPrimitive().isBinaryShort();
	}
	
	/**
	 * Checks if this binary element is a binary integer value.<br>
	 * @return True if this element is a binary integer value, false otherwise
	 */
	default boolean isBinaryInteger() {
		return this.isBinaryPrimitive() && this.getAsBinaryPrimitive().isBinaryInteger();
	}
	
	/**
	 * Checks if this binary element is a binary long value.<br>
	 * @return True if this element is a binary long value, false otherwise
	 */
	default boolean isBinaryLong() {
		return this.isBinaryPrimitive() && this.getAsBinaryPrimitive().isBinaryLong();
	}
	
	/**
	 * Checks if this binary element is a binary float value.<br>
	 * @return True if this element is a binary float value, false otherwise
	 */
	default boolean isBinaryFloat() {
		return this.isBinaryPrimitive() && this.getAsBinaryPrimitive().isBinaryFloat();
	}
	
	/**
	 * Checks if this binary element is a binary double value.<br>
	 * @return True if this element is a binary double value, false otherwise
	 */
	default boolean isBinaryDouble() {
		return this.isBinaryPrimitive() && this.getAsBinaryPrimitive().isBinaryDouble();
	}
	
	/**
	 * Checks if this binary element is a binary string value.<br>
	 * @return True if this element is a binary string value, false otherwise
	 */
	default boolean isBinaryString() {
		return this.isBinaryPrimitive() && this.getAsBinaryPrimitive().isBinaryString();
	}
	
	/**
	 * Converts this binary element to a binary primitive.<br>
	 *
	 * @return This element as a binary primitive
	 * @throws BinaryTypeException If this element is not a binary primitive
	 */
	default @NonNull BinaryPrimitive getAsBinaryPrimitive() {
		if (this instanceof BinaryPrimitive primitive) {
			return primitive;
		}
		throw new BinaryTypeException("Expected a binary primitive, but found: " + this.getType());
	}
	
	/**
	 * Converts this binary element to a binary struct.<br>
	 *
	 * @return This element as a binary struct
	 * @throws BinaryTypeException If this element is not a binary struct
	 */
	default @NonNull BinaryStruct getAsBinaryStruct() {
		if (this instanceof BinaryStruct struct) {
			return struct;
		}
		throw new BinaryTypeException("Expected a binary struct, but found: " + this.getType());
	}
	
	/**
	 * Converts this binary element to a binary map.<br>
	 *
	 * @return This element as a binary map
	 * @throws BinaryTypeException If this element is not a binary map
	 */
	default @NonNull BinaryMap getAsBinaryMap() {
		if (this instanceof BinaryMap map) {
			return map;
		}
		throw new BinaryTypeException("Expected a binary map, but found: " + this.getType());
	}
	
	/**
	 * Converts this binary element to a binary array.<br>
	 *
	 * @return This element as a binary array
	 * @throws BinaryTypeException If this element is not a binary array
	 */
	default @NonNull BinaryArray getAsBinaryArray() {
		if (this instanceof BinaryArray array) {
			return array;
		}
		throw new BinaryTypeException("Expected a binary array, but found: " + this.getType());
	}
	
	/**
	 * Converts this binary element to a boolean value.<br>
	 *
	 * @return This element as a boolean value
	 * @throws BinaryTypeException If this element is not a binary boolean
	 */
	default boolean getAsBoolean() {
		throw new BinaryTypeException("Expected a binary boolean, but found: " + this.getType());
	}
	
	/**
	 * Converts this binary element to a number value.<br>
	 *
	 * @return This element as a number value
	 * @throws BinaryTypeException If this element is not a binary number
	 */
	default @NonNull Number getAsNumber() {
		throw new BinaryTypeException("Expected a binary number, but found: " + this.getType());
	}
	
	/**
	 * Converts this binary element to a byte value.<br>
	 *
	 * @return This element as a byte value
	 * @throws BinaryTypeException If this element is not a binary number
	 */
	default byte getAsByte() {
		throw new BinaryTypeException("Expected a binary byte, but found: " + this.getType());
	}
	
	/**
	 * Converts this binary element to a short value.<br>
	 *
	 * @return This element as a short value
	 * @throws BinaryTypeException If this element is not a binary number
	 */
	default short getAsShort() {
		throw new BinaryTypeException("Expected a binary short, but found: " + this.getType());
	}
	
	/**
	 * Converts this binary element to an integer value.<br>
	 *
	 * @return This element as an integer value
	 * @throws BinaryTypeException If this element is not a binary number
	 */
	default int getAsInteger() {
		throw new BinaryTypeException("Expected a binary integer, but found: " + this.getType());
	}
	
	/**
	 * Converts this binary element to a long value.<br>
	 *
	 * @return This element as a long value
	 * @throws BinaryTypeException If this element is not a binary number
	 */
	default long getAsLong() {
		throw new BinaryTypeException("Expected a binary long, but found: " + this.getType());
	}
	
	/**
	 * Converts this binary element to a float value.<br>
	 *
	 * @return This element as a float value
	 * @throws BinaryTypeException If this element is not a binary number
	 */
	default float getAsFloat() {
		throw new BinaryTypeException("Expected a binary float, but found: " + this.getType());
	}
	
	/**
	 * Converts this binary element to a double value.<br>
	 *
	 * @return This element as a double value
	 * @throws BinaryTypeException If this element is not a binary number
	 */
	default double getAsDouble() {
		throw new BinaryTypeException("Expected a binary double, but found: " + this.getType());
	}
	
	/**
	 * Converts this binary element to a string value.<br>
	 *
	 * @return This element as a string value
	 * @throws BinaryTypeException If this element is not a binary string
	 */
	default @NonNull String getAsString() {
		throw new BinaryTypeException("Expected a binary string, but found: " + this.getType());
	}
}
