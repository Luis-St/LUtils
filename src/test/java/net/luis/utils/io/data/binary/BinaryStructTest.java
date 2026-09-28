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

import net.luis.utils.io.data.binary.exception.*;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryStruct}.<br>
 *
 * @author Luis-St
 */
class BinaryStructTest {
	
	@Test
	void constructWithFieldCount() {
		BinaryStruct struct = new BinaryStruct(3);
		
		assertEquals(3, struct.size());
		assertEquals(0, struct.presentFields());
		assertSame(BinaryAbsent.INSTANCE, struct.get(0));
		assertSame(BinaryAbsent.INSTANCE, struct.get(1));
		assertSame(BinaryAbsent.INSTANCE, struct.get(2));
		assertFalse(struct.hasNames());
	}
	
	@Test
	void constructWithZeroFieldCount() {
		BinaryStruct struct = new BinaryStruct(0);
		
		assertEquals(0, struct.size());
		assertTrue(struct.isEmpty());
		assertEquals("()", struct.toString());
	}
	
	@Test
	void constructWithValueList() {
		BinaryStruct struct = new BinaryStruct(List.of(new BinaryPrimitive(1), new BinaryPrimitive("a")));
		
		assertEquals(2, struct.size());
		assertEquals(2, struct.presentFields());
		assertFalse(struct.hasNames());
		assertTrue(struct.getName(0).isEmpty());
	}
	
	@Test
	void constructWithValueVarargs() {
		BinaryStruct struct = new BinaryStruct(new BinaryPrimitive(1), new BinaryPrimitive("a"));
		
		assertEquals(2, struct.size());
		assertEquals(1, struct.getAsInteger(0));
		assertEquals("a", struct.getAsString(1));
	}
	
	@Test
	void constructWithEmptyValueList() {
		assertTrue(new BinaryStruct(List.of()).isEmpty());
	}
	
	@Test
	void constructWithNegativeFieldCount() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new BinaryStruct(-1));
		
		assertTrue(exception.getMessage().contains("-1"));
	}
	
	@Test
	void constructWithNullValueList() {
		assertThrows(NullPointerException.class, () -> new BinaryStruct((List<BinaryElement>) null));
	}
	
	@Test
	void constructWithNullValueVarargs() {
		assertThrows(NullPointerException.class, () -> new BinaryStruct((BinaryElement[]) null));
	}
	
	@Test
	void getWithNegativeIndex() {
		BinaryStruct struct = new BinaryStruct(2);
		
		BinaryIndexOutOfBoundsException exception = assertThrows(BinaryIndexOutOfBoundsException.class, () -> struct.get(-1));
		assertFalse(exception.getMessage().contains("of size"));
	}
	
	@Test
	void getWithIndexEqualToSize() {
		BinaryStruct struct = new BinaryStruct(2);
		
		BinaryIndexOutOfBoundsException exception = assertThrows(BinaryIndexOutOfBoundsException.class, () -> struct.get(2));
		assertTrue(exception.getMessage().contains("of size 2"));
	}
	
	@Test
	void getOnEmptyStruct() {
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> new BinaryStruct(0).get(0));
	}
	
	@Test
	void setWithInvalidIndex() {
		BinaryStruct struct = new BinaryStruct(2);
		
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> struct.set(-1, new BinaryPrimitive(1)));
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> struct.set(2, 1));
	}
	
	@Test
	void setNamedWithInvalidIndex() {
		BinaryStruct struct = new BinaryStruct(2);
		
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> struct.set(2, "name", new BinaryPrimitive(1)));
		assertTrue(struct.getName(0).isEmpty());
		assertTrue(struct.getName(1).isEmpty());
	}
	
	@Test
	void setNamedWithNullName() {
		BinaryStruct struct = new BinaryStruct(2);
		
		assertThrows(NullPointerException.class, () -> struct.set(0, null, new BinaryPrimitive(1)));
		assertThrows(NullPointerException.class, () -> struct.set(5, null, new BinaryPrimitive(1)));
	}
	
	@Test
	void removeWithInvalidIndex() {
		BinaryStruct struct = new BinaryStruct(2);
		
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> struct.remove(-1));
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> struct.remove(2));
	}
	
	@Test
	void getNameWithInvalidIndex() {
		BinaryStruct struct = new BinaryStruct(2);
		
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> struct.getName(-1));
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> struct.getName(2));
	}
	
	@Test
	void getAsTypedOnAbsentFieldThrows() {
		BinaryStruct struct = new BinaryStruct(2);
		
		assertTrue(assertThrows(NoSuchBinaryElementException.class, () -> struct.getAsInteger(0)).getMessage().contains("field 0"));
		assertThrows(NoSuchBinaryElementException.class, () -> struct.getAsString(0));
		assertThrows(NoSuchBinaryElementException.class, () -> struct.getAsBinaryArray(0));
		assertThrows(NoSuchBinaryElementException.class, () -> struct.getAsBinaryStruct(0));
		assertThrows(NoSuchBinaryElementException.class, () -> struct.getAsBinaryMap(0));
		assertThrows(NoSuchBinaryElementException.class, () -> struct.getAsBinaryPrimitive(0));
		assertThrows(NoSuchBinaryElementException.class, () -> struct.getAsBoolean(0));
		assertThrows(NoSuchBinaryElementException.class, () -> struct.getAsNumber(0));
	}
	
	@Test
	void getAsTypedWithInvalidIndex() {
		BinaryStruct struct = new BinaryStruct(2);
		
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> struct.getAsInteger(5));
		assertThrows(BinaryIndexOutOfBoundsException.class, () -> struct.getAsString(-1));
	}
	
	@Test
	void getAsWrongTypeThrows() {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, 1);
		
		assertThrows(BinaryTypeException.class, () -> struct.getAsString(0));
		assertThrows(BinaryTypeException.class, () -> struct.getAsBoolean(0));
		assertThrows(BinaryTypeException.class, () -> struct.getAsBinaryMap(0));
	}
	
	@Test
	void getAsTypedOnRemovedFieldThrows() {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, 1);
		struct.remove(0);
		
		assertThrows(NoSuchBinaryElementException.class, () -> struct.getAsInteger(0));
	}
	
	@Test
	void setUnsupportedNumberThrows() {
		BinaryStruct struct = new BinaryStruct(2);
		
		assertThrows(BinaryTypeException.class, () -> struct.set(0, BigInteger.ONE));
		assertFalse(struct.has(0));
		
		assertThrows(BinaryTypeException.class, () -> struct.set(1, "name", BigInteger.ONE));
		assertFalse(struct.has(1));
	}
	
	@Test
	void getValuesIsUnmodifiable() {
		BinaryStruct struct = new BinaryStruct(1);
		
		assertThrows(UnsupportedOperationException.class, () -> struct.getValues().add(BinaryNull.INSTANCE));
	}
	
	@Test
	void getNamesIsUnmodifiable() {
		BinaryStruct struct = new BinaryStruct(1);
		
		assertThrows(UnsupportedOperationException.class, () -> struct.getNames().add(Optional.empty()));
	}
	
	@Test
	void hasWithNegativeIndex() {
		assertFalse(new BinaryStruct(2).has(-1));
	}
	
	@Test
	void hasWithIndexAboveSize() {
		BinaryStruct struct = new BinaryStruct(2);
		
		assertFalse(struct.has(struct.size()));
	}
	
	@Test
	void hasWithAbsentField() {
		assertFalse(new BinaryStruct(2).has(0));
	}
	
	@Test
	void hasWithPresentField() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, 1);
		
		assertTrue(struct.has(0));
	}
	
	@Test
	void hasWithNullValueField() {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, (BinaryElement) null);
		
		assertTrue(struct.has(0));
	}
	
	@Test
	void getOrDefaultWithAbsentField() {
		BinaryStruct struct = new BinaryStruct(1);
		BinaryPrimitive fallback = new BinaryPrimitive(9);
		
		assertSame(fallback, struct.getOrDefault(0, fallback));
	}
	
	@Test
	void getOrDefaultWithOutOfRangeIndex() {
		BinaryStruct struct = new BinaryStruct(1);
		BinaryPrimitive fallback = new BinaryPrimitive(9);
		
		assertSame(fallback, struct.getOrDefault(-1, fallback));
		assertSame(fallback, struct.getOrDefault(struct.size(), fallback));
	}
	
	@Test
	void getOrDefaultWithPresentField() {
		BinaryPrimitive stored = new BinaryPrimitive(1);
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, stored);
		
		assertSame(stored, struct.getOrDefault(0, new BinaryPrimitive(9)));
	}
	
	@Test
	void getOrDefaultWithNullDefault() {
		assertNull(new BinaryStruct(1).getOrDefault(0, null));
	}
	
	@Test
	void setNullElementStoresBinaryNull() {
		BinaryStruct struct = new BinaryStruct(1);
		
		assertSame(BinaryAbsent.INSTANCE, struct.set(0, (BinaryElement) null));
		assertTrue(struct.get(0).isBinaryNull());
	}
	
	@Test
	void setNonNullElementReplacesValue() {
		BinaryPrimitive element = new BinaryPrimitive(1);
		BinaryStruct struct = new BinaryStruct(1);
		
		assertSame(BinaryAbsent.INSTANCE, struct.set(0, element));
		assertSame(element, struct.get(0));
	}
	
	@Test
	void setNullNumberAndStringStoreBinaryNull() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, (Number) null);
		struct.set(1, (String) null);
		
		assertTrue(struct.get(0).isBinaryNull());
		assertTrue(struct.get(1).isBinaryNull());
	}
	
	@Test
	void setNamedNullNumberAndStringStoreBinaryNull() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, "first", (Number) null);
		struct.set(1, "second", (String) null);
		
		assertTrue(struct.get(0).isBinaryNull());
		assertTrue(struct.get(1).isBinaryNull());
		assertEquals(Optional.of("first"), struct.getName(0));
		assertEquals(Optional.of("second"), struct.getName(1));
	}
	
	@Test
	void setStringAndNumberStorePrimitives() {
		BinaryStruct struct = new BinaryStruct(2);
		
		assertSame(BinaryAbsent.INSTANCE, struct.set(0, Long.valueOf(9L)));
		assertSame(BinaryAbsent.INSTANCE, struct.set(1, "text"));
		
		assertEquals(9L, struct.getAsLong(0));
		assertEquals(BinaryType.LONG, struct.get(0).getType());
		assertEquals("text", struct.getAsString(1));
		assertEquals(BinaryType.STRING, struct.get(1).getType());
		assertEquals(2, struct.presentFields());
	}
	
	@Test
	void setNamedStringAndNumberStorePrimitives() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, "count", Integer.valueOf(3));
		struct.set(1, "label", "text");
		
		assertEquals(3, struct.getAsInteger(0));
		assertEquals("text", struct.getAsString(1));
		assertEquals(0, struct.indexOf("count"));
		assertEquals(1, struct.indexOf("label"));
		assertTrue(struct.hasNames());
	}
	
	@Test
	void presentFieldsWithNoneSomeAndAll() {
		BinaryStruct struct = new BinaryStruct(3);
		assertEquals(0, struct.presentFields());
		
		struct.set(0, 1);
		assertEquals(1, struct.presentFields());
		
		struct.set(1, 2);
		struct.set(2, 3);
		assertEquals(3, struct.presentFields());
	}
	
	@Test
	void hasNamesWithoutAnyName() {
		assertFalse(new BinaryStruct(2).hasNames());
		assertFalse(new BinaryStruct(List.of(new BinaryPrimitive(1))).hasNames());
	}
	
	@Test
	void hasNamesWithOneName() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, "name", 1);
		
		assertTrue(struct.hasNames());
	}
	
	@Test
	void indexOfWithNullName() {
		assertEquals(-1, new BinaryStruct(1).indexOf(null));
	}
	
	@Test
	void indexOfWithKnownName() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(1, "name", 1);
		
		assertEquals(1, struct.indexOf("name"));
	}
	
	@Test
	void indexOfWithUnknownName() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, "name", 1);
		
		assertEquals(-1, struct.indexOf("other"));
	}
	
	@Test
	void getNameWithKnownAndUnknownName() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, "name", 1);
		struct.set(1, 2);
		
		assertEquals(Optional.of("name"), struct.getName(0));
		assertTrue(struct.getName(1).isEmpty());
	}
	
	@Test
	void getNamesMixesPresentAndEmpty() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, "name", 1);
		
		List<Optional<String>> names = struct.getNames();
		
		assertEquals(struct.size(), names.size());
		assertEquals(Optional.of("name"), names.get(0));
		assertTrue(names.get(1).isEmpty());
	}
	
	@Test
	void toStringWithNamedAndUnnamedFields() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, "name", 1);
		struct.set(1, 2);
		
		String result = struct.toString();
		
		assertTrue(result.contains("name: "));
		assertTrue(result.contains("1: "));
	}
	
	@Test
	void toStringOnEmptyStruct() {
		assertEquals("()", new BinaryStruct(0).toString());
	}
	
	@Test
	void equalsWithNonStruct() {
		BinaryStruct struct = new BinaryStruct(1);
		
		assertNotEquals(null, struct);
		assertNotEquals(struct, new BinaryArray());
		assertNotEquals(struct, List.of());
	}
	
	@Test
	void equalsIgnoresFieldNames() {
		BinaryStruct first = new BinaryStruct(1);
		first.set(0, "alpha", 1);
		BinaryStruct second = new BinaryStruct(1);
		second.set(0, "beta", 1);
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void equalsWithDifferentValues() {
		BinaryStruct first = new BinaryStruct(1);
		first.set(0, 1);
		BinaryStruct different = new BinaryStruct(1);
		different.set(0, 2);
		BinaryStruct larger = new BinaryStruct(2);
		larger.set(0, 1);
		
		assertNotEquals(first, different);
		assertNotEquals(first, larger);
	}
	
	@Test
	void setPrimitiveValueOverloads() {
		BinaryStruct struct = new BinaryStruct(7);
		
		assertSame(BinaryAbsent.INSTANCE, struct.set(0, true));
		assertSame(BinaryAbsent.INSTANCE, struct.set(1, (byte) 1));
		assertSame(BinaryAbsent.INSTANCE, struct.set(2, (short) 2));
		assertSame(BinaryAbsent.INSTANCE, struct.set(3, 3));
		assertSame(BinaryAbsent.INSTANCE, struct.set(4, 4L));
		assertSame(BinaryAbsent.INSTANCE, struct.set(5, 5.0F));
		assertSame(BinaryAbsent.INSTANCE, struct.set(6, 6.0));
		
		assertEquals(BinaryType.BOOLEAN, struct.get(0).getType());
		assertEquals(BinaryType.BYTE, struct.get(1).getType());
		assertEquals(BinaryType.SHORT, struct.get(2).getType());
		assertEquals(BinaryType.INTEGER, struct.get(3).getType());
		assertEquals(BinaryType.LONG, struct.get(4).getType());
		assertEquals(BinaryType.FLOAT, struct.get(5).getType());
		assertEquals(BinaryType.DOUBLE, struct.get(6).getType());
	}
	
	@Test
	void setNamedPrimitiveValueOverloads() {
		BinaryStruct struct = new BinaryStruct(7);
		struct.set(0, "flag", true);
		struct.set(1, "byteField", (byte) 1);
		struct.set(2, "shortField", (short) 2);
		struct.set(3, "intField", 3);
		struct.set(4, "longField", 4L);
		struct.set(5, "floatField", 5.0F);
		struct.set(6, "doubleField", 6.0);
		
		assertEquals(0, struct.indexOf("flag"));
		assertEquals(1, struct.indexOf("byteField"));
		assertEquals(2, struct.indexOf("shortField"));
		assertEquals(3, struct.indexOf("intField"));
		assertEquals(4, struct.indexOf("longField"));
		assertEquals(5, struct.indexOf("floatField"));
		assertEquals(6, struct.indexOf("doubleField"));
		
		assertTrue(struct.getAsBoolean(0));
		assertEquals(6.0, struct.getAsDouble(6));
	}
	
	@Test
	void getAsValueOverloads() {
		BinaryStruct struct = new BinaryStruct(8);
		struct.set(0, true);
		struct.set(1, (byte) 1);
		struct.set(2, (short) 2);
		struct.set(3, 3);
		struct.set(4, 4L);
		struct.set(5, 5.0F);
		struct.set(6, 6.0);
		struct.set(7, "text");
		
		assertTrue(struct.getAsBoolean(0));
		assertEquals((byte) 1, struct.getAsByte(1));
		assertEquals((short) 2, struct.getAsShort(2));
		assertEquals(3, struct.getAsInteger(3));
		assertEquals(4L, struct.getAsLong(4));
		assertEquals(5.0F, struct.getAsFloat(5));
		assertEquals(6.0, struct.getAsDouble(6));
		assertEquals("text", struct.getAsString(7));
		assertEquals(3, struct.getAsNumber(3).intValue());
	}
	
	@Test
	void getAsBinaryPrimitiveReturnsPrimitive() {
		BinaryPrimitive element = new BinaryPrimitive(1);
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, element);
		
		assertSame(element, struct.getAsBinaryPrimitive(0));
		assertEquals(BinaryType.INTEGER, struct.getAsBinaryPrimitive(0).getType());
	}
	
	@Test
	void removeSetsFieldAbsentWithoutShrinking() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, 1);
		struct.set(1, 2);
		
		assertEquals(1, struct.remove(0).getAsInteger());
		assertEquals(2, struct.size());
		assertFalse(struct.has(0));
		assertEquals(1, struct.presentFields());
	}
	
	@Test
	void removeKeepsFieldName() {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, "name", 1);
		
		struct.remove(0);
		
		assertEquals(Optional.of("name"), struct.getName(0));
		assertEquals(0, struct.indexOf("name"));
	}
	
	@Test
	void clearSetsAllFieldsAbsent() {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, "first", 1);
		struct.set(1, "second", 2);
		
		struct.clear();
		
		assertEquals(0, struct.presentFields());
		assertEquals(2, struct.size());
		assertTrue(struct.hasNames());
		
		assertDoesNotThrow(() -> new BinaryStruct(0).clear());
	}
	
	@Test
	void containsAbsentAndPresentValues() {
		BinaryStruct struct = new BinaryStruct(2);
		assertTrue(struct.contains(BinaryAbsent.INSTANCE));
		
		struct.set(0, 1);
		assertTrue(struct.contains(new BinaryPrimitive(1)));
		assertFalse(struct.contains(new BinaryPrimitive(9)));
		assertFalse(struct.contains(null));
	}
	
	@Test
	void iterateOverFields() {
		BinaryStruct struct = new BinaryStruct(3);
		struct.set(1, 1);
		
		int count = 0;
		for (BinaryElement element : struct) {
			assertNotNull(element);
			count++;
		}
		assertEquals(3, count);
		
		assertFalse(new BinaryStruct(0).iterator().hasNext());
	}
	
	@Test
	void streamOverFields() {
		BinaryStruct struct = new BinaryStruct(3);
		struct.set(0, 1);
		struct.set(2, 3);
		
		assertEquals(struct.size(), struct.stream().count());
		assertEquals(struct.presentFields(), struct.stream().filter(value -> !value.isBinaryAbsent()).count());
	}
	
	@Test
	void getValuesReturnsCopy() {
		BinaryStruct struct = new BinaryStruct(1);
		List<BinaryElement> values = struct.getValues();
		
		struct.set(0, 1);
		
		assertSame(BinaryAbsent.INSTANCE, values.getFirst());
		assertEquals(1, struct.getAsInteger(0));
	}
	
	@Test
	void getTypeIsStruct() {
		BinaryStruct struct = new BinaryStruct(0);
		
		assertEquals(BinaryType.STRUCT, struct.getType());
		assertTrue(struct.isBinaryStruct());
		assertFalse(struct.isBinaryMap());
	}
	
	@Test
	void namedFieldLookupAcrossAllFields() {
		BinaryStruct struct = new BinaryStruct(3);
		struct.set(0, "a", 1);
		struct.set(1, "b", 2);
		struct.set(2, 3);
		
		assertEquals(0, struct.indexOf("a"));
		assertEquals(1, struct.indexOf("b"));
		assertEquals(-1, struct.indexOf("c"));
		assertTrue(struct.hasNames());
		
		List<Optional<String>> names = struct.getNames();
		assertEquals(Optional.of("a"), names.get(0));
		assertEquals(Optional.of("b"), names.get(1));
		assertTrue(names.get(2).isEmpty());
	}
	
	@Test
	void renameFieldByRepeatedNamedSet() {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, "old", 1);
		struct.set(0, "new", 2);
		
		assertEquals(Optional.of("new"), struct.getName(0));
		assertEquals(-1, struct.indexOf("old"));
		assertEquals(2, struct.getAsInteger(0));
	}
	
	@Test
	void nestedStructsAndContainers() {
		BinaryStruct inner = new BinaryStruct(1);
		inner.set(0, "innerValue");
		BinaryArray array = new BinaryArray(new BinaryPrimitive(7));
		BinaryMap map = new BinaryMap();
		map.add("key", "mapValue");
		
		BinaryStruct struct = new BinaryStruct(3);
		struct.set(0, inner);
		struct.set(1, array);
		struct.set(2, map);
		
		assertEquals("innerValue", struct.getAsBinaryStruct(0).getAsString(0));
		assertEquals(7, struct.getAsBinaryArray(1).getAsInteger(0));
		assertEquals("mapValue", struct.getAsBinaryMap(2).getAsString("key"));
	}
	
	@Test
	void deeplyNestedStructure() {
		BinaryStruct level4 = new BinaryStruct(2);
		level4.set(0, "deepest");
		
		BinaryStruct level3 = new BinaryStruct(2);
		level3.set(0, level4);
		
		BinaryStruct level2 = new BinaryStruct(2);
		level2.set(0, level3);
		
		BinaryStruct level1 = new BinaryStruct(2);
		level1.set(0, level2);
		
		assertEquals("deepest", level1.getAsBinaryStruct(0).getAsBinaryStruct(0).getAsBinaryStruct(0).getAsString(0));
		
		assertFalse(level1.has(1));
		assertFalse(level2.has(1));
		assertFalse(level3.has(1));
		assertFalse(level4.has(1));
	}
	
	@Test
	void mutationSequenceKeepsConsistency() {
		BinaryStruct struct = new BinaryStruct(3);
		assertEquals(0, struct.presentFields());
		
		struct.set(0, 1);
		assertEquals(1, struct.presentFields());
		assertTrue(struct.has(0));
		
		struct.set(1, "named", 2);
		assertEquals(2, struct.presentFields());
		assertEquals(Optional.of("named"), struct.getName(1));
		assertTrue(struct.toString().contains("named: 2"));
		
		struct.remove(0);
		assertEquals(1, struct.presentFields());
		assertFalse(struct.has(0));
		assertEquals(3, struct.size());
		
		struct.clear();
		assertEquals(0, struct.presentFields());
		assertEquals(3, struct.size());
		assertEquals(Optional.of("named"), struct.getName(1));
	}
	
	@Test
	void structRoundTripThroughWriterAndReader() {
		BinaryStruct inner = new BinaryStruct(1);
		inner.set(0, "innerField", "innerValue");
		
		BinaryStruct original = new BinaryStruct(3);
		original.set(0, "first", 1);
		original.set(2, "third", inner);
		
		BinaryElement decoded = BinaryReader.fromByteArray(BinaryWriter.toByteArray(original));
		
		assertEquals(original, decoded);
		
		BinaryStruct decodedStruct = decoded.getAsBinaryStruct();
		assertFalse(decodedStruct.hasNames());
		assertFalse(decodedStruct.has(1));
		assertTrue(decodedStruct.has(0));
		assertTrue(decodedStruct.has(2));
	}
	
	@Test
	void emptyStructRoundTrip() {
		BinaryStruct original = new BinaryStruct(0);
		
		BinaryElement decoded = BinaryReader.fromByteArray(BinaryWriter.toByteArray(original));
		
		assertInstanceOf(BinaryStruct.class, decoded);
		assertEquals(original, decoded);
		assertTrue(decoded.getAsBinaryStruct().isEmpty());
	}
}
