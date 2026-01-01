/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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
 * Test class for {@link XmlDocument}.<br>
 *
 * @author Luis-St
 */
class XmlDocumentTest {
	
	@Test
	void constructorWithValidParameters() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		XmlElement rootElement = new XmlElement("root");
		
		XmlDocument document = new XmlDocument(declaration, rootElement);
		
		assertEquals(declaration, document.declaration());
		assertEquals(rootElement, document.rootElement());
	}
	
	@Test
	void constructorWithNullDeclarationThrowsException() {
		XmlElement rootElement = new XmlElement("root");
		
		assertThrows(NullPointerException.class, () -> new XmlDocument(null, rootElement));
	}
	
	@Test
	void constructorWithNullRootElementThrowsException() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		
		assertThrows(NullPointerException.class, () -> new XmlDocument(declaration, null));
	}
	
	@Test
	void constructorWithBothParametersNullThrowsException() {
		assertThrows(NullPointerException.class, () -> new XmlDocument(null, null));
	}
	
	@Test
	void accessorMethodsReturnCorrectValues() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 1));
		XmlElement rootElement = new XmlElement("document");
		XmlDocument document = new XmlDocument(declaration, rootElement);
		
		assertSame(declaration, document.declaration());
		assertSame(rootElement, document.rootElement());
	}
	
	@Test
	void toStringWithDefaultConfig() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		XmlElement rootElement = new XmlElement("test");
		XmlDocument document = new XmlDocument(declaration, rootElement);
		
		String result = document.toString();
		
		assertTrue(result.contains("<?xml"));
		assertTrue(result.contains("version=\"1.0\""));
		assertTrue(result.contains("<test/>"));
	}
	
	@Test
	void toStringWithCustomConfigNoPrettyPrint() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		XmlElement rootElement = new XmlElement("root");
		XmlDocument document = new XmlDocument(declaration, rootElement);
		XmlConfig config = new XmlConfig(false, false, "", true, false, StandardCharsets.UTF_8);
		
		String result = document.toString(config);
		
		assertFalse(result.contains(System.lineSeparator()));
		assertTrue(result.contains("<?xml"));
		assertTrue(result.contains("<root/>"));
	}
	
	@Test
	void toStringWithCustomConfigPrettyPrint() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		XmlElement rootElement = new XmlElement("root");
		XmlDocument document = new XmlDocument(declaration, rootElement);
		XmlConfig config = new XmlConfig(true, false, "", true, false, StandardCharsets.UTF_8);
		
		String result = document.toString(config);
		
		assertFalse(result.contains(System.lineSeparator()));
		assertTrue(result.contains("<?xml"));
		assertTrue(result.contains("<root/>"));
	}
	
	@Test
	void toStringWithNullConfigThrowsException() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		XmlElement rootElement = new XmlElement("test");
		XmlDocument document = new XmlDocument(declaration, rootElement);
		
		assertThrows(NullPointerException.class, () -> document.toString(null));
	}
	
	@Test
	void toStringContainsDeclarationAndRootElement() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 1), StandardCharsets.ISO_8859_1);
		XmlElement rootElement = new XmlElement("complex");
		XmlDocument document = new XmlDocument(declaration, rootElement);
		
		String result = document.toString();
		
		assertTrue(result.contains("version=\"1.1\""));
		assertTrue(result.contains("encoding=\"ISO-8859-1\""));
		assertTrue(result.contains("<complex/>"));
	}
	
	@Test
	void hashCodeConsistencyForSameObjects() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		XmlElement rootElement = new XmlElement("test");
		XmlDocument document1 = new XmlDocument(declaration, rootElement);
		XmlDocument document2 = new XmlDocument(declaration, rootElement);
		
		assertEquals(document1.hashCode(), document2.hashCode());
	}
	
	@Test
	void equalsWithSameContent() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		XmlElement rootElement = new XmlElement("test");
		XmlDocument document1 = new XmlDocument(declaration, rootElement);
		XmlDocument document2 = new XmlDocument(declaration, rootElement);
		
		assertEquals(document1, document2);
	}
	
	@Test
	void equalsWithDifferentDeclaration() {
		XmlDeclaration declaration1 = new XmlDeclaration(Version.of(1, 0));
		XmlDeclaration declaration2 = new XmlDeclaration(Version.of(1, 1));
		XmlElement rootElement = new XmlElement("test");
		XmlDocument document1 = new XmlDocument(declaration1, rootElement);
		XmlDocument document2 = new XmlDocument(declaration2, rootElement);
		
		assertNotEquals(document1, document2);
	}
	
	@Test
	void equalsWithDifferentRootElement() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		XmlElement rootElement1 = new XmlElement("test1");
		XmlElement rootElement2 = new XmlElement("test2");
		XmlDocument document1 = new XmlDocument(declaration, rootElement1);
		XmlDocument document2 = new XmlDocument(declaration, rootElement2);
		
		assertNotEquals(document1, document2);
	}
	
	@Test
	void equalsWithNull() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		XmlElement rootElement = new XmlElement("test");
		XmlDocument document = new XmlDocument(declaration, rootElement);
		
		assertNotEquals(document, null);
	}
	
	@Test
	void equalsWithDifferentType() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		XmlElement rootElement = new XmlElement("test");
		XmlDocument document = new XmlDocument(declaration, rootElement);
		
		assertNotEquals(document, "not a document");
	}
	
	@Test
	void equalsReflexive() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		XmlElement rootElement = new XmlElement("test");
		XmlDocument document = new XmlDocument(declaration, rootElement);
		
		assertEquals(document, document);
	}
}
