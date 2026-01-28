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
import net.luis.utils.util.result.Result;
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
		assertEquals(TomlNull.INSTANCE, TomlTypeProvider.INSTANCE.createNull().resultOrThrow());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new TomlValue(true), TomlTypeProvider.INSTANCE.createBoolean(true).resultOrThrow());
		assertEquals(new TomlValue((byte) 42), TomlTypeProvider.INSTANCE.createByte((byte) 42).resultOrThrow());
		assertEquals(new TomlValue((short) 42), TomlTypeProvider.INSTANCE.createShort((short) 42).resultOrThrow());
		assertEquals(new TomlValue(42), TomlTypeProvider.INSTANCE.createInteger(42).resultOrThrow());
		assertEquals(new TomlValue(42L), TomlTypeProvider.INSTANCE.createLong(42L).resultOrThrow());
		assertEquals(new TomlValue(42.5f), TomlTypeProvider.INSTANCE.createFloat(42.5f).resultOrThrow());
		assertEquals(new TomlValue(42.5), TomlTypeProvider.INSTANCE.createDouble(42.5).resultOrThrow());
		assertEquals(new TomlValue("test"), TomlTypeProvider.INSTANCE.createString("test").resultOrThrow());
	}
	
	@Test
	void createStringWithNullThrowsException() {
		Result<TomlElement> res = TomlTypeProvider.INSTANCE.createString(null);
		assertTrue(res.isError());
		assertTrue(res.errorOrThrow().startsWith("Value 'null'"));
	}
	
	@Test
	void createCollectionTypes() {
		TomlElement element1 = new TomlValue("a");
		TomlElement element2 = new TomlValue("b");
		
		Result<TomlElement> nullList = TomlTypeProvider.INSTANCE.createList(null);
		assertTrue(nullList.isError());
		assertTrue(nullList.errorOrThrow().startsWith("Value 'null'"));
		
		TomlArray emptyArray = new TomlArray();
		assertEquals(emptyArray, TomlTypeProvider.INSTANCE.createList(List.of()).resultOrThrow());
		
		TomlArray arrayWithElements = new TomlArray(List.of(element1, element2));
		assertEquals(arrayWithElements, TomlTypeProvider.INSTANCE.createList(List.of(element1, element2)).resultOrThrow());
		
		TomlTable emptyTable = new TomlTable();
		assertEquals(emptyTable, TomlTypeProvider.INSTANCE.createMap().resultOrThrow());
		assertEquals(emptyTable, TomlTypeProvider.INSTANCE.createMap(Map.of()).resultOrThrow());
		
		TomlTable tableWithElements = new TomlTable(Map.of("key1", element1, "key2", element2));
		assertEquals(tableWithElements, TomlTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2)).resultOrThrow());
	}
	
	@Test
	void getEmptyValidation() {
		Result<TomlElement> nullEmpty = TomlTypeProvider.INSTANCE.getEmpty(null);
		assertTrue(nullEmpty.isError());
		assertTrue(nullEmpty.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(TomlTypeProvider.INSTANCE.getEmpty(new TomlArray()).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getEmpty(new TomlValue(1)).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getEmpty(new TomlTable()).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getEmpty(TomlNull.INSTANCE).isError());
	}
	
	@Test
	void isNullValidation() {
		Result<Boolean> nullIsNull = TomlTypeProvider.INSTANCE.isNull(null);
		assertTrue(nullIsNull.isError());
		assertTrue(nullIsNull.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(TomlTypeProvider.INSTANCE.isNull(TomlNull.INSTANCE).resultOrThrow());
		
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlArray()).resultOrThrow());
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlTable()).resultOrThrow());
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlValue(1)).resultOrThrow());
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlValue(true)).resultOrThrow());
		assertFalse(TomlTypeProvider.INSTANCE.isNull(new TomlValue("test")).resultOrThrow());
	}
	
	@Test
	void getPrimitiveTypes() {
		Result<Boolean> nullBoolean = TomlTypeProvider.INSTANCE.getBoolean(null);
		assertTrue(nullBoolean.isError());
		assertTrue(nullBoolean.errorOrThrow().startsWith("Value 'null'"));
		Result<Byte> nullByte = TomlTypeProvider.INSTANCE.getByte(null);
		assertTrue(nullByte.isError());
		assertTrue(nullByte.errorOrThrow().startsWith("Value 'null'"));
		Result<Short> nullShort = TomlTypeProvider.INSTANCE.getShort(null);
		assertTrue(nullShort.isError());
		assertTrue(nullShort.errorOrThrow().startsWith("Value 'null'"));
		Result<Integer> nullInteger = TomlTypeProvider.INSTANCE.getInteger(null);
		assertTrue(nullInteger.isError());
		assertTrue(nullInteger.errorOrThrow().startsWith("Value 'null'"));
		Result<Long> nullLong = TomlTypeProvider.INSTANCE.getLong(null);
		assertTrue(nullLong.isError());
		assertTrue(nullLong.errorOrThrow().startsWith("Value 'null'"));
		Result<Float> nullFloat = TomlTypeProvider.INSTANCE.getFloat(null);
		assertTrue(nullFloat.isError());
		assertTrue(nullFloat.errorOrThrow().startsWith("Value 'null'"));
		Result<Double> nullDouble = TomlTypeProvider.INSTANCE.getDouble(null);
		assertTrue(nullDouble.isError());
		assertTrue(nullDouble.errorOrThrow().startsWith("Value 'null'"));
		Result<String> nullString = TomlTypeProvider.INSTANCE.getString(null);
		assertTrue(nullString.isError());
		assertTrue(nullString.errorOrThrow().startsWith("Value 'null'"));
		
		TomlArray wrongType = new TomlArray();
		assertTrue(TomlTypeProvider.INSTANCE.getBoolean(wrongType).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getByte(wrongType).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getShort(wrongType).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getInteger(wrongType).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getLong(wrongType).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getFloat(wrongType).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getDouble(wrongType).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getString(wrongType).isError());
		
		TomlValue invalidValue = new TomlValue("not-a-number");
		assertTrue(TomlTypeProvider.INSTANCE.getBoolean(invalidValue).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getByte(invalidValue).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getShort(invalidValue).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getInteger(invalidValue).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getLong(invalidValue).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getFloat(invalidValue).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getDouble(invalidValue).isError());
		
		assertTrue(TomlTypeProvider.INSTANCE.getBoolean(new TomlValue(true)).resultOrThrow());
		assertEquals((byte) 42, TomlTypeProvider.INSTANCE.getByte(new TomlValue((byte) 42)).resultOrThrow());
		assertEquals((short) 42, TomlTypeProvider.INSTANCE.getShort(new TomlValue((short) 42)).resultOrThrow());
		assertEquals(42, TomlTypeProvider.INSTANCE.getInteger(new TomlValue(42)).resultOrThrow());
		assertEquals(42L, TomlTypeProvider.INSTANCE.getLong(new TomlValue(42L)).resultOrThrow());
		assertEquals(42.5f, TomlTypeProvider.INSTANCE.getFloat(new TomlValue(42.5f)).resultOrThrow());
		assertEquals(42.5, TomlTypeProvider.INSTANCE.getDouble(new TomlValue(42.5)).resultOrThrow());
		assertEquals("test", TomlTypeProvider.INSTANCE.getString(new TomlValue("test")).resultOrThrow());
	}
	
	@Test
	void getCollectionTypes() {
		Result<List<TomlElement>> nullList = TomlTypeProvider.INSTANCE.getList(null);
		assertTrue(nullList.isError());
		assertTrue(nullList.errorOrThrow().startsWith("Value 'null'"));
		Result<Map<String, TomlElement>> nullMap = TomlTypeProvider.INSTANCE.getMap(null);
		assertTrue(nullMap.isError());
		assertTrue(nullMap.errorOrThrow().startsWith("Value 'null'"));
		
		TomlValue wrongType = new TomlValue(1);
		assertTrue(TomlTypeProvider.INSTANCE.getList(wrongType).isError());
		assertTrue(TomlTypeProvider.INSTANCE.getMap(wrongType).isError());
		
		TomlArray emptyArray = new TomlArray();
		assertTrue(TomlTypeProvider.INSTANCE.getList(emptyArray).resultOrThrow().isEmpty());
		
		TomlArray arrayWithElements = new TomlArray(List.of(new TomlValue("a"), new TomlValue("b")));
		List<TomlElement> listResult = TomlTypeProvider.INSTANCE.getList(arrayWithElements).resultOrThrow();
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.getFirst().getAsTomlValue().getAsString());
		
		TomlTable emptyTable = new TomlTable();
		assertTrue(TomlTypeProvider.INSTANCE.getMap(emptyTable).resultOrThrow().isEmpty());
		
		TomlTable tableWithElements = new TomlTable(Map.of("key", new TomlValue("value")));
		Map<String, TomlElement> mapResult = TomlTypeProvider.INSTANCE.getMap(tableWithElements).resultOrThrow();
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsTomlValue().getAsString());
	}
	
	@Test
	void mapOperations() {
		TomlTable tomlTable = new TomlTable();
		TomlElement testValue = new TomlValue("test");
		
		Result<Boolean> nullHas = TomlTypeProvider.INSTANCE.has(null, "key");
		assertTrue(nullHas.isError());
		assertTrue(nullHas.errorOrThrow().startsWith("Value 'null'"));
		Result<Boolean> hasNullKey = TomlTypeProvider.INSTANCE.has(tomlTable, null);
		assertTrue(hasNullKey.isError());
		assertTrue(hasNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<TomlElement> nullGet = TomlTypeProvider.INSTANCE.get(null, "key");
		assertTrue(nullGet.isError());
		assertTrue(nullGet.errorOrThrow().startsWith("Value 'null'"));
		Result<TomlElement> getNullKey = TomlTypeProvider.INSTANCE.get(tomlTable, null);
		assertTrue(getNullKey.isError());
		assertTrue(getNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<TomlElement> nullSet = TomlTypeProvider.INSTANCE.set(null, "key", testValue);
		assertTrue(nullSet.isError());
		assertTrue(nullSet.errorOrThrow().startsWith("Value 'null'"));
		Result<TomlElement> setNullKey = TomlTypeProvider.INSTANCE.set(tomlTable, null, testValue);
		assertTrue(setNullKey.isError());
		assertTrue(setNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<TomlElement> nullValueSet = TomlTypeProvider.INSTANCE.set(tomlTable, "key", (TomlElement) null);
		assertTrue(nullValueSet.isError());
		assertTrue(nullValueSet.errorOrThrow().startsWith("Value 'null'"));
		
		TomlArray wrongType = new TomlArray();
		assertTrue(TomlTypeProvider.INSTANCE.has(wrongType, "key").isError());
		assertTrue(TomlTypeProvider.INSTANCE.get(wrongType, "key").isError());
		assertTrue(TomlTypeProvider.INSTANCE.set(wrongType, "key", testValue).isError());
		
		assertFalse(TomlTypeProvider.INSTANCE.has(tomlTable, "key").resultOrThrow());
		assertNull(TomlTypeProvider.INSTANCE.get(tomlTable, "key").resultOrThrow());
		
		assertNull(TomlTypeProvider.INSTANCE.set(tomlTable, "key", testValue).resultOrThrow());
		assertTrue(TomlTypeProvider.INSTANCE.has(tomlTable, "key").resultOrThrow());
		assertEquals(testValue, TomlTypeProvider.INSTANCE.get(tomlTable, "key").resultOrThrow());
	}
	
	@Test
	void mapOperationsWithResults() {
		TomlTable tomlTable = new TomlTable();
		TomlElement testValue = new TomlValue("test");
		
		Result<TomlElement> nullType = TomlTypeProvider.INSTANCE.set(null, "key", Result.success(testValue));
		assertTrue(nullType.isError());
		assertTrue(nullType.errorOrThrow().startsWith("Type 'null'"));
		Result<TomlElement> nullKey = TomlTypeProvider.INSTANCE.set(tomlTable, null, Result.success(testValue));
		assertTrue(nullKey.isError());
		assertTrue(nullKey.errorOrThrow().startsWith("Key 'null'"));
		Result<TomlElement> nullValue = TomlTypeProvider.INSTANCE.set(tomlTable, "key", (Result<TomlElement>) null);
		assertTrue(nullValue.isError());
		assertTrue(nullValue.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(TomlTypeProvider.INSTANCE.set(tomlTable, "key", Result.error("error")).isError());
		assertTrue(TomlTypeProvider.INSTANCE.set(tomlTable, "key", Result.success(testValue)).isSuccess());
		assertEquals(testValue, tomlTable.get("key"));
	}
	
	@Test
	void mergeOperations() {
		assertEquals(TomlNull.INSTANCE, TomlTypeProvider.INSTANCE.merge(null, TomlNull.INSTANCE).resultOrThrow());
		assertEquals(TomlNull.INSTANCE, TomlTypeProvider.INSTANCE.merge(TomlNull.INSTANCE, (TomlElement) null).resultOrThrow());
		
		TomlElement primitive = new TomlValue(1);
		TomlArray array1 = new TomlArray(List.of(new TomlValue("a")));
		TomlArray array2 = new TomlArray(List.of(new TomlValue("b")));
		TomlTable table1 = new TomlTable(Map.of("key1", new TomlValue("value1")));
		TomlTable table2 = new TomlTable(Map.of("key2", new TomlValue("value2")));
		
		assertEquals(primitive, TomlTypeProvider.INSTANCE.merge(TomlTypeProvider.INSTANCE.empty(), primitive).resultOrThrow());
		assertEquals(array1, TomlTypeProvider.INSTANCE.merge(TomlTypeProvider.INSTANCE.empty(), array1).resultOrThrow());
		assertEquals(table1, TomlTypeProvider.INSTANCE.merge(TomlTypeProvider.INSTANCE.empty(), table1).resultOrThrow());
		
		assertEquals(TomlNull.INSTANCE, TomlTypeProvider.INSTANCE.merge(TomlTypeProvider.INSTANCE.empty(), TomlNull.INSTANCE).resultOrThrow());
		
		assertEquals(primitive, TomlTypeProvider.INSTANCE.merge(primitive, TomlNull.INSTANCE).resultOrThrow());
		assertEquals(primitive, TomlTypeProvider.INSTANCE.merge(TomlNull.INSTANCE, primitive).resultOrThrow());
		
		assertEquals(array1, TomlTypeProvider.INSTANCE.merge(array1, TomlNull.INSTANCE).resultOrThrow());
		assertEquals(array1, TomlTypeProvider.INSTANCE.merge(TomlNull.INSTANCE, array1).resultOrThrow());
		
		assertEquals(table1, TomlTypeProvider.INSTANCE.merge(table1, TomlNull.INSTANCE).resultOrThrow());
		assertEquals(table1, TomlTypeProvider.INSTANCE.merge(TomlNull.INSTANCE, table1).resultOrThrow());
		
		TomlArray mergedArray = TomlTypeProvider.INSTANCE.merge(array1, array2).resultOrThrow().getAsTomlArray();
		assertEquals(2, mergedArray.size());
		assertEquals("a", mergedArray.get(0).getAsTomlValue().getAsString());
		assertEquals("b", mergedArray.get(1).getAsTomlValue().getAsString());
		
		TomlTable mergedTable = TomlTypeProvider.INSTANCE.merge(table1, table2).resultOrThrow().getAsTomlTable();
		assertEquals(2, mergedTable.size());
		assertEquals("value1", mergedTable.get("key1").getAsTomlValue().getAsString());
		assertEquals("value2", mergedTable.get("key2").getAsTomlValue().getAsString());
		
		assertTrue(TomlTypeProvider.INSTANCE.merge(primitive, array1).isError());
		assertTrue(TomlTypeProvider.INSTANCE.merge(primitive, table1).isError());
		assertTrue(TomlTypeProvider.INSTANCE.merge(array1, primitive).isError());
		assertTrue(TomlTypeProvider.INSTANCE.merge(array1, table1).isError());
		assertTrue(TomlTypeProvider.INSTANCE.merge(table1, primitive).isError());
		assertTrue(TomlTypeProvider.INSTANCE.merge(table1, array1).isError());
	}
	
	@Test
	void mergeOperationsWithResults() {
		TomlTable tomlTable = new TomlTable();
		TomlElement testValue = new TomlValue("test");
		
		Result<TomlElement> nullCurrent = TomlTypeProvider.INSTANCE.merge(null, Result.success(testValue));
		assertTrue(nullCurrent.isError());
		assertTrue(nullCurrent.errorOrThrow().startsWith("Current value 'null'"));
		Result<TomlElement> nullValue = TomlTypeProvider.INSTANCE.merge(tomlTable, (Result<TomlElement>) null);
		assertTrue(nullValue.isError());
		assertTrue(nullValue.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(TomlTypeProvider.INSTANCE.merge(tomlTable, Result.error("error")).isError());
		assertEquals(testValue, TomlTypeProvider.INSTANCE.merge(TomlTypeProvider.INSTANCE.empty(), Result.success(testValue)).resultOrThrow());
	}
}
