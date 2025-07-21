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

package net.luis.utils.resources;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ResourceLocation}.<br>
 *
 * @author Luis-St
 */
class ResourceLocationTest {
	
	@BeforeAll
	static void setUp() throws Exception {
		File file = new File("ResourceLocation.json");
		Files.createFile(file.toPath());
		File folder = new File("ResourceLocation/ResourceLocation.json");
		Files.createDirectory(new File("ResourceLocation").toPath());
		Files.createFile(folder.toPath());
		Files.write(file.toPath(), ("{" + System.lineSeparator() + "\t\"path\": \"disk:ResourceLocation.json\"" + System.lineSeparator() + "}").getBytes());
		Files.write(folder.toPath(), ("{" + System.lineSeparator() + "\t\"path\": \"disk:ResourceLocation/ResourceLocation.json\"" + System.lineSeparator() + "}").getBytes());
	}
	
	@AfterAll
	static void cleanUp() throws Exception {
		Files.deleteIfExists(Path.of("ResourceLocation.json"));
		Files.deleteIfExists(Path.of("ResourceLocation/ResourceLocation.json"));
		Files.deleteIfExists(Path.of("ResourceLocation/InternalResourceLocation.json"));
		Files.deleteIfExists(Path.of("ResourceLocation/ExternalResourceLocation.json"));
		Files.deleteIfExists(Path.of("ResourceLocation"));
	}
	
	@Test
	void internalFactoryMethodFailsWithNullFile() {
		assertThrows(NullPointerException.class, () -> ResourceLocation.internal(null));
	}
	
	@Test
	void internalFactoryMethodFailsWithNullName() {
		assertThrows(NullPointerException.class, () -> ResourceLocation.internal(null, null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.internal("", null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.internal("path", null));
	}
	
	@Test
	void internalFactoryMethodWorksWithValidInputs() {
		assertDoesNotThrow(() -> ResourceLocation.internal(""));
		assertDoesNotThrow(() -> ResourceLocation.internal("test.txt"));
		assertDoesNotThrow(() -> ResourceLocation.internal(null, ""));
		assertDoesNotThrow(() -> ResourceLocation.internal("", ""));
		assertDoesNotThrow(() -> ResourceLocation.internal("path", "file.txt"));
	}
	
	@Test
	void externalFactoryMethodFailsWithNullFile() {
		assertThrows(NullPointerException.class, () -> ResourceLocation.external(null));
	}
	
	@Test
	void externalFactoryMethodFailsWithNullName() {
		assertThrows(NullPointerException.class, () -> ResourceLocation.external(null, null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.external("", null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.external("path", null));
	}
	
	@Test
	void externalFactoryMethodWorksWithValidInputs() {
		assertDoesNotThrow(() -> ResourceLocation.external(""));
		assertDoesNotThrow(() -> ResourceLocation.external("test.txt"));
		assertDoesNotThrow(() -> ResourceLocation.external(null, ""));
		assertDoesNotThrow(() -> ResourceLocation.external("", ""));
		assertDoesNotThrow(() -> ResourceLocation.external("path", "file.txt"));
	}
	
	@Test
	void getResourceFailsWithNullName() {
		assertThrows(NullPointerException.class, () -> ResourceLocation.getResource(null, null, null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.getResource("", null, null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.getResource("path", null, ResourceLocation.Type.EXTERNAL));
	}
	
	@Test
	void getResourceWorksWithValidInputs() {
		assertDoesNotThrow(() -> ResourceLocation.getResource("", ""));
		assertDoesNotThrow(() -> ResourceLocation.getResource(null, "", null));
		assertDoesNotThrow(() -> ResourceLocation.getResource("", "", null));
		assertDoesNotThrow(() -> ResourceLocation.getResource("", "", ResourceLocation.Type.EXTERNAL));
		assertDoesNotThrow(() -> ResourceLocation.getResource("", "", ResourceLocation.Type.INTERNAL));
	}
	
	@Test
	void getResourceFailsForNonExistentResource() {
		assertThrows(IllegalArgumentException.class, () -> ResourceLocation.getResource("", "nonexistent.txt"));
		assertThrows(IllegalArgumentException.class, () -> ResourceLocation.getResource("nonexistent", "file.txt"));
		assertThrows(IllegalArgumentException.class, () -> ResourceLocation.getResource(null, "missing.json"));
	}
	
	@Test
	void getTypeReturnsCorrectTypeForInternal() {
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.internal("ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.internal("", "ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getType());
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getType());
	}
	
	@Test
	void getTypeReturnsCorrectTypeForExternal() {
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.external("ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.external("", "ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.external("ResourceLocation", "ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.getResource("", "ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").getType());
	}
	
	@Test
	void getFileReturnsCorrectFileName() {
		assertEquals("ResourceLocation.json", ResourceLocation.internal("ResourceLocation.json").getFile());
		assertEquals("ResourceLocation.json", ResourceLocation.internal("", "ResourceLocation.json").getFile());
		assertEquals("ResourceLocation.json", ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").getFile());
		assertEquals("ResourceLocation.json", ResourceLocation.external("ResourceLocation.json").getFile());
		assertEquals("ResourceLocation.json", ResourceLocation.external("", "ResourceLocation.json").getFile());
		assertEquals("ResourceLocation.json", ResourceLocation.external("ResourceLocation", "ResourceLocation.json").getFile());
	}
	
	@Test
	void getFileHandlesDifferentFileNames() {
		assertEquals("test.txt", ResourceLocation.internal("test.txt").getFile());
		assertEquals("data.xml", ResourceLocation.external("data.xml").getFile());
		assertEquals("config.properties", ResourceLocation.internal("folder", "config.properties").getFile());
		assertEquals("", ResourceLocation.internal("", "").getFile());
	}
	
	@Test
	void getPathReturnsCorrectPathForInternal() {
		assertEquals("/", ResourceLocation.internal("ResourceLocation.json").getPath());
		assertEquals("/", ResourceLocation.internal("", "ResourceLocation.json").getPath());
		assertEquals("/ResourceLocation/", ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").getPath());
		assertEquals("/", ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getPath());
		assertEquals("/ResourceLocation/", ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getPath());
	}
	
	@Test
	void getPathReturnsCorrectPathForExternal() {
		assertEquals("./", ResourceLocation.external("ResourceLocation.json").getPath());
		assertEquals("./", ResourceLocation.external("", "ResourceLocation.json").getPath());
		assertEquals("./ResourceLocation/", ResourceLocation.external("ResourceLocation", "ResourceLocation.json").getPath());
		assertEquals("./", ResourceLocation.getResource("", "ResourceLocation.json").getPath());
		assertEquals("./ResourceLocation/", ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").getPath());
	}
	
	@Test
	void getPathHandlesDifferentPaths() {
		assertEquals("/folder/subfolder/", ResourceLocation.internal("folder/subfolder", "file.txt").getPath());
		assertEquals("./data/", ResourceLocation.external("data", "config.xml").getPath());
		assertEquals("/", ResourceLocation.internal("", "").getPath());
		assertEquals("./", ResourceLocation.external("", "").getPath());
	}
	
	@Test
	void asFileFailsForInternalResources() {
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("ResourceLocation.json").asFile());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("", "ResourceLocation.json").asFile());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").asFile());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).asFile());
	}
	
	@Test
	void asFileWorksForExternalResources() {
		assertDoesNotThrow(() -> ResourceLocation.external("ResourceLocation.json").asFile());
		assertDoesNotThrow(() -> ResourceLocation.external("", "ResourceLocation.json").asFile());
		assertDoesNotThrow(() -> ResourceLocation.external("ResourceLocation", "ResourceLocation.json").asFile());
		assertDoesNotThrow(() -> ResourceLocation.getResource("", "ResourceLocation.json").asFile());
		assertDoesNotThrow(() -> ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").asFile());
	}
	
	@Test
	void asFileReturnsCorrectFile() {
		File file1 = ResourceLocation.external("ResourceLocation.json").asFile();
		assertEquals("ResourceLocation.json", file1.getName());
		
		File file2 = ResourceLocation.external("ResourceLocation", "ResourceLocation.json").asFile();
		assertEquals("ResourceLocation.json", file2.getName());
		assertTrue(file2.getPath().contains("ResourceLocation"));
	}
	
	@Test
	void asPathFailsForInternalResources() {
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("ResourceLocation.json").asPath());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("", "ResourceLocation.json").asPath());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").asPath());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).asPath());
	}
	
	@Test
	void asPathWorksForExternalResources() {
		assertDoesNotThrow(() -> ResourceLocation.external("ResourceLocation.json").asPath());
		assertDoesNotThrow(() -> ResourceLocation.external("", "ResourceLocation.json").asPath());
		assertDoesNotThrow(() -> ResourceLocation.external("ResourceLocation", "ResourceLocation.json").asPath());
		assertDoesNotThrow(() -> ResourceLocation.getResource("", "ResourceLocation.json").asPath());
		assertDoesNotThrow(() -> ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").asPath());
	}
	
	@Test
	void asPathReturnsCorrectPath() {
		Path path1 = ResourceLocation.external("ResourceLocation.json").asPath();
		assertEquals("ResourceLocation.json", path1.getFileName().toString());
		
		Path path2 = ResourceLocation.external("ResourceLocation", "ResourceLocation.json").asPath();
		assertEquals("ResourceLocation.json", path2.getFileName().toString());
		assertTrue(path2.toString().contains("ResourceLocation"));
	}
	
	@Test
	void existsReturnsTrueForExistingResources() {
		assertTrue(ResourceLocation.internal("ResourceLocation.json").exists());
		assertTrue(ResourceLocation.internal("", "ResourceLocation.json").exists());
		assertTrue(ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").exists());
		assertTrue(ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).exists());
		assertTrue(ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).exists());
		
		assertTrue(ResourceLocation.external("ResourceLocation.json").exists());
		assertTrue(ResourceLocation.external("", "ResourceLocation.json").exists());
		assertTrue(ResourceLocation.external("ResourceLocation", "ResourceLocation.json").exists());
		assertTrue(ResourceLocation.getResource("", "ResourceLocation.json").exists());
		assertTrue(ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").exists());
	}
	
	@Test
	void existsReturnsFalseForNonExistingResources() {
		assertFalse(ResourceLocation.internal("test.xml").exists());
		assertFalse(ResourceLocation.internal("", "test.xml").exists());
		assertFalse(ResourceLocation.internal("ResourceLocation", "test.xml").exists());
		assertFalse(ResourceLocation.internal("NonExistent", "ResourceLocation.json").exists());
		
		assertFalse(ResourceLocation.external("test.xml").exists());
		assertFalse(ResourceLocation.external("", "test.xml").exists());
		assertFalse(ResourceLocation.external("ResourceLocation", "test.xml").exists());
		assertFalse(ResourceLocation.external("NonExistent", "ResourceLocation.json").exists());
	}
	
	@Test
	void getStreamWorksForExistingResources() {
		assertDoesNotThrow(() -> {
			try (var stream = ResourceLocation.internal("ResourceLocation.json").getStream()) {
				assertNotNull(stream);
			}
		});
		
		assertDoesNotThrow(() -> {
			try (var stream = ResourceLocation.external("ResourceLocation.json").getStream()) {
				assertNotNull(stream);
			}
		});
	}
	
	@Test
	void getStreamFailsForNonExistingResources() {
		assertThrows(Exception.class, () -> ResourceLocation.internal("nonexistent.txt").getStream());
		assertThrows(Exception.class, () -> ResourceLocation.external("nonexistent.txt").getStream());
	}
	
	@Test
	void getBytesWorksForExistingResources() {
		assertDoesNotThrow(() -> {
			byte[] bytes = ResourceLocation.internal("ResourceLocation.json").getBytes();
			assertNotNull(bytes);
			assertTrue(bytes.length > 0);
		});
		
		assertDoesNotThrow(() -> {
			byte[] bytes = ResourceLocation.external("ResourceLocation.json").getBytes();
			assertNotNull(bytes);
			assertTrue(bytes.length > 0);
		});
	}
	
	@Test
	void getBytesFailsForNonExistingResources() {
		assertThrows(Exception.class, () -> ResourceLocation.internal("nonexistent.txt").getBytes());
		assertThrows(Exception.class, () -> ResourceLocation.external("nonexistent.txt").getBytes());
	}
	
	@Test
	void getStringWorksForExistingResources() throws IOException {
		String internalContent = ResourceLocation.internal("ResourceLocation.json").getString();
		assertNotNull(internalContent);
		assertTrue(internalContent.contains("classpath:"));
		assertTrue(internalContent.contains("ResourceLocation.json"));
		
		String externalContent = ResourceLocation.external("ResourceLocation.json").getString();
		assertNotNull(externalContent);
		assertTrue(externalContent.contains("disk:"));
		assertTrue(externalContent.contains("ResourceLocation.json"));
	}
	
	@Test
	void getStringReturnsCorrectContentForDifferentPaths() throws IOException {
		String rootContent = ResourceLocation.external("ResourceLocation.json").getString();
		String folderContent = ResourceLocation.external("ResourceLocation", "ResourceLocation.json").getString();
		
		assertNotEquals(rootContent, folderContent);
		assertTrue(rootContent.contains("disk:ResourceLocation.json"));
		assertTrue(folderContent.contains("disk:ResourceLocation/ResourceLocation.json"));
	}
	
	@Test
	void getStringFailsForNonExistingResources() {
		assertThrows(Exception.class, () -> ResourceLocation.internal("nonexistent.txt").getString());
		assertThrows(Exception.class, () -> ResourceLocation.external("nonexistent.txt").getString());
	}
	
	@Test
	void getLinesWorksForExistingResources() throws IOException {
		List<String> internalLines = ResourceLocation.internal("ResourceLocation.json").getLines().toList();
		assertNotNull(internalLines);
		assertEquals(3, internalLines.size());
		assertEquals("{", internalLines.get(0));
		assertTrue(internalLines.get(1).contains("classpath:"));
		assertEquals("}", internalLines.get(2));
		
		List<String> externalLines = ResourceLocation.external("ResourceLocation.json").getLines().toList();
		assertNotNull(externalLines);
		assertEquals(3, externalLines.size());
		assertEquals("{", externalLines.get(0));
		assertTrue(externalLines.get(1).contains("disk:"));
		assertEquals("}", externalLines.get(2));
	}
	
	@Test
	void getLinesReturnsCorrectContentForDifferentPaths() throws IOException {
		List<String> rootLines = ResourceLocation.external("ResourceLocation.json").getLines().toList();
		List<String> folderLines = ResourceLocation.external("ResourceLocation", "ResourceLocation.json").getLines().toList();
		
		assertNotEquals(rootLines, folderLines);
		assertTrue(rootLines.get(1).contains("disk:ResourceLocation.json"));
		assertTrue(folderLines.get(1).contains("disk:ResourceLocation/ResourceLocation.json"));
	}
	
	@Test
	void getLinesFailsForNonExistingResources() {
		assertThrows(Exception.class, () -> ResourceLocation.internal("nonexistent.txt").getLines());
		assertThrows(Exception.class, () -> ResourceLocation.external("nonexistent.txt").getLines());
	}
	
	@Test
	void copyWorksForBothResourceTypes() throws IOException {
		ResourceLocation internal = ResourceLocation.internal("ResourceLocation.json");
		ResourceLocation external = ResourceLocation.external("ResourceLocation.json");
		
		Path internalCopy = internal.copy();
		assertTrue(Files.exists(internalCopy));
		assertTrue(Files.isRegularFile(internalCopy));
		
		Path externalCopy = external.copy();
		assertTrue(Files.exists(externalCopy));
		assertTrue(Files.isRegularFile(externalCopy));
	}
	
	@Test
	void copyCanBeCalledMultipleTimes() throws IOException {
		ResourceLocation resource = ResourceLocation.internal("ResourceLocation.json");
		
		Path copy1 = resource.copy();
		Path copy2 = resource.copy();
		
		assertTrue(Files.exists(copy1));
		assertTrue(Files.exists(copy2));
		assertEquals(copy1, copy2);
	}
	
	@Test
	void copyWithTargetWorksForBothResourceTypes() throws IOException {
		ResourceLocation internal = ResourceLocation.internal("ResourceLocation.json");
		ResourceLocation external = ResourceLocation.external("ResourceLocation.json");
		
		Path internalTarget = Path.of("./ResourceLocation/InternalResourceLocation.json");
		Path internalCopy = internal.copy(internalTarget);
		assertTrue(Files.exists(internalCopy));
		assertEquals(internalTarget, internalCopy);
		
		Path externalTarget = Path.of("./ResourceLocation/ExternalResourceLocation.json");
		Path externalCopy = external.copy(externalTarget);
		assertTrue(Files.exists(externalCopy));
		assertEquals(externalTarget, externalCopy);
	}
	
	@Test
	void copyWithTargetCanOverwriteExistingFiles() throws IOException {
		ResourceLocation resource = ResourceLocation.external("ResourceLocation.json");
		Path target = Path.of("./ResourceLocation/OverwriteTest.json");
		
		resource.copy(target);
		assertTrue(Files.exists(target));
		
		resource.copy(target);
		assertTrue(Files.exists(target));
		
		Files.deleteIfExists(target);
	}
	
	@Test
	void copyFailsForNonExistingResources() {
		ResourceLocation nonExistent = ResourceLocation.internal("nonexistent.txt");
		assertThrows(Exception.class, nonExistent::copy);
		assertThrows(Exception.class, () -> nonExistent.copy(Path.of("target.txt")));
	}
	
	@Test
	void equalsWorksCorrectly() {
		ResourceLocation res1 = ResourceLocation.internal("test.txt");
		ResourceLocation res2 = ResourceLocation.internal("test.txt");
		ResourceLocation res3 = ResourceLocation.internal("other.txt");
		ResourceLocation res4 = ResourceLocation.external("test.txt");
		
		assertEquals(res1, res2);
		assertNotEquals(res1, res3);
		assertNotEquals(res1, res4);
		assertNotEquals(res1, null);
		assertNotEquals(res1, "not a resource");
	}
	
	@Test
	void hashCodeIsConsistentForEqualObjects() {
		ResourceLocation res1 = ResourceLocation.internal("test.txt");
		ResourceLocation res2 = ResourceLocation.internal("test.txt");
		
		assertEquals(res1, res2);
		assertEquals(res1.hashCode(), res2.hashCode());
	}
	
	@Test
	void toStringContainsPathAndFile() {
		ResourceLocation internal = ResourceLocation.internal("folder", "file.txt");
		ResourceLocation external = ResourceLocation.external("folder", "file.txt");
		
		String internalStr = internal.toString();
		String externalStr = external.toString();
		
		assertTrue(internalStr.contains("folder"));
		assertTrue(internalStr.contains("file.txt"));
		assertTrue(externalStr.contains("folder"));
		assertTrue(externalStr.contains("file.txt"));
		
		assertNotEquals(internalStr, externalStr);
	}
	
	@Test
	void splitPathWorksCorrectly() {
		var pair1 = ResourceLocation.splitPath("folder/file.txt");
		assertEquals("folder/", pair1.getFirst());
		assertEquals("file.txt", pair1.getSecond());
		
		var pair2 = ResourceLocation.splitPath("file.txt");
		assertEquals("", pair2.getFirst());
		assertEquals("file.txt", pair2.getSecond());
		
		var pair3 = ResourceLocation.splitPath("path/to/folder/file.txt");
		assertEquals("path/to/folder/", pair3.getFirst());
		assertEquals("file.txt", pair3.getSecond());
	}
	
	@Test
	void splitPathHandlesWindowsPaths() {
		var pair = ResourceLocation.splitPath("folder\\file.txt");
		assertEquals("folder/", pair.getFirst());
		assertEquals("file.txt", pair.getSecond());
	}
	
	@Test
	void splitPathHandlesEdgeCases() {
		var pair1 = ResourceLocation.splitPath("");
		assertEquals("", pair1.getFirst());
		assertEquals("", pair1.getSecond());
		
		var pair2 = ResourceLocation.splitPath("   ");
		assertEquals("", pair2.getFirst());
		assertEquals("", pair2.getSecond());
		
		assertThrows(NullPointerException.class, () -> ResourceLocation.splitPath(null));
	}
}
