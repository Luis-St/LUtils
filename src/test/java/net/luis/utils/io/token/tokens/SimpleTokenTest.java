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
 * Test class for {@link SimpleToken}.<br>
 *
 * @author Luis-St
 */
class SimpleTokenTest {
	
	private static final TokenDefinition NUMBER_DEFINITION = (word) -> word.matches("\\d+");
	private static final TokenPosition START_POSITION = new TokenPosition(0, 0, 0);
	private static final TokenPosition END_POSITION = new TokenPosition(0, 2, 2);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new SimpleToken(null, "123", START_POSITION, END_POSITION));
		assertThrows(NullPointerException.class, () -> new SimpleToken(NUMBER_DEFINITION, null, START_POSITION, END_POSITION));
		assertThrows(NullPointerException.class, () -> new SimpleToken(NUMBER_DEFINITION, "123", null, END_POSITION));
		assertThrows(NullPointerException.class, () -> new SimpleToken(NUMBER_DEFINITION, "123", START_POSITION, null));
		
		assertThrows(IllegalArgumentException.class, () -> new SimpleToken(NUMBER_DEFINITION, "abc", START_POSITION, START_POSITION));
		assertThrows(IllegalArgumentException.class, () -> new SimpleToken(NUMBER_DEFINITION, "123", START_POSITION, new TokenPosition(0, 3, 3)));
		assertDoesNotThrow(() -> new SimpleToken(NUMBER_DEFINITION, "123", START_POSITION, TokenPosition.UNPOSITIONED));
		
		assertDoesNotThrow(() -> new SimpleToken(NUMBER_DEFINITION, "123", START_POSITION, END_POSITION));
	}
	
	@Test
	void createUnpositioned() {
		assertThrows(NullPointerException.class, () -> SimpleToken.createUnpositioned(null, "123"));
		assertThrows(NullPointerException.class, () -> SimpleToken.createUnpositioned(NUMBER_DEFINITION, null));
		
		assertDoesNotThrow(() -> SimpleToken.createUnpositioned(NUMBER_DEFINITION, "123"));
	}
	
	@Test
	void definition() {
		SimpleToken token = new SimpleToken(NUMBER_DEFINITION, "123", START_POSITION, END_POSITION);
		assertEquals(NUMBER_DEFINITION, token.definition());
	}
	
	@Test
	void value() {
		SimpleToken token = new SimpleToken(NUMBER_DEFINITION, "123", START_POSITION, END_POSITION);
		assertEquals("123", token.value());
	}
	
	@Test
	void startPosition() {
		SimpleToken token = new SimpleToken(NUMBER_DEFINITION, "123", START_POSITION, END_POSITION);
		assertEquals(START_POSITION, token.startPosition());
		assertEquals(0, token.startPosition().line());
		assertEquals(0, token.startPosition().character());
		assertEquals(0, token.startPosition().characterInLine());
	}
	
	@Test
	void endPosition() {
		SimpleToken token = new SimpleToken(NUMBER_DEFINITION, "123", START_POSITION, END_POSITION);
		assertEquals(END_POSITION, token.endPosition());
		assertEquals(0, token.endPosition().line());
		assertEquals(2, token.endPosition().character());
		assertEquals(2, token.endPosition().characterInLine());
	}
}
