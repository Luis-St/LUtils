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
 * Test class for {@link ThrowableTriFunction}.<br>
 *
 * @author Luis-St
 */
class ThrowableTriFunctionTest {
	
	@Test
	void caught() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> {
			if (a != 1 || b != 2 || c != 3) {
				throw new Exception();
			}
			assertEquals(1, a);
			assertEquals(2, b);
			assertEquals(3, c);
			return a + b + c;
		};
		assertEquals(6, assertDoesNotThrow(() -> ThrowableTriFunction.caught(function).apply(1, 2, 3)));
		assertThrows(RuntimeException.class, () -> ThrowableTriFunction.caught(function).apply(2, 2, 3));
		assertThrows(NullPointerException.class, () -> ThrowableTriFunction.caught(null));
	}
	
	@Test
	void apply() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> {
			if (a != 1 || b != 2 || c != 3) {
				throw new Exception();
			}
			assertEquals(1, a);
			assertEquals(2, b);
			assertEquals(3, c);
			return a + b + c;
		};
		assertEquals(6, assertDoesNotThrow(() -> function.apply(1, 2, 3)));
		assertThrows(Exception.class, () -> function.apply(2, 2, 3));
	}
	
	@Test
	void andThen() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> a + b + c;
		assertDoesNotThrow(() -> function.andThen(i -> i * 2));
		assertEquals(12, assertDoesNotThrow(() -> function.andThen(i -> i * 2).apply(1, 2, 3)));
		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
}
