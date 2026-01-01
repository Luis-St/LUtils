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

package net.luis.utils.function.throwable;

import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThrowableBiFunction}.<br>
 *
 * @author Luis-St
 */
class ThrowableBiFunctionTest {
	
	@Test
	void caughtConvertsSuccessfulFunction() {
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = (a, b) -> a + b;
		BiFunction<Integer, Integer, Integer> caught = ThrowableBiFunction.caught(function);
		
		assertEquals(5, caught.apply(2, 3));
		assertDoesNotThrow(() -> caught.apply(1, 1));
	}
	
	@Test
	void caughtWrapsExceptionInRuntimeException() {
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = (a, b) -> {
			throw new Exception("Test exception");
		};
		BiFunction<Integer, Integer, Integer> caught = ThrowableBiFunction.caught(function);
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> caught.apply(1, 2));
		assertInstanceOf(Exception.class, exception.getCause());
		assertEquals("Test exception", exception.getCause().getMessage());
	}
	
	@Test
	void caughtWithNullFunction() {
		assertThrows(NullPointerException.class, () -> ThrowableBiFunction.caught(null));
	}
	
	@Test
	void caughtWithDifferentParameterTypes() {
		ThrowableBiFunction<String, Integer, String, Exception> function = (s, i) -> s + i;
		BiFunction<String, Integer, String> caught = ThrowableBiFunction.caught(function);
		
		assertEquals("test5", caught.apply("test", 5));
	}
	
	@Test
	void caughtWithNullParameters() {
		ThrowableBiFunction<String, String, String, Exception> function = (a, b) ->
			(a == null ? "null" : a) + (b == null ? "null" : b);
		BiFunction<String, String, String> caught = ThrowableBiFunction.caught(function);
		
		assertEquals("nullnull", caught.apply(null, null));
		assertEquals("anull", caught.apply("a", null));
		assertEquals("nullb", caught.apply(null, "b"));
	}
	
	@Test
	void caughtWithNullResult() {
		ThrowableBiFunction<String, String, String, Exception> function = (a, b) -> null;
		BiFunction<String, String, String> caught = ThrowableBiFunction.caught(function);
		
		assertNull(caught.apply("a", "b"));
	}
	
	@Test
	void applyWithSuccessfulExecution() {
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = (a, b) -> a * b;
		
		assertEquals(6, assertDoesNotThrow(() -> function.apply(2, 3)));
		assertEquals(0, assertDoesNotThrow(() -> function.apply(0, 5)));
		assertEquals(-10, assertDoesNotThrow(() -> function.apply(-2, 5)));
	}
	
	@Test
	void applyWithException() {
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = (a, b) -> {
			if (a < 0 || b < 0) {
				throw new Exception("Negative values not allowed");
			}
			return a + b;
		};
		
		assertEquals(7, assertDoesNotThrow(() -> function.apply(3, 4)));
		
		Exception exception = assertThrows(Exception.class, () -> function.apply(-1, 2));
		assertEquals("Negative values not allowed", exception.getMessage());
		
		assertThrows(Exception.class, () -> function.apply(1, -2));
	}
	
	@Test
	void applyWithNullParameters() {
		ThrowableBiFunction<String, String, String, Exception> function = (a, b) -> {
			if (a == null || b == null) {
				throw new Exception("Null parameters not allowed");
			}
			return a + b;
		};
		
		assertEquals("ab", assertDoesNotThrow(() -> function.apply("a", "b")));
		assertThrows(Exception.class, () -> function.apply(null, "b"));
		assertThrows(Exception.class, () -> function.apply("a", null));
		assertThrows(Exception.class, () -> function.apply(null, null));
	}
	
	@Test
	void applyWithDifferentTypes() {
		ThrowableBiFunction<String, Integer, Boolean, Exception> function = (s, i) -> s.length() == i;
		
		assertTrue(assertDoesNotThrow(() -> function.apply("hello", 5)));
		assertFalse(assertDoesNotThrow(() -> function.apply("hi", 5)));
	}
	
	@Test
	void applyReturnsNullResult() {
		ThrowableBiFunction<String, String, String, Exception> function = (a, b) -> null;
		
		assertNull(assertDoesNotThrow(() -> function.apply("a", "b")));
	}
	
	@Test
	void andThenCombinesFunctions() {
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = Integer::sum;
		ThrowableFunction<Integer, Integer, Exception> after = i -> i * 2;
		
		ThrowableBiFunction<Integer, Integer, Integer, Exception> combined = function.andThen(after);
		
		assertEquals(10, assertDoesNotThrow(() -> combined.apply(2, 3)));
		assertEquals(0, assertDoesNotThrow(() -> combined.apply(0, 0)));
	}
	
	@Test
	void andThenWithDifferentReturnType() {
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = Integer::sum;
		ThrowableFunction<Integer, String, Exception> after = i -> "Result: " + i;
		
		ThrowableBiFunction<Integer, Integer, String, Exception> combined = function.andThen(after);
		
		assertEquals("Result: 7", assertDoesNotThrow(() -> combined.apply(3, 4)));
	}
	
	@Test
	void andThenWithMultipleChaining() {
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = Integer::sum;
		ThrowableFunction<Integer, Integer, Exception> multiply = i -> i * 2;
		ThrowableFunction<Integer, String, Exception> toString = i -> "Final: " + i;
		
		ThrowableBiFunction<Integer, Integer, String, Exception> combined = function.andThen(multiply).andThen(toString);
		
		assertEquals("Final: 10", assertDoesNotThrow(() -> combined.apply(2, 3)));
	}
	
	@Test
	void andThenWithNullAfterFunction() {
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = Integer::sum;
		
		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
	
	@Test
	void andThenWithExceptionInOriginalFunction() {
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = (a, b) -> {
			throw new Exception("Original function failed");
		};
		ThrowableFunction<Integer, Integer, Exception> after = i -> i * 2;
		
		ThrowableBiFunction<Integer, Integer, Integer, Exception> combined = function.andThen(after);
		
		Exception exception = assertThrows(Exception.class, () -> combined.apply(1, 2));
		assertEquals("Original function failed", exception.getMessage());
	}
	
	@Test
	void andThenWithExceptionInAfterFunction() {
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = Integer::sum;
		ThrowableFunction<Integer, Integer, Exception> after = i -> {
			throw new Exception("After function failed");
		};
		
		ThrowableBiFunction<Integer, Integer, Integer, Exception> combined = function.andThen(after);
		
		Exception exception = assertThrows(Exception.class, () -> combined.apply(1, 2));
		assertEquals("After function failed", exception.getMessage());
	}
	
	@Test
	void andThenWithNullResult() {
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = (a, b) -> null;
		ThrowableFunction<Integer, String, Exception> after = i -> i == null ? "null" : i.toString();
		
		ThrowableBiFunction<Integer, Integer, String, Exception> combined = function.andThen(after);
		
		assertEquals("null", assertDoesNotThrow(() -> combined.apply(1, 2)));
	}
	
	@Test
	void andThenWithIdentityFunction() {
		ThrowableBiFunction<Integer, Integer, Integer, Exception> function = Integer::sum;
		ThrowableFunction<Integer, Integer, Exception> identity = i -> i;
		
		ThrowableBiFunction<Integer, Integer, Integer, Exception> combined = function.andThen(identity);
		
		assertEquals(5, assertDoesNotThrow(() -> combined.apply(2, 3)));
	}
}
