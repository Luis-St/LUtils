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
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PatternTokenRule}.<br>
 *
 * @author Luis-St
 */
class PatternTokenRuleTest {
	
	private static final Token TOKEN_0 = SimpleToken.createUnpositioned("test"::equals, "test");
	private static final Token TOKEN_1 = SimpleToken.createUnpositioned("test1"::equals, "test1");
	private static final Token NUMBER_TOKEN = SimpleToken.createUnpositioned("123"::equals, "123");
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new PatternTokenRule((String) null));
		assertThrows(NullPointerException.class, () -> new PatternTokenRule((Pattern) null));
		assertThrows(Exception.class, () -> new PatternTokenRule("[a-z"));
		assertDoesNotThrow(() -> new PatternTokenRule("\\d+"));
	}
	
	@Test
	void pattern() {
		assertEquals("test\\d+", new PatternTokenRule("test\\d+").pattern().pattern());
		
		Pattern pattern = Pattern.compile("\\d+");
		assertEquals(pattern, new PatternTokenRule(pattern).pattern());
	}
	
	@Test
	void match() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
		assertNull(rule.match(List.of(TOKEN_0), 0));
		assertNull(rule.match(Collections.emptyList(), 0));
		assertNull(rule.match(List.of(NUMBER_TOKEN), 1));
		
		TokenRuleMatch match0 = rule.match(List.of(NUMBER_TOKEN), 0);
		assertNotNull(match0);
		assertEquals(0, match0.startIndex());
		assertEquals(1, match0.endIndex());
		assertEquals(1, match0.matchedTokens().size());
		assertEquals(NUMBER_TOKEN, match0.matchedTokens().getFirst());
		
		PatternTokenRule testRule = new PatternTokenRule("test1");
		TokenRuleMatch match1 = testRule.match(List.of(TOKEN_0, TOKEN_1), 1);
		assertNotNull(match1);
		assertEquals(1, match1.startIndex());
		assertEquals(2, match1.endIndex());
		assertEquals(TOKEN_1, match1.matchedTokens().getFirst());
		
		assertNull(testRule.match(List.of(TOKEN_0, TOKEN_0), 0));
		assertNull(testRule.match(List.of(TOKEN_0, TOKEN_1), 2));
	}
}
