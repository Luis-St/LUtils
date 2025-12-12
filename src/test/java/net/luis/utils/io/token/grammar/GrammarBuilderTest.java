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

package net.luis.utils.io.token.grammar;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.actions.GroupingTokenAction;
import net.luis.utils.io.token.actions.TokenAction;
import net.luis.utils.io.token.actions.core.GroupingMode;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenGroupRule;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.rules.combinators.AnyOfTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link GrammarBuilder}.<br>
 *
 * @author Luis-St
 */
class GrammarBuilderTest {
	
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
	void defineRuleThrowsExceptionForNullName() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		
		assertThrows(NullPointerException.class, () -> builder.defineRule(null, rule));
	}
	
	@Test
	void defineRuleThrowsExceptionForNullRule() {
		GrammarBuilder builder = new GrammarBuilder();
		
		assertThrows(NullPointerException.class, () -> builder.defineRule("rule1", null));
	}
	
	@Test
	void defineRuleThrowsExceptionForEmptyName() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> builder.defineRule("", rule));
	}
	
	@Test
	void defineRuleAddsRuleSuccessfully() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		
		assertDoesNotThrow(() -> builder.defineRule("rule1", rule));
	}
	
	@Test
	void defineRuleThrowsExceptionForDuplicateName() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule1 = createRule("test1");
		TokenRule rule2 = createRule("test2");
		
		builder.defineRule("rule1", rule1);
		
		assertThrows(IllegalArgumentException.class, () -> builder.defineRule("rule1", rule2));
	}
	
	@Test
	void addRuleWithOneParameterThrowsExceptionForNullRule() {
		GrammarBuilder builder = new GrammarBuilder();
		
		assertThrows(NullPointerException.class, () -> builder.addRule(null));
	}
	
	@Test
	void addRuleWithOneParameterAddsRuleSuccessfully() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		
		assertDoesNotThrow(() -> builder.addRule(rule));
	}
	
	@Test
	void addRuleWithTwoParametersThrowsExceptionForNullRule() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenAction action = TokenAction.identity();
		
		assertThrows(NullPointerException.class, () -> builder.addRule(null, action));
	}
	
	@Test
	void addRuleWithTwoParametersThrowsExceptionForNullAction() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		
		assertThrows(NullPointerException.class, () -> builder.addRule(rule, null));
	}
	
	@Test
	void addRuleWithTwoParametersAddsRuleSuccessfully() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		TokenAction action = TokenAction.identity();
		
		assertDoesNotThrow(() -> builder.addRule(rule, action));
	}
	
	@Test
	void addRuleWithGroupingActionWrapsRule() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		
		assertDoesNotThrow(() -> builder.addRule(rule, action));
		
		Grammar grammar = builder.build();
		List<GrammarRule> rules = grammar.getRules();
		
		assertEquals(1, rules.size());
		assertInstanceOf(AnyOfTokenRule.class, rules.getFirst().rule());
	}
	
	@Test
	void addRuleWithGroupingActionWrapsAnyOfRuleWithThreeRules() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule innerRule1 = createRule("test1");
		TokenRule innerRule2 = createRule("test2");
		TokenRule innerRule3 = createRule("test3");
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(innerRule1, innerRule2, innerRule3));
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		
		assertDoesNotThrow(() -> builder.addRule(rule, action));
		
		Grammar grammar = builder.build();
		List<GrammarRule> rules = grammar.getRules();
		
		assertEquals(1, rules.size());
		assertInstanceOf(AnyOfTokenRule.class, rules.get(0).rule());
	}
	
	@Test
	void addRuleWithGroupingActionDoesNotWrapAnyOfRuleWithTwoRulesAndOnlyOneGrouped() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenGroupRule groupRule = new TokenGroupRule(createRule("test1"));
		TokenRule normalRule = createRule("test2");
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(groupRule, normalRule));
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		
		assertDoesNotThrow(() -> builder.addRule(rule, action));
		
		Grammar grammar = builder.build();
		List<GrammarRule> rules = grammar.getRules();
		
		assertEquals(1, rules.size());
		assertSame(rule, rules.getFirst().rule());
	}
	
	@Test
	void addRuleWithGroupingActionWrapsAnyOfRuleWithTwoGroupedRules() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenGroupRule groupRule1 = new TokenGroupRule(createRule("test1"));
		TokenGroupRule groupRule2 = new TokenGroupRule(createRule("test2"));
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(groupRule1, groupRule2));
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		
		assertDoesNotThrow(() -> builder.addRule(rule, action));
		
		Grammar grammar = builder.build();
		List<GrammarRule> rules = grammar.getRules();
		
		assertEquals(1, rules.size());
		assertInstanceOf(AnyOfTokenRule.class, rules.getFirst().rule());
	}
	
	@Test
	void addRuleWithGroupingActionWrapsAnyOfRuleWithTwoNonGroupedRules() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule normalRule1 = createRule("test1");
		TokenRule normalRule2 = createRule("test2");
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(normalRule1, normalRule2));
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		
		assertDoesNotThrow(() -> builder.addRule(rule, action));
		
		Grammar grammar = builder.build();
		List<GrammarRule> rules = grammar.getRules();
		
		assertEquals(1, rules.size());
		assertInstanceOf(AnyOfTokenRule.class, rules.getFirst().rule());
	}
	
	@Test
	void buildReturnsGrammarInstance() {
		GrammarBuilder builder = new GrammarBuilder();
		
		Grammar grammar = builder.build();
		
		assertNotNull(grammar);
	}
	
	@Test
	void buildReturnsGrammarWithAddedRules() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		builder.addRule(rule);
		
		Grammar grammar = builder.build();
		
		assertNotNull(grammar);
		assertEquals(1, grammar.getRules().size());
	}
	
	@Test
	void buildReturnsGrammarWithEmptyRulesForNoAddedRules() {
		GrammarBuilder builder = new GrammarBuilder();
		
		Grammar grammar = builder.build();
		
		assertNotNull(grammar);
		assertTrue(grammar.getRules().isEmpty());
	}
	
	@Test
	void addRuleWithWrapParameterFalseDoesNotWrapRule() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		
		assertDoesNotThrow(() -> builder.addRule(rule, action, false));
		
		Grammar grammar = builder.build();
		List<GrammarRule> rules = grammar.getRules();
		
		assertEquals(1, rules.size());
		assertSame(rule, rules.getFirst().rule());
	}
	
	@Test
	void addRuleWithWrapParameterTrueWrapsRule() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		GroupingTokenAction action = new GroupingTokenAction(GroupingMode.MATCHED);
		
		assertDoesNotThrow(() -> builder.addRule(rule, action, true));
		
		Grammar grammar = builder.build();
		List<GrammarRule> rules = grammar.getRules();
		
		assertEquals(1, rules.size());
		assertInstanceOf(AnyOfTokenRule.class, rules.getFirst().rule());
		assertNotSame(rule, rules.getFirst().rule());
	}
	
	@Test
	void addRuleWithBooleanParameterThrowsExceptionForNullRule() {
		GrammarBuilder builder = new GrammarBuilder();
		
		assertThrows(NullPointerException.class, () -> builder.addRule(null, false));
	}
	
	@Test
	void addRuleWithBooleanParameterAddsRuleSuccessfully() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		
		assertDoesNotThrow(() -> builder.addRule(rule, false));
	}
	
	@Test
	void addRuleWithThreeParametersThrowsExceptionForNullRule() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenAction action = TokenAction.identity();
		
		assertThrows(NullPointerException.class, () -> builder.addRule(null, action, false));
	}
	
	@Test
	void addRuleWithThreeParametersThrowsExceptionForNullAction() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		
		assertThrows(NullPointerException.class, () -> builder.addRule(rule, null, false));
	}
	
	@Test
	void addRuleWithWrapFalseAndIdentityActionDoesNotWrap() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		
		assertDoesNotThrow(() -> builder.addRule(rule, false));
		
		Grammar grammar = builder.build();
		List<GrammarRule> rules = grammar.getRules();
		
		assertEquals(1, rules.size());
		assertSame(rule, rules.getFirst().rule());
	}
	
	@Test
	void defineRuleDoesNotAddToRulesList() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		builder.defineRule("rule1", rule);
		
		Grammar grammar = builder.build();
		
		assertTrue(grammar.getRules().isEmpty());
	}
	
	@Test
	void addRuleAddsToRulesListButNotContext() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		builder.addRule(rule);
		
		Grammar grammar = builder.build();
		
		assertEquals(1, grammar.getRules().size());
		assertNull(grammar.getContext().getRuleReference("test"));
	}
	
	@Test
	void defineRuleAndAddRuleCombinedCorrectly() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule1 = createRule("test1");
		TokenRule rule2 = createRule("test2");
		
		builder.defineRule("rule1", rule1);
		builder.addRule(rule2);
		
		Grammar grammar = builder.build();
		
		assertEquals(1, grammar.getRules().size());
		assertNotNull(grammar.getContext().getRuleReference("rule1"));
	}
}
