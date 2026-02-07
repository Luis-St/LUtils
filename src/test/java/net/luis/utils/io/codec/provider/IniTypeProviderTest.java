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
		assertEquals(IniNull.INSTANCE, IniTypeProvider.INSTANCE.createNull());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new IniValue(true), IniTypeProvider.INSTANCE.createBoolean(true));
		assertEquals(new IniValue((byte) 42), IniTypeProvider.INSTANCE.createByte((byte) 42));
		assertEquals(new IniValue((short) 42), IniTypeProvider.INSTANCE.createShort((short) 42));
		assertEquals(new IniValue(42), IniTypeProvider.INSTANCE.createInteger(42));
		assertEquals(new IniValue(42L), IniTypeProvider.INSTANCE.createLong(42L));
		assertEquals(new IniValue(42.5f), IniTypeProvider.INSTANCE.createFloat(42.5f));
		assertEquals(new IniValue(42.5), IniTypeProvider.INSTANCE.createDouble(42.5));
		assertEquals(new IniValue("test"), IniTypeProvider.INSTANCE.createString("test"));
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.createString(null));
	}
	
	@Test
	void createListReturnsError() {
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.createList(null));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.createList(List.of()));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.createList(List.of(new IniValue("a"))));
	}
	
	@Test
	void createMapTypes() {
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.createMap(null));
		
		IniElement emptySection = IniTypeProvider.INSTANCE.createMap();
		assertTrue(emptySection.isIniSection());
		assertTrue(emptySection.getAsIniSection().isEmpty());
		
		IniElement element1 = new IniValue("value1");
		IniElement element2 = new IniValue("value2");
		
		IniElement sectionWithElements = IniTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2));
		assertTrue(sectionWithElements.isIniSection());
		assertEquals(2, sectionWithElements.getAsIniSection().size());
		assertEquals(element1, sectionWithElements.getAsIniSection().get("key1"));
		assertEquals(element2, sectionWithElements.getAsIniSection().get("key2"));
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.isEmpty(null));
		assertFalse(IniTypeProvider.INSTANCE.isEmpty(new IniSection("test")));
		assertFalse(IniTypeProvider.INSTANCE.isEmpty(new IniValue(1)));
		assertFalse(IniTypeProvider.INSTANCE.isEmpty(IniNull.INSTANCE));
	}
	
	@Test
	void isNullValidation() {
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.isNull(null));
		
		assertTrue(IniTypeProvider.INSTANCE.isNull(IniNull.INSTANCE));
		
		assertFalse(IniTypeProvider.INSTANCE.isNull(new IniSection("test")));
		assertFalse(IniTypeProvider.INSTANCE.isNull(new IniValue(1)));
		assertFalse(IniTypeProvider.INSTANCE.isNull(new IniValue(true)));
		assertFalse(IniTypeProvider.INSTANCE.isNull(new IniValue("test")));
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getBoolean(null));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getByte(null));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getShort(null));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getInteger(null));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getLong(null));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getFloat(null));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getDouble(null));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getString(null));
		
		IniSection wrongType = new IniSection("test");
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getBoolean(wrongType));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getByte(wrongType));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getShort(wrongType));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getInteger(wrongType));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getLong(wrongType));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getFloat(wrongType));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getDouble(wrongType));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getString(wrongType));
		
		IniValue invalidValue = new IniValue("not-a-number");
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getBoolean(invalidValue));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getByte(invalidValue));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getShort(invalidValue));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getInteger(invalidValue));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getLong(invalidValue));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getFloat(invalidValue));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getDouble(invalidValue));
		
		assertTrue(IniTypeProvider.INSTANCE.getBoolean(new IniValue(true)));
		assertEquals((byte) 42, IniTypeProvider.INSTANCE.getByte(new IniValue((byte) 42)));
		assertEquals((short) 42, IniTypeProvider.INSTANCE.getShort(new IniValue((short) 42)));
		assertEquals(42, IniTypeProvider.INSTANCE.getInteger(new IniValue(42)));
		assertEquals(42L, IniTypeProvider.INSTANCE.getLong(new IniValue(42L)));
		assertEquals(42.5f, IniTypeProvider.INSTANCE.getFloat(new IniValue(42.5f)));
		assertEquals(42.5, IniTypeProvider.INSTANCE.getDouble(new IniValue(42.5)));
		assertEquals("test", IniTypeProvider.INSTANCE.getString(new IniValue("test")));
	}
	
	@Test
	void getListReturnsError() {
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getList(null));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getList(new IniValue(1)));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getList(new IniSection("test")));
	}
	
	@Test
	void getMapTypes() {
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getMap(null));
		
		IniValue wrongType = new IniValue(1);
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.getMap(wrongType));
		
		IniSection emptySection = new IniSection("test");
		assertTrue(IniTypeProvider.INSTANCE.getMap(emptySection).isEmpty());
		
		IniSection sectionWithElements = new IniSection("test");
		sectionWithElements.add("key", new IniValue("value"));
		Map<String, IniElement> mapResult = IniTypeProvider.INSTANCE.getMap(sectionWithElements);
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsIniValue().getAsString());
	}
	
	@Test
	void mapOperations() {
		IniSection iniSection = new IniSection("test");
		IniElement testValue = new IniValue("test");
		
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.has(null, "key"));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.has(iniSection, null));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.get(null, "key"));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.get(iniSection, null));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.set(null, "key", testValue));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.set(iniSection, null, testValue));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.set(iniSection, "key", null));
		
		IniValue wrongType = new IniValue(1);
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.has(wrongType, "key"));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.get(wrongType, "key"));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.set(wrongType, "key", testValue));
		
		assertFalse(IniTypeProvider.INSTANCE.has(iniSection, "key"));
		
		IniTypeProvider.INSTANCE.set(iniSection, "key", testValue);
		assertTrue(IniTypeProvider.INSTANCE.has(iniSection, "key"));
		assertEquals(testValue, IniTypeProvider.INSTANCE.get(iniSection, "key"));
	}
	
	@Test
	void mergeOperations() {
		assertEquals(IniNull.INSTANCE, IniTypeProvider.INSTANCE.merge(null, IniNull.INSTANCE));
		assertEquals(IniNull.INSTANCE, IniTypeProvider.INSTANCE.merge(IniNull.INSTANCE, null));
		
		IniElement value = new IniValue(1);
		IniSection section1 = new IniSection("section1");
		section1.add("key1", new IniValue("value1"));
		IniSection section2 = new IniSection("section2");
		section2.add("key2", new IniValue("value2"));
		
		assertEquals(value, IniTypeProvider.INSTANCE.merge(IniTypeProvider.INSTANCE.empty(), value));
		assertEquals(section1, IniTypeProvider.INSTANCE.merge(IniTypeProvider.INSTANCE.empty(), section1));
		
		assertEquals(IniNull.INSTANCE, IniTypeProvider.INSTANCE.merge(IniTypeProvider.INSTANCE.empty(), IniNull.INSTANCE));
		
		assertEquals(value, IniTypeProvider.INSTANCE.merge(value, IniNull.INSTANCE));
		assertEquals(value, IniTypeProvider.INSTANCE.merge(IniNull.INSTANCE, value));
		
		assertEquals(section1, IniTypeProvider.INSTANCE.merge(section1, IniNull.INSTANCE));
		assertEquals(section1, IniTypeProvider.INSTANCE.merge(IniNull.INSTANCE, section1));
		
		IniSection mergedSection = IniTypeProvider.INSTANCE.merge(section1, section2).getAsIniSection();
		assertEquals(2, mergedSection.size());
		assertEquals("value1", mergedSection.get("key1").getAsIniValue().getAsString());
		assertEquals("value2", mergedSection.get("key2").getAsIniValue().getAsString());
		
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.merge(value, section1));
		assertThrows(TypeProviderException.class, () -> IniTypeProvider.INSTANCE.merge(section1, value));
	}
}
