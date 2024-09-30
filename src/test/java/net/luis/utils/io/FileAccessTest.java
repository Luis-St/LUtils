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

package net.luis.utils.io;

import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FileAccess}.<br>
 *
 * @author Luis-St
 */
class FileAccessTest {
	
	private final Path folder = FileUtils.createSessionDirectory("FileAccessTest");
	
	FileAccessTest() throws IOException {}
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new FileAccess(null));
		assertDoesNotThrow(() -> new FileAccess(this.folder.resolve("test.json")));
		assertThrows(IllegalStateException.class, () -> new FileAccess(this.folder.resolve("test.json")));
	}
	
	@Test
	void getFile() {
		FileAccess fileAccess = new FileAccess(this.folder.resolve("test.json"));
		assertNotNull(fileAccess.getFile());
		assertEquals(this.folder.resolve("test.json"), fileAccess.getFile());
	}
	
	@Test
	void canAccess() {
		FileAccess fileAccess = new FileAccess(this.folder.resolve("test.json"));
		assertTrue(fileAccess.canAccess());
		fileAccess.access(f -> {
			assertFalse(fileAccess.canAccess());
		});
		assertTrue(fileAccess.canAccess());
	}
	
	@Test
	void access() {
		FileAccess fileAccess = new FileAccess(this.folder.resolve("test.json"));
		assertThrows(NullPointerException.class, () -> fileAccess.access((FailableConsumer<Path, IOException>) null));
		assertDoesNotThrow(() -> fileAccess.access(file -> {
			if (Files.notExists(file)) {
				Files.createDirectories(file.getParent());
				Files.createFile(file);
			}
		}));
		assertThrows(UncheckedIOException.class, () -> fileAccess.access(file -> {
			Files.delete(file);
			Files.delete(file);
		}));
	
		assertThrows(NullPointerException.class, () -> fileAccess.access((FailableFunction<Path, Object, IOException>) null));
		assertDoesNotThrow(() -> fileAccess.access(file -> {
			if (Files.notExists(file)) {
				Files.createDirectories(file.getParent());
				Files.createFile(file);
			}
			return 0;
		}));
		assertThrows(UncheckedIOException.class, () -> fileAccess.access(file -> {
			Files.delete(file);
			Files.delete(file);
			return 0;
		}));
	}
}