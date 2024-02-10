package net.luis.utils.logging;

import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LoggingType}.<br>
 *
 * @author Luis-St
 */
class LoggingTypeTest {
	
	@Test
	void getAllowedLevels() {
		assertEquals(6, LoggingType.CONSOLE.getAllowedLevels().length);
		assertEquals(3, LoggingType.FILE.getAllowedLevels().length);
		assertArrayEquals(new Level[] {Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL}, LoggingType.CONSOLE.getAllowedLevels());
		assertArrayEquals(new Level[] {Level.DEBUG, Level.INFO, Level.ERROR}, LoggingType.FILE.getAllowedLevels());
	}
	
	@Test
	void iterator() {
		assertDoesNotThrow(LoggingType.CONSOLE::iterator);
		assertDoesNotThrow(LoggingType.FILE::iterator);
	}
}