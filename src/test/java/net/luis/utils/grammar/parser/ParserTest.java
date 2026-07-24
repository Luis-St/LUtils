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

import net.luis.utils.grammar.parser.action.TokenAction;
import net.luis.utils.grammar.parser.action.TokenActions;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.rule.TokenRules;
import net.luis.utils.grammar.token.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Parser}.<br>
 *
 * @author Luis-St
 */
class ParserTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructWithValidContextAndRules() {
		TokenRuleContext context = TokenRuleContext.empty();
		Parser parser = new Parser(context, List.of());
		
		assertSame(context, parser.getContext());
		assertTrue(parser.getRules().isEmpty());
	}
	
	@Test
	void constructWithNullContext() {
		assertThrows(NullPointerException.class, () -> new Parser(null, List.of()));
	}
	
	@Test
	void constructWithNullRules() {
		assertThrows(NullPointerException.class, () -> new Parser(TokenRuleContext.empty(), null));
	}
	
	@Test
	void constructWithRulesListContainingNullElement() {
		List<ParserRule> rulesWithNull = new ArrayList<>();
		rulesWithNull.add(null);
		
		assertThrows(NullPointerException.class, () -> new Parser(TokenRuleContext.empty(), rulesWithNull));
	}
	
	@Test
	void constructDefensivelyCopiesRulesList() {
		ParserRule rule1 = new ParserRule("first", TokenRules.alwaysMatch(), TokenAction.identity());
		ParserRule rule2 = new ParserRule("second", TokenRules.alwaysMatch(), TokenAction.identity());
		List<ParserRule> input = new ArrayList<>(List.of(rule1));
		
		Parser parser = new Parser(TokenRuleContext.empty(), input);
		input.add(rule2);
		
		assertEquals(List.of(rule1), parser.getRules());
	}
	
	@Test
	void builderWithNullBuilderFunction() {
		assertThrows(NullPointerException.class, () -> Parser.builder(null));
	}
	
	@Test
	void parseWithNullTokens() {
		Parser parser = Parser.builder(b -> b.rule("NAME", TokenRules.alwaysMatch()));
		assertThrows(NullPointerException.class, () -> parser.parse(null));
	}
	
	@Test
	void parseWithEmptyTokens() {
		Parser parser = Parser.builder(b -> b.rule("NAME", TokenRules.alwaysMatch()));
		assertThrows(IllegalArgumentException.class, () -> parser.parse(List.of()));
	}
	
	@Test
	void parseWithNonEmptyTokensDoesNotThrow() {
		Parser parser = Parser.builder(b -> b.rule("NAME", TokenRules.alwaysMatch()));
		List<Token> tokens = List.of(token("a"));
		
		assertDoesNotThrow(() -> parser.parse(tokens));
	}
	
	@Test
	void parseReturnsSingleTokenGroupDirectlyWhenResultIsOneGroup() {
		Parser parser = Parser.builder(b -> b.rule("EXPR", TokenRules.alwaysMatch().atLeast(1)));
		List<Token> tokens = List.of(token("a"), token("b"));
		
		TokenGroup result = parser.parse(tokens);
		
		assertEquals("EXPR", result.label());
	}
	
	@Test
	void parseWrapsResultWhenSizeIsNotOne() {
		Parser parser = Parser.builder(b -> b.rule("NAME", TokenRules.neverMatch()));
		List<Token> tokens = List.of(token("a"), token("b"));
		
		TokenGroup result = parser.parse(tokens);
		
		assertEquals("root", result.label());
		assertEquals(tokens, result.tokens());
	}
	
	@Test
	void parseWrapsResultWhenSingleResultIsNotATokenGroup() {
		Parser parser = new Parser(TokenRuleContext.empty(), List.of());
		Token single = token("a");
		List<Token> tokens = List.of(single);
		
		TokenGroup result = parser.parse(tokens);
		
		assertEquals("root", result.label());
		assertEquals(List.of(single), result.tokens());
	}
	
	@Test
	void parseWithEmptyRuleListStillProcessesTokens() {
		Parser parser = new Parser(TokenRuleContext.empty(), List.of());
		List<Token> tokens = List.of(token("a"), token("b"));
		
		TokenGroup result = parser.parse(tokens);
		
		assertEquals("root", result.label());
		assertEquals(tokens, result.tokens());
	}
	
	@Test
	void parseThrowsWhenRuleActionRemovesAllTokens() {
		Parser parser = Parser.builder(b -> b.rule("DROP", TokenRules.alwaysMatch(), TokenActions.skip(_ -> true)));
		List<Token> tokens = List.of(token("a"));
		
		assertThrows(IllegalArgumentException.class, () -> parser.parse(tokens));
	}
	
	@Test
	void parseWithMultipleRulesAppliesEachInOrder() {
		Parser parser = Parser.builder(b -> {
			b.rule("A", TokenRules.value("a", false).atLeast(1));
			b.rule("B", TokenRules.value("b", false).atLeast(1));
		});
		List<Token> tokens = List.of(token("a"), token("a"), token("b"), token("b"));
		
		TokenGroup result = parser.parse(tokens);
		
		assertEquals("root", result.label());
		assertEquals(2, result.children().size());
		assertEquals("A", ((TokenGroup) result.children().get(0)).label());
		assertEquals("B", ((TokenGroup) result.children().get(1)).label());
	}
	
	@Test
	void getContextReturnsStoredContext() {
		TokenRuleContext context = TokenRuleContext.empty();
		Parser parser = new Parser(context, List.of());
		
		assertSame(context, parser.getContext());
	}
	
	@Test
	void getRulesReturnsStoredRules() {
		ParserRule rule1 = new ParserRule("first", TokenRules.alwaysMatch(), TokenAction.identity());
		ParserRule rule2 = new ParserRule("second", TokenRules.alwaysMatch(), TokenAction.identity());
		Parser parser = new Parser(TokenRuleContext.empty(), List.of(rule1, rule2));
		
		assertEquals(List.of(rule1, rule2), parser.getRules());
	}
	
	@Test
	void getRulesIsUnmodifiable() {
		ParserRule rule = new ParserRule("first", TokenRules.alwaysMatch(), TokenAction.identity());
		Parser parser = new Parser(TokenRuleContext.empty(), List.of(rule));
		
		assertThrows(UnsupportedOperationException.class, () -> parser.getRules().add(rule));
	}
	
	@Test
	void builderProducesConfiguredParser() {
		Parser parser = Parser.builder(b -> {
			b.defineRule("REF", TokenRules.alwaysMatch());
			b.rule("NAME", TokenRules.alwaysMatch());
		});
		
		assertEquals(1, parser.getRules().size());
		assertEquals("NAME", parser.getRules().getFirst().name());
		assertNotNull(parser.getContext().getRuleReference("REF"));
	}
	
	@Test
	void parseSingleTokenWithMatchingRuleProducesLabeledGroup() {
		Parser parser = Parser.builder(b -> b.rule("VALUE", TokenRules.alwaysMatch()));
		Token single = token("a");
		
		TokenGroup result = parser.parse(List.of(single));
		
		assertEquals("VALUE", result.label());
		assertEquals(List.of(single), result.tokens());
	}
	
	@Test
	void parseNestedRulesProduceNestedTokenGroups() {
		Parser parser = Parser.builder(b -> {
			b.rule("INNER", TokenRules.value("x", false));
			b.rule("OUTER", TokenRules.alwaysMatch().atLeast(1));
		});
		List<Token> tokens = List.of(token("x"), token("y"));
		
		TokenGroup outer = parser.parse(tokens);
		
		assertEquals("OUTER", outer.label());
		assertFalse(outer.isLeaf());
		assertEquals(2, outer.children().size());
		TokenGroup inner = (TokenGroup) outer.children().getFirst();
		assertEquals("INNER", inner.label());
		assertTrue(inner.isLeaf());
	}
	
	@Test
	void parseLargeTokenSequenceWithMultipleMatchingRules() {
		Parser parser = Parser.builder(b -> {
			b.rule("A", TokenRules.value("a", false).atLeast(1));
			b.rule("B", TokenRules.value("b", false).atLeast(1));
			b.rule("C", TokenRules.value("c", false).atLeast(1));
		});
		List<Token> tokens = List.of(token("a"), token("a"), token("b"), token("b"), token("c"), token("c"), token("c"));
		
		TokenGroup result = parser.parse(tokens);
		
		assertEquals("root", result.label());
		assertEquals(3, result.children().size());
		assertEquals(List.of("A", "B", "C"), result.children().stream().map(t -> ((TokenGroup) t).label()).toList());
		assertEquals("aabbccc", result.value());
	}
	
	@Test
	void parseSameParserInstanceMultipleTimesIsConsistentAndIndependent() {
		Parser parser = Parser.builder(b -> b.rule("EXPR", TokenRules.alwaysMatch().atLeast(1)));
		List<Token> tokensA = List.of(token("a"), token("b"));
		List<Token> tokensB = List.of(token("x"), token("y"), token("z"));
		
		TokenGroup resultA = parser.parse(tokensA);
		TokenGroup resultB = parser.parse(tokensB);
		
		assertEquals("EXPR", resultA.label());
		assertEquals(2, resultA.children().size());
		assertEquals("EXPR", resultB.label());
		assertEquals(3, resultB.children().size());
	}
}
