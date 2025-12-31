/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Represents a json array.<br>
 * A json array is an ordered collection of values.<br>
 * The values can be of any type, including json null.<br>
 *
 * @author Luis-St
 */
public class JsonArray implements JsonElement, Iterable<JsonElement> {
	
	/**
	 * The internal linked list of json elements.<br>
	 * The elements are stored in the order they were added.<br>
	 */
	private final List<JsonElement> elements = Lists.newLinkedList();
	
	/**
	 * Constructs an empty json array.<br>
	 */
	public JsonArray() {}
	
	/**
	 * Constructs a json array with the given list of json elements.<br>
	 * @param elements The list of json elements to add
	 * @throws NullPointerException If the list of json elements is null
	 */
	public JsonArray(@NonNull List<? extends JsonElement> elements) {
		this.elements.addAll(Objects.requireNonNull(elements, "Json elements must not be null"));
	}
	
	/**
	 * Returns the number of elements in this json array.<br>
	 * @return The size of this json array
	 */
	public int size() {
		return this.elements.size();
	}
	
	/**
	 * Checks if this json array is empty.<br>
	 * @return True if this json array is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * Checks if this json array contains the given json element.<br>
	 *
	 * @param json The json element to check for
	 * @return True if this json array contains the given json element, false otherwise
	 */
	public boolean contains(@Nullable JsonElement json) {
		return this.elements.contains(json);
	}
	
	/**
	 * Returns an iterator over the elements in this json array.<br>
	 * @return The iterator over the elements
	 */
	@Override
	public @NonNull Iterator<JsonElement> iterator() {
		return this.elements.iterator();
	}
	
	/**
	 * Returns an unmodifiable collection of the json elements in this json array.<br>
	 * @return The collection of json elements
	 */
	public @NonNull @Unmodifiable Collection<JsonElement> elements() {
		return Collections.unmodifiableCollection(this.elements);
	}
	
	/**
	 * Returns an unmodifiable list of the json elements in this json array.<br>
	 * @return The list of json elements
	 */
	public @NonNull @Unmodifiable List<JsonElement> getElements() {
		return List.copyOf(this.elements);
	}
	
	/**
	 * Sets the element at the given index to the given json element.<br>
	 * If the json element is null, it will be replaced with a json null element.<br>
	 *
	 * @param index The index of the element to set
	 * @param json The json element to set
	 * @return The previous element at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 */
	public @NonNull JsonElement set(int index, @Nullable JsonElement json) {
		if (0 > index) {
			throw new JsonArrayIndexOutOfBoundsException(index);
		}
		if (index >= this.size()) {
			throw new JsonArrayIndexOutOfBoundsException(index, this.size());
		}
		
		return this.elements.set(index, json == null ? JsonNull.INSTANCE : json);
	}
	
	/**
	 * Sets the element at the given index to the given string.<br>
	 * The string will be converted to a json primitive element.<br>
	 * If the string is null, it will be replaced with a json null element.<br>
	 *
	 * @param index The index of the element to set
	 * @param value The string to set
	 * @return The previous element at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @see #set(int, JsonElement)
	 */
	public @NonNull JsonElement set(int index, @Nullable String value) {
		return this.set(index, value == null ? null : new JsonPrimitive(value));
	}
	
	/**
	 * Sets the element at the given index to the given boolean.<br>
	 * The boolean will be converted to a json primitive element.<br>
	 *
	 * @param index The index of the element to set
	 * @param value The boolean to set
	 * @return The previous element at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @see #set(int, JsonElement)
	 */
	public @NonNull JsonElement set(int index, boolean value) {
		return this.set(index, new JsonPrimitive(value));
	}
	
	/**
	 * Sets the element at the given index to the given number.<br>
	 * The number will be converted to a json primitive element.<br>
	 * If the number is null, it will be replaced with a json null element.<br>
	 *
	 * @param index The index of the element to set
	 * @param value The number to set
	 * @return The previous element at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @see #set(int, JsonElement)
	 */
	public @NonNull JsonElement set(int index, @Nullable Number value) {
		return this.set(index, value == null ? null : new JsonPrimitive(value));
	}
	
	/**
	 * Sets the element at the given index to the given byte.<br>
	 * The byte will be converted to a json primitive element.<br>
	 *
	 * @param index The index of the element to set
	 * @param value The byte to set
	 * @return The previous element at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @see #set(int, JsonElement)
	 */
	public @NonNull JsonElement set(int index, byte value) {
		return this.set(index, new JsonPrimitive(value));
	}
	
	/**
	 * Sets the element at the given index to the given short.<br>
	 * The short will be converted to a json primitive element.<br>
	 *
	 * @param index The index of the element to set
	 * @param value The short to set
	 * @return The previous element at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @see #set(int, JsonElement)
	 */
	public @NonNull JsonElement set(int index, short value) {
		return this.set(index, new JsonPrimitive(value));
	}
	
	/**
	 * Sets the element at the given index to the given int.<br>
	 * The int will be converted to a json primitive element.<br>
	 *
	 * @param index The index of the element to set
	 * @param value The int to set
	 * @return The previous element at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @see #set(int, JsonElement)
	 */
	public @NonNull JsonElement set(int index, int value) {
		return this.set(index, new JsonPrimitive(value));
	}
	
	/**
	 * Sets the element at the given index to the given long.<br>
	 * The long will be converted to a json primitive element.<br>
	 *
	 * @param index The index of the element to set
	 * @param value The long to set
	 * @return The previous element at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @see #set(int, JsonElement)
	 */
	public @NonNull JsonElement set(int index, long value) {
		return this.set(index, new JsonPrimitive(value));
	}
	
	/**
	 * Sets the element at the given index to the given float.<br>
	 * The float will be converted to a json primitive element.<br>
	 *
	 * @param index The index of the element to set
	 * @param value The float to set
	 * @return The previous element at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @see #set(int, JsonElement)
	 */
	public @NonNull JsonElement set(int index, float value) {
		return this.set(index, new JsonPrimitive(value));
	}
	
	/**
	 * Sets the element at the given index to the given double.<br>
	 * The double will be converted to a json primitive element.<br>
	 *
	 * @param index The index of the element to set
	 * @param value The double to set
	 * @return The previous element at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @see #set(int, JsonElement)
	 */
	public @NonNull JsonElement set(int index, double value) {
		return this.set(index, new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given json element to this json array.<br>
	 * If the json element is null, it will be replaced with a json null element.<br>
	 *
	 * @param json The json element to add
	 */
	public void add(@Nullable JsonElement json) {
		this.elements.add(json == null ? JsonNull.INSTANCE : json);
	}
	
	/**
	 * Adds the given string to this json array.<br>
	 * The string will be converted to a json primitive element.<br>
	 * If the string is null, it will be replaced with a json null element.<br>
	 *
	 * @param value The string to add
	 */
	public void add(@Nullable String value) {
		this.add(value == null ? null : new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given boolean to this json array.<br>
	 * The boolean will be converted to a json primitive element.<br>
	 *
	 * @param value The boolean to add
	 */
	public void add(boolean value) {
		this.add(new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given number to this json array.<br>
	 * The number will be converted to a json primitive element.<br>
	 * If the number is null, it will be replaced with a json null element.<br>
	 *
	 * @param value The number to add
	 */
	public void add(@Nullable Number value) {
		this.add(value == null ? null : new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given byte to this json array.<br>
	 * The byte will be converted to a json primitive element.<br>
	 *
	 * @param value The byte to add
	 */
	public void add(byte value) {
		this.add(new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given short to this json array.<br>
	 * The short will be converted to a json primitive element.<br>
	 *
	 * @param value The short to add
	 */
	public void add(short value) {
		this.add(new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given int to this json array.<br>
	 * The int will be converted to a json primitive element.<br>
	 *
	 * @param value The int to add
	 */
	public void add(int value) {
		this.add(new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given long to this json array.<br>
	 * The long will be converted to a json primitive element.<br>
	 *
	 * @param value The long to add
	 */
	public void add(long value) {
		this.add(new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given float to this json array.<br>
	 * The float will be converted to a json primitive element.<br>
	 *
	 * @param value The float to add
	 */
	public void add(float value) {
		this.add(new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given double to this json array.<br>
	 * The double will be converted to a json primitive element.<br>
	 *
	 * @param value The double to add
	 */
	public void add(double value) {
		this.add(new JsonPrimitive(value));
	}
	
	/**
	 * Adds all json elements from the given json array to this json array.<br>
	 *
	 * @param array The json array to add
	 * @throws NullPointerException If the json array is null
	 */
	public void addAll(@NonNull JsonArray array) {
		this.addAll(Objects.requireNonNull(array, "Json array must not be null").elements);
	}
	
	/**
	 * Adds all json elements from the given array to this json array.<br>
	 * @param elements The array of json elements to add
	 */
	public void addAll(JsonElement @NonNull ... elements) {
		Objects.requireNonNull(elements, "Json elements must not be null");
		this.addAll(Arrays.asList(elements));
	}
	
	/**
	 * Adds all json elements from the given list to this json array.<br>
	 *
	 * @param elements The list of json elements to add
	 * @throws NullPointerException If the list of json elements is null
	 */
	public void addAll(@NonNull List<? extends JsonElement> elements) {
		this.elements.addAll(Objects.requireNonNull(elements, "Json elements must not be null"));
	}
	
	/**
	 * Removes the element at the given index from this json array.<br>
	 *
	 * @param index The index of the element to remove
	 * @return The removed element
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 */
	public @NonNull JsonElement remove(int index) {
		if (0 > index) {
			throw new JsonArrayIndexOutOfBoundsException(index);
		}
		if (index >= this.size()) {
			throw new JsonArrayIndexOutOfBoundsException(index, this.size());
		}
		
		return this.elements.remove(index);
	}
	
	/**
	 * Removes the given json element from this json array.<br>
	 *
	 * @param json The json element to remove
	 * @return True if the json element was removed, false otherwise
	 */
	public boolean remove(@Nullable JsonElement json) {
		return this.elements.remove(json);
	}
	
	/**
	 * Removes all elements from this json array.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	
	/**
	 * Gets the json element at the given index from this json array.<br>
	 *
	 * @param index The index of the element to get
	 * @return The json element at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 */
	public @NonNull JsonElement get(int index) {
		if (0 > index) {
			throw new JsonArrayIndexOutOfBoundsException(index);
		}
		if (index >= this.size()) {
			throw new JsonArrayIndexOutOfBoundsException(index, this.size());
		}
		
		return this.elements.get(index);
	}
	
	/**
	 * Gets the json element at the given index from this json array as a json object.<br>
	 *
	 * @param index The index of the element to get
	 * @return The json object at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @throws JsonTypeException If the json element at the given index is not a json object
	 * @see #get(int)
	 */
	public @NonNull JsonObject getAsJsonObject(int index) {
		JsonElement json = this.get(index);
		
		if (json instanceof JsonObject object) {
			return object;
		}
		return json.getAsJsonObject();
	}
	
	/**
	 * Gets the json element at the given index from this json array as a json array.<br>
	 *
	 * @param index The index of the element to get
	 * @return The json array at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @throws JsonTypeException If the json element at the given index is not a json array
	 * @see #get(int)
	 */
	public @NonNull JsonArray getAsJsonArray(int index) {
		JsonElement json = this.get(index);
		
		if (json instanceof JsonArray array) {
			return array;
		}
		return json.getAsJsonArray();
	}
	
	/**
	 * Gets the json element at the given index from this json array as a json primitive.<br>
	 *
	 * @param index The index of the element to get
	 * @return The json primitive at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @throws JsonTypeException If the json element at the given index is not a json primitive
	 * @see #get(int)
	 */
	public @NonNull JsonPrimitive getAsJsonPrimitive(int index) {
		JsonElement json = this.get(index);
		
		if (json instanceof JsonPrimitive primitive) {
			return primitive;
		}
		return json.getAsJsonPrimitive();
	}
	
	/**
	 * Gets the json element at the given index from this json array as a string.<br>
	 * The element will be converted to a json primitive and then to a string.<br>
	 *
	 * @param index The index of the element to get
	 * @return The string at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @throws JsonTypeException If the json element at the given index is not a json primitive
	 * @see #getAsJsonPrimitive(int)
	 */
	public @NonNull String getAsString(int index) {
		return this.getAsJsonPrimitive(index).getAsString();
	}
	
	/**
	 * Gets the json element at the given index from this json array as a boolean.<br>
	 * The element will be converted to a json primitive and then to a boolean.<br>
	 *
	 * @param index The index of the element to get
	 * @return The boolean at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @throws JsonTypeException If the json element at the given index is not a json primitive
	 * @see #getAsJsonPrimitive(int)
	 */
	public boolean getAsBoolean(int index) {
		return this.getAsJsonPrimitive(index).getAsBoolean();
	}
	
	/**
	 * Gets the json element at the given index from this json array as a number.<br>
	 * The element will be converted to a json primitive and then to a number.<br>
	 *
	 * @param index The index of the element to get
	 * @return The number at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @throws JsonTypeException If the json element at the given index is not a json primitive
	 * @see #getAsJsonPrimitive(int)
	 */
	public @NonNull Number getAsNumber(int index) {
		return this.getAsJsonPrimitive(index).getAsNumber();
	}
	
	/**
	 * Gets the json element at the given index from this json array as a byte.<br>
	 * The element will be converted to a json primitive and then to a byte.<br>
	 *
	 * @param index The index of the element to get
	 * @return The byte at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @throws JsonTypeException If the json element at the given index is not a json primitive
	 * @see #getAsJsonPrimitive(int)
	 */
	public byte getAsByte(int index) {
		return this.getAsJsonPrimitive(index).getAsByte();
	}
	
	/**
	 * Gets the json element at the given index from this json array as a short.<br>
	 * The element will be converted to a json primitive and then to a short.<br>
	 *
	 * @param index The index of the element to get
	 * @return The short at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @throws JsonTypeException If the json element at the given index is not a json primitive
	 * @see #getAsJsonPrimitive(int)
	 */
	public short getAsShort(int index) {
		return this.getAsJsonPrimitive(index).getAsShort();
	}
	
	/**
	 * Gets the json element at the given index from this json array as an int.<br>
	 * The element will be converted to a json primitive and then to an int.<br>
	 *
	 * @param index The index of the element to get
	 * @return The int at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @throws JsonTypeException If the json element at the given index is not a json primitive
	 * @see #getAsJsonPrimitive(int)
	 */
	public int getAsInteger(int index) {
		return this.getAsJsonPrimitive(index).getAsInteger();
	}
	
	/**
	 * Gets the json element at the given index from this json array as a long.<br>
	 * The element will be converted to a json primitive and then to a long.<br>
	 *
	 * @param index The index of the element to get
	 * @return The long at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @throws JsonTypeException If the json element at the given index is not a json primitive
	 * @see #getAsJsonPrimitive(int)
	 */
	public long getAsLong(int index) {
		return this.getAsJsonPrimitive(index).getAsLong();
	}
	
	/**
	 * Gets the json element at the given index from this json array as a float.<br>
	 * The element will be converted to a json primitive and then to a float.<br>
	 *
	 * @param index The index of the element to get
	 * @return The float at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @throws JsonTypeException If the json element at the given index is not a json primitive
	 * @see #getAsJsonPrimitive(int)
	 */
	public float getAsFloat(int index) {
		return this.getAsJsonPrimitive(index).getAsFloat();
	}
	
	/**
	 * Gets the json element at the given index from this json array as a double.<br>
	 * The element will be converted to a json primitive and then to a double.<br>
	 *
	 * @param index The index of the element to get
	 * @return The double at the given index
	 * @throws JsonArrayIndexOutOfBoundsException If the index is negative or greater than the size of this json array
	 * @throws JsonTypeException If the json element at the given index is not a json primitive
	 * @see #getAsJsonPrimitive(int)
	 */
	public double getAsDouble(int index) {
		return this.getAsJsonPrimitive(index).getAsDouble();
	}
	
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
	public @NonNull String toString(@NonNull JsonConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
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
