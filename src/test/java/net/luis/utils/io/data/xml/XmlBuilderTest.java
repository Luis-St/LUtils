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

import net.luis.utils.util.Version;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlBuilder}.<br>
 *
 * @author Luis-St
 */
class XmlBuilderTest {
	
	@Test
	void createBuilder() {
		XmlBuilder builder = XmlBuilder.create();
		assertNotNull(builder);
		assertEquals(XmlConfig.DEFAULT, builder.getConfig());
		assertFalse(builder.isInContainer());
		assertEquals(0, builder.getContainerDepth());
	}
	
	@Test
	void createBuilderWithConfig() {
		XmlConfig customConfig = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
		XmlBuilder builder = XmlBuilder.create(customConfig);
		assertNotNull(builder);
		assertEquals(customConfig, builder.getConfig());
		
		assertThrows(NullPointerException.class, () -> XmlBuilder.create(null));
	}
	
	@Test
	void createContainer() {
		XmlBuilder builder = XmlBuilder.createContainer("root");
		assertNotNull(builder);
		assertTrue(builder.isInContainer());
		assertEquals(1, builder.getContainerDepth());
		
		assertThrows(NullPointerException.class, () -> XmlBuilder.createContainer(null));
	}
	
	@Test
	void createContainerWithConfig() {
		XmlConfig customConfig = new XmlConfig(false, false, "  ", true, false, StandardCharsets.UTF_8);
		XmlBuilder builder = XmlBuilder.createContainer("root", customConfig);
		assertNotNull(builder);
		assertEquals(customConfig, builder.getConfig());
		assertTrue(builder.isInContainer());
		
		assertThrows(NullPointerException.class, () -> XmlBuilder.createContainer(null, customConfig));
		assertThrows(NullPointerException.class, () -> XmlBuilder.createContainer("root", null));
	}
	
	@Test
	void buildSelfClosingElement() {
		XmlElement element = XmlBuilder.create()
			.element("test")
			.build();
		
		assertNotNull(element);
		assertEquals("test", element.getName());
		assertTrue(element.isSelfClosing());
		assertEquals("<test/>", element.toString());
	}
	
	@Test
	void buildSelfClosingElementWithAttributes() {
		XmlElement element = XmlBuilder.create()
			.element("test")
			.attribute("id", "1")
			.attribute("name", "example")
			.attribute("active", true)
			.attribute("count", 42)
			.build();
		
		assertNotNull(element);
		assertEquals("test", element.getName());
		assertEquals(4, element.getAttributes().size());
		assertEquals("1", element.getAttributeAsString("id"));
		assertEquals("example", element.getAttributeAsString("name"));
		assertTrue(element.getAttributeAsBoolean("active"));
		assertEquals(42, element.getAttributeAsInteger("count"));
	}
	
	@Test
	void buildValueElementString() {
		XmlValue value = XmlBuilder.create()
			.value("message", "Hello World")
			.buildValue();
		
		assertNotNull(value);
		assertEquals("message", value.getName());
		assertEquals("Hello World", value.getAsString());
		assertEquals("<message>Hello World</message>", value.toString());
	}
	
	@Test
	void buildValueElementBoolean() {
		XmlValue value = XmlBuilder.create()
			.value("active", true)
			.buildValue();
		
		assertNotNull(value);
		assertEquals("active", value.getName());
		assertTrue(value.getAsBoolean());
		assertEquals("<active>true</active>", value.toString());
	}
	
	@Test
	void buildValueElementNumber() {
		XmlValue value = XmlBuilder.create()
			.value("count", 42)
			.buildValue();
		
		assertNotNull(value);
		assertEquals("count", value.getName());
		assertEquals(42L, value.getAsNumber());
		assertEquals("<count>42</count>", value.toString());
	}
	
	@Test
	void buildValueElementWithAttributes() {
		XmlValue value = XmlBuilder.create()
			.value("user", "John Doe")
			.attribute("id", "123")
			.attribute("role", "admin")
			.buildValue();
		
		assertNotNull(value);
		assertEquals("user", value.getName());
		assertEquals("John Doe", value.getAsString());
		assertEquals("123", value.getAttributeAsString("id"));
		assertEquals("admin", value.getAttributeAsString("role"));
	}
	
	@Test
	void buildEmptyContainer() {
		XmlContainer container = XmlBuilder.create()
			.container("root")
			.end()
			.buildContainer();
		
		assertNotNull(container);
		assertEquals("root", container.getName());
		assertTrue(container.isEmpty());
		assertEquals("<root></root>", container.toString());
	}
	
	@Test
	void buildContainerWithSingleChild() {
		XmlContainer container = XmlBuilder.create()
			.container("root")
				.value("message", "Hello")
			.end()
			.buildContainer();
		
		assertNotNull(container);
		assertEquals("root", container.getName());
		assertEquals(1, container.size());
		assertEquals("Hello", container.getAsValue("message").getAsString());
	}
	
	@Test
	void buildContainerWithMultipleChildren() {
		XmlContainer container = XmlBuilder.create()
			.container("users")
				.value("user", "John")
					.attribute("id", "1")
				.value("user", "Jane")
					.attribute("id", "2")
			.end()
			.buildContainer();
		
		assertNotNull(container);
		assertEquals("users", container.getName());
		assertEquals(2, container.size());
		assertTrue(container.isContainerArray());
		assertEquals("John", container.getAsValue(0).getAsString());
		assertEquals("Jane", container.getAsValue(1).getAsString());
	}
	
	@Test
	void buildNestedContainers() {
		XmlContainer root = XmlBuilder.create()
			.container("library")
				.container("books")
					.value("book", "Java Programming")
						.attribute("id", "1")
					.value("book", "XML Processing")
						.attribute("id", "2")
				.end()
				.value("totalBooks", 2)
			.end()
			.buildContainer();
		
		assertNotNull(root);
		assertEquals("library", root.getName());
		assertEquals(2, root.size());
		
		XmlContainer books = root.getAsContainer("books");
		assertEquals("books", books.getName());
		assertEquals(2, books.size());
		assertTrue(books.isContainerArray());
		
		assertEquals(2L, root.getAsValue("totalBooks").getAsNumber());
	}
	
	@Test
	void buildComplexStructure() {
		XmlContainer root = XmlBuilder.create()
			.container("company")
				.attribute("name", "Tech Corp")
				.container("departments")
					.container("department")
						.attribute("id", "IT")
						.value("name", "Information Technology")
						.value("headCount", 25)
						.container("employees")
							.value("employee", "John Doe")
								.attribute("position", "Manager")
							.value("employee", "Jane Smith")
								.attribute("position", "Developer")
						.end()
					.end()
					.container("department")
						.attribute("id", "HR")
						.value("name", "Human Resources")
						.value("headCount", 10)
					.end()
				.end()
				.value("founded", 2010)
			.end()
			.buildContainer();
		
		assertNotNull(root);
		assertEquals("company", root.getName());
		assertEquals("Tech Corp", root.getAttributeAsString("name"));
		assertEquals(2, root.size());
		assertEquals(2010L, root.getAsValue("founded").getAsNumber());
		
		XmlContainer departments = root.getAsContainer("departments");
		assertEquals(2, departments.size());
	}
	
	@Test
	void attributesWithConfigurator() {
		XmlElement element = XmlBuilder.create()
			.element("test")
			.attributes(attrs -> {
				attrs.attribute("id", "1");
				attrs.attribute("name", "test");
				attrs.attribute("active", true);
				attrs.attribute("count", 42);
			})
			.build();
		
		assertNotNull(element);
		assertEquals(4, element.getAttributes().size());
		assertEquals("1", element.getAttributeAsString("id"));
		assertEquals("test", element.getAttributeAsString("name"));
		assertTrue(element.getAttributeAsBoolean("active"));
		assertEquals(42, element.getAttributeAsInteger("count"));
	}
	
	@Test
	void addPrebuiltElement() {
		XmlElement prebuilt = new XmlValue("prebuilt", "value");
		prebuilt.addAttribute("source", "external");
		
		XmlContainer container = XmlBuilder.create()
			.container("root")
				.value("normal", "built with builder")
				.add(prebuilt)
			.end()
			.buildContainer();
		
		assertNotNull(container);
		assertEquals(2, container.size());
		assertEquals("built with builder", container.getAsValue("normal").getAsString());
		assertEquals("value", container.getAsValue("prebuilt").getAsString());
		assertEquals("external", container.getAsValue("prebuilt").getAttributeAsString("source"));
	}
	
	@Test
	void childWithConfigurator() {
		XmlContainer container = XmlBuilder.create()
			.container("parent")
				.child(child -> child
					.value("name", "John")
					.attribute("id", "1")
				)
				.child(child -> child
					.element("separator")
				)
				.child(child -> child
					.container("nested")
						.value("data", "test")
					.end()
				)
			.end()
			.buildContainer();
		
		assertNotNull(container);
		assertEquals(3, container.size());
		assertEquals("John", container.getAsValue("name").getAsString());
		assertEquals("1", container.getAsValue("name").getAttributeAsString("id"));
		assertNotNull(container.get("separator"));
		assertTrue(container.get("separator").isSelfClosing());
		assertNotNull(container.getAsContainer("nested"));
		assertEquals("test", container.getAsContainer("nested").getAsValue("data").getAsString());
	}
	
	@Test
	void toXmlString() {
		String xml = XmlBuilder.create()
			.container("root")
				.value("message", "Hello World")
			.end()
			.toXml();
		
		assertNotNull(xml);
		assertTrue(xml.startsWith("<?xml version=\"1.0\""));
		assertTrue(xml.contains("<root>"));
		assertTrue(xml.contains("<message>Hello World</message>"));
		assertTrue(xml.contains("</root>"));
	}
	
	@Test
	void toXmlStringWithCustomDeclaration() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 1), StandardCharsets.UTF_16, true);
		String xml = XmlBuilder.create()
			.element("test")
			.toXml(declaration);
		
		assertNotNull(xml);
		assertTrue(xml.contains("version=\"1.1\""));
		assertTrue(xml.contains("encoding=\"UTF-16\""));
		assertTrue(xml.contains("standalone=\"true\""));
		assertTrue(xml.contains("<test/>"));
	}
	
	@Test
	void toDocument() {
		XmlDocument document = XmlBuilder.create()
			.container("root")
				.value("content", "test")
			.end()
			.toDocument();
		
		assertNotNull(document);
		assertNotNull(document.declaration());
		assertNotNull(document.rootElement());
		assertEquals("root", document.rootElement().getName());
		
		String xmlString = document.toString();
		assertTrue(xmlString.startsWith("<?xml"));
		assertTrue(xmlString.contains("<root>"));
	}
	
	@Test
	void toDocumentWithCustomDeclaration() {
		XmlDeclaration customDeclaration = new XmlDeclaration(Version.of(2, 0), StandardCharsets.UTF_16);
		XmlDocument document = XmlBuilder.create()
			.element("test")
			.toDocument(customDeclaration);
		
		assertNotNull(document);
		assertEquals(customDeclaration, document.declaration());
		assertEquals("test", document.rootElement().getName());
	}
	
	@Test
	void builderStateManagement() {
		XmlBuilder builder = XmlBuilder.create();
		
		assertFalse(builder.isInContainer());
		assertEquals(0, builder.getContainerDepth());
		
		builder.container("level1");
		assertTrue(builder.isInContainer());
		assertEquals(1, builder.getContainerDepth());
		
		builder.container("level2");
		assertTrue(builder.isInContainer());
		assertEquals(2, builder.getContainerDepth());
		
		builder.end();
		assertTrue(builder.isInContainer());
		assertEquals(1, builder.getContainerDepth());
		
		builder.end();
		assertFalse(builder.isInContainer());
		assertEquals(0, builder.getContainerDepth());
	}
	
	@Test
	void errorHandlingNoElementToBuild() {
		XmlBuilder builder = XmlBuilder.create();
		assertThrows(IllegalStateException.class, builder::build);
		assertThrows(IllegalStateException.class, builder::buildContainer);
		assertThrows(IllegalStateException.class, builder::buildValue);
		assertThrows(IllegalStateException.class, builder::toXml);
		assertThrows(IllegalStateException.class, builder::toDocument);
	}
	
	@Test
	void errorHandlingUnclosedContainers() {
		XmlBuilder builder = XmlBuilder.create()
			.container("root")
				.container("nested");
		
		assertThrows(IllegalStateException.class, builder::build);
		assertThrows(IllegalStateException.class, builder::buildContainer);
		assertThrows(IllegalStateException.class, builder::buildValue);
		assertThrows(IllegalStateException.class, builder::toXml);
		assertThrows(IllegalStateException.class, builder::toDocument);
	}
	
	@Test
	void errorHandlingNoCurrentElement() {
		XmlBuilder builder = XmlBuilder.create();
		
		assertThrows(IllegalStateException.class, () -> builder.attribute("test", "value"));
		assertThrows(IllegalStateException.class, () -> builder.attribute("test", true));
		assertThrows(IllegalStateException.class, () -> builder.attribute("test", 42));
		assertThrows(IllegalStateException.class, () -> builder.attributes(attrs -> {}));
	}
	
	@Test
	void errorHandlingNotInContainer() {
		XmlBuilder builder = XmlBuilder.create();
		
		assertThrows(IllegalStateException.class, () -> builder.add(new XmlElement("test")));
		assertThrows(IllegalStateException.class, () -> builder.child(child -> {}));
		assertThrows(IllegalStateException.class, builder::end);
	}
	
	@Test
	void errorHandlingElementAlreadyBuilding() {
		XmlBuilder builder = XmlBuilder.create().element("test");
		
		assertThrows(IllegalStateException.class, () -> builder.element("other"));
		assertThrows(IllegalStateException.class, () -> builder.container("other"));
		assertThrows(IllegalStateException.class, () -> builder.value("other", "value"));
	}
	
	@Test
	void errorHandlingWrongElementType() {
		XmlElement element = XmlBuilder.create()
			.element("test")
			.build();
		
		XmlBuilder builder = XmlBuilder.create().element("test");
		assertThrows(IllegalStateException.class, builder::buildContainer);
		assertThrows(IllegalStateException.class, builder::buildValue);
	}
	
	@Test
	void errorHandlingNullParameters() {
		XmlBuilder builder = XmlBuilder.create();
		
		assertThrows(NullPointerException.class, () -> XmlBuilder.create().element(null));
		assertThrows(NullPointerException.class, () -> XmlBuilder.create().container(null));
		assertThrows(NullPointerException.class, () -> XmlBuilder.create().value(null, "value"));
		assertThrows(NullPointerException.class, () -> XmlBuilder.create().value(null, true));
		assertThrows(NullPointerException.class, () -> XmlBuilder.create().value(null, 42));
		
		builder.element("test");
		assertThrows(NullPointerException.class, () -> XmlBuilder.create().attribute(null, "value"));
		assertThrows(NullPointerException.class, () -> XmlBuilder.create().attribute(null, true));
		assertThrows(NullPointerException.class, () -> XmlBuilder.create().attribute(null, 42));
		assertThrows(NullPointerException.class, () -> XmlBuilder.create().attributes(null));
		
		builder = XmlBuilder.create().container("test");
		assertThrows(NullPointerException.class, () -> XmlBuilder.create().add(null));
		assertThrows(NullPointerException.class, () -> XmlBuilder.create().child(null));
		
		builder = XmlBuilder.create().element("test");
		assertThrows(NullPointerException.class, () -> XmlBuilder.create().toXml(null));
		assertThrows(NullPointerException.class, () -> XmlBuilder.create().toDocument(null));
	}
	
	@Test
	void complexRealWorldExample() {
		String xml = XmlBuilder.create()
			.container("catalog")
				.attribute("xmlns", "http://example.com/catalog")
				.attribute("version", "1.0")
				.container("products")
					.container("product")
						.attribute("id", "P001")
						.attribute("featured", true)
						.value("name", "Smartphone")
						.value("price", 599.99)
						.value("description", "Latest smartphone with advanced features")
						.container("specifications")
							.value("screen", "6.1 inches")
							.value("storage", "128GB")
							.value("ram", "8GB")
						.end()
						.container("reviews")
							.value("review", "Excellent product!")
								.attribute("rating", 5)
								.attribute("reviewer", "John D.")
							.value("review", "Good value for money")
								.attribute("rating", 4)
								.attribute("reviewer", "Jane S.")
						.end()
					.end()
					.container("product")
						.attribute("id", "P002")
						.attribute("featured", false)
						.value("name", "Laptop")
						.value("price", 1299.99)
						.value("description", "High-performance laptop for professionals")
					.end()
				.end()
				.value("totalProducts", 2)
				.value("lastUpdated", "2025-01-15")
			.end()
			.toXml();
		
		assertNotNull(xml);
		assertTrue(xml.contains("<?xml version=\"1.0\""));
		assertTrue(xml.contains("<catalog xmlns=\"http://example.com/catalog\" version=\"1.0\">"));
		assertTrue(xml.contains("<product id=\"P001\" featured=\"true\">"));
		assertTrue(xml.contains("<name>Smartphone</name>"));
		assertTrue(xml.contains("<price>599.99</price>"));
		assertTrue(xml.contains("<specifications>"));
		assertTrue(xml.contains("<screen>6.1 inches</screen>"));
		assertTrue(xml.contains("<reviews>"));
		assertTrue(xml.contains("<review rating=\"5\" reviewer=\"John D.\">Excellent product!</review>"));
		assertTrue(xml.contains("<totalProducts>2</totalProducts>"));
		assertTrue(xml.contains("</catalog>"));
	}
	
	@Test
	void builderReuse() {
		XmlBuilder builder = XmlBuilder.create().element("test");
		
		XmlElement element = builder.build();
		assertNotNull(element);
		
		assertThrows(IllegalStateException.class, builder::build);
		
		assertDoesNotThrow(() -> builder.element("new"));
		XmlElement newElement = builder.build();
		assertEquals("new", newElement.getName());
	}
}
