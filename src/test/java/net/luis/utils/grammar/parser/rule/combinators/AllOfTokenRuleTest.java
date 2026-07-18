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
import net.luis.utils.grammar.parser.rule.matchers.TypeTokenRule;
import net.luis.utils.grammar.parser.rule.matchers.ValueTokenRule;
import net.luis.utils.grammar.parser.rule.quantifiers.OptionalTokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.*;
import net.luis.utils.grammar.token.type.StandardTokenType;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AllOfTokenRule}.<br>
 *
 * @author Luis-St
 */
class AllOfTokenRuleTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructWithValidTokenRules() {
		AllOfTokenRule rule = new AllOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		
		assertNotNull(rule);
		assertEquals(2, rule.tokenRules().size());
	}
	
	@Test
	void constructWithNullTokenRules() {
		assertThrows(NullPointerException.class, () -> new AllOfTokenRule(null));
	}
	
	@Test
	void constructListIsDefensivelyCopied() {
		List<TokenRule> source = new ArrayList<>(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		AllOfTokenRule rule = new AllOfTokenRule(source);
		
		source.add(AlwaysMatchTokenRule.INSTANCE);
		
		assertEquals(2, rule.tokenRules().size());
		assertThrows(UnsupportedOperationException.class, () -> rule.tokenRules().add(AlwaysMatchTokenRule.INSTANCE));
	}
	
	@Test
	void constructWithTokenRuleListContainingNullElementThrowsException() {
		assertThrows(NullPointerException.class, () -> new AllOfTokenRule(Arrays.asList(AlwaysMatchTokenRule.INSTANCE, null)));
	}
	
	@Test
	void constructWithEmptyTokenRulesThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> new AllOfTokenRule(List.of()));
	}
	
	@Test
	void constructWithSingleTokenRuleThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> new AllOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE)));
	}
	
	@Test
	void matchWithNullStreamThrowsException() {
		AllOfTokenRule rule = new AllOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		
		assertThrows(NullPointerException.class, () -> rule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNullContextThrowsException() {
		AllOfTokenRule rule = new AllOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithSubRuleMatchingMultipleTokensThrowsException() {
		SequenceTokenRule multiTokenRule = new SequenceTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		AllOfTokenRule rule = new AllOfTokenRule(List.of(multiTokenRule, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")));
		
		assertThrows(IllegalStateException.class, () -> rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithFirstSubRuleNotMatchingReturnsNull() {
		AllOfTokenRule rule = new AllOfTokenRule(List.of(NeverMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a")));
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithLaterSubRuleNotMatchingReturnsNull() {
		AllOfTokenRule rule = new AllOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a")));
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithAllSubRulesMatchingReturnsMatch() {
		AllOfTokenRule rule = new AllOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a")), 0);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertSame(rule, match.matchingTokenRule());
	}
	
	@Test
	void matchWithSubRuleProducingZeroLengthMatchDoesNotThrow() {
		AllOfTokenRule rule = new AllOfTokenRule(List.of(new OptionalTokenRule(NeverMatchTokenRule.INSTANCE), AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a")));
		
		TokenRuleMatch match = assertDoesNotThrow(() -> rule.match(stream, TokenRuleContext.empty()));
		
		assertNotNull(match);
	}
	
	@Test
	void matchWithThreeMatchingSubRulesReturnsMatch() {
		AllOfTokenRule rule = new AllOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("a")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.matchedTokens().size());
		assertSame(rule, match.matchingTokenRule());
	}
	
	@Test
	void matchUsingValueAndTypeSubRulesOnMatchingToken() {
		Token tok = new SimpleToken("foo", TokenPosition.UNPOSITIONED, Set.of(StandardTokenType.KEYWORD));
		AllOfTokenRule rule = new AllOfTokenRule(List.of(new ValueTokenRule("foo", false), new TypeTokenRule(Set.of(StandardTokenType.KEYWORD))));
		TokenStream stream = TokenStream.createMutable(List.of(tok));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
	}
	
	@Test
	void notNegatesEverySubRule() {
		AllOfTokenRule rule = new AllOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		
		TokenRule notRule = rule.not();
		
		assertInstanceOf(AllOfTokenRule.class, notRule);
		AllOfTokenRule casted = (AllOfTokenRule) notRule;
		assertEquals(rule.tokenRules().size(), casted.tokenRules().size());
		for (TokenRule element : casted.tokenRules()) {
			assertSame(NeverMatchTokenRule.INSTANCE, element);
		}
		assertNotSame(rule, notRule);
	}
	
	@Test
	void matchAdvancesStreamPastFirstTokenOnly() {
		AllOfTokenRule rule = new AllOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		TokenStream stream = TokenStream.createMutable(List.of(token("A"), token("B")), 0);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void notResultRoundTripsBackToOriginalSemantics() {
		AllOfTokenRule rule = new AllOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		TokenRule negated = rule.not();
		TokenRule doubleNegated = negated.not();
		
		assertNotNull(rule.match(TokenStream.createMutable(List.of(token("a"))), TokenRuleContext.empty()));
		assertNull(negated.match(TokenStream.createMutable(List.of(token("a"))), TokenRuleContext.empty()));
		assertNotNull(doubleNegated.match(TokenStream.createMutable(List.of(token("a"))), TokenRuleContext.empty()));
	}
	
	@Test
	void matchAtNonZeroCurrentIndexUsesCorrectToken() {
		AllOfTokenRule rule = new AllOfTokenRule(List.of(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
		Token tokenB = token("B");
		TokenStream stream = TokenStream.createMutable(List.of(token("A"), tokenB, token("C")), 1);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(2, match.endIndex());
		assertEquals(List.of(tokenB), match.matchedTokens());
	}
}
