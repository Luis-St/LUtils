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

package net.luis.utils.grammar.parser.rule.assertions.anchors;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.rule.TokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EndTokenRule}.<br>
 *
 * @author Luis-St
 */
class EndTokenRuleTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static Token positionedToken(String value, int line, int characterInLine, int character) {
		return new SimpleToken(value, new TokenPosition(line, characterInLine, character));
	}
	
	@Test
	void valuesContainsDocumentAndLineConstants() {
		EndTokenRule[] values = EndTokenRule.values();
		
		assertEquals(2, values.length);
		assertSame(EndTokenRule.DOCUMENT, values[0]);
		assertSame(EndTokenRule.LINE, values[1]);
	}
	
	@Test
	void valueOfReturnsDocumentConstant() {
		assertSame(EndTokenRule.DOCUMENT, EndTokenRule.valueOf("DOCUMENT"));
	}
	
	@Test
	void valueOfReturnsLineConstant() {
		assertSame(EndTokenRule.LINE, EndTokenRule.valueOf("LINE"));
	}
	
	@Test
	void matchDocumentWithNullStreamThrowsException() {
		assertThrows(NullPointerException.class, () -> EndTokenRule.DOCUMENT.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchDocumentWithNullContextThrowsException() {
		assertThrows(NullPointerException.class, () -> EndTokenRule.DOCUMENT.match(TokenStream.createMutable(List.of()), null));
	}
	
	@Test
	void matchLineWithNullStreamThrowsException() {
		assertThrows(NullPointerException.class, () -> EndTokenRule.LINE.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchLineWithNullContextThrowsException() {
		assertThrows(NullPointerException.class, () -> EndTokenRule.LINE.match(TokenStream.createMutable(List.of()), null));
	}
	
	@Test
	void notMatchWithNullStreamThrowsException() {
		assertThrows(NullPointerException.class, () -> EndTokenRule.DOCUMENT.not().match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void notMatchWithNullContextThrowsException() {
		assertThrows(NullPointerException.class, () -> EndTokenRule.DOCUMENT.not().match(TokenStream.createMutable(List.of()), null));
	}
	
	@Test
	void valueOfWithNullNameThrowsException() {
		assertThrows(NullPointerException.class, () -> EndTokenRule.valueOf(null));
	}
	
	@Test
	void valueOfWithUnknownNameThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> EndTokenRule.valueOf("UNKNOWN"));
	}
	
	@Test
	void matchDocumentAtEndOfStreamReturnsEmptyMatch() {
		TokenStream stream = TokenStream.createMutable(List.of());
		
		TokenRuleMatch match = EndTokenRule.DOCUMENT.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertSame(EndTokenRule.DOCUMENT, match.matchingTokenRule());
	}
	
	@Test
	void matchDocumentWithMoreTokensReturnsNull() {
		TokenStream stream = TokenStream.createMutable(List.of(token("a")), 0);
		
		assertNull(EndTokenRule.DOCUMENT.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchLineAtEndOfStreamReturnsEmptyMatch() {
		TokenStream stream = TokenStream.createMutable(List.of());
		
		TokenRuleMatch match = EndTokenRule.LINE.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertSame(EndTokenRule.LINE, match.matchingTokenRule());
	}
	
	@Test
	void matchLineAtLastTokenWithNewlineInValueReturnsEmptyMatch() {
		TokenStream stream = TokenStream.createMutable(List.of(token("line\n")), 0);
		
		TokenRuleMatch match = EndTokenRule.LINE.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
	}
	
	@Test
	void matchLineAtLastTokenWithoutNewlineReturnsNull() {
		TokenStream stream = TokenStream.createMutable(List.of(token("end")), 0);
		
		assertNull(EndTokenRule.LINE.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchLineBothTokensPositionedIncreasingLineReturnsEmptyMatch() {
		TokenStream stream = TokenStream.createMutable(List.of(positionedToken("a", 0, 0, 0), positionedToken("b", 1, 0, 1)), 0);
		
		TokenRuleMatch match = EndTokenRule.LINE.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
	}
	
	@Test
	void matchLineBothTokensPositionedSameLineWithoutNewlineReturnsNull() {
		TokenStream stream = TokenStream.createMutable(List.of(positionedToken("a", 0, 0, 0), positionedToken("b", 0, 1, 1)), 0);
		
		assertNull(EndTokenRule.LINE.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchLineBothTokensPositionedSameLineWithNewlineInValueReturnsEmptyMatch() {
		TokenStream stream = TokenStream.createMutable(List.of(positionedToken("a\n", 0, 0, 0), positionedToken("b", 0, 1, 2)), 0);
		
		TokenRuleMatch match = EndTokenRule.LINE.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
	}
	
	@Test
	void matchLineCurrentTokenUnpositionedReturnsNull() {
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), positionedToken("b", 0, 1, 1)), 0);
		
		assertNull(EndTokenRule.LINE.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchLineNextTokenUnpositionedReturnsNull() {
		TokenStream stream = TokenStream.createMutable(List.of(positionedToken("a", 0, 0, 0), token("b")), 0);
		
		assertNull(EndTokenRule.LINE.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchLineNextTokenUnpositionedWithNewlineInCurrentReturnsEmptyMatch() {
		TokenStream stream = TokenStream.createMutable(List.of(positionedToken("a\n", 0, 0, 0), token("b")), 0);
		
		TokenRuleMatch match = EndTokenRule.LINE.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
	}
	
	@Test
	void notReturnsNullWhenUnderlyingRuleMatches() {
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertNull(EndTokenRule.DOCUMENT.not().match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void notReturnsEmptyMatchWhenUnderlyingRuleDoesNotMatch() {
		TokenStream stream = TokenStream.createMutable(List.of(token("a")), 0);
		TokenRule notRule = EndTokenRule.DOCUMENT.not();
		
		TokenRuleMatch match = notRule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertSame(notRule, match.matchingTokenRule());
		assertNotSame(EndTokenRule.DOCUMENT, match.matchingTokenRule());
	}
	
	@Test
	void matchDocumentOnEmptyStreamReturnsMatchAtIndexZero() {
		TokenRuleMatch match = EndTokenRule.DOCUMENT.match(TokenStream.EMPTY, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
	}
	
	@Test
	void matchDocumentAfterConsumingAllTokensReturnsEmptyMatch() {
		TokenStream stream = TokenStream.createMutable(List.of(token("A"), token("B")), 2);
		
		TokenRuleMatch match = EndTokenRule.DOCUMENT.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(2, match.startIndex());
		assertEquals(2, match.endIndex());
	}
	
	@Test
	void matchLineWithSingleUnpositionedTokenWithoutNewlineReturnsNull() {
		TokenStream stream = TokenStream.createMutable(List.of(token("end")), 0);
		
		assertNull(EndTokenRule.LINE.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchLineWithMultiTokenStreamAllPositionedIncreasingLinesReturnsEmptyMatch() {
		TokenStream stream = TokenStream.createMutable(List.of(positionedToken("a", 0, 0, 0), positionedToken("b", 0, 1, 1), positionedToken("c", 1, 0, 2)), 1);
		
		TokenRuleMatch match = EndTokenRule.LINE.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
	}
	
	@Test
	void notNegatesDocumentMatchResult() {
		TokenRule notRule = EndTokenRule.DOCUMENT.not();
		
		assertNull(notRule.match(TokenStream.createMutable(List.of()), TokenRuleContext.empty()));
		assertNotNull(notRule.match(TokenStream.createMutable(List.of(token("a")), 0), TokenRuleContext.empty()));
	}
	
	@Test
	void notNotReturnsOriginalEndTokenRuleConstant() {
		assertSame(EndTokenRule.DOCUMENT, EndTokenRule.DOCUMENT.not().not());
		assertSame(EndTokenRule.LINE, EndTokenRule.LINE.not().not());
	}
	
	@Test
	void matchLineAcrossMultiTokenStreamAtEachPositionReflectsLineBoundaries() {
		Token tok0 = positionedToken("a", 0, 0, 0);
		Token tok1 = positionedToken("b\n", 0, 1, 1);
		Token tok2 = positionedToken("c", 1, 0, 2);
		Token tok3 = positionedToken("d\n", 1, 1, 3);
		List<Token> tokens = List.of(tok0, tok1, tok2, tok3);
		
		assertNull(EndTokenRule.LINE.match(TokenStream.createMutable(tokens, 0), TokenRuleContext.empty()));
		assertNotNull(EndTokenRule.LINE.match(TokenStream.createMutable(tokens, 1), TokenRuleContext.empty()));
		assertNull(EndTokenRule.LINE.match(TokenStream.createMutable(tokens, 2), TokenRuleContext.empty()));
		assertNotNull(EndTokenRule.LINE.match(TokenStream.createMutable(tokens, 3), TokenRuleContext.empty()));
	}
	
	@Test
	void notCombinedWithLineMatchOverMixedPositionedTokensReturnsExpectedNegation() {
		List<Token> tokens = List.of(positionedToken("a", 0, 0, 0), token("b"));
		TokenRule notLine = EndTokenRule.LINE.not();
		
		TokenRuleMatch direct = EndTokenRule.LINE.match(TokenStream.createMutable(tokens, 0), TokenRuleContext.empty());
		TokenRuleMatch negated = notLine.match(TokenStream.createMutable(tokens, 0), TokenRuleContext.empty());
		
		assertNull(direct);
		assertNotNull(negated);
	}
}
