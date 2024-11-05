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
import net.luis.utils.io.FileUtils;
import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.xml.exception.XmlSyntaxException;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringReader;
import net.luis.utils.util.Version;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class XmlReader implements AutoCloseable {
	
	private static final List<String> DECLARATION_ATTRIBUTES = List.of("version", "encoding", "standalone");
	private static final String COMMENT_PATTERN = "<!--.*?-->";
	
	private final XmlConfig config;
	private final ScopedStringReader reader;
	private boolean readDeclaration;
	
	public XmlReader(@NotNull String string) {
		this(string, XmlConfig.DEFAULT);
	}
	
	public XmlReader(@NotNull String string, @NotNull XmlConfig config) {
		this.config = Objects.requireNonNull(config, "Xml config must not be null");
		this.reader = new ScopedStringReader(deleteComments(string));
	}
	
	public XmlReader(@NotNull InputProvider input) {
		this(input, XmlConfig.DEFAULT);
	}
	
	public XmlReader(@NotNull InputProvider input, @NotNull XmlConfig config) {
		Objects.requireNonNull(input, "Input must not be null");
		this.config = Objects.requireNonNull(config, "Xml config must not be null");
		try {
			String content = FileUtils.readString(input, config.charset());
			this.reader = new ScopedStringReader(deleteComments(content));
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read string from reader", e);
		}
	}
	
	private static @NotNull String deleteComments(@NotNull String string) {
		Objects.requireNonNull(string, "String must not be null");
		return string.replaceAll(COMMENT_PATTERN, "");
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
	
	public @NotNull XmlElement readeXmlElement() {
		if (!this.readDeclaration) {
			throw new IllegalStateException("Xml declaration must be read before reading xml elements");
		}
		XmlElement element = this.readeXmlElement(this.reader);
		this.reader.skipWhitespaces();
		return element;
	}
	
	private @NotNull XmlElement readeXmlElement(@NotNull ScopedStringReader xmlReader) {
		xmlReader.skipWhitespaces();
		StringReader elementReader = new StringReader(xmlReader.readScope(ScopedStringReader.ANGLE_BRACKETS));
		elementReader.skip();
		elementReader.skipWhitespaces();
		
		String name = elementReader.readUntil(' ');
		if (name.isEmpty()) {
			throw new XmlSyntaxException("Expected element name, but found none");
		}
		elementReader.skipWhitespaces();
		XmlAttributes attributes = new XmlAttributes();
		if (elementReader.canRead() && elementReader.peek() != '/' && elementReader.peek() != '>') {
			attributes = this.readXmlAttributes(elementReader);
			if (elementReader.peek() == '/') {
				elementReader.skip();
				elementReader.skipWhitespaces();
				char next = elementReader.read();
				if (next != '>') {
					throw new XmlSyntaxException("Expected '>' after self-closing element, but found: '" + next + "'");
				}
				return new XmlElement(name, attributes);
			} else if (elementReader.peek() != '>') {
				throw new XmlSyntaxException("Expected closing '>' after xml attributes, but found: '" + elementReader.peek() + "'");
			}
			elementReader.skip();
		} else {
			name += elementReader.readRemaining();
			if (name.charAt(name.length() - 1) != '>') {
				throw new XmlSyntaxException("Expected closing '>' after element name, but found: '" + name + "'");
			}
			name = name.substring(0, name.length() - 1).stripTrailing();
			if (name.isEmpty()) {
				throw new XmlSyntaxException("Expected element name, but found empty element");
			}
			if (name.charAt(name.length() - 1) == '/') {
				return new XmlElement(name.substring(0, name.length() - 1).stripTrailing());
			}
		}
		int closingIndex = this.getClosingElement(xmlReader, name);
		String content = xmlReader.read(closingIndex).stripIndent();
		String next = xmlReader.read(2);
		if (!"</".equals(next)) {
			throw new XmlSyntaxException("Expected closing element for '" + name + "', but found: '" + next + "'");
		}
		xmlReader.skipWhitespaces();
		xmlReader.readExpected(name, false);
		xmlReader.skipWhitespaces();
		if (xmlReader.peek() != '>') {
			throw new XmlSyntaxException("Expected closing '>' after element name, but found: '" + xmlReader.peek() + "'");
		}
		xmlReader.skip();
		if (content.contains("<")) {
			return new XmlContainer(name, attributes, this.readeXmlElements(new ScopedStringReader(content)));
		}
		return new XmlValue(name, attributes, content.strip());
	}
	
	private @NotNull XmlAttributes readXmlAttributes(@NotNull StringReader attributeReader) {
		XmlAttributes attributes = new XmlAttributes();
		attributeReader.skipWhitespaces();
		while (attributeReader.peek() != '>' && attributeReader.peek() != '/') {
			String name = attributeReader.readUntilInclusive('=', ' ');
			attributeReader.skipWhitespaces();
			if (attributeReader.peek() == '=') {
				attributeReader.skip();
			} else {
				name = name.substring(0, name.length() - 1);
			}
			attributeReader.skipWhitespaces();
			String value = attributeReader.readQuotedString();
			attributeReader.skipWhitespaces();
			attributes.add(name.strip(), value);
		}
		return attributes;
	}
	
	private int getClosingElement(@NotNull ScopedStringReader xmlReader, @NotNull String name) {
		String remaining = xmlReader.getString().substring(xmlReader.getIndex());
		StringReader reader = new StringReader(remaining);
		Deque<String> scope = new ArrayDeque<>();
		while (reader.canRead()) {
			reader.skipWhitespaces();
			if (reader.peek() == '\'' || reader.peek() == '"') {
				reader.readQuotedString();
				continue;
			} else if (reader.peek() == '<') {
				int resultIndex = reader.getIndex();
				reader.skip();
				if (reader.peek() == '/') {
					reader.skip();
					reader.skipWhitespaces();
					String elementName = reader.readUntil('>').strip();
					if (elementName.isEmpty()) {
						throw new XmlSyntaxException("Expected closing element for '" + name + "', but found none in: '" + remaining + "'");
					}
					if (scope.isEmpty() && elementName.equalsIgnoreCase(name)) {
						return resultIndex;
					} else if (!scope.isEmpty() && scope.peek().equalsIgnoreCase(elementName)) {
						scope.pop();
						continue;
					}
					throw new XmlSyntaxException("Unexpected closing element for '" + elementName + "', expected closing element for '" + (scope.isEmpty() ? name : scope.peek()) + "'");
				} else {
					reader.skipWhitespaces();
					String elementName = reader.readUntilInclusive(' ', '>');
					if (elementName.isEmpty()) {
						throw new XmlSyntaxException("Expected element name, but found none");
					}
					if (elementName.charAt(elementName.length() - 1) == ' ') {
						String remainingElement = reader.readUntil('>').stripTrailing();
						if (remainingElement.charAt(remainingElement.length() - 1) == '/') {
							continue;
						}
						scope.push(elementName.strip());
						continue;
					} else if (elementName.charAt(elementName.length() - 1) == '>') {
						elementName = elementName.substring(0, elementName.length() - 1).stripTrailing();
						if (elementName.isEmpty()) {
							throw new XmlSyntaxException("Expected element name, but found empty element");
						} else if (elementName.charAt(elementName.length() - 1) == '/') {
							continue;
						}
						scope.push(elementName.strip());
					} else {
						throw new XmlSyntaxException("Expected closing element for '" + name + "', but found none in: '" + remaining + "'");
					}
				}
			} else {
				reader.skip();
			}
		}
		throw new XmlSyntaxException("Expected closing element for '" + name + "', but found none in: '" + remaining + "'");
	}
	
	private @NotNull XmlElements readeXmlElements(@NotNull ScopedStringReader xmlReader) {
		xmlReader.skipWhitespaces();
		XmlElements elements = new XmlElements();
		while (xmlReader.canRead()) {
			if (xmlReader.peek() == '<') {
				elements.add(this.readeXmlElement(xmlReader));
			} else {
				throw new XmlSyntaxException("Expected '<' to start new element, but found: '" + xmlReader.peek() + "'");
			}
			xmlReader.skipWhitespaces();
		}
		return elements;
	}
	
	@Override
	public void close() throws Exception {
		this.reader.readRemaining();  // Assert that there is no remaining content
	}
}
