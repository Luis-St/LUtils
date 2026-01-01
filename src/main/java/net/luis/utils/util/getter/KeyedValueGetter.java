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

package net.luis.utils.util.getter;

import net.luis.utils.exception.InvalidStringException;
import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.reader.StringReader;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A functional interface that provides methods to get a value of a specific type with a specific key.<br>
 * The value is hold by the object that implements this interface.<br>
 * <p>
 *     The interface is functional because it has only one abstract method {@link #getAsString(String)}.<br>
 *     All other methods rely on this method to get the value as a string and then parse it to the desired type.<br>
 *     The {@link #getAsString(String)} method must bei implemented correctly, or the other methods will not work as expected.
 * </p>
 * The interface provides the following methods to get the value as a specific type:<br>
 * <ul>
 *     <li>{@link #getAsBoolean(String)} to get the value as a boolean</li>
 *     <li>{@link #getAsNumber(String)} to get the value as a number</li>
 *     <li>{@link #getAsByte(String)} to get the value as a byte</li>
 *     <li>{@link #getAsShort(String)} to get the value as a short</li>
 *     <li>{@link #getAsInteger(String)} to get the value as an integer</li>
 *     <li>{@link #getAsLong(String)} to get the value as a long</li>
 *     <li>{@link #getAsFloat(String)} to get the value as a float</li>
 *     <li>{@link #getAsDouble(String)} to get the value as a double</li>
 *     <li>{@link #getAs(String, ThrowableFunction)} to get the value as the result of the given parser</li>
 * </ul>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface KeyedValueGetter {
	
	/**
	 * Returns the value which is hold by this object as a string for the given key.<br>
	 *
	 * @param key The key of the value
	 * @return The value as a string
	 * @throws NullPointerException If the key is null
	 */
	@NonNull String getAsString(@NonNull String key);
	
	/**
	 * Returns the value which is hold by this object as a boolean for the given key.<br>
	 *
	 * @param key The key of the value
	 * @return The value as a boolean
	 * @throws NullPointerException If the key is null
	 * @throws IllegalArgumentException If the value is not a boolean (e.g. not "true" or "false")
	 */
	default boolean getAsBoolean(@NonNull String key) {
		String value = this.getAsString(key);
		if (Strings.CI.equalsAny(value, "true", "false")) {
			return Boolean.parseBoolean(value);
		}
		throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a boolean, expected 'true' or 'false'");
	}
	
	/**
	 * Returns the value which is hold by this object as a number for the given key.<br>
	 *
	 * @param key The key of the value
	 * @return The value as a number
	 * @throws NullPointerException If the key is null
	 * @throws IllegalArgumentException If the value is not a number
	 */
	default @NonNull Number getAsNumber(@NonNull String key) {
		String value = this.getAsString(key);
		try {
			return new StringReader(value).readNumber();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a number: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a byte for the given key.<br>
	 *
	 * @param key The key of the value
	 * @return The value as a byte
	 * @throws NullPointerException If the key is null
	 * @throws IllegalArgumentException If the value is not a byte
	 */
	default byte getAsByte(@NonNull String key) {
		String value = this.getAsString(key);
		try {
			return new StringReader(value).readByte();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a byte: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a short for the given key.<br>
	 *
	 * @param key The key of the value
	 * @return The value as a short
	 * @throws NullPointerException If the key is null
	 * @throws IllegalArgumentException If the value is not a short
	 */
	default short getAsShort(@NonNull String key) {
		String value = this.getAsString(key);
		try {
			return new StringReader(value).readShort();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a short: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as an integer for the given key.<br>
	 *
	 * @param key The key of the value
	 * @return The value as an integer
	 * @throws NullPointerException If the key is null
	 * @throws IllegalArgumentException If the value is not an integer
	 */
	default int getAsInteger(@NonNull String key) {
		String value = this.getAsString(key);
		try {
			return new StringReader(value).readInt();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as an integer: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a long for the given key.<br>
	 *
	 * @param key The key of the value
	 * @return The value as a long
	 * @throws NullPointerException If the key is null
	 * @throws IllegalArgumentException If the value is not a long
	 */
	default long getAsLong(@NonNull String key) {
		String value = this.getAsString(key);
		try {
			return new StringReader(value).readLong();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a long: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a float for the given key.<br>
	 *
	 * @param key The key of the value
	 * @return The value as a float
	 * @throws NullPointerException If the key is null
	 * @throws IllegalArgumentException If the value is not a float
	 */
	default float getAsFloat(@NonNull String key) {
		String value = this.getAsString(key);
		try {
			return new StringReader(value).readFloat();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a float: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a double for the given key.<br>
	 *
	 * @param key The key of the value
	 * @return The value as a double
	 * @throws NullPointerException If the key is null
	 * @throws IllegalArgumentException If the value is not a double
	 */
	default double getAsDouble(@NonNull String key) {
		String value = this.getAsString(key);
		try {
			return new StringReader(value).readDouble();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a double: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object for the given key.<br>
	 * The value is parsed as the result of the given parser.<br>
	 * The parser must not be able to handle null values.<br>
	 * In the case the parser is not able to parse the value, it should not return null, it should throw an exception.<br>
	 *
	 * @param key The key of the value
	 * @param parser The parser to parse the value
	 * @return The value as the result of the parser
	 * @param <T> The type of the parsed value
	 * @throws NullPointerException If the key or the parser is null
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 * @throws IllegalArgumentException If the value could not be parsed
	 */
	default <T> @NonNull T getAs(@NonNull String key, @NonNull ThrowableFunction<String, T, ? extends Exception> parser) {
		Objects.requireNonNull(parser, "Parser must not be null");
		String value = this.getAsString(key);
		try {
			return parser.apply(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed", e);
		}
	}
}
