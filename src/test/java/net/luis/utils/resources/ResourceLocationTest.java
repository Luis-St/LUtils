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
		File file = new File("ResourceLocation.json");
		Files.createFile(file.toPath());
		File folder = new File("ResourceLocation/ResourceLocation.json");
		Files.createDirectory(new File("ResourceLocation").toPath());
		Files.createFile(folder.toPath());
		Files.write(file.toPath(), ("{" + System.lineSeparator() + "\t\"path\": \"disk:ResourceLocation.json\"" + System.lineSeparator() + "}").getBytes());
		Files.write(folder.toPath(), ("{" + System.lineSeparator() + "\t\"path\": \"disk:ResourceLocation/ResourceLocation.json\"" + System.lineSeparator() + "}").getBytes());
	}
	//endregion
	
	//region Cleanup
	@AfterAll
	static void cleanUpAfter() throws Exception {
		Files.deleteIfExists(Path.of("ResourceLocation.json"));
		Files.deleteIfExists(Path.of("ResourceLocation/ResourceLocation.json"));
		Files.deleteIfExists(Path.of("ResourceLocation/InternalResourceLocation.json"));
		Files.deleteIfExists(Path.of("ResourceLocation/ExternalResourceLocation.json"));
		Files.deleteIfExists(Path.of("ResourceLocation"));
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
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.internal("ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.internal("", "ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getType());
		assertEquals(ResourceLocation.Type.INTERNAL, ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getType());
		
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.external("ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.external("", "ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.external("ResourceLocation", "ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.getResource("", "ResourceLocation.json").getType());
		assertEquals(ResourceLocation.Type.EXTERNAL, ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").getType());
	}
	
	@Test
	void getFile() {
		assertEquals("ResourceLocation.json", ResourceLocation.internal("ResourceLocation.json").getFile());
		assertEquals("ResourceLocation.json", ResourceLocation.internal("", "ResourceLocation.json").getFile());
		assertEquals("ResourceLocation.json", ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").getFile());
		assertEquals("ResourceLocation.json", ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getFile());
		assertEquals("ResourceLocation.json", ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getFile());
		
		assertEquals("ResourceLocation.json", ResourceLocation.external("ResourceLocation.json").getFile());
		assertEquals("ResourceLocation.json", ResourceLocation.external("", "ResourceLocation.json").getFile());
		assertEquals("ResourceLocation.json", ResourceLocation.external("ResourceLocation", "ResourceLocation.json").getFile());
		assertEquals("ResourceLocation.json", ResourceLocation.getResource("", "ResourceLocation.json").getFile());
		assertEquals("ResourceLocation.json", ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").getFile());
	}
	
	@Test
	void getPath() {
		assertEquals("/", ResourceLocation.internal("ResourceLocation.json").getPath());
		assertEquals("/", ResourceLocation.internal("", "ResourceLocation.json").getPath());
		assertEquals("/ResourceLocation/", ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").getPath());
		assertEquals("/", ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getPath());
		assertEquals("/ResourceLocation/", ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getPath());
		
		assertEquals("./", ResourceLocation.external("ResourceLocation.json").getPath());
		assertEquals("./", ResourceLocation.external("", "ResourceLocation.json").getPath());
		assertEquals("./ResourceLocation/", ResourceLocation.external("ResourceLocation", "ResourceLocation.json").getPath());
		assertEquals("./", ResourceLocation.getResource("", "ResourceLocation.json").getPath());
		assertEquals("./ResourceLocation/", ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").getPath());
	}
	
	@Test
	void asFile() {
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("ResourceLocation.json").asFile());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("", "ResourceLocation.json").asFile());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").asFile());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).asFile());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).asFile());
		
		assertDoesNotThrow(() -> ResourceLocation.external("ResourceLocation.json").asFile());
		assertDoesNotThrow(() -> ResourceLocation.external("", "ResourceLocation.json").asFile());
		assertDoesNotThrow(() -> ResourceLocation.external("ResourceLocation", "ResourceLocation.json").asFile());
		assertDoesNotThrow(() -> ResourceLocation.getResource("", "ResourceLocation.json").asFile());
		assertDoesNotThrow(() -> ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").asFile());
	}
	
	@Test
	void asPath() {
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("ResourceLocation.json").asPath());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("", "ResourceLocation.json").asPath());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").asPath());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).asPath());
		assertThrows(UnsupportedOperationException.class, () -> ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).asPath());
		
		assertDoesNotThrow(() -> ResourceLocation.external("ResourceLocation.json").asPath());
		assertDoesNotThrow(() -> ResourceLocation.external("", "ResourceLocation.json").asPath());
		assertDoesNotThrow(() -> ResourceLocation.external("ResourceLocation", "ResourceLocation.json").asPath());
		assertDoesNotThrow(() -> ResourceLocation.getResource("", "ResourceLocation.json").asPath());
		assertDoesNotThrow(() -> ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").asPath());
	}
	
	@Test
	void exists() {
		assertTrue(ResourceLocation.internal("ResourceLocation.json").exists());
		assertTrue(ResourceLocation.internal("", "ResourceLocation.json").exists());
		assertTrue(ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").exists());
		assertTrue(ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).exists());
		assertTrue(ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).exists());
		
		assertFalse(ResourceLocation.internal("test.xml").exists());
		assertFalse(ResourceLocation.internal("", "test.xml").exists());
		assertFalse(ResourceLocation.internal("ResourceLocation", "test.xml").exists());
		
		assertTrue(ResourceLocation.external("ResourceLocation.json").exists());
		assertTrue(ResourceLocation.external("", "ResourceLocation.json").exists());
		assertTrue(ResourceLocation.external("ResourceLocation", "ResourceLocation.json").exists());
		assertTrue(ResourceLocation.getResource("", "ResourceLocation.json").exists());
		assertTrue(ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").exists());
		
		assertFalse(ResourceLocation.external("test.xml").exists());
		assertFalse(ResourceLocation.external("", "test.xml").exists());
		assertFalse(ResourceLocation.external("ResourceLocation", "test.xml").exists());
	}
	
	@Test
	void getStream() {
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.internal("ResourceLocation.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.internal("", "ResourceLocation.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.external("ResourceLocation.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.external("", "ResourceLocation.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.external("ResourceLocation", "ResourceLocation.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("", "ResourceLocation.json").getStream()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").getStream()));
	}
	
	@Test
	void getBytes() {
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.internal("ResourceLocation.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.internal("", "ResourceLocation.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.external("ResourceLocation.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.external("", "ResourceLocation.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.external("ResourceLocation", "ResourceLocation.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("", "ResourceLocation.json").getBytes()));
		assertDoesNotThrow(() -> assertNotNull(ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").getBytes()));
	}
	
	@Test
	void getString() {
		UnaryOperator<String> internal = path -> "{" + System.lineSeparator() + "\t\"path\": \"classpath:" + path + "ResourceLocation.json\"" + System.lineSeparator() + "}";
		UnaryOperator<String> external = path -> "{" + System.lineSeparator() + "\t\"path\": \"disk:" + path + "ResourceLocation.json\"" + System.lineSeparator() + "}";
		
		assertDoesNotThrow(() -> assertEquals(internal.apply(""), ResourceLocation.internal("ResourceLocation.json").getString()));
		assertDoesNotThrow(() -> assertEquals(internal.apply(""), ResourceLocation.internal("", "ResourceLocation.json").getString()));
		assertDoesNotThrow(() -> assertEquals(internal.apply("ResourceLocation/"), ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").getString()));
		assertDoesNotThrow(() -> assertEquals(internal.apply(""), ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getString()));
		assertDoesNotThrow(() -> assertEquals(internal.apply("ResourceLocation/"), ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getString()));
		
		assertDoesNotThrow(() -> assertEquals(external.apply(""), ResourceLocation.external("ResourceLocation.json").getString()));
		assertDoesNotThrow(() -> assertEquals(external.apply(""), ResourceLocation.external("", "ResourceLocation.json").getString()));
		assertDoesNotThrow(() -> assertEquals(external.apply("ResourceLocation/"), ResourceLocation.external("ResourceLocation", "ResourceLocation.json").getString()));
		assertDoesNotThrow(() -> assertEquals(external.apply(""), ResourceLocation.getResource("", "ResourceLocation.json").getString()));
		assertDoesNotThrow(() -> assertEquals(external.apply("ResourceLocation/"), ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").getString()));
	}
	
	@Test
	void getLines() {
		Function<String, List<String>> internal = path -> List.of("{", "\t\"path\": \"classpath:" + path + "ResourceLocation.json\"", "}");
		Function<String, List<String>> external = path -> List.of("{", "\t\"path\": \"disk:" + path + "ResourceLocation.json\"", "}");
		
		assertDoesNotThrow(() -> assertEquals(internal.apply(""), ResourceLocation.internal("ResourceLocation.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(internal.apply(""), ResourceLocation.internal("", "ResourceLocation.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(internal.apply("ResourceLocation/"), ResourceLocation.internal("ResourceLocation", "ResourceLocation.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(internal.apply(""), ResourceLocation.getResource("", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(internal.apply("ResourceLocation/"), ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json", ResourceLocation.Type.INTERNAL).getLines().toList()));
		
		assertDoesNotThrow(() -> assertEquals(external.apply(""), ResourceLocation.external("ResourceLocation.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(external.apply(""), ResourceLocation.external("", "ResourceLocation.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(external.apply("ResourceLocation/"), ResourceLocation.external("ResourceLocation", "ResourceLocation.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(external.apply(""), ResourceLocation.getResource("", "ResourceLocation.json").getLines().toList()));
		assertDoesNotThrow(() -> assertEquals(external.apply("ResourceLocation/"), ResourceLocation.getResource("ResourceLocation", "ResourceLocation.json").getLines().toList()));
	}
	
	@Test
	void copy() throws Exception {
		ResourceLocation internal = ResourceLocation.internal("ResourceLocation.json");
		ResourceLocation external = ResourceLocation.external("ResourceLocation.json");
		
		Path internalTempCopy = internal.copy();
		assertTrue(Files.exists(internalTempCopy));
		assertDoesNotThrow(() -> internal.copy());
		
		Path externalTempCopy = external.copy();
		assertTrue(Files.exists(externalTempCopy));
		assertDoesNotThrow(() -> external.copy());
		
		Path internalTargetCopy = internal.copy(Path.of("./ResourceLocation/InternalResourceLocation.json"));
		assertTrue(Files.exists(internalTargetCopy));
		assertDoesNotThrow(() -> internal.copy(Path.of("./ResourceLocation/InternalResourceLocation.json")));
		
		Path externalTargetCopy = external.copy(Path.of("./ResourceLocation/ExternalResourceLocation.json"));
		assertTrue(Files.exists(externalTargetCopy));
		assertDoesNotThrow(() -> external.copy(Path.of("./ResourceLocation/ExternalResourceLocation.json")));
	}
}
