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
 * Represents a xml value element.<br>
 *
 * @author Luis-St
 */
public final class XmlValue extends XmlElement implements DefaultValueGetter {
	
	/**
	 * The value of the xml element.<br>
	 */
	private final String value;
	
	/**
	 * Constructs a new xml value with the given name and boolean value.<br>
	 *
	 * @param name The name of the xml element
	 * @param value The value of the xml element
	 * @throws NullPointerException If the name is null
	 */
	public XmlValue(@NotNull String name, boolean value) {
		this(name, String.valueOf(value));
	}
	
	/**
	 * Constructs a new xml value with the given name and number value.<br>
	 *
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
	 *
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
	 *
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
	 *
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
	 *
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
