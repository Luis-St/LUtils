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
	void defineWithTwoParametersThrowsExceptionForNullName() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		
		assertThrows(NullPointerException.class, () -> builder.define(null, rule));
	}
	
	@Test
	void defineWithTwoParametersThrowsExceptionForNullRule() {
		GrammarBuilder builder = new GrammarBuilder();
		
		assertThrows(NullPointerException.class, () -> builder.define("rule1", null));
	}
	
	@Test
	void defineWithTwoParametersThrowsExceptionForEmptyName() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> builder.define("", rule));
	}
	
	@Test
	void defineWithTwoParametersAddsRuleSuccessfully() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		
		assertDoesNotThrow(() -> builder.define("rule1", rule));
	}
	
	@Test
	void defineWithTwoParametersThrowsExceptionForDuplicateName() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule1 = createRule("test1");
		TokenRule rule2 = createRule("test2");
		
		builder.define("rule1", rule1);
		
		assertThrows(IllegalArgumentException.class, () -> builder.define("rule1", rule2));
	}
	
	@Test
	void defineWithThreeParametersThrowsExceptionForNullName() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		TokenAction action = TokenAction.identity();
		
		assertThrows(NullPointerException.class, () -> builder.define(null, rule, action));
	}
	
	@Test
	void defineWithThreeParametersThrowsExceptionForNullRule() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenAction action = TokenAction.identity();
		
		assertThrows(NullPointerException.class, () -> builder.define("rule1", null, action));
	}
	
	@Test
	void defineWithThreeParametersThrowsExceptionForEmptyName() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		TokenAction action = TokenAction.identity();
		
		assertThrows(IllegalArgumentException.class, () -> builder.define("", rule, action));
	}
	
	@Test
	void defineWithThreeParametersAddsRuleSuccessfully() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		TokenAction action = TokenAction.identity();
		
		assertDoesNotThrow(() -> builder.define("rule1", rule, action));
	}
	
	@Test
	void defineWithThreeParametersThrowsExceptionForDuplicateName() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule1 = createRule("test1");
		TokenRule rule2 = createRule("test2");
		TokenAction action = TokenAction.identity();
		
		builder.define("rule1", rule1, action);
		
		assertThrows(IllegalArgumentException.class, () -> builder.define("rule1", rule2, action));
	}
	
	@Test
	void defineWithThreeParametersWrapsRuleForGroupingAction() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		GroupingTokenAction action = GroupingTokenAction.INSTANCE;
		
		assertDoesNotThrow(() -> builder.define("rule1", rule, action));
	}
	
	@Test
	void defineWithThreeParametersWrapsAnyOfRuleForGroupingAction() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule innerRule1 = createRule("test1");
		TokenRule innerRule2 = createRule("test2");
		TokenRule innerRule3 = createRule("test3");
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(innerRule1, innerRule2, innerRule3));
		GroupingTokenAction action = GroupingTokenAction.INSTANCE;
		
		assertDoesNotThrow(() -> builder.define("rule1", rule, action));
	}
	
	@Test
	void defineWithThreeParametersDoesNotWrapAnyOfRuleWithTwoRulesAndOnlyOneGrouped() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenGroupRule groupRule = new TokenGroupRule(createRule("test1"));
		TokenRule normalRule = createRule("test2");
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(groupRule, normalRule));
		GroupingTokenAction action = GroupingTokenAction.INSTANCE;
		
		assertDoesNotThrow(() -> builder.define("rule1", rule, action));
	}
	
	@Test
	void buildReturnsGrammarInstance() {
		GrammarBuilder builder = new GrammarBuilder();
		
		Grammar grammar = builder.build();
		
		assertNotNull(grammar);
	}
	
	@Test
	void buildReturnsGrammarWithDefinedRules() {
		GrammarBuilder builder = new GrammarBuilder();
		TokenRule rule = createRule("test");
		builder.define("rule1", rule);
		
		Grammar grammar = builder.build();
		
		assertNotNull(grammar);
		assertTrue(grammar.getRuleNames().contains("rule1"));
	}
	
	@Test
	void buildReturnsGrammarWithEmptyRulesForNoDefinedRules() {
		GrammarBuilder builder = new GrammarBuilder();
		
		Grammar grammar = builder.build();
		
		assertNotNull(grammar);
		assertTrue(grammar.getRuleNames().isEmpty());
	}
}
