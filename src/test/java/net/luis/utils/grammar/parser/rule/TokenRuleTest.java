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

package net.luis.utils.grammar.parser.rule;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRule}.<br>
 *
 * @author Luis-St
 */
class TokenRuleTest {
	
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
	
	private static TokenRule alwaysMatchLambda() {
		return (stream, _) -> {
			if (!stream.hasMoreTokens()) {
				return null;
			}
			int startIndex = stream.getCurrentIndex();
			Token current = stream.getCurrentToken();
			return new TokenRuleMatch(startIndex, stream.advance(), List.of(current), AlwaysMatchTokenRule.INSTANCE);
		};
	}
	
	private static TokenRule neverMatchLambda() {
		return (_, _) -> null;
	}
	
	@Test
	void atLeastWithNegativeMinThrowsException() {
		TokenRule rule = alwaysMatchLambda();
		
		assertThrows(IllegalArgumentException.class, () -> rule.atLeast(-1));
	}
	
	@Test
	void exactlyWithNegativeRepeatsThrowsException() {
		TokenRule rule = alwaysMatchLambda();
		
		assertThrows(IllegalArgumentException.class, () -> rule.exactly(-1));
	}
	
	@Test
	void atMostWithNegativeMaxThrowsException() {
		TokenRule rule = alwaysMatchLambda();
		
		assertThrows(IllegalArgumentException.class, () -> rule.atMost(-1));
	}
	
	@Test
	void atMostWithZeroMaxThrowsException() {
		TokenRule rule = alwaysMatchLambda();
		
		assertThrows(IllegalArgumentException.class, () -> rule.atMost(0));
	}
	
	@Test
	void betweenWithNegativeMinThrowsException() {
		TokenRule rule = alwaysMatchLambda();
		
		assertThrows(IllegalArgumentException.class, () -> rule.between(-1, 5));
	}
	
	@Test
	void betweenWithMaxLessThanMinThrowsException() {
		TokenRule rule = alwaysMatchLambda();
		
		assertThrows(IllegalArgumentException.class, () -> rule.between(5, 2));
	}
	
	@Test
	void betweenWithBothZeroThrowsException() {
		TokenRule rule = alwaysMatchLambda();
		
		assertThrows(IllegalArgumentException.class, () -> rule.between(0, 0));
	}
	
	@Test
	void exactlyWithZeroRepeatsThrowsException() {
		TokenRule rule = alwaysMatchLambda();
		
		assertThrows(IllegalArgumentException.class, () -> rule.exactly(0));
	}
	
	@Test
	void notThrowsUnsupportedOperationException() {
		TokenRule rule = alwaysMatchLambda();
		
		assertThrows(UnsupportedOperationException.class, rule::not);
	}
	
	@Test
	void atLeastWithZeroMinDoesNotThrow() {
		TokenRule rule = alwaysMatchLambda();
		
		TokenRule result = assertDoesNotThrow(() -> rule.atLeast(0));
		
		assertNotNull(result);
	}
	
	@Test
	void betweenWithMinEqualsMaxDoesNotThrow() {
		TokenRule rule = alwaysMatchLambda();
		TokenRule result = assertDoesNotThrow(() -> rule.between(3, 3));
		TokenStream stream = streamOf("a", "b", "c");
		
		TokenRuleMatch match = result.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void betweenWithMaxGreaterThanMinDoesNotThrow() {
		TokenRule rule = alwaysMatchLambda();
		
		assertDoesNotThrow(() -> rule.between(1, 5));
	}
	
	@Test
	void optionalWrapsRuleAndMatchesWhenPresent() {
		TokenRule rule = alwaysMatchLambda();
		TokenStream stream = streamOf("a");
		
		TokenRuleMatch match = rule.optional().match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.matchedTokens().size());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void optionalWrapsRuleAndMatchesEmptyWhenAbsent() {
		TokenRule rule = neverMatchLambda();
		TokenStream stream = streamOf("a");
		
		TokenRuleMatch match = rule.optional().match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(match.startIndex(), match.endIndex());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void atLeastWithPositiveMinMatchesRepeatedly() {
		TokenRule rule = alwaysMatchLambda();
		TokenStream stream = streamOf("a", "b", "c");
		
		TokenRuleMatch match = rule.atLeast(2).match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void exactlyWithPositiveRepeatsMatchesExactCount() {
		TokenRule rule = alwaysMatchLambda();
		TokenStream stream = streamOf("a", "b", "c", "d", "e");
		
		TokenRuleMatch match = rule.exactly(3).match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
		assertEquals(2, stream.size() - stream.getCurrentIndex());
	}
	
	@Test
	void atMostWithPositiveMaxLimitsMatches() {
		TokenRule rule = alwaysMatchLambda();
		TokenStream stream = streamOf("a", "b", "c", "d", "e");
		
		TokenRuleMatch match = rule.atMost(2).match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(2, match.matchedTokens().size());
	}
	
	@Test
	void zeroOrMoreMatchesAllAvailableTokens() {
		TokenRule rule = alwaysMatchLambda();
		TokenStream stream = streamOf("a", "b", "c", "d");
		
		TokenRuleMatch match = rule.zeroOrMore().match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(4, match.matchedTokens().size());
	}
	
	@Test
	void zeroOrMoreMatchesEmptyWhenNoTokensMatch() {
		TokenRule rule = neverMatchLambda();
		TokenStream stream = streamOf("a");
		
		TokenRuleMatch match = rule.zeroOrMore().match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void groupDelegatesToTokenGroupRule() {
		TokenRule rule = alwaysMatchLambda();
		
		TokenRule grouped = rule.group();
		
		assertInstanceOf(TokenGroupRule.class, grouped);
		assertSame(rule, ((TokenGroupRule) grouped).tokenRule());
	}
	
	@Test
	void lookaheadMatchesWithoutConsumingTokens() {
		TokenRule rule = alwaysMatchLambda();
		TokenStream stream = streamOf("a");
		
		TokenRuleMatch match = rule.lookahead().match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void negativeLookaheadMatchesWhenInnerRuleFails() {
		TokenRule rule = neverMatchLambda();
		TokenStream stream = streamOf("a");
		
		TokenRuleMatch match = rule.negativeLookahead().match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void lookbehindMatchesBehindCurrentPosition() {
		TokenRule rule = alwaysMatchLambda();
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")), 1);
		
		TokenRuleMatch match = rule.lookbehind().match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void negativeLookbehindMatchesWhenInnerRuleFailsBehind() {
		TokenRule rule = neverMatchLambda();
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")), 1);
		
		TokenRuleMatch match = rule.negativeLookbehind().match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void chainedQuantifiersComposeCorrectly() {
		TokenRule rule = alwaysMatchLambda();
		TokenRule chained = rule.between(1, 3).optional();
		
		TokenStream emptyStream = TokenStream.createMutable(List.of());
		TokenStream twoTokenStream = streamOf("a", "b");
		
		TokenRuleMatch emptyMatch = chained.match(emptyStream, TokenRuleContext.empty());
		TokenRuleMatch twoTokenMatch = chained.match(twoTokenStream, TokenRuleContext.empty());
		
		assertNull(emptyMatch);
		assertNotNull(twoTokenMatch);
		assertEquals(2, twoTokenMatch.matchedTokens().size());
	}
	
	@Test
	void lookaheadDoesNotAffectSubsequentAtLeastMatch() {
		TokenRule rule = alwaysMatchLambda();
		TokenStream stream = streamOf("a", "b", "c");
		
		TokenRuleMatch lookaheadMatch = rule.lookahead().match(stream, TokenRuleContext.empty());
		TokenRuleMatch atLeastMatch = rule.atLeast(1).match(stream, TokenRuleContext.empty());
		
		assertNotNull(lookaheadMatch);
		assertTrue(lookaheadMatch.matchedTokens().isEmpty());
		assertNotNull(atLeastMatch);
		assertEquals(3, atLeastMatch.matchedTokens().size());
	}
	
	@Test
	void notOnLambdaAlwaysThrowsRegardlessOfPriorDelegatedCalls() {
		TokenRule rule = alwaysMatchLambda();
		rule.optional();
		rule.atLeast(2);
		
		assertThrows(UnsupportedOperationException.class, rule::not);
	}
}
