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

package net.luis.utils.util.getter;

import net.luis.utils.exception.InvalidStringException;
import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.reader.StringReader;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A functional interface that provides methods to get a value of a specific type.<br>
 * The value is hold by the object that implements this interface.<br>
 * <p>
 *     The interface is functional because it has only one abstract method {@link #getAsString()}.<br>
 *     All other methods rely on this method to get the value as a string and then parse it to the desired type.<br>
 *     The {@link #getAsString()} method must bei implemented correctly, or the other methods will not work as expected.
 * </p>
 * The interface provides the following methods to get the value as a specific type:<br>
 * <ul>
 *     <li>{@link #getAsBoolean()} to get the value as a boolean</li>
 *     <li>{@link #getAsNumber()} to get the value as a number</li>
 *     <li>{@link #getAsByte()} to get the value as a byte</li>
 *     <li>{@link #getAsShort()} to get the value as a short</li>
 *     <li>{@link #getAsInteger()} to get the value as an integer</li>
 *     <li>{@link #getAsLong()} to get the value as a long</li>
 *     <li>{@link #getAsFloat()} to get the value as a float</li>
 *     <li>{@link #getAsDouble()} to get the value as a double</li>
 *     <li>{@link #getAs(ThrowableFunction)} to get the value as the result of the given parser</li>
 * </ul>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface ValueGetter {
	
	/**
	 * Returns the value which is hold by this object as a string.<br>
	 * @return The value as a string
	 */
	@NotNull String getAsString();
	
	/**
	 * Returns the value which is hold by this object as a boolean.<br>
	 *
	 * @return The value as a boolean
	 * @throws IllegalArgumentException If the value is not a boolean (e.g. not "true" or "false")
	 */
	default boolean getAsBoolean() {
		String value = this.getAsString();
		if (StringUtils.equalsAnyIgnoreCase(value, "true", "false")) {
			return Boolean.parseBoolean(value);
		}
		throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a boolean, expected 'true' or 'false'");
	}
	
	/**
	 * Returns the value which is hold by this object as a number.<br>
	 *
	 * @return The value as a number
	 * @throws IllegalArgumentException If the value is not a number
	 */
	default @NotNull Number getAsNumber() {
		String value = this.getAsString();
		try {
			return new StringReader(value).readNumber();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a number: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a byte.<br>
	 *
	 * @return The value as a byte
	 * @throws IllegalArgumentException If the value is not a byte
	 */
	default byte getAsByte() {
		String value = this.getAsString();
		try {
			return new StringReader(value).readByte();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a byte: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a short.<br>
	 *
	 * @return The value as a short
	 * @throws IllegalArgumentException If the value is not a short
	 */
	default short getAsShort() {
		String value = this.getAsString();
		try {
			return new StringReader(value).readShort();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a short: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as an integer.<br>
	 *
	 * @return The value as an integer
	 * @throws IllegalArgumentException If the value is not an integer
	 */
	default int getAsInteger() {
		String value = this.getAsString();
		try {
			return new StringReader(value).readInt();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as an integer: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a long.<br>
	 *
	 * @return The value as a long
	 * @throws IllegalArgumentException If the value is not a long
	 */
	default long getAsLong() {
		String value = this.getAsString();
		try {
			return new StringReader(value).readLong();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a long: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a float.<br>
	 *
	 * @return The value as a float
	 * @throws IllegalArgumentException If the value is not a float
	 */
	default float getAsFloat() {
		String value = this.getAsString();
		try {
			return new StringReader(value).readFloat();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a float: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a double.<br>
	 *
	 * @return The value as a double
	 * @throws IllegalArgumentException If the value is not a double
	 */
	default double getAsDouble() {
		String value = this.getAsString();
		try {
			return new StringReader(value).readDouble();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed as a double: " + e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value which is hold by this object as the result of the given parser.<br>
	 * The parser must not be able to handle null values.<br>
	 * In the case the parser is not able to parse the value, it should not return null, it should throw an exception.<br>
	 *
	 * @param parser The parser to parse the value
	 * @return The value as the result of the parser
	 * @param <T> The type of the parsed value
	 * @throws NullPointerException If the parser is null
	 * @throws IllegalArgumentException If the value could not be parsed
	 */
	default <T> @NotNull T getAs(@NotNull ThrowableFunction<String, T, ? extends Exception> parser) {
		Objects.requireNonNull(parser, "Parser must not be null");
		String value = this.getAsString();
		try {
			return parser.apply(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("Value '" + value + "' could not be parsed", e);
		}
	}
}
