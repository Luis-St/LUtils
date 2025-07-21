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

import net.luis.utils.function.QuadFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThrowableQuadFunction}.<br>
 *
 * @author Luis-St
 */
class ThrowableQuadFunctionTest {
	
	@Test
	void caughtConvertsSuccessfulFunction() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> a + b + c + d;
		QuadFunction<Integer, Integer, Integer, Integer, Integer> caught = ThrowableQuadFunction.caught(function);
		
		assertEquals(10, caught.apply(1, 2, 3, 4));
		assertEquals(0, caught.apply(0, 0, 0, 0));
		assertEquals(-2, caught.apply(-1, -2, 1, 0));
	}
	
	@Test
	void caughtWrapsExceptionInRuntimeException() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> {
			throw new Exception("Test exception");
		};
		QuadFunction<Integer, Integer, Integer, Integer, Integer> caught = ThrowableQuadFunction.caught(function);
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> caught.apply(1, 2, 3, 4));
		assertInstanceOf(Exception.class, exception.getCause());
		assertEquals("Test exception", exception.getCause().getMessage());
	}
	
	@Test
	void caughtWithNullFunction() {
		assertThrows(NullPointerException.class, () -> ThrowableQuadFunction.caught(null));
	}
	
	@Test
	void caughtWithDifferentParameterTypes() {
		ThrowableQuadFunction<String, Integer, Boolean, Double, String, Exception> function = (s, i, b, d) -> s + i + b + d;
		QuadFunction<String, Integer, Boolean, Double, String> caught = ThrowableQuadFunction.caught(function);
		
		assertEquals("test5true3.14", caught.apply("test", 5, true, 3.14));
	}
	
	@Test
	void caughtWithNullParameters() {
		ThrowableQuadFunction<String, String, String, String, String, Exception> function = (a, b, c, d) ->
			(a == null ? "null" : a) + (b == null ? "null" : b) + (c == null ? "null" : c) + (d == null ? "null" : d);
		QuadFunction<String, String, String, String, String> caught = ThrowableQuadFunction.caught(function);
		
		assertEquals("nullnullnullnull", caught.apply(null, null, null, null));
		assertEquals("anullnullnull", caught.apply("a", null, null, null));
	}
	
	@Test
	void caughtWithNullResult() {
		ThrowableQuadFunction<String, String, String, String, String, Exception> function = (a, b, c, d) -> null;
		QuadFunction<String, String, String, String, String> caught = ThrowableQuadFunction.caught(function);
		
		assertNull(caught.apply("a", "b", "c", "d"));
	}
	
	@Test
	void applyWithSuccessfulExecution() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> a * b * c * d;
		
		assertEquals(24, assertDoesNotThrow(() -> function.apply(1, 2, 3, 4)));
		assertEquals(0, assertDoesNotThrow(() -> function.apply(0, 1, 2, 3)));
		assertEquals(-24, assertDoesNotThrow(() -> function.apply(-1, 2, 3, 4)));
	}
	
	@Test
	void applyWithException() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> {
			if (a < 0 || b < 0 || c < 0 || d < 0) {
				throw new Exception("Negative values not allowed");
			}
			return a + b + c + d;
		};
		
		assertEquals(10, assertDoesNotThrow(() -> function.apply(1, 2, 3, 4)));
		
		Exception exception = assertThrows(Exception.class, () -> function.apply(-1, 2, 3, 4));
		assertEquals("Negative values not allowed", exception.getMessage());
		
		assertThrows(Exception.class, () -> function.apply(1, -2, 3, 4));
		assertThrows(Exception.class, () -> function.apply(1, 2, -3, 4));
		assertThrows(Exception.class, () -> function.apply(1, 2, 3, -4));
	}
	
	@Test
	void applyWithNullParameters() {
		ThrowableQuadFunction<String, String, String, String, String, Exception> function = (a, b, c, d) -> {
			if (a == null || b == null || c == null || d == null) {
				throw new Exception("Null parameters not allowed");
			}
			return a + b + c + d;
		};
		
		assertEquals("abcd", assertDoesNotThrow(() -> function.apply("a", "b", "c", "d")));
		assertThrows(Exception.class, () -> function.apply(null, "b", "c", "d"));
		assertThrows(Exception.class, () -> function.apply("a", null, "c", "d"));
		assertThrows(Exception.class, () -> function.apply("a", "b", null, "d"));
		assertThrows(Exception.class, () -> function.apply("a", "b", "c", null));
	}
	
	@Test
	void applyWithDifferentTypes() {
		ThrowableQuadFunction<String, Integer, Boolean, Double, String, Exception> function = (s, i, b, d) ->
			s + ":" + i + ":" + b + ":" + d;
		
		assertEquals("test:5:true:3.14", assertDoesNotThrow(() -> function.apply("test", 5, true, 3.14)));
	}
	
	@Test
	void applyReturnsNullResult() {
		ThrowableQuadFunction<String, String, String, String, String, Exception> function = (a, b, c, d) -> null;
		
		assertNull(assertDoesNotThrow(() -> function.apply("a", "b", "c", "d")));
	}
	
	@Test
	void andThenCombinesFunctions() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> a + b + c + d;
		ThrowableFunction<Integer, Integer, Exception> after = i -> i * 2;
		
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> combined = function.andThen(after);
		
		assertEquals(20, assertDoesNotThrow(() -> combined.apply(1, 2, 3, 4)));
		assertEquals(0, assertDoesNotThrow(() -> combined.apply(0, 0, 0, 0)));
	}
	
	@Test
	void andThenWithDifferentReturnType() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> a + b + c + d;
		ThrowableFunction<Integer, String, Exception> after = i -> "Result: " + i;
		
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, String, Exception> combined = function.andThen(after);
		
		assertEquals("Result: 10", assertDoesNotThrow(() -> combined.apply(1, 2, 3, 4)));
	}
	
	@Test
	void andThenWithMultipleChaining() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> a + b + c + d;
		ThrowableFunction<Integer, Integer, Exception> multiply = i -> i * 2;
		ThrowableFunction<Integer, String, Exception> toString = i -> "Final: " + i;
		
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, String, Exception> combined = function.andThen(multiply).andThen(toString);
		
		assertEquals("Final: 20", assertDoesNotThrow(() -> combined.apply(1, 2, 3, 4)));
	}
	
	@Test
	void andThenWithNullAfterFunction() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> a + b + c + d;
		
		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
	
	@Test
	void andThenWithExceptionInOriginalFunction() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> {
			throw new Exception("Original function failed");
		};
		ThrowableFunction<Integer, Integer, Exception> after = i -> i * 2;
		
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> combined = function.andThen(after);
		
		Exception exception = assertThrows(Exception.class, () -> combined.apply(1, 2, 3, 4));
		assertEquals("Original function failed", exception.getMessage());
	}
	
	@Test
	void andThenWithExceptionInAfterFunction() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> a + b + c + d;
		ThrowableFunction<Integer, Integer, Exception> after = i -> {
			throw new Exception("After function failed");
		};
		
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> combined = function.andThen(after);
		
		Exception exception = assertThrows(Exception.class, () -> combined.apply(1, 2, 3, 4));
		assertEquals("After function failed", exception.getMessage());
	}
	
	@Test
	void andThenWithNullResult() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> null;
		ThrowableFunction<Integer, String, Exception> after = i -> i == null ? "null" : i.toString();
		
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, String, Exception> combined = function.andThen(after);
		
		assertEquals("null", assertDoesNotThrow(() -> combined.apply(1, 2, 3, 4)));
	}
	
	@Test
	void andThenWithIdentityFunction() {
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> function = (a, b, c, d) -> a + b + c + d;
		ThrowableFunction<Integer, Integer, Exception> identity = i -> i;
		
		ThrowableQuadFunction<Integer, Integer, Integer, Integer, Integer, Exception> combined = function.andThen(identity);
		
		assertEquals(10, assertDoesNotThrow(() -> combined.apply(1, 2, 3, 4)));
	}
}
