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

package net.luis.utils.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TriFunction}.<br>
 *
 * @author Luis-St
 */
class TriFunctionTest {
	
	@Test
	void apply() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> {
			assertEquals(1, a);
			assertEquals(2, b);
			assertEquals(3, c);
			return a + b + c;
		};
		assertEquals(6, function.apply(1, 2, 3));
		assertDoesNotThrow(() -> function.apply(1, 2, 3));
	}
	
	@Test
	void andThen() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> a + b + c;
		assertDoesNotThrow(() -> function.andThen(i -> i * 2));
		assertEquals(12, function.andThen(i -> i * 2).apply(1, 2, 3));
		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
}