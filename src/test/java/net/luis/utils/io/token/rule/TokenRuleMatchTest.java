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

package net.luis.utils.io.token.rule;

import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRuleMatch}.<br>
 *
 * @author Luis-St
 */
class TokenRuleMatchTest {
	
	private static final Token NUMBER_TOKEN = SimpleToken.createUnpositioned((word) -> word.matches("\\d+"), "11");
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new TokenRuleMatch(0, 0, null, TokenRules.alwaysMatch()));
		assertThrows(NullPointerException.class, () -> new TokenRuleMatch(0, 0, List.of(), null));
		assertDoesNotThrow(() -> new TokenRuleMatch(0, 0, List.of(), TokenRules.alwaysMatch()));
	}
	
	@Test
	void empty() {
		TokenRuleMatch match = TokenRuleMatch.empty(0);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertSame(TokenRules.alwaysMatch(), match.matchingTokenRule());
	}
	
	@Test
	void startIndex() {
		assertEquals(0, new TokenRuleMatch(0, 1, List.of(), TokenRules.alwaysMatch()).startIndex());
	}
	
	@Test
	void endIndex() {
		assertEquals(1, new TokenRuleMatch(0, 1, List.of(), TokenRules.alwaysMatch()).endIndex());
	}
	
	@Test
	void matchedTokens() {
		List<Token> tokens = List.of(NUMBER_TOKEN);
		assertEquals(tokens, new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch()).matchedTokens());
	}
	
	@Test
	void matchingTokenRule() {
		assertSame(TokenRules.alwaysMatch(), new TokenRuleMatch(0, 1, List.of(), TokenRules.alwaysMatch()).matchingTokenRule());
	}
}
