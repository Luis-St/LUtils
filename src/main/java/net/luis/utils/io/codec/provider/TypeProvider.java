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

import net.luis.utils.io.codec.Codec;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A provider for a specific type.<br>
 * Used to create, get, set, and merge values of a specific type.<br>
 * <p>
 *     The implementations of this interface are used in {@link Codec Codecs} to provide type-specific encoding and decoding.<br>
 *     With this system the {@link Codec} can be used for any type,<br>
 *     as long as a {@link TypeProvider TypeProvider} is provided for that type.
 * </p>
 * <p>
 *     The most methods in this interface return a {@link Result} object.<br>
 *     This is to provide a way to handle errors in the encoding and decoding process.<br>
 *     If the operation was successful, the {@link Result} will contain the result of the operation<br>
 *     or if the operation failed, the {@link Result} will contain an error message.
 * </p>
 * <p>
 *     All methods of this interface must handle {@code null} values appropriately.<br>
 *     If a method does not accept {@code null} values, it must return an error {@link Result}.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type this provider is for
 */
public interface TypeProvider<T> {
	
	/**
	 * Creates an empty value of the type this provider is for.<br>
	 * @return An empty value
	 */
	@NonNull T empty();
	
	/**
	 * Creates a null value of the type this provider is for.<br>
	 * @return A result containing the null value
	 */
	@NonNull Result<T> createNull();
	
	/**
	 * Creates a boolean value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the boolean value from
	 * @return A result containing the boolean value
	 */
	@NonNull Result<T> createBoolean(boolean value);
	
	/**
	 * Creates a byte value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the byte value from
	 * @return A result containing the byte value
	 */
	@NonNull Result<T> createByte(byte value);
	
	/**
	 * Creates a short value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the short value from
	 * @return A result containing the short value
	 */
	@NonNull Result<T> createShort(short value);
	
	/**
	 * Creates an integer value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the integer value from
	 * @return A result containing the integer value
	 */
	@NonNull Result<T> createInteger(int value);
	
	/**
	 * Creates a long value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the long value from
	 * @return A result containing the long value
	 */
	@NonNull Result<T> createLong(long value);
	
	/**
	 * Creates a float value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the float value from
	 * @return A result containing the float value
	 */
	@NonNull Result<T> createFloat(float value);
	
	/**
	 * Creates a double value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the double value from
	 * @return A result containing the double value
	 */
	@NonNull Result<T> createDouble(double value);
	
	/**
	 * Creates a string value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the string value from
	 * @return A result containing the string value
	 */
	@NonNull Result<T> createString(@Nullable String value);
	
	/**
	 * Creates a list value of the type this provider is for using the given values.<br>
	 *
	 * @param values The values to create the list value from
	 * @return A result containing the list value
	 */
	@NonNull Result<T> createList(@Nullable List<? extends T> values);
	
	/**
	 * Creates an empty map value of the type this provider is for.<br>
	 * @return A result containing the empty map value
	 */
	@NonNull Result<T> createMap();
	
	/**
	 * Creates a map value of the type this provider is for using the given values.<br>
	 *
	 * @param values The values to create the map value from
	 * @return A result containing the map value
	 */
	@NonNull Result<T> createMap(@Nullable Map<String, ? extends T> values);
	
	/**
	 * Gets the given value as an empty value of the type this provider is for.<br>
	 *
	 * @param type The value to get as an empty value
	 * @return A success result containing the empty value, or an error result if the given value is not an empty value
	 */
	@NonNull Result<T> getEmpty(@Nullable T type);
	
	/**
	 * Checks if the given value is a null value of the type this provider is for.<br>
	 *
	 * @param type The value to check
	 * @return A success result containing true if the value is null, false otherwise, or an error result if the check failed
	 */
	@NonNull Result<Boolean> isNull(@Nullable T type);
	
	/**
	 * Gets the given value as a boolean value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a boolean value
	 * @return A success result containing the boolean value, or an error result if the given value is not a boolean value
	 */
	@NonNull Result<Boolean> getBoolean(@Nullable T type);
	
	/**
	 * Gets the given value as a byte value of the type this provider is for.<br>
	 * @param type The value to get as a byte value
	 * @return A success result containing the byte value, or an error result if the given value is not a byte value
	 */
	@NonNull Result<Byte> getByte(@Nullable T type);
	
	/**
	 * Gets the given value as a short value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a short value
	 * @return A success result containing the short value, or an error result if the given value is not a short value
	 */
	@NonNull Result<Short> getShort(@Nullable T type);
	
	/**
	 * Gets the given value as an integer value of the type this provider is for.<br>
	 *
	 * @param type The value to get as an integer value
	 * @return A success result containing the integer value, or an error result if the given value is not an integer value
	 */
	@NonNull Result<Integer> getInteger(@Nullable T type);
	
	/**
	 * Gets the given value as a long value of the type this provider is for.<br>
	 * @param type The value to get as a long value
	 * @return A success result containing the long value, or an error result if the given value is not a long value
	 */
	@NonNull Result<Long> getLong(@Nullable T type);
	
	/**
	 * Gets the given value as a float value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a float value
	 * @return A success result containing the float value, or an error result if the given value is not a float value
	 */
	@NonNull Result<Float> getFloat(@Nullable T type);
	
	/**
	 * Gets the given value as a double value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a double value
	 * @return A success result containing the double value, or an error result if the given value is not a double value
	 */
	@NonNull Result<Double> getDouble(@Nullable T type);
	
	/**
	 * Gets the given value as a string value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a string value
	 * @return A success result containing the string value, or an error result if the given value is not a string value
	 */
	@NonNull Result<String> getString(@Nullable T type);
	
	/**
	 * Gets the given value as a list value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a list value
	 * @return A success result containing the list value, or an error result if the given value is not a list value
	 */
	@NonNull Result<List<T>> getList(@Nullable T type);
	
	/**
	 * Gets the given value as a map value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a map value
	 * @return A success result containing the map value, or an error result if the given value is not a map value
	 */
	@NonNull Result<Map<String, T>> getMap(@Nullable T type);
	
	/**
	 * Checks if the given value is a map and contains the given key.<br>
	 * The given type must be a map.<br>
	 *
	 * @param type The value to check
	 * @param key The key to check
	 * @return A success result containing true if the map contains the key or false if not, or an error result if the given value is not a map
	 */
	@NonNull Result<Boolean> has(@Nullable T type, @Nullable String key);
	
	/**
	 * Gets the value of the given key from the given value.<br>
	 * The given type must be a map.<br>
	 *
	 * @param type The value to get the value from
	 * @param key The key to get the value for
	 * @return A success result containing the value for the key, or an error result if the given value is not a map
	 */
	@NonNull Result<T> get(@Nullable T type, @Nullable String key);
	
	/**
	 * Sets the value for the given key in the given value.<br>
	 * The given type must be a map.<br>
	 *
	 * @param type The value to set the value in
	 * @param key The key to set the value for
	 * @param value The value to set
	 * @return A success result containing the result of the set operation, or an error result if the given value is not a map
	 */
	@NonNull Result<T> set(@Nullable T type, @Nullable String key, @Nullable T value);
	
	/**
	 * Sets the value for the given key in the given value.<br>
	 * This is an overloaded method to handle the value as a {@link Result}.<br>
	 * If the given value is a success, the value will be set.<br>
	 * If the given value is an error, the value will not be set and an error result will be returned.<br>
	 *
	 * @param type The value to set the value in
	 * @param key The key to set the value for
	 * @param value The value to set
	 * @return A success result containing the result of the set operation, or an error result if the given value is not a map, or the given value is an error
	 * @see #set(Object, String, Object)
	 */
	default @NonNull Result<T> set(@Nullable T type, @Nullable String key, @Nullable Result<T> value) {
		if (type == null) {
			return Result.error("Type 'null' is not a map");
		}
		if (key == null) {
			return Result.error("Key 'null' is not valid");
		}
		if (value == null) {
			return Result.error("Value 'null' is not valid");
		}
		
		if (value.isSuccess()) {
			return this.set(type, key, value.resultOrThrow());
		}
		return Result.error("Unable to set value for key '" + key + "' in '" + type + "': " + value.errorOrThrow());
	}
	
	/**
	 * Merges the given value with the current value.<br>
	 * The logic is specific to the type this provider is for.<br>
	 *
	 * @param current The current value
	 * @param value The value to merge
	 * @return A success result containing the result of the merge operation, or an error result if the merge operation failed
	 */
	@NonNull Result<T> merge(@Nullable T current, @Nullable T value);
	
	/**
	 * Merges the given value with the current value.<br>
	 * This is an overloaded method to handle the value as a {@link Result}.<br>
	 * If the given value is a success, the value will be merged.<br>
	 * If the given value is an error, the value will not be merged and an error result will be returned.<br>
	 * The logic is specific to the type this provider is for.<br>
	 *
	 * @param current The current value
	 * @param value The value to merge
	 * @return A success result containing the result of the merge operation, or an error result if the merge operation failed, or the given value is an error
	 * @see #merge(Object, Object)
	 */
	default @NonNull Result<T> merge(@Nullable T current, @Nullable Result<T> value) {
		if (current == null) {
			return Result.error("Current value 'null' is not valid");
		}
		if (value == null) {
			return Result.error("Value 'null' is not valid");
		}
		
		if (value.isSuccess()) {
			return this.merge(current, value.resultOrThrow());
		}
		return Result.error("Unable to merge '" + current + "' with 'value': " + value.errorOrThrow());
	}
}
