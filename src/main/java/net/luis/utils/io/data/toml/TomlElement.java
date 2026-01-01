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

package net.luis.utils.io.data.toml;

import net.luis.utils.io.data.toml.exception.TomlTypeException;
import net.luis.utils.lang.StringUtils;
import org.jspecify.annotations.NonNull;

import java.time.*;

/**
 * A generic interface representing a TOML element.<br>
 * A TOML element can be a TOML value, a TOML array, a TOML table, or a TOML null.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface TomlElement {
	
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
	 * Checks if this TOML element is a TOML null.<br>
	 * @return True if this TOML element is a TOML null, false otherwise
	 */
	default boolean isTomlNull() {
		return this instanceof TomlNull;
	}
	
	/**
	 * Checks if this TOML element is a TOML value.<br>
	 * @return True if this TOML element is a TOML value, false otherwise
	 */
	default boolean isTomlValue() {
		return this instanceof TomlValue;
	}
	
	/**
	 * Checks if this TOML element is a TOML array.<br>
	 * @return True if this TOML element is a TOML array, false otherwise
	 */
	default boolean isTomlArray() {
		return this instanceof TomlArray;
	}
	
	/**
	 * Checks if this TOML element is a TOML table.<br>
	 * @return True if this TOML element is a TOML table, false otherwise
	 */
	default boolean isTomlTable() {
		return this instanceof TomlTable;
	}
	
	/**
	 * Checks if this TOML value is a boolean.<br>
	 * @return True if this TOML value is a boolean, false otherwise
	 */
	default boolean isBoolean() {
		return this.isTomlValue() && this.getAsTomlValue().isBoolean();
	}
	
	/**
	 * Checks if this TOML value is a number.<br>
	 * @return True if this TOML value is a number, false otherwise
	 */
	default boolean isNumber() {
		return this.isTomlValue() && this.getAsTomlValue().isNumber();
	}
	
	/**
	 * Checks if this TOML value is a byte.<br>
	 * @return True if this TOML value is a byte, false otherwise
	 */
	default boolean isByte() {
		return this.isTomlValue() && this.getAsTomlValue().isByte();
	}
	
	/**
	 * Checks if this TOML value is a short.<br>
	 * @return True if this TOML value is a short, false otherwise
	 */
	default boolean isShort() {
		return this.isTomlValue() && this.getAsTomlValue().isShort();
	}
	
	/**
	 * Checks if this TOML value is an integer.<br>
	 * @return True if this TOML value is an integer, false otherwise
	 */
	default boolean isInteger() {
		return this.isTomlValue() && this.getAsTomlValue().isInteger();
	}
	
	/**
	 * Checks if this TOML value is a long.<br>
	 * @return True if this TOML value is a long, false otherwise
	 */
	default boolean isLong() {
		return this.isTomlValue() && this.getAsTomlValue().isLong();
	}
	
	/**
	 * Checks if this TOML value is a float.<br>
	 * @return True if this TOML value is a float, false otherwise
	 */
	default boolean isFloat() {
		return this.isTomlValue() && this.getAsTomlValue().isFloat();
	}
	
	/**
	 * Checks if this TOML value is a double.<br>
	 * @return True if this TOML value is a double, false otherwise
	 */
	default boolean isDouble() {
		return this.isTomlValue() && this.getAsTomlValue().isDouble();
	}
	
	/**
	 * Checks if this TOML value is a string.<br>
	 * @return True if this TOML value is a string, false otherwise
	 */
	default boolean isString() {
		return this.isTomlValue() && this.getAsTomlValue().isString();
	}
	
	/**
	 * Checks if this TOML value is a local date.<br>
	 * @return True if this TOML value is a local date, false otherwise
	 */
	default boolean isLocalDate() {
		return this.isTomlValue() && this.getAsTomlValue().isLocalDate();
	}
	
	/**
	 * Checks if this TOML value is a local time.<br>
	 * @return True if this TOML value is a local time, false otherwise
	 */
	default boolean isLocalTime() {
		return this.isTomlValue() && this.getAsTomlValue().isLocalTime();
	}
	
	/**
	 * Checks if this TOML value is a local date-time.<br>
	 * @return True if this TOML value is a local date-time, false otherwise
	 */
	default boolean isLocalDateTime() {
		return this.isTomlValue() && this.getAsTomlValue().isLocalDateTime();
	}
	
	/**
	 * Checks if this TOML value is an offset date-time.<br>
	 * @return True if this TOML value is an offset date-time, false otherwise
	 */
	default boolean isOffsetDateTime() {
		return this.isTomlValue() && this.getAsTomlValue().isOffsetDateTime();
	}
	
	/**
	 * Checks if this TOML value is any date/time type.<br>
	 * @return True if this TOML value is a date/time type, false otherwise
	 */
	default boolean isDateTime() {
		return this.isTomlValue() && this.getAsTomlValue().isDateTime();
	}
	
	/**
	 * Converts this TOML element to a TOML value.<br>
	 *
	 * @return This TOML element as a TOML value
	 * @throws TomlTypeException If this TOML element is not a TOML value
	 */
	default @NonNull TomlValue getAsTomlValue() {
		if (this instanceof TomlValue value) {
			return value;
		}
		throw new TomlTypeException("Expected a TOML value, but found: " + this.getName());
	}
	
	/**
	 * Converts this TOML element to a TOML array.<br>
	 *
	 * @return This TOML element as a TOML array
	 * @throws TomlTypeException If this TOML element is not a TOML array
	 */
	default @NonNull TomlArray getAsTomlArray() {
		if (this instanceof TomlArray array) {
			return array;
		}
		throw new TomlTypeException("Expected a TOML array, but found: " + this.getName());
	}
	
	/**
	 * Converts this TOML element to a TOML table.<br>
	 *
	 * @return This TOML element as a TOML table
	 * @throws TomlTypeException If this TOML element is not a TOML table
	 */
	default @NonNull TomlTable getAsTomlTable() {
		if (this instanceof TomlTable table) {
			return table;
		}
		throw new TomlTypeException("Expected a TOML table, but found: " + this.getName());
	}
	
	/**
	 * Converts this TOML element to a boolean.<br>
	 *
	 * @return This TOML value as a boolean
	 * @throws TomlTypeException If this TOML element is not a TOML value or not a boolean
	 */
	default boolean getAsBoolean() {
		return this.getAsTomlValue().getAsBoolean();
	}
	
	/**
	 * Converts this TOML element to a number.<br>
	 *
	 * @return This TOML value as a number
	 * @throws TomlTypeException If this TOML element is not a TOML value or not a number
	 */
	default @NonNull Number getAsNumber() {
		return this.getAsTomlValue().getAsNumber();
	}
	
	/**
	 * Converts this TOML element to a byte.<br>
	 *
	 * @return This TOML value as a byte
	 * @throws TomlTypeException If this TOML element is not a TOML value or not a number
	 */
	default byte getAsByte() {
		return this.getAsTomlValue().getAsByte();
	}
	
	/**
	 * Converts this TOML element to a short.<br>
	 *
	 * @return This TOML value as a short
	 * @throws TomlTypeException If this TOML element is not a TOML value or not a number
	 */
	default short getAsShort() {
		return this.getAsTomlValue().getAsShort();
	}
	
	/**
	 * Converts this TOML element to an integer.<br>
	 *
	 * @return This TOML value as an integer
	 * @throws TomlTypeException If this TOML element is not a TOML value or not a number
	 */
	default int getAsInteger() {
		return this.getAsTomlValue().getAsInteger();
	}
	
	/**
	 * Converts this TOML element to a long.<br>
	 *
	 * @return This TOML value as a long
	 * @throws TomlTypeException If this TOML element is not a TOML value or not a number
	 */
	default long getAsLong() {
		return this.getAsTomlValue().getAsLong();
	}
	
	/**
	 * Converts this TOML element to a float.<br>
	 *
	 * @return This TOML value as a float
	 * @throws TomlTypeException If this TOML element is not a TOML value or not a number
	 */
	default float getAsFloat() {
		return this.getAsTomlValue().getAsFloat();
	}
	
	/**
	 * Converts this TOML element to a double.<br>
	 *
	 * @return This TOML value as a double
	 * @throws TomlTypeException If this TOML element is not a TOML value or not a number
	 */
	default double getAsDouble() {
		return this.getAsTomlValue().getAsDouble();
	}
	
	/**
	 * Converts this TOML element to a string.<br>
	 *
	 * @return This TOML value as a string
	 * @throws TomlTypeException If this TOML element is not a TOML value
	 */
	default @NonNull String getAsString() {
		return this.getAsTomlValue().getAsString();
	}
	
	/**
	 * Converts this TOML element to a local date.<br>
	 *
	 * @return This TOML value as a local date
	 * @throws TomlTypeException If this TOML element is not a TOML value or not a local date
	 */
	default @NonNull LocalDate getAsLocalDate() {
		return this.getAsTomlValue().getAsLocalDate();
	}
	
	/**
	 * Converts this TOML element to a local time.<br>
	 *
	 * @return This TOML value as a local time
	 * @throws TomlTypeException If this TOML element is not a TOML value or not a local time
	 */
	default @NonNull LocalTime getAsLocalTime() {
		return this.getAsTomlValue().getAsLocalTime();
	}
	
	/**
	 * Converts this TOML element to a local date-time.<br>
	 *
	 * @return This TOML value as a local date-time
	 * @throws TomlTypeException If this TOML element is not a TOML value or not a local date-time
	 */
	default @NonNull LocalDateTime getAsLocalDateTime() {
		return this.getAsTomlValue().getAsLocalDateTime();
	}
	
	/**
	 * Converts this TOML element to an offset date-time.<br>
	 *
	 * @return This TOML value as an offset date-time
	 * @throws TomlTypeException If this TOML element is not a TOML value or not an offset date-time
	 */
	default @NonNull OffsetDateTime getAsOffsetDateTime() {
		return this.getAsTomlValue().getAsOffsetDateTime();
	}
	
	/**
	 * Returns a string representation of this TOML element based on the given TOML config.<br>
	 * The TOML config specifies how the TOML element should be formatted.<br>
	 *
	 * @param config The TOML config to use
	 * @return The string representation of this TOML element
	 */
	@NonNull String toString(@NonNull TomlConfig config);
}
