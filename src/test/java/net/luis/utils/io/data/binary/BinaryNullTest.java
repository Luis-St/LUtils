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

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryNull}.<br>
 *
 * @author Luis-St
 */
class BinaryNullTest {
	
	@Test
	void constructIsSingleton() {
		assertNotNull(BinaryNull.INSTANCE);
		assertSame(BinaryNull.INSTANCE, BinaryNull.INSTANCE);
		assertTrue(Modifier.isFinal(BinaryNull.class.getModifiers()));
	}
	
	@Test
	void getAsPrimitiveThrows() {
		assertThrows(BinaryTypeException.class, () -> BinaryNull.INSTANCE.getAsBinaryPrimitive());
	}
	
	@Test
	void getAsStructThrows() {
		assertThrows(BinaryTypeException.class, () -> BinaryNull.INSTANCE.getAsBinaryStruct());
	}
	
	@Test
	void getAsMapThrows() {
		assertThrows(BinaryTypeException.class, () -> BinaryNull.INSTANCE.getAsBinaryMap());
	}
	
	@Test
	void getAsArrayThrows() {
		assertThrows(BinaryTypeException.class, () -> BinaryNull.INSTANCE.getAsBinaryArray());
	}
	
	@Test
	void getAsValueThrows() {
		BinaryNull element = BinaryNull.INSTANCE;
		
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsBoolean).getMessage().contains("binary null"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsNumber).getMessage().contains("binary null"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsByte).getMessage().contains("binary null"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsShort).getMessage().contains("binary null"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsInteger).getMessage().contains("binary null"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsLong).getMessage().contains("binary null"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsFloat).getMessage().contains("binary null"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsDouble).getMessage().contains("binary null"));
		assertTrue(assertThrows(BinaryTypeException.class, element::getAsString).getMessage().contains("binary null"));
	}
	
	@Test
	void isBinaryNullReturnsTrue() {
		assertTrue(BinaryNull.INSTANCE.isBinaryNull());
	}
	
	@Test
	void isOtherTypesReturnFalse() {
		BinaryNull element = BinaryNull.INSTANCE;
		
		assertFalse(element.isBinaryAbsent());
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
	void getTypeIsNull() {
		assertEquals(BinaryType.NULL, BinaryNull.INSTANCE.getType());
	}
	
	@Test
	void toStringRepresentation() {
		assertEquals("null", BinaryNull.INSTANCE.toString());
	}
	
	@Test
	void nullIsNotAbsent() {
		assertNotSame(BinaryAbsent.INSTANCE, BinaryNull.INSTANCE);
		assertNotEquals(BinaryAbsent.INSTANCE, BinaryNull.INSTANCE);
		assertNotEquals(BinaryAbsent.INSTANCE.getType(), BinaryNull.INSTANCE.getType());
		assertFalse(BinaryNull.INSTANCE.isBinaryAbsent());
	}
	
	@Test
	void nullInsideContainersKeepsIdentity() {
		BinaryArray array = new BinaryArray();
		array.add((BinaryElement) null);
		assertSame(BinaryNull.INSTANCE, array.get(0));
		
		BinaryMap map = new BinaryMap();
		map.add("k", (BinaryElement) null);
		assertSame(BinaryNull.INSTANCE, map.get("k"));
		
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, (BinaryElement) null);
		assertSame(BinaryNull.INSTANCE, struct.get(0));
	}
}
