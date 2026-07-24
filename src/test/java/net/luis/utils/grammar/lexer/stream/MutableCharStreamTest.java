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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MutableCharStream}.<br>
 *
 * @author Luis-St
 */
class MutableCharStreamTest {
	
	@Test
	void constructWithValidInputAndIndex() {
		MutableCharStream stream = new MutableCharStream("abc", 1);
		assertEquals("abc", stream.getInput());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void constructWithNullInput() {
		assertThrows(NullPointerException.class, () -> new MutableCharStream(null, 0));
	}
	
	@Test
	void constructWithNegativeIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> new MutableCharStream("abc", -1));
	}
	
	@Test
	void getCurrentCharAtEndOfStreamThrows() {
		MutableCharStream stream = new MutableCharStream("abc", 3);
		assertThrows(EndOfCharStreamException.class, stream::getCurrentChar);
	}
	
	@Test
	void readCharAtEndOfStreamThrows() {
		MutableCharStream stream = new MutableCharStream("abc", 3);
		assertThrows(EndOfCharStreamException.class, stream::readChar);
	}
	
	@Test
	void constructWithIndexGreaterThanLengthClampsToLength() {
		MutableCharStream stream = new MutableCharStream("abc", 100);
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void constructWithIndexWithinRangeIsUnchanged() {
		MutableCharStream stream = new MutableCharStream("abc", 2);
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void getCurrentCharWhenMoreAvailableReturnsCharWithoutAdvancing() {
		MutableCharStream stream = new MutableCharStream("abc", 1);
		assertEquals('b', stream.getCurrentChar());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void readCharWhenMoreAvailableReturnsCharAndAdvances() {
		MutableCharStream stream = new MutableCharStream("abc", 1);
		assertEquals('b', stream.readChar());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void advanceToNegativeIndexClampsToZero() {
		MutableCharStream stream = new MutableCharStream("abc", 2);
		stream.advanceTo(-5);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void advanceToIndexBeyondLengthClampsToLength() {
		MutableCharStream stream = new MutableCharStream("abc", 0);
		stream.advanceTo(50);
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void advanceToIndexWithinRangeSetsExactIndex() {
		MutableCharStream stream = new MutableCharStream("abc", 0);
		stream.advanceTo(2);
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexBelowZeroClampsToZero() {
		CharStream copy = new MutableCharStream("abc", 1).copyWithIndex(-5);
		assertEquals(0, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexAboveLengthClampsToLength() {
		CharStream copy = new MutableCharStream("abc", 1).copyWithIndex(50);
		assertEquals(3, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexWithinRangeIsUnchanged() {
		CharStream copy = new MutableCharStream("abc", 0).copyWithIndex(2);
		assertEquals(2, copy.getCurrentIndex());
	}
	
	@Test
	void constructWithZeroIndex() {
		MutableCharStream stream = new MutableCharStream("abc", 0);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void constructWithIndexEqualToLength() {
		MutableCharStream stream = new MutableCharStream("abc", 3);
		assertEquals(3, stream.getCurrentIndex());
		assertFalse(stream.hasMore());
	}
	
	@Test
	void constructWithEmptyInput() {
		MutableCharStream stream = new MutableCharStream("", 0);
		assertEquals("", stream.getInput());
		assertFalse(stream.hasMore());
	}
	
	@Test
	void copyWithIndexReturnsMutableInstance() {
		CharStream copy = new MutableCharStream("abc", 0).copyWithIndex(1);
		assertInstanceOf(MutableCharStream.class, copy);
		assertEquals("abc", copy.getInput());
	}
	
	@Test
	void readCharRepeatedlyConsumesEntireStream() {
		MutableCharStream stream = new MutableCharStream("abc", 0);
		assertEquals('a', stream.readChar());
		assertEquals('b', stream.readChar());
		assertEquals('c', stream.readChar());
		assertEquals(3, stream.getCurrentIndex());
		assertThrows(EndOfCharStreamException.class, stream::readChar);
	}
	
	@Test
	void copyWithIndexIsIndependentOfOriginal() {
		MutableCharStream original = new MutableCharStream("abc", 0);
		CharStream copy = original.copyWithIndex(1);
		original.readChar();
		assertEquals(1, original.getCurrentIndex());
		assertEquals(1, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexMutatingCopyDoesNotAffectOriginal() {
		MutableCharStream original = new MutableCharStream("abc", 0);
		CharStream copy = original.copyWithIndex(1);
		copy.readChar();
		assertEquals(2, copy.getCurrentIndex());
		assertEquals(0, original.getCurrentIndex());
	}
	
	@Test
	void advanceByUsesAdvanceToWithCurrentIndexOffset() {
		MutableCharStream stream = new MutableCharStream("abcdef", 3);
		stream.advanceBy(2);
		assertEquals(5, stream.getCurrentIndex());
		stream.advanceBy(-4);
		assertEquals(1, stream.getCurrentIndex());
	}
}
