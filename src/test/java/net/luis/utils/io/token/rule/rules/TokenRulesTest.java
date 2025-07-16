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
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
		
		assertInstanceOf(PatternTokenRule.class, rule);
		assertEquals("\\d+", ((PatternTokenRule) rule).pattern().pattern());
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
		
		assertInstanceOf(PatternTokenRule.class, rule);
		assertEquals(compiled, ((PatternTokenRule) rule).pattern());
	}
	
	@Test
	void patternWithNullCompiledPattern() {
		assertThrows(NullPointerException.class, () -> TokenRules.pattern((Pattern) null));
	}
	
	@Test
	void optionalCreatesOptionalTokenRule() {
		TokenRule innerRule = createRule("test");
		TokenRule optional = TokenRules.optional(innerRule);
		
		assertInstanceOf(OptionalTokenRule.class, optional);
		assertEquals(innerRule, ((OptionalTokenRule) optional).tokenRule());
	}
	
	@Test
	void optionalWithNullRule() {
		assertThrows(NullPointerException.class, () -> TokenRules.optional(null));
	}
	
	@Test
	void repeatAtLeastCreatesRepeatedTokenRule() {
		TokenRule innerRule = createRule("test");
		TokenRule repeated = TokenRules.repeatAtLeast(innerRule, 3);
		
		assertInstanceOf(RepeatedTokenRule.class, repeated);
		RepeatedTokenRule repeatedRule = (RepeatedTokenRule) repeated;
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
		
		assertInstanceOf(RepeatedTokenRule.class, repeated);
		RepeatedTokenRule repeatedRule = (RepeatedTokenRule) repeated;
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
		
		assertInstanceOf(RepeatedTokenRule.class, repeated);
		RepeatedTokenRule repeatedRule = (RepeatedTokenRule) repeated;
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
		
		assertInstanceOf(RepeatedTokenRule.class, repeated);
		RepeatedTokenRule repeatedRule = (RepeatedTokenRule) repeated;
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
		
		assertInstanceOf(RepeatedTokenRule.class, repeated);
		RepeatedTokenRule repeatedRule = (RepeatedTokenRule) repeated;
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
		
		assertInstanceOf(SequenceTokenRule.class, sequence);
		List<TokenRule> rules = ((SequenceTokenRule) sequence).tokenRules();
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
		
		assertInstanceOf(SequenceTokenRule.class, sequence);
		assertEquals(ruleList, ((SequenceTokenRule) sequence).tokenRules());
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
		
		assertInstanceOf(AnyOfTokenRule.class, anyOf);
		Set<TokenRule> rules = ((AnyOfTokenRule) anyOf).tokenRules();
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
		
		assertInstanceOf(AnyOfTokenRule.class, anyOf);
		assertEquals(ruleSet, ((AnyOfTokenRule) anyOf).tokenRules());
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
		
		assertInstanceOf(BoundaryTokenRule.class, boundary);
		BoundaryTokenRule boundaryRule = (BoundaryTokenRule) boundary;
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
		
		assertInstanceOf(BoundaryTokenRule.class, boundary);
		BoundaryTokenRule boundaryRule = (BoundaryTokenRule) boundary;
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
	void endReturnsSingleton() {
		TokenRule end1 = TokenRules.end();
		TokenRule end2 = TokenRules.end();
		
		assertSame(EndTokenRule.INSTANCE, end1);
		assertSame(end1, end2);
		assertInstanceOf(EndTokenRule.class, end1);
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
	void toRegexWithEndRule() {
		assertEquals("$", TokenRules.toRegex(TokenRules.end()));
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
		assertEquals("test?", TokenRules.toRegex(TokenRules.optional(TokenRules.pattern("test"))));
		assertEquals("(.*?)?", TokenRules.toRegex(TokenRules.optional(TokenRules.alwaysMatch())));
		assertEquals("(\\d+)?", TokenRules.toRegex(TokenRules.optional(TokenRules.pattern("\\d+"))));
	}
	
	@Test
	void toRegexWithRepeatedRules() {
		assertEquals("test{3}", TokenRules.toRegex(TokenRules.repeatExactly(TokenRules.pattern("test"), 3)));
		assertEquals("test", TokenRules.toRegex(TokenRules.repeatExactly(TokenRules.pattern("test"), 1)));
		assertEquals("test{2,}", TokenRules.toRegex(TokenRules.repeatAtLeast(TokenRules.pattern("test"), 2)));
		assertEquals("test+", TokenRules.toRegex(TokenRules.repeatAtLeast(TokenRules.pattern("test"), 1)));
		assertEquals("test{0,3}", TokenRules.toRegex(TokenRules.repeatAtMost(TokenRules.pattern("test"), 3)));
		assertEquals("test?", TokenRules.toRegex(TokenRules.repeatAtMost(TokenRules.pattern("test"), 1)));
		assertEquals("test*", TokenRules.toRegex(TokenRules.repeatInfinitely(TokenRules.pattern("test"))));
		assertEquals("test{2,5}", TokenRules.toRegex(TokenRules.repeatBetween(TokenRules.pattern("test"), 2, 5)));
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
		assertEquals("option1|prefix\\w{1,3}", TokenRules.toRegex(complex2));
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
