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
 *
 * @author Luis-St
 *
 */

public interface JsonElement {
	
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
		return name.toString();
	}
	
	default boolean isJsonNull() {
		return this instanceof JsonNull;
	}
	
	default boolean isJsonObject() {
		return this instanceof JsonObject;
	}
	
	default boolean isJsonArray() {
		return this instanceof JsonArray;
	}
	
	default boolean isJsonPrimitive() {
		return this instanceof JsonPrimitive;
	}
	
	default @NotNull JsonObject getAsJsonObject() {
		if (this instanceof JsonObject object) {
			return object;
		}
		throw new JsonTypeException("Expected a json object, but found: " + this.getName());
	}
	
	default @NotNull JsonArray getAsJsonArray() {
		if (this instanceof JsonArray array) {
			return array;
		}
		throw new JsonTypeException("Expected a json array, but found: " + this.getName());
	}
	
	default @NotNull JsonPrimitive getAsJsonPrimitive() {
		if (this instanceof JsonPrimitive primitive) {
			return primitive;
		}
		throw new JsonTypeException("Expected a json primitive, but found: " + this.getName());
	}
	
	@NotNull String toString(@NotNull JsonConfig config);
}
