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
 * Represents an ini document with global properties and named sections.<br>
 * Global properties appear before any [section] header in an ini file.<br>
 *
 * @author Luis-St
 */
public class IniDocument implements IniElement {

	/**
	 * Global properties that appear before any section.<br>
	 */
	private final Map<String, IniElement> globalProperties = Maps.newLinkedHashMap();

	/**
	 * Named sections in this document.<br>
	 */
	private final Map<String, IniSection> sections = Maps.newLinkedHashMap();

	/**
	 * Constructs an empty ini document.<br>
	 */
	public IniDocument() {}

	/**
	 * Returns the number of global properties in this document.<br>
	 * @return The number of global properties
	 */
	public int globalSize() {
		return this.globalProperties.size();
	}

	/**
	 * Checks if this document has any global properties.<br>
	 * @return True if this document has global properties, false otherwise
	 */
	public boolean hasGlobalProperties() {
		return !this.globalProperties.isEmpty();
	}

	/**
	 * Checks if this document contains a global property with the given key.<br>
	 *
	 * @param key The key to check
	 * @return True if this document contains the given global key, false otherwise
	 */
	public boolean containsGlobalKey(@Nullable String key) {
		return this.globalProperties.containsKey(key);
	}

	/**
	 * Returns the set of global property keys.<br>
	 * @return The global property keys
	 */
	public @NonNull Set<String> globalKeySet() {
		return this.globalProperties.keySet();
	}

	/**
	 * Returns the collection of global property values.<br>
	 * @return The global property values
	 */
	public @NonNull @Unmodifiable Collection<IniElement> globalElements() {
		return Collections.unmodifiableCollection(this.globalProperties.values());
	}

	/**
	 * Returns the set of global property entries.<br>
	 * @return The global property entries
	 */
	public @NonNull Set<Map.Entry<String, IniElement>> globalEntrySet() {
		return this.globalProperties.entrySet();
	}

	/**
	 * Iterates over global properties and applies the given action.<br>
	 *
	 * @param action The action to apply
	 * @throws NullPointerException If the action is null
	 */
	public void forEachGlobal(@NonNull BiConsumer<? super String, ? super IniElement> action) {
		this.globalProperties.forEach(Objects.requireNonNull(action, "Action must not be null"));
	}

	/**
	 * Adds a global property to this document.<br>
	 * If the element is null, it will be replaced with ini null.<br>
	 *
	 * @param key The key to add
	 * @param element The element to add
	 * @return The previous element associated with the key, or null if not present
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable IniElement addGlobal(@NonNull String key, @Nullable IniElement element) {
		Objects.requireNonNull(key, "Global property key must not be null");
		return this.globalProperties.put(key, element == null ? IniNull.INSTANCE : element);
	}

	/**
	 * Adds a global string property to this document.<br>
	 *
	 * @param key The key to add
	 * @param value The string value to add
	 * @return The previous element associated with the key, or null if not present
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable IniElement addGlobal(@NonNull String key, @Nullable String value) {
		return this.addGlobal(key, value == null ? null : new IniValue(value));
	}

	/**
	 * Adds a global boolean property to this document.<br>
	 *
	 * @param key The key to add
	 * @param value The boolean value to add
	 * @return The previous element associated with the key, or null if not present
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable IniElement addGlobal(@NonNull String key, boolean value) {
		return this.addGlobal(key, new IniValue(value));
	}

	/**
	 * Adds a global number property to this document.<br>
	 *
	 * @param key The key to add
	 * @param value The number value to add
	 * @return The previous element associated with the key, or null if not present
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable IniElement addGlobal(@NonNull String key, @Nullable Number value) {
		return this.addGlobal(key, value == null ? null : new IniValue(value));
	}

	/**
	 * Removes a global property from this document.<br>
	 *
	 * @param key The key to remove
	 * @return The removed element, or null if not present
	 */
	public @Nullable IniElement removeGlobal(@Nullable String key) {
		return this.globalProperties.remove(key);
	}

	/**
	 * Removes all global properties from this document.<br>
	 */
	public void clearGlobal() {
		this.globalProperties.clear();
	}

	/**
	 * Gets a global property from this document.<br>
	 *
	 * @param key The key to get
	 * @return The element associated with the key, or null if not present
	 * @throws NullPointerException If the key is null
	 */
	public @Nullable IniElement getGlobal(@NonNull String key) {
		Objects.requireNonNull(key, "Global property key must not be null");
		return this.globalProperties.get(key);
	}

	/**
	 * Gets a global property as an ini value.<br>
	 *
	 * @param key The key to get
	 * @return The element as an ini value
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchIniElementException If no element was found
	 * @throws IniTypeException If the element is not an ini value
	 */
	public @NonNull IniValue getGlobalIniValue(@NonNull String key) {
		Objects.requireNonNull(key, "Global property key must not be null");
		IniElement element = this.getGlobal(key);
		
		if (element == null) {
			throw new NoSuchIniElementException("Expected global ini value for key '" + key + "', but found none");
		}
		if (element instanceof IniValue value) {
			return value;
		}
		
		return element.getAsIniValue();
	}

	/**
	 * Gets a global property as a string.<br>
	 *
	 * @param key The key to get
	 * @return The element as a string
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchIniElementException If no element was found
	 * @throws IniTypeException If the element is not a string
	 */
	public @NonNull String getGlobalAsString(@NonNull String key) {
		return this.getGlobalIniValue(key).getAsString();
	}

	/**
	 * Gets a global property as a boolean.<br>
	 *
	 * @param key The key to get
	 * @return The element as a boolean
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchIniElementException If no element was found
	 * @throws IniTypeException If the element is not a boolean
	 */
	public boolean getGlobalAsBoolean(@NonNull String key) {
		return this.getGlobalIniValue(key).getAsBoolean();
	}

	/**
	 * Gets a global property as a number.<br>
	 *
	 * @param key The key to get
	 * @return The element as a number
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchIniElementException If no element was found
	 * @throws IniTypeException If the element is not a number
	 */
	public @NonNull Number getGlobalAsNumber(@NonNull String key) {
		return this.getGlobalIniValue(key).getAsNumber();
	}

	/**
	 * Gets a global property as an integer.<br>
	 *
	 * @param key The key to get
	 * @return The element as an integer
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchIniElementException If no element was found
	 * @throws IniTypeException If the element is not a number
	 */
	public int getGlobalAsInteger(@NonNull String key) {
		return this.getGlobalIniValue(key).getAsInteger();
	}

	/**
	 * Gets a global property as a long.<br>
	 *
	 * @param key The key to get
	 * @return The element as a long
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchIniElementException If no element was found
	 * @throws IniTypeException If the element is not a number
	 */
	public long getGlobalAsLong(@NonNull String key) {
		return this.getGlobalIniValue(key).getAsLong();
	}

	/**
	 * Gets a global property as a double.<br>
	 *
	 * @param key The key to get
	 * @return The element as a double
	 * @throws NullPointerException If the key is null
	 * @throws NoSuchIniElementException If no element was found
	 * @throws IniTypeException If the element is not a number
	 */
	public double getGlobalAsDouble(@NonNull String key) {
		return this.getGlobalIniValue(key).getAsDouble();
	}

	/**
	 * Returns the number of sections in this document.<br>
	 * @return The number of sections
	 */
	public int sectionCount() {
		return this.sections.size();
	}

	/**
	 * Checks if this document has any sections.<br>
	 * @return True if this document has sections, false otherwise
	 */
	public boolean hasSections() {
		return !this.sections.isEmpty();
	}

	/**
	 * Checks if this document contains a section with the given name.<br>
	 *
	 * @param name The section name to check
	 * @return True if this document contains the section, false otherwise
	 */
	public boolean containsSection(@Nullable String name) {
		return this.sections.containsKey(name);
	}

	/**
	 * Returns the set of section names.<br>
	 * @return The section names
	 */
	public @NonNull Set<String> sectionNames() {
		return this.sections.keySet();
	}

	/**
	 * Returns the collection of sections.<br>
	 * @return The sections
	 */
	public @NonNull @Unmodifiable Collection<IniSection> sections() {
		return Collections.unmodifiableCollection(this.sections.values());
	}

	/**
	 * Iterates over sections and applies the given action.<br>
	 *
	 * @param action The action to apply
	 * @throws NullPointerException If the action is null
	 */
	public void forEachSection(@NonNull BiConsumer<? super String, ? super IniSection> action) {
		this.sections.forEach(Objects.requireNonNull(action, "Action must not be null"));
	}

	/**
	 * Adds a section to this document.<br>
	 * If a section with the same name exists, it will be replaced.<br>
	 *
	 * @param section The section to add
	 * @return The previous section with the same name, or null if not present
	 * @throws NullPointerException If the section is null
	 */
	public @Nullable IniSection addSection(@NonNull IniSection section) {
		Objects.requireNonNull(section, "Section must not be null");
		return this.sections.put(section.getName(), section);
	}

	/**
	 * Creates and adds an empty section with the given name.<br>
	 *
	 * @param name The section name
	 * @return The newly created section
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is blank
	 */
	public @NonNull IniSection createSection(@NonNull String name) {
		Objects.requireNonNull(name, "Section name must not be null");
		
		IniSection section = new IniSection(name);
		this.sections.put(name, section);
		return section;
	}

	/**
	 * Gets or creates a section with the given name.<br>
	 *
	 * @param name The section name
	 * @return The existing or newly created section
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is blank
	 */
	public @NonNull IniSection getOrCreateSection(@NonNull String name) {
		Objects.requireNonNull(name, "Section name must not be null");
		return this.sections.computeIfAbsent(name, IniSection::new);
	}

	/**
	 * Removes a section from this document.<br>
	 *
	 * @param name The section name to remove
	 * @return The removed section, or null if not present
	 */
	public @Nullable IniSection removeSection(@Nullable String name) {
		return this.sections.remove(name);
	}

	/**
	 * Removes all sections from this document.<br>
	 */
	public void clearSections() {
		this.sections.clear();
	}

	/**
	 * Gets a section from this document.<br>
	 *
	 * @param name The section name
	 * @return The section, or null if not present
	 * @throws NullPointerException If the name is null
	 */
	public @Nullable IniSection getSection(@NonNull String name) {
		Objects.requireNonNull(name, "Section name must not be null");
		return this.sections.get(name);
	}

	/**
	 * Gets a section from this document, throwing if not found.<br>
	 *
	 * @param name The section name
	 * @return The section
	 * @throws NullPointerException If the name is null
	 * @throws NoSuchIniElementException If the section was not found
	 */
	public @NonNull IniSection requireSection(@NonNull String name) {
		Objects.requireNonNull(name, "Section name must not be null");
		
		IniSection section = this.getSection(name);
		if (section == null) {
			throw new NoSuchIniElementException("Section '" + name + "' not found in ini document");
		}
		return section;
	}

	/**
	 * Checks if this document is empty (no global properties and no sections).<br>
	 * @return True if this document is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.globalProperties.isEmpty() && this.sections.isEmpty();
	}

	/**
	 * Removes all global properties and sections from this document.<br>
	 */
	public void clear() {
		this.globalProperties.clear();
		this.sections.clear();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IniDocument that)) return false;

		return this.globalProperties.equals(that.globalProperties) && this.sections.equals(that.sections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.globalProperties, this.sections);
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

		boolean first = true;
		for (Map.Entry<String, IniElement> entry : this.globalProperties.entrySet()) {
			IniElement value = entry.getValue();

			if (value.isIniNull() && config.nullStyle() == IniConfig.NullStyle.SKIP) {
				continue;
			}

			if (!first) {
				builder.append(System.lineSeparator());
			}
			first = false;

			builder.append(entry.getKey());
			builder.append(alignment).append(config.separator()).append(alignment);
			builder.append(value.toString(config));
		}

		for (IniSection section : this.sections.values()) {
			if (!first || !builder.isEmpty()) {
				builder.append(System.lineSeparator());
				if (config.prettyPrint()) {
					builder.append(System.lineSeparator());
				}
			}
			first = false;

			builder.append(section.toString(config));
		}
		return builder.toString();
	}
	//endregion
}
