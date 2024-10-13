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

package net.luis.utils.io.data;

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
	
	//region Setup
	@BeforeAll
	static void setUpBefore() throws Exception {
		File folder = new File("InputProvider/InputProvider.json");
		Files.createDirectory(new File("InputProvider/").toPath());
		Files.createFile(folder.toPath());
	}
	//endregion
	
	//region Cleanup
	@AfterAll
	static void cleanUpAfter() throws Exception {
		Files.deleteIfExists(Path.of("InputProvider/InputProvider.json"));
		Files.deleteIfExists(Path.of("InputProvider/"));
	}
	//endregion
	
	@Test
	void constructor() throws Exception {
		assertThrows(NullPointerException.class, () -> new InputProvider((String) null));
		assertThrows(UncheckedIOException.class, () -> new InputProvider("InputProvider.json"));
		assertThrows(UncheckedIOException.class, () -> new InputProvider("InputProvider/"));
		assertDoesNotThrow(() -> new InputProvider("InputProvider/InputProvider.json")).close();
		
		assertThrows(NullPointerException.class, () -> new InputProvider(null, "InputProvider.json"));
		assertThrows(NullPointerException.class, () -> new InputProvider("InputProvider", null));
		assertThrows(UncheckedIOException.class, () -> new InputProvider("", "InputProvider.json"));
		assertThrows(UncheckedIOException.class, () -> new InputProvider("InputProvider/", ""));
		assertDoesNotThrow(() -> new InputProvider("InputProvider", "InputProvider.json")).close();
		
		assertThrows(NullPointerException.class, () -> new InputProvider((Path) null));
		assertThrows(UncheckedIOException.class, () -> new InputProvider(Path.of("InputProvider.json")));
		assertThrows(UncheckedIOException.class, () -> new InputProvider(Path.of("InputProvider/")));
		assertDoesNotThrow(() -> new InputProvider(Path.of("InputProvider/InputProvider.json"))).close();
		
		assertThrows(NullPointerException.class, () -> new InputProvider((File) null));
		assertThrows(UncheckedIOException.class, () -> new InputProvider(new File("InputProvider.json")));
		assertThrows(UncheckedIOException.class, () -> new InputProvider(new File("InputProvider/")));
		assertDoesNotThrow(() -> new InputProvider(new File("InputProvider/InputProvider.json"))).close();
		
		assertThrows(NullPointerException.class, () -> new InputProvider((InputStream) null));
		assertDoesNotThrow(() -> new InputProvider(new FileInputStream("InputProvider/InputProvider.json"))).close();
	}
	
	@Test
	void getStream() {
		assertNotNull(new InputProvider(InputStream.nullInputStream()).getStream());
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> new InputProvider(InputStream.nullInputStream()).close());
	}
}
