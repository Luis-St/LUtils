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
 * Test class for {@link AlwaysMatchTokenRule}.<br>
 *
 * @author Luis-St
 */
class AlwaysMatchTokenRuleTest {
	
	@Test
	void instanceIsSingletonAndNotNull() {
		assertNotNull(AlwaysMatchTokenRule.INSTANCE);
		assertSame(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE);
	}
	
	@Test
	void matchWithNullStreamThrowsException() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		assertThrows(NullPointerException.class, () -> AlwaysMatchTokenRule.INSTANCE.match(null, ctx));
	}
	
	@Test
	void matchWithNullContextThrowsException() {
		TokenStream stream = TokenStream.createMutable(List.of());
		assertThrows(NullPointerException.class, () -> AlwaysMatchTokenRule.INSTANCE.match(stream, null));
	}
	
	@Test
	void matchWithBothNullArgumentsThrowsForStreamFirst() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> AlwaysMatchTokenRule.INSTANCE.match(null, null));
		assertEquals("Token stream must not be null", exception.getMessage());
	}
	
	@Test
	void matchWithNoMoreTokensReturnsNull() {
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext ctx = TokenRuleContext.empty();
		assertNull(AlwaysMatchTokenRule.INSTANCE.match(stream, ctx));
	}
	
	@Test
	void matchWithMoreTokensReturnsMatch() {
		Token token = SimpleToken.createUnpositioned("abc");
		TokenStream stream = TokenStream.createMutable(List.of(token));
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch match = AlwaysMatchTokenRule.INSTANCE.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(List.of(token), match.matchedTokens());
		assertSame(AlwaysMatchTokenRule.INSTANCE, match.matchingTokenRule());
	}
	
	@Test
	void matchAdvancesStreamCurrentIndex() {
		Token first = SimpleToken.createUnpositioned("a");
		Token second = SimpleToken.createUnpositioned("b");
		TokenStream stream = TokenStream.createMutable(List.of(first, second));
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch match = AlwaysMatchTokenRule.INSTANCE.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(1, match.endIndex());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchAtNonZeroStartIndexReturnsCorrectStartIndex() {
		Token first = SimpleToken.createUnpositioned("a");
		Token second = SimpleToken.createUnpositioned("b");
		Token third = SimpleToken.createUnpositioned("c");
		TokenStream stream = TokenStream.createMutable(List.of(first, second, third), 1);
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch match = AlwaysMatchTokenRule.INSTANCE.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(List.of(second), match.matchedTokens());
	}
	
	@Test
	void matchOnLastRemainingTokenReturnsMatch() {
		Token a = SimpleToken.createUnpositioned("a");
		Token b = SimpleToken.createUnpositioned("b");
		TokenStream stream = TokenStream.createMutable(List.of(a, b), 1);
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch match = AlwaysMatchTokenRule.INSTANCE.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(List.of(b), match.matchedTokens());
		assertFalse(stream.hasMoreTokens());
	}
	
	@Test
	void repeatedMatchCallsConsumeTokensSequentially() {
		Token a = SimpleToken.createUnpositioned("a");
		Token b = SimpleToken.createUnpositioned("b");
		Token c = SimpleToken.createUnpositioned("c");
		TokenStream stream = TokenStream.createMutable(List.of(a, b, c));
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch first = AlwaysMatchTokenRule.INSTANCE.match(stream, ctx);
		TokenRuleMatch second = AlwaysMatchTokenRule.INSTANCE.match(stream, ctx);
		TokenRuleMatch third = AlwaysMatchTokenRule.INSTANCE.match(stream, ctx);
		TokenRuleMatch fourth = AlwaysMatchTokenRule.INSTANCE.match(stream, ctx);
		
		assertNotNull(first);
		assertEquals(0, first.startIndex());
		assertEquals(List.of(a), first.matchedTokens());
		assertNotNull(second);
		assertEquals(1, second.startIndex());
		assertEquals(List.of(b), second.matchedTokens());
		assertNotNull(third);
		assertEquals(2, third.startIndex());
		assertEquals(List.of(c), third.matchedTokens());
		assertNull(fourth);
	}
	
	@Test
	void notReturnsNeverMatchSingletonConsistently() {
		TokenStream stream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("a")));
		TokenRuleContext ctx = TokenRuleContext.empty();
		AlwaysMatchTokenRule.INSTANCE.match(stream, ctx);
		
		TokenRule firstNot = AlwaysMatchTokenRule.INSTANCE.not();
		TokenRule secondNot = AlwaysMatchTokenRule.INSTANCE.not();
		
		assertSame(NeverMatchTokenRule.INSTANCE, firstNot);
		assertSame(firstNot, secondNot);
		assertInstanceOf(NeverMatchTokenRule.class, firstNot);
	}
}
