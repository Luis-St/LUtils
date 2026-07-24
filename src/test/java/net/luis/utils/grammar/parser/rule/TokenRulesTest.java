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

package net.luis.utils.grammar.parser.rule;

import net.luis.utils.exception.NotInitializedException;
import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.rule.combinators.BoundaryTokenRule;
import net.luis.utils.grammar.parser.rule.core.ReferenceType;
import net.luis.utils.grammar.parser.rule.matchers.ValueTokenRule;
import net.luis.utils.grammar.parser.rule.reference.ReferenceTokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.*;
import net.luis.utils.grammar.token.type.StandardTokenType;
import net.luis.utils.grammar.token.type.TokenType;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRules}.<br>
 *
 * @author Luis-St
 */
class TokenRulesTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static Token typedToken(String value, TokenType... types) {
		return new SimpleToken(value, TokenPosition.UNPOSITIONED, Set.of(types));
	}
	
	private static TokenStream streamOf(String... values) {
		Token[] tokens = new Token[values.length];
		for (int i = 0; i < values.length; i++) {
			tokens[i] = token(values[i]);
		}
		return TokenStream.createMutable(List.of(tokens));
	}
	
	private static TokenStream streamOf(Token... tokens) {
		return TokenStream.createMutable(List.of(tokens));
	}
	
	@Test
	void valueWithNullString() {
		assertThrows(NullPointerException.class, () -> TokenRules.value(null, false));
	}
	
	@Test
	void valueWithEmptyString() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.value("", false));
	}
	
	@Test
	void patternStringWithNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.pattern((String) null));
	}
	
	@Test
	void patternStringWithInvalidRegex() {
		assertThrows(PatternSyntaxException.class, () -> TokenRules.pattern("["));
	}
	
	@Test
	void patternObjectWithNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.pattern((Pattern) null));
	}
	
	@Test
	void typeVarargsWithNullArray() {
		assertThrows(NullPointerException.class, () -> TokenRules.type((TokenType[]) null));
	}
	
	@Test
	void typeVarargsWithNullElement() {
		assertThrows(NullPointerException.class, () -> TokenRules.type((TokenType) null));
	}
	
	@Test
	void typeVarargsWithEmptyArray() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.type(new TokenType[0]));
	}
	
	@Test
	void typeSetWithNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.type((Set<TokenType>) null));
	}
	
	@Test
	void typeSetWithEmptySet() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.type(Set.of()));
	}
	
	@Test
	void minLengthWithNegativeValue() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.minLength(-1));
	}
	
	@Test
	void exactLengthWithNegativeValue() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.exactLength(-1));
	}
	
	@Test
	void maxLengthWithNegativeValue() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.maxLength(-1));
	}
	
	@Test
	void lengthBetweenWithNegativeMinLength() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.lengthBetween(-1, 5));
	}
	
	@Test
	void lengthBetweenWithNegativeMaxLength() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.lengthBetween(0, -1));
	}
	
	@Test
	void lengthBetweenWithMaxLessThanMinLength() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.lengthBetween(5, 2));
	}
	
	@Test
	void optionalWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.optional(null));
	}
	
	@Test
	void atLeastWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.atLeast(null, 1));
	}
	
	@Test
	void atLeastWithNegativeMin() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.atLeast(TokenRules.alwaysMatch(), -1));
	}
	
	@Test
	void exactlyWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.exactly(null, 1));
	}
	
	@Test
	void exactlyWithNegativeRepeats() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.exactly(TokenRules.alwaysMatch(), -1));
	}
	
	@Test
	void exactlyWithZeroRepeats() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.exactly(TokenRules.alwaysMatch(), 0));
	}
	
	@Test
	void atMostWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.atMost(null, 1));
	}
	
	@Test
	void atMostWithNegativeMax() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.atMost(TokenRules.alwaysMatch(), -1));
	}
	
	@Test
	void atMostWithZeroMax() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.atMost(TokenRules.alwaysMatch(), 0));
	}
	
	@Test
	void zeroOrMoreWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.zeroOrMore(null));
	}
	
	@Test
	void betweenWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.between(null, 0, 1));
	}
	
	@Test
	void betweenWithNegativeMin() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.between(TokenRules.alwaysMatch(), -1, 1));
	}
	
	@Test
	void betweenWithMaxLessThanMin() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.between(TokenRules.alwaysMatch(), 3, 1));
	}
	
	@Test
	void betweenWithBothZero() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.between(TokenRules.alwaysMatch(), 0, 0));
	}
	
	@Test
	void sequenceVarargsWithNullArray() {
		assertThrows(NullPointerException.class, () -> TokenRules.sequence((TokenRule[]) null));
	}
	
	@Test
	void sequenceVarargsWithNullElement() {
		assertThrows(NullPointerException.class, () -> TokenRules.sequence(TokenRules.alwaysMatch(), null));
	}
	
	@Test
	void sequenceVarargsWithEmptyArray() {
		assertThrows(IllegalArgumentException.class, TokenRules::sequence);
	}
	
	@Test
	void sequenceVarargsWithSingleElement() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.sequence(TokenRules.alwaysMatch()));
	}
	
	@Test
	void sequenceListWithNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.sequence((List<TokenRule>) null));
	}
	
	@Test
	void sequenceListWithNullElement() {
		assertThrows(NullPointerException.class, () -> TokenRules.sequence(Arrays.asList(TokenRules.alwaysMatch(), null)));
	}
	
	@Test
	void sequenceListWithEmptyList() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.sequence(List.of()));
	}
	
	@Test
	void sequenceListWithSingleElement() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.sequence(List.of(TokenRules.alwaysMatch())));
	}
	
	@Test
	void anyVarargsWithNullArray() {
		assertThrows(NullPointerException.class, () -> TokenRules.any((TokenRule[]) null));
	}
	
	@Test
	void anyVarargsWithNullElement() {
		assertThrows(NullPointerException.class, () -> TokenRules.any(TokenRules.alwaysMatch(), null));
	}
	
	@Test
	void anyVarargsWithEmptyArray() {
		assertThrows(IllegalArgumentException.class, TokenRules::any);
	}
	
	@Test
	void anyVarargsWithSingleElement() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.any(TokenRules.alwaysMatch()));
	}
	
	@Test
	void anyListWithNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.any((List<TokenRule>) null));
	}
	
	@Test
	void anyListWithNullElement() {
		assertThrows(NullPointerException.class, () -> TokenRules.any(Arrays.asList(TokenRules.alwaysMatch(), null)));
	}
	
	@Test
	void anyListWithEmptyList() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.any(List.of()));
	}
	
	@Test
	void anyListWithSingleElement() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.any(List.of(TokenRules.alwaysMatch())));
	}
	
	@Test
	void allVarargsWithNullArray() {
		assertThrows(NullPointerException.class, () -> TokenRules.all((TokenRule[]) null));
	}
	
	@Test
	void allVarargsWithNullElement() {
		assertThrows(NullPointerException.class, () -> TokenRules.all(TokenRules.alwaysMatch(), null));
	}
	
	@Test
	void allVarargsWithEmptyArray() {
		assertThrows(IllegalArgumentException.class, TokenRules::all);
	}
	
	@Test
	void allVarargsWithSingleElement() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.all(TokenRules.alwaysMatch()));
	}
	
	@Test
	void allListWithNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.all((List<TokenRule>) null));
	}
	
	@Test
	void allListWithNullElement() {
		assertThrows(NullPointerException.class, () -> TokenRules.all(Arrays.asList(TokenRules.alwaysMatch(), null)));
	}
	
	@Test
	void allListWithEmptyList() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.all(List.of()));
	}
	
	@Test
	void allListWithSingleElement() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.all(List.of(TokenRules.alwaysMatch())));
	}
	
	@Test
	void boundaryTwoArgWithNullStart() {
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(null, TokenRules.alwaysMatch()));
	}
	
	@Test
	void boundaryTwoArgWithNullEnd() {
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(TokenRules.alwaysMatch(), null));
	}
	
	@Test
	void boundaryThreeArgWithNullStart() {
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(null, TokenRules.alwaysMatch(), TokenRules.alwaysMatch()));
	}
	
	@Test
	void boundaryThreeArgWithNullBetween() {
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(TokenRules.alwaysMatch(), null, TokenRules.alwaysMatch()));
	}
	
	@Test
	void boundaryThreeArgWithNullEnd() {
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(TokenRules.alwaysMatch(), TokenRules.alwaysMatch(), null));
	}
	
	@Test
	void recursiveFunctionWithNullFactory() {
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(null));
	}
	
	@Test
	void recursiveFunctionWithFactoryReturningNull() {
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(_ -> null));
	}
	
	@Test
	void recursiveThreeArgWithNullOpening() {
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(null, TokenRules.alwaysMatch(), TokenRules.alwaysMatch()));
	}
	
	@Test
	void recursiveThreeArgWithNullContent() {
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(TokenRules.alwaysMatch(), null, TokenRules.alwaysMatch()));
	}
	
	@Test
	void recursiveThreeArgWithNullClosing() {
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(TokenRules.alwaysMatch(), TokenRules.alwaysMatch(), (TokenRule) null));
	}
	
	@Test
	void recursiveFactoryArgWithNullOpening() {
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(null, TokenRules.alwaysMatch(), _ -> TokenRules.alwaysMatch()));
	}
	
	@Test
	void recursiveFactoryArgWithNullClosing() {
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(TokenRules.alwaysMatch(), null, _ -> TokenRules.alwaysMatch()));
	}
	
	@Test
	void recursiveFactoryArgWithNullContentFactory() {
		assertThrows(NullPointerException.class, () -> TokenRules.recursive(TokenRules.alwaysMatch(), TokenRules.alwaysMatch(), (Function<TokenRule, TokenRule>) null));
	}
	
	@Test
	void groupWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.group(null));
	}
	
	@Test
	void customWithNullCondition() {
		assertThrows(NullPointerException.class, () -> TokenRules.custom(null));
	}
	
	@Test
	void lookaheadWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.lookahead(null));
	}
	
	@Test
	void negativeLookaheadWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.negativeLookahead(null));
	}
	
	@Test
	void lookbehindWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.lookbehind(null));
	}
	
	@Test
	void negativeLookbehindWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.negativeLookbehind(null));
	}
	
	@Test
	void captureWithNullKey() {
		assertThrows(NullPointerException.class, () -> TokenRules.capture(null, TokenRules.alwaysMatch()));
	}
	
	@Test
	void captureWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.capture("k", null));
	}
	
	@Test
	void captureWithEmptyKey() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.capture("", TokenRules.alwaysMatch()));
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
	void referenceRuleWithNullKey() {
		assertThrows(NullPointerException.class, () -> TokenRules.referenceRule(null));
	}
	
	@Test
	void referenceRuleWithEmptyKey() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.referenceRule(""));
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
	void alwaysMatchReturnsSingletonInstance() {
		assertSame(AlwaysMatchTokenRule.INSTANCE, TokenRules.alwaysMatch());
	}
	
	@Test
	void neverMatchReturnsSingletonInstance() {
		assertSame(NeverMatchTokenRule.INSTANCE, TokenRules.neverMatch());
	}
	
	@Test
	void valueCharCreatesMatchingRule() {
		TokenRule rule = TokenRules.value('a', false);
		TokenRule ignoreCaseRule = TokenRules.value('a', true);
		
		assertNotNull(rule.match(streamOf("a"), TokenRuleContext.empty()));
		assertNotNull(ignoreCaseRule.match(streamOf("A"), TokenRuleContext.empty()));
	}
	
	@Test
	void valueStringCreatesMatchingRule() {
		TokenRule rule = TokenRules.value("abc", false);
		
		assertInstanceOf(ValueTokenRule.class, rule);
		assertNotNull(rule.match(streamOf("abc"), TokenRuleContext.empty()));
	}
	
	@Test
	void patternStringCreatesMatchingRule() {
		TokenRule rule = TokenRules.pattern("[a-z]+");
		
		assertNotNull(rule.match(streamOf("abc"), TokenRuleContext.empty()));
	}
	
	@Test
	void patternObjectCreatesMatchingRule() {
		TokenRule rule = TokenRules.pattern(Pattern.compile("[a-z]+"));
		
		assertNotNull(rule.match(streamOf("abc"), TokenRuleContext.empty()));
	}
	
	@Test
	void typeVarargsCreatesRuleForNonEmptyArray() {
		TokenRule rule = TokenRules.type(StandardTokenType.KEYWORD);
		
		assertNotNull(rule.match(streamOf(typedToken("if", StandardTokenType.KEYWORD)), TokenRuleContext.empty()));
	}
	
	@Test
	void typeSetCreatesRuleForNonEmptySet() {
		TokenRule rule = TokenRules.type(Set.of(StandardTokenType.KEYWORD));
		
		assertNotNull(rule.match(streamOf(typedToken("if", StandardTokenType.KEYWORD)), TokenRuleContext.empty()));
	}
	
	@Test
	void minLengthWithZeroValue() {
		assertDoesNotThrow(() -> TokenRules.minLength(0));
	}
	
	@Test
	void exactLengthWithZeroValue() {
		assertDoesNotThrow(() -> TokenRules.exactLength(0));
	}
	
	@Test
	void maxLengthWithZeroValue() {
		assertDoesNotThrow(() -> TokenRules.maxLength(0));
	}
	
	@Test
	void lengthBetweenWithMinEqualToMax() {
		assertDoesNotThrow(() -> TokenRules.lengthBetween(3, 3));
	}
	
	@Test
	void lengthBetweenWithValidRange() {
		assertDoesNotThrow(() -> TokenRules.lengthBetween(1, 5));
	}
	
	@Test
	void optionalWithValidRule() {
		assertDoesNotThrow(() -> TokenRules.optional(TokenRules.alwaysMatch()));
	}
	
	@Test
	void atLeastWithZeroMin() {
		assertDoesNotThrow(() -> TokenRules.atLeast(TokenRules.alwaysMatch(), 0));
	}
	
	@Test
	void atLeastWithPositiveMin() {
		assertDoesNotThrow(() -> TokenRules.atLeast(TokenRules.alwaysMatch(), 2));
	}
	
	@Test
	void exactlyWithPositiveRepeats() {
		assertDoesNotThrow(() -> TokenRules.exactly(TokenRules.alwaysMatch(), 2));
	}
	
	@Test
	void atMostWithPositiveMax() {
		assertDoesNotThrow(() -> TokenRules.atMost(TokenRules.alwaysMatch(), 2));
	}
	
	@Test
	void zeroOrMoreWithValidRule() {
		assertDoesNotThrow(() -> TokenRules.zeroOrMore(TokenRules.alwaysMatch()));
	}
	
	@Test
	void betweenWithValidRange() {
		assertDoesNotThrow(() -> TokenRules.between(TokenRules.alwaysMatch(), 1, 3));
	}
	
	@Test
	void betweenWithMinEqualToMax() {
		assertDoesNotThrow(() -> TokenRules.between(TokenRules.alwaysMatch(), 2, 2));
	}
	
	@Test
	void sequenceVarargsWithTwoElements() {
		TokenRule rule = TokenRules.sequence(TokenRules.value("a", false), TokenRules.value("b", false));
		
		TokenRuleMatch match = rule.match(streamOf("a", "b"), TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(2, match.matchedTokens().size());
	}
	
	@Test
	void sequenceListWithTwoElements() {
		TokenRule rule = TokenRules.sequence(List.of(TokenRules.value("a", false), TokenRules.value("b", false)));
		
		TokenRuleMatch match = rule.match(streamOf("a", "b"), TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(2, match.matchedTokens().size());
	}
	
	@Test
	void anyVarargsWithTwoElements() {
		TokenRule rule = TokenRules.any(TokenRules.value("a", false), TokenRules.value("b", false));
		
		assertNotNull(rule.match(streamOf("b"), TokenRuleContext.empty()));
	}
	
	@Test
	void anyListWithTwoElements() {
		TokenRule rule = TokenRules.any(List.of(TokenRules.value("a", false), TokenRules.value("b", false)));
		
		assertNotNull(rule.match(streamOf("b"), TokenRuleContext.empty()));
	}
	
	@Test
	void allVarargsWithTwoElements() {
		TokenRule rule = TokenRules.all(TokenRules.minLength(1), TokenRules.maxLength(5));
		
		assertNotNull(rule.match(streamOf("abc"), TokenRuleContext.empty()));
	}
	
	@Test
	void allListWithTwoElements() {
		TokenRule rule = TokenRules.all(List.of(TokenRules.minLength(1), TokenRules.maxLength(5)));
		
		assertNotNull(rule.match(streamOf("abc"), TokenRuleContext.empty()));
	}
	
	@Test
	void boundaryTwoArgWithValidRules() {
		TokenRule rule = TokenRules.boundary(TokenRules.value("(", false), TokenRules.value(")", false));
		
		assertInstanceOf(BoundaryTokenRule.class, rule);
		assertSame(AlwaysMatchTokenRule.INSTANCE, ((BoundaryTokenRule) rule).betweenTokenRule());
	}
	
	@Test
	void boundaryThreeArgWithValidRules() {
		assertDoesNotThrow(() -> TokenRules.boundary(TokenRules.value("(", false), TokenRules.alwaysMatch(), TokenRules.value(")", false)));
	}
	
	@Test
	void recursiveFunctionWithValidFactory() {
		TokenRule rule = TokenRules.recursive(_ -> TokenRules.alwaysMatch());
		
		assertNotNull(rule);
	}
	
	@Test
	void recursiveThreeArgWithValidRules() {
		assertDoesNotThrow(() -> TokenRules.recursive(TokenRules.alwaysMatch(), TokenRules.alwaysMatch(), TokenRules.alwaysMatch()));
	}
	
	@Test
	void recursiveFactoryArgWithValidRules() {
		assertDoesNotThrow(() -> TokenRules.recursive(TokenRules.alwaysMatch(), TokenRules.alwaysMatch(), _ -> TokenRules.alwaysMatch()));
	}
	
	@Test
	void lazyReturnsDistinctInstancesPerCall() {
		assertNotSame(TokenRules.lazy(), TokenRules.lazy());
	}
	
	@Test
	void groupWithValidRule() {
		assertDoesNotThrow(() -> TokenRules.group(TokenRules.alwaysMatch()));
	}
	
	@Test
	void customWithValidCondition() {
		assertDoesNotThrow(() -> TokenRules.custom(_ -> true));
	}
	
	@Test
	void startDocumentAndStartLineAreDistinct() {
		assertNotEquals(TokenRules.startDocument(), TokenRules.startLine());
	}
	
	@Test
	void endDocumentAndEndLineAreDistinct() {
		assertNotEquals(TokenRules.endDocument(), TokenRules.endLine());
	}
	
	@Test
	void lookaheadWithValidRule() {
		TokenRule rule = TokenRules.lookahead(TokenRules.value("a", false));
		TokenStream stream = streamOf("a");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void negativeLookaheadWithValidRule() {
		TokenRule rule = TokenRules.negativeLookahead(TokenRules.value("a", false));
		TokenStream stream = streamOf("b");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void lookbehindWithValidRule() {
		TokenRule rule = TokenRules.lookbehind(TokenRules.value("a", false));
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")), 1);
		
		assertNotNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void negativeLookbehindWithValidRule() {
		TokenRule rule = TokenRules.negativeLookbehind(TokenRules.value("z", false));
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")), 1);
		
		assertNotNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void captureWithValidKeyAndRule() {
		assertDoesNotThrow(() -> TokenRules.capture("k", TokenRules.alwaysMatch()));
	}
	
	@Test
	void referenceWithValidKey() {
		TokenRule rule = TokenRules.reference("k");
		
		assertInstanceOf(ReferenceTokenRule.class, rule);
		assertEquals(ReferenceType.DYNAMIC, ((ReferenceTokenRule) rule).type());
	}
	
	@Test
	void referenceRuleWithValidKey() {
		TokenRule rule = TokenRules.referenceRule("k");
		
		assertInstanceOf(ReferenceTokenRule.class, rule);
		assertEquals(ReferenceType.RULE, ((ReferenceTokenRule) rule).type());
	}
	
	@Test
	void referenceTokensWithValidKey() {
		TokenRule rule = TokenRules.referenceTokens("k");
		
		assertInstanceOf(ReferenceTokenRule.class, rule);
		assertEquals(ReferenceType.TOKENS, ((ReferenceTokenRule) rule).type());
	}
	
	@Test
	void valueStringIgnoreCaseMatchesRegardlessOfCase() {
		TokenRule rule = TokenRules.value("ABC", true);
		
		assertNotNull(rule.match(streamOf("abc"), TokenRuleContext.empty()));
	}
	
	@Test
	void patternStringMatchesTypicalIdentifier() {
		TokenRule rule = TokenRules.pattern("[a-zA-Z]+");
		
		assertNotNull(rule.match(streamOf("myVar"), TokenRuleContext.empty()));
	}
	
	@Test
	void typeVarargsWithMultipleTokenTypes() {
		TokenRule rule = TokenRules.type(StandardTokenType.KEYWORD, StandardTokenType.MODIFIER);
		
		assertNotNull(rule.match(streamOf(typedToken("static", StandardTokenType.KEYWORD, StandardTokenType.MODIFIER)), TokenRuleContext.empty()));
		assertNull(rule.match(streamOf(typedToken("if", StandardTokenType.KEYWORD)), TokenRuleContext.empty()));
	}
	
	@Test
	void lengthBetweenWithLargeRange() {
		assertDoesNotThrow(() -> TokenRules.lengthBetween(0, Integer.MAX_VALUE));
	}
	
	@Test
	void atLeastMatchesRepeatedOccurrences() {
		TokenRule rule = TokenRules.atLeast(TokenRules.value("a", false), 2);
		
		TokenRuleMatch match = rule.match(streamOf("a", "a", "a"), TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void atMostMatchesUpToLimit() {
		TokenRule rule = TokenRules.atMost(TokenRules.value("a", false), 2);
		
		TokenRuleMatch oneMatch = rule.match(streamOf("a"), TokenRuleContext.empty());
		TokenRuleMatch twoMatch = rule.match(streamOf("a", "a"), TokenRuleContext.empty());
		TokenRuleMatch cappedMatch = rule.match(streamOf("a", "a", "a"), TokenRuleContext.empty());
		
		assertNotNull(oneMatch);
		assertEquals(1, oneMatch.matchedTokens().size());
		assertNotNull(twoMatch);
		assertEquals(2, twoMatch.matchedTokens().size());
		assertNotNull(cappedMatch);
		assertEquals(2, cappedMatch.matchedTokens().size());
	}
	
	@Test
	void sequenceVarargsWithThreeElements() {
		TokenRule rule = TokenRules.sequence(TokenRules.value("a", false), TokenRules.value("b", false), TokenRules.value("c", false));
		
		TokenRuleMatch match = rule.match(streamOf("a", "b", "c"), TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void anyVarargsSelectsFirstMatchingRule() {
		TokenRule first = TokenRules.value("a", false);
		TokenRule second = TokenRules.custom(_ -> true);
		TokenRule rule = TokenRules.any(first, second);
		
		TokenRuleMatch match = rule.match(streamOf("a"), TokenRuleContext.empty());
		
		assertNotNull(match);
		assertSame(first, match.matchingTokenRule());
	}
	
	@Test
	void allVarargsRequiresAllRulesToMatchSameToken() {
		TokenRule rule = TokenRules.all(TokenRules.minLength(1), TokenRules.maxLength(5), TokenRules.pattern("[a-z]+"));
		
		assertNotNull(rule.match(streamOf("abc"), TokenRuleContext.empty()));
	}
	
	@Test
	void captureStoresMatchedTokensUnderKey() {
		TokenRule rule = TokenRules.capture("cap", TokenRules.value("a", false));
		TokenRuleContext ctx = TokenRuleContext.empty();
		TokenStream stream = streamOf("a");
		
		TokenRuleMatch match = rule.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(match.matchedTokens(), ctx.getCapturedTokens("cap"));
	}
	
	@Test
	void recursiveFunctionBuildsSelfReferencingGrammar() {
		TokenRule rule = TokenRules.recursive(self -> TokenRules.any(
			TokenRules.value("x", false),
			TokenRules.sequence(TokenRules.value("(", false), self, TokenRules.value(")", false))
		));
		TokenStream stream = streamOf("(", "(", "x", ")", ")");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(5, match.matchedTokens().size());
	}
	
	@Test
	void recursiveThreeArgMatchesNestedOpeningContentClosing() {
		TokenRule rule = TokenRules.recursive(TokenRules.value("(", false), TokenRules.value("x", false), TokenRules.value(")", false));
		
		TokenRuleMatch match = rule.match(streamOf("(", "x", ")"), TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void lazyRuleUsedInsideSequenceThenInitialized() {
		LazyTokenRule lazy = TokenRules.lazy();
		
		assertThrows(NotInitializedException.class, lazy::get);
		
		lazy.set(TokenRules.value("x", false));
		TokenRule sequence = TokenRules.sequence(TokenRules.value("(", false), lazy, TokenRules.value(")", false));
		
		TokenRuleMatch match = sequence.match(streamOf("(", "x", ")"), TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void boundaryThreeArgWithBetweenRuleMatchesNestedContent() {
		TokenRule rule = TokenRules.boundary(TokenRules.value("[", false), TokenRules.zeroOrMore(TokenRules.value("a", false)), TokenRules.value("]", false));
		
		TokenRuleMatch match = rule.match(streamOf("[", "a", "a", "]"), TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(4, match.matchedTokens().size());
	}
	
	@Test
	void groupAppliedToNestedTokenGroupWithInnerSequence() {
		TokenGroup group = new TokenGroup(List.of(token("a"), token("b")));
		TokenRule rule = TokenRules.group(TokenRules.sequence(TokenRules.value("a", false), TokenRules.value("b", false)));
		TokenStream stream = streamOf(group);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(List.of(group), match.matchedTokens());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void referenceDynamicResolvesEitherRuleOrTokensDependingOnContext() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		TokenRule captureRule = TokenRules.capture("cap", TokenRules.value("x", false));
		assertNotNull(captureRule.match(streamOf("x"), ctx));
		
		TokenRule dynamicRef = TokenRules.reference("cap");
		TokenRuleMatch match = dynamicRef.match(streamOf("x"), ctx);
		
		assertNotNull(match);
	}
	
	@Test
	void captureFollowedByReferenceTokensRoundTrip() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		TokenRule captureRule = TokenRules.capture("word", TokenRules.value("hello", false));
		assertNotNull(captureRule.match(streamOf("hello"), ctx));
		
		TokenRule referenceRule = TokenRules.referenceTokens("word");
		TokenRuleMatch match = referenceRule.match(streamOf("hello"), ctx);
		
		assertNotNull(match);
	}
	
}
