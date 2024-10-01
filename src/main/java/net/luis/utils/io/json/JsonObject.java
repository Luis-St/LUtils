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

import com.google.common.collect.Maps;
import net.luis.utils.io.json.exception.JsonTypeException;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class JsonObject implements JsonElement {
	
	private final Map<String, JsonElement> elements = Maps.newLinkedHashMap();
	
	//region Query operations
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	public boolean containsKey(@Nullable String key) {
		return this.elements.containsKey(key);
	}
	
	public boolean containsValue(@Nullable JsonElement element) {
		return this.elements.containsValue(element);
	}
	
	public @NotNull Set<String> keySet() {
		return this.elements.keySet();
	}
	
	public @NotNull Collection<JsonElement> values() {
		return this.elements.values();
	}
	
	public @NotNull Set<Map.Entry<String, JsonElement>> entrySet() {
		return this.elements.entrySet();
	}
	//endregion
	
	public @Nullable JsonElement put(@NotNull String key, @Nullable JsonElement element) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.put(key, element);
	}
	
	//region Remove operations
	public @Nullable JsonElement remove(@Nullable String key) {
		return this.elements.remove(key);
	}
	
	public void clear() {
		this.elements.clear();
	}
	//endregion
	
	//region Replace operations
	public @Nullable JsonElement replace(@NotNull String key, @Nullable JsonElement value) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.replace(key, value == null ? JsonNull.INSTANCE : value);
	}
	
	public boolean replace(@NotNull String key, @NotNull JsonElement oldValue, @Nullable JsonElement newValue) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(oldValue, "Old value must not be null");
		return this.elements.replace(key, oldValue, newValue == null ? JsonNull.INSTANCE : newValue);
	}
	//endregion
	
	//region Get operations
	public @NotNull JsonElement get(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.getOrDefault(key, JsonNull.INSTANCE);
	}
	
	public @NotNull JsonObject getAsJsonObject(@NotNull String key) {
		JsonElement json = this.get(key);
		if (json instanceof JsonObject object) {
			return object;
		}
		throw new JsonTypeException("Expected JsonObject for key '" + key + "', but found " + json.getClass().getSimpleName());
	}
	
	public @NotNull JsonArray getAsJsonArray(@NotNull String key) {
		JsonElement json = this.get(key);
		if (json instanceof JsonArray array) {
			return array;
		}
		throw new JsonTypeException("Expected JsonArray for key '" + key + "', but found " + json.getClass().getSimpleName());
	}
	
	public @NotNull JsonPrimitive getJsonPrimitive(@NotNull String key) {
		JsonElement json = this.get(key);
		if (json instanceof JsonPrimitive primitive) {
			return primitive;
		}
		throw new JsonTypeException("Expected JsonPrimitive for key '" + key + "', but found " + json.getClass().getSimpleName());
	}
	
	public @NotNull String getAsString(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsString();
	}
	
	public boolean getAsBoolean(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsBoolean();
	}
	
	public @NotNull Number getAsNumber(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsNumber();
	}
	
	public byte getAsByte(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsByte();
	}
	
	public short getAsShort(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsShort();
	}
	
	public int getAsInteger(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsInteger();
	}
	
	public long getAsLong(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsLong();
	}
	
	public float getAsFloat(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsFloat();
	}
	
	public double getAsDouble(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsDouble();
	}
	//endregion
	
	@Override
	public @NotNull String toString(@NotNull JsonConfig config) {
		return "";
	}
}
