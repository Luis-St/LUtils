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

package net.luis.utils.grammar.parser;

import net.luis.utils.grammar.parser.action.GroupingTokenAction;
import net.luis.utils.grammar.parser.action.TokenAction;
import net.luis.utils.grammar.parser.action.core.GroupingMode;
import net.luis.utils.grammar.parser.rule.*;
import net.luis.utils.grammar.parser.rule.combinators.AnyOfTokenRule;
import net.luis.utils.grammar.parser.rule.core.ReferenceType;
import net.luis.utils.grammar.parser.rule.reference.ReferenceTokenRule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ParserBuilder}.<br>
 *
 * @author Luis-St
 */
class ParserBuilderTest {
	
	@Test
	void constructNewParserBuilder() {
		ParserBuilder builder = new ParserBuilder();
		
		Parser parser = assertDoesNotThrow(builder::build);
		assertTrue(parser.getRules().isEmpty());
		assertNotNull(parser.getContext());
	}
	
	@Test
	void defineRuleWithNullName() {
		ParserBuilder builder = new ParserBuilder();
		assertThrows(NullPointerException.class, () -> builder.defineRule(null, TokenRules.alwaysMatch()));
	}
	
	@Test
	void defineRuleWithNullRule() {
		ParserBuilder builder = new ParserBuilder();
		assertThrows(NullPointerException.class, () -> builder.defineRule("name", null));
	}
	
	@Test
	void defineRuleWithEmptyNameThrows() {
		ParserBuilder builder = new ParserBuilder();
		assertThrows(IllegalArgumentException.class, () -> builder.defineRule("", TokenRules.alwaysMatch()));
	}
	
	@Test
	void defineRuleWithDuplicateNameThrows() {
		ParserBuilder builder = new ParserBuilder();
		builder.defineRule("name", TokenRules.alwaysMatch());
		
		assertThrows(IllegalArgumentException.class, () -> builder.defineRule("name", TokenRules.alwaysMatch()));
	}
	
	@Test
	void ruleWithNullName() {
		ParserBuilder builder = new ParserBuilder();
		assertThrows(NullPointerException.class, () -> builder.rule(null, TokenRules.alwaysMatch()));
	}
	
	@Test
	void ruleWithNullRule() {
		ParserBuilder builder = new ParserBuilder();
		assertThrows(NullPointerException.class, () -> builder.rule("name", null));
	}
	
	@Test
	void ruleWithEmptyNameThrows() {
		ParserBuilder builder = new ParserBuilder();
		assertThrows(IllegalArgumentException.class, () -> builder.rule("", TokenRules.alwaysMatch()));
	}
	
	@Test
	void ruleWithActionWithNullName() {
		ParserBuilder builder = new ParserBuilder();
		TokenAction action = TokenAction.identity();
		
		assertThrows(NullPointerException.class, () -> builder.rule(null, TokenRules.alwaysMatch(), action));
	}
	
	@Test
	void ruleWithActionWithNullRule() {
		ParserBuilder builder = new ParserBuilder();
		TokenAction action = TokenAction.identity();
		
		assertThrows(NullPointerException.class, () -> builder.rule("name", null, action));
	}
	
	@Test
	void ruleWithActionWithNullAction() {
		ParserBuilder builder = new ParserBuilder();
		assertThrows(NullPointerException.class, () -> builder.rule("name", TokenRules.alwaysMatch(), null));
	}
	
	@Test
	void ruleWithActionWithEmptyNameThrows() {
		ParserBuilder builder = new ParserBuilder();
		TokenAction action = TokenAction.identity();
		
		assertThrows(IllegalArgumentException.class, () -> builder.rule("", TokenRules.alwaysMatch(), action));
	}
	
	@Test
	void defineRuleNameNotEmptyProceeds() {
		ParserBuilder builder = new ParserBuilder();
		TokenRule rule = TokenRules.alwaysMatch();
		
		assertDoesNotThrow(() -> builder.defineRule("value", rule));
		assertSame(rule, builder.build().getContext().getRuleReference("value"));
	}
	
	@Test
	void defineRuleNameNotAlreadyDefinedProceeds() {
		ParserBuilder builder = new ParserBuilder();
		TokenRule rule = TokenRules.alwaysMatch();
		
		assertDoesNotThrow(() -> builder.defineRule("fresh", rule));
		assertSame(rule, builder.build().getContext().getRuleReference("fresh"));
	}
	
	@Test
	void ruleNameNotEmptyProceeds() {
		ParserBuilder builder = new ParserBuilder();
		
		assertDoesNotThrow(() -> builder.rule("value", TokenRules.alwaysMatch()));
		List<ParserRule> rules = builder.build().getRules();
		assertEquals(1, rules.size());
		assertEquals("value", rules.getFirst().name());
	}
	
	@Test
	void ruleWithActionNameNotEmptyProceeds() {
		ParserBuilder builder = new ParserBuilder();
		TokenAction action = TokenAction.identity();
		
		assertDoesNotThrow(() -> builder.rule("value", TokenRules.alwaysMatch(), action));
		List<ParserRule> rules = builder.build().getRules();
		assertEquals(1, rules.size());
		assertSame(action, rules.getFirst().action());
	}
	
	@Test
	void wrapRuleActionNotGroupingTokenActionReturnsUnchanged() {
		ParserBuilder builder = new ParserBuilder();
		TokenRule rule = TokenRules.alwaysMatch();
		
		builder.rule("name", rule, TokenAction.identity());
		
		assertSame(rule, builder.build().getRules().getFirst().rule());
	}
	
	@Test
	void wrapRuleActionGroupingRuleNotAnyOfWraps() {
		ParserBuilder builder = new ParserBuilder();
		TokenRule rule = TokenRules.alwaysMatch();
		
		builder.rule("name", rule, new GroupingTokenAction("name", GroupingMode.MATCHED));
		TokenRule result = builder.build().getRules().getFirst().rule();
		
		assertInstanceOf(AnyOfTokenRule.class, result);
		AnyOfTokenRule anyOf = (AnyOfTokenRule) result;
		assertEquals(2, anyOf.tokenRules().size());
		assertSame(rule, anyOf.tokenRules().get(0));
		assertInstanceOf(TokenGroupRule.class, anyOf.tokenRules().get(1));
	}
	
	@Test
	void wrapRuleAnyOfWithMoreThanTwoRulesWraps() {
		ParserBuilder builder = new ParserBuilder();
		TokenRule rule = TokenRules.any(TokenRules.alwaysMatch(), TokenRules.neverMatch(), TokenRules.alwaysMatch());
		
		builder.rule("name", rule, new GroupingTokenAction("name", GroupingMode.MATCHED));
		TokenRule result = builder.build().getRules().getFirst().rule();
		
		assertInstanceOf(AnyOfTokenRule.class, result);
		AnyOfTokenRule anyOf = (AnyOfTokenRule) result;
		assertEquals(2, anyOf.tokenRules().size());
		assertSame(rule, anyOf.tokenRules().get(0));
		assertInstanceOf(TokenGroupRule.class, anyOf.tokenRules().get(1));
		assertSame(rule, ((TokenGroupRule) anyOf.tokenRules().get(1)).tokenRule());
	}
	
	@Test
	void wrapRuleAnyOfSizeTwoBothNonGroupRulesWraps() {
		ParserBuilder builder = new ParserBuilder();
		TokenRule rule = TokenRules.any(TokenRules.alwaysMatch(), TokenRules.neverMatch());
		
		builder.rule("name", rule, new GroupingTokenAction("name", GroupingMode.MATCHED));
		TokenRule result = builder.build().getRules().getFirst().rule();
		
		assertInstanceOf(AnyOfTokenRule.class, result);
		AnyOfTokenRule anyOf = (AnyOfTokenRule) result;
		assertEquals(2, anyOf.tokenRules().size());
		assertSame(rule, anyOf.tokenRules().get(0));
		assertInstanceOf(TokenGroupRule.class, anyOf.tokenRules().get(1));
	}
	
	@Test
	void wrapRuleAnyOfSizeTwoBothGroupRulesWraps() {
		ParserBuilder builder = new ParserBuilder();
		TokenRule rule = TokenRules.any(TokenRules.alwaysMatch().group(), TokenRules.neverMatch().group());
		
		builder.rule("name", rule, new GroupingTokenAction("name", GroupingMode.MATCHED));
		TokenRule result = builder.build().getRules().getFirst().rule();
		
		assertInstanceOf(AnyOfTokenRule.class, result);
		AnyOfTokenRule anyOf = (AnyOfTokenRule) result;
		assertEquals(2, anyOf.tokenRules().size());
		assertSame(rule, anyOf.tokenRules().get(0));
		assertInstanceOf(TokenGroupRule.class, anyOf.tokenRules().get(1));
	}
	
	@Test
	void wrapRuleAnyOfAlreadyProperlyWrappedReturnsUnchanged() {
		ParserBuilder builder = new ParserBuilder();
		TokenRule inner = TokenRules.alwaysMatch();
		TokenRule rule = TokenRules.any(inner, inner.group());
		
		builder.rule("name", rule, new GroupingTokenAction("name", GroupingMode.MATCHED));
		
		assertSame(rule, builder.build().getRules().getFirst().rule());
	}
	
	@Test
	void defineMultipleDistinctRulesAreAllRetrievable() {
		ParserBuilder builder = new ParserBuilder();
		TokenRule ruleA = TokenRules.alwaysMatch();
		TokenRule ruleB = TokenRules.neverMatch();
		TokenRule ruleC = TokenRules.value('x', false);
		builder.defineRule("a", ruleA);
		builder.defineRule("b", ruleB);
		builder.defineRule("c", ruleC);
		
		{}
		var context = builder.build().getContext();
		assertSame(ruleA, context.getRuleReference("a"));
		assertSame(ruleB, context.getRuleReference("b"));
		assertSame(ruleC, context.getRuleReference("c"));
	}
	
	@Test
	void ruleUsesDefaultGroupingActionWithMatchedMode() {
		ParserBuilder builder = new ParserBuilder();
		builder.rule("value", TokenRules.alwaysMatch());
		
		assertEquals(new GroupingTokenAction("value", GroupingMode.MATCHED), builder.build().getRules().getFirst().action());
	}
	
	@Test
	void ruleWithActionStoresCustomAction() {
		ParserBuilder builder = new ParserBuilder();
		TokenAction action = TokenAction.identity();
		builder.rule("value", TokenRules.alwaysMatch(), action);
		
		assertSame(action, builder.build().getRules().getFirst().action());
	}
	
	@Test
	void buildPreservesRuleDefinitionOrder() {
		ParserBuilder builder = new ParserBuilder();
		builder.rule("first", TokenRules.alwaysMatch());
		builder.rule("second", TokenRules.alwaysMatch());
		builder.rule("third", TokenRules.alwaysMatch());
		
		List<ParserRule> rules = builder.build().getRules();
		assertEquals(List.of("first", "second", "third"), rules.stream().map(ParserRule::name).toList());
	}
	
	@Test
	void buildReturnsParserWithEmptyRulesWhenNoneDefined() {
		ParserBuilder builder = new ParserBuilder();
		builder.defineRule("helper", TokenRules.alwaysMatch());
		
		Parser parser = builder.build();
		assertTrue(parser.getRules().isEmpty());
		assertNotNull(parser.getContext().getRuleReference("helper"));
	}
	
	@Test
	void defineRuleThenReferenceItInEmittingRule() {
		ParserBuilder builder = new ParserBuilder();
		builder.defineRule("helper", TokenRules.alwaysMatch());
		builder.rule("main", new ReferenceTokenRule("helper", ReferenceType.RULE));
		
		Parser parser = assertDoesNotThrow(builder::build);
		assertEquals(1, parser.getRules().size());
		assertEquals("main", parser.getRules().getFirst().name());
		assertNotNull(parser.getContext().getRuleReference("helper"));
	}
	
	@Test
	void mixedRuleDefinitionsWithVariousActionsAndWrappingBuildCorrectly() {
		ParserBuilder builder = new ParserBuilder();
		TokenRule plainRule = TokenRules.alwaysMatch();
		builder.rule("plain", plainRule, TokenAction.identity());
		
		TokenRule wrappedRule = TokenRules.alwaysMatch();
		builder.rule("wrapped", wrappedRule, new GroupingTokenAction("wrapped", GroupingMode.MATCHED));
		
		TokenRule unchangedInner = TokenRules.alwaysMatch();
		TokenRule unchangedRule = TokenRules.any(unchangedInner, unchangedInner.group());
		builder.rule("unchanged", unchangedRule, new GroupingTokenAction("unchanged", GroupingMode.MATCHED));
		
		List<ParserRule> rules = builder.build().getRules();
		assertEquals(3, rules.size());
		assertSame(plainRule, rules.get(0).rule());
		assertInstanceOf(AnyOfTokenRule.class, rules.get(1).rule());
		assertSame(unchangedRule, rules.get(2).rule());
	}
	
	@Test
	void defineRuleDuplicateAttemptDoesNotMutateExistingState() {
		ParserBuilder builder = new ParserBuilder();
		TokenRule ruleA = TokenRules.alwaysMatch();
		TokenRule ruleB = TokenRules.neverMatch();
		builder.defineRule("name", ruleA);
		builder.rule("emit", TokenRules.alwaysMatch());
		
		assertThrows(IllegalArgumentException.class, () -> builder.defineRule("name", ruleB));
		
		Parser parser = builder.build();
		assertSame(ruleA, parser.getContext().getRuleReference("name"));
		assertEquals(1, parser.getRules().size());
		assertEquals("emit", parser.getRules().getFirst().name());
	}
	
	@Test
	void builderReuseAcrossMultipleBuildCalls() {
		ParserBuilder builder = new ParserBuilder();
		builder.rule("first", TokenRules.alwaysMatch());
		Parser first = builder.build();
		
		builder.rule("second", TokenRules.alwaysMatch());
		Parser second = builder.build();
		
		assertEquals(1, first.getRules().size());
		assertEquals(2, second.getRules().size());
	}
}
