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
import net.luis.utils.grammar.parser.action.core.TokenConverter;
import net.luis.utils.grammar.parser.context.TokenActionContext;
import net.luis.utils.grammar.parser.rule.TokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ConvertTokenAction}.<br>
 *
 * @author Luis-St
 */
class ConvertTokenActionTest {
	
	private static final TokenRule RULE = (stream, ctx) -> null;
	private static final TokenActionContext CTX = new TokenActionContext(TokenStream.EMPTY);
	
	@Test
	void constructWithConverter() {
		TokenConverter converter = token -> token;
		ConvertTokenAction action = new ConvertTokenAction(converter);
		assertSame(converter, action.converter());
	}
	
	@Test
	void constructWithNullConverter() {
		assertThrows(NullPointerException.class, () -> new ConvertTokenAction(null));
	}
	
	@Test
	void applyWithNullMatch() {
		ConvertTokenAction action = new ConvertTokenAction(token -> token);
		assertThrows(NullPointerException.class, () -> action.apply(null, CTX));
	}
	
	@Test
	void applyWithNullContext() {
		ConvertTokenAction action = new ConvertTokenAction(token -> token);
		TokenRuleMatch match = TokenRuleMatch.empty(0, RULE);
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithEmptyMatchedTokens() {
		AtomicInteger calls = new AtomicInteger(0);
		ConvertTokenAction action = new ConvertTokenAction(token -> {
			calls.incrementAndGet();
			return token;
		});
		TokenRuleMatch match = TokenRuleMatch.empty(0, RULE);
		
		List<Token> result = action.apply(match, CTX);
		
		assertTrue(result.isEmpty());
		assertEquals(0, calls.get());
	}
	
	@Test
	void applyWithPopulatedMatchedTokens() {
		List<Token> tokens = List.of(
			SimpleToken.createUnpositioned("a"), SimpleToken.createUnpositioned("b"), SimpleToken.createUnpositioned("c")
		);
		TokenRuleMatch match = new TokenRuleMatch(0, tokens.size(), tokens, RULE);
		ConvertTokenAction action = new ConvertTokenAction(token -> new SimpleToken(token.value().toUpperCase(), TokenPosition.UNPOSITIONED));
		
		List<Token> result = action.apply(match, CTX);
		
		assertEquals(3, result.size());
		assertEquals(List.of("A", "B", "C"), result.stream().map(Token::value).toList());
	}
	
	@Test
	void applyWithIdentityConverter() {
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("a"), SimpleToken.createUnpositioned("b"));
		TokenRuleMatch match = new TokenRuleMatch(0, tokens.size(), tokens, RULE);
		ConvertTokenAction action = new ConvertTokenAction(token -> token);
		
		assertEquals(tokens, action.apply(match, CTX));
	}
	
	@Test
	void applyWithSingleToken() {
		Token a = SimpleToken.createUnpositioned("a");
		TokenRuleMatch match = new TokenRuleMatch(0, 1, List.of(a), RULE);
		ConvertTokenAction action = new ConvertTokenAction(token -> token);
		
		List<Token> result = action.apply(match, CTX);
		
		assertEquals(1, result.size());
	}
	
	@Test
	void applyResultListIsUnmodifiable() {
		TokenRuleMatch match = new TokenRuleMatch(0, 1, List.of(SimpleToken.createUnpositioned("a")), RULE);
		ConvertTokenAction action = new ConvertTokenAction(token -> token);
		
		List<Token> result = action.apply(match, CTX);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(SimpleToken.createUnpositioned("x")));
	}
	
	@Test
	void applyWithConverterProducingDifferentTokenTypesPerCall() {
		List<Token> tokens = List.of(
			SimpleToken.createUnpositioned("a"), SimpleToken.createUnpositioned("b"),
			SimpleToken.createUnpositioned("c"), SimpleToken.createUnpositioned("d")
		);
		TokenRuleMatch match = new TokenRuleMatch(0, tokens.size(), tokens, RULE);
		AtomicInteger counter = new AtomicInteger(0);
		ConvertTokenAction action = new ConvertTokenAction(token -> {
			int index = counter.getAndIncrement();
			return index % 2 == 0 ? token : new IndexedToken(token, index);
		});
		
		List<Token> result = action.apply(match, CTX);
		
		assertEquals(4, result.size());
		assertFalse(result.get(0) instanceof IndexedToken);
		assertInstanceOf(IndexedToken.class, result.get(1));
		assertFalse(result.get(2) instanceof IndexedToken);
		assertInstanceOf(IndexedToken.class, result.get(3));
		assertEquals(tokens, match.matchedTokens());
	}
}
