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

package net.luis.utils.io.token.definition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringTokenDefinition}.<br>
 *
 * @author Luis-St
 */
class StringTokenDefinitionTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new StringTokenDefinition(null, false));
		assertThrows(NullPointerException.class, () -> new StringTokenDefinition(null, true));
		assertThrows(IllegalArgumentException.class, () -> new StringTokenDefinition("", false));
		assertThrows(IllegalArgumentException.class, () -> new StringTokenDefinition("", true));
		assertDoesNotThrow(() -> new StringTokenDefinition("a", false));
		assertDoesNotThrow(() -> new StringTokenDefinition("a", true));
		assertDoesNotThrow(() -> new StringTokenDefinition(" ", false));
	}
	
	@Test
	void token() {
		assertEquals("a", new StringTokenDefinition("a", false).token());
		assertEquals("a", new StringTokenDefinition("a", true).token());
		assertEquals(" ", new StringTokenDefinition(" ", false).token());
	}
	
	@Test
	void equalsIgnoreCase() {
		assertFalse(new StringTokenDefinition("a", false).equalsIgnoreCase());
		assertTrue(new StringTokenDefinition("a", true).equalsIgnoreCase());
	}
	
	@Test
	void matches() {
		StringTokenDefinition definition0 = new StringTokenDefinition("a", false);
		assertThrows(NullPointerException.class, () -> definition0.matches(null));
		assertFalse(definition0.matches(""));
		assertTrue(definition0.matches("a"));
		assertFalse(definition0.matches("A"));
		assertFalse(definition0.matches("aa"));
		
		StringTokenDefinition definition1 = new StringTokenDefinition("a", true);
		assertThrows(NullPointerException.class, () -> definition1.matches(null));
		assertFalse(definition1.matches(""));
		assertTrue(definition1.matches("a"));
		assertTrue(definition1.matches("A"));
		assertFalse(definition1.matches("aa"));
	}
}
