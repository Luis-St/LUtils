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

import net.luis.utils.function.throwable.*;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FunctionUtils}.<br>
 *
 * @author Luis-St
 */
class FunctionUtilsTest {
	
	@Test
	void memorizeSupplier() {
		Supplier<Integer> supplier = FunctionUtils.memorize(() -> new Random(0).nextInt(100));
		assertEquals(60, supplier.get());
		assertEquals(60, supplier.get());
	}
	
	@Test
	void memorizeThrowableSupplier() {
		ThrowableSupplier<Integer, Exception> supplier = FunctionUtils.memorizeThrowable(() -> new Random(0).nextInt(100));
		assertEquals(60, assertDoesNotThrow(supplier::get));
		assertEquals(60, assertDoesNotThrow(supplier::get));
	}
	
	@Test
	void memorizeFunction() {
		Function<Integer, Integer> function = FunctionUtils.memorize((seed) -> new Random(seed).nextInt(100));
		assertEquals(60, function.apply(0));
		assertEquals(60, function.apply(0));
		assertEquals(85, function.apply(1));
		assertEquals(85, function.apply(1));
	}
	
	@Test
	void memorizeThrowableFunction() {
		ThrowableFunction<Integer, Integer, Exception> function = FunctionUtils.memorizeThrowable((seed) -> new Random(seed).nextInt(100));
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0)));
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0)));
		assertEquals(85, assertDoesNotThrow(() -> function.apply(1)));
		assertEquals(85, assertDoesNotThrow(() -> function.apply(1)));
	}
	
	@Test
	void memorizeBiFunction() {
		BiFunction<Integer, Integer, Integer> function = FunctionUtils.memorize((seed, value) -> new Random(seed).nextInt(100) + value);
		assertEquals(60, function.apply(0, 0));
		assertEquals(60, function.apply(0, 0));
		assertEquals(86, function.apply(1, 1));
		assertEquals(86, function.apply(1, 1));
	}
	
	@Test
	void memorizeThrowableBiFunction() {
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = FunctionUtils.memorizeThrowable((seed, value) -> new Random(seed).nextInt(100) + value);
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0, 0)));
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0, 0)));
		assertEquals(86, assertDoesNotThrow(() -> function.apply(1, 1)));
		assertEquals(86, assertDoesNotThrow(() -> function.apply(1, 1)));
	}
	
	@Test
	void memorizeTriFunction() {
		TriFunction<Integer, Integer, Integer, Integer> function = FunctionUtils.memorize((seed, value1, value2) -> new Random(seed).nextInt(100) + value1 + value2);
		assertEquals(60, function.apply(0, 0, 0));
		assertEquals(60, function.apply(0, 0, 0));
		assertEquals(87, function.apply(1, 1, 1));
		assertEquals(87, function.apply(1, 1, 1));
	}
	
	@Test
	void memorizeThrowableTriFunction() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = FunctionUtils.memorizeThrowable((seed, value1, value2) -> new Random(seed).nextInt(100) + value1 + value2);
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0, 0, 0)));
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0, 0, 0)));
		assertEquals(87, assertDoesNotThrow(() -> function.apply(1, 1, 1)));
		assertEquals(87, assertDoesNotThrow(() -> function.apply(1, 1, 1)));
	}
	
	@Test
	void memorizeQuadFunction() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = FunctionUtils.memorize((seed, value1, value2, value3) -> new Random(seed).nextInt(100) + value1 + value2 + value3);
		assertEquals(60, function.apply(0, 0, 0, 0));
		assertEquals(60, function.apply(0, 0, 0, 0));
		assertEquals(88, function.apply(1, 1, 1, 1));
		assertEquals(88, function.apply(1, 1, 1, 1));
	}
	
	@Test
	void memorizeThrowableQuadFunction() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = FunctionUtils.memorizeThrowable((seed, value1, value2, value3) -> new Random(seed).nextInt(100) + value1 + value2 + value3);
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0, 0, 0, 0)));
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0, 0, 0, 0)));
		assertEquals(88, assertDoesNotThrow(() -> function.apply(1, 1, 1, 1)));
		assertEquals(88, assertDoesNotThrow(() -> function.apply(1, 1, 1, 1)));
	}
}
