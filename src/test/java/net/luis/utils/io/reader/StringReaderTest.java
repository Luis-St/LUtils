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
	}
	
	@Test
	void skipWhitespaces() {
		StringReader reader = new StringReader("  \t\n\rt");
		reader.skipWhitespaces();
		assertEquals(5, reader.getIndex());
		assertEquals('t', reader.peek());
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
		assertThrows(IllegalArgumentException.class, () -> reader.readBoolean(' '));
		assertTrue(reader.readBoolean(' '));
		assertFalse(reader.readBoolean(' '));
		assertThrows(IllegalArgumentException.class, () -> reader.readBoolean(' '));
	}
	
	@Test
	void readByte() {
		StringReader reader = new StringReader("test 1 2");
		assertThrows(IllegalArgumentException.class, () -> reader.readByte(' '));
		assertEquals(1, reader.readByte(' '));
		assertEquals(2, reader.readByte(' '));
		assertThrows(IllegalArgumentException.class, () -> reader.readByte(' '));
	}
	
	@Test
	void readShort() {
		StringReader reader = new StringReader("test 1 2");
		assertThrows(IllegalArgumentException.class, () -> reader.readShort(' '));
		assertEquals(1, reader.readShort(' '));
		assertEquals(2, reader.readShort(' '));
		assertThrows(IllegalArgumentException.class, () -> reader.readShort(' '));
	}
	
	@Test
	void readInt() {
		StringReader reader = new StringReader("test 1 2");
		assertThrows(IllegalArgumentException.class, () -> reader.readInt(' '));
		assertEquals(1, reader.readInt(' '));
		assertEquals(2, reader.readInt(' '));
		assertThrows(IllegalArgumentException.class, () -> reader.readInt(' '));
	}
	
	@Test
	void readLong() {
		StringReader reader = new StringReader("test 1 2");
		assertThrows(IllegalArgumentException.class, () -> reader.readLong(' '));
		assertEquals(1, reader.readLong(' '));
		assertEquals(2, reader.readLong(' '));
		assertThrows(IllegalArgumentException.class, () -> reader.readLong(' '));
	}
	
	@Test
	void readFloat() {
		StringReader reader = new StringReader("test 1.0 0.2");
		assertThrows(IllegalArgumentException.class, () -> reader.readFloat(' '));
		assertEquals(1.0F, reader.readFloat(' '));
		assertEquals(0.2F, reader.readFloat(' '));
		assertThrows(IllegalArgumentException.class, () -> reader.readFloat(' '));
	}
	
	@Test
	void readDouble() {
		StringReader reader = new StringReader("test 1.0 0.2");
		assertThrows(IllegalArgumentException.class, () -> reader.readDouble(' '));
		assertEquals(1.0, reader.readDouble(' '));
		assertEquals(0.2, reader.readDouble(' '));
		assertThrows(IllegalArgumentException.class, () -> reader.readDouble(' '));
	}
}