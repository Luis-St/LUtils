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

package net.luis.utils.io.data.json;

import net.luis.utils.io.data.json.exception.JsonTypeException;
import net.luis.utils.lang.StringUtils;
import org.jspecify.annotations.NonNull;

/**
 * A generic class representing a json element.<br>
 * A json element can be a json object, a json array, a json primitive or a json null.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface JsonElement {
	
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
	 * Checks if this json element is a json null.<br>
	 * @return True if this json element is a json null, false otherwise
	 */
	default boolean isJsonNull() {
		return this instanceof JsonNull;
	}
	
	/**
	 * Checks if this json element is a json object.<br>
	 * @return True if this json element is a json object, false otherwise
	 */
	default boolean isJsonObject() {
		return this instanceof JsonObject;
	}
	
	/**
	 * Checks if this json element is a json array.<br>
	 * @return True if this json element is a json array, false otherwise
	 */
	default boolean isJsonArray() {
		return this instanceof JsonArray;
	}
	
	/**
	 * Checks if this json element is a json primitive.<br>
	 * @return True if this json element is a json primitive, false otherwise
	 */
	default boolean isJsonPrimitive() {
		return this instanceof JsonPrimitive;
	}
	
	/**
	 * Checks if this json primitive is a json boolean.<br>
	 * @return True if this json primitive is a json boolean, false otherwise
	 */
	default boolean isJsonBoolean() {
		return this.isJsonPrimitive() && this.getAsJsonPrimitive().isJsonBoolean();
	}
	
	/**
	 * Checks if this json primitive is a json number.<br>
	 * @return True if this json primitive is a json number, false otherwise
	 */
	default boolean isJsonNumber() {
		return this.isJsonPrimitive() && this.getAsJsonPrimitive().isJsonNumber();
	}
	
	/**
	 * Checks if this json primitive is a json byte.<br>
	 * @return True if this json primitive is a json byte, false otherwise
	 */
	default boolean isJsonByte() {
		return this.isJsonPrimitive() && this.getAsJsonPrimitive().isJsonByte();
	}
	
	/**
	 * Checks if this json primitive is a json short.<br>
	 * @return True if this json primitive is a json short, false otherwise
	 */
	default boolean isJsonShort() {
		return this.isJsonPrimitive() && this.getAsJsonPrimitive().isJsonShort();
	}
	
	/**
	 * Checks if this json primitive is a json integer.<br>
	 * @return True if this json primitive is a json integer, false otherwise
	 */
	default boolean isJsonInteger() {
		return this.isJsonPrimitive() && this.getAsJsonPrimitive().isJsonInteger();
	}
	
	/**
	 * Checks if this json primitive is a json long.<br>
	 * @return True if this json primitive is a json long, false otherwise
	 */
	default boolean isJsonLong() {
		return this.isJsonPrimitive() && this.getAsJsonPrimitive().isJsonLong();
	}
	
	/**
	 * Checks if this json primitive is a json float.<br>
	 * @return True if this json primitive is a json float, false otherwise
	 */
	default boolean isJsonFloat() {
		return this.isJsonPrimitive() && this.getAsJsonPrimitive().isJsonFloat();
	}
	
	/**
	 * Checks if this json primitive is a json double.<br>
	 * @return True if this json primitive is a json double, false otherwise
	 */
	default boolean isJsonDouble() {
		return this.isJsonPrimitive() && this.getAsJsonPrimitive().isJsonDouble();
	}
	
	/**
	 * Checks if this json primitive is a json string.<br>
	 * @return True if this json primitive is a json string, false otherwise
	 */
	default boolean isJsonString() {
		return this.isJsonPrimitive() && this.getAsJsonPrimitive().isJsonString();
	}
	
	/**
	 * Converts this json element to a json object.<br>
	 *
	 * @return This json element as a json object
	 * @throws JsonTypeException If this json element is not a json object
	 */
	default @NonNull JsonObject getAsJsonObject() {
		if (this instanceof JsonObject object) {
			return object;
		}
		throw new JsonTypeException("Expected a json object, but found: " + this.getName());
	}
	
	/**
	 * Converts this json element to a json array.<br>
	 *
	 * @return This json element as a json array
	 * @throws JsonTypeException If this json element is not a json array
	 */
	default @NonNull JsonArray getAsJsonArray() {
		if (this instanceof JsonArray array) {
			return array;
		}
		throw new JsonTypeException("Expected a json array, but found: " + this.getName());
	}
	
	/**
	 * Converts this json element to a json primitive.<br>
	 *
	 * @return This json element as a json primitive
	 * @throws JsonTypeException If this json element is not a json primitive
	 */
	default @NonNull JsonPrimitive getAsJsonPrimitive() {
		if (this instanceof JsonPrimitive primitive) {
			return primitive;
		}
		throw new JsonTypeException("Expected a json primitive, but found: " + this.getName());
	}
	
	/**
	 * Converts this json element to a boolean.<br>
	 *
	 * @return This json primitive as a boolean
	 * @throws JsonTypeException If this json primitive is not a json boolean
	 */
	default boolean getAsBoolean() {
		return this.getAsJsonPrimitive().getAsBoolean();
	}
	
	/**
	 * Converts this json element to a number.<br>
	 *
	 * @return This json primitive as a number
	 * @throws JsonTypeException If this json primitive is not a json number
	 */
	default @NonNull Number getAsNumber() {
		return this.getAsJsonPrimitive().getAsNumber();
	}
	
	/**
	 * Converts this json element to a byte.<br>
	 *
	 * @return This json primitive as a byte
	 * @throws JsonTypeException If this json primitive is not a json byte
	 */
	default byte getAsByte() {
		return this.getAsJsonPrimitive().getAsByte();
	}
	
	/**
	 * Converts this json element to a short.<br>
	 *
	 * @return This json primitive as a short
	 * @throws JsonTypeException If this json primitive is not a json short
	 */
	default short getAsShort() {
		return this.getAsJsonPrimitive().getAsShort();
	}
	
	/**
	 * Converts this json element to an integer.<br>
	 *
	 * @return This json primitive as an integer
	 * @throws JsonTypeException If this json primitive is not a json integer
	 */
	default int getAsInteger() {
		return this.getAsJsonPrimitive().getAsInteger();
	}
	
	/**
	 * Converts this json element to a long.<br>
	 *
	 * @return This json primitive as a long
	 * @throws JsonTypeException If this json primitive is not a json long
	 */
	default long getAsLong() {
		return this.getAsJsonPrimitive().getAsLong();
	}
	
	/**
	 * Converts this json element to a float.<br>
	 *
	 * @return This json primitive as a float
	 * @throws JsonTypeException If this json primitive is not a json float
	 */
	default float getAsFloat() {
		return this.getAsJsonPrimitive().getAsFloat();
	}
	
	/**
	 * Converts this json element to a double.<br>
	 *
	 * @return This json primitive as a double
	 * @throws JsonTypeException If this json primitive is not a json double
	 */
	default double getAsDouble() {
		return this.getAsJsonPrimitive().getAsDouble();
	}
	
	/**
	 * Converts this json element to a string.<br>
	 *
	 * @return This json primitive as a string
	 * @throws JsonTypeException If this json primitive is not a json string
	 */
	default @NonNull String getAsString() {
		return this.getAsJsonPrimitive().getAsString();
	}
	
	/**
	 * Returns a string representation of this json element based on the given json config.<br>
	 * The json config specifies how the json element should be formatted.<br>
	 *
	 * @param config The json config to use
	 * @return The string representation of this json element
	 */
	@NonNull String toString(@NonNull JsonConfig config);
}
