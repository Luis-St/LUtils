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

import com.google.common.collect.Lists;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TransformTokenAction}.<br>
 *
 * @author Luis-St
 */
class TransformTokenActionTest {
	
	private static final TokenDefinition ANY_DEFINITION = (word) -> true;
	private static final Token TOKEN_0 = SimpleToken.createUnpositioned("test0"::equals, "test0");
	private static final Token TOKEN_1 = SimpleToken.createUnpositioned("test1"::equals, "test1");
	private static final List<Token> TEST_TOKENS = List.of(TOKEN_0, TOKEN_1);
	private static final TokenRuleMatch TEST_MATCH = new TokenRuleMatch(0, 2, TEST_TOKENS, TokenRules.alwaysMatch());
	private static final TokenRuleMatch EMPTY_MATCH = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
	private static final TokenTransformer UPPERCASE_TRANSFORMER = tokens -> {
		List<Token> result = Lists.newArrayList();
		for (Token token : tokens) {
			result.add(SimpleToken.createUnpositioned(ANY_DEFINITION, token.value().toUpperCase()));
		}
		return Collections.unmodifiableList(result);
	};
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new TransformTokenAction(null));
	}
	
	@Test
	void transformer() {
		TransformTokenAction action = new TransformTokenAction(UPPERCASE_TRANSFORMER);
		assertSame(UPPERCASE_TRANSFORMER, action.transformer());
	}
	
	@Test
	void apply() {
		TransformTokenAction action = new TransformTokenAction(UPPERCASE_TRANSFORMER);
		assertThrows(NullPointerException.class, () -> action.apply(null));
		
		List<Token> result = action.apply(TEST_MATCH);
		assertEquals(2, result.size());
		assertEquals("TEST0", result.getFirst().value());
		assertEquals("TEST1", result.getLast().value());
		
		List<Token> emptyResult = action.apply(EMPTY_MATCH);
		assertTrue(emptyResult.isEmpty());
	}
}
