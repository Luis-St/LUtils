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

import net.luis.utils.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Edge cases and stress test class for the logging system.<br>
 *
 * @author Luis-St
 */
class LoggingEdgeCasesTest {
	
	@AfterAll
	static void cleanUp() throws Exception {
		FileUtils.deleteRecursively(Path.of("test"));
	}
	
	@Test
	void extremelyLongLoggerNames() {
		StringBuilder longName = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			longName.append("com.example.very.long.package.name.");
		}
		longName.append("ClassName");
		
		LoggerConfiguration config = new LoggerConfiguration(longName.toString());
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
	}
	
	@Test
	void manyLoggerNames() {
		String[] manyLoggers = new String[100];
		for (int i = 0; i < 100; i++) {
			manyLoggers[i] = "com.example.package" + i + ".Logger" + i;
		}
		
		LoggerConfiguration config = new LoggerConfiguration(manyLoggers);
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
	}
	
	@Test
	void specialCharactersInLoggerNames() {
		String[] specialLoggers = {
			"com.example.test-package",
			"com.example.test_package",
			"com.example.test$package",
			"com.example.123package",
			"123.456.789",
			"äöü.special.unicode",
			"com.example.package.with.dots...",
			"   com.example.whitespace   "
		};
		
		for (String logger : specialLoggers) {
			LoggerConfiguration config = new LoggerConfiguration(logger);
			Configuration result = assertDoesNotThrow(config::build);
			assertNotNull(result);
		}
	}
	
	@Test
	void extremeFileSizes() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		assertDoesNotThrow(() -> config.setFileSize("1B"));
		assertDoesNotThrow(() -> config.setFileSize("0.1KB"));
		assertDoesNotThrow(() -> config.setFileSize("0,5MB"));
		
		assertDoesNotThrow(() -> config.setFileSize("999999TB"));
		assertDoesNotThrow(() -> config.setFileSize("1000000000000GB"));
		
		assertDoesNotThrow(() -> config.setFileSize("1.0MB"));
		assertDoesNotThrow(() -> config.setFileSize("1,0MB"));
		assertDoesNotThrow(() -> config.setFileSize("123.456789MB"));
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
	}
	
	@Test
	void extremeCompressionAndArchiveSettings() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		config.setCompressionLevel(Integer.MIN_VALUE);
		config.setMaxArchiveFiles(Integer.MIN_VALUE);
		config.setArchiveAutoDeletionDepth(Integer.MIN_VALUE);
		config.setArchiveAutoDeletionAge(Integer.MIN_VALUE);
		
		Configuration result1 = assertDoesNotThrow(config::build);
		assertNotNull(result1);
		
		config.setCompressionLevel(Integer.MAX_VALUE);
		config.setMaxArchiveFiles(Integer.MAX_VALUE);
		config.setArchiveAutoDeletionDepth(Integer.MAX_VALUE);
		config.setArchiveAutoDeletionAge(Integer.MAX_VALUE);
		
		Configuration result2 = assertDoesNotThrow(config::build);
		assertNotNull(result2);
	}
	
	@Test
	void complexDirectoryPaths() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		String[] complexPaths = {
			"./logs/",
			"./logs/subfolder/subsubfolder/",
			"C:/Program Files/Application/logs/",
			"C:\\Program Files\\Application\\logs\\",
			"/usr/local/var/log/application/",
			"./logs with spaces/",
			"./logs-with-dashes/",
			"./logs_with_underscores/",
			"./logs123/",
			"D:/very/deep/nested/folder/structure/for/logs/",
			"/var/log/application/logs/"
		};
		
		for (String path : complexPaths) {
			assertDoesNotThrow(() -> config.setRootDirectory(path));
			Configuration result = assertDoesNotThrow(config::build);
			assertNotNull(result);
		}
	}
	
	@Test
	void complexLogFilePatterns() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		String[] complexFiles = {
			"application.log",
			"app-name.log",
			"app_name.log",
			"app.name.log",
			"123.log",
			"log-with-many-dashes.log",
			"log_with_many_underscores.log",
			"very-long-log-file-name-with-many-words.log",
			"subfolder/nested.log",
			"deep/nested/folder/structure/app.log"
		};
		
		String[] complexArchives = {
			"archive-%d{yyyy-MM-dd}.log",
			"archive-%d{yyyyMMdd-HHmmss}-%i.log",
			"app-%d{yyyy/MM/dd/HH}-%i.log",
			"very-complex-archive-pattern-%d{yyyy-MM-dd-HH-mm-ss}-%i.log",
			"subfolder/nested-archive-%d{yyyy-MM-dd}-%i.log",
			"deep/nested/archive-%d{yyyy}/%d{MM}/%d{dd}/app-%i.log"
		};
		
		for (int i = 0; i < complexFiles.length && i < complexArchives.length; i++) {
			int finalI = i;
			assertDoesNotThrow(() -> config.overrideLog(Level.ERROR, complexFiles[finalI], complexArchives[finalI]));
			Configuration result = assertDoesNotThrow(config::build);
			assertNotNull(result);
		}
	}
	
	@Test
	void extremePatternOverrides() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		StringBuilder longPattern = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			longPattern.append("[%d{yyyy-MM-dd HH:mm:ss.SSS}] ");
		}
		longPattern.append("%msg%n");
		
		assertDoesNotThrow(() -> config.overrideConsolePattern(Level.ALL, longPattern.toString()));
		
		String complexPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] [%marker] [%level] [%C{1}] [%M] [%line] [%F] %msg%n%throwable%ex%exception";
		assertDoesNotThrow(() -> config.overrideFilePattern(Level.ALL, complexPattern));
		
		String[] minimalPatterns = {"%msg", "%m", "%message", "%throwable", "%ex", "%exception"};
		for (String pattern : minimalPatterns) {
			assertDoesNotThrow(() -> config.overrideConsolePattern(Level.INFO, pattern));
		}
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
	}
	
	@Test
	void manyDefaultLoggers() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		for (int i = 0; i < 100; i++) {
			for (Level level : LoggingType.CONSOLE.getAllowedLevels()) {
				config.addDefaultLogger(LoggingType.CONSOLE, level);
			}
		}
		
		for (int i = 0; i < 100; i++) {
			for (Level level : LoggingType.FILE.getAllowedLevels()) {
				config.addDefaultLogger(LoggingType.FILE, level);
			}
		}
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
	}
	
	@Test
	void alternatingEnableDisable() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		for (int i = 0; i < 100; i++) {
			config.enableLogging(LoggingType.CONSOLE);
			config.disableLogging(LoggingType.CONSOLE);
			config.enableLogging(LoggingType.FILE);
			config.disableLogging(LoggingType.FILE);
		}
		
		config.enableLogging(LoggingType.CONSOLE);
		config.enableLogging(LoggingType.FILE);
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
	}
	
	@Test
	void manyPatternOverrides() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		for (int i = 0; i < 100; i++) {
			config.overrideConsolePattern(Level.INFO, "%msg" + i + "%n");
			config.overrideFilePattern(Level.ERROR, "%d{HH:mm:ss} %msg" + i + "%n%throwable");
		}
		
		config.overrideConsolePattern(Level.OFF, "%msg");
		config.overrideFilePattern(Level.OFF, "%msg");
		
		for (Level level : LoggingType.CONSOLE.getAllowedLevels()) {
			config.overrideConsolePattern(level, "%d{HH:mm:ss} [" + level.name() + "] %msg%n");
		}
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
	}
	
	@Test
	void extremeMarkerLengths() {
		for (int length = 1; length <= 32; length++) {
			for (int count = 0; count < 10; count++) {
				int finalLength = length;
				assertDoesNotThrow(() -> LoggingUtils.createRefrenceMarker(finalLength));
			}
		}
		
		String[] markers = new String[100];
		for (int i = 0; i < 100; i++) {
			markers[i] = LoggingUtils.createRefrenceMarker(16).getName();
		}
		
		for (int i = 0; i < markers.length; i++) {
			for (int j = i + 1; j < markers.length; j++) {
				assertNotEquals(markers[i], markers[j], "Markers should be unique");
			}
		}
	}
	
	@Test
	void rapidReconfiguration() {
		for (int i = 0; i < 20; i++) {
			LoggerConfiguration config = new LoggerConfiguration("*")
				.setStatusLevel(Level.values()[i % Level.values().length])
				.setFileSize((i * 10) + "MB")
				.setCompressionLevel(i % 10)
				.setMaxArchiveFiles(i + 1);
			
			if (i % 2 == 0) {
				config.enableLogging(LoggingType.CONSOLE);
				config.disableLogging(LoggingType.FILE);
			} else {
				config.disableLogging(LoggingType.CONSOLE);
				config.enableLogging(LoggingType.FILE);
			}
			
			assertDoesNotThrow(() -> LoggingUtils.reconfigure(config));
			assertTrue(LoggingUtils.isInitialized());
		}
	}
	
	@Test
	void nullAndEmptyStringHandling() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		assertThrows(NullPointerException.class, () -> config.setRootDirectory(null));
		assertThrows(NullPointerException.class, () -> config.setFileSize(null));
		assertThrows(NullPointerException.class, () -> config.setArchiveType(null));
		assertThrows(NullPointerException.class, () -> config.overrideLog(Level.ERROR, null, "archive.log"));
		assertThrows(NullPointerException.class, () -> config.overrideLog(Level.ERROR, "test.log", null));
		assertThrows(NullPointerException.class, () -> config.overrideConsolePattern(Level.INFO, null));
		
		assertThrows(IllegalArgumentException.class, () -> config.setRootDirectory(""));
		assertThrows(IllegalArgumentException.class, () -> config.setFileSize(""));
		assertThrows(IllegalArgumentException.class, () -> config.setArchiveType(""));
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.ERROR, "", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.ERROR, "test.log", ""));
		assertThrows(IllegalArgumentException.class, () -> config.overrideConsolePattern(Level.INFO, ""));
	}
	
	@Test
	void simultaneousOperations() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		config.setStatusLevel(Level.WARN)
			  .enableLogging(LoggingType.CONSOLE)
			  .enableLogging(LoggingType.FILE)
			  .disableLogging(LoggingType.CONSOLE)
			  .enableLogging(LoggingType.CONSOLE)
			  .setRootDirectory("./test/")
			  .setFileSize("100MB")
			  .setArchiveType(".gz")
			  .setCompressionLevel(5)
			  .setMaxArchiveFiles(10)
			  .addDefaultLogger(LoggingType.CONSOLE, Level.INFO)
			  .removeDefaultLogger(LoggingType.CONSOLE, Level.INFO)
			  .addDefaultLogger(LoggingType.CONSOLE, Level.ERROR)
			  .addDefaultLogger(LoggingType.FILE, Level.ERROR)
			  .overrideConsolePattern(Level.ALL, "%msg%n")
			  .overrideConsolePattern(Level.OFF, "%msg")
			  .overrideFilePattern(Level.ERROR, "%d %msg%n%throwable")
			  .overrideLog(Level.ERROR, "errors.log", "errors-%i.log");
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
	}
}
