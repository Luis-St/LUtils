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
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Priority}.<br>
 *
 * @author Luis-St
 */
class PriorityTest {
	
	@Test
	void createBelow() {
		assertThrows(NullPointerException.class, () -> Priority.createBelow(null));
		Priority priority = Priority.createBelow(Priority.LOW);
		assertNotNull(priority);
		assertEquals(Priority.LOW.priority() - 1, priority.priority());
		assertTrue(priority.compareTo(Priority.LOW) < 0);
		assertTrue(priority.compareTo(Priority.LOWEST) > 0);
	}
	
	@Test
	void createAbove() {
		assertThrows(NullPointerException.class, () -> Priority.createBelow(null));
		Priority priority = Priority.createAbove(Priority.HIGH);
		assertNotNull(priority);
		assertEquals(Priority.HIGH.priority() + 1, priority.priority());
		assertTrue(priority.compareTo(Priority.HIGH) > 0);
		assertTrue(priority.compareTo(Priority.HIGHEST) < 0);
	}
	
	@Test
	void createBetween() {
		assertThrows(NullPointerException.class, () -> Priority.createBetween(null, null));
		assertThrows(NullPointerException.class, () -> Priority.createBetween(Priority.LOW, null));
		assertThrows(NullPointerException.class, () -> Priority.createBetween(null, Priority.HIGH));
		Priority priority = Priority.createBetween(Priority.LOW, Priority.HIGH);
		assertNotNull(priority);
		assertEquals(Priority.NORMAL.priority(), priority.priority());
		assertTrue(priority.compareTo(Priority.LOW) > 0);
		assertEquals(0, priority.compareTo(Priority.NORMAL));
		assertTrue(priority.compareTo(Priority.HIGH) < 0);
	}
	
	@Test
	void lowest() {
		assertDoesNotThrow(() -> Priority.lowest());
		assertEquals(Priority.LOWEST, Priority.lowest());
		assertEquals(Priority.LOW, Priority.lowest(Priority.LOW, Priority.HIGH));
	}
	
	@Test
	void highest() {
		assertDoesNotThrow(() -> Priority.highest());
		assertEquals(Priority.HIGHEST, Priority.highest());
		assertEquals(Priority.HIGH, Priority.highest(Priority.LOW, Priority.HIGH));
	}
	
	@Test
	void getNearest() {
		assertEquals(Priority.LOWEST, Priority.getNearest(Long.MIN_VALUE));
		assertEquals(Priority.LOWEST, Priority.getNearest(Integer.MIN_VALUE));
		assertEquals(Priority.LOW, Priority.getNearest(Integer.MIN_VALUE / 2));
		assertEquals(Priority.NORMAL, Priority.getNearest(0));
		assertEquals(Priority.HIGH, Priority.getNearest(Integer.MAX_VALUE / 2));
		assertEquals(Priority.HIGHEST, Priority.getNearest(Integer.MAX_VALUE));
		assertEquals(Priority.HIGHEST, Priority.getNearest(Long.MAX_VALUE));
	}
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new Priority(null, 0));
	}
	
	@Test
	void values() {
		List<Priority> values = EnumLike.values(Priority.class);
		assertEquals(5, values.size());
		assertEquals(Priority.LOWEST, values.get(0));
		assertEquals(Priority.LOW, values.get(1));
		assertEquals(Priority.NORMAL, values.get(2));
		assertEquals(Priority.HIGH, values.get(3));
		assertEquals(Priority.HIGHEST, values.get(4));
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
		assertEquals(0, Priority.LOWEST.ordinal());
		assertEquals(1, Priority.LOW.ordinal());
		assertEquals(2, Priority.NORMAL.ordinal());
		assertEquals(3, Priority.HIGH.ordinal());
		assertEquals(4, Priority.HIGHEST.ordinal());
	}
	
	@Test
	void priority() {
		assertEquals(Integer.MIN_VALUE, Priority.LOWEST.priority());
		assertEquals(Integer.MIN_VALUE / 2, Priority.LOW.priority());
		assertEquals(0, Priority.NORMAL.priority());
		assertEquals(Integer.MAX_VALUE / 2, Priority.HIGH.priority());
		assertEquals(Integer.MAX_VALUE, Priority.HIGHEST.priority());
	}
	
	@Test
	void compareTo() {
		assertTrue(Priority.LOWEST.compareTo(Priority.LOW) < 0);
		assertTrue(Priority.LOW.compareTo(Priority.NORMAL) < 0);
		assertTrue(Priority.NORMAL.compareTo(Priority.HIGH) < 0);
		assertTrue(Priority.HIGH.compareTo(Priority.HIGHEST) < 0);
		assertTrue(Priority.HIGHEST.compareTo(Priority.HIGH) > 0);
		assertTrue(Priority.HIGH.compareTo(Priority.NORMAL) > 0);
		assertTrue(Priority.NORMAL.compareTo(Priority.LOW) > 0);
		assertTrue(Priority.LOW.compareTo(Priority.LOWEST) > 0);
		assertEquals(0, Priority.NORMAL.compareTo(Priority.NORMAL));
		
		List<Priority> values = Lists.newArrayList(EnumLike.values(Priority.class));
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
}