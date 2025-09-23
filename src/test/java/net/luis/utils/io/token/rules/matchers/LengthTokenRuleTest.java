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

/**
 * Test class for {@link LengthTokenRule}.<br>
 *
 * @author Luis-St
 */
class LengthTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
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
	void matchWithNullTokenStream() {
		LengthTokenRule rule = new LengthTokenRule(1, 3);
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		LengthTokenRule rule = new LengthTokenRule(1, 3);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("abc")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
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
	void matchWithTokenStreamAndContext() {
		LengthTokenRule rule = new LengthTokenRule(2, 4);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("abc")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals("abc", result.matchedTokens().getFirst().value());
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
	void matchWithNullToken() {
		LengthTokenRule rule = new LengthTokenRule(1, 3);
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithEmptyToken() {
		LengthTokenRule rule = new LengthTokenRule(0, 2);
		Token token = createToken("");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithTokenInRange() {
		LengthTokenRule rule = new LengthTokenRule(2, 5);
		Token token = createToken("abc");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithTokenAtMinLength() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		Token token = createToken("abc");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithTokenAtMaxLength() {
		LengthTokenRule rule = new LengthTokenRule(2, 3);
		Token token = createToken("abc");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithTokenTooShort() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		Token token = createToken("ab");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithTokenTooLong() {
		LengthTokenRule rule = new LengthTokenRule(2, 4);
		Token token = createToken("abcde");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithEmptyTokenWhenMinIsPositive() {
		LengthTokenRule rule = new LengthTokenRule(1, 3);
		Token token = createToken("");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void not() {
		LengthTokenRule rule = new LengthTokenRule(2, 4);
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void notBehavior() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("abcd")));
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("ab")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNotNull(rule.match(stream1, context));
		assertNull(negated.match(stream1.copyFromZero(), context));
		
		assertNull(rule.match(stream2, context));
		assertNotNull(negated.match(stream2.copyFromZero(), context));
	}
	
	@Test
	void notWithDoubleNegation() {
		LengthTokenRule rule = new LengthTokenRule(2, 4);
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
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
