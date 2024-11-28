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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.io.data.xml.exception.NoSuchXmlElementException;
import net.luis.utils.io.data.xml.exception.XmlTypeException;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Represents a collection of xml elements.<br>
 * The class provides methods to query, add, remove, and get elements.<br>
 * <p>
 *     The collection can be either an object or an array.<br>
 *     If the collection has exactly one element, it's undefined whether it's an object or an array.<br>
 *     In this case, the next element which is added will determine the type of the collection.<br>
 * </p>
 * <p>
 *     In the case this is an object collection, the keys of the elements must be unique.<br>
 *     If an element with a key that already exists is added, an exception will be thrown.<br>
 * </p>
 * <p>
 *     In the case this is an array collection, the keys of the elements must be the same.<br>
 *     If an element with a key that is different from the existing elements is added, an exception will be thrown.<br>
 * </p>
 *
 * @author Luis-St
 */
public class XmlElements {
	
	/**
	 * The elements of the collection.<br>
	 */
	private final SequencedMap<String, List<XmlElement>> elements = Maps.newLinkedHashMap();
	
	/**
	 * Constructs a new, empty xml elements collection.<br>
	 */
	public XmlElements() {}
	
	/**
	 * Constructs a new xml elements collection with the given elements.<br>
	 * This constructor will determine the type of the collection based on the elements.<br>
	 * @param elements The elements to add to the collection
	 * @throws NullPointerException If the elements are null
	 * @throws XmlTypeException If the elements are not valid for the collection type
	 */
	public XmlElements(@NotNull List<XmlElement> elements) {
		Objects.requireNonNull(elements, "Elements must not be null").forEach(this::add);
	}
	
	/**
	 * Constructs a new xml elements collection with the given elements.<br>
	 * This constructor will determine the type of the collection based on the elements.<br>
	 * @param elements The elements to add to the collection
	 * @throws NullPointerException If the elements are null
	 * @throws XmlTypeException If the elements are not valid for the collection type
	 */
	public XmlElements(@NotNull Map<String, XmlElement> elements) {
		Objects.requireNonNull(elements, "Elements must not be null").forEach((name, element) -> this.add(element));
	}
	
	//region Query operations
	
	/**
	 * Checks whether the collection is undefined.<br>
	 * @return True if the collection is neither an object nor an array, false otherwise
	 */
	public boolean isUndefined() {
		return !this.isObject() && !this.isArray();
	}
	
	/**
	 * Checks whether the collection is an array.<br>
	 * An array is a collection with exactly one key and multiple elements.<br>
	 * @return True if the collection is an array, false otherwise
	 */
	public boolean isArray() {
		return this.elements.size() == 1 && this.elements.firstEntry().getValue().size() > 1;
	}
	
	/**
	 * Checks whether the collection is an object.<br>
	 * An object is a collection with multiple keys.<br>
	 * Each key has exactly one element.<br>
	 * @return True if the collection is an object, false otherwise
	 */
	public boolean isObject() {
		return this.elements.size() > 1;
	}
	
	/**
	 * Returns the number of elements in the collection considering the type.<br>
	 * @return The size of the collection
	 */
	public int size() {
		if (this.isArray()) {
			return this.elements.firstEntry().getValue().size();
		}
		return this.elements.size();
	}
	
	/**
	 * Checks if this collection contains no elements.<br>
	 * @return True if the collection is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	/**
	 * Checks if this collection contains an element with the given name.<br>
	 * @param name The name of the element to check for
	 * @return True if the collection contains an element with the given name, false otherwise
	 */
	public boolean containsName(@Nullable String name) {
		return this.elements.containsKey(name);
	}
	
	/**
	 * Checks if this collection contains the given element considering the type.<br>
	 * @param element The element to check for
	 * @return True if the collection contains the given element, false otherwise
	 */
	public boolean containsElement(@Nullable XmlElement element) {
		if (this.isArray()) {
			return this.elements.firstEntry().getValue().contains(element);
		}
		return this.elements.containsValue(Lists.newArrayList(element));
	}
	
	/**
	 * Returns an unmodifiable set of all element names in the collection.<br>
	 * @return The set of element names
	 */
	public @NotNull @Unmodifiable Set<String> nameSet() {
		return Set.copyOf(this.elements.keySet());
	}
	
	/**
	 * Returns an unmodifiable collection of all elements in the collection considering the type.<br>
	 * @return The collection of elements
	 */
	public @NotNull @Unmodifiable Collection<XmlElement> elements() {
		if (this.isEmpty()) {
			return Collections.emptyList();
		} else if (this.isArray()) {
			return Collections.unmodifiableCollection(this.elements.firstEntry().getValue());
		}
		return this.elements.values().stream().flatMap(List::stream).toList();
	}
	//endregion
	
	//region Add operations
	
	/**
	 * Adds the given element to the collection.<br>
	 * @param element The element to add
	 * @throws NullPointerException If the element is null
	 * @throws XmlTypeException If the element is not valid for the collection type
	 */
	public void add(@NotNull XmlElement element) {
		Objects.requireNonNull(element, "Element must not be null");
		String name = element.getName();
		if (this.elements.isEmpty() || this.isUndefined()) {
			this.elements.put(name, Lists.newArrayList(element));
		} else if (this.isArray()) {
			if (this.elements.containsKey(name)) {
				this.elements.get(name).add(element);
			} else {
				throw new XmlTypeException("Unable to add element with name '" + name + "', expected array name: " + this.elements.firstEntry().getKey());
			}
		} else if (this.isObject()) {
			if (this.elements.containsKey(name)) {
				throw new XmlTypeException("Unable to add element with name '" + name + "', an element with this name already exists");
			} else {
				this.elements.put(name, Lists.newArrayList(element));
			}
		}
	}
	
	/**
	 * Adds the given container to the collection.<br>
	 * @param container The container to add
	 * @throws NullPointerException If the container is null
	 * @throws XmlTypeException If the container is not valid for the collection type
	 */
	public void addContainer(@NotNull XmlContainer container) {
		Objects.requireNonNull(container, "Container must not be null");
		this.add(container);
	}
	
	/**
	 * Adds the given value to the collection.<br>
	 * @param value The value to add
	 * @throws NullPointerException If the value is null
	 * @throws XmlTypeException If the value is not valid for the collection type
	 */
	public void addValue(@NotNull XmlValue value) {
		Objects.requireNonNull(value, "Value must not be null");
		this.add(value);
	}
	//endregion
	
	//region Remove operations
	
	/**
	 * Removes the element with the given name from the collection.<br>
	 * @param name The name of the element to remove
	 * @return True if an element was removed, false otherwise
	 */
	public boolean remove(@Nullable String name) {
		return this.elements.remove(name) != null;
	}
	
	/**
	 * Removes the given element from the collection considering the type.<br>
	 * @param element The element to remove
	 * @return True if the element was removed, false otherwise
	 * @throws NullPointerException If the element is null
	 */
	public boolean remove(@NotNull XmlElement element) {
		Objects.requireNonNull(element, "Element must not be null");
		boolean result = this.elements.get(element.getName()).remove(element);
		if (this.elements.get(element.getName()).isEmpty()) {
			this.elements.remove(element.getName());
		}
		return result;
	}
	
	/**
	 * Removes all elements from the collection.<br>
	 */
	public void clear() {
		this.elements.clear();
	}
	//endregion
	
	//region Get operations
	
	/**
	 * Returns the element with the given name from the collection.<br>
	 * This method should only be used if the collection is an object.<br>
	 * @param name The name of the element to get
	 * @return The element with the given name or null if no element with the name exists
	 * @throws XmlTypeException If the collection is an array
	 * @see #get(int) 
	 */
	public @Nullable XmlElement get(@Nullable String name) {
		if (this.isArray()) {
			throw new XmlTypeException("Unable to get a single element from an xml array");
		}
		return this.elements.get(name).getFirst();
	}
	
	/**
	 * Returns the element with the given name from the collection as a container.<br>
	 * This method should only be used if the collection is an object.<br>
	 * @param name The name of the element to get
	 * @return The element with the given name as a container
	 * @throws XmlTypeException If the collection is an array
	 * @throws NoSuchXmlElementException If no element with the given name exists
	 * @see #get(String) 
	 */
	public @NotNull XmlContainer getAsContainer(@Nullable String name) {
		XmlElement element = this.get(name);
		if (element == null) {
			throw new NoSuchXmlElementException("Expected xml element for name '" + name + "' but found none");
		}
		return element.getAsXmlContainer();
	}
	
	/**
	 * Returns the element with the given name from the collection as a value.<br>
	 * This method should only be used if the collection is an object.<br>
	 * @param name The name of the element to get
	 * @return The element with the given name as a value
	 * @throws XmlTypeException If the collection is an array
	 * @throws NoSuchXmlElementException If no element with the given name exists
	 * @see #get(String)
	 */
	public @NotNull XmlValue getAsValue(@Nullable String name) {
		XmlElement element = this.get(name);
		if (element == null) {
			throw new NoSuchXmlElementException("Expected xml element for name '" + name + "' but found none");
		}
		return element.getAsXmlValue();
	}
	
	/**
	 * Returns the element with the given index from the collection.<br>
	 * This method should only be used if the collection is an array.<br>
	 * @param index The index of the element to get
	 * @return The element with the given index or null if no element with the index exists
	 * @throws XmlTypeException If the collection is an object
	 */
	public @Nullable XmlElement get(int index) {
		if (this.isObject()) {
			throw new XmlTypeException("Unable to get an element by index from an xml object");
		}
		try {
			return this.elements.firstEntry().getValue().get(index);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Returns the element with the given index from the collection as a container.<br>
	 * This method should only be used if the collection is an array.<br>
	 * @param index The index of the element to get
	 * @return The element with the given index as a container
	 * @throws XmlTypeException If the collection is an object
	 * @throws NoSuchXmlElementException If no element with the given index exists
	 * @see #get(int)
	 */
	public @NotNull XmlContainer getAsContainer(int index) {
		XmlElement element = this.get(index);
		if (element == null) {
			throw new NoSuchXmlElementException("Expected xml element for index '" + index + "' but found none");
		}
		return element.getAsXmlContainer();
	}
	
	/**
	 * Returns the element with the given index from the collection as a value.<br>
	 * This method should only be used if the collection is an array.<br>
	 * @param index The index of the element to get
	 * @return The element with the given index as a value
	 * @throws XmlTypeException If the collection is an object
	 * @throws NoSuchXmlElementException If no element with the given index exists
	 * @see #get(int)
	 */
	public @NotNull XmlValue getAsValue(int index) {
		XmlElement element = this.get(index);
		if (element == null) {
			throw new NoSuchXmlElementException("Expected xml element for index '" + index + "' but found none");
		}
		return element.getAsXmlValue();
	}
	
	/**
	 * Returns the elements of the collection as an unmodifiable list.<br>
	 * This method should only be used if the collection is an array.<br>
	 * @return The elements of the collection
	 * @throws XmlTypeException If the collection is an object
	 */
	public @NotNull @Unmodifiable List<XmlElement> getAsArray() {
		if (!this.isArray()) {
			throw new XmlTypeException("Unable to get an xml array from an xml object");
		}
		return List.copyOf(this.elements.firstEntry().getValue());
	}
	
	/**
	 * Returns the elements of the collection as an unmodifiable map.<br>
	 * This method should only be used if the collection is an object.<br>
	 * @return The elements of the collection
	 * @throws XmlTypeException If the collection is an array
	 */
	public @NotNull @Unmodifiable Map<String, XmlElement> getAsObject() {
		if (!this.isObject()) {
			throw new XmlTypeException("Unable to get an xml object from an xml array");
		}
		Map<String, XmlElement> elements = Maps.newLinkedHashMap();
		this.elements.forEach((name, list) -> elements.put(name, list.getFirst()));
		return Map.copyOf(elements);
	}
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof XmlElements that)) return false;
		
		return this.elements.equals(that.elements);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.elements);
	}
	
	@Override
	public String toString() {
		return this.toString(XmlConfig.DEFAULT);
	}
	
	/**
	 * Returns a string representation of the collection using the given xml config.<br>
	 * @param config The xml config to use
	 * @return The string representation of the collection
	 * @throws NullPointerException If the xml config is null
	 */
	public @NotNull String toString(@NotNull XmlConfig config) {
		Objects.requireNonNull(config, "Xml config must not be null");
		StringBuilder builder = new StringBuilder();
		if (this.isArray()) {
			for (XmlElement element : this.getAsArray()) {
				builder.append(element.toString(config));
				if (config.prettyPrint()) {
					builder.append(System.lineSeparator());
				}
			}
		} else {
			for (XmlElement element : this.getAsObject().values()) {
				builder.append(element.toString(config));
				if (config.prettyPrint()) {
					builder.append(System.lineSeparator());
				}
			}
		}
		return builder.toString().strip();
	}
	//endregion
}
