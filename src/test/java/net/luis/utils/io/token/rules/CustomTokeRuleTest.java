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
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
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
	void matchWithEmptyTokenStream() {
		Predicate<Token> condition = token -> true;
		CustomTokeRule rule = new CustomTokeRule(condition);
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
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
