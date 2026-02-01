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
import org.jetbrains.annotations.UnknownNullability;
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
	 * @return The null value of the type
	 * @throws TypeProviderException If the type does not support null values or the creation failed
	 */
	@NonNull T createNull();
	
	/**
	 * Creates a boolean value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the boolean value from
	 * @return The boolean value of the type
	 * @throws TypeProviderException If the type does not support boolean values or the creation failed
	 */
	@NonNull T createBoolean(boolean value);
	
	/**
	 * Creates a byte value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the byte value from
	 * @return The byte value of the type
	 * @throws TypeProviderException If the type does not support byte values or the creation failed
	 */
	@NonNull T createByte(byte value);
	
	/**
	 * Creates a short value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the short value from
	 * @return The short value of the type
	 * @throws TypeProviderException If the type does not support short values or the creation failed
	 */
	@NonNull T createShort(short value);
	
	/**
	 * Creates an integer value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the integer value from
	 * @return The integer value of the type
	 * @throws TypeProviderException If the type does not support integer values or the creation failed
	 */
	@NonNull T createInteger(int value);
	
	/**
	 * Creates a long value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the long value from
	 * @return The long value of the type
	 * @throws TypeProviderException If the type does not support long values or the creation failed
	 */
	@NonNull T createLong(long value);
	
	/**
	 * Creates a float value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the float value from
	 * @return The float value of the type
	 * @throws TypeProviderException If the type does not support float values or the creation failed
	 */
	@NonNull T createFloat(float value);
	
	/**
	 * Creates a double value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the double value from
	 * @return The double value of the type
	 * @throws TypeProviderException If the type does not support double values or the creation failed
	 */
	@NonNull T createDouble(double value);
	
	/**
	 * Creates a string value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the string value from
	 * @return The string value of the type
	 * @throws TypeProviderException If the type does not support string values or the creation failed
	 */
	@NonNull T createString(@Nullable String value);
	
	/**
	 * Creates a list value of the type this provider is for using the given values.<br>
	 *
	 * @param values The values to create the list value from
	 * @return A list of the type
	 * @throws TypeProviderException If the type does not support list values or the creation failed
	 */
	@NonNull T createList(@Nullable List<? extends T> values);
	
	/**
	 * Creates an empty map value of the type this provider is for.<br>
	 * @return An empty map of the type
	 * @throws TypeProviderException If the type does not support map values or the creation failed
	 */
	@NonNull T createMap();
	
	/**
	 * Creates a map value of the type this provider is for using the given values.<br>
	 *
	 * @param values The values to create the map value from
	 * @return A map of the type
	 * @throws TypeProviderException If the type does not support map values or the creation failed
	 */
	@NonNull T createMap(@Nullable Map<String, ? extends T> values);
	
	/**
	 * Checks if the given value is an empty value of the type this provider is for.<br>
	 *
	 * @param type The value to check
	 * @return True if the value is empty, false otherwise
	 * @throws TypeProviderException If the type does not support empty values or the check failed
	 */
	boolean isEmpty(@Nullable T type);
	
	/**
	 * Checks if the given value is a null value of the type this provider is for.<br>
	 *
	 * @param type The value to check
	 * @return True if the value is null, false otherwise
	 * @throws TypeProviderException If the type does not support null values or the check failed
	 */
	boolean isNull(@Nullable T type);
	
	/**
	 * Gets the given value as a boolean value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a boolean value
	 * @return The boolean value
	 * @throws TypeProviderException If the type does not support boolean values or the retrieval failed
	 */
	@NonNull Boolean getBoolean(@Nullable T type);
	
	/**
	 * Gets the given value as a byte value of the type this provider is for.<br>
	 * @param type The value to get as a byte value
	 * @return The byte value
	 * @throws TypeProviderException If the type does not support byte values or the retrieval failed
	 */
	@NonNull Byte getByte(@Nullable T type);
	
	/**
	 * Gets the given value as a short value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a short value
	 * @return The short value
	 * @throws TypeProviderException If the type does not support short values or the retrieval failed
	 */
	@NonNull Short getShort(@Nullable T type);
	
	/**
	 * Gets the given value as an integer value of the type this provider is for.<br>
	 *
	 * @param type The value to get as an integer value
	 * @return The integer value
	 * @throws TypeProviderException If the type does not support integer values or the retrieval failed
	 */
	@NonNull Integer getInteger(@Nullable T type);
	
	/**
	 * Gets the given value as a long value of the type this provider is for.<br>
	 * @param type The value to get as a long value
	 * @return The long value
	 * @throws TypeProviderException If the type does not support long values or the retrieval failed
	 */
	@NonNull Long getLong(@Nullable T type);
	
	/**
	 * Gets the given value as a float value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a float value
	 * @return The float value
	 * @throws TypeProviderException If the type does not support float values or the retrieval failed
	 */
	@NonNull Float getFloat(@Nullable T type);
	
	/**
	 * Gets the given value as a double value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a double value
	 * @return The double value
	 * @throws TypeProviderException If the type does not support double values or the retrieval failed
	 */
	@NonNull Double getDouble(@Nullable T type);
	
	/**
	 * Gets the given value as a string value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a string value
	 * @return The string value
	 * @throws TypeProviderException If the type does not support string values or the retrieval failed
	 */
	@NonNull String getString(@Nullable T type);
	
	/**
	 * Gets the given value as a list value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a list value
	 * @return The list
	 * @throws TypeProviderException If the type does not support list values or the retrieval failed
	 */
	@NonNull List<T> getList(@Nullable T type);
	
	/**
	 * Gets the given value as a map value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a map value
	 * @return The map
	 * @throws TypeProviderException If the type does not support map values or the retrieval failed
	 */
	@NonNull Map<String, T> getMap(@Nullable T type);
	
	/**
	 * Checks if the given value is a map and contains the given key.<br>
	 * The given type must be a map.<br>
	 *
	 * @param type The value to check
	 * @param key The key to check
	 * @return True if the map contains the key or false if not
	 * @throws TypeProviderException If the type does not support map values or the check failed
	 */
	boolean has(@Nullable T type, @Nullable String key);
	
	/**
	 * Gets the value of the given key from the given value.<br>
	 * The given type must be a map.<br>
	 *
	 * @param type The value to get the value from
	 * @param key The key to get the value for
	 * @return The value for the key
	 * @throws TypeProviderException If the type does not support map values or the retrieval failed
	 */
	@NonNull T get(@Nullable T type, @Nullable String key);
	
	/**
	 * Sets the value for the given key in the given value.<br>
	 * The given type must be a map.<br>
	 *
	 * @param type The value to set the value in
	 * @param key The key to set the value for
	 * @param value The value to set
	 * @throws TypeProviderException If the type does not support map values or the set operation failed
	 */
	void set(@Nullable T type, @Nullable String key, @Nullable T value);
	
	/**
	 * Merges the given value with the current value.<br>
	 * The logic is specific to the type this provider is for.<br>
	 *
	 * @param current The current value
	 * @param value The value to merge
	 * @return The resulting merged value
	 * @throws TypeProviderException If the type does not support merging or the merge operation failed
	 */
	@UnknownNullability
	T merge(@Nullable T current, @Nullable T value);
}
