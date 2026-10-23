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

import net.luis.utils.io.data.binary.exception.*;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a binary struct with a fixed number of fields.<br>
 * <p>
 *     The fields of a struct are identified by their position, the names of the fields are not written to the output.<br>
 *     This makes a struct the most compact way to store the components of an object,<br>
 *     but it requires that the fields are read in the same order they were written.
 * </p>
 * <p>
 *     A field which has not been set holds an {@link BinaryAbsent absent value}.<br>
 *     An absent value is written as a single byte and is used to represent optional values which are empty.
 * </p>
 * <p>
 *     The names of the fields are kept in memory as long as they are known,<br>
 *     they are only used for debugging and to support name based lookups during encoding.<br>
 *     A struct which was read from an input does not know the names of its fields.
 * </p>
 *
 * @author Luis-St
 */
public class BinaryStruct implements BinaryElement, Iterable<BinaryElement> {
	
	/**
	 * The values of the fields of this struct.<br>
	 */
	private final List<BinaryElement> values;
	/**
	 * The names of the fields of this struct.<br>
	 * A name is null if it is unknown.<br>
	 */
	private final List<String> names;
	
	/**
	 * Constructs a new binary struct with the given number of fields.<br>
	 * All fields are initialized with an {@link BinaryAbsent absent value}.<br>
	 *
	 * @param fieldCount The number of fields of the struct
	 * @throws IllegalArgumentException If the field count is negative
	 */
	public BinaryStruct(int fieldCount) {
		if (0 > fieldCount) {
			throw new IllegalArgumentException("Field count must not be negative, but was " + fieldCount);
		}
		
		this.values = new ArrayList<>(Collections.nCopies(fieldCount, BinaryAbsent.INSTANCE));
		this.names = new ArrayList<>(Collections.nCopies(fieldCount, null));
	}
	
	/**
	 * Constructs a new binary struct with the given field values.<br>
	 * The names of the fields are unknown.<br>
	 *
	 * @param values The values of the fields
	 * @throws NullPointerException If the values are null
	 */
	public BinaryStruct(@NonNull List<? extends BinaryElement> values) {
		Objects.requireNonNull(values, "Values must not be null");
		
		this.values = new ArrayList<>(values);
		this.names = new ArrayList<>(Collections.nCopies(values.size(), null));
	}
	
	/**
	 * Constructs a new binary struct with the given field values.<br>
	 * The names of the fields are unknown.<br>
	 *
	 * @param values The values of the fields
	 * @throws NullPointerException If the values are null
	 */
	public BinaryStruct(BinaryElement @NonNull ... values) {
		this(Arrays.asList(Objects.requireNonNull(values, "Values must not be null")));
	}
	
	/**
	 * Validates that the given index is within the bounds of this struct.<br>
	 *
	 * @param index The index to validate
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	private void validateIndex(int index) {
		if (0 > index) {
			throw new BinaryIndexOutOfBoundsException(index);
		}
		if (index >= this.values.size()) {
			throw new BinaryIndexOutOfBoundsException(index, this.values.size());
		}
	}
	
	/**
	 * Returns the value of the field at the given index.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 */
	private @NonNull BinaryElement getRequired(int index) {
		BinaryElement value = this.get(index);
		if (value.isBinaryAbsent()) {
			throw new NoSuchBinaryElementException("Expected a binary element for field " + index + ", but the field is absent");
		}
		return value;
	}
	
	@Override
	public @NonNull BinaryType getType() {
		return BinaryType.STRUCT;
	}
	
	/**
	 * Returns the number of fields of this struct.<br>
	 * @return The size of this struct
	 */
	public int size() {
		return this.values.size();
	}
	
	/**
	 * Checks if this struct has no fields.<br>
	 * @return True if this struct is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.values.isEmpty();
	}
	
	/**
	 * Returns the number of fields of this struct which are present.<br>
	 * @return The number of present fields
	 */
	public int presentFields() {
		return (int) this.values.stream().filter(value -> !value.isBinaryAbsent()).count();
	}
	
	/**
	 * Checks if this struct contains the given value.<br>
	 *
	 * @param value The value to check
	 * @return True if this struct contains the value, false otherwise
	 */
	public boolean contains(@Nullable BinaryElement value) {
		return this.values.contains(value);
	}
	
	@Override
	public @NonNull Iterator<BinaryElement> iterator() {
		return this.values.iterator();
	}
	
	/**
	 * Returns a sequential stream over the values of the fields of this struct.<br>
	 * @return The stream of the values
	 */
	public @NonNull Stream<BinaryElement> stream() {
		return this.values.stream();
	}
	
	/**
	 * Returns the values of the fields of this struct.<br>
	 * @return An unmodifiable list of the values
	 */
	public @NonNull @Unmodifiable List<BinaryElement> getValues() {
		return List.copyOf(this.values);
	}
	
	/**
	 * Returns the names of the fields of this struct.<br>
	 * The names of the fields which are unknown are represented as an empty optional.<br>
	 *
	 * @return An unmodifiable list of the names
	 */
	public @NonNull @Unmodifiable List<Optional<String>> getNames() {
		return this.names.stream().map(Optional::ofNullable).toList();
	}
	
	/**
	 * Checks if the field at the given index is present.<br>
	 * A field is present if its value is not an {@link BinaryAbsent absent value}.<br>
	 *
	 * @param index The index of the field
	 * @return True if the field is present, false otherwise
	 */
	public boolean has(int index) {
		if (0 > index || index >= this.values.size()) {
			return false;
		}
		return !this.values.get(index).isBinaryAbsent();
	}
	
	/**
	 * Returns the name of the field at the given index.<br>
	 *
	 * @param index The index of the field
	 * @return The name of the field or an empty optional if the name is unknown
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull Optional<String> getName(int index) {
		this.validateIndex(index);
		
		return Optional.ofNullable(this.names.get(index));
	}
	
	/**
	 * Checks if the names of the fields of this struct are known.<br>
	 * A struct which was read from an input does not know the names of its fields.<br>
	 *
	 * @return True if at least one field name is known, false otherwise
	 */
	public boolean hasNames() {
		return this.names.stream().anyMatch(Objects::nonNull);
	}
	
	/**
	 * Returns the index of the field with the given name.<br>
	 *
	 * @param name The name of the field
	 * @return The index of the field or {@code -1} if no field with the given name is known
	 */
	public int indexOf(@Nullable String name) {
		if (name == null) {
			return -1;
		}
		return this.names.indexOf(name);
	}
	
	/**
	 * Sets the value of the field at the given index.<br>
	 * If the value is null, a {@link BinaryNull null value} is set instead.<br>
	 *
	 * @param index The index of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, @Nullable BinaryElement value) {
		this.validateIndex(index);
		
		return this.values.set(index, value == null ? BinaryNull.INSTANCE : value);
	}
	
	/**
	 * Sets the value of the field at the given index to the given boolean value.<br>
	 *
	 * @param index The index of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, boolean value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value of the field at the given index to the given byte value.<br>
	 *
	 * @param index The index of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, byte value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value of the field at the given index to the given short value.<br>
	 *
	 * @param index The index of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, short value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value of the field at the given index to the given integer value.<br>
	 *
	 * @param index The index of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, int value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value of the field at the given index to the given long value.<br>
	 *
	 * @param index The index of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, long value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value of the field at the given index to the given float value.<br>
	 *
	 * @param index The index of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, float value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value of the field at the given index to the given double value.<br>
	 *
	 * @param index The index of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, double value) {
		return this.set(index, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value of the field at the given index to the given number value.<br>
	 * If the value is null, a {@link BinaryNull null value} is set instead.<br>
	 *
	 * @param index The index of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws BinaryTypeException If the type of the number is not supported
	 */
	public @NonNull BinaryElement set(int index, @Nullable Number value) {
		return this.set(index, value == null ? BinaryNull.INSTANCE : new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value of the field at the given index to the given string value.<br>
	 * If the value is null, a {@link BinaryNull null value} is set instead.<br>
	 *
	 * @param index The index of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, @Nullable String value) {
		return this.set(index, value == null ? BinaryNull.INSTANCE : new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value and the name of the field at the given index.<br>
	 * If the value is null, a {@link BinaryNull null value} is set instead.<br>
	 *
	 * @param index The index of the field
	 * @param name The name of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws NullPointerException If the name is null
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, @NonNull String name, @Nullable BinaryElement value) {
		Objects.requireNonNull(name, "Name must not be null");
		this.validateIndex(index);
		
		this.names.set(index, name);
		return this.set(index, value);
	}
	
	/**
	 * Sets the value and the name of the field at the given index to the given boolean value.<br>
	 *
	 * @param index The index of the field
	 * @param name The name of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws NullPointerException If the name is null
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, @NonNull String name, boolean value) {
		return this.set(index, name, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value and the name of the field at the given index to the given byte value.<br>
	 *
	 * @param index The index of the field
	 * @param name The name of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws NullPointerException If the name is null
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, @NonNull String name, byte value) {
		return this.set(index, name, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value and the name of the field at the given index to the given short value.<br>
	 *
	 * @param index The index of the field
	 * @param name The name of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws NullPointerException If the name is null
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, @NonNull String name, short value) {
		return this.set(index, name, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value and the name of the field at the given index to the given integer value.<br>
	 *
	 * @param index The index of the field
	 * @param name The name of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws NullPointerException If the name is null
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, @NonNull String name, int value) {
		return this.set(index, name, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value and the name of the field at the given index to the given long value.<br>
	 *
	 * @param index The index of the field
	 * @param name The name of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws NullPointerException If the name is null
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, @NonNull String name, long value) {
		return this.set(index, name, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value and the name of the field at the given index to the given float value.<br>
	 *
	 * @param index The index of the field
	 * @param name The name of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws NullPointerException If the name is null
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, @NonNull String name, float value) {
		return this.set(index, name, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value and the name of the field at the given index to the given double value.<br>
	 *
	 * @param index The index of the field
	 * @param name The name of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws NullPointerException If the name is null
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, @NonNull String name, double value) {
		return this.set(index, name, new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value and the name of the field at the given index to the given number value.<br>
	 * If the value is null, a {@link BinaryNull null value} is set instead.<br>
	 *
	 * @param index The index of the field
	 * @param name The name of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws NullPointerException If the name is null
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws BinaryTypeException If the type of the number is not supported
	 */
	public @NonNull BinaryElement set(int index, @NonNull String name, @Nullable Number value) {
		return this.set(index, name, value == null ? BinaryNull.INSTANCE : new BinaryPrimitive(value));
	}
	
	/**
	 * Sets the value and the name of the field at the given index to the given string value.<br>
	 * If the value is null, a {@link BinaryNull null value} is set instead.<br>
	 *
	 * @param index The index of the field
	 * @param name The name of the field
	 * @param value The value to set
	 * @return The value which was replaced
	 * @throws NullPointerException If the name is null
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement set(int index, @NonNull String name, @Nullable String value) {
		return this.set(index, name, value == null ? BinaryNull.INSTANCE : new BinaryPrimitive(value));
	}
	
	/**
	 * Removes the value of the field at the given index.<br>
	 * The field is set to an {@link BinaryAbsent absent value}, the size of the struct does not change.<br>
	 *
	 * @param index The index of the field
	 * @return The value which was removed
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement remove(int index) {
		this.validateIndex(index);
		
		return this.values.set(index, BinaryAbsent.INSTANCE);
	}
	
	/**
	 * Removes the values of all fields of this struct.<br>
	 * All fields are set to an {@link BinaryAbsent absent value}, the size of the struct does not change.<br>
	 */
	public void clear() {
		this.values.replaceAll(_ -> BinaryAbsent.INSTANCE);
	}
	
	/**
	 * Returns the value of the field at the given index.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field or an {@link BinaryAbsent absent value} if the field was not set
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 */
	public @NonNull BinaryElement get(int index) {
		this.validateIndex(index);
		
		return this.values.get(index);
	}
	
	/**
	 * Returns the value of the field at the given index or the given default value.<br>
	 *
	 * @param index The index of the field
	 * @param defaultValue The value to return if the field is not present
	 * @return The value of the field or the default value if the field is not present
	 */
	public @Nullable BinaryElement getOrDefault(int index, @Nullable BinaryElement defaultValue) {
		if (!this.has(index)) {
			return defaultValue;
		}
		return this.values.get(index);
	}
	
	/**
	 * Returns the value of the field at the given index as a binary array.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field as a binary array
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 * @throws BinaryTypeException If the value of the field is not a binary array
	 * @see #get(int)
	 */
	public @NonNull BinaryArray getAsBinaryArray(int index) {
		return this.getRequired(index).getAsBinaryArray();
	}
	
	/**
	 * Returns the value of the field at the given index as a binary struct.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field as a binary struct
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 * @throws BinaryTypeException If the value of the field is not a binary struct
	 * @see #get(int)
	 */
	public @NonNull BinaryStruct getAsBinaryStruct(int index) {
		return this.getRequired(index).getAsBinaryStruct();
	}
	
	/**
	 * Returns the value of the field at the given index as a binary map.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field as a binary map
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 * @throws BinaryTypeException If the value of the field is not a binary map
	 * @see #get(int)
	 */
	public @NonNull BinaryMap getAsBinaryMap(int index) {
		return this.getRequired(index).getAsBinaryMap();
	}
	
	/**
	 * Returns the value of the field at the given index as a binary primitive.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field as a binary primitive
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 * @throws BinaryTypeException If the value of the field is not a binary primitive
	 * @see #get(int)
	 */
	public @NonNull BinaryPrimitive getAsBinaryPrimitive(int index) {
		return this.getRequired(index).getAsBinaryPrimitive();
	}
	
	/**
	 * Returns the value of the field at the given index as a boolean value.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field as a boolean value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 * @throws BinaryTypeException If the value of the field is not a binary boolean
	 * @see #get(int)
	 */
	public boolean getAsBoolean(int index) {
		return this.getRequired(index).getAsBoolean();
	}
	
	/**
	 * Returns the value of the field at the given index as a number value.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field as a number value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 * @throws BinaryTypeException If the value of the field is not a binary number
	 * @see #get(int)
	 */
	public @NonNull Number getAsNumber(int index) {
		return this.getRequired(index).getAsNumber();
	}
	
	/**
	 * Returns the value of the field at the given index as a byte value.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field as a byte value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 * @throws BinaryTypeException If the value of the field is not a binary number
	 * @see #get(int)
	 */
	public byte getAsByte(int index) {
		return this.getRequired(index).getAsByte();
	}
	
	/**
	 * Returns the value of the field at the given index as a short value.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field as a short value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 * @throws BinaryTypeException If the value of the field is not a binary number
	 * @see #get(int)
	 */
	public short getAsShort(int index) {
		return this.getRequired(index).getAsShort();
	}
	
	/**
	 * Returns the value of the field at the given index as an integer value.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field as an integer value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 * @throws BinaryTypeException If the value of the field is not a binary number
	 * @see #get(int)
	 */
	public int getAsInteger(int index) {
		return this.getRequired(index).getAsInteger();
	}
	
	/**
	 * Returns the value of the field at the given index as a long value.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field as a long value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 * @throws BinaryTypeException If the value of the field is not a binary number
	 * @see #get(int)
	 */
	public long getAsLong(int index) {
		return this.getRequired(index).getAsLong();
	}
	
	/**
	 * Returns the value of the field at the given index as a float value.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field as a float value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 * @throws BinaryTypeException If the value of the field is not a binary number
	 * @see #get(int)
	 */
	public float getAsFloat(int index) {
		return this.getRequired(index).getAsFloat();
	}
	
	/**
	 * Returns the value of the field at the given index as a double value.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field as a double value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 * @throws BinaryTypeException If the value of the field is not a binary number
	 * @see #get(int)
	 */
	public double getAsDouble(int index) {
		return this.getRequired(index).getAsDouble();
	}
	
	/**
	 * Returns the value of the field at the given index as a string value.<br>
	 *
	 * @param index The index of the field
	 * @return The value of the field as a string value
	 * @throws BinaryIndexOutOfBoundsException If the index is negative or greater than or equal to the size of this struct
	 * @throws NoSuchBinaryElementException If the field is not present
	 * @throws BinaryTypeException If the value of the field is not a binary string
	 * @see #get(int)
	 */
	public @NonNull String getAsString(int index) {
		return this.getRequired(index).getAsString();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BinaryStruct that)) return false;
		
		return this.values.equals(that.values);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.values);
	}
	
	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "(", ")");
		for (int i = 0; i < this.values.size(); i++) {
			String name = this.names.get(i);
			joiner.add((name == null ? String.valueOf(i) : name) + ": " + this.values.get(i));
		}
		return joiner.toString();
	}
	//endregion
}
