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
import net.luis.utils.lang.StringUtils;
import org.jspecify.annotations.NonNull;

/**
 * A generic interface representing a toon element.<br>
 * A toon element can be a toon value, a toon array, a toon object, or a toon null.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface ToonElement {
	
	/**
	 * Returns the name of the class in a human-readable format.<br>
	 * The name is the class name with spaces between the words and all letters in lower-case.<br>
	 * Used for debugging and error messages.<br>
	 *
	 * @return The name of the class in a human-readable format
	 */
	private @NonNull String getName() {
		return StringUtils.getReadableString(this.getClass().getSimpleName(), Character::isUpperCase).toLowerCase();
	}
	
	/**
	 * Checks if this toon element is a toon null.<br>
	 * @return True if this toon element is a toon null, false otherwise
	 */
	default boolean isToonNull() {
		return this instanceof ToonNull;
	}
	
	/**
	 * Checks if this toon element is a toon value.<br>
	 * @return True if this toon element is a toon value, false otherwise
	 */
	default boolean isToonValue() {
		return this instanceof ToonValue;
	}
	
	/**
	 * Checks if this toon element is a toon array.<br>
	 * @return True if this toon element is a toon array, false otherwise
	 */
	default boolean isToonArray() {
		return this instanceof ToonArray;
	}
	
	/**
	 * Checks if this toon element is a toon object.<br>
	 * @return True if this toon element is a toon object, false otherwise
	 */
	default boolean isToonObject() {
		return this instanceof ToonObject;
	}
	
	/**
	 * Checks if this toon value is a boolean.<br>
	 * @return True if this toon value is a boolean, false otherwise
	 */
	default boolean isToonBoolean() {
		return this.isToonValue() && this.getAsToonValue().isToonBoolean();
	}
	
	/**
	 * Checks if this toon value is a number.<br>
	 * @return True if this toon value is a number, false otherwise
	 */
	default boolean isToonNumber() {
		return this.isToonValue() && this.getAsToonValue().isToonNumber();
	}
	
	/**
	 * Checks if this toon value is a byte.<br>
	 * @return True if this toon value is a byte, false otherwise
	 */
	default boolean isToonByte() {
		return this.isToonValue() && this.getAsToonValue().isToonByte();
	}
	
	/**
	 * Checks if this toon value is a short.<br>
	 * @return True if this toon value is a short, false otherwise
	 */
	default boolean isToonShort() {
		return this.isToonValue() && this.getAsToonValue().isToonShort();
	}
	
	/**
	 * Checks if this toon value is an integer.<br>
	 * @return True if this toon value is an integer, false otherwise
	 */
	default boolean isToonInteger() {
		return this.isToonValue() && this.getAsToonValue().isToonInteger();
	}
	
	/**
	 * Checks if this toon value is a long.<br>
	 * @return True if this toon value is a long, false otherwise
	 */
	default boolean isToonLong() {
		return this.isToonValue() && this.getAsToonValue().isToonLong();
	}
	
	/**
	 * Checks if this toon value is a float.<br>
	 * @return True if this toon value is a float, false otherwise
	 */
	default boolean isToonFloat() {
		return this.isToonValue() && this.getAsToonValue().isToonFloat();
	}
	
	/**
	 * Checks if this toon value is a double.<br>
	 * @return True if this toon value is a double, false otherwise
	 */
	default boolean isToonDouble() {
		return this.isToonValue() && this.getAsToonValue().isToonDouble();
	}
	
	/**
	 * Checks if this toon value is a string.<br>
	 * @return True if this toon value is a string, false otherwise
	 */
	default boolean isToonString() {
		return this.isToonValue() && this.getAsToonValue().isToonString();
	}
	
	/**
	 * Converts this toon element to a toon value.<br>
	 *
	 * @return This toon element as a toon value
	 * @throws ToonTypeException If this toon element is not a toon value
	 */
	default @NonNull ToonValue getAsToonValue() {
		if (this instanceof ToonValue value) {
			return value;
		}
		throw new ToonTypeException("Expected a toon value, but found: " + this.getName());
	}
	
	/**
	 * Converts this toon element to a toon array.<br>
	 *
	 * @return This toon element as a toon array
	 * @throws ToonTypeException If this toon element is not a toon array
	 */
	default @NonNull ToonArray getAsToonArray() {
		if (this instanceof ToonArray array) {
			return array;
		}
		throw new ToonTypeException("Expected a toon array, but found: " + this.getName());
	}
	
	/**
	 * Converts this toon element to a toon object.<br>
	 *
	 * @return This toon element as a toon object
	 * @throws ToonTypeException If this toon element is not a toon object
	 */
	default @NonNull ToonObject getAsToonObject() {
		if (this instanceof ToonObject object) {
			return object;
		}
		throw new ToonTypeException("Expected a toon object, but found: " + this.getName());
	}
	
	/**
	 * Converts this toon element to a boolean.<br>
	 *
	 * @return This toon value as a boolean
	 * @throws ToonTypeException If this toon element is not a toon value or not a boolean
	 */
	default boolean getAsBoolean() {
		return this.getAsToonValue().getAsBoolean();
	}
	
	/**
	 * Converts this toon element to a number.<br>
	 *
	 * @return This toon value as a number
	 * @throws ToonTypeException If this toon element is not a toon value or not a number
	 */
	default @NonNull Number getAsNumber() {
		return this.getAsToonValue().getAsNumber();
	}
	
	/**
	 * Converts this toon element to a byte.<br>
	 *
	 * @return This toon value as a byte
	 * @throws ToonTypeException If this toon element is not a toon value or not a number
	 */
	default byte getAsByte() {
		return this.getAsToonValue().getAsByte();
	}
	
	/**
	 * Converts this toon element to a short.<br>
	 *
	 * @return This toon value as a short
	 * @throws ToonTypeException If this toon element is not a toon value or not a number
	 */
	default short getAsShort() {
		return this.getAsToonValue().getAsShort();
	}
	
	/**
	 * Converts this toon element to an integer.<br>
	 *
	 * @return This toon value as an integer
	 * @throws ToonTypeException If this toon element is not a toon value or not a number
	 */
	default int getAsInteger() {
		return this.getAsToonValue().getAsInteger();
	}
	
	/**
	 * Converts this toon element to a long.<br>
	 *
	 * @return This toon value as a long
	 * @throws ToonTypeException If this toon element is not a toon value or not a number
	 */
	default long getAsLong() {
		return this.getAsToonValue().getAsLong();
	}
	
	/**
	 * Converts this toon element to a float.<br>
	 *
	 * @return This toon value as a float
	 * @throws ToonTypeException If this toon element is not a toon value or not a number
	 */
	default float getAsFloat() {
		return this.getAsToonValue().getAsFloat();
	}
	
	/**
	 * Converts this toon element to a double.<br>
	 *
	 * @return This toon value as a double
	 * @throws ToonTypeException If this toon element is not a toon value or not a number
	 */
	default double getAsDouble() {
		return this.getAsToonValue().getAsDouble();
	}
	
	/**
	 * Converts this toon element to a string.<br>
	 *
	 * @return This toon value as a string
	 * @throws ToonTypeException If this toon element is not a toon value
	 */
	default @NonNull String getAsString() {
		return this.getAsToonValue().getAsString();
	}
	
	/**
	 * Returns a string representation of this toon element based on the given toon config.<br>
	 * The toon config specifies how the toon element should be formatted.<br>
	 *
	 * @param config The toon config to use
	 * @return The string representation of this toon element
	 */
	@NonNull String toString(@NonNull ToonConfig config);
}
