/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

import net.luis.utils.exception.InvalidStringException;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringReader}.<br>
 *
 * @author Luis-St
 */
class StringReaderTest {
	
	private static final double PRECISION = 0.0001;
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new StringReader((String) null));
		assertThrows(NullPointerException.class, () -> new StringReader((Reader) null));
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
	void readLine() {
		StringReader reader = new StringReader("This is the first line.\nThis is the second line.\rThis is the third line.\r\nThis is the fourth line.");
		assertEquals("This is the first line.", reader.readLine(false));
		assertEquals("This is the second line.", reader.readLine(false));
		assertEquals("This is the third line.", reader.readLine(false));
		assertEquals("This is the fourth line.", reader.readLine(false));
		
		reader.reset();
		
		assertEquals("This is the first line.\n", reader.readLine(true));
		assertEquals("This is the second line.\r", reader.readLine(true));
		assertEquals("This is the third line.\r\n", reader.readLine(true));
		assertEquals("This is the fourth line.", reader.readLine(true));
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
		assertThrows(InvalidStringException.class, reader::readQuotedString);
		reader.skip();
		assertEquals("string", reader.readQuotedString());
		reader.skip();
		assertEquals("for the string reader", reader.readQuotedString());
		assertThrows(StringIndexOutOfBoundsException.class, reader::readQuotedString);
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
	void readUntil() {
		StringReader reader = new StringReader("this is a sImple \\test 'string for the' \"string\" \\reade\\r");
		assertThrows(IllegalArgumentException.class, () -> reader.readUntil('\\'));
		assertEquals("", reader.readUntil('t'));
		assertEquals("his is a sImple tes", reader.readUntil('t'));
		reader.skip();
		assertEquals("'string for the'", reader.readUntil(' '));
		assertEquals("\"string\"", reader.readUntil(' '));
		assertEquals("reader", reader.readUntil('r'));
		assertEquals("", reader.readUntil(' '));
		
		reader.reset();
		
		assertThrows(NullPointerException.class, () -> reader.readUntil((char[]) null));
		assertThrows(IllegalArgumentException.class, reader::readUntil);
		assertThrows(IllegalArgumentException.class, () -> reader.readUntil('\\'));
		assertEquals("", reader.readUntil('t', 's'));
		assertEquals("hi", reader.readUntil('t', 's'));
		reader.skip();
		assertEquals("is a sImpl", reader.readUntil('e', 't'));
		reader.skip();
		assertEquals("te", reader.readUntil('s', ' '));
		reader.skip(2);
		assertEquals("'string for the'", reader.readUntil('s', ' '));
		assertEquals("\"string\"", reader.readUntil(' ', 'x'));
		assertEquals("reader", reader.readUntil('r', '\0'));
		assertEquals("", reader.readUntil(' ', ' '));
		
		reader.reset();
		
		assertThrows(NullPointerException.class, () -> reader.readUntil((String) null, false));
		assertThrows(NullPointerException.class, () -> reader.readUntil((String) null, true));
		assertEquals("", reader.readUntil("t", false));
		assertEquals('h', reader.peek());
		reader.skip(4);
		assertEquals("is ", reader.readUntil("a", false));
		reader.skip();
		assertEquals("", reader.readUntil("sImple", true));
		reader.skip(7);
		assertEquals("'string for the'", reader.readUntil(" ", true));
		reader.skip(9);
		assertEquals("reader", reader.readUntil("r", false));
	}
	
	@Test
	void readUntilInclusive() {
		StringReader reader = new StringReader("this is a sImple \\test 'string for the' \"string\" \\reade\\r");
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilInclusive('\\'));
		assertEquals("t", reader.readUntilInclusive('t'));
		assertEquals("his is a sImple test", reader.readUntilInclusive('t'));
		reader.skip();
		assertEquals("'string for the' ", reader.readUntilInclusive(' '));
		assertEquals("\"string\" ", reader.readUntilInclusive(' '));
		assertEquals("reader", reader.readUntilInclusive('r'));
		assertEquals("", reader.readUntilInclusive(' '));
		
		reader.reset();
		
		assertThrows(NullPointerException.class, () -> reader.readUntilInclusive((char[]) null));
		assertThrows(IllegalArgumentException.class, reader::readUntilInclusive);
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilInclusive('\\'));
		assertEquals("t", reader.readUntilInclusive('t', 's'));
		assertEquals("his", reader.readUntilInclusive('t', 's'));
		reader.skip();
		assertEquals("is a sImple", reader.readUntilInclusive('e', 't'));
		reader.skip();
		assertEquals("tes", reader.readUntilInclusive('s', ' '));
		reader.skip(2);
		assertEquals("'string for the' ", reader.readUntilInclusive('s', ' '));
		assertEquals("\"string\" ", reader.readUntilInclusive(' ', 'x'));
		assertEquals("reader", reader.readUntilInclusive('r', '\0'));
		assertEquals("", reader.readUntilInclusive(' ', ' '));
		
		reader.reset();
		
		assertThrows(NullPointerException.class, () -> reader.readUntilInclusive((String) null, false));
		assertThrows(NullPointerException.class, () -> reader.readUntilInclusive((String) null, true));
		assertEquals("t", reader.readUntilInclusive("t", false));
		assertEquals('h', reader.peek());
		reader.skip(4);
		assertEquals("is a", reader.readUntilInclusive("a", false));
		reader.skip();
		assertEquals("sImple", reader.readUntilInclusive("sImple", true));
		reader.skip(7);
		assertEquals("'string for the' ", reader.readUntilInclusive(" ", true));
		reader.skip(9);
		assertEquals("reader", reader.readUntilInclusive("r", false));
	}
	
	@Test
	void readExpected() {
		StringReader reader = new StringReader("this is a simPle tEsT string for the sTrinG reader");
		assertThrows(InvalidStringException.class, () -> reader.readExpected("test", false));
		assertEquals('h', reader.peek());
		reader.skip(4);
		assertEquals("is", reader.readExpected("is", true));
		reader.skip(3);
		assertEquals("simPle", reader.readExpected("simple", false));
		reader.skip();
		assertThrows(InvalidStringException.class, () -> reader.readExpected("test", true));
		assertEquals('E', reader.peek());
		reader.skip(4);
		
		assertEquals("string", reader.readExpected(List.of("number", "boolean", "string"), false));
		reader.skip();
		assertThrows(InvalidStringException.class, () -> reader.readExpected(List.of("number", "boolean", "string"), false));
		assertEquals('f', reader.peek());
		reader.skip(8);
		assertEquals("sTrinG", reader.readExpected(List.of("nUmbeR", "bOoleaN", "sTrinG"), false));
		reader.skip();
		assertThrows(InvalidStringException.class, () -> reader.readExpected(List.of("number", "boolean", "string"), true));
		assertEquals('r', reader.peek());
		assertEquals("reader", reader.readExpected(List.of("reader", "writer"), true));
	}
	
	@Test
	void readBoolean() {
		StringReader reader = new StringReader("test true false");
		assertThrows(InvalidStringException.class, reader::readBoolean);
		assertEquals('e', reader.read());
		reader.skip(3);
		assertTrue(reader.readBoolean());
		reader.skip();
		assertFalse(reader.readBoolean());
		assertThrows(StringIndexOutOfBoundsException.class, reader::readBoolean);
	}
	
	@Test
	void readNumber() {
		StringReader reader = new StringReader("test 1 3b 2.0 '3.5f' 0x10 0b11 \"1.23e-2\" 0x1.23P4");
		assertThrows(InvalidStringException.class, reader::readNumber);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(1, assertInstanceOf(Long.class, reader.readNumber()));
		reader.skip();
		assertEquals((byte) 3, assertInstanceOf(Byte.class, reader.readNumber()));
		reader.skip();
		assertEquals(2.0, assertInstanceOf(Double.class, reader.readNumber()), PRECISION);
		reader.skip();
		assertEquals(3.5, assertInstanceOf(Float.class, reader.readNumber()), PRECISION);
		reader.skip();
		assertEquals(16, assertInstanceOf(Long.class, reader.readNumber()));
		reader.skip();
		assertEquals(3, assertInstanceOf(Long.class, reader.readNumber()));
		reader.skip();
		assertEquals(0.0123, assertInstanceOf(Double.class, reader.readNumber()), PRECISION);
		reader.skip();
		assertEquals(18.1875, assertInstanceOf(Double.class, reader.readNumber()), PRECISION);
		assertThrows(StringIndexOutOfBoundsException.class, reader::readNumber);
	}
	
	@Test
	void readByte() {
		StringReader reader = new StringReader("test 127 -128b '100' 0x7F 0b01011");
		assertThrows(InvalidStringException.class, reader::readByte);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(127, reader.readByte());
		reader.skip();
		assertEquals(-128, reader.readByte());
		reader.skip();
		assertEquals(100, reader.readByte());
		reader.skip();
		assertEquals(127, reader.readByte());
		reader.skip();
		assertEquals(11, reader.readByte());
		assertThrows(StringIndexOutOfBoundsException.class, reader::readByte);
	}
	
	@Test
	void readShort() {
		StringReader reader = new StringReader("test -15078 9875s '22' 0x7FFF 0b111101");
		assertThrows(InvalidStringException.class, reader::readShort);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(-15078, reader.readShort());
		reader.skip();
		assertEquals(9875, reader.readShort());
		reader.skip();
		assertEquals(22, reader.readShort());
		reader.skip();
		assertEquals(32767, reader.readShort());
		reader.skip();
		assertEquals(61, reader.readShort());
		assertThrows(StringIndexOutOfBoundsException.class, reader::readShort);
	}
	
	@Test
	void readInt() {
		StringReader reader = new StringReader("test 1 2i '5' 0xFAF 0b101");
		assertThrows(InvalidStringException.class, reader::readInt);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(1, reader.readInt());
		reader.skip();
		assertEquals(2, reader.readInt());
		reader.skip();
		assertEquals(5, reader.readInt());
		reader.skip();
		assertEquals(4015, reader.readInt());
		reader.skip();
		assertEquals(5, reader.readInt());
		reader.skip();
		assertThrows(StringIndexOutOfBoundsException.class, reader::readInt);
	}
	
	@Test
	void readLong() {
		StringReader reader = new StringReader("test 1_000 2l '5l' 0xA5 0b110001");
		assertThrows(InvalidStringException.class, reader::readLong);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(1000, reader.readLong());
		reader.skip();
		assertEquals(2, reader.readLong());
		reader.skip();
		assertEquals(5, reader.readLong());
		reader.skip();
		assertEquals(165, reader.readLong());
		reader.skip();
		assertEquals(49, reader.readLong());
		assertThrows(StringIndexOutOfBoundsException.class, reader::readLong);
	}
	
	@Test
	void readFloat() {
		StringReader reader = new StringReader("test -10.46 0.256 '5.0f' 1.23E-2f 0x1.23p-4");
		assertThrows(InvalidStringException.class, reader::readFloat);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(-10.46, reader.readFloat(), PRECISION);
		reader.skip();
		assertEquals(0.256, reader.readFloat(), PRECISION);
		reader.skip();
		assertEquals(5.0, reader.readFloat(), PRECISION);
		reader.skip();
		assertEquals(0.0123, reader.readFloat(), PRECISION);
		reader.skip();
		assertEquals(0.071044921875, reader.readFloat(), PRECISION);
		assertThrows(StringIndexOutOfBoundsException.class, reader::readFloat);
	}
	
	@Test
	void readDouble() {
		StringReader reader = new StringReader("test 1.0 0.2 '0.5d' 1.23E+2 0x1.23p-4");
		assertThrows(InvalidStringException.class, reader::readDouble);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(1.0, reader.readDouble(), PRECISION);
		reader.skip();
		assertEquals(0.2, reader.readDouble(), PRECISION);
		reader.skip();
		assertEquals(0.5, reader.readDouble(), PRECISION);
		reader.skip();
		assertEquals(123.0, reader.readDouble(), PRECISION);
		reader.skip();
		assertEquals(0.071044921875, reader.readDouble(), PRECISION);
		assertThrows(StringIndexOutOfBoundsException.class, reader::readDouble);
	}
	
	@Test
	void readBigInteger() {
		StringReader reader = new StringReader("test 1 500 '10_000_000_000_000_000' 0xFFF_FFF_FFF_FFF_FFF_FFF -10");
		assertThrows(InvalidStringException.class, reader::readBigInteger);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(BigInteger.ONE, reader.readBigInteger());
		reader.skip();
		assertEquals(BigInteger.valueOf(500), reader.readBigInteger());
		reader.skip();
		assertEquals(new BigInteger("10_000_000_000_000_000".replace("_", "")), reader.readBigInteger());
		reader.skip();
		assertEquals(new BigInteger("FFF_FFF_FFF_FFF_FFF_FFF".replace("_", ""), 16), reader.readBigInteger());
		reader.skip();
		assertEquals(BigInteger.valueOf(-10), reader.readBigInteger());
		reader.skip();
		assertThrows(StringIndexOutOfBoundsException.class, reader::readBigInteger);
	}
	
	@Test
	void readBigDecimal() {
		StringReader reader = new StringReader("test 1.0 0.54321 '1.9e5000' 0x1.FFp+4 -5.");
		assertThrows(InvalidStringException.class, reader::readBigDecimal);
		assertEquals('t', reader.read());
		reader.skip(4);
		assertEquals(new BigDecimal("1.0"), reader.readBigDecimal());
		reader.skip();
		assertEquals(new BigDecimal("0.54321"), reader.readBigDecimal());
		reader.skip();
		assertEquals(new BigDecimal("1.9e5000"), reader.readBigDecimal());
		reader.skip();
		assertEquals(new BigDecimal("32"), reader.readBigDecimal());
		reader.skip();
		assertEquals(new BigDecimal("-5"), reader.readBigDecimal());
		reader.skip();
		assertThrows(StringIndexOutOfBoundsException.class, reader::readBigDecimal);
	}
}
