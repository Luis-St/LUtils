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
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for the entire logging system.<br>
 *
 * @author Luis-St
 */
class LoggingIntegrationTest {
	
	@BeforeAll
	static void setUp() {
		for (Level level : LoggingType.FILE) {
			System.setProperty("logging." + LoggingType.FILE + "." + level.name().toLowerCase(), "disabled");
		}
	}
	
	@AfterAll
	static void cleanUp() throws Exception {
		FileUtils.deleteRecursively(Path.of("integration-test-logs"));
		FileUtils.deleteRecursively(Path.of("test-logs"));
		Files.delete(Path.of("error.log"));
	}
	
	@Test
	void completeLoggingLifecycle() {
		LoggerConfiguration config = new LoggerConfiguration("com.example", "org.test")
			.setStatusLevel(Level.WARN)
			.enableLogging(LoggingType.CONSOLE)
			.enableLogging(LoggingType.FILE)
			.setRootDirectory("./test-logs/")
			.setFileSize("25MB")
			.setArchiveType(".gz")
			.setCompressionLevel(7)
			.setMaxArchiveFiles(15)
			.setArchiveAutoDeletionDepth(2)
			.setArchiveAutoDeletionAge(45)
			.addDefaultLogger(LoggingType.CONSOLE, Level.INFO)
			.addDefaultLogger(LoggingType.CONSOLE, Level.WARN)
			.addDefaultLogger(LoggingType.CONSOLE, Level.ERROR)
			.addDefaultLogger(LoggingType.FILE, Level.ERROR)
			.overrideConsolePattern(Level.INFO, "[%d{HH:mm:ss}] [INFO] %msg%n")
			.overrideConsolePattern(Level.ERROR, "[%d{HH:mm:ss}] [ERROR] %C{1} - %msg%n%throwable")
			.overrideFilePattern(Level.ERROR, "%d{yyyy-MM-dd HH:mm:ss} [ERROR] %C - %msg%n%ex")
			.overrideErrorLog("application-errors.log", "application-errors-%d{yyyy-MM-dd}-%i.log");
		
		assertDoesNotThrow(() -> LoggingUtils.reconfigure(config));
		
		assertTrue(LoggingUtils.isInitialized());
		assertNotNull(LoggingUtils.getConfiguration());
		
		List<String> configuredLoggers = LoggingUtils.getConfiguredLoggers();
		assertNotNull(configuredLoggers);
		assertTrue(configuredLoggers.contains("com.example") || configuredLoggers.contains("org.test"));
		
		Configuration builtConfig = config.build();
		assertNotNull(builtConfig);
		assertEquals("RuntimeConfiguration", builtConfig.getName());
		
		Logger logger = LogManager.getLogger("com.example.TestClass");
		assertNotNull(logger);
		
		Marker marker = LoggingUtils.createRefrenceMarker(8);
		assertNotNull(marker);
		assertEquals(8, marker.getName().length());
		
		assertDoesNotThrow(() -> logger.info("Test info message"));
		assertDoesNotThrow(() -> logger.error("Test error message"));
		assertDoesNotThrow(() -> logger.warn(marker, "Test warning with marker"));
		
		assertDoesNotThrow(() -> LoggingUtils.enableConsole(Level.DEBUG));
		assertDoesNotThrow(() -> LoggingUtils.disableConsole(Level.INFO));
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> LoggingUtils.enableFile(Level.INFO));
		assertEquals("Appender FileInfo not found, appender may not be registered", e.getMessage());
	}
	
	@Test
	void systemPropertiesIntegration() {
		try {
			System.setProperty("logging.console", "true");
			System.setProperty("logging.file", "true");
			System.setProperty("logging.status_level", "debug");
			System.setProperty("logging.file.size", "100MB");
			System.setProperty("logging.archive.type", ".zip");
			System.setProperty("logging.console.info", "true");
			System.setProperty("logging.console.warn", "false");
			System.setProperty("logging.file.error", "true");
			System.setProperty("logging.archive.max_files", "20");
			System.setProperty("logging.archive.compression_level", "9");
			System.setProperty("logging.archive.auto_deletion.depth", "3");
			System.setProperty("logging.archive.auto_deletion.age", "90");
			
			assertDoesNotThrow(() -> LoggingUtils.load("*"));
			
			assertTrue(LoggingUtils.isInitialized());
			
			assertDoesNotThrow(LoggingHelper::configure);
			
			Logger logger = LogManager.getLogger(LoggingIntegrationTest.class);
			assertDoesNotThrow(() -> logger.info("Integration test message"));
			
		} finally {
			System.clearProperty("logging.console");
			System.clearProperty("logging.file");
			System.clearProperty("logging.status_level");
			System.clearProperty("logging.file.size");
			System.clearProperty("logging.archive.type");
			System.clearProperty("logging.console.info");
			System.clearProperty("logging.file.error");
			System.clearProperty("logging.console.warn");
			System.clearProperty("logging.console.warn");
			System.clearProperty("logging.archive.max_files");
			System.clearProperty("logging.archive.compression_level");
			System.clearProperty("logging.archive.auto_deletion.depth");
			System.clearProperty("logging.archive.auto_deletion.age");
		}
	}
	
	@Test
	void springFactoryIntegration() {
		LoggingUtils.registerSpringFactory();
		assertTrue(LoggingUtils.hasFactoryBeenRegistered());
		
		String factoryProperty = System.getProperty("log4j.configurationFactory");
		assertNotNull(factoryProperty);
		assertTrue(factoryProperty.contains("SpringFactory"));
		
		LoggerConfiguration config = new LoggerConfiguration("*")
			.setStatusLevel(Level.INFO)
			.addDefaultLogger(LoggingType.CONSOLE, Level.ERROR);
		
		LoggingUtils.reconfigure(config);
		
		assertNotNull(LoggingUtils.getConfiguration());
		assertTrue(LoggingUtils.isInitialized());
	}
	
	@Test
	void exceptionHandlerIntegration() {
		LoggingExceptionHandler handler = new LoggingExceptionHandler();
		
		Thread testThread = new Thread(() -> {
			throw new RuntimeException("Test exception for integration");
		});
		testThread.setName("IntegrationTestThread");
		
		testThread.setUncaughtExceptionHandler(handler);
		
		assertDoesNotThrow(() -> {
			testThread.start();
			try {
				testThread.join(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
	}
	
	@Test
	void multipleLoggingTypesAndLevels() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		for (Level level : LoggingType.CONSOLE.getAllowedLevels()) {
			config.addDefaultLogger(LoggingType.CONSOLE, level);
		}
		
		for (Level level : LoggingType.FILE.getAllowedLevels()) {
			config.addDefaultLogger(LoggingType.FILE, level);
		}
		
		config.overrideConsolePattern(Level.ALL, "%d{HH:mm:ss} [%level] %msg%n")
			.overrideFilePattern(Level.ALL, "%d{yyyy-MM-dd HH:mm:ss} [%level] %C - %msg%n%throwable");
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
		
		assertDoesNotThrow(() -> LoggingUtils.reconfigure(config));
		
		Logger logger = LogManager.getLogger("TestLogger");
		
		assertDoesNotThrow(() -> logger.trace("Trace message"));
		assertDoesNotThrow(() -> logger.debug("Debug message"));
		assertDoesNotThrow(() -> logger.info("Info message"));
		assertDoesNotThrow(() -> logger.warn("Warn message"));
		assertDoesNotThrow(() -> logger.error("Error message"));
		assertDoesNotThrow(() -> logger.fatal("Fatal message"));
	}
	
	@Test
	void configurationsWithDifferentLoggers() {
		LoggerConfiguration config1 = new LoggerConfiguration("com.example.service", "com.example.controller");
		Configuration result1 = assertDoesNotThrow(config1::build);
		assertNotNull(result1);
		
		LoggerConfiguration config2 = new LoggerConfiguration("*");
		Configuration result2 = assertDoesNotThrow(config2::build);
		assertNotNull(result2);
		
		LoggerConfiguration config3 = new LoggerConfiguration("com.example", "*", "org.test");
		Configuration result3 = assertDoesNotThrow(config3::build);
		assertNotNull(result3);
		
		assertDoesNotThrow(() -> LoggingUtils.reconfigure(config1));
		assertDoesNotThrow(() -> LoggingUtils.reconfigure(config2));
		assertDoesNotThrow(() -> LoggingUtils.reconfigure(config3));
	}
	
	@Test
	void patternOverridesIntegration() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		String complexPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] [%marker] [%C{1}:%line/%level] %msg%n%throwable";
		config.overrideConsolePattern(Level.ALL, complexPattern);
		
		config.overrideFilePattern(Level.INFO, "%msg%n").overrideFilePattern(Level.OFF, "%msg");
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
		
		assertDoesNotThrow(() -> LoggingUtils.reconfigure(config));
		
		Logger logger = LogManager.getLogger("PatternTest");
		Marker marker = LoggingUtils.createRefrenceMarker(6);
		
		assertDoesNotThrow(() -> logger.info(marker, "Test message with complex pattern"));
		assertDoesNotThrow(() -> logger.error("Error message", new RuntimeException("Test exception")));
	}
	
	@Test
	void fileConfigurationIntegration() {
		LoggerConfiguration config = new LoggerConfiguration("*")
			.setRootDirectory("./integration-test-logs/")
			.setFileSize("5MB")
			.setArchiveType(".bz2")
			.setCompressionLevel(9)
			.setMaxArchiveFiles(5)
			.setArchiveAutoDeletionDepth(3)
			.setArchiveAutoDeletionAge(7)
			.overrideDebugLog("debug-integration.log", "debug-integration-%d{yyyyMMdd}-%i.log")
			.overrideInfoLog("info-integration.log", "info-integration-%d{yyyyMMdd}-%i.log")
			.overrideErrorLog("error-integration.log", "error-integration-%d{yyyyMMdd}-%i.log")
			.addDefaultLogger(LoggingType.FILE, Level.DEBUG)
			.addDefaultLogger(LoggingType.FILE, Level.INFO)
			.addDefaultLogger(LoggingType.FILE, Level.ERROR);
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
		
		assertDoesNotThrow(() -> LoggingUtils.reconfigure(config));
		
		Logger logger = LogManager.getLogger("FileIntegrationTest");
		
		assertDoesNotThrow(() -> logger.debug("Debug message for file"));
		assertDoesNotThrow(() -> logger.info("Info message for file"));
		assertDoesNotThrow(() -> logger.error("Error message for file", new Exception("Test file exception")));
	}
	
	@Test
	void markerIntegration() {
		for (int i = 1; i <= 32; i++) {
			Marker marker = LoggingUtils.createRefrenceMarker(i);
			assertNotNull(marker);
			assertEquals(i, marker.getName().length());
			assertTrue(marker.getName().matches("[0-9a-fA-F]+"));
		}
		
		LoggerConfiguration config = new LoggerConfiguration("*")
			.addDefaultLogger(LoggingType.CONSOLE, Level.INFO);
		
		assertDoesNotThrow(() -> LoggingUtils.reconfigure(config));
		
		Logger logger = LogManager.getLogger("MarkerTest");
		Marker shortMarker = LoggingUtils.createRefrenceMarker(4);
		Marker longMarker = LoggingUtils.createRefrenceMarker(16);
		
		assertDoesNotThrow(() -> logger.info(shortMarker, "Message with short marker"));
		assertDoesNotThrow(() -> logger.info(longMarker, "Message with long marker"));
		assertDoesNotThrow(() -> logger.warn(shortMarker, "Warning with marker"));
	}
	
	@Test
	void enableDisableIntegration() {
		LoggerConfiguration config = new LoggerConfiguration("*")
			.addDefaultLogger(LoggingType.CONSOLE, Level.INFO)
			.addDefaultLogger(LoggingType.CONSOLE, Level.ERROR);
		
		assertDoesNotThrow(() -> LoggingUtils.reconfigure(config));
		
		Logger logger = LogManager.getLogger("EnableDisableTest");
		
		assertDoesNotThrow(() -> logger.info("Initial info message"));
		assertDoesNotThrow(() -> logger.error("Initial error message"));
		
		assertDoesNotThrow(() -> LoggingUtils.disableConsole(Level.INFO));
		assertDoesNotThrow(() -> logger.info("This info should be disabled"));
		assertDoesNotThrow(() -> logger.error("This error should still work"));
		
		assertDoesNotThrow(() -> LoggingUtils.enableConsole(Level.DEBUG));
		assertDoesNotThrow(() -> logger.debug("This debug should now work"));
		
		assertDoesNotThrow(() -> LoggingUtils.enableConsole(Level.INFO));
		assertDoesNotThrow(() -> logger.info("This info should work again"));
	}
}
