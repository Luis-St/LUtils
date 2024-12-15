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

package net.luis.utils.function.throwable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThrowableFunction}.<br>
 *
 * @author Luis-St
 */
class ThrowableFunctionTest {
	
	@Test
	void caught() {
		ThrowableFunction<Integer, Integer, Exception> function = (a) -> {
			if (a != 1) {
				throw new Exception();
			}
			assertEquals(1, a);
			return a;
		};
		assertEquals(1, assertDoesNotThrow(() -> ThrowableFunction.caught(function).apply(1)));
		assertThrows(RuntimeException.class, () -> ThrowableFunction.caught(function).apply(2));
		assertThrows(NullPointerException.class, () -> ThrowableFunction.caught(null));
	}
	
	@Test
	void apply() {
		ThrowableFunction<Integer, Integer, Exception> function = (a) -> {
			if (a != 1) {
				throw new Exception();
			}
			assertEquals(1, a);
			return a;
		};
		assertEquals(1, assertDoesNotThrow(() -> function.apply(1)));
		assertThrows(Exception.class, () -> function.apply(2));
	}
	
	@Test
	void andThen() {
		ThrowableFunction<Integer, Integer, Exception> function = (a) -> a;
		assertDoesNotThrow(() -> function.andThen(i -> i * 2));
		assertEquals(2, assertDoesNotThrow(() -> function.andThen(i -> i * 2).apply(1)));
		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
}
