/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.data.property;

import com.google.common.collect.Lists;
import net.luis.utils.util.Utils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Properties}.<br>
 *
 * @author Luis-St
 */
class PropertiesTest {
	
	private static final List<Property> TEST_PROPERTIES = Utils.make(Lists.newArrayList(), list -> {
		list.add(Property.of("test.a.key", "value1"));
		list.add(Property.of("test.b.key", "value2"));
		list.add(Property.of("test.c.key", "value3"));
	});
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new Properties((List<Property>) null));
		assertDoesNotThrow(() -> new Properties(Lists.newArrayList()));
		
		assertThrows(NullPointerException.class, () -> new Properties((Map<String, Property>) null));
		assertDoesNotThrow(() -> new Properties(Map.of()));
	}
	
	@Test
	void size() {
		Properties properties = new Properties(TEST_PROPERTIES);
		assertEquals(3, properties.size());
	}
	
	@Test
	void getProperties() {
		Properties properties = new Properties(TEST_PROPERTIES);
		assertNotNull(properties.getProperties());
		assertEquals(TEST_PROPERTIES.size(), properties.getProperties().size());
		assertIterableEquals(TEST_PROPERTIES, properties.getProperties());
	}
	
	@Test
	void hasProperty() {
		Properties properties = new Properties(TEST_PROPERTIES);
		assertTrue(properties.hasProperty("test.a.key"));
		assertTrue(properties.hasProperty("test.b.key"));
		assertTrue(properties.hasProperty("test.c.key"));
		assertFalse(properties.hasProperty("test.d.key"));
	}
	
	@Test
	void getProperty() {
		Properties properties = new Properties(TEST_PROPERTIES);
		assertNotNull(properties.getProperty("test.a.key"));
		assertEquals("value1", properties.getProperty("test.a.key").getRawValue());
		assertNotNull(properties.getProperty("test.b.key"));
		assertEquals("value2", properties.getProperty("test.b.key").getRawValue());
		assertNotNull(properties.getProperty("test.c.key"));
		assertEquals("value3", properties.getProperty("test.c.key").getRawValue());
	}
	
	@Test
	void getPropertiesOfSubgroup() {
		Properties properties = new Properties(TEST_PROPERTIES);
		assertNotNull(properties.getPropertiesOfSubgroup("test"));
		assertEquals(3, properties.getPropertiesOfSubgroup("test").size());
		assertEquals(1, properties.getPropertiesOfSubgroup("test.a").size());
		assertEquals(1, properties.getPropertiesOfSubgroup("test.b").size());
		assertEquals(1, properties.getPropertiesOfSubgroup("test.c").size());
		assertEquals(0, properties.getPropertiesOfSubgroup("test.d").size());
	}
	
	@Test
	void getGroupedMap() {
		Properties properties = new Properties(TEST_PROPERTIES);
		assertNotNull(properties.getGroupedMap());
		assertEquals(1, properties.getGroupedMap().size());
		
		Map<String, Object> base = properties.getGroupedMap();
		assertInstanceOf(Map.class, base.get("test"));
		assertEquals(3, ((Map<String, Object>) base.get("test")).size());
		
		Map<String, Object> inner = (Map<String, Object>) base.get("test");
		assertInstanceOf(Map.class, inner.get("a"));
		assertEquals(1, ((Map<String, Object>) inner.get("a")).size());
		assertEquals("value1", ((Map<String, Object>) inner.get("a")).get("key"));
		
		assertInstanceOf(Map.class, inner.get("b"));
		assertEquals(1, ((Map<String, Object>) inner.get("b")).size());
		assertEquals("value2", ((Map<String, Object>) inner.get("b")).get("key"));
		
		assertInstanceOf(Map.class, inner.get("c"));
		assertEquals(1, ((Map<String, Object>) inner.get("c")).size());
		assertEquals("value3", ((Map<String, Object>) inner.get("c")).get("key"));
	}
}
