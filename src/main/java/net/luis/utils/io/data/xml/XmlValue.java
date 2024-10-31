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
 *
 * @author Luis-St
 *
 */

public class XmlValue extends XmlElement {
	
	private final String value;
	
	public XmlValue(@NotNull String name, boolean value) {
		this(name, String.valueOf(value));
	}
	
	public XmlValue(@NotNull String name, @Nullable Number value) {
		this(name, String.valueOf(value));
	}
	
	public XmlValue(@NotNull String name, @Nullable String value) {
		super(name);
		this.value = escapeXml(String.valueOf(value));
	}
	
	public XmlValue(@NotNull String name, @NotNull XmlAttributes attributes, boolean value) {
		this(name, attributes, String.valueOf(value));
	}
	
	public XmlValue(@NotNull String name, @NotNull XmlAttributes attributes, @Nullable Number value) {
		this(name, attributes, String.valueOf(value));
	}
	
	public XmlValue(@NotNull String name, @NotNull XmlAttributes attributes, @Nullable String value) {
		super(name, attributes);
		this.value = escapeXml(String.valueOf(value));
	}
	
	public @NotNull String getRawValue() {
		return this.value;
	}
	
	public @NotNull String getUnescapedValue() {
		return unescapeXml(this.value);
	}
	
	//region Getters
	public @NotNull String getAsString() {
		return unescapeXml(this.value);
	}
	
	public boolean getAsBoolean() {
		if (StringUtils.equalsAnyIgnoreCase(this.value, "true", "false")) {
			return Boolean.parseBoolean(this.getAsString());
		}
		throw new IllegalArgumentException("Value is not a boolean");
	}
	
	public boolean getAsBoolean(boolean defaultValue) {
		try {
			return this.getAsBoolean();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public @NotNull Number getAsNumber() {
		try {
			return new StringReader(this.value).readNumber();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	public @NotNull Number getAsNumber(@NotNull Number defaultValue) {
		try {
			return this.getAsNumber();
		} catch (Exception e) {
			return Objects.requireNonNull(defaultValue, "Default value must not be null");
		}
	}
	
	public byte getAsByte() {
		try {
			return new StringReader(this.value).readByte();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	public byte getAsByte(byte defaultValue) {
		try {
			return this.getAsByte();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public short getAsShort() {
		try {
			return new StringReader(this.value).readShort();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	public short getAsShort(short defaultValue) {
		try {
			return this.getAsShort();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public int getAsInteger() {
		try {
			return new StringReader(this.value).readInt();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	public int getAsInteger(int defaultValue) {
		try {
			return this.getAsInteger();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public long getAsLong() {
		try {
			return new StringReader(this.value).readLong();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	public long getAsLong(long defaultValue) {
		try {
			return this.getAsLong();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public float getAsFloat() {
		try {
			return new StringReader(this.value).readFloat();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	public float getAsFloat(float defaultValue) {
		try {
			return this.getAsFloat();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public double getAsDouble() {
		try {
			return new StringReader(this.value).readDouble();
		} catch (InvalidStringException e) {
			throw new IllegalArgumentException(e.getMessage(), e.getCause());
		}
	}
	
	public double getAsDouble(double defaultValue) {
		try {
			return this.getAsDouble();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public <T> @NotNull T getAs(@NotNull ValueParser<String, T> parser) {
		Objects.requireNonNull(parser, "Parser must not be null");
		return parser.parse(this.value);
	}
	
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
	
	@Override
	public @NotNull String toString(@NotNull XmlConfig config) {
		StringBuilder builder = this.toBaseString(config);
		if (config.prettyPrint() && !config.simplifyValues()) {
			builder.append(System.lineSeparator()).append(config.indent());
		}
		builder.append(this.value);
		if (config.prettyPrint() && !config.simplifyValues()) {
			builder.append(System.lineSeparator());
		}
		return builder.append("</").append(this.name).append(">").toString();
	}
	//endregion
}
