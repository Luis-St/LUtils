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

package net.luis.utils.grammar.parser.stream;

import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MutableTokenStream}.<br>
 *
 * @author Luis-St
 */
class MutableTokenStreamTest {
	
	private static final Token tokenA = SimpleToken.createUnpositioned("A");
	private static final Token tokenB = SimpleToken.createUnpositioned("B");
	private static final Token tokenC = SimpleToken.createUnpositioned("C");
	private static final Token tokenD = SimpleToken.createUnpositioned("D");
	private static final Token shadowA = tokenA.shadow();
	private static final Token shadowB = tokenB.shadow();
	private static final Token shadowC = tokenC.shadow();
	
	@Test
	void constructWithValidTokensAndIndex() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 1);
		
		assertEquals(List.of(tokenA, tokenB, tokenC), stream.getAllTokens());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void constructWithNullTokens() {
		assertThrows(NullPointerException.class, () -> new MutableTokenStream(null, 0));
	}
	
	@Test
	void constructWithNegativeIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> new MutableTokenStream(List.of(tokenA), -1));
	}
	
	@Test
	void getCurrentTokenAtEndOfStreamThrows() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB), 2);
		
		assertThrows(EndOfTokenStreamException.class, stream::getCurrentToken);
	}
	
	@Test
	void getCurrentTokenOnEmptyStreamThrows() {
		MutableTokenStream stream = new MutableTokenStream(List.of(), 0);
		
		assertThrows(EndOfTokenStreamException.class, stream::getCurrentToken);
	}
	
	@Test
	void readTokenAtEndOfStreamThrows() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA), 1);
		
		assertThrows(EndOfTokenStreamException.class, stream::readToken);
	}
	
	@Test
	void advanceToTokenStreamWithNullOtherThrows() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA), 0);
		
		assertThrows(NullPointerException.class, () -> stream.advanceTo(null));
	}
	
	@Test
	void readTokenOnEmptyStreamThrows() {
		MutableTokenStream stream = new MutableTokenStream(List.of(), 0);
		
		assertThrows(EndOfTokenStreamException.class, stream::readToken);
	}
	
	@Test
	void constructSkipsSingleLeadingShadowToken() {
		MutableTokenStream stream = new MutableTokenStream(List.of(shadowA, tokenB, tokenC), 0);
		
		assertEquals(1, stream.getCurrentIndex());
		assertEquals(tokenB, stream.getCurrentToken());
	}
	
	@Test
	void constructSkipsMultipleConsecutiveShadowTokens() {
		MutableTokenStream stream = new MutableTokenStream(List.of(shadowA, shadowB, tokenC), 0);
		
		assertEquals(2, stream.getCurrentIndex());
		assertEquals(tokenC, stream.getCurrentToken());
	}
	
	@Test
	void constructDoesNotSkipWhenTokenAtIndexIsNotShadow() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, shadowB, tokenC), 0);
		
		assertEquals(0, stream.getCurrentIndex());
		assertEquals(tokenA, stream.getCurrentToken());
	}
	
	@Test
	void constructDoesNotSkipWhenIndexAtEnd() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, shadowB), 2);
		
		assertEquals(2, stream.getCurrentIndex());
		assertFalse(stream.hasMoreTokens());
	}
	
	@Test
	void constructSkipsShadowTokensUntilEndOfStream() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, shadowB, shadowC), 1);
		
		assertEquals(3, stream.getCurrentIndex());
		assertFalse(stream.hasMoreTokens());
	}
	
	@Test
	void getCurrentTokenWhenMoreAvailableReturnsToken() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB), 1);
		
		assertEquals(tokenB, stream.getCurrentToken());
	}
	
	@Test
	void getCurrentTokenSkipsShadowTokenReachedAfterConstruction() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, shadowB, tokenC), 0);
		stream.advanceTo(1);
		
		assertEquals(tokenC, stream.getCurrentToken());
	}
	
	@Test
	void hasMoreTokensTrueWhenNonShadowTokenAvailable() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB), 0);
		
		assertTrue(stream.hasMoreTokens());
	}
	
	@Test
	void hasMoreTokensFalseWhenAtEnd() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB), 2);
		
		assertFalse(stream.hasMoreTokens());
	}
	
	@Test
	void hasMoreTokensFalseWhenOnlyShadowTokensRemain() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, shadowB), 1);
		
		assertFalse(stream.hasMoreTokens());
	}
	
	@Test
	void readTokenReadsCurrentTokenAndAdvancesPastIt() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB), 0);
		Token read = stream.readToken();
		
		assertEquals(tokenA, read);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void readTokenSkipsShadowTokensBeforeReading() {
		MutableTokenStream stream = new MutableTokenStream(List.of(shadowA, tokenB, tokenC), 0);
		Token read = stream.readToken();
		
		assertEquals(tokenB, read);
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void advanceToBelowZeroClampsToZero() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB), 1);
		stream.advanceTo(-5);
		
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void advanceToAboveSizeClampsToSize() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB), 0);
		stream.advanceTo(50);
		
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void advanceToWithinRangeIsUnchanged() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 0);
		stream.advanceTo(2);
		
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void advanceToShadowTokenIndexSkipsForward() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, shadowB, tokenC), 0);
		stream.advanceTo(1);
		
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexBelowZeroClampsToZero() {
		TokenStream copy = new MutableTokenStream(List.of(tokenA, tokenB), 1).copyWithIndex(-5);
		
		assertEquals(0, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexAboveSizeClampsToSize() {
		TokenStream copy = new MutableTokenStream(List.of(tokenA, tokenB), 0).copyWithIndex(50);
		
		assertEquals(2, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexWithinRangeIsUnchanged() {
		TokenStream copy = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 0).copyWithIndex(2);
		
		assertEquals(2, copy.getCurrentIndex());
	}
	
	@Test
	void reversedFromStartMapsToLastIndex() {
		TokenStream reversed = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 0).reversed();
		
		assertEquals(2, reversed.getCurrentIndex());
		assertEquals(List.of(tokenC, tokenB, tokenA), reversed.getAllTokens());
	}
	
	@Test
	void reversedFromEndClampsToZero() {
		TokenStream reversed = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 3).reversed();
		
		assertEquals(0, reversed.getCurrentIndex());
	}
	
	@Test
	void reversedFromMiddleIndexMapsExactly() {
		TokenStream reversed = new MutableTokenStream(List.of(tokenA, tokenB, tokenC, tokenD), 1).reversed();
		
		assertEquals(2, reversed.getCurrentIndex());
		assertEquals(tokenB, reversed.getCurrentToken());
	}
	
	@Test
	void createLookaheadStreamFromStartContainsAllTokens() {
		TokenStream lookahead = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 0).createLookaheadStream();
		
		assertEquals(List.of(tokenA, tokenB, tokenC), lookahead.getAllTokens());
		assertEquals(0, lookahead.getCurrentIndex());
	}
	
	@Test
	void createLookaheadStreamAtEndIsEmpty() {
		TokenStream lookahead = new MutableTokenStream(List.of(tokenA, tokenB), 2).createLookaheadStream();
		
		assertTrue(lookahead.isEmpty());
		assertEquals(0, lookahead.getCurrentIndex());
	}
	
	@Test
	void createLookbehindStreamAtStartIsEmpty() {
		TokenStream lookbehind = new MutableTokenStream(List.of(tokenA, tokenB), 0).createLookbehindStream();
		
		assertTrue(lookbehind.isEmpty());
		assertEquals(0, lookbehind.getCurrentIndex());
	}
	
	@Test
	void createLookbehindStreamAfterFirstTokenContainsReversedPrefix() {
		TokenStream lookbehind = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 2).createLookbehindStream();
		
		assertEquals(List.of(tokenB, tokenA), lookbehind.getAllTokens());
		assertEquals(0, lookbehind.getCurrentIndex());
	}
	
	@Test
	void getAllTokensReturnsCopiedTokens() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB), 0);
		
		assertEquals(List.of(tokenA, tokenB), stream.getAllTokens());
	}
	
	@Test
	void getAllTokensIsUnmodifiable() {
		List<Token> result = new MutableTokenStream(List.of(tokenA), 0).getAllTokens();
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(tokenB));
	}
	
	@Test
	void isEmptyOnConstructedEmptyStream() {
		MutableTokenStream stream = new MutableTokenStream(List.of(), 0);
		
		assertTrue(stream.isEmpty());
		assertEquals(0, stream.size());
	}
	
	@Test
	void sizeOnConstructedNonEmptyStream() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 0);
		
		assertEquals(3, stream.size());
		assertFalse(stream.isEmpty());
	}
	
	@Test
	void moveByDelegatesToAdvanceTo() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 0);
		stream.moveBy(2);
		
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void resetDelegatesToAdvanceToZero() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB), 1);
		stream.reset();
		
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void advanceDelegatesToReadTokenThenGetCurrentIndex() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB), 0);
		int newIndex = stream.advance();
		
		assertEquals(1, newIndex);
	}
	
	@Test
	void advanceToTokenStreamDelegatesToAdvanceToIndex() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 0);
		TokenStream other = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 2);
		stream.advanceTo(other);
		
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexReturnsMutableTokenStreamInstance() {
		TokenStream copy = new MutableTokenStream(List.of(tokenA, tokenB), 0).copyWithIndex(1);
		
		assertInstanceOf(MutableTokenStream.class, copy);
		assertEquals(List.of(tokenA, tokenB), copy.getAllTokens());
	}
	
	@Test
	void copyFromZeroDelegatesToCopyWithIndexZero() {
		TokenStream copy = new MutableTokenStream(List.of(tokenA, tokenB), 1).copyFromZero();
		
		assertEquals(0, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithOffsetDelegatesToCopyWithIndex() {
		TokenStream copy = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 1).copyWithOffset(1);
		
		assertEquals(2, copy.getCurrentIndex());
	}
	
	@Test
	void constructorCopiesTokensDefensively() {
		List<Token> list = new ArrayList<>(List.of(tokenA, tokenB));
		MutableTokenStream stream = new MutableTokenStream(list, 0);
		
		list.add(tokenC);
		
		assertEquals(List.of(tokenA, tokenB), stream.getAllTokens());
		assertEquals(2, stream.getAllTokens().size());
	}
	
	@Test
	void readTokenRepeatedlyDrainsStreamThenThrows() {
		MutableTokenStream stream = new MutableTokenStream(List.of(tokenA, tokenB), 0);
		
		assertEquals(tokenA, stream.readToken());
		assertEquals(tokenB, stream.readToken());
		assertThrows(EndOfTokenStreamException.class, stream::readToken);
	}
	
	@Test
	void reversedTwiceDoesNotNecessarilyRestoreOriginalIndex() {
		MutableTokenStream original = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 3);
		TokenStream onceReversed = original.reversed();
		TokenStream twiceReversed = onceReversed.reversed();
		
		assertEquals(0, onceReversed.getCurrentIndex());
		assertEquals(List.of(tokenA, tokenB, tokenC), twiceReversed.getAllTokens());
		assertEquals(2, twiceReversed.getCurrentIndex());
	}
	
	@Test
	void createLookaheadStreamThenAdvanceToDoesNotAffectOriginalStream() {
		MutableTokenStream base = new MutableTokenStream(List.of(tokenA, tokenB, tokenC), 1);
		TokenStream lookahead = base.createLookaheadStream();
		lookahead.advanceTo(2);
		
		assertEquals(2, lookahead.getCurrentIndex());
		assertEquals(1, base.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexIntoLeadingShadowTokensSkipsThemAgain() {
		TokenStream copy = new MutableTokenStream(List.of(tokenA, shadowB, shadowC, tokenD), 0).copyWithIndex(1);
		
		assertEquals(3, copy.getCurrentIndex());
		assertEquals(tokenD, copy.getCurrentToken());
	}
}
