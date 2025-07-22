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

package net.luis.utils.util;

import com.google.common.collect.Lists;
import net.luis.utils.lang.EnumLike;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Priority}.<br>
 *
 * @author Luis-St
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Required because some tests will create new Priority instances that affect the return values of lowest() and highest()
class PriorityTest {
	
	@Test
	void constructorWithValidParameters() {
		assertDoesNotThrow(() -> new Priority("test", 0));
		assertDoesNotThrow(() -> new Priority("high", 100));
		assertDoesNotThrow(() -> new Priority("low", -100));
	}
	
	@Test
	void constructorWithNullName() {
		assertThrows(NullPointerException.class, () -> new Priority(null, 0));
	}
	
	@Test
	void createBelowWithValidPriority() {
		Priority priority = Priority.createBelow(Priority.LOW);
		assertNotNull(priority);
		assertEquals(Priority.LOW.priority() - 1, priority.priority());
		assertTrue(priority.compareTo(Priority.LOW) < 0);
		assertTrue(priority.compareTo(Priority.LOWEST) > 0);
	}
	
	@Test
	void createBelowWithNullPriority() {
		assertThrows(NullPointerException.class, () -> Priority.createBelow(null));
	}
	
	@Test
	void createBelowWithExtremePriorities() {
		Priority belowLowest = Priority.createBelow(Priority.LOWEST);
		assertTrue(belowLowest.compareTo(Priority.LOWEST) < 0);
		
		Priority belowNormal = Priority.createBelow(Priority.NORMAL);
		assertTrue(belowNormal.compareTo(Priority.NORMAL) < 0);
		assertTrue(belowNormal.compareTo(Priority.LOW) > 0);
	}
	
	@Test
	void createAboveWithValidPriority() {
		Priority priority = Priority.createAbove(Priority.HIGH);
		assertNotNull(priority);
		assertEquals(Priority.HIGH.priority() + 1, priority.priority());
		assertTrue(priority.compareTo(Priority.HIGH) > 0);
		assertTrue(priority.compareTo(Priority.HIGHEST) < 0);
	}
	
	@Test
	void createAboveWithNullPriority() {
		assertThrows(NullPointerException.class, () -> Priority.createAbove(null));
	}
	
	@Test
	void createAboveWithExtremePriorities() {
		Priority aboveHighest = Priority.createAbove(Priority.HIGHEST);
		assertTrue(aboveHighest.compareTo(Priority.HIGHEST) > 0);
		
		Priority aboveNormal = Priority.createAbove(Priority.NORMAL);
		assertTrue(aboveNormal.compareTo(Priority.NORMAL) > 0);
		assertTrue(aboveNormal.compareTo(Priority.HIGH) < 0);
	}
	
	@Test
	void createBetweenWithValidPriorities() {
		Priority priority = Priority.createBetween(Priority.LOW, Priority.HIGH);
		assertNotNull(priority);
		assertEquals(Priority.NORMAL.priority(), priority.priority());
		assertTrue(priority.compareTo(Priority.LOW) > 0);
		assertEquals(0, priority.compareTo(Priority.NORMAL));
		assertTrue(priority.compareTo(Priority.HIGH) < 0);
	}
	
	@Test
	void createBetweenWithNullPriorities() {
		assertThrows(NullPointerException.class, () -> Priority.createBetween(null, null));
		assertThrows(NullPointerException.class, () -> Priority.createBetween(Priority.LOW, null));
		assertThrows(NullPointerException.class, () -> Priority.createBetween(null, Priority.HIGH));
	}
	
	@Test
	void createBetweenWithSamePriority() {
		Priority priority = Priority.createBetween(Priority.NORMAL, Priority.NORMAL);
		assertEquals(Priority.NORMAL.priority(), priority.priority());
	}
	
	@Test
	void createBetweenWithAdjacentPriorities() {
		Priority p1 = new Priority("p1", 10);
		Priority p2 = new Priority("p2", 12);
		Priority between = Priority.createBetween(p1, p2);
		assertEquals(11, between.priority());
	}
	
	@Test
	void lowestWithoutParameters() {
		assertEquals(Priority.LOWEST, Priority.lowest());
	}
	
	@Test
	void lowestWithNullOrEmpty() {
		assertEquals(Priority.getNearest(Long.MIN_VALUE), Priority.lowest((Priority[]) null));
		assertEquals(Priority.getNearest(Long.MIN_VALUE), Priority.lowest(new Priority[0]));
	}
	
	@Test
	void lowestWithMultiplePriorities() {
		assertEquals(Priority.LOW, Priority.lowest(Priority.LOW, Priority.HIGH));
		assertEquals(Priority.LOWEST, Priority.lowest(Priority.LOWEST, Priority.NORMAL, Priority.HIGHEST));
		
		Priority custom1 = new Priority("custom1", -1000);
		Priority custom2 = new Priority("custom2", 1000);
		assertEquals(custom1, Priority.lowest(custom1, custom2, Priority.NORMAL));
	}
	
	@Test
	@Order(10)
	void highestWithoutParameters() {
		assertEquals(Priority.HIGHEST, Priority.highest());
	}
	
	@Test
	void highestWithNullOrEmpty() {
		assertEquals(Priority.getNearest(Long.MAX_VALUE), Priority.highest((Priority[]) null));
		assertEquals(Priority.getNearest(Long.MAX_VALUE), Priority.highest(new Priority[0]));
	}
	
	@Test
	void highestWithMultiplePriorities() {
		assertEquals(Priority.HIGH, Priority.highest(Priority.LOW, Priority.HIGH));
		assertEquals(Priority.HIGHEST, Priority.highest(Priority.LOWEST, Priority.NORMAL, Priority.HIGHEST));
		
		Priority custom1 = Priority.LOW;
		Priority custom2 = Priority.HIGH;
		assertEquals(custom2, Priority.highest(custom1, custom2, Priority.NORMAL));
	}
	
	@Test
	@Order(10)
	void getNearestWithExistingValues() {
		assertEquals(Priority.LOWEST, Priority.getNearest(Long.MIN_VALUE));
		assertEquals(Priority.LOWEST, Priority.getNearest(Integer.MIN_VALUE));
		assertEquals(Priority.LOW, Priority.getNearest(Integer.MIN_VALUE / 2));
		assertEquals(Priority.NORMAL, Priority.getNearest(0));
		assertEquals(Priority.HIGH, Priority.getNearest(Integer.MAX_VALUE / 2));
		assertEquals(Priority.HIGHEST, Priority.getNearest(Integer.MAX_VALUE));
		assertEquals(Priority.HIGHEST, Priority.getNearest(Long.MAX_VALUE));
	}
	
	@Test
	@Order(10)
	void getNearestWithIntermediateValues() {
		assertEquals(Priority.LOW, Priority.getNearest(-1073741824));
		assertEquals(Priority.NORMAL, Priority.getNearest(100));
		assertEquals(Priority.HIGH, Priority.getNearest(1000000000));
	}
	
	@Test
	void values() {
		List<Priority> values = EnumLike.values(Priority.class);
		assertNotNull(values);
		assertTrue(values.size() >= 5);
		assertTrue(values.contains(Priority.LOWEST));
		assertTrue(values.contains(Priority.LOW));
		assertTrue(values.contains(Priority.NORMAL));
		assertTrue(values.contains(Priority.HIGH));
		assertTrue(values.contains(Priority.HIGHEST));
	}
	
	@Test
	void valueOf() {
		assertEquals(Priority.LOWEST, EnumLike.valueOf(Priority.class, "LOWEST"));
		assertEquals(Priority.LOW, EnumLike.valueOf(Priority.class, "LOW"));
		assertEquals(Priority.NORMAL, EnumLike.valueOf(Priority.class, "NORMAL"));
		assertEquals(Priority.HIGH, EnumLike.valueOf(Priority.class, "HIGH"));
		assertEquals(Priority.HIGHEST, EnumLike.valueOf(Priority.class, "HIGHEST"));
		
		assertEquals(Priority.LOWEST, EnumLike.valueOf(Priority.class, "lowest"));
		assertEquals(Priority.LOW, EnumLike.valueOf(Priority.class, "low"));
		assertEquals(Priority.NORMAL, EnumLike.valueOf(Priority.class, "normal"));
		assertEquals(Priority.HIGH, EnumLike.valueOf(Priority.class, "high"));
		assertEquals(Priority.HIGHEST, EnumLike.valueOf(Priority.class, "highest"));
	}
	
	@Test
	void name() {
		assertEquals("lowest", Priority.LOWEST.name());
		assertEquals("low", Priority.LOW.name());
		assertEquals("normal", Priority.NORMAL.name());
		assertEquals("high", Priority.HIGH.name());
		assertEquals("highest", Priority.HIGHEST.name());
	}
	
	@Test
	void ordinal() {
		List<Priority> values = EnumLike.values(Priority.class);
		assertEquals(0, Priority.LOWEST.ordinal());
		assertEquals(1, Priority.LOW.ordinal());
		assertEquals(2, Priority.NORMAL.ordinal());
		assertEquals(3, Priority.HIGH.ordinal());
		assertEquals(4, Priority.HIGHEST.ordinal());
	}
	
	@Test
	void priorityValue() {
		assertEquals(Integer.MIN_VALUE, Priority.LOWEST.priority());
		assertEquals(Integer.MIN_VALUE / 2, Priority.LOW.priority());
		assertEquals(0, Priority.NORMAL.priority());
		assertEquals(Integer.MAX_VALUE / 2, Priority.HIGH.priority());
		assertEquals(Integer.MAX_VALUE, Priority.HIGHEST.priority());
	}
	
	@Test
	void compareToWithBuiltInPriorities() {
		assertTrue(Priority.LOWEST.compareTo(Priority.LOW) < 0);
		assertTrue(Priority.LOW.compareTo(Priority.NORMAL) < 0);
		assertTrue(Priority.NORMAL.compareTo(Priority.HIGH) < 0);
		assertTrue(Priority.HIGH.compareTo(Priority.HIGHEST) < 0);
		
		assertTrue(Priority.HIGHEST.compareTo(Priority.HIGH) > 0);
		assertTrue(Priority.HIGH.compareTo(Priority.NORMAL) > 0);
		assertTrue(Priority.NORMAL.compareTo(Priority.LOW) > 0);
		assertTrue(Priority.LOW.compareTo(Priority.LOWEST) > 0);
		
		assertEquals(0, Priority.NORMAL.compareTo(Priority.NORMAL));
	}
	
	@Test
	void compareToWithCustomPriorities() {
		Priority p1 = new Priority("p1", -100);
		Priority p2 = new Priority("p2", 0);
		Priority p3 = new Priority("p3", 100);
		
		assertTrue(p1.compareTo(p2) < 0);
		assertTrue(p2.compareTo(p3) < 0);
		assertTrue(p3.compareTo(p1) > 0);
		assertEquals(0, p2.compareTo(Priority.NORMAL));
	}
	
	@Test
	void sorting() {
		List<Priority> values = Lists.newArrayList(Priority.HIGHEST, Priority.LOW, Priority.NORMAL, Priority.LOWEST, Priority.HIGH);
		
		values.sort(Priority::compareTo);
		assertEquals(Priority.LOWEST, values.get(0));
		assertEquals(Priority.LOW, values.get(1));
		assertEquals(Priority.NORMAL, values.get(2));
		assertEquals(Priority.HIGH, values.get(3));
		assertEquals(Priority.HIGHEST, values.get(4));
		
		values.sort(Collections.reverseOrder(Priority::compareTo));
		assertEquals(Priority.HIGHEST, values.get(0));
		assertEquals(Priority.HIGH, values.get(1));
		assertEquals(Priority.NORMAL, values.get(2));
		assertEquals(Priority.LOW, values.get(3));
		assertEquals(Priority.LOWEST, values.get(4));
	}
	
	@Test
	void hashCodeConsistency() {
		Priority p1 = new Priority("same", 42);
		Priority p2 = new Priority("same", 42);
		
		assertEquals(p1.hashCode(), p2.hashCode());
	}
}
