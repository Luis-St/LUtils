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

import net.luis.utils.io.data.binary.exception.BinaryIndexOutOfBoundsException;
import net.luis.utils.io.data.binary.exception.BinaryTypeException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a binary array of elements.<br>
 * The elements of the array are stored in the order they were added.<br>
 * The elements of an array do not need to be of the same type, the type of each element is written to the output.
 *
 *  @author Luis-St
 */
public class BinaryArray implements BinaryElement, Iterable<BinaryElement> {
	
	/**
	 * The internal list of elements of this array.<br>
	 */
	private final List<BinaryElement> elements = new ArrayList<>();
	
	/**
	 * Constructs an empty binary array.<br>
	 */
	public BinaryArray() {}
	
	/**
	 * Constructs a binary array with the given elements.<br>
	 *
	 * @param elements The elements to add to the array
	 * @throws NullPointerException If the elements are null
	 */
	public BinaryArray(@NonNull List<? extends BinaryElement> elements) {
		this.elements.addAll(Objects.requireNonNull(elements, "Elements must not be null"));
	}
	
	/**
	 * Constructs a binary array with the given elements.<br>
	 *
	 * @param elements The elements to add to the array
	 * @throws NullPointerException If the elements are null
	 */
	public BinaryArray(BinaryElement @NonNull ... elements) {
		this.elements.addAll(Arrays.asList(Objects.requireNonNull(elements, "Elements must not be null")));
	}
	
	/**
	 * Validates that the given index is within the bounds of this array.<br>
	 *
	 * @param index The index to validate
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 */
	private void validateIndex(int index) {
		if (0 > index) {
			throw new BinaryIndexOutOfBoundsException(index);
		}
		if (index >= this.elements.size()) {
			throw new BinaryIndexOutOfBoundsException(index, this.elements.size());
		}
	}
	
	@Override
	public @NonNull BinaryType getType() {
		return BinaryType.LIST;
	}
	
	/**
	 * Returns the number of elements in this array.<br>
	 * @return The size of this array
	 */
	public int size() {
		return this.elements.size();
	}
	
	/**
	 * Checks if this array contains no elements.<br>
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
	public boolean contains(@Nullable BinaryElement element) {
		return this.elements.contains(element);
	}
	
	@Override
	public @NonNull Iterator<BinaryElement> iterator() {
		return this.elements.iterator();
	}
	
	/**
	 * Returns a sequential stream over the elements of this array.<br>
	 * @return The stream of the elements
	 */
	public @NonNull Stream<BinaryElement> stream() {
		return this.elements.stream();
	}
	
	/**
	 * Returns the elements of this array.<br>
	 * @return An unmodifiable list of the elements
	 */
	public @NonNull @Unmodifiable List<BinaryElement> getElements() {
		return List.copyOf(this.elements);
	}
	
	/**
	 * Replaces the element at the given index with the given element.<br>
	 * If the element is null, a {@link BinaryNull null value} is set instead.<br>
	 *
	 * @param index The index of the element to replace
	 * @param element The element to set
	 * @return The element which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 */
	public @NonNull BinaryElement set(int index, @Nullable BinaryElement element) {
		this.validateIndex(index);
		
		return this.elements.set(index, element == null ? BinaryNull.INSTANCE : element);
	}
	
	/**
	 * Replaces the element at the given index with the given boolean value.<br>
	 *
	 * @param index The index of the element to replace
	 * @param value The value to set
	 * @return The element which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 */
	public @NonNull BinaryElement set(int index, boolean value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Replaces the element at the given index with the given byte value.<br>
	 *
	 * @param index The index of the element to replace
	 * @param value The value to set
	 * @return The element which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 */
	public @NonNull BinaryElement set(int index, byte value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Replaces the element at the given index with the given short value.<br>
	 *
	 * @param index The index of the element to replace
	 * @param value The value to set
	 * @return The element which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 */
	public @NonNull BinaryElement set(int index, short value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Replaces the element at the given index with the given integer value.<br>
	 *
	 * @param index The index of the element to replace
	 * @param value The value to set
	 * @return The element which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 */
	public @NonNull BinaryElement set(int index, int value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Replaces the element at the given index with the given long value.<br>
	 *
	 * @param index The index of the element to replace
	 * @param value The value to set
	 * @return The element which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 */
	public @NonNull BinaryElement set(int index, long value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Replaces the element at the given index with the given float value.<br>
	 *
	 * @param index The index of the element to replace
	 * @param value The value to set
	 * @return The element which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 */
	public @NonNull BinaryElement set(int index, float value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Replaces the element at the given index with the given double value.<br>
	 *
	 * @param index The index of the element to replace
	 * @param value The value to set
	 * @return The element which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 */
	public @NonNull BinaryElement set(int index, double value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Replaces the element at the given index with the given number value.<br>
	 * If the value is null, a {@link BinaryNull null value} is set instead.<br>
	 *
	 * @param index The index of the element to replace
	 * @param value The value to set
	 * @return The element which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the type of the number is not supported
	 */
	public @NonNull BinaryElement set(int index, @Nullable Number value) {
		return this.set(index, value == null ? BinaryNull.INSTANCE : new BinaryPrimitive(value));
	}
	
	/**
	 * Replaces the element at the given index with the given string value.<br>
	 * If the value is null, a {@link BinaryNull null value} is set instead.<br>
	 *
	 * @param index The index of the element to replace
	 * @param value The value to set
	 * @return The element which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 */
	public @NonNull BinaryElement set(int index, @Nullable String value) {
		return this.set(index, value == null ? BinaryNull.INSTANCE : new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given element to the end of this array.<br>
	 * If the element is null, a {@link BinaryNull null value} is added instead.<br>
	 *
	 * @param element The element to add
	 */
	public void add(@Nullable BinaryElement element) {
		this.elements.add(element == null ? BinaryNull.INSTANCE : element);
	}
	
	/**
	 * Adds the given boolean value to the end of this array.<br>
	 * @param value The value to add
	 */
	public void add(boolean value) {
		this.add(new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given byte value to the end of this array.<br>
	 * @param value The value to add
	 */
	public void add(byte value) {
		this.add(new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given short value to the end of this array.<br>
	 * @param value The value to add
	 */
	public void add(short value) {
		this.add(new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given integer value to the end of this array.<br>
	 * @param value The value to add
	 */
	public void add(int value) {
		this.add(new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given long value to the end of this array.<br>
	 * @param value The value to add
	 */
	public void add(long value) {
		this.add(new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given float value to the end of this array.<br>
	 * @param value The value to add
	 */
	public void add(float value) {
		this.add(new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given double value to the end of this array.<br>
	 * @param value The value to add
	 */
	public void add(double value) {
		this.add(new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given number value to the end of this array.<br>
	 * If the value is null, a {@link BinaryNull null value} is added instead.<br>
	 *
	 * @param value The value to add
	 * @throws BinaryTypeException If the type of the number is not supported
	 */
	public void add(@Nullable Number value) {
		this.add(value == null ? BinaryNull.INSTANCE : new BinaryPrimitive(value));
	}
	
	/**
	 * Adds the given string value to the end of this array.<br>
	 * If the value is null, a {@link BinaryNull null value} is added instead.<br>
	 *
	 * @param value The value to add
	 */
	public void add(@Nullable String value) {
		this.add(value == null ? BinaryNull.INSTANCE : new BinaryPrimitive(value));
	}
	
	/**
	 * Adds all elements of the given array to the end of this array.<br>
	 *
	 * @param array The array with the elements to add
	 * @throws NullPointerException If the array is null
	 */
	public void addAll(@NonNull BinaryArray array) {
		Objects.requireNonNull(array, "Array must not be null");
		
		this.elements.addAll(array.elements);
	}
	
	/**
	 * Adds all given elements to the end of this array.<br>
	 *
	 * @param elements The elements to add
	 * @throws NullPointerException If the elements are null
	 */
	public void addAll(@NonNull List<? extends BinaryElement> elements) {
		Objects.requireNonNull(elements, "Elements must not be null");
		
		elements.forEach(this::add);
	}
	
	/**
	 * Adds all given elements to the end of this array.<br>
	 *
	 * @param elements The elements to add
	 * @throws NullPointerException If the elements are null
	 */
	public void addAll(BinaryElement @NonNull ... elements) {
		Objects.requireNonNull(elements, "Elements must not be null");
		
		for (BinaryElement element : elements) {
			this.add(element);
		}
	}
	
	/**
	 * Removes the element at the given index.<br>
	 *
	 * @param index The index of the element to remove
	 * @return The removed element
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 */
	public @NonNull BinaryElement remove(int index) {
		this.validateIndex(index);
		
		return this.elements.remove(index);
	}
	
	/**
	 * Removes the given element from this array.<br>
	 *
	 * @param element The element to remove
	 * @return True if the element was removed, false otherwise
	 */
	public boolean remove(@Nullable BinaryElement element) {
		return this.elements.remove(element);
	}
	
	/**
	 * Removes all elements from this array.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	
	/**
	 * Returns the element at the given index.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 */
	public @NonNull BinaryElement get(int index) {
		this.validateIndex(index);
		
		return this.elements.get(index);
	}
	
	/**
	 * Returns the element at the given index as a binary array.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index as a binary array
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the element at the given index is not a binary array
	 * @see #get(int)
	 */
	public @NonNull BinaryArray getAsBinaryArray(int index) {
		return this.get(index).getAsBinaryArray();
	}
	
	/**
	 * Returns the element at the given index as a binary struct.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index as a binary struct
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the element at the given index is not a binary struct
	 * @see #get(int)
	 */
	public @NonNull BinaryStruct getAsBinaryStruct(int index) {
		return this.get(index).getAsBinaryStruct();
	}
	
	/**
	 * Returns the element at the given index as a binary map.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index as a binary map
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the element at the given index is not a binary map
	 * @see #get(int)
	 */
	public @NonNull BinaryMap getAsBinaryMap(int index) {
		return this.get(index).getAsBinaryMap();
	}
	
	/**
	 * Returns the element at the given index as a binary primitive.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index as a binary primitive
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the element at the given index is not a binary primitive
	 * @see #get(int)
	 */
	public @NonNull BinaryPrimitive getAsBinaryPrimitive(int index) {
		return this.get(index).getAsBinaryPrimitive();
	}
	
	/**
	 * Returns the element at the given index as a boolean value.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index as a boolean value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the element at the given index is not a binary boolean
	 * @see #get(int)
	 */
	public boolean getAsBoolean(int index) {
		return this.get(index).getAsBoolean();
	}
	
	/**
	 * Returns the element at the given index as a number value.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index as a number value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the element at the given index is not a binary number
	 * @see #get(int)
	 */
	public @NonNull Number getAsNumber(int index) {
		return this.get(index).getAsNumber();
	}
	
	/**
	 * Returns the element at the given index as a byte value.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index as a byte value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the element at the given index is not a binary number
	 * @see #get(int)
	 */
	public byte getAsByte(int index) {
		return this.get(index).getAsByte();
	}
	
	/**
	 * Returns the element at the given index as a short value.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index as a short value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the element at the given index is not a binary number
	 * @see #get(int)
	 */
	public short getAsShort(int index) {
		return this.get(index).getAsShort();
	}
	
	/**
	 * Returns the element at the given index as an integer value.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index as an integer value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the element at the given index is not a binary number
	 * @see #get(int)
	 */
	public int getAsInteger(int index) {
		return this.get(index).getAsInteger();
	}
	
	/**
	 * Returns the element at the given index as a long value.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index as a long value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the element at the given index is not a binary number
	 * @see #get(int)
	 */
	public long getAsLong(int index) {
		return this.get(index).getAsLong();
	}
	
	/**
	 * Returns the element at the given index as a float value.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index as a float value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the element at the given index is not a binary number
	 * @see #get(int)
	 */
	public float getAsFloat(int index) {
		return this.get(index).getAsFloat();
	}
	
	/**
	 * Returns the element at the given index as a double value.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index as a double value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the element at the given index is not a binary number
	 * @see #get(int)
	 */
	public double getAsDouble(int index) {
		return this.get(index).getAsDouble();
	}
	
	/**
	 * Returns the element at the given index as a string value.<br>
	 *
	 * @param index The index of the element
	 * @return The element at the given index as a string value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this array
	 * @throws BinaryTypeException If the element at the given index is not a binary string
	 * @see #get(int)
	 */
	public @NonNull String getAsString(int index) {
		return this.get(index).getAsString();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BinaryArray that)) return false;
		
		return this.elements.equals(that.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.elements);
	}
	
	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "[", "]");
		this.elements.forEach(element -> joiner.add(String.valueOf(element)));
		return joiner.toString();
	}
	//endregion
}
