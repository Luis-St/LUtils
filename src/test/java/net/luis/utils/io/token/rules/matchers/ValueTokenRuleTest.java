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
 * Test class for {@link ValueTokenRule}.<br>
 *
 * @author Luis-St
 */
class ValueTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
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
	void matchWithNullTokenStream() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
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
	void matchWithTokenStreamAndContext() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals("test", result.matchedTokens().getFirst().value());
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
	void matchWithNullToken() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
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
	void not() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void notBehavior() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("test")));
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNotNull(rule.match(stream1, context));
		assertNull(negated.match(stream1.copyFromZero(), context));
		
		assertNull(rule.match(stream2, context));
		assertNotNull(negated.match(stream2.copyFromZero(), context));
	}
	
	@Test
	void notWithDoubleNegation() {
		ValueTokenRule rule = new ValueTokenRule("test", false);
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
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
