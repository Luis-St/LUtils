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

package net.luis.utils.logging;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LoggerConfiguration}.<br>
 *
 * @author Luis-St
 */
class LoggerConfigurationTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new LoggerConfiguration((String[]) null));
		assertThrows(IllegalArgumentException.class, LoggerConfiguration::new);
		assertThrows(IllegalArgumentException.class, () -> new LoggerConfiguration((String) null));
		assertThrows(NullPointerException.class, () -> new LoggerConfiguration((List<String>) null));
		assertThrows(IllegalArgumentException.class, () -> new LoggerConfiguration(Lists.newArrayList()));
		assertThrows(IllegalArgumentException.class, () -> new LoggerConfiguration(Lists.newArrayList("")));
		assertThrows(IllegalArgumentException.class, () -> new LoggerConfiguration(Lists.newArrayList(" ")));
		assertDoesNotThrow(() -> new LoggerConfiguration("*"));
		assertDoesNotThrow(() -> new LoggerConfiguration("test"));
		assertDoesNotThrow(() -> new LoggerConfiguration(Lists.newArrayList("*")));
		assertDoesNotThrow(() -> new LoggerConfiguration(Lists.newArrayList("test")));
	}
	
	@Test
	void setStatusLevel() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.setStatusLevel(null));
		for (Level level : Level.values()) {
			assertSame(config, assertDoesNotThrow(() -> config.setStatusLevel(level)));
		}
	}
	
	@Test
	void enableLogging() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.enableLogging(null));
		for (LoggingType type : LoggingType.values()) {
			assertSame(config, assertDoesNotThrow(() -> config.enableLogging(type)));
		}
	}
	
	@Test
	void disableLogging() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertDoesNotThrow(() -> config.disableLogging(null));
		for (LoggingType type : LoggingType.values()) {
			assertSame(config, assertDoesNotThrow(() -> config.disableLogging(type)));
		}
	}
	
	@Test
	void overridePattern() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overridePattern(null, null, null));
		assertThrows(NullPointerException.class, () -> config.overridePattern(LoggingType.CONSOLE, Level.ALL, null));
		for (LoggingType type : LoggingType.values()) {
			assertThrows(NullPointerException.class, () -> config.overridePattern(type, null, "%msg"));
			for (Level level : Level.values()) {
				assertThrows(NullPointerException.class, () -> config.overridePattern(null, level, ""));
				assertThrows(IllegalArgumentException.class, () -> config.overridePattern(type, level, ""));
				assertThrows(IllegalArgumentException.class, () -> config.overridePattern(type, level, "No message"));
				assertSame(config, assertDoesNotThrow(() -> config.overridePattern(type, level, "%msg")));
			}
		}
	}
	
	@Test
	void overrideConsolePattern() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overrideConsolePattern(null, null));
		assertThrows(NullPointerException.class, () -> config.overrideConsolePattern(null, "%msg"));
		for (Level level : Level.values()) {
			assertThrows(NullPointerException.class, () -> config.overrideConsolePattern(level, null));
			assertThrows(IllegalArgumentException.class, () -> config.overrideConsolePattern(level, ""));
			assertSame(config, assertDoesNotThrow(() -> config.overrideConsolePattern(level, "%msg")));
		}
	}
	
	@Test
	void overrideFilePattern() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overrideFilePattern(null, null));
		assertThrows(NullPointerException.class, () -> config.overrideFilePattern(null, "%msg"));
		for (Level level : Level.values()) {
			assertThrows(NullPointerException.class, () -> config.overrideFilePattern(level, null));
			assertThrows(IllegalArgumentException.class, () -> config.overrideFilePattern(level, ""));
			assertSame(config, assertDoesNotThrow(() -> config.overrideFilePattern(level, "%msg")));
		}
	}
	
	@Test
	void setRootDirectory() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.setRootDirectory(null));
		assertThrows(IllegalArgumentException.class, () -> config.setRootDirectory(""));
		assertThrows(IllegalArgumentException.class, () -> config.setRootDirectory(" "));
		assertThrows(IllegalArgumentException.class, () -> config.setRootDirectory("test"));
		assertThrows(IllegalArgumentException.class, () -> config.setRootDirectory("/test"));
		assertThrows(IllegalArgumentException.class, () -> config.setRootDirectory("test/"));
		assertSame(config, assertDoesNotThrow(() -> config.setRootDirectory("./test")));
		assertSame(config, assertDoesNotThrow(() -> config.setRootDirectory("D:/test")));
	}
	
	@Test
	void overrideLog() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overrideLog(null, null, null));
		assertThrows(NullPointerException.class, () -> config.overrideLog(null, "", ""));
		assertThrows(NullPointerException.class, () -> config.overrideLog(Level.DEBUG, null, ""));
		assertThrows(NullPointerException.class, () -> config.overrideLog(Level.DEBUG, "", null));
		for (Level level : new Level[] { Level.OFF, Level.TRACE, Level.FATAL, Level.ALL }) {
			assertThrows(IllegalArgumentException.class, () -> config.overrideLog(level, "", ""));
		}
		for (Level level : new Level[] { Level.DEBUG, Level.INFO, Level.ERROR }) {
			assertThrows(IllegalArgumentException.class, () -> config.overrideLog(level, "", "test.log"));
			assertThrows(IllegalArgumentException.class, () -> config.overrideLog(level, "test.log", ""));
			assertThrows(IllegalArgumentException.class, () -> config.overrideLog(level, "D:/test.log", "D:/test.log"));
			assertThrows(IllegalArgumentException.class, () -> config.overrideLog(level, "test.log", "D:/test.log"));
			assertThrows(IllegalArgumentException.class, () -> config.overrideLog(level, "D:/test.log", "test.log"));
			assertThrows(IllegalArgumentException.class, () -> config.overrideLog(level, "./test.log", "./test.log"));
			assertThrows(IllegalArgumentException.class, () -> config.overrideLog(level, "test.log", "./test.log"));
			assertThrows(IllegalArgumentException.class, () -> config.overrideLog(level, "./test.log", "test.log"));
			assertSame(config, assertDoesNotThrow(() -> config.overrideLog(level, "/test.log", "/test.log")));
		}
	}
	
	@Test
	void overrideDebugLog() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overrideDebugLog(null, null));
		assertThrows(NullPointerException.class, () -> config.overrideDebugLog(null, ""));
		assertThrows(NullPointerException.class, () -> config.overrideDebugLog("", null));
		assertThrows(IllegalArgumentException.class, () -> config.overrideDebugLog("", "test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideDebugLog("test.log", ""));
		assertThrows(IllegalArgumentException.class, () -> config.overrideDebugLog("D:/test.log", "D:/test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideDebugLog("test.log", "D:/test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideDebugLog("D:/test.log", "test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideDebugLog("./test.log", "./test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideDebugLog("test.log", "./test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideDebugLog("./test.log", "test.log"));
		assertSame(config, assertDoesNotThrow(() -> config.overrideDebugLog("/test.log", "/test.log")));
	}
	
	@Test
	void overrideInfoLog() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overrideInfoLog(null, null));
		assertThrows(NullPointerException.class, () -> config.overrideInfoLog(null, ""));
		assertThrows(NullPointerException.class, () -> config.overrideInfoLog("", null));
		assertThrows(IllegalArgumentException.class, () -> config.overrideInfoLog("", "test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideInfoLog("test.log", ""));
		assertThrows(IllegalArgumentException.class, () -> config.overrideInfoLog("D:/test.log", "D:/test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideInfoLog("test.log", "D:/test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideInfoLog("D:/test.log", "test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideInfoLog("./test.log", "./test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideInfoLog("test.log", "./test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideInfoLog("./test.log", "test.log"));
		assertSame(config, assertDoesNotThrow(() -> config.overrideInfoLog("/test.log", "/test.log")));
	}
	
	@Test
	void overrideErrorLog() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overrideErrorLog(null, null));
		assertThrows(NullPointerException.class, () -> config.overrideErrorLog(null, ""));
		assertThrows(NullPointerException.class, () -> config.overrideErrorLog("", null));
		assertThrows(IllegalArgumentException.class, () -> config.overrideErrorLog("", "test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideErrorLog("test.log", ""));
		assertThrows(IllegalArgumentException.class, () -> config.overrideErrorLog("D:/test.log", "D:/test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideErrorLog("test.log", "D:/test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideErrorLog("D:/test.log", "test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideErrorLog("./test.log", "./test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideErrorLog("test.log", "./test.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideErrorLog("./test.log", "test.log"));
		assertSame(config, assertDoesNotThrow(() -> config.overrideErrorLog("/test.log", "/test.log")));
	}
	
	@Test
	void addDefaultLogger() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.addDefaultLogger(null, null));
		assertThrows(NullPointerException.class, () -> config.addDefaultLogger(null, Level.ALL));
		assertThrows(NullPointerException.class, () -> config.addDefaultLogger(LoggingType.CONSOLE, null));
		for (LoggingType type : LoggingType.values()) {
			assertThrows(IllegalArgumentException.class, () -> config.addDefaultLogger(type, Level.OFF));
			assertThrows(IllegalArgumentException.class, () -> config.addDefaultLogger(type, Level.ALL));
		}
		assertThrows(IllegalArgumentException.class, () -> config.addDefaultLogger(LoggingType.FILE, Level.TRACE));
		assertThrows(IllegalArgumentException.class, () -> config.addDefaultLogger(LoggingType.FILE, Level.FATAL));
		for (Level level : new Level[] { Level.TRACE, Level.DEBUG, Level.INFO, Level.ERROR, Level.FATAL }) {
			assertSame(config, assertDoesNotThrow(() -> config.addDefaultLogger(LoggingType.CONSOLE, level)));
		}
		for (Level level : new Level[] { Level.DEBUG, Level.INFO, Level.ERROR }) {
			assertSame(config, assertDoesNotThrow(() -> config.addDefaultLogger(LoggingType.FILE, level)));
		}
	}
	
	@Test
	void removeDefaultLogger() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertDoesNotThrow(() -> config.removeDefaultLogger(null, null));
		assertDoesNotThrow(() -> config.removeDefaultLogger(null, Level.ALL));
		assertDoesNotThrow(() -> config.removeDefaultLogger(LoggingType.CONSOLE, null));
		for (LoggingType type : LoggingType.values()) {
			for (Level level : Level.values()) {
				assertSame(config, assertDoesNotThrow(() -> config.removeDefaultLogger(type, level)));
			}
		}
	}
	
	@Test
	void setFileSize() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.setFileSize(null));
		assertThrows(IllegalArgumentException.class, () -> config.setFileSize(""));
		assertThrows(IllegalArgumentException.class, () -> config.setFileSize(" "));
		assertThrows(IllegalArgumentException.class, () -> config.setFileSize("test"));
		assertDoesNotThrow(() -> config.setFileSize("1"));
		assertDoesNotThrow(() -> config.setFileSize("1B"));
		assertDoesNotThrow(() -> config.setFileSize("1KB"));
		assertDoesNotThrow(() -> config.setFileSize("1MB"));
		assertDoesNotThrow(() -> config.setFileSize("1GB"));
		assertDoesNotThrow(() -> config.setFileSize("1TB"));
	}
	
	@Test
	void setArchiveType() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.setArchiveType(null));
		assertThrows(IllegalArgumentException.class, () -> config.setArchiveType(""));
		assertThrows(IllegalArgumentException.class, () -> config.setArchiveType(" "));
		assertThrows(IllegalArgumentException.class, () -> config.setArchiveType(".log"));
		assertDoesNotThrow(() -> config.setArchiveType(".zip"));
		assertDoesNotThrow(() -> config.setArchiveType(".gz"));
		assertDoesNotThrow(() -> config.setArchiveType(".bz2"));
		assertDoesNotThrow(() -> config.setArchiveType(".xy"));
	}
	
	@Test
	void setCompressionLevel() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertDoesNotThrow(() -> config.setCompressionLevel(-1));
		assertDoesNotThrow(() -> config.setCompressionLevel(0));
		assertDoesNotThrow(() -> config.setCompressionLevel(1));
	}
	
	@Test
	void setMaxArchiveFiles() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertDoesNotThrow(() -> config.setMaxArchiveFiles(-1));
		assertDoesNotThrow(() -> config.setMaxArchiveFiles(0));
		assertDoesNotThrow(() -> config.setMaxArchiveFiles(1));
	}
	
	@Test
	void build() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertDoesNotThrow(config::build);
		assertNotNull(config.build());
	}
}
