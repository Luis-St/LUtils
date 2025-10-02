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

import com.google.common.collect.Lists;
import net.luis.utils.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LoggerConfiguration}.<br>
 *
 * @author Luis-St
 */
class LoggerConfigurationTest {
	
	@AfterAll
	static void cleanUp() throws Exception {
		FileUtils.deleteRecursively(Path.of("logs/test-logs"));
	}
	
	@Test
	void constructorWithVarArgs() {
		assertThrows(NullPointerException.class, () -> new LoggerConfiguration((String[]) null));
		assertThrows(IllegalArgumentException.class, LoggerConfiguration::new);
		assertThrows(IllegalArgumentException.class, () -> new LoggerConfiguration((String) null));
		assertThrows(IllegalArgumentException.class, () -> new LoggerConfiguration(""));
		assertThrows(IllegalArgumentException.class, () -> new LoggerConfiguration("   "));
		assertThrows(IllegalArgumentException.class, () -> new LoggerConfiguration("", "  ", null));
		
		assertDoesNotThrow(() -> new LoggerConfiguration("*"));
		assertDoesNotThrow(() -> new LoggerConfiguration("test"));
		assertDoesNotThrow(() -> new LoggerConfiguration("com.example"));
		assertDoesNotThrow(() -> new LoggerConfiguration("test", "com.example"));
		
		LoggerConfiguration config = new LoggerConfiguration("test", "*", "com.example");
		assertDoesNotThrow(config::build);
	}
	
	@Test
	void constructorWithList() {
		assertThrows(NullPointerException.class, () -> new LoggerConfiguration((List<String>) null));
		assertThrows(IllegalArgumentException.class, () -> new LoggerConfiguration(Collections.emptyList()));
		assertThrows(IllegalArgumentException.class, () -> new LoggerConfiguration(Lists.newArrayList("")));
		assertThrows(IllegalArgumentException.class, () -> new LoggerConfiguration(Lists.newArrayList("   ")));
		assertThrows(IllegalArgumentException.class, () -> new LoggerConfiguration(Lists.newArrayList("", "  ", null)));
		
		assertDoesNotThrow(() -> new LoggerConfiguration(Lists.newArrayList("*")));
		assertDoesNotThrow(() -> new LoggerConfiguration(Lists.newArrayList("test")));
		assertDoesNotThrow(() -> new LoggerConfiguration(Lists.newArrayList("com.example")));
		assertDoesNotThrow(() -> new LoggerConfiguration(Lists.newArrayList("test", "com.example")));
		
		LoggerConfiguration config = new LoggerConfiguration(Lists.newArrayList("test", "*", "com.example"));
		assertDoesNotThrow(config::build);
	}
	
	@Test
	void setStatusLevel() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.setStatusLevel(null));
		
		for (Level level : Level.values()) {
			assertSame(config, config.setStatusLevel(level));
		}
		
		LoggerConfiguration errorConfig = new LoggerConfiguration("*").setStatusLevel(Level.ERROR);
		assertDoesNotThrow(errorConfig::build);
	}
	
	@Test
	void enableLogging() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.enableLogging(null));
		
		for (LoggingType type : LoggingType.values()) {
			assertSame(config, config.enableLogging(type));
		}
		
		assertSame(config, config.enableLogging(LoggingType.CONSOLE));
	}
	
	@Test
	void disableLogging() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertDoesNotThrow(() -> config.disableLogging(null));
		
		for (LoggingType type : LoggingType.values()) {
			assertSame(config, config.disableLogging(type));
		}
		
		config.disableLogging(LoggingType.CONSOLE);
		assertSame(config, config.disableLogging(LoggingType.CONSOLE));
	}
	
	@Test
	void disableAllLoggingTypes() {
		LoggerConfiguration config = new LoggerConfiguration("*")
			.disableLogging(LoggingType.CONSOLE)
			.disableLogging(LoggingType.FILE);
		assertDoesNotThrow(config::build);
	}
	
	@Test
	void overridePattern() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overridePattern(null, null, null));
		assertThrows(NullPointerException.class, () -> config.overridePattern(LoggingType.CONSOLE, Level.INFO, null));
		assertThrows(NullPointerException.class, () -> config.overridePattern(LoggingType.CONSOLE, null, "%msg"));
		assertThrows(NullPointerException.class, () -> config.overridePattern(null, Level.INFO, "%msg"));
		
		assertThrows(IllegalArgumentException.class, () -> config.overridePattern(LoggingType.CONSOLE, Level.INFO, ""));
		assertThrows(IllegalArgumentException.class, () -> config.overridePattern(LoggingType.CONSOLE, Level.INFO, "   "));
		assertThrows(IllegalArgumentException.class, () -> config.overridePattern(LoggingType.CONSOLE, Level.INFO, "No message placeholder"));
		
		String[] validPatterns = { "%msg", "%message", "%m", "%throwable", "%ex", "%exception", "[%level] %msg%n", "%d{HH:mm:ss} %msg %throwable" };
		
		for (LoggingType type : LoggingType.values()) {
			for (String pattern : validPatterns) {
				assertSame(config, config.overridePattern(type, Level.INFO, pattern));
			}
		}
		
		assertSame(config, config.overridePattern(LoggingType.CONSOLE, Level.ALL, "%msg"));
		assertSame(config, config.overridePattern(LoggingType.CONSOLE, Level.OFF, "%msg"));
	}
	
	@Test
	void overrideConsolePattern() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overrideConsolePattern(null, null));
		assertThrows(NullPointerException.class, () -> config.overrideConsolePattern(Level.INFO, null));
		assertThrows(NullPointerException.class, () -> config.overrideConsolePattern(null, "%msg"));
		
		assertThrows(IllegalArgumentException.class, () -> config.overrideConsolePattern(Level.INFO, ""));
		assertThrows(IllegalArgumentException.class, () -> config.overrideConsolePattern(Level.INFO, "Invalid pattern"));
		
		for (Level level : Level.values()) {
			assertSame(config, config.overrideConsolePattern(level, "%msg"));
		}
	}
	
	@Test
	void overrideFilePattern() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overrideFilePattern(null, null));
		assertThrows(NullPointerException.class, () -> config.overrideFilePattern(Level.INFO, null));
		assertThrows(NullPointerException.class, () -> config.overrideFilePattern(null, "%msg"));
		
		assertThrows(IllegalArgumentException.class, () -> config.overrideFilePattern(Level.INFO, ""));
		assertThrows(IllegalArgumentException.class, () -> config.overrideFilePattern(Level.INFO, "Invalid pattern"));
		
		for (Level level : Level.values()) {
			assertSame(config, config.overrideFilePattern(level, "%msg"));
		}
	}
	
	@Test
	void setRootDirectory() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.setRootDirectory(null));
		assertThrows(IllegalArgumentException.class, () -> config.setRootDirectory(""));
		assertThrows(IllegalArgumentException.class, () -> config.setRootDirectory("   "));
		assertThrows(IllegalArgumentException.class, () -> config.setRootDirectory("test"));
		assertThrows(IllegalArgumentException.class, () -> config.setRootDirectory("test/"));
		assertThrows(IllegalArgumentException.class, () -> config.setRootDirectory("folder/subfolder"));
		
		assertSame(config, config.setRootDirectory("./"));
		assertSame(config, config.setRootDirectory("./test"));
		assertSame(config, config.setRootDirectory("./test/"));
		assertSame(config, config.setRootDirectory("C:/test"));
		assertSame(config, config.setRootDirectory("D:\\test\\"));
		assertSame(config, config.setRootDirectory("/absolute/path"));
		
		LoggerConfiguration testConfig = new LoggerConfiguration("*").setRootDirectory("./test\\folder/");
		assertDoesNotThrow(testConfig::build);
	}
	
	@Test
	void overrideLog() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overrideLog(null, null, null));
		assertThrows(NullPointerException.class, () -> config.overrideLog(Level.DEBUG, null, "archive.log"));
		assertThrows(NullPointerException.class, () -> config.overrideLog(Level.DEBUG, "test.log", null));
		assertThrows(NullPointerException.class, () -> config.overrideLog(null, "test.log", "archive.log"));
		
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.OFF, "test.log", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.ALL, "test.log", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.TRACE, "test.log", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.WARN, "test.log", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.FATAL, "test.log", "archive.log"));
		
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.DEBUG, "", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.DEBUG, "test.log", ""));
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.DEBUG, "   ", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.DEBUG, "test.log", "   "));
		
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.DEBUG, "C:/test.log", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.DEBUG, "./test.log", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.DEBUG, "test.log", "C:/archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideLog(Level.DEBUG, "test.log", "./archive.log"));
		
		Level[] validLevels = { Level.DEBUG, Level.INFO, Level.ERROR };
		for (Level level : validLevels) {
			assertSame(config, config.overrideLog(level, "test.log", "archive.log"));
			assertSame(config, config.overrideLog(level, "subfolder/test.log", "subfolder/archive.log"));
			assertSame(config, config.overrideLog(level, "test.log", "archive.log"));
		}
	}
	
	@Test
	void overrideDebugLog() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overrideDebugLog(null, null));
		assertThrows(NullPointerException.class, () -> config.overrideDebugLog(null, "archive.log"));
		assertThrows(NullPointerException.class, () -> config.overrideDebugLog("debug.log", null));
		
		assertThrows(IllegalArgumentException.class, () -> config.overrideDebugLog("", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideDebugLog("debug.log", ""));
		assertThrows(IllegalArgumentException.class, () -> config.overrideDebugLog("./debug.log", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideDebugLog("debug.log", "./archive.log"));
		
		assertSame(config, config.overrideDebugLog("debug.log", "debug-archive.log"));
		assertSame(config, config.overrideDebugLog("logs/debug.log", "logs/debug-archive.log"));
	}
	
	@Test
	void overrideInfoLog() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overrideInfoLog(null, null));
		assertThrows(NullPointerException.class, () -> config.overrideInfoLog(null, "archive.log"));
		assertThrows(NullPointerException.class, () -> config.overrideInfoLog("info.log", null));
		
		assertThrows(IllegalArgumentException.class, () -> config.overrideInfoLog("", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideInfoLog("info.log", ""));
		assertThrows(IllegalArgumentException.class, () -> config.overrideInfoLog("./info.log", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideInfoLog("info.log", "./archive.log"));
		
		assertSame(config, config.overrideInfoLog("info.log", "info-archive.log"));
		assertSame(config, config.overrideInfoLog("logs/info.log", "logs/info-archive.log"));
	}
	
	@Test
	void overrideErrorLog() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.overrideErrorLog(null, null));
		assertThrows(NullPointerException.class, () -> config.overrideErrorLog(null, "archive.log"));
		assertThrows(NullPointerException.class, () -> config.overrideErrorLog("error.log", null));
		
		assertThrows(IllegalArgumentException.class, () -> config.overrideErrorLog("", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideErrorLog("error.log", ""));
		assertThrows(IllegalArgumentException.class, () -> config.overrideErrorLog("./error.log", "archive.log"));
		assertThrows(IllegalArgumentException.class, () -> config.overrideErrorLog("error.log", "./archive.log"));
		
		assertSame(config, config.overrideErrorLog("error.log", "error-archive.log"));
		assertSame(config, config.overrideErrorLog("logs/error.log", "logs/error-archive.log"));
	}
	
	@Test
	void addDefaultLogger() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.addDefaultLogger(null, null));
		assertThrows(NullPointerException.class, () -> config.addDefaultLogger(null, Level.INFO));
		assertThrows(NullPointerException.class, () -> config.addDefaultLogger(LoggingType.CONSOLE, null));
		
		assertThrows(IllegalArgumentException.class, () -> config.addDefaultLogger(LoggingType.CONSOLE, Level.OFF));
		assertThrows(IllegalArgumentException.class, () -> config.addDefaultLogger(LoggingType.CONSOLE, Level.ALL));
		assertThrows(IllegalArgumentException.class, () -> config.addDefaultLogger(LoggingType.FILE, Level.OFF));
		assertThrows(IllegalArgumentException.class, () -> config.addDefaultLogger(LoggingType.FILE, Level.ALL));
		
		assertThrows(IllegalArgumentException.class, () -> config.addDefaultLogger(LoggingType.FILE, Level.TRACE));
		assertThrows(IllegalArgumentException.class, () -> config.addDefaultLogger(LoggingType.FILE, Level.WARN));
		assertThrows(IllegalArgumentException.class, () -> config.addDefaultLogger(LoggingType.FILE, Level.FATAL));
		
		Level[] consoleLevels = { Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL };
		for (Level level : consoleLevels) {
			assertSame(config, config.addDefaultLogger(LoggingType.CONSOLE, level));
		}
		
		Level[] fileLevels = { Level.DEBUG, Level.INFO, Level.ERROR };
		for (Level level : fileLevels) {
			assertSame(config, config.addDefaultLogger(LoggingType.FILE, level));
		}
		
		LoggerConfiguration disabledFileConfig = new LoggerConfiguration("*").disableLogging(LoggingType.FILE);
		assertThrows(IllegalArgumentException.class, () -> disabledFileConfig.addDefaultLogger(LoggingType.FILE, Level.INFO));
	}
	
	@Test
	void removeDefaultLogger() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertDoesNotThrow(() -> config.removeDefaultLogger(null, null));
		assertDoesNotThrow(() -> config.removeDefaultLogger(null, Level.INFO));
		assertDoesNotThrow(() -> config.removeDefaultLogger(LoggingType.CONSOLE, null));
		
		for (LoggingType type : LoggingType.values()) {
			for (Level level : Level.values()) {
				assertSame(config, config.removeDefaultLogger(type, level));
			}
		}
		
		assertSame(config, config.removeDefaultLogger(LoggingType.CONSOLE, Level.TRACE));
		
		config.addDefaultLogger(LoggingType.CONSOLE, Level.DEBUG);
		assertSame(config, config.removeDefaultLogger(LoggingType.CONSOLE, Level.DEBUG));
	}
	
	@Test
	void setFileSize() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.setFileSize(null));
		assertThrows(IllegalArgumentException.class, () -> config.setFileSize(""));
		assertThrows(IllegalArgumentException.class, () -> config.setFileSize("   "));
		assertThrows(IllegalArgumentException.class, () -> config.setFileSize("invalid"));
		assertThrows(IllegalArgumentException.class, () -> config.setFileSize("10XX"));
		assertThrows(IllegalArgumentException.class, () -> config.setFileSize("MB10"));
		
		String[] validSizes = { "1", "10", "1B", "1KB", "1MB", "1GB", "1TB", "10.5MB", "1,5GB", "100 KB" };
		for (String size : validSizes) {
			assertSame(config, config.setFileSize(size));
		}
		
		assertSame(config, config.setFileSize("10mb"));
		assertSame(config, config.setFileSize("10Mb"));
		assertSame(config, config.setFileSize("10mB"));
	}
	
	@Test
	void setArchiveType() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		assertThrows(NullPointerException.class, () -> config.setArchiveType(null));
		assertThrows(IllegalArgumentException.class, () -> config.setArchiveType(""));
		assertThrows(IllegalArgumentException.class, () -> config.setArchiveType("   "));
		assertThrows(IllegalArgumentException.class, () -> config.setArchiveType("log"));
		assertThrows(IllegalArgumentException.class, () -> config.setArchiveType(".tar"));
		assertThrows(IllegalArgumentException.class, () -> config.setArchiveType("rar"));
		
		String[] validTypes = { ".zip", ".gz", ".bz2", ".xy", "zip", "gz", "bz2", "xy" };
		for (String type : validTypes) {
			assertSame(config, config.setArchiveType(type));
		}
		
		assertSame(config, config.setArchiveType(".ZIP"));
		assertSame(config, config.setArchiveType(".Gz"));
		assertSame(config, config.setArchiveType("BZ2"));
	}
	
	@Test
	void setCompressionLevel() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		assertSame(config, config.setCompressionLevel(-5));
		assertSame(config, config.setCompressionLevel(0));
		assertSame(config, config.setCompressionLevel(5));
		assertSame(config, config.setCompressionLevel(9));
		assertSame(config, config.setCompressionLevel(15));
		
		for (int i = 0; i <= 9; i++) {
			assertSame(config, config.setCompressionLevel(i));
		}
	}
	
	@Test
	void setMaxArchiveFiles() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		assertSame(config, config.setMaxArchiveFiles(-5));
		assertSame(config, config.setMaxArchiveFiles(0));
		assertSame(config, config.setMaxArchiveFiles(1));
		assertSame(config, config.setMaxArchiveFiles(100));
		
		assertSame(config, config.setMaxArchiveFiles(Integer.MAX_VALUE));
	}
	
	@Test
	void setArchiveAutoDeletionDepth() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		assertSame(config, config.setArchiveAutoDeletionDepth(-5));
		assertSame(config, config.setArchiveAutoDeletionDepth(0));
		assertSame(config, config.setArchiveAutoDeletionDepth(1));
		assertSame(config, config.setArchiveAutoDeletionDepth(10));
		
		assertSame(config, config.setArchiveAutoDeletionDepth(Integer.MAX_VALUE));
	}
	
	@Test
	void setArchiveAutoDeletionAge() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		assertSame(config, config.setArchiveAutoDeletionAge(-5));
		assertSame(config, config.setArchiveAutoDeletionAge(0));
		assertSame(config, config.setArchiveAutoDeletionAge(1));
		assertSame(config, config.setArchiveAutoDeletionAge(30));
		assertSame(config, config.setArchiveAutoDeletionAge(365));
		
		assertSame(config, config.setArchiveAutoDeletionAge(Integer.MAX_VALUE));
	}
	
	@Test
	void build() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
		assertEquals("RuntimeConfiguration", result.getName());
	}
	
	@Test
	void buildWithComplexConfiguration() {
		LoggerConfiguration config = new LoggerConfiguration("com.example", "org.test")
			.setStatusLevel(Level.WARN)
			.enableLogging(LoggingType.CONSOLE)
			.enableLogging(LoggingType.FILE)
			.setRootDirectory("./logs/")
			.setFileSize("50MB")
			.setArchiveType(".gz")
			.setCompressionLevel(6)
			.setMaxArchiveFiles(20)
			.addDefaultLogger(LoggingType.CONSOLE, Level.INFO)
			.addDefaultLogger(LoggingType.CONSOLE, Level.ERROR)
			.addDefaultLogger(LoggingType.FILE, Level.ERROR)
			.overrideConsolePattern(Level.INFO, "[%level] %msg%n")
			.overrideLog(Level.ERROR, "errors.log", "errors-%d{yyyy-MM-dd}-%i.log");
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
		assertEquals("RuntimeConfiguration", result.getName());
	}
	
	@Test
	void buildWithDisabledLogging() {
		LoggerConfiguration config = new LoggerConfiguration("*")
			.disableLogging(LoggingType.CONSOLE)
			.disableLogging(LoggingType.FILE);
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
	}
	
	@Test
	void buildWithWildcardLogger() {
		LoggerConfiguration config = new LoggerConfiguration("com.example", "*", "org.test");
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
	}
	
	@Test
	void chainedConfiguration() {
		LoggerConfiguration config = new LoggerConfiguration("*")
			.setStatusLevel(Level.DEBUG)
			.enableLogging(LoggingType.CONSOLE)
			.enableLogging(LoggingType.FILE)
			.setRootDirectory("./logs/test-logs/")
			.setFileSize("10MB")
			.setArchiveType(".zip")
			.setCompressionLevel(9)
			.setMaxArchiveFiles(5)
			.addDefaultLogger(LoggingType.CONSOLE, Level.INFO)
			.addDefaultLogger(LoggingType.FILE, Level.ERROR)
			.overrideConsolePattern(Level.ALL, "%d{HH:mm:ss} [%level] %msg%n")
			.overrideFilePattern(Level.ERROR, "%d{yyyy-MM-dd HH:mm:ss} [%level] %C{1} - %msg%n%throwable")
			.overrideDebugLog("debug.log", "debug-%d{yyyy-MM-dd}-%i.log")
			.overrideInfoLog("info.log", "info-%d{yyyy-MM-dd}-%i.log")
			.overrideErrorLog("error.log", "error-%d{yyyy-MM-dd}-%i.log");
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
	}
	
	@Test
	void multiplePatternOverrides() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		config.overrideConsolePattern(Level.DEBUG, "[DEBUG] %msg%n")
			.overrideConsolePattern(Level.INFO, "[INFO] %msg%n")
			.overrideConsolePattern(Level.ERROR, "[ERROR] %msg%n%throwable");
		
		config.overrideFilePattern(Level.ALL, "%d %level %msg%n")
			.overrideFilePattern(Level.ERROR, "%d [%level] %C - %msg%n%ex");
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
	}
	
	@Test
	void patternOverrideClearance() {
		LoggerConfiguration config = new LoggerConfiguration("*");
		
		config.overrideConsolePattern(Level.DEBUG, "[DEBUG] %msg%n")
			.overrideConsolePattern(Level.INFO, "[INFO] %msg%n");
		
		config.overrideConsolePattern(Level.OFF, "%msg");
		
		Configuration result = assertDoesNotThrow(config::build);
		assertNotNull(result);
	}
}
