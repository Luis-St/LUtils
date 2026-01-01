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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TriFunction}.<br>
 *
 * @author Luis-St
 */
class TriFunctionTest {
	
	@Test
	void applyWithCorrectParameters() {
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
	void applyWithNullParameters() {
		TriFunction<String, String, String, String> function = (a, b, c) -> {
			assertNull(a);
			assertNull(b);
			assertNull(c);
			return "result";
		};
		
		assertEquals("result", function.apply(null, null, null));
	}
	
	@Test
	void applyWithMixedParameters() {
		TriFunction<Object, Object, Object, String> function = (a, b, c) -> {
			assertEquals("string", a);
			assertEquals(42, b);
			assertNull(c);
			return "mixed";
		};
		
		assertEquals("mixed", function.apply("string", 42, null));
	}
	
	@Test
	void applyWithDifferentTypes() {
		TriFunction<Integer, String, Boolean, String> function = (i, s, b) -> {
			assertEquals(1, i);
			assertEquals("test", s);
			assertTrue(b);
			return i + s + b;
		};
		
		assertEquals("1testtrue", function.apply(1, "test", true));
	}
	
	@Test
	void applyReturnsNullResult() {
		TriFunction<String, String, String, String> function = (a, b, c) -> null;
		
		assertNull(function.apply("a", "b", "c"));
	}
	
	@Test
	void applyWithZeroValues() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> a + b + c;
		
		assertEquals(0, function.apply(0, 0, 0));
	}
	
	@Test
	void applyWithNegativeValues() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> a + b + c;
		
		assertEquals(-6, function.apply(-1, -2, -3));
	}
	
	@Test
	void andThenCombinesFunctions() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> a + b + c;
		
		TriFunction<Integer, Integer, Integer, Integer> combined = function.andThen(i -> i * 2);
		
		assertEquals(12, combined.apply(1, 2, 3));
		assertDoesNotThrow(() -> combined.apply(1, 2, 3));
	}
	
	@Test
	void andThenWithDifferentReturnType() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> a + b + c;
		
		TriFunction<Integer, Integer, Integer, String> combined = function.andThen(i -> "Result: " + i);
		
		assertEquals("Result: 6", combined.apply(1, 2, 3));
	}
	
	@Test
	void andThenWithMultipleChaining() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> a + b + c;
		
		TriFunction<Integer, Integer, Integer, String> combined = function
			.andThen(i -> i * 2)
			.andThen(i -> "Final: " + i);
		
		assertEquals("Final: 12", combined.apply(1, 2, 3));
	}
	
	@Test
	void andThenWithNullAfterFunction() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> a + b + c;
		
		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
	
	@Test
	void andThenWithIdentityFunction() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> a + b + c;
		
		TriFunction<Integer, Integer, Integer, Integer> combined = function.andThen(i -> i);
		
		assertEquals(6, combined.apply(1, 2, 3));
	}
	
	@Test
	void andThenWithExceptionInOriginalFunction() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> {
			throw new RuntimeException("Original function failed");
		};
		
		TriFunction<Integer, Integer, Integer, Integer> combined = function.andThen(i -> i * 2);
		
		assertThrows(RuntimeException.class, () -> combined.apply(1, 2, 3));
	}
	
	@Test
	void andThenWithExceptionInAfterFunction() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> a + b + c;
		
		TriFunction<Integer, Integer, Integer, Integer> combined = function.andThen(i -> {
			throw new RuntimeException("After function failed");
		});
		
		assertThrows(RuntimeException.class, () -> combined.apply(1, 2, 3));
	}
	
	@Test
	void andThenWithNullResult() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> null;
		
		TriFunction<Integer, Integer, Integer, String> combined = function.andThen(i ->
			i == null ? "null" : i.toString());
		
		assertEquals("null", combined.apply(1, 2, 3));
	}
}
