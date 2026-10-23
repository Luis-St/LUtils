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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryElement}.<br>
 *
 * @author Luis-St
 */
class BinaryElementTest {
	
	private static final BinaryElement FOREIGN_ELEMENT = () -> BinaryType.LIST;
	
	@Test
	void getAsBinaryPrimitiveOnForeignElementThrows() {
		BinaryTypeException exception = assertThrows(BinaryTypeException.class, FOREIGN_ELEMENT::getAsBinaryPrimitive);
		
		assertTrue(exception.getMessage().contains("binary list"));
	}
	
	@Test
	void getAsBinaryStructOnForeignElementThrows() {
		assertThrows(BinaryTypeException.class, FOREIGN_ELEMENT::getAsBinaryStruct);
	}
	
	@Test
	void getAsBinaryMapOnForeignElementThrows() {
		assertThrows(BinaryTypeException.class, FOREIGN_ELEMENT::getAsBinaryMap);
	}
	
	@Test
	void getAsBinaryArrayOnForeignElementThrows() {
		assertThrows(BinaryTypeException.class, FOREIGN_ELEMENT::getAsBinaryArray);
	}
	
	@Test
	void getAsScalarOnForeignElementThrows() {
		assertThrows(BinaryTypeException.class, FOREIGN_ELEMENT::getAsBoolean);
		assertThrows(BinaryTypeException.class, FOREIGN_ELEMENT::getAsNumber);
		assertThrows(BinaryTypeException.class, FOREIGN_ELEMENT::getAsByte);
		assertThrows(BinaryTypeException.class, FOREIGN_ELEMENT::getAsShort);
		assertThrows(BinaryTypeException.class, FOREIGN_ELEMENT::getAsInteger);
		assertThrows(BinaryTypeException.class, FOREIGN_ELEMENT::getAsLong);
		assertThrows(BinaryTypeException.class, FOREIGN_ELEMENT::getAsFloat);
		assertThrows(BinaryTypeException.class, FOREIGN_ELEMENT::getAsDouble);
		assertThrows(BinaryTypeException.class, FOREIGN_ELEMENT::getAsString);
	}
	
	@Test
	void getAsScalarOnContainersThrows() {
		List<BinaryElement> containers = List.of(new BinaryArray(), new BinaryStruct(1), new BinaryMap());
		
		for (BinaryElement container : containers) {
			assertThrows(BinaryTypeException.class, container::getAsString);
			assertThrows(BinaryTypeException.class, container::getAsInteger);
		}
	}
	
	@Test
	void getAsWrongContainerThrows() {
		BinaryArray array = new BinaryArray();
		BinaryMap map = new BinaryMap();
		BinaryStruct struct = new BinaryStruct(1);
		
		assertTrue(assertThrows(BinaryTypeException.class, array::getAsBinaryMap).getMessage().contains("binary list"));
		assertTrue(assertThrows(BinaryTypeException.class, map::getAsBinaryStruct).getMessage().contains("binary map"));
		assertTrue(assertThrows(BinaryTypeException.class, struct::getAsBinaryArray).getMessage().contains("binary struct"));
	}
	
	@Test
	void isBinaryNullTrueAndFalse() {
		assertTrue(BinaryNull.INSTANCE.isBinaryNull());
		assertFalse(BinaryAbsent.INSTANCE.isBinaryNull());
		assertFalse(FOREIGN_ELEMENT.isBinaryNull());
	}
	
	@Test
	void isBinaryAbsentTrueAndFalse() {
		assertTrue(BinaryAbsent.INSTANCE.isBinaryAbsent());
		assertFalse(BinaryNull.INSTANCE.isBinaryAbsent());
	}
	
	@Test
	void isBinaryArrayTrueAndFalse() {
		assertTrue(new BinaryArray().isBinaryArray());
		assertFalse(FOREIGN_ELEMENT.isBinaryArray());
	}
	
	@Test
	void isBinaryStructTrueAndFalse() {
		assertTrue(new BinaryStruct(0).isBinaryStruct());
		assertFalse(new BinaryMap().isBinaryStruct());
	}
	
	@Test
	void isBinaryMapTrueAndFalse() {
		assertTrue(new BinaryMap().isBinaryMap());
		assertFalse(new BinaryStruct(0).isBinaryMap());
	}
	
	@Test
	void isBinaryPrimitiveTrueAndFalse() {
		assertTrue(new BinaryPrimitive(1).isBinaryPrimitive());
		assertFalse(BinaryNull.INSTANCE.isBinaryPrimitive());
	}
	
	@Test
	void isBinaryBooleanShortCircuitsOnNonPrimitive() {
		assertFalse(assertDoesNotThrow(FOREIGN_ELEMENT::isBinaryBoolean));
		assertFalse(assertDoesNotThrow(() -> new BinaryArray().isBinaryBoolean()));
	}
	
	@Test
	void isBinaryBooleanOnPrimitiveTrueAndFalse() {
		assertTrue(new BinaryPrimitive(true).isBinaryBoolean());
		assertFalse(new BinaryPrimitive(1).isBinaryBoolean());
	}
	
	@Test
	void isBinaryNumberShortCircuitsOnNonPrimitive() {
		assertFalse(assertDoesNotThrow(BinaryNull.INSTANCE::isBinaryNumber));
		assertFalse(assertDoesNotThrow(FOREIGN_ELEMENT::isBinaryNumber));
	}
	
	@Test
	void isBinaryNumberOnPrimitiveTrueAndFalse() {
		assertTrue(new BinaryPrimitive(1).isBinaryNumber());
		assertFalse(new BinaryPrimitive("text").isBinaryNumber());
		assertFalse(new BinaryPrimitive(true).isBinaryNumber());
	}
	
	@Test
	void isBinaryByteOnPrimitiveTrueAndFalse() {
		assertTrue(new BinaryPrimitive((byte) 1).isBinaryByte());
		assertFalse(new BinaryPrimitive(1).isBinaryByte());
	}
	
	@Test
	void isBinaryShortOnPrimitiveTrueAndFalse() {
		assertTrue(new BinaryPrimitive((short) 1).isBinaryShort());
		assertFalse(new BinaryPrimitive(1).isBinaryShort());
	}
	
	@Test
	void isBinaryIntegerOnPrimitiveTrueAndFalse() {
		assertTrue(new BinaryPrimitive(1).isBinaryInteger());
		assertFalse(new BinaryPrimitive(1L).isBinaryInteger());
	}
	
	@Test
	void isBinaryLongOnPrimitiveTrueAndFalse() {
		assertTrue(new BinaryPrimitive(1L).isBinaryLong());
		assertFalse(new BinaryPrimitive(1).isBinaryLong());
	}
	
	@Test
	void isBinaryFloatOnPrimitiveTrueAndFalse() {
		assertTrue(new BinaryPrimitive(1.0F).isBinaryFloat());
		assertFalse(new BinaryPrimitive(1.0).isBinaryFloat());
	}
	
	@Test
	void isBinaryDoubleOnPrimitiveTrueAndFalse() {
		assertTrue(new BinaryPrimitive(1.0).isBinaryDouble());
		assertFalse(new BinaryPrimitive(1.0F).isBinaryDouble());
	}
	
	@Test
	void isBinaryStringOnPrimitiveTrueAndFalse() {
		assertTrue(new BinaryPrimitive("text").isBinaryString());
		assertFalse(new BinaryPrimitive(1).isBinaryString());
	}
	
	@Test
	void isBinaryTypeCheckShortCircuitsForAllScalarChecks() {
		BinaryStruct struct = new BinaryStruct(0);
		
		assertFalse(assertDoesNotThrow(struct::isBinaryByte));
		assertFalse(assertDoesNotThrow(struct::isBinaryShort));
		assertFalse(assertDoesNotThrow(struct::isBinaryInteger));
		assertFalse(assertDoesNotThrow(struct::isBinaryLong));
		assertFalse(assertDoesNotThrow(struct::isBinaryFloat));
		assertFalse(assertDoesNotThrow(struct::isBinaryDouble));
		assertFalse(assertDoesNotThrow(struct::isBinaryString));
	}
	
	@Test
	void getAsBinaryPrimitiveReturnsSameInstance() {
		BinaryPrimitive primitive = new BinaryPrimitive(1);
		
		assertSame(primitive, primitive.getAsBinaryPrimitive());
	}
	
	@Test
	void getAsBinaryStructReturnsSameInstance() {
		BinaryStruct struct = new BinaryStruct(1);
		
		assertSame(struct, struct.getAsBinaryStruct());
	}
	
	@Test
	void getAsBinaryMapReturnsSameInstance() {
		BinaryMap map = new BinaryMap();
		
		assertSame(map, map.getAsBinaryMap());
	}
	
	@Test
	void getAsBinaryArrayReturnsSameInstance() {
		BinaryArray array = new BinaryArray();
		
		assertSame(array, array.getAsBinaryArray());
	}
	
	@Test
	void functionalInterfaceUsableAsLambda() {
		BinaryElement element = () -> BinaryType.STRING;
		
		assertEquals(BinaryType.STRING, element.getType());
		assertFalse(element.isBinaryNull());
		assertFalse(element.isBinaryAbsent());
		assertFalse(element.isBinaryArray());
		assertFalse(element.isBinaryStruct());
		assertFalse(element.isBinaryMap());
		assertFalse(element.isBinaryPrimitive());
		assertFalse(element.isBinaryString());
	}
	
	@Test
	void typeChecksAreMutuallyExclusive() {
		List<BinaryElement> elements = List.of(
			BinaryNull.INSTANCE,
			BinaryAbsent.INSTANCE,
			new BinaryPrimitive(1),
			new BinaryArray(),
			new BinaryStruct(0),
			new BinaryMap()
		);
		
		for (BinaryElement element : elements) {
			int matches = 0;
			if (element.isBinaryNull()) matches++;
			if (element.isBinaryAbsent()) matches++;
			if (element.isBinaryPrimitive()) matches++;
			if (element.isBinaryArray()) matches++;
			if (element.isBinaryStruct()) matches++;
			if (element.isBinaryMap()) matches++;
			assertEquals(1, matches, "Expected exactly one structural check for " + element.getType());
		}
		
		int foreignMatches = 0;
		if (FOREIGN_ELEMENT.isBinaryNull()) foreignMatches++;
		if (FOREIGN_ELEMENT.isBinaryAbsent()) foreignMatches++;
		if (FOREIGN_ELEMENT.isBinaryPrimitive()) foreignMatches++;
		if (FOREIGN_ELEMENT.isBinaryArray()) foreignMatches++;
		if (FOREIGN_ELEMENT.isBinaryStruct()) foreignMatches++;
		if (FOREIGN_ELEMENT.isBinaryMap()) foreignMatches++;
		assertEquals(0, foreignMatches);
	}
	
	@Test
	void narrowingConversionChainOnNestedElements() {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, "value");
		BinaryArray array = new BinaryArray(struct);
		BinaryMap map = new BinaryMap();
		map.add("inner", array);
		
		assertEquals("value", map.getAsBinaryMap().getAsBinaryArray("inner").getAsBinaryStruct(0).getAsString(0));
		
		assertThrows(BinaryTypeException.class, () -> map.getAsBinaryArray("inner").getAsBinaryMap(0));
		assertThrows(BinaryTypeException.class, () -> map.getAsBinaryStruct("inner"));
	}
	
	@Test
	void scalarChecksAcrossAllPrimitiveTypes() {
		assertTrue(new BinaryPrimitive(true).isBinaryBoolean());
		assertTrue(new BinaryPrimitive(true).getAsBoolean());
		
		assertTrue(new BinaryPrimitive((byte) 1).isBinaryByte());
		assertEquals((byte) 1, new BinaryPrimitive((byte) 1).getAsByte());
		
		assertTrue(new BinaryPrimitive((short) 2).isBinaryShort());
		assertEquals((short) 2, new BinaryPrimitive((short) 2).getAsShort());
		
		assertTrue(new BinaryPrimitive(3).isBinaryInteger());
		assertEquals(3, new BinaryPrimitive(3).getAsInteger());
		
		assertTrue(new BinaryPrimitive(4L).isBinaryLong());
		assertEquals(4L, new BinaryPrimitive(4L).getAsLong());
		
		assertTrue(new BinaryPrimitive(5.0F).isBinaryFloat());
		assertEquals(5.0F, new BinaryPrimitive(5.0F).getAsFloat());
		
		assertTrue(new BinaryPrimitive(6.0).isBinaryDouble());
		assertEquals(6.0, new BinaryPrimitive(6.0).getAsDouble());
		
		assertTrue(new BinaryPrimitive('c').isBinaryString());
		assertEquals("c", new BinaryPrimitive('c').getAsString());
		
		assertTrue(new BinaryPrimitive("text").isBinaryString());
		assertEquals("text", new BinaryPrimitive("text").getAsString());
	}
}
