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

package net.luis.utils.io.token.rules.matchers;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class LengthTokenRuleTest {
	
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
	void constructorWithValidLengths() {
		LengthTokenRule rule = new LengthTokenRule(2, 5);
		
		assertEquals(2, rule.minLength());
		assertEquals(5, rule.maxLength());
	}
	
	@Test
	void constructorWithEqualLengths() {
		LengthTokenRule rule = new LengthTokenRule(3, 3);
		
		assertEquals(3, rule.minLength());
		assertEquals(3, rule.maxLength());
	}
	
	@Test
	void constructorWithZeroMinLength() {
		LengthTokenRule rule = new LengthTokenRule(0, 5);
		
		assertEquals(0, rule.minLength());
		assertEquals(5, rule.maxLength());
	}
	
	@Test
	void constructorWithNegativeMinLength() {
		assertThrows(IllegalArgumentException.class, () -> new LengthTokenRule(-1, 5));
	}
	
	@Test
	void constructorWithNegativeMaxLength() {
		assertThrows(IllegalArgumentException.class, () -> new LengthTokenRule(0, -1));
	}
	
	@Test
	void constructorWithMaxLessThanMin() {
		assertThrows(IllegalArgumentException.class, () -> new LengthTokenRule(5, 2));
	}
	
	@Test
	void matchWithTokenInRange() {
		LengthTokenRule rule = new LengthTokenRule(2, 5);
		Token token = createToken("abc"); // Length 3
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithTokenAtMinLength() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		Token token = createToken("abc"); // Length 3
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithTokenAtMaxLength() {
		LengthTokenRule rule = new LengthTokenRule(2, 3);
		Token token = createToken("abc"); // Length 3
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithTokenTooShort() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		Token token = createToken("ab"); // Length 2
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithTokenTooLong() {
		LengthTokenRule rule = new LengthTokenRule(2, 4);
		Token token = createToken("abcde"); // Length 5
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithEmptyToken() {
		LengthTokenRule rule = new LengthTokenRule(0, 2);
		Token token = createToken(""); // Length 0
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithEmptyTokenWhenMinIsPositive() {
		LengthTokenRule rule = new LengthTokenRule(1, 3);
		Token token = createToken(""); // Length 0
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithNullToken() {
		LengthTokenRule rule = new LengthTokenRule(1, 3);
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithTokenStreamAndContext() {
		LengthTokenRule rule = new LengthTokenRule(2, 4);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("abc")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals("abc", result.matchedTokens().get(0).value());
	}
	
	@Test
	void matchWithTokenStreamNoMatch() {
		LengthTokenRule rule = new LengthTokenRule(5, 10);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("abc")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithEmptyTokenStream() {
		LengthTokenRule rule = new LengthTokenRule(1, 3);
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void not() {
		LengthTokenRule rule = new LengthTokenRule(2, 4);
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void notWithDoubleNegation() {
		LengthTokenRule rule = new LengthTokenRule(2, 4);
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void notBehavior() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("abcd"))); // Length 4, should match original
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("ab"))); // Length 2, should not match original
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNotNull(rule.match(stream1, context)); // Original matches
		assertNull(negated.match(stream1.copyFromZero(), context)); // Negated doesn't match
		
		assertNull(rule.match(stream2, context)); // Original doesn't match
		assertNotNull(negated.match(stream2.copyFromZero(), context)); // Negated matches
	}
	
	@Test
	void equalsAndHashCode() {
		LengthTokenRule rule1 = new LengthTokenRule(2, 5);
		LengthTokenRule rule2 = new LengthTokenRule(2, 5);
		LengthTokenRule rule3 = new LengthTokenRule(3, 5);
		LengthTokenRule rule4 = new LengthTokenRule(2, 6);
		
		assertEquals(rule1, rule2);
		assertNotEquals(rule1, rule3);
		assertNotEquals(rule1, rule4);
		assertNotEquals(rule1, null);
		assertNotEquals(rule1, "string");
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringTest() {
		LengthTokenRule rule = new LengthTokenRule(2, 5);
		
		String result = rule.toString();
		
		assertTrue(result.contains("LengthTokenRule"));
		assertTrue(result.contains("minLength"));
		assertTrue(result.contains("maxLength"));
	}
}
