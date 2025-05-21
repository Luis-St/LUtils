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

package net.luis.utils.io.token.rule.actions;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link WrapTokenAction}.<br>
 *
 * @author Luis-St
 */
class WrapTokenActionTest {
	
	private static final Token PREFIX_TOKEN = SimpleToken.createUnpositioned("("::equals, "(");
	private static final Token SUFFIX_TOKEN = SimpleToken.createUnpositioned(")"::equals, ")");
	private static final Token CONTENT_TOKEN = SimpleToken.createUnpositioned("abc"::equals, "abc");
	private static final List<Token> TEST_TOKENS = List.of(CONTENT_TOKEN);
	
	private static final TokenRuleMatch TEST_MATCH = new TokenRuleMatch(0, 1, TEST_TOKENS, TokenRules.alwaysMatch());
	private static final TokenRuleMatch EMPTY_MATCH = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new WrapTokenAction(null, SUFFIX_TOKEN));
		assertThrows(NullPointerException.class, () -> new WrapTokenAction(PREFIX_TOKEN, null));
	}
	
	@Test
	void prefixToken() {
		assertEquals(PREFIX_TOKEN, new WrapTokenAction(PREFIX_TOKEN, SUFFIX_TOKEN).prefixToken());
	}
	
	@Test
	void suffixToken() {
		assertEquals(SUFFIX_TOKEN, new WrapTokenAction(PREFIX_TOKEN, SUFFIX_TOKEN).suffixToken());
	}
	
	@Test
	void apply() {
		WrapTokenAction action = new WrapTokenAction(PREFIX_TOKEN, SUFFIX_TOKEN);
		assertThrows(NullPointerException.class, () -> action.apply(null));
		
		List<Token> result = action.apply(TEST_MATCH);
		assertEquals(3, result.size());
		assertEquals(PREFIX_TOKEN, result.getFirst());
		assertEquals(CONTENT_TOKEN, result.get(1));
		assertEquals(SUFFIX_TOKEN, result.getLast());
		
		List<Token> emptyResult = action.apply(EMPTY_MATCH);
		assertTrue(emptyResult.isEmpty());
	}
}
