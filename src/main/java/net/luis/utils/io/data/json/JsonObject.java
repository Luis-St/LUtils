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

import com.google.common.collect.Maps;
import net.luis.utils.io.data.json.exception.JsonTypeException;
import net.luis.utils.io.data.json.exception.NoSuchJsonElementException;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class JsonObject implements JsonElement {
	
	private final Map<String, JsonElement> elements = Maps.newLinkedHashMap();
	
	public JsonObject() {}
	
	public JsonObject(@NotNull Map<String, ? extends JsonElement> elements) {
		this.elements.putAll(Objects.requireNonNull(elements, "Json elements must not be null"));
	}
	
	//region Query operations
	public int size() {
		return this.elements.size();
	}
	
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
	
	//region Add operations
	public @Nullable JsonElement add(@NotNull String key, @Nullable JsonElement element) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.put(key, element == null ? JsonNull.INSTANCE : element);
	}
	
	public @Nullable JsonElement add(@NotNull String key, @Nullable String value) {
		return this.add(key, value == null ? null : new JsonPrimitive(value));
	}
	
	public @Nullable JsonElement add(@NotNull String key, boolean value) {
		return this.add(key, new JsonPrimitive(value));
	}
	
	public @Nullable JsonElement add(@NotNull String key, @Nullable Number value) {
		return this.add(key, value == null ? null : new JsonPrimitive(value));
	}
	
	public @Nullable JsonElement add(@NotNull String key, byte value) {
		return this.add(key, new JsonPrimitive(value));
	}
	
	public @Nullable JsonElement add(@NotNull String key, short value) {
		return this.add(key, new JsonPrimitive(value));
	}
	
	public @Nullable JsonElement add(@NotNull String key, int value) {
		return this.add(key, new JsonPrimitive(value));
	}
	
	public @Nullable JsonElement add(@NotNull String key, long value) {
		return this.add(key, new JsonPrimitive(value));
	}
	
	public @Nullable JsonElement add(@NotNull String key, float value) {
		return this.add(key, new JsonPrimitive(value));
	}
	
	public @Nullable JsonElement add(@NotNull String key, double value) {
		return this.add(key, new JsonPrimitive(value));
	}
	//endregion
	
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
	public @Nullable JsonElement get(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.get(key);
	}
	
	public @NotNull JsonObject getAsJsonObject(@NotNull String key) {
		JsonElement json = this.get(key);
		if (json == null) {
			throw new NoSuchJsonElementException("Expected json object for key '" + key + "', but found null");
		}
		if (json instanceof JsonObject object) {
			return object;
		}
		throw new JsonTypeException("Expected JsonObject for key '" + key + "', but found " + json.getClass().getSimpleName());
	}
	
	public @NotNull JsonArray getAsJsonArray(@NotNull String key) {
		JsonElement json = this.get(key);
		if (json == null) {
			throw new NoSuchJsonElementException("Expected json array for key '" + key + "', but found null");
		}
		if (json instanceof JsonArray array) {
			return array;
		}
		throw new JsonTypeException("Expected JsonArray for key '" + key + "', but found " + json.getClass().getSimpleName());
	}
	
	public @NotNull JsonPrimitive getJsonPrimitive(@NotNull String key) {
		JsonElement json = this.get(key);
		if (json == null) {
			throw new NoSuchJsonElementException("Expected json primitive for key '" + key + "', but found null");
		}
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
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof JsonObject that)) return false;
		
		return this.elements.equals(that.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.elements);
	}
	
	@Override
	public String toString() {
		return this.toString(JsonConfig.DEFAULT);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public @NotNull String toString(@NotNull JsonConfig config) {
		StringBuilder builder = new StringBuilder("{");
		List<Map.Entry<String, JsonElement>> entries = List.copyOf(this.elements.entrySet());
		boolean shouldSimplify = config.simplifyObjects() && config.maxObjectSimplificationSize() >=  entries.size();
		for (int i = 0; i < entries.size(); i++) {
			if (config.prettyPrint() && !shouldSimplify) {
				builder.append(System.lineSeparator());
				builder.append(config.indent());
			}
			
			Map.Entry<String, JsonElement> entry = entries.get(i);
			builder.append("\"").append(entry.getKey()).append("\": ");
			String value = entry.getValue().toString(config);
			if (config.prettyPrint() && !shouldSimplify) {
				value = value.replace(System.lineSeparator(), System.lineSeparator() + config.indent());
			}
			builder.append(value);
			if (i < this.elements.size() - 1) {
				builder.append(",");
				if (shouldSimplify) {
					builder.append(" ");
				}
			} else if (config.prettyPrint() && !shouldSimplify) {
				builder.append(System.lineSeparator());
			}
		}
		return builder.append("}").toString();
	}
	//endregion
}
