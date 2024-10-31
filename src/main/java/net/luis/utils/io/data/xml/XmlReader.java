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
import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.xml.exception.XmlSyntaxException;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringReader;
import net.luis.utils.util.Version;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class XmlReader implements AutoCloseable {
	
	private static final List<String> DECLARATION_ATTRIBUTES = List.of("version", "encoding", "standalone");
	
	private final XmlConfig config;
	private final ScopedStringReader reader;
	private boolean readDeclaration;
	
	public XmlReader(@NotNull String string) {
		this(string, XmlConfig.DEFAULT);
	}
	
	public XmlReader(@NotNull String string, @NotNull XmlConfig config) {
		this.config = Objects.requireNonNull(config, "Xml config must not be null");
		this.reader = new ScopedStringReader(Objects.requireNonNull(string, "String must not be null"));
	}
	
	public XmlReader(@NotNull InputProvider input) {
		this(input, XmlConfig.DEFAULT);
	}
	
	public XmlReader(@NotNull InputProvider input, @NotNull XmlConfig config) {
		this.config = Objects.requireNonNull(config, "Xml config must not be null");
		this.reader = new ScopedStringReader(new InputStreamReader(Objects.requireNonNull(input, "Input must not be null").getStream(), config.charset()));
	}
	
	public @NotNull XmlDeclaration readDeclaration() {
		this.reader.skipWhitespaces();
		StringReader declarationReader = new StringReader(this.reader.readScope(ScopedStringReader.ANGLE_BRACKETS));
		String type = declarationReader.readUntil(' ');
		if (!"<?xml".equalsIgnoreCase(type)) {
			throw new XmlSyntaxException("Expected xml declaration, but found: '" + declarationReader.getString() + "'");
		}
		declarationReader.skipWhitespaces();
		
		Map<String, String> declarationAttributes = new HashMap<>();
		List<String> attributes = Lists.newArrayList(DECLARATION_ATTRIBUTES);
		while (declarationReader.peek() != '?' && !attributes.isEmpty()) {
			String attribute = declarationReader.readExpected(attributes, false);
			attributes.remove(attribute.toLowerCase());
			
			declarationReader.skipWhitespaces();
			char next = declarationReader.read();
			if (next != '=') {
				throw new XmlSyntaxException("Expected '=' after attribute key '" + attribute + "' in xml declaration, but found: '" + next + "'");
			}
			declarationReader.skipWhitespaces();
			String value = declarationReader.readQuotedString();
			declarationReader.skipWhitespaces();
			declarationAttributes.put(attribute.toLowerCase(), value);
		}
		
		if (!declarationReader.canRead(2) || declarationReader.read() != '?' || declarationReader.read() != '>') {
			throw new XmlSyntaxException("Expected '?>' at the end of the xml declaration, but found: '" + declarationReader.getString() + "'");
		}
		if (!declarationAttributes.containsKey("version")) {
			throw new XmlSyntaxException("Missing required attribute 'version' in xml declaration");
		}
		Version version = Version.parse(declarationAttributes.get("version"));
		Charset charset = declarationAttributes.containsKey("encoding") ? Charset.forName(declarationAttributes.get("encoding")) : this.config.charset();
		boolean standalone = declarationAttributes.containsKey("standalone") && "yes".equalsIgnoreCase(declarationAttributes.get("standalone"));
		this.readDeclaration = true;
		return new XmlDeclaration(version, charset, standalone);
	}
	
	public void readeXmlElement() {
		if (!this.readDeclaration) {
			throw new IllegalStateException("Xml declaration must be read before reading xml elements");
		}
	
	
	
	
	
	
	}
	
	private @NotNull XmlElement readeXmlElement(@NotNull ScopedStringReader xmlReader) {
		xmlReader.skipWhitespaces();
		StringReader elementReader = new StringReader(xmlReader.readScope(ScopedStringReader.ANGLE_BRACKETS));
		elementReader.skip(); // Skip '<'
		elementReader.skipWhitespaces();
		
		String name = elementReader.readUntil(" ");
		if (name.isEmpty()) {
			throw new XmlSyntaxException("Expected element name, but found: '" + elementReader.getString() + "'");
		}
		if (!elementReader.canRead()) {
			if (name.charAt(name.length() - 1) != '>') {
				throw new XmlSyntaxException("Expected closing '>' after element name, but found: '" + elementReader.getString() + "'");
			}
			name = name.substring(0, name.length() - 1).stripTrailing();
			if (name.isEmpty()) {
				throw new XmlSyntaxException("Expected element name, but found: '" + elementReader.getString() + "'");
			}
			if (name.charAt(0) == '/') {
				return XmlElement.createSelfClosingNoAttributes(name.substring(0, name.length() - 1).stripTrailing());
			}
		
		}
	
	
		return null;
	}
	
	private @NotNull XmlAttributes readeXmlAttributes(@NotNull StringReader attributeReader) {
		XmlAttributes attributes = new XmlAttributes();
		attributeReader.skipWhitespaces();
		while (attributeReader.peek() != '>' && attributeReader.peek() != '/') {
			String name = attributeReader.readUntil(' ');
			attributeReader.skipWhitespaces();
			if (attributeReader.peek() != '=') {
				throw new XmlSyntaxException("Expected '=' after attribute key '" + name + "', but found: '" + attributeReader.peek() + "'");
			}
			attributeReader.skipWhitespaces();
			String value = attributeReader.readQuotedString();
			attributeReader.skipWhitespaces();
			attributes.add(name, value);
		}
		attributeReader.readRemaining();
		return attributes;
	}
	
	@Override
	public void close() throws Exception {
		this.reader.readRemaining();  // Assert that there is no remaining content
	}
}
