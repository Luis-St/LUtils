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

package net.luis.utils.io.data.yaml;

import com.google.common.collect.Lists;
import net.luis.utils.io.data.yaml.exception.YamlSequenceIndexOutOfBoundsException;
import net.luis.utils.io.data.yaml.exception.YamlTypeException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Represents a yaml sequence.<br>
 * A yaml sequence is an ordered collection of values.<br>
 * The values can be of any type, including yaml null.<br>
 *
 * @author Luis-St
 */
public class YamlSequence implements YamlElement, Iterable<YamlElement> {
	
	/**
	 * The internal linked list of yaml elements.<br>
	 * The elements are stored in the order they were added.<br>
	 */
	private final List<YamlElement> elements = Lists.newLinkedList();
	
	/**
	 * Constructs an empty yaml sequence.<br>
	 */
	public YamlSequence() {}
	
	/**
	 * Constructs a yaml sequence with the given list of yaml elements.<br>
	 * @param elements The list of yaml elements to add
	 * @throws NullPointerException If the list of yaml elements is null
	 */
	public YamlSequence(@NonNull List<? extends YamlElement> elements) {
		this.elements.addAll(Objects.requireNonNull(elements, "Yaml elements must not be null"));
	}
	
	//region Query operations
	
	/**
	 * Returns the number of elements in this yaml sequence.<br>
	 * @return The size of this yaml sequence
	 */
	public int size() {
		return this.elements.size();
	}
	
	/**
	 * Checks if this yaml sequence is empty.<br>
	 * @return True if this yaml sequence is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * Checks if this yaml sequence contains the given yaml element.<br>
	 *
	 * @param yaml The yaml element to check for
	 * @return True if this yaml sequence contains the given yaml element, false otherwise
	 */
	public boolean contains(@Nullable YamlElement yaml) {
		return this.elements.contains(yaml);
	}
	
	/**
	 * Returns an iterator over the elements in this yaml sequence.<br>
	 * @return The iterator over the elements
	 */
	@Override
	public @NonNull Iterator<YamlElement> iterator() {
		return this.elements.iterator();
	}
	
	/**
	 * Returns an unmodifiable collection of the yaml elements in this yaml sequence.<br>
	 * @return The collection of yaml elements
	 */
	public @NonNull @Unmodifiable Collection<YamlElement> elements() {
		return Collections.unmodifiableCollection(this.elements);
	}
	
	/**
	 * Returns an unmodifiable list of the yaml elements in this yaml sequence.<br>
	 * @return The list of yaml elements
	 */
	public @NonNull @Unmodifiable List<YamlElement> getElements() {
		return List.copyOf(this.elements);
	}
	//endregion
	
	//region Set operations
	
	/**
	 * Sets the element at the given index to the given yaml element.<br>
	 * If the yaml element is null, it will be replaced with a yaml null element.<br>
	 *
	 * @param index The index of the element to set
	 * @param yaml The yaml element to set
	 * @return The previous element at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 */
	public @NonNull YamlElement set(int index, @Nullable YamlElement yaml) {
		if (0 > index) {
			throw new YamlSequenceIndexOutOfBoundsException(index);
		}
		if (index >= this.size()) {
			throw new YamlSequenceIndexOutOfBoundsException(index, this.size());
		}
		return this.elements.set(index, yaml == null ? YamlNull.INSTANCE : yaml);
	}
	
	/**
	 * Sets the element at the given index to the given string.<br>
	 * The string will be converted to a yaml scalar element.<br>
	 * If the string is null, it will be replaced with a yaml null element.<br>
	 *
	 * @param index The index of the element to set
	 * @param value The string to set
	 * @return The previous element at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 * @see #set(int, YamlElement)
	 */
	public @NonNull YamlElement set(int index, @Nullable String value) {
		return this.set(index, value == null ? null : new YamlScalar(value));
	}
	
	/**
	 * Sets the element at the given index to the given boolean.<br>
	 * The boolean will be converted to a yaml scalar element.<br>
	 *
	 * @param index The index of the element to set
	 * @param value The boolean to set
	 * @return The previous element at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 * @see #set(int, YamlElement)
	 */
	public @NonNull YamlElement set(int index, boolean value) {
		return this.set(index, new YamlScalar(value));
	}
	
	/**
	 * Sets the element at the given index to the given number.<br>
	 * The number will be converted to a yaml scalar element.<br>
	 * If the number is null, it will be replaced with a yaml null element.<br>
	 *
	 * @param index The index of the element to set
	 * @param value The number to set
	 * @return The previous element at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 * @see #set(int, YamlElement)
	 */
	public @NonNull YamlElement set(int index, @Nullable Number value) {
		return this.set(index, value == null ? null : new YamlScalar(value));
	}
	//endregion
	
	//region Add operations
	
	/**
	 * Adds the given yaml element to this yaml sequence.<br>
	 * If the yaml element is null, it will be replaced with a yaml null element.<br>
	 *
	 * @param yaml The yaml element to add
	 */
	public void add(@Nullable YamlElement yaml) {
		this.elements.add(yaml == null ? YamlNull.INSTANCE : yaml);
	}
	
	/**
	 * Adds the given string to this yaml sequence.<br>
	 * The string will be converted to a yaml scalar element.<br>
	 * If the string is null, it will be replaced with a yaml null element.<br>
	 *
	 * @param value The string to add
	 */
	public void add(@Nullable String value) {
		this.add(value == null ? null : new YamlScalar(value));
	}
	
	/**
	 * Adds the given boolean to this yaml sequence.<br>
	 * The boolean will be converted to a yaml scalar element.<br>
	 *
	 * @param value The boolean to add
	 */
	public void add(boolean value) {
		this.add(new YamlScalar(value));
	}
	
	/**
	 * Adds the given number to this yaml sequence.<br>
	 * The number will be converted to a yaml scalar element.<br>
	 * If the number is null, it will be replaced with a yaml null element.<br>
	 *
	 * @param value The number to add
	 */
	public void add(@Nullable Number value) {
		this.add(value == null ? null : new YamlScalar(value));
	}
	
	/**
	 * Adds all yaml elements from the given yaml sequence to this yaml sequence.<br>
	 *
	 * @param sequence The yaml sequence to add
	 * @throws NullPointerException If the yaml sequence is null
	 */
	public void addAll(@NonNull YamlSequence sequence) {
		this.addAll(Objects.requireNonNull(sequence, "Yaml sequence must not be null").elements);
	}
	
	/**
	 * Adds all yaml elements from the given array to this yaml sequence.<br>
	 * @param elements The array of yaml elements to add
	 */
	public void addAll(YamlElement @NonNull ... elements) {
		Objects.requireNonNull(elements, "Yaml elements must not be null");
		this.addAll(Arrays.asList(elements));
	}
	
	/**
	 * Adds all yaml elements from the given list to this yaml sequence.<br>
	 *
	 * @param elements The list of yaml elements to add
	 * @throws NullPointerException If the list of yaml elements is null
	 */
	public void addAll(@NonNull List<? extends YamlElement> elements) {
		this.elements.addAll(Objects.requireNonNull(elements, "Yaml elements must not be null"));
	}
	//endregion
	
	//region Remove operations
	
	/**
	 * Removes the element at the given index from this yaml sequence.<br>
	 *
	 * @param index The index of the element to remove
	 * @return The removed element
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 */
	public @NonNull YamlElement remove(int index) {
		if (0 > index) {
			throw new YamlSequenceIndexOutOfBoundsException(index);
		}
		if (index >= this.size()) {
			throw new YamlSequenceIndexOutOfBoundsException(index, this.size());
		}
		return this.elements.remove(index);
	}
	
	/**
	 * Removes the given yaml element from this yaml sequence.<br>
	 *
	 * @param yaml The yaml element to remove
	 * @return True if the yaml element was removed, false otherwise
	 */
	public boolean remove(@Nullable YamlElement yaml) {
		return this.elements.remove(yaml);
	}
	
	/**
	 * Removes all elements from this yaml sequence.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	//endregion
	
	//region Get operations
	
	/**
	 * Gets the yaml element at the given index from this yaml sequence.<br>
	 *
	 * @param index The index of the element to get
	 * @return The yaml element at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 */
	public @NonNull YamlElement get(int index) {
		if (0 > index) {
			throw new YamlSequenceIndexOutOfBoundsException(index);
		}
		if (index >= this.size()) {
			throw new YamlSequenceIndexOutOfBoundsException(index, this.size());
		}
		return this.elements.get(index);
	}
	
	/**
	 * Gets the yaml element at the given index from this yaml sequence as a yaml mapping.<br>
	 *
	 * @param index The index of the element to get
	 * @return The yaml mapping at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 * @throws YamlTypeException If the yaml element at the given index is not a yaml mapping
	 * @see #get(int)
	 */
	public @NonNull YamlMapping getAsYamlMapping(int index) {
		YamlElement yaml = this.get(index);
		if (yaml instanceof YamlMapping mapping) {
			return mapping;
		}
		return yaml.getAsYamlMapping(); // throws YamlTypeException
	}
	
	/**
	 * Gets the yaml element at the given index from this yaml sequence as a yaml sequence.<br>
	 *
	 * @param index The index of the element to get
	 * @return The yaml sequence at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 * @throws YamlTypeException If the yaml element at the given index is not a yaml sequence
	 * @see #get(int)
	 */
	public @NonNull YamlSequence getAsYamlSequence(int index) {
		YamlElement yaml = this.get(index);
		if (yaml instanceof YamlSequence sequence) {
			return sequence;
		}
		return yaml.getAsYamlSequence(); // throws YamlTypeException
	}
	
	/**
	 * Gets the yaml element at the given index from this yaml sequence as a yaml scalar.<br>
	 *
	 * @param index The index of the element to get
	 * @return The yaml scalar at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 * @throws YamlTypeException If the yaml element at the given index is not a yaml scalar
	 * @see #get(int)
	 */
	public @NonNull YamlScalar getAsYamlScalar(int index) {
		YamlElement yaml = this.get(index);
		if (yaml instanceof YamlScalar scalar) {
			return scalar;
		}
		return yaml.getAsYamlScalar(); // throws YamlTypeException
	}
	
	/**
	 * Gets the yaml element at the given index from this yaml sequence as a string.<br>
	 * The element will be converted to a yaml scalar and then to a string.<br>
	 *
	 * @param index The index of the element to get
	 * @return The string at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 * @throws YamlTypeException If the yaml element at the given index is not a yaml scalar
	 * @see #getAsYamlScalar(int)
	 */
	public @NonNull String getAsString(int index) {
		return this.getAsYamlScalar(index).getAsString();
	}
	
	/**
	 * Gets the yaml element at the given index from this yaml sequence as a boolean.<br>
	 * The element will be converted to a yaml scalar and then to a boolean.<br>
	 *
	 * @param index The index of the element to get
	 * @return The boolean at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 * @throws YamlTypeException If the yaml element at the given index is not a yaml scalar
	 * @see #getAsYamlScalar(int)
	 */
	public boolean getAsBoolean(int index) {
		return this.getAsYamlScalar(index).getAsBoolean();
	}
	
	/**
	 * Gets the yaml element at the given index from this yaml sequence as a number.<br>
	 * The element will be converted to a yaml scalar and then to a number.<br>
	 *
	 * @param index The index of the element to get
	 * @return The number at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 * @throws YamlTypeException If the yaml element at the given index is not a yaml scalar
	 * @see #getAsYamlScalar(int)
	 */
	public @NonNull Number getAsNumber(int index) {
		return this.getAsYamlScalar(index).getAsNumber();
	}
	
	/**
	 * Gets the yaml element at the given index from this yaml sequence as an integer.<br>
	 * The element will be converted to a yaml scalar and then to an integer.<br>
	 *
	 * @param index The index of the element to get
	 * @return The integer at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 * @throws YamlTypeException If the yaml element at the given index is not a yaml scalar
	 * @see #getAsYamlScalar(int)
	 */
	public int getAsInteger(int index) {
		return this.getAsYamlScalar(index).getAsInteger();
	}
	
	/**
	 * Gets the yaml element at the given index from this yaml sequence as a long.<br>
	 * The element will be converted to a yaml scalar and then to a long.<br>
	 *
	 * @param index The index of the element to get
	 * @return The long at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 * @throws YamlTypeException If the yaml element at the given index is not a yaml scalar
	 * @see #getAsYamlScalar(int)
	 */
	public long getAsLong(int index) {
		return this.getAsYamlScalar(index).getAsLong();
	}
	
	/**
	 * Gets the yaml element at the given index from this yaml sequence as a double.<br>
	 * The element will be converted to a yaml scalar and then to a double.<br>
	 *
	 * @param index The index of the element to get
	 * @return The double at the given index
	 * @throws YamlSequenceIndexOutOfBoundsException If the index is negative or greater than the size of this yaml sequence
	 * @throws YamlTypeException If the yaml element at the given index is not a yaml scalar
	 * @see #getAsYamlScalar(int)
	 */
	public double getAsDouble(int index) {
		return this.getAsYamlScalar(index).getAsDouble();
	}
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof YamlSequence sequence)) return false;
		
		return this.elements.equals(sequence.elements);
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
			return "[]";
		}
		
		// Use flow style if configured or for simple sequences
		if (!config.useBlockStyle()) {
			return this.toFlowString(config);
		}
		
		return this.toBlockString(config);
	}
	
	/**
	 * Returns the flow style string representation of this yaml sequence.<br>
	 *
	 * @param config The yaml config to use
	 * @return The flow style string representation
	 */
	private @NonNull String toFlowString(@NonNull YamlConfig config) {
		StringBuilder builder = new StringBuilder("[");
		for (int i = 0; i < this.elements.size(); i++) {
			if (i > 0) {
				builder.append(", ");
			}
			builder.append(this.elements.get(i).toString(config));
		}
		return builder.append("]").toString();
	}
	
	/**
	 * Returns the block style string representation of this yaml sequence.<br>
	 *
	 * @param config The yaml config to use
	 * @return The block style string representation
	 */
	private @NonNull String toBlockString(@NonNull YamlConfig config) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < this.elements.size(); i++) {
			if (i > 0) {
				builder.append(System.lineSeparator());
			}
			builder.append("- ");
			String value = this.elements.get(i).toString(config);
			if (value.contains(System.lineSeparator())) {
				// Indent multi-line values
				value = value.replace(System.lineSeparator(), System.lineSeparator() + config.indent());
			}
			builder.append(value);
		}
		return builder.toString();
	}
	//endregion
}
