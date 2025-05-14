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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AnyOfTokenRule}.<br>
 *
 * @author Luis-St
 */
class AnyOfTokenRuleTest {
	
	private static final Token TOKEN_0 = SimpleToken.createUnpositioned("test0"::equals, "test0");
	private static final Token TOKEN_1 = SimpleToken.createUnpositioned("test1"::equals, "test1");
	private static final Token TOKEN_2 = SimpleToken.createUnpositioned("test2"::equals, "test2");
	private static final Set<TokenRule> TOKEN_DEFINITIONS = Set.of(TOKEN_0.definition(), TOKEN_1.definition());
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new AnyOfTokenRule(null));
		assertThrows(IllegalArgumentException.class, () -> new AnyOfTokenRule(Collections.emptySet()));
		assertDoesNotThrow(() -> new AnyOfTokenRule(TOKEN_DEFINITIONS));
	}
	
	@Test
	void tokenRules() {
		AnyOfTokenRule rule = new AnyOfTokenRule(TOKEN_DEFINITIONS);
		
		assertIterableEquals(TOKEN_DEFINITIONS, rule.tokenRules());
		assertThrows(UnsupportedOperationException.class, () -> rule.tokenRules().add(TOKEN_2.definition()));
	}
	
	@Test
	void match() {
		AnyOfTokenRule rule = new AnyOfTokenRule(TOKEN_DEFINITIONS);
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
		assertNull(rule.match(Collections.emptyList(), 0));
		assertNull(rule.match(List.of(TOKEN_0, TOKEN_2), 3));
		
		TokenRuleMatch match0 = rule.match(List.of(TOKEN_0, TOKEN_2), 0);
		assertNotNull(match0);
		assertEquals(0, match0.startIndex());
		assertEquals(1, match0.endIndex());
		assertEquals(1, match0.matchedTokens().size());
		assertEquals(TOKEN_0, match0.matchedTokens().getFirst());
		
		TokenRuleMatch match1 = rule.match(List.of(TOKEN_1, TOKEN_2), 0);
		assertNotNull(match1);
		assertEquals(0, match1.startIndex());
		assertEquals(1, match1.endIndex());
		assertEquals(1, match1.matchedTokens().size());
		assertEquals(TOKEN_1, match1.matchedTokens().getFirst());
		
		
		assertNull(rule.match(List.of(TOKEN_2, TOKEN_2), 0));
		
		AnyOfTokenRule emptyRule = new AnyOfTokenRule(Collections.emptySet());
		assertNull(emptyRule.match(List.of(TOKEN_0, TOKEN_1), 0));
	}
}
