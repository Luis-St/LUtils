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
 * Test class for {@link RepeatedTokenRule}.<br>
 *
 * @author Luis-St
 */
class RepeatedTokenRuleTest {
	
	private static final TokenDefinition NUMBER_DEFINITION = (word) -> word.matches("\\d+");
	private static final Token NUMBER_TOKEN = SimpleToken.createUnpositioned(NUMBER_DEFINITION, "123");
	private static final Token STRING_TOKEN = SimpleToken.createUnpositioned("test"::equals, "test");
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new RepeatedTokenRule(null, 1));
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(NUMBER_DEFINITION, -1));
		assertThrows(NullPointerException.class, () -> new RepeatedTokenRule(null, 1, 2));
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(NUMBER_DEFINITION, -1, 2));
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(NUMBER_DEFINITION, 3, 2));
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(NUMBER_DEFINITION, 0, 0));
		assertDoesNotThrow(() -> new RepeatedTokenRule(NUMBER_DEFINITION, 0, 2));
	}
	
	@Test
	void tokenRule() {
		assertSame(NUMBER_DEFINITION, new RepeatedTokenRule(NUMBER_DEFINITION, 0, 5).tokenRule());
	}
	
	@Test
	void minOccurrences() {
		assertEquals(2, new RepeatedTokenRule(NUMBER_DEFINITION, 2, 5).minOccurrences());
	}
	
	@Test
	void maxOccurrences() {
		assertEquals(4, new RepeatedTokenRule(NUMBER_DEFINITION, 1, 4).maxOccurrences());
	}
	
	@Test
	void match() {
		RepeatedTokenRule repeatedRule = new RepeatedTokenRule(NUMBER_DEFINITION, 2, 3);
		assertThrows(NullPointerException.class, () -> repeatedRule.match(null, 0));
		assertNull(repeatedRule.match(Collections.emptyList(), 0));
		assertNull(repeatedRule.match(List.of(STRING_TOKEN, STRING_TOKEN), 0));
		assertNull(repeatedRule.match(List.of(NUMBER_TOKEN, NUMBER_TOKEN, NUMBER_TOKEN), 5));
		
		List<Token> tokens0 = List.of(NUMBER_TOKEN, NUMBER_TOKEN, STRING_TOKEN);
		TokenRuleMatch match0 = repeatedRule.match(tokens0, 0);
		assertNotNull(match0);
		assertEquals(0, match0.startIndex());
		assertEquals(2, match0.endIndex());
		assertEquals(2, match0.matchedTokens().size());
		assertEquals(NUMBER_TOKEN, match0.matchedTokens().getFirst());
		assertEquals(NUMBER_TOKEN, match0.matchedTokens().getLast());
		
		List<Token> tokens1 = List.of(NUMBER_TOKEN, NUMBER_TOKEN, NUMBER_TOKEN, STRING_TOKEN);
		TokenRuleMatch match1 = repeatedRule.match(tokens1, 0);
		assertNotNull(match1);
		assertEquals(0, match1.startIndex());
		assertEquals(3, match1.endIndex());
		assertEquals(3, match1.matchedTokens().size());
		
		List<Token> tokens2 = List.of(STRING_TOKEN, NUMBER_TOKEN, NUMBER_TOKEN, NUMBER_TOKEN);
		TokenRuleMatch match2 = repeatedRule.match(tokens2, 1);
		assertNotNull(match2);
		assertEquals(1, match2.startIndex());
		assertEquals(4, match2.endIndex());
		assertEquals(3, match2.matchedTokens().size());
		
		List<Token> tokens3 = List.of(NUMBER_TOKEN, NUMBER_TOKEN, NUMBER_TOKEN, NUMBER_TOKEN, STRING_TOKEN);
		TokenRuleMatch match3 = repeatedRule.match(tokens3, 1);
		assertNotNull(match3);
		assertEquals(1, match3.startIndex());
		assertEquals(4, match3.endIndex());
		assertEquals(3, match3.matchedTokens().size());
		
		RepeatedTokenRule zeroMinRule = new RepeatedTokenRule(NUMBER_DEFINITION, 0, 2);
		List<Token> noMatchTokens = List.of(STRING_TOKEN, STRING_TOKEN);
		TokenRuleMatch zeroMatch = zeroMinRule.match(noMatchTokens, 0);
		assertNotNull(zeroMatch);
		assertEquals(0, zeroMatch.startIndex());
		assertEquals(0, zeroMatch.endIndex());
		assertTrue(zeroMatch.matchedTokens().isEmpty());
	}
}
