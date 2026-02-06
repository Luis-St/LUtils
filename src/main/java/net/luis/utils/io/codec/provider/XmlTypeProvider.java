/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.codec.provider;

import com.google.common.collect.Maps;
import net.luis.utils.annotation.type.Singleton;
import net.luis.utils.io.data.xml.*;
import net.luis.utils.io.data.xml.exception.XmlTypeException;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Type provider implementation for xml elements.<br>
 * This class is a singleton and should be accessed through the {@link #INSTANCE} constant.<br>
 *
 * @author Luis-St
 */
@Singleton
public final class XmlTypeProvider implements TypeProvider<XmlElement> {
	
	/**
	 * Pattern for valid xml element names.<br>
	 */
	private static final Pattern XML_ELEMENT_NAME_PATTERN = Pattern.compile("^[a-z_-][a-z0-9_-]*(:[a-z0-9_-]+)?$", Pattern.CASE_INSENSITIVE);
	/**
	 * Constant for the generated namespace of the xml type provider.<br>
	 * This namespace is used to create unique names for the xml types.<br>
	 */
	private static final String GENERATED = ":generated";
	/**
	 * Constant for the name of the empty xml element type.<br>
	 */
	private static final String EMPTY = "empty" + GENERATED;
	/**
	 * Constants for the name of null xml element type.<br>
	 */
	private static final String NULL = "null" + GENERATED;
	/**
	 * Constants for the name of boolean xml element type.<br>
	 */
	private static final String BOOLEAN = "boolean" + GENERATED;
	/**
	 * Constants for the name of byte xml element type.<br>
	 */
	private static final String BYTE = "byte" + GENERATED;
	/**
	 * Constants for the name of short xml element type.<br>
	 */
	private static final String SHORT = "short" + GENERATED;
	/**
	 * Constants for the name of integer xml element type.<br>
	 */
	private static final String INTEGER = "integer" + GENERATED;
	/**
	 * Constants for the name of long xml element type.<br>
	 */
	private static final String LONG = "long" + GENERATED;
	/**
	 * Constants for the name of float xml element type.<br>
	 */
	private static final String FLOAT = "float" + GENERATED;
	/**
	 * Constants for the name of double xml element type.<br>
	 */
	private static final String DOUBLE = "double" + GENERATED;
	/**
	 * Constants for the name of string xml element type.<br>
	 */
	private static final String STRING = "string" + GENERATED;
	/**
	 * Constants for the name of list xml element type.<br>
	 */
	private static final String LIST = "list" + GENERATED;
	/**
	 * Constants for the name of list element xml element type.<br>
	 */
	private static final String ELEMENT = "element" + GENERATED;
	/**
	 * Constants for the name of map xml element type.<br>
	 */
	private static final String MAP = "map" + GENERATED;
	
	/**
	 * The singleton instance of this class.<br>
	 */
	public static final XmlTypeProvider INSTANCE = new XmlTypeProvider();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private XmlTypeProvider() {}
	
	@Override
	public @NonNull XmlElement empty() {
		return new XmlElement(EMPTY);
	}
	
	@Override
	public @NonNull XmlElement createNull() {
		return new XmlElement(NULL);
	}
	
	@Override
	public @NonNull XmlElement createBoolean(boolean value) {
		return new XmlValue(BOOLEAN, value);
	}
	
	@Override
	public @NonNull XmlElement createByte(byte value) {
		return new XmlValue(BYTE, value);
	}
	
	@Override
	public @NonNull XmlElement createShort(short value) {
		return new XmlValue(SHORT, value);
	}
	
	@Override
	public @NonNull XmlElement createInteger(int value) {
		return new XmlValue(INTEGER, value);
	}
	
	@Override
	public @NonNull XmlElement createLong(long value) {
		return new XmlValue(LONG, value);
	}
	
	@Override
	public @NonNull XmlElement createFloat(float value) {
		return new XmlValue(FLOAT, value);
	}
	
	@Override
	public @NonNull XmlElement createDouble(double value) {
		return new XmlValue(DOUBLE, value);
	}
	
	@Override
	public @NonNull XmlElement createString(@Nullable String value) {
		if (value == null) {
			throw new TypeProviderException("Value 'null' is not a valid string");
		}
		return new XmlValue(STRING, value);
	}
	
	@Override
	public @NonNull XmlElement createList(@Nullable List<? extends XmlElement> values) {
		if (values == null) {
			throw new TypeProviderException("Value 'null' is not a valid list");
		}
		
		List<XmlElement> elements = values.stream().map(element -> this.copyWithName(ELEMENT, element)).toList();
		return new XmlContainer(LIST, new XmlElements(elements));
	}
	
	@Override
	public @NonNull XmlElement createMap() {
		return new XmlContainer(MAP);
	}
	
	@Override
	public @NonNull XmlElement createMap(@Nullable Map<String, ? extends XmlElement> values) {
		if (values == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		return new XmlContainer(MAP, new XmlElements(values));
	}
	
	@Override
	public boolean isEmpty(@Nullable XmlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not empty");
		}
		return type.isSelfClosing();
	}
	
	@Override
	public boolean isNull(@Nullable XmlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not xml null");
		}
		return type.isSelfClosing() && type.getName().equals(NULL);
	}
	
	@Override
	public @NonNull Boolean getBoolean(@Nullable XmlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a boolean");
		}
		if (!type.isXmlValue()) {
			throw new TypeProviderException("Xml element '" + type + "' must have a value to be a boolean");
		}
		
		try {
			return type.getAsXmlValue().getAsBoolean();
		} catch (IllegalArgumentException e) {
			throw new TypeProviderException("Unable to parse xml value as boolean: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NonNull Byte getByte(@Nullable XmlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a byte");
		}
		if (!type.isXmlValue()) {
			throw new TypeProviderException("Xml element '" + type + "' must have a value to be a byte");
		}
		
		try {
			return type.getAsXmlValue().getAsByte();
		} catch (IllegalArgumentException e) {
			throw new TypeProviderException("Unable to parse xml value as byte: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NonNull Short getShort(@Nullable XmlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a short");
		}
		if (!type.isXmlValue()) {
			throw new TypeProviderException("Xml element '" + type + "' must have a value to be a short");
		}
		
		try {
			return type.getAsXmlValue().getAsShort();
		} catch (IllegalArgumentException e) {
			throw new TypeProviderException("Unable to parse xml value as short: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NonNull Integer getInteger(@Nullable XmlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not an integer");
		}
		if (!type.isXmlValue()) {
			throw new TypeProviderException("Xml element '" + type + "' must have a value to be an integer");
		}
		
		try {
			return type.getAsXmlValue().getAsInteger();
		} catch (IllegalArgumentException e) {
			throw new TypeProviderException("Unable to parse xml value as integer: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NonNull Long getLong(@Nullable XmlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a long");
		}
		if (!type.isXmlValue()) {
			throw new TypeProviderException("Xml element '" + type + "' must have a value to be a long");
		}
		
		try {
			return type.getAsXmlValue().getAsLong();
		} catch (IllegalArgumentException e) {
			throw new TypeProviderException("Unable to parse xml value as long: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NonNull Float getFloat(@Nullable XmlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a float");
		}
		if (!type.isXmlValue()) {
			throw new TypeProviderException("Xml element '" + type + "' must have a value to be a float");
		}
		
		try {
			return type.getAsXmlValue().getAsFloat();
		} catch (IllegalArgumentException e) {
			throw new TypeProviderException("Unable to parse xml value as float: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NonNull Double getDouble(@Nullable XmlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a double");
		}
		if (!type.isXmlValue()) {
			throw new TypeProviderException("Xml element '" + type + "' must have a value to be a double");
		}
		
		try {
			return type.getAsXmlValue().getAsDouble();
		} catch (IllegalArgumentException e) {
			throw new TypeProviderException("Unable to parse xml value as double: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NonNull String getString(@Nullable XmlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a string");
		}
		
		if (!type.isXmlValue()) {
			throw new TypeProviderException("Xml element '" + type + "' must have a value to be a string");
		}
		return type.getAsXmlValue().getAsString();
	}
	
	@Override
	public @NonNull List<XmlElement> getList(@Nullable XmlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid list");
		}
		
		if (!type.isXmlContainer()) {
			throw new TypeProviderException("Xml element '" + type + "' is not a container");
		}
		
		XmlContainer container = type.getAsXmlContainer();
		if (container.isEmpty()) {
			return List.of();
		}
		
		XmlElements elements = container.getElements();
		if (elements.isArray()) {
			return elements.getAsArray();
		}
		
		if (elements.isUndefined()) {
			XmlElement element = elements.get(0);
			if (element != null) {
				return List.of(element);
			}
			throw new TypeProviderException("Xml element '" + type + "' is an undefined xml container with no elements but not empty");
		}
		throw new TypeProviderException("Xml element '" + type + "' is a container with non-array elements");
	}
	
	@Override
	public @NonNull Map<String, XmlElement> getMap(@Nullable XmlElement type) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		
		if (!type.isXmlContainer()) {
			throw new TypeProviderException("Xml element '" + type + "' is not a container");
		}
		
		XmlElements elements = type.getAsXmlContainer().getElements();
		if (elements.isEmpty()) {
			return Map.of();
		}
		
		if (elements.isObject()) {
			Map<String, XmlElement> map = Maps.newLinkedHashMap();
			elements.getAsObject().forEach((key, value) -> map.put(this.unescapeName(key), value));
			return map;
		}
		
		if (elements.isUndefined()) {
			XmlElement element = elements.get(0);
			if (element != null) {
				XmlElement copied = this.copyWithName(element.getName(), element);
				return Map.of(this.unescapeName(copied.getName()), copied);
			}
			throw new TypeProviderException("Xml element '" + type + "' is an undefined container with no elements but not empty");
		}
		throw new TypeProviderException("Xml element '" + type + "' is a container with non-object elements");
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public boolean has(@Nullable XmlElement type, @Nullable String key) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		
		if (!type.isXmlContainer()) {
			throw new TypeProviderException("Xml element '" + type + "' is not a container");
		}
		
		XmlElements elements = type.getAsXmlContainer().getElements();
		if (elements.isEmpty()) {
			return false;
		}
		return elements.containsName(this.escapeName(key));
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public @NonNull XmlElement get(@Nullable XmlElement type, @Nullable String key) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		
		if (!type.isXmlContainer()) {
			throw new TypeProviderException("Xml element '" + type + "' is not a container");
		}
		
		XmlElements elements = type.getAsXmlContainer().getElements();
		if (elements.isArray()) {
			throw new TypeProviderException("Xml element '" + type + "' is a container with array elements");
		}
		
		XmlElement element = elements.get(this.escapeName(key));
		if (element == null) { // null is valid for unit codec
			return null;
		}
		return this.copyWithName(this.escapeName(key), element);
	}
	
	@Override
	public void set(@Nullable XmlElement type, @Nullable String key, @Nullable XmlElement value) {
		if (type == null) {
			throw new TypeProviderException("Value 'null' is not a valid map");
		}
		if (key == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		if (value == null) {
			throw new TypeProviderException("Value 'null' is not valid");
		}
		
		if (!type.isXmlContainer()) {
			throw new TypeProviderException("Xml element '" + type + "' is not a container");
		}
		
		XmlElements elements = type.getAsXmlContainer().getElements();
		if (elements.isArray()) {
			throw new TypeProviderException("Xml element '" + type + "' is a container with array elements");
		}
		elements.add(this.copyWithName(this.escapeName(key), value));
	}
	
	@Override
	public @UnknownNullability XmlElement merge(@Nullable XmlElement current, @Nullable XmlElement value) {
		if (current == null) {
			return value;
		}
		if (value == null) {
			return current;
		}
		
		if (current.isSelfClosing()) {
			return value;
		}
		if (value.isSelfClosing()) {
			return current;
		}
		
		if (current.isXmlContainer() && value.isXmlContainer()) {
			XmlContainer currentContainer = current.getAsXmlContainer();
			XmlContainer valueContainer = value.getAsXmlContainer();
			
			if (currentContainer.isEmpty()) {
				return value;
			}
			if (valueContainer.isEmpty()) {
				return current;
			}
			if (currentContainer.isContainerArray() && valueContainer.isContainerArray()) {
				return this.createList(this.mergeArray(currentContainer.getElements(), valueContainer.getElements()));
			}
			if (currentContainer.isContainerObject() && valueContainer.isContainerObject()) {
				return this.createMap(this.mergeObject(currentContainer.getElements(), valueContainer.getElements()));
			}
			if (currentContainer.isUndefinedContainer() && valueContainer.isUndefinedContainer()) {
				return this.mergeUndefined(currentContainer.getElements(), valueContainer.getElements());
			}
			
			throw new TypeProviderException("Unable to merge container of different types: '" + current + "' with '" + value + "'");
		}
		throw new TypeProviderException("Unable to merge '" + current + "' with '" + value + "'");
	}
	
	//region Helper methods
	
	/**
	 * Escapes the given name if it is numeric.<br>
	 * The name is escaped by adding an underscore in front of it.<br>
	 * This is required because xml element names must not start with a number.<br>
	 *
	 * @param name The name to escape
	 * @return The escaped name
	 * @throws NullPointerException If the name is null
	 */
	private @NonNull String escapeName(@NonNull String name) {
		Objects.requireNonNull(name, "Name must not be null");
		return !XML_ELEMENT_NAME_PATTERN.matcher(name).matches() ? "_" + name : name;
	}
	
	/**
	 * Unescapes the given name if it is numeric.<br>
	 * The name is unescaped by removing an underscore in front of it.<br>
	 * This is required because xml element names must not start with a number.<br>
	 *
	 * @param name The name to unescape
	 * @return The unescaped name
	 * @throws NullPointerException If the name is null
	 */
	private @NonNull String unescapeName(@NonNull String name) {
		Objects.requireNonNull(name, "Name must not be null");
		
		if (name.startsWith("_") && !XML_ELEMENT_NAME_PATTERN.matcher(name.substring(1)).matches()) {
			return name.substring(1);
		}
		return name;
	}
	
	/**
	 * Copies the given xml element with the given name.<br>
	 * The name is used as the new name for the copied element.<br>
	 * The copy operation is based on the type of the given element.<br>
	 *
	 * @param name The name for the copied element
	 * @param value The element to copy
	 * @return The copied element
	 * @throws NullPointerException If the name or value is null
	 * @throws IllegalStateException If the value is not a valid xml element (should not happen)
	 */
	private @NonNull XmlElement copyWithName(@NonNull String name, @NonNull XmlElement value) {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		
		if (value.isSelfClosing()) {
			return new XmlElement(name);
		} else if (value.isXmlValue()) {
			return new XmlValue(name, value.getAsXmlValue().getUnescapedValue());
		} else if (value.isXmlContainer()) {
			return new XmlContainer(name, value.getAsXmlContainer().getElements());
		}
		throw new IllegalStateException("Unable to copy xml element '" + value + "' with name '" + name + "'");
	}
	
	/**
	 * Merges two arrays of xml elements.<br>
	 * The elements of the current and value arrays are copied with the same name.<br>
	 * The copied elements are then added to a new array.<br>
	 *
	 * @param current The current elements
	 * @param value The value elements
	 * @return The merged array
	 * @throws NullPointerException If the current or value elements are null
	 * @throws XmlTypeException If the current or value elements are not arrays
	 */
	private @NonNull List<XmlElement> mergeArray(@NonNull XmlElements current, @NonNull XmlElements value) {
		Objects.requireNonNull(current, "Current elements must not be null");
		Objects.requireNonNull(value, "Value elements must not be null");
		
		String name = Objects.requireNonNull(current.get(0)).getName();
		XmlElements elements = new XmlElements();
		current.getAsArray().stream().map(element -> this.copyWithName(name, element)).forEach(elements::add);
		value.getAsArray().stream().map(element -> this.copyWithName(name, element)).forEach(elements::add);
		return elements.getAsArray();
	}
	
	/**
	 * Merges two objects of xml elements.<br>
	 * The elements of the current and value objects are moved to a new object.<br>
	 *
	 * @param current The current elements
	 * @param value The value elements
	 * @return The merged object as map
	 * @throws NullPointerException If the current or value elements are null
	 * @throws XmlTypeException If the current or value elements are not objects
	 */
	private @NonNull Map<String, XmlElement> mergeObject(@NonNull XmlElements current, @NonNull XmlElements value) {
		Objects.requireNonNull(current, "Current elements must not be null");
		Objects.requireNonNull(value, "Value elements must not be null");
		
		XmlElements elements = new XmlElements();
		current.getAsObject().values().forEach(elements::add);
		value.getAsObject().values().forEach(elements::add);
		return elements.getAsObject();
	}
	
	/**
	 * Merges two undefined containers of xml elements.<br>
	 * The elements of the current and value containers are moved to a new container.<br>
	 *
	 * @param current The current elements
	 * @param value The value elements
	 * @return The merged container
	 * @throws NullPointerException If the current or value elements are null
	 */
	private @NonNull XmlElement mergeUndefined(@NonNull XmlElements current, @NonNull XmlElements value) {
		Objects.requireNonNull(current, "Current elements must not be null");
		Objects.requireNonNull(value, "Value elements must not be null");
		
		XmlElements elements = new XmlElements();
		Optional.ofNullable(current.get(0)).ifPresent(elements::add);
		Optional.ofNullable(value.get(0)).ifPresent(elements::add);
		
		if (elements.isArray()) {
			return this.createList(elements.getAsArray());
		} else if (elements.isObject()) {
			return this.createMap(elements.getAsObject());
		}
		throw new TypeProviderException("Unable to merge undefined container with elements: '" + elements + "'");
	}
	//endregion
}
