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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LoggingUtils}.<br>
 *
 * @author Luis-St
 */
class LoggingUtilsTest {
	
	@BeforeAll
	static void setUp() {
		for (Level level : LoggingType.FILE) {
			System.setProperty("logging." + LoggingType.FILE + "." + level.name().toLowerCase(), "disabled");
		}
	}
	
	@BeforeEach
	void setUpEach() {
		LoggingUtils.configuration = null;
	}
	
	@Test
	void loadWithVarArgs() {
		assertThrows(NullPointerException.class, () -> LoggingUtils.load((String[]) null));
		assertThrows(IllegalArgumentException.class, LoggingUtils::load);
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.load((String) null));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.load(""));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.load("   "));
		
		assertDoesNotThrow(() -> LoggingUtils.load("*"));
		assertDoesNotThrow(() -> LoggingUtils.load("com.example"));
		assertDoesNotThrow(() -> LoggingUtils.load("com.example", "org.test"));
	}
	
	@Test
	void loadWithList() {
		assertThrows(NullPointerException.class, () -> LoggingUtils.load((List<String>) null));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.load(Collections.emptyList()));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.load(List.of("")));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.load(List.of("   ")));
		
		assertDoesNotThrow(() -> LoggingUtils.load(List.of("*")));
		assertDoesNotThrow(() -> LoggingUtils.load(List.of("com.example")));
		assertDoesNotThrow(() -> LoggingUtils.load(Arrays.asList("com.example", "org.test")));
	}
	
	@Test
	void initialize() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> LoggingUtils.initialize(null));
		
		try {
			assertDoesNotThrow(() -> LoggingUtils.initialize(config));
		} catch (IllegalStateException e) {
			assertTrue(e.getMessage().contains("already been initialized"));
		}
	}
	
	@Test
	void initializeWithOverride() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> LoggingUtils.initialize(null, false));
		assertThrows(NullPointerException.class, () -> LoggingUtils.initialize(null, true));
		
		assertDoesNotThrow(() -> LoggingUtils.initialize(config, true));
		
		assertThrows(IllegalStateException.class, () -> LoggingUtils.initialize(config, false));
	}
	
	@Test
	void initializeSafe() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> LoggingUtils.initializeSafe(null));
		
		assertDoesNotThrow(() -> LoggingUtils.initializeSafe(config));
	}
	
	@Test
	void reconfigure() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> LoggingUtils.reconfigure(null));
		
		assertDoesNotThrow(() -> LoggingUtils.reconfigure(config));
	}
	
	@Test
	void initializeOrReconfigure() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> LoggingUtils.initializeOrReconfigure(null));
		
		assertDoesNotThrow(() -> LoggingUtils.initializeOrReconfigure(config));
		assertDoesNotThrow(() -> LoggingUtils.initializeOrReconfigure(config));
	}
	
	@Test
	void isInitialized() {
		assertDoesNotThrow(LoggingUtils::isInitialized);
	}
	
	@Test
	void hasFactoryBeenRegistered() {
		assertDoesNotThrow(LoggingUtils::hasFactoryBeenRegistered);
		
		boolean initialState = LoggingUtils.hasFactoryBeenRegistered();
		
		LoggingUtils.registerSpringFactory();
		
		assertTrue(LoggingUtils.hasFactoryBeenRegistered());
		
		LoggingUtils.registerSpringFactory();
		assertTrue(LoggingUtils.hasFactoryBeenRegistered());
	}
	
	@Test
	void getConfiguration() {
		assertDoesNotThrow(LoggingUtils::getConfiguration);
	}
	
	@Test
	void getConfiguredLoggers() {
		List<String> loggers = assertDoesNotThrow(LoggingUtils::getConfiguredLoggers);
		assertNotNull(loggers);
		
		assertThrows(UnsupportedOperationException.class, () -> loggers.add("test"));
	}
	
	@Test
	void isRootLoggerConfigured() {
		assertDoesNotThrow(LoggingUtils::isRootLoggerConfigured);
	}
	
	@Test
	void registerSpringFactory() {
		assertDoesNotThrow(LoggingUtils::registerSpringFactory);
		
		String factoryProperty = System.getProperty("log4j.configurationFactory");
		assertNotNull(factoryProperty);
		assertTrue(factoryProperty.contains("SpringFactory"));
		
		assertDoesNotThrow(LoggingUtils::registerSpringFactory);
		assertDoesNotThrow(LoggingUtils::registerSpringFactory);
	}
	
	@Test
	void enableLogging() {
		assertThrows(NullPointerException.class, () -> LoggingUtils.enable(null, null));
		assertThrows(NullPointerException.class, () -> LoggingUtils.enable(LoggingType.CONSOLE, null));
		assertThrows(NullPointerException.class, () -> LoggingUtils.enable(null, Level.INFO));
		
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.enable(LoggingType.CONSOLE, Level.ALL));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.enable(LoggingType.CONSOLE, Level.OFF));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.enable(LoggingType.FILE, Level.TRACE));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.enable(LoggingType.FILE, Level.WARN));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.enable(LoggingType.FILE, Level.FATAL));
		
		try {
			assertDoesNotThrow(() -> LoggingUtils.enable(LoggingType.CONSOLE, Level.INFO));
			IllegalStateException e = assertThrows(IllegalStateException.class, () -> LoggingUtils.enable(LoggingType.FILE, Level.ERROR));
			assertEquals("Appender FileError not found, appender may not be registered", e.getMessage());
		} catch (IllegalStateException e) {
			assertTrue(e.getMessage().contains("not found") || e.getMessage().contains("not registered"));
		}
	}
	
	@Test
	void enableConsole() {
		assertThrows(NullPointerException.class, () -> LoggingUtils.enableConsole(null));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.enableConsole(Level.ALL));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.enableConsole(Level.OFF));
		
		Level[] validLevels = { Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL };
		for (Level level : validLevels) {
			assertDoesNotThrow(() -> LoggingUtils.enableConsole(level));
		}
		
		assertDoesNotThrow(() -> LoggingUtils.enableConsole());
	}
	
	@Test
	void enableFile() {
		assertThrows(NullPointerException.class, () -> LoggingUtils.enableFile(null));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.enableFile(Level.ALL));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.enableFile(Level.OFF));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.enableFile(Level.TRACE));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.enableFile(Level.WARN));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.enableFile(Level.FATAL));
		
		Level[] validLevels = { Level.DEBUG, Level.INFO, Level.ERROR };
		for (Level level : validLevels) {
			IllegalStateException e = assertThrows(IllegalStateException.class, () -> LoggingUtils.enableFile(level));
			assertEquals("Appender File" + StringUtils.capitalize(level.toString().toLowerCase()) + " not found, appender may not be registered", e.getMessage());
		}
		
		IllegalStateException e = assertThrows(IllegalStateException.class, LoggingUtils::enableFile);
		assertEquals("Appender FileDebug not found, appender may not be registered", e.getMessage());
	}
	
	@Test
	void disableLogging() {
		assertThrows(NullPointerException.class, () -> LoggingUtils.disable(null, null));
		assertThrows(NullPointerException.class, () -> LoggingUtils.disable(LoggingType.CONSOLE, null));
		assertThrows(NullPointerException.class, () -> LoggingUtils.disable(null, Level.INFO));
		
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.disable(LoggingType.CONSOLE, Level.ALL));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.disable(LoggingType.CONSOLE, Level.OFF));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.disable(LoggingType.FILE, Level.TRACE));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.disable(LoggingType.FILE, Level.WARN));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.disable(LoggingType.FILE, Level.FATAL));
		
		assertDoesNotThrow(() -> LoggingUtils.disable(LoggingType.CONSOLE, Level.INFO));
		assertDoesNotThrow(() -> LoggingUtils.disable(LoggingType.FILE, Level.ERROR));
	}
	
	@Test
	void disableConsole() {
		assertThrows(NullPointerException.class, () -> LoggingUtils.disableConsole(null));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.disableConsole(Level.ALL));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.disableConsole(Level.OFF));
		
		Level[] validLevels = { Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL };
		for (Level level : validLevels) {
			assertDoesNotThrow(() -> LoggingUtils.disableConsole(level));
		}
		
		assertDoesNotThrow(() -> LoggingUtils.disableConsole());
	}
	
	@Test
	void disableFile() {
		assertThrows(NullPointerException.class, () -> LoggingUtils.disableFile(null));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.disableFile(Level.ALL));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.disableFile(Level.OFF));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.disableFile(Level.TRACE));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.disableFile(Level.WARN));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.disableFile(Level.FATAL));
		
		Level[] validLevels = { Level.DEBUG, Level.INFO, Level.ERROR };
		for (Level level : validLevels) {
			assertDoesNotThrow(() -> LoggingUtils.disableFile(level));
		}
		
		assertDoesNotThrow(() -> LoggingUtils.disableFile());
	}
	
	@Test
	void createReferenceMarker() {
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.createRefrenceMarker(0));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.createRefrenceMarker(-1));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.createRefrenceMarker(33));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.createRefrenceMarker(100));
		
		for (int i = 1; i <= 32; i++) {
			int finalI = i;
			Marker marker = assertDoesNotThrow(() -> LoggingUtils.createRefrenceMarker(finalI));
			assertNotNull(marker);
			assertEquals(i, marker.getName().length());
			
			assertTrue(marker.getName().matches("[0-9a-fA-F]+"));
		}
		
		Marker marker1 = LoggingUtils.createRefrenceMarker(10);
		Marker marker2 = LoggingUtils.createRefrenceMarker(10);
		assertNotEquals(marker1.getName(), marker2.getName());
	}
	
	@Test
	void createReferenceMarkerBoundaryValues() {
		Marker marker1 = LoggingUtils.createRefrenceMarker(1);
		assertEquals(1, marker1.getName().length());
		
		Marker marker32 = LoggingUtils.createRefrenceMarker(32);
		assertEquals(32, marker32.getName().length());
		
		Marker marker8 = LoggingUtils.createRefrenceMarker(8);
		assertEquals(8, marker8.getName().length());
		
		Marker marker16 = LoggingUtils.createRefrenceMarker(16);
		assertEquals(16, marker16.getName().length());
	}
	
	@Test
	void checkLevel() {
		assertThrows(NullPointerException.class, () -> LoggingUtils.checkLevel(null, null));
		assertThrows(NullPointerException.class, () -> LoggingUtils.checkLevel(LoggingType.CONSOLE, null));
		assertThrows(NullPointerException.class, () -> LoggingUtils.checkLevel(null, Level.INFO));
		
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.checkLevel(LoggingType.CONSOLE, Level.ALL));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.checkLevel(LoggingType.CONSOLE, Level.OFF));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.checkLevel(LoggingType.FILE, Level.ALL));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.checkLevel(LoggingType.FILE, Level.OFF));
		
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.checkLevel(LoggingType.FILE, Level.TRACE));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.checkLevel(LoggingType.FILE, Level.WARN));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.checkLevel(LoggingType.FILE, Level.FATAL));
		
		Level[] consoleLevels = { Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL };
		for (Level level : consoleLevels) {
			assertDoesNotThrow(() -> LoggingUtils.checkLevel(LoggingType.CONSOLE, level));
		}
		
		Level[] fileLevels = { Level.DEBUG, Level.INFO, Level.ERROR };
		for (Level level : fileLevels) {
			assertDoesNotThrow(() -> LoggingUtils.checkLevel(LoggingType.FILE, level));
		}
	}
	
	@Test
	void getLogger() {
		assertThrows(NullPointerException.class, () -> LoggingUtils.getLogger(null, null));
		assertThrows(NullPointerException.class, () -> LoggingUtils.getLogger(LoggingType.CONSOLE, null));
		assertThrows(NullPointerException.class, () -> LoggingUtils.getLogger(null, Level.INFO));
		
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.getLogger(LoggingType.CONSOLE, Level.ALL));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.getLogger(LoggingType.CONSOLE, Level.OFF));
		assertThrows(IllegalArgumentException.class, () -> LoggingUtils.getLogger(LoggingType.FILE, Level.TRACE));
		
		assertEquals("ConsoleInfo", LoggingUtils.getLogger(LoggingType.CONSOLE, Level.INFO));
		assertEquals("ConsoleError", LoggingUtils.getLogger(LoggingType.CONSOLE, Level.ERROR));
		assertEquals("FileDebug", LoggingUtils.getLogger(LoggingType.FILE, Level.DEBUG));
		assertEquals("FileInfo", LoggingUtils.getLogger(LoggingType.FILE, Level.INFO));
		assertEquals("FileError", LoggingUtils.getLogger(LoggingType.FILE, Level.ERROR));
		
		String[] expectedConsole = { "ConsoleTrace", "ConsoleDebug", "ConsoleInfo", "ConsoleWarn", "ConsoleError", "ConsoleFatal" };
		Level[] consoleLevels = { Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL };
		for (int i = 0; i < consoleLevels.length; i++) {
			assertEquals(expectedConsole[i], LoggingUtils.getLogger(LoggingType.CONSOLE, consoleLevels[i]));
		}
		
		String[] expectedFile = { "FileDebug", "FileInfo", "FileError" };
		Level[] fileLevels = { Level.DEBUG, Level.INFO, Level.ERROR };
		for (int i = 0; i < fileLevels.length; i++) {
			assertEquals(expectedFile[i], LoggingUtils.getLogger(LoggingType.FILE, fileLevels[i]));
		}
	}
}
