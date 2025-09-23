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

/**
 * Test class for {@link TokenRule}.<br>
 *
 * @author Luis-St
 */
class TokenRuleTest {
	
	private static @NotNull Token createToken() {
		return SimpleToken.createUnpositioned("lambda");
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
	void optional() {
		TokenRule baseRule = createRule();
		
		TokenRule optionalRule = baseRule.optional();
		
		OptionalTokenRule optional = assertInstanceOf(OptionalTokenRule.class, optionalRule);
		assertEquals(baseRule, optional.tokenRule());
	}
	
	@Test
	void atLeastWithNegativeValue() {
		TokenRule baseRule = createRule();
		
		assertThrows(IllegalArgumentException.class, () -> baseRule.atLeast(-1));
	}
	
	@Test
	void atLeast() {
		TokenRule baseRule = createRule();
		
		TokenRule repeatedRule = baseRule.atLeast(3);
		
		RepeatedTokenRule repeated = assertInstanceOf(RepeatedTokenRule.class, repeatedRule);
		assertEquals(baseRule, repeated.tokenRule());
		assertEquals(3, repeated.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeated.maxOccurrences());
	}
	
	@Test
	void exactlyWithNegativeValue() {
		TokenRule baseRule = createRule();
		
		assertThrows(IllegalArgumentException.class, () -> baseRule.exactly(-1));
	}
	
	@Test
	void exactly() {
		TokenRule baseRule = createRule();
		
		TokenRule repeatedRule = baseRule.exactly(5);
		
		RepeatedTokenRule repeated = assertInstanceOf(RepeatedTokenRule.class, repeatedRule);
		assertEquals(baseRule, repeated.tokenRule());
		assertEquals(5, repeated.minOccurrences());
		assertEquals(5, repeated.maxOccurrences());
	}
	
	@Test
	void atMostWithNegativeValue() {
		TokenRule baseRule = createRule();
		
		assertThrows(IllegalArgumentException.class, () -> baseRule.atMost(-1));
	}
	
	@Test
	void atMost() {
		TokenRule baseRule = createRule();
		
		TokenRule repeatedRule = baseRule.atMost(7);
		
		RepeatedTokenRule repeated = assertInstanceOf(RepeatedTokenRule.class, repeatedRule);
		assertEquals(baseRule, repeated.tokenRule());
		assertEquals(0, repeated.minOccurrences());
		assertEquals(7, repeated.maxOccurrences());
	}
	
	@Test
	void zeroOrMore() {
		TokenRule baseRule = createRule();
		
		TokenRule repeatedRule = baseRule.zeroOrMore();
		
		RepeatedTokenRule repeated = assertInstanceOf(RepeatedTokenRule.class, repeatedRule);
		assertEquals(baseRule, repeated.tokenRule());
		assertEquals(0, repeated.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeated.maxOccurrences());
	}
	
	@Test
	void betweenWithNegativeMin() {
		TokenRule baseRule = createRule();
		
		assertThrows(IllegalArgumentException.class, () -> baseRule.between(-1, 5));
	}
	
	@Test
	void betweenWithMaxLessThanMin() {
		TokenRule baseRule = createRule();
		
		assertThrows(IllegalArgumentException.class, () -> baseRule.between(5, 2));
	}
	
	@Test
	void betweenWithBothZero() {
		TokenRule baseRule = createRule();
		
		assertThrows(IllegalArgumentException.class, () -> baseRule.between(0, 0));
	}
	
	@Test
	void between() {
		TokenRule baseRule = createRule();
		
		TokenRule repeatedRule = baseRule.between(2, 8);
		
		RepeatedTokenRule repeated = assertInstanceOf(RepeatedTokenRule.class, repeatedRule);
		assertEquals(baseRule, repeated.tokenRule());
		assertEquals(2, repeated.minOccurrences());
		assertEquals(8, repeated.maxOccurrences());
	}
	
	@Test
	void notThrowsUnsupportedOperationException() {
		TokenRule baseRule = createRule();
		
		assertThrows(UnsupportedOperationException.class, baseRule::not);
	}
	
	@Test
	void group() {
		TokenRule baseRule = createRule();
		
		TokenRule groupRule = baseRule.group();
		
		TokenGroupRule group = assertInstanceOf(TokenGroupRule.class, groupRule);
		assertEquals(baseRule, group.tokenRule());
	}
	
	@Test
	void lookahead() {
		TokenRule baseRule = createRule();
		
		TokenRule lookaheadRule = baseRule.lookahead();
		
		LookaheadTokenRule lookahead = assertInstanceOf(LookaheadTokenRule.class, lookaheadRule);
		assertEquals(baseRule, lookahead.tokenRule());
	}
	
	@Test
	void negativeLookahead() {
		TokenRule baseRule = createRule();
		
		TokenRule negativeLookaheadRule = baseRule.negativeLookahead();
		
		LookaheadTokenRule lookahead = assertInstanceOf(LookaheadTokenRule.class, negativeLookaheadRule);
		assertEquals(baseRule, lookahead.tokenRule());
	}
	
	@Test
	void lookbehind() {
		TokenRule baseRule = createRule();
		
		TokenRule lookbehindRule = baseRule.lookbehind();
		
		LookbehindTokenRule lookbehind = assertInstanceOf(LookbehindTokenRule.class, lookbehindRule);
		assertEquals(baseRule, lookbehind.tokenRule());
	}
	
	@Test
	void negativeLookbehind() {
		TokenRule baseRule = createRule();
		
		TokenRule negativeLookbehindRule = baseRule.negativeLookbehind();
		
		LookbehindTokenRule lookbehind = assertInstanceOf(LookbehindTokenRule.class, negativeLookbehindRule);
		assertEquals(baseRule, lookbehind.tokenRule());
	}
	
	@Test
	void functionalInterfaceUsage() {
		TokenRule lambdaRule = new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
				if (!stream.hasMoreTokens()) {
					return null;
				}
				int startIndex = stream.getCurrentIndex();
				Token token = stream.getCurrentToken();
				if ("lambda".equals(token.value())) {
					return new TokenRuleMatch(startIndex, stream.advance(), List.of(token), this);
				}
				return null;
			}
		};
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken()));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = lambdaRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("lambda", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void chainingDefaultMethods() {
		TokenRule baseRule = createRule();
		
		TokenRule chainedRule = baseRule.optional().atLeast(2).group();
		
		TokenGroupRule group = assertInstanceOf(TokenGroupRule.class, chainedRule);
		RepeatedTokenRule repeated = assertInstanceOf(RepeatedTokenRule.class, group.tokenRule());
		OptionalTokenRule optional = assertInstanceOf(OptionalTokenRule.class, repeated.tokenRule());
		assertEquals(baseRule, optional.tokenRule());
	}
}
