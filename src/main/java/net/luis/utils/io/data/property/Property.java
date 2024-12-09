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

package net.luis.utils.io.data.property;

import net.luis.utils.exception.InvalidStringException;
import net.luis.utils.io.reader.StringReader;
import net.luis.utils.util.ValueParser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a property with a key and a value.<br>
 * The key and the value are both strings.<br>
 * The value can be parsed to different types using the methods provided by this class.<br>
 *
 * @author Luis-St
 */
public class Property {
	
	/**
	 * The key of the property.<br>
	 */
	private final String key;
	/**
	 * The value of the property.<br>
	 */
	private final String value;
	
	/**
	 * Constructs a new property with the given key and value.<br>
	 * @param key The key of the property
	 * @param value The value of the property
	 * @throws NullPointerException If the key or the value is null
	 */
	private Property(@NotNull String key, @NotNull String value) {
		this.key = Objects.requireNonNull(key, "Key must not be null");
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	/**
	 * Creates a new property with the given key and value.<br>
	 * @param key The key of the property
	 * @param value The value of the property
	 * @return A new property
	 * @throws NullPointerException If the key or the value is null
	 */
	public static @NotNull Property of(@NotNull String key, @NotNull String value) {
		return new Property(key, value);
	}
	
	/**
	 * Returns the key of the property as a string.<br>
	 * @return The key of the property
	 */
	public @NotNull String getKey() {
		return this.key;
	}
	
	/**
	 * Returns the value of the property as a string.<br>
	 * @return The value of the property
	 */
	public @NotNull String getRawValue() {
		return this.value;
	}
	
	//region Getters
	
	/**
	 * Returns the value of the property as a string.<br>
	 * Equivalent to {@link #getRawValue()}.<br>
	 * @return The value of the property
	 */
	public @NotNull String getString() {
		return this.value;
	}
	
	/**
	 * Returns the value of the property as a boolean.<br>
	 * @return The value of the property
	 * @throws IllegalArgumentException If the value is not a boolean
	 */
	public boolean getBoolean() {
		if (StringUtils.equalsAnyIgnoreCase(this.value, "true", "false")) {
			return Boolean.parseBoolean(this.getString());
		}
		throw new IllegalArgumentException("Value is not a boolean");
	}
	
	/**
	 * Returns the value of the property as a boolean.<br>
	 * In case the value is not a boolean, the given default value is returned.<br>
	 * @param defaultValue The default value
	 * @return The value of the property
	 */
	public boolean getBoolean(boolean defaultValue) {
		try {
			return this.getBoolean();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the property as a number.<br>
	 * @return The value of the property
	 * @throws IllegalArgumentException If the value is not a number
	 */
	public @NotNull Number getNumber() {
		try {
			return new StringReader(this.value).readNumber();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the property as a number.<br>
	 * In case the value is not a number, the given default value is returned.<br>
	 * @param defaultValue The default value
	 * @return The value of the property
	 * @throws NullPointerException If the default value is null
	 */
	public @NotNull Number getNumber(@NotNull Number defaultValue) {
		try {
			return this.getNumber();
		} catch (Exception e) {
			return Objects.requireNonNull(defaultValue, "Default value must not be null");
		}
	}
	
	/**
	 * Returns the value of the property as a byte.<br>
	 * @return The value of the property
	 * @throws IllegalArgumentException If the value is not a byte
	 */
	public byte getByte() {
		try {
			return new StringReader(this.value).readByte();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the property as a byte.<br>
	 * In case the value is not a byte, the given default value is returned.<br>
	 * @param defaultValue The default value
	 * @return The value of the property
	 */
	public byte getByte(byte defaultValue) {
		try {
			return this.getByte();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the property as a short.<br>
	 * @return The value of the property
	 * @throws IllegalArgumentException If the value is not a short
	 */
	public short getShort() {
		try {
			return new StringReader(this.value).readShort();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the property as a short.<br>
	 * In case the value is not a short, the given default value is returned.<br>
	 * @param defaultValue The default value
	 * @return The value of the property
	 */
	public short getShort(short defaultValue) {
		try {
			return this.getShort();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the property as an integer.<br>
	 * @return The value of the property
	 * @throws IllegalArgumentException If the value is not an integer
	 */
	public int getInteger() {
		try {
			return new StringReader(this.value).readInt();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the property as an integer.<br>
	 * In case the value is not an integer, the given default value is returned.<br>
	 * @param defaultValue The default value
	 * @return The value of the property
	 */
	public int getInteger(int defaultValue) {
		try {
			return this.getInteger();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the property as a long.<br>
	 * @return The value of the property
	 * @throws IllegalArgumentException If the value is not a long
	 */
	public long getLong() {
		try {
			return new StringReader(this.value).readLong();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the property as a long.<br>
	 * In case the value is not a long, the given default value is returned.<br>
	 * @param defaultValue The default value
	 * @return The value of the property
	 */
	public long getLong(long defaultValue) {
		try {
			return this.getLong();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the property as a float.<br>
	 * @return The value of the property
	 * @throws IllegalArgumentException If the value is not a float
	 */
	public float getFloat() {
		try {
			return new StringReader(this.value).readFloat();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the property as a float.<br>
	 * In case the value is not a float, the given default value is returned.<br>
	 * @param defaultValue The default value
	 * @return The value of the property
	 */
	public float getFloat(float defaultValue) {
		try {
			return this.getFloat();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the property as a double.<br>
	 * @return The value of the property
	 * @throws IllegalArgumentException If the value is not a double
	 */
	public double getDouble() {
		try {
			return new StringReader(this.value).readDouble();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the property as a double.<br>
	 * In case the value is not a double, the given default value is returned.<br>
	 * @param defaultValue The default value
	 * @return The value of the property
	 */
	public double getDouble(double defaultValue) {
		try {
			return this.getDouble();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the property as a parsed value.<br>
	 * @param parser The parser to parse the value
	 * @return The value of the property
	 * @param <T> The type of the parsed value
	 * @throws NullPointerException If the parser is null
	 * @throws IllegalArgumentException If the value cannot be parsed (optional)
	 */
	public <T> @NotNull T get(@NotNull ValueParser<String, T> parser) {
		Objects.requireNonNull(parser, "Parser must not be null");
		return parser.parse(this.value);
	}
	
	/**
	 * Returns the value of the property as a parsed value.<br>
	 * In case the value cannot be parsed, the given default value is returned.<br>
	 * @param parser The parser to parse the value
	 * @param defaultValue The default value
	 * @return The value of the property
	 * @param <T> The type of the parsed value
	 * @throws NullPointerException If the parser or the default value is null
	 */
	public <T> @NotNull T get(@NotNull ValueParser<String, T> parser, @NotNull T defaultValue) {
		Objects.requireNonNull(parser, "Parser must not be null");
		try {
			return this.get(parser);
		} catch (Exception e) {
			return Objects.requireNonNull(defaultValue, "Default value must not be null");
		}
	}
	//endregion
	
	/**
	 * Returns whether the key of the property is part of the given group.<br>
	 * A group is a string that ends optionally with a dot.<br>
	 * If the group is empty, the key is always part of the group.<br>
	 * Null will be treated as an empty string.<br>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * Property property = Property.of("this.is.an.example.key", "value");
	 * property.isPartOfGroup(null); // true
	 * property.isPartOfGroup(""); // true
	 * property.isPartOfGroup("this"); // true
	 * property.isPartOfGroup("this.is"); // true
	 * property.isPartOfGroup("this.is.an.example.key"); // false -> 'key' is not part of the group, it is the key itself
	 * property.isPartOfGroup("some.other.group"); // false
	 * }</pre>
	 * @param group The group to check
	 * @return Whether the key is part of the group or not
	 * @throws IllegalArgumentException If the group is blank or starts with a dot
	 */
	public boolean isPartOfGroup(@Nullable String group) {
		if (StringUtils.isEmpty(group)) {
			return true;
		}
		if (group.isBlank()) {
			throw new IllegalArgumentException("Group must not be blank");
		}
		if (group.startsWith(".")) {
			throw new IllegalArgumentException("Group must not start with a dot");
		}
		if (!group.endsWith(".")) {
			group += ".";
		}
		return this.key.startsWith(group);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof Property property)) return false;
		
		if (!this.key.equals(property.key)) return false;
		return this.value.equals(property.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key, this.value);
	}
	
	@Override
	public String toString() {
		return this.key + "=" + this.value;
	}
	
	/**
	 * Returns the property as a string with the given configuration.<br>
	 * @param config The configuration to use
	 * @return The property as a string
	 * @throws NullPointerException If the configuration is null
	 */
	public @NotNull String toString(@NotNull PropertyConfig config) {
		Objects.requireNonNull(config, "Property config must not be null");
		String alignment = " ".repeat(config.alignment());
		return this.key + alignment + config.separator() + alignment + this.value;
	}
	//endregion
}
