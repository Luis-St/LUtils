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

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.assertions.*;
import net.luis.utils.io.token.rule.rules.quantifiers.*;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRule}.<br>
 *
 * @author Luis-St
 */
class TokenRuleTest {
	
	private static @NotNull TokenRule createRule(@NotNull String value) {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
				return null;
			}
		};
	}
	
	@Test
	void optionalReturnsOptionalTokenRule() {
		TokenRule rule = createRule("test");
		
		TokenRule optional = rule.optional();
		
		assertInstanceOf(OptionalTokenRule.class, optional);
		assertEquals(rule, ((OptionalTokenRule) optional).tokenRule());
	}
	
	@Test
	void optionalWithDifferentRules() {
		TokenRule numberRule = TokenRules.pattern("\\d+");
		TokenRule alwaysRule = TokenRules.alwaysMatch();
		
		assertInstanceOf(OptionalTokenRule.class, numberRule.optional());
		assertInstanceOf(OptionalTokenRule.class, alwaysRule.optional());
	}
	
	@Test
	void repeatAtLeastWithNegativeMin() {
		TokenRule rule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> rule.repeatAtLeast(-1));
		assertThrows(IllegalArgumentException.class, () -> rule.repeatAtLeast(-5));
	}
	
	@Test
	void repeatAtLeastWithValidMin() {
		TokenRule rule = createRule("test");
		
		RepeatedTokenRule repeated0 = assertInstanceOf(RepeatedTokenRule.class, rule.repeatAtLeast(0));
		assertEquals(0, repeated0.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeated0.maxOccurrences());
		
		RepeatedTokenRule repeated2 = assertInstanceOf(RepeatedTokenRule.class, rule.repeatAtLeast(2));
		assertEquals(2, repeated2.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeated2.maxOccurrences());
		
		RepeatedTokenRule repeated10 = assertInstanceOf(RepeatedTokenRule.class, rule.repeatAtLeast(10));
		assertEquals(10, repeated10.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeated10.maxOccurrences());
	}
	
	@Test
	void repeatExactlyWithNegativeCount() {
		TokenRule rule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> rule.repeatExactly(-1));
		assertThrows(IllegalArgumentException.class, () -> rule.repeatExactly(-3));
	}
	
	@Test
	void repeatExactlyWithValidCount() {
		TokenRule rule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> rule.repeatExactly(0));
		
		RepeatedTokenRule repeated1 = assertInstanceOf(RepeatedTokenRule.class, rule.repeatExactly(1));
		assertEquals(1, repeated1.minOccurrences());
		assertEquals(1, repeated1.maxOccurrences());
		
		RepeatedTokenRule repeated5 = assertInstanceOf(RepeatedTokenRule.class, rule.repeatExactly(5));
		assertEquals(5, repeated5.minOccurrences());
		assertEquals(5, repeated5.maxOccurrences());
	}
	
	@Test
	void repeatAtMostWithNegativeMax() {
		TokenRule rule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> rule.repeatAtMost(-1));
		assertThrows(IllegalArgumentException.class, () -> rule.repeatAtMost(-7));
	}
	
	@Test
	void repeatAtMostWithValidMax() {
		TokenRule rule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> rule.repeatAtMost(0));
		
		RepeatedTokenRule repeated3 = assertInstanceOf(RepeatedTokenRule.class, rule.repeatAtMost(3));
		assertEquals(0, repeated3.minOccurrences());
		assertEquals(3, repeated3.maxOccurrences());
		
		RepeatedTokenRule repeated100 = assertInstanceOf(RepeatedTokenRule.class, rule.repeatAtMost(100));
		assertEquals(0, repeated100.minOccurrences());
		assertEquals(100, repeated100.maxOccurrences());
	}
	
	@Test
	void repeatInfinitelyReturnsCorrectRule() {
		TokenRule rule = createRule("test");
		
		RepeatedTokenRule repeated = assertInstanceOf(RepeatedTokenRule.class, rule.repeatInfinitely());
		
		assertEquals(0, repeated.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeated.maxOccurrences());
		assertEquals(rule, repeated.tokenRule());
	}
	
	@Test
	void repeatInfinitelyWithDifferentRules() {
		TokenRule numberRule = TokenRules.pattern("\\d+");
		TokenRule endRule = TokenRules.endDocument();
		
		RepeatedTokenRule repeated1 = assertInstanceOf(RepeatedTokenRule.class, numberRule.repeatInfinitely());
		assertEquals(numberRule, repeated1.tokenRule());
		
		RepeatedTokenRule repeated2 = assertInstanceOf(RepeatedTokenRule.class, endRule.repeatInfinitely());
		assertEquals(endRule, repeated2.tokenRule());
	}
	
	@Test
	void repeatBetweenWithInvalidParameters() {
		TokenRule rule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> rule.repeatBetween(0, 0));
		assertThrows(IllegalArgumentException.class, () -> rule.repeatBetween(-1, 5));
		assertThrows(IllegalArgumentException.class, () -> rule.repeatBetween(-10, 0));
	}
	
	@Test
	void repeatBetweenWithMaxLessThanMin() {
		TokenRule rule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> rule.repeatBetween(5, 3));
		assertThrows(IllegalArgumentException.class, () -> rule.repeatBetween(10, 5));
		assertThrows(IllegalArgumentException.class, () -> rule.repeatBetween(2, 1));
	}
	
	@Test
	void repeatBetweenWithValidRange() {
		TokenRule rule = createRule("test");
		
		RepeatedTokenRule repeated1 = assertInstanceOf(RepeatedTokenRule.class, rule.repeatBetween(1, 5));
		assertEquals(1, repeated1.minOccurrences());
		assertEquals(5, repeated1.maxOccurrences());
		
		RepeatedTokenRule repeated2 = assertInstanceOf(RepeatedTokenRule.class, rule.repeatBetween(0, Integer.MAX_VALUE));
		assertEquals(0, repeated2.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeated2.maxOccurrences());
		
		RepeatedTokenRule repeated3 = assertInstanceOf(RepeatedTokenRule.class, rule.repeatBetween(7, 7));
		assertEquals(7, repeated3.minOccurrences());
		assertEquals(7, repeated3.maxOccurrences());
	}
	
	@Test
	void repeatBetweenWithEqualMinMax() {
		TokenRule rule = createRule("test");
		
		for (int i = 1; i <= 10; i++) {
			RepeatedTokenRule repeated = assertInstanceOf(RepeatedTokenRule.class, rule.repeatBetween(i, i));
			assertEquals(i, repeated.minOccurrences());
			assertEquals(i, repeated.maxOccurrences());
		}
	}
	
	@Test
	void lookaheadReturnsPositiveLookaheadTokenRule() {
		TokenRule rule = createRule("test");
		
		TokenRule lookaheadRule = rule.lookahead();
		
		LookaheadTokenRule lookahead = assertInstanceOf(LookaheadTokenRule.class, lookaheadRule);
		assertEquals(rule, lookahead.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, lookahead.mode());
	}
	
	@Test
	void lookaheadWithDifferentRules() {
		TokenRule numberRule = TokenRules.pattern("\\d+");
		TokenRule sequenceRule = TokenRules.sequence(createRule("a"), createRule("b"));
		
		LookaheadTokenRule lookahead1 = assertInstanceOf(LookaheadTokenRule.class, numberRule.lookahead());
		assertEquals(numberRule, lookahead1.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, lookahead1.mode());
		
		LookaheadTokenRule lookahead2 = assertInstanceOf(LookaheadTokenRule.class, sequenceRule.lookahead());
		assertEquals(sequenceRule, lookahead2.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, lookahead2.mode());
	}
	
	@Test
	void negativeLookaheadReturnsNegativeLookaheadTokenRule() {
		TokenRule rule = createRule("test");
		
		TokenRule negativeLookaheadRule = rule.negativeLookahead();
		
		LookaheadTokenRule lookahead = assertInstanceOf(LookaheadTokenRule.class, negativeLookaheadRule);
		assertEquals(rule, lookahead.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, lookahead.mode());
	}
	
	@Test
	void negativeLookaheadWithDifferentRules() {
		TokenRule patternRule = TokenRules.pattern("[a-z]+");
		TokenRule optionalRule = createRule("test").optional();
		
		LookaheadTokenRule negativeLookahead1 = assertInstanceOf(LookaheadTokenRule.class, patternRule.negativeLookahead());
		assertEquals(patternRule, negativeLookahead1.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, negativeLookahead1.mode());
		
		LookaheadTokenRule negativeLookahead2 = assertInstanceOf(LookaheadTokenRule.class, optionalRule.negativeLookahead());
		assertEquals(optionalRule, negativeLookahead2.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, negativeLookahead2.mode());
	}
	
	@Test
	void lookbehindReturnsPositiveLookbehindTokenRule() {
		TokenRule rule = createRule("test");
		
		TokenRule lookbehindRule = rule.lookbehind();
		
		LookbehindTokenRule lookbehind = assertInstanceOf(LookbehindTokenRule.class, lookbehindRule);
		assertEquals(rule, lookbehind.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, lookbehind.mode());
	}
	
	@Test
	void lookbehindWithDifferentRules() {
		TokenRule boundaryRule = TokenRules.boundary(createRule("start"), createRule("end"));
		TokenRule repeatedRule = createRule("x").repeatAtLeast(2);
		
		LookbehindTokenRule lookbehind1 = assertInstanceOf(LookbehindTokenRule.class, boundaryRule.lookbehind());
		assertEquals(boundaryRule, lookbehind1.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, lookbehind1.mode());
		
		LookbehindTokenRule lookbehind2 = assertInstanceOf(LookbehindTokenRule.class, repeatedRule.lookbehind());
		assertEquals(repeatedRule, lookbehind2.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, lookbehind2.mode());
	}
	
	@Test
	void negativeLookbehindReturnsNegativeLookbehindTokenRule() {
		TokenRule rule = createRule("test");
		
		TokenRule negativeLookbehindRule = rule.negativeLookbehind();
		
		LookbehindTokenRule lookbehind = assertInstanceOf(LookbehindTokenRule.class, negativeLookbehindRule);
		assertEquals(rule, lookbehind.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, lookbehind.mode());
	}
	
	@Test
	void negativeLookbehindWithDifferentRules() {
		TokenRule lengthRule = TokenRules.lengthBetween(3, 5);
		TokenRule anyRule = TokenRules.any(createRule("a"), createRule("b"));
		
		LookbehindTokenRule negativeLookbehind1 = assertInstanceOf(LookbehindTokenRule.class, lengthRule.negativeLookbehind());
		assertEquals(lengthRule, negativeLookbehind1.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, negativeLookbehind1.mode());
		
		LookbehindTokenRule negativeLookbehind2 = assertInstanceOf(LookbehindTokenRule.class, anyRule.negativeLookbehind());
		assertEquals(anyRule, negativeLookbehind2.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, negativeLookbehind2.mode());
	}
	
	@Test
	void groupReturnsTokenGroupRule() {
		TokenRule rule = createRule("test");
		
		TokenRule groupRule = rule.group();
		
		assertEquals(rule, assertInstanceOf(TokenGroupRule.class, groupRule).tokenRule());
	}
	
	@Test
	void groupWithDifferentRules() {
		TokenRule startRule = TokenRules.startDocument();
		TokenRule complexRule = TokenRules.sequence(
			TokenRules.pattern("\\d+"),
			TokenRules.optional(TokenRules.pattern("[a-z]+")),
			TokenRules.endDocument()
		);
		
		assertEquals(startRule, assertInstanceOf(TokenGroupRule.class, startRule.group()).tokenRule());
		assertEquals(complexRule, assertInstanceOf(TokenGroupRule.class, complexRule.group()).tokenRule());
	}
	
	@Test
	void chainedOperations() {
		TokenRule rule = createRule("test");
		
		TokenRule chained1 = rule.optional().repeatAtLeast(1);
		RepeatedTokenRule repeated1 = assertInstanceOf(RepeatedTokenRule.class, chained1);
		assertInstanceOf(OptionalTokenRule.class, repeated1.tokenRule());
		
		TokenRule chained2 = rule.repeatExactly(3).optional();
		OptionalTokenRule optional = assertInstanceOf(OptionalTokenRule.class, chained2);
		assertInstanceOf(RepeatedTokenRule.class, optional.tokenRule());
	}
	
	@Test
	void chainedLookaroundOperations() {
		TokenRule rule = createRule("test");
		
		TokenRule chained1 = rule.lookahead().lookbehind();
		LookbehindTokenRule lookbehind = assertInstanceOf(LookbehindTokenRule.class, chained1);
		assertInstanceOf(LookaheadTokenRule.class, lookbehind.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, lookbehind.mode());
		
		TokenRule chained2 = rule.negativeLookahead().negativeLookbehind();
		LookbehindTokenRule negativeLookbehind = assertInstanceOf(LookbehindTokenRule.class, chained2);
		assertInstanceOf(LookaheadTokenRule.class, negativeLookbehind.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, negativeLookbehind.mode());
	}
	
	@Test
	void chainedGroupOperations() {
		TokenRule rule = createRule("test");
		
		TokenRule groupRepeated = rule.group().repeatExactly(3);
		RepeatedTokenRule repeated = assertInstanceOf(RepeatedTokenRule.class, groupRepeated);
		assertInstanceOf(TokenGroupRule.class, repeated.tokenRule());
		
		TokenRule groupOptional = rule.group().optional();
		OptionalTokenRule optional = assertInstanceOf(OptionalTokenRule.class, groupOptional);
		assertInstanceOf(TokenGroupRule.class, optional.tokenRule());
	}
	
	@Test
	void multipleChainedRepeats() {
		TokenRule rule = createRule("test");
		
		TokenRule chained = rule.repeatAtLeast(1).repeatAtMost(5).repeatExactly(3);
		RepeatedTokenRule outer = assertInstanceOf(RepeatedTokenRule.class, chained);
		assertEquals(3, outer.minOccurrences());
		assertEquals(3, outer.maxOccurrences());
		assertInstanceOf(RepeatedTokenRule.class, outer.tokenRule());
	}
	
	@Test
	void complexChainedOperations() {
		TokenRule rule = createRule("test");
		
		TokenRule complex = rule.optional()
			.repeatAtLeast(1)
			.lookahead()
			.group();
		
		
		TokenGroupRule group = assertInstanceOf(TokenGroupRule.class, complex);
		LookaheadTokenRule lookahead = assertInstanceOf(LookaheadTokenRule.class, group.tokenRule());
		RepeatedTokenRule repeated = assertInstanceOf(RepeatedTokenRule.class, lookahead.tokenRule());
		OptionalTokenRule optional = assertInstanceOf(OptionalTokenRule.class, repeated.tokenRule());
		assertEquals(rule, optional.tokenRule());
	}
	
	@Test
	void operationsWithComplexRules() {
		TokenRule complexRule = TokenRules.sequence(
			TokenRules.pattern("\\d+"),
			TokenRules.any(createRule("a"), createRule("b"))
		);
		
		TokenRule optional = complexRule.optional();
		assertEquals(complexRule, assertInstanceOf(OptionalTokenRule.class, optional).tokenRule());
		
		TokenRule repeated = complexRule.repeatBetween(2, 4);
		assertEquals(complexRule, assertInstanceOf(RepeatedTokenRule.class, repeated).tokenRule());
	}
	
	@Test
	void newOperationsWithComplexRules() {
		TokenRule complexRule = TokenRules.sequence(
			TokenRules.pattern("\\d+"),
			TokenRules.any(createRule("a"), createRule("b")),
			TokenRules.optional(TokenRules.pattern("[A-Z]+"))
		);
		
		LookaheadTokenRule lookahead = assertInstanceOf(LookaheadTokenRule.class, complexRule.lookahead());
		assertEquals(complexRule, lookahead.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, lookahead.mode());
		
		LookbehindTokenRule lookbehind = assertInstanceOf(LookbehindTokenRule.class, complexRule.lookbehind());
		assertEquals(complexRule, lookbehind.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, lookbehind.mode());
		
		TokenGroupRule group = assertInstanceOf(TokenGroupRule.class, complexRule.group());
		assertEquals(complexRule, group.tokenRule());
	}
	
	@Test
	void boundaryValueTesting() {
		TokenRule rule = createRule("test");
		
		RepeatedTokenRule maxRule = assertInstanceOf(RepeatedTokenRule.class, rule.repeatBetween(Integer.MAX_VALUE - 1, Integer.MAX_VALUE));
		assertEquals(Integer.MAX_VALUE - 1, maxRule.minOccurrences());
		assertEquals(Integer.MAX_VALUE, maxRule.maxOccurrences());
	}
	
	@Test
	void operationPreservesOriginalRule() {
		TokenRule originalRule = createRule("original");
		
		TokenRule optional = originalRule.optional();
		TokenRule repeated = originalRule.repeatExactly(5);
		
		assertEquals(originalRule, ((OptionalTokenRule) optional).tokenRule());
		assertEquals(originalRule, ((RepeatedTokenRule) repeated).tokenRule());
	}
	
	@Test
	void allOperationsPreserveOriginalRule() {
		TokenRule originalRule = createRule("original");
		
		assertEquals(originalRule, ((LookaheadTokenRule) originalRule.lookahead()).tokenRule());
		assertEquals(originalRule, ((LookaheadTokenRule) originalRule.negativeLookahead()).tokenRule());
		assertEquals(originalRule, ((LookbehindTokenRule) originalRule.lookbehind()).tokenRule());
		assertEquals(originalRule, ((LookbehindTokenRule) originalRule.negativeLookbehind()).tokenRule());
		assertEquals(originalRule, ((TokenGroupRule) originalRule.group()).tokenRule());
	}
	
	@Test
	void operationsReturnDifferentInstances() {
		TokenRule rule = createRule("test");
		
		TokenRule optional1 = rule.optional();
		TokenRule optional2 = rule.optional();
		TokenRule repeated1 = rule.repeatExactly(1);
		TokenRule repeated2 = rule.repeatExactly(1);
		
		assertNotSame(optional1, optional2);
		assertNotSame(repeated1, repeated2);
		assertNotSame(optional1, repeated1);
	}
	
	@Test
	void lookaroundOperationsReturnDifferentInstances() {
		TokenRule rule = createRule("test");
		
		TokenRule lookahead1 = rule.lookahead();
		TokenRule lookahead2 = rule.lookahead();
		TokenRule negativeLookahead1 = rule.negativeLookahead();
		TokenRule negativeLookahead2 = rule.negativeLookahead();
		TokenRule lookbehind1 = rule.lookbehind();
		TokenRule lookbehind2 = rule.lookbehind();
		TokenRule negativeLookbehind1 = rule.negativeLookbehind();
		TokenRule negativeLookbehind2 = rule.negativeLookbehind();
		
		assertNotSame(lookahead1, lookahead2);
		assertNotSame(negativeLookahead1, negativeLookahead2);
		assertNotSame(lookbehind1, lookbehind2);
		assertNotSame(negativeLookbehind1, negativeLookbehind2);
		
		assertNotSame(lookahead1, negativeLookahead1);
		assertNotSame(lookbehind1, negativeLookbehind1);
	}
	
	@Test
	void groupOperationsReturnDifferentInstances() {
		TokenRule rule = createRule("test");
		
		TokenRule group1 = rule.group();
		TokenRule group2 = rule.group();
		
		assertNotSame(group1, group2);
	}
	
	@Test
	void operationsWithAlwaysMatchRule() {
		TokenRule alwaysMatch = TokenRules.alwaysMatch();
		
		assertInstanceOf(OptionalTokenRule.class, alwaysMatch.optional());
		assertInstanceOf(RepeatedTokenRule.class, alwaysMatch.repeatAtLeast(1));
		assertInstanceOf(RepeatedTokenRule.class, alwaysMatch.repeatExactly(3));
		assertInstanceOf(RepeatedTokenRule.class, alwaysMatch.repeatAtMost(5));
		assertInstanceOf(RepeatedTokenRule.class, alwaysMatch.repeatInfinitely());
		assertInstanceOf(RepeatedTokenRule.class, alwaysMatch.repeatBetween(2, 7));
	}
	
	@Test
	void operationsWithEndRule() {
		TokenRule endRule = TokenRules.endDocument();
		
		assertInstanceOf(OptionalTokenRule.class, endRule.optional());
		assertInstanceOf(RepeatedTokenRule.class, endRule.repeatAtLeast(1));
		assertInstanceOf(RepeatedTokenRule.class, endRule.repeatExactly(1));
		assertInstanceOf(RepeatedTokenRule.class, endRule.repeatAtMost(2));
		assertInstanceOf(RepeatedTokenRule.class, endRule.repeatInfinitely());
		assertInstanceOf(RepeatedTokenRule.class, endRule.repeatBetween(1, 3));
	}
	
	@Test
	void newOperationsWithSingletonRules() {
		TokenRule alwaysMatch = TokenRules.alwaysMatch();
		TokenRule start = TokenRules.startDocument();
		TokenRule end = TokenRules.endDocument();
		
		TokenRule[] singletonRules = {alwaysMatch, start, end};
		for (TokenRule singletonRule : singletonRules) {
			assertInstanceOf(LookaheadTokenRule.class, singletonRule.lookahead());
			assertInstanceOf(LookaheadTokenRule.class, singletonRule.negativeLookahead());
			assertInstanceOf(LookbehindTokenRule.class, singletonRule.lookbehind());
			assertInstanceOf(LookbehindTokenRule.class, singletonRule.negativeLookbehind());
			assertInstanceOf(TokenGroupRule.class, singletonRule.group());
		}
	}
	
	@Test
	void mixedOldAndNewOperationsChaining() {
		TokenRule rule = createRule("test");
		
		TokenRule mixed1 = rule.repeatExactly(2).lookahead().optional();
		assertInstanceOf(OptionalTokenRule.class, mixed1);
		
		TokenRule mixed3 = rule.optional()
			.repeatBetween(1, 3)
			.lookahead()
			.group()
			.lookbehind();
		assertInstanceOf(LookbehindTokenRule.class, mixed3);
	}
}
