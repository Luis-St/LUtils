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

import net.luis.utils.io.data.toml.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TomlTypeProvider}.<br>
 *
 * @author Luis-St
 */
class TomlTypeProviderTest {
	
	@Test
	void emptyReturnsTomlElement() {
		TomlElement element = TomlTypeProvider.INSTANCE.empty();
		assertFalse(element.isTomlNull());
		assertFalse(element.isTomlValue());
		assertFalse(element.isTomlArray());
		assertFalse(element.isTomlTable());
	}
	
	@Test
	void createNullReturnsTomlNull() {
		assertEquals(TomlNull.INSTANCE, TomlTypeProvider.INSTANCE.createNull(RuntimeException::new));
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new TomlValue(true), TomlTypeProvider.INSTANCE.createBoolean(true, RuntimeException::new));
		assertEquals(new TomlValue((byte) 42), TomlTypeProvider.INSTANCE.createByte((byte) 42, RuntimeException::new));
		assertEquals(new TomlValue((short) 42), TomlTypeProvider.INSTANCE.createShort((short) 42, RuntimeException::new));
		assertEquals(new TomlValue(42), TomlTypeProvider.INSTANCE.createInteger(42, RuntimeException::new));
		assertEquals(new TomlValue(42L), TomlTypeProvider.INSTANCE.createLong(42L, RuntimeException::new));
		assertEquals(new TomlValue(42.5f), TomlTypeProvider.INSTANCE.createFloat(42.5f, RuntimeException::new));
		assertEquals(new TomlValue(42.5), TomlTypeProvider.INSTANCE.createDouble(42.5, RuntimeException::new));
		assertEquals(new TomlValue("test"), TomlTypeProvider.INSTANCE.createString("test", RuntimeException::new));
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.createString(null, RuntimeException::new));
	}
	
	@Test
	void createCollectionTypes() {
		TomlElement element1 = new TomlValue("a");
		TomlElement element2 = new TomlValue("b");
		
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.createList(null, RuntimeException::new));
		
		TomlArray emptyArray = new TomlArray();
		assertEquals(emptyArray, TomlTypeProvider.INSTANCE.createList(List.of(), RuntimeException::new));
		
		TomlArray arrayWithElements = new TomlArray(List.of(element1, element2));
		assertEquals(arrayWithElements, TomlTypeProvider.INSTANCE.createList(List.of(element1, element2), RuntimeException::new));
		
		TomlTable emptyTable = new TomlTable();
		assertEquals(emptyTable, TomlTypeProvider.INSTANCE.createMap(RuntimeException::new));
		assertEquals(emptyTable, TomlTypeProvider.INSTANCE.createMap(Map.of(), RuntimeException::new));
		
		TomlTable tableWithElements = new TomlTable(Map.of("key1", element1, "key2", element2));
		assertEquals(tableWithElements, TomlTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2), RuntimeException::new));
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.isEmpty(null, RuntimeException::new));
		assertFalse(TomlTypeProvider.INSTANCE.isEmpty(new TomlArray(), RuntimeException::new));
		assertFalse(TomlTypeProvider.INSTANCE.isEmpty(new TomlValue(1), RuntimeException::new));
		assertFalse(TomlTypeProvider.INSTANCE.isEmpty(new TomlTable(), RuntimeException::new));
		assertFalse(TomlTypeProvider.INSTANCE.isEmpty(TomlNull.INSTANCE, RuntimeException::new));
	}
	
	@Test
	void isNullValidation() {
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.isNull(null, RuntimeException::new));
		
		assertTrue(TomlTypeProvider.INSTANCE.isNull(TomlNull.INSTANCE, RuntimeException::new));
		
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlArray(), RuntimeException::new));
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlTable(), RuntimeException::new));
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlValue(1), RuntimeException::new));
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlValue(true), RuntimeException::new));
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlValue("test"), RuntimeException::new));
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getBoolean(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getByte(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getShort(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getInteger(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getLong(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getFloat(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getDouble(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getString(null, RuntimeException::new));
		
		TomlArray wrongType = new TomlArray();
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getBoolean(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getByte(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getShort(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getInteger(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getLong(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getFloat(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getDouble(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getString(wrongType, RuntimeException::new));
		
		TomlValue invalidValue = new TomlValue("not-a-number");
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getBoolean(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getByte(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getShort(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getInteger(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getLong(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getFloat(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getDouble(invalidValue, RuntimeException::new));
		
		assertTrue(TomlTypeProvider.INSTANCE.getBoolean(new TomlValue(true), RuntimeException::new));
		assertEquals((byte) 42, TomlTypeProvider.INSTANCE.getByte(new TomlValue((byte) 42), RuntimeException::new));
		assertEquals((short) 42, TomlTypeProvider.INSTANCE.getShort(new TomlValue((short) 42), RuntimeException::new));
		assertEquals(42, TomlTypeProvider.INSTANCE.getInteger(new TomlValue(42), RuntimeException::new));
		assertEquals(42L, TomlTypeProvider.INSTANCE.getLong(new TomlValue(42L), RuntimeException::new));
		assertEquals(42.5f, TomlTypeProvider.INSTANCE.getFloat(new TomlValue(42.5f), RuntimeException::new));
		assertEquals(42.5, TomlTypeProvider.INSTANCE.getDouble(new TomlValue(42.5), RuntimeException::new));
		assertEquals("test", TomlTypeProvider.INSTANCE.getString(new TomlValue("test"), RuntimeException::new));
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getList(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getMap(null, RuntimeException::new));
		
		TomlValue wrongType = new TomlValue(1);
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getList(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.getMap(wrongType, RuntimeException::new));
		
		TomlArray emptyArray = new TomlArray();
		assertTrue(TomlTypeProvider.INSTANCE.getList(emptyArray, RuntimeException::new).isEmpty());
		
		TomlArray arrayWithElements = new TomlArray(List.of(new TomlValue("a"), new TomlValue("b")));
		List<TomlElement> listResult = TomlTypeProvider.INSTANCE.getList(arrayWithElements, RuntimeException::new);
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.getFirst().getAsTomlValue().getAsString());
		
		TomlTable emptyTable = new TomlTable();
		assertTrue(TomlTypeProvider.INSTANCE.getMap(emptyTable, RuntimeException::new).isEmpty());
		
		TomlTable tableWithElements = new TomlTable(Map.of("key", new TomlValue("value")));
		Map<String, TomlElement> mapResult = TomlTypeProvider.INSTANCE.getMap(tableWithElements, RuntimeException::new);
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsTomlValue().getAsString());
	}
	
	@Test
	void mapOperations() {
		TomlTable tomlTable = new TomlTable();
		TomlElement testValue = new TomlValue("test");
		
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.has(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.has(tomlTable, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.get(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.get(tomlTable, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.set(null, "key", testValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.set(tomlTable, null, testValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.set(tomlTable, "key", null, RuntimeException::new));
		
		TomlArray wrongType = new TomlArray();
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.has(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.get(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.set(wrongType, "key", testValue, RuntimeException::new));
		
		assertFalse(TomlTypeProvider.INSTANCE.has(tomlTable, "key", RuntimeException::new));
		
		TomlTypeProvider.INSTANCE.set(tomlTable, "key", testValue, RuntimeException::new);
		assertTrue(TomlTypeProvider.INSTANCE.has(tomlTable, "key", RuntimeException::new));
		assertEquals(testValue, TomlTypeProvider.INSTANCE.get(tomlTable, "key", RuntimeException::new));
	}
	
	@Test
	void mergeOperations() {
		assertEquals(TomlNull.INSTANCE, TomlTypeProvider.INSTANCE.merge(null, TomlNull.INSTANCE, RuntimeException::new));
		assertEquals(TomlNull.INSTANCE, TomlTypeProvider.INSTANCE.merge(TomlNull.INSTANCE, null, RuntimeException::new));
		
		TomlElement primitive = new TomlValue(1);
		TomlArray array1 = new TomlArray(List.of(new TomlValue("a")));
		TomlArray array2 = new TomlArray(List.of(new TomlValue("b")));
		TomlTable table1 = new TomlTable(Map.of("key1", new TomlValue("value1")));
		TomlTable table2 = new TomlTable(Map.of("key2", new TomlValue("value2")));
		
		assertEquals(primitive, TomlTypeProvider.INSTANCE.merge(TomlTypeProvider.INSTANCE.empty(), primitive, RuntimeException::new));
		assertEquals(array1, TomlTypeProvider.INSTANCE.merge(TomlTypeProvider.INSTANCE.empty(), array1, RuntimeException::new));
		assertEquals(table1, TomlTypeProvider.INSTANCE.merge(TomlTypeProvider.INSTANCE.empty(), table1, RuntimeException::new));
		
		assertEquals(TomlNull.INSTANCE, TomlTypeProvider.INSTANCE.merge(TomlTypeProvider.INSTANCE.empty(), TomlNull.INSTANCE, RuntimeException::new));
		
		assertEquals(primitive, TomlTypeProvider.INSTANCE.merge(primitive, TomlNull.INSTANCE, RuntimeException::new));
		assertEquals(primitive, TomlTypeProvider.INSTANCE.merge(TomlNull.INSTANCE, primitive, RuntimeException::new));
		
		assertEquals(array1, TomlTypeProvider.INSTANCE.merge(array1, TomlNull.INSTANCE, RuntimeException::new));
		assertEquals(array1, TomlTypeProvider.INSTANCE.merge(TomlNull.INSTANCE, array1, RuntimeException::new));
		
		assertEquals(table1, TomlTypeProvider.INSTANCE.merge(table1, TomlNull.INSTANCE, RuntimeException::new));
		assertEquals(table1, TomlTypeProvider.INSTANCE.merge(TomlNull.INSTANCE, table1, RuntimeException::new));
		
		TomlArray mergedArray = TomlTypeProvider.INSTANCE.merge(array1, array2, RuntimeException::new).getAsTomlArray();
		assertEquals(2, mergedArray.size());
		assertEquals("a", mergedArray.get(0).getAsTomlValue().getAsString());
		assertEquals("b", mergedArray.get(1).getAsTomlValue().getAsString());
		
		TomlTable mergedTable = TomlTypeProvider.INSTANCE.merge(table1, table2, RuntimeException::new).getAsTomlTable();
		assertEquals(2, mergedTable.size());
		assertEquals("value1", mergedTable.get("key1").getAsTomlValue().getAsString());
		assertEquals("value2", mergedTable.get("key2").getAsTomlValue().getAsString());
		
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.merge(primitive, array1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.merge(primitive, table1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.merge(array1, primitive, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.merge(array1, table1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.merge(table1, primitive, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> TomlTypeProvider.INSTANCE.merge(table1, array1, RuntimeException::new));
	}
}
