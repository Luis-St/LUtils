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

package net.luis.utils.util.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ResultingFunction}.<br>
 *
 * @author Luis-St
 */
class ResultingFunctionTest {
	
	@Test
	void identityReturnsInputAsSuccess() {
		ResultingFunction<String, String> identity = ResultingFunction.identity();
		Result<String> result = identity.apply("test");
		
		assertTrue(result.isSuccess());
		assertEquals("test", result.resultOrThrow());
	}
	
	@Test
	void identityWithNullValue() {
		ResultingFunction<String, String> identity = ResultingFunction.identity();
		Result<String> result = identity.apply(null);
		
		assertTrue(result.isSuccess());
		assertNull(result.resultOrThrow());
	}
	
	@Test
	void directNullThrows() {
		assertThrows(NullPointerException.class, () -> ResultingFunction.direct(null));
	}
	
	@Test
	void directWrapsFunction() {
		ResultingFunction<String, Integer> function = ResultingFunction.direct(String::length);
		Result<Integer> result = function.apply("hello");
		
		assertTrue(result.isSuccess());
		assertEquals(5, result.resultOrThrow());
	}
	
	@Test
	void directWithTransformation() {
		ResultingFunction<Integer, String> function = ResultingFunction.direct(i -> "Number: " + i);
		Result<String> result = function.apply(42);
		
		assertTrue(result.isSuccess());
		assertEquals("Number: 42", result.resultOrThrow());
	}
	
	@Test
	void throwableNullThrows() {
		assertThrows(NullPointerException.class, () -> ResultingFunction.throwable(null));
	}
	
	@Test
	void throwableSuccessfulExecution() {
		ResultingFunction<String, Integer> function = ResultingFunction.throwable(Integer::parseInt);
		Result<Integer> result = function.apply("42");
		
		assertTrue(result.isSuccess());
		assertEquals(42, result.resultOrThrow());
	}
	
	@Test
	void throwableExceptionReturnsError() {
		ResultingFunction<String, Integer> function = ResultingFunction.throwable(Integer::parseInt);
		Result<Integer> result = function.apply("not a number");
		
		assertTrue(result.isError());
		assertNotNull(result.errorOrThrow());
	}
	
	@Test
	void throwableWithNullMessage() {
		ResultingFunction<String, String> function = ResultingFunction.throwable(s -> {
			throw new RuntimeException((String) null);
		});
		Result<String> result = function.apply("test");
		
		assertTrue(result.isError());
		assertEquals("Unknown error, no message provided", result.errorOrThrow());
	}
	
	@Test
	void applyReturnsResult() {
		ResultingFunction<Integer, Integer> doubler = value -> Result.success(value * 2);
		Result<Integer> result = doubler.apply(5);
		
		assertTrue(result.isSuccess());
		assertEquals(10, result.resultOrThrow());
	}
	
	@Test
	void applyCanReturnError() {
		ResultingFunction<Integer, Integer> validator = value -> {
			if (value < 0) {
				return Result.error("Value must be non-negative");
			}
			return Result.success(value);
		};
		
		Result<Integer> success = validator.apply(5);
		assertTrue(success.isSuccess());
		
		Result<Integer> error = validator.apply(-1);
		assertTrue(error.isError());
		assertEquals("Value must be non-negative", error.errorOrThrow());
	}
}
