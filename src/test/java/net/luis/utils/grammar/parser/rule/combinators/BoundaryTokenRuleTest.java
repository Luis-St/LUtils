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
import net.luis.utils.grammar.parser.rule.assertions.anchors.EndTokenRule;
import net.luis.utils.grammar.parser.rule.matchers.ValueTokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BoundaryTokenRule}.<br>
 *
 * @author Luis-St
 */
class BoundaryTokenRuleTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static ValueTokenRule value(String literal) {
		return new ValueTokenRule(literal, false);
	}
	
	@Test
	void constructWithStartAndEndRule() {
		BoundaryTokenRule rule = new BoundaryTokenRule(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE);
		
		assertSame(AlwaysMatchTokenRule.INSTANCE, rule.startTokenRule());
		assertSame(AlwaysMatchTokenRule.INSTANCE, rule.endTokenRule());
		assertSame(TokenRules.alwaysMatch(), rule.betweenTokenRule());
	}
	
	@Test
	void constructWithNullStartRuleTwoArg() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(null, AlwaysMatchTokenRule.INSTANCE));
	}
	
	@Test
	void constructWithNullEndRuleTwoArg() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(AlwaysMatchTokenRule.INSTANCE, null));
	}
	
	@Test
	void constructWithStartBetweenAndEndRule() {
		BoundaryTokenRule rule = new BoundaryTokenRule(value("S"), value("M"), value("E"));
		
		assertEquals(value("S"), rule.startTokenRule());
		assertEquals(value("M"), rule.betweenTokenRule());
		assertEquals(value("E"), rule.endTokenRule());
	}
	
	@Test
	void constructWithNullStartRuleThreeArg() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(null, AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE));
	}
	
	@Test
	void constructWithNullBetweenRuleThreeArg() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(AlwaysMatchTokenRule.INSTANCE, null, AlwaysMatchTokenRule.INSTANCE));
	}
	
	@Test
	void constructWithNullEndRuleThreeArg() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE, null));
	}
	
	@Test
	void matchWithNullStream() {
		BoundaryTokenRule rule = new BoundaryTokenRule(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> rule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNullContext() {
		BoundaryTokenRule rule = new BoundaryTokenRule(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE);
		TokenStream stream = TokenStream.createMutable(List.of(token("a")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchEmptyStreamReturnsNull() {
		BoundaryTokenRule rule = new BoundaryTokenRule(AlwaysMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE);
		
		assertNull(rule.match(TokenStream.EMPTY, TokenRuleContext.empty()));
	}
	
	@Test
	void matchNonEmptyStreamPassesInitialGuard() {
		BoundaryTokenRule rule = new BoundaryTokenRule(value("S"), value("E"));
		TokenStream stream = TokenStream.createMutable(List.of(token("S"), token("E")));
		
		assertNotNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchStartRuleFails() {
		BoundaryTokenRule rule = new BoundaryTokenRule(NeverMatchTokenRule.INSTANCE, AlwaysMatchTokenRule.INSTANCE);
		TokenStream stream = TokenStream.createMutable(List.of(token("a")));
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchStartRuleSucceeds() {
		BoundaryTokenRule rule = new BoundaryTokenRule(value("S"), value("E"));
		TokenStream stream = TokenStream.createMutable(List.of(token("S"), token("E")));
		
		assertNotNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWhileLoopNotTakenBecauseNoMoreTokens() {
		BoundaryTokenRule rule = new BoundaryTokenRule(value("S"), EndTokenRule.DOCUMENT);
		TokenStream stream = TokenStream.createMutable(List.of(token("S")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
	}
	
	@Test
	void matchWhileLoopTakenWithImmediateEndMatch() {
		BoundaryTokenRule rule = new BoundaryTokenRule(value("S"), value("E"));
		TokenStream stream = TokenStream.createMutable(List.of(token("S"), token("E")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(2, match.endIndex());
	}
	
	@Test
	void matchBetweenRuleFailsInsideLoop() {
		BoundaryTokenRule rule = new BoundaryTokenRule(value("S"), NeverMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE);
		TokenStream stream = TokenStream.createMutable(List.of(token("S"), token("X")));
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchBetweenRuleSucceedsAndMoreTokensRemain() {
		BoundaryTokenRule rule = new BoundaryTokenRule(value("S"), value("M"), value("E"));
		TokenStream stream = TokenStream.createMutable(List.of(token("S"), token("M"), token("E")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
		assertTrue(match.matchedTokens().stream().anyMatch(t -> "M".equals(t.value())));
	}
	
	@Test
	void matchBetweenRuleSucceedsAndNoMoreTokensRemain() {
		BoundaryTokenRule rule = new BoundaryTokenRule(value("S"), value("M"), EndTokenRule.DOCUMENT);
		TokenStream stream = TokenStream.createMutable(List.of(token("S"), token("M")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(2, match.endIndex());
	}
	
	@Test
	void matchFinalEndCheckSucceedsAfterLoopExit() {
		BoundaryTokenRule rule = new BoundaryTokenRule(value("S"), AlwaysMatchTokenRule.INSTANCE, EndTokenRule.DOCUMENT);
		TokenStream stream = TokenStream.createMutable(List.of(token("S"), token("M1"), token("M2")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(3, match.endIndex());
	}
	
	@Test
	void matchFinalEndCheckFailsAfterLoopExit() {
		BoundaryTokenRule rule = new BoundaryTokenRule(value("S"), AlwaysMatchTokenRule.INSTANCE, NeverMatchTokenRule.INSTANCE);
		TokenStream stream = TokenStream.createMutable(List.of(token("S"), token("M")));
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchTypicalStartBetweenEndSequence() {
		BoundaryTokenRule rule = new BoundaryTokenRule(value("("), value("x"), value(")"));
		TokenStream stream = TokenStream.createMutable(List.of(token("("), token("x"), token("x"), token(")")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(4, match.matchedTokens().size());
		assertEquals(4, match.endIndex());
	}
	
	@Test
	void matchAtNonZeroStreamOffset() {
		BoundaryTokenRule rule = new BoundaryTokenRule(value("S"), value("E"));
		TokenStream stream = TokenStream.createMutable(List.of(token("pad"), token("S"), token("E")), 1);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
	}
	
	@Test
	void notReturnsBoundaryWithNegatedComponents() {
		BoundaryTokenRule rule = new BoundaryTokenRule(value("S"), value("M"), value("E"));
		
		TokenRule notRule = rule.not();
		
		assertInstanceOf(BoundaryTokenRule.class, notRule);
		BoundaryTokenRule casted = (BoundaryTokenRule) notRule;
		
		assertNull(casted.startTokenRule().match(TokenStream.createMutable(List.of(token("S"))), TokenRuleContext.empty()));
		assertNotNull(casted.startTokenRule().match(TokenStream.createMutable(List.of(token("Z"))), TokenRuleContext.empty()));
		
		assertNull(casted.endTokenRule().match(TokenStream.createMutable(List.of(token("E"))), TokenRuleContext.empty()));
		assertNotNull(casted.endTokenRule().match(TokenStream.createMutable(List.of(token("Z"))), TokenRuleContext.empty()));
	}
	
	@Test
	void matchNestedBoundaryTokenRule() {
		BoundaryTokenRule inner = new BoundaryTokenRule(value("("), value(")"));
		BoundaryTokenRule outer = new BoundaryTokenRule(value("<"), inner, value(">"));
		TokenStream stream = TokenStream.createMutable(List.of(token("<"), token("("), token("x"), token(")"), token(">")));
		
		TokenRuleMatch match = outer.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(5, match.endIndex());
		assertEquals(5, match.matchedTokens().size());
	}
}
