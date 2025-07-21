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
import java.nio.file.*;
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
		Files.deleteIfExists(Path.of("temp/deep/structure/file.txt"));
		Files.deleteIfExists(Path.of("temp/deep/structure"));
		Files.deleteIfExists(Path.of("temp/deep"));
		Files.deleteIfExists(Path.of("temp"));
	}
	//endregion
	
	@Test
	void splitWithNullInput() {
		Pair<String, String> result = FileUtils.split(null);
		assertEquals(Pair.of("", ""), result);
	}
	
	@Test
	void splitWithEmptyInput() {
		assertEquals(Pair.of("", ""), FileUtils.split(""));
		assertEquals(Pair.of("", ""), FileUtils.split("   "));
	}
	
	@Test
	void splitWithDirectoriesOnly() {
		assertEquals(Pair.of(".", ""), FileUtils.split("."));
		assertEquals(Pair.of("/", ""), FileUtils.split("/"));
		assertEquals(Pair.of("./", ""), FileUtils.split("./"));
		assertEquals(Pair.of("FileUtils", ""), FileUtils.split("FileUtils"));
		assertEquals(Pair.of("/FileUtils/", ""), FileUtils.split("/FileUtils/"));
	}
	
	@Test
	void splitWithFiles() {
		assertEquals(Pair.of("/", "FileUtils.json"), FileUtils.split("/FileUtils.json"));
		assertEquals(Pair.of("/FileUtils/", "FileUtils.json"), FileUtils.split("/FileUtils/FileUtils.json"));
		assertEquals(Pair.of("./path/", "file.txt"), FileUtils.split("./path/file.txt"));
	}
	
	@Test
	void splitWithWindowsPaths() {
		assertEquals(Pair.of("C:/", "file.txt"), FileUtils.split("C:\\file.txt"));
		assertEquals(Pair.of("C:/Windows/System32", ""), FileUtils.split("C:\\Windows\\System32"));
	}
	
	@Test
	void splitWithComplexPaths() {
		assertEquals(Pair.of("/very/deep/nested/path/", "file.ext"), FileUtils.split("/very/deep/nested/path/file.ext"));
		assertEquals(Pair.of("./relative/path/", "file.name.ext"), FileUtils.split("./relative/path/file.name.ext"));
	}
	
	@Test
	void getNameWithNullInput() {
		assertEquals("", FileUtils.getName(null));
	}
	
	@Test
	void getNameWithEmptyInput() {
		assertEquals("", FileUtils.getName(""));
		assertEquals("", FileUtils.getName("/"));
		assertEquals("", FileUtils.getName("/FileUtils"));
	}
	
	@Test
	void getNameWithValidFiles() {
		assertEquals("FileUtils", FileUtils.getName("/FileUtils.json"));
		assertEquals("FileUtils", FileUtils.getName("/FileUtils/FileUtils.json"));
		assertEquals("file", FileUtils.getName("./path/file.txt"));
		assertEquals("complex.file", FileUtils.getName("/path/complex.file.ext"));
	}
	
	@Test
	void getNameWithFileNameOnly() {
		assertEquals("filename", FileUtils.getName("filename.ext"));
		assertEquals("test", FileUtils.getName("test.txt"));
	}
	
	@Test
	void getExtensionWithNullInput() {
		assertEquals("", FileUtils.getExtension(null));
	}
	
	@Test
	void getExtensionWithEmptyInput() {
		assertEquals("", FileUtils.getExtension(""));
		assertEquals("", FileUtils.getExtension("/"));
		assertEquals("", FileUtils.getExtension("/FileUtils"));
	}
	
	@Test
	void getExtensionWithValidFiles() {
		assertEquals("json", FileUtils.getExtension("/FileUtils.json"));
		assertEquals("json", FileUtils.getExtension("/FileUtils/FileUtils.json"));
		assertEquals("txt", FileUtils.getExtension("./path/file.txt"));
		assertEquals("ext", FileUtils.getExtension("/path/complex.file.ext"));
	}
	
	@Test
	void getExtensionWithMultipleDots() {
		assertEquals("gz", FileUtils.getExtension("file.tar.gz"));
		assertEquals("backup", FileUtils.getExtension("data.old.backup"));
	}
	
	@Test
	void getRelativePathWithNullInput() {
		assertEquals("./", FileUtils.getRelativePath(null));
	}
	
	@Test
	void getRelativePathWithEmptyInput() {
		assertEquals("./", FileUtils.getRelativePath(""));
		assertEquals("./", FileUtils.getRelativePath("/"));
		assertEquals("./", FileUtils.getRelativePath("./"));
	}
	
	@Test
	void getRelativePathWithDirectories() {
		assertEquals("./FileUtils/", FileUtils.getRelativePath("FileUtils"));
		assertEquals("./FileUtils/", FileUtils.getRelativePath("/FileUtils"));
		assertEquals("./FileUtils/", FileUtils.getRelativePath("/FileUtils/"));
		assertEquals("./FileUtils/", FileUtils.getRelativePath("./FileUtils/"));
	}
	
	@Test
	void getRelativePathWithFiles() {
		assertEquals("./FileUtils/", FileUtils.getRelativePath("FileUtils/FileUtils.json"));
		assertEquals("./FileUtils/", FileUtils.getRelativePath("/FileUtils/FileUtils.json"));
		assertEquals("./FileUtils/", FileUtils.getRelativePath("./FileUtils/FileUtils.json"));
	}
	
	@Test
	void getRelativePathWithComplexPaths() {
		assertEquals("./deep/nested/path/", FileUtils.getRelativePath("deep/nested/path"));
		assertEquals("./very/deep/structure/", FileUtils.getRelativePath("/very/deep/structure/file.txt"));
	}
	
	@Test
	void createWithNullFile() {
		assertThrows(NullPointerException.class, () -> FileUtils.create(null));
	}
	
	@Test
	void createSimpleFile() throws Exception {
		Path file = Path.of("FileUtils.json");
		assertFalse(Files.exists(file));
		
		FileUtils.create(file);
		assertTrue(Files.exists(file));
	}
	
	@Test
	void createDirectoryOnly() throws Exception {
		Path directory = Path.of("FileUtils");
		assertFalse(Files.exists(directory));
		
		FileUtils.create(directory);
		assertTrue(Files.exists(directory));
		assertTrue(Files.isDirectory(directory));
	}
	
	@Test
	void createFileWithNestedDirectories() throws Exception {
		Path file = Path.of("FileUtils/FileUtils.json");
		assertFalse(Files.exists(file));
		assertFalse(Files.exists(file.getParent()));
		
		FileUtils.create(file);
		assertTrue(Files.exists(file));
		assertTrue(Files.exists(file.getParent()));
		assertTrue(Files.isRegularFile(file));
		assertTrue(Files.isDirectory(file.getParent()));
	}
	
	@Test
	void createFileAlreadyExists() throws Exception {
		Path file = Path.of("FileUtils.json");
		Files.delete(file);
		Files.createFile(file);
		
		assertThrows(FileAlreadyExistsException.class, () -> FileUtils.create(file));
	}
	
	@Test
	void createDeepNestedStructure() throws Exception {
		Path file = Path.of("temp/deep/structure/file.txt");
		assertFalse(Files.exists(file));
		
		FileUtils.create(file);
		assertTrue(Files.exists(file));
		assertTrue(Files.exists(file.getParent()));
		assertTrue(Files.isRegularFile(file));
	}
	
	@Test
	void createIfNotExistsWithNullFile() {
		assertThrows(NullPointerException.class, () -> FileUtils.createIfNotExists(null));
	}
	
	@Test
	void createIfNotExistsNewFile() throws Exception {
		Path file = Path.of("FileUtils.xml");
		assertFalse(Files.exists(file));
		
		FileUtils.createIfNotExists(file);
		assertTrue(Files.exists(file));
		Files.delete(file);
	}
	
	@Test
	void createIfNotExistsExistingFile() throws Exception {
		Path file = Path.of("FileUtils.xml");
		Files.createFile(file);
		assertTrue(Files.exists(file));
		
		assertDoesNotThrow(() -> FileUtils.createIfNotExists(file));
		assertTrue(Files.exists(file));
		Files.delete(file);
	}
	
	@Test
	void createSessionDirectoryWithNullPrefix() throws Exception {
		Path sessionDir = FileUtils.createSessionDirectory(null);
		
		assertNotNull(sessionDir);
		assertTrue(Files.exists(sessionDir));
		assertTrue(Files.isDirectory(sessionDir));
		assertTrue(sessionDir.toString().contains("temp"));
	}
	
	@Test
	void createSessionDirectoryWithEmptyPrefix() throws Exception {
		Path sessionDir = FileUtils.createSessionDirectory("");
		
		assertNotNull(sessionDir);
		assertTrue(Files.exists(sessionDir));
		assertTrue(Files.isDirectory(sessionDir));
	}
	
	@Test
	void createSessionDirectoryWithCustomPrefix() throws Exception {
		Path sessionDir = FileUtils.createSessionDirectory("FileUtils");
		
		assertNotNull(sessionDir);
		assertTrue(Files.exists(sessionDir));
		assertTrue(Files.isDirectory(sessionDir));
		assertTrue(sessionDir.toString().contains("FileUtils"));
	}
	
	@Test
	void deleteRecursivelyWithNullPath() {
		assertThrows(NullPointerException.class, () -> FileUtils.deleteRecursively(null));
	}
	
	@Test
	void deleteRecursivelyNonExistentPath() {
		Path nonExistent = Path.of("nonexistent/path");
		assertThrows(FileNotFoundException.class, () -> FileUtils.deleteRecursively(nonExistent));
	}
	
	@Test
	void deleteRecursivelySimpleFile() throws Exception {
		Path file = Path.of("FileUtils.json");
		Files.createFile(file);
		assertTrue(Files.exists(file));
		
		FileUtils.deleteRecursively(file);
		assertFalse(Files.exists(file));
	}
	
	@Test
	void deleteRecursivelyDirectoryWithFiles() throws Exception {
		Path dir = Path.of("FileUtils");
		Path file1 = dir.resolve("file1.txt");
		Path file2 = dir.resolve("file2.txt");
		Path subDir = dir.resolve("subdir");
		Path file3 = subDir.resolve("file3.txt");
		
		Files.createDirectories(subDir);
		Files.createFile(file1);
		Files.createFile(file2);
		Files.createFile(file3);
		
		assertTrue(Files.exists(dir));
		assertTrue(Files.exists(file1));
		assertTrue(Files.exists(file2));
		assertTrue(Files.exists(file3));
		
		FileUtils.deleteRecursively(dir);
		
		assertFalse(Files.exists(dir));
		assertFalse(Files.exists(file1));
		assertFalse(Files.exists(file2));
		assertFalse(Files.exists(file3));
	}
	
	@Test
	void readStringWithNullProvider() {
		assertThrows(NullPointerException.class, () -> FileUtils.readString((InputProvider) null));
	}
	
	@Test
	void readStringFromInputProvider() throws Exception {
		InputProvider provider = new InputProvider(new StringInputStream("Test Content"));
		
		String result = FileUtils.readString(provider);
		assertEquals("Test Content", result);
	}
	
	@Test
	void readStringWithCharset() throws Exception {
		InputProvider provider = new InputProvider(new StringInputStream("Test Content"));
		
		String result = FileUtils.readString(provider, StandardCharsets.UTF_8);
		assertEquals("Test Content", result);
		
		assertThrows(NullPointerException.class, () -> FileUtils.readString(provider, null));
	}
	
	@Test
	void readStringFromReader() throws Exception {
		StringReader reader = new StringReader("Test Content");
		
		String result = FileUtils.readString(reader);
		assertEquals("Test Content", result);
		
		assertThrows(NullPointerException.class, () -> FileUtils.readString((Reader) null));
	}
	
	@Test
	void readStringWithMultipleLines() throws Exception {
		String content = "Line 1\nLine 2\nLine 3";
		StringReader reader = new StringReader(content);
		
		String result = FileUtils.readString(reader);
		assertEquals(content, result);
	}
	
	@Test
	void readStringWithEmptyContent() throws Exception {
		StringReader reader = new StringReader("");
		
		String result = FileUtils.readString(reader);
		assertEquals("", result);
	}
	
	@Test
	void readStringWithSpecialCharacters() throws Exception {
		String content = "Special chars: äöü ñ 中文 🎉";
		InputProvider provider = new InputProvider(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
		
		String result = FileUtils.readString(provider, StandardCharsets.UTF_8);
		assertEquals(content, result);
	}
	
	//region Internal classes
	@MockObject(InputStream.class)
	private static final class StringInputStream extends InputStream {
		
		private final String string;
		private int index;
		
		private StringInputStream(@NotNull String string) {
			this.string = Objects.requireNonNull(string, "String must not be null");
		}
		
		@Override
		public int read() throws IOException {
			return this.index < this.string.length() ? this.string.charAt(this.index++) : -1;
		}
		
		@Override
		public void reset() {
			this.index = 0;
		}
	}
	//endregion
}
