/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringReader}.<br>
 *
 * @author Luis-St
 */
class StringReaderTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new StringReader(null));
	}
	
	@Test
	void getString() {
		StringReader reader = new StringReader("test");
		assertNotNull(reader.getString());
		assertEquals("test", reader.getString());
	}
	
	@Test
	void getIndex() {
		StringReader reader = new StringReader("test");
		assertEquals(0, reader.getIndex());
		reader.read();
		assertEquals(1, reader.getIndex());
	}
	
	@Test
	void read() {
		StringReader reader = new StringReader("test");
		assertEquals('t', reader.read());
		assertEquals('e', reader.read());
		assertEquals('s', reader.read());
		assertEquals('t', reader.read());
		assertThrows(IndexOutOfBoundsException.class, reader::read);
		
		reader.reset();
		
		assertThrows(IllegalArgumentException.class, () -> reader.read(-1));
		assertThrows(IllegalArgumentException.class, () -> reader.read(0));
		assertEquals("t", reader.read(1));
		assertEquals("es", reader.read(2));
		assertEquals("t", reader.read(1));
		assertThrows(IndexOutOfBoundsException.class, () -> reader.read(1));
	}
	
	@Test
	void canRead() {
		StringReader reader = new StringReader("test");
		assertEquals('t', reader.peek());
		assertTrue(reader.canRead());
		assertEquals('t', reader.read());
		assertTrue(reader.canRead());
		assertEquals('e', reader.read());
		assertTrue(reader.canRead());
		assertEquals('s', reader.read());
		assertTrue(reader.canRead());
		assertEquals('t', reader.read());
		assertFalse(reader.canRead());
		assertThrows(IndexOutOfBoundsException.class, reader::read);
		
		reader.reset();
		
		assertThrows(IllegalArgumentException.class, () -> reader.canRead(-1));
		assertThrows(IllegalArgumentException.class, () -> reader.canRead(0));
		assertTrue(reader.canRead(1));
		assertTrue(reader.canRead(2));
		assertTrue(reader.canRead(3));
		assertTrue(reader.canRead(4));
		assertFalse(reader.canRead(5));
		
		reader.reset();
		
		assertThrows(NullPointerException.class, () -> reader.canRead(1, null));
		assertThrows(IllegalArgumentException.class, () -> reader.canRead(-1, c -> true));
		assertThrows(IllegalArgumentException.class, () -> reader.canRead(0, c -> true));
		assertTrue(reader.canRead(2, c -> c == 't' || c == 'e'));
		reader.skip(2);
		assertTrue(reader.canRead(2, c -> c == 's' || c == 't'));
		reader.skip(2);
		assertFalse(reader.canRead());
	}
	
	@Test
	void peek() {
		StringReader reader = new StringReader("test");
		assertEquals('t', reader.peek());
		reader.read();
		assertEquals('e', reader.peek());
		reader.read();
		assertEquals('s', reader.peek());
		reader.read();
		assertEquals('t', reader.peek());
	}
	
	@Test
	void skip() {
		StringReader reader = new StringReader("teet");
		reader.skip();
		assertEquals(1, reader.getIndex());
		assertEquals('e', reader.peek());
		reader.skip();
		assertEquals(2, reader.getIndex());
		assertEquals('e', reader.peek());
		reader.skip();
		assertEquals(3, reader.getIndex());
		assertEquals('t', reader.peek());
		reader.skip();
		assertEquals(4, reader.getIndex());
		assertDoesNotThrow(() -> reader.skip());
		
		reader.reset();
		
		reader.skip('t');
		assertEquals(1, reader.getIndex());
		assertEquals('e', reader.peek());
		reader.skip('e');
		assertEquals(3, reader.getIndex());
		assertEquals('t', reader.peek());
		assertDoesNotThrow(() -> reader.skip('t'));
		
		reader.reset();
		
		reader.skip(c -> c == 't');
		assertEquals(1, reader.getIndex());
		assertEquals('e', reader.peek());
		reader.skip(c -> c == 'e');
		assertEquals(3, reader.getIndex());
		assertEquals('t', reader.peek());
		assertDoesNotThrow(() -> reader.skip(c -> c == 't'));
		
		reader.reset();
		assertThrows(IllegalArgumentException.class, () -> reader.skip(-1));
		assertThrows(IllegalArgumentException.class, () -> reader.skip(0));
		reader.skip(2);
		assertEquals(2, reader.getIndex());
		assertEquals('e', reader.peek());
		reader.skip(2);
		assertEquals(4, reader.getIndex());
		assertDoesNotThrow(() -> reader.skip(1));
	}
	
	@Test
	void skipWhitespaces() {
		StringReader reader = new StringReader("  \t\n\rt");
		reader.skipWhitespaces();
		assertEquals(5, reader.getIndex());
		assertEquals('t', reader.peek());
	}
	
	@Test
	void mark() {
		StringReader reader = new StringReader("test");
		reader.skip(2);
		assertEquals(2, reader.getIndex());
		reader.mark();
		reader.skip();
		assertEquals(3, reader.getIndex());
		reader.reset();
		assertEquals(2, reader.getIndex());
		reader.reset();
		assertEquals(0, reader.getIndex());
	}
	
	@Test
	void reset() {
		StringReader reader = new StringReader("test");
		reader.read();
		reader.read();
		reader.reset();
		assertEquals(0, reader.getIndex());
	}
	
	@Test
	void readRemaining() {
		StringReader reader = new StringReader("test");
		assertEquals("test", reader.readRemaining());
		assertFalse(reader.canRead());
		reader.reset();
		reader.read();
		assertEquals("est", reader.readRemaining());
		assertFalse(reader.canRead());
	}
	
	@Test
	void readUnquotedString() {
		StringReader reader = new StringReader("this is a simple test string for the string reader");
		assertEquals("this", reader.readUnquotedString());
		assertEquals("is", reader.readUnquotedString());
		assertEquals("a", reader.readUnquotedString());
		
		reader.reset();
		
		assertEquals("this", reader.readUnquotedString(' '));
		assertEquals("is a simple ", reader.readUnquotedString('t'));
	}
	
	@Test
	void readQuotedString() {
		StringReader reader = new StringReader("\"this is a simple\" test 'string' \"for the string reader\"");
		assertEquals("this is a simple", reader.readQuotedString());
		reader.skip();
		assertEquals("test", reader.read(4));
		assertThrows(IllegalArgumentException.class, reader::readQuotedString);
		reader.skip();
		assertEquals("string", reader.readQuotedString());
		reader.skip();
		assertEquals("for the string reader", reader.readQuotedString());
		assertEquals("", reader.readQuotedString());
	}
	
	@Test
	void readUntil() {
		StringReader reader = new StringReader("this is a simple \\test 'string for the' \"string\" \\reade\\r");
		assertThrows(IllegalArgumentException.class, () -> reader.readUntil('\\'));
		assertEquals("", reader.readUntil('t'));
		assertEquals("his is a simple tes", reader.readUntil('t'));
		reader.skip();
		assertEquals("'string for the'", reader.readUntil(' '));
		assertEquals("\"string\"", reader.readUntil(' '));
		assertEquals("reader", reader.readUntil('r'));
		assertEquals("", reader.readUntil(' '));
	}
	
	@Test
	void readString() {
		StringReader reader = new StringReader("this \"is a simple\" 'test string'");
		assertEquals("this", reader.readString());
		assertEquals("is a simple", reader.readString());
		reader.skip();
		assertEquals("test string", reader.readString());
	}
	
	@Test
	void readBoolean() {
		StringReader reader = new StringReader("test true false");
		assertThrows(IllegalArgumentException.class, reader::readBoolean);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertTrue(reader.readBoolean());
		reader.skip();
		assertFalse(reader.readBoolean());
		assertThrows(IllegalArgumentException.class, reader::readBoolean);
	}
	
	@Test
	void readByte() {
		StringReader reader = new StringReader("test 127 -128");
		assertThrows(IllegalArgumentException.class, reader::readByte);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(127, reader.readByte());
		reader.skip();
		assertEquals(-128, reader.readByte());
		assertThrows(IllegalArgumentException.class, reader::readByte);
	}
	
	@Test
	void readShort() {
		StringReader reader = new StringReader("test -15078 9875s");
		assertThrows(IllegalArgumentException.class, reader::readShort);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(-15078, reader.readShort());
		reader.skip();
		assertEquals(9875, reader.readShort());
		assertThrows(IllegalArgumentException.class, reader::readShort);
	}
	
	@Test
	void readInt() {
		StringReader reader = new StringReader("test 1 2i");
		assertThrows(IllegalArgumentException.class, reader::readInt);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(1, reader.readInt());
		reader.skip();
		assertEquals(2, reader.readInt());
		assertThrows(IllegalArgumentException.class, reader::readInt);
	}
	
	@Test
	void readLong() {
		StringReader reader = new StringReader("test 1 2l");
		assertThrows(IllegalArgumentException.class, reader::readLong);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(1, reader.readLong());
		reader.skip();
		assertEquals(2, reader.readLong());
		assertThrows(IllegalArgumentException.class, reader::readLong);
	}
	
	@Test
	void readFloat() {
		StringReader reader = new StringReader("test -10.46 0.256");
		assertThrows(IllegalArgumentException.class, reader::readFloat);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(-10.46, reader.readFloat(), 0.01);
		reader.skip();
		assertEquals(0.256, reader.readFloat(), 0.01);
		assertThrows(IllegalArgumentException.class, reader::readFloat);
	}
	
	@Test
	void readDouble() {
		StringReader reader = new StringReader("test 1.0 0.2");
		assertThrows(IllegalArgumentException.class, reader::readDouble);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(1.0, reader.readDouble(), 0.01);
		reader.skip();
		assertEquals(0.2, reader.readDouble(), 0.01);
		assertThrows(IllegalArgumentException.class, reader::readDouble);
	}
}