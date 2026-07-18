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

package net.luis.utils.grammar.parser.context;

import net.luis.utils.grammar.parser.rule.*;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRuleContext}.<br>
 *
 * @author Luis-St
 */
class TokenRuleContextTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructEmptyContext() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		assertNotNull(ctx);
		assertNull(ctx.getRuleReference("missing"));
		assertNull(ctx.getCapturedTokens("missing"));
	}
	
	@Test
	void defineRuleWithNullKey() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		assertThrows(NullPointerException.class, () -> ctx.defineRule(null, AlwaysMatchTokenRule.INSTANCE));
	}
	
	@Test
	void defineRuleWithNullRule() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		assertThrows(NullPointerException.class, () -> ctx.defineRule("key", null));
	}
	
	@Test
	void getRuleReferenceWithNullKey() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		assertThrows(NullPointerException.class, () -> ctx.getRuleReference(null));
	}
	
	@Test
	void captureTokensWithNullKey() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		assertThrows(NullPointerException.class, () -> ctx.captureTokens(null, List.of()));
	}
	
	@Test
	void captureTokensWithNullTokens() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		assertThrows(NullPointerException.class, () -> ctx.captureTokens("key", null));
	}
	
	@Test
	void getCapturedTokensWithNullKey() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		assertThrows(NullPointerException.class, () -> ctx.getCapturedTokens(null));
	}
	
	@Test
	void captureTokensWithListContainingNullElement() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		List<Token> tokens = new ArrayList<>();
		tokens.add(null);
		
		assertThrows(NullPointerException.class, () -> ctx.captureTokens("key", tokens));
	}
	
	@Test
	void getRuleReferenceForUndefinedKeyReturnsNull() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		assertNull(ctx.getRuleReference("undefined"));
	}
	
	@Test
	void getRuleReferenceForDefinedKeyReturnsRule() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		ctx.defineRule("key", AlwaysMatchTokenRule.INSTANCE);
		
		assertSame(AlwaysMatchTokenRule.INSTANCE, ctx.getRuleReference("key"));
	}
	
	@Test
	void getCapturedTokensForUncapturedKeyReturnsNull() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		assertNull(ctx.getCapturedTokens("undefined"));
	}
	
	@Test
	void getCapturedTokensForCapturedKeyReturnsTokens() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		Token token1 = token("a");
		Token token2 = token("b");
		ctx.captureTokens("key", List.of(token1, token2));
		
		assertEquals(List.of(token1, token2), ctx.getCapturedTokens("key"));
	}
	
	@Test
	void defineRuleOverwritesExistingKey() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		TokenRule ruleA = AlwaysMatchTokenRule.INSTANCE;
		TokenRule ruleB = NeverMatchTokenRule.INSTANCE;
		ctx.defineRule("key", ruleA);
		ctx.defineRule("key", ruleB);
		
		assertSame(ruleB, ctx.getRuleReference("key"));
	}
	
	@Test
	void captureTokensOverwritesExistingKey() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		Token tokenA = token("a");
		Token tokenB = token("b");
		Token tokenC = token("c");
		ctx.captureTokens("key", List.of(tokenA));
		ctx.captureTokens("key", List.of(tokenB, tokenC));
		
		assertEquals(List.of(tokenB, tokenC), ctx.getCapturedTokens("key"));
	}
	
	@Test
	void captureTokensWithEmptyList() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		ctx.captureTokens("key", List.of());
		
		List<Token> result = ctx.getCapturedTokens("key");
		
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void captureTokensStoresImmutableCopy() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		Token originalToken = token("a");
		List<Token> mutable = new ArrayList<>(List.of(originalToken));
		ctx.captureTokens("key", mutable);
		mutable.add(token("b"));
		
		List<Token> result = ctx.getCapturedTokens("key");
		
		assertNotNull(result);
		assertEquals(1, result.size());
		assertThrows(UnsupportedOperationException.class, () -> result.add(token("c")));
	}
	
	@Test
	void multipleIndependentKeysForRulesAndTokensCoexist() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		TokenRule ruleA = AlwaysMatchTokenRule.INSTANCE;
		TokenRule ruleB = NeverMatchTokenRule.INSTANCE;
		Token tokenA = token("a");
		Token tokenC = token("c");
		
		ctx.defineRule("a", ruleA);
		ctx.defineRule("b", ruleB);
		ctx.captureTokens("a", List.of(tokenA));
		ctx.captureTokens("c", List.of(tokenC));
		
		assertSame(ruleA, ctx.getRuleReference("a"));
		assertSame(ruleB, ctx.getRuleReference("b"));
		assertNull(ctx.getRuleReference("c"));
		assertEquals(List.of(tokenA), ctx.getCapturedTokens("a"));
		assertNull(ctx.getCapturedTokens("b"));
		assertEquals(List.of(tokenC), ctx.getCapturedTokens("c"));
	}
	
	@Test
	void separateContextInstancesDoNotShareState() {
		TokenRuleContext first = TokenRuleContext.empty();
		TokenRuleContext second = TokenRuleContext.empty();
		first.defineRule("key", AlwaysMatchTokenRule.INSTANCE);
		first.captureTokens("key", List.of(token("a")));
		
		assertNull(second.getRuleReference("key"));
		assertNull(second.getCapturedTokens("key"));
	}
}
