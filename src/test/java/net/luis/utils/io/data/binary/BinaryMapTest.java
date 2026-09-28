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
import net.luis.utils.io.data.binary.exception.NoSuchBinaryElementException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryMap}.<br>
 *
 * @author Luis-St
 */
class BinaryMapTest {
	
	@Test
	void constructEmptyMap() {
		BinaryMap map = new BinaryMap();
		
		assertEquals(0, map.size());
		assertTrue(map.isEmpty());
		assertEquals(BinaryType.MAP, map.getType());
		assertEquals("{}", map.toString());
	}
	
	@Test
	void constructWithEntryMap() {
		BinaryMap map = new BinaryMap(Map.of("a", new BinaryPrimitive(1)));
		
		assertEquals(1, map.size());
		assertEquals(1, map.getAsInteger("a"));
		assertTrue(map.containsKey("a"));
	}
	
	@Test
	void constructWithEmptyEntryMap() {
		assertTrue(new BinaryMap(Map.of()).isEmpty());
	}
	
	@Test
	void constructWithNullEntryMap() {
		assertThrows(NullPointerException.class, () -> new BinaryMap(null));
	}
	
	@Test
	void addWithNullKey() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(NullPointerException.class, () -> map.add(null, new BinaryPrimitive(1)));
	}
	
	@Test
	void addValueOverloadsWithNullKey() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(NullPointerException.class, () -> map.add(null, true));
		assertThrows(NullPointerException.class, () -> map.add(null, 1));
		assertThrows(NullPointerException.class, () -> map.add(null, Integer.valueOf(1)));
		assertThrows(NullPointerException.class, () -> map.add(null, "text"));
	}
	
	@Test
	void replaceWithNullKey() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(NullPointerException.class, () -> map.replace(null, new BinaryPrimitive(1)));
		assertThrows(NullPointerException.class, () -> map.replace(null, new BinaryPrimitive(1), new BinaryPrimitive(2)));
	}
	
	@Test
	void replaceWithNullOldElement() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(NullPointerException.class, () -> map.replace("a", null, new BinaryPrimitive(1)));
	}
	
	@Test
	void addAllWithNullMap() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(NullPointerException.class, () -> map.addAll((BinaryMap) null));
	}
	
	@Test
	void addAllWithNullEntryMap() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(NullPointerException.class, () -> map.addAll((Map<String, BinaryElement>) null));
	}
	
	@Test
	void forEachWithNullAction() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(NullPointerException.class, () -> map.forEach(null));
	}
	
	@Test
	void getAsTypedWithNullKey() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(NullPointerException.class, () -> map.getAsString(null));
		assertThrows(NullPointerException.class, () -> map.getAsInteger(null));
		assertThrows(NullPointerException.class, () -> map.getAsBinaryArray(null));
	}
	
	@Test
	void getAsTypedWithMissingKey() {
		BinaryMap map = new BinaryMap();
		
		assertTrue(assertThrows(NoSuchBinaryElementException.class, () -> map.getAsInteger("missing")).getMessage().contains("missing"));
		assertThrows(NoSuchBinaryElementException.class, () -> map.getAsString("missing"));
		assertThrows(NoSuchBinaryElementException.class, () -> map.getAsBinaryArray("missing"));
		assertThrows(NoSuchBinaryElementException.class, () -> map.getAsBinaryStruct("missing"));
		assertThrows(NoSuchBinaryElementException.class, () -> map.getAsBinaryMap("missing"));
		assertThrows(NoSuchBinaryElementException.class, () -> map.getAsBinaryPrimitive("missing"));
		assertThrows(NoSuchBinaryElementException.class, () -> map.getAsBoolean("missing"));
		assertThrows(NoSuchBinaryElementException.class, () -> map.getAsNumber("missing"));
	}
	
	@Test
	void getAsWrongTypeThrows() {
		BinaryMap map = new BinaryMap();
		map.add("n", 1);
		
		assertThrows(BinaryTypeException.class, () -> map.getAsString("n"));
		assertThrows(BinaryTypeException.class, () -> map.getAsBoolean("n"));
		assertThrows(BinaryTypeException.class, () -> map.getAsBinaryMap("n"));
		assertThrows(BinaryTypeException.class, () -> map.getAsBinaryArray("n"));
	}
	
	@Test
	void addUnsupportedNumberThrows() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(BinaryTypeException.class, () -> map.add("k", new BigDecimal("1")));
		assertFalse(map.containsKey("k"));
	}
	
	@Test
	void keySetIsUnmodifiable() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(UnsupportedOperationException.class, () -> map.keySet().add("x"));
	}
	
	@Test
	void valuesIsUnmodifiable() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(UnsupportedOperationException.class, () -> map.values().add(BinaryNull.INSTANCE));
	}
	
	@Test
	void entrySetIsUnmodifiable() {
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		
		assertThrows(UnsupportedOperationException.class, () -> map.entrySet().clear());
	}
	
	@Test
	void getElementsIsUnmodifiable() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(UnsupportedOperationException.class, () -> map.getElements().put("x", BinaryNull.INSTANCE));
	}
	
	@Test
	void addNullElementStoresBinaryNull() {
		BinaryMap map = new BinaryMap();
		map.add("k", (BinaryElement) null);
		
		assertSame(BinaryNull.INSTANCE, map.get("k"));
	}
	
	@Test
	void addNonNullElementStoresElement() {
		BinaryPrimitive element = new BinaryPrimitive(1);
		BinaryMap map = new BinaryMap();
		map.add("k", element);
		
		assertSame(element, map.get("k"));
	}
	
	@Test
	void addReturnsPreviousElement() {
		BinaryPrimitive first = new BinaryPrimitive(1);
		BinaryPrimitive second = new BinaryPrimitive(2);
		BinaryMap map = new BinaryMap();
		
		assertNull(map.add("k", first));
		assertSame(first, map.add("k", second));
		assertSame(second, map.get("k"));
	}
	
	@Test
	void addNullNumberAndStringStoreBinaryNull() {
		BinaryMap map = new BinaryMap();
		map.add("number", (Number) null);
		map.add("string", (String) null);
		
		assertTrue(map.get("number").isBinaryNull());
		assertTrue(map.get("string").isBinaryNull());
	}
	
	@Test
	void addNumberAndStringStorePrimitives() {
		BinaryMap map = new BinaryMap();
		map.add("number", Integer.valueOf(5));
		map.add("string", "text");
		
		assertEquals(5, map.getAsInteger("number"));
		assertEquals("text", map.getAsString("string"));
	}
	
	@Test
	void isEmptyOnEmptyAndFilledMap() {
		BinaryMap map = new BinaryMap();
		assertTrue(map.isEmpty());
		
		map.add("k", 1);
		assertFalse(map.isEmpty());
	}
	
	@Test
	void containsKeyForExistingAndMissingKey() {
		BinaryMap map = new BinaryMap();
		map.add("k", 1);
		
		assertTrue(map.containsKey("k"));
		assertFalse(map.containsKey("missing"));
		assertFalse(map.containsKey(null));
	}
	
	@Test
	void containsValueForExistingAndMissingValue() {
		BinaryMap map = new BinaryMap();
		map.add("k", 1);
		
		assertTrue(map.containsValue(new BinaryPrimitive(1)));
		assertFalse(map.containsValue(new BinaryPrimitive(9)));
		assertFalse(map.containsValue(null));
	}
	
	@Test
	void removeExistingKeyReturnsElement() {
		BinaryMap map = new BinaryMap();
		map.add("k", 1);
		
		assertEquals(1, map.remove("k").getAsInteger());
		assertFalse(map.containsKey("k"));
		assertEquals(0, map.size());
	}
	
	@Test
	void removeMissingKeyReturnsNull() {
		BinaryMap map = new BinaryMap();
		map.add("k", 1);
		
		assertNull(map.remove("missing"));
		assertNull(map.remove(null));
		assertEquals(1, map.size());
	}
	
	@Test
	void replaceExistingKeyReturnsPrevious() {
		BinaryPrimitive first = new BinaryPrimitive(1);
		BinaryPrimitive second = new BinaryPrimitive(2);
		BinaryMap map = new BinaryMap();
		map.add("k", first);
		
		assertSame(first, map.replace("k", second));
		assertSame(second, map.get("k"));
	}
	
	@Test
	void replaceMissingKeyReturnsNull() {
		BinaryMap map = new BinaryMap();
		
		assertNull(map.replace("k", new BinaryPrimitive(1)));
		assertFalse(map.containsKey("k"));
		assertTrue(map.isEmpty());
	}
	
	@Test
	void replaceWithNullElementStoresBinaryNull() {
		BinaryMap map = new BinaryMap();
		map.add("k", 1);
		
		map.replace("k", null);
		
		assertTrue(map.get("k").isBinaryNull());
	}
	
	@Test
	void replaceWithMatchingOldElementReturnsTrue() {
		BinaryPrimitive stored = new BinaryPrimitive(1);
		BinaryPrimitive replacement = new BinaryPrimitive(2);
		BinaryMap map = new BinaryMap();
		map.add("k", stored);
		
		assertTrue(map.replace("k", stored, replacement));
		assertSame(replacement, map.get("k"));
	}
	
	@Test
	void replaceWithNonMatchingOldElementReturnsFalse() {
		BinaryPrimitive stored = new BinaryPrimitive(1);
		BinaryMap map = new BinaryMap();
		map.add("k", stored);
		
		assertFalse(map.replace("k", new BinaryPrimitive(9), new BinaryPrimitive(2)));
		assertSame(stored, map.get("k"));
		
		assertFalse(map.replace("missing", new BinaryPrimitive(1), new BinaryPrimitive(2)));
		assertFalse(map.containsKey("missing"));
	}
	
	@Test
	void replaceWithNullNewElementStoresBinaryNull() {
		BinaryPrimitive stored = new BinaryPrimitive(1);
		BinaryMap map = new BinaryMap();
		map.add("k", stored);
		
		assertTrue(map.replace("k", stored, null));
		assertTrue(map.get("k").isBinaryNull());
	}
	
	@Test
	void getForExistingAndMissingKey() {
		BinaryPrimitive stored = new BinaryPrimitive(1);
		BinaryMap map = new BinaryMap();
		map.add("k", stored);
		
		assertSame(stored, map.get("k"));
		assertNull(map.get("missing"));
		assertNull(map.get(null));
	}
	
	@Test
	void getOrDefaultForExistingAndMissingKey() {
		BinaryPrimitive stored = new BinaryPrimitive(1);
		BinaryPrimitive fallback = new BinaryPrimitive(9);
		BinaryMap map = new BinaryMap();
		map.add("k", stored);
		
		assertSame(stored, map.getOrDefault("k", fallback));
		assertSame(fallback, map.getOrDefault("missing", fallback));
		assertSame(fallback, map.getOrDefault(null, fallback));
		assertNull(map.getOrDefault("missing", null));
	}
	
	@Test
	void addAllWithEmptyAndFilledEntryMap() {
		BinaryMap map = new BinaryMap();
		
		map.addAll(Map.of());
		assertEquals(0, map.size());
		
		map.addAll(Map.of("a", new BinaryPrimitive(1), "b", new BinaryPrimitive(2)));
		assertEquals(2, map.size());
	}
	
	@Test
	void addAllEntryMapConvertsNullValues() {
		Map<String, BinaryElement> source = new HashMap<>();
		source.put("k", null);
		
		BinaryMap map = new BinaryMap();
		map.addAll(source);
		
		assertSame(BinaryNull.INSTANCE, map.get("k"));
	}
	
	@Test
	void addAllWithEmptyAndFilledBinaryMap() {
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		
		map.addAll(new BinaryMap());
		assertEquals(1, map.size());
		
		BinaryMap source = new BinaryMap();
		source.add("a", 9);
		source.add("b", 2);
		
		map.addAll(source);
		assertEquals(2, map.size());
		assertEquals(9, map.getAsInteger("a"));
		assertEquals(2, map.getAsInteger("b"));
		assertEquals(2, source.size());
	}
	
	@Test
	void forEachOnEmptyAndFilledMap() {
		AtomicInteger emptyCount = new AtomicInteger(0);
		new BinaryMap().forEach((key, element) -> emptyCount.incrementAndGet());
		assertEquals(0, emptyCount.get());
		
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		map.add("b", 2);
		
		List<String> keys = new ArrayList<>();
		AtomicInteger count = new AtomicInteger(0);
		map.forEach((key, element) -> {
			keys.add(key);
			count.incrementAndGet();
		});
		
		assertEquals(2, count.get());
		assertEquals(List.of("a", "b"), keys);
	}
	
	@Test
	void toStringOnEmptyAndFilledMap() {
		assertEquals("{}", new BinaryMap().toString());
		
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		map.add("b", "text");
		assertEquals("{a: 1, b: text}", map.toString());
	}
	
	@Test
	void equalsWithNonMap() {
		BinaryMap map = new BinaryMap();
		
		assertNotEquals(null, map);
		assertNotEquals(map, new BinaryArray());
		assertNotEquals(map, Map.of());
	}
	
	@Test
	void equalsWithSameAndDifferentEntries() {
		BinaryMap first = new BinaryMap();
		first.add("a", 1);
		first.add("b", 2);
		
		BinaryMap reordered = new BinaryMap();
		reordered.add("b", 2);
		reordered.add("a", 1);
		
		BinaryMap differentValue = new BinaryMap();
		differentValue.add("a", 1);
		differentValue.add("b", 9);
		
		BinaryMap differentKeys = new BinaryMap();
		differentKeys.add("a", 1);
		differentKeys.add("c", 2);
		
		assertEquals(first, reordered);
		assertEquals(first.hashCode(), reordered.hashCode());
		assertNotEquals(first, differentValue);
		assertNotEquals(first, differentKeys);
	}
	
	@Test
	void addPrimitiveValueOverloads() {
		BinaryMap map = new BinaryMap();
		map.add("boolean", true);
		map.add("byte", (byte) 1);
		map.add("short", (short) 2);
		map.add("int", 3);
		map.add("long", 4L);
		map.add("float", 5.0F);
		map.add("double", 6.0);
		
		assertEquals(7, map.size());
		assertEquals(BinaryType.BOOLEAN, map.get("boolean").getType());
		assertEquals(BinaryType.BYTE, map.get("byte").getType());
		assertEquals(BinaryType.SHORT, map.get("short").getType());
		assertEquals(BinaryType.INTEGER, map.get("int").getType());
		assertEquals(BinaryType.LONG, map.get("long").getType());
		assertEquals(BinaryType.FLOAT, map.get("float").getType());
		assertEquals(BinaryType.DOUBLE, map.get("double").getType());
	}
	
	@Test
	void getAsValueOverloads() {
		BinaryMap map = new BinaryMap();
		map.add("boolean", true);
		map.add("byte", (byte) 1);
		map.add("short", (short) 2);
		map.add("int", 3);
		map.add("long", 4L);
		map.add("float", 5.0F);
		map.add("double", 6.0);
		map.add("string", "text");
		
		assertTrue(map.getAsBoolean("boolean"));
		assertEquals((byte) 1, map.getAsByte("byte"));
		assertEquals((short) 2, map.getAsShort("short"));
		assertEquals(3, map.getAsInteger("int"));
		assertEquals(4L, map.getAsLong("long"));
		assertEquals(5.0F, map.getAsFloat("float"));
		assertEquals(6.0, map.getAsDouble("double"));
		assertEquals("text", map.getAsString("string"));
		assertEquals(3, map.getAsNumber("int").intValue());
	}
	
	@Test
	void getAsBinaryPrimitiveReturnsPrimitive() {
		BinaryPrimitive element = new BinaryPrimitive(1);
		BinaryMap map = new BinaryMap();
		map.add("k", element);
		
		assertSame(element, map.getAsBinaryPrimitive("k"));
		assertEquals(BinaryType.INTEGER, map.getAsBinaryPrimitive("k").getType());
	}
	
	@Test
	void sizeAfterAddAndRemove() {
		BinaryMap map = new BinaryMap();
		assertEquals(0, map.size());
		
		map.add("a", 1);
		map.add("b", 2);
		map.add("c", 3);
		assertEquals(3, map.size());
		
		map.remove("a");
		assertEquals(2, map.size());
		
		map.clear();
		assertEquals(0, map.size());
	}
	
	@Test
	void clearEmptiesMap() {
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		
		map.clear();
		assertTrue(map.isEmpty());
		
		assertDoesNotThrow(map::clear);
		assertTrue(map.isEmpty());
	}
	
	@Test
	void addSameKeyTwiceKeepsSize() {
		BinaryMap map = new BinaryMap();
		map.add("k", 1);
		map.add("k", 2);
		
		assertEquals(1, map.size());
		assertEquals(2, map.getAsInteger("k"));
	}
	
	@Test
	void keySetContainsAllKeys() {
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		map.add("b", 2);
		
		assertEquals(Set.of("a", "b"), map.keySet());
	}
	
	@Test
	void valuesPreservesInsertionOrder() {
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		map.add("b", 2);
		map.add("c", 3);
		
		assertEquals(List.of(1, 2, 3), map.values().stream().map(BinaryElement::getAsInteger).toList());
	}
	
	@Test
	void entrySetContainsAllEntries() {
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		map.add("b", 2);
		
		Set<Map.Entry<String, BinaryElement>> entries = map.entrySet();
		
		assertEquals(2, entries.size());
		assertTrue(entries.contains(Map.entry("a", new BinaryPrimitive(1))));
		assertTrue(entries.contains(Map.entry("b", new BinaryPrimitive(2))));
	}
	
	@Test
	void getElementsReturnsCopy() {
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		
		Map<String, BinaryElement> elements = map.getElements();
		map.add("b", 2);
		
		assertEquals(1, elements.size());
		assertEquals(2, map.size());
	}
	
	@Test
	void getTypeIsMap() {
		BinaryMap map = new BinaryMap();
		
		assertEquals(BinaryType.MAP, map.getType());
		assertTrue(map.isBinaryMap());
		assertFalse(map.isBinaryStruct());
	}
	
	@Test
	void emptyStringKeyIsAllowed() {
		BinaryMap map = new BinaryMap();
		map.add("", 1);
		
		assertTrue(map.containsKey(""));
		assertEquals(1, map.getAsInteger(""));
	}
	
	@Test
	void mixedTypeValuesCoexist() {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, 1);
		
		BinaryMap map = new BinaryMap();
		map.add("boolean", true);
		map.add("int", 1);
		map.add("string", "text");
		map.add("null", (BinaryElement) null);
		map.add("array", new BinaryArray());
		map.add("struct", struct);
		map.add("map", new BinaryMap());
		
		assertEquals(BinaryType.BOOLEAN, map.get("boolean").getType());
		assertEquals(BinaryType.INTEGER, map.get("int").getType());
		assertEquals(BinaryType.STRING, map.get("string").getType());
		assertEquals(BinaryType.NULL, map.get("null").getType());
		assertEquals(BinaryType.LIST, map.get("array").getType());
		assertEquals(BinaryType.STRUCT, map.get("struct").getType());
		assertEquals(BinaryType.MAP, map.get("map").getType());
		
		assertTrue(map.getAsBoolean("boolean"));
		assertEquals("text", map.getAsString("string"));
	}
	
	@Test
	void nestedMapsAreAccessible() {
		BinaryMap inner = new BinaryMap();
		inner.add("value", 42);
		
		BinaryMap map = new BinaryMap();
		map.add("inner", inner);
		
		assertEquals(42, map.getAsBinaryMap("inner").getAsInteger("value"));
	}
	
	@Test
	void nestedArrayAndStructAreAccessible() {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, "structValue");
		
		BinaryMap map = new BinaryMap();
		map.add("array", new BinaryArray(new BinaryPrimitive(7)));
		map.add("struct", struct);
		
		assertEquals(7, map.getAsBinaryArray("array").getAsInteger(0));
		assertEquals("structValue", map.getAsBinaryStruct("struct").getAsString(0));
	}
	
	@Test
	void deeplyNestedStructure() {
		BinaryMap level4 = new BinaryMap();
		level4.add("value", "deepest");
		
		BinaryMap level3 = new BinaryMap();
		level3.add("next", level4);
		
		BinaryMap level2 = new BinaryMap();
		level2.add("next", level3);
		
		BinaryMap level1 = new BinaryMap();
		level1.add("next", level2);
		
		assertEquals("deepest", level1.getAsBinaryMap("next").getAsBinaryMap("next").getAsBinaryMap("next").getAsString("value"));
		
		assertThrows(BinaryTypeException.class, () -> level1.getAsBinaryArray("next"));
	}
	
	@Test
	void insertionOrderIsPreservedAcrossMutations() {
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		map.add("b", 2);
		map.add("c", 3);
		
		assertEquals("{a: 1, b: 2, c: 3}", map.toString());
		
		map.replace("a", new BinaryPrimitive(9));
		assertEquals("{a: 9, b: 2, c: 3}", map.toString());
		
		map.remove("a");
		map.add("a", 1);
		assertEquals("{b: 2, c: 3, a: 1}", map.toString());
		assertEquals(List.of(2, 3, 1), map.values().stream().map(BinaryElement::getAsInteger).toList());
	}
	
	@Test
	void mutationSequenceKeepsConsistency() {
		BinaryMap map = new BinaryMap();
		
		map.add("a", 1);
		assertEquals(1, map.size());
		assertTrue(map.containsKey("a"));
		assertTrue(map.containsValue(new BinaryPrimitive(1)));
		
		map.addAll(Map.of("b", new BinaryPrimitive(2)));
		assertEquals(2, map.size());
		assertEquals(2, map.getElements().size());
		
		map.replace("a", new BinaryPrimitive(9));
		assertFalse(map.containsValue(new BinaryPrimitive(1)));
		assertTrue(map.containsValue(new BinaryPrimitive(9)));
		
		map.remove("b");
		assertEquals(1, map.size());
		assertEquals("{a: 9}", map.toString());
		
		map.clear();
		assertTrue(map.isEmpty());
		assertEquals("{}", map.toString());
		assertTrue(map.getElements().isEmpty());
	}
	
	@Test
	void mapRoundTripThroughWriterAndReader() {
		BinaryMap inner = new BinaryMap();
		inner.add("nested", 1);
		
		BinaryMap original = new BinaryMap();
		original.add("int", 1);
		original.add("string", "text");
		original.add("array", new BinaryArray(new BinaryPrimitive(true)));
		original.add("map", inner);
		
		BinaryElement decoded = BinaryReader.fromByteArray(BinaryWriter.toByteArray(original));
		
		assertEquals(original, decoded);
		assertEquals(Set.of("int", "string", "array", "map"), decoded.getAsBinaryMap().keySet());
	}
	
	@Test
	void emptyMapRoundTrip() {
		BinaryMap original = new BinaryMap();
		
		BinaryElement decoded = BinaryReader.fromByteArray(BinaryWriter.toByteArray(original));
		
		assertInstanceOf(BinaryMap.class, decoded);
		assertEquals(original, decoded);
		assertTrue(decoded.getAsBinaryMap().isEmpty());
	}
	
	@Test
	void unicodeKeysRoundTrip() {
		BinaryMap original = new BinaryMap();
		original.add("schlüssel", 1);
		original.add("键", 2);
		
		BinaryElement decoded = BinaryReader.fromByteArray(BinaryWriter.toByteArray(original));
		
		BinaryMap decodedMap = decoded.getAsBinaryMap();
		assertEquals(1, decodedMap.getAsInteger("schlüssel"));
		assertEquals(2, decodedMap.getAsInteger("键"));
		assertEquals(original, decoded);
	}
}
