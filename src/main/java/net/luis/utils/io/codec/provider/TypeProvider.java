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

package net.luis.utils.io.codec.provider;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A provider for a specific type.<br>
 * Used to create, get, set, and merge values of a specific type.<br>
 * <p>
 *     The implementations of this interface are used in {@link Codec Codecs} to provide type-specific encoding and decoding.<br>
 *     With this system the {@link Codec} can be used for any type,<br>
 *     as long as a {@link TypeProvider TypeProvider} is provided for that type.<br>
 * </p>
 * <p>
 *     The most methods in this interface return a {@link Result} object.<br>
 *     This is to provide a way to handle errors in the encoding and decoding process.<br>
 *     If the operation was successful, the {@link Result} will contain the result of the operation<br>
 *     or if the operation failed, the {@link Result} will contain an error message.<br>
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
	@NotNull T empty();
	
	/**
	 * Creates a boolean value of the type this provider is for using the given value.<br>
	 * @param value The value to create the boolean value from
	 * @return A result containing the boolean value
	 */
	@NotNull Result<T> createBoolean(boolean value);
	
	/**
	 * Creates a byte value of the type this provider is for using the given value.<br>
	 * @param value The value to create the byte value from
	 * @return A result containing the byte value
	 */
	@NotNull Result<T> createByte(byte value);
	
	/**
	 * Creates a short value of the type this provider is for using the given value.<br>
	 * @param value The value to create the short value from
	 * @return A result containing the short value
	 */
	@NotNull Result<T> createShort(short value);
	
	/**
	 * Creates an integer value of the type this provider is for using the given value.<br>
	 * @param value The value to create the integer value from
	 * @return A result containing the integer value
	 */
	@NotNull Result<T> createInteger(int value);
	
	/**
	 * Creates a long value of the type this provider is for using the given value.<br>
	 * @param value The value to create the long value from
	 * @return A result containing the long value
	 */
	@NotNull Result<T> createLong(long value);
	
	/**
	 * Creates a float value of the type this provider is for using the given value.<br>
	 * @param value The value to create the float value from
	 * @return A result containing the float value
	 */
	@NotNull Result<T> createFloat(float value);
	
	/**
	 * Creates a double value of the type this provider is for using the given value.<br>
	 * @param value The value to create the double value from
	 * @return A result containing the double value
	 */
	@NotNull Result<T> createDouble(double value);
	
	/**
	 * Creates a string value of the type this provider is for using the given value.<br>
	 * @param value The value to create the string value from
	 * @return A result containing the string value
	 * @throws NullPointerException If the given value is null
	 */
	@NotNull Result<T> createString(@NotNull String value);
	
	/**
	 * Creates a list value of the type this provider is for using the given values.<br>
	 * @param values The values to create the list value from
	 * @return A result containing the list value
	 * @throws NullPointerException If the given values are null
	 */
	@NotNull Result<T> createList(@NotNull List<? extends T> values);
	
	/**
	 * Creates an empty map value of the type this provider is for.<br>
	 * @return A result containing the empty map value
	 */
	@NotNull Result<T> createMap();
	
	/**
	 * Creates a map value of the type this provider is for using the given values.<br>
	 * @param values The values to create the map value from
	 * @return A result containing the map value
	 * @throws NullPointerException If the given values are null
	 */
	@NotNull Result<T> createMap(@NotNull Map<String, ? extends T> values);
	
	/**
	 * Gets the given value as an empty value of the type this provider is for.<br>
	 * @param type The value to get as an empty value
	 * @return A success result containing the empty value, or an error result if the given value is not an empty value
	 * @throws NullPointerException If the given value is null
	 */
	@NotNull Result<T> getEmpty(@NotNull T type);
	
	/**
	 * Gets the given value as a boolean value of the type this provider is for.<br>
	 * @param type The value to get as a boolean value
	 * @return A success result containing the boolean value, or an error result if the given value is not a boolean value
	 * @throws NullPointerException If the given value is null
	 */
	@NotNull Result<Boolean> getBoolean(@NotNull T type);
	
	/**
	 * Gets the given value as a byte value of the type this provider is for.<br>
	 * @param type The value to get as a byte value
	 * @return A success result containing the byte value, or an error result if the given value is not a byte value
	 * @throws NullPointerException If the given value is null
	 */
	@NotNull Result<Byte> getByte(@NotNull T type);
	
	/**
	 * Gets the given value as a short value of the type this provider is for.<br>
	 * @param type The value to get as a short value
	 * @return A success result containing the short value, or an error result if the given value is not a short value
	 * @throws NullPointerException If the given value is null
	 */
	@NotNull Result<Short> getShort(@NotNull T type);
	
	/**
	 * Gets the given value as an integer value of the type this provider is for.<br>
	 * @param type The value to get as an integer value
	 * @return A success result containing the integer value, or an error result if the given value is not an integer value
	 * @throws NullPointerException If the given value is null
	 */
	@NotNull Result<Integer> getInteger(@NotNull T type);
	
	/**
	 * Gets the given value as a long value of the type this provider is for.<br>
	 * @param type The value to get as a long value
	 * @return A success result containing the long value, or an error result if the given value is not a long value
	 * @throws NullPointerException If the given value is null
	 */
	@NotNull Result<Long> getLong(@NotNull T type);
	
	/**
	 * Gets the given value as a float value of the type this provider is for.<br>
	 * @param type The value to get as a float value
	 * @return A success result containing the float value, or an error result if the given value is not a float value
	 * @throws NullPointerException If the given value is null
	 */
	@NotNull Result<Float> getFloat(@NotNull T type);
	
	/**
	 * Gets the given value as a double value of the type this provider is for.<br>
	 * @param type The value to get as a double value
	 * @return A success result containing the double value, or an error result if the given value is not a double value
	 * @throws NullPointerException If the given value is null
	 */
	@NotNull Result<Double> getDouble(@NotNull T type);
	
	/**
	 * Gets the given value as a string value of the type this provider is for.<br>
	 * @param type The value to get as a string value
	 * @return A success result containing the string value, or an error result if the given value is not a string value
	 * @throws NullPointerException If the given value is null
	 */
	@NotNull Result<String> getString(@NotNull T type);
	
	/**
	 * Gets the given value as a list value of the type this provider is for.<br>
	 * @param type The value to get as a list value
	 * @return A success result containing the list value, or an error result if the given value is not a list value
	 * @throws NullPointerException If the given value is null
	 */
	@NotNull Result<List<T>> getList(@NotNull T type);
	
	/**
	 * Gets the given value as a map value of the type this provider is for.<br>
	 * @param type The value to get as a map value
	 * @return A success result containing the map value, or an error result if the given value is not a map value
	 * @throws NullPointerException If the given value is null
	 */
	@NotNull Result<Map<String, T>> getMap(@NotNull T type);
	
	/**
	 * Checks if the given value is a map and contains the given key.<br>
	 * The given type must be a map.<br>
	 * @param type The value to check
	 * @param key The key to check
	 * @return A success result containing true if the map contains the key or false if not, or an error result if the given value is not a map
	 * @throws NullPointerException If the given value or key is null
	 */
	@NotNull Result<Boolean> has(@NotNull T type, @NotNull String key);
	
	/**
	 * Gets the value of the given key from the given value.<br>
	 * The given type must be a map.<br>
	 * @param type The value to get the value from
	 * @param key The key to get the value for
	 * @return A success result containing the value for the key, or an error result if the given value is not a map
	 * @throws NullPointerException If the given value or key is null
	 */
	@NotNull Result<T> get(@NotNull T type, @NotNull String key);
	
	/**
	 * Sets the value for the given key in the given value.<br>
	 * The given type must be a map.<br>
	 * @param type The value to set the value in
	 * @param key The key to set the value for
	 * @param value The value to set
	 * @return A success result containing the result of the set operation, or an error result if the given value is not a map
	 * @throws NullPointerException If the given value, key, or value is null
	 */
	@NotNull Result<T> set(@NotNull T type, @NotNull String key, @NotNull T value);
	
	/**
	 * Sets the value for the given key in the given value.<br>
	 * This is an overloaded method to handle the value as a {@link Result}.<br>
	 * If the given value is a success, the value will be set.<br>
	 * If the given value is an error, the value will not be set and an error result will be returned.<br>
	 * @param type The value to set the value in
	 * @param key The key to set the value for
	 * @param value The value to set
	 * @return A success result containing the result of the set operation, or an error result if the given value is not a map, or the given value is an error
	 * @throws NullPointerException If the given value, key, or value is null
	 * @see #set(Object, String, Object)
	 */
	default @NotNull Result<T> set(@NotNull T type, @NotNull String key, @NotNull Result<T> value) {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		if (value.isSuccess()) {
			return this.set(type, key, value.orThrow());
		}
		return Result.error("Unable to set value for key '" + key + "' in '" + type + "': " + value.errorOrThrow());
	}
	
	/**
	 * Merges the given value with the current value.<br>
	 * The logic is specific to the type this provider is for.<br>
	 * @param current The current value
	 * @param value The value to merge
	 * @return A success result containing the result of the merge operation, or an error result if the merge operation failed
	 * @throws NullPointerException If the given current value or value is null
	 */
	@NotNull Result<T> merge(@NotNull T current, @NotNull T value);
	
	/**
	 * Merges the given value with the current value.<br>
	 * This is an overloaded method to handle the value as a {@link Result}.<br>
	 * If the given value is a success, the value will be merged.<br>
	 * If the given value is an error, the value will not be merged and an error result will be returned.<br>
	 * The logic is specific to the type this provider is for.<br>
	 * @param current The current value
	 * @param value The value to merge
	 * @return A success result containing the result of the merge operation, or an error result if the merge operation failed, or the given value is an error
	 * @throws NullPointerException If the given current value or value is null
	 * @see #merge(Object, Object)
	 */
	default @NotNull Result<T> merge(@NotNull T current, @NotNull Result<T> value) {
		Objects.requireNonNull(current, "Current value must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		if (value.isSuccess()) {
			return this.merge(current, value.orThrow());
		}
		return Result.error("Unable to merge '" + current + "' with 'value': " + value.errorOrThrow());
	}
}
