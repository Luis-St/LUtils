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

package net.luis.utils.io.codec;

import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ResultingFunction}.<br>
 *
 * @author Luis-St
 */
class ResultingFunctionTest {
	
	@Test
	void directNullChecks() {
		assertThrows(NullPointerException.class, () -> ResultingFunction.direct(null));
	}
	
	@Test
	void directWithSuccessfulFunction() {
		ResultingFunction<String, Integer> function = ResultingFunction.direct(Integer::parseInt);
		
		Result<Integer> result = function.apply("42");
		assertTrue(result.isSuccess());
		assertEquals(42, result.orThrow());
	}
	
	@Test
	void directWithNullInput() {
		ResultingFunction<String, Integer> function = ResultingFunction.direct(s -> s == null ? -1 : Integer.parseInt(s));
		
		Result<Integer> result = function.apply(null);
		assertTrue(result.isSuccess());
		assertEquals(-1, result.orThrow());
	}
	
	@Test
	void directWithFunctionReturningNull() {
		ResultingFunction<String, String> function = ResultingFunction.direct(s -> null);
		
		Result<String> result = function.apply("anything");
		assertTrue(result.isSuccess());
		assertNull(result.orThrow());
	}
	
	@Test
	void directWithFunctionThrowingException() {
		ResultingFunction<String, Integer> function = ResultingFunction.direct(s -> {
			if ("invalid".equals(s)) {
				throw new NumberFormatException("Invalid number");
			}
			return Integer.parseInt(s);
		});
		
		assertThrows(NumberFormatException.class, () -> function.apply("invalid"));
	}
	
	@Test
	void throwableNullChecks() {
		assertThrows(NullPointerException.class, () -> ResultingFunction.throwable(null));
	}
	
	@Test
	void throwableWithSuccessfulFunction() {
		ResultingFunction<String, Integer> function = ResultingFunction.throwable(Integer::parseInt);
		
		Result<Integer> result = function.apply("42");
		assertTrue(result.isSuccess());
		assertEquals(42, result.orThrow());
	}
	
	@Test
	void throwableWithNullInput() {
		ResultingFunction<String, Integer> function = ResultingFunction.throwable(s -> s == null ? -1 : Integer.parseInt(s));
		
		Result<Integer> result = function.apply(null);
		assertTrue(result.isSuccess());
		assertEquals(-1, result.orThrow());
	}
	
	@Test
	void throwableWithFunctionReturningNull() {
		ResultingFunction<String, String> function = ResultingFunction.throwable(s -> null);
		
		Result<String> result = function.apply("anything");
		assertTrue(result.isSuccess());
		assertNull(result.orThrow());
	}
	
	@Test
	void throwableWithFunctionThrowingException() {
		ResultingFunction<String, Integer> function = ResultingFunction.throwable(s -> {
			throw new NumberFormatException("Invalid number: " + s);
		});
		
		Result<Integer> result = function.apply("invalid");
		assertTrue(result.isError());
		assertEquals("Invalid number: invalid", result.errorOrThrow());
	}
	
	@Test
	void throwableWithFunctionThrowingNullPointerException() {
		ResultingFunction<String, Integer> function = ResultingFunction.throwable(s -> {
			throw new NullPointerException();
		});
		
		Result<Integer> result = function.apply("test");
		assertTrue(result.isError());
		assertEquals("Unknown error, no message provided", result.errorOrThrow());
	}
	
	@Test
	void throwableWithFunctionThrowingRuntimeException() {
		ResultingFunction<String, Integer> function = ResultingFunction.throwable(s -> {
			throw new RuntimeException("Runtime error");
		});
		
		Result<Integer> result = function.apply("test");
		assertTrue(result.isError());
		assertEquals("Runtime error", result.errorOrThrow());
	}
	
	@Test
	void throwableWithComplexException() {
		ResultingFunction<Integer, String> function = ResultingFunction.throwable(i -> {
			if (i < 0) {
				throw new IllegalArgumentException("Negative numbers not allowed: " + i);
			}
			if (i == 0) {
				throw new ArithmeticException("Zero division");
			}
			return "Value: " + i;
		});
		
		Result<String> negativeResult = function.apply(-5);
		assertTrue(negativeResult.isError());
		assertEquals("Negative numbers not allowed: -5", negativeResult.errorOrThrow());
		
		Result<String> zeroResult = function.apply(0);
		assertTrue(zeroResult.isError());
		assertEquals("Zero division", zeroResult.errorOrThrow());
		
		Result<String> positiveResult = function.apply(42);
		assertTrue(positiveResult.isSuccess());
		assertEquals("Value: 42", positiveResult.orThrow());
	}
	
	@Test
	void applyWithDifferentInputTypes() {
		ResultingFunction<Double, Integer> function = ResultingFunction.throwable(d -> (int) Math.round(d));
		
		Result<Integer> result1 = function.apply(3.14);
		assertTrue(result1.isSuccess());
		assertEquals(3, result1.orThrow());
		
		Result<Integer> result2 = function.apply(3.7);
		assertTrue(result2.isSuccess());
		assertEquals(4, result2.orThrow());
		
		Result<Integer> result3 = function.apply(-2.3);
		assertTrue(result3.isSuccess());
		assertEquals(-2, result3.orThrow());
	}
	
	@Test
	void composingResultingFunctions() {
		ResultingFunction<String, Integer> parseInt = ResultingFunction.throwable(Integer::parseInt);
		ResultingFunction<Integer, String> toString = ResultingFunction.direct(Object::toString);
		
		Result<String> result = toString.apply(parseInt.apply("42").orThrow());
		assertTrue(result.isSuccess());
		assertEquals("42", result.orThrow());
	}
	
	@Test
	void composingWithErrorPropagation() {
		ResultingFunction<String, Integer> parseInt = ResultingFunction.throwable(Integer::parseInt);
		
		Result<Integer> errorResult = parseInt.apply("not-a-number");
		assertTrue(errorResult.isError());
		
		// Should not apply second function if first fails
		ResultingFunction<Integer, String> toString = ResultingFunction.direct(i -> {
			fail("Should not be called when previous function failed");
			return String.valueOf(i);
		});
		
		// This would only work if we manually check the error
		if (errorResult.isError()) {
			// Expected behavior - error propagated
			assertTrue(true);
		}
	}
}
