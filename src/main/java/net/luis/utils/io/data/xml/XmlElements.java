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
 *
 * @author Luis-St
 *
 */

public class XmlElements {
	
	private final SequencedMap<String, List<XmlElement>> elements = Maps.newLinkedHashMap();
	
	public XmlElements() {}
	
	public XmlElements(@NotNull List<XmlElement> elements) {
		Objects.requireNonNull(elements, "Elements must not be null").forEach(element -> this.add(element.getName(), element));
	}
	
	public XmlElements(@NotNull Map<String, XmlElement> elements) {
		Objects.requireNonNull(elements, "Elements must not be null").forEach(this::add);
	}
	
	//region Query operations
	public boolean isUndefined() {
		return !this.isObject() && !this.isArray();
	}
	
	public boolean isArray() {
		return this.elements.size() == 1 && this.elements.firstEntry().getValue().size() > 1;
	}
	
	public boolean isObject() {
		return this.elements.size() > 1;
	}
	
	public int size() {
		if (this.isArray()) {
			return this.elements.firstEntry().getValue().size();
		}
		return this.elements.size();
	}
	
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	public boolean containsName(@Nullable String name) {
		return this.elements.containsKey(name);
	}
	
	public boolean containsElement(@Nullable XmlElement element) {
		if (this.isArray()) {
			return this.elements.firstEntry().getValue().contains(element);
		}
		return this.elements.containsValue(Lists.newArrayList(element));
	}
	
	public @NotNull Set<String> nameSet() {
		return this.elements.keySet();
	}
	
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
	public void add(@NotNull String name, @NotNull XmlElement element) {
		Objects.requireNonNull(name, "Name must not be null");
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
	
	public void add(@NotNull XmlElement element) {
		Objects.requireNonNull(element, "Element must not be null");
		this.add(element.getName(), element);
	}
	
	public void addContainer(@NotNull String name, @NotNull XmlContainer container) {
		Objects.requireNonNull(container, "Container must not be null");
		this.add(name, container);
	}
	
	public void addContainer(@NotNull XmlContainer container) {
		Objects.requireNonNull(container, "Container must not be null");
		this.add(container.getName(), container);
	}
	
	public void addValue(@NotNull String name, @NotNull XmlValue value) {
		Objects.requireNonNull(value, "Value must not be null");
		this.add(name, value);
	}
	
	public void addValue(@NotNull XmlValue value) {
		Objects.requireNonNull(value, "Value must not be null");
		this.add(value.getName(), value);
	}
	//endregion
	
	//region Remove operations
	public void remove(@Nullable String name) {
		this.elements.remove(name);
	}
	
	public void remove(@NotNull XmlElement element) {
		Objects.requireNonNull(element, "Element must not be null");
		this.elements.get(element.getName()).remove(element);
		if (this.elements.get(element.getName()).isEmpty()) {
			this.elements.remove(element.getName());
		}
	}
	
	public void clear() {
		this.elements.clear();
	}
	//endregion
	
	//region Get operations
	public @Nullable XmlElement get(@Nullable String name) {
		if (this.isArray()) {
			throw new XmlTypeException("Unable to get a single element from an xml array");
		}
		return this.elements.get(name).getFirst();
	}
	
	public @NotNull XmlContainer getAsContainer(@Nullable String name) {
		XmlElement element = this.get(name);
		if (element == null) {
			throw new NoSuchXmlElementException("Expected xml element for name '" + name + "' but found none");
		}
		return element.getAsXmlContainer();
	}
	
	public @NotNull XmlValue getAsValue(@Nullable String name) {
		XmlElement element = this.get(name);
		if (element == null) {
			throw new NoSuchXmlElementException("Expected xml element for name '" + name + "' but found none");
		}
		return element.getAsXmlValue();
	}
	
	public @Nullable XmlElement get(int index) {
		if (this.isObject()) {
			throw new XmlTypeException("Unable to get an element by index from an xml object");
		}
		return this.elements.firstEntry().getValue().get(index);
	}
	
	public @NotNull XmlContainer getAsContainer(int index) {
		XmlElement element = this.get(index);
		if (element == null) {
			throw new NoSuchXmlElementException("Expected xml element for index '" + index + "' but found none");
		}
		return element.getAsXmlContainer();
	}
	
	public @NotNull XmlValue getAsValue(int index) {
		XmlElement element = this.get(index);
		if (element == null) {
			throw new NoSuchXmlElementException("Expected xml element for index '" + index + "' but found none");
		}
		return element.getAsXmlValue();
	}
	
	public @NotNull @Unmodifiable List<XmlElement> getAsArray() {
		if (!this.isArray()) {
			throw new XmlTypeException("Unable to get an xml array from an xml object");
		}
		return Collections.unmodifiableList(this.elements.firstEntry().getValue());
	}
	
	public @NotNull @Unmodifiable Map<String, XmlElement> getAsObject() {
		if (!this.isObject()) {
			throw new XmlTypeException("Unable to get an xml object from an xml array");
		}
		Map<String, XmlElement> elements = Maps.newLinkedHashMap();
		this.elements.forEach((name, list) -> elements.put(name, list.getFirst()));
		return Collections.unmodifiableMap(elements);
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
	
	public @NotNull String toString(@NotNull XmlConfig config) { // ToDO: Fix
		Objects.requireNonNull(config, "Xml config must not be null");
		StringBuilder builder = new StringBuilder();
		
		
		
		if (this.isArray()) {
			for (XmlElement element : this.getAsArray()) {
				builder.append(element.toString(config)).append(System.lineSeparator());
			}
		} else {
			for (XmlElement element : this.getAsObject().values()) {
				builder.append(element.toString(config)).append(System.lineSeparator());
			}
		}
		return builder.toString().strip();
	}
	//endregion
}
