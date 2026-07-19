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
 * Test class for {@link StartTokenRule}.<br>
 *
 * @author Luis-St
 */
class StartTokenRuleTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static Token positionedToken(String value, int line, int characterInLine, int character) {
		return new SimpleToken(value, new TokenPosition(line, characterInLine, character));
	}
	
	@Test
	void enumConstantsContainDocumentAndLine() {
		StartTokenRule[] values = StartTokenRule.values();
		
		assertEquals(2, values.length);
		assertSame(StartTokenRule.DOCUMENT, values[0]);
		assertSame(StartTokenRule.LINE, values[1]);
	}
	
	@Test
	void enumValueOfReturnsMatchingConstant() {
		assertSame(StartTokenRule.DOCUMENT, StartTokenRule.valueOf("DOCUMENT"));
		assertSame(StartTokenRule.LINE, StartTokenRule.valueOf("LINE"));
	}
	
	@Test
	void documentMatchWithNullStreamThrowsException() {
		assertThrows(NullPointerException.class, () -> StartTokenRule.DOCUMENT.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void documentMatchWithNullContextThrowsException() {
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertThrows(NullPointerException.class, () -> StartTokenRule.DOCUMENT.match(stream, null));
	}
	
	@Test
	void lineMatchWithNullStreamThrowsException() {
		assertThrows(NullPointerException.class, () -> StartTokenRule.LINE.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void lineMatchWithNullContextThrowsException() {
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertThrows(NullPointerException.class, () -> StartTokenRule.LINE.match(stream, null));
	}
	
	@Test
	void notMatchWithNullStreamThrowsException() {
		TokenRule rule = StartTokenRule.DOCUMENT.not();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void notMatchWithNullContextThrowsException() {
		TokenRule rule = StartTokenRule.DOCUMENT.not();
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void documentMatchesAtIndexZero() {
		TokenStream stream = TokenStream.createMutable(List.of(token("a")));
		
		TokenRuleMatch match = StartTokenRule.DOCUMENT.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertSame(StartTokenRule.DOCUMENT, match.matchingTokenRule());
	}
	
	@Test
	void documentDoesNotMatchAtNonZeroIndex() {
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")), 1);
		
		assertNull(StartTokenRule.DOCUMENT.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void lineMatchesAtIndexZero() {
		TokenStream stream = TokenStream.createMutable(List.of(token("a")));
		
		TokenRuleMatch match = StartTokenRule.LINE.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertSame(StartTokenRule.LINE, match.matchingTokenRule());
	}
	
	@Test
	void lineDoesNotMatchWhenNoMoreTokensAtNonZeroIndex() {
		TokenStream stream = TokenStream.createMutable(List.of(token("a")), 1);
		
		assertNull(StartTokenRule.LINE.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void lineMatchesWhenCurrentLineGreaterThanPreviousLine() {
		TokenStream stream = TokenStream.createMutable(List.of(positionedToken("a", 0, 0, 0), positionedToken("b", 1, 0, 1)), 1);
		
		TokenRuleMatch match = StartTokenRule.LINE.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(1, match.endIndex());
		assertSame(StartTokenRule.LINE, match.matchingTokenRule());
	}
	
	@Test
	void lineDoesNotMatchWhenSameLineNumberPositionedAndNoNewlineInPreviousValue() {
		TokenStream stream = TokenStream.createMutable(List.of(positionedToken("a", 0, 0, 0), positionedToken("b", 0, 1, 1)), 1);
		
		assertNull(StartTokenRule.LINE.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void lineMatchesWhenSameLineNumberPositionedButPreviousValueContainsNewline() {
		TokenStream stream = TokenStream.createMutable(List.of(positionedToken("a\n", 0, 0, 0), positionedToken("b", 0, 1, 2)), 1);
		
		TokenRuleMatch match = StartTokenRule.LINE.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
	}
	
	@Test
	void lineDoesNotMatchWhenCurrentTokenUnpositioned() {
		TokenStream stream = TokenStream.createMutable(List.of(positionedToken("a", 0, 0, 0), token("b")), 1);
		
		assertNull(StartTokenRule.LINE.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void lineMatchesWhenCurrentTokenUnpositionedAndPreviousValueContainsNewline() {
		TokenStream stream = TokenStream.createMutable(List.of(positionedToken("a\n", 0, 0, 0), token("b")), 1);
		
		TokenRuleMatch match = StartTokenRule.LINE.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
	}
	
	@Test
	void lineDoesNotMatchWhenPreviousTokenUnpositioned() {
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), positionedToken("b", 0, 0, 1)), 1);
		
		assertNull(StartTokenRule.LINE.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void lineMatchesWhenPreviousTokenUnpositionedAndValueContainsNewline() {
		TokenStream stream = TokenStream.createMutable(List.of(token("a\n"), positionedToken("b", 0, 0, 2)), 1);
		
		TokenRuleMatch match = StartTokenRule.LINE.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
	}
	
	@Test
	void notMatchReturnsNullWhenUnderlyingRuleMatches() {
		TokenRule rule = StartTokenRule.DOCUMENT.not();
		TokenStream stream = TokenStream.createMutable(List.of(token("a")), 0);
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void notMatchReturnsMatchWhenUnderlyingRuleDoesNotMatch() {
		TokenRule rule = StartTokenRule.DOCUMENT.not();
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")), 1);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(1, match.endIndex());
		assertSame(rule, match.matchingTokenRule());
	}
	
	@Test
	void lineDoesNotMatchWhenLookbehindStreamContainsOnlyShadowTokens() {
		List<Token> tokens = List.of(new ShadowToken(token("shadow")), positionedToken("b", 0, 1, 1));
		TokenStream stream = TokenStream.createMutable(tokens, 1);
		
		assertNull(StartTokenRule.LINE.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void documentDoesNotMatchAtLastIndexOfMultiTokenStream() {
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b"), token("c")), 3);
		
		assertNull(StartTokenRule.DOCUMENT.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void lineMatchesAtTypicalMultiLineBoundary() {
		List<Token> tokens = List.of(
			positionedToken("a", 0, 0, 0),
			positionedToken("b", 0, 1, 1),
			positionedToken("c", 1, 0, 2),
			positionedToken("d", 1, 1, 3),
			positionedToken("e", 2, 0, 4)
		);
		TokenStream stream = TokenStream.createMutable(tokens, 4);
		
		TokenRuleMatch match = StartTokenRule.LINE.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(4, match.startIndex());
	}
	
	@Test
	void notOfLineReturnsAnonymousRuleDistinctFromLine() {
		TokenRule notRule = StartTokenRule.LINE.not();
		
		assertNotNull(notRule);
		assertNotSame(StartTokenRule.LINE, notRule);
	}
	
	@Test
	void notOfNotReturnsOriginalConstantForBothDocumentAndLine() {
		assertSame(StartTokenRule.DOCUMENT, StartTokenRule.DOCUMENT.not().not());
		assertSame(StartTokenRule.LINE, StartTokenRule.LINE.not().not());
	}
	
	@Test
	void lineMatchTraversesMixedPositionedAndUnpositionedTokensAcrossStream() {
		List<Token> tokens = List.of(positionedToken("start", 0, 0, 0), positionedToken("mid", 0, 1, 5), token("tail\n"), token("next"));
		
		assertNotNull(StartTokenRule.LINE.match(TokenStream.createMutable(tokens, 0), TokenRuleContext.empty()));
		assertNull(StartTokenRule.LINE.match(TokenStream.createMutable(tokens, 1), TokenRuleContext.empty()));
		assertNull(StartTokenRule.LINE.match(TokenStream.createMutable(tokens, 2), TokenRuleContext.empty()));
		assertNotNull(StartTokenRule.LINE.match(TokenStream.createMutable(tokens, 3), TokenRuleContext.empty()));
	}
	
	@Test
	void notOfDocumentThenMatchAcrossStreamPositionsBehavesAsInvertedRule() {
		TokenRule negated = StartTokenRule.DOCUMENT.not();
		List<Token> tokens = List.of(token("a"), token("b"), token("c"));
		
		assertNotNull(StartTokenRule.DOCUMENT.match(TokenStream.createMutable(tokens, 0), TokenRuleContext.empty()));
		assertNull(negated.match(TokenStream.createMutable(tokens, 0), TokenRuleContext.empty()));
		
		assertNull(StartTokenRule.DOCUMENT.match(TokenStream.createMutable(tokens, 2), TokenRuleContext.empty()));
		assertNotNull(negated.match(TokenStream.createMutable(tokens, 2), TokenRuleContext.empty()));
	}
}
