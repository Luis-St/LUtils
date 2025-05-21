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
 * Test class for {@link EscapedTokenDefinition}.<br>
 *
 * @author Luis-St
 */
class EscapedTokenDefinitionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new EscapedTokenDefinition('a'));
		assertDoesNotThrow(() -> new EscapedTokenDefinition('\\'));
	}
	
	@Test
	void token() {
		assertEquals('a', new EscapedTokenDefinition('a').token());
		assertEquals('\\', new EscapedTokenDefinition('\\').token());
	}
	
	@Test
	void matches() {
		EscapedTokenDefinition definition = new EscapedTokenDefinition('a');
		assertThrows(NullPointerException.class, () -> definition.matches(null));
		assertFalse(definition.matches(""));
		assertFalse(definition.matches("a"));
		assertTrue(definition.matches("\\a"));
		assertFalse(definition.matches("a\\"));
		assertFalse(definition.matches("\\aa"));
	}
}
