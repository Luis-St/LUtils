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

class RepeatedTokenRuleTest {
	
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
	void constructorWithSingleOccurrence() {
		TokenRule innerRule = createRule("test");
		
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 3);
		
		assertEquals(innerRule, rule.tokenRule());
		assertEquals(3, rule.minOccurrences());
		assertEquals(3, rule.maxOccurrences());
	}
	
	@Test
	void constructorWithMinMax() {
		TokenRule innerRule = createRule("test");
		
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 5);
		
		assertEquals(innerRule, rule.tokenRule());
		assertEquals(2, rule.minOccurrences());
		assertEquals(5, rule.maxOccurrences());
	}
	
	@Test
	void constructorWithNullRule() {
		assertThrows(NullPointerException.class, () -> new RepeatedTokenRule(null, 2));
	}
	
	@Test
	void constructorWithNegativeMin() {
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(createRule("test"), -1, 5));
	}
	
	@Test
	void constructorWithMaxLessThanMin() {
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(createRule("test"), 5, 2));
	}
	
	@Test
	void constructorWithBothZero() {
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(createRule("test"), 0, 0));
	}
	
	@Test
	void constructorWithNegativeOccurrences() {
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(createRule("test"), -1));
	}
	
	@Test
	void matchExactlyMinOccurrences() {
		TokenRule innerRule = createRule("test");
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
	void matchExactlyMaxOccurrences() {
		TokenRule innerRule = createRule("test");
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
		assertEquals(3, stream.getCurrentIndex()); // Only 3 tokens consumed
	}
	
	@Test
	void matchBetweenMinAndMax() {
		TokenRule innerRule = createRule("test");
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
		assertEquals(3, stream.getCurrentIndex()); // Stopped at "other"
	}
	
	@Test
	void matchFewerThanMinOccurrences() {
		TokenRule innerRule = createRule("test");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 3, 5);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test"), createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex()); // Stream position unchanged
	}
	
	@Test
	void matchWithNoMatches() {
		TokenRule innerRule = createRule("test");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 1, 3);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex()); // Stream position unchanged
	}
	
	@Test
	void matchWithEmptyStream() {
		TokenRule innerRule = createRule("test");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 1, 3);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchExactOccurrences() {
		TokenRule innerRule = createRule("test");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2); // Exactly 2
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test"), createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(2, result.matchedTokens().size());
	}
	
	@Test
	void matchExactOccurrencesWithMore() {
		TokenRule innerRule = createRule("test");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2); // Exactly 2
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("test"), createToken("test"), createToken("test")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(2, result.matchedTokens().size());
		assertEquals(2, stream.getCurrentIndex()); // Only 2 consumed
	}
	
	@Test
	void matchExactOccurrencesWithFewer() {
		TokenRule innerRule = createRule("test");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 3); // Exactly 3
		
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
		TokenRule innerRule = createRule("test");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 1, 5);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test"), createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(2, result.matchedTokens().size());
		assertEquals(2, stream.getCurrentIndex()); // All tokens consumed
	}
	
	@Test
	void matchWithNullStream() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("test"), 1, 3);
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("test"), 1, 3);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void not() {
		TokenRule innerRule = createRule("test");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 4);
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void notDoubleNegation() {
		TokenRule innerRule = createRule("test");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 4);
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void notBehavior() {
		TokenRule innerRule = createRule("test");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 3);
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("test"), createToken("test")));
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNotNull(rule.match(stream1, context)); // 2 matches, within range
		assertNull(negated.match(stream1.copyFromZero(), context)); // Negated doesn't match
		
		assertNull(rule.match(stream2, context)); // 1 match, below min
		assertNotNull(negated.match(stream2.copyFromZero(), context)); // Negated matches
	}
	
	@Test
	void equalsAndHashCode() {
		TokenRule innerRule1 = createRule("test");
		TokenRule innerRule2 = createRule("test");
		TokenRule innerRule3 = createRule("other");
		
		RepeatedTokenRule rule1 = new RepeatedTokenRule(innerRule1, 2, 4);
		RepeatedTokenRule rule2 = new RepeatedTokenRule(innerRule1, 2, 4);
		RepeatedTokenRule rule3 = new RepeatedTokenRule(innerRule2, 2, 4);
		RepeatedTokenRule rule4 = new RepeatedTokenRule(innerRule1, 3, 4);
		RepeatedTokenRule rule5 = new RepeatedTokenRule(innerRule1, 2, 5);
		RepeatedTokenRule rule6 = new RepeatedTokenRule(innerRule3, 2, 4);
		
		assertEquals(rule1, rule2);
		assertNotEquals(rule1, rule3); // Different inner rule instances
		assertNotEquals(rule1, rule4); // Different min
		assertNotEquals(rule1, rule5); // Different max
		assertNotEquals(rule1, rule6); // Different inner rule
		assertNotEquals(rule1, null);
		assertNotEquals(rule1, "string");
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringTest() {
		TokenRule innerRule = createRule("test");
		RepeatedTokenRule rule = new RepeatedTokenRule(innerRule, 2, 4);
		
		String result = rule.toString();
		
		assertTrue(result.contains("RepeatedTokenRule"));
		assertTrue(result.contains("tokenRule"));
		assertTrue(result.contains("minOccurrences"));
		assertTrue(result.contains("maxOccurrences"));
	}
}
