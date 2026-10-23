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

package net.luis.utils.io.reader;

import org.junit.jupiter.api.Test;

import java.io.Reader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PositionedStringReader}.<br>
 *
 * @author Luis-St
 */
class PositionedStringReaderTest {
	
	@Test
	void constructWithString() {
		PositionedStringReader reader = new PositionedStringReader("test");
		assertEquals(new StringPosition(0, 1, 1), reader.getPosition());
		assertEquals('\\', reader.getEscapeCharacter());
	}
	
	@Test
	void constructWithStringAndEscapeCharacter() {
		assertEquals('#', new PositionedStringReader("test", '#').getEscapeCharacter());
		assertNull(new PositionedStringReader("test", null).getEscapeCharacter());
	}
	
	@Test
	void constructWithReader() {
		PositionedStringReader reader = new PositionedStringReader(new java.io.StringReader("content"));
		assertEquals("content", reader.getString());
		
		PositionedStringReader escaped = new PositionedStringReader(new java.io.StringReader("content"), '#');
		assertEquals("content", escaped.getString());
		assertEquals('#', escaped.getEscapeCharacter());
	}
	
	@Test
	void constructWithNullArguments() {
		assertThrows(NullPointerException.class, () -> new PositionedStringReader((String) null));
		assertThrows(NullPointerException.class, () -> new PositionedStringReader((String) null, '\\'));
		assertThrows(NullPointerException.class, () -> new PositionedStringReader((Reader) null));
		assertThrows(NullPointerException.class, () -> new PositionedStringReader((Reader) null, '\\'));
	}
	
	@Test
	void getPositionWithNegativeIndex() {
		PositionedStringReader reader = new PositionedStringReader("abc");
		assertThrows(IndexOutOfBoundsException.class, () -> reader.getPosition(-1));
	}
	
	@Test
	void getPositionWithIndexAboveLength() {
		PositionedStringReader reader = new PositionedStringReader("abc");
		assertThrows(IndexOutOfBoundsException.class, () -> reader.getPosition(4));
	}
	
	@Test
	void getPositionAtStart() {
		PositionedStringReader reader = new PositionedStringReader("ab\ncd");
		assertEquals(new StringPosition(0, 1, 1), reader.getPosition(0));
	}
	
	@Test
	void getPositionAtLineStart() {
		PositionedStringReader reader = new PositionedStringReader("ab\ncd");
		assertEquals(new StringPosition(3, 2, 1), reader.getPosition(3));
	}
	
	@Test
	void getPositionInsideLine() {
		PositionedStringReader reader = new PositionedStringReader("ab\ncd");
		assertEquals(new StringPosition(4, 2, 2), reader.getPosition(4));
	}
	
	@Test
	void getPositionOnLineBreak() {
		PositionedStringReader reader = new PositionedStringReader("ab\ncd");
		assertEquals(new StringPosition(2, 1, 3), reader.getPosition(2));
	}
	
	@Test
	void getPositionWithCarriageReturn() {
		PositionedStringReader reader = new PositionedStringReader("ab\rcd");
		assertEquals(new StringPosition(2, 1, 3), reader.getPosition(2));
		assertEquals(new StringPosition(3, 2, 1), reader.getPosition(3));
	}
	
	@Test
	void getPositionWithCarriageReturnLineFeed() {
		PositionedStringReader reader = new PositionedStringReader("ab\r\ncd");
		assertEquals(new StringPosition(2, 1, 3), reader.getPosition(2));
		assertEquals(new StringPosition(3, 1, 4), reader.getPosition(3));
		assertEquals(new StringPosition(4, 2, 1), reader.getPosition(4));
	}
	
	@Test
	void getPositionWithoutLineBreak() {
		PositionedStringReader reader = new PositionedStringReader("abc");
		for (int i = 0; i <= 3; i++) {
			assertEquals(new StringPosition(i, 1, i + 1), reader.getPosition(i));
		}
	}
	
	@Test
	void getPositionAtLength() {
		PositionedStringReader reader = new PositionedStringReader("ab\ncd");
		assertEquals(new StringPosition(5, 2, 3), assertDoesNotThrow(() -> reader.getPosition(5)));
	}
	
	@Test
	void getPositionAfterOvershoot() {
		PositionedStringReader reader = new PositionedStringReader("abc");
		reader.skip(10);
		assertEquals(new StringPosition(3, 1, 4), reader.getPosition());
	}
	
	@Test
	void getPositionWithManyLines() {
		PositionedStringReader reader = new PositionedStringReader("x\n".repeat(40));
		assertEquals(new StringPosition(78, 40, 1), reader.getPosition(78));
		assertEquals(new StringPosition(79, 40, 2), reader.getPosition(79));
		assertEquals(new StringPosition(80, 41, 1), reader.getPosition(80));
	}
	
	@Test
	void getPositionIsCached() {
		PositionedStringReader reader = new PositionedStringReader("ab\ncd");
		assertEquals(new StringPosition(3, 2, 1), reader.getPosition(3));
		assertEquals(new StringPosition(0, 1, 1), reader.getPosition(0));
		assertEquals(new StringPosition(3, 2, 1), reader.getPosition(3));
	}
	
	@Test
	void describeLocationUsesLineAndColumn() {
		PositionedStringReader reader = new PositionedStringReader("ab\ncde");
		reader.skip(5);
		assertEquals("line 2, column 3", reader.describeLocation());
	}
	
	@Test
	void getPositionFollowsReading() {
		PositionedStringReader reader = new PositionedStringReader("ab\ncd");
		reader.readLine(false);
		assertEquals(new StringPosition(3, 2, 1), reader.getPosition());
	}
	
	@Test
	void exceptionMessageContainsPosition() {
		PositionedStringReader reader = new PositionedStringReader("a\nb");
		reader.skip(3);
		StringIndexOutOfBoundsException exception = assertThrows(StringIndexOutOfBoundsException.class, reader::readUnquotedString);
		assertTrue(exception.getMessage().endsWith("at line 2, column 2"));
	}
	
	@Test
	void getPositionOfSubReader() {
		PositionedStringReader reader = new PositionedStringReader("ab\ncd");
		reader.skip(3);
		
		PositionedStringReader subReader = reader.createSubReader(PositionedStringReader::new);
		assertEquals(new StringPosition(3, 2, 1), subReader.getPosition());
		subReader.skip();
		assertEquals(new StringPosition(4, 2, 2), subReader.getPosition());
	}
	
	@Test
	void getPositionWithMixedLineBreaks() {
		PositionedStringReader reader = new PositionedStringReader("a\nb\rc\r\nd");
		assertEquals(new StringPosition(2, 2, 1), reader.getPosition(2));
		assertEquals(new StringPosition(4, 3, 1), reader.getPosition(4));
		assertEquals(new StringPosition(7, 4, 1), reader.getPosition(7));
	}
}
