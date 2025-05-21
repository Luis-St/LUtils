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

import com.google.common.collect.Lists;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SequenceTokenRule}.<br>
 *
 * @author Luis-St
 */
class SequenceTokenRuleTest {
	
	private static final Token TOKEN_0 = SimpleToken.createUnpositioned("test0"::equals, "test0");
	private static final Token TOKEN_1 = SimpleToken.createUnpositioned("test1"::equals, "test1");
	private static final Token TOKEN_2 = SimpleToken.createUnpositioned("test2"::equals, "test2");
	
	private static final TokenDefinition NUMBER_DEFINITION = (word) -> word.matches("\\d+");
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new SequenceTokenRule(null));
		assertThrows(IllegalArgumentException.class, () -> new SequenceTokenRule(Collections.emptyList()));
		
		List<TokenRule> rulesWithNull = Lists.newArrayList(TOKEN_0.definition(), null);
		assertThrows(NullPointerException.class, () -> new SequenceTokenRule(rulesWithNull));
		
		assertDoesNotThrow(() -> new SequenceTokenRule(List.of(TOKEN_0.definition(), TOKEN_1.definition())));
	}
	
	@Test
	void tokenRules() {
		List<TokenRule> rules = List.of(TOKEN_0.definition(), TOKEN_1.definition());
		SequenceTokenRule rule = new SequenceTokenRule(rules);
		
		List<TokenRule> tokenRules = rule.tokenRules();
		assertNotNull(tokenRules);
		assertEquals(2, tokenRules.size());
		assertEquals(rules, tokenRules);
		assertThrows(UnsupportedOperationException.class, () -> tokenRules.add(NUMBER_DEFINITION));
	}
	
	@Test
	void match() {
		List<Token> tokens = List.of(TOKEN_0, TOKEN_1, TOKEN_2);
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(TOKEN_0.definition(), TOKEN_1.definition()));
		
		assertThrows(NullPointerException.class, () -> sequenceRule.match(null, 0));
		assertNull(sequenceRule.match(Collections.emptyList(), 0));
		assertNull(sequenceRule.match(tokens, 5));
		
		TokenRuleMatch match = sequenceRule.match(tokens, 0);
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(2, match.endIndex());
		assertEquals(2, match.matchedTokens().size());
		assertEquals(TOKEN_0, match.matchedTokens().get(0));
		assertEquals(TOKEN_1, match.matchedTokens().get(1));
		assertEquals(sequenceRule, match.matchingTokenRule());
		
		assertNull(sequenceRule.match(tokens, 1));
		
		List<Token> incorrectTokens = List.of(TOKEN_1, TOKEN_0, TOKEN_2);
		assertNull(sequenceRule.match(incorrectTokens, 0));
	}
}
