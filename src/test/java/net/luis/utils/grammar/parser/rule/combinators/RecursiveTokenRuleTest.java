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

package net.luis.utils.grammar.parser.rule.combinators;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.rule.*;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link RecursiveTokenRule}.<br>
 *
 * @author Luis-St
 */
class RecursiveTokenRuleTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static TokenStream streamOf(String... values) {
		return TokenStream.createMutable(List.of(Arrays.stream(values).map(RecursiveTokenRuleTest::token).toArray(Token[]::new)));
	}
	
	@Test
	void constructWithRuleFactory() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> AlwaysMatchTokenRule.INSTANCE);
		
		assertEquals(AlwaysMatchTokenRule.INSTANCE, rule.getTokenRule());
	}
	
	@Test
	void constructWithNullRuleFactory() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(null));
	}
	
	@Test
	void constructWithFactoryReturningNull() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(self -> null));
	}
	
	@Test
	void constructWithOpeningContentClosingRules() {
		RecursiveTokenRule rule = new RecursiveTokenRule(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE);
		TokenStream stream = streamOf("a", "b", "c");
		
		TokenRuleMatch match = rule.getTokenRule().match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void constructWithNullOpeningRuleThreeArg() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(null, AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
	}
	
	@Test
	void constructWithNullContentRuleThreeArg() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(AlwaysMatchTokenRule.INSTANCE, null, AlwaysMatchTokenRule.INSTANCE));
	}
	
	@Test
	void constructWithNullClosingRuleThreeArg() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE, (TokenRule) null));
	}
	
	@Test
	void constructWithOpeningClosingAndContentFactory() {
		RecursiveTokenRule rule = new RecursiveTokenRule(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE, self -> AlwaysMatchTokenRule.INSTANCE);
		TokenStream stream = streamOf("a", "b", "c");
		
		TokenRuleMatch match = rule.getTokenRule().match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void constructWithNullOpeningRuleContentFactory() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(null, AlwaysMatchTokenRule.INSTANCE, self -> AlwaysMatchTokenRule.INSTANCE));
	}
	
	@Test
	void constructWithNullClosingRuleContentFactory() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(AlwaysMatchTokenRule.INSTANCE, null, self -> AlwaysMatchTokenRule.INSTANCE));
	}
	
	@Test
	void constructWithNullContentRuleFactory() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE, (Function<TokenRule, TokenRule>) null));
	}
	
	@Test
	void matchWithNullStream() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> AlwaysMatchTokenRule.INSTANCE);
		assertThrows(NullPointerException.class, () -> rule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNullContext() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> AlwaysMatchTokenRule.INSTANCE);
		assertThrows(NullPointerException.class, () -> rule.match(streamOf("a"), null));
	}
	
	@Test
	void matchInnerRuleFails() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> NeverMatchTokenRule.INSTANCE);
		TokenStream stream = streamOf("a");
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchInnerRuleSucceeds() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> AlwaysMatchTokenRule.INSTANCE);
		TokenStream stream = streamOf("a");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void equalsWithNonRecursiveTokenRuleInstance() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> AlwaysMatchTokenRule.INSTANCE);
		
		assertNotEquals("not a rule", rule);
	}
	
	@Test
	void equalsWithDifferentUnderlyingRule() {
		RecursiveTokenRule first = new RecursiveTokenRule(self -> AlwaysMatchTokenRule.INSTANCE);
		RecursiveTokenRule second = new RecursiveTokenRule(self -> NeverMatchTokenRule.INSTANCE);
		
		assertNotEquals(first, second);
	}
	
	@Test
	void equalsWithSameUnderlyingRule() {
		RecursiveTokenRule first = new RecursiveTokenRule(self -> AlwaysMatchTokenRule.INSTANCE);
		RecursiveTokenRule second = new RecursiveTokenRule(self -> AlwaysMatchTokenRule.INSTANCE);
		
		assertEquals(first, second);
	}
	
	@Test
	void hashCodeConsistentWithEquals() {
		RecursiveTokenRule first = new RecursiveTokenRule(self -> AlwaysMatchTokenRule.INSTANCE);
		RecursiveTokenRule second = new RecursiveTokenRule(self -> AlwaysMatchTokenRule.INSTANCE);
		
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toStringContainsTokenRule() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> AlwaysMatchTokenRule.INSTANCE);
		
		assertTrue(rule.toString().contains("RecursiveTokenRule["));
		assertTrue(rule.toString().contains(AlwaysMatchTokenRule.INSTANCE.toString()));
	}
	
	@Test
	void getTokenRuleReturnsFactoryResult() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> NeverMatchTokenRule.INSTANCE);
		
		assertSame(NeverMatchTokenRule.INSTANCE, rule.getTokenRule());
	}
	
	@Test
	void notPreventsDoubleNegationNesting() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> AlwaysMatchTokenRule.INSTANCE);
		
		TokenRule negatedOnce = rule.not();
		TokenRule negatedTwice = negatedOnce.not();
		
		assertInstanceOf(RecursiveTokenRule.class, negatedOnce);
		assertSame(rule, ((RecursiveTokenRule) negatedOnce).getTokenRule());
		assertInstanceOf(RecursiveTokenRule.class, negatedTwice);
		assertSame(negatedOnce, ((RecursiveTokenRule) negatedTwice).getTokenRule());
		
		TokenRuleMatch originalMatch = rule.match(streamOf("a"), TokenRuleContext.empty());
		TokenRuleMatch twiceNegatedMatch = negatedTwice.match(streamOf("a"), TokenRuleContext.empty());
		assertNotNull(originalMatch);
		assertNotNull(twiceNegatedMatch);
	}
	
	@Test
	void matchTrueRecursiveGrammar() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> TokenRules.any(
			TokenRules.value("x", false),
			TokenRules.sequence(TokenRules.value("(", false), self, TokenRules.value(")", false))
		));
		TokenStream stream = streamOf("(", "(", "x", ")", ")");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(5, match.endIndex());
		assertEquals(5, stream.getCurrentIndex());
	}
	
	@Test
	void matchRecursiveGrammarRejectsUnbalancedInput() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> TokenRules.any(
			TokenRules.value("x", false),
			TokenRules.sequence(TokenRules.value("(", false), self, TokenRules.value(")", false))
		));
		TokenStream stream = streamOf("(", "(", "x", ")");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNull(match);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void notInvertsMatchResultOfSingleNegation() {
		RecursiveTokenRule alwaysRule = new RecursiveTokenRule(self -> AlwaysMatchTokenRule.INSTANCE);
		RecursiveTokenRule neverRule = new RecursiveTokenRule(self -> NeverMatchTokenRule.INSTANCE);
		
		TokenRule negatedAlways = alwaysRule.not();
		TokenRule negatedNever = neverRule.not();
		
		assertNotNull(alwaysRule.match(streamOf("a"), TokenRuleContext.empty()));
		assertNull(negatedAlways.match(streamOf("a"), TokenRuleContext.empty()));
		
		assertNull(neverRule.match(streamOf("a"), TokenRuleContext.empty()));
		assertNotNull(negatedNever.match(streamOf("a"), TokenRuleContext.empty()));
	}
}
