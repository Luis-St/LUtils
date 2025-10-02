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
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PatternTokenRule}.<br>
 *
 * @author Luis-St
 */
class PatternTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithNullStringPattern() {
		assertThrows(NullPointerException.class, () -> new PatternTokenRule((String) null));
	}
	
	@Test
	void constructorWithStringPattern() {
		String regex = "\\d+";
		
		PatternTokenRule rule = new PatternTokenRule(regex);
		
		assertEquals(regex, rule.pattern().pattern());
	}
	
	@Test
	void constructorWithNullPatternObject() {
		assertThrows(NullPointerException.class, () -> new PatternTokenRule((Pattern) null));
	}
	
	@Test
	void constructorWithPatternObject() {
		Pattern pattern = Pattern.compile("\\w+");
		
		PatternTokenRule rule = new PatternTokenRule(pattern);
		
		assertEquals(pattern, rule.pattern());
	}
	
	@Test
	void matchWithNullTokenStream() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("abc")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyTokenStream() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithTokenStreamAndContext() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("123")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals("123", result.matchedTokens().get(0).value());
	}
	
	@Test
	void matchWithTokenStreamNoMatch() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("abc")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchAtDifferentStreamPosition() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("abc"), createToken("123"), createToken("xyz")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals("123", result.matchedTokens().get(0).value());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithMultipleTokensInStream() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("abc"), createToken("123"), createToken("456")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSecondTokenMatching() {
		PatternTokenRule rule = new PatternTokenRule("\\w+");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("123"), createToken("abc"), createToken("xyz")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals("abc", result.matchedTokens().get(0).value());
	}
	
	@Test
	void matchWithPartialTokenMatch() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("abc123")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithAnchoredPattern() {
		PatternTokenRule rule = new PatternTokenRule("^\\d+$");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("123")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("123", result.matchedTokens().get(0).value());
	}
	
	@Test
	void matchWithAnchoredPatternNoMatch() {
		PatternTokenRule rule = new PatternTokenRule("^\\d+$");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("abc123")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithDotPattern() {
		PatternTokenRule rule = new PatternTokenRule("...");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("abc")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("abc", result.matchedTokens().get(0).value());
	}
	
	@Test
	void matchWithDotPatternNoMatch() {
		PatternTokenRule rule = new PatternTokenRule("...");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("ab")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithOptionalPattern() {
		PatternTokenRule rule = new PatternTokenRule("colou?r");
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("color")));
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("colour")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result1 = rule.match(stream1, context);
		TokenRuleMatch result2 = rule.match(stream2, context);
		
		assertNotNull(result1);
		assertNotNull(result2);
		assertEquals("color", result1.matchedTokens().get(0).value());
		assertEquals("colour", result2.matchedTokens().get(0).value());
	}
	
	@Test
	void matchWithAlternationPattern() {
		PatternTokenRule rule = new PatternTokenRule("cat|dog|bird");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("dog")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("dog", result.matchedTokens().get(0).value());
	}
	
	@Test
	void matchWithAlternationPatternNoMatch() {
		PatternTokenRule rule = new PatternTokenRule("cat|dog|bird");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("fish")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithNullToken() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithEmptyStringPattern() {
		PatternTokenRule rule = new PatternTokenRule("");
		Token token = createToken("");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithEmptyStringPatternNonEmpty() {
		PatternTokenRule rule = new PatternTokenRule("");
		Token token = createToken("text");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithDigitPattern() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		Token token = createToken("123");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithDigitPatternNonDigit() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		Token token = createToken("abc");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithWordPattern() {
		PatternTokenRule rule = new PatternTokenRule("\\w+");
		Token token = createToken("hello");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithWordPatternSpecialChars() {
		PatternTokenRule rule = new PatternTokenRule("\\w+");
		Token token = createToken("hello!");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithExactStringPattern() {
		PatternTokenRule rule = new PatternTokenRule("hello");
		Token token = createToken("hello");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithExactStringPatternDifferentCase() {
		PatternTokenRule rule = new PatternTokenRule("hello");
		Token token = createToken("Hello");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithCaseInsensitivePattern() {
		PatternTokenRule rule = new PatternTokenRule(Pattern.compile("hello", Pattern.CASE_INSENSITIVE));
		Token token = createToken("Hello");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithComplexPattern() {
		PatternTokenRule rule = new PatternTokenRule("\\d{2,4}-\\d{2}-\\d{2}");
		Token token = createToken("2023-12-25");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithComplexPatternNonMatching() {
		PatternTokenRule rule = new PatternTokenRule("\\d{2,4}-\\d{2}-\\d{2}");
		Token token = createToken("2023/12/25");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithEmailPattern() {
		PatternTokenRule rule = new PatternTokenRule("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
		Token token = createToken("user@example.com");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithEmailPatternInvalid() {
		PatternTokenRule rule = new PatternTokenRule("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
		Token token = createToken("invalid-email");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithWhitespacePattern() {
		PatternTokenRule rule = new PatternTokenRule("\\s+");
		Token token = createToken("   ");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithWhitespacePatternNoWhitespace() {
		PatternTokenRule rule = new PatternTokenRule("\\s+");
		Token token = createToken("text");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithHexPattern() {
		PatternTokenRule rule = new PatternTokenRule("[0-9a-fA-F]+");
		Token token = createToken("1a2B3c");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithHexPatternInvalid() {
		PatternTokenRule rule = new PatternTokenRule("[0-9a-fA-F]+");
		Token token = createToken("xyz123");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithUnicodePattern() {
		PatternTokenRule rule = new PatternTokenRule("\\p{L}+");
		Token token = createToken("café");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithUnicodePatternNumbers() {
		PatternTokenRule rule = new PatternTokenRule("\\p{L}+");
		Token token = createToken("123");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithQuantifierPattern() {
		PatternTokenRule rule = new PatternTokenRule("a{3,5}");
		Token token = createToken("aaaa");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithQuantifierPatternTooFew() {
		PatternTokenRule rule = new PatternTokenRule("a{3,5}");
		Token token = createToken("aa");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithQuantifierPatternTooMany() {
		PatternTokenRule rule = new PatternTokenRule("a{3,5}");
		Token token = createToken("aaaaaa");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void not() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void notBehavior() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("123")));
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("abc")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNotNull(rule.match(stream1, context));
		assertNull(negated.match(stream1.copyFromZero(), context));
		
		assertNull(rule.match(stream2, context));
		assertNotNull(negated.match(stream2.copyFromZero(), context));
	}
	
	@Test
	void notWithDoubleNegation() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void toStringTest() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		
		String result = rule.toString();
		
		assertTrue(result.contains("PatternTokenRule"));
		assertTrue(result.contains("pattern"));
	}
}
