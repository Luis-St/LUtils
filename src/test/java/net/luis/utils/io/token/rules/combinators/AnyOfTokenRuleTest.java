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
import net.luis.utils.io.token.rules.matchers.PatternTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AnyOfTokenRule}.<br>
 *
 * @author Luis-St
 */
class AnyOfTokenRuleTest {
	
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
	void constructorWithNullRules() {
		assertThrows(NullPointerException.class, () -> new AnyOfTokenRule(null));
	}
	
	@Test
	void constructorWithEmptyRules() {
		assertThrows(IllegalArgumentException.class, () -> new AnyOfTokenRule(List.of()));
	}
	
	@Test
	void constructorWithSingleRule() {
		assertThrows(IllegalArgumentException.class, () -> new AnyOfTokenRule(List.of(createRule("test"))));
	}
	
	@Test
	void constructorWithNullRuleInList() {
		List<TokenRule> rules = new ArrayList<>();
		rules.add(createRule("first"));
		rules.add(null);
		
		assertThrows(NullPointerException.class, () -> new AnyOfTokenRule(rules));
	}
	
	@Test
	void constructorWithValidRules() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		List<TokenRule> rules = List.of(rule1, rule2);
		
		AnyOfTokenRule anyRule = new AnyOfTokenRule(rules);
		
		assertEquals(rules, anyRule.tokenRules());
	}
	
	@Test
	void matchWithNullStream() {
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(createRule("first"), createRule("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> anyRule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(createRule("first"), createRule("second")));
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first")));
		
		assertThrows(NullPointerException.class, () -> anyRule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyStream() {
		TokenRule rule1 = createRule("test");
		TokenRule rule2 = createRule("other");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = anyRule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithFirstRuleMatching() {
		TokenRule rule1 = createRule("match");
		TokenRule rule2 = createRule("nomatch");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("match")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = anyRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals("match", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSecondRuleMatching() {
		TokenRule rule1 = createRule("nomatch");
		TokenRule rule2 = createRule("match");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("match")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = anyRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals("match", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithThirdRuleMatching() {
		TokenRule rule1 = createRule("nomatch1");
		TokenRule rule2 = createRule("nomatch2");
		TokenRule rule3 = createRule("match");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(rule1, rule2, rule3));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("match")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = anyRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals("match", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithNoRulesMatching() {
		TokenRule rule1 = createRule("nomatch1");
		TokenRule rule2 = createRule("nomatch2");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = anyRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithStreamPositionPreservation() {
		TokenRule rule1 = createRule("nomatch1");
		TokenRule rule2 = createRule("nomatch2");
		TokenRule rule3 = createRule("nomatch3");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(rule1, rule2, rule3));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("different"), createToken("tokens")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = anyRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchAtMiddleOfStream() {
		TokenRule rule1 = createRule("target");
		TokenRule rule2 = createRule("alternative");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("target"), createToken("third")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = anyRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals("target", result.matchedTokens().getFirst().value());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithComplexSequenceRules() {
		TokenRule rule1 = TokenRules.sequence(createRule("if"), createRule("("));
		TokenRule rule2 = TokenRules.sequence(createRule("while"), createRule("("));
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("while"), createToken("("), createToken("condition")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = anyRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals("while", result.matchedTokens().get(0).value());
		assertEquals("(", result.matchedTokens().get(1).value());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithDifferentTokenConsumption() {
		TokenRule rule1 = createRule("single");
		TokenRule rule2 = TokenRules.sequence(createRule("multi"), createRule("token"));
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("multi"), createToken("token"), createToken("rest")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = anyRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals("multi", result.matchedTokens().get(0).value());
		assertEquals("token", result.matchedTokens().get(1).value());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithMixedRuleTypes() {
		TokenRule rule1 = TokenRules.pattern("\\d+");
		TokenRule rule2 = TokenRules.value("literal", false);
		TokenRule rule3 = TokenRules.endDocument();
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(rule1, rule2, rule3));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("123")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = anyRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("123", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithAlwaysMatchRule() {
		TokenRule alwaysMatch = TokenRules.alwaysMatch();
		TokenRule neverMatch = TokenRules.neverMatch();
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(alwaysMatch, neverMatch));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = anyRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("any", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithNeverMatchRules() {
		TokenRule neverMatch1 = TokenRules.neverMatch();
		TokenRule neverMatch2 = TokenRules.neverMatch();
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(neverMatch1, neverMatch2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = anyRule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithPartiallyMatchingSequences() {
		TokenRule rule1 = TokenRules.sequence(createRule("start"), createRule("missing"));
		TokenRule rule2 = TokenRules.sequence(createRule("start"), createRule("correct"));
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("start"), createToken("correct")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = anyRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithPriorityOrder() {
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(
			TokenRules.pattern("\\w+"),
			TokenRules.value("test", false)
		));
		Token token = SimpleToken.createUnpositioned("test");
		
		TokenStream stream = TokenStream.createMutable(List.of(token));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertInstanceOf(PatternTokenRule.class, result.matchingTokenRule());
	}
	
	@Test
	void matchWithManyAlternatives() {
		List<TokenRule> rules = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			rules.add(TokenRules.value("option" + i, false));
		}
		AnyOfTokenRule rule = new AnyOfTokenRule(rules);
		Token token = SimpleToken.createUnpositioned("option999");
		
		TokenStream stream = TokenStream.createMutable(List.of(token));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("option999", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithNestedAnyOfRules() {
		TokenRule inner1 = new AnyOfTokenRule(List.of(createRule("a"), createRule("b")));
		TokenRule inner2 = new AnyOfTokenRule(List.of(createRule("c"), createRule("d")));
		AnyOfTokenRule outer = new AnyOfTokenRule(List.of(inner1, inner2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("c")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = outer.match(stream, context);
		
		assertNotNull(result);
		assertEquals("c", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void not() {
		TokenRule rule1 = TokenRules.pattern("first");
		TokenRule rule2 = TokenRules.pattern("second");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(rule1, rule2));
		
		TokenRule negated = anyRule.not();
		
		SequenceTokenRule sequenceRule = assertInstanceOf(SequenceTokenRule.class, negated);
		assertEquals(2, sequenceRule.tokenRules().size());
	}

	@Test
	void toStringTest() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(List.of(rule1, rule2));
		
		String result = anyRule.toString();
		
		assertTrue(result.contains("AnyOfTokenRule"));
		assertTrue(result.contains("tokenRules"));
	}
}
