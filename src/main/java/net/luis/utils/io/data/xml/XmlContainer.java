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

package net.luis.utils.io.data.xml;

import net.luis.utils.io.data.xml.exception.NoSuchXmlElementException;
import net.luis.utils.io.data.xml.exception.XmlTypeException;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Represents an xml container element.<br>
 * A container element can contain other elements.<br>
 *
 * @author Luis-St
 */
public final class XmlContainer extends XmlElement {
	
	/**
	 * The elements of this container.<br>
	 */
	private final XmlElements elements;
	
	/**
	 * Constructs a new xml container with the given name.<br>
	 *
	 * @param name The name of the container
	 * @throws NullPointerException If the name is null
	 */
	public XmlContainer(@NonNull String name) {
		this(name, new XmlElements());
	}
	
	/**
	 * Constructs a new xml container with the given name and elements.<br>
	 *
	 * @param name The name of the container
	 * @param elements The elements of the container
	 * @throws NullPointerException If the name or elements are null
	 */
	public XmlContainer(@NonNull String name, @NonNull XmlElements elements) {
		this(name, new XmlAttributes(), elements);
	}
	
	/**
	 * Constructs a new xml container with the given name and attributes.<br>
	 *
	 * @param name The name of the container
	 * @param attributes The attributes of the container
	 * @throws NullPointerException If the name or attributes are null
	 */
	public XmlContainer(@NonNull String name, @NonNull XmlAttributes attributes) {
		this(name, attributes, new XmlElements());
	}
	
	/**
	 * Constructs a new xml container with the given name, attributes and elements.<br>
	 *
	 * @param name The name of the container
	 * @param attributes The attributes of the container
	 * @param elements The elements of the container
	 * @throws NullPointerException If the name, attributes or elements are null
	 */
	public XmlContainer(@NonNull String name, @NonNull XmlAttributes attributes, @NonNull XmlElements elements) {
		super(name, attributes);
		this.elements = Objects.requireNonNull(elements, "Elements must not be null");
	}
	
	@Override
	protected @NonNull String getElementType() {
		return "xml container";
	}
	
	/**
	 * @return Always false
	 */
	@Override
	public boolean isSelfClosing() {
		return false;
	}
	
	/**
	 * Returns the elements of this container.<br>
	 * @return The elements of this container (modifiable)
	 */
	public @NonNull XmlElements getElements() {
		return this.elements;
	}
	
	/**
	 * Checks if the collection of elements in this container is undefined.<br>
	 * @return True if the collection of elements is undefined, otherwise false
	 */
	public boolean isUndefinedContainer() {
		return this.elements.isUndefined();
	}
	
	/**
	 * Checks if the collection of elements in this container is an array.<br>
	 * @return True if the collection of elements is an array, otherwise false
	 */
	public boolean isContainerArray() {
		return this.elements.isArray();
	}
	
	/**
	 * Checks if the collection of elements in this container is an object.<br>
	 * @return True if the collection of elements is an object, otherwise false
	 */
	public boolean isContainerObject() {
		return this.elements.isObject();
	}
	
	/**
	 * Returns the number of elements in this container.<br>
	 * @return The size of the container
	 */
	public int size() {
		return this.elements.size();
	}
	
	/**
	 * Checks if this container contains no elements.<br>
	 * @return True if this container contains no elements, otherwise false
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * Checks if this container contains an element with the given name.<br>
	 *
	 * @param name The name of the element
	 * @return True if this container contains an element with the given name, otherwise false
	 */
	public boolean containsName(@Nullable String name) {
		return this.elements.containsName(name);
	}
	
	/**
	 * Checks if this container contains the given element.<br>
	 *
	 * @param element The element to check
	 * @return True if this container contains the given element, otherwise false
	 */
	public boolean containsElement(@Nullable XmlElement element) {
		return this.elements.containsElement(element);
	}
	
	/**
	 * Returns an unmodifiable set of the names of the elements in this container.<br>
	 * @return The set of names
	 */
	public @NonNull @Unmodifiable Set<String> nameSet() {
		return this.elements.nameSet();
	}
	
	/**
	 * Returns an unmodifiable collection of the elements in this container.<br>
	 * @return The collection of elements
	 */
	public @NonNull @Unmodifiable Collection<XmlElement> elements() {
		return this.elements.elements();
	}
	
	/**
	 * Adds the given element to this container.<br>
	 *
	 * @param element The element to add
	 * @throws NullPointerException If the element is null
	 * @throws XmlTypeException If the element is not valid for the collection type
	 */
	public void add(@NonNull XmlElement element) {
		this.elements.add(element);
	}
	
	/**
	 * Adds the given container to this container.<br>
	 *
	 * @param container The container to add
	 * @throws NullPointerException If the container is null
	 * @throws XmlTypeException If the container is not valid for the collection type
	 */
	public void addContainer(@NonNull XmlContainer container) {
		this.elements.addContainer(container);
	}
	
	/**
	 * Adds the given value to this container.<br>
	 *
	 * @param value The value to add
	 * @throws NullPointerException If the value is null
	 * @throws XmlTypeException If the value is not valid for the collection type
	 */
	public void addValue(@NonNull XmlValue value) {
		this.elements.addValue(value);
	}
	
	/**
	 * Removes the given element from this container.<br>
	 *
	 * @param element The element to remove
	 * @return True if the element was removed, otherwise false
	 */
	public boolean remove(@NonNull XmlElement element) {
		return this.elements.remove(element);
	}
	
	/**
	 * Removes the element with the given name from this container.<br>
	 * This method should only be used if the collection is an object.<br>
	 *
	 * @param name The name of the element to remove
	 * @return True if the element was removed, otherwise false
	 * @throws XmlTypeException If the collection is an array
	 */
	public boolean remove(@Nullable String name) {
		return this.elements.remove(name);
	}
	
	/**
	 * Removes the element with the given index from this container.<br>
	 * This method should only be used if the collection is an array.<br>
	 *
	 * @param index The name of the element to remove
	 * @return True if the element was removed, otherwise false
	 * @throws XmlTypeException If the collection is an object
	 */
	public boolean remove(int index) {
		return this.elements.remove(index);
	}
	
	/**
	 * Removes all elements from this container.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	
	/**
	 * Returns the element with the given name from this container.<br>
	 * This method should only be used if the container is an object.<br>
	 *
	 * @param name The name of the element
	 * @return The element with the given name, or null if the element does not exist
	 * @throws XmlTypeException If the container is not an array
	 * @see #get(int)
	 */
	public @Nullable XmlElement get(@Nullable String name) {
		return this.elements.get(name);
	}
	
	/**
	 * Returns the element with the given name from this container.<br>
	 * This method should only be used if the container is an object.<br>
	 *
	 * @param name The name of the element
	 * @return The element with the given name
	 * @throws XmlTypeException If the container is an array
	 * @throws NoSuchXmlElementException If the element does not exist
	 * @see #get(String)
	 */
	public @NonNull XmlContainer getAsContainer(@Nullable String name) {
		return this.elements.getAsContainer(name);
	}
	
	/**
	 * Returns the element with the given name from this container.<br>
	 * This method should only be used if the container is an object.<br>
	 *
	 * @param name The name of the element
	 * @return The element with the given name
	 * @throws XmlTypeException If the container is an array
	 * @throws NoSuchXmlElementException If the element does not exist
	 * @see #get(String)
	 */
	public @NonNull XmlValue getAsValue(@Nullable String name) {
		return this.elements.getAsValue(name);
	}
	
	/**
	 * Returns the element at the given index from this container.<br>
	 * This method should only be used if the container is an array.<br>
	 *
	 * @param index The index of the element
	 * @return The element with the given index, or null if no element with the index exists
	 * @throws XmlTypeException If the container is not an array
	 * @see #get(String)
	 */
	public @Nullable XmlElement get(int index) {
		return this.elements.get(index);
	}
	
	/**
	 * Returns the element at the given index from this container.<br>
	 * This method should only be used if the container is an array.<br>
	 *
	 * @param index The index of the element
	 * @return The element with the given index
	 * @throws XmlTypeException If the container is an object
	 * @throws NoSuchXmlElementException If no element with the given index exists
	 * @see #get(int)
	 */
	public @NonNull XmlContainer getAsContainer(int index) {
		return this.elements.getAsContainer(index);
	}
	
	/**
	 * Returns the element at the given index from this container.<br>
	 * This method should only be used if the container is an array.<br>
	 *
	 * @param index The index of the element
	 * @return The element with the given index
	 * @throws XmlTypeException If the container is an object
	 * @throws NoSuchXmlElementException If no element with the given index exists
	 * @see #get(int)
	 */
	public @NonNull XmlValue getAsValue(int index) {
		return this.elements.getAsValue(index);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof XmlContainer that)) return false;
		if (!super.equals(o)) return false;
		
		return this.elements.equals(that.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.elements);
	}
	
	/**
	 * Returns a string representation of this container.<br>
	 * @param config The xml config to use for the string representation
	 * @return The string representation
	 * @throws NullPointerException If the config is null
	 */
	@Override
	public @NonNull String toString(@NonNull XmlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		StringBuilder builder = this.toBaseString(config);
		if (!this.elements.isEmpty()) {
			if (config.prettyPrint()) {
				builder.append(System.lineSeparator()).append(config.indent());
			}
			String elements = this.elements.toString(config);
			if (config.prettyPrint()) {
				builder.append(elements.replace(System.lineSeparator(), System.lineSeparator() + config.indent())).append(System.lineSeparator());
			} else {
				builder.append(elements);
			}
		}
		return builder.append("</").append(this.getName()).append(">").toString();
	}
	//endregion
}
