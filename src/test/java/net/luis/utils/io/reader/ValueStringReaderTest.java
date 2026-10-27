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

import net.luis.utils.exception.InvalidStringException;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ValueStringReader}.<br>
 *
 * @author Luis-St
 */
class ValueStringReaderTest {
	
	@Test
	void constructWithString() {
		ValueStringReader reader = new ValueStringReader("test");
		assertEquals('\\', reader.getEscapeCharacter());
		assertEquals(StringQuoting.DEFAULT, reader.getQuoting());
	}
	
	@Test
	void constructWithConfiguration() {
		ValueStringReader reader = new ValueStringReader("test", '#', StringQuoting.SINGLE_ONLY);
		assertEquals('#', reader.getEscapeCharacter());
		assertEquals(StringQuoting.SINGLE_ONLY, reader.getQuoting());
	}
	
	@Test
	void constructWithReader() {
		assertEquals("content", new ValueStringReader(new java.io.StringReader("content")).getString());
		
		ValueStringReader configured = new ValueStringReader(new java.io.StringReader("content"), null, StringQuoting.NONE);
		assertEquals("content", configured.getString());
		assertNull(configured.getEscapeCharacter());
		assertEquals(StringQuoting.NONE, configured.getQuoting());
	}
	
	@Test
	void constructWithNullArguments() {
		assertThrows(NullPointerException.class, () -> new ValueStringReader((String) null));
		assertThrows(NullPointerException.class, () -> new ValueStringReader((String) null, '\\', StringQuoting.DEFAULT));
		assertThrows(NullPointerException.class, () -> new ValueStringReader("test", '\\', null));
		assertThrows(NullPointerException.class, () -> new ValueStringReader((Reader) null));
		assertThrows(NullPointerException.class, () -> new ValueStringReader((Reader) null, '\\', StringQuoting.DEFAULT));
		assertThrows(NullPointerException.class, () -> new ValueStringReader(new java.io.StringReader("test"), '\\', null));
	}
	
	@Test
	void readBooleanAtEnd() {
		assertThrows(StringIndexOutOfBoundsException.class, () -> new ValueStringReader("").readBoolean());
	}
	
	@Test
	void readBooleanWithInvalidStart() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("yes").readBoolean());
	}
	
	@Test
	void readBooleanWithMismatch() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("tru").readBoolean());
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("fals e").readBoolean());
	}
	
	@Test
	void readNumberAtEnd() {
		assertThrows(StringIndexOutOfBoundsException.class, () -> new ValueStringReader("").readNumber());
	}
	
	@Test
	void readNumberWithoutDigits() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("abc").readNumber());
	}
	
	@Test
	void readNumberFromEmptyQuotes() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("\"\"").readNumber());
	}
	
	@Test
	void readNumberWithLeadingUnderscore() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("_1").readNumber());
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("-_1").readNumber());
	}
	
	@Test
	void readNumberWithMultipleDots() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("1.2.3").readNumber());
	}
	
	@Test
	void readNumberWithDotInBinary() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("0b1.1").readNumber());
	}
	
	@Test
	void readNumberWithInvalidHexPrefix() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("0x0x1").readNumber());
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("1x2").readNumber());
	}
	
	@Test
	void readNumberWithMultipleExponents() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("1e2e3").readNumber());
	}
	
	@Test
	void readNumberWithExponentInWrongRadix() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("0b1e2").readNumber());
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("1p3").readNumber());
	}
	
	@Test
	void readNumberWithMisplacedSign() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("1-2").readNumber());
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("1e2+3").readNumber());
	}
	
	@Test
	void readNumberWithExponentWithoutDigits() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("1e").readNumber());
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("1e+").readNumber());
	}
	
	@Test
	void readNumberWithInvalidOctalDigit() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("019").readNumber());
	}
	
	@Test
	void readNumberWithSuffixOfUnsupportedRadix() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("0b1f").readNumber());
	}
	
	@Test
	void readNumberWithSuffixWhichCannotConvert() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("1.5i").readNumber());
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("300b").readNumber());
	}
	
	@Test
	void readTypedNumberAtEnd() {
		ValueStringReader reader = new ValueStringReader("");
		assertThrows(StringIndexOutOfBoundsException.class, reader::readByte);
		assertThrows(StringIndexOutOfBoundsException.class, reader::readShort);
		assertThrows(StringIndexOutOfBoundsException.class, reader::readInt);
		assertThrows(StringIndexOutOfBoundsException.class, reader::readLong);
		assertThrows(StringIndexOutOfBoundsException.class, reader::readFloat);
		assertThrows(StringIndexOutOfBoundsException.class, reader::readDouble);
		assertThrows(StringIndexOutOfBoundsException.class, reader::readBigInteger);
		assertThrows(StringIndexOutOfBoundsException.class, reader::readBigDecimal);
	}
	
	@Test
	void readTypedNumberWithoutDigits() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("abc").readInt());
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("abc").readDouble());
	}
	
	@Test
	void readTypedNumberOfOtherType() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("10l").readInt());
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("1.5").readInt());
	}
	
	@Test
	void readTypedNumberOutOfRange() {
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("99999999999").readInt());
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("300").readByte());
	}
	
	@Test
	void readTypedNumberMessageArticle() {
		StringIndexOutOfBoundsException integer = assertThrows(StringIndexOutOfBoundsException.class, () -> new ValueStringReader("").readInt());
		assertTrue(integer.getMessage().contains("an integer value"));
		
		StringIndexOutOfBoundsException longValue = assertThrows(StringIndexOutOfBoundsException.class, () -> new ValueStringReader("").readLong());
		assertTrue(longValue.getMessage().contains("a long value"));
	}
	
	@Test
	void readObjectAtEnd() {
		assertThrows(StringIndexOutOfBoundsException.class, () -> new ValueStringReader("").readObject());
	}
	
	@Test
	void readBooleanTrue() {
		assertTrue(new ValueStringReader("true").readBoolean());
		assertTrue(new ValueStringReader("TRUE").readBoolean());
	}
	
	@Test
	void readBooleanFalse() {
		assertFalse(new ValueStringReader("false").readBoolean());
		assertFalse(new ValueStringReader("False").readBoolean());
	}
	
	@Test
	void readNumberQuoted() {
		ValueStringReader reader = new ValueStringReader("\"10\" rest");
		assertEquals(10L, reader.readNumber());
		assertEquals(4, reader.getIndex());
	}
	
	@Test
	void readNumberQuotedWithOtherQuoting() {
		assertEquals(10L, new ValueStringReader("'10'", '\\', StringQuoting.SINGLE_ONLY).readNumber());
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("'10'", '\\', StringQuoting.DOUBLE_ONLY).readNumber());
	}
	
	@Test
	void readNumberWithSign() {
		assertEquals(-5L, new ValueStringReader("-5").readNumber());
		assertEquals(5L, new ValueStringReader("+5").readNumber());
	}
	
	@Test
	void readNumberWithUnderscores() {
		assertEquals(1000L, new ValueStringReader("1_000").readNumber());
	}
	
	@Test
	void readNumberOctal() {
		assertEquals(15L, new ValueStringReader("017").readNumber());
	}
	
	@Test
	void readNumberOctalWithFloatingSuffix() {
		assertEquals(17.0f, new ValueStringReader("017f").readNumber());
	}
	
	@Test
	void readNumberDecimalFloatingPoint() {
		assertEquals(1.5, new ValueStringReader("1.5").readNumber());
	}
	
	@Test
	void readNumberFloatingPointWithLeadingZero() {
		assertEquals(1.5, new ValueStringReader("01.5").readNumber());
	}
	
	@Test
	void readNumberHexadecimal() {
		assertEquals(31L, new ValueStringReader("0x1F").readNumber());
	}
	
	@Test
	void readNumberHexadecimalStopsAtNonHexDigit() {
		ValueStringReader reader = new ValueStringReader("0x1G");
		assertEquals(1L, reader.readNumber());
		assertEquals('G', reader.peek());
	}
	
	@Test
	void readNumberBinary() {
		assertEquals(5L, new ValueStringReader("0b101").readNumber());
	}
	
	@Test
	void readNumberWithByteSuffix() {
		assertEquals((byte) 12, new ValueStringReader("12b").readNumber());
	}
	
	@Test
	void readNumberHexadecimalWithDigitB() {
		assertEquals(11L, new ValueStringReader("0xB").readNumber());
	}
	
	@Test
	void readNumberWithExponent() {
		assertEquals(1000.0, new ValueStringReader("1e3").readNumber());
		assertEquals(1000.0, new ValueStringReader("1e+3").readNumber());
		assertEquals(0.001, new ValueStringReader("1e-3").readNumber());
	}
	
	@Test
	void readNumberOctalWithExponent() {
		assertEquals(100.0, new ValueStringReader("01e2").readNumber());
	}
	
	@Test
	void readNumberHexadecimalFloatingPoint() {
		assertEquals(8.0, new ValueStringReader("0x1p3").readNumber());
	}
	
	@Test
	void readNumberStopsAtOtherCharacter() {
		ValueStringReader reader = new ValueStringReader("12 rest");
		assertEquals(12L, reader.readNumber());
		assertEquals(' ', reader.peek());
	}
	
	@Test
	void readNumberWithSuffix() {
		assertEquals(10L, new ValueStringReader("10l").readNumber());
		assertEquals(1.5f, new ValueStringReader("1.5f").readNumber());
		assertEquals(10, new ValueStringReader("10i").readNumber());
	}
	
	@Test
	void readTypedNumbers() {
		assertEquals((byte) 1, new ValueStringReader("1").readByte());
		assertEquals((short) 2, new ValueStringReader("2").readShort());
		assertEquals(3, new ValueStringReader("3").readInt());
		assertEquals(4L, new ValueStringReader("4").readLong());
		assertEquals(1.5f, new ValueStringReader("1.5").readFloat());
		assertEquals(2.5, new ValueStringReader("2.5").readDouble());
		assertEquals(new BigInteger("123"), new ValueStringReader("123").readBigInteger());
		assertEquals(new BigDecimal("1.5"), new ValueStringReader("1.5").readBigDecimal());
	}
	
	@Test
	void readObjectQuotedCharacter() {
		assertEquals('c', assertInstanceOf(Character.class, new ValueStringReader("'c'").readObject()));
	}
	
	@Test
	void readObjectQuotedString() {
		assertEquals("ab", new ValueStringReader("'ab'").readObject());
		assertEquals("c", assertInstanceOf(String.class, new ValueStringReader("\"c\"").readObject()));
	}
	
	@Test
	void readObjectBoolean() {
		assertEquals(true, new ValueStringReader("true").readObject());
		assertEquals(false, new ValueStringReader("FALSE,").readObject());
	}
	
	@Test
	void readObjectBooleanWithoutBoundary() {
		assertEquals("trueish", new ValueStringReader("trueish").readObject());
	}
	
	@Test
	void readObjectLongAndDouble() {
		assertEquals(10L, new ValueStringReader("10").readObject());
		assertEquals(1.5, new ValueStringReader("1.5").readObject());
		assertEquals(0.5, new ValueStringReader(".5").readObject());
		assertEquals(255L, new ValueStringReader("0xFF").readObject());
	}
	
	@Test
	void readObjectWithExplicitSuffix() {
		assertEquals(10, assertInstanceOf(Integer.class, new ValueStringReader("10i").readObject()));
	}
	
	@Test
	void readObjectLongOverflow() {
		assertEquals(new BigInteger("99999999999999999999"), new ValueStringReader("99999999999999999999").readObject());
	}
	
	@Test
	void readObjectLongOverflowWithSuffix() {
		assertEquals("99999999999999999999l", new ValueStringReader("99999999999999999999l").readObject());
	}
	
	@Test
	void readObjectInfiniteDouble() {
		assertEquals(new BigDecimal("1e400"), new ValueStringReader("1e400").readObject());
	}
	
	@Test
	void readObjectNumberWithoutBoundary() {
		assertEquals("12abc", new ValueStringReader("12abc").readObject());
	}
	
	@Test
	void readObjectInvalidNumber() {
		assertEquals("1.2.3", new ValueStringReader("1.2.3").readObject());
		assertEquals("-", new ValueStringReader("-").readObject());
	}
	
	@Test
	void readObjectUnquotedString() {
		ValueStringReader reader = new ValueStringReader("word rest");
		assertEquals("word", reader.readObject());
		assertEquals('r', reader.peek());
	}
	
	@Test
	void readObjectRestoresIndex() {
		ValueStringReader reader = new ValueStringReader("12abc rest");
		assertEquals("12abc", reader.readObject());
		assertEquals(6, reader.getIndex());
	}
	
	@Test
	void readObjectKeepsMark() {
		ValueStringReader reader = new ValueStringReader("x 12abc");
		reader.skip(2);
		reader.mark();
		assertEquals("12abc", reader.readObject());
		reader.reset();
		assertEquals(2, reader.getIndex());
	}
	
	@Test
	void exceptionMessageContainsPosition() {
		ValueStringReader reader = new ValueStringReader("a\n1.2.3");
		reader.readLine(false);
		InvalidStringException exception = assertThrows(InvalidStringException.class, reader::readNumber);
		assertTrue(exception.getMessage().endsWith("at line 2, column 4"));
	}
	
	@Test
	void readSequenceOfValues() {
		ValueStringReader reader = new ValueStringReader("true 42 'text' 1.5f");
		assertTrue(reader.readBoolean());
		reader.skipWhitespaces();
		assertEquals(42L, reader.readNumber());
		reader.skipWhitespaces();
		assertEquals("text", reader.readString());
		reader.skipWhitespaces();
		assertEquals(1.5f, reader.readNumber());
	}
	
	@Test
	void readNumberBoundaries() {
		assertEquals(Byte.MAX_VALUE, new ValueStringReader("127").readByte());
		assertEquals(Short.MIN_VALUE, new ValueStringReader("-32768").readShort());
		assertEquals(Integer.MAX_VALUE, new ValueStringReader("2147483647").readInt());
		assertEquals(Long.MIN_VALUE, new ValueStringReader("-9223372036854775808").readLong());
		assertThrows(InvalidStringException.class, () -> new ValueStringReader("128").readByte());
	}
}
