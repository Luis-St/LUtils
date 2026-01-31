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

import net.luis.utils.io.data.yaml.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YamlTypeProvider}.<br>
 *
 * @author Luis-St
 */
class YamlTypeProviderTest {
	
	@Test
	void emptyReturnsYamlElement() {
		YamlElement element = YamlTypeProvider.INSTANCE.empty();
		assertFalse(element.isYamlNull());
		assertFalse(element.isYamlScalar());
		assertFalse(element.isYamlSequence());
		assertFalse(element.isYamlMapping());
	}
	
	@Test
	void createNullReturnsYamlNull() {
		assertEquals(YamlNull.INSTANCE, YamlTypeProvider.INSTANCE.createNull().resultOrThrow());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new YamlScalar(true), YamlTypeProvider.INSTANCE.createBoolean(true).resultOrThrow());
		assertEquals(new YamlScalar((byte) 42), YamlTypeProvider.INSTANCE.createByte((byte) 42).resultOrThrow());
		assertEquals(new YamlScalar((short) 42), YamlTypeProvider.INSTANCE.createShort((short) 42).resultOrThrow());
		assertEquals(new YamlScalar(42), YamlTypeProvider.INSTANCE.createInteger(42).resultOrThrow());
		assertEquals(new YamlScalar(42L), YamlTypeProvider.INSTANCE.createLong(42L).resultOrThrow());
		assertEquals(new YamlScalar(42.5f), YamlTypeProvider.INSTANCE.createFloat(42.5f).resultOrThrow());
		assertEquals(new YamlScalar(42.5), YamlTypeProvider.INSTANCE.createDouble(42.5).resultOrThrow());
		assertEquals(new YamlScalar("test"), YamlTypeProvider.INSTANCE.createString("test").resultOrThrow());
	}
	
	@Test
	void createStringWithNullThrowsException() {
		Result<YamlElement> res = YamlTypeProvider.INSTANCE.createString(null);
		assertTrue(res.isError());
		assertTrue(res.errorOrThrow().startsWith("Value 'null'"));
	}
	
	@Test
	void createCollectionTypes() {
		YamlElement element1 = new YamlScalar("a");
		YamlElement element2 = new YamlScalar("b");
		
		Result<YamlElement> nullList = YamlTypeProvider.INSTANCE.createList(null);
		assertTrue(nullList.isError());
		assertTrue(nullList.errorOrThrow().startsWith("Value 'null'"));
		
		YamlSequence emptySequence = new YamlSequence();
		assertEquals(emptySequence, YamlTypeProvider.INSTANCE.createList(List.of()).resultOrThrow());
		
		YamlSequence sequenceWithElements = new YamlSequence(List.of(element1, element2));
		assertEquals(sequenceWithElements, YamlTypeProvider.INSTANCE.createList(List.of(element1, element2)).resultOrThrow());
		
		YamlMapping emptyMapping = new YamlMapping();
		assertEquals(emptyMapping, YamlTypeProvider.INSTANCE.createMap().resultOrThrow());
		assertEquals(emptyMapping, YamlTypeProvider.INSTANCE.createMap(Map.of()).resultOrThrow());
		
		YamlMapping mappingWithElements = new YamlMapping(Map.of("key1", element1, "key2", element2));
		assertEquals(mappingWithElements, YamlTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2)).resultOrThrow());
	}
	
	@Test
	void getEmptyValidation() {
		Result<YamlElement> nullEmpty = YamlTypeProvider.INSTANCE.getEmpty(null);
		assertTrue(nullEmpty.isError());
		assertTrue(nullEmpty.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(YamlTypeProvider.INSTANCE.getEmpty(new YamlSequence()).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getEmpty(new YamlScalar(1)).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getEmpty(new YamlMapping()).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getEmpty(YamlNull.INSTANCE).isError());
	}
	
	@Test
	void isNullValidation() {
		Result<Boolean> nullIsNull = YamlTypeProvider.INSTANCE.isNull(null);
		assertTrue(nullIsNull.isError());
		assertTrue(nullIsNull.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(YamlTypeProvider.INSTANCE.isNull(YamlNull.INSTANCE).resultOrThrow());
		
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlSequence()).resultOrThrow());
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlMapping()).resultOrThrow());
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlScalar(1)).resultOrThrow());
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlScalar(true)).resultOrThrow());
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlScalar("test")).resultOrThrow());
	}
	
	@Test
	void getPrimitiveTypes() {
		Result<Boolean> nullBoolean = YamlTypeProvider.INSTANCE.getBoolean(null);
		assertTrue(nullBoolean.isError());
		assertTrue(nullBoolean.errorOrThrow().startsWith("Value 'null'"));
		Result<Byte> nullByte = YamlTypeProvider.INSTANCE.getByte(null);
		assertTrue(nullByte.isError());
		assertTrue(nullByte.errorOrThrow().startsWith("Value 'null'"));
		Result<Short> nullShort = YamlTypeProvider.INSTANCE.getShort(null);
		assertTrue(nullShort.isError());
		assertTrue(nullShort.errorOrThrow().startsWith("Value 'null'"));
		Result<Integer> nullInteger = YamlTypeProvider.INSTANCE.getInteger(null);
		assertTrue(nullInteger.isError());
		assertTrue(nullInteger.errorOrThrow().startsWith("Value 'null'"));
		Result<Long> nullLong = YamlTypeProvider.INSTANCE.getLong(null);
		assertTrue(nullLong.isError());
		assertTrue(nullLong.errorOrThrow().startsWith("Value 'null'"));
		Result<Float> nullFloat = YamlTypeProvider.INSTANCE.getFloat(null);
		assertTrue(nullFloat.isError());
		assertTrue(nullFloat.errorOrThrow().startsWith("Value 'null'"));
		Result<Double> nullDouble = YamlTypeProvider.INSTANCE.getDouble(null);
		assertTrue(nullDouble.isError());
		assertTrue(nullDouble.errorOrThrow().startsWith("Value 'null'"));
		Result<String> nullString = YamlTypeProvider.INSTANCE.getString(null);
		assertTrue(nullString.isError());
		assertTrue(nullString.errorOrThrow().startsWith("Value 'null'"));
		
		YamlSequence wrongType = new YamlSequence();
		assertTrue(YamlTypeProvider.INSTANCE.getBoolean(wrongType).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getByte(wrongType).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getShort(wrongType).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getInteger(wrongType).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getLong(wrongType).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getFloat(wrongType).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getDouble(wrongType).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getString(wrongType).isError());
		
		YamlScalar invalidValue = new YamlScalar("not-a-number");
		assertTrue(YamlTypeProvider.INSTANCE.getBoolean(invalidValue).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getByte(invalidValue).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getShort(invalidValue).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getInteger(invalidValue).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getLong(invalidValue).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getFloat(invalidValue).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getDouble(invalidValue).isError());
		
		assertTrue(YamlTypeProvider.INSTANCE.getBoolean(new YamlScalar(true)).resultOrThrow());
		assertEquals((byte) 42, YamlTypeProvider.INSTANCE.getByte(new YamlScalar((byte) 42)).resultOrThrow());
		assertEquals((short) 42, YamlTypeProvider.INSTANCE.getShort(new YamlScalar((short) 42)).resultOrThrow());
		assertEquals(42, YamlTypeProvider.INSTANCE.getInteger(new YamlScalar(42)).resultOrThrow());
		assertEquals(42L, YamlTypeProvider.INSTANCE.getLong(new YamlScalar(42L)).resultOrThrow());
		assertEquals(42.5f, YamlTypeProvider.INSTANCE.getFloat(new YamlScalar(42.5f)).resultOrThrow());
		assertEquals(42.5, YamlTypeProvider.INSTANCE.getDouble(new YamlScalar(42.5)).resultOrThrow());
		assertEquals("test", YamlTypeProvider.INSTANCE.getString(new YamlScalar("test")).resultOrThrow());
	}
	
	@Test
	void getCollectionTypes() {
		Result<List<YamlElement>> nullList = YamlTypeProvider.INSTANCE.getList(null);
		assertTrue(nullList.isError());
		assertTrue(nullList.errorOrThrow().startsWith("Value 'null'"));
		Result<Map<String, YamlElement>> nullMap = YamlTypeProvider.INSTANCE.getMap(null);
		assertTrue(nullMap.isError());
		assertTrue(nullMap.errorOrThrow().startsWith("Value 'null'"));
		
		YamlScalar wrongType = new YamlScalar(1);
		assertTrue(YamlTypeProvider.INSTANCE.getList(wrongType).isError());
		assertTrue(YamlTypeProvider.INSTANCE.getMap(wrongType).isError());
		
		YamlSequence emptySequence = new YamlSequence();
		assertTrue(YamlTypeProvider.INSTANCE.getList(emptySequence).resultOrThrow().isEmpty());
		
		YamlSequence sequenceWithElements = new YamlSequence(List.of(new YamlScalar("a"), new YamlScalar("b")));
		List<YamlElement> listResult = YamlTypeProvider.INSTANCE.getList(sequenceWithElements).resultOrThrow();
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.getFirst().getAsYamlScalar().getAsString());
		
		YamlMapping emptyMapping = new YamlMapping();
		assertTrue(YamlTypeProvider.INSTANCE.getMap(emptyMapping).resultOrThrow().isEmpty());
		
		YamlMapping mappingWithElements = new YamlMapping(Map.of("key", new YamlScalar("value")));
		Map<String, YamlElement> mapResult = YamlTypeProvider.INSTANCE.getMap(mappingWithElements).resultOrThrow();
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsYamlScalar().getAsString());
	}
	
	@Test
	void mapOperations() {
		YamlMapping yamlMapping = new YamlMapping();
		YamlElement testValue = new YamlScalar("test");
		
		Result<Boolean> nullHas = YamlTypeProvider.INSTANCE.has(null, "key");
		assertTrue(nullHas.isError());
		assertTrue(nullHas.errorOrThrow().startsWith("Value 'null'"));
		Result<Boolean> hasNullKey = YamlTypeProvider.INSTANCE.has(yamlMapping, null);
		assertTrue(hasNullKey.isError());
		assertTrue(hasNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<YamlElement> nullGet = YamlTypeProvider.INSTANCE.get(null, "key");
		assertTrue(nullGet.isError());
		assertTrue(nullGet.errorOrThrow().startsWith("Value 'null'"));
		Result<YamlElement> getNullKey = YamlTypeProvider.INSTANCE.get(yamlMapping, null);
		assertTrue(getNullKey.isError());
		assertTrue(getNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<YamlElement> nullSet = YamlTypeProvider.INSTANCE.set(null, "key", testValue);
		assertTrue(nullSet.isError());
		assertTrue(nullSet.errorOrThrow().startsWith("Value 'null'"));
		Result<YamlElement> setNullKey = YamlTypeProvider.INSTANCE.set(yamlMapping, null, testValue);
		assertTrue(setNullKey.isError());
		assertTrue(setNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<YamlElement> nullValueSet = YamlTypeProvider.INSTANCE.set(yamlMapping, "key", (YamlElement) null);
		assertTrue(nullValueSet.isError());
		assertTrue(nullValueSet.errorOrThrow().startsWith("Value 'null'"));
		
		YamlSequence wrongType = new YamlSequence();
		assertTrue(YamlTypeProvider.INSTANCE.has(wrongType, "key").isError());
		assertTrue(YamlTypeProvider.INSTANCE.get(wrongType, "key").isError());
		assertTrue(YamlTypeProvider.INSTANCE.set(wrongType, "key", testValue).isError());
		
		assertFalse(YamlTypeProvider.INSTANCE.has(yamlMapping, "key").resultOrThrow());
		assertNull(YamlTypeProvider.INSTANCE.get(yamlMapping, "key").resultOrThrow());
		
		assertEquals(yamlMapping, YamlTypeProvider.INSTANCE.set(yamlMapping, "key", testValue).resultOrThrow());
		assertTrue(YamlTypeProvider.INSTANCE.has(yamlMapping, "key").resultOrThrow());
		assertEquals(testValue, YamlTypeProvider.INSTANCE.get(yamlMapping, "key").resultOrThrow());
	}
	
	@Test
	void mapOperationsWithResults() {
		YamlMapping yamlMapping = new YamlMapping();
		YamlElement testValue = new YamlScalar("test");
		
		Result<YamlElement> nullType = YamlTypeProvider.INSTANCE.set(null, "key", Result.success(testValue));
		assertTrue(nullType.isError());
		assertTrue(nullType.errorOrThrow().startsWith("Type 'null'"));
		Result<YamlElement> nullKey = YamlTypeProvider.INSTANCE.set(yamlMapping, null, Result.success(testValue));
		assertTrue(nullKey.isError());
		assertTrue(nullKey.errorOrThrow().startsWith("Key 'null'"));
		Result<YamlElement> nullValue = YamlTypeProvider.INSTANCE.set(yamlMapping, "key", (Result<YamlElement>) null);
		assertTrue(nullValue.isError());
		assertTrue(nullValue.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(YamlTypeProvider.INSTANCE.set(yamlMapping, "key", Result.error("error")).isError());
		assertTrue(YamlTypeProvider.INSTANCE.set(yamlMapping, "key", Result.success(testValue)).isSuccess());
		assertEquals(testValue, yamlMapping.get("key"));
	}
	
	@Test
	void mergeOperations() {
		assertEquals(YamlNull.INSTANCE, YamlTypeProvider.INSTANCE.merge(null, YamlNull.INSTANCE).resultOrThrow());
		assertEquals(YamlNull.INSTANCE, YamlTypeProvider.INSTANCE.merge(YamlNull.INSTANCE, (YamlElement) null).resultOrThrow());
		
		YamlElement scalar = new YamlScalar(1);
		YamlSequence sequence1 = new YamlSequence(List.of(new YamlScalar("a")));
		YamlSequence sequence2 = new YamlSequence(List.of(new YamlScalar("b")));
		YamlMapping mapping1 = new YamlMapping(Map.of("key1", new YamlScalar("value1")));
		YamlMapping mapping2 = new YamlMapping(Map.of("key2", new YamlScalar("value2")));
		
		assertEquals(scalar, YamlTypeProvider.INSTANCE.merge(YamlTypeProvider.INSTANCE.empty(), scalar).resultOrThrow());
		assertEquals(sequence1, YamlTypeProvider.INSTANCE.merge(YamlTypeProvider.INSTANCE.empty(), sequence1).resultOrThrow());
		assertEquals(mapping1, YamlTypeProvider.INSTANCE.merge(YamlTypeProvider.INSTANCE.empty(), mapping1).resultOrThrow());
		
		assertEquals(YamlNull.INSTANCE, YamlTypeProvider.INSTANCE.merge(YamlTypeProvider.INSTANCE.empty(), YamlNull.INSTANCE).resultOrThrow());
		
		assertEquals(scalar, YamlTypeProvider.INSTANCE.merge(scalar, YamlNull.INSTANCE).resultOrThrow());
		assertEquals(scalar, YamlTypeProvider.INSTANCE.merge(YamlNull.INSTANCE, scalar).resultOrThrow());
		
		assertEquals(sequence1, YamlTypeProvider.INSTANCE.merge(sequence1, YamlNull.INSTANCE).resultOrThrow());
		assertEquals(sequence1, YamlTypeProvider.INSTANCE.merge(YamlNull.INSTANCE, sequence1).resultOrThrow());
		
		assertEquals(mapping1, YamlTypeProvider.INSTANCE.merge(mapping1, YamlNull.INSTANCE).resultOrThrow());
		assertEquals(mapping1, YamlTypeProvider.INSTANCE.merge(YamlNull.INSTANCE, mapping1).resultOrThrow());
		
		YamlSequence mergedSequence = YamlTypeProvider.INSTANCE.merge(sequence1, sequence2).resultOrThrow().getAsYamlSequence();
		assertEquals(2, mergedSequence.size());
		assertEquals("a", mergedSequence.get(0).getAsYamlScalar().getAsString());
		assertEquals("b", mergedSequence.get(1).getAsYamlScalar().getAsString());
		
		YamlMapping mergedMapping = YamlTypeProvider.INSTANCE.merge(mapping1, mapping2).resultOrThrow().getAsYamlMapping();
		assertEquals(2, mergedMapping.size());
		assertEquals("value1", mergedMapping.get("key1").getAsYamlScalar().getAsString());
		assertEquals("value2", mergedMapping.get("key2").getAsYamlScalar().getAsString());
		
		assertTrue(YamlTypeProvider.INSTANCE.merge(scalar, sequence1).isError());
		assertTrue(YamlTypeProvider.INSTANCE.merge(scalar, mapping1).isError());
		assertTrue(YamlTypeProvider.INSTANCE.merge(sequence1, scalar).isError());
		assertTrue(YamlTypeProvider.INSTANCE.merge(sequence1, mapping1).isError());
		assertTrue(YamlTypeProvider.INSTANCE.merge(mapping1, scalar).isError());
		assertTrue(YamlTypeProvider.INSTANCE.merge(mapping1, sequence1).isError());
	}
	
	@Test
	void mergeOperationsWithResults() {
		YamlMapping yamlMapping = new YamlMapping();
		YamlElement testValue = new YamlScalar("test");
		
		Result<YamlElement> nullCurrent = YamlTypeProvider.INSTANCE.merge(null, Result.success(testValue));
		assertTrue(nullCurrent.isError());
		assertTrue(nullCurrent.errorOrThrow().startsWith("Current value 'null'"));
		Result<YamlElement> nullValue = YamlTypeProvider.INSTANCE.merge(yamlMapping, (Result<YamlElement>) null);
		assertTrue(nullValue.isError());
		assertTrue(nullValue.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(YamlTypeProvider.INSTANCE.merge(yamlMapping, Result.error("error")).isError());
		assertEquals(testValue, YamlTypeProvider.INSTANCE.merge(YamlTypeProvider.INSTANCE.empty(), Result.success(testValue)).resultOrThrow());
	}
}
