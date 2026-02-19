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

package net.luis.utils.io.data.toon;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ToonBuilder}.<br>
 *
 * @author Luis-St
 */
class ToonBuilderTest {
	
	@Test
	void objectFactory() {
		ToonBuilder builder = ToonBuilder.object();
		assertNotNull(builder);
		assertTrue(builder.isInObjectContext());
		assertFalse(builder.isInArrayContext());
		assertTrue(builder.isAtRootLevel());
		assertEquals(0, builder.getNestingDepth());
	}
	
	@Test
	void arrayFactory() {
		ToonBuilder builder = ToonBuilder.array();
		assertNotNull(builder);
		assertTrue(builder.isInArrayContext());
		assertFalse(builder.isInObjectContext());
		assertTrue(builder.isAtRootLevel());
		assertEquals(0, builder.getNestingDepth());
	}
	
	@Test
	void addStringProperty() {
		ToonObject result = ToonBuilder.object()
			.add("key", "value")
			.buildObject();
		
		assertTrue(result.containsKey("key"));
		assertEquals(new ToonValue("value"), result.get("key"));
	}
	
	@Test
	void addBooleanProperty() {
		ToonObject result = ToonBuilder.object()
			.add("key", true)
			.buildObject();
		
		assertTrue(result.containsKey("key"));
		assertEquals(new ToonValue(true), result.get("key"));
	}
	
	@Test
	void addNumberProperty() {
		ToonObject result = ToonBuilder.object()
			.add("key", 42)
			.buildObject();
		
		assertTrue(result.containsKey("key"));
		assertEquals(new ToonValue(42), result.get("key"));
	}
	
	@Test
	void addElementProperty() {
		ToonValue element = new ToonValue("x");
		ToonObject result = ToonBuilder.object()
			.add("key", element)
			.buildObject();
		
		assertTrue(result.containsKey("key"));
		assertEquals(element, result.get("key"));
	}
	
	@Test
	void addNullProperties() {
		ToonObject result = ToonBuilder.object()
			.add("nullStr", (String) null)
			.add("nullNum", (Number) null)
			.add("nullElem", (ToonElement) null)
			.buildObject();
		
		assertEquals(3, result.size());
		assertEquals(ToonNull.INSTANCE, result.get("nullStr"));
		assertEquals(ToonNull.INSTANCE, result.get("nullNum"));
		assertEquals(ToonNull.INSTANCE, result.get("nullElem"));
	}
	
	@Test
	void addIfTrueInObject() {
		ToonObject result = ToonBuilder.object()
			.addIf(true, "strKey", "value")
			.addIf(true, "boolKey", true)
			.addIf(true, "numKey", 42)
			.buildObject();
		
		assertEquals(3, result.size());
		assertTrue(result.containsKey("strKey"));
		assertTrue(result.containsKey("boolKey"));
		assertTrue(result.containsKey("numKey"));
	}
	
	@Test
	void addIfFalseInObject() {
		ToonObject result = ToonBuilder.object()
			.addIf(false, "strKey", "value")
			.addIf(false, "boolKey", true)
			.addIf(false, "numKey", 42)
			.buildObject();
		
		assertEquals(0, result.size());
	}
	
	@Test
	void addStringToArray() {
		ToonArray result = ToonBuilder.array()
			.add("a")
			.add("b")
			.buildArray();
		
		assertEquals(2, result.size());
		assertEquals(new ToonValue("a"), result.get(0));
		assertEquals(new ToonValue("b"), result.get(1));
	}
	
	@Test
	void addBooleanToArray() {
		ToonArray result = ToonBuilder.array()
			.add(true)
			.add(false)
			.buildArray();
		
		assertEquals(2, result.size());
		assertEquals(new ToonValue(true), result.get(0));
		assertEquals(new ToonValue(false), result.get(1));
	}
	
	@Test
	void addNumberToArray() {
		ToonArray result = ToonBuilder.array()
			.add(1)
			.add(2.5)
			.add(3L)
			.buildArray();
		
		assertEquals(3, result.size());
		assertEquals(new ToonValue(1), result.get(0));
		assertEquals(new ToonValue(2.5), result.get(1));
		assertEquals(new ToonValue(3L), result.get(2));
	}
	
	@Test
	void addElementToArray() {
		ToonValue element = new ToonValue("x");
		ToonArray result = ToonBuilder.array()
			.add(element)
			.buildArray();
		
		assertEquals(1, result.size());
		assertEquals(element, result.get(0));
	}
	
	@Test
	void addAllStrings() {
		ToonArray result = ToonBuilder.array()
			.addAll("a", "b", "c")
			.buildArray();
		
		assertEquals(3, result.size());
		assertEquals(new ToonValue("a"), result.get(0));
		assertEquals(new ToonValue("b"), result.get(1));
		assertEquals(new ToonValue("c"), result.get(2));
	}
	
	@Test
	void addAllNumbers() {
		ToonArray result = ToonBuilder.array()
			.addAll(1, 2, 3)
			.buildArray();
		
		assertEquals(3, result.size());
		assertEquals(new ToonValue(1), result.get(0));
		assertEquals(new ToonValue(2), result.get(1));
		assertEquals(new ToonValue(3), result.get(2));
	}
	
	@Test
	void addAllBooleans() {
		ToonArray result = ToonBuilder.array()
			.addAll(true, false, true)
			.buildArray();
		
		assertEquals(3, result.size());
		assertEquals(new ToonValue(true), result.get(0));
		assertEquals(new ToonValue(false), result.get(1));
		assertEquals(new ToonValue(true), result.get(2));
	}
	
	@Test
	void addIfInArray() {
		ToonArray result = ToonBuilder.array()
			.addIf(true, "included")
			.addIf(false, "excluded")
			.addIf(true, true)
			.addIf(false, false)
			.addIf(true, 42)
			.addIf(false, 99)
			.buildArray();
		
		assertEquals(3, result.size());
		assertEquals(new ToonValue("included"), result.get(0));
		assertEquals(new ToonValue(true), result.get(1));
		assertEquals(new ToonValue(42), result.get(2));
	}
	
	@Test
	void startAndEndObjectInObject() {
		ToonObject result = ToonBuilder.object()
			.startObject("nested")
			.add("inner", "value")
			.endObject()
			.buildObject();
		
		assertTrue(result.containsKey("nested"));
		ToonObject nested = result.getToonObject("nested");
		assertEquals(new ToonValue("value"), nested.get("inner"));
	}
	
	@Test
	void startObjectInArray() {
		ToonArray result = ToonBuilder.array()
			.add("first")
			.startObject()
			.add("id", 1)
			.add("name", "Item")
			.endObject()
			.add("last")
			.buildArray();
		
		assertEquals(3, result.size());
		assertEquals(new ToonValue("first"), result.get(0));
		assertTrue(result.get(1).isToonObject());
		assertEquals(new ToonValue("last"), result.get(2));
	}
	
	@Test
	void startAndEndArrayInObject() {
		ToonObject result = ToonBuilder.object()
			.add("name", "John")
			.startArray("hobbies")
			.add("reading")
			.add("coding")
			.endArray()
			.buildObject();
		
		assertEquals(2, result.size());
		assertTrue(result.get("hobbies").isToonArray());
		assertEquals(2, result.getToonArray("hobbies").size());
	}
	
	@Test
	void startArrayInArray() {
		ToonArray result = ToonBuilder.array()
			.add("outer")
			.startArray()
			.add("inner1")
			.add("inner2")
			.endArray()
			.buildArray();
		
		assertEquals(2, result.size());
		assertTrue(result.get(1).isToonArray());
		assertEquals(2, result.get(1).getAsToonArray().size());
	}
	
	@Test
	void deeplyNestedStructure() {
		ToonObject result = ToonBuilder.object()
			.add("level", 1)
			.startObject("nested")
			.add("level", 2)
			.startArray("items")
			.startObject()
			.add("level", 3)
			.endObject()
			.endArray()
			.endObject()
			.buildObject();
		
		assertEquals(2, result.size());
		ToonObject nested = result.getToonObject("nested");
		assertEquals(new ToonValue(2), nested.get("level"));
		ToonArray items = nested.getToonArray("items");
		assertEquals(1, items.size());
		assertTrue(items.get(0).isToonObject());
	}
	
	@Test
	void endObjectAtRootThrows() {
		ToonBuilder builder = ToonBuilder.object();
		assertThrows(IllegalStateException.class, builder::endObject);
	}
	
	@Test
	void endArrayAtRootThrows() {
		ToonBuilder builder = ToonBuilder.array();
		assertThrows(IllegalStateException.class, builder::endArray);
	}
	
	@Test
	void endObjectInArrayContextThrows() {
		ToonBuilder builder = ToonBuilder.array();
		assertThrows(IllegalStateException.class, builder::endObject);
	}
	
	@Test
	void endArrayInObjectContextThrows() {
		ToonBuilder builder = ToonBuilder.object();
		assertThrows(IllegalStateException.class, builder::endArray);
	}
	
	@Test
	void contextValidationInObjectMode() {
		ToonBuilder builder = ToonBuilder.object();
		
		assertDoesNotThrow(() -> builder.add("key", "value"));
		assertDoesNotThrow(() -> builder.startObject("nested"));
		assertDoesNotThrow(() -> builder.endObject());
		
		assertThrows(IllegalStateException.class, () -> builder.add("value"));
		assertThrows(IllegalStateException.class, () -> builder.add(42));
		assertThrows(IllegalStateException.class, () -> builder.startObject());
		assertThrows(IllegalStateException.class, () -> builder.startArray());
	}
	
	@Test
	void contextValidationInArrayMode() {
		ToonBuilder builder = ToonBuilder.array();
		
		assertDoesNotThrow(() -> builder.add("value"));
		assertDoesNotThrow(() -> builder.add(42));
		assertDoesNotThrow(() -> builder.startObject());
		assertDoesNotThrow(() -> builder.endObject());
		
		assertThrows(IllegalStateException.class, () -> builder.add("key", "value"));
		assertThrows(IllegalStateException.class, () -> builder.startObject("key"));
		assertThrows(IllegalStateException.class, () -> builder.startArray("key"));
	}
	
	@Test
	void nestingDepth() {
		ToonBuilder builder = ToonBuilder.object();
		assertEquals(0, builder.getNestingDepth());
		
		builder.startObject("level1");
		assertEquals(1, builder.getNestingDepth());
		
		builder.startArray("items");
		assertEquals(2, builder.getNestingDepth());
		
		builder.endArray();
		assertEquals(1, builder.getNestingDepth());
		
		builder.endObject();
		assertEquals(0, builder.getNestingDepth());
	}
	
	@Test
	void contextQueryMethods() {
		ToonBuilder builder = ToonBuilder.object();
		assertTrue(builder.isInObjectContext());
		assertFalse(builder.isInArrayContext());
		assertTrue(builder.isAtRootLevel());
		
		builder.startArray("arr");
		assertFalse(builder.isInObjectContext());
		assertTrue(builder.isInArrayContext());
		assertFalse(builder.isAtRootLevel());
		
		builder.endArray();
		assertTrue(builder.isInObjectContext());
		assertFalse(builder.isInArrayContext());
		assertTrue(builder.isAtRootLevel());
	}
	
	@Test
	void buildThrowsWithUnclosedContexts() {
		ToonBuilder builder = ToonBuilder.object()
			.startObject("nested")
			.add("inner", "data");
		
		assertThrows(IllegalStateException.class, builder::build);
	}
	
	@Test
	void buildObjectFromArrayThrows() {
		ToonBuilder builder = ToonBuilder.array().add("item");
		assertThrows(IllegalStateException.class, builder::buildObject);
	}
	
	@Test
	void buildArrayFromObjectThrows() {
		ToonBuilder builder = ToonBuilder.object().add("key", "value");
		assertThrows(IllegalStateException.class, builder::buildArray);
	}
	
	@Test
	void buildMultipleTimesReturnsConsistentResults() {
		ToonBuilder builder = ToonBuilder.object()
			.add("key", "value");
		
		ToonElement first = builder.build();
		ToonElement second = builder.build();
		ToonObject firstObject = builder.buildObject();
		ToonObject secondObject = builder.buildObject();
		
		assertEquals(first, second);
		assertEquals(firstObject, secondObject);
	}
	
	@Test
	void chainingSameInstance() {
		ToonBuilder builder = ToonBuilder.object();
		assertSame(builder, builder.add("key", "val"));
		assertSame(builder, builder.add("bool", true));
		assertSame(builder, builder.add("num", 42));
		assertSame(builder, builder.add("elem", new ToonValue("x")));
		assertSame(builder, builder.addIf(true, "cond", "yes"));
		assertSame(builder, builder.startObject("obj"));
		assertSame(builder, builder.endObject());
		assertSame(builder, builder.startArray("arr"));
		assertSame(builder, builder.endArray());
	}
	
	@Test
	void toStringReturnsContent() {
		ToonBuilder builder = ToonBuilder.object()
			.add("key", "value");
		
		String result = builder.toString();
		assertNotNull(result);
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
	}
	
	@Test
	void nullKeyValidation() {
		ToonBuilder builder = ToonBuilder.object();
		assertThrows(NullPointerException.class, () -> builder.add(null, "value"));
		assertThrows(NullPointerException.class, () -> builder.add(null, true));
		assertThrows(NullPointerException.class, () -> builder.add(null, 42));
		assertThrows(NullPointerException.class, () -> builder.add(null, (ToonElement) null));
		assertThrows(NullPointerException.class, () -> builder.startObject(null));
		assertThrows(NullPointerException.class, () -> builder.startArray(null));
	}
	
	@Test
	void complexRealWorldExample() {
		ToonObject result = ToonBuilder.object()
			.add("title", "toon Example")
			.add("version", 1)
			.startObject("database")
			.add("host", "localhost")
			.add("port", 5432)
			.add("enabled", true)
			.endObject()
			.startArray("tags")
			.add("production")
			.add("v1")
			.endArray()
			.addIf(true, "status", "active")
			.addIf(false, "deprecated", true)
			.buildObject();
		
		assertEquals(5, result.size());
		assertEquals(new ToonValue("toon Example"), result.get("title"));
		assertTrue(result.get("database").isToonObject());
		assertTrue(result.get("tags").isToonArray());
		assertEquals(2, result.getToonArray("tags").size());
		assertTrue(result.containsKey("status"));
		assertFalse(result.containsKey("deprecated"));
	}
}
