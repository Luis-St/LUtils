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

package net.luis.utils.io.data.ini;

import net.luis.utils.io.data.ini.exception.IniTypeException;
import net.luis.utils.io.data.ini.exception.NoSuchIniElementException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IniSection}.<br>
 *
 * @author Luis-St
 */
class IniSectionTest {
	
	private static final IniConfig CUSTOM_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 1,
		false, false, false,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
	);
	
	private static final IniConfig SKIP_NULL_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 1,
		false, false, false,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.SKIP, StandardCharsets.UTF_8
	);
	
	@Test
	void constructorValid() {
		IniSection section = new IniSection("test");
		assertEquals("test", section.getName());
		assertTrue(section.isEmpty());
		assertEquals(0, section.size());
	}
	
	@Test
	void constructorInvalid() {
		assertThrows(NullPointerException.class, () -> new IniSection(null));
		assertThrows(IllegalArgumentException.class, () -> new IniSection(""));
		assertThrows(IllegalArgumentException.class, () -> new IniSection("   "));
	}
	
	@Test
	void getName() {
		IniSection section = new IniSection("mySection");
		assertEquals("mySection", section.getName());
		
		IniSection sectionWithNumbers = new IniSection("section123");
		assertEquals("section123", sectionWithNumbers.getName());
	}
	
	@Test
	void iniElementTypeChecks() {
		IniSection section = new IniSection("test");
		
		assertFalse(section.isIniNull());
		assertFalse(section.isIniValue());
		assertTrue(section.isIniSection());
		assertFalse(section.isIniDocument());
	}
	
	@Test
	void iniElementConversions() {
		IniSection section = new IniSection("test");
		
		assertSame(section, section.getAsIniSection());
		assertThrows(IniTypeException.class, section::getAsIniValue);
		assertThrows(IniTypeException.class, section::getAsIniDocument);
	}
	
	@Test
	void sizeAndEmptyOperations() {
		IniSection section = new IniSection("test");
		assertEquals(0, section.size());
		assertTrue(section.isEmpty());
		
		section.add("key1", new IniValue("value1"));
		assertEquals(1, section.size());
		assertFalse(section.isEmpty());
		
		section.add("key2", new IniValue(42));
		assertEquals(2, section.size());
		
		section.remove("key1");
		assertEquals(1, section.size());
		
		section.clear();
		assertEquals(0, section.size());
		assertTrue(section.isEmpty());
	}
	
	@Test
	void containsOperations() {
		IniSection section = new IniSection("test");
		IniElement value = new IniValue("test");
		
		assertFalse(section.containsKey("key"));
		assertFalse(section.containsKey(null));
		assertFalse(section.containsValue(value));
		assertFalse(section.containsValue(null));
		
		section.add("key", value);
		assertTrue(section.containsKey("key"));
		assertTrue(section.containsValue(value));
		assertFalse(section.containsKey("other"));
		assertFalse(section.containsValue(new IniValue("different")));
		
		section.add("nullKey", (String) null);
		assertTrue(section.containsKey("nullKey"));
		assertTrue(section.containsValue(IniNull.INSTANCE));
	}
	
	@Test
	void keySetAndValuesOperations() {
		IniSection section = new IniSection("test");
		
		assertEquals(Set.of(), section.keySet());
		assertTrue(section.elements().isEmpty());
		assertTrue(section.entrySet().isEmpty());
		
		section.add("key1", new IniValue("value1"));
		section.add("key2", new IniValue(42));
		section.add("key3", IniNull.INSTANCE);
		
		Set<String> keys = section.keySet();
		assertEquals(Set.of("key1", "key2", "key3"), keys);
		
		assertEquals(3, section.elements().size());
		assertTrue(section.elements().contains(new IniValue("value1")));
		assertTrue(section.elements().contains(new IniValue(42)));
		assertTrue(section.elements().contains(IniNull.INSTANCE));
		
		assertEquals(3, section.entrySet().size());
	}
	
	@Test
	void iteratorOperations() {
		IniSection section = new IniSection("test");
		section.add("key1", new IniValue("value1"));
		section.add("key2", new IniValue(42));
		
		Iterator<Map.Entry<String, IniElement>> iterator = section.iterator();
		assertTrue(iterator.hasNext());
		
		int count = 0;
		while (iterator.hasNext()) {
			Map.Entry<String, IniElement> entry = iterator.next();
			assertNotNull(entry.getKey());
			assertNotNull(entry.getValue());
			count++;
		}
		assertEquals(2, count);
	}
	
	@Test
	void forEachOperation() {
		IniSection section = new IniSection("test");
		
		assertThrows(NullPointerException.class, () -> section.forEach((java.util.function.BiConsumer<? super String, ? super IniElement>) null));
		
		section.forEach((key, value) -> fail("Should not be called for empty section"));
		
		section.add("key1", new IniValue("value1"));
		section.add("key2", new IniValue(42));
		
		AtomicInteger callCount = new AtomicInteger(0);
		section.forEach((key, value) -> {
			callCount.incrementAndGet();
			assertTrue(Set.of("key1", "key2").contains(key));
			assertNotNull(value);
		});
		
		assertEquals(2, callCount.get());
	}
	
	@Test
	void addOperationsWithIniElement() {
		IniSection section = new IniSection("test");
		
		assertThrows(NullPointerException.class, () -> section.add(null, new IniValue("value")));
		
		IniElement previous = section.add("key1", (IniElement) null);
		assertNull(previous);
		assertEquals(IniNull.INSTANCE, section.get("key1"));
		
		previous = section.add("key1", new IniValue("newValue"));
		assertEquals(IniNull.INSTANCE, previous);
		assertEquals(new IniValue("newValue"), section.get("key1"));
	}
	
	@Test
	void addOperationsWithPrimitiveTypes() {
		IniSection section = new IniSection("test");
		
		section.add("stringKey", "testString");
		assertEquals(new IniValue("testString"), section.get("stringKey"));
		
		section.add("nullStringKey", (String) null);
		assertEquals(IniNull.INSTANCE, section.get("nullStringKey"));
		
		section.add("booleanKey", true);
		assertEquals(new IniValue(true), section.get("booleanKey"));
		
		section.add("numberKey", Integer.valueOf(42));
		assertEquals(new IniValue(42), section.get("numberKey"));
		
		section.add("nullNumberKey", (Number) null);
		assertEquals(IniNull.INSTANCE, section.get("nullNumberKey"));
	}
	
	@Test
	void addAllOperations() {
		IniSection section = new IniSection("test");
		
		IniSection other = new IniSection("other");
		other.add("key1", new IniValue("value1"));
		other.add("key2", new IniValue(42));
		
		assertThrows(NullPointerException.class, () -> section.addAll((IniSection) null));
		
		section.addAll(other);
		assertEquals(2, section.size());
		assertEquals(new IniValue("value1"), section.get("key1"));
		assertEquals(new IniValue(42), section.get("key2"));
		
		Map<String, IniElement> map = new HashMap<>();
		map.put("key3", new IniValue("value3"));
		map.put("key4", IniNull.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> section.addAll((Map<String, IniElement>) null));
		
		section.addAll(map);
		assertEquals(4, section.size());
		assertEquals(new IniValue("value3"), section.get("key3"));
		assertEquals(IniNull.INSTANCE, section.get("key4"));
	}
	
	@Test
	void removeOperations() {
		IniSection section = new IniSection("test");
		
		assertNull(section.remove("nonexistent"));
		assertNull(section.remove(null));
		
		section.add("key1", new IniValue("value1"));
		section.add("key2", new IniValue(42));
		section.add("key3", IniNull.INSTANCE);
		
		IniElement removed = section.remove("key1");
		assertEquals(new IniValue("value1"), removed);
		assertEquals(2, section.size());
		assertFalse(section.containsKey("key1"));
		
		assertNull(section.remove("nonexistent"));
		assertEquals(2, section.size());
		
		section.clear();
		assertEquals(0, section.size());
		assertTrue(section.isEmpty());
	}
	
	@Test
	void replaceOperations() {
		IniSection section = new IniSection("test");
		
		assertThrows(NullPointerException.class, () -> section.replace(null, new IniValue("value")));
		
		IniElement result = section.replace("key", new IniValue("value"));
		assertNull(result);
		assertTrue(section.isEmpty());
		
		section.add("key1", new IniValue("oldValue"));
		
		result = section.replace("key1", new IniValue("newValue"));
		assertEquals(new IniValue("oldValue"), result);
		assertEquals(new IniValue("newValue"), section.get("key1"));
		
		result = section.replace("key1", null);
		assertEquals(new IniValue("newValue"), result);
		assertEquals(IniNull.INSTANCE, section.get("key1"));
	}
	
	@Test
	void getOperations() {
		IniSection section = new IniSection("test");
		
		assertThrows(NullPointerException.class, () -> section.get(null));
		assertNull(section.get("nonexistent"));
		
		section.add("stringKey", new IniValue("stringValue"));
		section.add("numberKey", new IniValue(42));
		section.add("booleanKey", new IniValue(true));
		section.add("nullKey", IniNull.INSTANCE);
		
		assertEquals(new IniValue("stringValue"), section.get("stringKey"));
		assertEquals(new IniValue(42), section.get("numberKey"));
		assertEquals(new IniValue(true), section.get("booleanKey"));
		assertEquals(IniNull.INSTANCE, section.get("nullKey"));
	}
	
	@Test
	void getIniValueOperations() {
		IniSection section = new IniSection("test");
		
		assertThrows(NoSuchIniElementException.class, () -> section.getIniValue("nonexistent"));
		
		section.add("valueKey", new IniValue("test"));
		section.add("nullKey", IniNull.INSTANCE);
		
		assertEquals(new IniValue("test"), section.getIniValue("valueKey"));
		assertThrows(IniTypeException.class, () -> section.getIniValue("nullKey"));
	}
	
	@Test
	void getAsSpecificTypesSuccess() {
		IniSection section = new IniSection("test");
		section.add("stringKey", new IniValue("stringValue"));
		section.add("numberKey", new IniValue(42));
		section.add("booleanKey", new IniValue(true));
		section.add("doubleKey", new IniValue(3.14));
		
		assertEquals("stringValue", section.getAsString("stringKey"));
		assertEquals(42, section.getAsNumber("numberKey"));
		assertEquals(42, section.getAsInteger("numberKey"));
		assertEquals(42L, section.getAsLong("numberKey"));
		assertEquals(42.0f, section.getAsFloat("numberKey"));
		assertEquals(42.0, section.getAsDouble("numberKey"));
		assertEquals((byte) 42, section.getAsByte("numberKey"));
		assertEquals((short) 42, section.getAsShort("numberKey"));
		assertTrue(section.getAsBoolean("booleanKey"));
		assertEquals(3.14, section.getAsDouble("doubleKey"));
	}
	
	@Test
	void getAsSpecificTypesExceptions() {
		IniSection section = new IniSection("test");
		section.add("stringKey", new IniValue("stringValue"));
		section.add("nullKey", IniNull.INSTANCE);
		
		assertThrows(NoSuchIniElementException.class, () -> section.getAsString("nonexistent"));
		assertThrows(NoSuchIniElementException.class, () -> section.getAsBoolean("nonexistent"));
		assertThrows(NoSuchIniElementException.class, () -> section.getAsNumber("nonexistent"));
		
		assertThrows(IniTypeException.class, () -> section.getAsString("nullKey"));
		assertThrows(IniTypeException.class, () -> section.getAsBoolean("nullKey"));
		assertThrows(IniTypeException.class, () -> section.getAsNumber("nullKey"));
		
		assertThrows(IniTypeException.class, () -> section.getAsBoolean("stringKey"));
		assertThrows(IniTypeException.class, () -> section.getAsNumber("stringKey"));
	}
	
	@Test
	void equalsAndHashCode() {
		IniSection section1 = new IniSection("test");
		IniSection section2 = new IniSection("test");
		IniSection section3 = new IniSection("different");
		
		assertEquals(section1, section2);
		assertEquals(section1.hashCode(), section2.hashCode());
		
		section1.add("key1", new IniValue("value1"));
		section1.add("key2", new IniValue(42));
		
		section2.add("key1", new IniValue("value1"));
		section2.add("key2", new IniValue(42));
		
		assertEquals(section1, section2);
		assertEquals(section1.hashCode(), section2.hashCode());
		
		section3.add("key1", new IniValue("value1"));
		section3.add("key2", new IniValue(42));
		
		assertNotEquals(section1, section3);
		assertNotEquals(section1, null);
		assertNotEquals(section1, "not a section");
		
		assertEquals(section1, section1);
		
		IniSection section4 = new IniSection("test");
		section4.add("key1", new IniValue("value1"));
		assertNotEquals(section1, section4);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		IniSection section = new IniSection("test");
		assertEquals("[test]", section.toString());
		
		section.add("key", new IniValue("value"));
		String result = section.toString();
		assertTrue(result.startsWith("[test]"));
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
	}
	
	@Test
	void toStringWithCustomConfig() {
		IniSection section = new IniSection("test");
		
		assertThrows(NullPointerException.class, () -> section.toString(null));
		
		assertEquals("[test]", section.toString(CUSTOM_CONFIG));
		
		section.add("key1", new IniValue("value1"));
		section.add("key2", new IniValue(42));
		
		String result = section.toString(CUSTOM_CONFIG);
		assertTrue(result.startsWith("[test]"));
		assertTrue(result.contains("key1"));
		assertTrue(result.contains("value1"));
		assertTrue(result.contains("key2"));
		assertTrue(result.contains("42"));
	}
	
	@Test
	void toStringSkipsNullValues() {
		IniSection section = new IniSection("test");
		section.add("key1", new IniValue("value1"));
		section.add("nullKey", IniNull.INSTANCE);
		section.add("key2", new IniValue(42));
		
		String result = section.toString(SKIP_NULL_CONFIG);
		assertTrue(result.contains("key1"));
		assertFalse(result.contains("nullKey"));
		assertTrue(result.contains("key2"));
	}
	
	@Test
	void preservesInsertionOrder() {
		IniSection section = new IniSection("test");
		
		section.add("third", new IniValue(3));
		section.add("first", new IniValue(1));
		section.add("second", new IniValue(2));
		
		String[] expectedOrder = { "third", "first", "second" };
		String[] actualOrder = section.keySet().toArray(new String[0]);
		assertArrayEquals(expectedOrder, actualOrder);
		
		String result = section.toString();
		int thirdIndex = result.indexOf("third");
		int firstIndex = result.indexOf("first");
		int secondIndex = result.indexOf("second");
		
		assertTrue(thirdIndex < firstIndex);
		assertTrue(firstIndex < secondIndex);
	}
}
