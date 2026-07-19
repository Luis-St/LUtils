/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.grammar.parser.action;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenActionContext;
import net.luis.utils.grammar.parser.rule.TokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenAction}.<br>
 *
 * @author Luis-St
 */
class TokenActionTest {
	
	private static final TokenRule RULE = (_, _) -> null;
	private static final TokenActionContext CTX = new TokenActionContext(TokenStream.EMPTY);
	
	@Test
	void identityActionAppliedToNullMatchThrows() {
		TokenAction action = TokenAction.identity();
		assertThrows(NullPointerException.class, () -> action.apply(null, CTX));
	}
	
	@Test
	void identityReturnsNonNullAction() {
		assertNotNull(TokenAction.identity());
	}
	
	@Test
	void identityActionReturnsMatchedTokensUnchanged() {
		Token a = SimpleToken.createUnpositioned("a");
		Token b = SimpleToken.createUnpositioned("b");
		List<Token> matchedTokens = List.of(a, b);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, matchedTokens, RULE);
		
		TokenAction action = TokenAction.identity();
		List<Token> result = action.apply(match, CTX);
		
		assertEquals(matchedTokens, result);
		assertEquals(2, result.size());
	}
	
	@Test
	void identityActionWithEmptyMatchedTokens() {
		TokenRuleMatch match = TokenRuleMatch.empty(0, RULE);
		TokenAction action = TokenAction.identity();
		
		assertTrue(action.apply(match, CTX).isEmpty());
	}
	
	@Test
	void identityActionResultIsImmutableCopyIndependentOfSource() {
		List<Token> source = new ArrayList<>();
		source.add(SimpleToken.createUnpositioned("a"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, source, RULE);
		
		TokenAction action = TokenAction.identity();
		List<Token> result = action.apply(match, CTX);
		
		source.add(SimpleToken.createUnpositioned("b"));
		
		assertEquals(1, result.size());
		assertThrows(UnsupportedOperationException.class, () -> result.add(SimpleToken.createUnpositioned("c")));
	}
}
