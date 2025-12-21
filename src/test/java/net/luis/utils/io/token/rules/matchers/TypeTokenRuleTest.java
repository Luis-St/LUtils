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
import net.luis.utils.io.token.type.StandardTokenType;
import net.luis.utils.io.token.type.TokenType;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TypeTokenRule}.<br>
 *
 * @author Luis-St
 */
class TypeTokenRuleTest {
	
	private static @NonNull Token createToken(@NonNull String value, @NonNull Set<TokenType> types) {
		Token token = SimpleToken.createUnpositioned(value);
		token.types().addAll(types);
		return token;
	}
	
	@Test
	void constructorWithNullTokenTypes() {
		assertThrows(NullPointerException.class, () -> new TypeTokenRule(null));
	}
	
	@Test
	void constructorWithEmptyTokenTypes() {
		assertThrows(IllegalArgumentException.class, () -> new TypeTokenRule(Set.of()));
	}
	
	@Test
	void constructorWithSingleTokenType() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		
		assertEquals(1, rule.tokenTypes().size());
		assertTrue(rule.tokenTypes().contains(StandardTokenType.KEYWORD));
	}
	
	@Test
	void constructorWithMultipleTokenTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER));
		
		assertEquals(3, rule.tokenTypes().size());
		assertTrue(rule.tokenTypes().containsAll(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER)));
	}
	
	@Test
	void constructorWithTwoTokenTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		
		assertEquals(2, rule.tokenTypes().size());
		assertTrue(rule.tokenTypes().contains(StandardTokenType.KEYWORD));
		assertTrue(rule.tokenTypes().contains(StandardTokenType.IDENTIFIER));
	}
	
	@Test
	void matchWithNullTokenStream() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test", Set.of(StandardTokenType.KEYWORD))));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyTokenStream() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithTokenStreamAndContext() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test", Set.of(StandardTokenType.KEYWORD))));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals("test", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithTokenStreamNoMatch() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test", Set.of(StandardTokenType.IDENTIFIER))));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchAtDifferentStreamPosition() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.IDENTIFIER));
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("first", Set.of(StandardTokenType.KEYWORD)),
			createToken("second", Set.of(StandardTokenType.IDENTIFIER)),
			createToken("third", Set.of(StandardTokenType.NUMBER))
		));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals("second", result.matchedTokens().getFirst().value());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithMultipleTokensInStream() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("first", Set.of(StandardTokenType.IDENTIFIER)),
			createToken("second", Set.of(StandardTokenType.KEYWORD)),
			createToken("third", Set.of(StandardTokenType.KEYWORD))
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSecondTokenMatching() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.IDENTIFIER));
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("first", Set.of(StandardTokenType.KEYWORD)),
			createToken("second", Set.of(StandardTokenType.IDENTIFIER)),
			createToken("third", Set.of(StandardTokenType.NUMBER))
		));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals("second", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithThirdTokenMatching() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.NUMBER));
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("first", Set.of(StandardTokenType.KEYWORD)),
			createToken("second", Set.of(StandardTokenType.IDENTIFIER)),
			createToken("third", Set.of(StandardTokenType.NUMBER))
		));
		stream.advance();
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(2, result.startIndex());
		assertEquals(3, result.endIndex());
		assertEquals("third", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithNullToken() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithSingleTypeExactMatch() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD));
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithSingleTypeNoMatch() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		Token token = createToken("test", Set.of(StandardTokenType.IDENTIFIER));
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithMultipleTypesExactMatch() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithMultipleTypesPartialMatch() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD));
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithMultipleTypesNoMatch() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		Token token = createToken("test", Set.of(StandardTokenType.NUMBER, StandardTokenType.OPERATOR));
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithTokenHavingAdditionalTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER));
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithMultipleRequiredTypesAndAdditionalTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER, StandardTokenType.OPERATOR));
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithTokenHavingOneOfTwoRequiredTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD, StandardTokenType.NUMBER));
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithTokenHavingNoneOfRequiredTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		Token token = createToken("test", Set.of(StandardTokenType.NUMBER));
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithThreeRequiredTypesExactMatch() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER));
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithThreeRequiredTypesTwoPresent() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithThreeRequiredTypesOnePresent() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD));
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithThreeRequiredTypesAndAdditionalType() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER, StandardTokenType.OPERATOR));
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithEmptyTokenTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		Token token = createToken("test", Set.of());
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithDifferentOrderOfTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER));
		Token token = createToken("test", Set.of(StandardTokenType.NUMBER, StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithSingleRequiredTypeAndMultipleTokenTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.IDENTIFIER));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER, StandardTokenType.OPERATOR));
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithSubsetOfTokenTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER, StandardTokenType.OPERATOR));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchWithSupersetOfTokenTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER, StandardTokenType.OPERATOR));
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithIntersectingButInsufficientTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER));
		Token token = createToken("test", Set.of(StandardTokenType.KEYWORD, StandardTokenType.OPERATOR));
		
		boolean result = rule.match(token);
		
		assertFalse(result);
	}
	
	@Test
	void matchMultipleTimesWithSameRule() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		Token token1 = createToken("test1", Set.of(StandardTokenType.KEYWORD));
		Token token2 = createToken("test2", Set.of(StandardTokenType.IDENTIFIER));
		Token token3 = createToken("test3", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		
		assertTrue(rule.match(token1));
		assertFalse(rule.match(token2));
		assertTrue(rule.match(token3));
	}
	
	@Test
	void matchWithDifferentValuesSameTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		Token token1 = createToken("value1", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		Token token2 = createToken("value2", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		Token token3 = createToken("completely different", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		
		assertTrue(rule.match(token1));
		assertTrue(rule.match(token2));
		assertTrue(rule.match(token3));
	}
	
	@Test
	void matchWithEmptyStringValue() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		Token token = createToken("", Set.of(StandardTokenType.KEYWORD));
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithLongStringValue() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		String longValue = "a".repeat(1000);
		Token token = createToken(longValue, Set.of(StandardTokenType.KEYWORD));
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithSpecialCharactersValue() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		Token token = createToken("!@#$%^&*()", Set.of(StandardTokenType.KEYWORD));
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void matchWithUnicodeValue() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		Token token = createToken("こんにちは", Set.of(StandardTokenType.KEYWORD));
		
		boolean result = rule.match(token);
		
		assertTrue(result);
	}
	
	@Test
	void not() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void notBehavior() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("test1", Set.of(StandardTokenType.KEYWORD))));
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("test2", Set.of(StandardTokenType.IDENTIFIER))));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNotNull(rule.match(stream1, context));
		assertNull(negated.match(stream1.copyFromZero(), context));
		
		assertNull(rule.match(stream2, context));
		assertNotNull(negated.match(stream2.copyFromZero(), context));
	}
	
	@Test
	void notWithMultipleTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("test1", Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER))));
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("test2", Set.of(StandardTokenType.KEYWORD))));
		TokenStream stream3 = TokenStream.createMutable(List.of(createToken("test3", Set.of(StandardTokenType.NUMBER))));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNotNull(rule.match(stream1, context));
		assertNull(negated.match(stream1.copyFromZero(), context));
		
		assertNull(rule.match(stream2, context));
		assertNotNull(negated.match(stream2.copyFromZero(), context));
		
		assertNull(rule.match(stream3, context));
		assertNotNull(negated.match(stream3.copyFromZero(), context));
	}
	
	@Test
	void notWithDoubleNegation() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void toStringTest() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER));
		
		String result = rule.toString();
		
		assertTrue(result.contains("TypeTokenRule"));
		assertTrue(result.contains("tokenTypes"));
	}
	
	@Test
	void toStringWithSingleType() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD));
		
		String result = rule.toString();
		
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}
	
	@Test
	void toStringWithMultipleTypes() {
		TypeTokenRule rule = new TypeTokenRule(Set.of(StandardTokenType.KEYWORD, StandardTokenType.IDENTIFIER, StandardTokenType.NUMBER));
		
		String result = rule.toString();
		
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}
}
