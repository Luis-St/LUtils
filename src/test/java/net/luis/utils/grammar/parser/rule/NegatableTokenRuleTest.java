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
 * Test class for {@link NegatableTokenRule}.<br>
 *
 * @author Luis-St
 */
class NegatableTokenRuleTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static NegatableTokenRule matchesX() {
		return t -> "x".equals(t.value());
	}
	
	@Test
	void matchStreamWithNullStream() {
		NegatableTokenRule rule = matchesX();
		assertThrows(NullPointerException.class, () -> rule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchStreamWithNullContext() {
		NegatableTokenRule rule = matchesX();
		assertThrows(NullPointerException.class, () -> rule.match(TokenStream.createMutable(List.of()), null));
	}
	
	@Test
	void notMatchStreamWithNullStream() {
		NegatableTokenRule rule = matchesX();
		assertThrows(NullPointerException.class, () -> rule.not().match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void notMatchStreamWithNullContext() {
		NegatableTokenRule rule = matchesX();
		assertThrows(NullPointerException.class, () -> rule.not().match(TokenStream.createMutable(List.of()), null));
	}
	
	@Test
	void matchStreamWithNoMoreTokensReturnsNull() {
		NegatableTokenRule rule = matchesX();
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchStreamWithMatchingTokenReturnsMatch() {
		NegatableTokenRule rule = matchesX();
		TokenStream stream = TokenStream.createMutable(List.of(token("x")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(List.of(token("x")), match.matchedTokens());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchStreamWithNonMatchingTokenReturnsNull() {
		NegatableTokenRule rule = matchesX();
		TokenStream stream = TokenStream.createMutable(List.of(token("y")));
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void notMatchStreamWithNoMoreTokensReturnsNull() {
		NegatableTokenRule rule = matchesX();
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertNull(rule.not().match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void notMatchStreamWithNonMatchingOriginalReturnsMatch() {
		NegatableTokenRule rule = matchesX();
		TokenStream stream = TokenStream.createMutable(List.of(token("y")));
		
		assertNotNull(rule.not().match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void notMatchStreamWithMatchingOriginalReturnsNull() {
		NegatableTokenRule rule = matchesX();
		TokenStream stream = TokenStream.createMutable(List.of(token("x")));
		
		assertNull(rule.not().match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void notOfNotReturnsOriginalRule() {
		NegatableTokenRule rule = matchesX();
		
		assertSame(rule, rule.not().not());
	}
	
	@Test
	void matchStreamMatchReflectsStartAndEndIndex() {
		NegatableTokenRule rule = matchesX();
		TokenStream stream = TokenStream.createMutable(List.of(token("x"), token("y")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertSame(rule, match.matchingTokenRule());
	}
	
	@Test
	void notMatchStreamMatchReferencesNegatedRule() {
		NegatableTokenRule rule = matchesX();
		TokenStream stream = TokenStream.createMutable(List.of(token("y")));
		TokenRule negated = rule.not();
		
		TokenRuleMatch match = negated.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertSame(negated, match.matchingTokenRule());
	}
	
	@Test
	void notMatchStreamMatchReflectsStartAndEndIndexAndConsumesToken() {
		NegatableTokenRule rule = matchesX();
		TokenStream stream = TokenStream.createMutable(List.of(token("y"), token("x")));
		
		TokenRuleMatch match = rule.not().match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(List.of(token("y")), match.matchedTokens());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchStreamConsumesOnlyOneTokenFromMultiTokenStream() {
		NegatableTokenRule rule = matchesX();
		TokenStream stream = TokenStream.createMutable(List.of(token("x"), token("x"), token("y")));
		
		TokenRuleMatch first = rule.match(stream, TokenRuleContext.empty());
		assertNotNull(first);
		assertEquals(1, stream.getCurrentIndex());
		
		TokenRuleMatch second = rule.match(stream, TokenRuleContext.empty());
		assertNotNull(second);
		assertEquals(2, stream.getCurrentIndex());
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void notThenMatchThenNotAgainBehavesConsistently() {
		NegatableTokenRule rule = matchesX();
		TokenStream stream1 = TokenStream.createMutable(List.of(token("y")));
		TokenStream stream2 = TokenStream.createMutable(List.of(token("y")));
		TokenStream stream3 = TokenStream.createMutable(List.of(token("y")));
		
		assertNull(rule.match(stream1, TokenRuleContext.empty()));
		assertNotNull(rule.not().match(stream2, TokenRuleContext.empty()));
		assertNull(rule.not().not().match(stream3, TokenRuleContext.empty()));
	}
}
