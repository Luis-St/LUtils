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

package net.luis.utils.logging;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LoggingHelper}.<br>
 * Note: LoggingHelper is package-private, so these tests focus on functionality
 * that can be tested indirectly through LoggingUtils.load() method.
 *
 * @author Luis-St
 */
class LoggingHelperTest {
	
	@BeforeAll
	static void setUp() {
		for (Level level : LoggingType.FILE) {
			System.setProperty("logging." + LoggingType.FILE + "." + level.name().toLowerCase(), "disabled");
		}
	}
	
	@Test
	void loadThroughLoggingUtils() {
		assertDoesNotThrow(() -> LoggingUtils.load("*"));
		assertDoesNotThrow(() -> LoggingUtils.load("com.example"));
		assertDoesNotThrow(() -> LoggingUtils.load(Arrays.asList("com.example", "org.test")));
	}
	
	@Test
	void loadWithInvalidInputsThroughLoggingUtils() {
		assertThrows(NullPointerException.class, () -> LoggingUtils.load((String[]) null));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.load(Collections.emptyList()));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.load(""));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.load("   "));
	}
	
	@Test
	void systemPropertyHandling() {
		String originalConsole = System.getProperty("logging.console");
		String originalFile = System.getProperty("logging.file");
		String originalStatusLevel = System.getProperty("logging.status_level");
		
		try {
			System.setProperty("logging.console", "true");
			System.setProperty("logging.file", "false");
			System.setProperty("logging.status_level", "warn");
			
			assertDoesNotThrow(() -> LoggingUtils.load("*"));
			
			System.setProperty("logging.console", "invalid");
			assertThrows(Exception.class, () -> LoggingUtils.load("*"));
			
			System.setProperty("logging.console", "true");
			System.setProperty("logging.status_level", "invalid_level");
			assertThrows(Exception.class, () -> LoggingUtils.load("*"));
			
		} finally {
			if (originalConsole != null) {
				System.setProperty("logging.console", originalConsole);
			} else {
				System.clearProperty("logging.console");
			}
			if (originalFile != null) {
				System.setProperty("logging.file", originalFile);
			} else {
				System.clearProperty("logging.file");
			}
			if (originalStatusLevel != null) {
				System.setProperty("logging.status_level", originalStatusLevel);
			} else {
				System.clearProperty("logging.status_level");
			}
		}
	}
	
	@Test
	void fileLoggingSystemProperties() {
		String originalFileSize = System.getProperty("logging.file.size");
		String originalArchiveType = System.getProperty("logging.archive.type");
		String originalCompressionLevel = System.getProperty("logging.archive.compression_level");
		
		try {
			System.setProperty("logging.file.size", "50MB");
			System.setProperty("logging.archive.type", ".zip");
			System.setProperty("logging.archive.compression_level", "9");
			
			assertDoesNotThrow(() -> LoggingUtils.load("*"));
			
			System.setProperty("logging.file.size", "invalid_size");
			assertThrows(Exception.class, () -> LoggingUtils.load("*"));
			
			System.setProperty("logging.file.size", "50MB");
			
			System.setProperty("logging.archive.type", ".invalid");
			assertThrows(Exception.class, () -> LoggingUtils.load("*"));
			
		} finally {
			if (originalFileSize != null) {
				System.setProperty("logging.file.size", originalFileSize);
			} else {
				System.clearProperty("logging.file.size");
			}
			if (originalArchiveType != null) {
				System.setProperty("logging.archive.type", originalArchiveType);
			} else {
				System.clearProperty("logging.archive.type");
			}
			if (originalCompressionLevel != null) {
				System.setProperty("logging.archive.compression_level", originalCompressionLevel);
			} else {
				System.clearProperty("logging.archive.compression_level");
			}
		}
	}
	
	@Test
	void enableDisableProperties() {
		String originalConsoleDebug = System.getProperty("logging.console.debug");
		String originalFileInfo = System.getProperty("logging.file.info");
		
		try {
			String[] enableValues = { "true", "enable", "enabled", "1", "on", "yes" };
			String[] disableValues = { "false", "disable", "disabled", "0", "off", "no" };
			
			for (String enableValue : enableValues) {
				System.setProperty("logging.console.debug", enableValue);
				assertDoesNotThrow(LoggingHelper::configure);
			}
			
			for (String disableValue : disableValues) {
				System.setProperty("logging.console.debug", disableValue);
				assertDoesNotThrow(LoggingHelper::configure);
			}
			
			System.setProperty("logging.console.debug", "invalid");
			assertThrows(Exception.class, LoggingHelper::configure);
			
		} finally {
			if (originalConsoleDebug != null) {
				System.setProperty("logging.console.debug", originalConsoleDebug);
			} else {
				System.clearProperty("logging.console.debug");
			}
			if (originalFileInfo != null) {
				System.setProperty("logging.file.info", originalFileInfo);
			} else {
				System.clearProperty("logging.file.info");
			}
		}
	}
	
	@Test
	void configureMethod() {
		assertDoesNotThrow(LoggingHelper::configure);
		
		assertDoesNotThrow(LoggingHelper::configure);
		assertDoesNotThrow(LoggingHelper::configure);
	}
	
	@Test
	void configurationFileProperty() {
		String originalConfig = System.getProperty("logging.config");
		
		try {
			System.setProperty("logging.config", "./non-existent-file.properties");
			assertThrows(Exception.class, () -> LoggingUtils.load("*"));
			
			System.setProperty("logging.config", "invalid-path");
			assertThrows(Exception.class, () -> LoggingUtils.load("*"));
			
		} finally {
			if (originalConfig != null) {
				System.setProperty("logging.config", originalConfig);
			} else {
				System.clearProperty("logging.config");
			}
		}
	}
	
	@Test
	void rootDirectoryProperty() {
		String originalRoot = System.getProperty("logging.file.folder.root");
		
		try {
			System.setProperty("logging.file.folder.root", "./logs/");
			assertDoesNotThrow(() -> LoggingUtils.load("*"));
			
			System.setProperty("logging.file.folder.root", "C:/logs/");
			assertDoesNotThrow(() -> LoggingUtils.load("*"));
			
			System.setProperty("logging.file.folder.root", "invalid-path");
			assertThrows(Exception.class, () -> LoggingUtils.load("*"));
			
		} finally {
			if (originalRoot != null) {
				System.setProperty("logging.file.folder.root", originalRoot);
			} else {
				System.clearProperty("logging.file.folder.root");
			}
		}
	}
	
	@Test
	void numericProperties() {
		String originalMaxFiles = System.getProperty("logging.archive.max_files");
		String originalDepth = System.getProperty("logging.archive.auto_deletion.depth");
		String originalAge = System.getProperty("logging.archive.auto_deletion.age");
		
		try {
			System.setProperty("logging.archive.max_files", "5");
			System.setProperty("logging.archive.auto_deletion.depth", "2");
			System.setProperty("logging.archive.auto_deletion.age", "60");
			
			assertDoesNotThrow(() -> LoggingUtils.load("*"));
			
			System.setProperty("logging.archive.max_files", "not_a_number");
			assertDoesNotThrow(() -> LoggingUtils.load("*"));
			
		} finally {
			if (originalMaxFiles != null) {
				System.setProperty("logging.archive.max_files", originalMaxFiles);
			} else {
				System.clearProperty("logging.archive.max_files");
			}
			if (originalDepth != null) {
				System.setProperty("logging.archive.auto_deletion.depth", originalDepth);
			} else {
				System.clearProperty("logging.archive.auto_deletion.depth");
			}
			if (originalAge != null) {
				System.setProperty("logging.archive.auto_deletion.age", originalAge);
			} else {
				System.clearProperty("logging.archive.auto_deletion.age");
			}
		}
	}
}
