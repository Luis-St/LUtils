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
import net.luis.utils.lang.StringUtils;
import org.jspecify.annotations.NonNull;

/**
 * A generic interface representing a property element.<br>
 * A property element can be a property object, a property array, a property value or a property null.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface PropertyElement {
	
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
	 * Checks if this property element is a property null.<br>
	 * @return True if this property element is a property null, false otherwise
	 */
	default boolean isPropertyNull() {
		return this instanceof PropertyNull;
	}
	
	/**
	 * Checks if this property element is a property object.<br>
	 * @return True if this property element is a property object, false otherwise
	 */
	default boolean isPropertyObject() {
		return this instanceof PropertyObject;
	}
	
	/**
	 * Checks if this property element is a property array.<br>
	 * @return True if this property element is a property array, false otherwise
	 */
	default boolean isPropertyArray() {
		return this instanceof PropertyArray;
	}
	
	/**
	 * Checks if this property element is a property value.<br>
	 * @return True if this property element is a property value, false otherwise
	 */
	default boolean isPropertyValue() {
		return this instanceof PropertyValue;
	}
	
	/**
	 * Checks if this property value is a boolean.<br>
	 * @return True if this property value is a boolean, false otherwise
	 */
	default boolean isBoolean() {
		return this.isPropertyValue() && this.getAsPropertyValue().isBoolean();
	}
	
	/**
	 * Checks if this property value is a number.<br>
	 * @return True if this property value is a number, false otherwise
	 */
	default boolean isNumber() {
		return this.isPropertyValue() && this.getAsPropertyValue().isNumber();
	}
	
	/**
	 * Checks if this property value is a byte.<br>
	 * @return True if this property value is a byte, false otherwise
	 */
	default boolean isByte() {
		return this.isPropertyValue() && this.getAsPropertyValue().isByte();
	}
	
	/**
	 * Checks if this property value is a short.<br>
	 * @return True if this property value is a short, false otherwise
	 */
	default boolean isShort() {
		return this.isPropertyValue() && this.getAsPropertyValue().isShort();
	}
	
	/**
	 * Checks if this property value is an integer.<br>
	 * @return True if this property value is an integer, false otherwise
	 */
	default boolean isInteger() {
		return this.isPropertyValue() && this.getAsPropertyValue().isInteger();
	}
	
	/**
	 * Checks if this property value is a long.<br>
	 * @return True if this property value is a long, false otherwise
	 */
	default boolean isLong() {
		return this.isPropertyValue() && this.getAsPropertyValue().isLong();
	}
	
	/**
	 * Checks if this property value is a float.<br>
	 * @return True if this property value is a float, false otherwise
	 */
	default boolean isFloat() {
		return this.isPropertyValue() && this.getAsPropertyValue().isFloat();
	}
	
	/**
	 * Checks if this property value is a double.<br>
	 * @return True if this property value is a double, false otherwise
	 */
	default boolean isDouble() {
		return this.isPropertyValue() && this.getAsPropertyValue().isDouble();
	}
	
	/**
	 * Checks if this property value is a string.<br>
	 * @return True if this property value is a string, false otherwise
	 */
	default boolean isString() {
		return this.isPropertyValue() && this.getAsPropertyValue().isString();
	}
	
	/**
	 * Converts this property element to a property object.<br>
	 *
	 * @return This property element as a property object
	 * @throws PropertyTypeException If this property element is not a property object
	 */
	default @NonNull PropertyObject getAsPropertyObject() {
		if (this instanceof PropertyObject object) {
			return object;
		}
		throw new PropertyTypeException("Expected a property object, but found: " + this.getName());
	}
	
	/**
	 * Converts this property element to a property array.<br>
	 *
	 * @return This property element as a property array
	 * @throws PropertyTypeException If this property element is not a property array
	 */
	default @NonNull PropertyArray getAsPropertyArray() {
		if (this instanceof PropertyArray array) {
			return array;
		}
		throw new PropertyTypeException("Expected a property array, but found: " + this.getName());
	}
	
	/**
	 * Converts this property element to a property value.<br>
	 *
	 * @return This property element as a property value
	 * @throws PropertyTypeException If this property element is not a property value
	 */
	default @NonNull PropertyValue getAsPropertyValue() {
		if (this instanceof PropertyValue value) {
			return value;
		}
		throw new PropertyTypeException("Expected a property value, but found: " + this.getName());
	}
	
	/**
	 * Converts this property element to a boolean.<br>
	 *
	 * @return This property value as a boolean
	 * @throws PropertyTypeException If this property element is not a property value or not a boolean
	 */
	default boolean getAsBoolean() {
		return this.getAsPropertyValue().getAsBoolean();
	}
	
	/**
	 * Converts this property element to a number.<br>
	 *
	 * @return This property value as a number
	 * @throws PropertyTypeException If this property element is not a property value or not a number
	 */
	default @NonNull Number getAsNumber() {
		return this.getAsPropertyValue().getAsNumber();
	}
	
	/**
	 * Converts this property element to a byte.<br>
	 *
	 * @return This property value as a byte
	 * @throws PropertyTypeException If this property element is not a property value or not a number
	 */
	default byte getAsByte() {
		return this.getAsPropertyValue().getAsByte();
	}
	
	/**
	 * Converts this property element to a short.<br>
	 *
	 * @return This property value as a short
	 * @throws PropertyTypeException If this property element is not a property value or not a number
	 */
	default short getAsShort() {
		return this.getAsPropertyValue().getAsShort();
	}
	
	/**
	 * Converts this property element to an integer.<br>
	 *
	 * @return This property value as an integer
	 * @throws PropertyTypeException If this property element is not a property value or not a number
	 */
	default int getAsInteger() {
		return this.getAsPropertyValue().getAsInteger();
	}
	
	/**
	 * Converts this property element to a long.<br>
	 *
	 * @return This property value as a long
	 * @throws PropertyTypeException If this property element is not a property value or not a number
	 */
	default long getAsLong() {
		return this.getAsPropertyValue().getAsLong();
	}
	
	/**
	 * Converts this property element to a float.<br>
	 *
	 * @return This property value as a float
	 * @throws PropertyTypeException If this property element is not a property value or not a number
	 */
	default float getAsFloat() {
		return this.getAsPropertyValue().getAsFloat();
	}
	
	/**
	 * Converts this property element to a double.<br>
	 *
	 * @return This property value as a double
	 * @throws PropertyTypeException If this property element is not a property value or not a number
	 */
	default double getAsDouble() {
		return this.getAsPropertyValue().getAsDouble();
	}
	
	/**
	 * Converts this property element to a string.<br>
	 *
	 * @return This property value as a string
	 * @throws PropertyTypeException If this property element is not a property value
	 */
	default @NonNull String getAsString() {
		return this.getAsPropertyValue().getAsString();
	}
	
	/**
	 * Returns a string representation of this property element based on the given property config.<br>
	 * The property config specifies how the property element should be formatted.<br>
	 *
	 * @param config The property config to use
	 * @return The string representation of this property element
	 */
	@NonNull String toString(@NonNull PropertyConfig config);
}
