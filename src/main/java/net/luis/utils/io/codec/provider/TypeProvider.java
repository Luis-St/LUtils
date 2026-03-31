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
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A provider for a specific type.<br>
 * Used to create, get, set, and merge values of a specific type.<br>
 * <p>
 *     The implementations of this interface are used in {@link Codec Codecs} to provide type-specific encoding and decoding.<br>
 *     With this system the {@link Codec} can be used for any type,<br>
 *     as long as a {@link TypeProvider TypeProvider} is provided for that type.
 * </p>
 * <p>
 *     The most methods in this interface might fail.<br>
 *     If a method fails, it should throw an exception.<br>
 *     The exception type is determined by the caller of the method, as the caller provides a function to create the exception.<br>
 * </p>
 * <p>
 *     All methods of this interface must handle {@code null} values appropriately.<br>
 *     If a method does not accept {@code null} values, it must throw an exception when a {@code null} value is passed.<br>
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
	 *
	 * @param exceptionConstructor A function to create an exception if the type does not support null values or the creation failed
	 * @param <X> The type of the exception to throw
	 * @return The null value of the type
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support null values or the creation failed
	 */
	<X extends Exception> @NonNull T createNull(@NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Creates a boolean value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the boolean value from
	 * @param exceptionConstructor A function to create an exception if the type does not support null values or the creation failed
	 * @param <X> The type of the exception to throw
	 * @return The boolean value of the type
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support boolean values or the creation failed
	 */
	<X extends Exception> @NonNull T createBoolean(boolean value, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Creates a byte value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the byte value from
	 * @param exceptionConstructor A function to create an exception if the type does not support null values or the creation failed
	 * @param <X> The type of the exception to throw
	 * @return The byte value of the type
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support byte values or the creation failed
	 */
	<X extends Exception> @NonNull T createByte(byte value, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Creates a short value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the short value from
	 * @param exceptionConstructor A function to create an exception if the type does not support null values or the creation failed
	 * @param <X> The type of the exception to throw
	 * @return The short value of the type
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support short values or the creation failed
	 */
	<X extends Exception> @NonNull T createShort(short value, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Creates an integer value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the integer value from
	 * @param exceptionConstructor A function to create an exception if the type does not support null values or the creation failed
	 * @param <X> The type of the exception to throw
	 * @return The integer value of the type
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support integer values or the creation failed
	 */
	<X extends Exception> @NonNull T createInteger(int value, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Creates a long value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the long value from
	 * @param exceptionConstructor A function to create an exception if the type does not support null values or the creation failed
	 * @param <X> The type of the exception to throw
	 * @return The long value of the type
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support long values or the creation failed
	 */
	<X extends Exception> @NonNull T createLong(long value, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Creates a float value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the float value from
	 * @param exceptionConstructor A function to create an exception if the type does not support null values or the creation failed
	 * @param <X> The type of the exception to throw
	 * @return The float value of the type
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support float values or the creation failed
	 */
	<X extends Exception> @NonNull T createFloat(float value, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Creates a double value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the double value from
	 * @param exceptionConstructor A function to create an exception if the type does not support null values or the creation failed
	 * @param <X> The type of the exception to throw
	 * @return The double value of the type
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support double values or the creation failed
	 */
	<X extends Exception> @NonNull T createDouble(double value, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Creates a string value of the type this provider is for using the given value.<br>
	 *
	 * @param value The value to create the string value from
	 * @param exceptionConstructor A function to create an exception if the type does not support null values or the creation failed
	 * @param <X> The type of the exception to throw
	 * @return The string value of the type
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support string values or the creation failed
	 */
	<X extends Exception> @NonNull T createString(@Nullable String value, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Creates a list value of the type this provider is for using the given values.<br>
	 *
	 * @param values The values to create the list value from
	 * @param exceptionConstructor A function to create an exception if the type does not support null values or the creation failed
	 * @param <X> The type of the exception to throw
	 * @return A list of the type
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support list values or the creation failed
	 */
	<X extends Exception> @NonNull T createList(@Nullable List<? extends T> values, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Creates an empty map value of the type this provider is for.<br>
	 *
	 * @param exceptionConstructor A function to create an exception if the type does not support null values or the creation failed
	 * @param <X> The type of the exception to throw
	 * @return An empty map of the type
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support map values or the creation failed
	 */
	<X extends Exception> @NonNull T createMap(@NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Creates a map value of the type this provider is for using the given values.<br>
	 *
	 * @param values The values to create the map value from
	 * @param exceptionConstructor A function to create an exception if the type does not support empty values or the check failed
	 * @param <X> The type of the exception to throw
	 * @return A map of the type
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support map values or the creation failed
	 */
	<X extends Exception> @NonNull T createMap(@Nullable Map<String, ? extends T> values, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Checks if the given value is an empty value of the type this provider is for.<br>
	 *
	 * @param type The value to check
	 * @param exceptionConstructor A function to create an exception if the type does not support empty values or the check failed
	 * @param <X> The type of the exception to throw
	 * @return True if the value is empty, false otherwise
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support empty values or the check failed
	 */
	<X extends Exception> boolean isEmpty(@Nullable T type, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Checks if the given value is a null value of the type this provider is for.<br>
	 *
	 * @param type The value to check
	 * @param exceptionConstructor A function to create an exception if the type does not support null values or the check failed
	 * @param <X> The type of the exception to throw
	 * @return True if the value is null, false otherwise
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support null values or the check failed
	 */
	<X extends Exception> boolean isNull(@Nullable T type, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Gets the given value as a boolean value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a boolean value
	 * @param exceptionConstructor A function to create an exception if the type does not support boolean values or the retrieval failed
	 * @param <X> The type of the exception to throw
	 * @return The boolean value
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support boolean values or the retrieval failed
	 */
	<X extends Exception> @NonNull Boolean getBoolean(@Nullable T type, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Gets the given value as a byte value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a byte value
	 * @param exceptionConstructor A function to create an exception if the type does not support byte values or the retrieval failed
	 * @param <X> The type of the exception to throw
	 * @return The byte value
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support byte values or the retrieval failed
	 */
	<X extends Exception> @NonNull Byte getByte(@Nullable T type, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Gets the given value as a short value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a short value
	 * @param exceptionConstructor A function to create an exception if the type does not support short values or the retrieval failed
	 * @param <X> The type of the exception to throw
	 * @return The short value
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support short values or the retrieval failed
	 */
	<X extends Exception> @NonNull Short getShort(@Nullable T type, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Gets the given value as an integer value of the type this provider is for.<br>
	 *
	 * @param type The value to get as an integer value
	 * @param exceptionConstructor A function to create an exception if the type does not support integer values or the retrieval failed
	 * @param <X> The type of the exception to throw
	 * @return The integer value
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support integer values or the retrieval failed
	 */
	<X extends Exception> @NonNull Integer getInteger(@Nullable T type, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Gets the given value as a long value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a long value
	 * @param exceptionConstructor A function to create an exception if the type does not support long values or the retrieval failed
	 * @param <X> The type of the exception to throw
	 * @return The long value
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support long values or the retrieval failed
	 */
	<X extends Exception> @NonNull Long getLong(@Nullable T type, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Gets the given value as a float value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a float value
	 * @param exceptionConstructor A function to create an exception if the type does not support float values or the retrieval failed
	 * @param <X> The type of the exception to throw
	 * @return The float value
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support float values or the retrieval failed
	 */
	<X extends Exception> @NonNull Float getFloat(@Nullable T type, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Gets the given value as a double value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a double value
	 * @param exceptionConstructor A function to create an exception if the type does not support double values or the retrieval failed
	 * @param <X> The type of the exception to throw
	 * @return The double value
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support double values or the retrieval failed
	 */
	<X extends Exception> @NonNull Double getDouble(@Nullable T type, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Gets the given value as a string value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a string value
	 * @param exceptionConstructor A function to create an exception if the type does not support string values or the retrieval failed
	 * @param <X> The type of the exception to throw
	 * @return The string value
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support string values or the retrieval failed
	 */
	<X extends Exception> @NonNull String getString(@Nullable T type, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Gets the given value as a list value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a list value
	 * @param exceptionConstructor A function to create an exception if the type does not support list values or the retrieval failed
	 * @param <X> The type of the exception to throw
	 * @return The list
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support list values or the retrieval failed
	 */
	<X extends Exception> @NonNull List<T> getList(@Nullable T type, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Gets the given value as a map value of the type this provider is for.<br>
	 *
	 * @param type The value to get as a map value
	 * @param exceptionConstructor A function to create an exception if the type does not support map values or the retrieval failed
	 * @param <X> The type of the exception to throw
	 * @return The map
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support map values or the retrieval failed
	 */
	<X extends Exception> @NonNull Map<String, T> getMap(@Nullable T type, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Checks if the given value is a map and contains the given key.<br>
	 * The given type must be a map.<br>
	 *
	 * @param type The value to check
	 * @param key The key to check
	 * @param exceptionConstructor A function to create an exception if the type does not support map values or the check failed
	 * @param <X> The type of the exception to throw
	 * @return True if the map contains the key or false if not
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support map values or the check failed
	 */
	<X extends Exception> boolean has(@Nullable T type, @Nullable String key, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Gets the value of the given key from the given value.<br>
	 * The given type must be a map.<br>
	 * <p>
	 *     The returned value might be null if the key does not exist in the map.<br>
	 *     This method is allowed to return null values to support optional values (which are represented as null).
	 * </p>
	 *
	 * @param type The value to get the value from
	 * @param key The key to get the value for
	 * @param exceptionConstructor A function to create an exception if the type does not support map values or the retrieval failed
	 * @param <X> The type of the exception to throw
	 * @return The value for the key
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support map values or the retrieval failed
	 */
	<X extends Exception> @Nullable T get(@Nullable T type, @Nullable String key, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Sets the value for the given key in the given value.<br>
	 * The given type must be a map.<br>
	 *
	 * @param type The value to set the value in
	 * @param key The key to set the value for
	 * @param value The value to set
	 * @param exceptionConstructor A function to create an exception if the type does not support map values or the set operation failed
	 * @param <X> The type of the exception to throw
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support map values or the set operation failed
	 */
	<X extends Exception> void set(@Nullable T type, @Nullable String key, @Nullable T value, @NonNull Function<String, X> exceptionConstructor) throws X;
	
	/**
	 * Merges the given value with the current value.<br>
	 * The logic is specific to the type this provider is for.<br>
	 *
	 * @param current The current value
	 * @param value The value to merge
	 * @param exceptionConstructor A function to create an exception if the type does not support merging or the merge operation failed
	 * @param <X> The type of the exception to throw
	 * @return The resulting merged value
	 * @throws NullPointerException If the exception constructor is null
	 * @throws X If the type does not support merging or the merge operation failed
	 */
	<X extends Exception> @UnknownNullability T merge(@Nullable T current, @Nullable T value, @NonNull Function<String, X> exceptionConstructor) throws X;
}
