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

import net.luis.utils.io.token.rules.assertions.*;
import net.luis.utils.io.token.rules.assertions.anchors.EndTokenRule;
import net.luis.utils.io.token.rules.assertions.anchors.StartTokenRule;
import net.luis.utils.io.token.rules.combinators.*;
import net.luis.utils.io.token.rules.core.LookMatchMode;
import net.luis.utils.io.token.rules.core.ReferenceType;
import net.luis.utils.io.token.rules.matchers.*;
import net.luis.utils.io.token.rules.quantifiers.OptionalTokenRule;
import net.luis.utils.io.token.rules.quantifiers.RepeatedTokenRule;
import net.luis.utils.io.token.rules.reference.*;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRules}.<br>
 *
 * @author Luis-St
 */
class TokenRulesTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void alwaysMatch() {
		TokenRule result = TokenRules.alwaysMatch();
		
		assertSame(AlwaysMatchTokenRule.INSTANCE, result);
	}
	
	@Test
	void neverMatch() {
		TokenRule result = TokenRules.neverMatch();
		
		assertSame(NeverMatchTokenRule.INSTANCE, result);
	}
	
	@Test
	void valueWithNullString() {
		assertThrows(NullPointerException.class, () -> TokenRules.value(null, false));
	}
	
	@Test
	void valueWithCharAndIgnoreCase() {
		TokenRule result = TokenRules.value('a', true);
		
		ValueTokenRule valueRule = assertInstanceOf(ValueTokenRule.class, result);
		assertEquals("a", valueRule.value());
		assertTrue(valueRule.ignoreCase());
	}
	
	@Test
	void valueWithCharAndCaseSensitive() {
		TokenRule result = TokenRules.value('A', false);
		
		ValueTokenRule valueRule = assertInstanceOf(ValueTokenRule.class, result);
		assertEquals("A", valueRule.value());
		assertFalse(valueRule.ignoreCase());
	}
	
	@Test
	void valueWithStringAndIgnoreCase() {
		TokenRule result = TokenRules.value("test", true);
		
		ValueTokenRule valueRule = assertInstanceOf(ValueTokenRule.class, result);
		assertEquals("test", valueRule.value());
		assertTrue(valueRule.ignoreCase());
	}
	
	@Test
	void valueWithStringAndCaseSensitive() {
		TokenRule result = TokenRules.value("TEST", false);
		
		ValueTokenRule valueRule = assertInstanceOf(ValueTokenRule.class, result);
		assertEquals("TEST", valueRule.value());
		assertFalse(valueRule.ignoreCase());
	}
	
	@Test
	void patternWithStringNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.pattern((String) null));
	}
	
	@Test
	void patternWithString() {
		TokenRule result = TokenRules.pattern("\\d+");
		
		PatternTokenRule patternRule = assertInstanceOf(PatternTokenRule.class, result);
		assertEquals("\\d+", patternRule.pattern().pattern());
	}
	
	@Test
	void patternWithPatternNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.pattern((Pattern) null));
	}
	
	@Test
	void patternWithPatternObject() {
		Pattern pattern = Pattern.compile("\\w+");
		
		TokenRule result = TokenRules.pattern(pattern);
		
		PatternTokenRule patternRule = assertInstanceOf(PatternTokenRule.class, result);
		assertEquals(pattern, patternRule.pattern());
	}
	
	@Test
	void minLengthNegative() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.minLength(-1));
	}
	
	@Test
	void minLength() {
		TokenRule result = TokenRules.minLength(5);
		
		LengthTokenRule lengthRule = assertInstanceOf(LengthTokenRule.class, result);
		assertEquals(5, lengthRule.minLength());
		assertEquals(Integer.MAX_VALUE, lengthRule.maxLength());
	}
	
	@Test
	void exactLengthNegative() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.exactLength(-1));
	}
	
	@Test
	void exactLength() {
		TokenRule result = TokenRules.exactLength(3);
		
		LengthTokenRule lengthRule = assertInstanceOf(LengthTokenRule.class, result);
		assertEquals(3, lengthRule.minLength());
		assertEquals(3, lengthRule.maxLength());
	}
	
	@Test
	void maxLengthNegative() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.maxLength(-1));
	}
	
	@Test
	void maxLength() {
		TokenRule result = TokenRules.maxLength(10);
		
		LengthTokenRule lengthRule = assertInstanceOf(LengthTokenRule.class, result);
		assertEquals(0, lengthRule.minLength());
		assertEquals(10, lengthRule.maxLength());
	}
	
	@Test
	void lengthBetweenInvalidRange() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.lengthBetween(8, 2));
	}
	
	@Test
	void lengthBetweenNegativeMin() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.lengthBetween(-1, 5));
	}
	
	@Test
	void lengthBetweenNegativeMax() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.lengthBetween(0, -1));
	}
	
	@Test
	void lengthBetween() {
		TokenRule result = TokenRules.lengthBetween(2, 8);
		
		LengthTokenRule lengthRule = assertInstanceOf(LengthTokenRule.class, result);
		assertEquals(2, lengthRule.minLength());
		assertEquals(8, lengthRule.maxLength());
	}
	
	@Test
	void optionalWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.optional(null));
	}
	
	@Test
	void optional() {
		TokenRule innerRule = TokenRules.alwaysMatch();
		
		TokenRule result = TokenRules.optional(innerRule);
		
		OptionalTokenRule optionalRule = assertInstanceOf(OptionalTokenRule.class, result);
		assertEquals(innerRule, optionalRule.tokenRule());
	}
	
	@Test
	void atLeastWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.atLeast(null, 2));
	}
	
	@Test
	void atLeastWithNegativeCount() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.atLeast(TokenRules.alwaysMatch(), -1));
	}
	
	@Test
	void atLeast() {
		TokenRule innerRule = TokenRules.alwaysMatch();
		
		TokenRule result = TokenRules.atLeast(innerRule, 2);
		
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class, result);
		assertEquals(innerRule, repeatedRule.tokenRule());
		assertEquals(2, repeatedRule.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeatedRule.maxOccurrences());
	}
	
	@Test
	void exactlyWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.exactly(null, 3));
	}
	
	@Test
	void exactlyWithNegativeCount() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.exactly(TokenRules.alwaysMatch(), -1));
	}
	
	@Test
	void exactly() {
		TokenRule innerRule = TokenRules.alwaysMatch();
		
		TokenRule result = TokenRules.exactly(innerRule, 3);
		
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class, result);
		assertEquals(innerRule, repeatedRule.tokenRule());
		assertEquals(3, repeatedRule.minOccurrences());
		assertEquals(3, repeatedRule.maxOccurrences());
	}
	
	@Test
	void atMostWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.atMost(null, 5));
	}
	
	@Test
	void atMostWithNegativeCount() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.atMost(TokenRules.alwaysMatch(), -1));
	}
	
	@Test
	void atMost() {
		TokenRule innerRule = TokenRules.alwaysMatch();
		
		TokenRule result = TokenRules.atMost(innerRule, 5);
		
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class, result);
		assertEquals(innerRule, repeatedRule.tokenRule());
		assertEquals(0, repeatedRule.minOccurrences());
		assertEquals(5, repeatedRule.maxOccurrences());
	}
	
	@Test
	void zeroOrMoreWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.zeroOrMore(null));
	}
	
	@Test
	void zeroOrMore() {
		TokenRule innerRule = TokenRules.alwaysMatch();
		
		TokenRule result = TokenRules.zeroOrMore(innerRule);
		
		assertInstanceOf(RepeatedTokenRule.class, result);
		RepeatedTokenRule repeatedRule = (RepeatedTokenRule) result;
		assertEquals(innerRule, repeatedRule.tokenRule());
		assertEquals(0, repeatedRule.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeatedRule.maxOccurrences());
	}
	
	@Test
	void betweenWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.between(null, 2, 5));
	}
	
	@Test
	void betweenWithInvalidRange() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.between(TokenRules.alwaysMatch(), 5, 2));
	}
	
	@Test
	void betweenWithNegativeMin() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.between(TokenRules.alwaysMatch(), -1, 5));
	}
	
	@Test
	void betweenWithBothZero() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.between(TokenRules.alwaysMatch(), 0, 0));
	}
	
	@Test
	void between() {
		TokenRule innerRule = TokenRules.alwaysMatch();
		
		TokenRule result = TokenRules.between(innerRule, 2, 5);
		
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class, result);
		assertEquals(innerRule, repeatedRule.tokenRule());
		assertEquals(2, repeatedRule.minOccurrences());
		assertEquals(5, repeatedRule.maxOccurrences());
	}
	
	@Test
	void sequenceWithVarArgsNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.sequence((TokenRule[]) null));
	}
	
	@Test
	void sequenceWithVarArgsEmpty() {
		assertThrows(IllegalArgumentException.class, TokenRules::sequence);
	}
	
	@Test
	void sequenceWithVarArgsContainingNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.sequence(TokenRules.alwaysMatch(), null));
	}
	
	@Test
	void sequenceWithVarArgs() {
		TokenRule rule1 = TokenRules.alwaysMatch();
		TokenRule rule2 = TokenRules.neverMatch();
		
		TokenRule result = TokenRules.sequence(rule1, rule2);
		
		SequenceTokenRule sequenceRule = assertInstanceOf(SequenceTokenRule.class, result);
		assertEquals(List.of(rule1, rule2), sequenceRule.tokenRules());
	}
	
	@Test
	void sequenceWithListNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.sequence((List<TokenRule>) null));
	}
	
	@Test
	void sequenceWithEmptyList() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.sequence(List.of()));
	}
	
	@Test
	void sequenceWithList() {
		TokenRule rule1 = TokenRules.alwaysMatch();
		TokenRule rule2 = TokenRules.neverMatch();
		List<TokenRule> rules = List.of(rule1, rule2);
		
		TokenRule result = TokenRules.sequence(rules);
		
		SequenceTokenRule sequenceRule = assertInstanceOf(SequenceTokenRule.class, result);
		assertEquals(rules, sequenceRule.tokenRules());
	}
	
	@Test
	void anyWithVarArgsNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.any((TokenRule[]) null));
	}
	
	@Test
	void anyWithVarArgsEmpty() {
		assertThrows(IllegalArgumentException.class, TokenRules::any);
	}
	
	@Test
	void anyWithVarArgsContainingNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.any(TokenRules.alwaysMatch(), null));
	}
	
	@Test
	void anyWithVarArgs() {
		TokenRule rule1 = TokenRules.alwaysMatch();
		TokenRule rule2 = TokenRules.neverMatch();
		
		TokenRule result = TokenRules.any(rule1, rule2);
		
		AnyOfTokenRule anyRule = assertInstanceOf(AnyOfTokenRule.class, result);
		assertEquals(List.of(rule1, rule2), anyRule.tokenRules());
	}
	
	@Test
	void anyWithListNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.any((List<TokenRule>) null));
	}
	
	@Test
	void anyWithEmptyList() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.any(List.of()));
	}
	
	@Test
	void anyWithList() {
		TokenRule rule1 = TokenRules.alwaysMatch();
		TokenRule rule2 = TokenRules.neverMatch();
		List<TokenRule> rules = List.of(rule1, rule2);
		
		TokenRule result = TokenRules.any(rules);
		
		AnyOfTokenRule anyRule = assertInstanceOf(AnyOfTokenRule.class, result);
		assertEquals(rules, anyRule.tokenRules());
	}
	
	@Test
	void boundaryTwoParametersNullStart() {
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(null, TokenRules.value(")", false)));
	}
	
	@Test
	void boundaryTwoParametersNullEnd() {
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(TokenRules.value("(", false), null));
	}
	
	@Test
	void boundaryTwoParameters() {
		TokenRule start = TokenRules.value("(", false);
		TokenRule end = TokenRules.value(")", false);
		
		TokenRule result = TokenRules.boundary(start, end);
		
		BoundaryTokenRule boundaryRule = assertInstanceOf(BoundaryTokenRule.class, result);
		assertEquals(start, boundaryRule.startTokenRule());
		assertEquals(end, boundaryRule.endTokenRule());
		assertEquals(TokenRules.alwaysMatch().getClass(), boundaryRule.betweenTokenRule().getClass());
	}
	
	@Test
	void boundaryThreeParametersNullStart() {
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(null, TokenRules.alwaysMatch(), TokenRules.value(")", false)));
	}
	
	@Test
	void boundaryThreeParametersNullBetween() {
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(TokenRules.value("(", false), null, TokenRules.value(")", false)));
	}
	
	@Test
	void boundaryThreeParametersNullEnd() {
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(TokenRules.value("(", false), TokenRules.alwaysMatch(), null));
	}
	
	@Test
	void boundaryThreeParameters() {
		TokenRule start = TokenRules.value("(", false);
		TokenRule between = TokenRules.value("content", false);
		TokenRule end = TokenRules.value(")", false);
		
		TokenRule result = TokenRules.boundary(start, between, end);
		
		BoundaryTokenRule boundaryRule = assertInstanceOf(BoundaryTokenRule.class, result);
		assertEquals(start, boundaryRule.startTokenRule());
		assertEquals(between, boundaryRule.betweenTokenRule());
		assertEquals(end, boundaryRule.endTokenRule());
	}
	
	@Test
	void recursiveWithNullFunction() {
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(null));
	}
	
	@Test
	void recursiveWithFunction() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.any(TokenRules.value("base", false), self);
		
		TokenRule result = TokenRules.recursive(factory);
		
		assertInstanceOf(RecursiveTokenRule.class, result);
	}
	
	@Test
	void recursiveThreeParametersNullOpening() {
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(null, TokenRules.alwaysMatch(), TokenRules.value(")", false)));
	}
	
	@Test
	void recursiveThreeParametersNullContent() {
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(TokenRules.value("(", false), null, TokenRules.value(")", false)));
	}
	
	@Test
	void recursiveThreeParametersNullClosing() {
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(TokenRules.value("(", false), TokenRules.alwaysMatch(), (TokenRule) null));
	}
	
	@Test
	void recursiveThreeParameters() {
		TokenRule opening = TokenRules.value("(", false);
		TokenRule content = TokenRules.alwaysMatch();
		TokenRule closing = TokenRules.value(")", false);
		
		TokenRule result = TokenRules.recursive(opening, content, closing);
		
		assertInstanceOf(RecursiveTokenRule.class, result);
	}
	
	@Test
	void recursiveWithOpeningClosingAndFactoryNullOpening() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.alwaysMatch();
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(null, TokenRules.value(")", false), factory));
	}
	
	@Test
	void recursiveWithOpeningClosingAndFactoryNullClosing() {
		Function<TokenRule, TokenRule> factory = self -> TokenRules.alwaysMatch();
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(TokenRules.value("(", false), null, factory));
	}
	
	@Test
	void recursiveWithOpeningClosingAndFactoryNullFactory() {
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(TokenRules.value("(", false), TokenRules.value(")", false), (Function<TokenRule, TokenRule>) null));
	}
	
	@Test
	void recursiveWithOpeningClosingAndFactory() {
		TokenRule opening = TokenRules.value("(", false);
		TokenRule closing = TokenRules.value(")", false);
		Function<TokenRule, TokenRule> contentFactory = self -> TokenRules.any(TokenRules.alwaysMatch(), self);
		
		TokenRule result = TokenRules.recursive(opening, closing, contentFactory);
		
		assertInstanceOf(RecursiveTokenRule.class, result);
	}
	
	@Test
	void lazy() {
		TokenRule result = TokenRules.lazy();
		
		assertInstanceOf(LazyTokenRule.class, result);
	}
	
	@Test
	void groupWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.group(null));
	}
	
	@Test
	void group() {
		TokenRule innerRule = TokenRules.alwaysMatch();
		
		TokenRule result = TokenRules.group(innerRule);
		
		assertInstanceOf(TokenGroupRule.class, result);
		TokenGroupRule groupRule = (TokenGroupRule) result;
		assertEquals(innerRule, groupRule.tokenRule());
	}
	
	@Test
	void customWithNullCondition() {
		assertThrows(NullPointerException.class, () -> TokenRules.custom(null));
	}
	
	@Test
	void custom() {
		Predicate<Token> condition = token -> "test".equals(token.value());
		
		TokenRule result = TokenRules.custom(condition);
		
		assertInstanceOf(CustomTokeRule.class, result);
		CustomTokeRule customRule = (CustomTokeRule) result;
		assertEquals(condition, customRule.condition());
	}
	
	@Test
	void startDocument() {
		TokenRule result = TokenRules.startDocument();
		
		assertEquals(StartTokenRule.DOCUMENT, result);
	}
	
	@Test
	void startLine() {
		TokenRule result = TokenRules.startLine();
		
		assertEquals(StartTokenRule.LINE, result);
	}
	
	@Test
	void endDocument() {
		TokenRule result = TokenRules.endDocument();
		
		assertEquals(EndTokenRule.DOCUMENT, result);
	}
	
	@Test
	void endLine() {
		TokenRule result = TokenRules.endLine();
		
		assertEquals(EndTokenRule.LINE, result);
	}
	
	@Test
	void lookaheadWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.lookahead(null));
	}
	
	@Test
	void lookahead() {
		TokenRule innerRule = TokenRules.alwaysMatch();
		
		TokenRule result = TokenRules.lookahead(innerRule);
		
		assertInstanceOf(LookaheadTokenRule.class, result);
		LookaheadTokenRule lookaheadRule = (LookaheadTokenRule) result;
		assertEquals(innerRule, lookaheadRule.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, lookaheadRule.mode());
	}
	
	@Test
	void negativeLookaheadWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.negativeLookahead(null));
	}
	
	@Test
	void negativeLookahead() {
		TokenRule innerRule = TokenRules.alwaysMatch();
		
		TokenRule result = TokenRules.negativeLookahead(innerRule);
		
		assertInstanceOf(LookaheadTokenRule.class, result);
		LookaheadTokenRule lookaheadRule = (LookaheadTokenRule) result;
		assertEquals(innerRule, lookaheadRule.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, lookaheadRule.mode());
	}
	
	@Test
	void lookbehindWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.lookbehind(null));
	}
	
	@Test
	void lookbehind() {
		TokenRule innerRule = TokenRules.alwaysMatch();
		
		TokenRule result = TokenRules.lookbehind(innerRule);
		
		assertInstanceOf(LookbehindTokenRule.class, result);
		LookbehindTokenRule lookbehindRule = (LookbehindTokenRule) result;
		assertEquals(innerRule, lookbehindRule.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, lookbehindRule.mode());
	}
	
	@Test
	void negativeLookbehindWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.negativeLookbehind(null));
	}
	
	@Test
	void negativeLookbehind() {
		TokenRule innerRule = TokenRules.alwaysMatch();
		
		TokenRule result = TokenRules.negativeLookbehind(innerRule);
		
		assertInstanceOf(LookbehindTokenRule.class, result);
		LookbehindTokenRule lookbehindRule = (LookbehindTokenRule) result;
		assertEquals(innerRule, lookbehindRule.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, lookbehindRule.mode());
	}
	
	@Test
	void captureWithNullKey() {
		assertThrows(NullPointerException.class, () -> TokenRules.capture(null, TokenRules.alwaysMatch()));
	}
	
	@Test
	void captureWithEmptyKey() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.capture("", TokenRules.alwaysMatch()));
	}
	
	@Test
	void captureWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.capture("key", null));
	}
	
	@Test
	void capture() {
		String key = "testKey";
		TokenRule innerRule = TokenRules.alwaysMatch();
		
		TokenRule result = TokenRules.capture(key, innerRule);
		
		assertInstanceOf(CaptureTokenRule.class, result);
		CaptureTokenRule captureRule = (CaptureTokenRule) result;
		assertEquals(key, captureRule.key());
		assertEquals(innerRule, captureRule.tokenRule());
	}
	
	@Test
	void referenceWithNullKey() {
		assertThrows(NullPointerException.class, () -> TokenRules.reference(null));
	}
	
	@Test
	void referenceWithEmptyKey() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.reference(""));
	}
	
	@Test
	void reference() {
		String key = "testKey";
		
		TokenRule result = TokenRules.reference(key);
		
		assertInstanceOf(ReferenceTokenRule.class, result);
		ReferenceTokenRule referenceRule = (ReferenceTokenRule) result;
		assertEquals(key, referenceRule.key());
		assertEquals(ReferenceType.DYNAMIC, referenceRule.type());
	}
	
	@Test
	void referenceRuleWithNullKey() {
		assertThrows(NullPointerException.class, () -> TokenRules.referenceRule(null));
	}
	
	@Test
	void referenceRuleWithEmptyKey() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.referenceRule(""));
	}
	
	@Test
	void referenceRule() {
		String key = "testKey";
		
		TokenRule result = TokenRules.referenceRule(key);
		
		assertInstanceOf(ReferenceTokenRule.class, result);
		ReferenceTokenRule referenceRule = (ReferenceTokenRule) result;
		assertEquals(key, referenceRule.key());
		assertEquals(ReferenceType.RULE, referenceRule.type());
	}
	
	@Test
	void referenceTokensWithNullKey() {
		assertThrows(NullPointerException.class, () -> TokenRules.referenceTokens(null));
	}
	
	@Test
	void referenceTokensWithEmptyKey() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.referenceTokens(""));
	}
	
	@Test
	void referenceTokens() {
		String key = "testKey";
		
		TokenRule result = TokenRules.referenceTokens(key);
		
		assertInstanceOf(ReferenceTokenRule.class, result);
		ReferenceTokenRule referenceRule = (ReferenceTokenRule) result;
		assertEquals(key, referenceRule.key());
		assertEquals(ReferenceType.TOKENS, referenceRule.type());
	}
}
