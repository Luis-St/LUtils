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

import com.google.common.collect.Maps;
import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.data.xml.exception.NoSuchXmlAttributeException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Represents a collection of xml attributes.<br>
 * The class provides methods to query, add, remove, replace, and get attributes.<br>
 *
 * @author Luis-St
 */
public class XmlAttributes {
	
	/**
	 * The map of attributes.<br>
	 */
	private final Map<String, XmlAttribute> attributes = Maps.newLinkedHashMap();
	
	/**
	 * Constructs a new empty xml attributes collection.<br>
	 */
	public XmlAttributes() {}
	
	/**
	 * Constructs a new xml attributes collection with the given attributes.<br>
	 * The given attributes will be copied into the new collection.<br>
	 *
	 * @param attributes The attributes
	 * @throws NullPointerException If the given attributes are null
	 */
	public XmlAttributes(@NonNull Map<String, XmlAttribute> attributes) {
		this.attributes.putAll(Objects.requireNonNull(attributes, "Attributes must not be null"));
	}
	
	/**
	 * Returns the number of attributes in this collection.<br>
	 * @return The size of this collection
	 */
	public int size() {
		return this.attributes.size();
	}
	
	/**
	 * Checks if this collection contains no attributes.<br>
	 * @return True if this collection contains no attributes, otherwise false
	 */
	public boolean isEmpty() {
		return this.attributes.isEmpty();
	}
	
	/**
	 * Checks if this collection contains an attribute with the given name.<br>
	 *
	 * @param name The name to check
	 * @return True if this collection contains an attribute with the given name, otherwise false
	 */
	public boolean containsName(@Nullable String name) {
		return this.attributes.containsKey(name);
	}
	
	/**
	 * Checks if this collection contains the given attribute.<br>
	 *
	 * @param attribute The attribute to check
	 * @return True if this collection contains the given attribute, otherwise false
	 */
	public boolean containsValue(@Nullable XmlAttribute attribute) {
		return this.attributes.containsValue(attribute);
	}
	
	/**
	 * Returns an unmodifiable set of all attribute names in this collection.<br>
	 * @return The set of attribute names
	 */
	public @NonNull @Unmodifiable Set<String> nameSet() {
		return Set.copyOf(this.attributes.keySet());
	}
	
	/**
	 * Returns an unmodifiable collection of all attributes in this collection.<br>
	 * @return The collection of attributes
	 */
	public @NonNull @Unmodifiable Collection<XmlAttribute> attributes() {
		return Collections.unmodifiableCollection(this.attributes.values());
	}
	
	/**
	 * Adds the given attribute to this collection.<br>
	 *
	 * @param attribute The attribute to add
	 * @return The previous attribute with the same name, or null if there was none
	 * @throws NullPointerException If the given attribute is null
	 */
	public @Nullable XmlAttribute add(@NonNull XmlAttribute attribute) {
		Objects.requireNonNull(attribute, "Attribute must not be null");
		return this.attributes.put(attribute.getName(), attribute);
	}
	
	/**
	 * Adds a new attribute with the given name and string value to this collection.<br>
	 *
	 * @param name The name of the attribute
	 * @param value The string value of the attribute
	 * @return The previous attribute with the same name, or null if there was none
	 * @throws NullPointerException If the given name is null
	 * @see #add(XmlAttribute)
	 * @see XmlAttribute#XmlAttribute(String, String)
	 */
	public @Nullable XmlAttribute add(@NonNull String name, @Nullable String value) {
		return this.add(new XmlAttribute(name, value));
	}
	
	/**
	 * Adds a new attribute with the given name and boolean value to this collection.<br>
	 *
	 * @param name The name of the attribute
	 * @param value The boolean value of the attribute
	 * @return The previous attribute with the same name, or null if there was none
	 * @throws NullPointerException If the given name is null
	 * @see #add(XmlAttribute)
	 * @see XmlAttribute#XmlAttribute(String, boolean)
	 */
	public @Nullable XmlAttribute add(@NonNull String name, boolean value) {
		return this.add(new XmlAttribute(name, value));
	}
	
	/**
	 * Adds a new attribute with the given name and number value to this collection.<br>
	 *
	 * @param name The name of the attribute
	 * @param value The number value of the attribute
	 * @return The previous attribute with the same name, or null if there was none
	 * @throws NullPointerException If the given name is null
	 * @see #add(XmlAttribute)
	 * @see XmlAttribute#XmlAttribute(String, Number)
	 */
	public @Nullable XmlAttribute add(@NonNull String name, @Nullable Number value) {
		return this.add(new XmlAttribute(name, value));
	}
	
	/**
	 * Adds a new attribute with the given name and byte value to this collection.<br>
	 *
	 * @param name The name of the attribute
	 * @param value The byte value of the attribute
	 * @return The previous attribute with the same name, or null if there was none
	 * @throws NullPointerException If the given name is null
	 * @see #add(XmlAttribute)
	 * @see XmlAttribute#XmlAttribute(String, Number)
	 */
	public @Nullable XmlAttribute add(@NonNull String name, byte value) {
		return this.add(new XmlAttribute(name, value));
	}
	
	/**
	 * Adds a new attribute with the given name and short value to this collection.<br>
	 *
	 * @param name The name of the attribute
	 * @param value The short value of the attribute
	 * @return The previous attribute with the same name, or null if there was none
	 * @throws NullPointerException If the given name is null
	 * @see #add(XmlAttribute)
	 * @see XmlAttribute#XmlAttribute(String, Number)
	 */
	public @Nullable XmlAttribute add(@NonNull String name, short value) {
		return this.add(new XmlAttribute(name, value));
	}
	
	/**
	 * Adds a new attribute with the given name and integer value to this collection.<br>
	 *
	 * @param name The name of the attribute
	 * @param value The integer value of the attribute
	 * @return The previous attribute with the same name, or null if there was none
	 * @throws NullPointerException If the given name is null
	 * @see #add(XmlAttribute)
	 * @see XmlAttribute#XmlAttribute(String, Number)
	 */
	public @Nullable XmlAttribute add(@NonNull String name, int value) {
		return this.add(new XmlAttribute(name, value));
	}
	
	/**
	 * Adds a new attribute with the given name and long value to this collection.<br>
	 *
	 * @param name The name of the attribute
	 * @param value The long value of the attribute
	 * @return The previous attribute with the same name, or null if there was none
	 * @throws NullPointerException If the given name is null
	 * @see #add(XmlAttribute)
	 * @see XmlAttribute#XmlAttribute(String, Number)
	 */
	public @Nullable XmlAttribute add(@NonNull String name, long value) {
		return this.add(new XmlAttribute(name, value));
	}
	
	/**
	 * Adds a new attribute with the given name and float value to this collection.<br>
	 *
	 * @param name The name of the attribute
	 * @param value The float value of the attribute
	 * @return The previous attribute with the same name, or null if there was none
	 * @throws NullPointerException If the given name is null
	 * @see #add(XmlAttribute)
	 * @see XmlAttribute#XmlAttribute(String, Number)
	 */
	public @Nullable XmlAttribute add(@NonNull String name, float value) {
		return this.add(new XmlAttribute(name, value));
	}
	
	/**
	 * Adds a new attribute with the given name and double value to this collection.<br>
	 *
	 * @param name The name of the attribute
	 * @param value The double value of the attribute
	 * @return The previous attribute with the same name, or null if there was none
	 * @throws NullPointerException If the given name is null
	 * @see #add(XmlAttribute)
	 * @see XmlAttribute#XmlAttribute(String, Number)
	 */
	public @Nullable XmlAttribute add(@NonNull String name, double value) {
		return this.add(new XmlAttribute(name, value));
	}
	
	
	/**
	 * Removes the attribute with the given name from this collection.<br>
	 *
	 * @param name The name of the attribute to remove
	 * @return The removed attribute, or null if there was none
	 */
	public @Nullable XmlAttribute remove(@Nullable String name) {
		return this.attributes.remove(name);
	}
	
	/**
	 * Removes the given attribute from this collection.<br>
	 *
	 * @param attribute The attribute to remove
	 * @return The removed attribute, or null if there was none
	 * @throws NullPointerException If the given attribute is null
	 * @see #remove(String)
	 */
	public @Nullable XmlAttribute remove(@NonNull XmlAttribute attribute) {
		Objects.requireNonNull(attribute, "Attribute must not be null");
		return this.remove(attribute.getName());
	}
	
	/**
	 * Removes all attributes from this collection.<br>
	 */
	public void clear() {
		this.attributes.clear();
	}
	
	
	/**
	 * Replaces the attribute with the given name in this collection with the new attribute.<br>
	 *
	 * @param name The name of the attribute to replace
	 * @param newAttribute The new attribute
	 * @return The replaced attribute, or null if there was none
	 * @throws NullPointerException If the given name or new attribute is null
	 */
	public @Nullable XmlAttribute replace(@NonNull String name, @NonNull XmlAttribute newAttribute) {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(newAttribute, "New attribute must not be null");
		return this.attributes.replace(name, newAttribute);
	}
	
	/**
	 * Replaces the attribute with the given name in this collection with the new attribute.<br>
	 * Under the condition that the name is associated with the given current attribute.<br>
	 *
	 * @param name The name of the attribute to replace
	 * @param currentAttribute The current attribute
	 * @param newAttribute The new attribute
	 * @return True if the replacement was successful, otherwise false
	 * @throws NullPointerException If the given name or new attribute is null
	 */
	public boolean replace(@NonNull String name, @Nullable XmlAttribute currentAttribute, @NonNull XmlAttribute newAttribute) {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(newAttribute, "New attribute must not be null");
		return this.attributes.replace(name, currentAttribute, newAttribute);
	}
	
	/**
	 * Returns the attribute with the given name from this collection.<br>
	 *
	 * @param name The name of the attribute to get
	 * @return The attribute, or null if there was none
	 * @throws NullPointerException If the given name is null
	 */
	public @Nullable XmlAttribute get(@NonNull String name) {
		Objects.requireNonNull(name, "Name must not be null");
		return this.attributes.get(name);
	}
	
	/**
	 * Returns the value of the attribute with the given name as a string.<br>
	 *
	 * @param name The name of the attribute to get
	 * @return The value as a string
	 * @throws NullPointerException If the given name is null
	 * @throws NoSuchXmlAttributeException If there is no attribute with the given name
	 */
	public @NonNull String getAsString(@NonNull String name) {
		XmlAttribute attribute = this.get(name);
		
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for name '" + name + "' but found none");
		}
		return attribute.getAsString();
	}
	
	/**
	 * Returns the value of the attribute with the given name as a boolean.<br>
	 *
	 * @param name The name of the attribute to get
	 * @return The value as a boolean
	 * @throws NullPointerException If the given name is null
	 * @throws NoSuchXmlAttributeException If there is no attribute with the given name
	 */
	public boolean getAsBoolean(@NonNull String name) {
		XmlAttribute attribute = this.get(name);
		
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for name '" + name + "' but found none");
		}
		return attribute.getAsBoolean();
	}
	
	/**
	 * Returns the value of the attribute with the given name as a number.<br>
	 *
	 * @param name The name of the attribute to get
	 * @return The value as a number
	 * @throws NullPointerException If the given name is null
	 * @throws NoSuchXmlAttributeException If there is no attribute with the given name
	 */
	public @NonNull Number getAsNumber(@NonNull String name) {
		XmlAttribute attribute = this.get(name);
		
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for name '" + name + "' but found none");
		}
		return attribute.getAsNumber();
	}
	
	/**
	 * Returns the value of the attribute with the given name as a byte.<br>
	 *
	 * @param name The name of the attribute to get
	 * @return The value as a byte
	 * @throws NullPointerException If the given name is null
	 * @throws NoSuchXmlAttributeException If there is no attribute with the given name
	 */
	public byte getAsByte(@NonNull String name) {
		XmlAttribute attribute = this.get(name);
		
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for name '" + name + "' but found none");
		}
		return attribute.getAsByte();
	}
	
	/**
	 * Returns the value of the attribute with the given name as a short.<br>
	 *
	 * @param name The name of the attribute to get
	 * @return The value as a short
	 * @throws NullPointerException If the given name is null
	 * @throws NoSuchXmlAttributeException If there is no attribute with the given name
	 */
	public short getAsShort(@NonNull String name) {
		XmlAttribute attribute = this.get(name);
		
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for name '" + name + "' but found none");
		}
		return attribute.getAsShort();
	}
	
	/**
	 * Returns the value of the attribute with the given name as an integer.<br>
	 *
	 * @param name The name of the attribute to get
	 * @return The value as an integer
	 * @throws NullPointerException If the given name is null
	 * @throws NoSuchXmlAttributeException If there is no attribute with the given name
	 */
	public int getAsInteger(@NonNull String name) {
		XmlAttribute attribute = this.get(name);
		
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for name '" + name + "' but found none");
		}
		return attribute.getAsInteger();
	}
	
	/**
	 * Returns the value of the attribute with the given name as a long.<br>
	 *
	 * @param name The name of the attribute to get
	 * @return The value as a long
	 * @throws NullPointerException If the given name is null
	 * @throws NoSuchXmlAttributeException If there is no attribute with the given name
	 */
	public long getAsLong(@NonNull String name) {
		XmlAttribute attribute = this.get(name);
		
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for name '" + name + "' but found none");
		}
		return attribute.getAsLong();
	}
	
	/**
	 * Returns the value of the attribute with the given name as a float.<br>
	 *
	 * @param name The name of the attribute to get
	 * @return The value as a float
	 * @throws NullPointerException If the given name is null
	 * @throws NoSuchXmlAttributeException If there is no attribute with the given name
	 */
	public float getAsFloat(@NonNull String name) {
		XmlAttribute attribute = this.get(name);
		
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for name '" + name + "' but found none");
		}
		return attribute.getAsFloat();
	}
	
	/**
	 * Returns the value of the attribute with the given name as a double.<br>
	 *
	 * @param name The name of the attribute to get
	 * @return The value as a double
	 * @throws NullPointerException If the given name is null
	 * @throws NoSuchXmlAttributeException If there is no attribute with the given name
	 */
	public double getAsDouble(@NonNull String name) {
		XmlAttribute attribute = this.get(name);
		
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for name '" + name + "' but found none");
		}
		return attribute.getAsDouble();
	}
	
	/**
	 * Returns the value of the attribute with the given name as the type of the given parser.<br>
	 *
	 * @param name The name of the attribute to get
	 * @param parser The parser to convert the value to the given type
	 * @return The value as the given type
	 * @param <T> The type to convert the value to
	 * @throws NullPointerException If the given name or parser is null
	 * @throws NoSuchXmlAttributeException If there is no attribute with the given name
	 */
	public <T> @NonNull T getAs(@NonNull String name, @NonNull ThrowableFunction<String, T, ? extends Exception> parser) {
		Objects.requireNonNull(parser, "Parser must not be null");
		XmlAttribute attribute = this.get(name);
		
		if (attribute == null) {
			throw new NoSuchXmlAttributeException("Expected xml attribute for name '" + name + "' but found none");
		}
		return attribute.getAs(parser);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof XmlAttributes that)) return false;
		
		return this.attributes.equals(that.attributes);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.attributes);
	}
	
	@Override
	public String toString() {
		return this.toString(XmlConfig.DEFAULT);
	}
	
	/**
	 * Returns a string representation of this collection.<br>
	 * @param config The xml config to use
	 * @return The string representation
	 */
	public @NonNull String toString(@Nullable XmlConfig config) {
		StringBuilder builder = new StringBuilder();
		this.attributes.forEach((name, value) -> builder.append(value.toString(config)).append(" "));
		return builder.toString().strip();
	}
	//endregion
}
