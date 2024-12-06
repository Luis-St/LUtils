/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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
import org.jetbrains.annotations.NotNull;

/**
 * A generic class representing a json element.<br>
 * A json element can be a json object, a json array, a json primitive or a json null.<br>
 *
 * @author Luis-St
 */
public interface JsonElement {
	
	/**
	 * Returns the name of the class in a human-readable format.<br>
	 * The name is the class name with spaces between the words and all letters in lower case.<br>
	 * @return The name of the class in a human-readable format
	 */
	private @NotNull String getName() {
		StringBuilder name = new StringBuilder();
		String className = this.getClass().getSimpleName();
		for (int i = 0; i < className.length(); i++) {
			char c = className.charAt(i);
			if (Character.isUpperCase(c)) {
				name.append(" ");
				name.append(Character.toLowerCase(c));
			} else {
				name.append(c);
			}
		}
		return name.toString().strip();
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
	 * Converts this json element to a json object.<br>
	 * @return This json element as a json object
	 * @throws JsonTypeException If this json element is not a json object
	 */
	default @NotNull JsonObject getAsJsonObject() {
		if (this instanceof JsonObject object) {
			return object;
		}
		throw new JsonTypeException("Expected a json object, but found: " + this.getName());
	}
	
	/**
	 * Converts this json element to a json array.<br>
	 * @return This json element as a json array
	 * @throws JsonTypeException If this json element is not a json array
	 */
	default @NotNull JsonArray getAsJsonArray() {
		if (this instanceof JsonArray array) {
			return array;
		}
		throw new JsonTypeException("Expected a json array, but found: " + this.getName());
	}
	
	/**
	 * Converts this json element to a json primitive.<br>
	 * @return This json element as a json primitive
	 * @throws JsonTypeException If this json element is not a json primitive
	 */
	default @NotNull JsonPrimitive getAsJsonPrimitive() {
		if (this instanceof JsonPrimitive primitive) {
			return primitive;
		}
		throw new JsonTypeException("Expected a json primitive, but found: " + this.getName());
	}
	
	/**
	 * Returns a string representation of this json element based on the given json config.<br>
	 * The json config specifies how the json element should be formatted.<br>
	 * @param config The json config to use
	 * @return The string representation of this json element
	 */
	@NotNull String toString(@NotNull JsonConfig config);
}
