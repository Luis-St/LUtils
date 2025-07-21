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

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a complete XML document with declaration and root element.<br>
 *
 * @author Luis-St
 *
 * @param declaration The XML declaration (e.g., {@code <?xml version="1.0" encoding="UTF-8"?>})
 * @param rootElement The root element of the XML document
 */
public record XmlDocument(@NotNull XmlDeclaration declaration, @NotNull XmlElement rootElement) {
	
	/**
	 * Creates a new XML document with the specified declaration and root element.<br>
	 *
	 * @param declaration The XML declaration
	 * @param rootElement The root element
	 *
	 * @throws NullPointerException If the declaration or root element is null
	 */
	public XmlDocument {
		Objects.requireNonNull(declaration, "Declaration must not be null");
		Objects.requireNonNull(rootElement, "Root element must not be null");
	}
	
	/**
	 * Converts the document to an XML string using the default configuration.<br>
	 *
	 * @return The XML string representation
	 */
	@Override
	public @NotNull String toString() {
		return this.toString(XmlConfig.DEFAULT);
	}
	
	/**
	 * Converts the document to an XML string using the specified configuration.<br>
	 *
	 * @param config The XML configuration to use
	 * @return The XML string representation
	 */
	public @NotNull String toString(@NotNull XmlConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		StringBuilder xml = new StringBuilder();
		xml.append(this.declaration);
		if (config.prettyPrint()) {
			xml.append(System.lineSeparator());
		}
		xml.append(this.rootElement.toString(config));
		return xml.toString();
	}
}
