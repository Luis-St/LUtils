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

package net.luis.utils.io.data.toml;

import com.google.common.collect.Maps;
import net.luis.utils.io.data.toml.exception.NoSuchTomlElementException;
import net.luis.utils.io.data.toml.exception.TomlTypeException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.*;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Represents a TOML table.<br>
 * A TOML table is an ordered set of key/value pairs.<br>
 * Keys are strings and values can be any TOML element.<br>
 *
 * @author Luis-St
 */
public class TomlTable implements TomlElement, Iterable<Map.Entry<String, TomlElement>> {
	
	/**
	 * The internal map of elements.<br>
	 * The order of the elements is preserved.<br>
	 */
	private final Map<String, TomlElement> elements = Maps.newLinkedHashMap();
	
	/**
	 * Whether this table should be formatted as an inline table.<br>
	 */
	private boolean inline;
	
	/**
	 * Constructs an empty TOML table.<br>
	 */
	public TomlTable() {}
	
	/**
	 * Constructs a TOML table with the given elements.<br>
	 *
	 * @param elements The map of elements to add
	 * @throws NullPointerException If the given elements are null
	 */
	public TomlTable(@NonNull Map<String, ? extends TomlElement> elements) {
		this.elements.putAll(Objects.requireNonNull(elements, "Elements must not be null"));
	}
	
	//region Static helper methods
	
	/**
	 * Checks if the given key is a valid bare key in TOML.<br>
	 * Bare keys may only contain A-Za-z0-9_-.<br>
	 *
	 * @param key The key to check
	 * @return True if the key is a valid bare key, false otherwise
	 */
	private static boolean isBareKey(@NonNull String key) {
		if (key.isEmpty()) {
			return false;
		}
		
		for (int i = 0; i < key.length(); i++) {
			char c = key.charAt(i);
			if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '-')) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Formats a key for TOML output, quoting if necessary.<br>
	 *
	 * @param key The key to format
	 * @return The formatted key
	 */
	private static @NonNull String formatKey(@NonNull String key) {
		if (isBareKey(key)) {
			return key;
		}
		return "\"" + escapeString(key) + "\"";
	}
	
	/**
	 * Escapes special characters in a string for TOML output.<br>
	 *
	 * @param str The string to escape
	 * @return The escaped string
	 */
	private static @NonNull String escapeString(@NonNull String str) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
				case '"' -> result.append("\\\"");
				case '\\' -> result.append("\\\\");
				case '\b' -> result.append("\\b");
				case '\f' -> result.append("\\f");
				case '\n' -> result.append("\\n");
				case '\r' -> result.append("\\r");
				case '\t' -> result.append("\\t");
				default -> {
					if (c < 0x20) {
						result.append(String.format("\\u%04X", (int) c));
					} else {
						result.append(c);
					}
				}
			}
		}
		return result.toString();
	}
	//endregion
	
	/**
	 * Returns whether this table should be formatted as an inline table.<br>
	 * @return True if this is an inline table, false otherwise
	 */
	public boolean isInline() {
		return this.inline;
	}
	
	/**
	 * Sets whether this table should be formatted as an inline table.<br>
	 * @param inline True if this should be an inline table
	 */
	public void setInline(boolean inline) {
		this.inline = inline;
	}
	
	/**
	 * Returns the number of elements in this table.<br>
	 * @return The size of this table
	 */
	public int size() {
		return this.elements.size();
	}
	
	/**
	 * Checks if this table is empty.<br>
	 * @return True if this table is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * Checks if this table contains the given key.<br>
	 *
	 * @param key The key to check
	 * @return True if this table contains the given key, false otherwise
	 */
	public boolean containsKey(@Nullable String key) {
		return this.elements.containsKey(key);
	}
	
	/**
	 * Checks if this table contains the given element.<br>
	 *
	 * @param element The element to check
	 * @return True if this table contains the given element, false otherwise
	 */
	public boolean containsValue(@Nullable TomlElement element) {
		return this.elements.containsValue(element);
	}
	
	/**
	 * Returns the set of keys in this table.<br>
	 * @return The keys of this table
	 */
	public @NonNull Set<String> keySet() {
		return this.elements.keySet();
	}
	
	/**
	 * Returns the collection of values in this table.<br>
	 * @return The values of this table
	 */
	public @NonNull @Unmodifiable Collection<TomlElement> elements() {
		return Collections.unmodifiableCollection(this.elements.values());
	}
	
	/**
	 * Returns the set of entries in this table.<br>
	 * @return The entries of this table
	 */
	public @NonNull Set<Map.Entry<String, TomlElement>> entrySet() {
		return this.elements.entrySet();
	}
	
	@Override
	public @NonNull Iterator<Map.Entry<String, TomlElement>> iterator() {
		return this.elements.entrySet().iterator();
	}
	
	/**
	 * Iterates over the entries of this table and applies the given action to each entry.<br>
	 *
	 * @param action The action to apply to each entry
	 * @throws NullPointerException If the given action is null
	 */
	public void forEach(@NonNull BiConsumer<? super String, ? super TomlElement> action) {
		this.elements.forEach(Objects.requireNonNull(action, "Action must not be null"));
	}
	
	/**
	 * Adds the given element with the given key to this table.<br>
	 * If the element is null, it will be replaced with TOML null.<br>
	 * If the key is already present, the element will be replaced.<br>
	 *
	 * @param key The key to add
	 * @param element The element to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable TomlElement add(@NonNull String key, @Nullable TomlElement element) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.put(key, element == null ? TomlNull.INSTANCE : element);
	}
	
	/**
	 * Adds the given string with the given key to this table.<br>
	 * If the string is null, it will be replaced with TOML null.<br>
	 *
	 * @param key The key to add
	 * @param value The string value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable TomlElement add(@NonNull String key, @Nullable String value) {
		return this.add(key, value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Adds the given boolean with the given key to this table.<br>
	 *
	 * @param key The key to add
	 * @param value The boolean value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable TomlElement add(@NonNull String key, boolean value) {
		return this.add(key, new TomlValue(value));
	}
	
	/**
	 * Adds the given number with the given key to this table.<br>
	 * If the number is null, it will be replaced with TOML null.<br>
	 *
	 * @param key The key to add
	 * @param value The number value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable TomlElement add(@NonNull String key, @Nullable Number value) {
		return this.add(key, value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Adds the given local date with the given key to this table.<br>
	 * If the date is null, it will be replaced with TOML null.<br>
	 *
	 * @param key The key to add
	 * @param value The local date value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable TomlElement add(@NonNull String key, @Nullable LocalDate value) {
		return this.add(key, value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Adds the given local time with the given key to this table.<br>
	 * If the time is null, it will be replaced with TOML null.<br>
	 *
	 * @param key The key to add
	 * @param value The local time value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable TomlElement add(@NonNull String key, @Nullable LocalTime value) {
		return this.add(key, value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Adds the given local date-time with the given key to this table.<br>
	 * If the date-time is null, it will be replaced with TOML null.<br>
	 *
	 * @param key The key to add
	 * @param value The local date-time value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable TomlElement add(@NonNull String key, @Nullable LocalDateTime value) {
		return this.add(key, value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Adds the given offset date-time with the given key to this table.<br>
	 * If the date-time is null, it will be replaced with TOML null.<br>
	 *
	 * @param key The key to add
	 * @param value The offset date-time value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable TomlElement add(@NonNull String key, @Nullable OffsetDateTime value) {
		return this.add(key, value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Adds all elements from the given table to this table.<br>
	 *
	 * @param table The table of elements to add
	 * @throws NullPointerException If the given table is null
	 */
	public void addAll(@NonNull TomlTable table) {
		this.elements.putAll(Objects.requireNonNull(table, "Table must not be null").elements);
	}
	
	/**
	 * Adds all elements from the given map to this table.<br>
	 *
	 * @param elements The map of elements to add
	 * @throws NullPointerException If the given elements are null
	 */
	public void addAll(@NonNull Map<String, ? extends TomlElement> elements) {
		this.elements.putAll(Objects.requireNonNull(elements, "Elements must not be null"));
	}
	
	/**
	 * Adds the given element using a dotted key (e.g., "a.b.c").<br>
	 * Creates intermediate tables as needed.<br>
	 *
	 * @param dottedKey The dotted key path
	 * @param element The element to add
	 * @throws NullPointerException If the given key is null
	 */
	public void addDotted(@NonNull String dottedKey, @Nullable TomlElement element) {
		Objects.requireNonNull(dottedKey, "Dotted key must not be null");
		String[] parts = dottedKey.split("\\.");
		
		if (parts.length == 1) {
			this.add(dottedKey, element);
			return;
		}
		
		TomlTable current = this;
		for (int i = 0; i < parts.length - 1; i++) {
			TomlElement existing = current.get(parts[i]);
			if (existing instanceof TomlTable table) {
				current = table;
			} else {
				TomlTable newTable = new TomlTable();
				current.add(parts[i], newTable);
				current = newTable;
			}
		}
		current.add(parts[parts.length - 1], element);
	}
	
	/**
	 * Gets the element at the given dotted key path (e.g., "a.b.c").<br>
	 *
	 * @param dottedKey The dotted key path
	 * @return The element at the path, or null if not found
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable TomlElement getDotted(@NonNull String dottedKey) {
		Objects.requireNonNull(dottedKey, "Dotted key must not be null");
		String[] parts = dottedKey.split("\\.");
		
		if (parts.length == 1) {
			return this.get(dottedKey);
		}
		
		TomlTable current = this;
		for (int i = 0; i < parts.length - 1; i++) {
			TomlElement element = current.get(parts[i]);
			if (element instanceof TomlTable table) {
				current = table;
			} else {
				return null;
			}
		}
		return current.get(parts[parts.length - 1]);
	}
	
	/**
	 * Checks if the table contains an element at the given dotted key path.<br>
	 *
	 * @param dottedKey The dotted key path
	 * @return True if an element exists at the path, false otherwise
	 * @throws NullPointerException If the given key is null
	 */
	public boolean containsDotted(@NonNull String dottedKey) {
		return this.getDotted(dottedKey) != null;
	}
	
	/**
	 * Gets the element with the given key from this table.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable TomlElement get(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.get(key);
	}
	
	/**
	 * Gets the element with the given key as a TOML value.<br>
	 *
	 * @param key The key to get
	 * @return The element as a TOML value
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a TOML value
	 */
	public @NonNull TomlValue getTomlValue(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		TomlElement element = this.get(key);
		
		if (element == null) {
			throw new NoSuchTomlElementException("Expected TOML value for key '" + key + "', but found none");
		}
		return element.getAsTomlValue();
	}
	
	/**
	 * Gets the element with the given key as a TOML array.<br>
	 *
	 * @param key The key to get
	 * @return The element as a TOML array
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a TOML array
	 */
	public @NonNull TomlArray getTomlArray(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		TomlElement element = this.get(key);
		
		if (element == null) {
			throw new NoSuchTomlElementException("Expected TOML array for key '" + key + "', but found none");
		}
		return element.getAsTomlArray();
	}
	
	/**
	 * Gets the element with the given key as a TOML table.<br>
	 *
	 * @param key The key to get
	 * @return The element as a TOML table
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a TOML table
	 */
	public @NonNull TomlTable getTomlTable(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		TomlElement element = this.get(key);
		
		if (element == null) {
			throw new NoSuchTomlElementException("Expected TOML table for key '" + key + "', but found none");
		}
		return element.getAsTomlTable();
	}
	
	/**
	 * Gets the element with the given key as a string.<br>
	 *
	 * @param key The key to get
	 * @return The element as a string
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a string
	 */
	public @NonNull String getAsString(@NonNull String key) {
		return this.getTomlValue(key).getAsString();
	}
	
	/**
	 * Gets the element with the given key as a boolean.<br>
	 *
	 * @param key The key to get
	 * @return The element as a boolean
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a boolean
	 */
	public boolean getAsBoolean(@NonNull String key) {
		return this.getTomlValue(key).getAsBoolean();
	}
	
	/**
	 * Gets the element with the given key as a number.<br>
	 *
	 * @param key The key to get
	 * @return The element as a number
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a number
	 */
	public @NonNull Number getAsNumber(@NonNull String key) {
		return this.getTomlValue(key).getAsNumber();
	}
	
	/**
	 * Gets the element with the given key as a byte.<br>
	 *
	 * @param key The key to get
	 * @return The element as a byte
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a number
	 */
	public byte getAsByte(@NonNull String key) {
		return this.getTomlValue(key).getAsByte();
	}
	
	/**
	 * Gets the element with the given key as a short.<br>
	 *
	 * @param key The key to get
	 * @return The element as a short
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a number
	 */
	public short getAsShort(@NonNull String key) {
		return this.getTomlValue(key).getAsShort();
	}
	
	/**
	 * Gets the element with the given key as an integer.<br>
	 *
	 * @param key The key to get
	 * @return The element as an integer
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a number
	 */
	public int getAsInteger(@NonNull String key) {
		return this.getTomlValue(key).getAsInteger();
	}
	
	/**
	 * Gets the element with the given key as a long.<br>
	 *
	 * @param key The key to get
	 * @return The element as a long
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a number
	 */
	public long getAsLong(@NonNull String key) {
		return this.getTomlValue(key).getAsLong();
	}
	
	/**
	 * Gets the element with the given key as a float.<br>
	 *
	 * @param key The key to get
	 * @return The element as a float
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a number
	 */
	public float getAsFloat(@NonNull String key) {
		return this.getTomlValue(key).getAsFloat();
	}
	
	/**
	 * Gets the element with the given key as a double.<br>
	 *
	 * @param key The key to get
	 * @return The element as a double
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a number
	 */
	public double getAsDouble(@NonNull String key) {
		return this.getTomlValue(key).getAsDouble();
	}
	
	/**
	 * Gets the element with the given key as a local date.<br>
	 *
	 * @param key The key to get
	 * @return The element as a local date
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a local date
	 */
	public @NonNull LocalDate getAsLocalDate(@NonNull String key) {
		return this.getTomlValue(key).getAsLocalDate();
	}
	
	/**
	 * Gets the element with the given key as a local time.<br>
	 *
	 * @param key The key to get
	 * @return The element as a local time
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a local time
	 */
	public @NonNull LocalTime getAsLocalTime(@NonNull String key) {
		return this.getTomlValue(key).getAsLocalTime();
	}
	
	/**
	 * Gets the element with the given key as a local date-time.<br>
	 *
	 * @param key The key to get
	 * @return The element as a local date-time
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not a local date-time
	 */
	public @NonNull LocalDateTime getAsLocalDateTime(@NonNull String key) {
		return this.getTomlValue(key).getAsLocalDateTime();
	}
	
	/**
	 * Gets the element with the given key as an offset date-time.<br>
	 *
	 * @param key The key to get
	 * @return The element as an offset date-time
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchTomlElementException If no element was found for the given key
	 * @throws TomlTypeException If the element is not an offset date-time
	 */
	public @NonNull OffsetDateTime getAsOffsetDateTime(@NonNull String key) {
		return this.getTomlValue(key).getAsOffsetDateTime();
	}
	
	/**
	 * Removes the element with the given key from this table.<br>
	 *
	 * @param key The key to remove
	 * @return The element associated with the key, or null if the key was not present
	 */
	public @Nullable TomlElement remove(@Nullable String key) {
		return this.elements.remove(key);
	}
	
	/**
	 * Removes all elements from this table.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	
	/**
	 * Replaces the element with the given key with the new given element.<br>
	 * If the given element is null, it will be replaced with TOML null.<br>
	 *
	 * @param key The key to replace
	 * @param newElement The new element to replace with
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable TomlElement replace(@NonNull String key, @Nullable TomlElement newElement) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.replace(key, newElement == null ? TomlNull.INSTANCE : newElement);
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
	public boolean replace(@NonNull String key, @NonNull TomlElement oldElement, @Nullable TomlElement newElement) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(oldElement, "Old element must not be null");
		return this.elements.replace(key, oldElement, newElement == null ? TomlNull.INSTANCE : newElement);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TomlTable that)) return false;
		
		return this.elements.equals(that.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.elements);
	}
	
	@Override
	public String toString() {
		return this.toString(TomlConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull TomlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		if (this.elements.isEmpty()) {
			return "{}";
		}
		
		if ((this.inline || config.useInlineTables()) && this.elements.size() <= config.maxInlineTableSize()) {
			return this.toInlineString(config);
		}
		return this.toBlockString(config);
	}
	
	/**
	 * Formats this table as an inline table.<br>
	 *
	 * @param config The TOML config
	 * @return The inline table string
	 */
	private @NonNull String toInlineString(@NonNull TomlConfig config) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		
		boolean first = true;
		for (Map.Entry<String, TomlElement> entry : this.elements.entrySet()) {
			if (!first) {
				builder.append(", ");
			}
			first = false;
			
			builder.append(formatKey(entry.getKey()));
			builder.append(" = ");
			builder.append(entry.getValue().toString(config));
		}
		
		builder.append("}");
		return builder.toString();
	}
	
	/**
	 * Formats this table as a block table (multi-line).<br>
	 *
	 * @param config The TOML config
	 * @return The block table string
	 */
	private @NonNull String toBlockString(@NonNull TomlConfig config) {
		StringBuilder builder = new StringBuilder();
		
		for (Map.Entry<String, TomlElement> entry : this.elements.entrySet()) {
			builder.append(formatKey(entry.getKey()));
			builder.append(" = ");
			builder.append(entry.getValue().toString(config));
			builder.append(System.lineSeparator());
		}
		
		return builder.toString();
	}
	
	/**
	 * Returns a formatted string representation of this table with section headers.<br>
	 * This is the standard TOML format with [section] headers.<br>
	 *
	 * @param tablePath The path to this table (e.g., "server" or "server.database")
	 * @param config The TOML config
	 * @return The formatted table string with section header
	 */
	public @NonNull String toSectionString(@NonNull String tablePath, @NonNull TomlConfig config) {
		Objects.requireNonNull(tablePath, "Table path must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		
		List<Map.Entry<String, TomlElement>> simpleEntries = new ArrayList<>();
		List<Map.Entry<String, TomlElement>> nestedTables = new ArrayList<>();
		List<Map.Entry<String, TomlElement>> arrayOfTables = new ArrayList<>();
		
		for (Map.Entry<String, TomlElement> entry : this.elements.entrySet()) {
			TomlElement value = entry.getValue();
			
			if (value instanceof TomlTable table && !table.isInline()) {
				nestedTables.add(entry);
			} else if (value instanceof TomlArray array && array.isArrayOfTables()) {
				arrayOfTables.add(entry);
			} else {
				simpleEntries.add(entry);
			}
		}
		
		StringBuilder builder = new StringBuilder();
		if (!simpleEntries.isEmpty() || (nestedTables.isEmpty() && arrayOfTables.isEmpty())) {
			builder.append("[").append(tablePath).append("]");
			builder.append(System.lineSeparator());
			
			for (Map.Entry<String, TomlElement> entry : simpleEntries) {
				builder.append(formatKey(entry.getKey()));
				builder.append(" = ");
				builder.append(entry.getValue().toString(config));
				builder.append(System.lineSeparator());
			}
		}
		
		for (Map.Entry<String, TomlElement> entry : nestedTables) {
			if (!builder.isEmpty()) {
				builder.append(System.lineSeparator());
			}
			
			String nestedPath = tablePath + "." + formatKey(entry.getKey());
			builder.append(((TomlTable) entry.getValue()).toSectionString(nestedPath, config));
		}
		
		for (Map.Entry<String, TomlElement> entry : arrayOfTables) {
			TomlArray array = (TomlArray) entry.getValue();
			String arrayPath = tablePath + "." + formatKey(entry.getKey());
			for (TomlElement element : array) {
				if (!builder.isEmpty()) {
					builder.append(System.lineSeparator());
				}
				
				builder.append("[[").append(arrayPath).append("]]");
				builder.append(System.lineSeparator());
				
				if (element instanceof TomlTable table) {
					for (Map.Entry<String, TomlElement> tableEntry : table.elements.entrySet()) {
						builder.append(formatKey(tableEntry.getKey()));
						builder.append(" = ");
						builder.append(tableEntry.getValue().toString(config));
						builder.append(System.lineSeparator());
					}
				}
			}
		}
		return builder.toString();
	}
	//endregion
}
