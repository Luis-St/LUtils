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

import net.luis.utils.io.data.OutputProvider;
import net.luis.utils.util.Version;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlWriter}.<br>
 *
 * @author Luis-St
 */
class XmlWriterTest {
	
	@Test
	void constructorWithNullParameters() {
		assertThrows(NullPointerException.class, () -> new XmlWriter(null));
		assertThrows(NullPointerException.class, () -> new XmlWriter(null, XmlConfig.DEFAULT));
		assertThrows(NullPointerException.class, () -> new XmlWriter(new OutputProvider(OutputStream.nullOutputStream()), null));
	}
	
	@Test
	void constructorWithValidParameters() {
		OutputProvider provider = new OutputProvider(OutputStream.nullOutputStream());
		assertDoesNotThrow(() -> new XmlWriter(provider));
		assertDoesNotThrow(() -> new XmlWriter(provider, XmlConfig.DEFAULT));
		
		XmlConfig customConfig = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
		assertDoesNotThrow(() -> new XmlWriter(provider, customConfig));
	}
	
	@Test
	void writeDeclarationWithDefaultConfig() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		assertThrows(NullPointerException.class, () -> writer.writeDeclaration(null));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" + System.lineSeparator();
		assertEquals(expected, stream.toString());
		
		assertThrows(IllegalStateException.class, () -> writer.writeDeclaration(declaration));
		writer.close();
	}
	
	@Test
	void writeDeclarationWithCustomConfig() throws Exception {
		XmlConfig customConfig = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream), customConfig);
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>";
		assertEquals(expected, stream.toString());
		
		writer.writeDeclaration(declaration);
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeDeclarationVariations() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 1), StandardCharsets.UTF_16, true);
		writer.writeDeclaration(declaration);
		
		String expected = "<?xml version=\"1.1\" encoding=\"UTF-16\" standalone=\"true\"?>" + System.lineSeparator();
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlSelfClosingElement() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		assertThrows(NullPointerException.class, () -> writer.writeXml(null));
		assertThrows(IllegalStateException.class, () -> writer.writeXml(new XmlElement("test")));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlElement element = new XmlElement("test");
		writer.writeXml(element);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" + System.lineSeparator() + "<test/>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlWithoutDeclarationNonStrict() throws Exception {
		XmlConfig customConfig = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream), customConfig);
		
		XmlElement element = new XmlElement("test");
		writer.writeXml(element);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?><test/>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlElementWithAttributes() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlElement element = new XmlElement("test");
		element.addAttribute("attr1", "value1");
		element.addAttribute("attr2", "value2");
		writer.writeXml(element);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" + System.lineSeparator() + "<test attr1=\"value1\" attr2=\"value2\"/>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlValue() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlValue value = new XmlValue("test", "content");
		writer.writeXml(value);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" + System.lineSeparator() + "<test>content</test>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlValueWithAttributes() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlValue value = new XmlValue("test", "content");
		value.addAttribute("attr", "value");
		writer.writeXml(value);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" + System.lineSeparator() + "<test attr=\"value\">content</test>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlContainerEmpty() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlContainer container = new XmlContainer("test");
		writer.writeXml(container);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" + System.lineSeparator() + "<test></test>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlContainerWithSingleChild() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlContainer container = new XmlContainer("test");
		container.addValue(new XmlValue("child", "value"));
		writer.writeXml(container);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" + System.lineSeparator() + "<test>" + System.lineSeparator() + "\t<child>value</child>" + System.lineSeparator() + "</test>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlContainerWithMultipleChildren() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlContainer container = new XmlContainer("root");
		container.addValue(new XmlValue("child1", "value1"));
		container.addValue(new XmlValue("child2", "value2"));
		writer.writeXml(container);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" +
			System.lineSeparator() + "<root>" + System.lineSeparator() +
			"\t<child1>value1</child1>" + System.lineSeparator() +
			"\t<child2>value2</child2>" + System.lineSeparator() + "</root>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlNestedContainers() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlContainer root = new XmlContainer("root");
		XmlContainer child = new XmlContainer("child");
		child.addValue(new XmlValue("grandchild", "value"));
		root.addContainer(child);
		writer.writeXml(root);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" +
			System.lineSeparator() + "<root>" + System.lineSeparator() +
			"\t<child>" + System.lineSeparator() +
			"\t\t<grandchild>value</grandchild>" + System.lineSeparator() +
			"\t</child>" + System.lineSeparator() + "</root>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlWithAttributesDisabled() throws Exception {
		XmlConfig noAttributesConfig = new XmlConfig(true, true, "\t", false, true, StandardCharsets.UTF_8);
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream), noAttributesConfig);
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlElement element = new XmlElement("test");
		element.addAttribute("attr", "value");
		
		assertThrows(IllegalStateException.class, () -> writer.writeXml(element));
		writer.close();
	}
	
	@Test
	void writeXmlWithCustomConfig() throws Exception {
		XmlConfig customConfig = new XmlConfig(false, false, "  ", true, false, StandardCharsets.UTF_8);
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream), customConfig);
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlContainer root = new XmlContainer("root");
		root.addValue(new XmlValue("child", "value"));
		writer.writeXml(root);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" +
			"<root><child>value</child></root>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlArrayElements() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlContainer root = new XmlContainer("root");
		root.addValue(new XmlValue("item", "value1"));
		root.addValue(new XmlValue("item", "value2"));
		root.addValue(new XmlValue("item", "value3"));
		writer.writeXml(root);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" +
			System.lineSeparator() + "<root>" + System.lineSeparator() +
			"\t<item>value1</item>" + System.lineSeparator() +
			"\t<item>value2</item>" + System.lineSeparator() +
			"\t<item>value3</item>" + System.lineSeparator() + "</root>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlWithEscapedContent() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlValue value = new XmlValue("test", "<>&\"'");
		writer.writeXml(value);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" +
			System.lineSeparator() + "<test>&lt;&gt;&amp;&quot;&apos;</test>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlWithEscapedAttributes() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlElement element = new XmlElement("test");
		element.addAttribute("attr", "<>&\"'");
		writer.writeXml(element);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" +
			System.lineSeparator() + "<test attr=\"&lt;&gt;&amp;&quot;&apos;\"/>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlComplexDocument() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlContainer root = new XmlContainer("library");
		root.addAttribute("name", "My Library");
		
		XmlContainer book1 = new XmlContainer("book");
		book1.addAttribute("id", "1");
		book1.addValue(new XmlValue("title", "Java Programming"));
		book1.addValue(new XmlValue("author", "John Doe"));
		book1.addValue(new XmlValue("year", "2023"));
		
		XmlContainer book2 = new XmlContainer("book");
		book2.addAttribute("id", "2");
		book2.addValue(new XmlValue("title", "XML Processing"));
		book2.addValue(new XmlValue("author", "Jane Smith"));
		book2.addValue(new XmlValue("year", "2022"));
		
		root.addContainer(book1);
		root.addContainer(book2);
		writer.writeXml(root);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" +
			System.lineSeparator() + "<library name=\"My Library\">" + System.lineSeparator() +
			"\t<book id=\"1\">" + System.lineSeparator() +
			"\t\t<title>Java Programming</title>" + System.lineSeparator() +
			"\t\t<author>John Doe</author>" + System.lineSeparator() +
			"\t\t<year>2023</year>" + System.lineSeparator() +
			"\t</book>" + System.lineSeparator() +
			"\t<book id=\"2\">" + System.lineSeparator() +
			"\t\t<title>XML Processing</title>" + System.lineSeparator() +
			"\t\t<author>Jane Smith</author>" + System.lineSeparator() +
			"\t\t<year>2022</year>" + System.lineSeparator() +
			"\t</book>" + System.lineSeparator() + "</library>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	@Test
	void writeXmlWithDifferentCharsets() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlConfig utf16Config = new XmlConfig(true, true, "\t", true, true, StandardCharsets.UTF_16);
		XmlWriter writer = new XmlWriter(new OutputProvider(stream), utf16Config);
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_16);
		writer.writeDeclaration(declaration);
		
		XmlElement element = new XmlElement("test");
		writer.writeXml(element);
		
		String result = stream.toString(StandardCharsets.UTF_16);
		assertTrue(result.contains("UTF-16"));
		assertTrue(result.contains("<test/>"));
		writer.close();
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> {
			try (XmlWriter writer = new XmlWriter(new OutputProvider(OutputStream.nullOutputStream()))) {
				writer.writeDeclaration(new XmlDeclaration(Version.of(1, 0)));
				writer.writeXml(new XmlElement("test"));
			}
		});
	}
	
	@Test
	void writeMultipleElements() throws Exception {
		TestOutputStream stream = new TestOutputStream();
		XmlWriter writer = new XmlWriter(new OutputProvider(stream));
		
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		writer.writeDeclaration(declaration);
		
		XmlElement element1 = new XmlElement("test1");
		XmlElement element2 = new XmlElement("test2");
		
		writer.writeXml(element1);
		writer.writeXml(element2);
		
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>" + System.lineSeparator() + "<test1/><test2/>";
		assertEquals(expected, stream.toString());
		writer.close();
	}
	
	//region Helper class
	private static class TestOutputStream extends OutputStream {
		
		private final ByteArrayOutputStream delegate = new ByteArrayOutputStream();
		
		@Override
		public void write(int b) throws IOException {
			this.delegate.write(b);
		}
		
		@Override
		public void write(byte @NotNull [] b) throws IOException {
			this.delegate.write(b);
		}
		
		@Override
		public void write(byte @NotNull [] b, int off, int len) throws IOException {
			this.delegate.write(b, off, len);
		}
		
		@Override
		public String toString() {
			return this.delegate.toString(StandardCharsets.UTF_8);
		}
		
		public String toString(java.nio.charset.Charset charset) {
			return this.delegate.toString(charset);
		}
		
		@Override
		public void close() throws IOException {
			this.delegate.close();
		}
	}
	//endregion
}
