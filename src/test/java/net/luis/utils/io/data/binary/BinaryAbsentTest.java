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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryAbsent}.<br>
 *
 * @author Luis-St
 */
class BinaryAbsentTest {
	
	@Test
	void constructIsSingleton() {
		assertNotNull(BinaryAbsent.INSTANCE);
		assertSame(BinaryAbsent.INSTANCE, BinaryAbsent.INSTANCE);
	}
	
	@Test
	void getAsPrimitiveThrows() {
		assertThrows(BinaryTypeException.class, () -> BinaryAbsent.INSTANCE.getAsBinaryPrimitive());
	}
	
	@Test
	void getAsStructThrows() {
		assertThrows(BinaryTypeException.class, () -> BinaryAbsent.INSTANCE.getAsBinaryStruct());
	}
	
	@Test
	void getAsMapThrows() {
		assertThrows(BinaryTypeException.class, () -> BinaryAbsent.INSTANCE.getAsBinaryMap());
	}
	
	@Test
	void getAsArrayThrows() {
		assertThrows(BinaryTypeException.class, () -> BinaryAbsent.INSTANCE.getAsBinaryArray());
	}
	
	@Test
	void getAsValueThrows() {
		BinaryAbsent element = BinaryAbsent.INSTANCE;
		
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsBoolean).getMessage().contains("binary absent"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsNumber).getMessage().contains("binary absent"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsByte).getMessage().contains("binary absent"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsShort).getMessage().contains("binary absent"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsInteger).getMessage().contains("binary absent"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsLong).getMessage().contains("binary absent"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsFloat).getMessage().contains("binary absent"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsDouble).getMessage().contains("binary absent"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsString).getMessage().contains("binary absent"));
	}
	
	@Test
	void writeAbsentAsRootThrows() {
		BinaryTypeException exception = assertThrows(BinaryTypeException.class, () -> BinaryWriter.toByteArray(BinaryAbsent.INSTANCE));
		
		assertTrue(exception.getMessage().contains("only be written as a field of a struct"));
	}
	
	@Test
	void isBinaryAbsentReturnsTrue() {
		assertTrue(BinaryAbsent.INSTANCE.isBinaryAbsent());
	}
	
	@Test
	void isOtherTypesReturnFalse() {
		BinaryAbsent element = BinaryAbsent.INSTANCE;
		
		assertFalse(element.isBinaryNull());
		assertFalse(element.isBinaryArray());
		assertFalse(element.isBinaryStruct());
		assertFalse(element.isBinaryMap());
		assertFalse(element.isBinaryPrimitive());
		assertFalse(element.isBinaryBoolean());
		assertFalse(element.isBinaryNumber());
		assertFalse(element.isBinaryByte());
		assertFalse(element.isBinaryShort());
		assertFalse(element.isBinaryInteger());
		assertFalse(element.isBinaryLong());
		assertFalse(element.isBinaryFloat());
		assertFalse(element.isBinaryDouble());
		assertFalse(element.isBinaryString());
	}
	
	@Test
	void getTypeIsAbsent() {
		assertEquals(BinaryType.ABSENT, BinaryAbsent.INSTANCE.getType());
	}
	
	@Test
	void toStringRepresentation() {
		assertEquals("absent", BinaryAbsent.INSTANCE.toString());
	}
	
	@Test
	void absentIsNotNull() {
		assertNotSame(BinaryNull.INSTANCE, BinaryAbsent.INSTANCE);
		assertNotEquals(BinaryNull.INSTANCE.getType(), BinaryAbsent.INSTANCE.getType());
		assertFalse(BinaryAbsent.INSTANCE.isBinaryNull());
	}
	
	@Test
	void absentIsDefaultOfStructFields() {
		BinaryStruct struct = new BinaryStruct(2);
		
		assertSame(BinaryAbsent.INSTANCE, struct.get(0));
		assertSame(BinaryAbsent.INSTANCE, struct.get(1));
		assertEquals(0, struct.presentFields());
		
		struct.set(0, 1);
		assertEquals(1, struct.presentFields());
		
		struct.remove(0);
		assertSame(BinaryAbsent.INSTANCE, struct.get(0));
		assertEquals(0, struct.presentFields());
	}
	
	@Test
	void absentSurvivesRoundTripInsideStruct() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(1, 1);
		
		BinaryElement decoded = BinaryReader.fromByteArray(BinaryWriter.toByteArray(struct));
		
		BinaryStruct decodedStruct = decoded.getAsBinaryStruct();
		assertFalse(decodedStruct.has(0));
		assertTrue(decodedStruct.has(1));
		assertEquals(BinaryType.ABSENT, decodedStruct.get(0).getType());
	}
}
