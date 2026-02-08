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
		assertEquals(IniNull.INSTANCE, IniTypeProvider.INSTANCE.createNull(RuntimeException::new));
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new IniValue(true), IniTypeProvider.INSTANCE.createBoolean(true, RuntimeException::new));
		assertEquals(new IniValue((byte) 42), IniTypeProvider.INSTANCE.createByte((byte) 42, RuntimeException::new));
		assertEquals(new IniValue((short) 42), IniTypeProvider.INSTANCE.createShort((short) 42, RuntimeException::new));
		assertEquals(new IniValue(42), IniTypeProvider.INSTANCE.createInteger(42, RuntimeException::new));
		assertEquals(new IniValue(42L), IniTypeProvider.INSTANCE.createLong(42L, RuntimeException::new));
		assertEquals(new IniValue(42.5f), IniTypeProvider.INSTANCE.createFloat(42.5f, RuntimeException::new));
		assertEquals(new IniValue(42.5), IniTypeProvider.INSTANCE.createDouble(42.5, RuntimeException::new));
		assertEquals(new IniValue("test"), IniTypeProvider.INSTANCE.createString("test", RuntimeException::new));
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.createString(null, RuntimeException::new));
	}
	
	@Test
	void createListReturnsError() {
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.createList(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.createList(List.of(), RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.createList(List.of(new IniValue("a")), RuntimeException::new));
	}
	
	@Test
	void createMapTypes() {
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.createMap(null, RuntimeException::new));
		
		IniElement emptySection = IniTypeProvider.INSTANCE.createMap(RuntimeException::new);
		assertTrue(emptySection.isIniSection());
		assertTrue(emptySection.getAsIniSection().isEmpty());
		
		IniElement element1 = new IniValue("value1");
		IniElement element2 = new IniValue("value2");
		
		IniElement sectionWithElements = IniTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2), RuntimeException::new);
		assertTrue(sectionWithElements.isIniSection());
		assertEquals(2, sectionWithElements.getAsIniSection().size());
		assertEquals(element1, sectionWithElements.getAsIniSection().get("key1"));
		assertEquals(element2, sectionWithElements.getAsIniSection().get("key2"));
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.isEmpty(null, RuntimeException::new));
		assertFalse(IniTypeProvider.INSTANCE.isEmpty(new IniSection("test"), RuntimeException::new));
		assertFalse(IniTypeProvider.INSTANCE.isEmpty(new IniValue(1), RuntimeException::new));
		assertFalse(IniTypeProvider.INSTANCE.isEmpty(IniNull.INSTANCE, RuntimeException::new));
	}
	
	@Test
	void isNullValidation() {
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.isNull(null, RuntimeException::new));
		
		assertTrue(IniTypeProvider.INSTANCE.isNull(IniNull.INSTANCE, RuntimeException::new));
		
		assertFalse(IniTypeProvider.INSTANCE.isNull(new IniSection("test"), RuntimeException::new));
		assertFalse(IniTypeProvider.INSTANCE.isNull(new IniValue(1), RuntimeException::new));
		assertFalse(IniTypeProvider.INSTANCE.isNull(new IniValue(true), RuntimeException::new));
		assertFalse(IniTypeProvider.INSTANCE.isNull(new IniValue("test"), RuntimeException::new));
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getBoolean(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getByte(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getShort(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getInteger(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getLong(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getFloat(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getDouble(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getString(null, RuntimeException::new));
		
		IniSection wrongType = new IniSection("test");
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getBoolean(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getByte(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getShort(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getInteger(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getLong(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getFloat(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getDouble(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getString(wrongType, RuntimeException::new));
		
		IniValue invalidValue = new IniValue("not-a-number");
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getBoolean(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getByte(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getShort(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getInteger(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getLong(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getFloat(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getDouble(invalidValue, RuntimeException::new));
		
		assertTrue(IniTypeProvider.INSTANCE.getBoolean(new IniValue(true), RuntimeException::new));
		assertEquals((byte) 42, IniTypeProvider.INSTANCE.getByte(new IniValue((byte) 42), RuntimeException::new));
		assertEquals((short) 42, IniTypeProvider.INSTANCE.getShort(new IniValue((short) 42), RuntimeException::new));
		assertEquals(42, IniTypeProvider.INSTANCE.getInteger(new IniValue(42), RuntimeException::new));
		assertEquals(42L, IniTypeProvider.INSTANCE.getLong(new IniValue(42L), RuntimeException::new));
		assertEquals(42.5f, IniTypeProvider.INSTANCE.getFloat(new IniValue(42.5f), RuntimeException::new));
		assertEquals(42.5, IniTypeProvider.INSTANCE.getDouble(new IniValue(42.5), RuntimeException::new));
		assertEquals("test", IniTypeProvider.INSTANCE.getString(new IniValue("test"), RuntimeException::new));
	}
	
	@Test
	void getListReturnsError() {
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getList(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getList(new IniValue(1), RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getList(new IniSection("test"), RuntimeException::new));
	}
	
	@Test
	void getMapTypes() {
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getMap(null, RuntimeException::new));
		
		IniValue wrongType = new IniValue(1);
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.getMap(wrongType, RuntimeException::new));
		
		IniSection emptySection = new IniSection("test");
		assertTrue(IniTypeProvider.INSTANCE.getMap(emptySection, RuntimeException::new).isEmpty());
		
		IniSection sectionWithElements = new IniSection("test");
		sectionWithElements.add("key", new IniValue("value"));
		Map<String, IniElement> mapResult = IniTypeProvider.INSTANCE.getMap(sectionWithElements, RuntimeException::new);
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsIniValue().getAsString());
	}
	
	@Test
	void mapOperations() {
		IniSection iniSection = new IniSection("test");
		IniElement testValue = new IniValue("test");
		
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.has(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.has(iniSection, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.get(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.get(iniSection, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.set(null, "key", testValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.set(iniSection, null, testValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.set(iniSection, "key", null, RuntimeException::new));
		
		IniValue wrongType = new IniValue(1);
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.has(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.get(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.set(wrongType, "key", testValue, RuntimeException::new));
		
		assertFalse(IniTypeProvider.INSTANCE.has(iniSection, "key", RuntimeException::new));
		
		IniTypeProvider.INSTANCE.set(iniSection, "key", testValue, RuntimeException::new);
		assertTrue(IniTypeProvider.INSTANCE.has(iniSection, "key", RuntimeException::new));
		assertEquals(testValue, IniTypeProvider.INSTANCE.get(iniSection, "key", RuntimeException::new));
	}
	
	@Test
	void mergeOperations() {
		assertEquals(IniNull.INSTANCE, IniTypeProvider.INSTANCE.merge(null, IniNull.INSTANCE, RuntimeException::new));
		assertEquals(IniNull.INSTANCE, IniTypeProvider.INSTANCE.merge(IniNull.INSTANCE, null, RuntimeException::new));
		
		IniElement value = new IniValue(1);
		IniSection section1 = new IniSection("section1");
		section1.add("key1", new IniValue("value1"));
		IniSection section2 = new IniSection("section2");
		section2.add("key2", new IniValue("value2"));
		
		assertEquals(value, IniTypeProvider.INSTANCE.merge(IniTypeProvider.INSTANCE.empty(), value, RuntimeException::new));
		assertEquals(section1, IniTypeProvider.INSTANCE.merge(IniTypeProvider.INSTANCE.empty(), section1, RuntimeException::new));
		
		assertEquals(IniNull.INSTANCE, IniTypeProvider.INSTANCE.merge(IniTypeProvider.INSTANCE.empty(), IniNull.INSTANCE, RuntimeException::new));
		
		assertEquals(value, IniTypeProvider.INSTANCE.merge(value, IniNull.INSTANCE, RuntimeException::new));
		assertEquals(value, IniTypeProvider.INSTANCE.merge(IniNull.INSTANCE, value, RuntimeException::new));
		
		assertEquals(section1, IniTypeProvider.INSTANCE.merge(section1, IniNull.INSTANCE, RuntimeException::new));
		assertEquals(section1, IniTypeProvider.INSTANCE.merge(IniNull.INSTANCE, section1, RuntimeException::new));
		
		IniSection mergedSection = IniTypeProvider.INSTANCE.merge(section1, section2, RuntimeException::new).getAsIniSection();
		assertEquals(2, mergedSection.size());
		assertEquals("value1", mergedSection.get("key1").getAsIniValue().getAsString());
		assertEquals("value2", mergedSection.get("key2").getAsIniValue().getAsString());
		
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.merge(value, section1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> IniTypeProvider.INSTANCE.merge(section1, value, RuntimeException::new));
	}
}
