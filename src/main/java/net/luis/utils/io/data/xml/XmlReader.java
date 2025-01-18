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

import com.google.common.collect.Lists;
import net.luis.utils.exception.InvalidStringException;
import net.luis.utils.io.FileUtils;
import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.xml.exception.XmlSyntaxException;
import net.luis.utils.io.reader.*;
import net.luis.utils.util.Version;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * A xml reader that reads xml content from a {@link String string} or {@link InputProvider input provider}.<br>
 * The reader expects a xml declaration at the beginning of the content, which can be read with {@link #readDeclaration()}.<br>
 * After reading the declaration, the xml root element can be read with {@link #readXmlElement()}.<br>
 *
 * @author Luis-St
 */
public class XmlReader implements AutoCloseable {
	
	/**
	 * The possible attributes of a xml declaration.<br>
	 */
	private static final List<String> DECLARATION_ATTRIBUTES = List.of("version", "encoding", "standalone");
	/**
	 * The pattern to match comments in xml content.<br>
	 */
	private static final String COMMENT_PATTERN = "<!--.*?-->";
	
	/**
	 * The xml config of this reader.<br>
	 */
	private final XmlConfig config;
	/**
	 * The internal reader used to read the xml content.<br>
	 */
	private final ScopedStringReader reader;
	/**
	 * A flag to indicate if the xml declaration has been read.<br>
	 */
	private boolean readDeclaration;
	
	/**
	 * Constructs a new xml reader with the given string and default xml config.<br>
	 * @param string The string to read the xml content from
	 * @throws NullPointerException If the string is null
	 */
	public XmlReader(@NotNull String string) {
		this(string, XmlConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new xml reader with the given string and xml config.<br>
	 * @param string The string to read the xml content from
	 * @param config The xml config to use
	 * @throws NullPointerException If the string or xml config is null
	 */
	public XmlReader(@NotNull String string, @NotNull XmlConfig config) {
		this.config = Objects.requireNonNull(config, "Xml config must not be null");
		this.reader = new ScopedStringReader(deleteComments(string));
	}
	
	/**
	 * Constructs a new xml reader with the given input provider and default xml config.<br>
	 * @param input The input provider to read the xml content from
	 * @throws NullPointerException If the input provider is null
	 */
	public XmlReader(@NotNull InputProvider input) {
		this(input, XmlConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new xml reader with the given input provider and xml config.<br>
	 * @param input The input provider to read the xml content from
	 * @param config The xml config to use
	 * @throws NullPointerException If the input provider or xml config is null
	 */
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
	
	/**
	 * Deletes all xml comments from the given string.<br>
	 * @param string The string to delete the comments from
	 * @return The string without any xml comments
	 * @throws NullPointerException If the string is null
	 */
	private static @NotNull String deleteComments(@NotNull String string) {
		Objects.requireNonNull(string, "String must not be null");
		return string.replaceAll(COMMENT_PATTERN, "");
	}
	
	/**
	 * Reads the xml declaration from the xml content.<br>
	 * @return The xml declaration of the xml content
	 * @throws IllegalStateException If the xml declaration has already been read
	 * @throws XmlSyntaxException If the xml declaration is invalid
	 */
	public @NotNull XmlDeclaration readDeclaration() {
		try {
			if (this.readDeclaration) {
				if (this.config.strict()) {
					throw new IllegalStateException("Xml declaration has already been read");
				}
				return new XmlDeclaration(Version.of(1, 0));
			}
			this.reader.skipWhitespaces();
			StringReader declarationReader = new StringReader(this.reader.readScope(StringScope.ANGLE_BRACKETS));
			String type = declarationReader.readUntil(' ');
			if (!"<?xml".equalsIgnoreCase(type)) {
				throw new XmlSyntaxException("Expected xml declaration, but found: '" + declarationReader.getString() + "'");
			}
			if (!this.config.strict()) {
				declarationReader.skipWhitespaces();
			} else if (Character.isWhitespace(declarationReader.peek())) {
				throw new XmlSyntaxException("Expected attribute after '<?xml', but found whitespace");
			}
			
			Map<String, String> declarationAttributes = new HashMap<>();
			List<String> attributes = Lists.newArrayList(DECLARATION_ATTRIBUTES);
			while (declarationReader.peek() != '?' && !attributes.isEmpty()) {
				String attribute = declarationReader.readExpected(attributes, false);
				attributes.remove(attribute.toLowerCase());
				
				if (!this.config.strict()) {
					declarationReader.skipWhitespaces();
				}
				char next = declarationReader.read();
				if (next != '=') {
					throw new XmlSyntaxException("Expected '=' after attribute key '" + attribute + "' in xml declaration, but found: '" + next + "'");
				}
				if (!this.config.strict()) {
					declarationReader.skipWhitespaces();
				}
				
				String value = declarationReader.readQuotedString();
				declarationAttributes.put(attribute.toLowerCase(), value);
				if (this.config.strict()) {
					declarationReader.skip(Character::isWhitespace);
				} else {
					declarationReader.skipWhitespaces();
				}
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
		} catch (InvalidStringException e) {
			throw new XmlSyntaxException("Error while parsing xml declaration", e);
		}
	}
	
	/**
	 * Reads the xml root element from the xml content.<br>
	 * @return The xml root element read
	 * @throws IllegalStateException If the xml declaration has not been read
	 * @throws XmlSyntaxException If the xml content is invalid
	 */
	public @NotNull XmlElement readXmlElement() {
		if (!this.readDeclaration) {
			if (this.config.strict()) {
				throw new IllegalStateException("Xml declaration must be read before reading xml elements");
			} else {
				this.readDeclaration();
			}
		}
		XmlElement element = this.readXmlElement(this.reader);
		this.reader.skipWhitespaces();
		return element;
	}
	
	/**
	 * Reads a xml element from the given xml reader.<br>
	 * @param xmlReader The xml reader to read the xml element from
	 * @return The xml element read
	 * @throws XmlSyntaxException If the xml element is invalid
	 */
	private @NotNull XmlElement readXmlElement(@NotNull ScopedStringReader xmlReader) {
		try {
			xmlReader.skipWhitespaces();
			StringReader elementReader = new StringReader(xmlReader.readScope(StringScope.ANGLE_BRACKETS));
			elementReader.skip();
			if (!this.config.strict()) {
				elementReader.skipWhitespaces();
			}
			
			String name = elementReader.readUntil(' ');
			if (name.isBlank()) {
				throw new XmlSyntaxException("Expected element name, but found none");
			}
			if (!this.config.strict()) {
				elementReader.skipWhitespaces();
			}
			XmlAttributes attributes = new XmlAttributes();
			if (elementReader.canRead() && elementReader.peek() != '/' && elementReader.peek() != '>') {
				attributes = this.readXmlAttributes(elementReader);
				if (elementReader.peek() == '/') {
					elementReader.skip();
					this.skipWhitespacesConfigBased(elementReader);
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
			String content = closingIndex > 0 ? xmlReader.read(closingIndex).stripIndent() : "";
			String next = xmlReader.read(2);
			if (!"</".equals(next)) {
				throw new XmlSyntaxException("Expected closing element for '" + name + "', but found: '" + next + "'");
			}
			this.skipWhitespacesConfigBased(xmlReader);
			xmlReader.readExpected(name, false);
			this.skipWhitespacesConfigBased(xmlReader);
			if (xmlReader.peek() != '>') {
				throw new XmlSyntaxException("Expected closing '>' after element name, but found: '" + xmlReader.peek() + "'");
			}
			xmlReader.skip();
			if (content.contains("<")) {
				return new XmlContainer(name, attributes, this.readeXmlElements(new ScopedStringReader(content)));
			}
			return new XmlValue(name, attributes, content.strip());
		} catch (InvalidStringException e) {
			throw new XmlSyntaxException("Error while parsing xml element", e);
		}
	}
	
	/**
	 * Reads the xml attributes from the given attribute reader.<br>
	 * @param attributeReader The attribute reader to read the xml attributes from
	 * @return The xml attributes read
	 * @throws XmlSyntaxException If the xml attributes are invalid
	 */
	private @NotNull XmlAttributes readXmlAttributes(@NotNull StringReader attributeReader) {
		XmlAttributes attributes = new XmlAttributes();
		attributeReader.skipWhitespaces();
		while (attributeReader.peek() != '>' && attributeReader.peek() != '/') {
			char[] terminators = { '=', ' ' };
			if (this.config.strict()) {
				terminators = new char[] { '=' };
			}
			String name = attributeReader.readUntilInclusive(terminators);
			if (this.config.strict()) {
				name = name.substring(0, name.length() - 1);
				if (name.length() != name.strip().length()) {
					throw new XmlSyntaxException("Unexpected whitespace in attribute name '" + name + "'");
				}
			} else {
				attributeReader.skipWhitespaces();
				if (attributeReader.peek() == '=') {
					attributeReader.skip();
				} else {
					name = name.substring(0, name.length() - 1);
				}
			}
			if (!this.config.strict()) {
				attributeReader.skipWhitespaces();
			}
			String value = attributeReader.readQuotedString();
			attributeReader.skipWhitespaces();
			attributes.add(name.strip(), value);
		}
		if (!attributes.isEmpty() && !this.config.allowAttributes()) {
			throw new XmlSyntaxException("Attributes are not allowed in xml elements according to the xml config");
		}
		return attributes;
	}
	
	/**
	 * Gets the number of characters until the closing element of the given element name.<br>
	 * @param xmlReader The xml reader to read the closing element from
	 * @param name The name of the element to get the closing element for
	 * @return The number of characters
	 * @throws XmlSyntaxException If any syntax error occurs while reading the closing element
	 */
	private int getClosingElement(@NotNull ScopedStringReader xmlReader, @NotNull String name) {
		String remaining = xmlReader.getString().substring(xmlReader.getIndex());
		StringReader reader = new StringReader(remaining);
		Deque<String> scope = new ArrayDeque<>();
		while (reader.canRead()) {
			reader.skipWhitespaces();
			if (reader.peek() == '\'' || reader.peek() == '"') {
				reader.readQuotedString();
			} else if (reader.peek() == '<') {
				int resultIndex = reader.getIndex();
				reader.skip();
				if (reader.peek() == '/') {
					reader.skip();
					this.skipWhitespacesConfigBased(reader);
					if (this.config.strict() && reader.peek() == ' ') {
						throw new XmlSyntaxException("Expected element name, but found too many whitespaces after '/'");
					}
					String elementName = reader.readUntil('>').strip();
					if (elementName.isEmpty()) {
						throw new XmlSyntaxException("Expected closing element for '" + name + "', but found none");
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
					if (elementName.isBlank()) {
						throw new XmlSyntaxException("Expected element name, but found none");
					}
					if (elementName.charAt(elementName.length() - 1) == ' ') {
						String remainingElement = reader.readUntil('>').stripTrailing();
						if (remainingElement.charAt(remainingElement.length() - 1) == '/') {
							continue;
						}
						scope.push(elementName.strip());
					} else if (elementName.charAt(elementName.length() - 1) == '>') {
						elementName = elementName.substring(0, elementName.length() - 1).stripTrailing();
						if (elementName.isEmpty()) {
							throw new XmlSyntaxException("Expected element name, but found empty element");
						} else if (elementName.charAt(elementName.length() - 1) == '/') {
							continue;
						}
						scope.push(elementName.strip());
					} else {
						throw new XmlSyntaxException("Expected closing element for '" + name + "', but found none");
					}
				}
			} else {
				reader.skip();
			}
		}
		throw new XmlSyntaxException("Expected closing element for '" + name + "', but found none");
	}
	
	/**
	 * Reads the xml elements from the given xml reader.<br>
	 * @param xmlReader The xml reader to read the xml elements from
	 * @return The xml elements read
	 * @throws XmlSyntaxException If the xml elements are invalid
	 */
	private @NotNull XmlElements readeXmlElements(@NotNull ScopedStringReader xmlReader) {
		xmlReader.skipWhitespaces();
		XmlElements elements = new XmlElements();
		while (xmlReader.canRead()) {
			if (xmlReader.peek() == '<') {
				elements.add(this.readXmlElement(xmlReader));
			} else {
				throw new XmlSyntaxException("Expected '<' the start of new element, but found: '" + xmlReader.peek() + "'");
			}
			xmlReader.skipWhitespaces();
		}
		return elements;
	}
	
	/**
	 * Skips the next whitespace character based on the xml config.<br>
	 * In strict mode, only the next whitespace character is skipped; otherwise all whitespaces are skipped.<br>
	 * @param reader The reader to skip the whitespace character from
	 */
	private void skipWhitespacesConfigBased(@NotNull StringReader reader) {
		if (this.config.strict()) {
			char next = reader.peek();
			if (Character.isWhitespace(next)) {
				reader.skip();
			}
		} else {
			reader.skipWhitespaces();
		}
	}
	
	@Override
	public void close() throws Exception {
		this.reader.readRemaining(); // Assert that there is no remaining content
	}
}
