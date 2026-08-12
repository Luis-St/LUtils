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

package net.luis.utils.io.data.binary;

import net.luis.utils.io.data.binary.exception.BinaryTypeException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryPrimitive}.<br>
 *
 * @author Luis-St
 */
class BinaryPrimitiveTest {
	
	@Test
	void constructWithBoolean() {
		BinaryPrimitive trueValue = new BinaryPrimitive(true);
		assertEquals(BinaryType.BOOLEAN, trueValue.getType());
		assertTrue(trueValue.getAsBoolean());
		
		BinaryPrimitive falseValue = new BinaryPrimitive(false);
		assertEquals(BinaryType.BOOLEAN, falseValue.getType());
		assertFalse(falseValue.getAsBoolean());
	}
	
	@Test
	void constructWithByte() {
		BinaryPrimitive primitive = new BinaryPrimitive((byte) 42);
		
		assertEquals(BinaryType.BYTE, primitive.getType());
		assertEquals((byte) 42, primitive.getAsByte());
	}
	
	@Test
	void constructWithShort() {
		BinaryPrimitive primitive = new BinaryPrimitive((short) 1234);
		
		assertEquals(BinaryType.SHORT, primitive.getType());
		assertEquals((short) 1234, primitive.getAsShort());
	}
	
	@Test
	void constructWithInteger() {
		BinaryPrimitive primitive = new BinaryPrimitive(100000);
		
		assertEquals(BinaryType.INTEGER, primitive.getType());
		assertEquals(100000, primitive.getAsInteger());
	}
	
	@Test
	void constructWithLong() {
		BinaryPrimitive primitive = new BinaryPrimitive(10_000_000_000L);
		
		assertEquals(BinaryType.LONG, primitive.getType());
		assertEquals(10_000_000_000L, primitive.getAsLong());
	}
	
	@Test
	void constructWithFloat() {
		BinaryPrimitive primitive = new BinaryPrimitive(1.5F);
		
		assertEquals(BinaryType.FLOAT, primitive.getType());
		assertEquals(1.5F, primitive.getAsFloat());
	}
	
	@Test
	void constructWithDouble() {
		BinaryPrimitive primitive = new BinaryPrimitive(1.5);
		
		assertEquals(BinaryType.DOUBLE, primitive.getType());
		assertEquals(1.5, primitive.getAsDouble());
	}
	
	@Test
	void constructWithChar() {
		BinaryPrimitive primitive = new BinaryPrimitive('a');
		
		assertEquals(BinaryType.STRING, primitive.getType());
		assertEquals("a", primitive.getAsString());
		assertTrue(primitive.isBinaryString());
	}
	
	@Test
	void constructWithString() {
		BinaryPrimitive primitive = new BinaryPrimitive("text");
		
		assertEquals(BinaryType.STRING, primitive.getType());
		assertEquals("text", primitive.getAsString());
	}
	
	@Test
	void constructWithNumber() {
		BinaryPrimitive primitive = new BinaryPrimitive(Integer.valueOf(7));
		
		assertEquals(BinaryType.INTEGER, primitive.getType());
		assertEquals(7, primitive.getAsInteger());
	}
	
	@Test
	void constructWithNullString() {
		assertThrows(NullPointerException.class, () -> new BinaryPrimitive((String) null));
	}
	
	@Test
	void constructWithNullNumber() {
		assertThrows(NullPointerException.class, () -> new BinaryPrimitive((Number) null));
	}
	
	@Test
	void constructWithUnsupportedNumberType() {
		BinaryTypeException exception = assertThrows(BinaryTypeException.class, () -> new BinaryPrimitive(new BigDecimal("1.5")));
		
		assertTrue(exception.getMessage().contains("BigDecimal"));
	}
	
	@Test
	void constructWithAtomicNumberType() {
		assertThrows(BinaryTypeException.class, () -> new BinaryPrimitive(new AtomicInteger(1)));
	}
	
	@Test
	void getAsBooleanOnNonBooleanThrows() {
		assertTrue(assertThrows(BinaryTypeException.class, () -> new BinaryPrimitive(1).getAsBoolean()).getMessage().contains("binary integer"));
		assertTrue(assertThrows(BinaryTypeException.class, () -> new BinaryPrimitive("text").getAsBoolean()).getMessage().contains("binary string"));
	}
	
	@Test
	void getAsNumberOnNonNumberThrows() {
		assertThrows(BinaryTypeException.class, () -> new BinaryPrimitive("text").getAsNumber());
		assertThrows(BinaryTypeException.class, () -> new BinaryPrimitive(true).getAsNumber());
	}
	
	@Test
	void getAsStringOnNonStringThrows() {
		assertThrows(BinaryTypeException.class, () -> new BinaryPrimitive(1).getAsString());
		assertThrows(BinaryTypeException.class, () -> new BinaryPrimitive(true).getAsString());
	}
	
	@Test
	void getAsNumericValueOnStringThrows() {
		BinaryPrimitive primitive = new BinaryPrimitive("text");
		
		assertThrows(BinaryTypeException.class, primitive::getAsByte);
		assertThrows(BinaryTypeException.class, primitive::getAsShort);
		assertThrows(BinaryTypeException.class, primitive::getAsInteger);
		assertThrows(BinaryTypeException.class, primitive::getAsLong);
		assertThrows(BinaryTypeException.class, primitive::getAsFloat);
		assertThrows(BinaryTypeException.class, primitive::getAsDouble);
	}
	
	@Test
	void getAsNumericValueOnBooleanThrows() {
		BinaryPrimitive primitive = new BinaryPrimitive(true);
		
		assertThrows(BinaryTypeException.class, primitive::getAsByte);
		assertThrows(BinaryTypeException.class, primitive::getAsShort);
		assertThrows(BinaryTypeException.class, primitive::getAsInteger);
		assertThrows(BinaryTypeException.class, primitive::getAsLong);
		assertThrows(BinaryTypeException.class, primitive::getAsFloat);
		assertThrows(BinaryTypeException.class, primitive::getAsDouble);
	}
	
	@Test
	void isBinaryBooleanTrueAndFalse() {
		assertTrue(new BinaryPrimitive(true).isBinaryBoolean());
		assertFalse(new BinaryPrimitive(1).isBinaryBoolean());
		assertFalse(new BinaryPrimitive("text").isBinaryBoolean());
	}
	
	@Test
	void isBinaryNumberTrueAndFalse() {
		assertTrue(new BinaryPrimitive((byte) 1).isBinaryNumber());
		assertTrue(new BinaryPrimitive((short) 1).isBinaryNumber());
		assertTrue(new BinaryPrimitive(1).isBinaryNumber());
		assertTrue(new BinaryPrimitive(1L).isBinaryNumber());
		assertTrue(new BinaryPrimitive(1.0F).isBinaryNumber());
		assertTrue(new BinaryPrimitive(1.0).isBinaryNumber());
		
		assertFalse(new BinaryPrimitive("text").isBinaryNumber());
		assertFalse(new BinaryPrimitive(true).isBinaryNumber());
	}
	
	@Test
	void isBinaryByteTrueAndFalse() {
		assertTrue(new BinaryPrimitive((byte) 1).isBinaryByte());
		assertFalse(new BinaryPrimitive((short) 1).isBinaryByte());
		assertFalse(new BinaryPrimitive(1).isBinaryByte());
	}
	
	@Test
	void isBinaryShortTrueAndFalse() {
		assertTrue(new BinaryPrimitive((short) 1).isBinaryShort());
		assertFalse(new BinaryPrimitive((byte) 1).isBinaryShort());
		assertFalse(new BinaryPrimitive(1).isBinaryShort());
	}
	
	@Test
	void isBinaryIntegerTrueAndFalse() {
		assertTrue(new BinaryPrimitive(1).isBinaryInteger());
		assertFalse(new BinaryPrimitive(1L).isBinaryInteger());
		assertFalse(new BinaryPrimitive((short) 1).isBinaryInteger());
	}
	
	@Test
	void isBinaryLongTrueAndFalse() {
		assertTrue(new BinaryPrimitive(1L).isBinaryLong());
		assertFalse(new BinaryPrimitive(1).isBinaryLong());
	}
	
	@Test
	void isBinaryFloatTrueAndFalse() {
		assertTrue(new BinaryPrimitive(1.0F).isBinaryFloat());
		assertFalse(new BinaryPrimitive(1.0).isBinaryFloat());
	}
	
	@Test
	void isBinaryDoubleTrueAndFalse() {
		assertTrue(new BinaryPrimitive(1.0).isBinaryDouble());
		assertFalse(new BinaryPrimitive(1.0F).isBinaryDouble());
	}
	
	@Test
	void isBinaryStringTrueAndFalse() {
		assertTrue(new BinaryPrimitive("text").isBinaryString());
		assertTrue(new BinaryPrimitive('a').isBinaryString());
		assertFalse(new BinaryPrimitive(1).isBinaryString());
		assertFalse(new BinaryPrimitive(true).isBinaryString());
	}
	
	@Test
	void isBinaryPrimitiveIsAlwaysTrue() {
		assertTrue(new BinaryPrimitive(true).isBinaryPrimitive());
		assertTrue(new BinaryPrimitive((byte) 1).isBinaryPrimitive());
		assertTrue(new BinaryPrimitive((short) 1).isBinaryPrimitive());
		assertTrue(new BinaryPrimitive(1).isBinaryPrimitive());
		assertTrue(new BinaryPrimitive(1L).isBinaryPrimitive());
		assertTrue(new BinaryPrimitive(1.0F).isBinaryPrimitive());
		assertTrue(new BinaryPrimitive(1.0).isBinaryPrimitive());
		assertTrue(new BinaryPrimitive('a').isBinaryPrimitive());
		assertTrue(new BinaryPrimitive("text").isBinaryPrimitive());
	}
	
	@Test
	void constructWithEachSupportedNumberSubclass() {
		assertEquals(BinaryType.BYTE, new BinaryPrimitive(Byte.valueOf((byte) 1)).getType());
		assertEquals(BinaryType.SHORT, new BinaryPrimitive(Short.valueOf((short) 1)).getType());
		assertEquals(BinaryType.INTEGER, new BinaryPrimitive(Integer.valueOf(1)).getType());
		assertEquals(BinaryType.LONG, new BinaryPrimitive(Long.valueOf(1L)).getType());
		assertEquals(BinaryType.FLOAT, new BinaryPrimitive(Float.valueOf(1.0F)).getType());
		assertEquals(BinaryType.DOUBLE, new BinaryPrimitive(Double.valueOf(1.0)).getType());
	}
	
	@Test
	void equalsWithNonPrimitive() {
		BinaryPrimitive primitive = new BinaryPrimitive(1);
		
		assertNotEquals(null, primitive);
		assertNotEquals(primitive, BinaryNull.INSTANCE);
		assertNotEquals(primitive, Integer.valueOf(1));
	}
	
	@Test
	void equalsWithDifferentType() {
		assertNotEquals(new BinaryPrimitive(1), new BinaryPrimitive(1L));
	}
	
	@Test
	void equalsWithSameTypeAndValue() {
		BinaryPrimitive first = new BinaryPrimitive(1);
		BinaryPrimitive second = new BinaryPrimitive(1);
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void equalsWithSameTypeDifferentValue() {
		assertNotEquals(new BinaryPrimitive(1), new BinaryPrimitive(2));
	}
	
	@Test
	void getTypeOfEachPrimitiveShape() {
		assertEquals(BinaryType.BOOLEAN, new BinaryPrimitive(true).getType());
		assertEquals(BinaryType.BYTE, new BinaryPrimitive((byte) 1).getType());
		assertEquals(BinaryType.SHORT, new BinaryPrimitive((short) 1).getType());
		assertEquals(BinaryType.INTEGER, new BinaryPrimitive(1).getType());
		assertEquals(BinaryType.LONG, new BinaryPrimitive(1L).getType());
		assertEquals(BinaryType.FLOAT, new BinaryPrimitive(1.0F).getType());
		assertEquals(BinaryType.DOUBLE, new BinaryPrimitive(1.0).getType());
		assertEquals(BinaryType.STRING, new BinaryPrimitive('a').getType());
		assertEquals(BinaryType.STRING, new BinaryPrimitive("text").getType());
	}
	
	@Test
	void constructWithEmptyString() {
		BinaryPrimitive primitive = new BinaryPrimitive("");
		
		assertEquals(BinaryType.STRING, primitive.getType());
		assertEquals("", primitive.getAsString());
		assertEquals("", primitive.toString());
	}
	
	@Test
	void constructWithBoundaryNumbers() {
		assertEquals(Byte.MIN_VALUE, new BinaryPrimitive(Byte.MIN_VALUE).getAsByte());
		assertEquals(Byte.MAX_VALUE, new BinaryPrimitive(Byte.MAX_VALUE).getAsByte());
		assertEquals(Short.MIN_VALUE, new BinaryPrimitive(Short.MIN_VALUE).getAsShort());
		assertEquals(Short.MAX_VALUE, new BinaryPrimitive(Short.MAX_VALUE).getAsShort());
		assertEquals(Integer.MIN_VALUE, new BinaryPrimitive(Integer.MIN_VALUE).getAsInteger());
		assertEquals(Integer.MAX_VALUE, new BinaryPrimitive(Integer.MAX_VALUE).getAsInteger());
		assertEquals(Long.MIN_VALUE, new BinaryPrimitive(Long.MIN_VALUE).getAsLong());
		assertEquals(Long.MAX_VALUE, new BinaryPrimitive(Long.MAX_VALUE).getAsLong());
	}
	
	@Test
	void constructWithSpecialFloatingPointValues() {
		assertTrue(Float.isNaN(new BinaryPrimitive(Float.NaN).getAsFloat()));
		assertEquals(Float.POSITIVE_INFINITY, new BinaryPrimitive(Float.POSITIVE_INFINITY).getAsFloat());
		assertEquals(Float.NEGATIVE_INFINITY, new BinaryPrimitive(Float.NEGATIVE_INFINITY).getAsFloat());
		
		assertTrue(Double.isNaN(new BinaryPrimitive(Double.NaN).getAsDouble()));
		assertEquals(Double.POSITIVE_INFINITY, new BinaryPrimitive(Double.POSITIVE_INFINITY).getAsDouble());
		assertEquals(Double.NEGATIVE_INFINITY, new BinaryPrimitive(Double.NEGATIVE_INFINITY).getAsDouble());
		assertEquals(Double.doubleToRawLongBits(-0.0), Double.doubleToRawLongBits(new BinaryPrimitive(-0.0).getAsDouble()));
	}
	
	@Test
	void toStringRepresentation() {
		assertEquals("true", new BinaryPrimitive(true).toString());
		assertEquals("42", new BinaryPrimitive((byte) 42).toString());
		assertEquals("1.5", new BinaryPrimitive(1.5).toString());
		assertEquals("text", new BinaryPrimitive("text").toString());
		assertEquals("a", new BinaryPrimitive('a').toString());
		assertEquals("1.5", new BinaryPrimitive(1.5F).toString());
	}
	
	@Test
	void getAsNumberReturnsBoxedValue() {
		assertInstanceOf(Byte.class, new BinaryPrimitive((byte) 1).getAsNumber());
		assertInstanceOf(Short.class, new BinaryPrimitive((short) 1).getAsNumber());
		assertInstanceOf(Integer.class, new BinaryPrimitive(1).getAsNumber());
		assertInstanceOf(Long.class, new BinaryPrimitive(1L).getAsNumber());
		assertInstanceOf(Float.class, new BinaryPrimitive(1.0F).getAsNumber());
		assertInstanceOf(Double.class, new BinaryPrimitive(1.0).getAsNumber());
	}
	
	@Test
	void narrowingConversionsAreLossy() {
		BinaryPrimitive large = new BinaryPrimitive(300);
		assertEquals((byte) 44, large.getAsByte());
		assertEquals(300, large.getAsNumber().intValue());
		
		BinaryPrimitive fractional = new BinaryPrimitive(1.9);
		assertEquals(1, fractional.getAsInteger());
		assertEquals(1.9, fractional.getAsNumber().doubleValue());
	}
	
	@Test
	void wideningConversionsAcrossNumericTypes() {
		BinaryPrimitive byteValue = new BinaryPrimitive((byte) 7);
		assertEquals((short) 7, byteValue.getAsShort());
		assertEquals(7, byteValue.getAsInteger());
		assertEquals(7L, byteValue.getAsLong());
		assertEquals(7.0F, byteValue.getAsFloat());
		assertEquals(7.0, byteValue.getAsDouble());
		
		BinaryPrimitive longValue = new BinaryPrimitive(5L);
		assertEquals(5, longValue.getAsInteger());
	}
	
	@Test
	void charAndStringPrimitivesAreEqual() {
		BinaryPrimitive fromChar = new BinaryPrimitive('a');
		BinaryPrimitive fromString = new BinaryPrimitive("a");
		
		assertEquals(fromChar, fromString);
		assertEquals(fromChar.hashCode(), fromString.hashCode());
	}
	
	@Test
	void equalsAndHashCodeAcrossAllShapes() {
		BinaryPrimitive[] shapes = {
			new BinaryPrimitive(true),
			new BinaryPrimitive((byte) 1),
			new BinaryPrimitive((short) 1),
			new BinaryPrimitive(1),
			new BinaryPrimitive(1L),
			new BinaryPrimitive(1.0F),
			new BinaryPrimitive(1.0),
			new BinaryPrimitive("text")
		};
		BinaryPrimitive[] twins = {
			new BinaryPrimitive(true),
			new BinaryPrimitive((byte) 1),
			new BinaryPrimitive((short) 1),
			new BinaryPrimitive(1),
			new BinaryPrimitive(1L),
			new BinaryPrimitive(1.0F),
			new BinaryPrimitive(1.0),
			new BinaryPrimitive("text")
		};
		
		for (int i = 0; i < shapes.length; i++) {
			assertEquals(shapes[i], shapes[i]);
			assertEquals(shapes[i], twins[i]);
			assertEquals(twins[i], shapes[i]);
			assertEquals(shapes[i].hashCode(), twins[i].hashCode());
			
			for (int j = 0; j < shapes.length; j++) {
				if (i != j) {
					assertNotEquals(shapes[i], shapes[j]);
				}
			}
		}
	}
	
	@Test
	void primitiveRoundTripThroughWriterAndReader() {
		BinaryPrimitive[] shapes = {
			new BinaryPrimitive(true),
			new BinaryPrimitive(false),
			new BinaryPrimitive((byte) 42),
			new BinaryPrimitive((short) 1234),
			new BinaryPrimitive(100000),
			new BinaryPrimitive(10_000_000_000L),
			new BinaryPrimitive(1.5F),
			new BinaryPrimitive(1.5),
			new BinaryPrimitive("text")
		};
		
		for (BinaryPrimitive original : shapes) {
			BinaryElement decoded = BinaryReader.fromByteArray(BinaryWriter.toByteArray(original));
			assertEquals(original, decoded);
		}
		
		BinaryElement decodedChar = BinaryReader.fromByteArray(BinaryWriter.toByteArray(new BinaryPrimitive('a')));
		assertEquals(new BinaryPrimitive("a"), decodedChar);
	}
}
