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

package net.luis.utils.io.data;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OutputProvider}.<br>
 *
 * @author Luis-St
 */
class OutputProviderTest {
	
	//region Setup and Cleanup
	@BeforeAll
	static void setUp() throws Exception {
		Files.createDirectories(Path.of("OutputProvider"));
		Files.createFile(Path.of("OutputProvider/OutputProvider.json"));
	}
	
	@AfterAll
	static void tearDown() throws Exception {
		Files.deleteIfExists(Path.of("OutputProvider/OutputProvider.json"));
		Files.deleteIfExists(Path.of("OutputProvider"));
	}
	//endregion
	
	@Test
	void constructorWithStringPath() {
		assertThrows(NullPointerException.class, () -> new OutputProvider((String) null));
		assertThrows(UncheckedIOException.class, () -> new OutputProvider("OutputProvider/"));
		
		try (OutputProvider provider = new OutputProvider("OutputProvider/OutputProvider.json")) {
			assertNotNull(provider.getStream());
		} catch (IOException e) {
			fail("Should not throw IOException for valid path");
		}
		
		try (OutputProvider provider = new OutputProvider("OutputProvider/new_file.json")) {
			assertNotNull(provider.getStream());
		} catch (IOException e) {
			fail("Should not throw IOException for valid path");
		} finally {
			try {
				Files.deleteIfExists(Path.of("OutputProvider/new_file.json"));
			} catch (IOException e) {
				fail("Should not throw IOException when deleting test file");
			}
		}
	}
	
	@Test
	void constructorWithPathAndFileName() {
		assertThrows(NullPointerException.class, () -> new OutputProvider(null, "file.json"));
		assertThrows(NullPointerException.class, () -> new OutputProvider("path", null));
		assertThrows(NullPointerException.class, () -> new OutputProvider(null, null));
		
		assertThrows(UncheckedIOException.class, () -> new OutputProvider("OutputProvider", ""));
		assertThrows(UncheckedIOException.class, () -> new OutputProvider("nonexistent", "file.json"));
		
		try (OutputProvider provider = new OutputProvider("OutputProvider", "OutputProvider.json")) {
			assertNotNull(provider.getStream());
		} catch (IOException e) {
			fail("Should not throw IOException for valid path and file name");
		}
	}
	
	@Test
	void constructorWithNioPath() {
		assertThrows(NullPointerException.class, () -> new OutputProvider((Path) null));
		assertThrows(UncheckedIOException.class, () -> new OutputProvider(Path.of("OutputProvider/")));
		
		try (OutputProvider provider = new OutputProvider(Path.of("OutputProvider/OutputProvider.json"))) {
			assertNotNull(provider.getStream());
		} catch (IOException e) {
			fail("Should not throw IOException for valid path");
		}
	}
	
	@Test
	void constructorWithFile() {
		assertThrows(NullPointerException.class, () -> new OutputProvider((File) null));
		assertThrows(UncheckedIOException.class, () -> new OutputProvider(new File("OutputProvider/")));
		
		try (OutputProvider provider = new OutputProvider(new File("OutputProvider/OutputProvider.json"))) {
			assertNotNull(provider.getStream());
		} catch (IOException e) {
			fail("Should not throw IOException for valid file");
		}
	}
	
	@Test
	void constructorWithOutputStream() {
		assertThrows(NullPointerException.class, () -> new OutputProvider((OutputStream) null));
		
		try (OutputProvider provider = new OutputProvider(OutputStream.nullOutputStream())) {
			assertNotNull(provider.getStream());
		} catch (IOException e) {
			fail("Should not throw IOException for null output stream");
		}
		
		try (ByteArrayOutputStream os = new ByteArrayOutputStream(); OutputProvider provider = new OutputProvider(os)) {
			assertNotNull(provider.getStream());
		} catch (IOException e) {
			fail("Should not throw IOException for ByteArrayOutputStream");
		}
		
		try (FileOutputStream fos = new FileOutputStream("OutputProvider/OutputProvider.json");
			 OutputProvider provider3 = new OutputProvider(fos)) {
			assertNotNull(provider3.getStream());
		} catch (IOException e) {
			fail("Should not throw IOException for valid file");
		}
	}
	
	@Test
	void getStreamReturnsCorrectStream() {
		try (OutputProvider provider = new OutputProvider(OutputStream.nullOutputStream())) {
			OutputStream stream = provider.getStream();
			assertNotNull(stream);
			assertSame(stream, provider.getStream());
		} catch (IOException e) {
			fail("Should not throw IOException for null output stream");
		}
	}
	
	@Test
	void closeClosesUnderlyingStream() {
		ByteArrayOutputStream mockStream = new ByteArrayOutputStream();
		OutputProvider provider = new OutputProvider(mockStream);
		
		try {
			provider.close();
		} catch (IOException e) {
			fail("Should not throw IOException when closing provider");
		}
	}
	
	@Test
	void multipleCloseCallsAreSafe() {
		OutputProvider provider = new OutputProvider(OutputStream.nullOutputStream());
		
		assertDoesNotThrow(provider::close);
		assertDoesNotThrow(provider::close);
		assertDoesNotThrow(provider::close);
	}
	
	@Test
	void streamCanWriteData() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (OutputProvider provider = new OutputProvider(os)) {
			OutputStream stream = provider.getStream();
			stream.write("test data".getBytes());
			stream.flush();
		}
		
		assertEquals("test data", os.toString());
	}
}
