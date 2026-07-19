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

package net.luis.utils.grammar.parser;

import net.luis.utils.grammar.parser.action.TokenAction;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.rule.AlwaysMatchTokenRule;
import net.luis.utils.grammar.parser.rule.TokenRule;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRuleEngine}.<br>
 *
 * @author Luis-St
 */
class TokenRuleEngineTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static TokenRule matchRuleForValue(String value) {
		return (stream, _) -> {
			if (!stream.hasMoreTokens()) {
				return null;
			}
			Token current = stream.getCurrentToken();
			if (!current.value().equals(value)) {
				return null;
			}
			int index = stream.getCurrentIndex();
			return new TokenRuleMatch(index, index + 1, List.of(current), AlwaysMatchTokenRule.INSTANCE);
		};
	}
	
	@Test
	void constructWithDefaultContext() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		List<Token> result = assertDoesNotThrow(() -> engine.process(List.of()));
		assertTrue(result.isEmpty());
	}
	
	@Test
	void constructWithProvidedContext() {
		TokenRuleEngine engine = new TokenRuleEngine(TokenRuleContext.empty());
		Token tokenA = token("a");
		
		List<Token> result = assertDoesNotThrow(() -> engine.process(List.of(tokenA)));
		assertEquals(List.of(tokenA), result);
	}
	
	@Test
	void constructWithNullContextThrowsException() {
		assertThrows(NullPointerException.class, () -> new TokenRuleEngine(null));
	}
	
	@Test
	void addRuleWithNullTokenRuleThrowsException() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		assertThrows(NullPointerException.class, () -> engine.addRule(null));
	}
	
	@Test
	void addRuleWithActionAndNullTokenRuleThrowsException() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		assertThrows(NullPointerException.class, () -> engine.addRule(null, TokenAction.identity()));
	}
	
	@Test
	void addRuleWithActionAndNullActionThrowsException() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		assertThrows(NullPointerException.class, () -> engine.addRule(AlwaysMatchTokenRule.INSTANCE, null));
	}
	
	@Test
	void processWithNullTokensThrowsException() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		assertThrows(NullPointerException.class, () -> engine.process(null));
	}
	
	@Test
	void processWithNoRulesReturnsTokensUnchanged() {
		TokenRuleEngine engine = new TokenRuleEngine();
		Token tokenA = token("a");
		Token tokenB = token("b");
		
		List<Token> result = engine.process(List.of(tokenA, tokenB));
		assertEquals(List.of(tokenA, tokenB), result);
	}
	
	@Test
	void processWithRegisteredRuleEntersRuleLoop() {
		TokenRuleEngine engine = new TokenRuleEngine();
		Token tokenA = token("a");
		engine.addRule((_, _) -> null);
		
		List<Token> result = assertDoesNotThrow(() -> engine.process(List.of(tokenA)));
		assertEquals(List.of(tokenA), result);
	}
	
	@Test
	void processOnEmptyTokenListReturnsEmptyList() {
		TokenRuleEngine engine = new TokenRuleEngine();
		AtomicInteger matchCount = new AtomicInteger();
		engine.addRule((_, _) -> {
			matchCount.incrementAndGet();
			return null;
		});
		
		List<Token> result = engine.process(List.of());
		assertTrue(result.isEmpty());
		assertEquals(0, matchCount.get());
	}
	
	@Test
	void processWhenRuleNeverMatchesLeavesTokensUnchangedAndAdvancesThroughAll() {
		TokenRuleEngine engine = new TokenRuleEngine();
		AtomicInteger matchCount = new AtomicInteger();
		Token tokenA = token("a");
		Token tokenB = token("b");
		Token tokenC = token("c");
		engine.addRule((_, _) -> {
			matchCount.incrementAndGet();
			return null;
		});
		
		List<Token> result = engine.process(List.of(tokenA, tokenB, tokenC));
		assertEquals(List.of(tokenA, tokenB, tokenC), result);
		assertEquals(3, matchCount.get());
	}
	
	@Test
	void processWhenMatchHasEmptyMatchedTokensSkipsReplacement() {
		TokenRuleEngine engine = new TokenRuleEngine();
		AtomicInteger actionCount = new AtomicInteger();
		Token tokenA = token("a");
		TokenRule rule = (stream, _) -> TokenRuleMatch.empty(stream.getCurrentIndex(), AlwaysMatchTokenRule.INSTANCE);
		TokenAction action = (_, _) -> {
			actionCount.incrementAndGet();
			return List.of();
		};
		engine.addRule(rule, action);
		
		List<Token> result = engine.process(List.of(tokenA));
		assertEquals(List.of(tokenA), result);
		assertEquals(0, actionCount.get());
	}
	
	@Test
	void processWhenRuleMatchesReplacesMatchedTokens() {
		TokenRuleEngine engine = new TokenRuleEngine();
		Token tokenA = token("a");
		Token tokenB = token("b");
		Token replacement = token("x");
		TokenRule rule = (stream, _) -> {
			if (stream.getCurrentIndex() != 0) {
				return null;
			}
			return new TokenRuleMatch(0, 1, List.of(stream.getCurrentToken()), AlwaysMatchTokenRule.INSTANCE);
		};
		engine.addRule(rule, (_, _) -> List.of(replacement));
		
		List<Token> result = engine.process(List.of(tokenA, tokenB));
		assertEquals(List.of(replacement, tokenB), result);
	}
	
	@Test
	void processAppliesTransformationActionToMatchedTokens() {
		TokenRuleEngine engine = new TokenRuleEngine();
		TokenRule rule = matchRuleForValue("hello");
		TokenAction action = (match, _) -> List.of(token(match.matchedTokens().getFirst().value().toUpperCase()));
		engine.addRule(rule, action);
		
		List<Token> result = engine.process(List.of(token("hello"), token("world")));
		assertEquals("HELLO", result.get(0).value());
		assertEquals("world", result.get(1).value());
	}
	
	@Test
	void processWithSingleArgAddRuleUsesIdentityActionByDefault() {
		TokenRuleEngine engine = new TokenRuleEngine();
		Token tokenA = token("a");
		Token tokenB = token("b");
		engine.addRule(AlwaysMatchTokenRule.INSTANCE);
		
		List<Token> result = engine.process(List.of(tokenA, tokenB));
		assertEquals(List.of(tokenA, tokenB), result);
	}
	
	@Test
	void processReturnsUnmodifiableList() {
		TokenRuleEngine engine = new TokenRuleEngine();
		Token tokenA = token("a");
		Token tokenB = token("b");
		
		List<Token> result = engine.process(List.of(tokenA));
		assertThrows(UnsupportedOperationException.class, () -> result.add(tokenB));
	}
	
	@Test
	void processWithRuleMatchingAtEndOfTokenListReplacesLastTokens() {
		TokenRuleEngine engine = new TokenRuleEngine();
		Token tokenA = token("a");
		Token tokenB = token("b");
		Token tokenC = token("c");
		engine.addRule(matchRuleForValue("c"), (_, _) -> List.of(token("x")));
		
		List<Token> result = engine.process(List.of(tokenA, tokenB, tokenC));
		assertEquals(3, result.size());
		assertEquals(tokenA, result.get(0));
		assertEquals(tokenB, result.get(1));
		assertEquals("x", result.get(2).value());
	}
	
	@Test
	void processDoesNotMutateOriginalInputList() {
		TokenRuleEngine engine = new TokenRuleEngine();
		Token tokenA = token("a");
		Token tokenB = token("b");
		ArrayList<Token> input = new ArrayList<>(List.of(tokenA, tokenB));
		engine.addRule(matchRuleForValue("a"), (_, _) -> List.of(token("x")));
		
		engine.process(input);
		assertEquals(2, input.size());
		assertEquals(tokenA, input.get(0));
		assertEquals(tokenB, input.get(1));
	}
	
	@Test
	void processAppliesMultipleRulesInOrderAdded() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(matchRuleForValue("a"), (_, _) -> List.of(token("b")));
		engine.addRule(matchRuleForValue("b"), (_, _) -> List.of(token("c")));
		
		List<Token> result = engine.process(List.of(token("a")));
		assertEquals(1, result.size());
		assertEquals("c", result.getFirst().value());
	}
	
	@Test
	void processHandlesRuleThatShrinksTokenListDuringReplacement() {
		TokenRuleEngine engine = new TokenRuleEngine();
		TokenRule rule = (stream, _) -> {
			int index = stream.getCurrentIndex();
			List<Token> all = stream.getAllTokens();
			if (index + 3 > all.size() || !"a".equals(all.get(index).value()) || !"b".equals(all.get(index + 1).value()) || !"c".equals(all.get(index + 2).value())) {
				return null;
			}
			return new TokenRuleMatch(index, index + 3, all.subList(index, index + 3), AlwaysMatchTokenRule.INSTANCE);
		};
		engine.addRule(rule, (_, _) -> List.of(token("abc")));
		
		List<Token> result = engine.process(List.of(token("a"), token("b"), token("c"), token("d")));
		assertEquals(2, result.size());
		assertEquals("abc", result.get(0).value());
		assertEquals("d", result.get(1).value());
	}
	
	@Test
	void processHandlesRuleThatGrowsTokenListDuringReplacement() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(matchRuleForValue("a"), (_, _) -> List.of(token("x1"), token("x2"), token("x3")));
		
		List<Token> result = engine.process(List.of(token("a"), token("trailing")));
		assertEquals(4, result.size());
		assertEquals("x1", result.get(0).value());
		assertEquals("x2", result.get(1).value());
		assertEquals("x3", result.get(2).value());
		assertEquals("trailing", result.get(3).value());
	}
	
	@Test
	void processHandlesRuleThatDeletesMatchedTokensViaEmptyActionOutput() {
		TokenRuleEngine singleTokenEngine = new TokenRuleEngine();
		singleTokenEngine.addRule(matchRuleForValue("a"), (_, _) -> List.of());
		
		List<Token> singleResult = singleTokenEngine.process(List.of(token("a")));
		assertTrue(singleResult.isEmpty());
		
		TokenRuleEngine trailingEngine = new TokenRuleEngine();
		trailingEngine.addRule(matchRuleForValue("a"), (_, _) -> List.of());
		
		List<Token> trailingResult = trailingEngine.process(List.of(token("a"), token("b"), token("c")));
		assertEquals(2, trailingResult.size());
		assertEquals("b", trailingResult.get(0).value());
		assertEquals("c", trailingResult.get(1).value());
	}
	
	@Test
	void processWithMultipleNonOverlappingMatchesReplacesEachOccurrence() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(matchRuleForValue("x"), (_, _) -> List.of(token("replaced")));
		
		List<Token> result = engine.process(List.of(token("x"), token("other"), token("x"), token("other"), token("x")));
		assertEquals(5, result.size());
		assertEquals("replaced", result.get(0).value());
		assertEquals("other", result.get(1).value());
		assertEquals("replaced", result.get(2).value());
		assertEquals("other", result.get(3).value());
		assertEquals("replaced", result.get(4).value());
	}
	
	@Test
	void processSharesRuleContextAcrossAllRuleMatchesWithinSameCall() {
		TokenRuleContext customContext = TokenRuleContext.empty();
		TokenRuleEngine engine = new TokenRuleEngine(customContext);
		List<TokenRuleContext> captured = new ArrayList<>();
		engine.addRule((_, ctx) -> {
			captured.add(ctx);
			return null;
		});
		engine.addRule((_, ctx) -> {
			captured.add(ctx);
			return null;
		});
		
		engine.process(List.of(token("a"), token("b")));
		assertFalse(captured.isEmpty());
		for (TokenRuleContext ctx : captured) {
			assertSame(customContext, ctx);
		}
	}
}
