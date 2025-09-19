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

import static org.junit.jupiter.api.Assertions.*;

class NegatableTokenRuleTest {
	
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
	
	private static NegatableTokenRule createNegatableRule(@NotNull String expectedValue) {
		return token -> token.value().equals(expectedValue);
	}
	
	@Test
	void matchWithTokenStreamAndContextMatching() {
		NegatableTokenRule rule = createNegatableRule("test");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals("test", result.matchedTokens().get(0).value());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(1, stream.getCurrentIndex()); // Stream advanced
	}
	
	@Test
	void matchWithTokenStreamAndContextNotMatching() {
		NegatableTokenRule rule = createNegatableRule("expected");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex()); // Stream not advanced
	}
	
	@Test
	void matchWithEmptyStream() {
		NegatableTokenRule rule = createNegatableRule("test");
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithNullStream() {
		NegatableTokenRule rule = createNegatableRule("test");
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		NegatableTokenRule rule = createNegatableRule("test");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchDirectWithMatchingToken() {
		NegatableTokenRule rule = createNegatableRule("test");
		Token token = createToken("test");
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchDirectWithNonMatchingToken() {
		NegatableTokenRule rule = createNegatableRule("expected");
		Token token = createToken("actual");
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchDirectWithNullToken() {
		NegatableTokenRule rule = createNegatableRule("test");
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void notWithPositiveRule() {
		NegatableTokenRule rule = createNegatableRule("test");
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void notBehaviorWithMatchingToken() {
		NegatableTokenRule rule = createNegatableRule("test");
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("test")));
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		// Original rule matches "test"
		assertNotNull(rule.match(stream1, context));
		// Negated rule doesn't match "test"
		assertNull(negated.match(stream1.copyFromZero(), context));
		
		// Original rule doesn't match "other"
		assertNull(rule.match(stream2, context));
		// Negated rule matches "other"
		assertNotNull(negated.match(stream2.copyFromZero(), context));
	}
	
	@Test
	void notDoubleNegation() {
		NegatableTokenRule rule = createNegatableRule("test");
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void notWithEmptyStream() {
		NegatableTokenRule rule = createNegatableRule("test");
		TokenRule negated = rule.not();
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = negated.match(stream, context);
		
		assertNull(result); // Can't match on empty stream
	}
	
	@Test
	void notStreamAdvancement() {
		NegatableTokenRule rule = createNegatableRule("nomatch");
		TokenRule negated = rule.not();
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test"), createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = negated.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals("test", result.matchedTokens().get(0).value());
		assertEquals(1, stream.getCurrentIndex()); // Stream advanced
	}
	
	@Test
	void functionalInterfaceUsage() {
		// Test that we can use lambda expressions
		NegatableTokenRule lengthRule = token -> token.value().length() > 3;
		
		assertTrue(lengthRule.match(createToken("hello")));
		assertFalse(lengthRule.match(createToken("hi")));
	}
	
	@Test
	void functionalInterfaceWithComplexLogic() {
		// Test more complex matching logic
		NegatableTokenRule complexRule = token -> {
			String value = token.value();
			return value.startsWith("prefix_") && value.endsWith("_suffix") && value.length() > 10;
		};
		
		assertTrue(complexRule.match(createToken("prefix_middle_suffix")));
		assertFalse(complexRule.match(createToken("prefix_suffix")));
		assertFalse(complexRule.match(createToken("middle_suffix")));
		assertFalse(complexRule.match(createToken("prefix_middle")));
	}
	
	@Test
	void functionalInterfaceWithMethodReference() {
		// Test using method references
		NegatableTokenRule methodRefRule = this::isNumeric;
		
		assertTrue(methodRefRule.match(createToken("123")));
		assertTrue(methodRefRule.match(createToken("0")));
		assertFalse(methodRefRule.match(createToken("abc")));
		assertFalse(methodRefRule.match(createToken("12a")));
	}
	
	private boolean isNumeric(@NotNull Token token) {
		try {
			Integer.parseInt(token.value());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
