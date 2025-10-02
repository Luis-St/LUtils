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

package net.luis.utils.io.token.rules.combinators;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.rules.TokenRules;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BoundaryTokenRule}.<br>
 *
 * @author Luis-St
 */
class BoundaryTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
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
	void constructorWithNullStart() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(null, createRule(")")));
	}
	
	@Test
	void constructorWithNullEnd() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(createRule("("), null));
	}
	
	@Test
	void constructorWithValidRules() {
		TokenRule start = createRule("(");
		TokenRule end = createRule(")");
		
		BoundaryTokenRule rule = new BoundaryTokenRule(start, end);
		
		assertEquals(start, rule.startTokenRule());
		assertEquals(end, rule.endTokenRule());
		assertEquals(TokenRules.alwaysMatch().getClass(), rule.betweenTokenRule().getClass());
	}
	
	@Test
	void contentConstructorWithNullStart() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(null, createRule("content"), createRule(")")));
	}
	
	@Test
	void contentConstructorWithNullBetween() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(createRule("("), null, createRule(")")));
	}
	
	@Test
	void contentConstructorWithNullEnd() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(createRule("("), createRule("content"), null));
	}
	
	@Test
	void contentConstructorWithValidRules() {
		TokenRule start = createRule("(");
		TokenRule between = createRule("content");
		TokenRule end = createRule(")");
		
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		assertEquals(start, rule.startTokenRule());
		assertEquals(between, rule.betweenTokenRule());
		assertEquals(end, rule.endTokenRule());
	}
	
	@Test
	void matchWithNullStream() {
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("("), createRule(")"));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("("), createRule(")"));
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyStream() {
		TokenRule start = createRule("(");
		TokenRule end = createRule(")");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, end);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithSimpleBoundary() {
		TokenRule start = createRule("(");
		TokenRule end = createRule(")");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("("), createToken(")")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals("(", result.matchedTokens().get(0).value());
		assertEquals(")", result.matchedTokens().get(1).value());
		assertEquals(rule, result.matchingTokenRule());
	}
	
	@Test
	void matchWithEmptyContent() {
		TokenRule start = createRule("start");
		TokenRule between = createRule("specific");
		TokenRule end = createRule("end");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("start"), createToken("end")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals("start", result.matchedTokens().get(0).value());
		assertEquals("end", result.matchedTokens().get(1).value());
	}
	
	@Test
	void matchWithContentBetween() {
		TokenRule start = createRule("(");
		TokenRule between = createRule("content");
		TokenRule end = createRule(")");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("("), createToken("content"), createToken(")")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(3, result.endIndex());
		assertEquals(3, result.matchedTokens().size());
		assertEquals("(", result.matchedTokens().get(0).value());
		assertEquals("content", result.matchedTokens().get(1).value());
		assertEquals(")", result.matchedTokens().get(2).value());
	}
	
	@Test
	void matchWithMultipleContentTokens() {
		TokenRule start = createRule("{");
		TokenRule between = TokenRules.alwaysMatch();
		TokenRule end = createRule("}");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("{"), createToken("first"), createToken("second"), createToken("}")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(4, result.endIndex());
		assertEquals(4, result.matchedTokens().size());
		assertEquals("{", result.matchedTokens().get(0).value());
		assertEquals("first", result.matchedTokens().get(1).value());
		assertEquals("second", result.matchedTokens().get(2).value());
		assertEquals("}", result.matchedTokens().get(3).value());
	}
	
	@Test
	void matchAtDifferentStreamPosition() {
		TokenRule start = createRule("(");
		TokenRule end = createRule(")");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("prefix"), createToken("("), createToken("content"), createToken(")")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(4, result.endIndex());
		assertEquals(3, result.matchedTokens().size());
		assertEquals("(", result.matchedTokens().get(0).value());
		assertEquals("content", result.matchedTokens().get(1).value());
		assertEquals(")", result.matchedTokens().get(2).value());
		assertEquals(4, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithNestedBoundaries() {
		TokenRule start = createRule("(");
		TokenRule between = TokenRules.alwaysMatch();
		TokenRule end = createRule(")");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("("), createToken("("), createToken("nested"), createToken(")")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(4, result.endIndex());
		assertEquals(4, result.matchedTokens().size());
		assertEquals("(", result.matchedTokens().get(0).value());
		assertEquals("(", result.matchedTokens().get(1).value());
		assertEquals("nested", result.matchedTokens().get(2).value());
		assertEquals(")", result.matchedTokens().get(3).value());
	}
	
	@Test
	void matchWithComplexBetweenRule() {
		TokenRule start = createRule("begin");
		TokenRule between = TokenRules.sequence(createRule("word"), createRule("number"));
		TokenRule end = createRule("end");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("begin"), createToken("word"), createToken("number"), createToken("end")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(4, result.endIndex());
		assertEquals(4, result.matchedTokens().size());
		assertEquals("begin", result.matchedTokens().get(0).value());
		assertEquals("word", result.matchedTokens().get(1).value());
		assertEquals("number", result.matchedTokens().get(2).value());
		assertEquals("end", result.matchedTokens().get(3).value());
	}
	
	@Test
	void matchWithMultipleValidEndPositions() {
		TokenRule start = createRule("start");
		TokenRule between = createRule("item");
		TokenRule end = createRule("end");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("start"), createToken("item"), createToken("end"), createToken("item"), createToken("end")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(3, result.endIndex());
		assertEquals(3, result.matchedTokens().size());
		assertEquals("start", result.matchedTokens().get(0).value());
		assertEquals("item", result.matchedTokens().get(1).value());
		assertEquals("end", result.matchedTokens().get(2).value());
	}
	
	@Test
	void matchWithOptionalBetweenContent() {
		TokenRule start = createRule("start");
		TokenRule between = TokenRules.optional(createRule("optional"));
		TokenRule end = createRule("end");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("start"), createToken("end")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals("start", result.matchedTokens().get(0).value());
		assertEquals("end", result.matchedTokens().get(1).value());
	}
	
	@Test
	void matchWithSameStartAndEndPatterns() {
		BoundaryTokenRule rule = new BoundaryTokenRule(
			TokenRules.value("quote", false),
			TokenRules.value("quote", false)
		);
		List<Token> tokens = List.of(
			SimpleToken.createUnpositioned("quote"),
			SimpleToken.createUnpositioned("content"),
			SimpleToken.createUnpositioned("quote")
		);
		
		TokenRuleContext context = TokenRuleContext.empty();
		TokenStream stream = TokenStream.createMutable(tokens);
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(3, result.matchedTokens().size());
	}
	
	@Test
	void matchWithNoStartMatch() {
		TokenRule start = createRule("(");
		TokenRule end = createRule(")");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("["), createToken(")")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithNoEndMatch() {
		TokenRule start = createRule("(");
		TokenRule end = createRule(")");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("("), createToken("]")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithContentNotMatching() {
		TokenRule start = createRule("(");
		TokenRule between = createRule("specific");
		TokenRule end = createRule(")");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("("), createToken("wrong"), createToken(")")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithEndAtStreamEnd() {
		TokenRule start = createRule("(");
		TokenRule end = TokenRules.endDocument();
		BoundaryTokenRule rule = new BoundaryTokenRule(start, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("(")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals("(", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithNeverMatchBetween() {
		TokenRule start = createRule("(");
		TokenRule between = TokenRules.neverMatch();
		TokenRule end = createRule(")");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("("), createToken("content"), createToken(")")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithFailingBetweenRule() {
		BoundaryTokenRule rule = new BoundaryTokenRule(
			TokenRules.value("start", false),
			TokenRules.value("specific", false),
			TokenRules.value("end", false)
		);
		
		List<Token> tokens = List.of(
			SimpleToken.createUnpositioned("start"),
			SimpleToken.createUnpositioned("different"),
			SimpleToken.createUnpositioned("end")
		);
		TokenStream stream = TokenStream.createMutable(tokens);
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithGreedyBetweenMatching() {
		TokenRule start = createRule("start");
		TokenRule between = TokenRules.alwaysMatch();
		TokenRule end = createRule("end");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("start"), createToken("lots"), createToken("of"), createToken("content"), createToken("here"), createToken("end")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(6, result.endIndex());
		assertEquals(6, result.matchedTokens().size());
		assertEquals("start", result.matchedTokens().get(0).value());
		assertEquals("lots", result.matchedTokens().get(1).value());
		assertEquals("of", result.matchedTokens().get(2).value());
		assertEquals("content", result.matchedTokens().get(3).value());
		assertEquals("here", result.matchedTokens().get(4).value());
		assertEquals("end", result.matchedTokens().get(5).value());
	}
	
	@Test
	void not() {
		TokenRule start = TokenRules.value("(", false);
		TokenRule between = TokenRules.pattern("content");
		TokenRule end = TokenRules.value(")", false);
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		TokenRule negated = rule.not();
		
		BoundaryTokenRule negatedBoundary = assertInstanceOf(BoundaryTokenRule.class, negated);
		assertNotEquals(start, negatedBoundary.startTokenRule());
		assertNotEquals(between, negatedBoundary.betweenTokenRule());
		assertNotEquals(end, negatedBoundary.endTokenRule());
	}
	
	@Test
	void toStringTest() {
		TokenRule start = createRule("(");
		TokenRule between = createRule("content");
		TokenRule end = createRule(")");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		String result = rule.toString();
		
		assertTrue(result.contains("BoundaryTokenRule"));
		assertTrue(result.contains("startTokenRule"));
		assertTrue(result.contains("betweenTokenRule"));
		assertTrue(result.contains("endTokenRule"));
	}
}
