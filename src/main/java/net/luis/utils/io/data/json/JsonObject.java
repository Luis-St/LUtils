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

import com.google.common.collect.Maps;
import net.luis.utils.io.data.json.exception.JsonTypeException;
import net.luis.utils.io.data.json.exception.NoSuchJsonElementException;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Represents a json object.<br>
 * A json object is an ordered set of name/value pairs.<br>
 * A name is a string and a value can be any json element, including json null.<br>
 *
 * @author Luis-St
 */
public class JsonObject implements JsonElement {
	
	/**
	 * The internal map of elements.<br>
	 * The order of the elements is preserved.<br>
	 */
	private final Map<String, JsonElement> elements = Maps.newLinkedHashMap();
	
	/**
	 * Constructs an empty json object.<br>
	 */
	public JsonObject() {}
	
	/**
	 * Constructs a json object with the given elements.<br>
	 * @param elements The map of elements to add
	 * @throws NullPointerException If the given elements are null
	 */
	public JsonObject(@NotNull Map<String, ? extends JsonElement> elements) {
		this.elements.putAll(Objects.requireNonNull(elements, "Json elements must not be null"));
	}
	
	//region Query operations
	
	/**
	 * Returns the number of elements in this json object.<br>
	 * @return The size of this json object
	 */
	public int size() {
		return this.elements.size();
	}
	
	/**
	 * Checks if this json object is empty.<br>
	 * @return True if this json object is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * Checks if this json object contains the given key.<br>
	 * @param key The key to check
	 * @return True if this json object contains the given key, false otherwise
	 */
	public boolean containsKey(@Nullable String key) {
		return this.elements.containsKey(key);
	}
	
	/**
	 * Checks if this json object contains the given element.<br>
	 * @param element The element to check
	 * @return True if this json object contains the given element, false otherwise
	 */
	public boolean containsValue(@Nullable JsonElement element) {
		return this.elements.containsValue(element);
	}
	
	/**
	 * Returns the set of keys in this json object.<br>
	 * @return The keys of this json object
	 */
	public @NotNull Set<String> keySet() {
		return this.elements.keySet();
	}
	
	/**
	 * Returns the collection of values in this json object.<br>
	 * @return The values of this json object
	 */
	public @NotNull @Unmodifiable Collection<JsonElement> elements() {
		return Collections.unmodifiableCollection(this.elements.values());
	}
	
	/**
	 * Returns the set of entries in this json object.<br>
	 * @return The entries of this json object
	 */
	public @NotNull Set<Map.Entry<String, JsonElement>> entrySet() {
		return this.elements.entrySet();
	}
	
	/**
	 * Iterates over the entries of this json object and applies the given action to each entry.<br>
	 * @param action The action to apply to each entry
	 * @throws NullPointerException If the given action is null
	 */
	public void forEach(@NotNull BiConsumer<? super String, ? super JsonElement> action) {
		this.elements.forEach(Objects.requireNonNull(action, "Action must not be null"));
	}
	//endregion
	
	//region Add operations
	
	/**
	 * Adds the given element with the given key to this json object.<br>
	 * If the element is null, it will be replaced with json null.<br>
	 * If the key is already present, the element will be replaced.<br>
	 * @param key The key to add
	 * @param element The element to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable JsonElement add(@NotNull String key, @Nullable JsonElement element) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.put(key, element == null ? JsonNull.INSTANCE : element);
	}
	
	/**
	 * Adds the given string with the given key to this json object.<br>
	 * If the string is null, it will be replaced with json null.<br>
	 * The string value will be converted to a json primitive.<br>
	 * @param key The key to add
	 * @param value The string value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, JsonElement)
	 */
	public @Nullable JsonElement add(@NotNull String key, @Nullable String value) {
		return this.add(key, value == null ? null : new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given boolean with the given key to this json object.<br>
	 * The boolean will be converted to a json primitive.<br>
	 * @param key The key to add
	 * @param value The boolean value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, JsonElement)
	 */
	public @Nullable JsonElement add(@NotNull String key, boolean value) {
		return this.add(key, new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given number with the given key to this json object.<br>
	 * If the number is null, it will be replaced with json null.<br>
	 * The number value will be converted to a json primitive.<br>
	 * @param key The key to add
	 * @param value The number value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, JsonElement)
	 */
	public @Nullable JsonElement add(@NotNull String key, @Nullable Number value) {
		return this.add(key, value == null ? null : new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given byte with the given key to this json object.<br>
	 * The byte will be converted to a json primitive.<br>
	 * @param key The key to add
	 * @param value The byte value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, JsonElement)
	 */
	public @Nullable JsonElement add(@NotNull String key, byte value) {
		return this.add(key, new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given short with the given key to this json object.<br>
	 * The short will be converted to a json primitive.<br>
	 * @param key The key to add
	 * @param value The short value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, JsonElement)
	 */
	public @Nullable JsonElement add(@NotNull String key, short value) {
		return this.add(key, new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given int with the given key to this json object.<br>
	 * The int will be converted to a json primitive.<br>
	 * @param key The key to add
	 * @param value The int value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, JsonElement)
	 */
	public @Nullable JsonElement add(@NotNull String key, int value) {
		return this.add(key, new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given long with the given key to this json object.<br>
	 * The long will be converted to a json primitive.<br>
	 * @param key The key to add
	 * @param value The long value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, JsonElement)
	 */
	public @Nullable JsonElement add(@NotNull String key, long value) {
		return this.add(key, new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given float with the given key to this json object.<br>
	 * The float will be converted to a json primitive.<br>
	 * @param key The key to add
	 * @param value The float value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, JsonElement)
	 */
	public @Nullable JsonElement add(@NotNull String key, float value) {
		return this.add(key, new JsonPrimitive(value));
	}
	
	/**
	 * Adds the given double with the given key to this json object.<br>
	 * The double will be converted to a json primitive.<br>
	 * @param key The key to add
	 * @param value The double value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, JsonElement)
	 */
	public @Nullable JsonElement add(@NotNull String key, double value) {
		return this.add(key, new JsonPrimitive(value));
	}
	
	/**
	 * Adds all elements from the given json object to this json object.<br>
	 * @param object The json object of elements to add
	 * @throws NullPointerException If the given json object is null
	 */
	public void addAll(@NotNull JsonObject object) {
		this.elements.putAll(Objects.requireNonNull(object, "Json object must not be null").elements);
	}
	
	/**
	 * Adds all elements from the given map to this json object.<br>
	 * @param elements The map of elements to add
	 * @throws NullPointerException If the given elements are null
	 */
	public void addAll(@NotNull Map<String, ? extends JsonElement> elements) {
		this.elements.putAll(Objects.requireNonNull(elements, "Json elements must not be null"));
	}
	//endregion
	
	//region Remove operations
	
	/**
	 * Removes the element with the given key from this json object.<br>
	 * @param key The key to remove
	 * @return The element associated with the key, or null if the key was not present
	 */
	public @Nullable JsonElement remove(@Nullable String key) {
		return this.elements.remove(key);
	}
	
	/**
	 * Removes all element pairs from this json object.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	//endregion
	
	//region Replace operations
	
	/**
	 * Replaces the element with the given key in this json object with the new given element.<br>
	 * If the given element is null, it will be replaced with json null.<br>
	 * @param key The key to replace
	 * @param newElement The new element to replace with
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable JsonElement replace(@NotNull String key, @Nullable JsonElement newElement) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.replace(key, newElement == null ? JsonNull.INSTANCE : newElement);
	}
	
	/**
	 * Replaces the given element with the given key in this json object with the new given element.<br>
	 * @param key The key to replace
	 * @param oldElement The old element to replace
	 * @param newElement The new element to replace with
	 * @return True if the element was replaced, false otherwise
	 * @throws NullPointerException If the given key or old element is null
	 */
	public boolean replace(@NotNull String key, @NotNull JsonElement oldElement, @Nullable JsonElement newElement) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(oldElement, "Old value must not be null");
		return this.elements.replace(key, oldElement, newElement == null ? JsonNull.INSTANCE : newElement);
	}
	//endregion
	
	//region Get operations
	
	/**
	 * Gets the element with the given key from this json object.<br>
	 * @param key The key to get
	 * @return The element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable JsonElement get(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.get(key);
	}
	
	/**
	 * Gets the element with the given key from this json object as a json object.<br>
	 * @param key The key to get
	 * @return The element associated with the key as a json object
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchJsonElementException If no element was found for the given key
	 * @throws JsonTypeException If the element is not a json object
	 * @see #get(String)
	 */
	public @NotNull JsonObject getAsJsonObject(@NotNull String key) {
		JsonElement json = this.get(key);
		if (json == null) {
			throw new NoSuchJsonElementException("Expected json object for key '" + key + "', but found none");
		}
		if (json instanceof JsonObject object) {
			return object;
		}
		return json.getAsJsonObject(); // throws JsonTypeException
	}
	
	/**
	 * Gets the element with the given key from this json object as a json array.<br>
	 * @param key The key to get
	 * @return The element associated with the key as a json array
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchJsonElementException If no element was found for the given key
	 * @throws JsonTypeException If the element is not a json array
	 * @see #get(String)
	 */
	public @NotNull JsonArray getAsJsonArray(@NotNull String key) {
		JsonElement json = this.get(key);
		if (json == null) {
			throw new NoSuchJsonElementException("Expected json array for key '" + key + "', but found none");
		}
		if (json instanceof JsonArray array) {
			return array;
		}
		return json.getAsJsonArray(); // throws JsonTypeException
	}
	
	/**
	 * Gets the element with the given key from this json object as a json primitive.<br>
	 * @param key The key to get
	 * @return The element associated with the key as a json primitive
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchJsonElementException If no element was found for the given key
	 * @throws JsonTypeException If the element is not a json primitive
	 * @see #get(String)
	 */
	public @NotNull JsonPrimitive getJsonPrimitive(@NotNull String key) {
		JsonElement json = this.get(key);
		if (json == null) {
			throw new NoSuchJsonElementException("Expected json primitive for key '" + key + "', but found none");
		}
		if (json instanceof JsonPrimitive primitive) {
			return primitive;
		}
		return json.getAsJsonPrimitive(); // throws JsonTypeException
	}
	
	/**
	 * Gets the element with the given key from this json object as a string.<br>
	 * The element will be converted to a json primitive and then to a string.<br>
	 * @param key The key to get
	 * @return The element associated with the key as a string
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchJsonElementException If no element was found for the given key
	 * @throws JsonTypeException If the element is not a string
	 * @see #getAsJsonPrimitive()
	 */
	public @NotNull String getAsString(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsString();
	}
	
	/**
	 * Gets the element with the given key from this json object as a boolean.<br>
	 * The element will be converted to a json primitive and then to a boolean.<br>
	 * @param key The key to get
	 * @return The element associated with the key as a boolean
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchJsonElementException If no element was found for the given key
	 * @throws JsonTypeException If the element is not a boolean
	 * @see #getAsJsonPrimitive()
	 */
	public boolean getAsBoolean(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsBoolean();
	}
	
	/**
	 * Gets the element with the given key from this json object as a number.<br>
	 * The element will be converted to a json primitive and then to a number.<br>
	 * @param key The key to get
	 * @return The element associated with the key as a number
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchJsonElementException If no element was found for the given key
	 * @throws JsonTypeException If the element is not a number
	 * @see #getAsJsonPrimitive()
	 */
	public @NotNull Number getAsNumber(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsNumber();
	}
	
	/**
	 * Gets the element with the given key from this json object as a byte.<br>
	 * The element will be converted to a json primitive and then to a byte.<br>
	 * @param key The key to get
	 * @return The element associated with the key as a byte
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchJsonElementException If no element was found for the given key
	 * @throws JsonTypeException If the element is not a byte
	 * @see #getAsJsonPrimitive()
	 */
	public byte getAsByte(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsByte();
	}
	
	/**
	 * Gets the element with the given key from this json object as a short.<br>
	 * The element will be converted to a json primitive and then to a short.<br>
	 * @param key The key to get
	 * @return The element associated with the key as a short
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchJsonElementException If no element was found for the given key
	 * @throws JsonTypeException If the element is not a short
	 * @see #getAsJsonPrimitive()
	 */
	public short getAsShort(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsShort();
	}
	
	/**
	 * Gets the element with the given key from this json object as an integer.<br>
	 * The element will be converted to a json primitive and then to an integer.<br>
	 * @param key The key to get
	 * @return The element associated with the key as an integer
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchJsonElementException If no element was found for the given key
	 * @throws JsonTypeException If the element is not an integer
	 * @see #getAsJsonPrimitive()
	 */
	public int getAsInteger(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsInteger();
	}
	
	/**
	 * Gets the element with the given key from this json object as a long.<br>
	 * The element will be converted to a json primitive and then to a long.<br>
	 * @param key The key to get
	 * @return The element associated with the key as a long
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchJsonElementException If no element was found for the given key
	 * @throws JsonTypeException If the element is not a long
	 * @see #getAsJsonPrimitive()
	 */
	public long getAsLong(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsLong();
	}
	
	/**
	 * Gets the element with the given key from this json object as a float.<br>
	 * The element will be converted to a json primitive and then to a float.<br>
	 * @param key The key to get
	 * @return The element associated with the key as a float
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchJsonElementException If no element was found for the given key
	 * @throws JsonTypeException If the element is not a float
	 * @see #getAsJsonPrimitive()
	 */
	public float getAsFloat(@NotNull String key) {
		return this.getJsonPrimitive(key).getAsFloat();
	}
	
	/**
	 * Gets the element with the given key from this json object as a double.<br>
	 * The element will be converted to a json primitive and then to a double.<br>
	 * @param key The key to get
	 * @return The element associated with the key as a double
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchJsonElementException If no element was found for the given key
	 * @throws JsonTypeException If the element is not a double
	 * @see #getAsJsonPrimitive()
	 */
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
		if (!entries.isEmpty()) {
			boolean shouldSimplify = config.simplifyObjects() && config.maxObjectSimplificationSize() >= entries.size();
			if (shouldSimplify) {
				builder.append(" ");
			}
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
			if (shouldSimplify) {
				builder.append(" ");
			}
		}
		return builder.append("}").toString();
	}
	//endregion
}
