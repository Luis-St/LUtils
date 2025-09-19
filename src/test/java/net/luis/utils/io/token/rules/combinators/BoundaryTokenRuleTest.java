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
	void constructorTwoParametersWithValidRules() {
		TokenRule start = createRule("(");
		TokenRule end = createRule(")");
		
		BoundaryTokenRule rule = new BoundaryTokenRule(start, end);
		
		assertEquals(start, rule.startTokenRule());
		assertEquals(end, rule.endTokenRule());
		assertEquals(TokenRules.alwaysMatch().getClass(), rule.betweenTokenRule().getClass());
	}
	
	@Test
	void constructorTwoParametersWithNullStart() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(null, createRule(")")));
	}
	
	@Test
	void constructorTwoParametersWithNullEnd() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(createRule("("), null));
	}
	
	@Test
	void constructorThreeParametersWithValidRules() {
		TokenRule start = createRule("(");
		TokenRule between = createRule("content");
		TokenRule end = createRule(")");
		
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		assertEquals(start, rule.startTokenRule());
		assertEquals(between, rule.betweenTokenRule());
		assertEquals(end, rule.endTokenRule());
	}
	
	@Test
	void constructorThreeParametersWithNullStart() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(null, createRule("content"), createRule(")")));
	}
	
	@Test
	void constructorThreeParametersWithNullBetween() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(createRule("("), null, createRule(")")));
	}
	
	@Test
	void constructorThreeParametersWithNullEnd() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(createRule("("), createRule("content"), null));
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
		assertEquals("(", result.matchedTokens().get(0).value());
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
	void not() {
		TokenRule start = createRule("(");
		TokenRule between = createRule("content");
		TokenRule end = createRule(")");
		BoundaryTokenRule rule = new BoundaryTokenRule(start, between, end);
		
		TokenRule negated = rule.not();
		
		assertTrue(negated instanceof BoundaryTokenRule);
		BoundaryTokenRule negatedBoundary = (BoundaryTokenRule) negated;
		assertNotEquals(start, negatedBoundary.startTokenRule());
		assertNotEquals(between, negatedBoundary.betweenTokenRule());
		assertNotEquals(end, negatedBoundary.endTokenRule());
	}
	
	@Test
	void equalsAndHashCode() {
		TokenRule start1 = createRule("(");
		TokenRule between1 = createRule("content");
		TokenRule end1 = createRule(")");
		
		TokenRule start2 = createRule("(");
		TokenRule between2 = createRule("content");
		TokenRule end2 = createRule(")");
		
		TokenRule start3 = createRule("[");
		
		BoundaryTokenRule rule1 = new BoundaryTokenRule(start1, between1, end1);
		BoundaryTokenRule rule2 = new BoundaryTokenRule(start1, between1, end1);
		BoundaryTokenRule rule3 = new BoundaryTokenRule(start2, between2, end2);
		BoundaryTokenRule rule4 = new BoundaryTokenRule(start3, between1, end1);
		
		assertEquals(rule1, rule2);
		assertNotEquals(rule1, rule3); // Different rule instances
		assertNotEquals(rule1, rule4); // Different start rule
		assertNotEquals(rule1, null);
		assertNotEquals(rule1, "string");
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
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
