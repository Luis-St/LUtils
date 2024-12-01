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

package net.luis.utils.io.data.xml;

import net.luis.utils.exception.InvalidStringException;
import net.luis.utils.io.reader.StringReader;
import net.luis.utils.util.ValueParser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.luis.utils.io.data.xml.XmlHelper.*;

/**
 * Represents a xml value element.<br>
 *
 * @author Luis-St
 */
public final class XmlValue extends XmlElement {
	
	/**
	 * The value of the xml element.<br>
	 */
	private final String value;
	
	/**
	 * Constructs a new xml value with the given name and boolean value.<br>
	 * @param name The name of the xml element
	 * @param value The value of the xml element
	 * @throws NullPointerException If the name is null
	 */
	public XmlValue(@NotNull String name, boolean value) {
		this(name, String.valueOf(value));
	}
	
	/**
	 * Constructs a new xml value with the given name and number value.<br>
	 * @param name The name of the xml element
	 * @param value The value of the xml element
	 * @throws NullPointerException If the name is null
	 */
	public XmlValue(@NotNull String name, @Nullable Number value) {
		this(name, String.valueOf(value));
	}
	
	/**
	 * Constructs a new xml value with the given name and string value.<br>
	 * The value will be escaped automatically.<br>
	 * @param name The name of the xml element
	 * @param value The value of the xml element
	 * @throws NullPointerException If the name is null
	 */
	public XmlValue(@NotNull String name, @Nullable String value) {
		super(name);
		this.value = escapeXml(String.valueOf(value));
	}
	
	/**
	 * Constructs a new xml value with the given name, attributes and boolean value.<br>
	 * @param name The name of the xml element
	 * @param attributes The attributes of the xml element
	 * @param value The value of the xml element
	 * @throws NullPointerException If the name or attributes are null
	 */
	public XmlValue(@NotNull String name, @NotNull XmlAttributes attributes, boolean value) {
		this(name, attributes, String.valueOf(value));
	}
	
	/**
	 * Constructs a new xml value with the given name, attributes and number value.<br>
	 * @param name The name of the xml element
	 * @param attributes The attributes of the xml element
	 * @param value The value of the xml element
	 */
	public XmlValue(@NotNull String name, @NotNull XmlAttributes attributes, @Nullable Number value) {
		this(name, attributes, String.valueOf(value));
	}
	
	/**
	 * Constructs a new xml value with the given name, attributes and string value.<br>
	 * The value will be escaped automatically.<br>
	 * @param name The name of the xml element
	 * @param attributes The attributes of the xml element
	 * @param value The value of the xml element
	 * @throws NullPointerException If the name or attributes are null
	 */
	public XmlValue(@NotNull String name, @NotNull XmlAttributes attributes, @Nullable String value) {
		super(name, attributes);
		this.value = escapeXml(String.valueOf(value));
	}
	
	@Override
	protected @NotNull String getElementType() {
		return "xml value";
	}
	
	/**
	 * @return Always false
	 */
	@Override
	public boolean isSelfClosing() {
		return false;
	}
	
	/**
	 * Returns the raw unescaped value of the element.<br>
	 * @return The raw value
	 */
	public @NotNull String getRawValue() {
		return this.value;
	}
	
	/**
	 * Returns the unescaped value of the element.<br>
	 * @return The unescaped value
	 */
	public @NotNull String getUnescapedValue() {
		return unescapeXml(this.value);
	}
	
	//region Getters
	
	/**
	 * Returns the value of the element as a string.<br>
	 * This method is equivalent to {@link #getUnescapedValue()}.<br>
	 * @return The value as a string
	 */
	public @NotNull String getAsString() {
		return this.getUnescapedValue();
	}
	
	/**
	 * Returns the value of the element as a boolean.<br>
	 * @return The value as a boolean
	 * @throws IllegalArgumentException If the value is not a boolean (e.g. not "true" or "false")
	 */
	public boolean getAsBoolean() {
		if (StringUtils.equalsAnyIgnoreCase(this.value, "true", "false")) {
			return Boolean.parseBoolean(this.value);
		}
		throw new IllegalArgumentException("Value is not a boolean");
	}
	
	/**
	 * Returns the value of the element as a boolean.<br>
	 * If the value is not a boolean, the default value will be returned.<br>
	 * @param defaultValue The default value
	 * @return The value as a boolean or the default value
	 */
	public boolean getAsBoolean(boolean defaultValue) {
		try {
			return this.getAsBoolean();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the element as a number.<br>
	 * @return The value as a number
	 * @throws IllegalArgumentException If the value is not a number
	 */
	public @NotNull Number getAsNumber() {
		try {
			return new StringReader(this.value).readNumber();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the element as a number.<br>
	 * If the value is not a number, the default value will be returned.<br>
	 * @param defaultValue The default value
	 * @return The value as a number or the default value
	 * @throws NullPointerException If the default value is null
	 */
	public @NotNull Number getAsNumber(@NotNull Number defaultValue) {
		Objects.requireNonNull(defaultValue, "Default value must not be null");
		try {
			return this.getAsNumber();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the element as a byte.<br>
	 * @return The value as a byte
	 * @throws IllegalArgumentException If the value is not a byte or cannot be converted to a byte (out of range)
	 */
	public byte getAsByte() {
		try {
			return new StringReader(this.value).readByte();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the element as a byte.<br>
	 * If the value is not a byte or cannot be converted to a byte (out of range), the default value will be returned.<br>
	 * @param defaultValue The default value
	 * @return The value as a byte or the default value
	 */
	public byte getAsByte(byte defaultValue) {
		try {
			return this.getAsByte();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the element as a short.<br>
	 * @return The value as a short
	 * @throws IllegalArgumentException If the value is not a short or cannot be converted to a short (out of range)
	 */
	public short getAsShort() {
		try {
			return new StringReader(this.value).readShort();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the element as a short.<br>
	 * If the value is not a short or cannot be converted to a short (out of range), the default value will be returned.<br>
	 * @param defaultValue The default value
	 * @return The value as a short or the default value
	 */
	public short getAsShort(short defaultValue) {
		try {
			return this.getAsShort();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the element as an integer.<br>
	 * @return The value as an integer
	 * @throws IllegalArgumentException If the value is not an integer or cannot be converted to an integer (out of range)
	 */
	public int getAsInteger() {
		try {
			return new StringReader(this.value).readInt();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the element as an integer.<br>
	 * If the value is not an integer or cannot be converted to an integer (out of range), the default value will be returned.<br>
	 * @param defaultValue The default value
	 * @return The value as an integer or the default value
	 */
	public int getAsInteger(int defaultValue) {
		try {
			return this.getAsInteger();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the element as a long.<br>
	 * @return The value as a long
	 * @throws IllegalArgumentException If the value is not a long or cannot be converted to a long (out of range)
	 */
	public long getAsLong() {
		try {
			return new StringReader(this.value).readLong();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the element as a long.<br>
	 * If the value is not a long or cannot be converted to a long (out of range), the default value will be returned.<br>
	 * @param defaultValue The default value
	 * @return The value as a long or the default value
	 */
	public long getAsLong(long defaultValue) {
		try {
			return this.getAsLong();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the element as a float.<br>
	 * @return The value as a float
	 * @throws IllegalArgumentException If the value is not a float or cannot be converted to a float (out of range)
	 */
	public float getAsFloat() {
		try {
			return new StringReader(this.value).readFloat();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the element as a float.<br>
	 * If the value is not a float or cannot be converted to a float (out of range), the default value will be returned.<br>
	 * @param defaultValue The default value
	 * @return The value as a float or the default value
	 */
	public float getAsFloat(float defaultValue) {
		try {
			return this.getAsFloat();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the element as a double.<br>
	 * @return The value as a double
	 * @throws IllegalArgumentException If the value is not a double or cannot be converted to a double (out of range)
	 */
	public double getAsDouble() {
		try {
			return new StringReader(this.value).readDouble();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the element as a double.<br>
	 * If the value is not a double or cannot be converted to a double (out of range), the default value will be returned.<br>
	 * @param defaultValue The default value
	 * @return The value as a double or the default value
	 */
	public double getAsDouble(double defaultValue) {
		try {
			return this.getAsDouble();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value of the element as the result of the given parser.<br>
	 * The parser must not be able to handle null values.<br>
	 * @param parser The parser to use
	 * @return The value as the result of the parser
	 * @param <T> The type of the result
	 * @throws NullPointerException If the parser is null
	 */
	public <T> @NotNull T getAs(@NotNull ValueParser<String, T> parser) {
		Objects.requireNonNull(parser, "Parser must not be null");
		try {
			return parser.parse(this.value);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Returns the value of the element as the result of the given parser.<br>
	 * If the parser is unable to parse the value, the default value will be returned.<br>
	 * @param parser The parser to use
	 * @param defaultValue The default value
	 * @return The value as the result of the parser or the default value
	 * @param <T> The type of the result
	 * @throws NullPointerException If the parser or the default value is null
	 */
	public <T> @NotNull T getAs(@NotNull ValueParser<String, T> parser, @NotNull T defaultValue) {
		Objects.requireNonNull(parser, "Parser must not be null");
		try {
			return this.getAs(parser);
		} catch (Exception e) {
			return Objects.requireNonNull(defaultValue, "Default value must not be null");
		}
	}
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof XmlValue that)) return false;
		if (!super.equals(o)) return false;
		
		return this.value.equals(that.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.value);
	}
	
	/**
	 * Returns a string representation of this value.<br>
	 * @param config The xml config to use for the string representation
	 * @return The string representation
	 * @throws NullPointerException If the config is null
	 */
	@Override
	public @NotNull String toString(@NotNull XmlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		StringBuilder builder = this.toBaseString(config);
		if (config.prettyPrint() && !config.simplifyValues()) {
			builder.append(System.lineSeparator()).append(config.indent());
		}
		builder.append(this.value);
		if (config.prettyPrint() && !config.simplifyValues()) {
			builder.append(System.lineSeparator());
		}
		return builder.append("</").append(this.getName()).append(">").toString();
	}
	//endregion
}
