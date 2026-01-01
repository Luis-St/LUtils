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
 * A functional interface that provides methods to get a value of a specific type at a specific index.<br>
 * The value is hold by the object that implements this interface.<br>
 * <p>
 *     The interface is functional because it has only one abstract method {@link #getAsString(int)}.<br>
 *     All other methods rely on this method to get the value as a string and then parse it to the desired type.<br>
 *     The {@link #getAsString(int)} method must bei implemented correctly, or the other methods will not work as expected.
 * </p>
 * The interface provides the following methods to get the value as a specific type:<br>
 * <ul>
 *     <li>{@link #getAsBoolean(int)} to get the value as a boolean</li>
 *     <li>{@link #getAsNumber(int)} to get the value as a number</li>
 *     <li>{@link #getAsByte(int)} to get the value as a byte</li>
 *     <li>{@link #getAsShort(int)} to get the value as a short</li>
 *     <li>{@link #getAsInteger(int)} to get the value as an integer</li>
 *     <li>{@link #getAsLong(int)} to get the value as a long</li>
 *     <li>{@link #getAsFloat(int)} to get the value as a float</li>
 *     <li>{@link #getAsDouble(int)} to get the value as a double</li>
 *     <li>{@link #getAs(int, ThrowableFunction)} to get the value as the result of the given parser</li>
 * </ul>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface IndexedValueGetter {
	
	/**
	 * Returns the value which is hold by this object as a string at the given index.<br>
	 *
	 * @param index The index of the value
	 * @return The value as a string
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 */
	@NonNull String getAsString(int index);
	
	/**
	 * Returns the value which is hold by this object as a boolean at the given index.<br>
	 *
	 * @param index The index of the value
	 * @return The value as a boolean
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 * @throws IllegalArgumentException If the value is not a boolean (e.g. not "true" or "false")
	 */
	default boolean getAsBoolean(int index) {
		String value = this.getAsString(index);
		if (Strings.CI.equalsAny(value, "true", "false")) {
			return Boolean.parseBoolean(value);
		}
		throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a boolean, expected 'true' or 'false'");
	}
	
	/**
	 * Returns the value which is hold by this object as a number at the given index.<br>
	 *
	 * @param index The index of the value
	 * @return The value as a number
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 * @throws IllegalArgumentException If the value is not a number
	 */
	default @NonNull Number getAsNumber(int index) {
		String value = this.getAsString(index);
		try {
			return new StringReader(value).readNumber();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a number: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a byte at the given index.<br>
	 *
	 * @param index The index of the value
	 * @return The value as a byte
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 * @throws IllegalArgumentException If the value is not a byte
	 */
	default byte getAsByte(int index) {
		String value = this.getAsString(index);
		try {
			return new StringReader(value).readByte();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a byte: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a short at the given index.<br>
	 *
	 * @param index The index of the value
	 * @return The value as a short
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 * @throws IllegalArgumentException If the value is not a short
	 */
	default short getAsShort(int index) {
		String value = this.getAsString(index);
		try {
			return new StringReader(value).readShort();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a short: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as an integer at the given index.<br>
	 *
	 * @param index The index of the value
	 * @return The value as an integer
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 * @throws IllegalArgumentException If the value is not an integer
	 */
	default int getAsInteger(int index) {
		String value = this.getAsString(index);
		try {
			return new StringReader(value).readInt();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as an integer: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a long at the given index.<br>
	 *
	 * @param index The index of the value
	 * @return The value as a long
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 * @throws IllegalArgumentException If the value is not a long
	 */
	default long getAsLong(int index) {
		String value = this.getAsString(index);
		try {
			return new StringReader(value).readLong();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a long: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a float at the given index.<br>
	 *
	 * @param index The index of the value
	 * @return The value as a float
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 * @throws IllegalArgumentException If the value is not a float
	 */
	default float getAsFloat(int index) {
		String value = this.getAsString(index);
		try {
			return new StringReader(value).readFloat();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a float: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a double at the given index.<br>
	 *
	 * @param index The index of the value
	 * @return The value as a double
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 * @throws IllegalArgumentException If the value is not a double
	 */
	default double getAsDouble(int index) {
		String value = this.getAsString(index);
		try {
			return new StringReader(value).readDouble();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a double: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object at the given index.<br>
	 * The value is parsed as the result of the given parser.<br>
	 * The parser must not be able to handle null values.<br>
	 * In the case the parser is not able to parse the value, it should not return null, it should throw an exception.<br>
	 *
	 * @param index The index of the value
	 * @param parser The parser to parse the value
	 * @return The value as the result of the parser
	 * @param <T> The type of the parsed value
	 * @throws NullPointerException If the parser is null
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 * @throws IllegalArgumentException If the value could not be parsed
	 */
	default <T> @NonNull T getAs(int index, @NonNull ThrowableFunction<String, T, ? extends Exception> parser) {
		Objects.requireNonNull(parser, "Parser must not be null");
		String value = this.getAsString(index);
		try {
			return parser.apply(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed", e);
		}
	}
}
