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
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OptionalTokenRule}.<br>
 *
 * @author Luis-St
 */
class OptionalTokenRuleTest {
	
	@Test
	void constructWithTokenRule() {
		TokenRule inner = TokenRules.value("a", false);
		OptionalTokenRule rule = new OptionalTokenRule(inner);
		
		assertEquals(inner, rule.tokenRule());
	}
	
	@Test
	void constructWithNullTokenRule() {
		assertThrows(NullPointerException.class, () -> new OptionalTokenRule(null));
	}
	
	@Test
	void matchWithNullStream() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.value("a", false));
		assertThrows(NullPointerException.class, () -> rule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNullContext() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.value("a", false));
		assertThrows(NullPointerException.class, () -> rule.match(TokenStream.createMutable(List.of()), null));
	}
	
	@Test
	void matchReturnsInnerMatchWhenPresent() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.value("a", false));
		TokenStream stream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("a")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.matchedTokens().size());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchReturnsEmptyMatchWhenInnerRuleFailsWithinBounds() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.value("a", false));
		TokenStream stream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("b")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchReturnsNullWhenInnerRuleFailsAtEndOfStream() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.value("a", false));
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchOptionalRuleAtMidStreamPosition() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.value("a", false));
		TokenStream stream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("a"), SimpleToken.createUnpositioned("b")), 1);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void notWrapsNegatedInnerRule() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.value("a", false));
		
		TokenRule negated = rule.not();
		
		assertInstanceOf(OptionalTokenRule.class, negated);
		
		TokenStream nonMatchingStream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("b")));
		TokenRuleMatch nonMatchingMatch = negated.match(nonMatchingStream, TokenRuleContext.empty());
		assertNotNull(nonMatchingMatch);
		assertEquals(1, nonMatchingMatch.matchedTokens().size());
		
		TokenStream matchingStream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("a")));
		TokenRuleMatch matchingMatch = negated.match(matchingStream, TokenRuleContext.empty());
		assertNotNull(matchingMatch);
		assertTrue(matchingMatch.matchedTokens().isEmpty());
	}
	
	@Test
	void optionalWrappingAnotherOptionalAlwaysProducesAMatch() {
		OptionalTokenRule rule = new OptionalTokenRule(new OptionalTokenRule(TokenRules.value("a", false)));
		TokenStream stream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("b")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
	}
}
