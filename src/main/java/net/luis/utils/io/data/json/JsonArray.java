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

import com.google.common.collect.Lists;
import net.luis.utils.io.data.json.exception.JsonArrayIndexOutOfBoundsException;
import net.luis.utils.io.data.json.exception.JsonTypeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class JsonArray implements JsonElement {
	
	private final List<JsonElement> elements = Lists.newLinkedList();
	
	public JsonArray() {}
	
	public JsonArray(@NotNull List<? extends JsonElement> elements) {
		this.elements.addAll(Objects.requireNonNull(elements, "Json elements must not be null"));
	}
	
	//region Query operations
	public int size() {
		return this.elements.size();
	}
	
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	public boolean contains(@Nullable JsonElement json) {
		return this.elements.contains(json);
	}
	
	public @NotNull Iterator<JsonElement> iterator() {
		return this.elements.iterator();
	}
	//endregion
	
	//region Modify operations
	public @NotNull JsonElement set(int i, @Nullable JsonElement json) {
		return this.elements.set(i, json == null ? JsonNull.INSTANCE : json);
	}
	
	public void add(@Nullable JsonElement json) {
		this.elements.add(json == null ? JsonNull.INSTANCE : json);
	}
	//endregion
	
	//region Remove operations
	public @NotNull JsonElement remove(int index) {
		if (0 > index) {
			throw new JsonArrayIndexOutOfBoundsException(index);
		}
		if (index >= this.size()) {
			throw new JsonArrayIndexOutOfBoundsException(index, this.size());
		}
		return this.elements.remove(index);
	}
	
	public boolean remove(@Nullable JsonElement json) {
		return this.elements.remove(json);
	}
	
	public void clear() {
		this.elements.clear();
	}
	//endregion
	
	//region Get operations
	public @NotNull JsonElement get(int index) {
		if (0 > index) {
			throw new JsonArrayIndexOutOfBoundsException(index);
		}
		if (index >= this.size()) {
			throw new JsonArrayIndexOutOfBoundsException(index, this.size());
		}
		return this.elements.get(index);
	}
	
	public @NotNull JsonObject getAsJsonObject(int index) {
		JsonElement json = this.get(index);
		if (json instanceof JsonObject object) {
			return object;
		}
		throw new JsonTypeException("Expected JsonObject at index " + index + ", but found: " + json.getClass().getSimpleName());
	}
	
	public @NotNull JsonArray getAsJsonArray(int index) {
		JsonElement json = this.get(index);
		if (json instanceof JsonArray array) {
			return array;
		}
		throw new JsonTypeException("Expected JsonArray at index " + index + ", but found: " + json.getClass().getSimpleName());
	}
	
	public @NotNull JsonPrimitive getJsonPrimitive(int index) {
		JsonElement json = this.get(index);
		if (json instanceof JsonPrimitive primitive) {
			return primitive;
		}
		throw new JsonTypeException("Expected JsonPrimitive at index " + index + ", but found: " + json.getClass().getSimpleName());
	}
	
	public @NotNull String getAsString(int index) {
		return this.getJsonPrimitive(index).getAsString();
	}
	
	public boolean getAsBoolean(int index) {
		return this.getJsonPrimitive(index).getAsBoolean();
	}
	
	public @NotNull Number getAsNumber(int index) {
		return this.getJsonPrimitive(index).getAsNumber();
	}
	
	public byte getAsByte(int index) {
		return this.getJsonPrimitive(index).getAsByte();
	}
	
	public short getAsShort(int index) {
		return this.getJsonPrimitive(index).getAsShort();
	}
	
	public int getAsInteger(int index) {
		return this.getJsonPrimitive(index).getAsInteger();
	}
	
	public long getAsLong(int index) {
		return this.getJsonPrimitive(index).getAsLong();
	}
	
	public float getAsFloat(int index) {
		return this.getJsonPrimitive(index).getAsFloat();
	}
	
	public double getAsDouble(int index) {
		return this.getJsonPrimitive(index).getAsDouble();
	}
	//endregion
	
	@Override
	public @NotNull String toString(@NotNull JsonConfig config) {
		StringBuilder builder = new StringBuilder("[");
		for (int i = 0; i < this.elements.size(); i++) {
			builder.append(System.lineSeparator());
			builder.append(config.indent());
			
			String json = this.elements.get(i).toString(config);
			builder.append(json.replace(System.lineSeparator(), System.lineSeparator() + config.indent()));
			if (i < this.elements.size() - 1) {
				builder.append(",");
			} else {
				builder.append(System.lineSeparator());
			}
		}
		return builder.append("]").toString();
	}
}
