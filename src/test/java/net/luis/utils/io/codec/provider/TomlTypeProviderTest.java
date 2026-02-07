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
		assertEquals(TomlNull.INSTANCE, TomlTypeProvider.INSTANCE.createNull());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new TomlValue(true), TomlTypeProvider.INSTANCE.createBoolean(true));
		assertEquals(new TomlValue((byte) 42), TomlTypeProvider.INSTANCE.createByte((byte) 42));
		assertEquals(new TomlValue((short) 42), TomlTypeProvider.INSTANCE.createShort((short) 42));
		assertEquals(new TomlValue(42), TomlTypeProvider.INSTANCE.createInteger(42));
		assertEquals(new TomlValue(42L), TomlTypeProvider.INSTANCE.createLong(42L));
		assertEquals(new TomlValue(42.5f), TomlTypeProvider.INSTANCE.createFloat(42.5f));
		assertEquals(new TomlValue(42.5), TomlTypeProvider.INSTANCE.createDouble(42.5));
		assertEquals(new TomlValue("test"), TomlTypeProvider.INSTANCE.createString("test"));
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.createString(null));
	}
	
	@Test
	void createCollectionTypes() {
		TomlElement element1 = new TomlValue("a");
		TomlElement element2 = new TomlValue("b");
		
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.createList(null));
		
		TomlArray emptyArray = new TomlArray();
		assertEquals(emptyArray, TomlTypeProvider.INSTANCE.createList(List.of()));
		
		TomlArray arrayWithElements = new TomlArray(List.of(element1, element2));
		assertEquals(arrayWithElements, TomlTypeProvider.INSTANCE.createList(List.of(element1, element2)));
		
		TomlTable emptyTable = new TomlTable();
		assertEquals(emptyTable, TomlTypeProvider.INSTANCE.createMap());
		assertEquals(emptyTable, TomlTypeProvider.INSTANCE.createMap(Map.of()));
		
		TomlTable tableWithElements = new TomlTable(Map.of("key1", element1, "key2", element2));
		assertEquals(tableWithElements, TomlTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2)));
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.isEmpty(null));
		assertFalse(TomlTypeProvider.INSTANCE.isEmpty(new TomlArray()));
		assertFalse(TomlTypeProvider.INSTANCE.isEmpty(new TomlValue(1)));
		assertFalse(TomlTypeProvider.INSTANCE.isEmpty(new TomlTable()));
		assertFalse(TomlTypeProvider.INSTANCE.isEmpty(TomlNull.INSTANCE));
	}
	
	@Test
	void isNullValidation() {
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.isNull(null));
		
		assertTrue(TomlTypeProvider.INSTANCE.isNull(TomlNull.INSTANCE));
		
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlArray()));
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlTable()));
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlValue(1)));
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlValue(true)));
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlValue("test")));
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getBoolean(null));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getByte(null));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getShort(null));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getInteger(null));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getLong(null));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getFloat(null));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getDouble(null));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getString(null));
		
		TomlArray wrongType = new TomlArray();
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getBoolean(wrongType));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getByte(wrongType));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getShort(wrongType));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getInteger(wrongType));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getLong(wrongType));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getFloat(wrongType));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getDouble(wrongType));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getString(wrongType));
		
		TomlValue invalidValue = new TomlValue("not-a-number");
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getBoolean(invalidValue));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getByte(invalidValue));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getShort(invalidValue));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getInteger(invalidValue));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getLong(invalidValue));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getFloat(invalidValue));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getDouble(invalidValue));
		
		assertTrue(TomlTypeProvider.INSTANCE.getBoolean(new TomlValue(true)));
		assertEquals((byte) 42, TomlTypeProvider.INSTANCE.getByte(new TomlValue((byte) 42)));
		assertEquals((short) 42, TomlTypeProvider.INSTANCE.getShort(new TomlValue((short) 42)));
		assertEquals(42, TomlTypeProvider.INSTANCE.getInteger(new TomlValue(42)));
		assertEquals(42L, TomlTypeProvider.INSTANCE.getLong(new TomlValue(42L)));
		assertEquals(42.5f, TomlTypeProvider.INSTANCE.getFloat(new TomlValue(42.5f)));
		assertEquals(42.5, TomlTypeProvider.INSTANCE.getDouble(new TomlValue(42.5)));
		assertEquals("test", TomlTypeProvider.INSTANCE.getString(new TomlValue("test")));
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getList(null));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getMap(null));
		
		TomlValue wrongType = new TomlValue(1);
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getList(wrongType));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.getMap(wrongType));
		
		TomlArray emptyArray = new TomlArray();
		assertTrue(TomlTypeProvider.INSTANCE.getList(emptyArray).isEmpty());
		
		TomlArray arrayWithElements = new TomlArray(List.of(new TomlValue("a"), new TomlValue("b")));
		List<TomlElement> listResult = TomlTypeProvider.INSTANCE.getList(arrayWithElements);
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.getFirst().getAsTomlValue().getAsString());
		
		TomlTable emptyTable = new TomlTable();
		assertTrue(TomlTypeProvider.INSTANCE.getMap(emptyTable).isEmpty());
		
		TomlTable tableWithElements = new TomlTable(Map.of("key", new TomlValue("value")));
		Map<String, TomlElement> mapResult = TomlTypeProvider.INSTANCE.getMap(tableWithElements);
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsTomlValue().getAsString());
	}
	
	@Test
	void mapOperations() {
		TomlTable tomlTable = new TomlTable();
		TomlElement testValue = new TomlValue("test");
		
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.has(null, "key"));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.has(tomlTable, null));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.get(null, "key"));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.get(tomlTable, null));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.set(null, "key", testValue));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.set(tomlTable, null, testValue));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.set(tomlTable, "key", null));
		
		TomlArray wrongType = new TomlArray();
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.has(wrongType, "key"));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.get(wrongType, "key"));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.set(wrongType, "key", testValue));
		
		assertFalse(TomlTypeProvider.INSTANCE.has(tomlTable, "key"));
		
		TomlTypeProvider.INSTANCE.set(tomlTable, "key", testValue);
		assertTrue(TomlTypeProvider.INSTANCE.has(tomlTable, "key"));
		assertEquals(testValue, TomlTypeProvider.INSTANCE.get(tomlTable, "key"));
	}
	
	@Test
	void mergeOperations() {
		assertEquals(TomlNull.INSTANCE, TomlTypeProvider.INSTANCE.merge(null, TomlNull.INSTANCE));
		assertEquals(TomlNull.INSTANCE, TomlTypeProvider.INSTANCE.merge(TomlNull.INSTANCE, null));
		
		TomlElement primitive = new TomlValue(1);
		TomlArray array1 = new TomlArray(List.of(new TomlValue("a")));
		TomlArray array2 = new TomlArray(List.of(new TomlValue("b")));
		TomlTable table1 = new TomlTable(Map.of("key1", new TomlValue("value1")));
		TomlTable table2 = new TomlTable(Map.of("key2", new TomlValue("value2")));
		
		assertEquals(primitive, TomlTypeProvider.INSTANCE.merge(TomlTypeProvider.INSTANCE.empty(), primitive));
		assertEquals(array1, TomlTypeProvider.INSTANCE.merge(TomlTypeProvider.INSTANCE.empty(), array1));
		assertEquals(table1, TomlTypeProvider.INSTANCE.merge(TomlTypeProvider.INSTANCE.empty(), table1));
		
		assertEquals(TomlNull.INSTANCE, TomlTypeProvider.INSTANCE.merge(TomlTypeProvider.INSTANCE.empty(), TomlNull.INSTANCE));
		
		assertEquals(primitive, TomlTypeProvider.INSTANCE.merge(primitive, TomlNull.INSTANCE));
		assertEquals(primitive, TomlTypeProvider.INSTANCE.merge(TomlNull.INSTANCE, primitive));
		
		assertEquals(array1, TomlTypeProvider.INSTANCE.merge(array1, TomlNull.INSTANCE));
		assertEquals(array1, TomlTypeProvider.INSTANCE.merge(TomlNull.INSTANCE, array1));
		
		assertEquals(table1, TomlTypeProvider.INSTANCE.merge(table1, TomlNull.INSTANCE));
		assertEquals(table1, TomlTypeProvider.INSTANCE.merge(TomlNull.INSTANCE, table1));
		
		TomlArray mergedArray = TomlTypeProvider.INSTANCE.merge(array1, array2).getAsTomlArray();
		assertEquals(2, mergedArray.size());
		assertEquals("a", mergedArray.get(0).getAsTomlValue().getAsString());
		assertEquals("b", mergedArray.get(1).getAsTomlValue().getAsString());
		
		TomlTable mergedTable = TomlTypeProvider.INSTANCE.merge(table1, table2).getAsTomlTable();
		assertEquals(2, mergedTable.size());
		assertEquals("value1", mergedTable.get("key1").getAsTomlValue().getAsString());
		assertEquals("value2", mergedTable.get("key2").getAsTomlValue().getAsString());
		
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.merge(primitive, array1));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.merge(primitive, table1));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.merge(array1, primitive));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.merge(array1, table1));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.merge(table1, primitive));
		assertThrows(TypeProviderException.class, () -> TomlTypeProvider.INSTANCE.merge(table1, array1));
	}
}
