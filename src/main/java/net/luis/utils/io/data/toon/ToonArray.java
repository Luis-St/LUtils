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

import com.google.common.collect.Lists;
import net.luis.utils.io.data.toon.exception.ToonArrayIndexOutOfBoundsException;
import net.luis.utils.io.data.toon.exception.ToonTypeException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Represents a toon array.<br>
 * A toon array is an ordered collection of toon elements.<br>
 *
 * @author Luis-St
 */
public class ToonArray implements ToonElement, Iterable<ToonElement> {
	
	/**
	 * The internal list of elements.<br>
	 */
	private final List<ToonElement> elements = Lists.newArrayList();
	
	/**
	 * Constructs an empty toon array.<br>
	 */
	public ToonArray() {}
	
	/**
	 * Constructs a toon array with the given elements.<br>
	 *
	 * @param elements The elements to add
	 * @throws NullPointerException If the elements are null
	 */
	public ToonArray(@NonNull List<? extends ToonElement> elements) {
		this.elements.addAll(Objects.requireNonNull(elements, "Elements must not be null"));
	}
	
	/**
	 * Checks if a given index is valid for this array.<br>
	 *
	 * @param index The index to check
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	private void checkIndex(int index) {
		if (index < 0 || index >= this.elements.size()) {
			throw new ToonArrayIndexOutOfBoundsException("Index " + index + " is out of bounds for toon array of size " + this.elements.size());
		}
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
	public boolean contains(@Nullable ToonElement element) {
		return this.elements.contains(element);
	}
	
	@Override
	public @NonNull Iterator<ToonElement> iterator() {
		return this.elements.iterator();
	}
	
	/**
	 * Returns an unmodifiable collection of elements in this array.<br>
	 * @return The elements of this array
	 */
	public @NonNull @Unmodifiable Collection<ToonElement> elements() {
		return Collections.unmodifiableCollection(this.elements);
	}
	
	/**
	 * Returns an unmodifiable list of elements in this array.<br>
	 * @return The elements of this array as a list
	 */
	public @NonNull @Unmodifiable List<ToonElement> getElements() {
		return Collections.unmodifiableList(this.elements);
	}
	
	/**
	 * Sets the element at the given index.<br>
	 *
	 * @param index The index to set
	 * @param element The element to set (null becomes ToonNull)
	 * @return The previous element at the index
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @NonNull ToonElement set(int index, @Nullable ToonElement element) {
		this.checkIndex(index);
		return this.elements.set(index, element == null ? ToonNull.INSTANCE : element);
	}
	
	/**
	 * Sets the element at the given index to a string value.<br>
	 *
	 * @param index The index to set
	 * @param value The string value (null becomes ToonNull)
	 * @return The previous element at the index
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @NonNull ToonElement set(int index, @Nullable String value) {
		return this.set(index, value == null ? null : new ToonValue(value));
	}
	
	/**
	 * Sets the element at the given index to a boolean value.<br>
	 *
	 * @param index The index to set
	 * @param value The boolean value
	 * @return The previous element at the index
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @NonNull ToonElement set(int index, boolean value) {
		return this.set(index, new ToonValue(value));
	}
	
	/**
	 * Sets the element at the given index to a number value.<br>
	 *
	 * @param index The index to set
	 * @param value The number value (null becomes ToonNull)
	 * @return The previous element at the index
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @NonNull ToonElement set(int index, @Nullable Number value) {
		return this.set(index, value == null ? null : new ToonValue(value));
	}
	
	/**
	 * Adds an element to this array.<br>
	 *
	 * @param element The element to add (null becomes ToonNull)
	 */
	public void add(@Nullable ToonElement element) {
		this.elements.add(element == null ? ToonNull.INSTANCE : element);
	}
	
	/**
	 * Adds a string value to this array.<br>
	 *
	 * @param value The string value (null becomes ToonNull)
	 */
	public void add(@Nullable String value) {
		this.add(value == null ? null : new ToonValue(value));
	}
	
	/**
	 * Adds a boolean value to this array.<br>
	 *
	 * @param value The boolean value
	 */
	public void add(boolean value) {
		this.add(new ToonValue(value));
	}
	
	/**
	 * Adds a number value to this array.<br>
	 *
	 * @param value The number value (null becomes ToonNull)
	 */
	public void add(@Nullable Number value) {
		this.add(value == null ? null : new ToonValue(value));
	}
	
	/**
	 * Adds all elements from another array to this array.<br>
	 *
	 * @param array The array to add elements from
	 * @throws NullPointerException If the array is null
	 */
	public void addAll(@NonNull ToonArray array) {
		this.elements.addAll(Objects.requireNonNull(array, "Array must not be null").elements);
	}
	
	/**
	 * Adds all elements from a varargs array.<br>
	 *
	 * @param elements The elements to add
	 * @throws NullPointerException If the elements array is null
	 */
	public void addAll(ToonElement @NonNull ... elements) {
		for (ToonElement element : Objects.requireNonNull(elements, "Elements must not be null")) {
			this.add(element);
		}
	}
	
	/**
	 * Adds all elements from a list.<br>
	 *
	 * @param elements The elements to add
	 * @throws NullPointerException If the elements list is null
	 */
	public void addAll(@NonNull List<? extends ToonElement> elements) {
		for (ToonElement element : Objects.requireNonNull(elements, "Elements must not be null")) {
			this.add(element);
		}
	}
	
	/**
	 * Gets the element at the given index.<br>
	 *
	 * @param index The index to get
	 * @return The element at the index
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @NonNull ToonElement get(int index) {
		this.checkIndex(index);
		return this.elements.get(index);
	}
	
	/**
	 * Gets the element at the given index as a toon value.<br>
	 *
	 * @param index The index to get
	 * @return The element as a toon value
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws ToonTypeException If the element is not a toon value
	 */
	public @NonNull ToonValue getAsToonValue(int index) {
		return this.get(index).getAsToonValue();
	}
	
	/**
	 * Gets the element at the given index as a toon array.<br>
	 *
	 * @param index The index to get
	 * @return The element as a toon array
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws ToonTypeException If the element is not a toon array
	 */
	public @NonNull ToonArray getAsToonArray(int index) {
		return this.get(index).getAsToonArray();
	}
	
	/**
	 * Gets the element at the given index as a toon object.<br>
	 *
	 * @param index The index to get
	 * @return The element as a toon object
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws ToonTypeException If the element is not a toon object
	 */
	public @NonNull ToonObject getAsToonObject(int index) {
		return this.get(index).getAsToonObject();
	}
	
	/**
	 * Gets the element at the given index as a string.<br>
	 *
	 * @param index The index to get
	 * @return The element as a string
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws ToonTypeException If the element is not a string
	 */
	public @NonNull String getAsString(int index) {
		return this.get(index).getAsString();
	}
	
	/**
	 * Gets the element at the given index as a boolean.<br>
	 *
	 * @param index The index to get
	 * @return The element as a boolean
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws ToonTypeException If the element is not a boolean
	 */
	public boolean getAsBoolean(int index) {
		return this.get(index).getAsBoolean();
	}
	
	/**
	 * Gets the element at the given index as a number.<br>
	 *
	 * @param index The index to get
	 * @return The element as a number
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws ToonTypeException If the element is not a number
	 */
	public @NonNull Number getAsNumber(int index) {
		return this.get(index).getAsNumber();
	}
	
	/**
	 * Gets the element at the given index as an integer.<br>
	 *
	 * @param index The index to get
	 * @return The element as an integer
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws ToonTypeException If the element is not a number
	 */
	public int getAsInteger(int index) {
		return this.get(index).getAsInteger();
	}
	
	/**
	 * Gets the element at the given index as a long.<br>
	 *
	 * @param index The index to get
	 * @return The element as a long
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws ToonTypeException If the element is not a number
	 */
	public long getAsLong(int index) {
		return this.get(index).getAsLong();
	}
	
	/**
	 * Gets the element at the given index as a double.<br>
	 *
	 * @param index The index to get
	 * @return The element as a double
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws ToonTypeException If the element is not a number
	 */
	public double getAsDouble(int index) {
		return this.get(index).getAsDouble();
	}
	
	/**
	 * Removes the element at the given index.<br>
	 *
	 * @param index The index to remove
	 * @return The removed element
	 * @throws ToonArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @NonNull ToonElement remove(int index) {
		this.checkIndex(index);
		return this.elements.remove(index);
	}
	
	/**
	 * Removes the given element from this array.<br>
	 *
	 * @param element The element to remove
	 * @return True if the element was removed, false otherwise
	 */
	public boolean remove(@Nullable ToonElement element) {
		return this.elements.remove(element);
	}
	
	/**
	 * Removes all elements from this array.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	
	/**
	 * Checks if all elements in this array are primitives or null.<br>
	 *
	 * @return True if all elements are primitive values or null, false otherwise
	 */
	private boolean isPrimitiveArray() {
		for (ToonElement element : this.elements) {
			if (!(element instanceof ToonValue) && !(element instanceof ToonNull)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Formats this array with a key prefix for use in toon object output.<br>
	 *
	 * @param key The formatted key
	 * @param config The toon config
	 * @param depth The current nesting depth
	 * @param indentStr The current indentation string
	 * @return The formatted array string with key prefix
	 * @throws NullPointerException If any parameter is null
	 */
	@NonNull String toStringWithKey(@NonNull String key, @NonNull ToonConfig config, int depth, @NonNull String indentStr) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		Objects.requireNonNull(indentStr, "Indent string must not be null");
		
		if (this.elements.isEmpty()) {
			return indentStr + key + "[0" + config.delimiter().getSymbol() + "]:";
		}
		
		char delim = config.delimiter().getChar();
		String delimSymbol = config.delimiter().getSymbol();
		
		if (this.isPrimitiveArray()) {
			return this.toInlineString(key, config, indentStr, delim, delimSymbol);
		}
		
		if (ToonHelper.isTabularEligible(this)) {
			return this.toTabularString(key, config, depth, indentStr, delim, delimSymbol);
		}
		
		return this.toExpandedListString(key, config, depth, indentStr, delimSymbol);
	}
	
	/**
	 * Formats this array as an inline primitive array.<br>
	 *
	 * @param key The formatted key
	 * @param config The toon config
	 * @param indentStr The current indentation string
	 * @param delim The delimiter character
	 * @param delimSymbol The delimiter symbol
	 * @return The formatted inline array string
	 * @throws NullPointerException If any parameter is null
	 */
	private @NonNull String toInlineString(@NonNull String key, @NonNull ToonConfig config, @NonNull String indentStr, char delim, @NonNull String delimSymbol) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		Objects.requireNonNull(indentStr, "Indent string must not be null");
		Objects.requireNonNull(delimSymbol, "Delimiter symbol must not be null");
		
		StringBuilder builder = new StringBuilder();
		builder.append(indentStr).append(key);
		builder.append("[").append(this.elements.size()).append(delimSymbol).append("]: ");
		
		boolean first = true;
		for (ToonElement element : this.elements) {
			if (!first) {
				if (delim == '\t') {
					builder.append('\t');
				} else {
					builder.append(delimSymbol).append(" ");
				}
			}
			
			first = false;
			builder.append(element.toString(config));
		}
		return builder.toString();
	}
	
	/**
	 * Formats this array as a tabular array with header and rows.<br>
	 *
	 * @param key The formatted key
	 * @param config The toon config
	 * @param depth The current nesting depth
	 * @param indentStr The current indentation string
	 * @param delim The delimiter character
	 * @param delimSymbol The delimiter symbol
	 * @return The formatted tabular array string
	 * @throws NullPointerException If any parameter is null
	 */
	private @NonNull String toTabularString(@NonNull String key, @NonNull ToonConfig config, int depth, @NonNull String indentStr, char delim, @NonNull String delimSymbol) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		Objects.requireNonNull(indentStr, "Indent string must not be null");
		Objects.requireNonNull(delimSymbol, "Delimiter symbol must not be null");
		
		ToonObject firstObj = this.elements.getFirst().getAsToonObject();
		Set<String> fields = firstObj.keySet();
		
		StringBuilder builder = new StringBuilder();
		builder.append(indentStr).append(key);
		builder.append("[").append(this.elements.size()).append(delimSymbol).append("]{");
		
		boolean firstField = true;
		for (String field : fields) {
			if (!firstField) {
				if (delim == '\t') {
					builder.append('\t');
				} else {
					builder.append(delimSymbol).append(" ");
				}
			}
			
			firstField = false;
			builder.append(ToonHelper.formatKey(field));
		}
		builder.append("}:");
		
		String rowIndent = " ".repeat(config.indent() * (depth + 1));
		for (ToonElement element : this.elements) {
			builder.append("\n").append(rowIndent);
			ToonObject obj = element.getAsToonObject();
			
			boolean firstValue = true;
			for (String field : fields) {
				if (!firstValue) {
					if (delim == '\t') {
						builder.append('\t');
					} else {
						builder.append(delimSymbol).append(" ");
					}
				}
				
				firstValue = false;
				ToonElement value = obj.get(field);
				builder.append(value != null ? value.toString(config) : "null");
			}
		}
		return builder.toString();
	}
	
	/**
	 * Formats this array as an expanded list with dash-prefixed items.<br>
	 *
	 * @param key The formatted key
	 * @param config The toon config
	 * @param depth The current nesting depth
	 * @param indentStr The current indentation string
	 * @param delimSymbol The delimiter symbol
	 * @return The formatted expanded list string
	 * @throws NullPointerException If any parameter is null
	 */
	private @NonNull String toExpandedListString(@NonNull String key, @NonNull ToonConfig config, int depth, @NonNull String indentStr, @NonNull String delimSymbol) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		Objects.requireNonNull(indentStr, "Indent string must not be null");
		Objects.requireNonNull(delimSymbol, "Delimiter symbol must not be null");
		
		StringBuilder builder = new StringBuilder();
		builder.append(indentStr).append(key).append("[").append(this.elements.size()).append("]:");
		
		String itemIndent = " ".repeat(config.indent() * (depth + 1));
		for (ToonElement element : this.elements) {
			builder.append("\n");
			
			if (element instanceof ToonObject obj) {
				builder.append(itemIndent).append("-");
				
				if (!obj.isEmpty()) {
					builder.append("\n");
					builder.append(obj.toBlockString(config, depth + 2));
				}
			} else if (element instanceof ToonArray innerArray) {
				builder.append(innerArray.toStringWithKey("- ", config, depth + 1, itemIndent));
			} else {
				builder.append(itemIndent).append("- ").append(element.toString(config));
			}
		}
		return builder.toString();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ToonArray that)) return false;
		
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
		return this.toStringWithKey("", config, 0, "");
	}
	//endregion
}
