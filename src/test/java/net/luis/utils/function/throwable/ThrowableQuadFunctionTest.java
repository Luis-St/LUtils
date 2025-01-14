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

package net.luis.utils.function.throwable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThrowableQuadFunction}.<br>
 *
 * @author Luis-St
 */
class ThrowableQuadFunctionTest {
	
	@Test
	void caught() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> {
			if (a != 1 || b != 2 || c != 3 || d != 4) {
				throw new Exception();
			}
			assertEquals(1, a);
			assertEquals(2, b);
			assertEquals(3, c);
			assertEquals(4, d);
			return a + b + c + d;
		};
		assertEquals(10, assertDoesNotThrow(() -> ThrowableQuadFunction.caught(function).apply(1, 2, 3, 4)));
		assertThrows(RuntimeException.class, () -> ThrowableQuadFunction.caught(function).apply(2, 2, 3, 4));
		assertThrows(NullPointerException.class, () -> ThrowableQuadFunction.caught(null));
	}
	
	@Test
	void apply() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> {
			if (a != 1 || b != 2 || c != 3 || d != 4) {
				throw new Exception();
			}
			assertEquals(1, a);
			assertEquals(2, b);
			assertEquals(3, c);
			assertEquals(4, d);
			return a + b + c + d;
		};
		assertEquals(10, assertDoesNotThrow(() -> function.apply(1, 2, 3, 4)));
		assertThrows(Exception.class, () -> function.apply(2, 2, 3, 4));
	}
	
	@Test
	void andThen() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> a + b + c + d;
		assertDoesNotThrow(() -> function.andThen(i -> i * 2));
		assertEquals(20, assertDoesNotThrow(() -> function.andThen(i -> i * 2).apply(1, 2, 3, 4)));
		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
}
