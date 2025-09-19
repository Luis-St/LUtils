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

package net.luis.utils.io.token.rules.combinators;

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
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class RecursiveTokenRuleTest {
	
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
	void constructorWithValidFunction() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.any(createRule("base"), self);
		
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		assertNotNull(rule.getTokenRule());
	}
	
	@Test
	void constructorWithNullFunction() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule((Function<TokenRule, TokenRule>) null));
	}
	
	@Test
	void constructorWithFunctionReturningNull() {
		Function<TokenRule, TokenRule> factory = self -> null;
		
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(factory));
	}
	
	@Test
	void constructorThreeParameters() {
		TokenRule opening = createRule("(");
		TokenRule content = createRule("content");
		TokenRule closing = createRule(")");
		
		RecursiveTokenRule rule = new RecursiveTokenRule(opening, content, closing);
		
		assertNotNull(rule.getTokenRule());
	}
	
	@Test
	void constructorThreeParametersNullOpening() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(null, createRule("content"), createRule(")")));
	}
	
	@Test
	void constructorThreeParametersNullContent() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("("), null, createRule(")")));
	}
	
	@Test
	void constructorThreeParametersNullClosing() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("("), createRule("content"), (TokenRule) null));
	}
	
	@Test
	void constructorWithOpeningClosingAndFactory() {
		TokenRule opening = createRule("(");
		TokenRule closing = createRule(")");
		Function<TokenRule, TokenRule> contentFactory = self -> TokenRules.any(createRule("base"), self);
		
		RecursiveTokenRule rule = new RecursiveTokenRule(opening, closing, contentFactory);
		
		assertNotNull(rule.getTokenRule());
	}
	
	@Test
	void constructorWithOpeningClosingAndFactoryNullOpening() {
		Function<TokenRule, TokenRule> factory = self -> createRule("content");
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(null, createRule(")"), factory));
	}
	
	@Test
	void constructorWithOpeningClosingAndFactoryNullClosing() {
		Function<TokenRule, TokenRule> factory = self -> createRule("content");
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("("), null, factory));
	}
	
	@Test
	void constructorWithOpeningClosingAndFactoryNullFactory() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("("), createRule(")"), (Function<TokenRule, TokenRule>) null));
	}
	
	@Test
	void getTokenRule() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenRule tokenRule = rule.getTokenRule();
		
		assertNotNull(tokenRule);
	}
	
	@Test
	void matchWithSimpleRecursion() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.any(
			createRule("base"),
			TokenRules.sequence(createRule("("), self, createRule(")"))
		);
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("base")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("base", result.matchedTokens().get(0).value());
	}
	
	@Test
	void matchWithNestedRecursion() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.any(
			createRule("base"),
			TokenRules.sequence(createRule("("), self, createRule(")"))
		);
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("("), createToken("base"), createToken(")")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(3, result.matchedTokens().size());
		assertEquals("(", result.matchedTokens().get(0).value());
		assertEquals("base", result.matchedTokens().get(1).value());
		assertEquals(")", result.matchedTokens().get(2).value());
	}
	
	@Test
	void matchWithDeeplyNestedRecursion() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.any(
			createRule("base"),
			TokenRules.sequence(createRule("("), self, createRule(")"))
		);
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("("), createToken("("), createToken("base"), createToken(")"), createToken(")")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(5, result.matchedTokens().size());
		assertEquals("(", result.matchedTokens().get(0).value());
		assertEquals("(", result.matchedTokens().get(1).value());
		assertEquals("base", result.matchedTokens().get(2).value());
		assertEquals(")", result.matchedTokens().get(3).value());
		assertEquals(")", result.matchedTokens().get(4).value());
	}
	
	@Test
	void matchWithNoMatch() {
		Function<TokenRule, TokenRule> factory = self -> createRule("expected");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithEmptyStream() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithNullStream() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void not() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenRule negated = rule.not();
		
		assertTrue(negated instanceof RecursiveTokenRule);
		RecursiveTokenRule negatedRecursive = (RecursiveTokenRule) negated;
		assertEquals(rule, negatedRecursive.getTokenRule());
	}
	
	@Test
	void notDoubleNegation() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void equalsWithSameTokenRule() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule1 = new RecursiveTokenRule(factory);
		RecursiveTokenRule rule2 = new RecursiveTokenRule(factory);
		
		// They won't be equal since they have different inner token rules created by the factory
		assertNotEquals(rule1, rule2);
	}
	
	@Test
	void equalsWithSameInstance() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		assertEquals(rule, rule);
	}
	
	@Test
	void equalsWithNull() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		assertNotEquals(rule, null);
	}
	
	@Test
	void equalsWithDifferentType() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		assertNotEquals(rule, "string");
	}
	
	@Test
	void hashCodeConsistency() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		int hashCode1 = rule.hashCode();
		int hashCode2 = rule.hashCode();
		
		assertEquals(hashCode1, hashCode2);
	}
	
	@Test
	void toStringTest() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		String result = rule.toString();
		
		assertTrue(result.contains("RecursiveTokenRule"));
		assertTrue(result.contains("tokenRule"));
	}
}
