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

import net.luis.utils.function.TriFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThrowableTriFunction}.<br>
 *
 * @author Luis-St
 */
class ThrowableTriFunctionTest {
	
	@Test
	void caughtConvertsSuccessfulFunction() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> a + b + c;
		TriFunction<Integer, Integer, Integer, Integer> caught = ThrowableTriFunction.caught(function);
		
		assertEquals(6, caught.apply(1, 2, 3));
		assertEquals(0, caught.apply(0, 0, 0));
		assertEquals(-3, caught.apply(-1, -2, 0));
	}
	
	@Test
	void caughtWrapsExceptionInRuntimeException() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> {
			throw new Exception("Test exception");
		};
		TriFunction<Integer, Integer, Integer, Integer> caught = ThrowableTriFunction.caught(function);
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> caught.apply(1, 2, 3));
		assertInstanceOf(Exception.class, exception.getCause());
		assertEquals("Test exception", exception.getCause().getMessage());
	}
	
	@Test
	void caughtWithNullFunction() {
		assertThrows(NullPointerException.class, () -> ThrowableTriFunction.caught(null));
	}
	
	@Test
	void caughtWithDifferentParameterTypes() {
		ThrowableTriFunction<String, Integer, Boolean, String, Exception> function = (s, i, b) -> s + i + b;
		TriFunction<String, Integer, Boolean, String> caught = ThrowableTriFunction.caught(function);
		
		assertEquals("test5true", caught.apply("test", 5, true));
	}
	
	@Test
	void caughtWithNullParameters() {
		ThrowableTriFunction<String, String, String, String, Exception> function = (a, b, c) ->
			(a == null ? "null" : a) + (b == null ? "null" : b) + (c == null ? "null" : c);
		TriFunction<String, String, String, String> caught = ThrowableTriFunction.caught(function);
		
		assertEquals("nullnullnull", caught.apply(null, null, null));
		assertEquals("anullnull", caught.apply("a", null, null));
	}
	
	@Test
	void caughtWithNullResult() {
		ThrowableTriFunction<String, String, String, String, Exception> function = (a, b, c) -> null;
		TriFunction<String, String, String, String> caught = ThrowableTriFunction.caught(function);
		
		assertNull(caught.apply("a", "b", "c"));
	}
	
	@Test
	void applyWithSuccessfulExecution() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> a * b * c;
		
		assertEquals(6, assertDoesNotThrow(() -> function.apply(1, 2, 3)));
		assertEquals(0, assertDoesNotThrow(() -> function.apply(0, 1, 2)));
		assertEquals(-6, assertDoesNotThrow(() -> function.apply(-1, 2, 3)));
	}
	
	@Test
	void applyWithException() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> {
			if (a < 0 || b < 0 || c < 0) {
				throw new Exception("Negative values not allowed");
			}
			return a + b + c;
		};
		
		assertEquals(6, assertDoesNotThrow(() -> function.apply(1, 2, 3)));
		
		Exception exception = assertThrows(Exception.class, () -> function.apply(-1, 2, 3));
		assertEquals("Negative values not allowed", exception.getMessage());
		
		assertThrows(Exception.class, () -> function.apply(1, -2, 3));
		assertThrows(Exception.class, () -> function.apply(1, 2, -3));
	}
	
	@Test
	void applyWithNullParameters() {
		ThrowableTriFunction<String, String, String, String, Exception> function = (a, b, c) -> {
			if (a == null || b == null || c == null) {
				throw new Exception("Null parameters not allowed");
			}
			return a + b + c;
		};
		
		assertEquals("abc", assertDoesNotThrow(() -> function.apply("a", "b", "c")));
		assertThrows(Exception.class, () -> function.apply(null, "b", "c"));
		assertThrows(Exception.class, () -> function.apply("a", null, "c"));
		assertThrows(Exception.class, () -> function.apply("a", "b", null));
	}
	
	@Test
	void applyWithDifferentTypes() {
		ThrowableTriFunction<String, Integer, Boolean, String, Exception> function = (s, i, b) -> s + ":" + i + ":" + b;
		
		assertEquals("test:5:true", assertDoesNotThrow(() -> function.apply("test", 5, true)));
	}
	
	@Test
	void applyReturnsNullResult() {
		ThrowableTriFunction<String, String, String, String, Exception> function = (a, b, c) -> null;
		
		assertNull(assertDoesNotThrow(() -> function.apply("a", "b", "c")));
	}
	
	@Test
	void andThenCombinesFunctions() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> a + b + c;
		ThrowableFunction<Integer, Integer, Exception> after = i -> i * 2;
		
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> combined = function.andThen(after);
		
		assertEquals(12, assertDoesNotThrow(() -> combined.apply(1, 2, 3)));
		assertEquals(0, assertDoesNotThrow(() -> combined.apply(0, 0, 0)));
	}
	
	@Test
	void andThenWithDifferentReturnType() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> a + b + c;
		ThrowableFunction<Integer, String, Exception> after = i -> "Result: " + i;
		
		ThrowableTriFunction<Integer, Integer, Integer, String, Exception> combined = function.andThen(after);
		
		assertEquals("Result: 6", assertDoesNotThrow(() -> combined.apply(1, 2, 3)));
	}
	
	@Test
	void andThenWithMultipleChaining() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> a + b + c;
		ThrowableFunction<Integer, Integer, Exception> multiply = i -> i * 2;
		ThrowableFunction<Integer, String, Exception> toString = i -> "Final: " + i;
		
		ThrowableTriFunction<Integer, Integer, Integer, String, Exception> combined = function.andThen(multiply).andThen(toString);
		
		assertEquals("Final: 12", assertDoesNotThrow(() -> combined.apply(1, 2, 3)));
	}
	
	@Test
	void andThenWithNullAfterFunction() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> a + b + c;
		
		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
	
	@Test
	void andThenWithExceptionInOriginalFunction() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> {
			throw new Exception("Original function failed");
		};
		ThrowableFunction<Integer, Integer, Exception> after = i -> i * 2;
		
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> combined = function.andThen(after);
		
		Exception exception = assertThrows(Exception.class, () -> combined.apply(1, 2, 3));
		assertEquals("Original function failed", exception.getMessage());
	}
	
	@Test
	void andThenWithExceptionInAfterFunction() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> a + b + c;
		ThrowableFunction<Integer, Integer, Exception> after = i -> {
			throw new Exception("After function failed");
		};
		
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> combined = function.andThen(after);
		
		Exception exception = assertThrows(Exception.class, () -> combined.apply(1, 2, 3));
		assertEquals("After function failed", exception.getMessage());
	}
	
	@Test
	void andThenWithNullResult() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> null;
		ThrowableFunction<Integer, String, Exception> after = i -> i == null ? "null" : i.toString();
		
		ThrowableTriFunction<Integer, Integer, Integer, String, Exception> combined = function.andThen(after);
		
		assertEquals("null", assertDoesNotThrow(() -> combined.apply(1, 2, 3)));
	}
	
	@Test
	void andThenWithIdentityFunction() {
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> function = (a, b, c) -> a + b + c;
		ThrowableFunction<Integer, Integer, Exception> identity = i -> i;
		
		ThrowableTriFunction<Integer, Integer, Integer, Integer, Exception> combined = function.andThen(identity);
		
		assertEquals(6, assertDoesNotThrow(() -> combined.apply(1, 2, 3)));
	}
}
