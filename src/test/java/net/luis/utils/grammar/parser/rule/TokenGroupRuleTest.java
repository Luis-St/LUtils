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
import net.luis.utils.grammar.token.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenGroupRule}.<br>
 *
 * @author Luis-St
 */
class TokenGroupRuleTest {
	
	@Test
	void constructWithValidTokenRule() {
		TokenGroupRule rule = new TokenGroupRule(AlwaysMatchTokenRule.INSTANCE);
		assertNotNull(rule);
		assertSame(AlwaysMatchTokenRule.INSTANCE, rule.tokenRule());
	}
	
	@Test
	void constructWithNullTokenRuleThrowsException() {
		assertThrows(NullPointerException.class, () -> new TokenGroupRule(null));
	}
	
	@Test
	void matchWithNullStreamThrowsException() {
		TokenGroupRule rule = new TokenGroupRule(AlwaysMatchTokenRule.INSTANCE);
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, ctx));
	}
	
	@Test
	void matchWithNullContextThrowsException() {
		TokenGroupRule rule = new TokenGroupRule(AlwaysMatchTokenRule.INSTANCE);
		TokenStream stream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("a")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithNoMoreTokensReturnsNull() {
		TokenGroupRule rule = new TokenGroupRule(AlwaysMatchTokenRule.INSTANCE);
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		assertNull(rule.match(stream, ctx));
	}
	
	@Test
	void matchWithNonGroupCurrentTokenReturnsNull() {
		TokenGroupRule rule = new TokenGroupRule(AlwaysMatchTokenRule.INSTANCE);
		TokenStream stream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("plain")));
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		assertNull(rule.match(stream, ctx));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithInnerRuleNotMatchingGroupContentsReturnsNull() {
		TokenGroupRule rule = new TokenGroupRule(NeverMatchTokenRule.INSTANCE);
		TokenGroup tokenGroup = new TokenGroup(List.of(SimpleToken.createUnpositioned("inner")));
		TokenStream stream = TokenStream.createMutable(List.of(tokenGroup));
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		assertNull(rule.match(stream, ctx));
	}
	
	@Test
	void matchWithInnerRuleMatchingGroupContentsReturnsMatch() {
		TokenGroupRule rule = new TokenGroupRule(AlwaysMatchTokenRule.INSTANCE);
		TokenGroup tokenGroup = new TokenGroup(List.of(SimpleToken.createUnpositioned("inner")));
		TokenStream stream = TokenStream.createMutable(List.of(tokenGroup));
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, ctx);
		
		assertNotNull(match);
		assertSame(rule, match.matchingTokenRule());
		assertEquals(List.of(tokenGroup), match.matchedTokens());
	}
	
	@Test
	void matchReturnsGroupItselfAsSingleMatchedTokenNotInnerTokens() {
		TokenGroupRule rule = new TokenGroupRule(AlwaysMatchTokenRule.INSTANCE);
		TokenGroup tokenGroup = new TokenGroup(List.of(
			SimpleToken.createUnpositioned("a"),
			SimpleToken.createUnpositioned("b"),
			SimpleToken.createUnpositioned("c")
		));
		TokenStream stream = TokenStream.createMutable(List.of(tokenGroup));
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(1, match.matchedTokens().size());
		assertSame(tokenGroup, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchAdvancesOuterStreamByExactlyOne() {
		TokenGroupRule rule = new TokenGroupRule(AlwaysMatchTokenRule.INSTANCE);
		TokenGroup tokenGroup = new TokenGroup(List.of(
			SimpleToken.createUnpositioned("a"),
			SimpleToken.createUnpositioned("b")
		));
		Token otherToken = SimpleToken.createUnpositioned("other");
		TokenStream stream = TokenStream.createMutable(List.of(tokenGroup, otherToken));
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(1, match.endIndex());
		assertEquals(1, stream.getCurrentIndex());
		assertSame(otherToken, stream.getCurrentToken());
	}
	
	@Test
	void matchAtNonZeroStartIndexReturnsCorrectStartIndex() {
		TokenGroupRule rule = new TokenGroupRule(AlwaysMatchTokenRule.INSTANCE);
		Token plainToken = SimpleToken.createUnpositioned("plain");
		TokenGroup tokenGroup = new TokenGroup(List.of(SimpleToken.createUnpositioned("inner")));
		TokenStream stream = TokenStream.createMutable(List.of(plainToken, tokenGroup), 1);
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
	}
	
	@Test
	void innerRuleMatchesAgainstIndependentMutableStreamOfGroupTokens() {
		TokenGroupRule rule = new TokenGroupRule(AlwaysMatchTokenRule.INSTANCE);
		TokenGroup tokenGroup = new TokenGroup(List.of(
			SimpleToken.createUnpositioned("a"),
			SimpleToken.createUnpositioned("b")
		));
		Token trailingToken = SimpleToken.createUnpositioned("trailing");
		TokenStream stream = TokenStream.createMutable(List.of(tokenGroup, trailingToken));
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
		assertSame(trailingToken, stream.getCurrentToken());
	}
	
	@Test
	void notDelegatesNegationToInnerRuleAndWrapsResultInNewGroupRule() {
		TokenGroupRule rule = new TokenGroupRule(AlwaysMatchTokenRule.INSTANCE);
		
		TokenRule negated = rule.not();
		
		assertNotSame(rule, negated);
		assertInstanceOf(TokenGroupRule.class, negated);
		TokenGroupRule negatedGroupRule = (TokenGroupRule) negated;
		assertSame(NeverMatchTokenRule.INSTANCE, negatedGroupRule.tokenRule());
		
		TokenGroup tokenGroup = new TokenGroup(List.of(SimpleToken.createUnpositioned("inner")));
		TokenStream stream = TokenStream.createMutable(List.of(tokenGroup));
		TokenRuleContext ctx = TokenRuleContext.empty();
		assertNull(negated.match(stream, ctx));
	}
}
