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

import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OptionalTokenRule}.<br>
 *
 * @author Luis-St
 */
class OptionalTokenRuleTest {
	
	private static final TokenDefinition NUMBER_DEFINITION = (word) -> word.matches("\\d+");
	private static final Token NUMBER_TOKEN_1 = SimpleToken.createUnpositioned(NUMBER_DEFINITION, "11");
	private static final Token STRINMG_TOKEN = SimpleToken.createUnpositioned("aa"::equals, "aa");
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new OptionalTokenRule(null));
		assertDoesNotThrow(() -> new OptionalTokenRule(NUMBER_DEFINITION));
	}
	
	@Test
	void tokenRule() {
		assertEquals(NUMBER_DEFINITION, new OptionalTokenRule(NUMBER_DEFINITION).tokenRule());
	}
	
	@Test
	void match() {
		OptionalTokenRule rule = new OptionalTokenRule(NUMBER_DEFINITION);
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
		assertNull(rule.match(Collections.emptyList(), 0));
		assertNull(rule.match(List.of(NUMBER_TOKEN_1, STRINMG_TOKEN), 2));
		
		TokenRuleMatch match0 = rule.match(List.of(NUMBER_TOKEN_1, STRINMG_TOKEN), 0);
		assertNotNull(match0);
		assertEquals(0, match0.startIndex());
		assertEquals(1, match0.endIndex());
		assertEquals(1, match0.matchedTokens().size());
		assertEquals(NUMBER_TOKEN_1, match0.matchedTokens().getFirst());
		
		TokenRuleMatch match1 = rule.match(List.of(STRINMG_TOKEN), 0);
		assertNotNull(match1);
		assertEquals(0, match1.startIndex());
		assertEquals(0, match1.endIndex());
		assertTrue(match1.matchedTokens().isEmpty());
	}
}
