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

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.xml.exception.XmlSyntaxException;
import net.luis.utils.util.Version;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlReader}.<br>
 *
 * @author Luis-St
 */
class XmlReaderTest {
	
	@Test
	void constructorWithNullParameters() {
		assertThrows(NullPointerException.class, () -> new XmlReader((String) null));
		assertThrows(NullPointerException.class, () -> new XmlReader((String) null, XmlConfig.DEFAULT));
		assertThrows(NullPointerException.class, () -> new XmlReader("test", null));
		assertThrows(NullPointerException.class, () -> new XmlReader((InputProvider) null));
		assertThrows(NullPointerException.class, () -> new XmlReader((InputProvider) null, XmlConfig.DEFAULT));
		assertThrows(NullPointerException.class, () -> new XmlReader(new InputProvider(InputStream.nullInputStream()), null));
	}
	
	@Test
	void constructorWithValidParameters() {
		assertDoesNotThrow(() -> new XmlReader("test"));
		assertDoesNotThrow(() -> new XmlReader("test", XmlConfig.DEFAULT));
		assertDoesNotThrow(() -> new XmlReader(new InputProvider(InputStream.nullInputStream())));
		assertDoesNotThrow(() -> new XmlReader(new InputProvider(InputStream.nullInputStream()), XmlConfig.DEFAULT));
		
		String xmlContent = "<?xml version=\"v1.0\"?><test/>";
		ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
		assertDoesNotThrow(() -> new XmlReader(new InputProvider(inputStream)));
	}
	
	@Test
	void readDeclarationValidWithDefaultConfig() {
		XmlDeclaration expected = new XmlDeclaration(Version.of(1, 0));
		
		assertEquals(expected, new XmlReader("<?xml version=\"v1.0\"?>").readDeclaration());
		assertEquals(expected, new XmlReader("<?xml version=\"v1.0\" ?>").readDeclaration());
		assertEquals(expected, new XmlReader("  <?xml version=\"v1.0\"?>  ").readDeclaration());
		
		XmlDeclaration withEncoding = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_16);
		assertEquals(withEncoding, new XmlReader("<?xml version=\"v1.0\" encoding=\"UTF-16\"?>").readDeclaration());
		
		XmlDeclaration standalone = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8, true);
		assertEquals(standalone, new XmlReader("<?xml version=\"v1.0\" standalone=\"yes\"?>").readDeclaration());
		
		XmlDeclaration full = new XmlDeclaration(Version.of(1, 1), StandardCharsets.ISO_8859_1, true);
		assertEquals(full, new XmlReader("<?xml version=\"v1.1\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>").readDeclaration());
	}
	
	@Test
	void readDeclarationInvalidWithDefaultConfig() {
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<? version=\"v1.0\"?>").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xmlversion=\"v1.0\"?>").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml  version=\"v1.0\"?>").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml version=v1.0?>").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml version =\"v1.0\"?>").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml version= \"v1.0\"?>").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml version=\"v1.0\"").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml encoding=\"UTF-8\"?>").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml standalone=\"yes\"?>").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml version=\"invalid\"?>").readDeclaration());
	}
	
	@Test
	void readDeclarationWithCustomConfig() {
		XmlConfig customConfig = new XmlConfig(false, false, "\t", false, false, StandardCharsets.UTF_8);
		XmlDeclaration expected = new XmlDeclaration(Version.of(1, 0));
		
		assertDoesNotThrow(() -> new XmlReader("<?xml  version=\"v1.0\"?>", customConfig).readDeclaration());
		assertDoesNotThrow(() -> new XmlReader("<?xml version =\"v1.0\"?>", customConfig).readDeclaration());
		assertDoesNotThrow(() -> new XmlReader("<?xml version= \"v1.0\"?>", customConfig).readDeclaration());
		assertEquals(expected, new XmlReader("<?xml version=\"v1.0\"?>", customConfig).readDeclaration());
		
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<? version=\"v1.0\"?>", customConfig).readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml version=v1.0?>", customConfig).readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml version=\"v1.0\"", customConfig).readDeclaration());
	}
	
	@Test
	void readDeclarationAlreadyRead() {
		XmlReader readerStrict = new XmlReader("<?xml version=\"v1.0\"?>");
		readerStrict.readDeclaration();
		assertThrows(IllegalStateException.class, readerStrict::readDeclaration);
		
		XmlConfig customConfig = new XmlConfig(false, false, "\t", false, false, StandardCharsets.UTF_8);
		XmlReader readerNonStrict = new XmlReader("<?xml version=\"v1.0\"?>", customConfig);
		readerNonStrict.readDeclaration();
		assertDoesNotThrow(readerNonStrict::readDeclaration);
	}
	
	@Test
	void readXmlElementSelfClosing() {
		String xml = "<?xml version=\"v1.0\"?><test/>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlElement expected = new XmlElement("test");
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementSelfClosingWithAttributes() {
		String xml = "<?xml version=\"v1.0\"?><test attr1=\"value1\" attr2=\"value2\"/>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlElement expected = new XmlElement("test");
		expected.addAttribute("attr1", "value1");
		expected.addAttribute("attr2", "value2");
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementEmpty() {
		String xml = "<?xml version=\"v1.0\"?><test></test>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlValue expected = new XmlValue("test", "");
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementWithValue() {
		String xml = "<?xml version=\"v1.0\"?><test>value</test>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlValue expected = new XmlValue("test", "value");
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementWithValueAndAttributes() {
		String xml = "<?xml version=\"v1.0\"?><test attribute1=\"value1\">content</test>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlValue expected = new XmlValue("test", "content");
		expected.addAttribute("attribute1", "value1");
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementNestedSingle() {
		String xml = "<?xml version=\"v1.0\"?><test><nested>value</nested></test>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlContainer expected = new XmlContainer("test");
		expected.addValue(new XmlValue("nested", "value"));
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementNestedArray() {
		String xml = "<?xml version=\"v1.0\"?><test><nested>value1</nested><nested>value2</nested></test>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlContainer expected = new XmlContainer("test");
		expected.addValue(new XmlValue("nested", "value1"));
		expected.addValue(new XmlValue("nested", "value2"));
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementNestedObject() {
		String xml = "<?xml version=\"v1.0\"?><test><nested1>value1</nested1><nested2>value2</nested2></test>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlContainer expected = new XmlContainer("test");
		expected.addValue(new XmlValue("nested1", "value1"));
		expected.addValue(new XmlValue("nested2", "value2"));
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementComplex() {
		String xml = "<?xml version=\"v1.0\"?>" +
			"<root>" +
			"<person id=\"1\">" +
			"<name>John</name>" +
			"<age>30</age>" +
			"<emails>" +
			"<email>john@example.com</email>" +
			"<email>john.doe@work.com</email>" +
			"</emails>" +
			"</person>" +
			"<person id=\"2\">" +
			"<name>Jane</name>" +
			"<age>25</age>" +
			"</person>" +
			"</root>";
		
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlContainer root = new XmlContainer("root");
		
		XmlContainer person1 = new XmlContainer("person");
		person1.addAttribute("id", "1");
		person1.addValue(new XmlValue("name", "John"));
		person1.addValue(new XmlValue("age", "30"));
		
		XmlContainer emails = new XmlContainer("emails");
		emails.addValue(new XmlValue("email", "john@example.com"));
		emails.addValue(new XmlValue("email", "john.doe@work.com"));
		person1.addContainer(emails);
		
		XmlContainer person2 = new XmlContainer("person");
		person2.addAttribute("id", "2");
		person2.addValue(new XmlValue("name", "Jane"));
		person2.addValue(new XmlValue("age", "25"));
		
		root.addContainer(person1);
		root.addContainer(person2);
		
		assertEquals(root, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementWithoutDeclarationStrict() {
		String xml = "<?xml version=\"v1.0\"?><test></test>";
		XmlReader reader = new XmlReader(xml);
		assertThrows(IllegalStateException.class, reader::readXmlElement);
	}
	
	@Test
	void readXmlElementWithoutDeclarationNonStrict() {
		XmlConfig customConfig = new XmlConfig(false, false, "\t", false, false, StandardCharsets.UTF_8);
		String xml = "<?xml version=\"v1.0\"?><test></test>";
		XmlReader reader = new XmlReader(xml, customConfig);
		assertDoesNotThrow(reader::readXmlElement);
	}
	
	@Test
	void readXmlElementInvalidSyntax() {
		String declaration = "<?xml version=\"v1.0\"?>";
		
		assertThrows(XmlSyntaxException.class, () -> {
			XmlReader reader = new XmlReader(declaration + "<></>");
			reader.readDeclaration();
			reader.readXmlElement();
		});
		
		assertThrows(XmlSyntaxException.class, () -> {
			XmlReader reader = new XmlReader(declaration + "<test/");
			reader.readDeclaration();
			reader.readXmlElement();
		});
		
		assertThrows(XmlSyntaxException.class, () -> {
			XmlReader reader = new XmlReader(declaration + "<test>");
			reader.readDeclaration();
			reader.readXmlElement();
		});
		
		assertThrows(XmlSyntaxException.class, () -> {
			XmlReader reader = new XmlReader(declaration + "<test></test2>");
			reader.readDeclaration();
			reader.readXmlElement();
		});
		
		assertThrows(XmlSyntaxException.class, () -> {
			XmlReader reader = new XmlReader(declaration + "<test>text<nested></nested></test>");
			reader.readDeclaration();
			reader.readXmlElement();
		});
	}
	
	@Test
	void readXmlElementWithAttributesDisabled() {
		XmlConfig noAttributesConfig = new XmlConfig(true, true, "\t", false, true, StandardCharsets.UTF_8);
		String xml = "<?xml version=\"v1.0\"?><test attribute=\"value\"></test>";
		XmlReader reader = new XmlReader(xml, noAttributesConfig);
		reader.readDeclaration();
		
		assertThrows(XmlSyntaxException.class, reader::readXmlElement);
	}
	
	@Test
	void readXmlElementWithEscapedContent() {
		String xml = "<?xml version=\"v1.0\"?><test>&lt;content&gt;</test>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlValue expected = new XmlValue("test", "<content>");
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementWithComments() {
		String xml = "<?xml version=\"v1.0\"?><!-- This is a comment --><test><!-- Another comment -->value</test>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlValue expected = new XmlValue("test", "value");
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementWithNamespaces() {
		String xml = "<?xml version=\"v1.0\"?><ns:test xmlns:ns=\"http://example.com\"><ns:child>value</ns:child></ns:test>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlContainer expected = new XmlContainer("ns:test");
		expected.addAttribute("xmlns:ns", "http://example.com");
		expected.addValue(new XmlValue("ns:child", "value"));
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementWithWhitespace() {
		String xml = "<?xml version=\"v1.0\"?>\n  <test>\n    <child>value</child>\n  </test>\n";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlContainer expected = new XmlContainer("test");
		expected.addValue(new XmlValue("child", "value"));
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementAttributeVariations() {
		String xml = "<?xml version=\"v1.0\"?><test attr1=\"value1\" attr2='value2' attr3=\"value with spaces\"></test>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlValue expected = new XmlValue("test", "");
		expected.addAttribute("attr1", "value1");
		expected.addAttribute("attr2", "value2");
		expected.addAttribute("attr3", "value with spaces");
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementAttributesWithCustomConfig() {
		XmlConfig customConfig = new XmlConfig(false, false, "\t", true, false, StandardCharsets.UTF_8);
		String xml = "<?xml version=\"v1.0\"?><test attr1 = \"value1\" attr2= 'value2'></test>";
		XmlReader reader = new XmlReader(xml, customConfig);
		reader.readDeclaration();
		
		XmlValue expected = new XmlValue("test", "");
		expected.addAttribute("attr1", "value1");
		expected.addAttribute("attr2", "value2");
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> {
			try (XmlReader reader = new XmlReader(new InputProvider(InputStream.nullInputStream()))) {
				// Reader should close without issues
			}
		});
		
		assertDoesNotThrow(() -> {
			try (XmlReader reader = new XmlReader("<?xml version=\"v1.0\"?><test/>")) {
				reader.readDeclaration();
				reader.readXmlElement();
			}
		});
	}
	
	@Test
	void readXmlElementEmptyElements() {
		String xml = "<?xml version=\"v1.0\"?><root><empty1/><empty2></empty2></root>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlContainer expected = new XmlContainer("root");
		expected.add(new XmlElement("empty1"));
		expected.addValue(new XmlValue("empty2", ""));
		assertEquals(expected, reader.readXmlElement());
	}
	
	@Test
	void readXmlElementMixedContent() {
		String xml = "<?xml version=\"v1.0\"?><root><container><value>test</value></container><simple>direct</simple></root>";
		XmlReader reader = new XmlReader(xml);
		reader.readDeclaration();
		
		XmlContainer expected = new XmlContainer("root");
		XmlContainer container = new XmlContainer("container");
		container.addValue(new XmlValue("value", "test"));
		expected.addContainer(container);
		expected.addValue(new XmlValue("simple", "direct"));
		assertEquals(expected, reader.readXmlElement());
	}
}
