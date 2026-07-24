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

package net.luis.utils.grammar.token;

import net.luis.utils.grammar.token.type.StandardTokenType;
import net.luis.utils.grammar.token.type.TokenType;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SimpleToken}.<br>
 *
 * @author Luis-St
 */
class SimpleTokenTest {
	
	@Test
	void constructWithValueAndPosition() {
		SimpleToken token = new SimpleToken("v", TokenPosition.UNPOSITIONED);
		assertEquals("v", token.value());
		assertEquals(TokenPosition.UNPOSITIONED, token.position());
		assertTrue(token.types().isEmpty());
	}
	
	@Test
	void constructWithNullValueTwoArgThrows() {
		assertThrows(NullPointerException.class, () -> new SimpleToken(null, TokenPosition.UNPOSITIONED));
	}
	
	@Test
	void constructWithNullPositionTwoArgThrows() {
		assertThrows(NullPointerException.class, () -> new SimpleToken("v", null));
	}
	
	@Test
	void constructWithValuePositionAndTypes() {
		SimpleToken token = new SimpleToken("v", TokenPosition.UNPOSITIONED, Set.of(StandardTokenType.UNKNOWN));
		assertEquals("v", token.value());
		assertEquals(TokenPosition.UNPOSITIONED, token.position());
		assertTrue(token.types().contains(StandardTokenType.UNKNOWN));
	}
	
	@Test
	void constructWithNullValueThreeArgThrows() {
		assertThrows(NullPointerException.class, () -> new SimpleToken(null, TokenPosition.UNPOSITIONED, Set.of()));
	}
	
	@Test
	void constructWithNullPositionThreeArgThrows() {
		assertThrows(NullPointerException.class, () -> new SimpleToken("v", null, Set.of()));
	}
	
	@Test
	void constructWithNullTypesThrows() {
		assertThrows(NullPointerException.class, () -> new SimpleToken("v", TokenPosition.UNPOSITIONED, null));
	}
	
	@Test
	void createUnpositionedProducesUnpositionedToken() {
		SimpleToken token = SimpleToken.createUnpositioned("v");
		assertEquals("v", token.value());
		assertEquals(TokenPosition.UNPOSITIONED, token.position());
		assertTrue(token.types().isEmpty());
	}
	
	@Test
	void createUnpositionedWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> SimpleToken.createUnpositioned(null));
	}
	
	@Test
	void typesAreDefensivelyCopiedFromInputSet() {
		Set<TokenType> mutable = new HashSet<>(Set.of(StandardTokenType.UNKNOWN));
		SimpleToken token = new SimpleToken("v", TokenPosition.UNPOSITIONED, mutable);
		mutable.clear();
		assertTrue(token.types().contains(StandardTokenType.UNKNOWN));
	}
	
	@Test
	void toStringContainsValueAndPosition() {
		SimpleToken token = new SimpleToken("abc", TokenPosition.UNPOSITIONED);
		String result = token.toString();
		assertTrue(result.startsWith("SimpleToken[value=abc,position="));
		assertTrue(result.contains(TokenPosition.UNPOSITIONED.toString()));
	}
	
	@Test
	void toStringEscapesTabsAndNewlinesInValue() {
		SimpleToken token = new SimpleToken("a\tb\nc", TokenPosition.UNPOSITIONED);
		String result = token.toString();
		assertTrue(result.contains("a\\\\tb\\\\nc"));
		assertFalse(result.contains("\t"));
		assertFalse(result.contains("\n"));
	}
	
	@Test
	void typesReturnedFromConstructorAreIndependentlyMutable() {
		SimpleToken token = new SimpleToken("v", TokenPosition.UNPOSITIONED, Set.of());
		token.types().add(StandardTokenType.UNKNOWN);
		assertTrue(token.types().contains(StandardTokenType.UNKNOWN));
	}
}
