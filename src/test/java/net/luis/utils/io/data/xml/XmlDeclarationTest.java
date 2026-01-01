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
 * Test class for {@link XmlDeclaration}.<br>
 *
 * @author Luis-St
 */
class XmlDeclarationTest {
	
	@Test
	void constructorWithNullParameters() {
		assertThrows(NullPointerException.class, () -> new XmlDeclaration(null));
		assertThrows(NullPointerException.class, () -> new XmlDeclaration(null, StandardCharsets.UTF_8));
		assertThrows(NullPointerException.class, () -> new XmlDeclaration(Version.of(1, 0), null));
		assertThrows(NullPointerException.class, () -> new XmlDeclaration(null, StandardCharsets.UTF_8, false));
		assertThrows(NullPointerException.class, () -> new XmlDeclaration(Version.of(1, 0), null, false));
	}
	
	@Test
	void constructorWithInvalidVersion() {
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.of(0, 0)));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.of(-1, 0)));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.of(1, 10)));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.of(1, 0, 1)));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.builder(1, 0, 0).withBuild('r', 1).build()));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.builder(1, 0, 0).withSuffix("alpha").build()));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.builder(1, 0, 0).withSuffixVersion(1).build()));
	}
	
	@Test
	void constructorWithValidVersions() {
		assertDoesNotThrow(() -> new XmlDeclaration(Version.of(1, 0)));
		assertDoesNotThrow(() -> new XmlDeclaration(Version.of(1, 1)));
		assertDoesNotThrow(() -> new XmlDeclaration(Version.of(1, 9)));
		assertDoesNotThrow(() -> new XmlDeclaration(Version.of(2, 0)));
		assertDoesNotThrow(() -> new XmlDeclaration(Version.of(10, 5)));
	}
	
	@Test
	void constructorSingleParameter() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 0));
		
		assertEquals(Version.of(1, 0), declaration.version());
		assertEquals(StandardCharsets.UTF_8, declaration.encoding());
		assertFalse(declaration.standalone());
	}
	
	@Test
	void constructorTwoParameters() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(1, 1), StandardCharsets.UTF_16);
		
		assertEquals(Version.of(1, 1), declaration.version());
		assertEquals(StandardCharsets.UTF_16, declaration.encoding());
		assertFalse(declaration.standalone());
	}
	
	@Test
	void constructorThreeParameters() {
		XmlDeclaration declaration = new XmlDeclaration(Version.of(2, 0), StandardCharsets.ISO_8859_1, true);
		
		assertEquals(Version.of(2, 0), declaration.version());
		assertEquals(StandardCharsets.ISO_8859_1, declaration.encoding());
		assertTrue(declaration.standalone());
	}
	
	@Test
	void versionProperty() {
		assertEquals(Version.of(1, 0), new XmlDeclaration(Version.of(1, 0)).version());
		assertEquals(Version.of(1, 1), new XmlDeclaration(Version.of(1, 1), StandardCharsets.UTF_8).version());
		assertEquals(Version.of(2, 0), new XmlDeclaration(Version.of(2, 0), StandardCharsets.UTF_8, false).version());
		assertEquals(Version.of(1, 9), new XmlDeclaration(Version.of(1, 9), StandardCharsets.UTF_16, true).version());
	}
	
	@Test
	void encodingProperty() {
		assertEquals(StandardCharsets.UTF_8, new XmlDeclaration(Version.of(1, 0)).encoding());
		assertEquals(StandardCharsets.UTF_16, new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_16).encoding());
		assertEquals(StandardCharsets.ISO_8859_1, new XmlDeclaration(Version.of(1, 0), StandardCharsets.ISO_8859_1, false).encoding());
		assertEquals(StandardCharsets.US_ASCII, new XmlDeclaration(Version.of(1, 0), StandardCharsets.US_ASCII, true).encoding());
	}
	
	@Test
	void standaloneProperty() {
		assertFalse(new XmlDeclaration(Version.of(1, 0)).standalone());
		assertFalse(new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8).standalone());
		assertFalse(new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8, false).standalone());
		assertTrue(new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8, true).standalone());
	}
	
	@Test
	void equalsAndHashCode() {
		XmlDeclaration declaration1 = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8, false);
		XmlDeclaration declaration2 = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8, false);
		XmlDeclaration declaration3 = new XmlDeclaration(Version.of(1, 1), StandardCharsets.UTF_8, false);
		XmlDeclaration declaration4 = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_16, false);
		XmlDeclaration declaration5 = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8, true);
		
		assertEquals(declaration1, declaration2);
		assertEquals(declaration1.hashCode(), declaration2.hashCode());
		assertNotEquals(declaration1, declaration3);
		assertNotEquals(declaration1, declaration4);
		assertNotEquals(declaration1, declaration5);
		assertNotEquals(declaration1, null);
		assertNotEquals(declaration1, "string");
	}
	
	@Test
	void toStringMethod() {
		XmlDeclaration declaration1 = new XmlDeclaration(Version.of(1, 0));
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"false\"?>", declaration1.toString());
		
		XmlDeclaration declaration2 = new XmlDeclaration(Version.of(1, 1), StandardCharsets.UTF_16);
		assertEquals("<?xml version=\"1.1\" encoding=\"UTF-16\" standalone=\"false\"?>", declaration2.toString());
		
		XmlDeclaration declaration3 = new XmlDeclaration(Version.of(2, 0), StandardCharsets.ISO_8859_1, true);
		assertEquals("<?xml version=\"2.0\" encoding=\"ISO-8859-1\" standalone=\"true\"?>", declaration3.toString());
		
		XmlDeclaration declaration4 = new XmlDeclaration(Version.of(1, 5), StandardCharsets.US_ASCII, false);
		assertEquals("<?xml version=\"1.5\" encoding=\"US-ASCII\" standalone=\"false\"?>", declaration4.toString());
	}
	
	@Test
	void differentCharsets() {
		XmlDeclaration utf8 = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8);
		XmlDeclaration utf16 = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_16);
		XmlDeclaration iso = new XmlDeclaration(Version.of(1, 0), StandardCharsets.ISO_8859_1);
		XmlDeclaration ascii = new XmlDeclaration(Version.of(1, 0), StandardCharsets.US_ASCII);
		
		assertTrue(utf8.toString().contains("UTF-8"));
		assertTrue(utf16.toString().contains("UTF-16"));
		assertTrue(iso.toString().contains("ISO-8859-1"));
		assertTrue(ascii.toString().contains("US-ASCII"));
	}
	
	@Test
	void standaloneVariations() {
		XmlDeclaration notStandalone = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8, false);
		XmlDeclaration standalone = new XmlDeclaration(Version.of(1, 0), StandardCharsets.UTF_8, true);
		
		assertTrue(notStandalone.toString().contains("standalone=\"false\""));
		assertTrue(standalone.toString().contains("standalone=\"true\""));
	}
}
