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

import net.luis.utils.grammar.token.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ImmutableTokenStream}.<br>
 *
 * @author Luis-St
 */
class ImmutableTokenStreamTest {
	
	private static final Token tokenA = new SimpleToken("A", TokenPosition.UNPOSITIONED);
	private static final Token tokenB = new SimpleToken("B", TokenPosition.UNPOSITIONED);
	private static final Token tokenC = new SimpleToken("C", TokenPosition.UNPOSITIONED);
	private static final Token tokenD = new SimpleToken("D", TokenPosition.UNPOSITIONED);
	private static final Token shadowA = tokenA.shadow();
	private static final Token shadowB = tokenB.shadow();
	private static final Token shadowC = tokenC.shadow();
	
	@Test
	void constructWithValidTokensAndIndex() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA, tokenB, tokenC), 1);
		
		assertEquals(List.of(tokenA, tokenB, tokenC), stream.getAllTokens());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void constructWithNullTokens() {
		assertThrows(NullPointerException.class, () -> new ImmutableTokenStream(null, 0));
	}
	
	@Test
	void constructWithNegativeIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> new ImmutableTokenStream(List.of(tokenA), -1));
	}
	
	@Test
	void getCurrentTokenAtEndOfStreamThrows() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA, tokenB), 2);
		
		assertThrows(EndOfTokenStreamException.class, stream::getCurrentToken);
	}
	
	@Test
	void getCurrentTokenOnEmptyStreamThrows() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(), 0);
		
		assertThrows(EndOfTokenStreamException.class, stream::getCurrentToken);
	}
	
	@Test
	void resetThrowsUnsupportedOperation() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA), 0);
		
		assertThrows(UnsupportedOperationException.class, stream::reset);
	}
	
	@Test
	void advanceToIndexThrowsUnsupportedOperation() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA), 0);
		
		assertThrows(UnsupportedOperationException.class, () -> stream.advanceTo(1));
	}
	
	@Test
	void readTokenThrowsUnsupportedOperation() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA), 0);
		
		assertThrows(UnsupportedOperationException.class, stream::readToken);
	}
	
	@Test
	void moveByThrowsUnsupportedOperation() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA), 0);
		
		assertThrows(UnsupportedOperationException.class, () -> stream.moveBy(1));
	}
	
	@Test
	void advanceThrowsUnsupportedOperation() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA), 0);
		
		assertThrows(UnsupportedOperationException.class, stream::advance);
	}
	
	@Test
	void advanceToOtherStreamThrowsUnsupportedOperation() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA), 0);
		
		assertThrows(UnsupportedOperationException.class, () -> stream.advanceTo(TokenStream.EMPTY));
	}
	
	@Test
	void advanceToOtherStreamWithNullThrows() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA), 0);
		
		assertThrows(NullPointerException.class, () -> stream.advanceTo(null));
	}
	
	@Test
	void constructSkipsSingleLeadingShadowToken() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(shadowA, tokenB, tokenC), 0);
		
		assertEquals(1, stream.getCurrentIndex());
		assertEquals(tokenB, stream.getCurrentToken());
	}
	
	@Test
	void constructSkipsMultipleConsecutiveShadowTokens() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(shadowA, shadowB, tokenC), 0);
		
		assertEquals(2, stream.getCurrentIndex());
		assertEquals(tokenC, stream.getCurrentToken());
	}
	
	@Test
	void constructDoesNotSkipWhenTokenAtIndexIsNotShadow() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA, shadowB, tokenC), 0);
		
		assertEquals(0, stream.getCurrentIndex());
		assertEquals(tokenA, stream.getCurrentToken());
	}
	
	@Test
	void constructDoesNotSkipWhenIndexAtEnd() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA, shadowB), 2);
		
		assertEquals(2, stream.getCurrentIndex());
		assertFalse(stream.hasMoreTokens());
	}
	
	@Test
	void constructSkipsShadowTokensUntilEndOfStream() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA, shadowB, shadowC), 1);
		
		assertEquals(3, stream.getCurrentIndex());
		assertFalse(stream.hasMoreTokens());
	}
	
	@Test
	void getCurrentTokenWhenMoreAvailableReturnsToken() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA, tokenB), 1);
		
		assertEquals(tokenB, stream.getCurrentToken());
	}
	
	@Test
	void copyWithIndexBelowZeroClampsToZero() {
		TokenStream copy = new ImmutableTokenStream(List.of(tokenA, tokenB), 1).copyWithIndex(-5);
		
		assertEquals(0, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexAboveSizeClampsToSize() {
		TokenStream copy = new ImmutableTokenStream(List.of(tokenA, tokenB), 0).copyWithIndex(50);
		
		assertEquals(2, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexWithinRangeIsUnchanged() {
		TokenStream copy = new ImmutableTokenStream(List.of(tokenA, tokenB, tokenC), 0).copyWithIndex(2);
		
		assertEquals(2, copy.getCurrentIndex());
	}
	
	@Test
	void reversedFromStartMapsToLastIndex() {
		TokenStream reversed = new ImmutableTokenStream(List.of(tokenA, tokenB, tokenC), 0).reversed();
		
		assertEquals(2, reversed.getCurrentIndex());
		assertEquals(List.of(tokenC, tokenB, tokenA), reversed.getAllTokens());
	}
	
	@Test
	void reversedFromEndClampsToZero() {
		TokenStream reversed = new ImmutableTokenStream(List.of(tokenA, tokenB, tokenC), 3).reversed();
		
		assertEquals(0, reversed.getCurrentIndex());
	}
	
	@Test
	void reversedFromMiddleIndexMapsExactly() {
		TokenStream reversed = new ImmutableTokenStream(List.of(tokenA, tokenB, tokenC, tokenD), 1).reversed();
		
		assertEquals(2, reversed.getCurrentIndex());
		assertEquals(tokenB, reversed.getCurrentToken());
	}
	
	@Test
	void createLookaheadStreamFromStartContainsAllTokens() {
		TokenStream lookahead = new ImmutableTokenStream(List.of(tokenA, tokenB, tokenC), 0).createLookaheadStream();
		
		assertEquals(List.of(tokenA, tokenB, tokenC), lookahead.getAllTokens());
		assertEquals(0, lookahead.getCurrentIndex());
	}
	
	@Test
	void createLookaheadStreamFromMiddleContainsRemainingTokens() {
		TokenStream lookahead = new ImmutableTokenStream(List.of(tokenA, tokenB, tokenC), 1).createLookaheadStream();
		
		assertEquals(List.of(tokenB, tokenC), lookahead.getAllTokens());
		assertEquals(0, lookahead.getCurrentIndex());
	}
	
	@Test
	void createLookaheadStreamAtEndIsEmpty() {
		TokenStream lookahead = new ImmutableTokenStream(List.of(tokenA, tokenB), 2).createLookaheadStream();
		
		assertTrue(lookahead.isEmpty());
		assertEquals(0, lookahead.getCurrentIndex());
	}
	
	@Test
	void createLookbehindStreamAtStartIsEmpty() {
		TokenStream lookbehind = new ImmutableTokenStream(List.of(tokenA, tokenB), 0).createLookbehindStream();
		
		assertTrue(lookbehind.isEmpty());
		assertEquals(0, lookbehind.getCurrentIndex());
	}
	
	@Test
	void createLookbehindStreamAfterFirstTokenContainsReversedPrefix() {
		TokenStream lookbehind = new ImmutableTokenStream(List.of(tokenA, tokenB, tokenC), 2).createLookbehindStream();
		
		assertEquals(List.of(tokenB, tokenA), lookbehind.getAllTokens());
		assertEquals(0, lookbehind.getCurrentIndex());
	}
	
	@Test
	void getAllTokensReturnsCopiedTokens() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA, tokenB), 0);
		
		assertEquals(List.of(tokenA, tokenB), stream.getAllTokens());
	}
	
	@Test
	void getAllTokensIsUnmodifiable() {
		List<Token> result = new ImmutableTokenStream(List.of(tokenA), 0).getAllTokens();
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(tokenB));
	}
	
	@Test
	void getCurrentIndexReflectsShadowAdjustedPosition() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(shadowA, tokenB), 0);
		
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void isEmptyOnConstructedEmptyStream() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(), 0);
		
		assertTrue(stream.isEmpty());
		assertEquals(0, stream.size());
	}
	
	@Test
	void sizeOnConstructedNonEmptyStream() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA, tokenB, tokenC), 0);
		
		assertEquals(3, stream.size());
		assertFalse(stream.isEmpty());
	}
	
	@Test
	void hasMoreTokensTrueWhenWithinBounds() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA, tokenB), 0);
		
		assertTrue(stream.hasMoreTokens());
	}
	
	@Test
	void hasMoreTokensFalseWhenAtEnd() {
		ImmutableTokenStream stream = new ImmutableTokenStream(List.of(tokenA, tokenB), 2);
		
		assertFalse(stream.hasMoreTokens());
	}
	
	@Test
	void copyWithIndexReturnsImmutableTokenStreamInstance() {
		TokenStream copy = new ImmutableTokenStream(List.of(tokenA, tokenB), 0).copyWithIndex(1);
		
		assertInstanceOf(ImmutableTokenStream.class, copy);
		assertEquals(List.of(tokenA, tokenB), copy.getAllTokens());
	}
	
	@Test
	void copyFromZeroDelegatesToCopyWithIndexZero() {
		TokenStream copy = new ImmutableTokenStream(List.of(tokenA, tokenB), 1).copyFromZero();
		
		assertEquals(0, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithOffsetDelegatesToCopyWithIndex() {
		TokenStream copy = new ImmutableTokenStream(List.of(tokenA, tokenB, tokenC), 1).copyWithOffset(1);
		
		assertEquals(2, copy.getCurrentIndex());
	}
	
	@Test
	void constructorCopiesTokensDefensively() {
		List<Token> list = new ArrayList<>(List.of(tokenA, tokenB));
		ImmutableTokenStream stream = new ImmutableTokenStream(list, 0);
		
		list.add(tokenC);
		
		assertEquals(List.of(tokenA, tokenB), stream.getAllTokens());
		assertEquals(2, stream.getAllTokens().size());
	}
	
	@Test
	void reversedTwiceDoesNotNecessarilyRestoreOriginalIndex() {
		ImmutableTokenStream original = new ImmutableTokenStream(List.of(tokenA, tokenB, tokenC), 3);
		TokenStream onceReversed = original.reversed();
		TokenStream twiceReversed = onceReversed.reversed();
		
		assertEquals(0, onceReversed.getCurrentIndex());
		assertEquals(List.of(tokenA, tokenB, tokenC), twiceReversed.getAllTokens());
		assertEquals(2, twiceReversed.getCurrentIndex());
	}
	
	@Test
	void createLookaheadStreamThenLookbehindStreamComposition() {
		TokenStream base = new ImmutableTokenStream(List.of(tokenA, shadowB, tokenC, tokenD), 1);
		TokenStream lookahead = base.createLookaheadStream();
		TokenStream shifted = lookahead.copyWithIndex(1);
		TokenStream lookbehind = shifted.createLookbehindStream();
		
		assertEquals(List.of(tokenC, tokenD), lookahead.getAllTokens());
		assertEquals(List.of(tokenC), lookbehind.getAllTokens());
		assertEquals(0, lookbehind.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexIntoLeadingShadowTokensSkipsThemAgain() {
		TokenStream copy = new ImmutableTokenStream(List.of(tokenA, shadowB, shadowC, tokenD), 0).copyWithIndex(1);
		
		assertEquals(3, copy.getCurrentIndex());
		assertEquals(tokenD, copy.getCurrentToken());
	}
	
	@Test
	void createLookbehindStreamWithLeadingShadowTokenInPrefix() {
		TokenStream lookbehind = new ImmutableTokenStream(List.of(tokenA, shadowB, tokenC), 2).createLookbehindStream();
		
		assertEquals(List.of(shadowB, tokenA), lookbehind.getAllTokens());
		assertEquals(1, lookbehind.getCurrentIndex());
		assertEquals(tokenA, lookbehind.getCurrentToken());
	}
	
	@Test
	void reversedFromEndWithTrailingShadowTokenSkipsIntoReversedPrefix() {
		TokenStream reversed = new ImmutableTokenStream(List.of(tokenA, tokenB, shadowC), 3).reversed();
		
		assertEquals(List.of(shadowC, tokenB, tokenA), reversed.getAllTokens());
		assertEquals(1, reversed.getCurrentIndex());
		assertEquals(tokenB, reversed.getCurrentToken());
	}
}
