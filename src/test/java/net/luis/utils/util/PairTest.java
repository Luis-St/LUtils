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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Pair}.<br>
 *
 * @author Luis-St
 */
class PairTest {
	
	@Test
	void of() {
		assertNotNull(Pair.of(1, 2));
		assertDoesNotThrow(() -> Pair.of(null, null));
		assertDoesNotThrow(() -> Pair.of(1, null));
		assertDoesNotThrow(() -> Pair.of(null, 2));
	}
	
	@Test
	void getFirst() {
		assertNull(Pair.of(null, 2).getFirst());
		assertEquals(1, Pair.of(1, 2).getFirst());
		assertNotEquals(2, Pair.of(1, 2).getFirst());
	}
	
	@Test
	void getSecond() {
		assertNull(Pair.of(1, null).getSecond());
		assertEquals(2, Pair.of(1, 2).getSecond());
		assertNotEquals(1, Pair.of(1, 2).getSecond());
	}
	
	@Test
	void swap() {
		assertEquals(Pair.of(2, 1), Pair.of(1, 2).swap());
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).swap());
	}
	
	@Test
	void mapFirst() {
		assertThrows(NullPointerException.class, () -> Pair.of(1, 2).mapFirst(null));
		assertDoesNotThrow(() -> Pair.of(1, 2).mapFirst(i -> i + 1));
		assertEquals(Pair.of(2, 2), Pair.of(1, 2).mapFirst(i -> i + 1));
		assertEquals(Pair.of(1, 1), Pair.of(null, 1).mapFirst(i -> 1));
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).mapFirst(i -> i + 1));
	}
	
	@Test
	void mapSecond() {
		assertThrows(NullPointerException.class, () -> Pair.of(1, 2).mapSecond(null));
		assertDoesNotThrow(() -> Pair.of(1, 2).mapSecond(i -> i + 1));
		assertEquals(Pair.of(1, 3), Pair.of(1, 2).mapSecond(i -> i + 1));
		assertEquals(Pair.of(1, 1), Pair.of(1, null).mapSecond(i -> 1));
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).mapSecond(i -> i + 1));
	}
	
	@Test
	void withFirst() {
		assertEquals(Pair.of(3, 2), Pair.of(1, 2).withFirst(3));
		assertEquals(Pair.of(null, 2), Pair.of(1, 2).withFirst(null));
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).withFirst(3));
	}
	
	@Test
	void withSecond() {
		assertEquals(Pair.of(1, 3), Pair.of(1, 2).withSecond(3));
		assertEquals(Pair.of(1, null), Pair.of(1, 2).withSecond(null));
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).withSecond(3));
	}
}