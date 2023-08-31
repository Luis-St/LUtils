package net.luis.utils.resources;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class ResourceLocationTest {
	
	@BeforeAll
	static void setUpBefore() throws Exception {
		File test0 = new File("./test.json");
		Files.createFile(test0.toPath());
		test0.deleteOnExit();
		File test1 = new File("./test/test.json");
		Files.createDirectory(new File("./test").toPath()).toFile().deleteOnExit();
		Files.createFile(test1.toPath());
		test1.deleteOnExit();
		Files.write(test0.toPath(), ("{" + System.lineSeparator() + "\t\"path\": \"disk:test.json\"" + System.lineSeparator() + "}").getBytes());
		Files.write(test1.toPath(), ("{" + System.lineSeparator() + "\t\"path\": \"disk:test/test.json\"" + System.lineSeparator() + "}").getBytes());
	}
	
	@Test
	void internal() {
		assertThrows(NullPointerException.class, () -> ResourceLocation.internal(null));
		assertDoesNotThrow(() -> ResourceLocation.internal(""));
		assertDoesNotThrow(() -> ResourceLocation.internal(null, null));
		assertDoesNotThrow(() -> ResourceLocation.internal(null, ""));
		assertDoesNotThrow(() -> ResourceLocation.internal("", null));
		assertDoesNotThrow(() -> ResourceLocation.internal("", ""));
	}
	
	@Test
	void external() {
		assertThrows(NullPointerException.class, () -> ResourceLocation.external(null));
		assertDoesNotThrow(() -> ResourceLocation.external(""));
		assertDoesNotThrow(() -> ResourceLocation.external(null, null));
		assertDoesNotThrow(() -> ResourceLocation.external(null, ""));
		assertDoesNotThrow(() -> ResourceLocation.external("", null));
		assertDoesNotThrow(() -> ResourceLocation.external("", ""));
	}
	
	@Test
	void getResource() {
		assertThrows(NullPointerException.class, () -> ResourceLocation.getResource(null, null, null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.getResource(null, "", null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.getResource("", null, null));
		assertThrows(NullPointerException.class, () -> ResourceLocation.getResource("", "", null));
		
		assertDoesNotThrow(() -> ResourceLocation.getResource("", ""));
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
	void getStream() throws Exception {
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
}