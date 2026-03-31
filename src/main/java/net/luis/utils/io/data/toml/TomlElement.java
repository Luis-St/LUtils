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
 * A generic interface representing a toml element.<br>
 * A toml element can be a toml value, a toml array, a toml table, or a toml null.<br>
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
	 * Checks if this toml element is a toml null.<br>
	 * @return True if this toml element is a toml null, false otherwise
	 */
	default boolean isTomlNull() {
		return this instanceof TomlNull;
	}
	
	/**
	 * Checks if this toml element is a toml value.<br>
	 * @return True if this toml element is a toml value, false otherwise
	 */
	default boolean isTomlValue() {
		return this instanceof TomlValue;
	}
	
	/**
	 * Checks if this toml element is a toml array.<br>
	 * @return True if this toml element is a toml array, false otherwise
	 */
	default boolean isTomlArray() {
		return this instanceof TomlArray;
	}
	
	/**
	 * Checks if this toml element is a toml table.<br>
	 * @return True if this toml element is a toml table, false otherwise
	 */
	default boolean isTomlTable() {
		return this instanceof TomlTable;
	}
	
	/**
	 * Checks if this toml value is a boolean.<br>
	 * @return True if this toml value is a boolean, false otherwise
	 */
	default boolean isTomlBoolean() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlBoolean();
	}
	
	/**
	 * Checks if this toml value is a number.<br>
	 * @return True if this toml value is a number, false otherwise
	 */
	default boolean isTomlNumber() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlNumber();
	}
	
	/**
	 * Checks if this toml value is a byte.<br>
	 * @return True if this toml value is a byte, false otherwise
	 */
	default boolean isTomlByte() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlByte();
	}
	
	/**
	 * Checks if this toml value is a short.<br>
	 * @return True if this toml value is a short, false otherwise
	 */
	default boolean isTomlShort() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlShort();
	}
	
	/**
	 * Checks if this toml value is an integer.<br>
	 * @return True if this toml value is an integer, false otherwise
	 */
	default boolean isTomlInteger() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlInteger();
	}
	
	/**
	 * Checks if this toml value is a long.<br>
	 * @return True if this toml value is a long, false otherwise
	 */
	default boolean isTomlLong() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlLong();
	}
	
	/**
	 * Checks if this toml value is a float.<br>
	 * @return True if this toml value is a float, false otherwise
	 */
	default boolean isTomlFloat() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlFloat();
	}
	
	/**
	 * Checks if this toml value is a double.<br>
	 * @return True if this toml value is a double, false otherwise
	 */
	default boolean isTomlDouble() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlDouble();
	}
	
	/**
	 * Checks if this toml value is a string.<br>
	 * @return True if this toml value is a string, false otherwise
	 */
	default boolean isTomlString() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlString();
	}
	
	/**
	 * Checks if this toml value is a local date.<br>
	 * @return True if this toml value is a local date, false otherwise
	 */
	default boolean isTomlLocalDate() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlLocalDate();
	}
	
	/**
	 * Checks if this toml value is a local time.<br>
	 * @return True if this toml value is a local time, false otherwise
	 */
	default boolean isTomlLocalTime() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlLocalTime();
	}
	
	/**
	 * Checks if this toml value is a local date-time.<br>
	 * @return True if this toml value is a local date-time, false otherwise
	 */
	default boolean isTomlLocalDateTime() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlLocalDateTime();
	}
	
	/**
	 * Checks if this toml value is an offset date-time.<br>
	 * @return True if this toml value is an offset date-time, false otherwise
	 */
	default boolean isTomlOffsetDateTime() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlOffsetDateTime();
	}
	
	/**
	 * Checks if this toml value is any date/time type.<br>
	 * @return True if this toml value is a date/time type, false otherwise
	 */
	default boolean isTomlDateTime() {
		return this.isTomlValue() && this.getAsTomlValue().isTomlDateTime();
	}
	
	/**
	 * Converts this toml element to a toml value.<br>
	 *
	 * @return This toml element as a toml value
	 * @throws TomlTypeException If this toml element is not a toml value
	 */
	default @NonNull TomlValue getAsTomlValue() {
		if (this instanceof TomlValue value) {
			return value;
		}
		throw new TomlTypeException("Expected a toml value, but found: " + this.getName());
	}
	
	/**
	 * Converts this toml element to a toml array.<br>
	 *
	 * @return This toml element as a toml array
	 * @throws TomlTypeException If this toml element is not a toml array
	 */
	default @NonNull TomlArray getAsTomlArray() {
		if (this instanceof TomlArray array) {
			return array;
		}
		throw new TomlTypeException("Expected a toml array, but found: " + this.getName());
	}
	
	/**
	 * Converts this toml element to a toml table.<br>
	 *
	 * @return This toml element as a toml table
	 * @throws TomlTypeException If this toml element is not a toml table
	 */
	default @NonNull TomlTable getAsTomlTable() {
		if (this instanceof TomlTable table) {
			return table;
		}
		throw new TomlTypeException("Expected a toml table, but found: " + this.getName());
	}
	
	/**
	 * Converts this toml element to a boolean.<br>
	 *
	 * @return This toml value as a boolean
	 * @throws TomlTypeException If this toml element is not a toml value or not a boolean
	 */
	default boolean getAsBoolean() {
		return this.getAsTomlValue().getAsBoolean();
	}
	
	/**
	 * Converts this toml element to a number.<br>
	 *
	 * @return This toml value as a number
	 * @throws TomlTypeException If this toml element is not a toml value or not a number
	 */
	default @NonNull Number getAsNumber() {
		return this.getAsTomlValue().getAsNumber();
	}
	
	/**
	 * Converts this toml element to a byte.<br>
	 *
	 * @return This toml value as a byte
	 * @throws TomlTypeException If this toml element is not a toml value or not a number
	 */
	default byte getAsByte() {
		return this.getAsTomlValue().getAsByte();
	}
	
	/**
	 * Converts this toml element to a short.<br>
	 *
	 * @return This toml value as a short
	 * @throws TomlTypeException If this toml element is not a toml value or not a number
	 */
	default short getAsShort() {
		return this.getAsTomlValue().getAsShort();
	}
	
	/**
	 * Converts this toml element to an integer.<br>
	 *
	 * @return This toml value as an integer
	 * @throws TomlTypeException If this toml element is not a toml value or not a number
	 */
	default int getAsInteger() {
		return this.getAsTomlValue().getAsInteger();
	}
	
	/**
	 * Converts this toml element to a long.<br>
	 *
	 * @return This toml value as a long
	 * @throws TomlTypeException If this toml element is not a toml value or not a number
	 */
	default long getAsLong() {
		return this.getAsTomlValue().getAsLong();
	}
	
	/**
	 * Converts this toml element to a float.<br>
	 *
	 * @return This toml value as a float
	 * @throws TomlTypeException If this toml element is not a toml value or not a number
	 */
	default float getAsFloat() {
		return this.getAsTomlValue().getAsFloat();
	}
	
	/**
	 * Converts this toml element to a double.<br>
	 *
	 * @return This toml value as a double
	 * @throws TomlTypeException If this toml element is not a toml value or not a number
	 */
	default double getAsDouble() {
		return this.getAsTomlValue().getAsDouble();
	}
	
	/**
	 * Converts this toml element to a string.<br>
	 *
	 * @return This toml value as a string
	 * @throws TomlTypeException If this toml element is not a toml value
	 */
	default @NonNull String getAsString() {
		return this.getAsTomlValue().getAsString();
	}
	
	/**
	 * Converts this toml element to a local date.<br>
	 *
	 * @return This toml value as a local date
	 * @throws TomlTypeException If this toml element is not a toml value or not a local date
	 */
	default @NonNull LocalDate getAsLocalDate() {
		return this.getAsTomlValue().getAsLocalDate();
	}
	
	/**
	 * Converts this toml element to a local time.<br>
	 *
	 * @return This toml value as a local time
	 * @throws TomlTypeException If this toml element is not a toml value or not a local time
	 */
	default @NonNull LocalTime getAsLocalTime() {
		return this.getAsTomlValue().getAsLocalTime();
	}
	
	/**
	 * Converts this toml element to a local date-time.<br>
	 *
	 * @return This toml value as a local date-time
	 * @throws TomlTypeException If this toml element is not a toml value or not a local date-time
	 */
	default @NonNull LocalDateTime getAsLocalDateTime() {
		return this.getAsTomlValue().getAsLocalDateTime();
	}
	
	/**
	 * Converts this toml element to an offset date-time.<br>
	 *
	 * @return This toml value as an offset date-time
	 * @throws TomlTypeException If this toml element is not a toml value or not an offset date-time
	 */
	default @NonNull OffsetDateTime getAsOffsetDateTime() {
		return this.getAsTomlValue().getAsOffsetDateTime();
	}
	
	/**
	 * Returns a string representation of this toml element based on the given toml config.<br>
	 * The toml config specifies how the toml element should be formatted.<br>
	 *
	 * @param config The toml config to use
	 * @return The string representation of this toml element
	 */
	@NonNull String toString(@NonNull TomlConfig config);
}
