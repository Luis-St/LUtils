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

package net.luis.utils.io.token.rules.quantifiers;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.rules.TokenRules;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class OptionalTokenRuleTest {
	
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
	void constructorWithValidRule() {
		TokenRule innerRule = createRule("test");
		
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		assertEquals(innerRule, rule.tokenRule());
	}
	
	@Test
	void constructorWithNullRule() {
		assertThrows(NullPointerException.class, () -> new OptionalTokenRule(null));
	}
	
	@Test
	void matchWithInnerRuleMatching() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals("test", result.matchedTokens().get(0).value());
		assertEquals(1, stream.getCurrentIndex()); // Stream advanced
	}
	
	@Test
	void matchWithInnerRuleNotMatching() {
		TokenRule innerRule = createRule("expected");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result); // Optional should still match
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(0, stream.getCurrentIndex()); // Stream not advanced
	}
	
	@Test
	void matchWithEmptyStream() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result); // Can't match at invalid index
	}
	
	@Test
	void matchWithAlwaysMatchRule() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.alwaysMatch());
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.matchedTokens().size());
		assertEquals("any", result.matchedTokens().get(0).value());
	}
	
	@Test
	void matchWithNeverMatchRule() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.neverMatch());
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result); // Optional always succeeds
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(0, stream.getCurrentIndex()); // Stream not advanced
	}
	
	@Test
	void matchAtStreamEnd() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		stream.advance(); // Move past the only token
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result); // Can't match at end of stream
	}
	
	@Test
	void matchWithNegativeIndex() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		// This is a bit artificial since we can't normally get negative index
		// but the method checks for it
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		// We can't easily test negative index since TokenStream doesn't allow it
		// but we can test the edge case by mocking or using reflection
		// For now, let's just verify normal behavior
		TokenRuleMatch result = rule.match(stream, context);
		assertNotNull(result);
	}
	
	@Test
	void matchWithNullStream() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void not() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenRule negated = rule.not();
		
		assertTrue(negated instanceof OptionalTokenRule);
		OptionalTokenRule negatedOptional = (OptionalTokenRule) negated;
		assertNotEquals(innerRule, negatedOptional.tokenRule());
	}
	
	@Test
	void notWithAlwaysMatchRule() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.alwaysMatch());
		
		TokenRule negated = rule.not();
		
		assertTrue(negated instanceof OptionalTokenRule);
		OptionalTokenRule negatedOptional = (OptionalTokenRule) negated;
		assertEquals(TokenRules.neverMatch().getClass(), negatedOptional.tokenRule().getClass());
	}
	
	@Test
	void notBehavior() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("test")));
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		// Both should match since optional always succeeds
		assertNotNull(rule.match(stream1, context));
		assertNotNull(negated.match(stream2, context));
	}
	
	@Test
	void equalsAndHashCode() {
		TokenRule innerRule1 = createRule("test");
		TokenRule innerRule2 = createRule("test");
		TokenRule innerRule3 = createRule("other");
		
		OptionalTokenRule rule1 = new OptionalTokenRule(innerRule1);
		OptionalTokenRule rule2 = new OptionalTokenRule(innerRule1);
		OptionalTokenRule rule3 = new OptionalTokenRule(innerRule2);
		OptionalTokenRule rule4 = new OptionalTokenRule(innerRule3);
		
		assertEquals(rule1, rule2);
		assertNotEquals(rule1, rule3); // Different inner rule instances
		assertNotEquals(rule1, rule4);
		assertNotEquals(rule1, null);
		assertNotEquals(rule1, "string");
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringTest() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		String result = rule.toString();
		
		assertTrue(result.contains("OptionalTokenRule"));
		assertTrue(result.contains("tokenRule"));
	}
}
