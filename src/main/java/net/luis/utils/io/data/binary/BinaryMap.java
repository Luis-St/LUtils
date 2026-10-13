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

package net.luis.utils.io.data.binary;

import com.google.common.collect.Maps;
import net.luis.utils.io.data.binary.exception.BinaryTypeException;
import net.luis.utils.io.data.binary.exception.NoSuchBinaryElementException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Represents a binary map of key-value pairs.<br>
 * The entries of the map are stored in the order they were added.<br>
 * <p>
 *     In contrast to a {@link BinaryStruct struct}, the keys of a map are written to the output.<br>
 *     A map is used for dynamic data structures where the keys are not known in advance.
 * </p>
 *
 * @author Luis-St
 */
public class BinaryMap implements BinaryElement {
	
	/**
	 * The internal map of entries of this binary map.<br>
	 */
	private final Map<String, BinaryElement> elements = Maps.newLinkedHashMap();
	
	/**
	 * Constructs an empty binary map.<br>
	 */
	public BinaryMap() {}
	
	/**
	 * Constructs a binary map with the given entries.<br>
	 *
	 * @param elements The entries to add to the map
	 * @throws NullPointerException If the entries are null
	 */
	public BinaryMap(@NonNull Map<String, ? extends BinaryElement> elements) {
		this.elements.putAll(Objects.requireNonNull(elements, "Elements must not be null"));
	}
	
	/**
	 * Returns the element which is associated with the given key.<br>
	 *
	 * @param key The key of the element
	 * @return The element which is associated with the key
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 */
	private @NonNull BinaryElement getRequired(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		BinaryElement element = this.elements.get(key);
		if (element == null) {
			throw new NoSuchBinaryElementException("Expected a binary element for key '" + key + "', but found none");
		}
		return element;
	}
	
	@Override
	public @NonNull BinaryType getType() {
		return BinaryType.MAP;
	}
	
	/**
	 * Returns the number of entries in this map.<br>
	 * @return The size of this map
	 */
	public int size() {
		return this.elements.size();
	}
	
	/**
	 * Checks if this map contains no entries.<br>
	 * @return True if this map is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * Checks if this map contains an entry with the given key.<br>
	 *
	 * @param key The key to check
	 * @return True if this map contains the key, false otherwise
	 */
	public boolean containsKey(@Nullable String key) {
		return this.elements.containsKey(key);
	}
	
	/**
	 * Checks if this map contains an entry with the given element.<br>
	 *
	 * @param element The element to check
	 * @return True if this map contains the element, false otherwise
	 */
	public boolean containsValue(@Nullable BinaryElement element) {
		return this.elements.containsValue(element);
	}
	
	/**
	 * Returns the keys of this map.<br>
	 * @return An unmodifiable set of the keys
	 */
	public @NonNull @Unmodifiable Set<String> keySet() {
		return Set.copyOf(this.elements.keySet());
	}
	
	/**
	 * Returns the values of this map.<br>
	 * @return An unmodifiable collection of the values
	 */
	public @NonNull @Unmodifiable Collection<BinaryElement> values() {
		return List.copyOf(this.elements.values());
	}
	
	/**
	 * Returns the entries of this map.<br>
	 * @return An unmodifiable set of the entries
	 */
	public @NonNull @Unmodifiable Set<Map.Entry<String, BinaryElement>> entrySet() {
		return Map.copyOf(this.elements).entrySet();
	}
	
	/**
	 * Returns the entries of this map.<br>
	 * @return An unmodifiable map of the entries
	 */
	public @NonNull @Unmodifiable Map<String, BinaryElement> getElements() {
		return Map.copyOf(this.elements);
	}
	
	/**
	 * Performs the given action for each entry of this map.<br>
	 *
	 * @param action The action to perform
	 * @throws NullPointerException If the action is null
	 */
	public void forEach(@NonNull BiConsumer<? super String, ? super BinaryElement> action) {
		Objects.requireNonNull(action, "Action must not be null");
		
		this.elements.forEach(action);
	}
	
	/**
	 * Adds the given element with the given key to this map.<br>
	 * If the element is null, a {@link BinaryNull null value} is added instead.<br>
	 *
	 * @param key The key of the element
	 * @param element The element to add
	 * @return The element which was previously associated with the key or null if there was none
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable BinaryElement add(@NonNull String key, @Nullable BinaryElement element) {
		Objects.requireNonNull(key, "Key must not be null");
		
		return this.elements.put(key, element == null ? BinaryNull.INSTANCE : element);
	}
	
	/**
	 * Adds the given boolean value with the given key to this map.<br>
	 *
	 * @param key The key of the value
	 * @param value The value to add
	 * @return The element which was previously associated with the key or null if there was none
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable BinaryElement add(@NonNull String key, boolean value) {
		return this.add(key, new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given byte value with the given key to this map.<br>
	 *
	 * @param key The key of the value
	 * @param value The value to add
	 * @return The element which was previously associated with the key or null if there was none
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable BinaryElement add(@NonNull String key, byte value) {
		return this.add(key, new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given short value with the given key to this map.<br>
	 *
	 * @param key The key of the value
	 * @param value The value to add
	 * @return The element which was previously associated with the key or null if there was none
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable BinaryElement add(@NonNull String key, short value) {
		return this.add(key, new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given integer value with the given key to this map.<br>
	 *
	 * @param key The key of the value
	 * @param value The value to add
	 * @return The element which was previously associated with the key or null if there was none
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable BinaryElement add(@NonNull String key, int value) {
		return this.add(key, new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given long value with the given key to this map.<br>
	 *
	 * @param key The key of the value
	 * @param value The value to add
	 * @return The element which was previously associated with the key or null if there was none
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable BinaryElement add(@NonNull String key, long value) {
		return this.add(key, new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given float value with the given key to this map.<br>
	 *
	 * @param key The key of the value
	 * @param value The value to add
	 * @return The element which was previously associated with the key or null if there was none
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable BinaryElement add(@NonNull String key, float value) {
		return this.add(key, new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given double value with the given key to this map.<br>
	 *
	 * @param key The key of the value
	 * @param value The value to add
	 * @return The element which was previously associated with the key or null if there was none
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable BinaryElement add(@NonNull String key, double value) {
		return this.add(key, new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given number value with the given key to this map.<br>
	 * If the value is null, a {@link BinaryNull null value} is added instead.<br>
	 *
	 * @param key The key of the value
	 * @param value The value to add
	 * @return The element which was previously associated with the key or null if there was none
	 * @throws NullPointerException If the key is null
	 * @throws BinaryTypeException If the type of the number is not supported
	 */
	public @Nullable BinaryElement add(@NonNull String key, @Nullable Number value) {
		return this.add(key, value == null ? BinaryNull.INSTANCE : new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given string value with the given key to this map.<br>
	 * If the value is null, a {@link BinaryNull null value} is added instead.<br>
	 *
	 * @param key The key of the value
	 * @param value The value to add
	 * @return The element which was previously associated with the key or null if there was none
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable BinaryElement add(@NonNull String key, @Nullable String value) {
		return this.add(key, value == null ? BinaryNull.INSTANCE : new BinaryPrimitive(value));
	}
	
	/**
	 * Adds all entries of the given map to this map.<br>
	 *
	 * @param map The map with the entries to add
	 * @throws NullPointerException If the map is null
	 */
	public void addAll(@NonNull BinaryMap map) {
		Objects.requireNonNull(map, "Map must not be null");
		
		this.elements.putAll(map.elements);
	}
	
	/**
	 * Adds all given entries to this map.<br>
	 *
	 * @param elements The entries to add
	 * @throws NullPointerException If the entries are null
	 */
	public void addAll(@NonNull Map<String, ? extends BinaryElement> elements) {
		Objects.requireNonNull(elements, "Elements must not be null");
		
		elements.forEach(this::add);
	}
	
	/**
	 * Removes the entry with the given key from this map.<br>
	 *
	 * @param key The key of the entry to remove
	 * @return The removed element or null if the map did not contain the key
	 */
	public @Nullable BinaryElement remove(@Nullable String key) {
		return this.elements.remove(key);
	}
	
	/**
	 * Removes all entries from this map.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	
	/**
	 * Replaces the element which is associated with the given key with the given element.<br>
	 * If the element is null, a {@link BinaryNull null value} is set instead.<br>
	 *
	 * @param key The key of the element to replace
	 * @param element The element to set
	 * @return The element which was replaced or null if the map did not contain the key
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable BinaryElement replace(@NonNull String key, @Nullable BinaryElement element) {
		Objects.requireNonNull(key, "Key must not be null");
		
		return this.elements.replace(key, element == null ? BinaryNull.INSTANCE : element);
	}
	
	/**
	 * Replaces the given element which is associated with the given key with the given new element.<br>
	 * If the new element is null, a {@link BinaryNull null value} is set instead.<br>
	 *
	 * @param key The key of the element to replace
	 * @param oldElement The element which is expected to be associated with the key
	 * @param newElement The element to set
	 * @return True if the element was replaced, false otherwise
	 * @throws NullPointerException If the key or the old element is null
	 */
	public boolean replace(@NonNull String key, @NonNull BinaryElement oldElement, @Nullable BinaryElement newElement) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(oldElement, "Old element must not be null");
		
		return this.elements.replace(key, oldElement, newElement == null ? BinaryNull.INSTANCE : newElement);
	}
	
	/**
	 * Returns the element which is associated with the given key.<br>
	 *
	 * @param key The key of the element
	 * @return The element or null if the map does not contain the key
	 */
	public @Nullable BinaryElement get(@Nullable String key) {
		return this.elements.get(key);
	}
	
	/**
	 * Returns the element which is associated with the given key or the given default element.<br>
	 *
	 * @param key The key of the element
	 * @param defaultElement The element to return if the map does not contain the key
	 * @return The element or the default element if the map does not contain the key
	 */
	public @Nullable BinaryElement getOrDefault(@Nullable String key, @Nullable BinaryElement defaultElement) {
		return this.elements.getOrDefault(key, defaultElement);
	}
	
	/**
	 * Returns the element which is associated with the given key as a binary array.<br>
	 *
	 * @param key The key of the element
	 * @return The element as a binary array
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 * @throws BinaryTypeException If the element is not a binary array
	 * @see #get(String)
	 */
	public @NonNull BinaryArray getAsBinaryArray(@NonNull String key) {
		return this.getRequired(key).getAsBinaryArray();
	}
	
	/**
	 * Returns the element which is associated with the given key as a binary struct.<br>
	 *
	 * @param key The key of the element
	 * @return The element as a binary struct
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 * @throws BinaryTypeException If the element is not a binary struct
	 * @see #get(String)
	 */
	public @NonNull BinaryStruct getAsBinaryStruct(@NonNull String key) {
		return this.getRequired(key).getAsBinaryStruct();
	}
	
	/**
	 * Returns the element which is associated with the given key as a binary map.<br>
	 *
	 * @param key The key of the element
	 * @return The element as a binary map
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 * @throws BinaryTypeException If the element is not a binary map
	 * @see #get(String)
	 */
	public @NonNull BinaryMap getAsBinaryMap(@NonNull String key) {
		return this.getRequired(key).getAsBinaryMap();
	}
	
	/**
	 * Returns the element which is associated with the given key as a binary primitive.<br>
	 *
	 * @param key The key of the element
	 * @return The element as a binary primitive
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 * @throws BinaryTypeException If the element is not a binary primitive
	 * @see #get(String)
	 */
	public @NonNull BinaryPrimitive getAsBinaryPrimitive(@NonNull String key) {
		return this.getRequired(key).getAsBinaryPrimitive();
	}
	
	/**
	 * Returns the element which is associated with the given key as a boolean value.<br>
	 *
	 * @param key The key of the element
	 * @return The element as a boolean value
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 * @throws BinaryTypeException If the element is not a binary boolean
	 * @see #get(String)
	 */
	public boolean getAsBoolean(@NonNull String key) {
		return this.getRequired(key).getAsBoolean();
	}
	
	/**
	 * Returns the element which is associated with the given key as a number value.<br>
	 *
	 * @param key The key of the element
	 * @return The element as a number value
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 * @throws BinaryTypeException If the element is not a binary number
	 * @see #get(String)
	 */
	public @NonNull Number getAsNumber(@NonNull String key) {
		return this.getRequired(key).getAsNumber();
	}
	
	/**
	 * Returns the element which is associated with the given key as a byte value.<br>
	 *
	 * @param key The key of the element
	 * @return The element as a byte value
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 * @throws BinaryTypeException If the element is not a binary number
	 * @see #get(String)
	 */
	public byte getAsByte(@NonNull String key) {
		return this.getRequired(key).getAsByte();
	}
	
	/**
	 * Returns the element which is associated with the given key as a short value.<br>
	 *
	 * @param key The key of the element
	 * @return The element as a short value
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 * @throws BinaryTypeException If the element is not a binary number
	 * @see #get(String)
	 */
	public short getAsShort(@NonNull String key) {
		return this.getRequired(key).getAsShort();
	}
	
	/**
	 * Returns the element which is associated with the given key as an integer value.<br>
	 *
	 * @param key The key of the element
	 * @return The element as an integer value
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 * @throws BinaryTypeException If the element is not a binary number
	 * @see #get(String)
	 */
	public int getAsInteger(@NonNull String key) {
		return this.getRequired(key).getAsInteger();
	}
	
	/**
	 * Returns the element which is associated with the given key as a long value.<br>
	 *
	 * @param key The key of the element
	 * @return The element as a long value
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 * @throws BinaryTypeException If the element is not a binary number
	 * @see #get(String)
	 */
	public long getAsLong(@NonNull String key) {
		return this.getRequired(key).getAsLong();
	}
	
	/**
	 * Returns the element which is associated with the given key as a float value.<br>
	 *
	 * @param key The key of the element
	 * @return The element as a float value
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 * @throws BinaryTypeException If the element is not a binary number
	 * @see #get(String)
	 */
	public float getAsFloat(@NonNull String key) {
		return this.getRequired(key).getAsFloat();
	}
	
	/**
	 * Returns the element which is associated with the given key as a double value.<br>
	 *
	 * @param key The key of the element
	 * @return The element as a double value
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 * @throws BinaryTypeException If the element is not a binary number
	 * @see #get(String)
	 */
	public double getAsDouble(@NonNull String key) {
		return this.getRequired(key).getAsDouble();
	}
	
	/**
	 * Returns the element which is associated with the given key as a string value.<br>
	 *
	 * @param key The key of the element
	 * @return The element as a string value
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchBinaryElementException If the map does not contain the key
	 * @throws BinaryTypeException If the element is not a binary string
	 * @see #get(String)
	 */
	public @NonNull String getAsString(@NonNull String key) {
		return this.getRequired(key).getAsString();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BinaryMap that)) return false;
		
		return this.elements.equals(that.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.elements);
	}
	
	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		this.elements.forEach((key, element) -> joiner.add(key + ": " + element));
		return joiner.toString();
	}
	//endregion
}
