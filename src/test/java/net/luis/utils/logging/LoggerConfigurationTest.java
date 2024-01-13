package net.luis.utils.logging;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("DataFlowIssue")
class LoggerConfigurationTest {
	
	private final LoggerConfiguration configuration = new LoggerConfiguration("*");
	
	@Test
	void setStatusLevel() {
		assertThrows(NullPointerException.class, () -> this.configuration.setStatusLevel(null));
		assertDoesNotThrow(() -> this.configuration.setStatusLevel(Level.ERROR));
	}
	
	@Test
	void enableLogging() {
		assertThrows(NullPointerException.class, () -> this.configuration.enableLogging(null));
		assertDoesNotThrow(() -> this.configuration.enableLogging(LoggingType.FILE));
		assertDoesNotThrow(() -> this.configuration.enableLogging(LoggingType.CONSOLE));
	}
	
	@Test
	void disableLogging() {
		assertDoesNotThrow(() -> this.configuration.disableLogging(LoggingType.FILE));
		assertDoesNotThrow(() -> this.configuration.disableLogging(LoggingType.CONSOLE));
	}
	
	@Test
	void overridePattern() {
		assertThrows(NullPointerException.class, () -> this.configuration.overridePattern(null, Level.ALL, ""));
		assertThrows(NullPointerException.class, () -> this.configuration.overridePattern(LoggingType.CONSOLE, null, ""));
		assertThrows(NullPointerException.class, () -> this.configuration.overridePattern(LoggingType.CONSOLE, Level.ALL, null));
		for (LoggingType type : LoggingType.values()) {
			for (Level level : Level.values()) {
				assertDoesNotThrow(() -> this.configuration.overridePattern(type, level, ""));
			}
		}
	}
	
	@Test
	void overrideLog() {
		assertThrows(NullPointerException.class, () -> this.configuration.overrideLog(Level.DEBUG, null, ""));
		assertThrows(NullPointerException.class, () -> this.configuration.overrideLog(Level.DEBUG, "", null));
		assertDoesNotThrow(() -> this.configuration.overrideLog(Level.DEBUG, "", ""));
		assertThrows(IllegalArgumentException.class, () -> this.configuration.overrideLog(Level.OFF, "", ""));
		assertThrows(IllegalArgumentException.class, () -> this.configuration.overrideLog(Level.TRACE, "", ""));
		assertThrows(IllegalArgumentException.class, () -> this.configuration.overrideLog(Level.WARN, "", ""));
		assertThrows(IllegalArgumentException.class, () -> this.configuration.overrideLog(Level.FATAL, "", ""));
		assertDoesNotThrow(() -> this.configuration.overrideLog(Level.DEBUG, "", ""));
		assertDoesNotThrow(() -> this.configuration.overrideLog(Level.INFO, "", ""));
		assertDoesNotThrow(() -> this.configuration.overrideLog(Level.ERROR, "", ""));
	}
	
	@Test
	void addDefaultLogger() {
		assertThrows(NullPointerException.class, () -> this.configuration.addDefaultLogger(null, Level.INFO));
		assertThrows(NullPointerException.class, () -> this.configuration.addDefaultLogger(LoggingType.CONSOLE, null));
		assertThrows(IllegalArgumentException.class, () -> this.configuration.addDefaultLogger(LoggingType.CONSOLE, Level.OFF));
		assertThrows(IllegalArgumentException.class, () -> this.configuration.addDefaultLogger(LoggingType.CONSOLE, Level.ALL));
		assertThrows(IllegalArgumentException.class, () -> this.configuration.addDefaultLogger(LoggingType.FILE, Level.OFF));
		assertThrows(IllegalArgumentException.class, () -> this.configuration.addDefaultLogger(LoggingType.FILE, Level.TRACE));
		assertThrows(IllegalArgumentException.class, () -> this.configuration.addDefaultLogger(LoggingType.FILE, Level.WARN));
		assertThrows(IllegalArgumentException.class, () -> this.configuration.addDefaultLogger(LoggingType.FILE, Level.FATAL));
		assertThrows(IllegalArgumentException.class, () -> this.configuration.addDefaultLogger(LoggingType.FILE, Level.ALL));
		for (Level level : new Level[] {Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL}) {
			assertDoesNotThrow(() -> this.configuration.addDefaultLogger(LoggingType.CONSOLE, level));
		}
		for (Level level : new Level[] {Level.DEBUG, Level.INFO, Level.ERROR}) {
			assertDoesNotThrow(() -> this.configuration.addDefaultLogger(LoggingType.FILE, level));
		}
	}
	
	@Test
	void removeDefaultLogger() {
		assertDoesNotThrow(() -> this.configuration.removeDefaultLogger(null, null));
		assertDoesNotThrow(() -> this.configuration.removeDefaultLogger(LoggingType.CONSOLE, null));
		assertDoesNotThrow(() -> this.configuration.removeDefaultLogger(LoggingType.FILE, null));
		for (Level level : new Level[] {Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL}) {
			assertDoesNotThrow(() -> this.configuration.removeDefaultLogger(LoggingType.CONSOLE, level));
		}
		for (Level level : new Level[] {Level.DEBUG, Level.INFO, Level.ERROR}) {
			assertDoesNotThrow(() -> this.configuration.removeDefaultLogger(LoggingType.FILE, level));
		}
	}
	
	@Test
	void build() {
		assertDoesNotThrow(this.configuration::build);
	}
}