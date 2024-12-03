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

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.json.JsonReader;
import net.luis.utils.io.data.xml.exception.XmlSyntaxException;
import net.luis.utils.util.Version;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlReader}.<br>
 *
 * @author Luis-St
 */
class XmlReaderTest {
	
	private static final XmlConfig CUSTOM_CONFIG = new XmlConfig(false, false, "\t", false, false, StandardCharsets.UTF_8);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new XmlReader((String) null));
		assertDoesNotThrow(() -> new JsonReader("test"));
		
		assertThrows(NullPointerException.class, () -> new XmlReader((String) null, XmlConfig.DEFAULT));
		assertThrows(NullPointerException.class, () -> new XmlReader("test", null));
		assertDoesNotThrow(() -> new XmlReader("test", XmlConfig.DEFAULT));
		
		assertThrows(NullPointerException.class, () -> new XmlReader((InputProvider) null));
		assertDoesNotThrow(() -> new XmlReader(new InputProvider(InputStream.nullInputStream())));
		
		assertThrows(NullPointerException.class, () -> new XmlReader((InputProvider) null, XmlConfig.DEFAULT));
		assertThrows(NullPointerException.class, () -> new XmlReader(new InputProvider(InputStream.nullInputStream()), null));
		assertDoesNotThrow(() -> new XmlReader(new InputProvider(InputStream.nullInputStream()), XmlConfig.DEFAULT));
	}
	
	@Test
	void readDeclarationDefaultConfig() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<? version=\"v1.0\"?>").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml  version=\"v1.0\"?>").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml version=v1.0?>").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml version =\"v1.0\"?>").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml version= \"v1.0\"?>").readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml version=\"v1.0\"", CUSTOM_CONFIG).readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml encoding=\"UTF-8\"").readDeclaration());
		assertEquals(declaration, new XmlReader("<?xml version=\"v1.0\"?>").readDeclaration());
		
		XmlReader reader = new XmlReader("<?xml version=\"v1.0\"?>");
		assertEquals(declaration, reader.readDeclaration());
		assertThrows(IllegalStateException.class, reader::readDeclaration);
	}
	
	@Test
	void readDeclarationCustomConfig() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<? version=\"v1.0\"?>", CUSTOM_CONFIG).readDeclaration());
		assertDoesNotThrow(() -> new XmlReader("<?xml  version=\"v1.0\"?>", CUSTOM_CONFIG).readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml version=v1.0?>", CUSTOM_CONFIG).readDeclaration());
		assertDoesNotThrow(() -> new XmlReader("<?xml version =\"v1.0\"?>", CUSTOM_CONFIG).readDeclaration());
		assertDoesNotThrow(() -> new XmlReader("<?xml version= \"v1.0\"?>", CUSTOM_CONFIG).readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml version=\"v1.0\"", CUSTOM_CONFIG).readDeclaration());
		assertThrows(XmlSyntaxException.class, () -> new XmlReader("<?xml encoding=\"UTF-8\"", CUSTOM_CONFIG).readDeclaration());
		assertEquals(declaration, new XmlReader("<?xml version=\"v1.0\"?>").readDeclaration());
		
		XmlReader reader = new XmlReader("<?xml version=\"v1.0\"?>", CUSTOM_CONFIG);
		assertEquals(declaration, reader.readDeclaration());
		assertEquals(declaration, assertDoesNotThrow(reader::readDeclaration));
	}
	
	@Test
	void readXmlElementDefaultConfig() {
		String dec = "<?xml version=\"v1.0\"?>";
		
		XmlReader selfClosingReader = new XmlReader(dec + "<test/>");
		selfClosingReader.readDeclaration();
		assertEquals(new XmlElement("test"), selfClosingReader.readXmlElement());
		
		XmlReader emptyReader = new XmlReader(dec + "<test></test>");
		emptyReader.readDeclaration();
		assertEquals(new XmlValue("test", ""), emptyReader.readXmlElement());
	
		XmlReader valueReader = new XmlReader(dec + "<test>value</test>");
		valueReader.readDeclaration();
		assertEquals(new XmlValue("test", "value"), valueReader.readXmlElement());
		
		XmlReader attributeReader = new XmlReader(dec + "<test attribute1=\"value1\"></test>");
		attributeReader.readDeclaration();
		XmlValue attributeElement = new XmlValue("test", "");
		attributeElement.addAttribute("attribute1", "value1");
		assertEquals(attributeElement, attributeReader.readXmlElement());
	
		XmlReader multipleAttributesReader = new XmlReader(dec + "<test attribute1=\"value1\" attribute2=\"value2\"></test>");
		multipleAttributesReader.readDeclaration();
		attributeElement.addAttribute("attribute2", "value2");
		assertEquals(attributeElement, multipleAttributesReader.readXmlElement());
		
		XmlReader nestedReader = new XmlReader(dec + "<test><nested>value</nested></test>");
		nestedReader.readDeclaration();
		XmlContainer nestedElement = new XmlContainer("test");
		nestedElement.addValue(new XmlValue("nested", "value"));
		assertEquals(nestedElement, nestedReader.readXmlElement());
		
		XmlReader arrayReader = new XmlReader(dec + "<test><nested>value1</nested><nested>value2</nested></test>");
		arrayReader.readDeclaration();
		XmlContainer arrayElement = new XmlContainer("test");
		arrayElement.addValue(new XmlValue("nested", "value1"));
		arrayElement.addValue(new XmlValue("nested", "value2"));
		assertEquals(arrayElement, arrayReader.readXmlElement());
		
		XmlReader objectReader = new XmlReader(dec + "<test><nested1>value1</nested1><nested2>value2</nested2></test>");
		objectReader.readDeclaration();
		XmlContainer objectElement = new XmlContainer("test");
		objectElement.addValue(new XmlValue("nested1", "value1"));
		objectElement.addValue(new XmlValue("nested2", "value2"));
		assertEquals(objectElement, objectReader.readXmlElement());
		
		XmlReader advanced = new XmlReader(dec + "<test><name><first>First</first><second>Second</second></name><array><array_element>Value</array_element></array></test>");
		advanced.readDeclaration();
		XmlContainer advancedElement = new XmlContainer("test");
		XmlContainer nameElement = new XmlContainer("name");
		nameElement.addValue(new XmlValue("first", "First"));
		nameElement.addValue(new XmlValue("second", "Second"));
		advancedElement.addContainer(nameElement);
		XmlContainer arrayContainer = new XmlContainer("array");
		arrayContainer.addValue(new XmlValue("array_element", "Value"));
		advancedElement.addContainer(arrayContainer);
		assertEquals(advancedElement, advanced.readXmlElement());
		
		XmlReader missingDeclarationReader = new XmlReader(dec + "<test></test>");
		assertThrows(IllegalStateException.class, missingDeclarationReader::readXmlElement);
		
		XmlReader noElementNameReader = new XmlReader(dec + "<></>");
		noElementNameReader.readDeclaration();
		assertThrows(XmlSyntaxException.class, noElementNameReader::readXmlElement);
		
		XmlReader invalidSelfClosingReader = new XmlReader(dec + "<test/");
		invalidSelfClosingReader.readDeclaration();
		assertThrows(XmlSyntaxException.class, invalidSelfClosingReader::readXmlElement);
		
		XmlReader missingClosingCharReader = new XmlReader(dec + "<test attr=\"value\"</test>");
		missingClosingCharReader.readDeclaration();
		assertThrows(XmlSyntaxException.class, missingClosingCharReader::readXmlElement);
		
		XmlReader noClosingTagReader = new XmlReader(dec + "<test>");
		noClosingTagReader.readDeclaration();
		assertThrows(XmlSyntaxException.class, noClosingTagReader::readXmlElement);
		
		XmlReader invalidClosingTagReader = new XmlReader(dec + "<test></  test>");
		invalidClosingTagReader.readDeclaration();
		assertThrows(XmlSyntaxException.class, invalidClosingTagReader::readXmlElement);
		
		XmlReader missingClosingTagReader = new XmlReader(dec + "<test></test2>");
		missingClosingTagReader.readDeclaration();
		assertThrows(XmlSyntaxException.class, missingClosingTagReader::readXmlElement);
		
		XmlReader invalidNestedReader = new XmlReader(dec + "<test>b<nested></nested></test>");
		invalidNestedReader.readDeclaration();
		assertThrows(XmlSyntaxException.class, invalidNestedReader::readXmlElement);
	}
	
	@Test
	void readXmlElementCustomConfig() {
		String dec = "<?xml version=\"v1.0\"?>";
		
		XmlReader selfClosingReader = new XmlReader(dec + "<test/>", CUSTOM_CONFIG);
		selfClosingReader.readDeclaration();
		assertEquals(new XmlElement("test"), selfClosingReader.readXmlElement());
		
		XmlReader emptyReader = new XmlReader(dec + "<test></test>", CUSTOM_CONFIG);
		emptyReader.readDeclaration();
		assertEquals(new XmlValue("test", ""), emptyReader.readXmlElement());
		
		XmlReader valueReader = new XmlReader(dec + "<test>value</test>", CUSTOM_CONFIG);
		valueReader.readDeclaration();
		assertEquals(new XmlValue("test", "value"), valueReader.readXmlElement());
		
		XmlReader attributeReader = new XmlReader(dec + "<test attribute1=\"value1\"></test>", CUSTOM_CONFIG);
		attributeReader.readDeclaration();
		XmlValue attributeElement = new XmlValue("test", "");
		attributeElement.addAttribute("attribute1", "value1");
		assertThrows(XmlSyntaxException.class, attributeReader::readXmlElement);
		
		XmlReader multipleAttributesReader = new XmlReader(dec + "<test attribute1=\"value1\" attribute2=\"value2\"></test>", CUSTOM_CONFIG);
		multipleAttributesReader.readDeclaration();
		attributeElement.addAttribute("attribute2", "value2");
		assertThrows(XmlSyntaxException.class, multipleAttributesReader::readXmlElement);
		
		XmlReader nestedReader = new XmlReader(dec + "<test><nested>value</nested></test>", CUSTOM_CONFIG);
		nestedReader.readDeclaration();
		XmlContainer nestedElement = new XmlContainer("test");
		nestedElement.addValue(new XmlValue("nested", "value"));
		assertEquals(nestedElement, nestedReader.readXmlElement());
		
		XmlReader arrayReader = new XmlReader(dec + "<test><nested>value1</nested><nested>value2</nested></test>", CUSTOM_CONFIG);
		arrayReader.readDeclaration();
		XmlContainer arrayElement = new XmlContainer("test");
		arrayElement.addValue(new XmlValue("nested", "value1"));
		arrayElement.addValue(new XmlValue("nested", "value2"));
		assertEquals(arrayElement, arrayReader.readXmlElement());
		
		XmlReader objectReader = new XmlReader(dec + "<test><nested1>value1</nested1><nested2>value2</nested2></test>", CUSTOM_CONFIG);
		objectReader.readDeclaration();
		XmlContainer objectElement = new XmlContainer("test");
		objectElement.addValue(new XmlValue("nested1", "value1"));
		objectElement.addValue(new XmlValue("nested2", "value2"));
		assertEquals(objectElement, objectReader.readXmlElement());
		
		XmlReader advanced = new XmlReader(dec + "<test><name><first>First</first><second>Second</second></name><array><array_element>Value</array_element></array></test>", CUSTOM_CONFIG);
		advanced.readDeclaration();
		XmlContainer advancedElement = new XmlContainer("test");
		XmlContainer nameElement = new XmlContainer("name");
		nameElement.addValue(new XmlValue("first", "First"));
		nameElement.addValue(new XmlValue("second", "Second"));
		advancedElement.addContainer(nameElement);
		XmlContainer arrayContainer = new XmlContainer("array");
		arrayContainer.addValue(new XmlValue("array_element", "Value"));
		advancedElement.addContainer(arrayContainer);
		assertEquals(advancedElement, advanced.readXmlElement());
		
		XmlReader missingDeclarationReader = new XmlReader(dec + "<test></test>", CUSTOM_CONFIG);
		assertDoesNotThrow(missingDeclarationReader::readXmlElement);
		
		XmlReader noElementNameReader = new XmlReader(dec + "<></>", CUSTOM_CONFIG);
		noElementNameReader.readDeclaration();
		assertThrows(XmlSyntaxException.class, noElementNameReader::readXmlElement);
		
		XmlReader invalidSelfClosingReader = new XmlReader(dec + "<test/", CUSTOM_CONFIG);
		invalidSelfClosingReader.readDeclaration();
		assertThrows(XmlSyntaxException.class, invalidSelfClosingReader::readXmlElement);
		
		XmlReader missingClosingCharReader = new XmlReader(dec + "<test attr=\"value\"</test>", CUSTOM_CONFIG);
		missingClosingCharReader.readDeclaration();
		assertThrows(XmlSyntaxException.class, missingClosingCharReader::readXmlElement);
		
		XmlReader noClosingTagReader = new XmlReader(dec + "<test>", CUSTOM_CONFIG);
		noClosingTagReader.readDeclaration();
		assertThrows(XmlSyntaxException.class, noClosingTagReader::readXmlElement);
		
		XmlReader invalidClosingTagReader = new XmlReader(dec + "<test></  test>", CUSTOM_CONFIG);
		invalidClosingTagReader.readDeclaration();
		assertDoesNotThrow(invalidClosingTagReader::readXmlElement);
		
		XmlReader missingClosingTagReader = new XmlReader(dec + "<test></test2>", CUSTOM_CONFIG);
		missingClosingTagReader.readDeclaration();
		assertThrows(XmlSyntaxException.class, missingClosingTagReader::readXmlElement);
		
		XmlReader invalidNestedReader = new XmlReader(dec + "<test>b<nested></nested></test>", CUSTOM_CONFIG);
		invalidNestedReader.readDeclaration();
		assertThrows(XmlSyntaxException.class, invalidNestedReader::readXmlElement);
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> new XmlReader(new InputProvider(InputStream.nullInputStream())));
	}
}
