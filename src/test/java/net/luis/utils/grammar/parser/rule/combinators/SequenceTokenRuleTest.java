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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SequenceTokenRule}.<br>
 *
 * @author Luis-St
 */
class SequenceTokenRuleTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static TokenStream streamOf(String... values) {
		return TokenStream.createMutable(List.of(Arrays.stream(values).map(SequenceTokenRuleTest::token).toArray(Token[]::new)));
	}
	
	@Test
	void constructWithValidRules() {
		List<TokenRule> rules = List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE);
		SequenceTokenRule rule = new SequenceTokenRule(rules);
		
		assertEquals(2, rule.tokenRules().size());
		assertEquals(rules, rule.tokenRules());
	}
	
	@Test
	void constructWithMoreThanTwoRules() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		
		assertEquals(3, rule.tokenRules().size());
	}
	
	@Test
	void constructWithNullList() {
		assertThrows(NullPointerException.class, () -> new SequenceTokenRule(null));
	}
	
	@Test
	void constructWithNullElementInList() {
		assertThrows(NullPointerException.class, () -> new SequenceTokenRule(Arrays.asList(AlwaysMatchTokenRule.INSTANCE, null)));
	}
	
	@Test
	void constructListIsDefensivelyCopied() {
		List<TokenRule> mutable = new ArrayList<>(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		SequenceTokenRule rule = new SequenceTokenRule(mutable);
		mutable.add(NeverMatchTokenRule.INSTANCE);
		
		assertEquals(2, rule.tokenRules().size());
	}
	
	@Test
	void constructWithEmptyList() {
		assertThrows(IllegalArgumentException.class, () -> new SequenceTokenRule(List.of()));
	}
	
	@Test
	void constructWithSingleRule() {
		assertThrows(IllegalArgumentException.class, () -> new SequenceTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE)));
	}
	
	@Test
	void matchWithNullStream() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		assertThrows(NullPointerException.class, () -> rule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNullContext() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		assertThrows(NullPointerException.class, () -> rule.match(streamOf("a"), null));
	}
	
	@Test
	void matchAllRulesSucceed() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = streamOf("a", "b");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(2, match.matchedTokens().size());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchFirstRuleFails() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(NeverMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = streamOf("a");
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchLaterRuleFails() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = streamOf("a", "b", "c");
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchSequenceAtNonZeroOffset() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b"), token("c")), 1);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(3, match.endIndex());
	}
	
	@Test
	void matchSequenceOfThreeRules() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = streamOf("a", "b", "c");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
		assertEquals(List.of(token("a"), token("b"), token("c")), match.matchedTokens());
	}
	
	@Test
	void notReturnsNegatedAnyOfViaDeMorgan() {
		NegatableTokenRule first = t -> "a".equals(t.value());
		NegatableTokenRule second = t -> "b".equals(t.value());
		SequenceTokenRule rule = new SequenceTokenRule(List.of(first, second));
		
		TokenRule negated = rule.not();
		
		assertInstanceOf(AnyOfTokenRule.class, negated);
		TokenStream stream = streamOf("x");
		assertNotNull(negated.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchNestedSequenceTokenRule() {
		SequenceTokenRule inner = new SequenceTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		SequenceTokenRule outer = new SequenceTokenRule(List.of(inner, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = streamOf("a", "b", "c");
		
		TokenRuleMatch match = outer.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
		assertEquals(3, stream.getCurrentIndex());
	}
}
