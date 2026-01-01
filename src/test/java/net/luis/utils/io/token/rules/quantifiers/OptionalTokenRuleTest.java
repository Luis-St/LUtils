/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OptionalTokenRule}.<br>
 *
 * @author Luis-St
 */
class OptionalTokenRuleTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static @NonNull TokenRule createRule(@NonNull String value) {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NonNull TokenStream stream, @NonNull TokenRuleContext ctx) {
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
	void constructorWithNullRule() {
		assertThrows(NullPointerException.class, () -> new OptionalTokenRule(null));
	}
	
	@Test
	void constructorWithValidRule() {
		TokenRule innerRule = createRule("test");
		
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		assertEquals(innerRule, rule.tokenRule());
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
	void matchWithEmptyStream() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
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
		assertEquals("test", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithInnerRuleNotMatching() {
		TokenRule innerRule = createRule("expected");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchAtDifferentStreamPosition() {
		TokenRule innerRule = createRule("target");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("target"), createToken("last")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals("target", result.matchedTokens().getFirst().value());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchAtDifferentStreamPositionNoMatch() {
		TokenRule innerRule = createRule("expected");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("actual"), createToken("last")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(1, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSequenceInnerRule() {
		TokenRule innerRule = TokenRules.sequence(createRule("first"), createRule("second"));
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second"), createToken("third")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals("first", result.matchedTokens().get(0).value());
		assertEquals("second", result.matchedTokens().get(1).value());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSequenceInnerRulePartialMatch() {
		TokenRule innerRule = TokenRules.sequence(createRule("first"), createRule("missing"));
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithAnyOfInnerRule() {
		TokenRule innerRule = TokenRules.any(createRule("option1"), createRule("option2"));
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("option2"), createToken("rest")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals("option2", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithAnyOfInnerRuleNoMatch() {
		TokenRule innerRule = TokenRules.any(createRule("option1"), createRule("option2"));
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("option3"), createToken("rest")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithPatternInnerRule() {
		TokenRule innerRule = TokenRules.pattern("\\d+");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("123"), createToken("abc")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals("123", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithPatternInnerRuleNoMatch() {
		TokenRule innerRule = TokenRules.pattern("\\d+");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("abc"), createToken("123")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithNestedOptionalRule() {
		TokenRule innerInnerRule = createRule("target");
		TokenRule innerRule = new OptionalTokenRule(innerInnerRule);
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("target")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals("target", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithNestedOptionalRuleNoMatch() {
		TokenRule innerInnerRule = createRule("expected");
		TokenRule innerRule = new OptionalTokenRule(innerInnerRule);
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(innerRule, result.matchingTokenRule());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithZeroWidthInnerRule() {
		TokenRule innerRule = TokenRules.startDocument();
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("content")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithZeroWidthInnerRuleNoMatch() {
		TokenRule innerRule = TokenRules.startDocument();
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("content")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithComplexInnerRuleMatching() {
		TokenRule innerRule = TokenRules.sequence(
			TokenRules.value("if", false),
			TokenRules.value("(", false),
			TokenRules.pattern("\\w+"),
			TokenRules.value(")", false)
		);
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("if"), createToken("("), createToken("condition"), createToken(")"), createToken("then")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(4, result.endIndex());
		assertEquals(4, result.matchedTokens().size());
		assertEquals("if", result.matchedTokens().get(0).value());
		assertEquals("(", result.matchedTokens().get(1).value());
		assertEquals("condition", result.matchedTokens().get(2).value());
		assertEquals(")", result.matchedTokens().get(3).value());
		assertEquals(4, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithComplexInnerRuleNotMatching() {
		TokenRule innerRule = TokenRules.sequence(
			TokenRules.value("if", false),
			TokenRules.value("(", false),
			TokenRules.pattern("\\w+"),
			TokenRules.value(")", false)
		);
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("while"), createToken("("), createToken("condition"), createToken(")"), createToken("do")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithAlwaysMatchRule() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.alwaysMatch());
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.matchedTokens().size());
		assertEquals("any", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithNeverMatchRule() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.neverMatch());
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchAtStreamEnd() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithNegativeIndex() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		assertNotNull(result);
	}
	
	@Test
	void not() {
		TokenRule innerRule = TokenRules.pattern("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenRule negated = rule.not();
		
		OptionalTokenRule negatedOptional = assertInstanceOf(OptionalTokenRule.class, negated);
		assertNotEquals(innerRule, negatedOptional.tokenRule());
	}
	
	@Test
	void notBehavior() {
		TokenRule innerRule = TokenRules.pattern("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("test")));
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNotNull(rule.match(stream1, context));
		assertNotNull(negated.match(stream2, context));
	}
	
	@Test
	void notWithAlwaysMatchRule() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.alwaysMatch());
		
		TokenRule negated = rule.not();
		
		assertInstanceOf(OptionalTokenRule.class, negated);
		OptionalTokenRule negatedOptional = (OptionalTokenRule) negated;
		assertEquals(TokenRules.neverMatch().getClass(), negatedOptional.tokenRule().getClass());
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
