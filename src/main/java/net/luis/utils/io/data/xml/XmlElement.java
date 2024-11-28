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
 * Represents a xml element.<br>
 * A xml element can be a self-closing element, a container element or a value element.<br>
 * <p>
 *     A self-closing element is an element that does not have any content.<br>
 *     It is represented by a tag with a name and optional attributes.<br>
 * </p>
 * <p>
 *     A container element is an element that contains other elements.<br>
 *     It is represented by a tag with a name, optional attributes and a closing tag.<br>
 *     The containing elements have either all the same name or unique names.<br>
 * </p>
 * <p>
 *     A value element is an element that contains a value.<br>
 *     It is represented by a tag with a name, optional attributes and a value.<br>
 *     The value can be a string, a number or a boolean.<br>
 * </p>
 *
 * @see XmlContainer
 * @see XmlValue
 *
 * @author Luis-St
 */
public sealed class XmlElement permits XmlContainer, XmlValue {
	
	/**
	 * The name of the xml element.<br>
	 */
	private final String name;
	/**
	 * The attributes of the xml element.<br>
	 */
	private final XmlAttributes attributes;
	
	/**
	 * Constructs a new xml element with the specified name and no attributes.<br>
	 * @param name The name of the xml element
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is invalid
	 */
	public XmlElement(@NotNull String name) {
		this(name, new XmlAttributes());
	}
	
	/**
	 * Constructs a new xml element with the specified name and attributes.<br>
	 * @param name The name of the xml element
	 * @param attributes The attributes of the xml element
	 * @throws NullPointerException If the name or attributes are null
	 * @throws IllegalArgumentException If the name is invalid
	 */
	public XmlElement(@NotNull String name, @NotNull XmlAttributes attributes) {
		this.name = validateElementName(name);
		this.attributes = Objects.requireNonNull(attributes, "Attributes must not be null");
	}
	
	/**
	 * Returns the type of the xml element.<br>
	 * This is used internally for error messages.<br>
	 * @return The type of the xml element
	 */
	protected @NotNull String getElementType() {
		return "xml self-closing element";
	}
	
	/**
	 * Checks if the xml element is self-closing.<br>
	 * @return Always true
	 */
	public boolean isSelfClosing() {
		return true;
	}
	
	/**
	 * Checks if the xml element is a container.<br>
	 * @return True if the xml element is a container, false otherwise
	 */
	public boolean isXmlContainer() {
		return this instanceof XmlContainer;
	}
	
	/**
	 * Checks if the xml element is a value.<br>
	 * @return True if the xml element is a value, false otherwise
	 */
	public boolean isXmlValue() {
		return this instanceof XmlValue;
	}
	
	/**
	 * Converts this xml element to a xml container.<br>
	 * @return This xml element as a xml container
	 * @throws XmlTypeException If this xml element is not a xml container
	 */
	public @NotNull XmlContainer getAsXmlContainer() {
		if (this instanceof XmlContainer container) {
			return container;
		}
		throw new XmlTypeException("Expected a xml container, but found: " + this.getElementType());
	}
	
	/**
	 * Converts this xml element to a xml value.<br>
	 * @return This xml element as a xml value
	 * @throws XmlTypeException If this xml element is not a xml value
	 */
	public @NotNull XmlValue getAsXmlValue() {
		if (this instanceof XmlValue value) {
			return value;
		}
		throw new XmlTypeException("Expected a xml value, but found: " + this.getElementType());
	}
	
	/**
	 * Returns the name of the xml element.<br>
	 * @return The element name
	 */
	public @NotNull String getName() {
		return this.name;
	}
	
	/**
	 * Returns the attributes of the xml element.<br>
	 * @return The element attributes (modifiable)
	 */
	public @NotNull XmlAttributes getAttributes() {
		return this.attributes;
	}
	
	//region Add attribute
	
	/**
	 * Adds the specified attribute to the element.<br>
	 * @param attribute The attribute to add
	 * @return The previous attribute with the same key, or null if there was no previous attribute
	 * @see XmlAttributes#add(XmlAttribute)
	 */
	public @Nullable XmlAttribute addAttribute(@Nullable XmlAttribute attribute) {
		return this.attributes.add(attribute);
	}
	
	/**
	 * Adds a new attribute with the given name and string value to this element.<br>
	 * @param key The key of the attribute
	 * @param value The value of the attribute
	 * @return The previous attribute with the same key, or null if there was no previous attribute
	 * @see XmlAttributes#add(String, String)
	 */
	public @Nullable XmlAttribute addAttribute(@NotNull String key, @Nullable String value) {
		return this.attributes.add(key, value);
	}
	
	/**
	 * Adds a new attribute with the given name and boolean value to this element.<br>
	 * @param key The key of the attribute
	 * @param value The value of the attribute
	 * @return The previous attribute with the same key, or null if there was no previous attribute
	 * @see XmlAttributes#add(String, boolean)
	 */
	public @Nullable XmlAttribute addAttribute(@NotNull String key, boolean value) {
		return this.attributes.add(key, value);
	}
	
	/**
	 * Adds a new attribute with the given name and number value to this element.<br>
	 * @param key The key of the attribute
	 * @param value The value of the attribute
	 * @return The previous attribute with the same key, or null if there was no previous attribute
	 * @see XmlAttributes#add(String, Number)
	 */
	public @Nullable XmlAttribute addAttribute(@NotNull String key, @Nullable Number value) {
		return this.attributes.add(key, value);
	}
	
	/**
	 * Adds a new attribute with the given name and byte value to this element.<br>
	 * @param key The key of the attribute
	 * @param value The value of the attribute
	 * @return The previous attribute with the same key, or null if there was no previous attribute
	 * @see XmlAttributes#add(String, byte)
	 */
	public @Nullable XmlAttribute addAttribute(@NotNull String key, byte value) {
		return this.attributes.add(key, value);
	}
	
	/**
	 * Adds a new attribute with the given name and short value to this element.<br>
	 * @param key The key of the attribute
	 * @param value The value of the attribute
	 * @return The previous attribute with the same key, or null if there was no previous attribute
	 * @see XmlAttributes#add(String, short)
	 */
	public @Nullable XmlAttribute addAttribute(@NotNull String key, short value) {
		return this.attributes.add(key, value);
	}
	
	/**
	 * Adds a new attribute with the given name and integer value to this element.<br>
	 * @param key The key of the attribute
	 * @param value The value of the attribute
	 * @return The previous attribute with the same key, or null if there was no previous attribute
	 * @see XmlAttributes#add(String, int)
	 */
	public @Nullable XmlAttribute addAttribute(@NotNull String key, int value) {
		return this.attributes.add(key, value);
	}
	
	/**
	 * Adds a new attribute with the given name and long value to this element.<br>
	 * @param key The key of the attribute
	 * @param value The value of the attribute
	 * @return The previous attribute with the same key, or null if there was no previous attribute
	 * @see XmlAttributes#add(String, long)
	 */
	public @Nullable XmlAttribute addAttribute(@NotNull String key, long value) {
		return this.attributes.add(key, value);
	}
	
	/**
	 * Adds a new attribute with the given name and float value to this element.<br>
	 * @param key The key of the attribute
	 * @param value The value of the attribute
	 * @return The previous attribute with the same key, or null if there was no previous attribute
	 * @see XmlAttributes#add(String, float)
	 */
	public @Nullable XmlAttribute addAttribute(@NotNull String key, float value) {
		return this.attributes.add(key, value);
	}
	
	/**
	 * Adds a new attribute with the given name and double value to this element.<br>
	 * @param key The key of the attribute
	 * @param value The value of the attribute
	 * @return The previous attribute with the same key, or null if there was no previous attribute
	 * @see XmlAttributes#add(String, double)
	 */
	public @Nullable XmlAttribute addAttribute(@NotNull String key, double value) {
		return this.attributes.add(key, value);
	}
	//endregion
	
	//region Get attribute
	
	/**
	 * Returns the attribute with the specified key, or null if there is no such attribute in this element.<br>
	 * @param key The key of the attribute
	 * @return The attribute with the specified key, or null if there is no such attribute
	 * @see XmlAttributes#get(String)
	 */
	public @Nullable XmlAttribute getAttribute(@NotNull String key) {
		return this.attributes.get(key);
	}
	
	/**
	 * Returns the attribute with the specified key as a string.<br>
	 * @param key The key of the attribute
	 * @return The value of the attribute as a string
	 * @see XmlAttributes#getAsString(String)
	 */
	public @NotNull String getAttributeAsString(@NotNull String key) {
		return this.attributes.getAsString(key);
	}
	
	/**
	 * Returns the attribute with the specified key as a boolean.<br>
	 * @param key The key of the attribute
	 * @return The value of the attribute as a boolean
	 * @see XmlAttributes#getAsBoolean(String)
	 */
	public boolean getAttributeAsBoolean(@NotNull String key) {
		return this.attributes.getAsBoolean(key);
	}
	
	/**
	 * Returns the attribute with the specified key as a number.<br>
	 * @param key The key of the attribute
	 * @return The value of the attribute as a number
	 * @see XmlAttributes#getAsNumber(String)
	 */
	public @NotNull Number getAttributeAsNumber(@NotNull String key) {
		return this.attributes.getAsNumber(key);
	}
	
	/**
	 * Returns the attribute with the specified key as a byte.<br>
	 * @param key The key of the attribute
	 * @return The value of the attribute as a byte
	 * @see XmlAttributes#getAsByte(String)
	 */
	public byte getAttributeAsByte(@NotNull String key) {
		return this.attributes.getAsByte(key);
	}
	
	/**
	 * Returns the attribute with the specified key as a short.<br>
	 * @param key The key of the attribute
	 * @return The value of the attribute as a short
	 * @see XmlAttributes#getAsShort(String)
	 */
	public short getAttributeAsShort(@NotNull String key) {
		return this.attributes.getAsShort(key);
	}
	
	/**
	 * Returns the attribute with the specified key as an integer.<br>
	 * @param key The key of the attribute
	 * @return The value of the attribute as an integer
	 * @see XmlAttributes#getAsInteger(String)
	 */
	public int getAttributeAsInteger(@NotNull String key) {
		return this.attributes.getAsInteger(key);
	}
	
	/**
	 * Returns the attribute with the specified key as a long.<br>
	 * @param key The key of the attribute
	 * @return The value of the attribute as a long
	 * @see XmlAttributes#getAsLong(String)
	 */
	public long getAttributeAsLong(@NotNull String key) {
		return this.attributes.getAsLong(key);
	}
	
	/**
	 * Returns the attribute with the specified key as a float.<br>
	 * @param key The key of the attribute
	 * @return The value of the attribute as a float
	 * @see XmlAttributes#getAsFloat(String)
	 */
	public float getAttributeAsFloat(@NotNull String key) {
		return this.attributes.getAsFloat(key);
	}
	
	/**
	 * Returns the attribute with the specified key as a double.<br>
	 * @param key The key of the attribute
	 * @return The value of the attribute as a double
	 * @see XmlAttributes#getAsDouble(String)
	 */
	public double getAttributeAsDouble(@NotNull String key) {
		return this.attributes.getAsDouble(key);
	}
	
	/**
	 * Returns the attribute with the specified key as the type specified by the parser.<br>
	 * @param key The key of the attribute
	 * @param parser The parser to convert the attribute value to the desired type
	 * @return The value of the attribute as the specified type
	 * @param <T> The type of the value
	 * @see XmlAttributes#getAs(String, ValueParser)
	 */
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
	
	/**
	 * Returns a string builder with the base string representation of this xml element.<br>
	 * The base string representation contains the root tag with the name and attributes.<br>
	 * @param config The xml config to use for the string representation
	 * @return The string builder
	 */
	protected @NotNull StringBuilder toBaseString(@Nullable XmlConfig config) {
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
	
	/**
	 * The string representation of this xml element.<br>
	 * @param config The xml config to use for the string representation
	 * @return The string representation
	 */
	public @NotNull String toString(@NotNull XmlConfig config) {
		return this.toBaseString(config).toString();
	}
	//endregion
}
