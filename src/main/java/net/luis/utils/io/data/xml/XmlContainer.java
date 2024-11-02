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

import org.jetbrains.annotations.*;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public final class XmlContainer extends XmlElement {
	
	private final XmlElements elements;
	
	public XmlContainer(@NotNull String name) {
		this(name, new XmlElements());
	}
	
	public XmlContainer(@NotNull String name, @NotNull XmlElements elements) {
		this(name, new XmlAttributes(), elements);
	}
	
	public XmlContainer(@NotNull String name, @NotNull XmlAttributes attributes) {
		this(name, attributes, new XmlElements());
	}
	
	public XmlContainer(@NotNull String name, @NotNull XmlAttributes attributes, @NotNull XmlElements elements) {
		super(name, attributes);
		this.elements = Objects.requireNonNull(elements, "Elements must not be null");
	}
	
	@Override
	protected @NotNull String getElementType() {
		return "xml container";
	}
	
	@Override
	public boolean isSelfClosing() {
		return false;
	}
	
	//region Query operations
	public @NotNull XmlElements getElements() {
		return this.elements;
	}
	
	public boolean isUndefinedContainer() {
		return this.elements.isUndefined();
	}
	
	public boolean isContainerArray() {
		return this.elements.isArray();
	}
	
	public boolean isContainerObject() {
		return this.elements.isObject();
	}
	
	public int size() {
		return this.elements.size();
	}
	
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}
	
	public boolean containsName(@Nullable String name) {
		return this.elements.containsName(name);
	}
	
	public boolean containsElement(@Nullable XmlElement element) {
		return this.elements.containsElement(element);
	}
	
	public @NotNull Set<String> nameSet() {
		return this.elements.nameSet();
	}
	
	public @NotNull @Unmodifiable Collection<XmlElement> elements() {
		return this.elements.elements();
	}
	//endregion
	
	//region Add operations
	public void addContainer(@NotNull String name, @NotNull XmlContainer container) {
		this.elements.addContainer(name, container);
	}
	
	public void addValue(@NotNull String name, @NotNull XmlValue value) {
		this.elements.addValue(name, value);
	}
	//endregion
	
	//region Remove operations
	public void remove(@Nullable String name) {
		this.elements.remove(name);
	}
	
	public void remove(@NotNull XmlElement element) {
		this.elements.remove(element);
	}
	
	public void clear() {
		this.elements.clear();
	}
	//endregion
	
	//region Get operations
	public @Nullable XmlElement get(@Nullable String name) {
		return this.elements.get(name);
	}
	
	public @NotNull XmlContainer getAsContainer(@Nullable String name) {
		return this.elements.getAsContainer(name);
	}
	
	public @NotNull XmlValue getAsValue(@Nullable String name) {
		return this.elements.getAsValue(name);
	}
	
	public @Nullable XmlElement get(int index) {
		return this.elements.get(index);
	}
	
	public @NotNull XmlContainer getAsContainer(int index) {
		return this.elements.getAsContainer(index);
	}
	
	public @NotNull XmlValue getAsValue(int index) {
		return this.elements.getAsValue(index);
	}
	//endregion
	
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
	
	@Override
	public @NotNull String toString(@NotNull XmlConfig config) {
		StringBuilder builder = this.toBaseString(config);
		if (config.prettyPrint()) {
			builder.append(System.lineSeparator()).append(config.indent());
		}
		String elements = this.elements.toString(config);
		if (config.prettyPrint()) {
			builder.append(elements.replace(System.lineSeparator(), System.lineSeparator() + config.indent())).append(System.lineSeparator());
		} else {
			builder.append(elements);
		}
		return builder.append("</").append(this.getName()).append(">").toString();
	}
	//endregion
}
