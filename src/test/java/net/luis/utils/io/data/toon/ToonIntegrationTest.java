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

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.OutputProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ToonReader} and {@link ToonWriter} integration.<br>
 *
 * @author Luis-St
 */
class ToonIntegrationTest {
	
	private static Path tempFile;
	
	@AfterAll
	static void cleanup() throws IOException {
		if (tempFile != null && Files.exists(tempFile)) {
			Files.delete(tempFile);
		}
	}
	
	private ToonObject writeAndReadObject(ToonObject original, ToonConfig config) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ToonWriter writer = new ToonWriter(new OutputProvider(baos), config);
		writer.writeToon(original);
		
		String written = baos.toString(StandardCharsets.UTF_8);
		ToonReader reader = new ToonReader(written, config);
		ToonElement result = reader.readToon();
		assertInstanceOf(ToonObject.class, result);
		return result.getAsToonObject();
	}
	
	private ToonObject readFromString(String input, ToonConfig config) {
		ToonReader reader = new ToonReader(input, config);
		ToonElement result = reader.readToon();
		assertInstanceOf(ToonObject.class, result);
		return result.getAsToonObject();
	}
	
	@Test
	void roundTripSimpleObject() {
		ToonObject original = new ToonObject();
		original.add("name", "Alice");
		original.add("age", 30L);
		original.add("active", true);
		original.add("score", 99.5);
		
		ToonObject result = this.writeAndReadObject(original, ToonConfig.DEFAULT);
		
		assertEquals("Alice", result.getAsString("name"));
		assertEquals(30L, result.getAsLong("age"));
		assertTrue(result.getAsBoolean("active"));
		assertEquals(99.5, result.getAsDouble("score"), 0.001);
	}
	
	@Test
	void roundTripNestedObject() {
		ToonObject original = new ToonObject();
		original.add("title", "config");
		
		ToonObject database = new ToonObject();
		database.add("host", "localhost");
		database.add("port", 5432L);
		database.add("enabled", true);
		original.add("database", database);
		
		ToonObject result = this.writeAndReadObject(original, ToonConfig.DEFAULT);
		
		assertEquals("config", result.getAsString("title"));
		assertTrue(result.containsKey("database"));
		
		ToonObject resultDb = result.getToonObject("database");
		assertEquals("localhost", resultDb.getAsString("host"));
		assertEquals(5432L, resultDb.getAsLong("port"));
		assertTrue(resultDb.getAsBoolean("enabled"));
	}
	
	@Test
	void roundTripPrimitiveArray() {
		String input = "tags: [3,]: alpha, beta, gamma";
		ToonObject result = this.readFromString(input, ToonConfig.DEFAULT);
		
		assertTrue(result.containsKey("tags"));
		ToonArray resultTags = result.getToonArray("tags");
		assertEquals(3, resultTags.size());
		assertEquals("alpha", resultTags.getAsString(0));
		assertEquals("beta", resultTags.getAsString(1));
		assertEquals("gamma", resultTags.getAsString(2));
		
		// Verify the writer can produce output for the array
		ToonObject original = new ToonObject();
		ToonArray tags = new ToonArray();
		tags.add("alpha");
		tags.add("beta");
		tags.add("gamma");
		original.add("tags", tags);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ToonWriter writer = new ToonWriter(new OutputProvider(baos));
		writer.writeToon(original);
		String written = baos.toString(StandardCharsets.UTF_8);
		assertTrue(written.contains("alpha"));
		assertTrue(written.contains("beta"));
		assertTrue(written.contains("gamma"));
	}
	
	@Test
	void roundTripTabularArray() {
		String input = "users: [2,]{name, age}:\n  Alice, 30\n  Bob, 25";
		ToonObject result = this.readFromString(input, ToonConfig.DEFAULT);
		
		assertTrue(result.containsKey("users"));
		ToonArray resultUsers = result.getToonArray("users");
		assertEquals(2, resultUsers.size());
		
		ToonObject alice = resultUsers.getAsToonObject(0);
		assertEquals("Alice", alice.getAsString("name"));
		assertEquals(30L, alice.getAsLong("age"));
		
		ToonObject bob = resultUsers.getAsToonObject(1);
		assertEquals("Bob", bob.getAsString("name"));
		assertEquals(25L, bob.getAsLong("age"));
	}
	
	@Test
	void roundTripAllDelimiters() {
		// Comma delimiter
		String commaInput = "items: [3,]: x, y, z";
		ToonObject commaResult = this.readFromString(commaInput, ToonConfig.DEFAULT);
		ToonArray commaItems = commaResult.getToonArray("items");
		assertEquals(3, commaItems.size());
		assertEquals("x", commaItems.getAsString(0));
		assertEquals("y", commaItems.getAsString(1));
		assertEquals("z", commaItems.getAsString(2));
		
		// Tab delimiter
		String tabInput = "items: [3\t]: x\ty\tz";
		ToonConfig tabConfig = new ToonConfig(
			true, 2, ToonConfig.Delimiter.TAB, ToonConfig.KeyFolding.OFF, Integer.MAX_VALUE, ToonConfig.PathExpansion.OFF, StandardCharsets.UTF_8
		);
		ToonObject tabResult = this.readFromString(tabInput, tabConfig);
		ToonArray tabItems = tabResult.getToonArray("items");
		assertEquals(3, tabItems.size());
		assertEquals("x", tabItems.getAsString(0));
		assertEquals("y", tabItems.getAsString(1));
		assertEquals("z", tabItems.getAsString(2));
		
		// Pipe delimiter
		String pipeInput = "items: [3|]: x| y| z";
		ToonConfig pipeConfig = new ToonConfig(
			true, 2, ToonConfig.Delimiter.PIPE, ToonConfig.KeyFolding.OFF, Integer.MAX_VALUE, ToonConfig.PathExpansion.OFF, StandardCharsets.UTF_8
		);
		ToonObject pipeResult = this.readFromString(pipeInput, pipeConfig);
		ToonArray pipeItems = pipeResult.getToonArray("items");
		assertEquals(3, pipeItems.size());
		assertEquals("x", pipeItems.getAsString(0));
		assertEquals("y", pipeItems.getAsString(1));
		assertEquals("z", pipeItems.getAsString(2));
	}
	
	@Test
	void roundTripBooleanAndNull() {
		ToonObject original = new ToonObject();
		original.add("enabled", true);
		original.add("disabled", false);
		original.add("missing", (ToonElement) null);
		
		ToonObject result = this.writeAndReadObject(original, ToonConfig.DEFAULT);
		
		assertTrue(result.getAsBoolean("enabled"));
		assertFalse(result.getAsBoolean("disabled"));
		assertTrue(result.get("missing").isToonNull());
	}
	
	@Test
	void roundTripQuotedStrings() {
		ToonObject original = new ToonObject();
		original.add("greeting", "hello world");
		original.add("special", "value:with:colons");
		original.add("empty", "");
		
		ToonObject result = this.writeAndReadObject(original, ToonConfig.DEFAULT);
		
		assertEquals("hello world", result.getAsString("greeting"));
		assertEquals("value:with:colons", result.getAsString("special"));
		assertEquals("", result.getAsString("empty"));
	}
	
	@Test
	void roundTripEmptyDocument() {
		ToonObject original = new ToonObject();
		
		ToonObject result = this.writeAndReadObject(original, ToonConfig.DEFAULT);
		
		assertEquals(0, result.size());
		assertTrue(result.isEmpty());
	}
	
	@Test
	void fileIoRoundTrip() throws IOException {
		tempFile = Files.createTempFile("toon_test_", ".toon");
		
		ToonObject original = new ToonObject();
		original.add("title", "file test");
		original.add("version", 1L);
		original.add("debug", false);
		
		try (ToonWriter writer = new ToonWriter(new OutputProvider(tempFile))) {
			writer.writeToon(original);
		}
		
		ToonElement result;
		try (ToonReader reader = new ToonReader(new InputProvider(tempFile))) {
			result = reader.readToon();
		}
		
		assertInstanceOf(ToonObject.class, result);
		ToonObject resultObj = result.getAsToonObject();
		assertEquals("file test", resultObj.getAsString("title"));
		assertEquals(1L, resultObj.getAsLong("version"));
		assertFalse(resultObj.getAsBoolean("debug"));
	}
}
