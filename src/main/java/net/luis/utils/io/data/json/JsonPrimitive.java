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

package net.luis.utils.io.data.json;

import net.luis.utils.io.reader.StringReader;
import net.luis.utils.util.getter.ValueGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a json primitive value.<br>
 * A primitive value can be a boolean, number or string.<br>
 *
 * @author Luis-St
 */
public class JsonPrimitive implements JsonElement, ValueGetter {
	
	/**
	 * The value of this json primitive.<br>
	 */
	private final String value;
	
	/**
	 * Constructs a new json primitive with the given boolean value.<br>
	 * @param value The boolean value
	 */
	public JsonPrimitive(boolean value) {
		this(String.valueOf(value));
	}
	
	/**
	 * Constructs a new json primitive with the given number value.<br>
	 *
	 * @param value The number value
	 * @throws NullPointerException If the value is null
	 */
	public JsonPrimitive(@NotNull Number value) {
		this(String.valueOf(Objects.requireNonNull(value, "Value must not be null")));
	}
	
	/**
	 * Constructs a new json primitive with the given string value.<br>
	 *
	 * @param value The string value
	 * @throws NullPointerException If the value is null
	 */
	public JsonPrimitive(@NotNull String value) {
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	//region Static helper methods
	
	/**
	 * Tries to parse the given string to a boolean or number.<br>
	 *
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
	
	@Override
	public @NotNull String getAsString() {
		return String.valueOf(this.value);
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
		Object value = tryParse(this.value);
		if (value instanceof String string) {
			return "\"" + string + "\"";
		}
		return this.getAsString();
	}
	//endregion
}
