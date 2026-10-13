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

import net.luis.utils.io.data.binary.exception.BinaryIndexOutOfBoundsException;
import net.luis.utils.io.data.binary.exception.BinaryTypeException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryArray}.<br>
 *
 * @author Luis-St
 */
class BinaryArrayTest {
	
	@Test
	void constructEmptyArray() {
		BinaryArray array = new BinaryArray();
		
		assertEquals(0, array.size());
		assertTrue(array.isEmpty());
		assertEquals(BinaryType.LIST, array.getType());
		assertEquals("[]", array.toString());
	}
	
	@Test
	void constructWithElementList() {
		BinaryArray array = new BinaryArray(List.of(new BinaryPrimitive(1), new BinaryPrimitive("a")));
		
		assertEquals(2, array.size());
		assertEquals(1, array.getAsInteger(0));
		assertEquals("a", array.getAsString(1));
	}
	
	@Test
	void constructWithElementVarargs() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(true), BinaryNull.INSTANCE);
		
		assertEquals(2, array.size());
		assertTrue(array.getAsBoolean(0));
		assertTrue(array.get(1).isBinaryNull());
	}
	
	@Test
	void constructWithEmptyList() {
		assertTrue(new BinaryArray(List.of()).isEmpty());
	}
	
	@Test
	void constructWithEmptyVarargs() {
		assertTrue(new BinaryArray(new BinaryElement[0]).isEmpty());
	}
	
	@Test
	void constructWithNullList() {
		assertThrows(NullPointerException.class, () -> new BinaryArray((List<BinaryElement>) null));
	}
	
	@Test
	void constructWithNullVarargs() {
		assertThrows(NullPointerException.class, () -> new BinaryArray((BinaryElement[]) null));
	}
	
	@Test
	void getWithNegativeIndex() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1));
		
		BinaryIndexOutOfBoundsException exception = assertThrows(BinaryIndexOutOfBoundsException.class, () -> array.get(-1));
		assertEquals("Binary index out of bounds: -1", exception.getMessage());
	}
	
	@Test
	void getWithIndexEqualToSize() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1), new BinaryPrimitive(2));
		
		BinaryIndexOutOfBoundsException exception = assertThrows(BinaryIndexOutOfBoundsException.class, () -> array.get(2));
		assertTrue(exception.getMessage().contains("of size 2"));
	}
	
	@Test
	void getOnEmptyArray() {
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> new BinaryArray().get(0));
	}
	
	@Test
	void setWithInvalidIndex() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1));
		
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> array.set(-1, 1));
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> array.set(1, 1));
	}
	
	@Test
	void removeWithInvalidIndex() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1));
		
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> array.remove(-1));
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> array.remove(1));
	}
	
	@Test
	void getAsTypedWithInvalidIndex() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1));
		
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> array.getAsString(5));
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> array.getAsBinaryArray(5));
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> array.getAsInteger(-1));
	}
	
	@Test
	void getAsWrongTypeThrows() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1));
		
		assertThrows(BinaryTypeException.class, () -> array.getAsString(0));
		assertThrows(BinaryTypeException.class, () -> array.getAsBoolean(0));
		assertThrows(BinaryTypeException.class, () -> array.getAsBinaryArray(0));
		assertThrows(BinaryTypeException.class, () -> array.getAsBinaryStruct(0));
		assertThrows(BinaryTypeException.class, () -> array.getAsBinaryMap(0));
	}
	
	@Test
	void getAsPrimitiveOnContainerThrows() {
		BinaryArray array = new BinaryArray(new BinaryArray());
		
		assertThrows(BinaryTypeException.class, () -> array.getAsBinaryPrimitive(0));
	}
	
	@Test
	void addUnsupportedNumberThrows() {
		BinaryArray array = new BinaryArray();
		
		assertThrows(BinaryTypeException.class, () -> array.add(new BigDecimal("1")));
		assertTrue(array.isEmpty());
	}
	
	@Test
	void setUnsupportedNumberThrows() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1));
		
		assertThrows(BinaryTypeException.class, () -> array.set(0, new BigDecimal("1")));
		assertEquals(1, array.getAsInteger(0));
	}
	
	@Test
	void addAllWithNullArray() {
		BinaryArray array = new BinaryArray();
		
		assertThrows(NullPointerException.class, () -> array.addAll((BinaryArray) null));
	}
	
	@Test
	void addAllWithNullList() {
		BinaryArray array = new BinaryArray();
		
		assertThrows(NullPointerException.class, () -> array.addAll((List<BinaryElement>) null));
	}
	
	@Test
	void addAllWithNullVarargs() {
		BinaryArray array = new BinaryArray();
		
		assertThrows(NullPointerException.class, () -> array.addAll((BinaryElement[]) null));
	}
	
	@Test
	void getElementsIsUnmodifiable() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1));
		
		assertThrows(UnsupportedOperationException.class, () -> array.getElements().add(BinaryNull.INSTANCE));
	}
	
	@Test
	void addNullElementStoresBinaryNull() {
		BinaryArray array = new BinaryArray();
		array.add((BinaryElement) null);
		
		assertSame(BinaryNull.INSTANCE, array.get(0));
	}
	
	@Test
	void addNonNullElementStoresElement() {
		BinaryPrimitive element = new BinaryPrimitive(1);
		BinaryArray array = new BinaryArray();
		array.add(element);
		
		assertSame(element, array.get(0));
	}
	
	@Test
	void addNullNumberStoresBinaryNull() {
		BinaryArray array = new BinaryArray();
		array.add((Number) null);
		
		assertTrue(array.get(0).isBinaryNull());
	}
	
	@Test
	void addNumberStoresPrimitive() {
		BinaryArray array = new BinaryArray();
		array.add(Integer.valueOf(5));
		
		assertEquals(5, array.getAsInteger(0));
	}
	
	@Test
	void addNullStringStoresBinaryNull() {
		BinaryArray array = new BinaryArray();
		array.add((String) null);
		
		assertTrue(array.get(0).isBinaryNull());
	}
	
	@Test
	void addStringStoresPrimitive() {
		BinaryArray array = new BinaryArray();
		array.add("text");
		
		assertEquals("text", array.getAsString(0));
	}
	
	@Test
	void setNullElementStoresBinaryNull() {
		BinaryPrimitive previous = new BinaryPrimitive(1);
		BinaryArray array = new BinaryArray(previous);
		
		assertSame(previous, array.set(0, (BinaryElement) null));
		assertTrue(array.get(0).isBinaryNull());
	}
	
	@Test
	void setNonNullElementReplacesElement() {
		BinaryPrimitive previous = new BinaryPrimitive(1);
		BinaryPrimitive replacement = new BinaryPrimitive(2);
		BinaryArray array = new BinaryArray(previous);
		
		assertSame(previous, array.set(0, replacement));
		assertSame(replacement, array.get(0));
	}
	
	@Test
	void setNullNumberAndStringStoreBinaryNull() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1), new BinaryPrimitive(2));
		
		array.set(0, (Number) null);
		array.set(1, (String) null);
		
		assertTrue(array.get(0).isBinaryNull());
		assertTrue(array.get(1).isBinaryNull());
	}
	
	@Test
	void setStringAndNumberStorePrimitives() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(0));
		
		BinaryElement first = array.set(0, Integer.valueOf(9));
		assertEquals(0, first.getAsInteger());
		assertEquals(9, array.getAsInteger(0));
		assertEquals(BinaryType.INTEGER, array.get(0).getType());
		
		BinaryElement second = array.set(0, "text");
		assertEquals(9, second.getAsInteger());
		assertEquals("text", array.getAsString(0));
		assertEquals(BinaryType.STRING, array.get(0).getType());
	}
	
	@Test
	void getAsBinaryPrimitiveReturnsPrimitive() {
		BinaryPrimitive element = new BinaryPrimitive(1);
		BinaryArray array = new BinaryArray(element);
		
		assertSame(element, array.getAsBinaryPrimitive(0));
		assertEquals(BinaryType.INTEGER, array.getAsBinaryPrimitive(0).getType());
	}
	
	@Test
	void isEmptyOnEmptyAndFilledArray() {
		BinaryArray array = new BinaryArray();
		assertTrue(array.isEmpty());
		
		array.add(1);
		assertFalse(array.isEmpty());
	}
	
	@Test
	void containsExistingAndMissingElement() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1));
		
		assertTrue(array.contains(new BinaryPrimitive(1)));
		assertFalse(array.contains(new BinaryPrimitive(2)));
		assertFalse(array.contains(null));
	}
	
	@Test
	void removeExistingElementReturnsTrue() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1), new BinaryPrimitive(2));
		
		assertTrue(array.remove(new BinaryPrimitive(1)));
		assertEquals(1, array.size());
	}
	
	@Test
	void removeMissingElementReturnsFalse() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1));
		
		assertFalse(array.remove(new BinaryPrimitive(9)));
		assertFalse(array.remove(null));
		assertEquals(1, array.size());
	}
	
	@Test
	void addAllWithEmptyAndFilledList() {
		BinaryArray array = new BinaryArray();
		
		array.addAll(List.of());
		assertEquals(0, array.size());
		
		array.addAll(List.of(new BinaryPrimitive(1), new BinaryPrimitive(2)));
		assertEquals(2, array.size());
	}
	
	@Test
	void addAllWithEmptyAndFilledVarargs() {
		BinaryArray array = new BinaryArray();
		
		array.addAll();
		assertEquals(0, array.size());
		
		array.addAll(new BinaryPrimitive(1), new BinaryPrimitive(2));
		assertEquals(2, array.size());
	}
	
	@Test
	void addAllWithEmptyAndFilledArray() {
		BinaryArray array = new BinaryArray();
		
		array.addAll(new BinaryArray());
		assertEquals(0, array.size());
		
		BinaryArray source = new BinaryArray(new BinaryPrimitive(1), new BinaryPrimitive(2));
		array.addAll(source);
		assertEquals(2, array.size());
		assertEquals(2, source.size());
	}
	
	@Test
	void addAllListConvertsNullElements() {
		BinaryArray array = new BinaryArray();
		array.addAll(Arrays.asList(new BinaryPrimitive(1), null));
		
		assertEquals(2, array.size());
		assertTrue(array.get(1).isBinaryNull());
	}
	
	@Test
	void constructorKeepsNullElementsUnconverted() {
		BinaryArray array = new BinaryArray(Arrays.asList(new BinaryPrimitive(1), null));
		
		assertEquals(2, array.size());
		assertNull(array.get(1));
		assertThrows(NullPointerException.class, array::getElements);
	}
	
	@Test
	void toStringOnEmptyAndFilledArray() {
		assertEquals("[]", new BinaryArray().toString());
		
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1), new BinaryPrimitive("text"));
		assertEquals("[1, text]", array.toString());
	}
	
	@Test
	void equalsWithNonArray() {
		BinaryArray array = new BinaryArray();
		
		assertNotEquals(null, array);
		assertNotEquals(array, BinaryNull.INSTANCE);
		assertNotEquals(array, List.of());
	}
	
	@Test
	void equalsWithSameAndDifferentElements() {
		BinaryArray first = new BinaryArray(new BinaryPrimitive(1), new BinaryPrimitive(2));
		BinaryArray same = new BinaryArray(new BinaryPrimitive(1), new BinaryPrimitive(2));
		BinaryArray reordered = new BinaryArray(new BinaryPrimitive(2), new BinaryPrimitive(1));
		BinaryArray shorter = new BinaryArray(new BinaryPrimitive(1));
		
		assertEquals(first, same);
		assertEquals(first.hashCode(), same.hashCode());
		assertNotEquals(first, reordered);
		assertNotEquals(first, shorter);
	}
	
	@Test
	void addPrimitiveValueOverloads() {
		BinaryArray array = new BinaryArray();
		array.add(true);
		array.add((byte) 1);
		array.add((short) 2);
		array.add(3);
		array.add(4L);
		array.add(5.0F);
		array.add(6.0);
		
		assertEquals(7, array.size());
		assertEquals(BinaryType.BOOLEAN, array.get(0).getType());
		assertEquals(BinaryType.BYTE, array.get(1).getType());
		assertEquals(BinaryType.SHORT, array.get(2).getType());
		assertEquals(BinaryType.INTEGER, array.get(3).getType());
		assertEquals(BinaryType.LONG, array.get(4).getType());
		assertEquals(BinaryType.FLOAT, array.get(5).getType());
		assertEquals(BinaryType.DOUBLE, array.get(6).getType());
	}
	
	@Test
	void setPrimitiveValueOverloads() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(0));
		
		assertEquals(0, array.set(0, true).getAsInteger());
		assertEquals(BinaryType.BOOLEAN, array.get(0).getType());
		
		assertTrue(array.set(0, (byte) 1).getAsBoolean());
		assertEquals(BinaryType.BYTE, array.get(0).getType());
		
		assertEquals((byte) 1, array.set(0, (short) 2).getAsByte());
		assertEquals(BinaryType.SHORT, array.get(0).getType());
		
		assertEquals((short) 2, array.set(0, 3).getAsShort());
		assertEquals(BinaryType.INTEGER, array.get(0).getType());
		
		assertEquals(3, array.set(0, 4L).getAsInteger());
		assertEquals(BinaryType.LONG, array.get(0).getType());
		
		assertEquals(4L, array.set(0, 5.0F).getAsLong());
		assertEquals(BinaryType.FLOAT, array.get(0).getType());
		
		assertEquals(5.0F, array.set(0, 6.0).getAsFloat());
		assertEquals(BinaryType.DOUBLE, array.get(0).getType());
	}
	
	@Test
	void getAsValueOverloads() {
		BinaryArray array = new BinaryArray(
			new BinaryPrimitive(true),
			new BinaryPrimitive((byte) 1),
			new BinaryPrimitive((short) 2),
			new BinaryPrimitive(3),
			new BinaryPrimitive(4L),
			new BinaryPrimitive(5.0F),
			new BinaryPrimitive(6.0),
			new BinaryPrimitive("text")
		);
		
		assertTrue(array.getAsBoolean(0));
		assertEquals((byte) 1, array.getAsByte(1));
		assertEquals((short) 2, array.getAsShort(2));
		assertEquals(3, array.getAsInteger(3));
		assertEquals(4L, array.getAsLong(4));
		assertEquals(5.0F, array.getAsFloat(5));
		assertEquals(6.0, array.getAsDouble(6));
		assertEquals("text", array.getAsString(7));
		assertEquals(3, array.getAsNumber(3).intValue());
	}
	
	@Test
	void sizeAfterAddAndRemove() {
		BinaryArray array = new BinaryArray();
		assertEquals(0, array.size());
		
		array.add(1);
		array.add(2);
		array.add(3);
		assertEquals(3, array.size());
		
		array.remove(0);
		assertEquals(2, array.size());
		
		array.clear();
		assertEquals(0, array.size());
	}
	
	@Test
	void clearEmptiesArray() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1));
		array.clear();
		assertTrue(array.isEmpty());
		
		assertDoesNotThrow(array::clear);
		assertTrue(array.isEmpty());
	}
	
	@Test
	void removeByIndexShiftsElements() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(0), new BinaryPrimitive(1), new BinaryPrimitive(2));
		
		assertEquals(1, array.remove(1).getAsInteger());
		assertEquals(2, array.size());
		assertEquals(2, array.getAsInteger(1));
	}
	
	@Test
	void iterateOverElements() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(0), new BinaryPrimitive(1), new BinaryPrimitive(2));
		
		int index = 0;
		for (BinaryElement element : array) {
			assertEquals(index, element.getAsInteger());
			index++;
		}
		assertEquals(array.size(), index);
	}
	
	@Test
	void iterateOverEmptyArray() {
		assertFalse(new BinaryArray().iterator().hasNext());
	}
	
	@Test
	void streamOverElements() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1), new BinaryPrimitive(2));
		
		assertEquals(array.size(), array.stream().count());
		assertEquals(List.of(1, 2), array.stream().map(BinaryElement::getAsInteger).toList());
	}
	
	@Test
	void getElementsReturnsCopy() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1));
		List<BinaryElement> elements = array.getElements();
		
		array.add(2);
		
		assertEquals(1, elements.size());
		assertEquals(2, array.size());
	}
	
	@Test
	void getTypeIsList() {
		BinaryArray array = new BinaryArray();
		
		assertEquals(BinaryType.LIST, array.getType());
		assertTrue(array.isBinaryArray());
	}
	
	@Test
	void mixedTypeElementsCoexist() {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, 1);
		BinaryMap map = new BinaryMap();
		map.add("k", 1);
		
		BinaryArray array = new BinaryArray(
			new BinaryPrimitive(true),
			new BinaryPrimitive(1),
			new BinaryPrimitive("text"),
			BinaryNull.INSTANCE,
			new BinaryArray(),
			struct,
			map
		);
		
		assertEquals(BinaryType.BOOLEAN, array.get(0).getType());
		assertEquals(BinaryType.INTEGER, array.get(1).getType());
		assertEquals(BinaryType.STRING, array.get(2).getType());
		assertEquals(BinaryType.NULL, array.get(3).getType());
		assertEquals(BinaryType.LIST, array.get(4).getType());
		assertEquals(BinaryType.STRUCT, array.get(5).getType());
		assertEquals(BinaryType.MAP, array.get(6).getType());
		
		assertTrue(array.getAsBoolean(0));
		assertEquals("text", array.getAsString(2));
	}
	
	@Test
	void nestedArraysAreAccessible() {
		BinaryArray array = new BinaryArray(new BinaryArray(new BinaryPrimitive(42)));
		
		assertEquals(42, array.getAsBinaryArray(0).getAsInteger(0));
	}
	
	@Test
	void nestedStructAndMapAreAccessible() {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, "structValue");
		BinaryMap map = new BinaryMap();
		map.add("key", "mapValue");
		
		BinaryArray array = new BinaryArray(struct, map);
		
		assertEquals("structValue", array.getAsBinaryStruct(0).getAsString(0));
		assertEquals("mapValue", array.getAsBinaryMap(1).getAsString("key"));
	}
	
	@Test
	void addAllFromSelfDoublesArray() {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1), new BinaryPrimitive(2));
		
		array.addAll(array);
		
		assertEquals(4, array.size());
		assertEquals(List.of(1, 2, 1, 2), array.stream().map(BinaryElement::getAsInteger).toList());
	}
	
	@Test
	void largeArrayHandling() {
		BinaryArray array = new BinaryArray();
		for (int i = 0; i < 1000; i++) {
			array.add(i);
		}
		
		assertEquals(1000, array.size());
		assertEquals(0, array.getAsInteger(0));
		assertEquals(999, array.getAsInteger(999));
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> array.get(1000));
	}
	
	@Test
	void mutationSequenceKeepsConsistency() {
		BinaryArray array = new BinaryArray();
		
		array.add(1);
		assertEquals(1, array.size());
		assertTrue(array.contains(new BinaryPrimitive(1)));
		
		array.set(0, 2);
		assertEquals(1, array.size());
		assertFalse(array.contains(new BinaryPrimitive(1)));
		assertEquals("[2]", array.toString());
		
		array.addAll(List.of(new BinaryPrimitive(3)));
		assertEquals(2, array.size());
		assertEquals(2, array.getElements().size());
		
		array.remove(0);
		assertEquals(1, array.size());
		assertEquals("[3]", array.toString());
		
		array.clear();
		assertTrue(array.isEmpty());
		assertEquals("[]", array.toString());
		assertTrue(array.getElements().isEmpty());
	}
	
	@Test
	void arrayRoundTripThroughWriterAndReader() {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, "inner");
		BinaryArray original = new BinaryArray(
			new BinaryPrimitive(1),
			new BinaryPrimitive("text"),
			new BinaryArray(new BinaryPrimitive(true)),
			struct
		);
		
		BinaryElement decoded = BinaryReader.fromByteArray(BinaryWriter.toByteArray(original));
		
		assertEquals(original, decoded);
	}
	
	@Test
	void emptyArrayRoundTrip() {
		BinaryArray original = new BinaryArray();
		
		BinaryElement decoded = BinaryReader.fromByteArray(BinaryWriter.toByteArray(original));
		
		assertInstanceOf(BinaryArray.class, decoded);
		assertEquals(original, decoded);
		assertTrue(decoded.getAsBinaryArray().isEmpty());
	}
}
