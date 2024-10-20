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
	
	//region Set operations
	public @NotNull JsonElement set(int index, @Nullable JsonElement json) {
		if (0 > index) {
			throw new JsonArrayIndexOutOfBoundsException(index);
		}
		if (index >= this.size()) {
			throw new JsonArrayIndexOutOfBoundsException(index, this.size());
		}
		return this.elements.set(index, json == null ? JsonNull.INSTANCE : json);
	}
	
	public @NotNull JsonElement set(int index, @Nullable String value) {
		return this.set(index, value == null ? null : new JsonPrimitive(value));
	}
	
	public @NotNull JsonElement set(int index, boolean value) {
		return this.set(index, new JsonPrimitive(value));
	}
	
	public @NotNull JsonElement set(int index, @Nullable Number value) {
		return this.set(index, value == null ? null : new JsonPrimitive(value));
	}
	
	public @NotNull JsonElement set(int index, byte value) {
		return this.set(index, new JsonPrimitive(value));
	}
	
	public @NotNull JsonElement set(int index, short value) {
		return this.set(index, new JsonPrimitive(value));
	}
	
	public @NotNull JsonElement set(int index, int value) {
		return this.set(index, new JsonPrimitive(value));
	}
	
	public @NotNull JsonElement set(int index, long value) {
		return this.set(index, new JsonPrimitive(value));
	}
	
	public @NotNull JsonElement set(int index, float value) {
		return this.set(index, new JsonPrimitive(value));
	}
	
	public @NotNull JsonElement set(int index, double value) {
		return this.set(index, new JsonPrimitive(value));
	}
	//endregion
	
	//region Add operations
	public void add(@Nullable JsonElement json) {
		this.elements.add(json == null ? JsonNull.INSTANCE : json);
	}
	
	public void add(@Nullable String value) {
		this.add(value == null ? null : new JsonPrimitive(value));
	}
	
	public void add(boolean value) {
		this.add(new JsonPrimitive(value));
	}
	
	public void add(@Nullable Number value) {
		this.add(value == null ? null : new JsonPrimitive(value));
	}
	
	public void add(byte value) {
		this.add(new JsonPrimitive(value));
	}
	
	public void add(short value) {
		this.add(new JsonPrimitive(value));
	}
	
	public void add(int value) {
		this.add(new JsonPrimitive(value));
	}
	
	public void add(long value) {
		this.add(new JsonPrimitive(value));
	}
	
	public void add(float value) {
		this.add(new JsonPrimitive(value));
	}
	
	public void add(double value) {
		this.add(new JsonPrimitive(value));
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
	
	public @NotNull JsonPrimitive getAsJsonPrimitive(int index) {
		JsonElement json = this.get(index);
		if (json instanceof JsonPrimitive primitive) {
			return primitive;
		}
		throw new JsonTypeException("Expected JsonPrimitive at index " + index + ", but found: " + json.getClass().getSimpleName());
	}
	
	public @NotNull String getAsString(int index) {
		return this.getAsJsonPrimitive(index).getAsString();
	}
	
	public boolean getAsBoolean(int index) {
		return this.getAsJsonPrimitive(index).getAsBoolean();
	}
	
	public @NotNull Number getAsNumber(int index) {
		return this.getAsJsonPrimitive(index).getAsNumber();
	}
	
	public byte getAsByte(int index) {
		return this.getAsJsonPrimitive(index).getAsByte();
	}
	
	public short getAsShort(int index) {
		return this.getAsJsonPrimitive(index).getAsShort();
	}
	
	public int getAsInteger(int index) {
		return this.getAsJsonPrimitive(index).getAsInteger();
	}
	
	public long getAsLong(int index) {
		return this.getAsJsonPrimitive(index).getAsLong();
	}
	
	public float getAsFloat(int index) {
		return this.getAsJsonPrimitive(index).getAsFloat();
	}
	
	public double getAsDouble(int index) {
		return this.getAsJsonPrimitive(index).getAsDouble();
	}
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof JsonArray array)) return false;
		
		return this.elements.equals(array.elements);
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
		StringBuilder builder = new StringBuilder("[");
		boolean shouldSimplify = config.simplifyArrays() && config.maxArraySimplificationSize() >= this.size();
		for (int i = 0; i < this.elements.size(); i++) {
			if (config.prettyPrint() && !shouldSimplify) {
				builder.append(System.lineSeparator());
				builder.append(config.indent());
			}
			
			String json = this.elements.get(i).toString(config);
			if (config.prettyPrint() && !shouldSimplify) {
				json = json.replace(System.lineSeparator(), System.lineSeparator() + config.indent());
			}
			builder.append(json);
			if (i < this.elements.size() - 1) {
				builder.append(",");
				if (shouldSimplify) {
					builder.append(" ");
				}
			} else if (config.prettyPrint() && !shouldSimplify) {
				builder.append(System.lineSeparator());
			}
		}
		return builder.append("]").toString();
	}
	//endregion
}
