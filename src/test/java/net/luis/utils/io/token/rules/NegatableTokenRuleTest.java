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

/**
 * Test class for {@link NegatableTokenRule}.<br>
 *
 * @author Luis-St
 */
class NegatableTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static @NotNull NegatableTokenRule createNegatableRule(@NotNull String expectedValue) {
		return token -> token.value().equals(expectedValue);
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
	void matchWithEmptyStream() {
		NegatableTokenRule rule = createNegatableRule("test");
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
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
		assertEquals("test", result.matchedTokens().getFirst().value());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithTokenStreamAndContextNotMatching() {
		NegatableTokenRule rule = createNegatableRule("expected");
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchDirectWithNullToken() {
		NegatableTokenRule rule = createNegatableRule("test");
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
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
	void notWithEmptyStream() {
		NegatableTokenRule rule = createNegatableRule("test");
		TokenRule negated = rule.not();
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = negated.match(stream, context);
		
		assertNull(result);
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
		
		assertNotNull(rule.match(stream1, context));
		assertNull(negated.match(stream1.copyFromZero(), context));
		
		assertNull(rule.match(stream2, context));
		assertNotNull(negated.match(stream2.copyFromZero(), context));
	}
	
	@Test
	void notDoubleNegation() {
		NegatableTokenRule rule = createNegatableRule("test");
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
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
		assertEquals("test", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void functionalInterfaceUsage() {
		NegatableTokenRule lengthRule = token -> token.value().length() > 3;
		
		assertTrue(lengthRule.match(createToken("hello")));
		assertFalse(lengthRule.match(createToken("hi")));
	}
	
	@Test
	void functionalInterfaceWithComplexLogic() {
		NegatableTokenRule complexRule = token -> {
			String value = token.value();
			return value.startsWith("prefix_") && value.endsWith("_suffix") && value.length() > 10;
		};
		
		assertTrue(complexRule.match(createToken("prefix_middle_suffix")));
		assertTrue(complexRule.match(createToken("prefix_suffix")));
		assertFalse(complexRule.match(createToken("middle_suffix")));
		assertFalse(complexRule.match(createToken("prefix_middle")));
	}
	
	@Test
	void functionalInterfaceWithMethodReference() {
		NegatableTokenRule methodRefRule = token -> {
			try {
				Integer.parseInt(token.value());
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		};
		
		assertTrue(methodRefRule.match(createToken("123")));
		assertTrue(methodRefRule.match(createToken("0")));
		assertFalse(methodRefRule.match(createToken("abc")));
		assertFalse(methodRefRule.match(createToken("12a")));
	}
}
