/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

import net.luis.utils.io.data.yaml.exception.YamlSequenceIndexOutOfBoundsException;
import net.luis.utils.io.data.yaml.exception.YamlTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YamlSequence}.<br>
 *
 * @author Luis-St
 */
class YamlSequenceTest {
	
	private static final YamlConfig BLOCK_CONFIG = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
	private static final YamlConfig FLOW_CONFIG = new YamlConfig(true, true, "  ", false, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
	
	@Test
	void constructorEmpty() {
		YamlSequence sequence = new YamlSequence();
		assertTrue(sequence.isEmpty());
		assertEquals(0, sequence.size());
	}
	
	@Test
	void constructorWithList() {
		List<YamlElement> elements = List.of(new YamlScalar("a"), new YamlScalar("b"), new YamlScalar("c"));
		YamlSequence sequence = new YamlSequence(elements);
		
		assertEquals(3, sequence.size());
		assertEquals(new YamlScalar("a"), sequence.get(0));
		assertEquals(new YamlScalar("b"), sequence.get(1));
		assertEquals(new YamlScalar("c"), sequence.get(2));
	}
	
	@Test
	void constructorWithNullList() {
		assertThrows(NullPointerException.class, () -> new YamlSequence(null));
	}
	
	@Test
	void yamlElementTypeChecks() {
		YamlSequence sequence = new YamlSequence();
		
		assertFalse(sequence.isYamlNull());
		assertFalse(sequence.isYamlMapping());
		assertTrue(sequence.isYamlSequence());
		assertFalse(sequence.isYamlScalar());
		assertFalse(sequence.isYamlAnchor());
		assertFalse(sequence.isYamlAlias());
	}
	
	@Test
	void yamlElementConversions() {
		YamlSequence sequence = new YamlSequence();
		
		assertThrows(YamlTypeException.class, sequence::getAsYamlMapping);
		assertSame(sequence, sequence.getAsYamlSequence());
		assertThrows(YamlTypeException.class, sequence::getAsYamlScalar);
		assertThrows(YamlTypeException.class, sequence::getAsYamlAnchor);
		assertThrows(YamlTypeException.class, sequence::getAsYamlAlias);
	}
	
	@Test
	void unwrap() {
		YamlSequence sequence = new YamlSequence();
		assertSame(sequence, sequence.unwrap());
	}
	
	@Test
	void sizeAndIsEmpty() {
		YamlSequence sequence = new YamlSequence();
		assertTrue(sequence.isEmpty());
		assertEquals(0, sequence.size());
		
		sequence.add("test");
		assertFalse(sequence.isEmpty());
		assertEquals(1, sequence.size());
		
		sequence.add("test2");
		assertEquals(2, sequence.size());
		
		sequence.clear();
		assertTrue(sequence.isEmpty());
		assertEquals(0, sequence.size());
	}
	
	@Test
	void contains() {
		YamlSequence sequence = new YamlSequence();
		YamlScalar scalar = new YamlScalar("test");
		
		assertFalse(sequence.contains(scalar));
		
		sequence.add(scalar);
		assertTrue(sequence.contains(scalar));
		assertTrue(sequence.contains(new YamlScalar("test")));
		assertFalse(sequence.contains(new YamlScalar("other")));
		
		sequence.add(YamlNull.INSTANCE);
		assertTrue(sequence.contains(YamlNull.INSTANCE));
	}
	
	@Test
	void addYamlElement() {
		YamlSequence sequence = new YamlSequence();
		YamlScalar scalar = new YamlScalar("test");
		YamlMapping mapping = new YamlMapping();
		YamlSequence nested = new YamlSequence();
		
		sequence.add(scalar);
		sequence.add(mapping);
		sequence.add(nested);
		sequence.add((YamlElement) null);
		
		assertEquals(4, sequence.size());
		assertEquals(scalar, sequence.get(0));
		assertEquals(mapping, sequence.get(1));
		assertEquals(nested, sequence.get(2));
		assertEquals(YamlNull.INSTANCE, sequence.get(3));
	}
	
	@Test
	void addString() {
		YamlSequence sequence = new YamlSequence();
		
		sequence.add("test");
		sequence.add((String) null);
		
		assertEquals(2, sequence.size());
		assertEquals(new YamlScalar("test"), sequence.get(0));
		assertEquals(YamlNull.INSTANCE, sequence.get(1));
	}
	
	@Test
	void addBoolean() {
		YamlSequence sequence = new YamlSequence();
		
		sequence.add(true);
		sequence.add(false);
		
		assertEquals(2, sequence.size());
		assertEquals(new YamlScalar(true), sequence.get(0));
		assertEquals(new YamlScalar(false), sequence.get(1));
	}
	
	@Test
	void addNumber() {
		YamlSequence sequence = new YamlSequence();
		
		sequence.add(42);
		sequence.add(3.14);
		sequence.add((Number) null);
		
		assertEquals(3, sequence.size());
		assertEquals(new YamlScalar(42), sequence.get(0));
		assertEquals(new YamlScalar(3.14), sequence.get(1));
		assertEquals(YamlNull.INSTANCE, sequence.get(2));
	}
	
	@Test
	void addAllSequence() {
		YamlSequence sequence1 = new YamlSequence();
		sequence1.add("a");
		sequence1.add("b");
		
		YamlSequence sequence2 = new YamlSequence();
		sequence2.add("c");
		sequence2.add("d");
		
		sequence1.addAll(sequence2);
		
		assertEquals(4, sequence1.size());
		assertEquals(new YamlScalar("a"), sequence1.get(0));
		assertEquals(new YamlScalar("b"), sequence1.get(1));
		assertEquals(new YamlScalar("c"), sequence1.get(2));
		assertEquals(new YamlScalar("d"), sequence1.get(3));
	}
	
	@Test
	void addAllVarargs() {
		YamlSequence sequence = new YamlSequence();
		sequence.addAll(new YamlScalar("a"), new YamlScalar("b"), new YamlScalar("c"));
		
		assertEquals(3, sequence.size());
		assertEquals(new YamlScalar("a"), sequence.get(0));
		assertEquals(new YamlScalar("b"), sequence.get(1));
		assertEquals(new YamlScalar("c"), sequence.get(2));
	}
	
	@Test
	void addAllList() {
		YamlSequence sequence = new YamlSequence();
		List<YamlElement> elements = List.of(new YamlScalar("a"), new YamlScalar("b"));
		
		sequence.addAll(elements);
		
		assertEquals(2, sequence.size());
		assertEquals(new YamlScalar("a"), sequence.get(0));
		assertEquals(new YamlScalar("b"), sequence.get(1));
	}
	
	@Test
	void addAllNullArgument() {
		YamlSequence sequence = new YamlSequence();
		assertThrows(NullPointerException.class, () -> sequence.addAll((YamlSequence) null));
		assertThrows(NullPointerException.class, () -> sequence.addAll((List<YamlElement>) null));
		assertThrows(NullPointerException.class, () -> sequence.addAll((YamlElement[]) null));
	}
	
	@Test
	void setYamlElement() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("original");
		
		YamlElement previous = sequence.set(0, new YamlScalar("replaced"));
		
		assertEquals(new YamlScalar("original"), previous);
		assertEquals(new YamlScalar("replaced"), sequence.get(0));
	}
	
	@Test
	void setString() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("original");
		
		sequence.set(0, "replaced");
		assertEquals(new YamlScalar("replaced"), sequence.get(0));
		
		sequence.set(0, (String) null);
		assertEquals(YamlNull.INSTANCE, sequence.get(0));
	}
	
	@Test
	void setBoolean() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("original");
		
		sequence.set(0, true);
		assertEquals(new YamlScalar(true), sequence.get(0));
	}
	
	@Test
	void setNumber() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("original");
		
		sequence.set(0, 42);
		assertEquals(new YamlScalar(42), sequence.get(0));
		
		sequence.set(0, (Number) null);
		assertEquals(YamlNull.INSTANCE, sequence.get(0));
	}
	
	@Test
	void setIndexOutOfBounds() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("test");
		
		assertThrows(YamlSequenceIndexOutOfBoundsException.class, () -> sequence.set(-1, "value"));
		assertThrows(YamlSequenceIndexOutOfBoundsException.class, () -> sequence.set(1, "value"));
		assertThrows(YamlSequenceIndexOutOfBoundsException.class, () -> sequence.set(10, "value"));
	}
	
	@Test
	void removeByIndex() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("a");
		sequence.add("b");
		sequence.add("c");
		
		YamlElement removed = sequence.remove(1);
		
		assertEquals(new YamlScalar("b"), removed);
		assertEquals(2, sequence.size());
		assertEquals(new YamlScalar("a"), sequence.get(0));
		assertEquals(new YamlScalar("c"), sequence.get(1));
	}
	
	@Test
	void removeByIndexOutOfBounds() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("test");
		
		assertThrows(YamlSequenceIndexOutOfBoundsException.class, () -> sequence.remove(-1));
		assertThrows(YamlSequenceIndexOutOfBoundsException.class, () -> sequence.remove(1));
		assertThrows(YamlSequenceIndexOutOfBoundsException.class, () -> sequence.remove(10));
	}
	
	@Test
	void removeByElement() {
		YamlSequence sequence = new YamlSequence();
		YamlScalar scalar = new YamlScalar("test");
		sequence.add(scalar);
		
		assertTrue(sequence.remove(scalar));
		assertFalse(sequence.contains(scalar));
		assertFalse(sequence.remove(scalar));
	}
	
	@Test
	void clear() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("a");
		sequence.add("b");
		sequence.add("c");
		
		sequence.clear();
		
		assertTrue(sequence.isEmpty());
		assertEquals(0, sequence.size());
	}
	
	@Test
	void get() {
		YamlSequence sequence = new YamlSequence();
		YamlScalar scalar = new YamlScalar("test");
		sequence.add(scalar);
		
		assertEquals(scalar, sequence.get(0));
	}
	
	@Test
	void getOutOfBounds() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("test");
		
		assertThrows(YamlSequenceIndexOutOfBoundsException.class, () -> sequence.get(-1));
		assertThrows(YamlSequenceIndexOutOfBoundsException.class, () -> sequence.get(1));
		assertThrows(YamlSequenceIndexOutOfBoundsException.class, () -> sequence.get(10));
	}
	
	@Test
	void getAsYamlMapping() {
		YamlSequence sequence = new YamlSequence();
		YamlMapping mapping = new YamlMapping();
		sequence.add(mapping);
		sequence.add("not a mapping");
		
		assertEquals(mapping, sequence.getAsYamlMapping(0));
		assertThrows(YamlTypeException.class, () -> sequence.getAsYamlMapping(1));
	}
	
	@Test
	void getAsYamlSequence() {
		YamlSequence sequence = new YamlSequence();
		YamlSequence nested = new YamlSequence();
		sequence.add(nested);
		sequence.add("not a sequence");
		
		assertEquals(nested, sequence.getAsYamlSequence(0));
		assertThrows(YamlTypeException.class, () -> sequence.getAsYamlSequence(1));
	}
	
	@Test
	void getAsYamlScalar() {
		YamlSequence sequence = new YamlSequence();
		YamlScalar scalar = new YamlScalar("test");
		sequence.add(scalar);
		sequence.add(new YamlMapping());
		
		assertEquals(scalar, sequence.getAsYamlScalar(0));
		assertThrows(YamlTypeException.class, () -> sequence.getAsYamlScalar(1));
	}
	
	@Test
	void getAsString() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("test");
		
		assertEquals("test", sequence.getAsString(0));
	}
	
	@Test
	void getAsBoolean() {
		YamlSequence sequence = new YamlSequence();
		sequence.add(true);
		sequence.add(false);
		
		assertTrue(sequence.getAsBoolean(0));
		assertFalse(sequence.getAsBoolean(1));
	}
	
	@Test
	void getAsNumber() {
		YamlSequence sequence = new YamlSequence();
		sequence.add(42);
		
		assertEquals(42, sequence.getAsNumber(0));
	}
	
	@Test
	void getAsInteger() {
		YamlSequence sequence = new YamlSequence();
		sequence.add(42);
		
		assertEquals(42, sequence.getAsInteger(0));
	}
	
	@Test
	void getAsLong() {
		YamlSequence sequence = new YamlSequence();
		sequence.add(42L);
		
		assertEquals(42L, sequence.getAsLong(0));
	}
	
	@Test
	void getAsDouble() {
		YamlSequence sequence = new YamlSequence();
		sequence.add(3.14);
		
		assertEquals(3.14, sequence.getAsDouble(0));
	}
	
	@Test
	void iterator() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("a");
		sequence.add("b");
		sequence.add("c");
		
		List<String> result = new ArrayList<>();
		for (YamlElement element : sequence) {
			result.add(element.getAsYamlScalar().getAsString());
		}
		
		assertEquals(List.of("a", "b", "c"), result);
	}
	
	@Test
	void elements() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("a");
		sequence.add("b");
		
		Collection<YamlElement> elements = sequence.elements();
		assertEquals(2, elements.size());
		assertThrows(UnsupportedOperationException.class, () -> elements.add(new YamlScalar("c")));
	}
	
	@Test
	void getElements() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("a");
		sequence.add("b");
		
		List<YamlElement> elements = sequence.getElements();
		assertEquals(2, elements.size());
		assertEquals(new YamlScalar("a"), elements.get(0));
		assertEquals(new YamlScalar("b"), elements.get(1));
	}
	
	@Test
	void equalsAndHashCode() {
		YamlSequence sequence1 = new YamlSequence();
		sequence1.add("a");
		sequence1.add("b");
		
		YamlSequence sequence2 = new YamlSequence();
		sequence2.add("a");
		sequence2.add("b");
		
		YamlSequence sequence3 = new YamlSequence();
		sequence3.add("a");
		sequence3.add("c");
		
		assertEquals(sequence1, sequence2);
		assertEquals(sequence1.hashCode(), sequence2.hashCode());
		assertNotEquals(sequence1, sequence3);
		
		assertEquals(sequence1, sequence1);
		assertNotEquals(sequence1, null);
		assertNotEquals(sequence1, "not a sequence");
	}
	
	@Test
	void toStringEmptySequence() {
		YamlSequence sequence = new YamlSequence();
		assertEquals("[]", sequence.toString());
		assertEquals("[]", sequence.toString(BLOCK_CONFIG));
		assertEquals("[]", sequence.toString(FLOW_CONFIG));
	}
	
	@Test
	void toStringFlowStyle() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("a");
		sequence.add("b");
		sequence.add("c");
		
		assertEquals("[a, b, c]", sequence.toString(FLOW_CONFIG));
	}
	
	@Test
	void toStringBlockStyle() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("a");
		sequence.add("b");
		sequence.add("c");
		
		String expected = "- a" + System.lineSeparator() + "- b" + System.lineSeparator() + "- c";
		assertEquals(expected, sequence.toString(BLOCK_CONFIG));
	}
	
	@Test
	void toStringWithNullConfig() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("test");
		
		assertThrows(NullPointerException.class, () -> sequence.toString(null));
	}
	
	@Test
	void toStringNestedSequence() {
		YamlSequence outer = new YamlSequence();
		YamlSequence inner = new YamlSequence();
		inner.add("a");
		inner.add("b");
		outer.add(inner);
		outer.add("c");
		
		String flowResult = outer.toString(FLOW_CONFIG);
		assertEquals("[[a, b], c]", flowResult);
	}
	
	@Test
	void toStringWithMixedTypes() {
		YamlSequence sequence = new YamlSequence();
		sequence.add("string");
		sequence.add(42);
		sequence.add(true);
		sequence.add(YamlNull.INSTANCE);
		
		assertEquals("[string, 42, true, null]", sequence.toString(FLOW_CONFIG));
	}
	
	@Test
	void preservesInsertionOrder() {
		YamlSequence sequence = new YamlSequence();
		for (int i = 0; i < 100; i++) {
			sequence.add(i);
		}
		
		for (int i = 0; i < 100; i++) {
			assertEquals(i, sequence.getAsInteger(i));
		}
	}
}
