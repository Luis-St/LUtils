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

import net.luis.utils.io.token.rule.TokenRuleMatch;
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
			public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
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
		
		assertThrows(IllegalArgumentException.class, () -> rule.repeatExactly(0));
		assertThrows(IllegalArgumentException.class, () -> rule.repeatExactly(-1));
		assertThrows(IllegalArgumentException.class, () -> rule.repeatExactly(-3));
	}
	
	@Test
	void repeatExactlyWithValidCount() {
		TokenRule rule = createRule("test");
		
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
		
		assertThrows(IllegalArgumentException.class, () -> rule.repeatAtMost(0));
		assertThrows(IllegalArgumentException.class, () -> rule.repeatAtMost(-1));
		assertThrows(IllegalArgumentException.class, () -> rule.repeatAtMost(-7));
	}
	
	@Test
	void repeatAtMostWithValidMax() {
		TokenRule rule = createRule("test");
		
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
		TokenRule endRule = TokenRules.end();
		
		RepeatedTokenRule repeated1 = assertInstanceOf(RepeatedTokenRule.class, numberRule.repeatInfinitely());
		assertEquals(numberRule, repeated1.tokenRule());
		
		RepeatedTokenRule repeated2 = assertInstanceOf(RepeatedTokenRule.class, endRule.repeatInfinitely());
		assertEquals(endRule, repeated2.tokenRule());
	}
	
	@Test
	void repeatBetweenWithNegativeMin() {
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
		assertThrows(IllegalArgumentException.class, () -> rule.repeatBetween(1, 0));
	}
	
	@Test
	void repeatBetweenWithValidRange() {
		TokenRule rule = createRule("test");
		
		RepeatedTokenRule repeated2 = assertInstanceOf(RepeatedTokenRule.class, rule.repeatBetween(2, 5));
		assertEquals(2, repeated2.minOccurrences());
		assertEquals(5, repeated2.maxOccurrences());
		
		RepeatedTokenRule repeated3 = assertInstanceOf(RepeatedTokenRule.class, rule.repeatBetween(0, Integer.MAX_VALUE));
		assertEquals(0, repeated3.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeated3.maxOccurrences());
		
		RepeatedTokenRule repeated4 = assertInstanceOf(RepeatedTokenRule.class, rule.repeatBetween(7, 7));
		assertEquals(7, repeated4.minOccurrences());
		assertEquals(7, repeated4.maxOccurrences());
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
	void chainedOperations() {
		TokenRule rule = createRule("test");
		
		TokenRule chained1 = rule.optional().repeatAtLeast(1);
		assertInstanceOf(RepeatedTokenRule.class, chained1);
		RepeatedTokenRule repeated1 = (RepeatedTokenRule) chained1;
		assertInstanceOf(OptionalTokenRule.class, repeated1.tokenRule());
		
		TokenRule chained2 = rule.repeatExactly(3).optional();
		assertInstanceOf(OptionalTokenRule.class, chained2);
		OptionalTokenRule optional = (OptionalTokenRule) chained2;
		assertInstanceOf(RepeatedTokenRule.class, optional.tokenRule());
	}
	
	@Test
	void multipleChainedRepeats() {
		TokenRule rule = createRule("test");
		
		TokenRule chained = rule.repeatAtLeast(1).repeatAtMost(5).repeatExactly(3);
		assertInstanceOf(RepeatedTokenRule.class, chained);
		
		RepeatedTokenRule outer = (RepeatedTokenRule) chained;
		assertEquals(3, outer.minOccurrences());
		assertEquals(3, outer.maxOccurrences());
		assertInstanceOf(RepeatedTokenRule.class, outer.tokenRule());
	}
	
	@Test
	void operationsWithComplexRules() {
		TokenRule complexRule = TokenRules.sequence(
			TokenRules.pattern("\\d+"),
			TokenRules.any(createRule("a"), createRule("b"))
		);
		
		TokenRule optional = complexRule.optional();
		assertInstanceOf(OptionalTokenRule.class, optional);
		assertEquals(complexRule, ((OptionalTokenRule) optional).tokenRule());
		
		TokenRule repeated = complexRule.repeatBetween(2, 4);
		assertInstanceOf(RepeatedTokenRule.class, repeated);
		assertEquals(complexRule, ((RepeatedTokenRule) repeated).tokenRule());
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
		TokenRule endRule = TokenRules.end();
		
		assertInstanceOf(OptionalTokenRule.class, endRule.optional());
		assertInstanceOf(RepeatedTokenRule.class, endRule.repeatAtLeast(0));
		assertInstanceOf(RepeatedTokenRule.class, endRule.repeatExactly(1));
		assertInstanceOf(RepeatedTokenRule.class, endRule.repeatAtMost(2));
		assertInstanceOf(RepeatedTokenRule.class, endRule.repeatInfinitely());
		assertInstanceOf(RepeatedTokenRule.class, endRule.repeatBetween(1, 3));
	}
}
