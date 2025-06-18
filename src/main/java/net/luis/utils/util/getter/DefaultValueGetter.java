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

import net.luis.utils.function.throwable.ThrowableFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A functional interface that provides methods to get a value of a specific type.<br>
 * This is an extension of the {@link ValueGetter} interface.<br>
 * <p>
 *     This interface provides safe methods to get the value as a specific type with a default value.<br>
 *     The default value will be returned if the value could not be parsed to the desired type.<br>
 *     The following methods are provided to get the value as a specific type with a default value:
 * </p>
 * <ul>
 *     <li>{@link #getAsBoolean(boolean)} to get the value as a boolean</li>
 *     <li>{@link #getAsNumber(Number)} to get the value as a number</li>
 *     <li>{@link #getAsByte(byte)} to get the value as a byte</li>
 *     <li>{@link #getAsShort(short)} to get the value as a short</li>
 *     <li>{@link #getAsInteger(int)} to get the value as an integer</li>
 *     <li>{@link #getAsLong(long)} to get the value as a long</li>
 *     <li>{@link #getAsFloat(float)} to get the value as a float</li>
 *     <li>{@link #getAsDouble(double)} to get the value as a double</li>
 *     <li>{@link #getAs(ThrowableFunction, Object)} to get the value as the result of the given parser</li>
 * </ul>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface DefaultValueGetter extends ValueGetter {
	
	/**
	 * Returns the value which is hold by this object as a boolean.<br>
	 * If the value is not a boolean, the default value will be returned.<br>
	 *
	 * @param defaultValue The default value
	 * @return The value as a boolean or the default value
	 */
	default boolean getAsBoolean(boolean defaultValue) {
		try {
			return this.getAsBoolean();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a number.<br>
	 * If the value is not a number, the default value will be returned.<br>
	 *
	 * @param defaultValue The default value
	 * @return The value as a number or the default value
	 * @throws NullPointerException If the default value is null
	 */
	default @NotNull Number getAsNumber(@NotNull Number defaultValue) {
		Objects.requireNonNull(defaultValue, "Default value must not be null");
		try {
			return this.getAsNumber();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a byte.<br>
	 * If the value is not a byte, the default value will be returned.<br>
	 *
	 * @param defaultValue The default value
	 * @return The value as a byte or the default value
	 */
	default byte getAsByte(byte defaultValue) {
		try {
			return this.getAsByte();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a short.<br>
	 * If the value is not a short, the default value will be returned.<br>
	 *
	 * @param defaultValue The default value
	 * @return The value as a short or the default value
	 */
	default short getAsShort(short defaultValue) {
		try {
			return this.getAsShort();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value which is hold by this object as an integer.<br>
	 * If the value is not an integer, the default value will be returned.<br>
	 *
	 * @param defaultValue The default value
	 * @return The value as an integer or the default value
	 */
	default int getAsInteger(int defaultValue) {
		try {
			return this.getAsInteger();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a long.<br>
	 * If the value is not a long, the default value will be returned.<br>
	 *
	 * @param defaultValue The default value
	 * @return The value as a long or the default value
	 */
	default long getAsLong(long defaultValue) {
		try {
			return this.getAsLong();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a float.<br>
	 * If the value is not a float, the default value will be returned.<br>
	 *
	 * @param defaultValue The default value
	 * @return The value as a float or the default value
	 */
	default float getAsFloat(float defaultValue) {
		try {
			return this.getAsFloat();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value which is hold by this object as a double.<br>
	 * If the value is not a double, the default value will be returned.<br>
	 *
	 * @param defaultValue The default value
	 * @return The value as a double or the default value
	 */
	default double getAsDouble(double defaultValue) {
		try {
			return this.getAsDouble();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value which is hold by this object as the result of the given parser.<br>
	 * The parser must not be able to handle null values.<br>
	 * In the case the parser is not able to parse the value, it should not return null, it should throw an exception.<br>
	 * If the parser throws an exception, the default value will be returned.<br>
	 *
	 * @param parser The parser to parse the value
	 * @param defaultValue The default value
	 * @return The value as the result of the parser or the default value
	 * @param <T> The type of the parsed value
	 * @throws NullPointerException If the parser or the default value is null
	 */
	default <T> @NotNull T getAs(@NotNull ThrowableFunction<String, T, ? extends Exception> parser, @NotNull T defaultValue) {
		Objects.requireNonNull(parser, "Parser must not be null");
		Objects.requireNonNull(defaultValue, "Default value must not be null");
		try {
			return this.getAs(parser);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
