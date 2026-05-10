/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.data.toon;

import com.google.common.collect.Maps;
import net.luis.utils.io.data.toon.exception.NoSuchToonElementException;
import net.luis.utils.io.data.toon.exception.ToonTypeException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Represents a toon object.<br>
 * A toon object is an ordered set of key/value pairs.<br>
 * Keys are strings and values can be any toon element.<br>
 *
 * @author Luis-St
 */
public class ToonObject implements ToonElement, Iterable<Map.Entry<String, ToonElement>> {
	
	/**
	 * The internal map of elements.<br>
	 * The order of the elements is preserved.<br>
	 */
	private final Map<String, ToonElement> elements = Maps.newLinkedHashMap();
	
	/**
	 * Constructs an empty toon object.<br>
	 */
	public ToonObject() {}
	
	/**
	 * Constructs a toon object with the given elements.<br>
	 *
	 * @param elements The map of elements to add
	 * @throws NullPointerException If the given elements are null
	 */
	public ToonObject(@NonNull Map<String, ? extends ToonElement> elements) {
		this.elements.putAll(Objects.requireNonNull(elements, "Elements must not be null"));
	}
	
	/**
	 * Returns the number of elements in this object.<br>
	 * @return The size of this object
	 */
	public int size() {
		return this.elements.size();
	}
	
	/**
	 * Checks if this object is empty.<br>
	 * @return True if this object is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * Checks if this object contains the given key.<br>
	 *
	 * @param key The key to check
	 * @return True if this object contains the given key, false otherwise
	 */
	public boolean containsKey(@Nullable String key) {
		return this.elements.containsKey(key);
	}
	
	/**
	 * Checks if this object contains the given element.<br>
	 *
	 * @param element The element to check
	 * @return True if this object contains the given element, false otherwise
	 */
	public boolean containsValue(@Nullable ToonElement element) {
		return this.elements.containsValue(element);
	}
	
	/**
	 * Returns the set of keys in this object.<br>
	 * @return The keys of this object
	 */
	public @NonNull Set<String> keySet() {
		return this.elements.keySet();
	}
	
	/**
	 * Returns the collection of values in this object.<br>
	 * @return The values of this object
	 */
	public @NonNull @Unmodifiable Collection<ToonElement> elements() {
		return Collections.unmodifiableCollection(this.elements.values());
	}
	
	/**
	 * Returns the set of entries in this object.<br>
	 * @return The entries of this object
	 */
	public @NonNull Set<Map.Entry<String, ToonElement>> entrySet() {
		return this.elements.entrySet();
	}
	
	@Override
	public @NonNull Iterator<Map.Entry<String, ToonElement>> iterator() {
		return this.elements.entrySet().iterator();
	}
	
	/**
	 * Iterates over the entries of this object and applies the given action to each entry.<br>
	 *
	 * @param action The action to apply to each entry
	 * @throws NullPointerException If the given action is null
	 */
	public void forEach(@NonNull BiConsumer<? super String, ? super ToonElement> action) {
		this.elements.forEach(Objects.requireNonNull(action, "Action must not be null"));
	}
	
	/**
	 * Adds the given element with the given key to this object.<br>
	 * If the element is null, it will be replaced with toon null.<br>
	 * If the key is already present, the element will be replaced.<br>
	 *
	 * @param key The key to add
	 * @param element The element to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable ToonElement add(@NonNull String key, @Nullable ToonElement element) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.put(key, element == null ? ToonNull.INSTANCE : element);
	}
	
	/**
	 * Adds the given string with the given key to this object.<br>
	 * If the string is null, it will be replaced with toon null.<br>
	 *
	 * @param key The key to add
	 * @param value The string value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable ToonElement add(@NonNull String key, @Nullable String value) {
		return this.add(key, value == null ? null : new ToonValue(value));
	}
	
	/**
	 * Adds the given boolean with the given key to this object.<br>
	 *
	 * @param key The key to add
	 * @param value The boolean value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable ToonElement add(@NonNull String key, boolean value) {
		return this.add(key, new ToonValue(value));
	}
	
	/**
	 * Adds the given number with the given key to this object.<br>
	 * If the number is null, it will be replaced with toon null.<br>
	 *
	 * @param key The key to add
	 * @param value The number value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable ToonElement add(@NonNull String key, @Nullable Number value) {
		return this.add(key, value == null ? null : new ToonValue(value));
	}
	
	/**
	 * Adds all elements from the given object to this object.<br>
	 *
	 * @param object The object of elements to add
	 * @throws NullPointerException If the given object is null
	 */
	public void addAll(@NonNull ToonObject object) {
		this.elements.putAll(Objects.requireNonNull(object, "Object must not be null").elements);
	}
	
	/**
	 * Adds all elements from the given map to this object.<br>
	 *
	 * @param elements The map of elements to add
	 * @throws NullPointerException If the given elements are null
	 */
	public void addAll(@NonNull Map<String, ? extends ToonElement> elements) {
		this.elements.putAll(Objects.requireNonNull(elements, "Elements must not be null"));
	}
	
	/**
	 * Gets the element with the given key from this object.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable ToonElement get(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.get(key);
	}
	
	/**
	 * Gets the element with the given key as a toon value.<br>
	 *
	 * @param key The key to get
	 * @return The element as a toon value
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchToonElementException If no element was found for the given key
	 * @throws ToonTypeException If the element is not a toon value
	 */
	public @NonNull ToonValue getToonValue(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		ToonElement element = this.get(key);
		
		if (element == null) {
			throw new NoSuchToonElementException("Expected toon value for key '" + key + "', but found none");
		}
		return element.getAsToonValue();
	}
	
	/**
	 * Gets the element with the given key as a toon array.<br>
	 *
	 * @param key The key to get
	 * @return The element as a toon array
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchToonElementException If no element was found for the given key
	 * @throws ToonTypeException If the element is not a toon array
	 */
	public @NonNull ToonArray getToonArray(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		ToonElement element = this.get(key);
		
		if (element == null) {
			throw new NoSuchToonElementException("Expected toon array for key '" + key + "', but found none");
		}
		return element.getAsToonArray();
	}
	
	/**
	 * Gets the element with the given key as a toon object.<br>
	 *
	 * @param key The key to get
	 * @return The element as a toon object
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchToonElementException If no element was found for the given key
	 * @throws ToonTypeException If the element is not a toon object
	 */
	public @NonNull ToonObject getToonObject(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		ToonElement element = this.get(key);
		
		if (element == null) {
			throw new NoSuchToonElementException("Expected toon object for key '" + key + "', but found none");
		}
		return element.getAsToonObject();
	}
	
	/**
	 * Gets the element with the given key as a string.<br>
	 *
	 * @param key The key to get
	 * @return The element as a string
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchToonElementException If no element was found for the given key
	 * @throws ToonTypeException If the element is not a string
	 */
	public @NonNull String getAsString(@NonNull String key) {
		return this.getToonValue(key).getAsString();
	}
	
	/**
	 * Gets the element with the given key as a boolean.<br>
	 *
	 * @param key The key to get
	 * @return The element as a boolean
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchToonElementException If no element was found for the given key
	 * @throws ToonTypeException If the element is not a boolean
	 */
	public boolean getAsBoolean(@NonNull String key) {
		return this.getToonValue(key).getAsBoolean();
	}
	
	/**
	 * Gets the element with the given key as a number.<br>
	 *
	 * @param key The key to get
	 * @return The element as a number
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchToonElementException If no element was found for the given key
	 * @throws ToonTypeException If the element is not a number
	 */
	public @NonNull Number getAsNumber(@NonNull String key) {
		return this.getToonValue(key).getAsNumber();
	}
	
	/**
	 * Gets the element with the given key as a byte.<br>
	 *
	 * @param key The key to get
	 * @return The element as a byte
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchToonElementException If no element was found for the given key
	 * @throws ToonTypeException If the element is not a number
	 */
	public byte getAsByte(@NonNull String key) {
		return this.getToonValue(key).getAsByte();
	}
	
	/**
	 * Gets the element with the given key as a short.<br>
	 *
	 * @param key The key to get
	 * @return The element as a short
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchToonElementException If no element was found for the given key
	 * @throws ToonTypeException If the element is not a number
	 */
	public short getAsShort(@NonNull String key) {
		return this.getToonValue(key).getAsShort();
	}
	
	/**
	 * Gets the element with the given key as an integer.<br>
	 *
	 * @param key The key to get
	 * @return The element as an integer
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchToonElementException If no element was found for the given key
	 * @throws ToonTypeException If the element is not a number
	 */
	public int getAsInteger(@NonNull String key) {
		return this.getToonValue(key).getAsInteger();
	}
	
	/**
	 * Gets the element with the given key as a long.<br>
	 *
	 * @param key The key to get
	 * @return The element as a long
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchToonElementException If no element was found for the given key
	 * @throws ToonTypeException If the element is not a number
	 */
	public long getAsLong(@NonNull String key) {
		return this.getToonValue(key).getAsLong();
	}
	
	/**
	 * Gets the element with the given key as a float.<br>
	 *
	 * @param key The key to get
	 * @return The element as a float
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchToonElementException If no element was found for the given key
	 * @throws ToonTypeException If the element is not a number
	 */
	public float getAsFloat(@NonNull String key) {
		return this.getToonValue(key).getAsFloat();
	}
	
	/**
	 * Gets the element with the given key as a double.<br>
	 *
	 * @param key The key to get
	 * @return The element as a double
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchToonElementException If no element was found for the given key
	 * @throws ToonTypeException If the element is not a number
	 */
	public double getAsDouble(@NonNull String key) {
		return this.getToonValue(key).getAsDouble();
	}
	
	/**
	 * Removes the element with the given key from this object.<br>
	 *
	 * @param key The key to remove
	 * @return The element associated with the key, or null if the key was not present
	 */
	public @Nullable ToonElement remove(@Nullable String key) {
		return this.elements.remove(key);
	}
	
	/**
	 * Removes all elements from this object.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	
	/**
	 * Replaces the element with the given key with the new given element.<br>
	 * If the given element is null, it will be replaced with toon null.<br>
	 *
	 * @param key The key to replace
	 * @param newElement The new element to replace with
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable ToonElement replace(@NonNull String key, @Nullable ToonElement newElement) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.replace(key, newElement == null ? ToonNull.INSTANCE : newElement);
	}
	
	/**
	 * Replaces the given element with the given key with the new given element.<br>
	 *
	 * @param key The key to replace
	 * @param oldElement The old element to replace
	 * @param newElement The new element to replace with
	 * @return True if the element was replaced, false otherwise
	 * @throws NullPointerException If the given key or old element is null
	 */
	public boolean replace(@NonNull String key, @NonNull ToonElement oldElement, @Nullable ToonElement newElement) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(oldElement, "Old element must not be null");
		
		return this.elements.replace(key, oldElement, newElement == null ? ToonNull.INSTANCE : newElement);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ToonObject that)) return false;
		
		return this.elements.equals(that.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.elements);
	}
	
	@Override
	public String toString() {
		return this.toString(ToonConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull ToonConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		return this.toBlockString(config, 0);
	}
	
	/**
	 * Formats this object as a block with the given indentation depth.<br>
	 *
	 * @param config The toon config
	 * @param depth The current indentation depth
	 * @return The formatted object string
	 * @throws NullPointerException If the given config is null
	 */
	@NonNull String toBlockString(@NonNull ToonConfig config, int depth) {
		Objects.requireNonNull(config, "Config must not be null");
		if (this.elements.isEmpty()) {
			return "";
		}
		
		String indentStr = " ".repeat(config.indent() * depth);
		StringBuilder builder = new StringBuilder();
		
		boolean first = true;
		for (Map.Entry<String, ToonElement> entry : this.elements.entrySet()) {
			if (!first) {
				builder.append("\n");
			}
			first = false;
			
			String key = ToonHelper.formatKey(entry.getKey());
			ToonElement value = entry.getValue();
			
			if (value instanceof ToonObject nested) {
				builder.append(indentStr).append(key).append(":");
				if (!nested.isEmpty()) {
					builder.append("\n");
					builder.append(nested.toBlockString(config, depth + 1));
				}
			} else if (value instanceof ToonArray array) {
				builder.append(array.toStringWithKey(key, config, depth, indentStr));
			} else {
				builder.append(indentStr).append(key).append(": ").append(value.toString(config));
			}
		}
		return builder.toString();
	}
	//endregion
}
