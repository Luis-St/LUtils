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

package net.luis.utils.io.json;

import net.luis.utils.io.json.exception.JsonTypeException;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public interface JsonElement {

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
		throw new JsonTypeException("Json element is not a json object: '" + this.toString() + "'");
	}
	
	default @NotNull JsonArray getAsJsonArray() {
		if (this instanceof JsonArray array) {
			return array;
		}
		throw new JsonTypeException("Json element is not a json array: '" + this.toString() + "'");
	}
	
	default @NotNull JsonPrimitive getAsJsonPrimitive() {
		if (this instanceof JsonPrimitive primitive) {
			return primitive;
		}
		throw new JsonTypeException("Json element is not a json primitive: '" + this.toString() + "'");
	}
	
	@NotNull String toString(@NotNull JsonConfig config);
}
