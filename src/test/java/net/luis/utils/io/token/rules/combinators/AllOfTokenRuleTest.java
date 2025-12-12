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

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.rules.TokenRules;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.type.StandardTokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AllOfTokenRule}.<br>
 *
 * @author Luis-St
 */
class AllOfTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static @NotNull Token createToken(@NotNull String value, StandardTokenType... types) {
		return new SimpleToken(value, TokenPosition.UNPOSITIONED, Set.of(types));
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
		assertThrows(NullPointerException.class, () -> new AllOfTokenRule(null));
	}
	
	@Test
	void constructorWithEmptyRules() {
		assertThrows(IllegalArgumentException.class, () -> new AllOfTokenRule(List.of()));
	}
	
	@Test
	void constructorWithSingleRule() {
		assertThrows(IllegalArgumentException.class, () -> new AllOfTokenRule(List.of(createRule("test"))));
	}
	
	@Test
	void constructorWithNullRuleInList() {
		List<TokenRule> rules = new ArrayList<>();
		rules.add(createRule("first"));
		rules.add(null);
		
		assertThrows(NullPointerException.class, () -> new AllOfTokenRule(rules));
	}
	
	@Test
	void constructorWithValidRules() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("first");
		List<TokenRule> rules = List.of(rule1, rule2);
		
		AllOfTokenRule allRule = new AllOfTokenRule(rules);
		
		assertEquals(rules, allRule.tokenRules());
	}
	
	@Test
	void matchWithNullStream() {
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(createRule("test"), createRule("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> allRule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(createRule("test"), createRule("test")));
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> allRule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyStream() {
		TokenRule rule1 = createRule("test");
		TokenRule rule2 = createRule("test");
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithBothRulesMatching() {
		TokenRule rule1 = createRule("match");
		TokenRule rule2 = createRule("match");
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("match")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals("match", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithFirstRuleNotMatching() {
		TokenRule rule1 = createRule("nomatch");
		TokenRule rule2 = createRule("match");
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("match")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSecondRuleNotMatching() {
		TokenRule rule1 = createRule("match");
		TokenRule rule2 = createRule("nomatch");
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("match")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithThreeRulesAllMatching() {
		TokenRule rule1 = createRule("match");
		TokenRule rule2 = createRule("match");
		TokenRule rule3 = createRule("match");
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2, rule3));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("match")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
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
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchAtMiddleOfStream() {
		TokenRule rule1 = createRule("target");
		TokenRule rule2 = createRule("target");
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("target"), createToken("third")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals("target", result.matchedTokens().getFirst().value());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithPatternAndValueRules() {
		TokenRule rule1 = TokenRules.pattern("\\w+");
		TokenRule rule2 = TokenRules.value("test", false);
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("test", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithPatternAndValueRulesNotMatching() {
		TokenRule rule1 = TokenRules.pattern("\\w+");
		TokenRule rule2 = TokenRules.value("test", false);
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithTypeAndValueRules() {
		TokenRule rule1 = TokenRules.type(StandardTokenType.KEYWORD);
		TokenRule rule2 = TokenRules.value("if", false);
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("if", StandardTokenType.KEYWORD)));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("if", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithTypeAndValueRulesWrongType() {
		TokenRule rule1 = TokenRules.type(StandardTokenType.KEYWORD);
		TokenRule rule2 = TokenRules.value("test", false);
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test", StandardTokenType.IDENTIFIER)));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithLengthAndPatternRules() {
		TokenRule rule1 = TokenRules.exactLength(4);
		TokenRule rule2 = TokenRules.pattern("\\d+");
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("1234")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("1234", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithLengthAndPatternRulesWrongLength() {
		TokenRule rule1 = TokenRules.exactLength(3);
		TokenRule rule2 = TokenRules.pattern("\\d+");
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("1234")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithAlwaysMatchAndSpecificRule() {
		TokenRule alwaysMatch = TokenRules.alwaysMatch();
		TokenRule specificRule = TokenRules.value("test", false);
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(alwaysMatch, specificRule));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("test", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithNeverMatchAndSpecificRule() {
		TokenRule neverMatch = TokenRules.neverMatch();
		TokenRule specificRule = TokenRules.value("test", false);
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(neverMatch, specificRule));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = allRule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithMultiTokenRuleThrowsException() {
		TokenRule singleTokenRule = createRule("first");
		TokenRule multiTokenRule = TokenRules.sequence(createRule("first"), createRule("second"));
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(singleTokenRule, multiTokenRule));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(IllegalStateException.class, () -> allRule.match(stream, context));
	}
	
	@Test
	void matchWithNestedAllOfRules() {
		TokenRule inner1 = new AllOfTokenRule(List.of(createRule("a"), TokenRules.pattern("\\w")));
		TokenRule inner2 = TokenRules.exactLength(1);
		AllOfTokenRule outer = new AllOfTokenRule(List.of(inner1, inner2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("a")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = outer.match(stream, context);
		
		assertNotNull(result);
		assertEquals("a", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithManyConjunctiveRules() {
		List<TokenRule> rules = new ArrayList<>();
		rules.add(TokenRules.pattern("\\w+"));
		rules.add(TokenRules.minLength(4));
		rules.add(TokenRules.maxLength(10));
		rules.add(TokenRules.value("test", false));
		AllOfTokenRule rule = new AllOfTokenRule(rules);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("test", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithManyConjunctiveRulesOneFails() {
		List<TokenRule> rules = new ArrayList<>();
		rules.add(TokenRules.pattern("\\w+"));
		rules.add(TokenRules.minLength(4));
		rules.add(TokenRules.maxLength(10));
		rules.add(TokenRules.value("test", false));
		AllOfTokenRule rule = new AllOfTokenRule(rules);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void not() {
		TokenRule rule1 = TokenRules.pattern("first");
		TokenRule rule2 = TokenRules.pattern("second");
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenRule negated = allRule.not();
		
		AllOfTokenRule negatedAllRule = assertInstanceOf(AllOfTokenRule.class, negated);
		assertEquals(2, negatedAllRule.tokenRules().size());
	}
	
	@Test
	void notWithTwoRules() {
		TokenRule rule1 = TokenRules.pattern("test");
		TokenRule rule2 = TokenRules.exactLength(5);
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		TokenRule negated = allRule.not();
		
		AllOfTokenRule negatedAllRule = assertInstanceOf(AllOfTokenRule.class, negated);
		assertEquals(2, negatedAllRule.tokenRules().size());
	}
	
	@Test
	void toStringTest() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		AllOfTokenRule allRule = new AllOfTokenRule(List.of(rule1, rule2));
		
		String result = allRule.toString();
		
		assertTrue(result.contains("AllOfTokenRule"));
		assertTrue(result.contains("tokenRules"));
	}
}
