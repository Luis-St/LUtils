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
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Grammar}.<br>
 *
 * @author Luis-St
 */
class GrammarTest {
	
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
	void builderThrowsExceptionForNullBuilderFunction() {
		assertThrows(NullPointerException.class, () -> Grammar.builder(null));
	}
	
	@Test
	void builderCreatesGrammarWithEmptyRules() {
		Grammar grammar = Grammar.builder(builder -> {});
		
		assertNotNull(grammar);
		assertTrue(grammar.getRuleNames().isEmpty());
	}
	
	@Test
	void builderCreatesGrammarWithSingleRule() {
		TokenRule rule = createRule("test");
		
		Grammar grammar = Grammar.builder(builder -> {
			builder.define("rule1", rule);
		});
		
		assertNotNull(grammar);
		assertEquals(1, grammar.getRuleNames().size());
		assertTrue(grammar.getRuleNames().contains("rule1"));
	}
	
	@Test
	void builderCreatesGrammarWithMultipleRules() {
		TokenRule rule1 = createRule("test1");
		TokenRule rule2 = createRule("test2");
		
		Grammar grammar = Grammar.builder(builder -> {
			builder.define("rule1", rule1);
			builder.define("rule2", rule2);
		});
		
		assertNotNull(grammar);
		assertEquals(2, grammar.getRuleNames().size());
		assertTrue(grammar.getRuleNames().contains("rule1"));
		assertTrue(grammar.getRuleNames().contains("rule2"));
	}
	
	@Test
	void getContextReturnsNonNullContext() {
		Grammar grammar = Grammar.builder(builder -> {});
		
		TokenRuleContext context = grammar.getContext();
		
		assertNotNull(context);
	}
	
	@Test
	void getRuleNamesReturnsUnmodifiableSet() {
		TokenRule rule = createRule("test");
		Grammar grammar = Grammar.builder(builder -> {
			builder.define("rule1", rule);
		});
		
		Set<String> ruleNames = grammar.getRuleNames();
		
		assertThrows(UnsupportedOperationException.class, () -> ruleNames.add("rule2"));
	}
	
	@Test
	void getRuleNamesReturnsEmptySetForNoRules() {
		Grammar grammar = Grammar.builder(builder -> {});
		
		Set<String> ruleNames = grammar.getRuleNames();
		
		assertTrue(ruleNames.isEmpty());
	}
	
	@Test
	void getRuleNamesReturnsCorrectRuleNames() {
		TokenRule rule1 = createRule("test1");
		TokenRule rule2 = createRule("test2");
		
		Grammar grammar = Grammar.builder(builder -> {
			builder.define("rule1", rule1);
			builder.define("rule2", rule2);
		});
		
		Set<String> ruleNames = grammar.getRuleNames();
		
		assertEquals(2, ruleNames.size());
		assertTrue(ruleNames.contains("rule1"));
		assertTrue(ruleNames.contains("rule2"));
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
			builder.define("rule1", rule);
		});
		
		List<Token> tokens = List.of(token);
		List<Token> result = grammar.parse(tokens);
		
		assertNotNull(result);
	}
}
