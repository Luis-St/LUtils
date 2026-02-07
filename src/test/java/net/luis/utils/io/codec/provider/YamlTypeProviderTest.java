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
		assertEquals(YamlNull.INSTANCE, YamlTypeProvider.INSTANCE.createNull());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new YamlScalar(true), YamlTypeProvider.INSTANCE.createBoolean(true));
		assertEquals(new YamlScalar((byte) 42), YamlTypeProvider.INSTANCE.createByte((byte) 42));
		assertEquals(new YamlScalar((short) 42), YamlTypeProvider.INSTANCE.createShort((short) 42));
		assertEquals(new YamlScalar(42), YamlTypeProvider.INSTANCE.createInteger(42));
		assertEquals(new YamlScalar(42L), YamlTypeProvider.INSTANCE.createLong(42L));
		assertEquals(new YamlScalar(42.5f), YamlTypeProvider.INSTANCE.createFloat(42.5f));
		assertEquals(new YamlScalar(42.5), YamlTypeProvider.INSTANCE.createDouble(42.5));
		assertEquals(new YamlScalar("test"), YamlTypeProvider.INSTANCE.createString("test"));
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.createString(null));
	}
	
	@Test
	void createCollectionTypes() {
		YamlElement element1 = new YamlScalar("a");
		YamlElement element2 = new YamlScalar("b");
		
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.createList(null));
		
		YamlSequence emptySequence = new YamlSequence();
		assertEquals(emptySequence, YamlTypeProvider.INSTANCE.createList(List.of()));
		
		YamlSequence sequenceWithElements = new YamlSequence(List.of(element1, element2));
		assertEquals(sequenceWithElements, YamlTypeProvider.INSTANCE.createList(List.of(element1, element2)));
		
		YamlMapping emptyMapping = new YamlMapping();
		assertEquals(emptyMapping, YamlTypeProvider.INSTANCE.createMap());
		assertEquals(emptyMapping, YamlTypeProvider.INSTANCE.createMap(Map.of()));
		
		YamlMapping mappingWithElements = new YamlMapping(Map.of("key1", element1, "key2", element2));
		assertEquals(mappingWithElements, YamlTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2)));
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.isEmpty(null));
		assertFalse(YamlTypeProvider.INSTANCE.isEmpty(new YamlSequence()));
		assertFalse(YamlTypeProvider.INSTANCE.isEmpty(new YamlScalar(1)));
		assertFalse(YamlTypeProvider.INSTANCE.isEmpty(new YamlMapping()));
		assertFalse(YamlTypeProvider.INSTANCE.isEmpty(YamlNull.INSTANCE));
	}
	
	@Test
	void isNullValidation() {
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.isNull(null));
		
		assertTrue(YamlTypeProvider.INSTANCE.isNull(YamlNull.INSTANCE));
		
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlSequence()));
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlMapping()));
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlScalar(1)));
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlScalar(true)));
		assertFalse(YamlTypeProvider.INSTANCE.isNull(new YamlScalar("test")));
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getBoolean(null));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getByte(null));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getShort(null));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getInteger(null));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getLong(null));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getFloat(null));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getDouble(null));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getString(null));
		
		YamlSequence wrongType = new YamlSequence();
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getBoolean(wrongType));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getByte(wrongType));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getShort(wrongType));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getInteger(wrongType));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getLong(wrongType));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getFloat(wrongType));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getDouble(wrongType));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getString(wrongType));
		
		YamlScalar invalidValue = new YamlScalar("not-a-number");
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getBoolean(invalidValue));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getByte(invalidValue));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getShort(invalidValue));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getInteger(invalidValue));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getLong(invalidValue));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getFloat(invalidValue));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getDouble(invalidValue));
		
		assertTrue(YamlTypeProvider.INSTANCE.getBoolean(new YamlScalar(true)));
		assertEquals((byte) 42, YamlTypeProvider.INSTANCE.getByte(new YamlScalar((byte) 42)));
		assertEquals((short) 42, YamlTypeProvider.INSTANCE.getShort(new YamlScalar((short) 42)));
		assertEquals(42, YamlTypeProvider.INSTANCE.getInteger(new YamlScalar(42)));
		assertEquals(42L, YamlTypeProvider.INSTANCE.getLong(new YamlScalar(42L)));
		assertEquals(42.5f, YamlTypeProvider.INSTANCE.getFloat(new YamlScalar(42.5f)));
		assertEquals(42.5, YamlTypeProvider.INSTANCE.getDouble(new YamlScalar(42.5)));
		assertEquals("test", YamlTypeProvider.INSTANCE.getString(new YamlScalar("test")));
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getList(null));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getMap(null));
		
		YamlScalar wrongType = new YamlScalar(1);
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getList(wrongType));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.getMap(wrongType));
		
		YamlSequence emptySequence = new YamlSequence();
		assertTrue(YamlTypeProvider.INSTANCE.getList(emptySequence).isEmpty());
		
		YamlSequence sequenceWithElements = new YamlSequence(List.of(new YamlScalar("a"), new YamlScalar("b")));
		List<YamlElement> listResult = YamlTypeProvider.INSTANCE.getList(sequenceWithElements);
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.getFirst().getAsYamlScalar().getAsString());
		
		YamlMapping emptyMapping = new YamlMapping();
		assertTrue(YamlTypeProvider.INSTANCE.getMap(emptyMapping).isEmpty());
		
		YamlMapping mappingWithElements = new YamlMapping(Map.of("key", new YamlScalar("value")));
		Map<String, YamlElement> mapResult = YamlTypeProvider.INSTANCE.getMap(mappingWithElements);
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsYamlScalar().getAsString());
	}
	
	@Test
	void mapOperations() {
		YamlMapping yamlMapping = new YamlMapping();
		YamlElement testValue = new YamlScalar("test");
		
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.has(null, "key"));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.has(yamlMapping, null));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.get(null, "key"));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.get(yamlMapping, null));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.set(null, "key", testValue));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.set(yamlMapping, null, testValue));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.set(yamlMapping, "key", null));
		
		YamlSequence wrongType = new YamlSequence();
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.has(wrongType, "key"));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.get(wrongType, "key"));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.set(wrongType, "key", testValue));
		
		assertFalse(YamlTypeProvider.INSTANCE.has(yamlMapping, "key"));
		
		YamlTypeProvider.INSTANCE.set(yamlMapping, "key", testValue);
		assertTrue(YamlTypeProvider.INSTANCE.has(yamlMapping, "key"));
		assertEquals(testValue, YamlTypeProvider.INSTANCE.get(yamlMapping, "key"));
	}
	
	@Test
	void mergeOperations() {
		assertEquals(YamlNull.INSTANCE, YamlTypeProvider.INSTANCE.merge(null, YamlNull.INSTANCE));
		assertEquals(YamlNull.INSTANCE, YamlTypeProvider.INSTANCE.merge(YamlNull.INSTANCE, null));
		
		YamlElement scalar = new YamlScalar(1);
		YamlSequence sequence1 = new YamlSequence(List.of(new YamlScalar("a")));
		YamlSequence sequence2 = new YamlSequence(List.of(new YamlScalar("b")));
		YamlMapping mapping1 = new YamlMapping(Map.of("key1", new YamlScalar("value1")));
		YamlMapping mapping2 = new YamlMapping(Map.of("key2", new YamlScalar("value2")));
		
		assertEquals(scalar, YamlTypeProvider.INSTANCE.merge(YamlTypeProvider.INSTANCE.empty(), scalar));
		assertEquals(sequence1, YamlTypeProvider.INSTANCE.merge(YamlTypeProvider.INSTANCE.empty(), sequence1));
		assertEquals(mapping1, YamlTypeProvider.INSTANCE.merge(YamlTypeProvider.INSTANCE.empty(), mapping1));
		
		assertEquals(YamlNull.INSTANCE, YamlTypeProvider.INSTANCE.merge(YamlTypeProvider.INSTANCE.empty(), YamlNull.INSTANCE));
		
		assertEquals(scalar, YamlTypeProvider.INSTANCE.merge(scalar, YamlNull.INSTANCE));
		assertEquals(scalar, YamlTypeProvider.INSTANCE.merge(YamlNull.INSTANCE, scalar));
		
		assertEquals(sequence1, YamlTypeProvider.INSTANCE.merge(sequence1, YamlNull.INSTANCE));
		assertEquals(sequence1, YamlTypeProvider.INSTANCE.merge(YamlNull.INSTANCE, sequence1));
		
		assertEquals(mapping1, YamlTypeProvider.INSTANCE.merge(mapping1, YamlNull.INSTANCE));
		assertEquals(mapping1, YamlTypeProvider.INSTANCE.merge(YamlNull.INSTANCE, mapping1));
		
		YamlSequence mergedSequence = YamlTypeProvider.INSTANCE.merge(sequence1, sequence2).getAsYamlSequence();
		assertEquals(2, mergedSequence.size());
		assertEquals("a", mergedSequence.get(0).getAsYamlScalar().getAsString());
		assertEquals("b", mergedSequence.get(1).getAsYamlScalar().getAsString());
		
		YamlMapping mergedMapping = YamlTypeProvider.INSTANCE.merge(mapping1, mapping2).getAsYamlMapping();
		assertEquals(2, mergedMapping.size());
		assertEquals("value1", mergedMapping.get("key1").getAsYamlScalar().getAsString());
		assertEquals("value2", mergedMapping.get("key2").getAsYamlScalar().getAsString());
		
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.merge(scalar, sequence1));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.merge(scalar, mapping1));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.merge(sequence1, scalar));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.merge(sequence1, mapping1));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.merge(mapping1, scalar));
		assertThrows(TypeProviderException.class, () -> YamlTypeProvider.INSTANCE.merge(mapping1, sequence1));
	}
}
