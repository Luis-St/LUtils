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

package net.luis.utils.io.codec.provider;

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.data.binary.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.*;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryTypeProvider}.<br>
 *
 * @author Luis-St
 */
class BinaryTypeProviderTest {
	
	private static final BinaryTypeProvider INSTANCE = BinaryTypeProvider.INSTANCE;
	
	private static BinaryStruct namedStruct(String name, BinaryElement value) {
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, name, value);
		return struct;
	}
	
	@Test
	void instanceIsSingleton() {
		assertNotNull(BinaryTypeProvider.INSTANCE);
		assertSame(BinaryTypeProvider.INSTANCE, BinaryTypeProvider.INSTANCE);
		assertTrue(Modifier.isFinal(BinaryTypeProvider.class.getModifiers()));
	}
	
	@Test
	void createMethodsWithNullExceptionConstructor() {
		assertThrows(NullPointerException.class, () -> INSTANCE.createNull(null));
		assertThrows(NullPointerException.class, () -> INSTANCE.createBoolean(true, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.createByte((byte) 1, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.createShort((short) 1, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.createInteger(1, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.createLong(1L, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.createFloat(1.0F, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.createDouble(1.0, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.createString("text", null));
		assertThrows(NullPointerException.class, () -> INSTANCE.createList(List.of(), null));
		assertThrows(NullPointerException.class, () -> INSTANCE.createMap(null));
		assertThrows(NullPointerException.class, () -> INSTANCE.createMap(Map.of(), null));
		assertThrows(NullPointerException.class, () -> INSTANCE.createStruct(1, null));
	}
	
	@Test
	void getMethodsWithNullExceptionConstructor() {
		BinaryPrimitive primitive = new BinaryPrimitive(1);
		BinaryMap map = new BinaryMap();
		FieldRef field = new FieldRef("name", Set.of(), 0);
		
		assertThrows(NullPointerException.class, () -> INSTANCE.isEmpty(primitive, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.isNull(primitive, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.getBoolean(primitive, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.getByte(primitive, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.getShort(primitive, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.getInteger(primitive, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.getLong(primitive, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.getFloat(primitive, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.getDouble(primitive, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.getString(primitive, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.getList(primitive, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.getMap(map, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.has(map, "key", null));
		assertThrows(NullPointerException.class, () -> INSTANCE.get(map, "key", null));
		assertThrows(NullPointerException.class, () -> INSTANCE.set(map, "key", primitive, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.merge(map, map, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.validateStruct(map, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.setField(map, field, primitive, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.hasField(map, field, null));
		assertThrows(NullPointerException.class, () -> INSTANCE.getField(map, field, null));
	}
	
	@Test
	void createStringWithNullValue() {
		EncoderException exception = assertThrows(EncoderException.class, () -> INSTANCE.createString(null, EncoderException::new));
		
		assertTrue(exception.getMessage().contains("not a valid string"));
	}
	
	@Test
	void createListWithNullValues() {
		EncoderException exception = assertThrows(EncoderException.class, () -> INSTANCE.createList(null, EncoderException::new));
		
		assertTrue(exception.getMessage().contains("not a valid list"));
	}
	
	@Test
	void createMapWithNullValues() {
		EncoderException exception = assertThrows(EncoderException.class, () -> INSTANCE.createMap(null, EncoderException::new));
		
		assertTrue(exception.getMessage().contains("not a valid map"));
	}
	
	@Test
	void createStructWithNegativeFieldCount() {
		assertThrows(IllegalArgumentException.class, () -> INSTANCE.createStruct(-1, EncoderException::new));
	}
	
	@Test
	void isEmptyWithNullValue() {
		assertThrows(DecoderException.class, () -> INSTANCE.isEmpty(null, DecoderException::new));
	}
	
	@Test
	void isNullWithNullValue() {
		assertThrows(DecoderException.class, () -> INSTANCE.isNull(null, DecoderException::new));
	}
	
	@Test
	void getBooleanWithNullValue() {
		DecoderException exception = assertThrows(DecoderException.class, () -> INSTANCE.getBoolean(null, DecoderException::new));
		
		assertTrue(exception.getMessage().contains("boolean"));
	}
	
	@Test
	void getBooleanWithNonPrimitive() {
		DecoderException exception = assertThrows(DecoderException.class, () -> INSTANCE.getBoolean(new BinaryArray(), DecoderException::new));
		
		assertTrue(exception.getMessage().contains("not a binary primitive"));
	}
	
	@Test
	void getBooleanWithNonBoolean() {
		DecoderException exception = assertThrows(DecoderException.class, () -> INSTANCE.getBoolean(new BinaryPrimitive(1), DecoderException::new));
		
		assertTrue(exception.getMessage().contains("not a binary boolean"));
	}
	
	@Test
	void getNumericValuesWithNullValue() {
		assertTrue(assertThrows(DecoderException.class, () -> INSTANCE.getByte(null, DecoderException::new)).getMessage().contains("byte"));
		assertTrue(assertThrows(DecoderException.class, () -> INSTANCE.getShort(null, DecoderException::new)).getMessage().contains("short"));
		assertTrue(assertThrows(DecoderException.class, () -> INSTANCE.getInteger(null, DecoderException::new)).getMessage().contains("integer"));
		assertTrue(assertThrows(DecoderException.class, () -> INSTANCE.getLong(null, DecoderException::new)).getMessage().contains("long"));
		assertTrue(assertThrows(DecoderException.class, () -> INSTANCE.getFloat(null, DecoderException::new)).getMessage().contains("float"));
		assertTrue(assertThrows(DecoderException.class, () -> INSTANCE.getDouble(null, DecoderException::new)).getMessage().contains("double"));
	}
	
	@Test
	void getNumericValuesWithNonNumber() {
		for (BinaryElement element : List.of(new BinaryPrimitive("text"), new BinaryPrimitive(true))) {
			assertThrows(DecoderException.class, () -> INSTANCE.getByte(element, DecoderException::new));
			assertThrows(DecoderException.class, () -> INSTANCE.getShort(element, DecoderException::new));
			assertThrows(DecoderException.class, () -> INSTANCE.getInteger(element, DecoderException::new));
			assertThrows(DecoderException.class, () -> INSTANCE.getLong(element, DecoderException::new));
			assertThrows(DecoderException.class, () -> INSTANCE.getFloat(element, DecoderException::new));
			assertThrows(DecoderException.class, () -> INSTANCE.getDouble(element, DecoderException::new));
		}
	}
	
	@Test
	void getStringWithNullValue() {
		assertThrows(DecoderException.class, () -> INSTANCE.getString(null, DecoderException::new));
	}
	
	@Test
	void getStringWithNonString() {
		DecoderException exception = assertThrows(DecoderException.class, () -> INSTANCE.getString(new BinaryPrimitive(1), DecoderException::new));
		
		assertTrue(exception.getMessage().contains("not a binary string"));
	}
	
	@Test
	void getListWithNullValue() {
		assertThrows(DecoderException.class, () -> INSTANCE.getList(null, DecoderException::new));
	}
	
	@Test
	void getListWithNonArray() {
		DecoderException exception = assertThrows(DecoderException.class, () -> INSTANCE.getList(new BinaryPrimitive(1), DecoderException::new));
		
		assertTrue(exception.getMessage().contains("not a binary list"));
	}
	
	@Test
	void getMapWithNullValue() {
		assertThrows(DecoderException.class, () -> INSTANCE.getMap(null, DecoderException::new));
	}
	
	@Test
	void getMapWithNonMap() {
		DecoderException exception = assertThrows(DecoderException.class, () -> INSTANCE.getMap(new BinaryArray(), DecoderException::new));
		
		assertTrue(exception.getMessage().contains("not a binary map"));
	}
	
	@Test
	void hasWithNullKey() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(DecoderException.class, () -> INSTANCE.has(map, null, DecoderException::new));
	}
	
	@Test
	void getWithNullKey() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(DecoderException.class, () -> INSTANCE.get(map, null, DecoderException::new));
	}
	
	@Test
	void setWithNullKey() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(EncoderException.class, () -> INSTANCE.set(map, null, new BinaryPrimitive(1), EncoderException::new));
	}
	
	@Test
	void setWithNullValue() {
		BinaryMap map = new BinaryMap();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> INSTANCE.set(map, "key", null, EncoderException::new));
		assertTrue(exception.getMessage().contains("not valid"));
	}
	
	@Test
	void hasOnNonStructNonMap() {
		assertThrows(DecoderException.class, () -> INSTANCE.has(null, "key", DecoderException::new));
		assertThrows(DecoderException.class, () -> INSTANCE.has(new BinaryArray(), "key", DecoderException::new));
	}
	
	@Test
	void hasOnStructWithUnknownName() {
		BinaryStruct decoded = BinaryReader.fromByteArray(BinaryWriter.toByteArray(namedStruct("name", new BinaryPrimitive(1)))).getAsBinaryStruct();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> INSTANCE.has(decoded, "name", DecoderException::new));
		assertTrue(exception.getMessage().contains("identified by their position"));
	}
	
	@Test
	void validateStructWithNullValue() {
		DecoderException exception = assertThrows(DecoderException.class, () -> INSTANCE.validateStruct(null, DecoderException::new));
		
		assertTrue(exception.getMessage().contains("not a valid struct"));
	}
	
	@Test
	void validateStructWithNonStructNonMap() {
		assertThrows(DecoderException.class, () -> INSTANCE.validateStruct(new BinaryArray(), DecoderException::new));
		assertThrows(DecoderException.class, () -> INSTANCE.validateStruct(new BinaryPrimitive(1), DecoderException::new));
		assertThrows(DecoderException.class, () -> INSTANCE.validateStruct(BinaryNull.INSTANCE, DecoderException::new));
	}
	
	@Test
	void setFieldWithNullField() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(NullPointerException.class, () -> INSTANCE.setField(map, null, new BinaryPrimitive(1), EncoderException::new));
	}
	
	@Test
	void hasFieldWithNullField() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(NullPointerException.class, () -> INSTANCE.hasField(map, null, DecoderException::new));
	}
	
	@Test
	void getFieldWithNullField() {
		BinaryMap map = new BinaryMap();
		
		assertThrows(NullPointerException.class, () -> INSTANCE.getField(map, null, DecoderException::new));
	}
	
	@Test
	void setFieldWithUnindexedFieldOnStruct() {
		BinaryStruct struct = new BinaryStruct(2);
		FieldRef field = new FieldRef("name");
		
		EncoderException exception = assertThrows(EncoderException.class, () -> INSTANCE.setField(struct, field, new BinaryPrimitive(1), EncoderException::new));
		assertTrue(exception.getMessage().contains("is not indexed"));
		assertTrue(exception.getMessage().contains("codec group"));
	}
	
	@Test
	void setFieldWithIndexOutOfBoundsOnStruct() {
		BinaryStruct struct = new BinaryStruct(2);
		FieldRef field = new FieldRef("name", Set.of(), 5);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> INSTANCE.setField(struct, field, new BinaryPrimitive(1), EncoderException::new));
		assertTrue(exception.getMessage().contains("out of bounds"));
		assertTrue(exception.getMessage().contains("5"));
		assertTrue(exception.getMessage().contains("2"));
	}
	
	@Test
	void setFieldWithIndexAtStructSizeBoundary() throws Exception {
		BinaryStruct struct = new BinaryStruct(3);
		FieldRef lastValid = new FieldRef("name", Set.of(), 2);
		FieldRef firstInvalid = new FieldRef("name", Set.of(), 3);
		
		INSTANCE.setField(struct, lastValid, new BinaryPrimitive(1), EncoderException::new);
		assertEquals(1, struct.getAsInteger(2));
		
		EncoderException exception = assertThrows(EncoderException.class, () -> INSTANCE.setField(struct, firstInvalid, new BinaryPrimitive(1), EncoderException::new));
		assertTrue(exception.getMessage().contains("index 3"));
		assertTrue(exception.getMessage().contains("size 3"));
		
		assertTrue(INSTANCE.hasField(struct, lastValid, DecoderException::new));
		assertThrows(DecoderException.class, () -> INSTANCE.hasField(struct, firstInvalid, DecoderException::new));
		
		assertNotNull(INSTANCE.getField(struct, lastValid, DecoderException::new));
		assertThrows(DecoderException.class, () -> INSTANCE.getField(struct, firstInvalid, DecoderException::new));
	}
	
	@Test
	void hasFieldAndGetFieldWithUnindexedFieldOnStruct() {
		BinaryStruct struct = new BinaryStruct(2);
		FieldRef field = new FieldRef("name");
		
		assertThrows(DecoderException.class, () -> INSTANCE.hasField(struct, field, DecoderException::new));
		assertThrows(DecoderException.class, () -> INSTANCE.getField(struct, field, DecoderException::new));
	}
	
	@Test
	void mergeIncompatibleTypes() {
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		
		assertTrue(assertThrows(EncoderException.class, () -> INSTANCE.merge(new BinaryArray(new BinaryPrimitive(1)), map, EncoderException::new)).getMessage().contains("Unable to merge"));
		
		BinaryStruct struct = new BinaryStruct(1);
		struct.set(0, 1);
		assertThrows(EncoderException.class, () -> INSTANCE.merge(struct, new BinaryArray(new BinaryPrimitive(1)), EncoderException::new));
		
		assertThrows(EncoderException.class, () -> INSTANCE.merge(new BinaryPrimitive(1), new BinaryPrimitive(2), EncoderException::new));
	}
	
	@Test
	void emptyReturnsSharedElement() {
		assertSame(INSTANCE.empty(), INSTANCE.empty());
		assertEquals(BinaryType.ABSENT, INSTANCE.empty().getType());
	}
	
	@Test
	void isEmptyWithEmptyElement() throws Exception {
		assertTrue(INSTANCE.isEmpty(INSTANCE.empty(), DecoderException::new));
	}
	
	@Test
	void isEmptyWithOtherElement() throws Exception {
		assertFalse(INSTANCE.isEmpty(BinaryAbsent.INSTANCE, DecoderException::new));
		assertFalse(INSTANCE.isEmpty(BinaryNull.INSTANCE, DecoderException::new));
		assertFalse(INSTANCE.isEmpty(new BinaryPrimitive(1), DecoderException::new));
		assertFalse(INSTANCE.isEmpty(new BinaryStruct(0), DecoderException::new));
	}
	
	@Test
	void isNullWithBinaryNull() throws Exception {
		assertTrue(INSTANCE.isNull(BinaryNull.INSTANCE, DecoderException::new));
	}
	
	@Test
	void isNullWithNonNullElement() throws Exception {
		assertFalse(INSTANCE.isNull(new BinaryPrimitive(1), DecoderException::new));
		assertFalse(INSTANCE.isNull(BinaryAbsent.INSTANCE, DecoderException::new));
		assertFalse(INSTANCE.isNull(INSTANCE.empty(), DecoderException::new));
	}
	
	@Test
	void createPrimitiveTypes() throws Exception {
		assertEquals(BinaryType.BOOLEAN, INSTANCE.createBoolean(true, EncoderException::new).getType());
		assertTrue(INSTANCE.createBoolean(true, EncoderException::new).getAsBoolean());
		
		assertEquals(BinaryType.BYTE, INSTANCE.createByte((byte) 1, EncoderException::new).getType());
		assertEquals(BinaryType.SHORT, INSTANCE.createShort((short) 2, EncoderException::new).getType());
		assertEquals(BinaryType.INTEGER, INSTANCE.createInteger(3, EncoderException::new).getType());
		assertEquals(BinaryType.LONG, INSTANCE.createLong(4L, EncoderException::new).getType());
		assertEquals(BinaryType.FLOAT, INSTANCE.createFloat(5.0F, EncoderException::new).getType());
		assertEquals(BinaryType.DOUBLE, INSTANCE.createDouble(6.0, EncoderException::new).getType());
		assertEquals(BinaryType.STRING, INSTANCE.createString("text", EncoderException::new).getType());
		assertEquals("text", INSTANCE.createString("text", EncoderException::new).getAsString());
	}
	
	@Test
	void createNullReturnsBinaryNull() throws Exception {
		assertSame(BinaryNull.INSTANCE, INSTANCE.createNull(EncoderException::new));
	}
	
	@Test
	void createListWithValues() throws Exception {
		BinaryElement list = INSTANCE.createList(List.of(new BinaryPrimitive(1), new BinaryPrimitive(2)), EncoderException::new);
		
		assertInstanceOf(BinaryArray.class, list);
		assertEquals(2, list.getAsBinaryArray().size());
		
		assertTrue(INSTANCE.createList(List.of(), EncoderException::new).getAsBinaryArray().isEmpty());
	}
	
	@Test
	void createMapWithoutValues() throws Exception {
		BinaryElement map = INSTANCE.createMap(EncoderException::new);
		
		assertInstanceOf(BinaryMap.class, map);
		assertTrue(map.getAsBinaryMap().isEmpty());
	}
	
	@Test
	void createMapWithValues() throws Exception {
		BinaryElement map = INSTANCE.createMap(Map.of("a", new BinaryPrimitive(1)), EncoderException::new);
		
		assertInstanceOf(BinaryMap.class, map);
		assertEquals(1, map.getAsBinaryMap().size());
		
		assertTrue(INSTANCE.createMap(Map.of(), EncoderException::new).getAsBinaryMap().isEmpty());
	}
	
	@Test
	void createStructWithZeroFieldCount() throws Exception {
		BinaryElement struct = INSTANCE.createStruct(0, EncoderException::new);
		
		assertInstanceOf(BinaryStruct.class, struct);
		assertEquals(0, struct.getAsBinaryStruct().size());
	}
	
	@Test
	void createStructWithFieldCount() throws Exception {
		BinaryStruct struct = INSTANCE.createStruct(3, EncoderException::new).getAsBinaryStruct();
		
		assertEquals(3, struct.size());
		assertEquals(0, struct.presentFields());
	}
	
	@Test
	void getBooleanWithBooleanPrimitive() throws Exception {
		assertTrue(INSTANCE.getBoolean(new BinaryPrimitive(true), DecoderException::new));
		assertFalse(INSTANCE.getBoolean(new BinaryPrimitive(false), DecoderException::new));
	}
	
	@Test
	void getNumericValuesWithMatchingPrimitives() throws Exception {
		assertEquals((byte) 1, INSTANCE.getByte(new BinaryPrimitive((byte) 1), DecoderException::new));
		assertEquals((short) 2, INSTANCE.getShort(new BinaryPrimitive((short) 2), DecoderException::new));
		assertEquals(3, INSTANCE.getInteger(new BinaryPrimitive(3), DecoderException::new));
		assertEquals(4L, INSTANCE.getLong(new BinaryPrimitive(4L), DecoderException::new));
		assertEquals(5.0F, INSTANCE.getFloat(new BinaryPrimitive(5.0F), DecoderException::new));
		assertEquals(6.0, INSTANCE.getDouble(new BinaryPrimitive(6.0), DecoderException::new));
	}
	
	@Test
	void getStringWithStringPrimitive() throws Exception {
		assertEquals("text", INSTANCE.getString(new BinaryPrimitive("text"), DecoderException::new));
		assertEquals("", INSTANCE.getString(new BinaryPrimitive(""), DecoderException::new));
	}
	
	@Test
	void getListWithBinaryArray() throws Exception {
		BinaryArray array = new BinaryArray(new BinaryPrimitive(1), new BinaryPrimitive(2));
		
		List<BinaryElement> elements = INSTANCE.getList(array, DecoderException::new);
		
		assertEquals(2, elements.size());
		assertEquals(1, elements.getFirst().getAsInteger());
		assertEquals(2, elements.get(1).getAsInteger());
		
		assertTrue(INSTANCE.getList(new BinaryArray(), DecoderException::new).isEmpty());
	}
	
	@Test
	void getMapWithBinaryMap() throws Exception {
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		
		Map<String, BinaryElement> elements = INSTANCE.getMap(map, DecoderException::new);
		
		assertEquals(1, elements.size());
		assertEquals(1, elements.get("a").getAsInteger());
		
		elements.put("b", new BinaryPrimitive(2));
		assertEquals(1, map.size());
		
		assertTrue(INSTANCE.getMap(new BinaryMap(), DecoderException::new).isEmpty());
	}
	
	@Test
	void hasOnMapWithPresentAndMissingKey() throws Exception {
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		
		assertTrue(INSTANCE.has(map, "a", DecoderException::new));
		assertFalse(INSTANCE.has(map, "missing", DecoderException::new));
	}
	
	@Test
	void hasOnStructWithPresentAndAbsentField() throws Exception {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, "a", 1);
		struct.set(1, "b", 2);
		struct.remove(1);
		
		assertTrue(INSTANCE.has(struct, "a", DecoderException::new));
		assertFalse(INSTANCE.has(struct, "b", DecoderException::new));
	}
	
	@Test
	void getOnMapWithPresentAndMissingKey() throws Exception {
		BinaryMap map = new BinaryMap();
		map.add("a", 1);
		
		assertEquals(1, INSTANCE.get(map, "a", DecoderException::new).getAsInteger());
		assertNull(INSTANCE.get(map, "missing", DecoderException::new));
	}
	
	@Test
	void getOnStructWithPresentAndAbsentField() throws Exception {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, "a", 1);
		struct.set(1, "b", 2);
		struct.remove(1);
		
		assertEquals(1, INSTANCE.get(struct, "a", DecoderException::new).getAsInteger());
		assertNull(INSTANCE.get(struct, "b", DecoderException::new));
	}
	
	@Test
	void setOnMapStoresValue() throws Exception {
		BinaryMap map = new BinaryMap();
		
		INSTANCE.set(map, "a", new BinaryPrimitive(1), EncoderException::new);
		
		assertEquals(1, INSTANCE.get(map, "a", DecoderException::new).getAsInteger());
	}
	
	@Test
	void setOnStructStoresValueAndName() throws Exception {
		BinaryStruct struct = namedStruct("a", new BinaryPrimitive(1));
		
		INSTANCE.set(struct, "a", new BinaryPrimitive(9), EncoderException::new);
		
		assertEquals(9, struct.getAsInteger(0));
		assertEquals(Optional.of("a"), struct.getName(0));
	}
	
	@Test
	void validateStructWithBinaryStruct() {
		assertDoesNotThrow(() -> INSTANCE.validateStruct(new BinaryStruct(1), DecoderException::new));
	}
	
	@Test
	void validateStructWithBinaryMap() {
		assertDoesNotThrow(() -> INSTANCE.validateStruct(new BinaryMap(), DecoderException::new));
	}
	
	@Test
	void setFieldOnStructUsesIndex() throws Exception {
		BinaryStruct struct = new BinaryStruct(3);
		FieldRef field = new FieldRef("name", Set.of(), 1);
		
		INSTANCE.setField(struct, field, new BinaryPrimitive(1), EncoderException::new);
		
		assertEquals(1, struct.getAsInteger(1));
		assertFalse(struct.has(0));
		assertFalse(struct.has(2));
		assertEquals(Optional.of("name"), struct.getName(1));
	}
	
	@Test
	void setFieldOnMapUsesName() throws Exception {
		BinaryMap map = new BinaryMap();
		FieldRef field = new FieldRef("name", Set.of("alias"), 2);
		
		INSTANCE.setField(map, field, new BinaryPrimitive(1), EncoderException::new);
		
		assertTrue(map.containsKey("name"));
		assertFalse(map.containsKey("alias"));
		assertEquals(1, map.getAsInteger("name"));
	}
	
	@Test
	void setFieldWithNullValue() {
		BinaryStruct struct = new BinaryStruct(1);
		FieldRef field = new FieldRef("name", Set.of(), 0);
		
		assertThrows(EncoderException.class, () -> INSTANCE.setField(struct, field, null, EncoderException::new));
		assertFalse(struct.has(0));
		
		BinaryMap map = new BinaryMap();
		assertThrows(EncoderException.class, () -> INSTANCE.setField(map, field, null, EncoderException::new));
	}
	
	@Test
	void hasFieldOnStructWithIndexedField() throws Exception {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, 1);
		
		assertTrue(INSTANCE.hasField(struct, new FieldRef("name", Set.of(), 0), DecoderException::new));
		assertFalse(INSTANCE.hasField(struct, new FieldRef("name", Set.of(), 1), DecoderException::new));
	}
	
	@Test
	void hasFieldOnMapDelegatesToDefault() throws Exception {
		FieldRef field = new FieldRef("name", Set.of("alias"));
		
		BinaryMap byName = new BinaryMap();
		byName.add("name", 1);
		assertTrue(INSTANCE.hasField(byName, field, DecoderException::new));
		
		BinaryMap byAlias = new BinaryMap();
		byAlias.add("alias", 1);
		assertTrue(INSTANCE.hasField(byAlias, field, DecoderException::new));
		
		BinaryMap missing = new BinaryMap();
		missing.add("other", 1);
		assertFalse(INSTANCE.hasField(missing, field, DecoderException::new));
	}
	
	@Test
	void getFieldOnStructWithIndexedField() throws Exception {
		BinaryStruct struct = new BinaryStruct(2);
		struct.set(0, 1);
		
		assertEquals(1, INSTANCE.getField(struct, new FieldRef("name", Set.of(), 0), DecoderException::new).getAsInteger());
		assertNull(INSTANCE.getField(struct, new FieldRef("name", Set.of(), 1), DecoderException::new));
	}
	
	@Test
	void getFieldOnMapDelegatesToDefault() throws Exception {
		FieldRef field = new FieldRef("name", Set.of("alias"));
		
		BinaryMap byName = new BinaryMap();
		byName.add("name", 1);
		assertEquals(1, INSTANCE.getField(byName, field, DecoderException::new).getAsInteger());
		
		BinaryMap byAlias = new BinaryMap();
		byAlias.add("alias", 2);
		assertEquals(2, INSTANCE.getField(byAlias, field, DecoderException::new).getAsInteger());
		
		BinaryMap missing = new BinaryMap();
		missing.add("other", 3);
		assertNull(INSTANCE.getField(missing, field, DecoderException::new));
	}
	
	@Test
	void mergeWithNullCurrent() throws Exception {
		BinaryPrimitive value = new BinaryPrimitive(1);
		
		assertSame(value, INSTANCE.merge(null, value, EncoderException::new));
	}
	
	@Test
	void mergeWithNullValue() throws Exception {
		BinaryPrimitive current = new BinaryPrimitive(1);
		
		assertSame(current, INSTANCE.merge(current, null, EncoderException::new));
	}
	
	@Test
	void mergeWithEmptyCurrent() throws Exception {
		BinaryPrimitive value = new BinaryPrimitive(1);
		
		assertSame(value, INSTANCE.merge(INSTANCE.empty(), value, EncoderException::new));
	}
	
	@Test
	void mergeWithNullElementCurrent() throws Exception {
		BinaryPrimitive value = new BinaryPrimitive(1);
		
		assertSame(value, INSTANCE.merge(BinaryNull.INSTANCE, value, EncoderException::new));
	}
	
	@Test
	void mergeWithAbsentCurrent() throws Exception {
		BinaryPrimitive value = new BinaryPrimitive(1);
		
		assertSame(value, INSTANCE.merge(BinaryAbsent.INSTANCE, value, EncoderException::new));
	}
	
	@Test
	void mergeWithEmptyValue() throws Exception {
		BinaryPrimitive current = new BinaryPrimitive(1);
		
		assertSame(current, INSTANCE.merge(current, INSTANCE.empty(), EncoderException::new));
	}
	
	@Test
	void mergeWithNullElementValue() throws Exception {
		BinaryPrimitive current = new BinaryPrimitive(1);
		
		assertSame(current, INSTANCE.merge(current, BinaryNull.INSTANCE, EncoderException::new));
	}
	
	@Test
	void mergeWithAbsentValue() throws Exception {
		BinaryPrimitive current = new BinaryPrimitive(1);
		
		assertSame(current, INSTANCE.merge(current, BinaryAbsent.INSTANCE, EncoderException::new));
	}
	
	@Test
	void mergeTwoArrays() throws Exception {
		BinaryArray current = new BinaryArray(new BinaryPrimitive(1), new BinaryPrimitive(2));
		BinaryArray value = new BinaryArray(new BinaryPrimitive(3));
		
		BinaryElement merged = INSTANCE.merge(current, value, EncoderException::new);
		
		assertSame(current, merged);
		assertEquals(List.of(1, 2, 3), current.stream().map(BinaryElement::getAsInteger).toList());
	}
	
	@Test
	void mergeTwoMaps() throws Exception {
		BinaryMap current = new BinaryMap();
		current.add("a", 1);
		
		BinaryMap addition = new BinaryMap();
		addition.add("b", 2);
		
		assertSame(current, INSTANCE.merge(current, addition, EncoderException::new));
		assertEquals(2, current.size());
		assertEquals(2, current.getAsInteger("b"));
		
		BinaryMap collision = new BinaryMap();
		collision.add("a", 9);
		
		INSTANCE.merge(current, collision, EncoderException::new);
		assertEquals(9, current.getAsInteger("a"));
	}
	
	@Test
	void mergeTwoStructs() throws Exception {
		BinaryStruct current = new BinaryStruct(2);
		current.set(0, 1);
		
		BinaryStruct value = new BinaryStruct(2);
		value.set(1, 2);
		
		BinaryElement merged = INSTANCE.merge(current, value, EncoderException::new);
		
		assertNotSame(current, merged);
		BinaryStruct mergedStruct = merged.getAsBinaryStruct();
		assertEquals(2, mergedStruct.size());
		assertEquals(1, mergedStruct.getAsInteger(0));
		assertEquals(2, mergedStruct.getAsInteger(1));
	}
	
	@Test
	void mergeArrayWithMapThrows() {
		assertThrows(EncoderException.class, () -> INSTANCE.merge(new BinaryArray(new BinaryPrimitive(1)), new BinaryMap(), EncoderException::new));
	}
	
	@Test
	void createAndGetRoundTripForEachScalar() throws Exception {
		assertTrue(INSTANCE.getBoolean(INSTANCE.createBoolean(true, EncoderException::new), DecoderException::new));
		assertEquals((byte) 1, INSTANCE.getByte(INSTANCE.createByte((byte) 1, EncoderException::new), DecoderException::new));
		assertEquals((short) 2, INSTANCE.getShort(INSTANCE.createShort((short) 2, EncoderException::new), DecoderException::new));
		assertEquals(3, INSTANCE.getInteger(INSTANCE.createInteger(3, EncoderException::new), DecoderException::new));
		assertEquals(4L, INSTANCE.getLong(INSTANCE.createLong(4L, EncoderException::new), DecoderException::new));
		assertEquals(5.0F, INSTANCE.getFloat(INSTANCE.createFloat(5.0F, EncoderException::new), DecoderException::new));
		assertEquals(6.0, INSTANCE.getDouble(INSTANCE.createDouble(6.0, EncoderException::new), DecoderException::new));
		assertEquals("text", INSTANCE.getString(INSTANCE.createString("text", EncoderException::new), DecoderException::new));
	}
	
	@Test
	void numericGettersNarrowAcrossTypes() throws Exception {
		BinaryPrimitive value = new BinaryPrimitive(300);
		
		assertEquals((byte) 44, INSTANCE.getByte(value, DecoderException::new));
		assertEquals((short) 300, INSTANCE.getShort(value, DecoderException::new));
		assertEquals(300L, INSTANCE.getLong(value, DecoderException::new));
		assertEquals(300.0F, INSTANCE.getFloat(value, DecoderException::new));
		assertEquals(300.0, INSTANCE.getDouble(value, DecoderException::new));
	}
	
	@Test
	void getListReturnsUnmodifiableElements() throws Exception {
		List<BinaryElement> elements = INSTANCE.getList(new BinaryArray(new BinaryPrimitive(1)), DecoderException::new);
		
		assertThrows(UnsupportedOperationException.class, () -> elements.add(BinaryNull.INSTANCE));
	}
	
	@Test
	void getMapReturnsMutableCopy() throws Exception {
		BinaryMap source = new BinaryMap();
		source.add("a", 1);
		
		Map<String, BinaryElement> copy = INSTANCE.getMap(source, DecoderException::new);
		copy.put("b", new BinaryPrimitive(2));
		
		assertEquals(2, copy.size());
		assertEquals(1, source.size());
	}
	
	@Test
	void createStructFieldsStartAbsent() throws Exception {
		BinaryStruct struct = INSTANCE.createStruct(3, EncoderException::new).getAsBinaryStruct();
		
		assertEquals(0, struct.presentFields());
		for (int i = 0; i < struct.size(); i++) {
			assertSame(BinaryAbsent.INSTANCE, struct.get(i));
		}
	}
	
	@Test
	void mergeStructWithDifferentSizes() throws Exception {
		BinaryStruct small = new BinaryStruct(2);
		small.set(0, 1);
		BinaryStruct large = new BinaryStruct(4);
		large.set(3, 4);
		
		BinaryStruct merged = INSTANCE.merge(small, large, EncoderException::new).getAsBinaryStruct();
		assertEquals(4, merged.size());
		assertEquals(1, merged.getAsInteger(0));
		assertEquals(4, merged.getAsInteger(3));
		assertFalse(merged.has(1));
		assertFalse(merged.has(2));
		
		BinaryStruct reversed = INSTANCE.merge(large, small, EncoderException::new).getAsBinaryStruct();
		assertEquals(4, reversed.size());
		assertEquals(1, reversed.getAsInteger(0));
		assertEquals(4, reversed.getAsInteger(3));
	}
	
	@Test
	void mergeStructPrefersValueFields() throws Exception {
		BinaryStruct current = new BinaryStruct(3);
		current.set(0, 1);
		current.set(1, 2);
		
		BinaryStruct value = new BinaryStruct(3);
		value.set(1, 9);
		
		BinaryStruct merged = INSTANCE.merge(current, value, EncoderException::new).getAsBinaryStruct();
		
		assertEquals(1, merged.getAsInteger(0));
		assertEquals(9, merged.getAsInteger(1));
		assertFalse(merged.has(2));
	}
	
	@Test
	void mergeStructResolvesFieldNames() throws Exception {
		BinaryStruct current = new BinaryStruct(3);
		current.set(0, "currentOnly", 1);
		current.set(1, "currentBoth", 2);
		
		BinaryStruct value = new BinaryStruct(3);
		value.set(1, "valueBoth", 9);
		
		BinaryStruct merged = INSTANCE.merge(current, value, EncoderException::new).getAsBinaryStruct();
		
		assertEquals(Optional.of("currentOnly"), merged.getName(0));
		assertEquals(Optional.of("valueBoth"), merged.getName(1));
		assertTrue(merged.getName(2).isEmpty());
	}
	
	@Test
	void mergeStructNameResolutionBeyondSourceSizes() throws Exception {
		BinaryStruct smallCurrent = new BinaryStruct(2);
		smallCurrent.set(0, "c0", 1);
		smallCurrent.set(1, "c1", 2);
		
		BinaryStruct largeValue = new BinaryStruct(4);
		largeValue.set(0, "v0", 10);
		largeValue.set(1, "v1", 11);
		largeValue.set(2, "v2", 12);
		largeValue.set(3, "v3", 13);
		
		BinaryStruct merged = INSTANCE.merge(smallCurrent, largeValue, EncoderException::new).getAsBinaryStruct();
		
		assertEquals(4, merged.getNames().size());
		assertEquals(Optional.of("v0"), merged.getName(0));
		assertEquals(Optional.of("v1"), merged.getName(1));
		assertEquals(Optional.of("v2"), merged.getName(2));
		assertEquals(Optional.of("v3"), merged.getName(3));
		
		BinaryStruct largeCurrent = new BinaryStruct(4);
		largeCurrent.set(0, "c0", 1);
		largeCurrent.set(1, "c1", 2);
		largeCurrent.set(2, "c2", 3);
		largeCurrent.set(3, "c3", 4);
		
		BinaryStruct smallValue = new BinaryStruct(2);
		smallValue.set(0, "v0", 10);
		smallValue.set(1, "v1", 11);
		
		BinaryStruct reversed = INSTANCE.merge(largeCurrent, smallValue, EncoderException::new).getAsBinaryStruct();
		
		assertEquals(4, reversed.getNames().size());
		assertEquals(Optional.of("v0"), reversed.getName(0));
		assertEquals(Optional.of("v1"), reversed.getName(1));
		assertEquals(Optional.of("c2"), reversed.getName(2));
		assertEquals(Optional.of("c3"), reversed.getName(3));
		assertEquals(3, reversed.getAsInteger(2));
		assertEquals(4, reversed.getAsInteger(3));
	}
	
	@Test
	void encodeRecordIntoStructThroughCodecGroup() throws Exception {
		CodecGroup<TestObject> group = createGroup();
		
		BinaryElement encoded = group.encode(INSTANCE, INSTANCE.empty(), new TestObject("test", 42, true));
		
		BinaryStruct struct = encoded.getAsBinaryStruct();
		assertEquals(3, struct.size());
		assertEquals("test", struct.getAsString(0));
		assertEquals(42, struct.getAsInteger(1));
		assertTrue(struct.getAsBoolean(2));
		assertEquals(Optional.of("name"), struct.getName(0));
		assertEquals(Optional.of("value"), struct.getName(1));
		assertEquals(Optional.of("flag"), struct.getName(2));
	}
	
	@Test
	void decodeRecordFromStructThroughCodecGroup() throws Exception {
		CodecGroup<TestObject> group = createGroup();
		TestObject original = new TestObject("test", 42, true);
		
		BinaryElement encoded = group.encode(INSTANCE, INSTANCE.empty(), original);
		
		assertEquals(original, group.decode(INSTANCE, encoded, encoded));
	}
	
	@Test
	void codecRoundTripThroughWriterAndReader() throws Exception {
		CodecGroup<TestObject> group = createGroup();
		TestObject original = new TestObject("test", 42, true);
		
		BinaryElement encoded = group.encode(INSTANCE, INSTANCE.empty(), original);
		BinaryElement decoded = BinaryReader.fromByteArray(BinaryWriter.toByteArray(encoded));
		
		assertEquals(original, group.decode(INSTANCE, decoded, decoded));
	}
	
	@Test
	void decodeByNameFailsForReadStruct() {
		BinaryStruct decoded = BinaryReader.fromByteArray(BinaryWriter.toByteArray(namedStruct("name", new BinaryPrimitive(1)))).getAsBinaryStruct();
		
		assertThrows(DecoderException.class, () -> INSTANCE.has(decoded, "name", DecoderException::new));
		assertThrows(DecoderException.class, () -> INSTANCE.get(decoded, "name", DecoderException::new));
	}
	
	@Test
	void optionalAndNullableComponentsUseAbsentAndNull() throws Exception {
		BinaryStruct struct = new BinaryStruct(2);
		FieldRef nullable = new FieldRef("nullable", Set.of(), 0);
		
		INSTANCE.setField(struct, nullable, INSTANCE.createNull(EncoderException::new), EncoderException::new);
		
		assertSame(BinaryNull.INSTANCE, struct.get(0));
		assertSame(BinaryAbsent.INSTANCE, struct.get(1));
		
		BinaryStruct decoded = BinaryReader.fromByteArray(BinaryWriter.toByteArray(struct)).getAsBinaryStruct();
		assertEquals(BinaryType.NULL, decoded.get(0).getType());
		assertEquals(BinaryType.ABSENT, decoded.get(1).getType());
		assertTrue(decoded.has(0));
		assertFalse(decoded.has(1));
	}
	
	@Test
	void mergeChainDuringGroupEncoding() throws Exception {
		CodecGroup<TestObject> group = createGroup();
		TestObject original = new TestObject("test", 42, true);
		
		BinaryElement fromEmpty = group.encode(INSTANCE, INSTANCE.empty(), original);
		assertEquals(3, fromEmpty.getAsBinaryStruct().size());
		
		BinaryStruct existing = new BinaryStruct(4);
		existing.set(3, "extra", 7);
		
		BinaryElement merged = group.encode(INSTANCE, existing, original);
		BinaryStruct mergedStruct = merged.getAsBinaryStruct();
		
		assertEquals(4, mergedStruct.size());
		assertEquals("test", mergedStruct.getAsString(0));
		assertEquals(7, mergedStruct.getAsInteger(3));
	}
	
	@Test
	void deeplyNestedStructureThroughProvider() throws Exception {
		BinaryMap level4 = new BinaryMap();
		level4.add("value", "deepest");
		
		BinaryArray level3 = new BinaryArray(level4);
		
		BinaryStruct level2 = new BinaryStruct(1);
		level2.set(0, "list", level3);
		
		BinaryMap level1 = new BinaryMap();
		level1.add("struct", level2);
		
		BinaryElement structElement = INSTANCE.getField(level1, new FieldRef("struct"), DecoderException::new);
		assertNotNull(structElement);
		
		BinaryElement listElement = INSTANCE.getField(structElement, new FieldRef("list", Set.of(), 0), DecoderException::new);
		assertNotNull(listElement);
		
		List<BinaryElement> elements = INSTANCE.getList(listElement, DecoderException::new);
		assertEquals(1, elements.size());
		assertEquals("deepest", INSTANCE.getString(INSTANCE.get(elements.getFirst(), "value", DecoderException::new), DecoderException::new));
		
		assertThrows(DecoderException.class, () -> INSTANCE.getList(level1, DecoderException::new));
	}
	
	private static CodecGroup<TestObject> createGroup() {
		List<FieldCodec<?, TestObject>> codecs = List.of(
			STRING.fieldOf("name", TestObject::name),
			INTEGER.fieldOf("value", TestObject::value),
			BOOLEAN.fieldOf("flag", TestObject::flag)
		);
		return new CodecGroup<>(codecs, components -> new TestObject((String) components.getFirst(), (Integer) components.get(1), (Boolean) components.get(2)));
	}
	
	private record TestObject(String name, int value, boolean flag) {}
}
