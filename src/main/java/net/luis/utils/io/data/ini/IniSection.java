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

package net.luis.utils.io.data.ini;

import com.google.common.collect.Maps;
import net.luis.utils.io.data.ini.exception.IniTypeException;
import net.luis.utils.io.data.ini.exception.NoSuchIniElementException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Represents an ini section with a name and key-value pairs.<br>
 * A section is denoted by [section_name] in ini files.<br>
 *
 * @author Luis-St
 */
public class IniSection implements IniElement, Iterable<Map.Entry<String, IniElement>> {
	
	/**
	 * The name of this section.<br>
	 */
	private final String name;
	
	/**
	 * The internal map of elements.<br>
	 * The order of the elements is preserved.<br>
	 */
	private final Map<String, IniElement> elements = Maps.newLinkedHashMap();
	
	/**
	 * Constructs a new ini section with the given name.<br>
	 *
	 * @param name The name of the section
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is blank
	 */
	public IniSection(@NonNull String name) {
		Objects.requireNonNull(name, "Section name must not be null");
		if (name.isBlank()) {
			throw new IllegalArgumentException("Section name must not be blank");
		}
		this.name = name;
	}
	
	/**
	 * Returns the name of this section.<br>
	 * @return The section name
	 */
	public @NonNull String getName() {
		return this.name;
	}
	
	/**
	 * Returns the number of elements in this section.<br>
	 * @return The size of this section
	 */
	public int size() {
		return this.elements.size();
	}
	
	/**
	 * Checks if this section is empty.<br>
	 * @return True if this section is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * Checks if this section contains the given key.<br>
	 *
	 * @param key The key to check
	 * @return True if this section contains the given key, false otherwise
	 */
	public boolean containsKey(@Nullable String key) {
		return this.elements.containsKey(key);
	}
	
	/**
	 * Checks if this section contains the given element.<br>
	 *
	 * @param element The element to check
	 * @return True if this section contains the given element, false otherwise
	 */
	public boolean containsValue(@Nullable IniElement element) {
		return this.elements.containsValue(element);
	}
	
	/**
	 * Returns the set of keys in this section.<br>
	 * @return The keys of this section
	 */
	public @NonNull Set<String> keySet() {
		return this.elements.keySet();
	}
	
	/**
	 * Returns the collection of values in this section.<br>
	 * @return The values of this section
	 */
	public @NonNull @Unmodifiable Collection<IniElement> elements() {
		return Collections.unmodifiableCollection(this.elements.values());
	}
	
	/**
	 * Returns the set of entries in this section.<br>
	 * @return The entries of this section
	 */
	public @NonNull Set<Map.Entry<String, IniElement>> entrySet() {
		return this.elements.entrySet();
	}
	
	@Override
	public @NonNull Iterator<Map.Entry<String, IniElement>> iterator() {
		return this.elements.entrySet().iterator();
	}
	
	/**
	 * Iterates over the entries of this section and applies the given action to each entry.<br>
	 *
	 * @param action The action to apply to each entry
	 * @throws NullPointerException If the given action is null
	 */
	public void forEach(@NonNull BiConsumer<? super String, ? super IniElement> action) {
		this.elements.forEach(Objects.requireNonNull(action, "Action must not be null"));
	}
	
	/**
	 * Adds the given element with the given key to this section.<br>
	 * If the element is null, it will be replaced with ini null.<br>
	 * If the key is already present, the element will be replaced.<br>
	 *
	 * @param key The key to add
	 * @param element The element to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable IniElement add(@NonNull String key, @Nullable IniElement element) {
		Objects.requireNonNull(key, "Section key must not be null");
		return this.elements.put(key, element == null ? IniNull.INSTANCE : element);
	}
	
	/**
	 * Adds the given string with the given key to this section.<br>
	 * If the string is null, it will be replaced with ini null.<br>
	 *
	 * @param key The key to add
	 * @param value The string value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable IniElement add(@NonNull String key, @Nullable String value) {
		return this.add(key, value == null ? null : new IniValue(value));
	}
	
	/**
	 * Adds the given boolean with the given key to this section.<br>
	 *
	 * @param key The key to add
	 * @param value The boolean value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable IniElement add(@NonNull String key, boolean value) {
		return this.add(key, new IniValue(value));
	}
	
	/**
	 * Adds the given number with the given key to this section.<br>
	 * If the number is null, it will be replaced with ini null.<br>
	 *
	 * @param key The key to add
	 * @param value The number value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable IniElement add(@NonNull String key, @Nullable Number value) {
		return this.add(key, value == null ? null : new IniValue(value));
	}
	
	/**
	 * Adds all elements from the given section to this section.<br>
	 *
	 * @param section The section to add elements from
	 * @throws NullPointerException If the given section is null
	 */
	public void addAll(@NonNull IniSection section) {
		this.elements.putAll(Objects.requireNonNull(section, "Section must not be null").elements);
	}
	
	/**
	 * Adds all elements from the given map to this section.<br>
	 *
	 * @param elements The map of elements to add
	 * @throws NullPointerException If the given elements are null
	 */
	public void addAll(@NonNull Map<String, ? extends IniElement> elements) {
		this.elements.putAll(Objects.requireNonNull(elements, "Elements must not be null"));
	}
	
	/**
	 * Removes the element with the given key from this section.<br>
	 *
	 * @param key The key to remove
	 * @return The element associated with the key, or null if the key was not present
	 */
	public @Nullable IniElement remove(@Nullable String key) {
		return this.elements.remove(key);
	}
	
	/**
	 * Removes all elements from this section.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	
	/**
	 * Replaces the element with the given key in this section with the new given element.<br>
	 * If the given element is null, it will be replaced with ini null.<br>
	 *
	 * @param key The key to replace
	 * @param newElement The new element to replace with
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable IniElement replace(@NonNull String key, @Nullable IniElement newElement) {
		Objects.requireNonNull(key, "Section key must not be null");
		return this.elements.replace(key, newElement == null ? IniNull.INSTANCE : newElement);
	}
	
	/**
	 * Gets the element with the given key from this section.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable IniElement get(@NonNull String key) {
		Objects.requireNonNull(key, "Section key must not be null");
		return this.elements.get(key);
	}
	
	/**
	 * Gets the element with the given key from this section as an ini value.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as an ini value
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchIniElementException If no element was found for the given key
	 * @throws IniTypeException If the element is not an ini value
	 */
	public @NonNull IniValue getIniValue(@NonNull String key) {
		Objects.requireNonNull(key, "Section key must not be null");
		IniElement element = this.get(key);
		
		if (element == null) {
			throw new NoSuchIniElementException("Expected ini value for key '" + key + "', but found none");
		}
		if (element instanceof IniValue value) {
			return value;
		}
		
		return element.getAsIniValue();
	}
	
	/**
	 * Gets the element with the given key from this section as a string.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a string
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchIniElementException If no element was found for the given key
	 * @throws IniTypeException If the element is not a string
	 */
	public @NonNull String getAsString(@NonNull String key) {
		return this.getIniValue(key).getAsString();
	}
	
	/**
	 * Gets the element with the given key from this section as a boolean.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a boolean
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchIniElementException If no element was found for the given key
	 * @throws IniTypeException If the element is not a boolean
	 */
	public boolean getAsBoolean(@NonNull String key) {
		return this.getIniValue(key).getAsBoolean();
	}
	
	/**
	 * Gets the element with the given key from this section as a number.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a number
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchIniElementException If no element was found for the given key
	 * @throws IniTypeException If the element is not a number
	 */
	public @NonNull Number getAsNumber(@NonNull String key) {
		return this.getIniValue(key).getAsNumber();
	}
	
	/**
	 * Gets the element with the given key from this section as a byte.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a byte
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchIniElementException If no element was found for the given key
	 * @throws IniTypeException If the element is not a number
	 */
	public byte getAsByte(@NonNull String key) {
		return this.getIniValue(key).getAsByte();
	}
	
	/**
	 * Gets the element with the given key from this section as a short.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a short
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchIniElementException If no element was found for the given key
	 * @throws IniTypeException If the element is not a number
	 */
	public short getAsShort(@NonNull String key) {
		return this.getIniValue(key).getAsShort();
	}
	
	/**
	 * Gets the element with the given key from this section as an integer.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as an integer
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchIniElementException If no element was found for the given key
	 * @throws IniTypeException If the element is not a number
	 */
	public int getAsInteger(@NonNull String key) {
		return this.getIniValue(key).getAsInteger();
	}
	
	/**
	 * Gets the element with the given key from this section as a long.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a long
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchIniElementException If no element was found for the given key
	 * @throws IniTypeException If the element is not a number
	 */
	public long getAsLong(@NonNull String key) {
		return this.getIniValue(key).getAsLong();
	}
	
	/**
	 * Gets the element with the given key from this section as a float.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a float
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchIniElementException If no element was found for the given key
	 * @throws IniTypeException If the element is not a number
	 */
	public float getAsFloat(@NonNull String key) {
		return this.getIniValue(key).getAsFloat();
	}
	
	/**
	 * Gets the element with the given key from this section as a double.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a double
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchIniElementException If no element was found for the given key
	 * @throws IniTypeException If the element is not a number
	 */
	public double getAsDouble(@NonNull String key) {
		return this.getIniValue(key).getAsDouble();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IniSection that)) return false;
		
		return this.name.equals(that.name) && this.elements.equals(that.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.elements);
	}
	
	@Override
	public String toString() {
		return this.toString(IniConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull IniConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		StringBuilder builder = new StringBuilder();
		String alignment = " ".repeat(config.alignment());
		
		builder.append("[").append(this.name).append("]");
		
		for (Map.Entry<String, IniElement> entry : this.elements.entrySet()) {
			IniElement value = entry.getValue();
			
			if (value.isIniNull() && config.nullStyle() == IniConfig.NullStyle.SKIP) {
				continue;
			}
			
			builder.append(System.lineSeparator());
			builder.append(config.indent());
			builder.append(entry.getKey());
			builder.append(alignment).append(config.separator()).append(alignment);
			builder.append(value.toString(config));
		}
		return builder.toString();
	}
	//endregion
}
