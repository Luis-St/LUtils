package net.luis.utils.io;

import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {
	
	@Test
	void split() {
		assertDoesNotThrow(() -> FileUtils.split(null));
		assertDoesNotThrow(() -> FileUtils.split(""));
		assertEquals(Pair.of("", ""), FileUtils.split(""));
		assertEquals(Pair.of("/", ""), FileUtils.split("/"));
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
		assertEquals("./test/", FileUtils.getRelativePath("/test"));
		assertEquals("./test/", FileUtils.getRelativePath("/test/"));
		assertEquals("./test/", FileUtils.getRelativePath("./test/"));
		assertEquals("./test/", FileUtils.getRelativePath("test/test.json"));
		assertEquals("./test/", FileUtils.getRelativePath("/test/test.json"));
		assertEquals("./test/", FileUtils.getRelativePath("./test/test.json"));
	}
}