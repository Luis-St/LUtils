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
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class PatternTokenRuleTest {
	
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
	void constructorWithStringPattern() {
		String regex = "\\d+";
		
		PatternTokenRule rule = new PatternTokenRule(regex);
		
		assertEquals(regex, rule.pattern().pattern());
	}
	
	@Test
	void constructorWithNullStringPattern() {
		assertThrows(NullPointerException.class, () -> new PatternTokenRule((String) null));
	}
	
	@Test
	void constructorWithPatternObject() {
		Pattern pattern = Pattern.compile("\\w+");
		
		PatternTokenRule rule = new PatternTokenRule(pattern);
		
		assertEquals(pattern, rule.pattern());
	}
	
	@Test
	void constructorWithNullPatternObject() {
		assertThrows(NullPointerException.class, () -> new PatternTokenRule((Pattern) null));
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
		PatternTokenRule rule = new PatternTokenRule("\\d{2,4}-\\d{2}-\\d{2}"); // Date pattern
		Token token = createToken("2023-12-25");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithComplexPatternNonMatching() {
		PatternTokenRule rule = new PatternTokenRule("\\d{2,4}-\\d{2}-\\d{2}"); // Date pattern
		Token token = createToken("2023/12/25");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
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
	void matchWithDotPattern() {
		PatternTokenRule rule = new PatternTokenRule(".");
		Token token = createToken("a");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithDotPatternMultipleChars() {
		PatternTokenRule rule = new PatternTokenRule(".");
		Token token = createToken("ab");
		
		boolean result = rule.match(token);
		
		assertFalse(result); // . matches only single character
	}
	
	@Test
	void matchWithDotStarPattern() {
		PatternTokenRule rule = new PatternTokenRule(".*");
		Token token = createToken("anything goes here");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithNullToken() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
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
	void matchWithEmptyTokenStream() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void not() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void notWithDoubleNegation() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void notBehavior() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("123"))); // Should match original
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("abc"))); // Should not match original
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNotNull(rule.match(stream1, context)); // Original matches
		assertNull(negated.match(stream1.copyFromZero(), context)); // Negated doesn't match
		
		assertNull(rule.match(stream2, context)); // Original doesn't match
		assertNotNull(negated.match(stream2.copyFromZero(), context)); // Negated matches
	}
	
	@Test
	void equalsAndHashCode() {
		Pattern pattern1 = Pattern.compile("\\d+");
		Pattern pattern2 = Pattern.compile("\\d+");
		Pattern pattern3 = Pattern.compile("\\w+");
		
		PatternTokenRule rule1 = new PatternTokenRule(pattern1);
		PatternTokenRule rule2 = new PatternTokenRule(pattern1);
		PatternTokenRule rule3 = new PatternTokenRule(pattern2);
		PatternTokenRule rule4 = new PatternTokenRule(pattern3);
		
		assertEquals(rule1, rule2);
		assertNotEquals(rule1, rule3); // Different pattern instances
		assertNotEquals(rule1, rule4);
		assertNotEquals(rule1, null);
		assertNotEquals(rule1, "string");
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void equalsWithStringConstructor() {
		PatternTokenRule rule1 = new PatternTokenRule("\\d+");
		PatternTokenRule rule2 = new PatternTokenRule("\\d+");
		PatternTokenRule rule3 = new PatternTokenRule("\\w+");
		
		// These might not be equal because Pattern.compile creates new instances
		assertNotEquals(rule1, rule2);
		assertNotEquals(rule1, rule3);
	}
	
	@Test
	void toStringTest() {
		PatternTokenRule rule = new PatternTokenRule("\\d+");
		
		String result = rule.toString();
		
		assertTrue(result.contains("PatternTokenRule"));
		assertTrue(result.contains("pattern"));
	}
}
