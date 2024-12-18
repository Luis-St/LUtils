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

package net.luis.utils.io.codec.provider;

import net.luis.utils.io.data.xml.*;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public final class XmlTypeProvider implements TypeProvider<XmlElement> {
	
	private static final String GENERATED = ":generated";
	private static final String EMPTY = "empty" + GENERATED;
	private static final String BOOLEAN = "boolean" + GENERATED;
	private static final String BYTE = "byte_" + GENERATED;
	private static final String SHORT = "short" + GENERATED;
	private static final String INTEGER = "integer" + GENERATED;
	private static final String LONG = "long" + GENERATED;
	private static final String FLOAT = "float" + GENERATED;
	private static final String DOUBLE = "double_" + GENERATED;
	private static final String STRING = "string" + GENERATED;
	private static final String LIST = "list" + GENERATED;
	private static final String LIST_ELEMENT = "list_element" + GENERATED;
	private static final String MAP = "map" + GENERATED;
	
	public static final XmlTypeProvider INSTANCE = new XmlTypeProvider();
	
	private XmlTypeProvider() {}
	
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
	public @NotNull Result<XmlElement> createList(@NotNull List<XmlElement> values) {
		Objects.requireNonNull(values, "Values must not be null");
		List<XmlElement> elements = values.stream().map(element -> this.copyWithName(LIST_ELEMENT, element)).toList();
		return Result.success(new XmlContainer(LIST, new XmlElements(elements)));
	}
	
	@Override
	public @NotNull Result<XmlElement> createMap() {
		return Result.success(new XmlContainer(MAP));
	}
	
	@Override
	public @NotNull Result<XmlElement> createMap(@NotNull Map<String, XmlElement> values) {
		Objects.requireNonNull(values, "Values must not be null");
		return Result.success(new XmlContainer(MAP, new XmlElements(values)));
	}
	//endregion
	
	//region Getters
	@Override
	public @NotNull Result<XmlElement> getEmpty(@NotNull XmlElement type) {
		Objects.requireNonNull(type, "Type must not be null");
		if (!type.isSelfClosing()) {
			return Result.error("Xml element '" + type + "' must be self-closing to be empty");
		}
		if (!type.getAttributes().isEmpty()) {
			return Result.error("Xml element '" + type + "' must not have attributes to be empty");
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
			return Result.success(elements.getAsObject());
		}
		if (elements.isUndefined()) {
			XmlElement element = elements.get(0);
			if (element != null) {
				return Result.success(Map.of(element.getName(), element));
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
		return Result.success(elements.containsName(key));
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
		return Result.success(elements.get(key));
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
		elements.add(this.copyWithName(key, value));
		return Result.success(type);
	}
	
	@Override
	public @NotNull Result<XmlElement> merge(@NotNull XmlElement current, @NotNull XmlElement value) {
		Objects.requireNonNull(current, "Current value must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		if (current.isSelfClosing() && current.getAttributes().isEmpty()) {
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
	private @NotNull XmlElement copyWithName(@NotNull String key, @NotNull XmlElement value) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		if (value.isSelfClosing()) {
			return new XmlElement(key);
		} else if (value.isXmlValue()) {
			return new XmlValue(key, value.getAsXmlValue().getUnescapedValue());
		} else if (value.isXmlContainer()) {
			return new XmlContainer(key, value.getAsXmlContainer().getElements());
		}
		throw new IllegalStateException("Unable to copy xml element '" + value + "' with name '" + key + "'");
	}
	
	private @NotNull List<XmlElement> mergeArray(@NotNull XmlElements current, @NotNull XmlElements value) {
		Objects.requireNonNull(current, "Current elements must not be null");
		Objects.requireNonNull(value, "Value elements must not be null");
		String name = Objects.requireNonNull(current.get(0)).getName();
		XmlElements elements = new XmlElements();
		current.getAsArray().stream().map(element -> this.copyWithName(name, element)).forEach(elements::add);
		value.getAsArray().stream().map(element -> this.copyWithName(name, element)).forEach(elements::add);
		return elements.getAsArray();
	}
	
	private @NotNull Map<String, XmlElement> mergeObject(@NotNull XmlElements current, @NotNull XmlElements value) {
		Objects.requireNonNull(current, "Current elements must not be null");
		Objects.requireNonNull(value, "Value elements must not be null");
		XmlElements elements = new XmlElements();
		current.getAsObject().values().forEach(elements::add);
		value.getAsObject().values().forEach(elements::add);
		return elements.getAsObject();
	}
	
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
