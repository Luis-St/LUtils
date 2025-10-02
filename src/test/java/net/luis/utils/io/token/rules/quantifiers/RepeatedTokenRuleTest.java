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

package net.luis.utils.io.token.rules.quantifiers;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.rules.TokenRules;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link RepeatedTokenRule}.<br>
 *
 * @author Luis-St
 */
class RepeatedTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static @NotNull TokenRule createRule() {
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
				if ("test".equals(token.value())) {
					return new TokenRuleMatch(startIndex, stream.advance(), List.of(token), this);
				}
				return null;
			}
		};
	}
	
	@Test
	void constructorWithNullRule() {
		assertThrows(NullPointerException.class, () -> new RepeatedTokenRule(null, 2));
	}
	
	@Test
	void constructorWithNegativeMin() {
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(createRule(), -1, 5));
	}
	
	@Test
	void constructorWithMaxLessThanMin() {
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(createRule(), 5, 2));
	}
	
	@Test
	void constructorWithBothZero() {
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(createRule(), 0, 0));
	}
	
	@Test
	void constructorWithNegativeOccurrences() {
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(createRule(), -1));
	}
	
	@Test
	void constructorWithSingleOccurrence() {
		TokenRule innerRule = createRule();
		
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 3);
		
		assertEquals(innerRule, rule.tokenRule());
		assertEquals(3, rule.minOccurrences());
		assertEquals(3, rule.maxOccurrences());
	}
	
	@Test
	void constructorWithMinMax() {
		TokenRule innerRule = createRule();
		
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 5);
		
		assertEquals(innerRule, rule.tokenRule());
		assertEquals(2, rule.minOccurrences());
		assertEquals(5, rule.maxOccurrences());
	}
	
	@Test
	void matchWithNullStream() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule(), 1, 3);
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule(), 1, 3);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyStream() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 1, 3);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchExactOccurrences() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test"), createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(2, result.matchedTokens().size());
	}
	
	@Test
	void matchExactlyMinOccurrences() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 4);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test"), createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals("test", result.matchedTokens().get(0).value());
		assertEquals("test", result.matchedTokens().get(1).value());
		assertEquals(rule, result.matchingTokenRule());
	}
	
	@Test
	void matchBetweenMinAndMax() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 5);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("test"), createToken("test"), createToken("test"), createToken("other")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(3, result.endIndex());
		assertEquals(3, result.matchedTokens().size());
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void matchExactlyMaxOccurrences() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 3);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("test"), createToken("test"), createToken("test"), createToken("test")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(3, result.endIndex());
		assertEquals(3, result.matchedTokens().size());
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void matchAtDifferentStreamPosition() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 3);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("prefix"), createToken("test"), createToken("test"), createToken("suffix")
		));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(3, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals("test", result.matchedTokens().get(0).value());
		assertEquals("test", result.matchedTokens().get(1).value());
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void matchAtDifferentStreamPositionInsufficientOccurrences() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 3, 5);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("prefix"), createToken("test"), createToken("suffix")
		));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithZeroMinOccurrences() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 0, 3);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithZeroMinOccurrencesAndMatches() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 0, 2);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test"), createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals("test", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithLargeMaxOccurrences() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 1, 1000);
		
		List<Token> tokens = List.of(createToken("test"), createToken("test"), createToken("test"), createToken("other"));
		TokenStream stream = TokenStream.createMutable(tokens);
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(3, result.endIndex());
		assertEquals(3, result.matchedTokens().size());
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSequenceInnerRule() {
		TokenRule innerRule = TokenRules.sequence(createRule(), TokenRules.value("suffix", false));
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 3);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("test"), createToken("suffix"), createToken("test"), createToken("suffix"), createToken("end")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(4, result.endIndex());
		assertEquals(4, result.matchedTokens().size());
		assertEquals("test", result.matchedTokens().get(0).value());
		assertEquals("suffix", result.matchedTokens().get(1).value());
		assertEquals("test", result.matchedTokens().get(2).value());
		assertEquals("suffix", result.matchedTokens().get(3).value());
		assertEquals(4, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSequenceInnerRulePartialMatch() {
		TokenRule innerRule = TokenRules.sequence(createRule(), TokenRules.value("suffix", false));
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 3);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("test"), createToken("suffix"), createToken("test"), createToken("wrong"), createToken("end")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithPatternInnerRule() {
		TokenRule innerRule = TokenRules.pattern("\\d+");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 4);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("123"), createToken("456"), createToken("789"), createToken("abc")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(3, result.endIndex());
		assertEquals(3, result.matchedTokens().size());
		assertEquals("123", result.matchedTokens().get(0).value());
		assertEquals("456", result.matchedTokens().get(1).value());
		assertEquals("789", result.matchedTokens().get(2).value());
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithPatternInnerRuleInsufficientMatches() {
		TokenRule innerRule = TokenRules.pattern("\\d+");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 3, 5);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("123"), createToken("abc"), createToken("456")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithAnyOfInnerRule() {
		TokenRule innerRule = TokenRules.any(TokenRules.value("a", false), TokenRules.value("b", false));
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 4);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("a"), createToken("b"), createToken("a"), createToken("c")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(3, result.endIndex());
		assertEquals(3, result.matchedTokens().size());
		assertEquals("a", result.matchedTokens().get(0).value());
		assertEquals("b", result.matchedTokens().get(1).value());
		assertEquals("a", result.matchedTokens().get(2).value());
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithNestedRepeatedRule() {
		TokenRule innerInnerRule = createRule();
		TokenRule innerRule = new RepeatedTokenRule(innerInnerRule, 1, 2);
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 3);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("test"), createToken("test"), createToken("test"), createToken("other")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(3, result.endIndex());
		assertEquals(3, result.matchedTokens().size());
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithMixedMatchingTokens() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 5);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("test"), createToken("other"), createToken("test"), createToken("test"), createToken("final")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSingleOccurrenceRequired() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 1);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test"), createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals("test", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSingleOccurrenceRequiredTooMany() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 1);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test"), createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.matchedTokens().size());
		assertEquals("test", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchFewerThanMinOccurrences() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 3, 5);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test"), createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithNoMatches() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 1, 3);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchExactOccurrencesWithMore() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("test"), createToken("test"), createToken("test")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(2, result.matchedTokens().size());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchExactOccurrencesWithFewer() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 3);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test"), createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithAlwaysMatchRule() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.alwaysMatch(), 2, 3);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any1"), createToken("any2")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(2, result.matchedTokens().size());
	}
	
	@Test
	void matchWithNeverMatchRule() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.neverMatch(), 1, 3);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchStopsAtStreamEnd() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 1, 5);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test"), createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(2, result.matchedTokens().size());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchStopsAtFirstNonMatch() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 1, 10);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("test"), createToken("test"), createToken("other"), createToken("test")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals("test", result.matchedTokens().get(0).value());
		assertEquals("test", result.matchedTokens().get(1).value());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void not() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 4);
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void notBehavior() {
		TokenRule innerRule = TokenRules.pattern("test");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 3);
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("test"), createToken("test")));
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNotNull(rule.match(stream1, context));
		assertNull(negated.match(stream1.copyFromZero(), context));
		
		assertNull(rule.match(stream2, context));
		assertNotNull(negated.match(stream2.copyFromZero(), context));
	}
	
	@Test
	void notDoubleNegation() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 4);
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void toStringTest() {
		TokenRule innerRule = createRule();
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 4);
		
		String result = rule.toString();
		
		assertTrue(result.contains("RepeatedTokenRule"));
		assertTrue(result.contains("tokenRule"));
		assertTrue(result.contains("minOccurrences"));
		assertTrue(result.contains("maxOccurrences"));
	}
}
