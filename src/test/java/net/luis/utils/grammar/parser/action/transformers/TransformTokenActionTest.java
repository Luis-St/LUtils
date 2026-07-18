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

package net.luis.utils.grammar.parser.action.transformers;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.action.core.TokenTransformer;
import net.luis.utils.grammar.parser.context.TokenActionContext;
import net.luis.utils.grammar.parser.rule.TokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TransformTokenAction}.<br>
 *
 * @author Luis-St
 */
class TransformTokenActionTest {
	
	private static final TokenRule RULE = (_, _) -> null;
	private static final TokenActionContext CTX = new TokenActionContext(TokenStream.EMPTY);
	
	@Test
	void constructWithTransformer() {
		TokenTransformer transformer = tokens -> tokens;
		TransformTokenAction action = new TransformTokenAction(transformer);
		assertSame(transformer, action.transformer());
	}
	
	@Test
	void constructWithNullTransformer() {
		assertThrows(NullPointerException.class, () -> new TransformTokenAction(null));
	}
	
	@Test
	void applyWithNullMatch() {
		TransformTokenAction action = new TransformTokenAction(tokens -> tokens);
		assertThrows(NullPointerException.class, () -> action.apply(null, CTX));
	}
	
	@Test
	void applyWithNullContext() {
		TransformTokenAction action = new TransformTokenAction(tokens -> tokens);
		TokenRuleMatch match = TokenRuleMatch.empty(0, RULE);
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithEmptyMatchedTokensList() {
		TransformTokenAction action = new TransformTokenAction(tokens -> tokens);
		TokenRuleMatch match = TokenRuleMatch.empty(0, RULE);
		
		assertTrue(action.apply(match, CTX).isEmpty());
	}
	
	@Test
	void applyWithIdentityTransformer() {
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("a"), SimpleToken.createUnpositioned("b"));
		TokenRuleMatch match = new TokenRuleMatch(0, tokens.size(), tokens, RULE);
		TransformTokenAction action = new TransformTokenAction(t -> t);
		
		assertEquals(match.matchedTokens(), action.apply(match, CTX));
	}
	
	@Test
	void applyWithTransformerReducingTokenCount() {
		List<Token> tokens = List.of(
			SimpleToken.createUnpositioned("a"), SimpleToken.createUnpositioned("b"), SimpleToken.createUnpositioned("c")
		);
		TokenRuleMatch match = new TokenRuleMatch(0, tokens.size(), tokens, RULE);
		TransformTokenAction action = new TransformTokenAction(t -> t.subList(0, 1));
		
		assertEquals(1, action.apply(match, CTX).size());
	}
	
	@Test
	void applyWithTransformerExpandingTokenCount() {
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("a"), SimpleToken.createUnpositioned("b"));
		TokenRuleMatch match = new TokenRuleMatch(0, tokens.size(), tokens, RULE);
		TransformTokenAction action = new TransformTokenAction(t -> {
			List<Token> doubled = new ArrayList<>();
			for (Token token : t) {
				doubled.add(token);
				doubled.add(token);
			}
			return doubled;
		});
		
		assertEquals(4, action.apply(match, CTX).size());
	}
	
	@Test
	void applyResultListIsUnmodifiable() {
		Token a = SimpleToken.createUnpositioned("a");
		TokenRuleMatch match = new TokenRuleMatch(0, 1, List.of(a), RULE);
		TransformTokenAction action = new TransformTokenAction(ArrayList::new);
		
		List<Token> result = action.apply(match, CTX);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(SimpleToken.createUnpositioned("x")));
	}
	
	@Test
	void applyWithTransformerReorderingAndRelabelingTokens() {
		Token a = SimpleToken.createUnpositioned("a");
		Token b = SimpleToken.createUnpositioned("b");
		Token c = SimpleToken.createUnpositioned("c");
		List<Token> tokens = List.of(a, b, c);
		TokenRuleMatch match = new TokenRuleMatch(0, tokens.size(), tokens, RULE);
		TransformTokenAction action = new TransformTokenAction(t -> {
			List<Token> reversed = new ArrayList<>(t);
			Collections.reverse(reversed);
			List<Token> reindexed = new ArrayList<>();
			for (int i = 0; i < reversed.size(); i++) {
				reindexed.add(new IndexedToken(reversed.get(i), i));
			}
			return reindexed;
		});
		
		List<Token> result = action.apply(match, CTX);
		
		assertEquals(3, result.size());
		assertEquals(c, ((IndexedToken) result.get(0)).token());
		assertEquals(0, ((IndexedToken) result.get(0)).index());
		assertEquals(b, ((IndexedToken) result.get(1)).token());
		assertEquals(1, ((IndexedToken) result.get(1)).index());
		assertEquals(a, ((IndexedToken) result.get(2)).token());
		assertEquals(2, ((IndexedToken) result.get(2)).index());
		assertEquals(List.of(a, b, c), match.matchedTokens());
	}
}
