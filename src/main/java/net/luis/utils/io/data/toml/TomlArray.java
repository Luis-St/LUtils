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

package net.luis.utils.io.data.toml;

import com.google.common.collect.Lists;
import net.luis.utils.io.data.toml.exception.TomlArrayIndexOutOfBoundsException;
import net.luis.utils.io.data.toml.exception.TomlTypeException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.*;
import java.util.*;

/**
 * Represents a TOML array.<br>
 * A TOML array is an ordered collection of TOML elements.<br>
 * Note: In TOML, arrays should be homogeneous (same type), but this implementation
 * allows heterogeneous arrays for flexibility.<br>
 *
 * @author Luis-St
 */
public class TomlArray implements TomlElement, Iterable<TomlElement> {
	
	/**
	 * The internal list of elements.<br>
	 */
	private final List<TomlElement> elements = Lists.newArrayList();
	
	/**
	 * Whether this array represents an array of tables ([[array]]).<br>
	 */
	private boolean isArrayOfTables;
	
	/**
	 * Constructs an empty TOML array.<br>
	 */
	public TomlArray() {}
	
	/**
	 * Constructs a TOML array with the given elements.<br>
	 *
	 * @param elements The elements to add
	 * @throws NullPointerException If the elements are null
	 */
	public TomlArray(@NonNull List<? extends TomlElement> elements) {
		this.elements.addAll(Objects.requireNonNull(elements, "Elements must not be null"));
	}
	
	/**
	 * Checks if a given index is valid for this array.<br>
	 *
	 * @param index The index to check
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	private void checkIndex(int index) {
		if (index < 0 || index >= this.elements.size()) {
			throw new TomlArrayIndexOutOfBoundsException("Index " + index + " is out of bounds for TOML array of size " + this.elements.size());
		}
	}
	
	/**
	 * Returns whether this array represents an array of tables.<br>
	 * @return True if this is an array of tables, false otherwise
	 */
	public boolean isArrayOfTables() {
		return this.isArrayOfTables;
	}
	
	/**
	 * Sets whether this array represents an array of tables.<br>
	 * @param isArrayOfTables True if this is an array of tables
	 */
	public void setArrayOfTables(boolean isArrayOfTables) {
		this.isArrayOfTables = isArrayOfTables;
	}
	
	/**
	 * Returns the number of elements in this array.<br>
	 * @return The size of this array
	 */
	public int size() {
		return this.elements.size();
	}
	
	/**
	 * Checks if this array is empty.<br>
	 * @return True if this array is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * Checks if this array contains the given element.<br>
	 *
	 * @param element The element to check
	 * @return True if this array contains the element, false otherwise
	 */
	public boolean contains(@Nullable TomlElement element) {
		return this.elements.contains(element);
	}
	
	@Override
	public @NonNull Iterator<TomlElement> iterator() {
		return this.elements.iterator();
	}
	
	/**
	 * Returns an unmodifiable collection of elements in this array.<br>
	 * @return The elements of this array
	 */
	public @NonNull @Unmodifiable Collection<TomlElement> elements() {
		return Collections.unmodifiableCollection(this.elements);
	}
	
	/**
	 * Returns an unmodifiable list of elements in this array.<br>
	 * @return The elements of this array as a list
	 */
	public @NonNull @Unmodifiable List<TomlElement> getElements() {
		return Collections.unmodifiableList(this.elements);
	}
	
	/**
	 * Sets the element at the given index.<br>
	 *
	 * @param index The index to set
	 * @param element The element to set (null becomes TomlNull)
	 * @return The previous element at the index
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @NonNull TomlElement set(int index, @Nullable TomlElement element) {
		this.checkIndex(index);
		return this.elements.set(index, element == null ? TomlNull.INSTANCE : element);
	}
	
	/**
	 * Sets the element at the given index to a string value.<br>
	 *
	 * @param index The index to set
	 * @param value The string value (null becomes TomlNull)
	 * @return The previous element at the index
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @NonNull TomlElement set(int index, @Nullable String value) {
		return this.set(index, value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Sets the element at the given index to a boolean value.<br>
	 *
	 * @param index The index to set
	 * @param value The boolean value
	 * @return The previous element at the index
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @NonNull TomlElement set(int index, boolean value) {
		return this.set(index, new TomlValue(value));
	}
	
	/**
	 * Sets the element at the given index to a number value.<br>
	 *
	 * @param index The index to set
	 * @param value The number value (null becomes TomlNull)
	 * @return The previous element at the index
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @NonNull TomlElement set(int index, @Nullable Number value) {
		return this.set(index, value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Adds an element to this array.<br>
	 *
	 * @param element The element to add (null becomes TomlNull)
	 */
	public void add(@Nullable TomlElement element) {
		this.elements.add(element == null ? TomlNull.INSTANCE : element);
	}
	
	/**
	 * Adds a string value to this array.<br>
	 *
	 * @param value The string value (null becomes TomlNull)
	 */
	public void add(@Nullable String value) {
		this.add(value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Adds a boolean value to this array.<br>
	 *
	 * @param value The boolean value
	 */
	public void add(boolean value) {
		this.add(new TomlValue(value));
	}
	
	/**
	 * Adds a number value to this array.<br>
	 *
	 * @param value The number value (null becomes TomlNull)
	 */
	public void add(@Nullable Number value) {
		this.add(value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Adds a local date value to this array.<br>
	 *
	 * @param value The local date value (null becomes TomlNull)
	 */
	public void add(@Nullable LocalDate value) {
		this.add(value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Adds a local time value to this array.<br>
	 *
	 * @param value The local time value (null becomes TomlNull)
	 */
	public void add(@Nullable LocalTime value) {
		this.add(value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Adds a local date-time value to this array.<br>
	 *
	 * @param value The local date-time value (null becomes TomlNull)
	 */
	public void add(@Nullable LocalDateTime value) {
		this.add(value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Adds an offset date-time value to this array.<br>
	 *
	 * @param value The offset date-time value (null becomes TomlNull)
	 */
	public void add(@Nullable OffsetDateTime value) {
		this.add(value == null ? null : new TomlValue(value));
	}
	
	/**
	 * Adds all elements from another array to this array.<br>
	 *
	 * @param array The array to add elements from
	 * @throws NullPointerException If the array is null
	 */
	public void addAll(@NonNull TomlArray array) {
		this.elements.addAll(Objects.requireNonNull(array, "Array must not be null").elements);
	}
	
	/**
	 * Adds all elements from a varargs array.<br>
	 *
	 * @param elements The elements to add
	 * @throws NullPointerException If the elements array is null
	 */
	public void addAll(TomlElement @NonNull ... elements) {
		for (TomlElement element : Objects.requireNonNull(elements, "Elements must not be null")) {
			this.add(element);
		}
	}
	
	/**
	 * Adds all elements from a list.<br>
	 *
	 * @param elements The elements to add
	 * @throws NullPointerException If the elements list is null
	 */
	public void addAll(@NonNull List<? extends TomlElement> elements) {
		for (TomlElement element : Objects.requireNonNull(elements, "Elements must not be null")) {
			this.add(element);
		}
	}
	
	/**
	 * Gets the element at the given index.<br>
	 *
	 * @param index The index to get
	 * @return The element at the index
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @NonNull TomlElement get(int index) {
		this.checkIndex(index);
		return this.elements.get(index);
	}
	
	/**
	 * Gets the element at the given index as a TOML value.<br>
	 *
	 * @param index The index to get
	 * @return The element as a TOML value
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws TomlTypeException If the element is not a TOML value
	 */
	public @NonNull TomlValue getAsTomlValue(int index) {
		return this.get(index).getAsTomlValue();
	}
	
	/**
	 * Gets the element at the given index as a TOML array.<br>
	 *
	 * @param index The index to get
	 * @return The element as a TOML array
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws TomlTypeException If the element is not a TOML array
	 */
	public @NonNull TomlArray getAsTomlArray(int index) {
		return this.get(index).getAsTomlArray();
	}
	
	/**
	 * Gets the element at the given index as a TOML table.<br>
	 *
	 * @param index The index to get
	 * @return The element as a TOML table
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws TomlTypeException If the element is not a TOML table
	 */
	public @NonNull TomlTable getAsTomlTable(int index) {
		return this.get(index).getAsTomlTable();
	}
	
	/**
	 * Gets the element at the given index as a string.<br>
	 *
	 * @param index The index to get
	 * @return The element as a string
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws TomlTypeException If the element is not a string
	 */
	public @NonNull String getAsString(int index) {
		return this.get(index).getAsString();
	}
	
	/**
	 * Gets the element at the given index as a boolean.<br>
	 *
	 * @param index The index to get
	 * @return The element as a boolean
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws TomlTypeException If the element is not a boolean
	 */
	public boolean getAsBoolean(int index) {
		return this.get(index).getAsBoolean();
	}
	
	/**
	 * Gets the element at the given index as a number.<br>
	 *
	 * @param index The index to get
	 * @return The element as a number
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws TomlTypeException If the element is not a number
	 */
	public @NonNull Number getAsNumber(int index) {
		return this.get(index).getAsNumber();
	}
	
	/**
	 * Gets the element at the given index as an integer.<br>
	 *
	 * @param index The index to get
	 * @return The element as an integer
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws TomlTypeException If the element is not a number
	 */
	public int getAsInteger(int index) {
		return this.get(index).getAsInteger();
	}
	
	/**
	 * Gets the element at the given index as a long.<br>
	 *
	 * @param index The index to get
	 * @return The element as a long
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws TomlTypeException If the element is not a number
	 */
	public long getAsLong(int index) {
		return this.get(index).getAsLong();
	}
	
	/**
	 * Gets the element at the given index as a double.<br>
	 *
	 * @param index The index to get
	 * @return The element as a double
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws TomlTypeException If the element is not a number
	 */
	public double getAsDouble(int index) {
		return this.get(index).getAsDouble();
	}
	
	/**
	 * Gets the element at the given index as a local date.<br>
	 *
	 * @param index The index to get
	 * @return The element as a local date
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws TomlTypeException If the element is not a local date
	 */
	public @NonNull LocalDate getAsLocalDate(int index) {
		return this.get(index).getAsLocalDate();
	}
	
	/**
	 * Gets the element at the given index as a local date-time.<br>
	 *
	 * @param index The index to get
	 * @return The element as a local date-time
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws TomlTypeException If the element is not a local date-time
	 */
	public @NonNull LocalDateTime getAsLocalDateTime(int index) {
		return this.get(index).getAsLocalDateTime();
	}
	
	/**
	 * Gets the element at the given index as an offset date-time.<br>
	 *
	 * @param index The index to get
	 * @return The element as an offset date-time
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws TomlTypeException If the element is not an offset date-time
	 */
	public @NonNull OffsetDateTime getAsOffsetDateTime(int index) {
		return this.get(index).getAsOffsetDateTime();
	}
	
	/**
	 * Removes the element at the given index.<br>
	 *
	 * @param index The index to remove
	 * @return The removed element
	 * @throws TomlArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @NonNull TomlElement remove(int index) {
		this.checkIndex(index);
		return this.elements.remove(index);
	}
	
	/**
	 * Removes the given element from this array.<br>
	 *
	 * @param element The element to remove
	 * @return True if the element was removed, false otherwise
	 */
	public boolean remove(@Nullable TomlElement element) {
		return this.elements.remove(element);
	}
	
	/**
	 * Removes all elements from this array.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	
	//region  overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TomlArray that)) return false;
		
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
			return "[]";
		}
		
		if (config.useInlineArrays() && this.elements.size() <= config.maxInlineArraySize() && !this.isArrayOfTables) {
			return this.toInlineString(config);
		}
		return this.toBlockString(config);
	}
	
	/**
	 * Formats this array as an inline array.<br>
	 *
	 * @param config The TOML config
	 * @return The inline array string
	 */
	private @NonNull String toInlineString(@NonNull TomlConfig config) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		
		boolean first = true;
		for (TomlElement element : this.elements) {
			if (!first) {
				builder.append(", ");
			}
			first = false;
			builder.append(element.toString(config));
		}
		
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * Formats this array as a block array (multi-line).<br>
	 *
	 * @param config The TOML config
	 * @return The block array string
	 */
	private @NonNull String toBlockString(@NonNull TomlConfig config) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		
		if (config.prettyPrint()) {
			builder.append(System.lineSeparator());
		}
		
		for (TomlElement element : this.elements) {
			if (config.prettyPrint()) {
				builder.append(config.indent());
			}
			builder.append(element.toString(config));
			builder.append(",");
			if (config.prettyPrint()) {
				builder.append(System.lineSeparator());
			}
		}
		
		builder.append("]");
		return builder.toString();
	}
	//endregion
}
