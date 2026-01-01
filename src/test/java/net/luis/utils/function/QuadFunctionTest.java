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
 * Test class for {@link QuadFunction}.<br>
 *
 * @author Luis-St
 */
class QuadFunctionTest {
	
	@Test
	void applyWithCorrectParameters() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> {
			assertEquals(1, a);
			assertEquals(2, b);
			assertEquals(3, c);
			assertEquals(4, d);
			return a + b + c + d;
		};
		
		assertEquals(10, function.apply(1, 2, 3, 4));
		assertDoesNotThrow(() -> function.apply(1, 2, 3, 4));
	}
	
	@Test
	void applyWithNullParameters() {
		QuadFunction<String, String, String, String, String> function = (a, b, c, d) -> {
			assertNull(a);
			assertNull(b);
			assertNull(c);
			assertNull(d);
			return "result";
		};
		
		assertEquals("result", function.apply(null, null, null, null));
	}
	
	@Test
	void applyWithMixedParameters() {
		QuadFunction<Object, Object, Object, Object, String> function = (a, b, c, d) -> {
			assertEquals("string", a);
			assertEquals(42, b);
			assertNull(c);
			assertTrue((Boolean) d);
			return "mixed";
		};
		
		assertEquals("mixed", function.apply("string", 42, null, true));
	}
	
	@Test
	void applyWithDifferentTypes() {
		QuadFunction<Integer, String, Boolean, Double, String> function = (i, s, b, d) -> {
			assertEquals(1, i);
			assertEquals("test", s);
			assertTrue(b);
			assertEquals(3.14, d, 0.001);
			return i + s + b + d;
		};
		
		assertEquals("1testtrue3.14", function.apply(1, "test", true, 3.14));
	}
	
	@Test
	void applyReturnsNullResult() {
		QuadFunction<String, String, String, String, String> function = (a, b, c, d) -> null;
		
		assertNull(function.apply("a", "b", "c", "d"));
	}
	
	@Test
	void applyWithZeroValues() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> a + b + c + d;
		
		assertEquals(0, function.apply(0, 0, 0, 0));
	}
	
	@Test
	void applyWithNegativeValues() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> a + b + c + d;
		
		assertEquals(-10, function.apply(-1, -2, -3, -4));
	}
	
	@Test
	void andThenCombinesFunctions() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> a + b + c + d;
		
		QuadFunction<Integer, Integer, Integer, Integer, Integer> combined = function.andThen(i -> i * 2);
		
		assertEquals(20, combined.apply(1, 2, 3, 4));
		assertDoesNotThrow(() -> combined.apply(1, 2, 3, 4));
	}
	
	@Test
	void andThenWithDifferentReturnType() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> a + b + c + d;
		
		QuadFunction<Integer, Integer, Integer, Integer, String> combined = function.andThen(i -> "Result: " + i);
		
		assertEquals("Result: 10", combined.apply(1, 2, 3, 4));
	}
	
	@Test
	void andThenWithMultipleChaining() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> a + b + c + d;
		
		QuadFunction<Integer, Integer, Integer, Integer, String> combined = function
			.andThen(i -> i * 2)
			.andThen(i -> "Final: " + i);
		
		assertEquals("Final: 20", combined.apply(1, 2, 3, 4));
	}
	
	@Test
	void andThenWithNullAfterFunction() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> a + b + c + d;
		
		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
	
	@Test
	void andThenWithIdentityFunction() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> a + b + c + d;
		
		QuadFunction<Integer, Integer, Integer, Integer, Integer> combined = function.andThen(i -> i);
		
		assertEquals(10, combined.apply(1, 2, 3, 4));
	}
	
	@Test
	void andThenWithExceptionInOriginalFunction() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> {
			throw new RuntimeException("Original function failed");
		};
		
		QuadFunction<Integer, Integer, Integer, Integer, Integer> combined = function.andThen(i -> i * 2);
		
		assertThrows(RuntimeException.class, () -> combined.apply(1, 2, 3, 4));
	}
	
	@Test
	void andThenWithExceptionInAfterFunction() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> a + b + c + d;
		
		QuadFunction<Integer, Integer, Integer, Integer, Integer> combined = function.andThen(i -> {
			throw new RuntimeException("After function failed");
		});
		
		assertThrows(RuntimeException.class, () -> combined.apply(1, 2, 3, 4));
	}
	
	@Test
	void andThenWithNullResult() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> null;
		
		QuadFunction<Integer, Integer, Integer, Integer, String> combined = function.andThen(i ->
			i == null ? "null" : i.toString());
		
		assertEquals("null", combined.apply(1, 2, 3, 4));
	}
}
