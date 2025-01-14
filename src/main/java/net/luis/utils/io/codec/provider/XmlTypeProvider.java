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

package net.luis.utils.io.codec.provider;

import com.google.common.collect.Maps;
import net.luis.utils.annotation.type.Singleton;
import net.luis.utils.io.data.xml.*;
import net.luis.utils.io.data.xml.exception.XmlTypeException;
import net.luis.utils.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Type provider implementation for xml elements.<br>
 * This class is a singleton and should be accessed through the {@link #INSTANCE} constant.<br>
 *
 * @author Luis-St
 */
@Singleton
public final class XmlTypeProvider implements TypeProvider<XmlElement> {
	
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
	 * Constants for the name of root xml element type.<br>
	 * Used as name for maps if {@link #useRoot} is true.<br>
	 */
	private static final String ROOT = "root" + GENERATED;
	
	/**
	 * The singleton instance of this class.<br>
	 */
	public static final XmlTypeProvider INSTANCE = new XmlTypeProvider(false);
	
	/**
	 * Whether to use {@link #ROOT} as name for maps.<br>
	 */
	private final boolean useRoot;
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * @param useRoot Whether to use root as name for maps
	 */
	private XmlTypeProvider(boolean useRoot) {
		this.useRoot = useRoot;
	}
	
	/**
	 * Returns a new instance of this class with {@link #useRoot} set to true.<br>
	 * If {@link #useRoot} is already true, the same instance is returned.<br>
	 * @return A new instance
	 */
	public @NotNull XmlTypeProvider useRoot() {
		if (this.useRoot) {
			return this;
		}
		return new XmlTypeProvider(true);
	}
	
	//region Creation
	@Override
	public @NotNull XmlElement empty() {
		return new XmlElement(EMPTY);
	}
	
	@Override
	public @NotNull Result<XmlElement> createBoolean(boolean value) {
		return Result.success(new XmlValue(BOOLEAN, value));
	}
	
	@Override
	public @NotNull Result<XmlElement> createByte(byte value) {
		return Result.success(new XmlValue(BYTE, value));
	}
	
	@Override
	public @NotNull Result<XmlElement> createShort(short value) {
		return Result.success(new XmlValue(SHORT, value));
	}
	
	@Override
	public @NotNull Result<XmlElement> createInteger(int value) {
		return Result.success(new XmlValue(INTEGER, value));
	}
	
	@Override
	public @NotNull Result<XmlElement> createLong(long value) {
		return Result.success(new XmlValue(LONG, value));
	}
	
	@Override
	public @NotNull Result<XmlElement> createFloat(float value) {
		return Result.success(new XmlValue(FLOAT, value));
	}
	
	@Override
	public @NotNull Result<XmlElement> createDouble(double value) {
		return Result.success(new XmlValue(DOUBLE, value));
	}
	
	@Override
	public @NotNull Result<XmlElement> createString(@NotNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		return Result.success(new XmlValue(STRING, value));
	}
	
	@Override
	public @NotNull Result<XmlElement> createList(@NotNull List<? extends XmlElement> values) {
		Objects.requireNonNull(values, "Values must not be null");
		List<XmlElement> elements = values.stream().map(element -> this.copyWithName(ELEMENT, element)).toList();
		return Result.success(new XmlContainer(LIST, new XmlElements(elements)));
	}
	
	@Override
	public @NotNull Result<XmlElement> createMap() {
		return Result.success(new XmlContainer(this.getMapName()));
	}
	
	@Override
	public @NotNull Result<XmlElement> createMap(@NotNull Map<String, ? extends XmlElement> values) {
		Objects.requireNonNull(values, "Values must not be null");
		return Result.success(new XmlContainer(this.getMapName(), new XmlElements(values)));
	}
	//endregion
	
	//region Getters
	@Override
	public @NotNull Result<XmlElement> getEmpty(@NotNull XmlElement type) {
		Objects.requireNonNull(type, "Type must not be null");
		if (!type.isSelfClosing()) {
			return Result.error("Xml element '" + type + "' must be self-closing to be empty");
		}
		return Result.success(type);
	}
	
	@Override
	public @NotNull Result<Boolean> getBoolean(@NotNull XmlElement type) {
		Objects.requireNonNull(type, "Type must not be null");
		if (!type.isXmlValue()) {
			return Result.error("Xml element '" + type + "' must have a value to be a boolean");
		}
		try {
			return Result.success(type.getAsXmlValue().getAsBoolean());
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to parse xml value as boolean: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NotNull Result<Byte> getByte(@NotNull XmlElement type) {
		Objects.requireNonNull(type, "Type must not be null");
		if (!type.isXmlValue()) {
			return Result.error("Xml element '" + type + "' must have a value to be a byte");
		}
		try {
			return Result.success(type.getAsXmlValue().getAsByte());
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to parse xml value as byte: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NotNull Result<Short> getShort(@NotNull XmlElement type) {
		Objects.requireNonNull(type, "Type must not be null");
		if (!type.isXmlValue()) {
			return Result.error("Xml element '" + type + "' must have a value to be a short");
		}
		try {
			return Result.success(type.getAsXmlValue().getAsShort());
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to parse xml value as short: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NotNull Result<Integer> getInteger(@NotNull XmlElement type) {
		Objects.requireNonNull(type, "Type must not be null");
		if (!type.isXmlValue()) {
			return Result.error("Xml element '" + type + "' must have a value to be an integer");
		}
		try {
			return Result.success(type.getAsXmlValue().getAsInteger());
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to parse xml value as integer: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NotNull Result<Long> getLong(@NotNull XmlElement type) {
		Objects.requireNonNull(type, "Type must not be null");
		if (!type.isXmlValue()) {
			return Result.error("Xml element '" + type + "' must have a value to be a long");
		}
		try {
			return Result.success(type.getAsXmlValue().getAsLong());
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to parse xml value as long: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NotNull Result<Float> getFloat(@NotNull XmlElement type) {
		Objects.requireNonNull(type, "Type must not be null");
		if (!type.isXmlValue()) {
			return Result.error("Xml element '" + type + "' must have a value to be a float");
		}
		try {
			return Result.success(type.getAsXmlValue().getAsFloat());
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to parse xml value as float: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NotNull Result<Double> getDouble(@NotNull XmlElement type) {
		Objects.requireNonNull(type, "Type must not be null");
		if (!type.isXmlValue()) {
			return Result.error("Xml element '" + type + "' must have a value to be a double");
		}
		try {
			return Result.success(type.getAsXmlValue().getAsDouble());
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to parse xml value as double: " + type.getAsXmlValue().getUnescapedValue());
		}
	}
	
	@Override
	public @NotNull Result<String> getString(@NotNull XmlElement type) {
		Objects.requireNonNull(type, "Type must not be null");
		if (!type.isXmlValue()) {
			return Result.error("Xml element '" + type + "' must have a value to be a string");
		}
		return Result.success(type.getAsXmlValue().getAsString());
	}
	
	@Override
	public @NotNull Result<List<XmlElement>> getList(@NotNull XmlElement type) {
		Objects.requireNonNull(type, "Type must not be null");
		if (!type.isXmlContainer()) {
			return Result.error("Xml element '" + type + "' is not a container");
		}
		XmlContainer container = type.getAsXmlContainer();
		if (container.isEmpty()) {
			return Result.success(List.of());
		}
		XmlElements elements = container.getElements();
		if (elements.isArray()) {
			return Result.success(elements.getAsArray());
		}
		if (elements.isUndefined()) {
			XmlElement element = elements.get(0);
			if (element != null) {
				return Result.success(List.of(element));
			}
			return Result.error("Xml element '" + type + "' is an undefined xml container with no elements but not empty");
		}
		return Result.error("Xml element '" + type + "' is a container with non-array elements");
	}
	
	@Override
	public @NotNull Result<Map<String, XmlElement>> getMap(@NotNull XmlElement type) {
		Objects.requireNonNull(type, "Type must not be null");
		if (!type.isXmlContainer()) {
			return Result.error("Xml element '" + type + "' is not a container");
		}
		XmlElements elements = type.getAsXmlContainer().getElements();
		if (elements.isEmpty()) {
			return Result.success(Map.of());
		}
		if (elements.isObject()) {
			Map<String, XmlElement> map = Maps.newLinkedHashMap();
			elements.getAsObject().forEach((key, value) -> map.put(this.unescapeName(key), value));
			return Result.success(map);
		}
		if (elements.isUndefined()) {
			XmlElement element = elements.get(0);
			if (element != null) {
				XmlElement copied = this.copyWithName(this.unescapeName(element.getName()), element);
				return Result.success(Map.of(copied.getName(), copied));
			}
			return Result.error("Xml element '" + type + "' is an undefined container with no elements but not empty");
		}
		return Result.error("Xml element '" + type + "' is a container with non-object elements");
	}
	//endregion
	
	//region Modification
	@Override
	public @NotNull Result<Boolean> has(@NotNull XmlElement type, @NotNull String key) {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		if (!type.isXmlContainer()) {
			return Result.error("Xml element '" + type + "' is not a container");
		}
		XmlElements elements = type.getAsXmlContainer().getElements();
		if (elements.isEmpty()) {
			return Result.success(false);
		}
		return Result.success(elements.containsName(this.escapeName(key)));
	}
	
	@Override
	public @NotNull Result<XmlElement> get(@NotNull XmlElement type, @NotNull String key) {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		if (!type.isXmlContainer()) {
			return Result.error("Xml element '" + type + "' is not a container");
		}
		XmlElements elements = type.getAsXmlContainer().getElements();
		if (elements.isArray()) {
			return Result.error("Xml element '" + type + "' is a container with array elements");
		}
		XmlElement element = elements.get(this.escapeName(key));
		if (element == null) { // null is valid for unit codec
			return Result.success(null);
		}
		return Result.success(this.copyWithName(this.unescapeName(key), element));
	}
	
	@Override
	public @NotNull Result<XmlElement> set(@NotNull XmlElement type, @NotNull String key, @NotNull XmlElement value) {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		if (!type.isXmlContainer()) {
			return Result.error("Xml element '" + type + "' is not a container");
		}
		XmlElements elements = type.getAsXmlContainer().getElements();
		if (elements.isArray()) {
			return Result.error("Xml element '" + type + "' is a container with array elements");
		}
		elements.add(this.copyWithName(this.escapeName(key), value));
		return Result.success(type);
	}
	
	@Override
	public @NotNull Result<XmlElement> merge(@NotNull XmlElement current, @NotNull XmlElement value) {
		Objects.requireNonNull(current, "Current value must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		if (current.isSelfClosing()) {
			return Result.success(value);
		}
		if (current.isXmlContainer() && value.isXmlContainer()) {
			XmlContainer currentContainer = current.getAsXmlContainer();
			XmlContainer valueContainer = value.getAsXmlContainer();
			if (currentContainer.isEmpty()) {
				return Result.success(value);
			}
			if (valueContainer.isEmpty()) {
				return Result.success(current);
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
			return Result.error("Unable to merge container of different types: '" + current + "' with '" + value + "'");
		}
		return Result.error("Unable to merge '" + current + "' with '" + value + "'");
	}
	//endregion
	
	//region Helper methods
	
	/**
	 * Returns the name for maps based on {@link #useRoot}.<br>
	 * @return The name for maps
	 */
	private @NotNull String getMapName() {
		return this.useRoot ? ROOT : MAP;
	}
	
	/**
	 * Escapes the given name if it is numeric.<br>
	 * The name is escaped by adding an underscore in front of it.<br>
	 * This is required because xml element names must not start with a number.<br>
	 * @param name The name to escape
	 * @return The escaped name
	 * @throws NullPointerException If the name is null
	 */
	private @NotNull String escapeName(@NotNull String name) {
		Objects.requireNonNull(name, "Name must not be null");
		return StringUtils.isNumeric(name) ? "_" + name : name;
	}
	
	/**
	 * Unescapes the given name if it is numeric.<br>
	 * The name is unescaped by removing an underscore in front of it.<br>
	 * This is required because xml element names must not start with a number.<br>
	 * @param name The name to unescape
	 * @return The unescaped name
	 * @throws NullPointerException If the name is null
	 */
	private @NotNull String unescapeName(@NotNull String name) {
		Objects.requireNonNull(name, "Name must not be null");
		if (name.startsWith("_") && StringUtils.isNumeric(name.substring(1))) {
			return name.substring(1);
		}
		return name;
	}
	
	/**
	 * Copies the given xml element with the given name.<br>
	 * The name is used as the new name for the copied element.<br>
	 * The copy operation is based on the type of the given element.<br>
	 * @param name The name for the copied element
	 * @param value The element to copy
	 * @return The copied element
	 * @throws NullPointerException If the name or value is null
	 * @throws IllegalStateException If the value is not a valid xml element (should not happen)
	 */
	private @NotNull XmlElement copyWithName(@NotNull String name, @NotNull XmlElement value) {
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
	 * @param current The current elements
	 * @param value The value elements
	 * @return The merged array
	 * @throws NullPointerException If the current or value elements are null
	 * @throws XmlTypeException If the current or value elements are not arrays
	 */
	private @NotNull List<XmlElement> mergeArray(@NotNull XmlElements current, @NotNull XmlElements value) {
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
	 * @param current The current elements
	 * @param value The value elements
	 * @return The merged object as map
	 * @throws NullPointerException If the current or value elements are null
	 * @throws XmlTypeException If the current or value elements are not objects
	 */
	private @NotNull Map<String, XmlElement> mergeObject(@NotNull XmlElements current, @NotNull XmlElements value) {
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
	 * @param current The current elements
	 * @param value The value elements
	 * @return A result containing the merged container or an error
	 * @throws NullPointerException If the current or value elements are null
	 */
	private @NotNull Result<XmlElement> mergeUndefined(@NotNull XmlElements current, @NotNull XmlElements value) {
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
		return Result.error("Unable to merge undefined container with elements: '" + elements + "'");
	}
	//endregion
}
