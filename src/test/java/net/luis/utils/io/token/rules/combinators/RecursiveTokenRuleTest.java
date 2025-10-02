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

/**
 * Test class for {@link RecursiveTokenRule}.<br>
 *
 * @author Luis-St
 */
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
	void constructorWithNullFunction() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(null));
	}
	
	@Test
	void constructorWithFunctionReturningNull() {
		Function<TokenRule, TokenRule> factory = self -> null;
		
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(factory));
	}
	
	@Test
	void constructorWithValidFunction() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.any(createRule("base"), self);
		
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		assertNotNull(rule.getTokenRule());
	}
	
	@Test
	void constructorWithNullOpening() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(null, createRule("content"), createRule(")")));
	}
	
	@Test
	void constructorWithNullContent() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("("), null, createRule(")")));
	}
	
	@Test
	void constructorWithNullClosing() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("("), createRule("content"), (TokenRule) null));
	}
	
	@Test
	void constructorWithFactoryAndNullOpening() {
		Function<TokenRule, TokenRule> factory = self -> createRule("content");
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(null, createRule(")"), factory));
	}
	
	@Test
	void constructorWithFactoryAndNullClosing() {
		Function<TokenRule, TokenRule> factory = self -> createRule("content");
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("("), null, factory));
	}
	
	@Test
	void constructorWithNullFactory() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("("), createRule(")"), (Function<TokenRule, TokenRule>) null));
	}
	
	@Test
	void constructorWithFactory() {
		TokenRule opening = createRule("(");
		TokenRule content = createRule("content");
		TokenRule closing = createRule(")");
		
		RecursiveTokenRule rule = new RecursiveTokenRule(opening, content, closing);
		
		assertNotNull(rule.getTokenRule());
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
	void getTokenRule() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenRule tokenRule = rule.getTokenRule();
		
		assertNotNull(tokenRule);
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
	void matchWithEmptyStream() {
		Function<TokenRule, TokenRule> factory = self -> createRule("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
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
		assertEquals("base", result.matchedTokens().getFirst().value());
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
	void matchAtDifferentStreamPosition() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.any(
			createRule("base"),
			TokenRules.sequence(createRule("("), self, createRule(")"))
		);
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("prefix"), createToken("("), createToken("base"), createToken(")"), createToken("suffix")
		));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(4, result.endIndex());
		assertEquals(3, result.matchedTokens().size());
		assertEquals("(", result.matchedTokens().get(0).value());
		assertEquals("base", result.matchedTokens().get(1).value());
		assertEquals(")", result.matchedTokens().get(2).value());
		assertEquals(4, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithAlternatingRecursivePattern() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.any(
			createRule("leaf"),
			TokenRules.sequence(createRule("A"), self, createRule("B")),
			TokenRules.sequence(createRule("X"), self, createRule("Y"))
		);
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("A"), createToken("X"), createToken("leaf"), createToken("Y"), createToken("B")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(5, result.matchedTokens().size());
		assertEquals("A", result.matchedTokens().get(0).value());
		assertEquals("X", result.matchedTokens().get(1).value());
		assertEquals("leaf", result.matchedTokens().get(2).value());
		assertEquals("Y", result.matchedTokens().get(3).value());
		assertEquals("B", result.matchedTokens().get(4).value());
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
	void matchWithPartialRecursiveMatch() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.any(
			createRule("base"),
			TokenRules.sequence(createRule("("), self, createRule(")"))
		);
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("("), createToken("base"), createToken("]")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithComplexNestedAlternatives() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.any(
			TokenRules.pattern("\\d+"),
			TokenRules.sequence(
				createRule("if"),
				TokenRules.optional(self),
				createRule("then"),
				TokenRules.optional(self)
			),
			TokenRules.sequence(createRule("("), self, createRule(")"))
		);
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("if"), createToken("("), createToken("123"), createToken(")"), createToken("then"), createToken("456")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(6, result.matchedTokens().size());
		assertEquals("if", result.matchedTokens().get(0).value());
		assertEquals("(", result.matchedTokens().get(1).value());
		assertEquals("123", result.matchedTokens().get(2).value());
		assertEquals(")", result.matchedTokens().get(3).value());
		assertEquals("then", result.matchedTokens().get(4).value());
		assertEquals("456", result.matchedTokens().get(5).value());
	}
	
	@Test
	void matchWithMultipleRecursivePaths() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self ->
			TokenRules.any(
				TokenRules.pattern("\\d+"),
				TokenRules.sequence(
					TokenRules.value("[", false),
					TokenRules.optional(self),
					TokenRules.value("]", false)
				),
				TokenRules.sequence(
					TokenRules.value("{", false),
					TokenRules.optional(self),
					TokenRules.value("}", false)
				)
			)
		);
		List<Token> tokens = List.of(
			SimpleToken.createUnpositioned("["),
			SimpleToken.createUnpositioned("{"),
			SimpleToken.createUnpositioned("123"),
			SimpleToken.createUnpositioned("}"),
			SimpleToken.createUnpositioned("]")
		);
		
		TokenStream stream = TokenStream.createMutable(tokens);
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(5, result.matchedTokens().size());
	}
	
	@Test
	void matchWithRecursiveSequenceRepeated() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.any(
			TokenRules.sequence(createRule("base"), self),
			TokenRules.sequence(createRule(","), createRule("base"))
		);
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("base"), createToken(","), createToken("base")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(3, result.matchedTokens().size());
		assertEquals("base", result.matchedTokens().get(0).value());
		assertEquals(",", result.matchedTokens().get(1).value());
		assertEquals("base", result.matchedTokens().get(2).value());
	}
	
	@Test
	void matchWithMixedRecursiveAndNonRecursive() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.any(
			TokenRules.sequence(createRule("prefix"), createRule("value")),
			TokenRules.sequence(createRule("recursive"), self, createRule("suffix"))
		);
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("recursive"), createToken("prefix"), createToken("value"), createToken("suffix")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(4, result.matchedTokens().size());
		assertEquals("recursive", result.matchedTokens().get(0).value());
		assertEquals("prefix", result.matchedTokens().get(1).value());
		assertEquals("value", result.matchedTokens().get(2).value());
		assertEquals("suffix", result.matchedTokens().get(3).value());
	}
	
	@Test
	void not() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.pattern("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenRule negated = rule.not();
		
		RecursiveTokenRule negatedRecursive = assertInstanceOf(RecursiveTokenRule.class, negated);
		assertEquals(rule, negatedRecursive.getTokenRule());
	}
	
	@Test
	void notDoubleNegation() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.pattern("test");
		RecursiveTokenRule rule = new RecursiveTokenRule(factory);
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
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
