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
import net.luis.utils.io.json.type.JsonType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
	
	default boolean isJsonType(@NotNull JsonType<? extends JsonElement, ?> type) {
		return Objects.requireNonNull(type, "Json type must not be null").isJsonType(this);
	}
	
	default @NotNull JsonObject getAsJsonObject() {
		if (this instanceof JsonObject object) {
			return object;
		}
		throw new JsonTypeException("Expected a JsonObject, but found: " + this.getClass().getSimpleName());
	}
	
	default @NotNull JsonArray getAsJsonArray() {
		if (this instanceof JsonArray array) {
			return array;
		}
		throw new JsonTypeException("Expected a JsonArray, but found: " + this.getClass().getSimpleName());
	}
	
	default @NotNull JsonPrimitive getAsJsonPrimitive() {
		if (this instanceof JsonPrimitive primitive) {
			return primitive;
		}
		throw new JsonTypeException("Expected a JsonPrimitive, but found: " + this.getClass().getSimpleName());
	}
	
	@SuppressWarnings("unchecked")
	default <J extends JsonElement, T> @NotNull T getAsJsonType(@NotNull JsonType<J, T> type) {
		Objects.requireNonNull(type, "Json type must not be null");
		if (type.isJsonType(this)) {
			J jsonElement = null;
			try {
				jsonElement = (J) this;
			} catch (ClassCastException e) {
				throw new IllegalStateException("Json type '" + type.getName() + "' is not correctly implemented: '" + this.toString() + "'", e);
			}
		}
		throw new JsonTypeException(this.getClass().getSimpleName() + " can not be converted to type: '" + type.getName() + "'");
	}
	
	@NotNull String toString(@NotNull JsonConfig config);
}
