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

package net.luis.utils.io.data.properties;

import net.luis.utils.io.reader.StringReader;
import net.luis.utils.util.ValueParser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class Property {
	
	private final String key;
	private final String value;
	
	private Property(@NotNull String key, @NotNull String value) {
		this.key = Objects.requireNonNull(key, "Key must not be null");
		this.value = Objects.requireNonNull(value, "Value must not be null");
	}
	
	public static @NotNull Property of(@NotNull String key, @NotNull String value) {
		return new Property(key, value);
	}
	
	public final @NotNull String getKey() {
		return this.key;
	}
	
	public final @NotNull String getRawValue() {
		return this.value;
	}
	
	//region Getters
	public @NotNull String getString() {
		return this.value;
	}
	
	public boolean getBoolean() {
		return Boolean.parseBoolean(this.getString());
	}
	
	public boolean getBoolean(boolean defaultValue) {
		try {
			return this.getBoolean();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public @NotNull Number getNumber() {
		return new StringReader(this.getString()).readNumber();
	}
	
	public @NotNull Number getNumber(@NotNull Number defaultValue) {
		try {
			return this.getNumber();
		} catch (Exception e) {
			return Objects.requireNonNull(defaultValue, "Default value must not be null");
		}
	}
	
	public byte getByte() {
		return this.getNumber().byteValue();
	}
	
	public byte getByte(byte defaultValue) {
		try {
			return this.getByte();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public short getShort() {
		return this.getNumber().shortValue();
	}
	
	public short getShort(short defaultValue) {
		try {
			return this.getShort();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public int getInteger() {
		return this.getNumber().intValue();
	}
	
	public int getInteger(int defaultValue) {
		try {
			return this.getInteger();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public long getLong() {
		return this.getNumber().longValue();
	}
	
	public long getLong(long defaultValue) {
		try {
			return this.getLong();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public float getFloat() {
		return this.getNumber().floatValue();
	}
	
	public float getFloat(float defaultValue) {
		try {
			return this.getFloat();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public double getDouble() {
		return this.getNumber().doubleValue();
	}
	
	public double getDouble(double defaultValue) {
		try {
			return this.getDouble();
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public <T> @NotNull T get(@NotNull ValueParser<String, T> parser) {
		Objects.requireNonNull(parser, "Parser must not be null");
		return parser.parse(this.getString());
	}
	
	public <T> @NotNull T get(@NotNull ValueParser<String, T> parser, @NotNull T defaultValue) {
		Objects.requireNonNull(parser, "Parser must not be null");
		try {
			return this.get(parser);
		} catch (Exception e) {
			return Objects.requireNonNull(defaultValue, "Default value must not be null");
		}
	}
	//endregion
	
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
	
	public @NotNull String toString(@NotNull PropertyConfig config) {
		return this.key + config.separator() + this.value;
	}
	//endregion
}
