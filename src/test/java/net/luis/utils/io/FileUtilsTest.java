package net.luis.utils.io;

import net.luis.utils.util.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {
	
	//region Clean up
	@AfterAll
	static void tearDownAfter() throws Exception {
		Files.deleteIfExists(Path.of("test.json"));
		Files.deleteIfExists(Path.of("test/test.json"));
		Files.deleteIfExists(Path.of("test"));
		Files.deleteIfExists(Path.of("test.xml"));
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
		assertEquals(Pair.of("test", ""), FileUtils.split("test"));
		assertEquals(Pair.of("/test", ""), FileUtils.split("/test"));
		assertEquals(Pair.of("/test/", ""), FileUtils.split("/test/"));
		assertEquals(Pair.of("/", "test.json"), FileUtils.split("/test.json"));
		assertEquals(Pair.of("/test/", "test.json"), FileUtils.split("/test/test.json"));
	}
	
	@Test
	void getName() {
		assertDoesNotThrow(() -> FileUtils.getName(null));
		assertDoesNotThrow(() -> FileUtils.getName(""));
		assertEquals("", FileUtils.getName(""));
		assertEquals("", FileUtils.getName("/"));
		assertEquals("", FileUtils.getName("/test"));
		assertEquals("test", FileUtils.getName("/test.json"));
		assertEquals("test", FileUtils.getName("/test/test.json"));
	}
	
	@Test
	void getExtension() {
		assertDoesNotThrow(() -> FileUtils.getExtension(null));
		assertDoesNotThrow(() -> FileUtils.getExtension(""));
		assertEquals("", FileUtils.getExtension(""));
		assertEquals("", FileUtils.getExtension("/"));
		assertEquals("", FileUtils.getExtension("/test"));
		assertEquals("json", FileUtils.getExtension("/test.json"));
		assertEquals("json", FileUtils.getExtension("/test/test.json"));
	}
	
	@Test
	void getRelativePath() {
		assertDoesNotThrow(() -> FileUtils.getRelativePath(null));
		assertDoesNotThrow(() -> FileUtils.getRelativePath(""));
		assertEquals("./", FileUtils.getRelativePath(null));
		assertEquals("./", FileUtils.getRelativePath(""));
		assertEquals("./", FileUtils.getRelativePath("/"));
		assertEquals("./test/", FileUtils.getRelativePath("test"));
		assertEquals("./test/", FileUtils.getRelativePath("/test"));
		assertEquals("./test/", FileUtils.getRelativePath("/test/"));
		assertEquals("./test/", FileUtils.getRelativePath("./test/"));
		assertEquals("./test/", FileUtils.getRelativePath("test/test.json"));
		assertEquals("./test/", FileUtils.getRelativePath("/test/test.json"));
		assertEquals("./test/", FileUtils.getRelativePath("./test/test.json"));
	}
	
	@Test
	void create() throws Exception {
		Path file = Path.of("test.json");
		Path directory = Path.of("test");
		Path both = Path.of("test/test.json");
		
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
		Path file = Path.of("test.xml");
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
		assertDoesNotThrow(() -> FileUtils.createSessionDirectory("test"));
	}
	
	@Test
	void deleteRecursively() throws IOException {
		Path file = Path.of("temp/temp/test.json");
		assertDoesNotThrow(() -> FileUtils.create(file));
		assertTrue(Files.exists(file));
		assertDoesNotThrow(() -> FileUtils.deleteRecursively(Path.of("temp")));
		assertFalse(Files.exists(Path.of("temp")));
		assertThrows(NullPointerException.class, () -> FileUtils.deleteRecursively(null));
		assertThrows(IOException.class, () -> FileUtils.deleteRecursively(file));
	}
}