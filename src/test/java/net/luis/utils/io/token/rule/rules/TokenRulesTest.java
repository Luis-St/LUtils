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

package net.luis.utils.io.token.rule.rules;

import net.luis.utils.io.token.definition.*;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.assertions.*;
import net.luis.utils.io.token.rule.rules.assertions.anchors.*;
import net.luis.utils.io.token.rule.rules.combinators.*;
import net.luis.utils.io.token.rule.rules.matchers.LengthTokenRule;
import net.luis.utils.io.token.rule.rules.matchers.PatternTokenRule;
import net.luis.utils.io.token.rule.rules.quantifiers.OptionalTokenRule;
import net.luis.utils.io.token.rule.rules.quantifiers.RepeatedTokenRule;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRules}.<br>
 *
 * @author Luis-St
 */
class TokenRulesTest {
	
	private static @NotNull TokenRule createRule(@NotNull String value) {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
				return null;
			}
		};
	}
	
	@Test
	void alwaysMatchReturnsSingleton() {
		TokenRule rule1 = TokenRules.alwaysMatch();
		TokenRule rule2 = TokenRules.alwaysMatch();
		
		assertSame(AlwaysMatchTokenRule.INSTANCE, rule1);
		assertSame(rule1, rule2);
		assertInstanceOf(AlwaysMatchTokenRule.class, rule1);
	}
	
	@Test
	void patternWithStringCreatesPatternTokenRule() {
		TokenRule rule = TokenRules.pattern("\\d+");
		
		assertEquals("\\d+", assertInstanceOf(PatternTokenRule.class, rule).pattern().pattern());
	}
	
	@Test
	void patternWithNullString() {
		assertThrows(NullPointerException.class, () -> TokenRules.pattern((String) null));
	}
	
	@Test
	void patternWithInvalidRegex() {
		assertThrows(Exception.class, () -> TokenRules.pattern("[invalid"));
	}
	
	@Test
	void patternWithCompiledPattern() {
		Pattern compiled = Pattern.compile("[a-z]+");
		TokenRule rule = TokenRules.pattern(compiled);
		
		assertEquals(compiled, assertInstanceOf(PatternTokenRule.class, rule).pattern());
	}
	
	@Test
	void patternWithNullCompiledPattern() {
		assertThrows(NullPointerException.class, () -> TokenRules.pattern((Pattern) null));
	}
	
	@Test
	void optionalCreatesOptionalTokenRule() {
		TokenRule innerRule = createRule("test");
		TokenRule optional = TokenRules.optional(innerRule);
		
		assertEquals(innerRule, assertInstanceOf(OptionalTokenRule.class, optional).tokenRule());
	}
	
	@Test
	void optionalWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.optional(null));
	}
	
	@Test
	void repeatAtLeastCreatesRepeatedTokenRule() {
		TokenRule innerRule = createRule("test");
		TokenRule repeated = TokenRules.repeatAtLeast(innerRule, 3);
		
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class, repeated);
		assertEquals(innerRule, repeatedRule.tokenRule());
		assertEquals(3, repeatedRule.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeatedRule.maxOccurrences());
	}
	
	@Test
	void repeatAtLeastWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.repeatAtLeast(null, 1));
	}
	
	@Test
	void repeatAtLeastWithNegativeMin() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.repeatAtLeast(createRule("test"), -1));
	}
	
	@Test
	void repeatExactlyCreatesRepeatedTokenRule() {
		TokenRule innerRule = createRule("test");
		TokenRule repeated = TokenRules.repeatExactly(innerRule, 5);
		
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class, repeated);
		assertEquals(innerRule, repeatedRule.tokenRule());
		assertEquals(5, repeatedRule.minOccurrences());
		assertEquals(5, repeatedRule.maxOccurrences());
	}
	
	@Test
	void repeatExactlyWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.repeatExactly(null, 1));
	}
	
	@Test
	void repeatExactlyWithNegativeCount() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.repeatExactly(createRule("test"), -1));
	}
	
	@Test
	void repeatAtMostCreatesRepeatedTokenRule() {
		TokenRule innerRule = createRule("test");
		TokenRule repeated = TokenRules.repeatAtMost(innerRule, 4);
		
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class, repeated);
		assertEquals(innerRule, repeatedRule.tokenRule());
		assertEquals(0, repeatedRule.minOccurrences());
		assertEquals(4, repeatedRule.maxOccurrences());
	}
	
	@Test
	void repeatAtMostWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.repeatAtMost(null, 1));
	}
	
	@Test
	void repeatAtMostWithNegativeMax() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.repeatAtMost(createRule("test"), -1));
	}
	
	@Test
	void repeatInfinitelyCreatesRepeatedTokenRule() {
		TokenRule innerRule = createRule("test");
		TokenRule repeated = TokenRules.repeatInfinitely(innerRule);
		
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class, repeated);
		assertEquals(innerRule, repeatedRule.tokenRule());
		assertEquals(0, repeatedRule.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeatedRule.maxOccurrences());
	}
	
	@Test
	void repeatInfinitelyWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.repeatInfinitely(null));
	}
	
	@Test
	void repeatBetweenCreatesRepeatedTokenRule() {
		TokenRule innerRule = createRule("test");
		TokenRule repeated = TokenRules.repeatBetween(innerRule, 2, 7);
		
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class, repeated);
		assertEquals(innerRule, repeatedRule.tokenRule());
		assertEquals(2, repeatedRule.minOccurrences());
		assertEquals(7, repeatedRule.maxOccurrences());
	}
	
	@Test
	void repeatBetweenWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.repeatBetween(null, 1, 3));
	}
	
	@Test
	void repeatBetweenWithInvalidRange() {
		TokenRule rule = createRule("test");
		assertThrows(IllegalArgumentException.class, () -> TokenRules.repeatBetween(rule, -1, 3));
		assertThrows(IllegalArgumentException.class, () -> TokenRules.repeatBetween(rule, 5, 3));
		assertThrows(IllegalArgumentException.class, () -> TokenRules.repeatBetween(rule, 0, 0));
	}
	
	@Test
	void sequenceWithArrayCreatesSequenceTokenRule() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		TokenRule sequence = TokenRules.sequence(rule1, rule2);
		
		List<TokenRule> rules = assertInstanceOf(SequenceTokenRule.class, sequence).tokenRules();
		assertEquals(2, rules.size());
		assertEquals(rule1, rules.get(0));
		assertEquals(rule2, rules.get(1));
	}
	
	@Test
	void sequenceWithNullArray() {
		assertThrows(NullPointerException.class, () -> TokenRules.sequence((TokenRule[]) null));
	}
	
	@Test
	void sequenceWithEmptyArray() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.sequence(new TokenRule[0]));
	}
	
	@Test
	void sequenceWithListCreatesSequenceTokenRule() {
		TokenRule rule1 = createRule("a");
		TokenRule rule2 = createRule("b");
		TokenRule rule3 = createRule("c");
		List<TokenRule> ruleList = List.of(rule1, rule2, rule3);
		TokenRule sequence = TokenRules.sequence(ruleList);
		
		assertEquals(ruleList, assertInstanceOf(SequenceTokenRule.class, sequence).tokenRules());
	}
	
	@Test
	void sequenceWithNullList() {
		assertThrows(NullPointerException.class, () -> TokenRules.sequence((List<TokenRule>) null));
	}
	
	@Test
	void anyWithArrayCreatesAnyOfTokenRule() {
		TokenRule rule1 = createRule("option1");
		TokenRule rule2 = createRule("option2");
		TokenRule anyOf = TokenRules.any(rule1, rule2);
		
		Set<TokenRule> rules = assertInstanceOf(AnyOfTokenRule.class, anyOf).tokenRules();
		assertEquals(2, rules.size());
		assertTrue(rules.contains(rule1));
		assertTrue(rules.contains(rule2));
	}
	
	@Test
	void anyWithNullArray() {
		assertThrows(NullPointerException.class, () -> TokenRules.any((TokenRule[]) null));
	}
	
	@Test
	void anyWithEmptyArray() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.any(new TokenRule[0]));
	}
	
	@Test
	void anyWithSetCreatesAnyOfTokenRule() {
		TokenRule rule1 = createRule("x");
		TokenRule rule2 = createRule("y");
		Set<TokenRule> ruleSet = Set.of(rule1, rule2);
		TokenRule anyOf = TokenRules.any(ruleSet);
		
		assertEquals(ruleSet, assertInstanceOf(AnyOfTokenRule.class, anyOf).tokenRules());
	}
	
	@Test
	void anyWithNullSet() {
		assertThrows(NullPointerException.class, () -> TokenRules.any((Set<TokenRule>) null));
	}
	
	@Test
	void boundaryWithTwoRulesCreatesBoundaryTokenRule() {
		TokenRule start = createRule("start");
		TokenRule end = createRule("end");
		TokenRule boundary = TokenRules.boundary(start, end);
		
		BoundaryTokenRule boundaryRule = assertInstanceOf(BoundaryTokenRule.class, boundary);
		assertEquals(start, boundaryRule.startTokenRule());
		assertEquals(TokenRules.alwaysMatch(), boundaryRule.betweenTokenRule());
		assertEquals(end, boundaryRule.endTokenRule());
	}
	
	@Test
	void boundaryWithThreeRulesCreatesBoundaryTokenRule() {
		TokenRule start = createRule("start");
		TokenRule between = createRule("between");
		TokenRule end = createRule("end");
		TokenRule boundary = TokenRules.boundary(start, between, end);
		
		BoundaryTokenRule boundaryRule = assertInstanceOf(BoundaryTokenRule.class, boundary);
		assertEquals(start, boundaryRule.startTokenRule());
		assertEquals(between, boundaryRule.betweenTokenRule());
		assertEquals(end, boundaryRule.endTokenRule());
	}
	
	@Test
	void boundaryWithNullRules() {
		TokenRule validRule = createRule("valid");
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(null, validRule));
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(validRule, null));
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(null, validRule, validRule));
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(validRule, null, validRule));
		assertThrows(NullPointerException.class, () -> TokenRules.boundary(validRule, validRule, null));
	}
	
	@Test
	void endDocumentCreatesEndTokenRule() {
		TokenRule endRule = TokenRules.endDocument();
		
		assertEquals(AnchorType.DOCUMENT, assertInstanceOf(EndTokenRule.class, endRule).anchorType());
	}
	
	@Test
	void endLineCreatesEndTokenRule() {
		TokenRule endRule = TokenRules.endLine();
		
		assertEquals(AnchorType.LINE, assertInstanceOf(EndTokenRule.class, endRule).anchorType());
	}
	
	@Test
	void notCreatesNotTokenRule() {
		TokenRule innerRule = createRule("test");
		TokenRule notRule = TokenRules.not(innerRule);
		
		assertInstanceOf(NotTokenRule.class, notRule);
		assertEquals(innerRule, ((NotTokenRule) notRule).tokenRule());
	}
	
	@Test
	void notWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.not(null));
	}
	
	@Test
	void lookaheadCreatesPositiveLookaheadTokenRule() {
		TokenRule innerRule = createRule("test");
		TokenRule lookaheadRule = TokenRules.lookahead(innerRule);
		
		LookaheadTokenRule rule = assertInstanceOf(LookaheadTokenRule.class, lookaheadRule);
		assertEquals(innerRule, rule.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, rule.mode());
	}
	
	@Test
	void lookaheadWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.lookahead(null));
	}
	
	@Test
	void negativeLookaheadCreatesNegativeLookaheadTokenRule() {
		TokenRule innerRule = createRule("test");
		TokenRule negativeLookaheadRule = TokenRules.negativeLookahead(innerRule);
		
		LookaheadTokenRule rule = assertInstanceOf(LookaheadTokenRule.class, negativeLookaheadRule);
		assertEquals(innerRule, rule.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, rule.mode());
	}
	
	@Test
	void negativeLookaheadWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.negativeLookahead(null));
	}
	
	@Test
	void lookbehindCreatesPositiveLookbehindTokenRule() {
		TokenRule innerRule = createRule("test");
		TokenRule lookbehindRule = TokenRules.lookbehind(innerRule);
		
		LookbehindTokenRule rule = assertInstanceOf(LookbehindTokenRule.class, lookbehindRule);
		assertEquals(innerRule, rule.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, rule.mode());
	}
	
	@Test
	void lookbehindWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.lookbehind(null));
	}
	
	@Test
	void negativeLookbehindCreatesNegativeLookbehindTokenRule() {
		TokenRule innerRule = createRule("test");
		TokenRule negativeLookbehindRule = TokenRules.negativeLookbehind(innerRule);
		
		LookbehindTokenRule rule = assertInstanceOf(LookbehindTokenRule.class, negativeLookbehindRule);
		assertEquals(innerRule, rule.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, rule.mode());
	}
	
	@Test
	void negativeLookbehindWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.negativeLookbehind(null));
	}
	
	@Test
	void startDocumentCreatesStartTokenRule() {
		TokenRule startRule = TokenRules.startDocument();
		
		assertEquals(AnchorType.DOCUMENT, assertInstanceOf(StartTokenRule.class, startRule).anchorType());
	}
	
	@Test
	void startLineCreatesStartTokenRule() {
		TokenRule startRule = TokenRules.startLine();
		
		assertEquals(AnchorType.LINE, assertInstanceOf(StartTokenRule.class, startRule).anchorType());
	}
	
	@Test
	void lengthBetweenCreatesLengthTokenRule() {
		TokenRule lengthRule = TokenRules.lengthBetween(2, 5);
		
		LengthTokenRule rule = assertInstanceOf(LengthTokenRule.class, lengthRule);
		assertEquals(2, rule.minLength());
		assertEquals(5, rule.maxLength());
	}
	
	@Test
	void lengthBetweenWithInvalidParameters() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.lengthBetween(-1, 5));
		assertThrows(IllegalArgumentException.class, () -> TokenRules.lengthBetween(2, -1));
		assertThrows(IllegalArgumentException.class, () -> TokenRules.lengthBetween(5, 2));
	}
	
	@Test
	void lengthBetweenWithEqualValues() {
		TokenRule lengthRule = TokenRules.lengthBetween(3, 3);
		
		LengthTokenRule rule = assertInstanceOf(LengthTokenRule.class, lengthRule);
		assertEquals(3, rule.minLength());
		assertEquals(3, rule.maxLength());
	}
	
	@Test
	void exactLengthCreatesLengthTokenRule() {
		TokenRule lengthRule = TokenRules.exactLength(7);
		
		LengthTokenRule rule = assertInstanceOf(LengthTokenRule.class, lengthRule);
		assertEquals(7, rule.minLength());
		assertEquals(7, rule.maxLength());
	}
	
	@Test
	void exactLengthWithNegativeValue() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.exactLength(-1));
	}
	
	@Test
	void exactLengthWithZero() {
		TokenRule lengthRule = TokenRules.exactLength(0);
		
		LengthTokenRule rule = assertInstanceOf(LengthTokenRule.class, lengthRule);
		assertEquals(0, rule.minLength());
		assertEquals(0, rule.maxLength());
	}
	
	@Test
	void minLengthCreatesLengthTokenRule() {
		TokenRule lengthRule = TokenRules.minLength(3);
		
		LengthTokenRule rule = assertInstanceOf(LengthTokenRule.class, lengthRule);
		assertEquals(3, rule.minLength());
		assertEquals(Integer.MAX_VALUE, rule.maxLength());
	}
	
	@Test
	void minLengthWithNegativeValue() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.minLength(-1));
	}
	
	@Test
	void minLengthWithZero() {
		TokenRule lengthRule = TokenRules.minLength(0);
		
		LengthTokenRule rule = assertInstanceOf(LengthTokenRule.class, lengthRule);
		assertEquals(0, rule.minLength());
		assertEquals(Integer.MAX_VALUE, rule.maxLength());
	}
	
	@Test
	void maxLengthCreatesLengthTokenRule() {
		TokenRule lengthRule = TokenRules.maxLength(10);
		
		LengthTokenRule rule = assertInstanceOf(LengthTokenRule.class, lengthRule);
		assertEquals(0, rule.minLength());
		assertEquals(10, rule.maxLength());
	}
	
	@Test
	void maxLengthWithNegativeValue() {
		assertThrows(IllegalArgumentException.class, () -> TokenRules.maxLength(-1));
	}
	
	@Test
	void maxLengthWithZero() {
		TokenRule lengthRule = TokenRules.maxLength(0);
		
		LengthTokenRule rule = assertInstanceOf(LengthTokenRule.class, lengthRule);
		assertEquals(0, rule.minLength());
		assertEquals(0, rule.maxLength());
	}
	
	@Test
	void lengthRulesEdgeCases() {
		assertDoesNotThrow(() -> TokenRules.lengthBetween(0, Integer.MAX_VALUE));
		assertDoesNotThrow(() -> TokenRules.exactLength(Integer.MAX_VALUE));
		assertDoesNotThrow(() -> TokenRules.minLength(Integer.MAX_VALUE));
		assertDoesNotThrow(() -> TokenRules.maxLength(Integer.MAX_VALUE));
	}
	
	@Test
	void groupCreatesTokenGroupRule() {
		TokenRule innerRule = createRule("test");
		TokenRule groupRule = TokenRules.group(innerRule);
		
		assertEquals(innerRule, assertInstanceOf(TokenGroupRule.class, groupRule).tokenRule());
	}
	
	@Test
	void groupWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.group(null));
	}
	
	@Test
	void groupWithAlwaysMatchRule() {
		TokenRule alwaysMatch = TokenRules.alwaysMatch();
		TokenRule groupRule = TokenRules.group(alwaysMatch);
		
		assertEquals(alwaysMatch, assertInstanceOf(TokenGroupRule.class, groupRule).tokenRule());
	}
	
	@Test
	void groupWithPatternRule() {
		TokenRule patternRule = TokenRules.pattern("\\d+");
		TokenRule groupRule = TokenRules.group(patternRule);
		
		assertEquals(patternRule, assertInstanceOf(TokenGroupRule.class, groupRule).tokenRule());
	}
	
	@Test
	void groupWithComplexRule() {
		TokenRule complexRule = TokenRules.sequence(
			TokenRules.pattern("start"),
			TokenRules.repeatInfinitely(TokenRules.pattern("middle")),
			TokenRules.pattern("end")
		);
		TokenRule groupRule = TokenRules.group(complexRule);
		
		assertEquals(complexRule, assertInstanceOf(TokenGroupRule.class, groupRule).tokenRule());
	}
	
	@Test
	void toRegexWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.toRegex(null));
	}
	
	@Test
	void toRegexWithAlwaysMatchRule() {
		assertEquals(".*?", TokenRules.toRegex(TokenRules.alwaysMatch()));
	}
	
	@Test
	void toRegexWithEndDocumentRule() {
		assertEquals("$", TokenRules.toRegex(TokenRules.endDocument()));
	}
	
	@Test
	void toRegexWithEndLineRule() {
		assertEquals("\\n", TokenRules.toRegex(TokenRules.endLine()));
	}
	
	@Test
	void toRegexWithSimplePatternRule() {
		assertEquals("test", TokenRules.toRegex(TokenRules.pattern("test")));
		assertEquals("\\d+", TokenRules.toRegex(TokenRules.pattern("\\d+")));
		assertEquals("[a-z]", TokenRules.toRegex(TokenRules.pattern("[a-z]")));
	}
	
	@Test
	void toRegexWithPatternInBrackets() {
		assertEquals("test", TokenRules.toRegex(TokenRules.pattern("(test)")));
		assertEquals("\\d+", TokenRules.toRegex(TokenRules.pattern("(\\d+)")));
	}
	
	@Test
	void toRegexWithOptionalRule() {
		assertEquals("(test)?", TokenRules.toRegex(TokenRules.optional(TokenRules.pattern("test"))));
		assertEquals("(.*?)?", TokenRules.toRegex(TokenRules.optional(TokenRules.alwaysMatch())));
		assertEquals("(\\d+)?", TokenRules.toRegex(TokenRules.optional(TokenRules.pattern("\\d+"))));
	}
	
	@Test
	void toRegexWithRepeatedRules() {
		assertEquals("(test){3}", TokenRules.toRegex(TokenRules.repeatExactly(TokenRules.pattern("test"), 3)));
		assertEquals("test", TokenRules.toRegex(TokenRules.repeatExactly(TokenRules.pattern("test"), 1)));
		assertEquals("(test){2,}", TokenRules.toRegex(TokenRules.repeatAtLeast(TokenRules.pattern("test"), 2)));
		assertEquals("(test)+", TokenRules.toRegex(TokenRules.repeatAtLeast(TokenRules.pattern("test"), 1)));
		assertEquals("(test){0,3}", TokenRules.toRegex(TokenRules.repeatAtMost(TokenRules.pattern("test"), 3)));
		assertEquals("(test)?", TokenRules.toRegex(TokenRules.repeatAtMost(TokenRules.pattern("test"), 1)));
		assertEquals("(test)*", TokenRules.toRegex(TokenRules.repeatInfinitely(TokenRules.pattern("test"))));
		assertEquals("(test){2,5}", TokenRules.toRegex(TokenRules.repeatBetween(TokenRules.pattern("test"), 2, 5)));
	}
	
	@Test
	void toRegexWithSpecialCharacterHandling() {
		assertEquals("(\\d+?){2,5}", TokenRules.toRegex(TokenRules.repeatBetween(TokenRules.pattern("\\d+?"), 2, 5)));
		assertEquals("(test*)+", TokenRules.toRegex(TokenRules.repeatAtLeast(TokenRules.pattern("test*"), 1)));
	}
	
	@Test
	void toRegexWithSequenceRule() {
		assertEquals("test\\d+", TokenRules.toRegex(TokenRules.sequence(
			TokenRules.pattern("test"),
			TokenRules.pattern("\\d+")
		)));
		assertEquals("test\\d+.*?", TokenRules.toRegex(TokenRules.sequence(
			TokenRules.pattern("test"),
			TokenRules.pattern("\\d+"),
			TokenRules.alwaysMatch()
		)));
	}
	
	@Test
	void toRegexWithAnyOfRule() {
		Set<TokenRule> anyRules = new LinkedHashSet<>(List.of(
			TokenRules.pattern("test"),
			TokenRules.pattern("\\d+")
		));
		assertEquals("test|\\d+", TokenRules.toRegex(TokenRules.any(anyRules)));
		
		anyRules.add(TokenRules.alwaysMatch());
		assertEquals("test|\\d+|.*?", TokenRules.toRegex(TokenRules.any(anyRules)));
	}
	
	@Test
	void toRegexWithCharacterTokenDefinitions() {
		Set<TokenRule> charRules = new LinkedHashSet<>(List.of(
			new CharTokenDefinition('a'),
			new CharTokenDefinition('b'),
			new CharTokenDefinition('c')
		));
		assertEquals("[abc]", TokenRules.toRegex(TokenRules.any(charRules)));
		
		Set<TokenRule> specialCharRules = new LinkedHashSet<>(List.of(
			new CharTokenDefinition('['),
			new CharTokenDefinition('\\')
		));
		assertEquals("[\\[\\\\]", TokenRules.toRegex(TokenRules.any(specialCharRules)));
	}
	
	@Test
	void toRegexWithBoundaryRule() {
		assertEquals("test(.*?)end", TokenRules.toRegex(TokenRules.boundary(
			TokenRules.pattern("test"),
			TokenRules.pattern("end")
		)));
		assertEquals("test(\\d+?)end", TokenRules.toRegex(TokenRules.boundary(
			TokenRules.pattern("test"),
			TokenRules.pattern("\\d+?"),
			TokenRules.pattern("end")
		)));
	}
	
	@Test
	void toRegexWithStartDocumentRule() {
		assertEquals("^", TokenRules.toRegex(TokenRules.startDocument()));
	}
	
	@Test
	void toRegexWithStartLineRule() {
		assertEquals("\\n", TokenRules.toRegex(TokenRules.startLine()));
	}
	
	@Test
	void toRegexWithNotRule() {
		assertEquals("(?!test)", TokenRules.toRegex(TokenRules.not(TokenRules.pattern("test"))));
		assertEquals("(?!\\d+)", TokenRules.toRegex(TokenRules.not(TokenRules.pattern("\\d+"))));
	}
	
	@Test
	void toRegexWithLookaheadRules() {
		assertEquals("(?=test)", TokenRules.toRegex(TokenRules.lookahead(TokenRules.pattern("test"))));
		assertEquals("(?!test)", TokenRules.toRegex(TokenRules.negativeLookahead(TokenRules.pattern("test"))));
		assertEquals("(?=\\d+)", TokenRules.toRegex(TokenRules.lookahead(TokenRules.pattern("\\d+"))));
		assertEquals("(?![a-z])", TokenRules.toRegex(TokenRules.negativeLookahead(TokenRules.pattern("[a-z]"))));
	}
	
	@Test
	void toRegexWithLookbehindRules() {
		assertEquals("(?<=test)", TokenRules.toRegex(TokenRules.lookbehind(TokenRules.pattern("test"))));
		assertEquals("(?<!test)", TokenRules.toRegex(TokenRules.negativeLookbehind(TokenRules.pattern("test"))));
		assertEquals("(?<=\\d+)", TokenRules.toRegex(TokenRules.lookbehind(TokenRules.pattern("\\d+"))));
		assertEquals("(?<![a-z])", TokenRules.toRegex(TokenRules.negativeLookbehind(TokenRules.pattern("[a-z]"))));
	}
	
	@Test
	void toRegexWithLengthRules() {
		assertEquals(".{5}", TokenRules.toRegex(TokenRules.exactLength(5)));
		assertEquals(".{0}", TokenRules.toRegex(TokenRules.exactLength(0)));
		assertEquals(".{3,7}", TokenRules.toRegex(TokenRules.lengthBetween(3, 7)));
		assertEquals(".{2,}", TokenRules.toRegex(TokenRules.minLength(2)));
		assertEquals(".{0,10}", TokenRules.toRegex(TokenRules.maxLength(10)));
	}
	
	@Test
	void toRegexWithLengthRulesEdgeCases() {
		assertEquals(".{0}", TokenRules.toRegex(TokenRules.lengthBetween(0, 0)));
		assertEquals(".{" + Integer.MAX_VALUE + "}", TokenRules.toRegex(TokenRules.exactLength(Integer.MAX_VALUE)));
		assertEquals(".{100,}", TokenRules.toRegex(TokenRules.minLength(100)));
	}
	
	@Test
	void toRegexWithTokenGroupRuleThrowsException() {
		TokenRule groupRule = TokenRules.group(TokenRules.pattern("test"));
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> TokenRules.toRegex(groupRule));
		assertTrue(exception.getMessage().contains("TokenGroupRule cannot be converted to regex"));
	}
	
	@Test
	void toRegexWithComplexLookaroundRules() {
		TokenRule complexLookahead = TokenRules.sequence(
			TokenRules.lookahead(TokenRules.pattern("\\d+")),
			TokenRules.pattern("[a-z]+")
		);
		assertEquals("(?=\\d+)[a-z]+", TokenRules.toRegex(complexLookahead));
		
		TokenRule complexLookbehind = TokenRules.sequence(
			TokenRules.negativeLookbehind(TokenRules.pattern("test")),
			TokenRules.pattern("word")
		);
		assertEquals("(?<!test)word", TokenRules.toRegex(complexLookbehind));
	}
	
	@Test
	void toRegexWithNestedComplexRules() {
		TokenRule notOptional = TokenRules.not(TokenRules.optional(TokenRules.pattern("skip")));
		assertEquals("(?!(skip)?)", TokenRules.toRegex(notOptional));
		
		TokenRule lookaheadRepeated = TokenRules.lookahead(TokenRules.repeatAtLeast(TokenRules.pattern("x"), 2));
		assertEquals("(?=x{2,})", TokenRules.toRegex(lookaheadRepeated));
	}
	
	@Test
	void toRegexWithStartDocumentAndEndDocumentAnchors() {
		TokenRule startSequence = TokenRules.sequence(
			TokenRules.startDocument(),
			TokenRules.pattern("content"),
			TokenRules.endDocument()
		);
		assertEquals("^content$", TokenRules.toRegex(startSequence));
	}
	
	@Test
	void toRegexWithStartLineAndEndLineAnchors() {
		TokenRule startSequence = TokenRules.sequence(
			TokenRules.startLine(),
			TokenRules.pattern("content"),
			TokenRules.endLine()
		);
		assertEquals("\\ncontent\\n", TokenRules.toRegex(startSequence));
	}
	
	@Test
	void toRegexWithMixedLookaroundAndLengthRules() {
		TokenRule mixedRule = TokenRules.sequence(
			TokenRules.lookahead(TokenRules.lengthBetween(3, 5)),
			TokenRules.pattern("[a-z]+")
		);
		assertEquals("(?=.{3,5})[a-z]+", TokenRules.toRegex(mixedRule));
	}
	
	@Test
	void toRegexWithTokenDefinitions() {
		assertEquals("[a-zA-Z0-9]+", TokenRules.toRegex(WordTokenDefinition.INSTANCE));
		assertEquals("a", TokenRules.toRegex(new CharTokenDefinition('a')));
		assertEquals("test", TokenRules.toRegex(new StringTokenDefinition("test", false)));
		assertEquals("\\\\a", TokenRules.toRegex(new EscapedTokenDefinition('a')));
	}
	
	@Test
	void toRegexWithEscapedSpecialCharacters() {
		assertEquals("\\$", TokenRules.toRegex(new CharTokenDefinition('$')));
		assertEquals("\\(", TokenRules.toRegex(new CharTokenDefinition('(')));
		assertEquals("\\+", TokenRules.toRegex(new CharTokenDefinition('+')));
		assertEquals("\\.", TokenRules.toRegex(new CharTokenDefinition('.')));
		assertEquals("\\*", TokenRules.toRegex(new CharTokenDefinition('*')));
		assertEquals("\\{", TokenRules.toRegex(new CharTokenDefinition('{')));
		assertEquals("\\}", TokenRules.toRegex(new CharTokenDefinition('}')));
		assertEquals("\\[", TokenRules.toRegex(new CharTokenDefinition('[')));
		assertEquals("\\]", TokenRules.toRegex(new CharTokenDefinition(']')));
		assertEquals("\\^", TokenRules.toRegex(new CharTokenDefinition('^')));
		assertEquals("\\|", TokenRules.toRegex(new CharTokenDefinition('|')));
	}
	
	@Test
	void toRegexWithComplexRules() {
		TokenRule complex1 = TokenRules.sequence(
			TokenRules.pattern("start"),
			TokenRules.optional(TokenRules.pattern("\\d+")),
			TokenRules.repeatAtLeast(TokenRules.pattern("[a-z]"), 1)
		);
		assertEquals("start(\\d+)?[a-z]+", TokenRules.toRegex(complex1));
		
		TokenRule complex2 = TokenRules.any(new LinkedHashSet<>(List.of(
			TokenRules.pattern("option1"),
			TokenRules.sequence(
				TokenRules.pattern("prefix"),
				TokenRules.repeatBetween(TokenRules.pattern("\\w"), 1, 3)
			)
		)));
		assertEquals("option1|prefix(\\w){1,3}", TokenRules.toRegex(complex2));
	}
	
	@Test
	void toRegexWithVeryComplexRule() {
		TokenRule complex = TokenRules.boundary(
			TokenRules.pattern("\""),
			TokenRules.repeatInfinitely(
				TokenRules.any(new LinkedHashSet<>(List.of(
					TokenRules.pattern("[^\"]"),
					TokenRules.pattern("\"")
				)))
			),
			TokenRules.pattern("\"")
		);
		assertEquals("\"(([^\"]|\")*)\"", TokenRules.toRegex(complex));
	}
	
	@Test
	void factoryMethodsWithEdgeCases() {
		assertDoesNotThrow(() -> TokenRules.repeatBetween(createRule("test"), 0, Integer.MAX_VALUE));
		assertDoesNotThrow(() -> TokenRules.repeatAtLeast(createRule("test"), Integer.MAX_VALUE));
		
		assertDoesNotThrow(() -> TokenRules.sequence(List.of(createRule("single"))));
		assertDoesNotThrow(() -> TokenRules.any(Set.of(createRule("single"))));
	}
}
