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
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CustomTokeRule}.<br>
 *
 * @author Luis-St
 */
class CustomTokeRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithNullCondition() {
		assertThrows(NullPointerException.class, () -> new CustomTokeRule(null));
	}
	
	@Test
	void constructorWithValidCondition() {
		Predicate<Token> condition = token -> "test".equals(token.value());
		
		CustomTokeRule rule = new CustomTokeRule(condition);
		
		assertEquals(condition, rule.condition());
	}
	
	@Test
	void matchWithNullTokenStream() {
		Predicate<Token> condition = token -> true;
		CustomTokeRule rule = new CustomTokeRule(condition);
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		Predicate<Token> condition = token -> true;
		CustomTokeRule rule = new CustomTokeRule(condition);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyTokenStream() {
		Predicate<Token> condition = token -> true;
		CustomTokeRule rule = new CustomTokeRule(condition);
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithAlwaysTruePredicate() {
		CustomTokeRule rule = new CustomTokeRule(token -> true);
		Token token = SimpleToken.createUnpositioned("anything");
		TokenStream stream = TokenStream.createMutable(List.of(token));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(token, result.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithAlwaysFalsePredicate() {
		CustomTokeRule rule = new CustomTokeRule(token -> false);
		Token token = SimpleToken.createUnpositioned("anything");
		TokenStream stream = TokenStream.createMutable(List.of(token));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithExceptionThrowingPredicate() {
		CustomTokeRule rule = new CustomTokeRule(token -> {
			throw new RuntimeException("Test exception");
		});
		Token token = SimpleToken.createUnpositioned("test");
		TokenStream stream = TokenStream.createMutable(List.of(token));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(RuntimeException.class, () -> rule.match(stream, context));
	}
	
	@Test
	void matchWithNullToken() {
		Predicate<Token> condition = token -> true;
		CustomTokeRule rule = new CustomTokeRule(condition);
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithMatchingToken() {
		Predicate<Token> condition = token -> "test".equals(token.value());
		CustomTokeRule rule = new CustomTokeRule(condition);
		Token token = createToken("test");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithNonMatchingToken() {
		Predicate<Token> condition = token -> "test".equals(token.value());
		CustomTokeRule rule = new CustomTokeRule(condition);
		Token token = createToken("other");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithComplexCondition() {
		Predicate<Token> condition = token -> token.value().length() > 3 && token.value().startsWith("test");
		CustomTokeRule rule = new CustomTokeRule(condition);
		
		assertTrue(rule.match(createToken("testing")));
		assertTrue(rule.match(createToken("test")));
		assertFalse(rule.match(createToken("tes")));
		assertFalse(rule.match(createToken("other")));
	}
	
	@Test
	void matchWithTokenStreamAndContext() {
		Predicate<Token> condition = token -> "match".equals(token.value());
		CustomTokeRule rule = new CustomTokeRule(condition);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("match")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals("match", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithTokenStreamNoMatch() {
		Predicate<Token> condition = token -> "match".equals(token.value());
		CustomTokeRule rule = new CustomTokeRule(condition);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("nomatch")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithStreamAdvancement() {
		Predicate<Token> condition = token -> "match".equals(token.value());
		CustomTokeRule rule = new CustomTokeRule(condition);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("match"), createToken("remaining")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("match", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
		assertTrue(stream.hasMoreTokens());
		assertEquals("remaining", stream.getCurrentToken().value());
	}
	
	@Test
	void matchWithStreamNoAdvancementOnFailure() {
		Predicate<Token> condition = token -> "expected".equals(token.value());
		CustomTokeRule rule = new CustomTokeRule(condition);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual"), createToken("remaining")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
		assertTrue(stream.hasMoreTokens());
		assertEquals("actual", stream.getCurrentToken().value());
	}
	
	@Test
	void matchWithEmptyStringCondition() {
		Predicate<Token> condition = token -> token.value().isEmpty();
		CustomTokeRule rule = new CustomTokeRule(condition);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithWhitespaceCondition() {
		Predicate<Token> condition = token -> token.value().trim().isEmpty();
		CustomTokeRule rule = new CustomTokeRule(condition);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("   ")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("   ", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithRegexCondition() {
		Predicate<Token> condition = token -> token.value().matches("\\d+");
		CustomTokeRule rule = new CustomTokeRule(condition);
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("123")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result1 = rule.match(stream1, context);
		assertNotNull(result1);
		assertEquals("123", result1.matchedTokens().getFirst().value());
		
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("abc")));
		TokenRuleMatch result2 = rule.match(stream2, context);
		assertNull(result2);
	}
	
	@Test
	void matchWithCaseSensitiveCondition() {
		Predicate<Token> condition = token -> "Test".equals(token.value());
		CustomTokeRule rule = new CustomTokeRule(condition);
		
		assertTrue(rule.match(createToken("Test")));
		assertFalse(rule.match(createToken("test")));
		assertFalse(rule.match(createToken("TEST")));
	}
	
	@Test
	void matchWithLengthCondition() {
		Predicate<Token> condition = token -> token.value().length() == 5;
		CustomTokeRule rule = new CustomTokeRule(condition);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("hello"), createToken("world")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("hello", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSpecialCharacterCondition() {
		Predicate<Token> condition = token -> token.value().contains("@");
		CustomTokeRule rule = new CustomTokeRule(condition);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("user@domain.com")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("user@domain.com", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void not() {
		Predicate<Token> condition = token -> "test".equals(token.value());
		CustomTokeRule rule = new CustomTokeRule(condition);
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void notWithDoubleNegation() {
		Predicate<Token> condition = token -> "test".equals(token.value());
		CustomTokeRule rule = new CustomTokeRule(condition);
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void toStringTest() {
		Predicate<Token> condition = token -> "test".equals(token.value());
		CustomTokeRule rule = new CustomTokeRule(condition);
		
		String result = rule.toString();
		
		assertTrue(result.contains("CustomTokeRule"));
		assertTrue(result.contains("condition"));
	}
}
