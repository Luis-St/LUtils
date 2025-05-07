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

package net.luis.utils.io.token.tokens;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.definition.TokenDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EscapedToken}.<br>
 *
 * @author Luis-St
 */
class EscapedTokenTest {
	
	private static final TokenDefinition ESCAPED_DEFINITION = (word) -> word.startsWith("\\") && word.length() == 2;
	private static final TokenPosition START_POSITION = new TokenPosition(0, 0, 0);
	private static final TokenPosition END_POSITION = new TokenPosition(0, 1, 1);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new EscapedToken(null, "\\n", START_POSITION, END_POSITION));
		assertThrows(NullPointerException.class, () -> new EscapedToken(ESCAPED_DEFINITION, null, START_POSITION, END_POSITION));
		assertThrows(NullPointerException.class, () -> new EscapedToken(ESCAPED_DEFINITION, "\\n", null, END_POSITION));
		assertThrows(NullPointerException.class, () -> new EscapedToken(ESCAPED_DEFINITION, "\\n", START_POSITION, null));
		
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(ESCAPED_DEFINITION, "n", START_POSITION, END_POSITION));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(ESCAPED_DEFINITION, "\\\\n", START_POSITION, END_POSITION));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(ESCAPED_DEFINITION, "nn", START_POSITION, END_POSITION));
		
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken((word) -> word.matches("[a-z]+"), "\\n", START_POSITION, END_POSITION));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(ESCAPED_DEFINITION, "\\n", START_POSITION, new TokenPosition(0, 2, 2)));
		
		assertDoesNotThrow(() -> new EscapedToken(ESCAPED_DEFINITION, "\\n", START_POSITION, END_POSITION));
		assertDoesNotThrow(() -> new EscapedToken(ESCAPED_DEFINITION, "\\t", START_POSITION, END_POSITION));
		assertDoesNotThrow(() -> new EscapedToken(ESCAPED_DEFINITION, "\\\"", START_POSITION, END_POSITION));
	}
	
	@Test
	void definition() {
		EscapedToken token = new EscapedToken(ESCAPED_DEFINITION, "\\n", START_POSITION, END_POSITION);
		assertEquals(ESCAPED_DEFINITION, token.definition());
	}
	
	@Test
	void value() {
		EscapedToken token = new EscapedToken(ESCAPED_DEFINITION, "\\n", START_POSITION, END_POSITION);
		assertEquals("\\n", token.value());
		
		EscapedToken tokenT = new EscapedToken(ESCAPED_DEFINITION, "\\t", START_POSITION, END_POSITION);
		assertEquals("\\t", tokenT.value());
		
		EscapedToken tokenQuote = new EscapedToken(ESCAPED_DEFINITION, "\\\"", START_POSITION, END_POSITION);
		assertEquals("\\\"", tokenQuote.value());
	}
	
	@Test
	void startPosition() {
		EscapedToken token = new EscapedToken(ESCAPED_DEFINITION, "\\n", START_POSITION, END_POSITION);
		assertEquals(START_POSITION, token.startPosition());
		assertEquals(0, token.startPosition().line());
		assertEquals(0, token.startPosition().character());
		assertEquals(0, token.startPosition().characterInLine());
	}
	
	@Test
	void endPosition() {
		EscapedToken token = new EscapedToken(ESCAPED_DEFINITION, "\\n", START_POSITION, END_POSITION);
		assertEquals(END_POSITION, token.endPosition());
		assertEquals(0, token.endPosition().line());
		assertEquals(1, token.endPosition().character());
		assertEquals(1, token.endPosition().characterInLine());
	}
}
