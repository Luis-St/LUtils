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

import net.luis.utils.io.data.xml.exception.XmlTypeException;
import net.luis.utils.util.ValueParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.luis.utils.io.data.xml.XmlHelper.*;

/**
 *
 * @author Luis-St
 *
 */

public sealed class XmlElement permits XmlContainer, XmlValue {
	
	private final String name;
	private final XmlAttributes attributes;
	
	public XmlElement(@NotNull String name) {
		this(name, new XmlAttributes());
	}
	
	public XmlElement(@NotNull String name, @NotNull XmlAttributes attributes) {
		this.name = validateElementName(name);
		this.attributes = Objects.requireNonNull(attributes, "Attributes must not be null");
	}
	
	protected @NotNull String getElementType() {
		return "xml self-closing element";
	}
	
	public boolean isSelfClosing() {
		return true;
	}
	
	public boolean isXmlContainer() {
		return this instanceof XmlContainer;
	}
	
	public boolean isXmlValue() {
		return this instanceof XmlValue;
	}
	
	public @NotNull XmlContainer getAsXmlContainer() {
		if (this instanceof XmlContainer container) {
			return container;
		}
		throw new XmlTypeException("Expected a xml container, but found: " + this.getElementType());
	}
	
	public @NotNull XmlValue getAsXmlValue() {
		if (this instanceof XmlValue value) {
			return value;
		}
		throw new XmlTypeException("Expected a xml value, but found: " + this.getElementType());
	}
	
	public @NotNull String getName() {
		return this.name;
	}
	
	public @NotNull XmlAttributes getAttributes() {
		return this.attributes;
	}
	
	//region Add attribute
	public @Nullable XmlAttribute addAttribute(@Nullable XmlAttribute attribute) {
		return this.attributes.add(attribute);
	}
	
	public @Nullable XmlAttribute addAttribute(@NotNull String key, @Nullable String value) {
		return this.attributes.add(key, value);
	}
	
	public @Nullable XmlAttribute addAttribute(@NotNull String key, boolean value) {
		return this.attributes.add(key, value);
	}
	
	public @Nullable XmlAttribute addAttribute(@NotNull String key, @Nullable Number value) {
		return this.attributes.add(key, value);
	}
	
	public @Nullable XmlAttribute addAttribute(@NotNull String key, byte value) {
		return this.attributes.add(key, value);
	}
	
	public @Nullable XmlAttribute addAttribute(@NotNull String key, short value) {
		return this.attributes.add(key, value);
	}
	
	public @Nullable XmlAttribute addAttribute(@NotNull String key, int value) {
		return this.attributes.add(key, value);
	}
	
	public @Nullable XmlAttribute addAttribute(@NotNull String key, long value) {
		return this.attributes.add(key, value);
	}
	
	public @Nullable XmlAttribute addAttribute(@NotNull String key, float value) {
		return this.attributes.add(key, value);
	}
	
	public @Nullable XmlAttribute addAttribute(@NotNull String key, double value) {
		return this.attributes.add(key, value);
	}
	//endregion
	
	//region Get attribute
	public @Nullable XmlAttribute getAttribute(@NotNull String key) {
		return this.attributes.get(key);
	}
	
	public @NotNull String getAttributeAsString(@NotNull String key) {
		return this.attributes.getAsString(key);
	}
	
	public boolean getAttributeAsBoolean(@NotNull String key) {
		return this.attributes.getAsBoolean(key);
	}
	
	public @NotNull Number getAttributeAsNumber(@NotNull String key) {
		return this.attributes.getAsNumber(key);
	}
	
	public byte getAttributeAsByte(@NotNull String key) {
		return this.attributes.getAsByte(key);
	}
	
	public short getAttributeAsShort(@NotNull String key) {
		return this.attributes.getAsShort(key);
	}
	
	public int getAttributeAsInteger(@NotNull String key) {
		return this.attributes.getAsInteger(key);
	}
	
	public long getAttributeAsLong(@NotNull String key) {
		return this.attributes.getAsLong(key);
	}
	
	public float getAttributeAsFloat(@NotNull String key) {
		return this.attributes.getAsFloat(key);
	}
	
	public double getAttributeAsDouble(@NotNull String key) {
		return this.attributes.getAsDouble(key);
	}
	
	public <T> @NotNull T getAttributeAs(@NotNull String key, @NotNull ValueParser<String, T> parser) {
		return this.attributes.getAs(key, parser);
	}
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof XmlElement element)) return false;
		
		if (!this.name.equals(element.name)) return false;
		return this.attributes.equals(element.attributes);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.attributes);
	}
	
	@Override
	public String toString() {
		return this.toString(XmlConfig.DEFAULT);
	}
	
	protected @NotNull StringBuilder toBaseString(@NotNull XmlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		StringBuilder builder = new StringBuilder();
		builder.append("<").append(this.name);
		if (!this.attributes.isEmpty()) {
			builder.append(" ").append(this.attributes.toString(config));
		}
		if (this.isSelfClosing()) {
			builder.append("/>");
		} else {
			builder.append(">");
		}
		return builder;
	}
	
	public @NotNull String toString(@NotNull XmlConfig config) {
		return this.toBaseString(config).toString();
	}
	//endregion
}
