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
		assertEquals(YamlNull.INSTANCE, YamlTypeProvider.INSTANCE.createNull(RuntimeException::new));
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new YamlScalar(true), YamlTypeProvider.INSTANCE.createBoolean(true, RuntimeException::new));
		assertEquals(new YamlScalar((byte) 42), YamlTypeProvider.INSTANCE.createByte((byte) 42, RuntimeException::new));
		assertEquals(new YamlScalar((short) 42), YamlTypeProvider.INSTANCE.createShort((short) 42, RuntimeException::new));
		assertEquals(new YamlScalar(42), YamlTypeProvider.INSTANCE.createInteger(42, RuntimeException::new));
		assertEquals(new YamlScalar(42L), YamlTypeProvider.INSTANCE.createLong(42L, RuntimeException::new));
		assertEquals(new YamlScalar(42.5f), YamlTypeProvider.INSTANCE.createFloat(42.5f, RuntimeException::new));
		assertEquals(new YamlScalar(42.5), YamlTypeProvider.INSTANCE.createDouble(42.5, RuntimeException::new));
		assertEquals(new YamlScalar("test"), YamlTypeProvider.INSTANCE.createString("test", RuntimeException::new));
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.createString(null, RuntimeException::new));
	}
	
	@Test
	void createCollectionTypes() {
		YamlElement element1 = new YamlScalar("a");
		YamlElement element2 = new YamlScalar("b");
		
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.createList(null, RuntimeException::new));
		
		YamlSequence emptySequence = new YamlSequence();
		assertEquals(emptySequence, YamlTypeProvider.INSTANCE.createList(List.of(), RuntimeException::new));
		
		YamlSequence sequenceWithElements = new YamlSequence(List.of(element1, element2));
		assertEquals(sequenceWithElements, YamlTypeProvider.INSTANCE.createList(List.of(element1, element2), RuntimeException::new));
		
		YamlMapping emptyMapping = new YamlMapping();
		assertEquals(emptyMapping, YamlTypeProvider.INSTANCE.createMap(RuntimeException::new));
		assertEquals(emptyMapping, YamlTypeProvider.INSTANCE.createMap(Map.of(), RuntimeException::new));
		
		YamlMapping mappingWithElements = new YamlMapping(Map.of("key1", element1, "key2", element2));
		assertEquals(mappingWithElements, YamlTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2), RuntimeException::new));
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.isEmpty(null, RuntimeException::new));
		assertFalse(YamlTypeProvider.INSTANCE.isEmpty(new YamlSequence(), RuntimeException::new));
		assertFalse(YamlTypeProvider.INSTANCE.isEmpty(new YamlScalar(1), RuntimeException::new));
		assertFalse(YamlTypeProvider.INSTANCE.isEmpty(new YamlMapping(), RuntimeException::new));
		assertFalse(YamlTypeProvider.INSTANCE.isEmpty(YamlNull.INSTANCE, RuntimeException::new));
	}
	
	@Test
	void isNullValidation() {
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.isNull(null, RuntimeException::new));
		
		assertTrue(YamlTypeProvider.INSTANCE.isNull(YamlNull.INSTANCE, RuntimeException::new));
		
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlSequence(), RuntimeException::new));
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlMapping(), RuntimeException::new));
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlScalar(1), RuntimeException::new));
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlScalar(true), RuntimeException::new));
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlScalar("test"), RuntimeException::new));
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getBoolean(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getByte(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getShort(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getInteger(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getLong(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getFloat(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getDouble(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getString(null, RuntimeException::new));
		
		YamlSequence wrongType = new YamlSequence();
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getBoolean(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getByte(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getShort(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getInteger(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getLong(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getFloat(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getDouble(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getString(wrongType, RuntimeException::new));
		
		YamlScalar invalidValue = new YamlScalar("not-a-number");
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getBoolean(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getByte(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getShort(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getInteger(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getLong(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getFloat(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getDouble(invalidValue, RuntimeException::new));
		
		assertTrue(YamlTypeProvider.INSTANCE.getBoolean(new YamlScalar(true), RuntimeException::new));
		assertEquals((byte) 42, YamlTypeProvider.INSTANCE.getByte(new YamlScalar((byte) 42), RuntimeException::new));
		assertEquals((short) 42, YamlTypeProvider.INSTANCE.getShort(new YamlScalar((short) 42), RuntimeException::new));
		assertEquals(42, YamlTypeProvider.INSTANCE.getInteger(new YamlScalar(42), RuntimeException::new));
		assertEquals(42L, YamlTypeProvider.INSTANCE.getLong(new YamlScalar(42L), RuntimeException::new));
		assertEquals(42.5f, YamlTypeProvider.INSTANCE.getFloat(new YamlScalar(42.5f), RuntimeException::new));
		assertEquals(42.5, YamlTypeProvider.INSTANCE.getDouble(new YamlScalar(42.5), RuntimeException::new));
		assertEquals("test", YamlTypeProvider.INSTANCE.getString(new YamlScalar("test"), RuntimeException::new));
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getList(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getMap(null, RuntimeException::new));
		
		YamlScalar wrongType = new YamlScalar(1);
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getList(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.getMap(wrongType, RuntimeException::new));
		
		YamlSequence emptySequence = new YamlSequence();
		assertTrue(YamlTypeProvider.INSTANCE.getList(emptySequence, RuntimeException::new).isEmpty());
		
		YamlSequence sequenceWithElements = new YamlSequence(List.of(new YamlScalar("a"), new YamlScalar("b")));
		List<YamlElement> listResult = YamlTypeProvider.INSTANCE.getList(sequenceWithElements, RuntimeException::new);
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.getFirst().getAsYamlScalar().getAsString());
		
		YamlMapping emptyMapping = new YamlMapping();
		assertTrue(YamlTypeProvider.INSTANCE.getMap(emptyMapping, RuntimeException::new).isEmpty());
		
		YamlMapping mappingWithElements = new YamlMapping(Map.of("key", new YamlScalar("value")));
		Map<String, YamlElement> mapResult = YamlTypeProvider.INSTANCE.getMap(mappingWithElements, RuntimeException::new);
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsYamlScalar().getAsString());
	}
	
	@Test
	void mapOperations() {
		YamlMapping yamlMapping = new YamlMapping();
		YamlElement testValue = new YamlScalar("test");
		
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.has(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.has(yamlMapping, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.get(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.get(yamlMapping, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.set(null, "key", testValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.set(yamlMapping, null, testValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.set(yamlMapping, "key", null, RuntimeException::new));
		
		YamlSequence wrongType = new YamlSequence();
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.has(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.get(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.set(wrongType, "key", testValue, RuntimeException::new));
		
		assertFalse(YamlTypeProvider.INSTANCE.has(yamlMapping, "key", RuntimeException::new));
		
		YamlTypeProvider.INSTANCE.set(yamlMapping, "key", testValue, RuntimeException::new);
		assertTrue(YamlTypeProvider.INSTANCE.has(yamlMapping, "key", RuntimeException::new));
		assertEquals(testValue, YamlTypeProvider.INSTANCE.get(yamlMapping, "key", RuntimeException::new));
	}
	
	@Test
	void mergeOperations() {
		assertEquals(YamlNull.INSTANCE, YamlTypeProvider.INSTANCE.merge(null, YamlNull.INSTANCE, RuntimeException::new));
		assertEquals(YamlNull.INSTANCE, YamlTypeProvider.INSTANCE.merge(YamlNull.INSTANCE, null, RuntimeException::new));
		
		YamlElement scalar = new YamlScalar(1);
		YamlSequence sequence1 = new YamlSequence(List.of(new YamlScalar("a")));
		YamlSequence sequence2 = new YamlSequence(List.of(new YamlScalar("b")));
		YamlMapping mapping1 = new YamlMapping(Map.of("key1", new YamlScalar("value1")));
		YamlMapping mapping2 = new YamlMapping(Map.of("key2", new YamlScalar("value2")));
		
		assertEquals(scalar, YamlTypeProvider.INSTANCE.merge(YamlTypeProvider.INSTANCE.empty(), scalar, RuntimeException::new));
		assertEquals(sequence1, YamlTypeProvider.INSTANCE.merge(YamlTypeProvider.INSTANCE.empty(), sequence1, RuntimeException::new));
		assertEquals(mapping1, YamlTypeProvider.INSTANCE.merge(YamlTypeProvider.INSTANCE.empty(), mapping1, RuntimeException::new));
		
		assertEquals(YamlNull.INSTANCE, YamlTypeProvider.INSTANCE.merge(YamlTypeProvider.INSTANCE.empty(), YamlNull.INSTANCE, RuntimeException::new));
		
		assertEquals(scalar, YamlTypeProvider.INSTANCE.merge(scalar, YamlNull.INSTANCE, RuntimeException::new));
		assertEquals(scalar, YamlTypeProvider.INSTANCE.merge(YamlNull.INSTANCE, scalar, RuntimeException::new));
		
		assertEquals(sequence1, YamlTypeProvider.INSTANCE.merge(sequence1, YamlNull.INSTANCE, RuntimeException::new));
		assertEquals(sequence1, YamlTypeProvider.INSTANCE.merge(YamlNull.INSTANCE, sequence1, RuntimeException::new));
		
		assertEquals(mapping1, YamlTypeProvider.INSTANCE.merge(mapping1, YamlNull.INSTANCE, RuntimeException::new));
		assertEquals(mapping1, YamlTypeProvider.INSTANCE.merge(YamlNull.INSTANCE, mapping1, RuntimeException::new));
		
		YamlSequence mergedSequence = YamlTypeProvider.INSTANCE.merge(sequence1, sequence2, RuntimeException::new).getAsYamlSequence();
		assertEquals(2, mergedSequence.size());
		assertEquals("a", mergedSequence.get(0).getAsYamlScalar().getAsString());
		assertEquals("b", mergedSequence.get(1).getAsYamlScalar().getAsString());
		
		YamlMapping mergedMapping = YamlTypeProvider.INSTANCE.merge(mapping1, mapping2, RuntimeException::new).getAsYamlMapping();
		assertEquals(2, mergedMapping.size());
		assertEquals("value1", mergedMapping.get("key1").getAsYamlScalar().getAsString());
		assertEquals("value2", mergedMapping.get("key2").getAsYamlScalar().getAsString());
		
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.merge(scalar, sequence1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.merge(scalar, mapping1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.merge(sequence1, scalar, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.merge(sequence1, mapping1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.merge(mapping1, scalar, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> YamlTypeProvider.INSTANCE.merge(mapping1, sequence1, RuntimeException::new));
	}
}
