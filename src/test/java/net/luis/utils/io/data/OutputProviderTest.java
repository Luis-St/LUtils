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
 * Test class for {@link OutputProvider}.<br>
 *
 * @author Luis-St
 */
class OutputProviderTest {
	
	//region Setup
	@BeforeAll
	static void setUpBefore() throws Exception {
		File folder = new File("OutputProvider/OutputProvider.json");
		Files.createDirectory(new File("OutputProvider/").toPath());
		Files.createFile(folder.toPath());
	}
	//endregion
	
	//region Cleanup
	@AfterAll
	static void cleanUpAfter() throws Exception {
		Files.deleteIfExists(Path.of("OutputProvider/OutputProvider.json"));
		Files.deleteIfExists(Path.of("OutputProvider/"));
	}
	//endregion
	
	@Test
	void constructor() throws Exception {
		assertThrows(NullPointerException.class, () -> new OutputProvider((String) null));
		assertThrows(UncheckedIOException.class, () -> new OutputProvider("OutputProvider/"));
		assertDoesNotThrow(() -> new OutputProvider("OutputProvider/OutputProvider.json")).close();
		
		assertThrows(NullPointerException.class, () -> new OutputProvider(null, "OutputProvider.json"));
		assertThrows(NullPointerException.class, () -> new OutputProvider("OutputProvider", null));
		assertThrows(UncheckedIOException.class, () -> new OutputProvider("OutputProvider", ""));
		assertDoesNotThrow(() -> new OutputProvider("OutputProvider", "OutputProvider.json")).close();
		
		assertThrows(NullPointerException.class, () -> new OutputProvider((Path) null));
		assertThrows(UncheckedIOException.class, () -> new OutputProvider(Path.of("OutputProvider/")));
		assertDoesNotThrow(() -> new InputProvider(Path.of("OutputProvider/OutputProvider.json"))).close();
		
		assertThrows(NullPointerException.class, () -> new OutputProvider((File) null));
		assertThrows(UncheckedIOException.class, () -> new OutputProvider(new File("OutputProvider/")));
		assertDoesNotThrow(() -> new OutputProvider(new File("OutputProvider/OutputProvider.json"))).close();
		
		assertThrows(NullPointerException.class, () -> new OutputProvider((OutputStream) null));
		assertDoesNotThrow(() -> new OutputProvider(new FileOutputStream("OutputProvider/OutputProvider.json"))).close();
	}
	
	@Test
	void getStream() {
		assertNotNull(new OutputProvider(OutputStream.nullOutputStream()).getStream());
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> new OutputProvider(OutputStream.nullOutputStream()).close());
	}
}
