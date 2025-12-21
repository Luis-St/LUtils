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
import net.luis.utils.io.token.actions.TokenAction;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Grammar}.<br>
 *
 * @author Luis-St
 */
class GrammarTest {
	
	private static @NonNull TokenRule createRule(@NonNull String value) {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NonNull TokenStream stream, @NonNull TokenRuleContext ctx) {
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
	void builderThrowsExceptionForNullBuilderFunction() {
		assertThrows(NullPointerException.class, () -> Grammar.builder(null));
	}
	
	@Test
	void builderCreatesGrammarWithEmptyRules() {
		Grammar grammar = Grammar.builder(builder -> {});
		
		assertNotNull(grammar);
		assertTrue(grammar.getRules().isEmpty());
	}
	
	@Test
	void builderCreatesGrammarWithSingleRule() {
		TokenRule rule = createRule("test");
		
		Grammar grammar = Grammar.builder(builder -> {
			builder.addRule(rule);
		});
		
		assertNotNull(grammar);
		assertEquals(1, grammar.getRules().size());
	}
	
	@Test
	void builderCreatesGrammarWithMultipleRules() {
		TokenRule rule1 = createRule("test1");
		TokenRule rule2 = createRule("test2");
		
		Grammar grammar = Grammar.builder(builder -> {
			builder.addRule(rule1);
			builder.addRule(rule2);
		});
		
		assertNotNull(grammar);
		assertEquals(2, grammar.getRules().size());
	}
	
	@Test
	void builderCreatesGrammarWithDefinedRules() {
		TokenRule rule1 = createRule("test1");
		TokenRule rule2 = createRule("test2");
		
		Grammar grammar = Grammar.builder(builder -> {
			builder.defineRule("rule1", rule1);
			builder.defineRule("rule2", rule2);
		});
		
		assertNotNull(grammar);
		assertTrue(grammar.getRules().isEmpty());
		assertNotNull(grammar.getContext().getRuleReference("rule1"));
		assertNotNull(grammar.getContext().getRuleReference("rule2"));
	}
	
	@Test
	void getContextReturnsNonNullContext() {
		Grammar grammar = Grammar.builder(builder -> {});
		
		TokenRuleContext context = grammar.getContext();
		
		assertNotNull(context);
	}
	
	@Test
	void getRulesReturnsUnmodifiableList() {
		TokenRule rule = createRule("test");
		Grammar grammar = Grammar.builder(builder -> {
			builder.addRule(rule);
		});
		
		List<GrammarRule> rules = grammar.getRules();
		
		assertThrows(UnsupportedOperationException.class, () -> rules.add(new GrammarRule(rule, TokenAction.identity())));
	}
	
	@Test
	void getRulesReturnsEmptyListForNoRules() {
		Grammar grammar = Grammar.builder(builder -> {});
		
		List<GrammarRule> rules = grammar.getRules();
		
		assertTrue(rules.isEmpty());
	}
	
	@Test
	void getRulesReturnsCorrectNumberOfRules() {
		TokenRule rule1 = createRule("test1");
		TokenRule rule2 = createRule("test2");
		
		Grammar grammar = Grammar.builder(builder -> {
			builder.addRule(rule1);
			builder.addRule(rule2);
		});
		
		List<GrammarRule> rules = grammar.getRules();
		
		assertEquals(2, rules.size());
	}
	
	@Test
	void parseThrowsExceptionForNullTokens() {
		Grammar grammar = Grammar.builder(builder -> {});
		
		assertThrows(NullPointerException.class, () -> grammar.parse(null));
	}
	
	@Test
	void parseReturnsListForEmptyTokens() {
		Grammar grammar = Grammar.builder(builder -> {});
		
		List<Token> result = grammar.parse(Collections.emptyList());
		
		assertNotNull(result);
	}
	
	@Test
	void parseProcessesTokensWithRules() {
		TokenRule rule = createRule("test");
		Token token = SimpleToken.createUnpositioned("test");
		
		Grammar grammar = Grammar.builder(builder -> {
			builder.addRule(rule);
		});
		
		List<Token> tokens = List.of(token);
		List<Token> result = grammar.parse(tokens);
		
		assertNotNull(result);
	}
}
