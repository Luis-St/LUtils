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
import org.junit.jupiter.api.Test;

import java.util.List;

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
	void matchAtDifferentStreamPosition() {
		LengthTokenRule rule = new LengthTokenRule(2, 4);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("a"), createToken("abc"), createToken("abcde")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals("abc", result.matchedTokens().getFirst().value());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithMultipleTokensInStream() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("ab"), createToken("abc"), createToken("abcd")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSecondTokenMatching() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("ab"), createToken("abc"), createToken("abcd")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals("abc", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithVeryLongToken() {
		LengthTokenRule rule = new LengthTokenRule(0, 1000);
		String longValue = "a".repeat(500);
		TokenStream stream = TokenStream.createMutable(List.of(createToken(longValue)));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(longValue, result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithVeryLongTokenExceedingMax() {
		LengthTokenRule rule = new LengthTokenRule(0, 10);
		String longValue = "a".repeat(50);
		TokenStream stream = TokenStream.createMutable(List.of(createToken(longValue)));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithZeroLengthRange() {
		LengthTokenRule rule = new LengthTokenRule(0, 0);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithZeroLengthRangeNonEmptyToken() {
		LengthTokenRule rule = new LengthTokenRule(0, 0);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("a")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithSingleCharacterRange() {
		LengthTokenRule rule = new LengthTokenRule(1, 1);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("x")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("x", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithSingleCharacterRangeTooLong() {
		LengthTokenRule rule = new LengthTokenRule(1, 1);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("xy")));
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
	void matchWithSingleCharacterToken() {
		LengthTokenRule rule = new LengthTokenRule(1, 1);
		Token token = createToken("a");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithWhitespaceToken() {
		LengthTokenRule rule = new LengthTokenRule(1, 5);
		Token token = createToken(" ");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithMultipleWhitespaceToken() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		Token token = createToken("   ");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithUnicodeToken() {
		LengthTokenRule rule = new LengthTokenRule(1, 5);
		Token token = createToken("🌟");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithMultipleUnicodeCharacters() {
		LengthTokenRule rule = new LengthTokenRule(3, 6);
		Token token = createToken("🌟🎉🎊");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithSpecialCharactersToken() {
		LengthTokenRule rule = new LengthTokenRule(3, 10);
		Token token = createToken("!@#$%");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithNumericToken() {
		LengthTokenRule rule = new LengthTokenRule(3, 6);
		Token token = createToken("12345");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithMixedContentToken() {
		LengthTokenRule rule = new LengthTokenRule(5, 15);
		Token token = createToken("abc123!@#");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
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
