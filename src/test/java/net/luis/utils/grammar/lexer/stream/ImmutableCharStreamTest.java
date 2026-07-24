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
 * Test class for {@link ImmutableCharStream}.<br>
 *
 * @author Luis-St
 */
class ImmutableCharStreamTest {
	
	@Test
	void constructWithValidInputAndIndex() {
		ImmutableCharStream stream = new ImmutableCharStream("abc", 1);
		assertEquals("abc", stream.getInput());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void constructWithNullInput() {
		assertThrows(NullPointerException.class, () -> new ImmutableCharStream(null, 0));
	}
	
	@Test
	void constructWithNegativeIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> new ImmutableCharStream("abc", -1));
	}
	
	@Test
	void getCurrentCharAtEndOfStreamThrows() {
		ImmutableCharStream stream = new ImmutableCharStream("abc", 3);
		assertThrows(EndOfCharStreamException.class, stream::getCurrentChar);
	}
	
	@Test
	void readCharThrowsUnsupportedOperation() {
		ImmutableCharStream stream = new ImmutableCharStream("abc", 0);
		assertThrows(UnsupportedOperationException.class, stream::readChar);
	}
	
	@Test
	void advanceToThrowsUnsupportedOperation() {
		ImmutableCharStream stream = new ImmutableCharStream("abc", 0);
		assertThrows(UnsupportedOperationException.class, () -> stream.advanceTo(1));
	}
	
	@Test
	void constructWithIndexGreaterThanLengthClampsToLength() {
		ImmutableCharStream stream = new ImmutableCharStream("abc", 100);
		assertEquals(3, stream.getCurrentIndex());
	}
	
	@Test
	void constructWithIndexWithinRangeIsUnchanged() {
		ImmutableCharStream stream = new ImmutableCharStream("abc", 2);
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void getCurrentCharWhenMoreAvailableReturnsChar() {
		ImmutableCharStream stream = new ImmutableCharStream("abc", 1);
		assertEquals('b', stream.getCurrentChar());
	}
	
	@Test
	void copyWithIndexBelowZeroClampsToZero() {
		CharStream copy = new ImmutableCharStream("abc", 1).copyWithIndex(-5);
		assertEquals(0, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexAboveLengthClampsToLength() {
		CharStream copy = new ImmutableCharStream("abc", 1).copyWithIndex(50);
		assertEquals(3, copy.getCurrentIndex());
	}
	
	@Test
	void copyWithIndexWithinRangeIsUnchanged() {
		CharStream copy = new ImmutableCharStream("abc", 0).copyWithIndex(2);
		assertEquals(2, copy.getCurrentIndex());
	}
	
	@Test
	void constructWithZeroIndex() {
		ImmutableCharStream stream = new ImmutableCharStream("abc", 0);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void constructWithIndexEqualToLength() {
		ImmutableCharStream stream = new ImmutableCharStream("abc", 3);
		assertEquals(3, stream.getCurrentIndex());
		assertFalse(stream.hasMore());
	}
	
	@Test
	void constructWithEmptyInput() {
		ImmutableCharStream stream = new ImmutableCharStream("", 0);
		assertEquals("", stream.getInput());
		assertFalse(stream.hasMore());
	}
	
	@Test
	void copyWithIndexReturnsImmutableInstance() {
		CharStream copy = new ImmutableCharStream("abc", 0).copyWithIndex(1);
		assertInstanceOf(ImmutableCharStream.class, copy);
		assertEquals("abc", copy.getInput());
	}
	
	@Test
	void copyWithIndexIsIndependentOfOriginal() {
		ImmutableCharStream original = new ImmutableCharStream("abc", 1);
		CharStream copy = original.copyWithIndex(2);
		
		assertEquals(1, original.getCurrentIndex());
		assertEquals(2, copy.getCurrentIndex());
		assertEquals(original.getInput(), copy.getInput());
	}
	
	@Test
	void currentPositionReflectsMultilineInput() {
		ImmutableCharStream stream = new ImmutableCharStream("ab\ncd\nef", 6);
		TokenPosition position = stream.currentPosition();
		
		assertEquals(2, position.line());
		assertEquals(0, position.characterInLine());
		assertEquals(6, position.character());
	}
}
