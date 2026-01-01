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
import net.luis.utils.lang.StringUtils;
import org.jspecify.annotations.NonNull;

/**
 * A generic interface representing an ini element.<br>
 * An ini element can be an ini value, an ini section, an ini document, or an ini null.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface IniElement {

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
	 * Checks if this ini element is an ini null.<br>
	 * @return True if this ini element is an ini null, false otherwise
	 */
	default boolean isIniNull() {
		return this instanceof IniNull;
	}

	/**
	 * Checks if this ini element is an ini value.<br>
	 * @return True if this ini element is an ini value, false otherwise
	 */
	default boolean isIniValue() {
		return this instanceof IniValue;
	}

	/**
	 * Checks if this ini element is an ini section.<br>
	 * @return True if this ini element is an ini section, false otherwise
	 */
	default boolean isIniSection() {
		return this instanceof IniSection;
	}

	/**
	 * Checks if this ini element is an ini document.<br>
	 * @return True if this ini element is an ini document, false otherwise
	 */
	default boolean isIniDocument() {
		return this instanceof IniDocument;
	}

	/**
	 * Checks if this ini value is a boolean.<br>
	 * @return True if this ini value is a boolean, false otherwise
	 */
	default boolean isBoolean() {
		return this.isIniValue() && this.getAsIniValue().isBoolean();
	}

	/**
	 * Checks if this ini value is a number.<br>
	 * @return True if this ini value is a number, false otherwise
	 */
	default boolean isNumber() {
		return this.isIniValue() && this.getAsIniValue().isNumber();
	}

	/**
	 * Checks if this ini value is a byte.<br>
	 * @return True if this ini value is a byte, false otherwise
	 */
	default boolean isByte() {
		return this.isIniValue() && this.getAsIniValue().isByte();
	}

	/**
	 * Checks if this ini value is a short.<br>
	 * @return True if this ini value is a short, false otherwise
	 */
	default boolean isShort() {
		return this.isIniValue() && this.getAsIniValue().isShort();
	}

	/**
	 * Checks if this ini value is an integer.<br>
	 * @return True if this ini value is an integer, false otherwise
	 */
	default boolean isInteger() {
		return this.isIniValue() && this.getAsIniValue().isInteger();
	}

	/**
	 * Checks if this ini value is a long.<br>
	 * @return True if this ini value is a long, false otherwise
	 */
	default boolean isLong() {
		return this.isIniValue() && this.getAsIniValue().isLong();
	}

	/**
	 * Checks if this ini value is a float.<br>
	 * @return True if this ini value is a float, false otherwise
	 */
	default boolean isFloat() {
		return this.isIniValue() && this.getAsIniValue().isFloat();
	}

	/**
	 * Checks if this ini value is a double.<br>
	 * @return True if this ini value is a double, false otherwise
	 */
	default boolean isDouble() {
		return this.isIniValue() && this.getAsIniValue().isDouble();
	}

	/**
	 * Checks if this ini value is a string.<br>
	 * @return True if this ini value is a string, false otherwise
	 */
	default boolean isString() {
		return this.isIniValue() && this.getAsIniValue().isString();
	}

	/**
	 * Converts this ini element to an ini value.<br>
	 *
	 * @return This ini element as an ini value
	 * @throws IniTypeException If this ini element is not an ini value
	 */
	default @NonNull IniValue getAsIniValue() {
		if (this instanceof IniValue value) {
			return value;
		}
		throw new IniTypeException("Expected an ini value, but found: " + this.getName());
	}

	/**
	 * Converts this ini element to an ini section.<br>
	 *
	 * @return This ini element as an ini section
	 * @throws IniTypeException If this ini element is not an ini section
	 */
	default @NonNull IniSection getAsIniSection() {
		if (this instanceof IniSection section) {
			return section;
		}
		throw new IniTypeException("Expected an ini section, but found: " + this.getName());
	}

	/**
	 * Converts this ini element to an ini document.<br>
	 *
	 * @return This ini element as an ini document
	 * @throws IniTypeException If this ini element is not an ini document
	 */
	default @NonNull IniDocument getAsIniDocument() {
		if (this instanceof IniDocument document) {
			return document;
		}
		throw new IniTypeException("Expected an ini document, but found: " + this.getName());
	}

	/**
	 * Converts this ini element to a boolean.<br>
	 *
	 * @return This ini value as a boolean
	 * @throws IniTypeException If this ini element is not an ini value or not a boolean
	 */
	default boolean getAsBoolean() {
		return this.getAsIniValue().getAsBoolean();
	}

	/**
	 * Converts this ini element to a number.<br>
	 *
	 * @return This ini value as a number
	 * @throws IniTypeException If this ini element is not an ini value or not a number
	 */
	default @NonNull Number getAsNumber() {
		return this.getAsIniValue().getAsNumber();
	}

	/**
	 * Converts this ini element to a byte.<br>
	 *
	 * @return This ini value as a byte
	 * @throws IniTypeException If this ini element is not an ini value or not a number
	 */
	default byte getAsByte() {
		return this.getAsIniValue().getAsByte();
	}

	/**
	 * Converts this ini element to a short.<br>
	 *
	 * @return This ini value as a short
	 * @throws IniTypeException If this ini element is not an ini value or not a number
	 */
	default short getAsShort() {
		return this.getAsIniValue().getAsShort();
	}

	/**
	 * Converts this ini element to an integer.<br>
	 *
	 * @return This ini value as an integer
	 * @throws IniTypeException If this ini element is not an ini value or not a number
	 */
	default int getAsInteger() {
		return this.getAsIniValue().getAsInteger();
	}

	/**
	 * Converts this ini element to a long.<br>
	 *
	 * @return This ini value as a long
	 * @throws IniTypeException If this ini element is not an ini value or not a number
	 */
	default long getAsLong() {
		return this.getAsIniValue().getAsLong();
	}

	/**
	 * Converts this ini element to a float.<br>
	 *
	 * @return This ini value as a float
	 * @throws IniTypeException If this ini element is not an ini value or not a number
	 */
	default float getAsFloat() {
		return this.getAsIniValue().getAsFloat();
	}

	/**
	 * Converts this ini element to a double.<br>
	 *
	 * @return This ini value as a double
	 * @throws IniTypeException If this ini element is not an ini value or not a number
	 */
	default double getAsDouble() {
		return this.getAsIniValue().getAsDouble();
	}

	/**
	 * Converts this ini element to a string.<br>
	 *
	 * @return This ini value as a string
	 * @throws IniTypeException If this ini element is not an ini value
	 */
	default @NonNull String getAsString() {
		return this.getAsIniValue().getAsString();
	}

	/**
	 * Returns a string representation of this ini element based on the given ini config.<br>
	 * The ini config specifies how the ini element should be formatted.<br>
	 *
	 * @param config The ini config to use
	 * @return The string representation of this ini element
	 */
	@NonNull String toString(@NonNull IniConfig config);
}
