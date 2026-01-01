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

package net.luis.utils.io.data.yaml;

import net.luis.utils.io.data.yaml.exception.NoSuchYamlElementException;
import net.luis.utils.io.data.yaml.exception.YamlTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YamlMapping}.<br>
 *
 * @author Luis-St
 */
class YamlMappingTest {
	
	private static final YamlConfig BLOCK_CONFIG = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
	private static final YamlConfig FLOW_CONFIG = new YamlConfig(true, true, "  ", false, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
	
	@Test
	void constructorEmpty() {
		YamlMapping mapping = new YamlMapping();
		assertTrue(mapping.isEmpty());
		assertEquals(0, mapping.size());
	}
	
	@Test
	void constructorWithMap() {
		Map<String, YamlElement> map = new LinkedHashMap<>();
		map.put("a", new YamlScalar(1));
		map.put("b", new YamlScalar(2));
		
		YamlMapping mapping = new YamlMapping(map);
		
		assertEquals(2, mapping.size());
		assertEquals(new YamlScalar(1), mapping.get("a"));
		assertEquals(new YamlScalar(2), mapping.get("b"));
	}
	
	@Test
	void constructorWithNullMap() {
		assertThrows(NullPointerException.class, () -> new YamlMapping(null));
	}
	
	@Test
	void yamlElementTypeChecks() {
		YamlMapping mapping = new YamlMapping();
		
		assertFalse(mapping.isYamlNull());
		assertTrue(mapping.isYamlMapping());
		assertFalse(mapping.isYamlSequence());
		assertFalse(mapping.isYamlScalar());
		assertFalse(mapping.isYamlAnchor());
		assertFalse(mapping.isYamlAlias());
	}
	
	@Test
	void yamlElementConversions() {
		YamlMapping mapping = new YamlMapping();
		
		assertSame(mapping, mapping.getAsYamlMapping());
		assertThrows(YamlTypeException.class, mapping::getAsYamlSequence);
		assertThrows(YamlTypeException.class, mapping::getAsYamlScalar);
		assertThrows(YamlTypeException.class, mapping::getAsYamlAnchor);
		assertThrows(YamlTypeException.class, mapping::getAsYamlAlias);
	}
	
	@Test
	void unwrap() {
		YamlMapping mapping = new YamlMapping();
		assertSame(mapping, mapping.unwrap());
	}
	
	@Test
	void sizeAndIsEmpty() {
		YamlMapping mapping = new YamlMapping();
		assertTrue(mapping.isEmpty());
		assertEquals(0, mapping.size());
		
		mapping.add("key", "value");
		assertFalse(mapping.isEmpty());
		assertEquals(1, mapping.size());
		
		mapping.add("key2", "value2");
		assertEquals(2, mapping.size());
		
		mapping.clear();
		assertTrue(mapping.isEmpty());
		assertEquals(0, mapping.size());
	}
	
	@Test
	void containsKey() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("key", "value");
		
		assertTrue(mapping.containsKey("key"));
		assertFalse(mapping.containsKey("nonexistent"));
		assertFalse(mapping.containsKey(null));
	}
	
	@Test
	void containsValue() {
		YamlMapping mapping = new YamlMapping();
		YamlScalar scalar = new YamlScalar("value");
		mapping.add("key", scalar);
		
		assertTrue(mapping.containsValue(scalar));
		assertTrue(mapping.containsValue(new YamlScalar("value")));
		assertFalse(mapping.containsValue(new YamlScalar("other")));
		assertFalse(mapping.containsValue(null));
	}
	
	@Test
	void addYamlElement() {
		YamlMapping mapping = new YamlMapping();
		YamlScalar scalar = new YamlScalar("value");
		
		YamlElement previous = mapping.add("key", scalar);
		assertNull(previous);
		assertEquals(scalar, mapping.get("key"));
		
		YamlScalar newScalar = new YamlScalar("newValue");
		previous = mapping.add("key", newScalar);
		assertEquals(scalar, previous);
		assertEquals(newScalar, mapping.get("key"));
		
		mapping.add("nullKey", (YamlElement) null);
		assertEquals(YamlNull.INSTANCE, mapping.get("nullKey"));
	}
	
	@Test
	void addYamlElementNullKey() {
		YamlMapping mapping = new YamlMapping();
		assertThrows(NullPointerException.class, () -> mapping.add(null, new YamlScalar("value")));
	}
	
	@Test
	void addString() {
		YamlMapping mapping = new YamlMapping();
		
		mapping.add("key", "value");
		assertEquals(new YamlScalar("value"), mapping.get("key"));
		
		mapping.add("nullKey", (String) null);
		assertEquals(YamlNull.INSTANCE, mapping.get("nullKey"));
	}
	
	@Test
	void addBoolean() {
		YamlMapping mapping = new YamlMapping();
		
		mapping.add("true", true);
		mapping.add("false", false);
		
		assertEquals(new YamlScalar(true), mapping.get("true"));
		assertEquals(new YamlScalar(false), mapping.get("false"));
	}
	
	@Test
	void addNumber() {
		YamlMapping mapping = new YamlMapping();
		
		mapping.add("int", 42);
		mapping.add("double", 3.14);
		mapping.add("null", (Number) null);
		
		assertEquals(new YamlScalar(42), mapping.get("int"));
		assertEquals(new YamlScalar(3.14), mapping.get("double"));
		assertEquals(YamlNull.INSTANCE, mapping.get("null"));
	}
	
	@Test
	void addAllMapping() {
		YamlMapping mapping1 = new YamlMapping();
		mapping1.add("a", 1);
		
		YamlMapping mapping2 = new YamlMapping();
		mapping2.add("b", 2);
		mapping2.add("c", 3);
		
		mapping1.addAll(mapping2);
		
		assertEquals(3, mapping1.size());
		assertEquals(new YamlScalar(1), mapping1.get("a"));
		assertEquals(new YamlScalar(2), mapping1.get("b"));
		assertEquals(new YamlScalar(3), mapping1.get("c"));
	}
	
	@Test
	void addAllMap() {
		YamlMapping mapping = new YamlMapping();
		Map<String, YamlElement> map = new LinkedHashMap<>();
		map.put("a", new YamlScalar(1));
		map.put("b", new YamlScalar(2));
		
		mapping.addAll(map);
		
		assertEquals(2, mapping.size());
		assertEquals(new YamlScalar(1), mapping.get("a"));
		assertEquals(new YamlScalar(2), mapping.get("b"));
	}
	
	@Test
	void addAllNullArgument() {
		YamlMapping mapping = new YamlMapping();
		assertThrows(NullPointerException.class, () -> mapping.addAll((YamlMapping) null));
		assertThrows(NullPointerException.class, () -> mapping.addAll((Map<String, YamlElement>) null));
	}
	
	@Test
	void remove() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("key", "value");
		
		YamlElement removed = mapping.remove("key");
		assertEquals(new YamlScalar("value"), removed);
		assertFalse(mapping.containsKey("key"));
		
		assertNull(mapping.remove("nonexistent"));
	}
	
	@Test
	void clear() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("a", 1);
		mapping.add("b", 2);
		
		mapping.clear();
		
		assertTrue(mapping.isEmpty());
	}
	
	@Test
	void replace() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("key", "oldValue");
		
		YamlElement previous = mapping.replace("key", new YamlScalar("newValue"));
		
		assertEquals(new YamlScalar("oldValue"), previous);
		assertEquals(new YamlScalar("newValue"), mapping.get("key"));
		
		assertNull(mapping.replace("nonexistent", new YamlScalar("value")));
		
		mapping.replace("key", null);
		assertEquals(YamlNull.INSTANCE, mapping.get("key"));
	}
	
	@Test
	void replaceWithOldValue() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("key", "oldValue");
		
		boolean replaced = mapping.replace("key", new YamlScalar("oldValue"), new YamlScalar("newValue"));
		assertTrue(replaced);
		assertEquals(new YamlScalar("newValue"), mapping.get("key"));
		
		replaced = mapping.replace("key", new YamlScalar("wrongValue"), new YamlScalar("anotherValue"));
		assertFalse(replaced);
		assertEquals(new YamlScalar("newValue"), mapping.get("key"));
	}
	
	@Test
	void get() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("key", "value");
		
		assertEquals(new YamlScalar("value"), mapping.get("key"));
		assertNull(mapping.get("nonexistent"));
	}
	
	@Test
	void getNullKey() {
		YamlMapping mapping = new YamlMapping();
		assertThrows(NullPointerException.class, () -> mapping.get(null));
	}
	
	@Test
	void getAsYamlMapping() {
		YamlMapping mapping = new YamlMapping();
		YamlMapping nested = new YamlMapping();
		mapping.add("nested", nested);
		mapping.add("scalar", "value");
		
		assertEquals(nested, mapping.getAsYamlMapping("nested"));
		assertThrows(YamlTypeException.class, () -> mapping.getAsYamlMapping("scalar"));
		assertThrows(NoSuchYamlElementException.class, () -> mapping.getAsYamlMapping("nonexistent"));
	}
	
	@Test
	void getAsYamlSequence() {
		YamlMapping mapping = new YamlMapping();
		YamlSequence sequence = new YamlSequence();
		mapping.add("sequence", sequence);
		mapping.add("scalar", "value");
		
		assertEquals(sequence, mapping.getAsYamlSequence("sequence"));
		assertThrows(YamlTypeException.class, () -> mapping.getAsYamlSequence("scalar"));
		assertThrows(NoSuchYamlElementException.class, () -> mapping.getAsYamlSequence("nonexistent"));
	}
	
	@Test
	void getAsYamlScalar() {
		YamlMapping mapping = new YamlMapping();
		YamlScalar scalar = new YamlScalar("value");
		mapping.add("scalar", scalar);
		mapping.add("mapping", new YamlMapping());
		
		assertEquals(scalar, mapping.getAsYamlScalar("scalar"));
		assertThrows(YamlTypeException.class, () -> mapping.getAsYamlScalar("mapping"));
		assertThrows(NoSuchYamlElementException.class, () -> mapping.getAsYamlScalar("nonexistent"));
	}
	
	@Test
	void getAsString() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("key", "value");
		
		assertEquals("value", mapping.getAsString("key"));
	}
	
	@Test
	void getAsBoolean() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("true", true);
		mapping.add("false", false);
		
		assertTrue(mapping.getAsBoolean("true"));
		assertFalse(mapping.getAsBoolean("false"));
	}
	
	@Test
	void getAsNumber() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("number", 42);
		
		assertEquals(42, mapping.getAsNumber("number"));
	}
	
	@Test
	void getAsInteger() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("int", 42);
		
		assertEquals(42, mapping.getAsInteger("int"));
	}
	
	@Test
	void getAsLong() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("long", 42L);
		
		assertEquals(42L, mapping.getAsLong("long"));
	}
	
	@Test
	void getAsDouble() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("double", 3.14);
		
		assertEquals(3.14, mapping.getAsDouble("double"));
	}
	
	@Test
	void keySet() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("a", 1);
		mapping.add("b", 2);
		mapping.add("c", 3);
		
		Set<String> keys = mapping.keySet();
		assertEquals(3, keys.size());
		assertTrue(keys.contains("a"));
		assertTrue(keys.contains("b"));
		assertTrue(keys.contains("c"));
	}
	
	@Test
	void elements() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("a", 1);
		mapping.add("b", 2);
		
		Collection<YamlElement> elements = mapping.elements();
		assertEquals(2, elements.size());
		assertTrue(elements.contains(new YamlScalar(1)));
		assertTrue(elements.contains(new YamlScalar(2)));
		assertThrows(UnsupportedOperationException.class, () -> elements.add(new YamlScalar(3)));
	}
	
	@Test
	void entrySet() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("a", 1);
		mapping.add("b", 2);
		
		Set<Map.Entry<String, YamlElement>> entries = mapping.entrySet();
		assertEquals(2, entries.size());
	}
	
	@Test
	void forEach() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("a", 1);
		mapping.add("b", 2);
		
		List<String> keys = new ArrayList<>();
		mapping.forEach((key, value) -> keys.add(key));
		
		assertEquals(List.of("a", "b"), keys);
	}
	
	@Test
	void forEachNullAction() {
		YamlMapping mapping = new YamlMapping();
		assertThrows(NullPointerException.class, () -> mapping.forEach(null));
	}
	
	@Test
	void equalsAndHashCode() {
		YamlMapping mapping1 = new YamlMapping();
		mapping1.add("a", 1);
		mapping1.add("b", 2);
		
		YamlMapping mapping2 = new YamlMapping();
		mapping2.add("a", 1);
		mapping2.add("b", 2);
		
		YamlMapping mapping3 = new YamlMapping();
		mapping3.add("a", 1);
		mapping3.add("b", 3);
		
		assertEquals(mapping1, mapping2);
		assertEquals(mapping1.hashCode(), mapping2.hashCode());
		assertNotEquals(mapping1, mapping3);
		
		assertEquals(mapping1, mapping1);
		assertNotEquals(mapping1, null);
		assertNotEquals(mapping1, "not a mapping");
	}
	
	@Test
	void toStringEmptyMapping() {
		YamlMapping mapping = new YamlMapping();
		assertEquals("{}", mapping.toString());
		assertEquals("{}", mapping.toString(BLOCK_CONFIG));
		assertEquals("{}", mapping.toString(FLOW_CONFIG));
	}
	
	@Test
	void toStringFlowStyle() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("a", 1);
		mapping.add("b", 2);
		
		assertEquals("{a: 1, b: 2}", mapping.toString(FLOW_CONFIG));
	}
	
	@Test
	void toStringBlockStyle() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("a", 1);
		mapping.add("b", 2);
		
		String expected = "a: 1" + System.lineSeparator() + "b: 2";
		assertEquals(expected, mapping.toString(BLOCK_CONFIG));
	}
	
	@Test
	void toStringWithNullConfig() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("key", "value");
		
		assertThrows(NullPointerException.class, () -> mapping.toString(null));
	}
	
	@Test
	void toStringWithQuotedKeys() {
		YamlMapping mapping = new YamlMapping();
		mapping.add("simple", "value");
		mapping.add("with space", "value");
		mapping.add("with:colon", "value");
		
		String result = mapping.toString(FLOW_CONFIG);
		assertTrue(result.contains("simple:"));
		assertTrue(result.contains("\"with space\":"));
		assertTrue(result.contains("\"with:colon\":"));
	}
	
	@Test
	void toStringNestedMapping() {
		YamlMapping outer = new YamlMapping();
		YamlMapping inner = new YamlMapping();
		inner.add("x", 1);
		inner.add("y", 2);
		outer.add("nested", inner);
		outer.add("simple", 3);
		
		String flowResult = outer.toString(FLOW_CONFIG);
		assertEquals("{nested: {x: 1, y: 2}, simple: 3}", flowResult);
	}
	
	@Test
	void preservesInsertionOrder() {
		YamlMapping mapping = new YamlMapping();
		String[] keys = { "z", "a", "m", "b", "y" };
		for (String key : keys) {
			mapping.add(key, key);
		}
		
		List<String> resultKeys = new ArrayList<>(mapping.keySet());
		assertArrayEquals(keys, resultKeys.toArray(new String[0]));
	}
}
