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

import net.luis.utils.io.data.toon.exception.ToonSyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ToonReader}.<br>
 *
 * @author Luis-St
 */
class ToonReaderTest {
	
	@Test
	void constructWithNullString() {
		assertThrows(NullPointerException.class, () -> new ToonReader((String) null));
		assertThrows(NullPointerException.class, () -> new ToonReader((String) null, ToonConfig.DEFAULT));
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new ToonReader("", null));
	}
	
	@Test
	void readEmptyDocument() {
		ToonReader reader = new ToonReader("");
		ToonElement result = reader.readToon();
		assertInstanceOf(ToonObject.class, result);
		ToonObject object = result.getAsToonObject();
		assertEquals(0, object.size());
		assertTrue(object.isEmpty());
	}
	
	@Test
	void readSimpleObject() {
		String input = "name: hello\nage: 30";
		ToonReader reader = new ToonReader(input);
		ToonElement result = reader.readToon();
		
		assertInstanceOf(ToonObject.class, result);
		ToonObject object = result.getAsToonObject();
		assertEquals(2, object.size());
		assertEquals("hello", object.getAsString("name"));
		assertEquals(30L, object.getAsLong("age"));
	}
	
	@Test
	void readNestedObject() {
		String input = "server:\n  host: localhost\n  port: 8080";
		ToonReader reader = new ToonReader(input);
		ToonElement result = reader.readToon();
		
		assertInstanceOf(ToonObject.class, result);
		ToonObject object = result.getAsToonObject();
		assertTrue(object.containsKey("server"));
		
		ToonObject server = object.getToonObject("server");
		assertEquals("localhost", server.getAsString("host"));
		assertEquals(8080L, server.getAsLong("port"));
	}
	
	@Test
	void readInlineArray() {
		String input = "tags: [3,]: a, b, c";
		ToonReader reader = new ToonReader(input);
		ToonElement result = reader.readToon();
		
		assertInstanceOf(ToonObject.class, result);
		ToonObject object = result.getAsToonObject();
		assertTrue(object.containsKey("tags"));
		
		ToonArray tags = object.getToonArray("tags");
		assertEquals(3, tags.size());
		assertEquals("a", tags.getAsString(0));
		assertEquals("b", tags.getAsString(1));
		assertEquals("c", tags.getAsString(2));
	}
	
	@Test
	void readTabularArray() {
		String input = "users: [2,]{name, age}:\n  Alice, 30\n  Bob, 25";
		ToonReader reader = new ToonReader(input);
		ToonElement result = reader.readToon();
		
		assertInstanceOf(ToonObject.class, result);
		ToonObject object = result.getAsToonObject();
		assertTrue(object.containsKey("users"));
		
		ToonArray users = object.getToonArray("users");
		assertEquals(2, users.size());
		
		ToonObject alice = users.getAsToonObject(0);
		assertEquals("Alice", alice.getAsString("name"));
		assertEquals(30L, alice.getAsLong("age"));
		
		ToonObject bob = users.getAsToonObject(1);
		assertEquals("Bob", bob.getAsString("name"));
		assertEquals(25L, bob.getAsLong("age"));
	}
	
	@Test
	void readExpandedArray() {
		String input = "items: [3]:\n  - alpha\n  - beta\n  - gamma";
		ToonReader reader = new ToonReader(input);
		ToonElement result = reader.readToon();
		
		assertInstanceOf(ToonObject.class, result);
		ToonObject object = result.getAsToonObject();
		assertTrue(object.containsKey("items"));
		
		ToonArray items = object.getToonArray("items");
		assertEquals(3, items.size());
		assertEquals("alpha", items.getAsString(0));
		assertEquals("beta", items.getAsString(1));
		assertEquals("gamma", items.getAsString(2));
	}
	
	@Test
	void readQuotedString() {
		String input = "name: \"hello world\"";
		ToonReader reader = new ToonReader(input);
		ToonElement result = reader.readToon();
		
		assertInstanceOf(ToonObject.class, result);
		ToonObject object = result.getAsToonObject();
		assertEquals("hello world", object.getAsString("name"));
	}
	
	@Test
	void readBooleanValues() {
		String input = "flag: true\nother: false";
		ToonReader reader = new ToonReader(input);
		ToonElement result = reader.readToon();
		
		assertInstanceOf(ToonObject.class, result);
		ToonObject object = result.getAsToonObject();
		assertTrue(object.getAsBoolean("flag"));
		assertFalse(object.getAsBoolean("other"));
	}
	
	@Test
	void readNullValue() {
		String input = "value: null";
		ToonReader reader = new ToonReader(input);
		ToonElement result = reader.readToon();
		
		assertInstanceOf(ToonObject.class, result);
		ToonObject object = result.getAsToonObject();
		ToonElement value = object.get("value");
		assertInstanceOf(ToonNull.class, value);
		assertTrue(value.isToonNull());
	}
	
	@Test
	void readNumberValues() {
		String input = "count: 42\nprice: 3.14";
		ToonReader reader = new ToonReader(input);
		ToonElement result = reader.readToon();
		
		assertInstanceOf(ToonObject.class, result);
		ToonObject object = result.getAsToonObject();
		assertEquals(42L, object.getAsLong("count"));
		assertEquals(3.14, object.getAsDouble("price"), 0.001);
	}
	
	@Test
	void readTypeInference() {
		String input = "str: hello\nbool: true\nnum: 42\nfloatNum: 1.5\nnullVal: null";
		ToonReader reader = new ToonReader(input);
		ToonElement result = reader.readToon();
		
		ToonObject object = result.getAsToonObject();
		assertTrue(object.get("str").isToonString());
		assertTrue(object.get("bool").isToonBoolean());
		assertTrue(object.get("num").isToonNumber());
		assertTrue(object.get("floatNum").isToonNumber());
		assertTrue(object.get("nullVal").isToonNull());
	}
	
	@Test
	void readRootArray() {
		String input = "[3,]: x, y, z";
		ToonReader reader = new ToonReader(input);
		ToonElement result = reader.readToon();
		
		assertInstanceOf(ToonArray.class, result);
		ToonArray array = result.getAsToonArray();
		assertEquals(3, array.size());
		assertEquals("x", array.getAsString(0));
		assertEquals("y", array.getAsString(1));
		assertEquals("z", array.getAsString(2));
	}
	
	@Test
	void strictModeCountMismatch() {
		ToonConfig strict = ToonConfig.DEFAULT;
		assertTrue(strict.strict());
		
		String input = "tags: [5,]: a, b, c";
		ToonReader reader = new ToonReader(input, strict);
		assertThrows(ToonSyntaxException.class, reader::readToon);
	}
}
