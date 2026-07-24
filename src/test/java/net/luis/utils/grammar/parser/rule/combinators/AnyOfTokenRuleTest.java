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
import net.luis.utils.grammar.parser.rule.matchers.ValueTokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AnyOfTokenRule}.<br>
 *
 * @author Luis-St
 */
class AnyOfTokenRuleTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructWithValidRules() {
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE));
		
		assertEquals(2, rule.tokenRules().size());
		assertEquals(List.of(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE), rule.tokenRules());
	}
	
	@Test
	void constructWithMoreThanTwoRules() {
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		
		assertEquals(3, rule.tokenRules().size());
	}
	
	@Test
	void constructWithNullList() {
		assertThrows(NullPointerException.class, () -> new AnyOfTokenRule(null));
	}
	
	@Test
	void constructWithNullElementInList() {
		assertThrows(NullPointerException.class, () -> new AnyOfTokenRule(Arrays.asList(AlwaysMatchTokenRule.INSTANCE, null)));
	}
	
	@Test
	void constructListIsDefensivelyCopied() {
		List<TokenRule> source = new ArrayList<>(List.of(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE));
		AnyOfTokenRule rule = new AnyOfTokenRule(source);
		
		source.clear();
		
		assertEquals(2, rule.tokenRules().size());
	}
	
	@Test
	void tokenRulesListIsImmutable() {
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE));
		
		assertThrows(UnsupportedOperationException.class, () -> rule.tokenRules().add(AlwaysMatchTokenRule.INSTANCE));
	}
	
	@Test
	void constructWithEmptyList() {
		assertThrows(IllegalArgumentException.class, () -> new AnyOfTokenRule(List.of()));
	}
	
	@Test
	void constructWithSingleRule() {
		assertThrows(IllegalArgumentException.class, () -> new AnyOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE)));
	}
	
	@Test
	void matchWithNullStream() {
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE));
		
		assertThrows(NullPointerException.class, () -> rule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNullContext() {
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchFirstRuleSucceeds() {
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a")), 0);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchLaterRuleSucceeds() {
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(NeverMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a")), 0);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchNoRuleSucceeds() {
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(NeverMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a")), 0);
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchUsesFirstMatchingRuleOnly() {
		ValueTokenRule first = new ValueTokenRule("a", false);
		ValueTokenRule second = new ValueTokenRule("a", true);
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(first, second));
		TokenStream stream = TokenStream.createMutable(List.of(token("a")), 0);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertSame(first, match.matchingTokenRule());
	}
	
	@Test
	void matchAtNonZeroStreamOffset() {
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")), 1);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
	}
	
	@Test
	void matchIsolatesWorkingStreamPerRuleAttempt() {
		SequenceTokenRule partiallyConsuming = new SequenceTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE));
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(partiallyConsuming, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a")), 0);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void notReturnsNegatedSequenceViaDeMorgan() {
		AnyOfTokenRule rule = new AnyOfTokenRule(List.of(new ValueTokenRule("a", false), new ValueTokenRule("b", false)));
		
		TokenRule notRule = rule.not();
		
		assertInstanceOf(SequenceTokenRule.class, notRule);
		SequenceTokenRule casted = (SequenceTokenRule) notRule;
		assertEquals(2, casted.tokenRules().size());
		
		TokenStream neitherMatches = TokenStream.createMutable(List.of(token("c"), token("d")), 0);
		assertNotNull(notRule.match(neitherMatches, TokenRuleContext.empty()));
		
		TokenStream firstMatchesOriginal = TokenStream.createMutable(List.of(token("a"), token("d")), 0);
		assertNull(notRule.match(firstMatchesOriginal, TokenRuleContext.empty()));
	}
	
	@Test
	void matchNestedAnyOfTokenRule() {
		AnyOfTokenRule inner = new AnyOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE));
		AnyOfTokenRule outer = new AnyOfTokenRule(List.of(inner, NeverMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a")), 0);
		
		TokenRuleMatch match = outer.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
}
