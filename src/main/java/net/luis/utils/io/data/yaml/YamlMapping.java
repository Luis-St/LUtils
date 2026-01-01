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

package net.luis.utils.io.data.yaml;

import com.google.common.collect.Maps;
import net.luis.utils.io.data.yaml.exception.NoSuchYamlElementException;
import net.luis.utils.io.data.yaml.exception.YamlTypeException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Represents a yaml mapping.<br>
 * A yaml mapping is an ordered set of key/value pairs.<br>
 * A key is a string and a value can be any yaml element, including yaml null.<br>
 *
 * @author Luis-St
 */
public class YamlMapping implements YamlElement {
	
	/**
	 * The internal map of elements.<br>
	 * The order of the elements is preserved.<br>
	 */
	private final Map<String, YamlElement> elements = Maps.newLinkedHashMap();
	
	/**
	 * Constructs an empty yaml mapping.<br>
	 */
	public YamlMapping() {}
	
	/**
	 * Constructs a yaml mapping with the given elements.<br>
	 *
	 * @param elements The map of elements to add
	 * @throws NullPointerException If the given elements are null
	 */
	public YamlMapping(@NonNull Map<String, ? extends YamlElement> elements) {
		this.elements.putAll(Objects.requireNonNull(elements, "Yaml elements must not be null"));
	}
	
	//region Internal methods
	
	/**
	 * Formats a key for YAML output, quoting if necessary.<br>
	 *
	 * @param key The key to format
	 * @return The formatted key
	 */
	private static @NonNull String formatKey(@NonNull String key) {
		if (key.isEmpty() || needsQuoting(key)) {
			return "\"" + escapeString(key) + "\"";
		}
		return key;
	}
	
	/**
	 * Checks if a key needs to be quoted in YAML output.<br>
	 *
	 * @param key The key to check
	 * @return True if the key needs quoting, false otherwise
	 */
	private static boolean needsQuoting(@NonNull String key) {
		if (key.isEmpty()) {
			return true;
		}
		// Check for characters that require quoting
		char first = key.charAt(0);
		if (first == '#' || first == '&' || first == '*' || first == '!' ||
			first == '|' || first == '>' || first == '\'' || first == '"' ||
			first == '%' || first == '@' || first == '`' || first == '{' ||
			first == '[' || first == '-' || first == '?' || Character.isDigit(first)) {
			return true;
		}
		// Check for special characters within the key
		for (int i = 0; i < key.length(); i++) {
			char c = key.charAt(i);
			if (c == ':' || c == '#' || c == ' ' || c == '\n' || c == '\r' || c == '\t') {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Escapes special characters in a string for YAML double-quoted output.<br>
	 *
	 * @param string The string to escape
	 * @return The escaped string
	 */
	private static @NonNull String escapeString(@NonNull String string) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
				case '"' -> builder.append("\\\"");
				case '\\' -> builder.append("\\\\");
				case '\n' -> builder.append("\\n");
				case '\r' -> builder.append("\\r");
				case '\t' -> builder.append("\\t");
				default -> builder.append(c);
			}
		}
		return builder.toString();
	}
	//endregion
	
	/**
	 * Returns the number of elements in this yaml mapping.<br>
	 * @return The size of this yaml mapping
	 */
	public int size() {
		return this.elements.size();
	}
	
	/**
	 * Checks if this yaml mapping is empty.<br>
	 * @return True if this yaml mapping is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * Checks if this yaml mapping contains the given key.<br>
	 *
	 * @param key The key to check
	 * @return True if this yaml mapping contains the given key, false otherwise
	 */
	public boolean containsKey(@Nullable String key) {
		return this.elements.containsKey(key);
	}
	
	/**
	 * Checks if this yaml mapping contains the given element.<br>
	 *
	 * @param element The element to check
	 * @return True if this yaml mapping contains the given element, false otherwise
	 */
	public boolean containsValue(@Nullable YamlElement element) {
		return this.elements.containsValue(element);
	}
	
	/**
	 * Returns the set of keys in this yaml mapping.<br>
	 * @return The keys of this yaml mapping
	 */
	public @NonNull Set<String> keySet() {
		return this.elements.keySet();
	}
	
	/**
	 * Returns the collection of values in this yaml mapping.<br>
	 * @return The values of this yaml mapping
	 */
	public @NonNull @Unmodifiable Collection<YamlElement> elements() {
		return Collections.unmodifiableCollection(this.elements.values());
	}
	
	/**
	 * Returns the set of entries in this yaml mapping.<br>
	 * @return The entries of this yaml mapping
	 */
	public @NonNull Set<Map.Entry<String, YamlElement>> entrySet() {
		return this.elements.entrySet();
	}
	
	/**
	 * Iterates over the entries of this yaml mapping and applies the given action to each entry.<br>
	 *
	 * @param action The action to apply to each entry
	 * @throws NullPointerException If the given action is null
	 */
	public void forEach(@NonNull BiConsumer<? super String, ? super YamlElement> action) {
		this.elements.forEach(Objects.requireNonNull(action, "Action must not be null"));
	}
	
	/**
	 * Adds the given element with the given key to this yaml mapping.<br>
	 * If the element is null, it will be replaced with yaml null.<br>
	 * If the key is already present, the element will be replaced.<br>
	 *
	 * @param key The key to add
	 * @param element The element to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable YamlElement add(@NonNull String key, @Nullable YamlElement element) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.put(key, element == null ? YamlNull.INSTANCE : element);
	}
	
	/**
	 * Adds the given string with the given key to this yaml mapping.<br>
	 * If the string is null, it will be replaced with yaml null.<br>
	 * The string value will be converted to a yaml scalar.<br>
	 *
	 * @param key The key to add
	 * @param value The string value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, YamlElement)
	 */
	public @Nullable YamlElement add(@NonNull String key, @Nullable String value) {
		return this.add(key, value == null ? null : new YamlScalar(value));
	}
	
	/**
	 * Adds the given boolean with the given key to this yaml mapping.<br>
	 * The boolean will be converted to a yaml scalar.<br>
	 *
	 * @param key The key to add
	 * @param value The boolean value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, YamlElement)
	 */
	public @Nullable YamlElement add(@NonNull String key, boolean value) {
		return this.add(key, new YamlScalar(value));
	}
	
	/**
	 * Adds the given number with the given key to this yaml mapping.<br>
	 * If the number is null, it will be replaced with yaml null.<br>
	 * The number value will be converted to a yaml scalar.<br>
	 *
	 * @param key The key to add
	 * @param value The number value to add
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 * @see #add(String, YamlElement)
	 */
	public @Nullable YamlElement add(@NonNull String key, @Nullable Number value) {
		return this.add(key, value == null ? null : new YamlScalar(value));
	}
	
	/**
	 * Adds all elements from the given yaml mapping to this yaml mapping.<br>
	 *
	 * @param mapping The yaml mapping of elements to add
	 * @throws NullPointerException If the given yaml mapping is null
	 */
	public void addAll(@NonNull YamlMapping mapping) {
		this.elements.putAll(Objects.requireNonNull(mapping, "Yaml mapping must not be null").elements);
	}
	
	/**
	 * Adds all elements from the given map to this yaml mapping.<br>
	 *
	 * @param elements The map of elements to add
	 * @throws NullPointerException If the given elements are null
	 */
	public void addAll(@NonNull Map<String, ? extends YamlElement> elements) {
		this.elements.putAll(Objects.requireNonNull(elements, "Yaml elements must not be null"));
	}
	
	/**
	 * Removes the element with the given key from this yaml mapping.<br>
	 *
	 * @param key The key to remove
	 * @return The element associated with the key, or null if the key was not present
	 */
	public @Nullable YamlElement remove(@Nullable String key) {
		return this.elements.remove(key);
	}
	
	/**
	 * Removes all element pairs from this yaml mapping.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	
	/**
	 * Replaces the element with the given key in this yaml mapping with the new given element.<br>
	 * If the given element is null, it will be replaced with yaml null.<br>
	 *
	 * @param key The key to replace
	 * @param newElement The new element to replace with
	 * @return The previous element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable YamlElement replace(@NonNull String key, @Nullable YamlElement newElement) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.replace(key, newElement == null ? YamlNull.INSTANCE : newElement);
	}
	
	/**
	 * Replaces the given element with the given key in this yaml mapping with the new given element.<br>
	 *
	 * @param key The key to replace
	 * @param oldElement The old element to replace
	 * @param newElement The new element to replace with
	 * @return True if the element was replaced, false otherwise
	 * @throws NullPointerException If the given key or old element is null
	 */
	public boolean replace(@NonNull String key, @NonNull YamlElement oldElement, @Nullable YamlElement newElement) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(oldElement, "Old value must not be null");
		return this.elements.replace(key, oldElement, newElement == null ? YamlNull.INSTANCE : newElement);
	}
	
	/**
	 * Gets the element with the given key from this yaml mapping.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key, or null if the key was not present
	 * @throws NullPointerException If the given key is null
	 */
	public @Nullable YamlElement get(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return this.elements.get(key);
	}
	
	/**
	 * Gets the element with the given key from this yaml mapping as a yaml mapping.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a yaml mapping
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchYamlElementException If no element was found for the given key
	 * @throws YamlTypeException If the element is not a yaml mapping
	 * @see #get(String)
	 */
	public @NonNull YamlMapping getAsYamlMapping(@NonNull String key) {
		YamlElement yaml = this.get(key);
		if (yaml == null) {
			throw new NoSuchYamlElementException("Expected yaml mapping for key '" + key + "', but found none");
		}
		if (yaml instanceof YamlMapping mapping) {
			return mapping;
		}
		return yaml.getAsYamlMapping(); // throws YamlTypeException
	}
	
	/**
	 * Gets the element with the given key from this yaml mapping as a yaml sequence.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a yaml sequence
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchYamlElementException If no element was found for the given key
	 * @throws YamlTypeException If the element is not a yaml sequence
	 * @see #get(String)
	 */
	public @NonNull YamlSequence getAsYamlSequence(@NonNull String key) {
		YamlElement yaml = this.get(key);
		if (yaml == null) {
			throw new NoSuchYamlElementException("Expected yaml sequence for key '" + key + "', but found none");
		}
		if (yaml instanceof YamlSequence sequence) {
			return sequence;
		}
		return yaml.getAsYamlSequence(); // throws YamlTypeException
	}
	
	/**
	 * Gets the element with the given key from this yaml mapping as a yaml scalar.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a yaml scalar
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchYamlElementException If no element was found for the given key
	 * @throws YamlTypeException If the element is not a yaml scalar
	 * @see #get(String)
	 */
	public @NonNull YamlScalar getAsYamlScalar(@NonNull String key) {
		YamlElement yaml = this.get(key);
		if (yaml == null) {
			throw new NoSuchYamlElementException("Expected yaml scalar for key '" + key + "', but found none");
		}
		if (yaml instanceof YamlScalar scalar) {
			return scalar;
		}
		return yaml.getAsYamlScalar(); // throws YamlTypeException
	}
	
	/**
	 * Gets the element with the given key from this yaml mapping as a string.<br>
	 * The element will be converted to a yaml scalar and then to a string.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a string
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchYamlElementException If no element was found for the given key
	 * @throws YamlTypeException If the element is not a string
	 * @see #getAsYamlScalar(String)
	 */
	public @NonNull String getAsString(@NonNull String key) {
		return this.getAsYamlScalar(key).getAsString();
	}
	
	/**
	 * Gets the element with the given key from this yaml mapping as a boolean.<br>
	 * The element will be converted to a yaml scalar and then to a boolean.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a boolean
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchYamlElementException If no element was found for the given key
	 * @throws YamlTypeException If the element is not a boolean
	 * @see #getAsYamlScalar(String)
	 */
	public boolean getAsBoolean(@NonNull String key) {
		return this.getAsYamlScalar(key).getAsBoolean();
	}
	
	/**
	 * Gets the element with the given key from this yaml mapping as a number.<br>
	 * The element will be converted to a yaml scalar and then to a number.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a number
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchYamlElementException If no element was found for the given key
	 * @throws YamlTypeException If the element is not a number
	 * @see #getAsYamlScalar(String)
	 */
	public @NonNull Number getAsNumber(@NonNull String key) {
		return this.getAsYamlScalar(key).getAsNumber();
	}
	
	/**
	 * Gets the element with the given key from this yaml mapping as an integer.<br>
	 * The element will be converted to a yaml scalar and then to an integer.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as an integer
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchYamlElementException If no element was found for the given key
	 * @throws YamlTypeException If the element is not an integer
	 * @see #getAsYamlScalar(String)
	 */
	public int getAsInteger(@NonNull String key) {
		return this.getAsYamlScalar(key).getAsInteger();
	}
	
	/**
	 * Gets the element with the given key from this yaml mapping as a long.<br>
	 * The element will be converted to a yaml scalar and then to a long.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a long
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchYamlElementException If no element was found for the given key
	 * @throws YamlTypeException If the element is not a long
	 * @see #getAsYamlScalar(String)
	 */
	public long getAsLong(@NonNull String key) {
		return this.getAsYamlScalar(key).getAsLong();
	}
	
	/**
	 * Gets the element with the given key from this yaml mapping as a double.<br>
	 * The element will be converted to a yaml scalar and then to a double.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key as a double
	 * @throws NullPointerException If the given key is null
	 * @throws NoSuchYamlElementException If no element was found for the given key
	 * @throws YamlTypeException If the element is not a double
	 * @see #getAsYamlScalar(String)
	 */
	public double getAsDouble(@NonNull String key) {
		return this.getAsYamlScalar(key).getAsDouble();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof YamlMapping that)) return false;
		
		return this.elements.equals(that.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.elements);
	}
	
	@Override
	public String toString() {
		return this.toString(YamlConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull YamlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		
		if (this.elements.isEmpty()) {
			return "{}";
		}
		
		// Use flow style if configured
		if (!config.useBlockStyle()) {
			return this.toFlowString(config);
		}
		
		return this.toBlockString(config);
	}
	
	/**
	 * Returns the flow style string representation of this yaml mapping.<br>
	 *
	 * @param config The yaml config to use
	 * @return The flow style string representation
	 */
	private @NonNull String toFlowString(@NonNull YamlConfig config) {
		StringBuilder builder = new StringBuilder("{");
		List<Map.Entry<String, YamlElement>> entries = List.copyOf(this.elements.entrySet());
		for (int i = 0; i < entries.size(); i++) {
			if (i > 0) {
				builder.append(", ");
			}
			Map.Entry<String, YamlElement> entry = entries.get(i);
			builder.append(formatKey(entry.getKey())).append(": ");
			builder.append(entry.getValue().toString(config));
		}
		return builder.append("}").toString();
	}
	
	/**
	 * Returns the block style string representation of this yaml mapping.<br>
	 *
	 * @param config The yaml config to use
	 * @return The block style string representation
	 */
	private @NonNull String toBlockString(@NonNull YamlConfig config) {
		StringBuilder builder = new StringBuilder();
		List<Map.Entry<String, YamlElement>> entries = List.copyOf(this.elements.entrySet());
		for (int i = 0; i < entries.size(); i++) {
			if (i > 0) {
				builder.append(System.lineSeparator());
			}
			Map.Entry<String, YamlElement> entry = entries.get(i);
			builder.append(formatKey(entry.getKey())).append(":");
			
			YamlElement value = entry.getValue();
			String valueStr = value.toString(config);
			
			// Handle different value types for proper formatting
			if (value instanceof YamlMapping || value instanceof YamlSequence) {
				if (!valueStr.isEmpty() && !valueStr.startsWith("{") && !valueStr.startsWith("[")) {
					// Block style nested structure
					builder.append(System.lineSeparator());
					// Indent the nested structure
					valueStr = config.indent() + valueStr.replace(System.lineSeparator(), System.lineSeparator() + config.indent());
					builder.append(valueStr);
				} else {
					// Empty or flow style
					builder.append(" ").append(valueStr);
				}
			} else {
				builder.append(" ").append(valueStr);
			}
		}
		return builder.toString();
	}
	//endregion
}
