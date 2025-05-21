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

package net.luis.utils.io.token.rule.rules;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EndTokenRule}.<br>
 *
 * @author Luis-St
 */
class EndTokenRuleTest {
	
	private static final Token TOKEN_0 = SimpleToken.createUnpositioned("test"::equals, "test");
	private static final Token TOKEN_1 = SimpleToken.createUnpositioned("test1"::equals, "test1");
	
	@Test
	void match() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
		assertNull(rule.match(List.of(TOKEN_0, TOKEN_1), 0));
		assertNull(rule.match(List.of(TOKEN_0, TOKEN_1), 1));
		
		List<Token> tokens = List.of(TOKEN_0, TOKEN_1);
		TokenRuleMatch match = rule.match(tokens, 2);
		assertNotNull(match);
		assertEquals(2, match.startIndex());
		assertEquals(2, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertSame(rule, match.matchingTokenRule());
		
		TokenRuleMatch beyondMatch = rule.match(tokens, 5);
		assertNotNull(beyondMatch);
		assertEquals(5, beyondMatch.startIndex());
		assertEquals(5, beyondMatch.endIndex());
		assertTrue(beyondMatch.matchedTokens().isEmpty());
		
		TokenRuleMatch emptyMatch = rule.match(Collections.emptyList(), 0);
		assertNotNull(emptyMatch);
		assertEquals(0, emptyMatch.startIndex());
		assertEquals(0, emptyMatch.endIndex());
		assertTrue(emptyMatch.matchedTokens().isEmpty());
	}
}
