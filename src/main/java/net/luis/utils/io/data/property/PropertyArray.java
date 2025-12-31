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

package net.luis.utils.io.data.property;

import com.google.common.collect.Lists;
import net.luis.utils.io.data.property.exception.*;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Represents a property array.<br>
 * A property array is an ordered collection of property elements.<br>
 *
 * @author Luis-St
 */
public class PropertyArray implements PropertyElement, Iterable<PropertyElement> {
	
	/**
	 * The internal list of elements.<br>
	 * The order of the elements is preserved.<br>
	 */
	private final List<PropertyElement> elements = Lists.newLinkedList();
	
	/**
	 * Constructs an empty property array.<br>
	 */
	public PropertyArray() {}
	
	/**
	 * Constructs a property array with the given elements.<br>
	 *
	 * @param elements The list of elements to add
	 * @throws NullPointerException If the given elements are null
	 */
	public PropertyArray(@NonNull List<? extends PropertyElement> elements) {
		this.elements.addAll(Objects.requireNonNull(elements, "Property elements must not be null"));
	}
	
	/**
	 * Returns the number of elements in this property array.<br>
	 * @return The size of this property array
	 */
	public int size() {
		return this.elements.size();
	}
	
	/**
	 * Checks if this property array is empty.<br>
	 * @return True if this property array is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * Checks if this property array contains the given element.<br>
	 *
	 * @param element The element to check
	 * @return True if this property array contains the given element, false otherwise
	 */
	public boolean contains(@Nullable PropertyElement element) {
		return this.elements.contains(element);
	}
	
	/**
	 * Returns the collection of elements in this property array.<br>
	 * @return The elements of this property array
	 */
	public @NonNull @Unmodifiable Collection<PropertyElement> elements() {
		return Collections.unmodifiableCollection(this.elements);
	}
	
	@Override
	public @NonNull Iterator<PropertyElement> iterator() {
		return this.elements.iterator();
	}
	
	/**
	 * Adds the given element to this property array.<br>
	 * If the element is null, it will be replaced with property null.<br>
	 *
	 * @param element The element to add
	 */
	public void add(@Nullable PropertyElement element) {
		this.elements.add(element == null ? PropertyNull.INSTANCE : element);
	}
	
	/**
	 * Adds the given string to this property array.<br>
	 * If the string is null, it will be replaced with property null.<br>
	 * The string value will be converted to a property value.<br>
	 *
	 * @param value The string value to add
	 * @see #add(PropertyElement)
	 */
	public void add(@Nullable String value) {
		this.add(value == null ? null : new PropertyValue(value));
	}
	
	/**
	 * Adds the given boolean to this property array.<br>
	 * The boolean will be converted to a property value.<br>
	 *
	 * @param value The boolean value to add
	 * @see #add(PropertyElement)
	 */
	public void add(boolean value) {
		this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds the given number to this property array.<br>
	 * If the number is null, it will be replaced with property null.<br>
	 * The number value will be converted to a property value.<br>
	 *
	 * @param value The number value to add
	 * @see #add(PropertyElement)
	 */
	public void add(@Nullable Number value) {
		this.add(value == null ? null : new PropertyValue(value));
	}
	
	/**
	 * Adds the given byte to this property array.<br>
	 * The byte will be converted to a property value.<br>
	 *
	 * @param value The byte value to add
	 * @see #add(PropertyElement)
	 */
	public void add(byte value) {
		this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds the given short to this property array.<br>
	 * The short will be converted to a property value.<br>
	 *
	 * @param value The short value to add
	 * @see #add(PropertyElement)
	 */
	public void add(short value) {
		this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds the given int to this property array.<br>
	 * The int will be converted to a property value.<br>
	 *
	 * @param value The int value to add
	 * @see #add(PropertyElement)
	 */
	public void add(int value) {
		this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds the given long to this property array.<br>
	 * The long will be converted to a property value.<br>
	 *
	 * @param value The long value to add
	 * @see #add(PropertyElement)
	 */
	public void add(long value) {
		this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds the given float to this property array.<br>
	 * The float will be converted to a property value.<br>
	 *
	 * @param value The float value to add
	 * @see #add(PropertyElement)
	 */
	public void add(float value) {
		this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds the given double to this property array.<br>
	 * The double will be converted to a property value.<br>
	 *
	 * @param value The double value to add
	 * @see #add(PropertyElement)
	 */
	public void add(double value) {
		this.add(new PropertyValue(value));
	}
	
	/**
	 * Adds all elements from the given property array to this property array.<br>
	 *
	 * @param array The property array of elements to add
	 * @throws NullPointerException If the given property array is null
	 */
	public void addAll(@NonNull PropertyArray array) {
		this.elements.addAll(Objects.requireNonNull(array, "Property array must not be null").elements);
	}
	
	/**
	 * Adds all elements from the given list to this property array.<br>
	 *
	 * @param elements The list of elements to add
	 * @throws NullPointerException If the given elements are null
	 */
	public void addAll(@NonNull List<? extends PropertyElement> elements) {
		this.elements.addAll(Objects.requireNonNull(elements, "Property elements must not be null"));
	}
	
	/**
	 * Sets the element at the given index in this property array.<br>
	 * If the element is null, it will be replaced with property null.<br>
	 *
	 * @param index The index to set
	 * @param element The element to set
	 * @return The previous element at the given index
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @Nullable PropertyElement set(int index, @Nullable PropertyElement element) {
		this.checkIndex(index);
		return this.elements.set(index, element == null ? PropertyNull.INSTANCE : element);
	}
	
	/**
	 * Sets the string at the given index in this property array.<br>
	 * If the string is null, it will be replaced with property null.<br>
	 *
	 * @param index The index to set
	 * @param value The string value to set
	 * @return The previous element at the given index
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @see #set(int, PropertyElement)
	 */
	public @Nullable PropertyElement set(int index, @Nullable String value) {
		return this.set(index, value == null ? null : new PropertyValue(value));
	}
	
	/**
	 * Sets the boolean at the given index in this property array.<br>
	 *
	 * @param index The index to set
	 * @param value The boolean value to set
	 * @return The previous element at the given index
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @see #set(int, PropertyElement)
	 */
	public @Nullable PropertyElement set(int index, boolean value) {
		return this.set(index, new PropertyValue(value));
	}
	
	/**
	 * Sets the number at the given index in this property array.<br>
	 * If the number is null, it will be replaced with property null.<br>
	 *
	 * @param index The index to set
	 * @param value The number value to set
	 * @return The previous element at the given index
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @see #set(int, PropertyElement)
	 */
	public @Nullable PropertyElement set(int index, @Nullable Number value) {
		return this.set(index, value == null ? null : new PropertyValue(value));
	}
	
	/**
	 * Removes the element at the given index from this property array.<br>
	 *
	 * @param index The index to remove
	 * @return The element that was removed
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @Nullable PropertyElement remove(int index) {
		this.checkIndex(index);
		return this.elements.remove(index);
	}
	
	/**
	 * Removes the given element from this property array.<br>
	 *
	 * @param element The element to remove
	 * @return True if the element was removed, false otherwise
	 */
	public boolean remove(@Nullable PropertyElement element) {
		return this.elements.remove(element);
	}
	
	/**
	 * Removes all elements from this property array.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	
	/**
	 * Checks if the given index is valid for this property array.<br>
	 *
	 * @param index The index to check
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	private void checkIndex(int index) {
		if (index < 0 || index >= this.elements.size()) {
			throw new PropertyArrayIndexOutOfBoundsException(index, this.elements.size());
		}
	}
	
	/**
	 * Gets the element at the given index from this property array.<br>
	 *
	 * @param index The index to get
	 * @return The element at the given index
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 */
	public @Nullable PropertyElement get(int index) {
		this.checkIndex(index);
		return this.elements.get(index);
	}
	
	/**
	 * Gets the element at the given index from this property array as a property object.<br>
	 *
	 * @param index The index to get
	 * @return The element at the given index as a property object
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws NoSuchPropertyException If no element was found at the given index
	 * @throws PropertyTypeException If the element is not a property object
	 * @see #get(int)
	 */
	public @NonNull PropertyObject getAsPropertyObject(int index) {
		PropertyElement element = this.get(index);
		
		if (element == null) {
			throw new NoSuchPropertyException("Expected property object at index " + index + ", but found none");
		}
		if (element instanceof PropertyObject object) {
			return object;
		}
		
		return element.getAsPropertyObject();
	}
	
	/**
	 * Gets the element at the given index from this property array as a property array.<br>
	 *
	 * @param index The index to get
	 * @return The element at the given index as a property array
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws NoSuchPropertyException If no element was found at the given index
	 * @throws PropertyTypeException If the element is not a property array
	 * @see #get(int)
	 */
	public @NonNull PropertyArray getAsPropertyArray(int index) {
		PropertyElement element = this.get(index);
		
		if (element == null) {
			throw new NoSuchPropertyException("Expected property array at index " + index + ", but found none");
		}
		if (element instanceof PropertyArray array) {
			return array;
		}
		
		return element.getAsPropertyArray();
	}
	
	/**
	 * Gets the element at the given index from this property array as a property value.<br>
	 *
	 * @param index The index to get
	 * @return The element at the given index as a property value
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws NoSuchPropertyException If no element was found at the given index
	 * @throws PropertyTypeException If the element is not a property value
	 * @see #get(int)
	 */
	public @NonNull PropertyValue getAsPropertyValue(int index) {
		PropertyElement element = this.get(index);
		if (element == null) {
			throw new NoSuchPropertyException("Expected property value at index " + index + ", but found none");
		}
		if (element instanceof PropertyValue value) {
			return value;
		}
		return element.getAsPropertyValue();
	}
	
	/**
	 * Gets the element at the given index from this property array as a string.<br>
	 *
	 * @param index The index to get
	 * @return The element at the given index as a string
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws NoSuchPropertyException If no element was found at the given index
	 * @throws PropertyTypeException If the element is not a string
	 * @see #getAsPropertyValue(int)
	 */
	public @NonNull String getAsString(int index) {
		return this.getAsPropertyValue(index).getAsString();
	}
	
	/**
	 * Gets the element at the given index from this property array as a boolean.<br>
	 *
	 * @param index The index to get
	 * @return The element at the given index as a boolean
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws NoSuchPropertyException If no element was found at the given index
	 * @throws PropertyTypeException If the element is not a boolean
	 * @see #getAsPropertyValue(int)
	 */
	public boolean getAsBoolean(int index) {
		return this.getAsPropertyValue(index).getAsBoolean();
	}
	
	/**
	 * Gets the element at the given index from this property array as a number.<br>
	 *
	 * @param index The index to get
	 * @return The element at the given index as a number
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws NoSuchPropertyException If no element was found at the given index
	 * @throws PropertyTypeException If the element is not a number
	 * @see #getAsPropertyValue(int)
	 */
	public @NonNull Number getAsNumber(int index) {
		return this.getAsPropertyValue(index).getAsNumber();
	}
	
	/**
	 * Gets the element at the given index from this property array as a byte.<br>
	 *
	 * @param index The index to get
	 * @return The element at the given index as a byte
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws NoSuchPropertyException If no element was found at the given index
	 * @throws PropertyTypeException If the element is not a byte
	 * @see #getAsPropertyValue(int)
	 */
	public byte getAsByte(int index) {
		return this.getAsPropertyValue(index).getAsByte();
	}
	
	/**
	 * Gets the element at the given index from this property array as a short.<br>
	 *
	 * @param index The index to get
	 * @return The element at the given index as a short
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws NoSuchPropertyException If no element was found at the given index
	 * @throws PropertyTypeException If the element is not a short
	 * @see #getAsPropertyValue(int)
	 */
	public short getAsShort(int index) {
		return this.getAsPropertyValue(index).getAsShort();
	}
	
	/**
	 * Gets the element at the given index from this property array as an integer.<br>
	 *
	 * @param index The index to get
	 * @return The element at the given index as an integer
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws NoSuchPropertyException If no element was found at the given index
	 * @throws PropertyTypeException If the element is not an integer
	 * @see #getAsPropertyValue(int)
	 */
	public int getAsInteger(int index) {
		return this.getAsPropertyValue(index).getAsInteger();
	}
	
	/**
	 * Gets the element at the given index from this property array as a long.<br>
	 *
	 * @param index The index to get
	 * @return The element at the given index as a long
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws NoSuchPropertyException If no element was found at the given index
	 * @throws PropertyTypeException If the element is not a long
	 * @see #getAsPropertyValue(int)
	 */
	public long getAsLong(int index) {
		return this.getAsPropertyValue(index).getAsLong();
	}
	
	/**
	 * Gets the element at the given index from this property array as a float.<br>
	 *
	 * @param index The index to get
	 * @return The element at the given index as a float
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws NoSuchPropertyException If no element was found at the given index
	 * @throws PropertyTypeException If the element is not a float
	 * @see #getAsPropertyValue(int)
	 */
	public float getAsFloat(int index) {
		return this.getAsPropertyValue(index).getAsFloat();
	}
	
	/**
	 * Gets the element at the given index from this property array as a double.<br>
	 *
	 * @param index The index to get
	 * @return The element at the given index as a double
	 * @throws PropertyArrayIndexOutOfBoundsException If the index is out of bounds
	 * @throws NoSuchPropertyException If no element was found at the given index
	 * @throws PropertyTypeException If the element is not a double
	 * @see #getAsPropertyValue(int)
	 */
	public double getAsDouble(int index) {
		return this.getAsPropertyValue(index).getAsDouble();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PropertyArray that)) return false;
		
		return this.elements.equals(that.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.elements);
	}
	
	@Override
	public String toString() {
		return this.toString(PropertyConfig.DEFAULT);
	}
	
	@Override
	public @NonNull String toString(@NonNull PropertyConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		StringBuilder builder = new StringBuilder();
		builder.append(config.arrayOpenChar());
		
		boolean first = true;
		for (PropertyElement element : this.elements) {
			if (!first) {
				builder.append(config.arraySeparator()).append(" ");
			}
			first = false;
			builder.append(element.toString(config));
		}
		
		builder.append(config.arrayCloseChar());
		return builder.toString();
	}
}
