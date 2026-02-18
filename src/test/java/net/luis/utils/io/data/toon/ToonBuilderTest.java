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
	void tableFactory() {
		ToonBuilder builder = ToonBuilder.table();
		assertNotNull(builder);
		assertTrue(builder.isAtRoot());
		assertEquals(0, builder.getNestingDepth());
	}
	
	@Test
	void arrayFactory() {
		ToonBuilder.ArrayBuilder builder = ToonBuilder.array();
		assertNotNull(builder);
	}
	
	@Test
	void addStringProperty() {
		ToonObject result = ToonBuilder.table()
			.add("key", "value")
			.build();
		
		assertTrue(result.containsKey("key"));
		assertEquals(new ToonValue("value"), result.get("key"));
	}
	
	@Test
	void addBooleanProperty() {
		ToonObject result = ToonBuilder.table()
			.add("key", true)
			.build();
		
		assertTrue(result.containsKey("key"));
		assertEquals(new ToonValue(true), result.get("key"));
	}
	
	@Test
	void addNumberProperty() {
		ToonObject result = ToonBuilder.table()
			.add("key", 42)
			.build();
		
		assertTrue(result.containsKey("key"));
		assertEquals(new ToonValue(42), result.get("key"));
	}
	
	@Test
	void addElementProperty() {
		ToonValue element = new ToonValue("x");
		ToonObject result = ToonBuilder.table()
			.add("key", element)
			.build();
		
		assertTrue(result.containsKey("key"));
		assertEquals(element, result.get("key"));
	}
	
	@Test
	void addIfTrue() {
		ToonObject result = ToonBuilder.table()
			.addIf(true, "key", "value")
			.build();
		
		assertTrue(result.containsKey("key"));
		assertEquals(new ToonValue("value"), result.get("key"));
	}
	
	@Test
	void addIfFalse() {
		ToonObject result = ToonBuilder.table()
			.addIf(false, "key", "value")
			.build();
		
		assertFalse(result.containsKey("key"));
		assertEquals(0, result.size());
	}
	
	@Test
	void startAndEndObject() {
		ToonObject result = ToonBuilder.table()
			.startObject("nested")
			.add("inner", "value")
			.endObject()
			.build();
		
		assertTrue(result.containsKey("nested"));
		ToonObject nested = result.getToonObject("nested");
		assertEquals(new ToonValue("value"), nested.get("inner"));
	}
	
	@Test
	void endObjectAtRoot() {
		ToonBuilder builder = ToonBuilder.table();
		assertThrows(IllegalStateException.class, builder::endObject);
	}
	
	@Test
	void nestingDepth() {
		ToonBuilder builder = ToonBuilder.table();
		assertEquals(0, builder.getNestingDepth());
		
		builder.startObject("level1");
		assertEquals(1, builder.getNestingDepth());
		
		builder.startObject("level2");
		assertEquals(2, builder.getNestingDepth());
		
		builder.endObject();
		assertEquals(1, builder.getNestingDepth());
		
		builder.endObject();
		assertEquals(0, builder.getNestingDepth());
	}
	
	@Test
	void isNestedAndIsAtRoot() {
		ToonBuilder builder = ToonBuilder.table();
		assertTrue(builder.isAtRoot());
		assertFalse(builder.isNested());
		
		builder.startObject("nested");
		assertFalse(builder.isAtRoot());
		assertTrue(builder.isNested());
		
		builder.endObject();
		assertTrue(builder.isAtRoot());
		assertFalse(builder.isNested());
	}
	
	@Test
	void buildClosesContext() {
		ToonBuilder builder = ToonBuilder.table();
		builder.add("root", "value");
		builder.startObject("nested");
		builder.add("inner", "data");
		
		ToonObject result = builder.build();
		assertNotNull(result);
		assertTrue(result.containsKey("root"));
		assertTrue(result.containsKey("nested"));
		
		assertTrue(builder.isAtRoot());
	}
	
	@Test
	void arrayBuilderAddMethods() {
		ToonArray result = ToonBuilder.array()
			.add("a")
			.add(1)
			.add(true)
			.build();
		
		assertEquals(3, result.size());
		assertEquals(new ToonValue("a"), result.get(0));
		assertEquals(new ToonValue(1), result.get(1));
		assertEquals(new ToonValue(true), result.get(2));
	}
	
	@Test
	void chainingSameInstance() {
		ToonBuilder builder = ToonBuilder.table();
		assertSame(builder, builder.add("key", "val"));
		assertSame(builder, builder.add("bool", true));
		assertSame(builder, builder.add("num", 42));
		assertSame(builder, builder.add("elem", new ToonValue("x")));
		assertSame(builder, builder.addIf(true, "cond", "yes"));
		assertSame(builder, builder.startObject("obj"));
		assertSame(builder, builder.endObject());
	}
}
