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

package net.luis.utils.io.token.rules;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.assertions.LookaheadTokenRule;
import net.luis.utils.io.token.rules.assertions.LookbehindTokenRule;
import net.luis.utils.io.token.rules.quantifiers.OptionalTokenRule;
import net.luis.utils.io.token.rules.quantifiers.RepeatedTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TokenRuleTest {
	
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
	void optional() {
		TokenRule baseRule = createRule("test");
		
		TokenRule optionalRule = baseRule.optional();
		
		assertTrue(optionalRule instanceof OptionalTokenRule);
		OptionalTokenRule optional = (OptionalTokenRule) optionalRule;
		assertEquals(baseRule, optional.tokenRule());
	}
	
	@Test
	void atLeast() {
		TokenRule baseRule = createRule("test");
		
		TokenRule repeatedRule = baseRule.atLeast(3);
		
		assertTrue(repeatedRule instanceof RepeatedTokenRule);
		RepeatedTokenRule repeated = (RepeatedTokenRule) repeatedRule;
		assertEquals(baseRule, repeated.tokenRule());
		assertEquals(3, repeated.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeated.maxOccurrences());
	}
	
	@Test
	void atLeastWithNegativeValue() {
		TokenRule baseRule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> baseRule.atLeast(-1));
	}
	
	@Test
	void exactly() {
		TokenRule baseRule = createRule("test");
		
		TokenRule repeatedRule = baseRule.exactly(5);
		
		assertTrue(repeatedRule instanceof RepeatedTokenRule);
		RepeatedTokenRule repeated = (RepeatedTokenRule) repeatedRule;
		assertEquals(baseRule, repeated.tokenRule());
		assertEquals(5, repeated.minOccurrences());
		assertEquals(5, repeated.maxOccurrences());
	}
	
	@Test
	void exactlyWithNegativeValue() {
		TokenRule baseRule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> baseRule.exactly(-1));
	}
	
	@Test
	void atMost() {
		TokenRule baseRule = createRule("test");
		
		TokenRule repeatedRule = baseRule.atMost(7);
		
		assertTrue(repeatedRule instanceof RepeatedTokenRule);
		RepeatedTokenRule repeated = (RepeatedTokenRule) repeatedRule;
		assertEquals(baseRule, repeated.tokenRule());
		assertEquals(0, repeated.minOccurrences());
		assertEquals(7, repeated.maxOccurrences());
	}
	
	@Test
	void atMostWithNegativeValue() {
		TokenRule baseRule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> baseRule.atMost(-1));
	}
	
	@Test
	void zeroOrMore() {
		TokenRule baseRule = createRule("test");
		
		TokenRule repeatedRule = baseRule.zeroOrMore();
		
		assertTrue(repeatedRule instanceof RepeatedTokenRule);
		RepeatedTokenRule repeated = (RepeatedTokenRule) repeatedRule;
		assertEquals(baseRule, repeated.tokenRule());
		assertEquals(0, repeated.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeated.maxOccurrences());
	}
	
	@Test
	void between() {
		TokenRule baseRule = createRule("test");
		
		TokenRule repeatedRule = baseRule.between(2, 8);
		
		assertTrue(repeatedRule instanceof RepeatedTokenRule);
		RepeatedTokenRule repeated = (RepeatedTokenRule) repeatedRule;
		assertEquals(baseRule, repeated.tokenRule());
		assertEquals(2, repeated.minOccurrences());
		assertEquals(8, repeated.maxOccurrences());
	}
	
	@Test
	void betweenWithNegativeMin() {
		TokenRule baseRule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> baseRule.between(-1, 5));
	}
	
	@Test
	void betweenWithMaxLessThanMin() {
		TokenRule baseRule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> baseRule.between(5, 2));
	}
	
	@Test
	void betweenWithBothZero() {
		TokenRule baseRule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> baseRule.between(0, 0));
	}
	
	@Test
	void notThrowsUnsupportedOperationException() {
		TokenRule baseRule = createRule("test");
		
		assertThrows(UnsupportedOperationException.class, baseRule::not);
	}
	
	@Test
	void group() {
		TokenRule baseRule = createRule("test");
		
		TokenRule groupRule = baseRule.group();
		
		assertTrue(groupRule instanceof TokenGroupRule);
		TokenGroupRule group = (TokenGroupRule) groupRule;
		assertEquals(baseRule, group.tokenRule());
	}
	
	@Test
	void lookahead() {
		TokenRule baseRule = createRule("test");
		
		TokenRule lookaheadRule = baseRule.lookahead();
		
		assertTrue(lookaheadRule instanceof LookaheadTokenRule);
		LookaheadTokenRule lookahead = (LookaheadTokenRule) lookaheadRule;
		assertEquals(baseRule, lookahead.tokenRule());
	}
	
	@Test
	void negativeLookahead() {
		TokenRule baseRule = createRule("test");
		
		TokenRule negativeLookaheadRule = baseRule.negativeLookahead();
		
		assertTrue(negativeLookaheadRule instanceof LookaheadTokenRule);
		LookaheadTokenRule lookahead = (LookaheadTokenRule) negativeLookaheadRule;
		assertEquals(baseRule, lookahead.tokenRule());
	}
	
	@Test
	void lookbehind() {
		TokenRule baseRule = createRule("test");
		
		TokenRule lookbehindRule = baseRule.lookbehind();
		
		assertTrue(lookbehindRule instanceof LookbehindTokenRule);
		LookbehindTokenRule lookbehind = (LookbehindTokenRule) lookbehindRule;
		assertEquals(baseRule, lookbehind.tokenRule());
	}
	
	@Test
	void negativeLookbehind() {
		TokenRule baseRule = createRule("test");
		
		TokenRule negativeLookbehindRule = baseRule.negativeLookbehind();
		
		assertTrue(negativeLookbehindRule instanceof LookbehindTokenRule);
		LookbehindTokenRule lookbehind = (LookbehindTokenRule) negativeLookbehindRule;
		assertEquals(baseRule, lookbehind.tokenRule());
	}
	
	@Test
	void functionalInterfaceUsage() {
		// Test that TokenRule can be used as a functional interface
		TokenRule lambdaRule = new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
				if (!stream.hasMoreTokens()) {
					return null;
				}
				int startIndex = stream.getCurrentIndex();
				Token token = stream.getCurrentToken();
				if (token.value().equals("lambda")) {
					return new TokenRuleMatch(startIndex, stream.advance(), List.of(token), this);
				}
				return null;
			}
		};
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("lambda")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = lambdaRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("lambda", result.matchedTokens().get(0).value());
	}
	
	@Test
	void chainingDefaultMethods() {
		TokenRule baseRule = createRule("test");
		
		TokenRule chainedRule = baseRule.optional().atLeast(2).group();
		
		// Should create a group rule containing a repeated rule containing an optional rule
		assertTrue(chainedRule instanceof TokenGroupRule);
		TokenGroupRule group = (TokenGroupRule) chainedRule;
		assertTrue(group.tokenRule() instanceof RepeatedTokenRule);
		RepeatedTokenRule repeated = (RepeatedTokenRule) group.tokenRule();
		assertTrue(repeated.tokenRule() instanceof OptionalTokenRule);
		OptionalTokenRule optional = (OptionalTokenRule) repeated.tokenRule();
		assertEquals(baseRule, optional.tokenRule());
	}
	
	@Test
	void defaultMethodsWithNullRule() {
		TokenRule nullRule = null;
		
		// These should throw NPE when the underlying constructor is called
		assertThrows(NullPointerException.class, () -> TokenRules.optional(nullRule));
		assertThrows(NullPointerException.class, () -> TokenRules.atLeast(nullRule, 1));
		assertThrows(NullPointerException.class, () -> TokenRules.exactly(nullRule, 1));
		assertThrows(NullPointerException.class, () -> TokenRules.atMost(nullRule, 1));
		assertThrows(NullPointerException.class, () -> TokenRules.zeroOrMore(nullRule));
		assertThrows(NullPointerException.class, () -> TokenRules.between(nullRule, 1, 2));
		assertThrows(NullPointerException.class, () -> TokenRules.group(nullRule));
		assertThrows(NullPointerException.class, () -> TokenRules.lookahead(nullRule));
		assertThrows(NullPointerException.class, () -> TokenRules.negativeLookahead(nullRule));
		assertThrows(NullPointerException.class, () -> TokenRules.lookbehind(nullRule));
		assertThrows(NullPointerException.class, () -> TokenRules.negativeLookbehind(nullRule));
	}
}
