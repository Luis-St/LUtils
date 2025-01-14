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

package net.luis.utils.io;

import net.luis.utils.annotation.type.MockObject;
import net.luis.utils.io.data.InputProvider;
import net.luis.utils.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FileUtils}.<br>
 *
 * @author Luis-St
 */
class FileUtilsTest {
	
	//region Cleanup
	@AfterAll
	static void cleanUpAfter() throws Exception {
		Files.deleteIfExists(Path.of("FileUtils.json"));
		Files.deleteIfExists(Path.of("FileUtils/FileUtils.json"));
		Files.deleteIfExists(Path.of("FileUtils"));
		Files.deleteIfExists(Path.of("FileUtils.xml"));
	}
	//endregion
	
	@Test
	void split() {
		assertDoesNotThrow(() -> FileUtils.split(null));
		assertDoesNotThrow(() -> FileUtils.split(""));
		assertEquals(Pair.of("", ""), FileUtils.split(""));
		assertEquals(Pair.of(".", ""), FileUtils.split("."));
		assertEquals(Pair.of("/", ""), FileUtils.split("/"));
		assertEquals(Pair.of("./", ""), FileUtils.split("./"));
		assertEquals(Pair.of("FileUtils", ""), FileUtils.split("FileUtils"));
		assertEquals(Pair.of("/FileUtils", ""), FileUtils.split("/FileUtils"));
		assertEquals(Pair.of("/FileUtils/", ""), FileUtils.split("/FileUtils/"));
		assertEquals(Pair.of("/", "FileUtils.json"), FileUtils.split("/FileUtils.json"));
		assertEquals(Pair.of("/FileUtils/", "FileUtils.json"), FileUtils.split("/FileUtils/FileUtils.json"));
	}
	
	@Test
	void getName() {
		assertDoesNotThrow(() -> FileUtils.getName(null));
		assertDoesNotThrow(() -> FileUtils.getName(""));
		assertEquals("", FileUtils.getName(""));
		assertEquals("", FileUtils.getName("/"));
		assertEquals("", FileUtils.getName("/FileUtils"));
		assertEquals("FileUtils", FileUtils.getName("/FileUtils.json"));
		assertEquals("FileUtils", FileUtils.getName("/FileUtils/FileUtils.json"));
	}
	
	@Test
	void getExtension() {
		assertDoesNotThrow(() -> FileUtils.getExtension(null));
		assertDoesNotThrow(() -> FileUtils.getExtension(""));
		assertEquals("", FileUtils.getExtension(""));
		assertEquals("", FileUtils.getExtension("/"));
		assertEquals("", FileUtils.getExtension("/FileUtils"));
		assertEquals("json", FileUtils.getExtension("/FileUtils.json"));
		assertEquals("json", FileUtils.getExtension("/FileUtils/FileUtils.json"));
	}
	
	@Test
	void getRelativePath() {
		assertDoesNotThrow(() -> FileUtils.getRelativePath(null));
		assertDoesNotThrow(() -> FileUtils.getRelativePath(""));
		assertEquals("./", FileUtils.getRelativePath(null));
		assertEquals("./", FileUtils.getRelativePath(""));
		assertEquals("./", FileUtils.getRelativePath("/"));
		assertEquals("./FileUtils/", FileUtils.getRelativePath("FileUtils"));
		assertEquals("./FileUtils/", FileUtils.getRelativePath("/FileUtils"));
		assertEquals("./FileUtils/", FileUtils.getRelativePath("/FileUtils/"));
		assertEquals("./FileUtils/", FileUtils.getRelativePath("./FileUtils/"));
		assertEquals("./FileUtils/", FileUtils.getRelativePath("FileUtils/FileUtils.json"));
		assertEquals("./FileUtils/", FileUtils.getRelativePath("/FileUtils/FileUtils.json"));
		assertEquals("./FileUtils/", FileUtils.getRelativePath("./FileUtils/FileUtils.json"));
	}
	
	@Test
	void create() throws Exception {
		Path file = Path.of("FileUtils.json");
		Path directory = Path.of("FileUtils");
		Path both = Path.of("FileUtils/FileUtils.json");
		
		assertDoesNotThrow(() -> FileUtils.create(file));
		assertTrue(Files.exists(file));
		assertDoesNotThrow(() -> FileUtils.create(directory));
		assertTrue(Files.exists(directory));
		assertDoesNotThrow(() -> FileUtils.create(both));
		assertTrue(Files.exists(both));
		assertThrows(NullPointerException.class, () -> FileUtils.create(null));
		assertThrows(IOException.class, () -> FileUtils.create(file));
	}
	
	@Test
	void createIfNotExists() {
		Path file = Path.of("FileUtils.xml");
		assertFalse(Files.exists(file));
		assertDoesNotThrow(() -> FileUtils.createIfNotExists(file));
		assertTrue(Files.exists(file));
		assertDoesNotThrow(() -> FileUtils.createIfNotExists(file));
		assertThrows(NullPointerException.class, () -> FileUtils.createIfNotExists(null));
	}
	
	@Test
	void createSessionDirectory() {
		assertDoesNotThrow(() -> FileUtils.createSessionDirectory(null));
		assertDoesNotThrow(() -> FileUtils.createSessionDirectory(""));
		assertDoesNotThrow(() -> FileUtils.createSessionDirectory("FileUtils"));
	}
	
	@Test
	void deleteRecursively() throws IOException {
		Path file = Path.of("FileUtils/FileUtils/FileUtils.json");
		assertDoesNotThrow(() -> FileUtils.create(file));
		assertTrue(Files.exists(file));
		assertDoesNotThrow(() -> FileUtils.deleteRecursively(Path.of("FileUtils")));
		assertFalse(Files.exists(Path.of("FileUtils")));
		assertThrows(NullPointerException.class, () -> FileUtils.deleteRecursively(null));
		assertThrows(IOException.class, () -> FileUtils.deleteRecursively(file));
	}
	
	@Test
	void readString() throws IOException {
		assertThrows(NullPointerException.class, () -> FileUtils.readString((InputProvider) null));
		assertEquals("Test", FileUtils.readString(new InputProvider(new StringInputStream("Test"))));
		
		assertThrows(NullPointerException.class, () -> FileUtils.readString(null, null));
		assertEquals("Test", FileUtils.readString(new InputProvider(new StringInputStream("Test")), StandardCharsets.UTF_8));
		
		assertThrows(NullPointerException.class, () -> FileUtils.readString((Reader) null));
		assertEquals("Test", FileUtils.readString(new StringReader("Test")));
	}
	
	//region Internal classes
	@MockObject(InputStream.class)
	private static class StringInputStream extends InputStream {
		
		private final String string;
		private int index;
		
		private StringInputStream(@NotNull String string) {
			this.string = Objects.requireNonNull(string, "String must not be null");
		}
		
		@Override
		public int read() throws IOException {
			return this.index < this.string.length() ? this.string.charAt(this.index++) : -1;
		}
		
		public void reset() {
			this.index = 0;
		}
	}
	//endregion
}
