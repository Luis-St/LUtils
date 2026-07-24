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
import net.luis.utils.grammar.parser.context.TokenActionContext;
import net.luis.utils.grammar.parser.rule.AlwaysMatchTokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link WrapTokenAction}.<br>
 *
 * @author Luis-St
 */
class WrapTokenActionTest {
	
	private static TokenActionContext contextFor(List<Token> tokens) {
		return new TokenActionContext(TokenStream.createImmutable(tokens));
	}
	
	private static TokenRuleMatch matchFor(List<Token> tokens) {
		return new TokenRuleMatch(0, tokens.size(), tokens, AlwaysMatchTokenRule.INSTANCE);
	}
	
	@Test
	void constructWithPrefixAndSuffix() {
		Token prefix = SimpleToken.createUnpositioned("prefix");
		Token suffix = SimpleToken.createUnpositioned("suffix");
		
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		assertEquals(prefix, action.prefixToken());
		assertEquals(suffix, action.suffixToken());
	}
	
	@Test
	void constructWithNullPrefix() {
		Token suffix = SimpleToken.createUnpositioned("suffix");
		assertThrows(NullPointerException.class, () -> new WrapTokenAction(null, suffix));
	}
	
	@Test
	void constructWithNullSuffix() {
		Token prefix = SimpleToken.createUnpositioned("prefix");
		assertThrows(NullPointerException.class, () -> new WrapTokenAction(prefix, null));
	}
	
	@Test
	void applyWithNullMatch() {
		WrapTokenAction action = new WrapTokenAction(SimpleToken.createUnpositioned("p"), SimpleToken.createUnpositioned("s"));
		TokenActionContext ctx = contextFor(List.of());
		assertThrows(NullPointerException.class, () -> action.apply(null, ctx));
	}
	
	@Test
	void applyWithNullContext() {
		WrapTokenAction action = new WrapTokenAction(SimpleToken.createUnpositioned("p"), SimpleToken.createUnpositioned("s"));
		TokenRuleMatch match = TokenRuleMatch.empty(0, AlwaysMatchTokenRule.INSTANCE);
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithEmptyMatchedTokensReturnsEmptyList() {
		WrapTokenAction action = new WrapTokenAction(SimpleToken.createUnpositioned("p"), SimpleToken.createUnpositioned("s"));
		TokenRuleMatch match = TokenRuleMatch.empty(0, AlwaysMatchTokenRule.INSTANCE);
		
		List<Token> result = action.apply(match, contextFor(List.of()));
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithNonEmptyMatchedTokensWrapsThem() {
		Token prefix = SimpleToken.createUnpositioned("prefix");
		Token suffix = SimpleToken.createUnpositioned("suffix");
		Token token1 = SimpleToken.createUnpositioned("a");
		Token token2 = SimpleToken.createUnpositioned("b");
		List<Token> tokens = List.of(token1, token2);
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(4, result.size());
		assertEquals(List.of(prefix, token1, token2, suffix), result);
	}
	
	@Test
	void applyWithSingleMatchedToken() {
		Token prefix = SimpleToken.createUnpositioned("prefix");
		Token suffix = SimpleToken.createUnpositioned("suffix");
		Token token = SimpleToken.createUnpositioned("a");
		List<Token> tokens = List.of(token);
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(List.of(prefix, token, suffix), result);
	}
	
	@Test
	void applyResultListIsUnmodifiable() {
		Token prefix = SimpleToken.createUnpositioned("prefix");
		Token suffix = SimpleToken.createUnpositioned("suffix");
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("a"));
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(SimpleToken.createUnpositioned("x")));
	}
	
	@Test
	void applyWithManyMatchedTokensPreservesOrderAndWraps() {
		Token prefix = new AnnotatedToken(SimpleToken.createUnpositioned("prefix"), Map.of("pos", "start"));
		Token suffix = new AnnotatedToken(SimpleToken.createUnpositioned("suffix"), Map.of("pos", "end"));
		Token t1 = SimpleToken.createUnpositioned("a");
		Token t2 = SimpleToken.createUnpositioned("b");
		Token t3 = new AnnotatedToken(SimpleToken.createUnpositioned("c"), Map.of());
		Token t4 = SimpleToken.createUnpositioned("d");
		Token t5 = SimpleToken.createUnpositioned("e");
		List<Token> tokens = List.of(t1, t2, t3, t4, t5);
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(7, result.size());
		assertSame(prefix, result.get(0));
		assertSame(t1, result.get(1));
		assertSame(t2, result.get(2));
		assertSame(t3, result.get(3));
		assertSame(t4, result.get(4));
		assertSame(t5, result.get(5));
		assertSame(suffix, result.get(6));
	}
}
