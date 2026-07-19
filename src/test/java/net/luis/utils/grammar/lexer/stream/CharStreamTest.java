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

package net.luis.utils.grammar.lexer.stream;

import net.luis.utils.grammar.token.TokenPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharStream}.<br>
 *
 * @author Luis-St
 */
class CharStreamTest {
	
	@Test
	void createMutableWithInputOnly() {
		CharStream stream = CharStream.createMutable("abc");
		assertEquals("abc", stream.getInput());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void createMutableWithInputAndIndex() {
		CharStream stream = CharStream.createMutable("abc", 1);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void createImmutableWithInputOnly() {
		CharStream stream = CharStream.createImmutable("abc");
		assertEquals("abc", stream.getInput());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void createImmutableWithInputAndIndex() {
		CharStream stream = CharStream.createImmutable("abc", 2);
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void createMutableWithNullInput() {
		assertThrows(NullPointerException.class, () -> CharStream.createMutable(null));
	}
	
	@Test
	void createMutableWithNullInputAndIndex() {
		assertThrows(NullPointerException.class, () -> CharStream.createMutable(null, 0));
	}
	
	@Test
	void createImmutableWithNullInput() {
		assertThrows(NullPointerException.class, () -> CharStream.createImmutable(null));
	}
	
	@Test
	void createImmutableWithNullInputAndIndex() {
		assertThrows(NullPointerException.class, () -> CharStream.createImmutable(null, 0));
	}
	
	@Test
	void createMutableWithNegativeIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> CharStream.createMutable("abc", -1));
	}
	
	@Test
	void createImmutableWithNegativeIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> CharStream.createImmutable("abc", -1));
	}
	
	@Test
	void subSequenceWithOutOfBoundsIndices() {
		CharStream stream = CharStream.createImmutable("abc");
		assertThrows(IndexOutOfBoundsException.class, () -> stream.subSequence(0, 10));
	}
	
	@Test
	void hasMoreWhenIndexBeforeEnd() {
		CharStream stream = CharStream.createImmutable("abc", 0);
		assertTrue(stream.hasMore());
	}
	
	@Test
	void hasMoreWhenIndexAtEnd() {
		CharStream stream = CharStream.createImmutable("abc", 3);
		assertFalse(stream.hasMore());
	}
	
	@Test
	void currentPositionWithoutIteratingLoopAtIndexZero() {
		CharStream stream = CharStream.createImmutable("abc", 0);
		TokenPosition position = stream.currentPosition();
		assertEquals(0, position.line());
		assertEquals(0, position.characterInLine());
	}
	
	@Test
	void currentPositionWithoutNewlinesInLoop() {
		CharStream stream = CharStream.createImmutable("abcdef", 4);
		TokenPosition position = stream.currentPosition();
		assertEquals(0, position.line());
		assertEquals(4, position.characterInLine());
	}
	
	@Test
	void currentPositionWithNewlineInLoop() {
		CharStream stream = CharStream.createImmutable("ab\ncd", 4);
		TokenPosition position = stream.currentPosition();
		assertEquals(1, position.line());
		assertEquals(1, position.characterInLine());
	}
	
	@Test
	void getLengthReturnsInputLength() {
		CharStream stream = CharStream.createImmutable("hello");
		assertEquals(5, stream.getLength());
	}
	
	@Test
	void peekReturnsCurrentCharacterWithoutAdvancing() {
		CharStream stream = CharStream.createImmutable("abc", 1);
		assertEquals('b', stream.peek());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void advanceByPositiveOffsetMovesForward() {
		CharStream stream = CharStream.createMutable("abcdef", 1);
		stream.advanceBy(2);
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void copyWithOffsetProducesShiftedCopy() {
		CharStream stream = CharStream.createImmutable("abcdef", 1);
		CharStream copy = stream.copyWithOffset(2);
		assertEquals(3, copy.getCurrentIndex());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void subSequenceReturnsExpectedSlice() {
		CharStream stream = CharStream.createImmutable("abcdef");
		assertEquals("bcd", stream.subSequence(1, 4));
	}
	
	@Test
	void emptyConstantIsEmptyImmutableStreamAtIndexZero() {
		CharStream stream = CharStream.EMPTY;
		assertNotNull(stream);
		assertEquals("", stream.getInput());
		assertEquals(0, stream.getLength());
		assertEquals(0, stream.getCurrentIndex());
		assertFalse(stream.hasMore());
	}
	
	@Test
	void advanceByNegativeOffsetMovesBackward() {
		CharStream stream = CharStream.createMutable("abcdef", 4);
		stream.advanceBy(-2);
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void currentPositionWithMultipleNewlinesAccumulatesLineCount() {
		CharStream stream = CharStream.createImmutable("a\nbb\nccc\nd", 9);
		TokenPosition position = stream.currentPosition();
		assertEquals(3, position.line());
		assertEquals(0, position.characterInLine());
	}
	
	@Test
	void currentPositionClampsIndexBeyondInputLength() {
		CharStream stream = CharStream.createImmutable("abc", 3);
		TokenPosition position = assertDoesNotThrow(stream::currentPosition);
		assertEquals(3, position.character());
	}
}
