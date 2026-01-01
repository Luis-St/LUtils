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

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThrowableFunction}.<br>
 *
 * @author Luis-St
 */
class ThrowableFunctionTest {
	
	@Test
	void caughtConvertsSuccessfulFunction() {
		ThrowableFunction<Integer, Integer, Exception> function = i -> i * 2;
		Function<Integer, Integer> caught = ThrowableFunction.caught(function);
		
		assertEquals(10, caught.apply(5));
		assertEquals(0, caught.apply(0));
		assertEquals(-6, caught.apply(-3));
	}
	
	@Test
	void caughtWrapsExceptionInRuntimeException() {
		ThrowableFunction<Integer, Integer, Exception> function = i -> {
			throw new Exception("Test exception");
		};
		Function<Integer, Integer> caught = ThrowableFunction.caught(function);
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> caught.apply(1));
		assertInstanceOf(Exception.class, exception.getCause());
		assertEquals("Test exception", exception.getCause().getMessage());
	}
	
	@Test
	void caughtWithNullFunction() {
		assertThrows(NullPointerException.class, () -> ThrowableFunction.caught(null));
	}
	
	@Test
	void caughtWithDifferentParameterTypes() {
		ThrowableFunction<String, Integer, Exception> function = String::length;
		Function<String, Integer> caught = ThrowableFunction.caught(function);
		
		assertEquals(5, caught.apply("hello"));
		assertEquals(0, caught.apply(""));
	}
	
	@Test
	void caughtWithNullParameter() {
		ThrowableFunction<String, String, Exception> function = s -> s == null ? "null" : s.toUpperCase();
		Function<String, String> caught = ThrowableFunction.caught(function);
		
		assertEquals("null", caught.apply(null));
		assertEquals("HELLO", caught.apply("hello"));
	}
	
	@Test
	void caughtWithNullResult() {
		ThrowableFunction<String, String, Exception> function = s -> null;
		Function<String, String> caught = ThrowableFunction.caught(function);
		
		assertNull(caught.apply("test"));
	}
	
	@Test
	void applyWithSuccessfulExecution() {
		ThrowableFunction<Integer, Integer, Exception> function = i -> i * i;
		
		assertEquals(25, assertDoesNotThrow(() -> function.apply(5)));
		assertEquals(0, assertDoesNotThrow(() -> function.apply(0)));
		assertEquals(9, assertDoesNotThrow(() -> function.apply(-3)));
	}
	
	@Test
	void applyWithException() {
		ThrowableFunction<Integer, Integer, Exception> function = i -> {
			if (i < 0) {
				throw new Exception("Negative values not allowed");
			}
			return i * 2;
		};
		
		assertEquals(10, assertDoesNotThrow(() -> function.apply(5)));
		assertEquals(0, assertDoesNotThrow(() -> function.apply(0)));
		
		Exception exception = assertThrows(Exception.class, () -> function.apply(-1));
		assertEquals("Negative values not allowed", exception.getMessage());
	}
	
	@Test
	void applyWithNullParameter() {
		ThrowableFunction<String, String, Exception> function = s -> {
			if (s == null) {
				throw new Exception("Null parameter not allowed");
			}
			return s.toUpperCase();
		};
		
		assertEquals("HELLO", assertDoesNotThrow(() -> function.apply("hello")));
		assertThrows(Exception.class, () -> function.apply(null));
	}
	
	@Test
	void applyWithDifferentTypes() {
		ThrowableFunction<String, Boolean, Exception> function = s -> s.length() > 3;
		
		assertTrue(assertDoesNotThrow(() -> function.apply("hello")));
		assertFalse(assertDoesNotThrow(() -> function.apply("hi")));
	}
	
	@Test
	void applyReturnsNullResult() {
		ThrowableFunction<String, String, Exception> function = s -> null;
		
		assertNull(assertDoesNotThrow(() -> function.apply("test")));
	}
	
	@Test
	void andThenCombinesFunctions() {
		ThrowableFunction<Integer, Integer, Exception> function = i -> i * 2;
		ThrowableFunction<Integer, Integer, Exception> after = i -> i + 1;
		
		ThrowableFunction<Integer, Integer, Exception> combined = function.andThen(after);
		
		assertEquals(11, assertDoesNotThrow(() -> combined.apply(5)));
		assertEquals(1, assertDoesNotThrow(() -> combined.apply(0)));
	}
	
	@Test
	void andThenWithDifferentReturnType() {
		ThrowableFunction<Integer, Integer, Exception> function = i -> i * 2;
		ThrowableFunction<Integer, String, Exception> after = i -> "Result: " + i;
		
		ThrowableFunction<Integer, String, Exception> combined = function.andThen(after);
		
		assertEquals("Result: 10", assertDoesNotThrow(() -> combined.apply(5)));
	}
	
	@Test
	void andThenWithMultipleChaining() {
		ThrowableFunction<Integer, Integer, Exception> function = i -> i + 1;
		ThrowableFunction<Integer, Integer, Exception> multiply = i -> i * 2;
		ThrowableFunction<Integer, String, Exception> toString = i -> "Final: " + i;
		
		ThrowableFunction<Integer, String, Exception> combined = function.andThen(multiply).andThen(toString);
		
		assertEquals("Final: 12", assertDoesNotThrow(() -> combined.apply(5)));
	}
	
	@Test
	void andThenWithNullAfterFunction() {
		ThrowableFunction<Integer, Integer, Exception> function = i -> i * 2;
		
		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
	
	@Test
	void andThenWithExceptionInOriginalFunction() {
		ThrowableFunction<Integer, Integer, Exception> function = i -> {
			throw new Exception("Original function failed");
		};
		ThrowableFunction<Integer, Integer, Exception> after = i -> i * 2;
		
		ThrowableFunction<Integer, Integer, Exception> combined = function.andThen(after);
		
		Exception exception = assertThrows(Exception.class, () -> combined.apply(1));
		assertEquals("Original function failed", exception.getMessage());
	}
	
	@Test
	void andThenWithExceptionInAfterFunction() {
		ThrowableFunction<Integer, Integer, Exception> function = i -> i * 2;
		ThrowableFunction<Integer, Integer, Exception> after = i -> {
			throw new Exception("After function failed");
		};
		
		ThrowableFunction<Integer, Integer, Exception> combined = function.andThen(after);
		
		Exception exception = assertThrows(Exception.class, () -> combined.apply(1));
		assertEquals("After function failed", exception.getMessage());
	}
	
	@Test
	void andThenWithNullResult() {
		ThrowableFunction<Integer, Integer, Exception> function = i -> null;
		ThrowableFunction<Integer, String, Exception> after = i -> i == null ? "null" : i.toString();
		
		ThrowableFunction<Integer, String, Exception> combined = function.andThen(after);
		
		assertEquals("null", assertDoesNotThrow(() -> combined.apply(1)));
	}
	
	@Test
	void andThenWithIdentityFunction() {
		ThrowableFunction<Integer, Integer, Exception> function = i -> i * 2;
		ThrowableFunction<Integer, Integer, Exception> identity = i -> i;
		
		ThrowableFunction<Integer, Integer, Exception> combined = function.andThen(identity);
		
		assertEquals(10, assertDoesNotThrow(() -> combined.apply(5)));
	}
}
