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

package net.luis.utils.io.token.rules.assertions;

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

class LookaheadTokenRuleTest {
	
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
	void constructorWithNullTokenRule() {
		assertThrows(NullPointerException.class, () -> new LookaheadTokenRule(null, LookMatchMode.POSITIVE));
	}
	
	@Test
	void constructorWithNullMode() {
		assertThrows(NullPointerException.class, () -> new LookaheadTokenRule(createRule("test"), null));
	}
	
	@Test
	void constructorWithValidParameters() {
		TokenRule tokenRule = createRule("test");
		LookMatchMode mode = LookMatchMode.POSITIVE;
		
		LookaheadTokenRule rule = new LookaheadTokenRule(tokenRule, mode);
		
		assertEquals(tokenRule, rule.tokenRule());
		assertEquals(mode, rule.mode());
	}
	
	@Test
	void matchWithNullStream() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyStream() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result); // Inner rule can't match empty stream
	}
	
	@Test
	void matchWithEmptyStreamNegative() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result); // Negative match when inner rule fails
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
	}
	
	@Test
	void matchPositiveWithMatchingRule() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex()); // Lookahead doesn't consume
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(0, stream.getCurrentIndex()); // Stream position unchanged
	}
	
	@Test
	void matchPositiveWithNonMatchingRule() {
		TokenRule innerRule = createRule("expected");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex()); // Stream position unchanged
	}
	
	@Test
	void matchNegativeWithMatchingRule() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex()); // Stream position unchanged
	}
	
	@Test
	void matchNegativeWithNonMatchingRule() {
		TokenRule innerRule = createRule("expected");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex()); // Lookahead doesn't consume
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(0, stream.getCurrentIndex()); // Stream position unchanged
	}
	
	@Test
	void matchWithAlwaysMatchRule() {
		LookaheadTokenRule rule = new LookaheadTokenRule(TokenRules.alwaysMatch(), LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, stream.getCurrentIndex()); // Stream not advanced
	}
	
	@Test
	void matchWithNeverMatchRule() {
		LookaheadTokenRule rule = new LookaheadTokenRule(TokenRules.neverMatch(), LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex()); // Stream not advanced
	}
	
	@Test
	void not() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenRule negated = rule.not();
		
		assertTrue(negated instanceof LookaheadTokenRule);
		LookaheadTokenRule negatedLookahead = (LookaheadTokenRule) negated;
		assertEquals(innerRule, negatedLookahead.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, negatedLookahead.mode());
	}
	
	@Test
	void notWithNegativeMode() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenRule negated = rule.not();
		
		assertTrue(negated instanceof LookaheadTokenRule);
		LookaheadTokenRule negatedLookahead = (LookaheadTokenRule) negated;
		assertEquals(innerRule, negatedLookahead.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, negatedLookahead.mode());
	}
	
	@Test
	void equalsAndHashCode() {
		TokenRule tokenRule1 = createRule("test");
		TokenRule tokenRule2 = createRule("test");
		TokenRule tokenRule3 = createRule("other");
		
		LookaheadTokenRule rule1 = new LookaheadTokenRule(tokenRule1, LookMatchMode.POSITIVE);
		LookaheadTokenRule rule2 = new LookaheadTokenRule(tokenRule1, LookMatchMode.POSITIVE);
		LookaheadTokenRule rule3 = new LookaheadTokenRule(tokenRule2, LookMatchMode.POSITIVE);
		LookaheadTokenRule rule4 = new LookaheadTokenRule(tokenRule1, LookMatchMode.NEGATIVE);
		LookaheadTokenRule rule5 = new LookaheadTokenRule(tokenRule3, LookMatchMode.POSITIVE);
		
		assertEquals(rule1, rule2);
		assertNotEquals(rule1, rule3); // Different token rule instances
		assertNotEquals(rule1, rule4); // Different mode
		assertNotEquals(rule1, rule5); // Different token rule
		assertNotEquals(rule1, null);
		assertNotEquals(rule1, "string");
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringTest() {
		TokenRule tokenRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(tokenRule, LookMatchMode.POSITIVE);
		
		String result = rule.toString();
		
		assertTrue(result.contains("LookaheadTokenRule"));
		assertTrue(result.contains("tokenRule"));
		assertTrue(result.contains("mode"));
	}
}
