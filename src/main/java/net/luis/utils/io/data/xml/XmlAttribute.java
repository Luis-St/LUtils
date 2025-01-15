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

package net.luis.utils.io.data.xml;

import net.luis.utils.util.getter.DefaultValueGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.luis.utils.io.data.xml.XmlHelper.*;

/**
 * Represents an xml attribute.<br>
 * An xml attribute consists of a name and a value.<br>
 * The name and the value are both strings.<br>
 *
 * @author Luis-St
 */
public class XmlAttribute implements DefaultValueGetter {
	
	/**
	 * The name of the attribute.<br>
	 */
	private final String name;
	/**
	 * The value of the attribute.<br>
	 */
	private final String value;
	
	/**
	 * Constructs a new xml attribute with the given name and boolean value.<br>
	 * @param name The name of the attribute
	 * @param value The boolean value of the attribute
	 * @throws IllegalArgumentException If the name is invalid (e.g. empty, blank, or does not match the pattern {@link XmlHelper#XML_ATTRIBUTE_NAME_PATTERN})
	 */
	public XmlAttribute(@NotNull String name, boolean value) {
		this(name, String.valueOf(value));
	}
	
	/**
	 * Constructs a new xml attribute with the given name and number value.<br>
	 * @param name The name of the attribute
	 * @param value The number value of the attribute
	 * @throws IllegalArgumentException If the name is invalid (e.g. empty, blank, or does not match the pattern {@link XmlHelper#XML_ATTRIBUTE_NAME_PATTERN})
	 */
	public XmlAttribute(@NotNull String name, @Nullable Number value) {
		this(name, String.valueOf(value));
	}
	
	/**
	 * Constructs a new xml attribute with the given name and string value.<br>
	 * The value will be xml-escaped automatically.<br>
	 * @param name The name of the attribute
	 * @param value The string value of the attribute
	 * @throws IllegalArgumentException If the name is invalid (e.g. empty, blank, or does not match the pattern {@link XmlHelper#XML_ATTRIBUTE_NAME_PATTERN})
	 */
	public XmlAttribute(@NotNull String name, @Nullable String value) {
		this.name = validateAttributeKey(name);
		this.value = escapeXml(String.valueOf(value));
	}
	
	/**
	 * Returns the name of the attribute.<br>
	 * @return The name
	 */
	public @NotNull String getName() {
		return this.name;
	}
	
	/**
	 * Returns the raw unescaped value of the attribute.<br>
	 * @return The raw value
	 */
	public @NotNull String getRawValue() {
		return this.value;
	}
	
	/**
	 * Returns the unescaped value of the attribute.<br>
	 * @return The unescaped value
	 */
	public @NotNull String getUnescapedValue() {
		return unescapeXml(this.value);
	}
	
	/**
	 * {@inheritDoc}
	 * This method is equivalent to {@link #getUnescapedValue()}.<br>
	 * @return The value as a string
	 */
	@Override
	public @NotNull String getAsString() {
		return this.getUnescapedValue();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof XmlAttribute that)) return false;
		
		if (!this.name.equals(that.name)) return false;
		return this.value.equals(that.value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.value);
	}
	
	@Override
	public String toString() {
		return this.toString(XmlConfig.DEFAULT);
	}
	
	/**
	 * Returns the string representation of the attribute.<br>
	 * The string representation is in the form of {@code name="value"}.<br>
	 * @param config The xml config to use for the string representation (unused)
	 * @return The string representation
	 */
	public @NotNull String toString(@Nullable XmlConfig config) {
		return this.name + "=\"" + this.value + "\"";
	}
	//endregion
}
