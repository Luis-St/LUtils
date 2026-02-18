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

import net.luis.utils.io.data.OutputProvider;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ToonWriter}.<br>
 *
 * @author Luis-St
 */
class ToonWriterTest {
	
	@Test
	void constructWithNullOutput() {
		assertThrows(NullPointerException.class, () -> new ToonWriter(null));
		assertThrows(NullPointerException.class, () -> new ToonWriter(null, ToonConfig.DEFAULT));
	}
	
	@Test
	void constructWithNullConfig() {
		OutputProvider provider = new OutputProvider(OutputStream.nullOutputStream());
		assertThrows(NullPointerException.class, () -> new ToonWriter(provider, null));
	}
	
	@Test
	void writeSimpleObject() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ToonWriter writer = new ToonWriter(new OutputProvider(baos));
		
		ToonObject object = new ToonObject();
		object.add("name", "hello");
		object.add("count", 42);
		
		writer.writeToon(object);
		String output = baos.toString();
		
		assertTrue(output.contains("name: hello"));
		assertTrue(output.contains("count: 42"));
	}
	
	@Test
	void writeNestedObject() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ToonWriter writer = new ToonWriter(new OutputProvider(baos));
		
		ToonObject root = new ToonObject();
		ToonObject nested = new ToonObject();
		nested.add("host", "localhost");
		nested.add("port", 8080);
		root.add("server", nested);
		
		writer.writeToon(root);
		String output = baos.toString();
		
		assertTrue(output.contains("server:"));
		assertTrue(output.contains("host: localhost"));
		assertTrue(output.contains("port: 8080"));
		assertTrue(output.contains("  host") || output.contains("host"));
	}
	
	@Test
	void writePrimitiveArray() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ToonWriter writer = new ToonWriter(new OutputProvider(baos));
		
		ToonObject root = new ToonObject();
		ToonArray array = new ToonArray();
		array.add("a");
		array.add("b");
		array.add("c");
		root.add("tags", array);
		
		writer.writeToon(root);
		String output = baos.toString();
		
		assertTrue(output.contains("tags"));
		assertTrue(output.contains("3"));
		assertTrue(output.contains("a"));
		assertTrue(output.contains("b"));
		assertTrue(output.contains("c"));
	}
	
	@Test
	void writeTabularArray() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ToonWriter writer = new ToonWriter(new OutputProvider(baos));
		
		ToonObject root = new ToonObject();
		ToonArray array = new ToonArray();
		
		ToonObject row1 = new ToonObject();
		row1.add("name", "Alice");
		row1.add("age", 30);
		array.add(row1);
		
		ToonObject row2 = new ToonObject();
		row2.add("name", "Bob");
		row2.add("age", 25);
		array.add(row2);
		
		root.add("users", array);
		
		writer.writeToon(root);
		String output = baos.toString();
		
		assertTrue(output.contains("users"));
		assertTrue(output.contains("name"));
		assertTrue(output.contains("age"));
		assertTrue(output.contains("Alice"));
		assertTrue(output.contains("Bob"));
	}
	
	@Test
	void writeExpandedArray() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ToonWriter writer = new ToonWriter(new OutputProvider(baos));
		
		ToonObject root = new ToonObject();
		ToonArray array = new ToonArray();
		
		ToonObject item1 = new ToonObject();
		item1.add("key", "val1");
		item1.add("extra", "data1");
		array.add(item1);
		
		ToonObject item2 = new ToonObject();
		item2.add("key", "val2");
		item2.add("other", "data2");
		array.add(item2);
		
		root.add("items", array);
		
		writer.writeToon(root);
		String output = baos.toString();
		
		assertTrue(output.contains("items"));
		assertTrue(output.contains("val1"));
		assertTrue(output.contains("val2"));
	}
	
	@Test
	void writeEmptyObject() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ToonWriter writer = new ToonWriter(new OutputProvider(baos));
		
		ToonObject object = new ToonObject();
		writer.writeToon(object);
		
		String output = baos.toString();
		assertTrue(output.isEmpty());
	}
	
	@Test
	void closeWriter() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ToonWriter writer = new ToonWriter(new OutputProvider(baos));
		assertDoesNotThrow(writer::close);
	}
}
