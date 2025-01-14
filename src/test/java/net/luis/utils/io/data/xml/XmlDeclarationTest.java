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
 * Test class for {@link XmlDeclaration}.<br>
 *
 * @author Luis-St
 */
class XmlDeclarationTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new XmlDeclaration(null));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.of(0, 0, 0)));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.of(1, 10, 0)));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.of(1, 0, 1)));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.builder(1, 0, 0).withBuild('r', 1).build()));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.builder(1, 0, 0).withSuffix("alpha").build()));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.builder(1, 0, 0).withSuffixVersion(1).build()));
		
		assertThrows(NullPointerException.class, () -> new XmlDeclaration(null, StandardCharsets.UTF_8));
		assertThrows(NullPointerException.class, () -> new XmlDeclaration(Version.ZERO, null));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.of(0, 0), StandardCharsets.UTF_8));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.of(1, 10), StandardCharsets.UTF_8));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.of(1, 0, 1), StandardCharsets.UTF_8));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.builder(1, 0, 0).withBuild('r', 1).build(), StandardCharsets.UTF_8));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.builder(1, 0, 0).withSuffix("alpha").build(), StandardCharsets.UTF_8));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.builder(1, 0, 0).withSuffixVersion(1).build(), StandardCharsets.UTF_8));
		
		assertThrows(NullPointerException.class, () -> new XmlDeclaration(null, StandardCharsets.UTF_8, false));
		assertThrows(NullPointerException.class, () -> new XmlDeclaration(Version.ZERO, null, false));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.of(0, 0), StandardCharsets.UTF_8, false));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.of(1, 10), StandardCharsets.UTF_8, false));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.of(1, 0, 1), StandardCharsets.UTF_8, false));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.builder(1, 0, 0).withBuild('r', 1).build(), StandardCharsets.UTF_8, false));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.builder(1, 0, 0).withSuffix("alpha").build(), StandardCharsets.UTF_8, false));
		assertThrows(IllegalArgumentException.class, () -> new XmlDeclaration(Version.builder(1, 0, 0).withSuffixVersion(1).build(), StandardCharsets.UTF_8, false));
	}
	
	@Test
	void version() {
		assertEquals(Version.of(2, 0, 0), new XmlDeclaration(Version.of(2, 0, 0)).version());
		assertEquals(Version.of(2, 1, 0), new XmlDeclaration(Version.of(2, 1, 0), StandardCharsets.UTF_8).version());
		assertEquals(Version.of(2, 2, 0), new XmlDeclaration(Version.of(2, 2, 0), StandardCharsets.UTF_8, false).version());
	}
	
	@Test
	void encoding() {
		assertEquals(StandardCharsets.UTF_8, new XmlDeclaration(Version.of(1, 0, 0)).encoding());
		assertEquals(StandardCharsets.UTF_16, new XmlDeclaration(Version.of(1, 0, 0), StandardCharsets.UTF_16).encoding());
		assertEquals(StandardCharsets.UTF_16, new XmlDeclaration(Version.of(1, 0, 0), StandardCharsets.UTF_16, false).encoding());
	}
	
	@Test
	void standalone() {
		assertFalse(new XmlDeclaration(Version.of(1, 0, 0)).standalone());
		assertFalse(new XmlDeclaration(Version.of(1, 0, 0), StandardCharsets.UTF_8).standalone());
		assertTrue(new XmlDeclaration(Version.of(1, 0, 0), StandardCharsets.UTF_8, true).standalone());
	}
}
