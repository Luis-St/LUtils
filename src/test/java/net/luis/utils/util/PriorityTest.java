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

package net.luis.utils.util;

import net.luis.utils.lang.EnumLike;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Priority}.<br>
 *
 * @author Luis-St
 */
class PriorityTest {
	
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
	}
}