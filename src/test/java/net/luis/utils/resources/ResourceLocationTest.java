/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ResourceLocation}.<br>
 *
 * @author Luis-St
 */
class ResourceLocationTest {
	
	//region Setup
	@BeforeAll
	static void setUpBefore() throws Exception {
		File file = new File("test.json");
		Files.createFile(file.toPath());
		File folder = new File("test/test.json");
		Files.createDirectory(new File("test").toPath());
		Files.createFile(folder.toPath());
		Files.write(file.toPath(), ("{" + System.lineSeparator() + "\t\"path\": \"disk:test.json\"" + System.lineSeparator() + "}").getBytes());
		Files.write(folder.toPath(), ("{" + System.lineSeparator() + "\t\"path\": \"disk:test/test.json\"" + System.lineSeparator() + "}").getBytes());
	}
	//endregion
	
	//region Cleanup
	@AfterAll
	static void cleanUpAfter() throws Exception {
		Files.deleteIfExists(Path.of("test.json"));
		Files.deleteIfExists(Path.of("test/test.json"));
		Files.deleteIfExists(Path.of("test/internal.json"));
		Files.deleteIfExists(Path.of("test/external.json"));
		Files.deleteIfExists(Path.of("test"));
	}
	//endregion
	
	@Test
	void internal() {
		assertThrows(NullPointerException.class, () -> ResourceLocation.internal(null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.internal(null, null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.internal("", null));
		assertDoesNotThrow(() -> ResourceLocation.internal(""));
		assertDoesNotThrow(() -> ResourceLocation.internal(null, ""));
		assertDoesNotThrow(() -> ResourceLocation.internal("", ""));
	}
	
	@Test
	void external() {
		assertThrows(NullPointerException.class, () -> ResourceLocation.external(null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.external(null, null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.external("", null));
		assertDoesNotThrow(() -> ResourceLocation.external(""));
		assertDoesNotThrow(() -> ResourceLocation.external(null, ""));
		assertDoesNotThrow(() -> ResourceLocation.external("", ""));
	}
	
	@Test
	void getResource() {
		assertThrows(NullPointerException.class, () -> ResourceLocation.getResource(null, null, null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.getResource("", null, null));
		
		assertDoesNotThrow(() -> ResourceLocation.getResource("", ""));
		assertDoesNotThrow(() -> ResourceLocation.getResource(null, "", null));
		assertDoesNotThrow(() -> ResourceLocation.getResource("", "", null));
		assertDoesNotThrow(() -> ResourceLocation.getResource("", "", ResourceLocation.Type.EXTERNAL));
		assertDoesNotThrow(() -> ResourceLocation.getResource("", "", ResourceLocation.Type.INTERNAL));
	}
	
	@Test
	void getType() {
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.internal("test.json").getType());
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.internal("", "test.json").getType());
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.internal("test", "test.json").getType());
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.getResource("", "test.json", ResourceLocation.Type.INTERNAL).getType());
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.getResource("test", "test.json", ResourceLocation.Type.INTERNAL).getType());
		
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.external("test.json").getType());
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.external("", "test.json").getType());
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.external("test", "test.json").getType());
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.getResource("", "test.json").getType());
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.getResource("test", "test.json").getType());
	}
	
	@Test
	void getFile() {
		assertEquals("test.json", ResourceLocation.internal("test.json").getFile());
		assertEquals("test.json", ResourceLocation.internal("", "test.json").getFile());
		assertEquals("test.json", ResourceLocation.internal("test", "test.json").getFile());
		assertEquals("test.json", ResourceLocation.getResource("", "test.json", ResourceLocation.Type.INTERNAL).getFile());
		assertEquals("test.json", ResourceLocation.getResource("test", "test.json", ResourceLocation.Type.INTERNAL).getFile());
		
		assertEquals("test.json", ResourceLocation.external("test.json").getFile());
		assertEquals("test.json", ResourceLocation.external("", "test.json").getFile());
		assertEquals("test.json", ResourceLocation.external("test", "test.json").getFile());
		assertEquals("test.json", ResourceLocation.getResource("", "test.json").getFile());
		assertEquals("test.json", ResourceLocation.getResource("test", "test.json").getFile());
	}
	
	@Test
	void getPath() {
		assertEquals("/", ResourceLocation.internal("test.json").getPath());
		assertEquals("/", ResourceLocation.internal("", "test.json").getPath());
		assertEquals("/test/", ResourceLocation.internal("test", "test.json").getPath());
		assertEquals("/", ResourceLocation.getResource("", "test.json", ResourceLocation.Type.INTERNAL).getPath());
		assertEquals("/test/", ResourceLocation.getResource("test", "test.json", ResourceLocation.Type.INTERNAL).getPath());
		
		assertEquals("./", ResourceLocation.external("test.json").getPath());
		assertEquals("./", ResourceLocation.external("", "test.json").getPath());
		assertEquals("./test/", ResourceLocation.external("test", "test.json").getPath());
		assertEquals("./", ResourceLocation.getResource("", "test.json").getPath());
		assertEquals("./test/", ResourceLocation.getResource("test", "test.json").getPath());
	}
	
	@Test
	void asFile() {
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("test.json").asFile());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("", "test.json").asFile());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("test", "test.json").asFile());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.getResource("", "test.json", ResourceLocation.Type.INTERNAL).asFile());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.getResource("test", "test.json", ResourceLocation.Type.INTERNAL).asFile());
		
		assertDoesNotThrow(() -> ResourceLocation.external("test.json").asFile());
		assertDoesNotThrow(() -> ResourceLocation.external("", "test.json").asFile());
		assertDoesNotThrow(() -> ResourceLocation.external("test", "test.json").asFile());
		assertDoesNotThrow(() -> ResourceLocation.getResource("", "test.json").asFile());
		assertDoesNotThrow(() -> ResourceLocation.getResource("test", "test.json").asFile());
	}
	
	@Test
	void asPath() {
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("test.json").asPath());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("", "test.json").asPath());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("test", "test.json").asPath());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.getResource("", "test.json", ResourceLocation.Type.INTERNAL).asPath());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.getResource("test", "test.json", ResourceLocation.Type.INTERNAL).asPath());
		
		assertDoesNotThrow(() -> ResourceLocation.external("test.json").asPath());
		assertDoesNotThrow(() -> ResourceLocation.external("", "test.json").asPath());
		assertDoesNotThrow(() -> ResourceLocation.external("test", "test.json").asPath());
		assertDoesNotThrow(() -> ResourceLocation.getResource("", "test.json").asPath());
		assertDoesNotThrow(() -> ResourceLocation.getResource("test", "test.json").asPath());
	}
	
	@Test
	void exists() {
		assertTrue(ResourceLocation.internal("test.json").exists());
		assertTrue(ResourceLocation.internal("", "test.json").exists());
		assertTrue(ResourceLocation.internal("test", "test.json").exists());
		assertTrue(ResourceLocation.getResource("", "test.json", ResourceLocation.Type.INTERNAL).exists());
		assertTrue(ResourceLocation.getResource("test", "test.json", ResourceLocation.Type.INTERNAL).exists());
		
		assertFalse(ResourceLocation.internal("test.xml").exists());
		assertFalse(ResourceLocation.internal("", "test.xml").exists());
		assertFalse(ResourceLocation.internal("test", "test.xml").exists());
		
		assertTrue(ResourceLocation.external("test.json").exists());
		assertTrue(ResourceLocation.external("", "test.json").exists());
		assertTrue(ResourceLocation.external("test", "test.json").exists());
		assertTrue(ResourceLocation.getResource("", "test.json").exists());
		assertTrue(ResourceLocation.getResource("test", "test.json").exists());
		
		assertFalse(ResourceLocation.external("test.xml").exists());
		assertFalse(ResourceLocation.external("", "test.xml").exists());
		assertFalse(ResourceLocation.external("test", "test.xml").exists());
	}
	
	@Test
	void getStream() {
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.internal("test.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.internal("", "test.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.internal("test", "test.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("", "test.json", ResourceLocation.Type.INTERNAL).getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("test", "test.json", ResourceLocation.Type.INTERNAL).getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.external("test.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.external("", "test.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.external("test", "test.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("", "test.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("test", "test.json").getStream()));
	}
	
	@Test
	void getBytes() {
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.internal("test.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.internal("", "test.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.internal("test", "test.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("", "test.json", ResourceLocation.Type.INTERNAL).getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("test", "test.json", ResourceLocation.Type.INTERNAL).getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.external("test.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.external("", "test.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.external("test", "test.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("", "test.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("test", "test.json").getBytes()));
	}
	
	@Test
	void getString() {
		UnaryOperator<String> internal = path -> "{" + System.lineSeparator() + "\t\"path\": \"classpath:" + path + "test.json\"" + System.lineSeparator() + "}";
		UnaryOperator<String> external = path -> "{" + System.lineSeparator() + "\t\"path\": \"disk:" + path + "test.json\"" + System.lineSeparator() + "}";
		
		assertDoesNotThrow(() -> assertEquals(internal.apply(""), ResourceLocation.internal("test.json").getString()));
		assertDoesNotThrow(() -> assertEquals(internal.apply(""), ResourceLocation.internal("", "test.json").getString()));
		assertDoesNotThrow(() -> assertEquals(internal.apply("test/"), ResourceLocation.internal("test", "test.json").getString()));
		assertDoesNotThrow(() -> assertEquals(internal.apply(""), ResourceLocation.getResource("", "test.json", ResourceLocation.Type.INTERNAL).getString()));
		assertDoesNotThrow(() -> assertEquals(internal.apply("test/"), ResourceLocation.getResource("test", "test.json", ResourceLocation.Type.INTERNAL).getString()));
		
		assertDoesNotThrow(() -> assertEquals(external.apply(""), ResourceLocation.external("test.json").getString()));
		assertDoesNotThrow(() -> assertEquals(external.apply(""), ResourceLocation.external("", "test.json").getString()));
		assertDoesNotThrow(() -> assertEquals(external.apply("test/"), ResourceLocation.external("test", "test.json").getString()));
		assertDoesNotThrow(() -> assertEquals(external.apply(""), ResourceLocation.getResource("", "test.json").getString()));
		assertDoesNotThrow(() -> assertEquals(external.apply("test/"), ResourceLocation.getResource("test", "test.json").getString()));
	}
	
	@Test
	void getLines() {
		Function<String, List<String>> internal = path -> List.of("{", "\t\"path\": \"classpath:" + path + "test.json\"", "}");
		Function<String, List<String>> external = path -> List.of("{", "\t\"path\": \"disk:" + path + "test.json\"", "}");
		
		assertDoesNotThrow(() -> assertEquals(internal.apply(""), ResourceLocation.internal("test.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(internal.apply(""), ResourceLocation.internal("", "test.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(internal.apply("test/"), ResourceLocation.internal("test", "test.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(internal.apply(""), ResourceLocation.getResource("", "test.json", ResourceLocation.Type.INTERNAL).getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(internal.apply("test/"), ResourceLocation.getResource("test", "test.json", ResourceLocation.Type.INTERNAL).getLines().toList()));
		
		assertDoesNotThrow(() -> assertEquals(external.apply(""), ResourceLocation.external("test.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(external.apply(""), ResourceLocation.external("", "test.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(external.apply("test/"), ResourceLocation.external("test", "test.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(external.apply(""), ResourceLocation.getResource("", "test.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(external.apply("test/"), ResourceLocation.getResource("test", "test.json").getLines().toList()));
	}
	
	@Test
	void copy() throws Exception {
		ResourceLocation internal = ResourceLocation.internal("test.json");
		ResourceLocation external = ResourceLocation.external("test.json");
		
		Path internalTempCopy = internal.copy();
		assertTrue(Files.exists(internalTempCopy));
		assertDoesNotThrow(() -> internal.copy());
		
		Path externalTempCopy = external.copy();
		assertTrue(Files.exists(externalTempCopy));
		assertDoesNotThrow(() -> external.copy());
		
		Path internalTargetCopy = internal.copy(Path.of("./test/internal.json"));
		assertTrue(Files.exists(internalTargetCopy));
		assertDoesNotThrow(() -> internal.copy(Path.of("./test/internal.json")));
		
		Path externalTargetCopy = external.copy(Path.of("./test/external.json"));
		assertTrue(Files.exists(externalTargetCopy));
		assertDoesNotThrow(() -> external.copy(Path.of("./test/external.json")));
	}
}