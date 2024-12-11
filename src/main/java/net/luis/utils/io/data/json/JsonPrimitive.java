/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.data.json;

import net.luis.utils.io.reader.StringReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a json primitive value.<br>
 * A primitive value can be a boolean, number or string.<br>
 *
 * @author Luis-St
 */
public class JsonPrimitive implements JsonElement {
	
	/**
	 * The value of this json primitive.<br>
	 */
	private final Object value;
	
	/**
	 * Constructs a new json primitive with the given boolean value.<br>
	 * @param value The boolean value
	 */
	public JsonPrimitive(boolean value) {
		this((Object) value);
	}
	
	/**
	 * Constructs a new json primitive with the given number value.<br>
	 * @param value The number value
	 * @throws NullPointerException If the value is null
	 */
	public JsonPrimitive(@NotNull Number value) {
		this((Object) value);
	}
	
	/**
	 * Constructs a new json primitive with the given string value.<br>
	 * @param value The string value
	 * @throws NullPointerException If the value is null
	 */
	public JsonPrimitive(@NotNull String value) {
		this((Object) value);
	}
	
	/**
	 * Constructs a new json primitive with the given value.<br>
	 * Private, because the constructor ist redundant, the value must be a boolean, number or string.<br>
	 * @param value The value
	 * @throws NullPointerException If the value is null
	 */
	private JsonPrimitive(@NotNull Object value) {
		this(value, false);
	}
	
	/**
	 * Constructs a new json primitive from a object of unknown type.<br>
	 * The value must be a boolean, number or string.<br>
	 * If {@code parse} is true and the type of the value is a string, the value will be parsed to a boolean or number if possible.<br>
	 * @param value The value
	 * @param parse Whether the value should be parsed if it is a string or not
	 * @throws NullPointerException If the value is null
	 * @throws IllegalArgumentException If the value is not a boolean, number or string
	 */
	public JsonPrimitive(@NotNull Object value, boolean parse) {
		Objects.requireNonNull(value, "Value must not be null");
		switch (value) {
			case Boolean b -> this.value = value;
			case Number n -> this.value = value;
			case String s -> this.value = parse ? tryParse(s) : value;
			default -> throw new IllegalArgumentException("Value must be a boolean, number or string");
		}
	}
	
	//region Static helper methods
	
	/**
	 * Tries to parse the given string to a boolean or number.<br>
	 * @param string The string
	 * @return The parsed value or the string if it could not be parsed
	 */
	private static @NotNull Object tryParse(@NotNull String string) {
		if ("true".equalsIgnoreCase(string) || "false".equalsIgnoreCase(string)) {
			return Boolean.parseBoolean(string);
		}
		StringReader reader = new StringReader(string);
		try {
			Number number = reader.readNumber();
			reader.skipWhitespaces();
			if (reader.canRead()) {
				return string;
			}
			return number;
		} catch (Exception e) {
			return string;
		}
	}
	//endregion
	
	/**
	 * Gets the value of this json primitive as a string.<br>
	 * The value will be converted to a string by using {@link String#valueOf(Object)}.<br>
	 * @return The string representation of the value
	 */
	public @NotNull String getAsString() {
		return String.valueOf(this.value);
	}
	
	/**
	 * Gets the value of this json primitive as a boolean.<br>
	 * The value will be converted to a boolean by using the following rules:<br>
	 * <ul>
	 *     <li>If the value is a boolean, it will be returned as it is</li>
	 *     <li>If the value is a number, it will be converted to a boolean by checking if it is not 0</li>
	 *     <li>If the value is a string, it will be parsed to a boolean using {@link Boolean#parseBoolean(String)}</li>
	 * </ul>
	 * @return The boolean representation of the value
	 * @throws IllegalStateException If the value cannot be converted to a boolean
	 */
	public boolean getAsBoolean() {
		return switch (this.value) {
			case Boolean b -> b;
			case Number n -> n.intValue() != 0;
			case String s -> {
				try {
					yield new StringReader(s).readBoolean();
				} catch (Exception e) {
					throw new IllegalStateException("Cannot convert value to boolean: " + this.value, e);
				}
			}
			default -> throw new IllegalStateException("Cannot convert value to boolean: " + this.value);
		};
	}
	
	/**
	 * Gets the value of this json primitive as a number.<br>
	 * The value will be converted to a number by using the following rules:<br>
	 * <ul>
	 *     <li>If the value is a boolean, it will be converted to a number by using 1 for true and 0 for false</li>
	 *     <li>If the value is a number, it will be returned as it is</li>
	 *     <li>If the value is a string, it will be parsed to a number using {@link StringReader#readNumber()}</li>
	 * </ul>
	 * @return The number representation of the value
	 * @throws IllegalStateException If the value cannot be converted to a number
	 */
	public @NotNull Number getAsNumber() {
		return switch (this.value) {
			case Boolean b -> b ? 1 : 0;
			case Number n -> n;
			case String s -> {
				try {
					yield new StringReader(s).readNumber();
				} catch (Exception e) {
					throw new IllegalStateException("Cannot convert value to number: " + this.value, e);
				}
			}
			default -> throw new IllegalStateException("Cannot convert value to number: " + this.value);
		};
	}
	
	/**
	 * Gets the value of this json primitive as a byte.<br>
	 * The value will be converted to a number and then to a byte.<br>
	 * @return The byte representation of the value
	 * @throws IllegalStateException If the value cannot be converted to a byte
	 */
	public byte getAsByte() {
		return this.getAsNumber().byteValue();
	}
	
	/**
	 * Gets the value of this json primitive as a short.<br>
	 * The value will be converted to a number and then to a short.<br>
	 * @return The short representation of the value
	 * @throws IllegalStateException If the value cannot be converted to a short
	 */
	public short getAsShort() {
		return this.getAsNumber().shortValue();
	}
	
	/**
	 * Gets the value of this json primitive as an integer.<br>
	 * The value will be converted to a number and then to an integer.<br>
	 * @return The integer representation of the value
	 * @throws IllegalStateException If the value cannot be converted to an integer
	 */
	public int getAsInteger() {
		return this.getAsNumber().intValue();
	}
	
	/**
	 * Gets the value of this json primitive as a long.<br>
	 * The value will be converted to a number and then to a long.<br>
	 * @return The long representation of the value
	 * @throws IllegalStateException If the value cannot be converted to a long
	 */
	public long getAsLong() {
		return this.getAsNumber().longValue();
	}
	
	/**
	 * Gets the value of this json primitive as a float.<br>
	 * The value will be converted to a number and then to a float.<br>
	 * @return The float representation of the value
	 * @throws IllegalStateException If the value cannot be converted to a float
	 */
	public float getAsFloat() {
		return this.getAsNumber().floatValue();
	}
	
	/**
	 * Gets the value of this json primitive as a double.<br>
	 * The value will be converted to a number and then to a double.<br>
	 * @return The double representation of the value
	 * @throws IllegalStateException If the value cannot be converted to a double
	 */
	public double getAsDouble() {
		return this.getAsNumber().doubleValue();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof JsonPrimitive that)) return false;
		
		return this.value.equals(that.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.value);
	}
	
	@Override
	public String toString() {
		return this.toString(JsonConfig.DEFAULT);
	}
	
	@Override
	public @NotNull String toString(@Nullable JsonConfig config) {
		if (this.value instanceof String string) {
			return "\"" + string + "\"";
		}
		return this.getAsString();
	}
	//endregion
}
