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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SequenceTokenRule}.<br>
 *
 * @author Luis-St
 */
class SequenceTokenRuleTest {
	
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
		assertThrows(NullPointerException.class, () -> new SequenceTokenRule(null));
	}
	
	@Test
	void constructorWithEmptyRules() {
		assertThrows(IllegalArgumentException.class, () -> new SequenceTokenRule(List.of()));
	}
	
	@Test
	void constructorWithNullRuleInList() {
		List<TokenRule> rules = new ArrayList<>();
		rules.add(createRule("first"));
		rules.add(null);
		
		assertThrows(NullPointerException.class, () -> new SequenceTokenRule(rules));
	}
	
	@Test
	void constructorWithValidRules() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		List<TokenRule> rules = List.of(rule1, rule2);
		
		SequenceTokenRule sequenceRule = new SequenceTokenRule(rules);
		
		assertEquals(rules, sequenceRule.tokenRules());
	}
	
	@Test
	void matchWithNullStream() {
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(createRule("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> sequenceRule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(createRule("test")));
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> sequenceRule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyStream() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithSingleRule() {
		TokenRule rule = createRule("test");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.matchedTokens().size());
		assertEquals("test", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithMultipleRules() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		TokenRule rule3 = createRule("third");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2, rule3));
		
		TokenStream stream = TokenStream.createMutable(List.of(
			createToken("first"), createToken("second"), createToken("third")
		));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(3, result.matchedTokens().size());
		assertEquals("first", result.matchedTokens().get(0).value());
		assertEquals("second", result.matchedTokens().get(1).value());
		assertEquals("third", result.matchedTokens().get(2).value());
	}
	
	@Test
	void matchWithAllRulesMatching() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(2, result.matchedTokens().size());
		assertEquals("first", result.matchedTokens().get(0).value());
		assertEquals("second", result.matchedTokens().get(1).value());
		assertEquals(sequenceRule, result.matchingTokenRule());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithFirstRuleNotMatching() {
		TokenRule rule1 = createRule("expected");
		TokenRule rule2 = createRule("second");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithSecondRuleNotMatching() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("expected");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithInsufficientTokens() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithAlwaysMatchRules() {
		TokenRule rule1 = TokenRules.alwaysMatch();
		TokenRule rule2 = TokenRules.alwaysMatch();
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any1"), createToken("any2")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(2, result.matchedTokens().size());
		assertEquals("any1", result.matchedTokens().get(0).value());
		assertEquals("any2", result.matchedTokens().get(1).value());
	}
	
	@Test
	void matchWithNeverMatchRule() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = TokenRules.neverMatch();
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = sequenceRule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void not() {
		TokenRule rule1 = TokenRules.pattern("first");
		TokenRule rule2 = TokenRules.pattern("second");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		TokenRule negated = sequenceRule.not();
		
		AnyOfTokenRule anyRule = assertInstanceOf(AnyOfTokenRule.class, negated);
		assertEquals(2, anyRule.tokenRules().size());
	}
	
	@Test
	void notWithSingleRule() {
		TokenRule rule = TokenRules.pattern("test");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule));
		
		TokenRule negated = sequenceRule.not();
		
		AnyOfTokenRule anyRule = assertInstanceOf(AnyOfTokenRule.class, negated);
		assertEquals(1, anyRule.tokenRules().size());
	}
	
	@Test
	void toStringTest() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		SequenceTokenRule sequenceRule = new SequenceTokenRule(List.of(rule1, rule2));
		
		String result = sequenceRule.toString();
		
		assertTrue(result.contains("SequenceTokenRule"));
		assertTrue(result.contains("tokenRules"));
	}
}
