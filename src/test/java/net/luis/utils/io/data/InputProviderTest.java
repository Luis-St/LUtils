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

package net.luis.utils.io.data;

import net.luis.utils.resources.ResourceLocation;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InputProvider}.<br>
 *
 * @author Luis-St
 */
class InputProviderTest {
	
	//region Setup and Cleanup
	@BeforeAll
	static void setUp() throws Exception {
		Files.createDirectories(Path.of("InputProvider"));
		Files.createFile(Path.of("InputProvider/InputProvider.json"));
	}
	
	@AfterAll
	static void tearDown() throws Exception {
		Files.deleteIfExists(Path.of("InputProvider/InputProvider.json"));
		Files.deleteIfExists(Path.of("InputProvider"));
	}
	//endregion
	
	@Test
	void constructorWithStringPath() throws Exception {
		assertThrows(NullPointerException.class, () -> new InputProvider((String) null));
		
		assertThrows(UncheckedIOException.class, () -> new InputProvider("nonexistent.json"));
		assertThrows(UncheckedIOException.class, () -> new InputProvider("InputProvider/"));
		
		try (InputProvider provider = new InputProvider("InputProvider/InputProvider.json")) {
			assertNotNull(provider.getStream());
		}
	}
	
	@Test
	void constructorWithPathAndFileName() throws Exception {
		assertThrows(NullPointerException.class, () -> new InputProvider(null, "file.json"));
		assertThrows(NullPointerException.class, () -> new InputProvider("path", null));
		assertThrows(NullPointerException.class, () -> new InputProvider(null, null));
		
		assertThrows(UncheckedIOException.class, () -> new InputProvider("", "InputProvider.json"));
		assertThrows(UncheckedIOException.class, () -> new InputProvider("InputProvider", ""));
		assertThrows(UncheckedIOException.class, () -> new InputProvider("nonexistent", "file.json"));
		
		try (InputProvider provider = new InputProvider("InputProvider", "InputProvider.json")) {
			assertNotNull(provider.getStream());
		}
	}
	
	@Test
	void constructorWithNioPath() throws Exception {
		assertThrows(NullPointerException.class, () -> new InputProvider((Path) null));
		
		assertThrows(UncheckedIOException.class, () -> new InputProvider(Path.of("nonexistent.json")));
		assertThrows(UncheckedIOException.class, () -> new InputProvider(Path.of("InputProvider/")));
		
		try (InputProvider provider = new InputProvider(Path.of("InputProvider/InputProvider.json"))) {
			assertNotNull(provider.getStream());
		}
	}
	
	@Test
	void constructorWithFile() throws Exception {
		assertThrows(NullPointerException.class, () -> new InputProvider((File) null));
		
		assertThrows(UncheckedIOException.class, () -> new InputProvider(new File("nonexistent.json")));
		assertThrows(UncheckedIOException.class, () -> new InputProvider(new File("InputProvider/")));
		
		try (InputProvider provider = new InputProvider(new File("InputProvider/InputProvider.json"))) {
			assertNotNull(provider.getStream());
		}
	}
	
	@Test
	void constructorWithResourceLocation() throws Exception {
		assertThrows(NullPointerException.class, () -> new InputProvider((ResourceLocation) null));
		
		assertThrows(UncheckedIOException.class, () -> new InputProvider(ResourceLocation.external("nonexistent.json")));
		
		try (InputProvider provider = new InputProvider(ResourceLocation.external("InputProvider/InputProvider.json"))) {
			assertNotNull(provider.getStream());
		}
	}
	
	@Test
	void constructorWithInputStream() throws Exception {
		assertThrows(NullPointerException.class, () -> new InputProvider((InputStream) null));
		
		try (InputProvider provider1 = new InputProvider(InputStream.nullInputStream())) {
			assertNotNull(provider1.getStream());
		}
		
		try (FileInputStream fis = new FileInputStream("InputProvider/InputProvider.json");
			 InputProvider provider2 = new InputProvider(fis)) {
			assertNotNull(provider2.getStream());
		} catch (IOException e) {
			fail("Should not throw IOException for valid file");
		}
	}
	
	@Test
	void getStreamReturnsCorrectStream() throws Exception {
		try (InputProvider provider = new InputProvider(InputStream.nullInputStream())) {
			InputStream stream = provider.getStream();
			assertNotNull(stream);
			assertSame(stream, provider.getStream());
		}
	}
	
	@Test
	void closeClosesUnderlyingStream() throws IOException {
		ByteArrayInputStream mockStream = new ByteArrayInputStream(new byte[0]);
		InputProvider provider = new InputProvider(mockStream);
		
		provider.close();
	}
	
	@Test
	void multipleCloseCallsAreSafe() {
		InputProvider provider = new InputProvider(InputStream.nullInputStream());
		
		assertDoesNotThrow(provider::close);
		assertDoesNotThrow(provider::close);
		assertDoesNotThrow(provider::close);
	}
}
