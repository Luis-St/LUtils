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

package net.luis.utils.function;

import net.luis.utils.function.throwable.*;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FunctionUtils}.<br>
 *
 * @author Luis-St
 */
class FunctionUtilsTest {
	
	@Test
	void memorizeSupplierCachesResult() {
		AtomicInteger callCount = new AtomicInteger(0);
		Supplier<Integer> supplier = FunctionUtils.memorize(() -> {
			callCount.incrementAndGet();
			return new Random(0).nextInt(100);
		});
		
		assertEquals(60, supplier.get());
		assertEquals(60, supplier.get());
		assertEquals(60, supplier.get());
		assertEquals(1, callCount.get());
	}
	
	@Test
	void memorizeSupplierWithNullDelegate() {
		assertThrows(NullPointerException.class, () -> FunctionUtils.memorize((Supplier<String>) null));
	}
	
	@Test
	void memorizeThrowableSupplierCachesResult() {
		AtomicInteger callCount = new AtomicInteger(0);
		ThrowableSupplier<Integer, RuntimeException> supplier = FunctionUtils.memorizeThrowable(() -> {
			callCount.incrementAndGet();
			return new Random(0).nextInt(100);
		});
		
		assertEquals(60, assertDoesNotThrow(supplier::get));
		assertEquals(60, assertDoesNotThrow(supplier::get));
		assertEquals(1, callCount.get());
	}
	
	@Test
	void memorizeThrowableSupplierWithException() {
		ThrowableSupplier<String, Exception> supplier = FunctionUtils.memorizeThrowable(() -> {
			throw new Exception("Test exception");
		});
		
		assertThrows(Exception.class, supplier::get);
	}
	
	@Test
	void memorizeThrowableSupplierWithNullDelegate() {
		assertThrows(NullPointerException.class, () -> FunctionUtils.memorizeThrowable((ThrowableSupplier<String, Exception>) null));
	}
	
	@Test
	void memorizeFunctionCachesResults() {
		AtomicInteger callCount = new AtomicInteger(0);
		Function<Integer, Integer> function = FunctionUtils.memorize((seed) -> {
			callCount.incrementAndGet();
			return new Random(seed).nextInt(100);
		});
		
		assertEquals(60, function.apply(0));
		assertEquals(60, function.apply(0));
		assertEquals(85, function.apply(1));
		assertEquals(85, function.apply(1));
		assertEquals(2, callCount.get());
	}
	
	@Test
	void memorizeFunctionWithDifferentInputs() {
		Function<String, Integer> function = FunctionUtils.memorize(String::length);
		
		assertEquals(5, function.apply("hello"));
		assertEquals(5, function.apply("world"));
		assertEquals(3, function.apply("foo"));
		assertEquals(5, function.apply("hello"));
	}
	
	@Test
	void memorizeFunctionWithNullDelegate() {
		assertThrows(NullPointerException.class, () -> FunctionUtils.memorize((Function<String, String>) null));
	}
	
	@Test
	void memorizeThrowableFunctionCachesResults() {
		AtomicInteger callCount = new AtomicInteger(0);
		ThrowableFunction<Integer, Integer, Exception> function = FunctionUtils.memorizeThrowable((seed) -> {
			callCount.incrementAndGet();
			return new Random(seed).nextInt(100);
		});
		
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0)));
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0)));
		assertEquals(85, assertDoesNotThrow(() -> function.apply(1)));
		assertEquals(2, callCount.get());
	}
	
	@Test
	void memorizeThrowableFunctionWithException() {
		ThrowableFunction<String, String, Exception> function = FunctionUtils.memorizeThrowable((input) -> {
			throw new Exception("Test exception");
		});
		
		assertThrows(Exception.class, () -> function.apply("test"));
	}
	
	@Test
	void memorizeThrowableFunctionWithNullDelegate() {
		assertThrows(NullPointerException.class, () -> FunctionUtils.memorizeThrowable((ThrowableFunction<String, String, Exception>) null));
	}
	
	@Test
	void memorizeBiFunctionCachesResults() {
		AtomicInteger callCount = new AtomicInteger(0);
		BiFunction<Integer, Integer, Integer> function = FunctionUtils.memorize((seed, value) -> {
			callCount.incrementAndGet();
			return new Random(seed).nextInt(100) + value;
		});
		
		assertEquals(60, function.apply(0, 0));
		assertEquals(60, function.apply(0, 0));
		assertEquals(86, function.apply(1, 1));
		assertEquals(86, function.apply(1, 1));
		assertEquals(61, function.apply(0, 1));
		assertEquals(3, callCount.get());
	}
	
	@Test
	void memorizeBiFunctionWithDifferentSecondParameter() {
		BiFunction<String, String, String> function = FunctionUtils.memorize((a, b) -> a + b);
		
		assertEquals("ab", function.apply("a", "b"));
		assertEquals("ac", function.apply("a", "c"));
		assertEquals("ab", function.apply("a", "b"));
	}
	
	@Test
	void memorizeBiFunctionWithNullDelegate() {
		assertThrows(NullPointerException.class, () -> FunctionUtils.memorize((BiFunction<String, String, String>) null));
	}
	
	@Test
	void memorizeThrowableBiFunctionCachesResults() {
		AtomicInteger callCount = new AtomicInteger(0);
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = FunctionUtils.memorizeThrowable((seed, value) -> {
			callCount.incrementAndGet();
			return new Random(seed).nextInt(100) + value;
		});
		
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0, 0)));
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0, 0)));
		assertEquals(86, assertDoesNotThrow(() -> function.apply(1, 1)));
		assertEquals(2, callCount.get());
	}
	
	@Test
	void memorizeThrowableBiFunctionWithException() {
		ThrowableBiFunction<String, String, String, Exception> function = FunctionUtils.memorizeThrowable((a, b) -> {
			throw new Exception("Test exception");
		});
		
		assertThrows(Exception.class, () -> function.apply("a", "b"));
	}
	
	@Test
	void memorizeThrowableBiFunctionWithNullDelegate() {
		assertThrows(NullPointerException.class, () -> FunctionUtils.memorizeThrowable((ThrowableBiFunction<String, String, String, Exception>) null));
	}
	
	@Test
	void memorizeTriFunctionCachesResults() {
		AtomicInteger callCount = new AtomicInteger(0);
		TriFunction<Integer, Integer, Integer, Integer> function = FunctionUtils.memorize((seed, value1, value2) -> {
			callCount.incrementAndGet();
			return new Random(seed).nextInt(100) + value1 + value2;
		});
		
		assertEquals(60, function.apply(0, 0, 0));
		assertEquals(60, function.apply(0, 0, 0));
		assertEquals(87, function.apply(1, 1, 1));
		assertEquals(87, function.apply(1, 1, 1));
		assertEquals(61, function.apply(0, 1, 0));
		assertEquals(3, callCount.get());
	}
	
	@Test
	void memorizeTriFunctionWithDifferentParameters() {
		TriFunction<String, String, String, String> function = FunctionUtils.memorize((a, b, c) -> a + b + c);
		
		assertEquals("abc", function.apply("a", "b", "c"));
		assertEquals("def", function.apply("d", "e", "f"));
		assertEquals("abc", function.apply("a", "b", "c"));
	}
	
	@Test
	void memorizeTriFunctionWithNullDelegate() {
		assertThrows(NullPointerException.class, () -> FunctionUtils.memorize((TriFunction<String, String, String, String>) null));
	}
	
	@Test
	void memorizeThrowableTriFunctionCachesResults() {
		AtomicInteger callCount = new AtomicInteger(0);
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = FunctionUtils.memorizeThrowable((seed, value1, value2) -> {
			callCount.incrementAndGet();
			return new Random(seed).nextInt(100) + value1 + value2;
		});
		
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0, 0, 0)));
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0, 0, 0)));
		assertEquals(87, assertDoesNotThrow(() -> function.apply(1, 1, 1)));
		assertEquals(2, callCount.get());
	}
	
	@Test
	void memorizeThrowableTriFunctionWithException() {
		ThrowableTriFunction<String, String, String, String, Exception> function = FunctionUtils.memorizeThrowable((a, b, c) -> {
			throw new Exception("Test exception");
		});
		
		assertThrows(Exception.class, () -> function.apply("a", "b", "c"));
	}
	
	@Test
	void memorizeThrowableTriFunctionWithNullDelegate() {
		assertThrows(NullPointerException.class, () -> FunctionUtils.memorizeThrowable((ThrowableTriFunction<String, String, String, String, Exception>) null));
	}
	
	@Test
	void memorizeQuadFunctionCachesResults() {
		AtomicInteger callCount = new AtomicInteger(0);
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = FunctionUtils.memorize((seed, value1, value2, value3) -> {
			callCount.incrementAndGet();
			return new Random(seed).nextInt(100) + value1 + value2 + value3;
		});
		
		assertEquals(60, function.apply(0, 0, 0, 0));
		assertEquals(60, function.apply(0, 0, 0, 0));
		assertEquals(88, function.apply(1, 1, 1, 1));
		assertEquals(88, function.apply(1, 1, 1, 1));
		assertEquals(61, function.apply(0, 1, 0, 0));
		assertEquals(3, callCount.get());
	}
	
	@Test
	void memorizeQuadFunctionWithDifferentParameters() {
		QuadFunction<String, String, String, String, String> function = FunctionUtils.memorize((a, b, c, d) -> a + b + c + d);
		
		assertEquals("abcd", function.apply("a", "b", "c", "d"));
		assertEquals("efgh", function.apply("e", "f", "g", "h"));
		assertEquals("abcd", function.apply("a", "b", "c", "d"));
	}
	
	@Test
	void memorizeQuadFunctionWithNullDelegate() {
		assertThrows(NullPointerException.class, () -> FunctionUtils.memorize((QuadFunction<String, String, String, String, String>) null));
	}
	
	@Test
	void memorizeThrowableQuadFunctionCachesResults() {
		AtomicInteger callCount = new AtomicInteger(0);
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = FunctionUtils.memorizeThrowable((seed, value1, value2, value3) -> {
			callCount.incrementAndGet();
			return new Random(seed).nextInt(100) + value1 + value2 + value3;
		});
		
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0, 0, 0, 0)));
		assertEquals(60, assertDoesNotThrow(() -> function.apply(0, 0, 0, 0)));
		assertEquals(88, assertDoesNotThrow(() -> function.apply(1, 1, 1, 1)));
		assertEquals(2, callCount.get());
	}
	
	@Test
	void memorizeThrowableQuadFunctionWithException() {
		ThrowableQuadFunction<String, String, String, String, String, Exception> function = FunctionUtils.memorizeThrowable((a, b, c, d) -> {
			throw new Exception("Test exception");
		});
		
		assertThrows(Exception.class, () -> function.apply("a", "b", "c", "d"));
	}
	
	@Test
	void memorizeThrowableQuadFunctionWithNullDelegate() {
		assertThrows(NullPointerException.class, () -> FunctionUtils.memorizeThrowable((ThrowableQuadFunction<String, String, String, String, String, Exception>) null));
	}
	
	@Test
	void memorizeHandlesNullResults() {
		Function<String, String> function = FunctionUtils.memorize(input -> null);
		
		assertNull(function.apply("test"));
		assertNull(function.apply("test"));
	}
	
	@Test
	void memorizeThrowableFunctionHandlesNullResults() {
		ThrowableFunction<String, String, RuntimeException> function = FunctionUtils.memorizeThrowable(input -> {
			throw new NullPointerException("Function result must not be null");
		});
		
		assertThrows(NullPointerException.class, () -> function.apply("test"));
	}
	
	@Test
	@SuppressWarnings("CallToSystemGC")
	void memorizeWithWeakReferences() {
		Function<String, String> function = FunctionUtils.memorize(str -> str.toUpperCase());
		
		String input = new String("test");
		assertEquals("TEST", function.apply(input));
		
		input = null;
		System.gc();
		
		String newInput = new String("test");
		assertEquals("TEST", function.apply(newInput));
	}
}
