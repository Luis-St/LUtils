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

package net.luis.utils.io.token;

import com.google.common.collect.Sets;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.definition.WordTokenDefinition;
import net.luis.utils.io.token.tokens.Token;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenReader}.<br>
 *
 * @author Luis-St
 */
class TokenReaderTest {
	
	private static final TokenDefinition OPEN_PAREN = TokenDefinition.of('(');
	private static final TokenDefinition CLOSE_PAREN = TokenDefinition.of(')');
	
	private static final TokenDefinition ZERO = TokenDefinition.of('0');
	private static final TokenDefinition ONE = TokenDefinition.of('1');
	private static final TokenDefinition TWO = TokenDefinition.of('2');
	private static final TokenDefinition THREE = TokenDefinition.of('3');
	private static final TokenDefinition FOUR = TokenDefinition.of('4');
	private static final TokenDefinition FIVE = TokenDefinition.of('5');
	private static final TokenDefinition SIX = TokenDefinition.of('6');
	private static final TokenDefinition SEVEN = TokenDefinition.of('7');
	private static final TokenDefinition EIGHT = TokenDefinition.of('8');
	private static final TokenDefinition NINE = TokenDefinition.of('9');
	
	private static final Set<TokenDefinition> DEFINITIONS = Set.of(
		OPEN_PAREN, CLOSE_PAREN, ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE
	);
	private static final Set<Character> ALLOWED_CHARS = Set.of(
		'(', ')', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
	);
	private static final Set<Character> SEPARATORS = Sets.union(
		Set.of(' '),
		ALLOWED_CHARS
	);
	
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new TokenReader(null, ALLOWED_CHARS, SEPARATORS));
		assertThrows(NullPointerException.class, () -> new TokenReader(DEFINITIONS, null, SEPARATORS));
		assertThrows(NullPointerException.class, () -> new TokenReader(DEFINITIONS, ALLOWED_CHARS, null));
		assertThrows(IllegalArgumentException.class, () -> new TokenReader(Set.of(WordTokenDefinition.INSTANCE), ALLOWED_CHARS, SEPARATORS));
		assertDoesNotThrow(() -> new TokenReader(DEFINITIONS, ALLOWED_CHARS, SEPARATORS));
	}
	
	@Test
	void readTokens() {
		TokenReader reader = new TokenReader(DEFINITIONS, ALLOWED_CHARS, SEPARATORS);
		
		assertThrows(NullPointerException.class, () -> reader.readTokens(null));
		assertThrows(IllegalStateException.class, () -> reader.readTokens("(_)"));
		
		List<Token> parenTokens = reader.readTokens("(()())");
		assertEquals(6, parenTokens.size());
		assertEquals(OPEN_PAREN, parenTokens.getFirst().definition());
		assertEquals(OPEN_PAREN, parenTokens.get(1).definition());
		assertEquals(CLOSE_PAREN, parenTokens.get(2).definition());
		assertEquals(OPEN_PAREN, parenTokens.get(3).definition());
		assertEquals(CLOSE_PAREN, parenTokens.get(4).definition());
		assertEquals(CLOSE_PAREN, parenTokens.get(5).definition());
	
		List<Token> numberTokens = reader.readTokens("1234567890");
		assertEquals(10, numberTokens.size());
		assertEquals(ONE, numberTokens.get(0).definition());
		assertEquals(TWO, numberTokens.get(1).definition());
		assertEquals(THREE, numberTokens.get(2).definition());
		assertEquals(FOUR, numberTokens.get(3).definition());
		assertEquals(FIVE, numberTokens.get(4).definition());
		assertEquals(SIX, numberTokens.get(5).definition());
		assertEquals(SEVEN, numberTokens.get(6).definition());
		assertEquals(EIGHT, numberTokens.get(7).definition());
		assertEquals(NINE, numberTokens.get(8).definition());
		assertEquals(ZERO, numberTokens.get(9).definition());
	}
}
