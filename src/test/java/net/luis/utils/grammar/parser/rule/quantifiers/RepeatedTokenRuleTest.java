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

package net.luis.utils.grammar.parser.rule.quantifiers;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.rule.TokenRule;
import net.luis.utils.grammar.parser.rule.TokenRules;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link RepeatedTokenRule}.<br>
 *
 * @author Luis-St
 */
class RepeatedTokenRuleTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static TokenStream streamOf(String... values) {
		Token[] tokens = new Token[values.length];
		for (int i = 0; i < values.length; i++) {
			tokens[i] = token(values[i]);
		}
		return TokenStream.createMutable(List.of(tokens));
	}
	
	@Test
	void constructWithExactOccurrences() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 3);
		
		assertEquals(3, rule.minOccurrences());
		assertEquals(3, rule.maxOccurrences());
	}
	
	@Test
	void constructWithNullTokenRuleExactOccurrences() {
		assertThrows(NullPointerException.class, () -> new RepeatedTokenRule(null, 2));
	}
	
	@Test
	void constructWithMinAndMax() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 1, 3);
		
		assertEquals(1, rule.minOccurrences());
		assertEquals(3, rule.maxOccurrences());
	}
	
	@Test
	void constructWithNullTokenRuleMinMax() {
		assertThrows(NullPointerException.class, () -> new RepeatedTokenRule(null, 1, 3));
	}
	
	@Test
	void constructWithNegativeExactOccurrences() {
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(TokenRules.value("a", false), -1));
	}
	
	@Test
	void constructWithNegativeMinOccurrences() {
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(TokenRules.value("a", false), -1, 3));
	}
	
	@Test
	void constructWithMaxLessThanMin() {
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(TokenRules.value("a", false), 5, 2));
	}
	
	@Test
	void constructWithZeroMinAndMaxOccurrences() {
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(TokenRules.value("a", false), 0, 0));
	}
	
	@Test
	void matchWithNullStream() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 1, 2);
		
		assertThrows(NullPointerException.class, () -> rule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNullContext() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 1, 2);
		
		assertThrows(NullPointerException.class, () -> rule.match(TokenStream.createMutable(List.of()), null));
	}
	
	@Test
	void notMatchWithNullStream() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 1, 2);
		
		assertThrows(NullPointerException.class, () -> rule.not().match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void notMatchWithNullContext() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 1, 2);
		
		assertThrows(NullPointerException.class, () -> rule.not().match(TokenStream.createMutable(List.of()), null));
	}
	
	@Test
	void matchWithEmptyStreamReturnsNull() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 0, 3);
		
		assertNull(rule.match(TokenStream.createMutable(List.of()), TokenRuleContext.empty()));
	}
	
	@Test
	void matchInnerRuleNeverMatchesWithMinZeroReturnsEmptyMatch() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("z", false), 0, 3);
		TokenStream stream = streamOf("a");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchInnerRuleNeverMatchesWithMinPositiveReturnsNull() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("z", false), 1, 3);
		TokenStream stream = streamOf("a");
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchInnerRuleMatchesExactlyMinAndMaxOccurrences() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 2, 2);
		TokenStream stream = streamOf("a", "a");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(2, match.matchedTokens().size());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchInnerRuleExceedsMaxOccurrencesStopsAtMax() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 1, 2);
		TokenStream stream = streamOf("a", "a", "a", "a");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(2, match.matchedTokens().size());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchZeroConsumingInnerRuleBreaksLoop() {
		RepeatedTokenRule rule = new RepeatedTokenRule(new OptionalTokenRule(TokenRules.value("z", false)), 0, 5);
		TokenStream stream = streamOf("a");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void notMatchWithEmptyStreamReturnsNull() {
		TokenRule notRule = new RepeatedTokenRule(TokenRules.value("a", false), 1, 2).not();
		
		assertNull(notRule.match(TokenStream.createMutable(List.of()), TokenRuleContext.empty()));
	}
	
	@Test
	void notMatchWhenOccurrencesOutsideRangeReturnsMatch() {
		TokenRule notRule = new RepeatedTokenRule(TokenRules.value("a", false), 2, 2).not();
		TokenStream stream = streamOf("a");
		
		TokenRuleMatch match = notRule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
	}
	
	@Test
	void notMatchWhenOccurrencesInsideRangeReturnsNull() {
		TokenRule notRule = new RepeatedTokenRule(TokenRules.value("a", false), 1, 2).not();
		TokenStream stream = streamOf("a", "a");
		
		assertNull(notRule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void notOfNotReturnsOriginalRule() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 1, 2);
		
		assertSame(rule, rule.not().not());
	}
	
	@Test
	void matchWithinVariableRangeStopsAsSoonAsAvailableTokensRunOut() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 1, 5);
		TokenStream stream = streamOf("a", "a");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(2, match.matchedTokens().size());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void constructWithExactOccurrencesDelegateMatchesCorrectly() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 3);
		TokenStream stream = streamOf("a", "a", "a");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void matchStopsAtFirstNonMatchingTokenWithinRange() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 1, 5);
		TokenStream stream = streamOf("a", "a", "b", "a");
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(2, match.matchedTokens().size());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void notNegationRoundTripAcrossDifferentOccurrenceCounts() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 2, 2);
		
		TokenStream streamA = streamOf("a", "a");
		TokenStream streamACopy = streamOf("a", "a");
		assertNotNull(rule.match(streamA, TokenRuleContext.empty()));
		assertNull(rule.not().match(streamACopy, TokenRuleContext.empty()));
		
		TokenStream streamB = streamOf("a");
		TokenStream streamBCopy = streamOf("a");
		assertNull(rule.match(streamB, TokenRuleContext.empty()));
		assertNotNull(rule.not().match(streamBCopy, TokenRuleContext.empty()));
	}
	
	@Test
	void notMatchAdvancesUnderlyingStreamEvenWhenReturningNull() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.value("a", false), 1, 2);
		TokenStream stream = streamOf("a", "a");
		
		assertNull(rule.not().match(stream, TokenRuleContext.empty()));
		assertEquals(2, stream.getCurrentIndex());
	}
}
