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
 * Test class for {@link EscapedToken}.<br>
 *
 * @author Luis-St
 */
class EscapedTokenTest {
	
	@Test
	void constructWithValueAndPosition() {
		EscapedToken token = new EscapedToken("\\n", TokenPosition.UNPOSITIONED);
		assertEquals("\\n", token.value());
		assertEquals(TokenPosition.UNPOSITIONED, token.position());
		assertTrue(token.types().isEmpty());
	}
	
	@Test
	void constructWithNullValueTwoArgThrows() {
		assertThrows(NullPointerException.class, () -> new EscapedToken(null, TokenPosition.UNPOSITIONED));
	}
	
	@Test
	void constructWithNullPositionTwoArgThrows() {
		assertThrows(NullPointerException.class, () -> new EscapedToken("\\n", null));
	}
	
	@Test
	void constructWithValuePositionAndTypes() {
		EscapedToken token = new EscapedToken("\\t", TokenPosition.UNPOSITIONED, Set.of(StandardTokenType.UNKNOWN));
		assertEquals("\\t", token.value());
		assertEquals(TokenPosition.UNPOSITIONED, token.position());
		assertTrue(token.types().contains(StandardTokenType.UNKNOWN));
	}
	
	@Test
	void constructWithNullValueThreeArgThrows() {
		assertThrows(NullPointerException.class, () -> new EscapedToken(null, TokenPosition.UNPOSITIONED, Set.of()));
	}
	
	@Test
	void constructWithNullPositionThreeArgThrows() {
		assertThrows(NullPointerException.class, () -> new EscapedToken("\\t", null, Set.of()));
	}
	
	@Test
	void constructWithNullTypesThrows() {
		assertThrows(NullPointerException.class, () -> new EscapedToken("\\t", TokenPosition.UNPOSITIONED, null));
	}
	
	@Test
	void constructWithWrongLengthValueThrows() {
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken("\\ab", TokenPosition.UNPOSITIONED));
	}
	
	@Test
	void constructWithMissingBackslashPrefixThrows() {
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken("ab", TokenPosition.UNPOSITIONED));
	}
	
	@Test
	void constructWithEmptyValueThrows() {
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken("", TokenPosition.UNPOSITIONED));
	}
	
	@Test
	void createUnpositionedWithInvalidValueThrows() {
		assertThrows(IllegalArgumentException.class, () -> EscapedToken.createUnpositioned("xx"));
	}
	
	@Test
	void createUnpositionedWithNullValueThrows() {
		assertThrows(NullPointerException.class, () -> EscapedToken.createUnpositioned(null));
	}
	
	@Test
	void constructWithValidTwoCharBackslashValueSucceeds() {
		EscapedToken token = new EscapedToken("\\x", TokenPosition.UNPOSITIONED);
		assertEquals("\\x", token.value());
	}
	
	@Test
	void constructWithBackslashPrefixButWrongLengthThrows() {
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken("\\", TokenPosition.UNPOSITIONED));
	}
	
	@Test
	void createUnpositionedProducesUnpositionedToken() {
		EscapedToken token = EscapedToken.createUnpositioned("\\n");
		assertEquals("\\n", token.value());
		assertEquals(TokenPosition.UNPOSITIONED, token.position());
		assertTrue(token.types().isEmpty());
	}
	
	@Test
	void typesAreDefensivelyCopiedFromInputSet() {
		Set<TokenType> mutable = new HashSet<>(Set.of(StandardTokenType.UNKNOWN));
		EscapedToken token = new EscapedToken("\\n", TokenPosition.UNPOSITIONED, mutable);
		mutable.clear();
		assertTrue(token.types().contains(StandardTokenType.UNKNOWN));
	}
	
	@Test
	void toStringContainsEscapedValueAndPosition() {
		EscapedToken token = new EscapedToken("\\n", TokenPosition.UNPOSITIONED);
		assertTrue(token.toString().startsWith("EscapedToken[value="));
		assertTrue(token.toString().contains("\\\\n"));
	}
	
	@Test
	void toStringEscapesBackslashCharacterInValue() {
		EscapedToken token = new EscapedToken("\\\\", TokenPosition.UNPOSITIONED);
		assertTrue(token.toString().contains("\\\\\\\\"));
	}
	
	@Test
	void typesReturnedFromConstructorAreIndependentlyMutable() {
		EscapedToken token = new EscapedToken("\\n", TokenPosition.UNPOSITIONED, Set.of());
		token.types().add(StandardTokenType.UNKNOWN);
		assertTrue(token.types().contains(StandardTokenType.UNKNOWN));
	}
}
