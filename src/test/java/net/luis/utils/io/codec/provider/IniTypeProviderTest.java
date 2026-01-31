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

import net.luis.utils.io.data.ini.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IniTypeProvider}.<br>
 *
 * @author Luis-St
 */
class IniTypeProviderTest {
	
	@Test
	void emptyReturnsIniElement() {
		IniElement element = IniTypeProvider.INSTANCE.empty();
		assertFalse(element.isIniNull());
		assertFalse(element.isIniValue());
		assertFalse(element.isIniSection());
		assertFalse(element.isIniDocument());
	}
	
	@Test
	void createNullReturnsIniNull() {
		assertEquals(IniNull.INSTANCE, IniTypeProvider.INSTANCE.createNull().resultOrThrow());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new IniValue(true), IniTypeProvider.INSTANCE.createBoolean(true).resultOrThrow());
		assertEquals(new IniValue((byte) 42), IniTypeProvider.INSTANCE.createByte((byte) 42).resultOrThrow());
		assertEquals(new IniValue((short) 42), IniTypeProvider.INSTANCE.createShort((short) 42).resultOrThrow());
		assertEquals(new IniValue(42), IniTypeProvider.INSTANCE.createInteger(42).resultOrThrow());
		assertEquals(new IniValue(42L), IniTypeProvider.INSTANCE.createLong(42L).resultOrThrow());
		assertEquals(new IniValue(42.5f), IniTypeProvider.INSTANCE.createFloat(42.5f).resultOrThrow());
		assertEquals(new IniValue(42.5), IniTypeProvider.INSTANCE.createDouble(42.5).resultOrThrow());
		assertEquals(new IniValue("test"), IniTypeProvider.INSTANCE.createString("test").resultOrThrow());
	}
	
	@Test
	void createStringWithNullThrowsException() {
		Result<IniElement> res = IniTypeProvider.INSTANCE.createString(null);
		assertTrue(res.isError());
		assertTrue(res.errorOrThrow().startsWith("Value 'null'"));
	}
	
	@Test
	void createListReturnsError() {
		Result<IniElement> nullList = IniTypeProvider.INSTANCE.createList(null);
		assertTrue(nullList.isError());
		assertTrue(nullList.errorOrThrow().contains("does not support lists"));
		
		Result<IniElement> emptyList = IniTypeProvider.INSTANCE.createList(List.of());
		assertTrue(emptyList.isError());
		assertTrue(emptyList.errorOrThrow().contains("does not support lists"));
		
		Result<IniElement> listWithElements = IniTypeProvider.INSTANCE.createList(List.of(new IniValue("a")));
		assertTrue(listWithElements.isError());
		assertTrue(listWithElements.errorOrThrow().contains("does not support lists"));
	}
	
	@Test
	void createMapTypes() {
		Result<IniElement> nullMap = IniTypeProvider.INSTANCE.createMap(null);
		assertTrue(nullMap.isError());
		assertTrue(nullMap.errorOrThrow().startsWith("Value 'null'"));
		
		IniElement emptySection = IniTypeProvider.INSTANCE.createMap().resultOrThrow();
		assertTrue(emptySection.isIniSection());
		assertTrue(emptySection.getAsIniSection().isEmpty());
		
		IniElement element1 = new IniValue("value1");
		IniElement element2 = new IniValue("value2");
		
		IniElement sectionWithElements = IniTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2)).resultOrThrow();
		assertTrue(sectionWithElements.isIniSection());
		assertEquals(2, sectionWithElements.getAsIniSection().size());
		assertEquals(element1, sectionWithElements.getAsIniSection().get("key1"));
		assertEquals(element2, sectionWithElements.getAsIniSection().get("key2"));
	}
	
	@Test
	void getEmptyValidation() {
		Result<IniElement> nullEmpty = IniTypeProvider.INSTANCE.getEmpty(null);
		assertTrue(nullEmpty.isError());
		assertTrue(nullEmpty.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(IniTypeProvider.INSTANCE.getEmpty(new IniSection("test")).isError());
		assertTrue(IniTypeProvider.INSTANCE.getEmpty(new IniValue(1)).isError());
		assertTrue(IniTypeProvider.INSTANCE.getEmpty(IniNull.INSTANCE).isError());
	}
	
	@Test
	void isNullValidation() {
		Result<Boolean> nullIsNull = IniTypeProvider.INSTANCE.isNull(null);
		assertTrue(nullIsNull.isError());
		assertTrue(nullIsNull.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(IniTypeProvider.INSTANCE.isNull(IniNull.INSTANCE).resultOrThrow());
		
		assertFalse(IniTypeProvider.INSTANCE.isNull(new IniSection("test")).resultOrThrow());
		assertFalse(IniTypeProvider.INSTANCE.isNull(new IniValue(1)).resultOrThrow());
		assertFalse(IniTypeProvider.INSTANCE.isNull(new IniValue(true)).resultOrThrow());
		assertFalse(IniTypeProvider.INSTANCE.isNull(new IniValue("test")).resultOrThrow());
	}
	
	@Test
	void getPrimitiveTypes() {
		Result<Boolean> nullBoolean = IniTypeProvider.INSTANCE.getBoolean(null);
		assertTrue(nullBoolean.isError());
		assertTrue(nullBoolean.errorOrThrow().startsWith("Value 'null'"));
		Result<Byte> nullByte = IniTypeProvider.INSTANCE.getByte(null);
		assertTrue(nullByte.isError());
		assertTrue(nullByte.errorOrThrow().startsWith("Value 'null'"));
		Result<Short> nullShort = IniTypeProvider.INSTANCE.getShort(null);
		assertTrue(nullShort.isError());
		assertTrue(nullShort.errorOrThrow().startsWith("Value 'null'"));
		Result<Integer> nullInteger = IniTypeProvider.INSTANCE.getInteger(null);
		assertTrue(nullInteger.isError());
		assertTrue(nullInteger.errorOrThrow().startsWith("Value 'null'"));
		Result<Long> nullLong = IniTypeProvider.INSTANCE.getLong(null);
		assertTrue(nullLong.isError());
		assertTrue(nullLong.errorOrThrow().startsWith("Value 'null'"));
		Result<Float> nullFloat = IniTypeProvider.INSTANCE.getFloat(null);
		assertTrue(nullFloat.isError());
		assertTrue(nullFloat.errorOrThrow().startsWith("Value 'null'"));
		Result<Double> nullDouble = IniTypeProvider.INSTANCE.getDouble(null);
		assertTrue(nullDouble.isError());
		assertTrue(nullDouble.errorOrThrow().startsWith("Value 'null'"));
		Result<String> nullString = IniTypeProvider.INSTANCE.getString(null);
		assertTrue(nullString.isError());
		assertTrue(nullString.errorOrThrow().startsWith("Value 'null'"));
		
		IniSection wrongType = new IniSection("test");
		assertTrue(IniTypeProvider.INSTANCE.getBoolean(wrongType).isError());
		assertTrue(IniTypeProvider.INSTANCE.getByte(wrongType).isError());
		assertTrue(IniTypeProvider.INSTANCE.getShort(wrongType).isError());
		assertTrue(IniTypeProvider.INSTANCE.getInteger(wrongType).isError());
		assertTrue(IniTypeProvider.INSTANCE.getLong(wrongType).isError());
		assertTrue(IniTypeProvider.INSTANCE.getFloat(wrongType).isError());
		assertTrue(IniTypeProvider.INSTANCE.getDouble(wrongType).isError());
		assertTrue(IniTypeProvider.INSTANCE.getString(wrongType).isError());
		
		IniValue invalidValue = new IniValue("not-a-number");
		assertTrue(IniTypeProvider.INSTANCE.getBoolean(invalidValue).isError());
		assertTrue(IniTypeProvider.INSTANCE.getByte(invalidValue).isError());
		assertTrue(IniTypeProvider.INSTANCE.getShort(invalidValue).isError());
		assertTrue(IniTypeProvider.INSTANCE.getInteger(invalidValue).isError());
		assertTrue(IniTypeProvider.INSTANCE.getLong(invalidValue).isError());
		assertTrue(IniTypeProvider.INSTANCE.getFloat(invalidValue).isError());
		assertTrue(IniTypeProvider.INSTANCE.getDouble(invalidValue).isError());
		
		assertTrue(IniTypeProvider.INSTANCE.getBoolean(new IniValue(true)).resultOrThrow());
		assertEquals((byte) 42, IniTypeProvider.INSTANCE.getByte(new IniValue((byte) 42)).resultOrThrow());
		assertEquals((short) 42, IniTypeProvider.INSTANCE.getShort(new IniValue((short) 42)).resultOrThrow());
		assertEquals(42, IniTypeProvider.INSTANCE.getInteger(new IniValue(42)).resultOrThrow());
		assertEquals(42L, IniTypeProvider.INSTANCE.getLong(new IniValue(42L)).resultOrThrow());
		assertEquals(42.5f, IniTypeProvider.INSTANCE.getFloat(new IniValue(42.5f)).resultOrThrow());
		assertEquals(42.5, IniTypeProvider.INSTANCE.getDouble(new IniValue(42.5)).resultOrThrow());
		assertEquals("test", IniTypeProvider.INSTANCE.getString(new IniValue("test")).resultOrThrow());
	}
	
	@Test
	void getListReturnsError() {
		Result<List<IniElement>> nullList = IniTypeProvider.INSTANCE.getList(null);
		assertTrue(nullList.isError());
		assertTrue(nullList.errorOrThrow().contains("does not support lists"));
		
		Result<List<IniElement>> fromValue = IniTypeProvider.INSTANCE.getList(new IniValue(1));
		assertTrue(fromValue.isError());
		assertTrue(fromValue.errorOrThrow().contains("does not support lists"));
		
		Result<List<IniElement>> fromSection = IniTypeProvider.INSTANCE.getList(new IniSection("test"));
		assertTrue(fromSection.isError());
		assertTrue(fromSection.errorOrThrow().contains("does not support lists"));
	}
	
	@Test
	void getMapTypes() {
		Result<Map<String, IniElement>> nullMap = IniTypeProvider.INSTANCE.getMap(null);
		assertTrue(nullMap.isError());
		assertTrue(nullMap.errorOrThrow().startsWith("Value 'null'"));
		
		IniValue wrongType = new IniValue(1);
		assertTrue(IniTypeProvider.INSTANCE.getMap(wrongType).isError());
		
		IniSection emptySection = new IniSection("test");
		assertTrue(IniTypeProvider.INSTANCE.getMap(emptySection).resultOrThrow().isEmpty());
		
		IniSection sectionWithElements = new IniSection("test");
		sectionWithElements.add("key", new IniValue("value"));
		Map<String, IniElement> mapResult = IniTypeProvider.INSTANCE.getMap(sectionWithElements).resultOrThrow();
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsIniValue().getAsString());
	}
	
	@Test
	void mapOperations() {
		IniSection iniSection = new IniSection("test");
		IniElement testValue = new IniValue("test");
		
		Result<Boolean> nullHas = IniTypeProvider.INSTANCE.has(null, "key");
		assertTrue(nullHas.isError());
		assertTrue(nullHas.errorOrThrow().startsWith("Value 'null'"));
		Result<Boolean> hasNullKey = IniTypeProvider.INSTANCE.has(iniSection, null);
		assertTrue(hasNullKey.isError());
		assertTrue(hasNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<IniElement> nullGet = IniTypeProvider.INSTANCE.get(null, "key");
		assertTrue(nullGet.isError());
		assertTrue(nullGet.errorOrThrow().startsWith("Value 'null'"));
		Result<IniElement> getNullKey = IniTypeProvider.INSTANCE.get(iniSection, null);
		assertTrue(getNullKey.isError());
		assertTrue(getNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<IniElement> nullSet = IniTypeProvider.INSTANCE.set(null, "key", testValue);
		assertTrue(nullSet.isError());
		assertTrue(nullSet.errorOrThrow().startsWith("Value 'null'"));
		Result<IniElement> setNullKey = IniTypeProvider.INSTANCE.set(iniSection, null, testValue);
		assertTrue(setNullKey.isError());
		assertTrue(setNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<IniElement> nullValueSet = IniTypeProvider.INSTANCE.set(iniSection, "key", (IniElement) null);
		assertTrue(nullValueSet.isError());
		assertTrue(nullValueSet.errorOrThrow().startsWith("Value 'null'"));
		
		IniValue wrongType = new IniValue(1);
		assertTrue(IniTypeProvider.INSTANCE.has(wrongType, "key").isError());
		assertTrue(IniTypeProvider.INSTANCE.get(wrongType, "key").isError());
		assertTrue(IniTypeProvider.INSTANCE.set(wrongType, "key", testValue).isError());
		
		assertFalse(IniTypeProvider.INSTANCE.has(iniSection, "key").resultOrThrow());
		assertNull(IniTypeProvider.INSTANCE.get(iniSection, "key").resultOrThrow());
		
		assertEquals(iniSection, IniTypeProvider.INSTANCE.set(iniSection, "key", testValue).resultOrThrow());
		assertTrue(IniTypeProvider.INSTANCE.has(iniSection, "key").resultOrThrow());
		assertEquals(testValue, IniTypeProvider.INSTANCE.get(iniSection, "key").resultOrThrow());
	}
	
	@Test
	void mapOperationsWithResults() {
		IniSection iniSection = new IniSection("test");
		IniElement testValue = new IniValue("test");
		
		Result<IniElement> nullType = IniTypeProvider.INSTANCE.set(null, "key", Result.success(testValue));
		assertTrue(nullType.isError());
		assertTrue(nullType.errorOrThrow().startsWith("Type 'null'"));
		Result<IniElement> nullKey = IniTypeProvider.INSTANCE.set(iniSection, null, Result.success(testValue));
		assertTrue(nullKey.isError());
		assertTrue(nullKey.errorOrThrow().startsWith("Key 'null'"));
		Result<IniElement> nullValue = IniTypeProvider.INSTANCE.set(iniSection, "key", (Result<IniElement>) null);
		assertTrue(nullValue.isError());
		assertTrue(nullValue.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(IniTypeProvider.INSTANCE.set(iniSection, "key", Result.error("error")).isError());
		assertTrue(IniTypeProvider.INSTANCE.set(iniSection, "key", Result.success(testValue)).isSuccess());
		assertEquals(testValue, iniSection.get("key"));
	}
	
	@Test
	void mergeOperations() {
		assertEquals(IniNull.INSTANCE, IniTypeProvider.INSTANCE.merge(null, IniNull.INSTANCE).resultOrThrow());
		assertEquals(IniNull.INSTANCE, IniTypeProvider.INSTANCE.merge(IniNull.INSTANCE, (IniElement) null).resultOrThrow());
		
		IniElement value = new IniValue(1);
		IniSection section1 = new IniSection("section1");
		section1.add("key1", new IniValue("value1"));
		IniSection section2 = new IniSection("section2");
		section2.add("key2", new IniValue("value2"));
		
		assertEquals(value, IniTypeProvider.INSTANCE.merge(IniTypeProvider.INSTANCE.empty(), value).resultOrThrow());
		assertEquals(section1, IniTypeProvider.INSTANCE.merge(IniTypeProvider.INSTANCE.empty(), section1).resultOrThrow());
		
		assertEquals(IniNull.INSTANCE, IniTypeProvider.INSTANCE.merge(IniTypeProvider.INSTANCE.empty(), IniNull.INSTANCE).resultOrThrow());
		
		assertEquals(value, IniTypeProvider.INSTANCE.merge(value, IniNull.INSTANCE).resultOrThrow());
		assertEquals(value, IniTypeProvider.INSTANCE.merge(IniNull.INSTANCE, value).resultOrThrow());
		
		assertEquals(section1, IniTypeProvider.INSTANCE.merge(section1, IniNull.INSTANCE).resultOrThrow());
		assertEquals(section1, IniTypeProvider.INSTANCE.merge(IniNull.INSTANCE, section1).resultOrThrow());
		
		IniSection mergedSection = IniTypeProvider.INSTANCE.merge(section1, section2).resultOrThrow().getAsIniSection();
		assertEquals(2, mergedSection.size());
		assertEquals("value1", mergedSection.get("key1").getAsIniValue().getAsString());
		assertEquals("value2", mergedSection.get("key2").getAsIniValue().getAsString());
		
		assertTrue(IniTypeProvider.INSTANCE.merge(value, section1).isError());
		assertTrue(IniTypeProvider.INSTANCE.merge(section1, value).isError());
	}
	
	@Test
	void mergeOperationsWithResults() {
		IniSection iniSection = new IniSection("test");
		IniElement testValue = new IniValue("test");
		
		Result<IniElement> nullCurrent = IniTypeProvider.INSTANCE.merge(null, Result.success(testValue));
		assertTrue(nullCurrent.isError());
		assertTrue(nullCurrent.errorOrThrow().startsWith("Current value 'null'"));
		Result<IniElement> nullValue = IniTypeProvider.INSTANCE.merge(iniSection, (Result<IniElement>) null);
		assertTrue(nullValue.isError());
		assertTrue(nullValue.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(IniTypeProvider.INSTANCE.merge(iniSection, Result.error("error")).isError());
		assertEquals(testValue, IniTypeProvider.INSTANCE.merge(IniTypeProvider.INSTANCE.empty(), Result.success(testValue)).resultOrThrow());
	}
}
