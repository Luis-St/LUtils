/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LoggingType}.<br>
 *
 * @author Luis-St
 */
class LoggingTypeTest {
	
	@Test
	void getAllowedLevels() {
		Level[] consoleLevels = LoggingType.CONSOLE.getAllowedLevels();
		Level[] fileLevels = LoggingType.FILE.getAllowedLevels();
		
		assertNotNull(consoleLevels);
		assertNotNull(fileLevels);
		
		assertEquals(6, consoleLevels.length);
		assertEquals(3, fileLevels.length);
		
		assertArrayEquals(new Level[] { Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL }, consoleLevels);
		assertArrayEquals(new Level[] { Level.DEBUG, Level.INFO, Level.ERROR }, fileLevels);
		
		Level[] consoleLevels2 = LoggingType.CONSOLE.getAllowedLevels();
		assertSame(consoleLevels, consoleLevels2);
		assertArrayEquals(consoleLevels, consoleLevels2);
	}
	
	@Test
	void getAllowedLevelsContainsExpectedLevels() {
		Level[] consoleLevels = LoggingType.CONSOLE.getAllowedLevels();
		List<Level> consoleList = List.of(consoleLevels);
		
		assertTrue(consoleList.contains(Level.TRACE));
		assertTrue(consoleList.contains(Level.DEBUG));
		assertTrue(consoleList.contains(Level.INFO));
		assertTrue(consoleList.contains(Level.WARN));
		assertTrue(consoleList.contains(Level.ERROR));
		assertTrue(consoleList.contains(Level.FATAL));
		assertFalse(consoleList.contains(Level.ALL));
		assertFalse(consoleList.contains(Level.OFF));
		
		Level[] fileLevels = LoggingType.FILE.getAllowedLevels();
		List<Level> fileList = List.of(fileLevels);
		
		assertFalse(fileList.contains(Level.TRACE));
		assertTrue(fileList.contains(Level.DEBUG));
		assertTrue(fileList.contains(Level.INFO));
		assertFalse(fileList.contains(Level.WARN));
		assertTrue(fileList.contains(Level.ERROR));
		assertFalse(fileList.contains(Level.FATAL));
		assertFalse(fileList.contains(Level.ALL));
		assertFalse(fileList.contains(Level.OFF));
	}
	
	@Test
	void iterator() {
		Iterator<Level> consoleIterator = LoggingType.CONSOLE.iterator();
		Iterator<Level> fileIterator = LoggingType.FILE.iterator();
		
		assertNotNull(consoleIterator);
		assertNotNull(fileIterator);
		
		List<Level> consoleLevels = new ArrayList<>();
		while (consoleIterator.hasNext()) {
			consoleLevels.add(consoleIterator.next());
		}
		
		assertEquals(6, consoleLevels.size());
		assertEquals(Level.TRACE, consoleLevels.get(0));
		assertEquals(Level.DEBUG, consoleLevels.get(1));
		assertEquals(Level.INFO, consoleLevels.get(2));
		assertEquals(Level.WARN, consoleLevels.get(3));
		assertEquals(Level.ERROR, consoleLevels.get(4));
		assertEquals(Level.FATAL, consoleLevels.get(5));
		
		List<Level> fileLevels = new ArrayList<>();
		while (fileIterator.hasNext()) {
			fileLevels.add(fileIterator.next());
		}
		
		assertEquals(3, fileLevels.size());
		assertEquals(Level.DEBUG, fileLevels.get(0));
		assertEquals(Level.INFO, fileLevels.get(1));
		assertEquals(Level.ERROR, fileLevels.get(2));
	}
	
	@Test
	void iteratorExhaustion() {
		Iterator<Level> iterator = LoggingType.CONSOLE.iterator();
		
		int count = 0;
		while (iterator.hasNext()) {
			assertNotNull(iterator.next());
			count++;
		}
		assertEquals(6, count);
		
		assertFalse(iterator.hasNext());
		assertThrows(NoSuchElementException.class, iterator::next);
	}
	
	@Test
	void iteratorBehaviorAfterExhaustion() {
		Iterator<Level> iterator = LoggingType.FILE.iterator();
		
		while (iterator.hasNext()) {
			iterator.next();
		}
		
		assertFalse(iterator.hasNext());
		assertFalse(iterator.hasNext());
		
		assertThrows(NoSuchElementException.class, iterator::next);
		assertThrows(NoSuchElementException.class, iterator::next);
	}
	
	@Test
	void iteratorIndependence() {
		Iterator<Level> iterator1 = LoggingType.CONSOLE.iterator();
		Iterator<Level> iterator2 = LoggingType.CONSOLE.iterator();
		
		assertNotSame(iterator1, iterator2);
		
		assertTrue(iterator1.hasNext());
		Level first1 = iterator1.next();
		
		assertTrue(iterator2.hasNext());
		Level first2 = iterator2.next();
		
		assertEquals(first1, first2);
		assertEquals(Level.TRACE, first1);
	}
	
	@Test
	void enhancedForLoopSupport() {
		List<Level> consoleLevels = new ArrayList<>();
		for (Level level : LoggingType.CONSOLE) {
			consoleLevels.add(level);
		}
		
		assertEquals(6, consoleLevels.size());
		assertArrayEquals(LoggingType.CONSOLE.getAllowedLevels(), consoleLevels.toArray(new Level[0]));
		
		List<Level> fileLevels = new ArrayList<>();
		for (Level level : LoggingType.FILE) {
			fileLevels.add(level);
		}
		
		assertEquals(3, fileLevels.size());
		assertArrayEquals(LoggingType.FILE.getAllowedLevels(), fileLevels.toArray(new Level[0]));
	}
	
	@Test
	void toStringMethod() {
		assertEquals("console", LoggingType.CONSOLE.toString());
		assertEquals("file", LoggingType.FILE.toString());
	}
	
	@Test
	void enumValues() {
		LoggingType[] values = LoggingType.values();
		assertEquals(2, values.length);
		assertEquals(LoggingType.CONSOLE, values[0]);
		assertEquals(LoggingType.FILE, values[1]);
	}
	
	@Test
	void enumValueOf() {
		assertEquals(LoggingType.CONSOLE, LoggingType.valueOf("CONSOLE"));
		assertEquals(LoggingType.FILE, LoggingType.valueOf("FILE"));
		
		assertThrows(IllegalArgumentException.class, () -> LoggingType.valueOf("INVALID"));
		assertThrows(IllegalArgumentException.class, () -> LoggingType.valueOf("console"));
		assertThrows(IllegalArgumentException.class, () -> LoggingType.valueOf("file"));
		assertThrows(NullPointerException.class, () -> LoggingType.valueOf(null));
	}
	
	@Test
	void enumComparison() {
		assertTrue(LoggingType.CONSOLE.ordinal() < LoggingType.FILE.ordinal());
		assertEquals(0, LoggingType.CONSOLE.ordinal());
		assertEquals(1, LoggingType.FILE.ordinal());
		
		assertSame(LoggingType.CONSOLE, LoggingType.CONSOLE);
		assertNotSame(LoggingType.CONSOLE, LoggingType.FILE);
		
		assertEquals(LoggingType.CONSOLE, LoggingType.CONSOLE);
		assertNotEquals(LoggingType.CONSOLE, LoggingType.FILE);
	}
}
