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

package net.luis.utils.io.codec.provider;

import net.luis.utils.annotation.type.Singleton;
import net.luis.utils.io.codec.FieldRef;
import net.luis.utils.io.data.binary.*;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * Type provider implementation for binary elements.<br>
 * This class is a singleton and should be accessed through the {@link #INSTANCE} constant.<br>
 * <p>
 *     The binary format is the most compact format of all provided formats.<br>
 *     The components of an object are stored in a {@link BinaryStruct struct} which identifies its fields by their position,<br>
 *     therefore the field names are not written to the output.
 * </p>
 * <p>
 *     Optional values which are empty are stored as an {@link BinaryAbsent absent value},<br>
 *     nullable values which are null are stored as a {@link BinaryNull null value}.<br>
 *     Both are written as a single byte.
 * </p>
 * <p>
 *     A struct which was read from an input does not know the names of its fields,<br>
 *     therefore fields of a decoded struct can only be accessed by their position.<br>
 *     Codecs which look up a field by its name, like the discriminated codecs, are not supported when decoding binary data.
 * </p>
 *
 * @author Luis-St
 */
@Singleton
public final class BinaryTypeProvider implements TypeProvider<BinaryElement> {
	
	/**
	 * An empty binary element instance.<br>
	 * Used for internal purposes only.<br>
	 * The binary element can not be written to an output.<br>
	 */
	private static final BinaryElement EMPTY_ELEMENT = () -> BinaryType.ABSENT;
	
	/**
	 * The singleton instance of this class.<br>
	 */
	public static final BinaryTypeProvider INSTANCE = new BinaryTypeProvider();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private BinaryTypeProvider() {}
	
	@Override
	public @NonNull BinaryElement empty() {
		return EMPTY_ELEMENT;
	}
	
	@Override
	public <X extends Exception> @NonNull BinaryElement createNull(@NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return BinaryNull.INSTANCE;
	}
	
	@Override
	public <X extends Exception> @NonNull BinaryElement createBoolean(boolean value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new BinaryPrimitive(value);
	}
	
	@Override
	public <X extends Exception> @NonNull BinaryElement createByte(byte value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new BinaryPrimitive(value);
	}
	
	@Override
	public <X extends Exception> @NonNull BinaryElement createShort(short value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new BinaryPrimitive(value);
	}
	
	@Override
	public <X extends Exception> @NonNull BinaryElement createInteger(int value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new BinaryPrimitive(value);
	}
	
	@Override
	public <X extends Exception> @NonNull BinaryElement createLong(long value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new BinaryPrimitive(value);
	}
	
	@Override
	public <X extends Exception> @NonNull BinaryElement createFloat(float value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new BinaryPrimitive(value);
	}
	
	@Override
	public <X extends Exception> @NonNull BinaryElement createDouble(double value, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new BinaryPrimitive(value);
	}
	
	@Override
	public <X extends Exception> @NonNull BinaryElement createString(@Nullable String value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid string");
		}
		
		return new BinaryPrimitive(value);
	}
	
	@Override
	public <X extends Exception> @NonNull BinaryElement createList(@Nullable List<? extends BinaryElement> values, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (values == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid list");
		}
		
		return new BinaryArray(values);
	}
	
	@Override
	public <X extends Exception> @NonNull BinaryElement createMap(@NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		return new BinaryMap();
	}
	
	@Override
	public <X extends Exception> @NonNull BinaryElement createMap(@Nullable Map<String, ? extends BinaryElement> values, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (values == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		
		return new BinaryMap(values);
	}
	
	@Override
	public <X extends Exception> boolean isEmpty(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		
		return type == EMPTY_ELEMENT;
	}
	
	@Override
	public <X extends Exception> boolean isNull(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		
		return type.isBinaryNull();
	}
	
	@Override
	public <X extends Exception> @NonNull Boolean getBoolean(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		BinaryPrimitive primitive = this.getPrimitive(type, "boolean", exceptionConstructor);
		if (!primitive.isBinaryBoolean()) {
			throw exceptionConstructor.apply("Value '" + type + "' is not a binary boolean");
		}
		return primitive.getAsBoolean();
	}
	
	@Override
	public <X extends Exception> @NonNull Byte getByte(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		return this.getNumber(type, "byte", exceptionConstructor).byteValue();
	}
	
	@Override
	public <X extends Exception> @NonNull Short getShort(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		return this.getNumber(type, "short", exceptionConstructor).shortValue();
	}
	
	@Override
	public <X extends Exception> @NonNull Integer getInteger(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		return this.getNumber(type, "integer", exceptionConstructor).intValue();
	}
	
	@Override
	public <X extends Exception> @NonNull Long getLong(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		return this.getNumber(type, "long", exceptionConstructor).longValue();
	}
	
	@Override
	public <X extends Exception> @NonNull Float getFloat(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		return this.getNumber(type, "float", exceptionConstructor).floatValue();
	}
	
	@Override
	public <X extends Exception> @NonNull Double getDouble(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		return this.getNumber(type, "double", exceptionConstructor).doubleValue();
	}
	
	@Override
	public <X extends Exception> @NonNull String getString(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		BinaryPrimitive primitive = this.getPrimitive(type, "string", exceptionConstructor);
		if (!primitive.isBinaryString()) {
			throw exceptionConstructor.apply("Value '" + type + "' is not a binary string");
		}
		return primitive.getAsString();
	}
	
	@Override
	public <X extends Exception> @NonNull List<BinaryElement> getList(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid list");
		}
		
		if (!type.isBinaryArray()) {
			throw exceptionConstructor.apply("Value '" + type + "' is not a binary list");
		}
		return type.getAsBinaryArray().getElements();
	}
	
	@Override
	public <X extends Exception> @NonNull Map<String, BinaryElement> getMap(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		
		if (!type.isBinaryMap()) {
			throw exceptionConstructor.apply("Value '" + type + "' is not a binary map");
		}
		return new LinkedHashMap<>(type.getAsBinaryMap().getElements());
	}
	
	@Override
	public <X extends Exception> boolean has(@Nullable BinaryElement type, @Nullable String key, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (key == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid key");
		}
		
		if (type instanceof BinaryStruct struct) {
			return struct.has(this.getNamedIndex(struct, key, exceptionConstructor));
		}
		return this.getAsMap(type, exceptionConstructor).containsKey(key);
	}
	
	@Override
	public <X extends Exception> @Nullable BinaryElement get(@Nullable BinaryElement type, @Nullable String key, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (key == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid key");
		}
		
		if (type instanceof BinaryStruct struct) {
			int index = this.getNamedIndex(struct, key, exceptionConstructor);
			return struct.has(index) ? struct.get(index) : null;
		}
		return this.getAsMap(type, exceptionConstructor).get(key);
	}
	
	@Override
	public <X extends Exception> void set(@Nullable BinaryElement type, @Nullable String key, @Nullable BinaryElement value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (key == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid key");
		}
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		
		if (type instanceof BinaryStruct struct) {
			struct.set(this.getNamedIndex(struct, key, exceptionConstructor), key, value);
			return;
		}
		this.getAsMap(type, exceptionConstructor).add(key, value);
	}
	
	@Override
	public <X extends Exception> @UnknownNullability BinaryElement merge(@Nullable BinaryElement current, @Nullable BinaryElement value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (current == null) {
			return value;
		}
		if (value == null) {
			return current;
		}
		if (current == EMPTY_ELEMENT || current.isBinaryNull() || current.isBinaryAbsent()) {
			return value;
		}
		if (value == EMPTY_ELEMENT || value.isBinaryNull() || value.isBinaryAbsent()) {
			return current;
		}
		
		if (current.isBinaryArray() && value.isBinaryArray()) {
			current.getAsBinaryArray().addAll(value.getAsBinaryArray());
			return current;
		}
		if (current.isBinaryMap() && value.isBinaryMap()) {
			current.getAsBinaryMap().addAll(value.getAsBinaryMap());
			return current;
		}
		if (current.isBinaryStruct() && value.isBinaryStruct()) {
			return this.mergeStruct(current.getAsBinaryStruct(), value.getAsBinaryStruct());
		}
		throw exceptionConstructor.apply("Unable to merge '" + current + "' with '" + value + "'");
	}
	
	@Override
	public <X extends Exception> @NonNull BinaryElement createStruct(int fieldCount, @NonNull Function<String, X> exceptionConstructor) {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (0 > fieldCount) {
			throw new IllegalArgumentException("Field count must not be negative, but was " + fieldCount);
		}
		
		return new BinaryStruct(fieldCount);
	}
	
	@Override
	public <X extends Exception> void validateStruct(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid struct");
		}
		
		if (!type.isBinaryStruct() && !type.isBinaryMap()) {
			throw exceptionConstructor.apply("Value '" + type + "' is not a binary struct");
		}
	}
	
	@Override
	public <X extends Exception> void setField(@Nullable BinaryElement type, @NonNull FieldRef field, @Nullable BinaryElement value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		Objects.requireNonNull(field, "Field must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not valid");
		}
		
		if (type instanceof BinaryStruct struct) {
			struct.set(this.getIndex(struct, field, exceptionConstructor), field.name(), value);
		} else {
			this.set(type, field.name(), value, exceptionConstructor);
		}
	}
	
	@Override
	public <X extends Exception> boolean hasField(@Nullable BinaryElement type, @NonNull FieldRef field, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		Objects.requireNonNull(field, "Field must not be null");
		
		if (type instanceof BinaryStruct struct) {
			return struct.has(this.getIndex(struct, field, exceptionConstructor));
		}
		return TypeProvider.super.hasField(type, field, exceptionConstructor);
	}
	
	@Override
	public <X extends Exception> @Nullable BinaryElement getField(@Nullable BinaryElement type, @NonNull FieldRef field, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		Objects.requireNonNull(field, "Field must not be null");
		
		if (type instanceof BinaryStruct struct) {
			int index = this.getIndex(struct, field, exceptionConstructor);
			return struct.has(index) ? struct.get(index) : null;
		}
		return TypeProvider.super.getField(type, field, exceptionConstructor);
	}
	
	/**
	 * Returns the index of the given field in the given struct.<br>
	 * The index of the field reference is used, the name of the field is only used for error messages.<br>
	 *
	 * @param struct The struct to get the index for
	 * @param field The field to get the index of
	 * @param exceptionConstructor A function to create an exception if the field is not indexed or out of bounds
	 * @param <X> The type of the exception to throw
	 * @return The index of the field
	 * @throws NullPointerException If the struct, the field or the exception constructor is null
	 * @throws X If the field is not indexed or the index is out of bounds
	 */
	private <X extends Exception> int getIndex(@NonNull BinaryStruct struct, @NonNull FieldRef field, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		Objects.requireNonNull(struct, "Struct must not be null");
		Objects.requireNonNull(field, "Field must not be null");
		
		if (!field.isIndexed()) {
			throw exceptionConstructor.apply("Field '" + field.name() + "' is not indexed, the binary provider requires a field codec which is part of a codec group");
		}
		if (field.index() >= struct.size()) {
			throw exceptionConstructor.apply("Field '" + field.name() + "' with index " + field.index() + " is out of bounds for struct of size " + struct.size());
		}
		return field.index();
	}
	
	/**
	 * Returns the index of the field with the given name in the given struct.<br>
	 * The names of the fields of a struct are only known if the struct was built in memory.<br>
	 *
	 * @param struct The struct to get the index for
	 * @param name The name of the field
	 * @param exceptionConstructor A function to create an exception if the name is not known
	 * @param <X> The type of the exception to throw
	 * @return The index of the field
	 * @throws NullPointerException If the struct, the name or the exception constructor is null
	 * @throws X If the struct does not know the name of the field
	 */
	private <X extends Exception> int getNamedIndex(@NonNull BinaryStruct struct, @NonNull String name, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		Objects.requireNonNull(struct, "Struct must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		
		int index = struct.indexOf(name);
		if (0 > index) {
			throw exceptionConstructor.apply("Unable to access field '" + name + "' by name, the fields of a binary struct are identified by their position");
		}
		return index;
	}
	
	/**
	 * Returns the given value as a binary map.<br>
	 *
	 * @param type The value to get as a binary map
	 * @param exceptionConstructor A function to create an exception if the value is not a binary map
	 * @param <X> The type of the exception to throw
	 * @return The value as a binary map
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the value is null or not a binary map
	 */
	private <X extends Exception> @NonNull BinaryMap getAsMap(@Nullable BinaryElement type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid map");
		}
		
		if (!type.isBinaryMap()) {
			throw exceptionConstructor.apply("Value '" + type + "' is not a binary map");
		}
		return type.getAsBinaryMap();
	}
	
	/**
	 * Returns the given value as a binary primitive.<br>
	 *
	 * @param type The value to get as a binary primitive
	 * @param name The name of the expected type, used for error messages
	 * @param exceptionConstructor A function to create an exception if the value is not a binary primitive
	 * @param <X> The type of the exception to throw
	 * @return The value as a binary primitive
	 * @throws NullPointerException If the name or the exception constructor is null
	 * @throws X If the value is null or not a binary primitive
	 */
	private <X extends Exception> @NonNull BinaryPrimitive getPrimitive(@Nullable BinaryElement type, @NonNull String name, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a " + name);
		}
		
		if (!type.isBinaryPrimitive()) {
			throw exceptionConstructor.apply("Value '" + type + "' is not a binary primitive");
		}
		return type.getAsBinaryPrimitive();
	}
	
	/**
	 * Returns the given value as a number.<br>
	 *
	 * @param type The value to get as a number
	 * @param name The name of the expected type, used for error messages
	 * @param exceptionConstructor A function to create an exception if the value is not a binary number
	 * @param <X> The type of the exception to throw
	 * @return The value as a number
	 * @throws NullPointerException If the name or the exception constructor is null
	 * @throws X If the value is null or not a binary number
	 */
	private <X extends Exception> @NonNull Number getNumber(@Nullable BinaryElement type, @NonNull String name, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		
		BinaryPrimitive primitive = this.getPrimitive(type, name, exceptionConstructor);
		if (!primitive.isBinaryNumber()) {
			throw exceptionConstructor.apply("Value '" + type + "' is not a binary " + name);
		}
		return primitive.getAsNumber();
	}
	
	/**
	 * Merges the given structs into a new struct.<br>
	 * The fields of the given value overwrite the fields of the current struct if they are present.<br>
	 * <p>
	 *     In contrast to the merge of two lists or maps, the current struct is not modified.<br>
	 *     The number of fields of a struct is fixed after its construction,<br>
	 *     therefore the current struct can not hold the merged fields if the given value has more fields than the current struct.<br>
	 *     A new struct is returned in all cases to keep the behavior of this method independent of the sizes of the given structs.
	 * </p>
	 *
	 * @param current The current struct
	 * @param value The struct to merge
	 * @return The merged struct
	 * @throws NullPointerException If the current struct or the struct to merge is null
	 */
	private @NonNull BinaryStruct mergeStruct(@NonNull BinaryStruct current, @NonNull BinaryStruct value) {
		Objects.requireNonNull(current, "Current struct must not be null");
		Objects.requireNonNull(value, "Struct to merge must not be null");
		
		int size = Math.max(current.size(), value.size());
		BinaryStruct merged = new BinaryStruct(size);
		for (int i = 0; i < size; i++) {
			int index = i;
			
			BinaryElement element;
			if (value.has(index)) {
				element = value.get(index);
			} else if (current.has(index)) {
				element = current.get(index);
			} else {
				element = BinaryAbsent.INSTANCE;
			}
			
			Optional<String> name = index < value.size() ? value.getName(index) : Optional.empty();
			if (name.isEmpty() && index < current.size()) {
				name = current.getName(index);
			}
			
			name.ifPresentOrElse(fieldName -> merged.set(index, fieldName, element), () -> merged.set(index, element));
		}
		return merged;
	}
}
