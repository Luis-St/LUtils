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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenStream}.<br>
 *
 * @author Luis-St
 */
class TokenStreamTest {
	
	private static final Token tokenA = new SimpleToken("A", TokenPosition.UNPOSITIONED);
	private static final Token tokenB = new SimpleToken("B", TokenPosition.UNPOSITIONED);
	private static final Token tokenC = new SimpleToken("C", TokenPosition.UNPOSITIONED);
	private static final Token tokenD = new SimpleToken("D", TokenPosition.UNPOSITIONED);
	
	@Test
	void createMutableWithTokensOnly() {
		TokenStream stream = TokenStream.createMutable(List.of(tokenA, tokenB));
		
		assertInstanceOf(MutableTokenStream.class, stream);
		assertEquals(List.of(tokenA, tokenB), stream.getAllTokens());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void createMutableWithTokensAndIndex() {
		TokenStream stream = TokenStream.createMutable(List.of(tokenA, tokenB, tokenC), 2);
		
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void createImmutableWithTokensOnly() {
		TokenStream stream = TokenStream.createImmutable(List.of(tokenA, tokenB));
		
		assertInstanceOf(ImmutableTokenStream.class, stream);
		assertEquals(List.of(tokenA, tokenB), stream.getAllTokens());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void createImmutableWithTokensAndIndex() {
		TokenStream stream = TokenStream.createImmutable(List.of(tokenA, tokenB, tokenC), 1);
		
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void createMutableWithNullTokens() {
		assertThrows(NullPointerException.class, () -> TokenStream.createMutable(null));
	}
	
	@Test
	void createMutableWithIndexAndNullTokens() {
		assertThrows(NullPointerException.class, () -> TokenStream.createMutable(null, 0));
	}
	
	@Test
	void createImmutableWithNullTokens() {
		assertThrows(NullPointerException.class, () -> TokenStream.createImmutable(null));
	}
	
	@Test
	void createImmutableWithIndexAndNullTokens() {
		assertThrows(NullPointerException.class, () -> TokenStream.createImmutable(null, 0));
	}
	
	@Test
	void createMutableWithNegativeIndexThrows() {
		assertThrows(IndexOutOfBoundsException.class, () -> TokenStream.createMutable(List.of(tokenA), -1));
	}
	
	@Test
	void createImmutableWithNegativeIndexThrows() {
		assertThrows(IndexOutOfBoundsException.class, () -> TokenStream.createImmutable(List.of(tokenA), -1));
	}
	
	@Test
	void advanceOnEmptyMutableStreamThrows() {
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertThrows(EndOfTokenStreamException.class, stream::advance);
	}
	
	@Test
	void advanceToOtherStreamWithNullThrows() {
		TokenStream stream = TokenStream.createMutable(List.of(tokenA));
		
		assertThrows(NullPointerException.class, () -> stream.advanceTo(null));
	}
	
	@Test
	void isEmptyTrueWhenNoTokens() {
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertTrue(stream.isEmpty());
	}
	
	@Test
	void isEmptyFalseWhenTokensPresent() {
		TokenStream stream = TokenStream.createMutable(List.of(tokenA));
		
		assertFalse(stream.isEmpty());
	}
	
	@Test
	void hasMoreTokensTrueWhenIndexWithinBounds() {
		TokenStream stream = TokenStream.createImmutable(List.of(tokenA, tokenB), 0);
		
		assertTrue(stream.hasMoreTokens());
	}
	
	@Test
	void hasMoreTokensFalseWhenIndexAtSize() {
		TokenStream stream = TokenStream.createImmutable(List.of(tokenA, tokenB), 2);
		
		assertFalse(stream.hasMoreTokens());
	}
	
	@Test
	void moveByPositiveOffsetAdvancesIndex() {
		TokenStream stream = TokenStream.createMutable(List.of(tokenA, tokenB, tokenC), 0);
		stream.moveBy(2);
		
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void moveByNegativeOffsetRewindsIndex() {
		TokenStream stream = TokenStream.createMutable(List.of(tokenA, tokenB, tokenC), 2);
		stream.moveBy(-1);
		
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void moveByOffsetBeyondBoundsClampsToSize() {
		TokenStream stream = TokenStream.createMutable(List.of(tokenA, tokenB), 0);
		stream.moveBy(10);
		
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void moveByZeroOffsetLeavesIndexUnchanged() {
		TokenStream stream = TokenStream.createMutable(List.of(tokenA, tokenB, tokenC), 1);
		stream.moveBy(0);
		
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void sizeReturnsTokenCount() {
		TokenStream stream = TokenStream.createMutable(List.of(tokenA, tokenB, tokenC));
		
		assertEquals(3, stream.size());
	}
	
	@Test
	void resetReturnsIndexToZero() {
		TokenStream stream = TokenStream.createMutable(List.of(tokenA, tokenB, tokenC), 2);
		stream.reset();
		
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void advanceReturnsNewIndexAndReadsToken() {
		TokenStream stream = TokenStream.createMutable(List.of(tokenA, tokenB));
		int newIndex = stream.advance();
		
		assertEquals(1, newIndex);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void advanceToOtherStreamMovesToMatchingIndex() {
		TokenStream mutable = TokenStream.createMutable(List.of(tokenA, tokenB, tokenC), 0);
		TokenStream other = TokenStream.createImmutable(List.of(tokenA, tokenB, tokenC), 2);
		mutable.advanceTo(other);
		
		assertEquals(2, mutable.getCurrentIndex());
	}
	
	@Test
	void copyFromZeroResetsIndexOnCopy() {
		TokenStream original = TokenStream.createMutable(List.of(tokenA, tokenB), 1);
		TokenStream copy = original.copyFromZero();
		
		assertEquals(0, copy.getCurrentIndex());
		assertEquals(1, original.getCurrentIndex());
	}
	
	@Test
	void copyWithOffsetShiftsIndexOnCopy() {
		TokenStream original = TokenStream.createMutable(List.of(tokenA, tokenB, tokenC), 0);
		TokenStream copy = original.copyWithOffset(2);
		
		assertEquals(2, copy.getCurrentIndex());
		assertEquals(0, original.getCurrentIndex());
	}
	
	@Test
	void copyWithOffsetZeroOffsetProducesCopyAtSameIndex() {
		TokenStream original = TokenStream.createMutable(List.of(tokenA, tokenB, tokenC), 1);
		TokenStream copy = original.copyWithOffset(0);
		
		assertEquals(1, copy.getCurrentIndex());
		assertEquals(1, original.getCurrentIndex());
	}
	
	@Test
	void copyWithOffsetNegativeBeyondZeroClampsOnCopy() {
		TokenStream copy = TokenStream.createMutable(List.of(tokenA, tokenB, tokenC), 1).copyWithOffset(-5);
		
		assertEquals(0, copy.getCurrentIndex());
	}
	
	@Test
	void moveByThenAdvanceThenResetSequenceOnSameMutableStream() {
		TokenStream stream = TokenStream.createMutable(List.of(tokenA, tokenB, tokenC, tokenD), 0);
		
		stream.moveBy(1);
		assertEquals(1, stream.getCurrentIndex());
		
		stream.advance();
		assertEquals(2, stream.getCurrentIndex());
		
		stream.reset();
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void createMutableAndCreateImmutableProduceIndependentInstancesFromSameTokenList() {
		List<Token> tokens = List.of(tokenA, tokenB);
		TokenStream mutable = TokenStream.createMutable(tokens, 1);
		TokenStream immutable = TokenStream.createImmutable(tokens, 1);
		mutable.advance();
		
		assertEquals(2, mutable.getCurrentIndex());
		assertEquals(1, immutable.getCurrentIndex());
	}
}
