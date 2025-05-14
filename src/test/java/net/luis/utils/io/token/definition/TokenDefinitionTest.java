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

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenDefinition}.<br>
 *
 * @author Luis-St
 */
class TokenDefinitionTest {
	
	private static final Token TOKEN_0 = SimpleToken.createUnpositioned("test"::equals, "test");
	private static final Token TOKEN_1 = SimpleToken.createUnpositioned("test1"::equals, "test1");
	private static final Token TOKEN_2 = SimpleToken.createUnpositioned("test2"::equals, "test2");
	private static final List<Token> TOKENS = List.of(TOKEN_0, TOKEN_1, TOKEN_2);
	
	@Test
	void of() {
		assertDoesNotThrow(() -> TokenDefinition.of('a'));
		assertDoesNotThrow(() -> TokenDefinition.of('\\'));
		
		assertThrows(NullPointerException.class, () -> TokenDefinition.of(null, false));
		assertThrows(NullPointerException.class, () -> TokenDefinition.of(null, true));
		assertThrows(IllegalArgumentException.class, () -> TokenDefinition.of("", false));
		assertThrows(IllegalArgumentException.class, () -> TokenDefinition.of("", true));
		assertDoesNotThrow(() -> TokenDefinition.of("a", false));
		assertDoesNotThrow(() -> TokenDefinition.of("a", true));
		assertDoesNotThrow(() -> TokenDefinition.of(" ", false));
	}
	
	@Test
	void ofEscaped() {
		assertDoesNotThrow(() -> TokenDefinition.ofEscaped('a'));
		assertDoesNotThrow(() -> TokenDefinition.ofEscaped('\\'));
	}
	
	@Test
	void match() {
		TokenDefinition testDefinition = TOKEN_0.definition();
		assertThrows(NullPointerException.class, () -> testDefinition.match(null, 0));
		assertNull(testDefinition.match(TOKENS, 1));
		assertNull(testDefinition.match(TOKENS, 3));
		assertNull(testDefinition.match(Collections.emptyList(), 0));
		
		TokenRuleMatch match = testDefinition.match(TOKENS, 0);
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(TOKEN_0, match.matchedTokens().get(0));
		assertEquals(testDefinition, match.matchingTokenRule());
	}
}
