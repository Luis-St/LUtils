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

package net.luis.utils.io.token.rules.combinators;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.rules.TokenRules;
import net.luis.utils.io.token.rules.assertions.anchors.StartTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SequenceTokenRule}.<br>
 *
 * @author Luis-St
 */
class SequenceTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static @NotNull TokenRule createRule(@NotNull String value) {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
				Objects.requireNonNull(stream, "Token stream must not be null");
				Objects.requireNonNull(ctx, "Token rule context must not be null");
				if (!stream.hasMoreTokens()) {
					return null;
				}
				
				int startIndex = stream.getCurrentIndex();
				Token token = stream.getCurrentToken();
				if (token.value().equals(value)) {
					return new TokenRuleMatch(startIndex, stream.advance(), List.of(token), this);
				}
				return null;
			}
		};
	}
	
	@Test
	void constructorWithNullRules() {
		assertThrows(NullPointerException.class, () -> new SequenceTokenRule(null));
	}
	
	@Test
	void constructorWithEmptyRules() {
		assertThrows(IllegalArgumentException.class, () -> new SequenceTokenRule(List.of()));
	}
	
	@Test
	void constructorWithNullRuleInList() {
		List<TokenRule> rules = new ArrayList<>();
		rules.add(createRule("first"));
		rules.add(null);
		
		assertThrows(NullPointerException.class, () -> new SequenceTokenRule(rules));
	}
	
	@Test
	void constructorWithValidRules() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		List<TokenRule> rules = List.of(rule1, rule2);
		
		SequenceTokenRule sequenceRule = new SequenceTokenRule(rules);
		
		assertEquals(rules, sequenceRule.tokenRules());
	}
	
	@Test
	void matchWithNullStream() {
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(createRule("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> sequenceRule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(createRule("test")));
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> sequenceRule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyStream() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithSingleRule() {
		TokenRule rule = createRule("test");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.matchedTokens().size());
		assertEquals("test", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithMultipleRules() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		TokenRule rule3 = createRule("third");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2, rule3));
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("first"), createToken("second"), createToken("third")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(3, result.matchedTokens().size());
		assertEquals("first", result.matchedTokens().get(0).value());
		assertEquals("second", result.matchedTokens().get(1).value());
		assertEquals("third", result.matchedTokens().get(2).value());
	}
	
	@Test
	void matchWithAllRulesMatching() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals("first", result.matchedTokens().get(0).value());
		assertEquals("second", result.matchedTokens().get(1).value());
		assertEquals(sequenceRule, result.matchingTokenRule());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchAtDifferentStreamPosition() {
		TokenRule rule1 = createRule("target");
		TokenRule rule2 = createRule("sequence");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("prefix"), createToken("target"), createToken("sequence"), createToken("suffix")
		));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(3, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals("target", result.matchedTokens().get(0).value());
		assertEquals("sequence", result.matchedTokens().get(1).value());
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithFirstRuleNotMatching() {
		TokenRule rule1 = createRule("expected");
		TokenRule rule2 = createRule("second");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSecondRuleNotMatching() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("expected");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithThirdRuleNotMatching() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		TokenRule rule3 = createRule("expected");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2, rule3));
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("first"), createToken("second"), createToken("actual")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithMiddleRuleNotMatching() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("expected");
		TokenRule rule3 = createRule("third");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2, rule3));
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("first"), createToken("actual"), createToken("third")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithInsufficientTokens() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithInsufficientTokensAfterPartialMatch() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		TokenRule rule3 = createRule("third");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2, rule3));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithComplexNestedRules() {
		TokenRule rule1 = TokenRules.sequence(createRule("start"), createRule("prefix"));
		TokenRule rule2 = TokenRules.any(createRule("option1"), createRule("option2"));
		TokenRule rule3 = createRule("end");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2, rule3));
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("start"), createToken("prefix"), createToken("option2"), createToken("end")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(4, result.endIndex());
		assertEquals(4, result.matchedTokens().size());
		assertEquals("start", result.matchedTokens().get(0).value());
		assertEquals("prefix", result.matchedTokens().get(1).value());
		assertEquals("option2", result.matchedTokens().get(2).value());
		assertEquals("end", result.matchedTokens().get(3).value());
	}
	
	@Test
	void matchWithDifferentTokenConsumption() {
		TokenRule rule1 = createRule("single");
		TokenRule rule2 = TokenRules.sequence(createRule("multi"), createRule("token"));
		TokenRule rule3 = createRule("final");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2, rule3));
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("single"), createToken("multi"), createToken("token"), createToken("final")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(4, result.endIndex());
		assertEquals(4, result.matchedTokens().size());
		assertEquals("single", result.matchedTokens().get(0).value());
		assertEquals("multi", result.matchedTokens().get(1).value());
		assertEquals("token", result.matchedTokens().get(2).value());
		assertEquals("final", result.matchedTokens().get(3).value());
	}
	
	@Test
	void matchWithMixedZeroWidthAndConsumingRules() {
		TokenRule rule1 = StartTokenRule.DOCUMENT;
		TokenRule rule2 = createRule("content");
		TokenRule rule3 = TokenRules.endDocument();
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2, rule3));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("content")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals("content", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithAlwaysMatchRules() {
		TokenRule rule1 = TokenRules.alwaysMatch();
		TokenRule rule2 = TokenRules.alwaysMatch();
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any1"), createToken("any2")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(2, result.matchedTokens().size());
		assertEquals("any1", result.matchedTokens().get(0).value());
		assertEquals("any2", result.matchedTokens().get(1).value());
	}
	
	@Test
	void matchWithNeverMatchRule() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = TokenRules.neverMatch();
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithNeverMatchFirstRule() {
		TokenRule rule1 = TokenRules.neverMatch();
		TokenRule rule2 = createRule("second");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithOptionalRules() {
		TokenRule rule1 = createRule("required");
		TokenRule rule2 = TokenRules.optional(createRule("optional"));
		TokenRule rule3 = createRule("end");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2, rule3));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("required"), createToken("end")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals("required", result.matchedTokens().get(0).value());
		assertEquals("end", result.matchedTokens().get(1).value());
	}
	
	@Test
	void matchWithStreamPositionRollback() {
		TokenRule rule1 = createRule("start");
		TokenRule rule2 = createRule("expected");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("start"), createToken("wrong"), createToken("tokens"), createToken("here")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithZeroWidthRules() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(
			StartTokenRule.DOCUMENT,
			TokenRules.value("test", false)
		));
		
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("test"));
		TokenStream stream = TokenStream.createMutable(tokens);
		
		TokenRuleContext context = TokenRuleContext.empty();
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.matchedTokens().size());
	}
	
	@Test
	void matchWithManyRules() {
		List<TokenRule> rules = new ArrayList<>();
		List<Token> tokens = new ArrayList<>();
		
		for (int i = 0; i < 100; i++) {
			rules.add(TokenRules.value("token" + i, false));
			tokens.add(SimpleToken.createUnpositioned("token" + i));
		}
		
		TokenRuleContext context = TokenRuleContext.empty();
		SequenceTokenRule rule = new SequenceTokenRule(rules);
		TokenStream stream = TokenStream.createMutable(tokens);
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(100, result.matchedTokens().size());
	}
	
	@Test
	void matchWithNestedSequences() {
		TokenRule inner1 = TokenRules.sequence(createRule("a"), createRule("b"));
		TokenRule inner2 = TokenRules.sequence(createRule("c"), createRule("d"));
		SequenceTokenRule outer = new SequenceTokenRule(List.of(inner1, inner2));
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("a"), createToken("b"), createToken("c"), createToken("d")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = outer.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(4, result.endIndex());
		assertEquals(4, result.matchedTokens().size());
		assertEquals("a", result.matchedTokens().get(0).value());
		assertEquals("b", result.matchedTokens().get(1).value());
		assertEquals("c", result.matchedTokens().get(2).value());
		assertEquals("d", result.matchedTokens().get(3).value());
	}
	
	@Test
	void not() {
		TokenRule rule1 = TokenRules.pattern("first");
		TokenRule rule2 = TokenRules.pattern("second");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenRule negated = sequenceRule.not();
		
		AnyOfTokenRule anyRule = assertInstanceOf(AnyOfTokenRule.class, negated);
		assertEquals(2, anyRule.tokenRules().size());
	}
	
	@Test
	void notWithSingleRule() {
		TokenRule rule = TokenRules.pattern("test");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule));
		
		TokenRule negated = sequenceRule.not();
		
		AnyOfTokenRule anyRule = assertInstanceOf(AnyOfTokenRule.class, negated);
		assertEquals(1, anyRule.tokenRules().size());
	}
	
	@Test
	void toStringTest() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		String result = sequenceRule.toString();
		
		assertTrue(result.contains("SequenceTokenRule"));
		assertTrue(result.contains("tokenRules"));
	}
}
