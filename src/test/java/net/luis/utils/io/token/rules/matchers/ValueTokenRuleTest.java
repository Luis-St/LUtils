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

class ValueTokenRuleTest {
	
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
	void constructorWithCharCaseSensitive() {
		ValueTokenRule rule = new ValueTokenRule('a', false);
		
		assertEquals("a", rule.value());
		assertFalse(rule.ignoreCase());
	}
	
	@Test
	void constructorWithCharIgnoreCase() {
		ValueTokenRule rule = new ValueTokenRule('A', true);
		
		assertEquals("A", rule.value());
		assertTrue(rule.ignoreCase());
	}
	
	@Test
	void constructorWithStringCaseSensitive() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		
		assertEquals("test", rule.value());
		assertFalse(rule.ignoreCase());
	}
	
	@Test
	void constructorWithStringIgnoreCase() {
		ValueTokenRule rule = new ValueTokenRule("TEST", true);
		
		assertEquals("TEST", rule.value());
		assertTrue(rule.ignoreCase());
	}
	
	@Test
	void constructorWithNullString() {
		assertThrows(NullPointerException.class, () -> new ValueTokenRule(null, false));
	}
	
	@Test
	void constructorWithEmptyString() {
		assertThrows(IllegalArgumentException.class, () -> new ValueTokenRule("", false));
	}
	
	@Test
	void matchWithExactStringCaseSensitive() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		Token token = createToken("test");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithDifferentStringCaseSensitive() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		Token token = createToken("other");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithDifferentCaseCaseSensitive() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		Token token = createToken("Test");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithExactStringIgnoreCase() {
		ValueTokenRule rule = new ValueTokenRule("test", true);
		Token token = createToken("test");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithDifferentCaseIgnoreCase() {
		ValueTokenRule rule = new ValueTokenRule("test", true);
		Token token = createToken("Test");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithUpperCaseIgnoreCase() {
		ValueTokenRule rule = new ValueTokenRule("test", true);
		Token token = createToken("TEST");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithMixedCaseIgnoreCase() {
		ValueTokenRule rule = new ValueTokenRule("test", true);
		Token token = createToken("TeSt");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithDifferentStringIgnoreCase() {
		ValueTokenRule rule = new ValueTokenRule("test", true);
		Token token = createToken("other");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithCharExactCaseSensitive() {
		ValueTokenRule rule = new ValueTokenRule('a', false);
		Token token = createToken("a");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithCharDifferentCaseSensitive() {
		ValueTokenRule rule = new ValueTokenRule('a', false);
		Token token = createToken("A");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithCharDifferentCaseIgnoreCase() {
		ValueTokenRule rule = new ValueTokenRule('a', true);
		Token token = createToken("A");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithCharAsMultipleChars() {
		ValueTokenRule rule = new ValueTokenRule('a', false);
		Token token = createToken("ab");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithSpecialCharacters() {
		ValueTokenRule rule = new ValueTokenRule("!@#", false);
		Token token = createToken("!@#");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithUnicodeCharacters() {
		ValueTokenRule rule = new ValueTokenRule("café", false);
		Token token = createToken("café");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithUnicodeCharactersIgnoreCase() {
		ValueTokenRule rule = new ValueTokenRule("café", true);
		Token token = createToken("CAFÉ");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithWhitespace() {
		ValueTokenRule rule = new ValueTokenRule(" ", false);
		Token token = createToken(" ");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithTabCharacter() {
		ValueTokenRule rule = new ValueTokenRule('\t', false);
		Token token = createToken("\t");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithNewlineCharacter() {
		ValueTokenRule rule = new ValueTokenRule('\n', false);
		Token token = createToken("\n");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithNullToken() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithTokenStreamAndContext() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals("test", result.matchedTokens().get(0).value());
	}
	
	@Test
	void matchWithTokenStreamNoMatch() {
		ValueTokenRule rule = new ValueTokenRule("expected", false);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithEmptyTokenStream() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void not() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void notWithDoubleNegation() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void notBehavior() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("test"))); // Should match original
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("other"))); // Should not match original
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNotNull(rule.match(stream1, context)); // Original matches
		assertNull(negated.match(stream1.copyFromZero(), context)); // Negated doesn't match
		
		assertNull(rule.match(stream2, context)); // Original doesn't match
		assertNotNull(negated.match(stream2.copyFromZero(), context)); // Negated matches
	}
	
	@Test
	void equalsAndHashCode() {
		ValueTokenRule rule1 = new ValueTokenRule("test", false);
		ValueTokenRule rule2 = new ValueTokenRule("test", false);
		ValueTokenRule rule3 = new ValueTokenRule("other", false);
		ValueTokenRule rule4 = new ValueTokenRule("test", true);
		
		assertEquals(rule1, rule2);
		assertNotEquals(rule1, rule3);
		assertNotEquals(rule1, rule4);
		assertNotEquals(rule1, null);
		assertNotEquals(rule1, "string");
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void equalsWithCharConstructor() {
		ValueTokenRule rule1 = new ValueTokenRule('a', false);
		ValueTokenRule rule2 = new ValueTokenRule('a', false);
		ValueTokenRule rule3 = new ValueTokenRule('b', false);
		ValueTokenRule rule4 = new ValueTokenRule('a', true);
		
		assertEquals(rule1, rule2);
		assertNotEquals(rule1, rule3);
		assertNotEquals(rule1, rule4);
	}
	
	@Test
	void toStringTest() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		
		String result = rule.toString();
		
		assertTrue(result.contains("ValueTokenRule"));
		assertTrue(result.contains("value"));
		assertTrue(result.contains("ignoreCase"));
	}
}
