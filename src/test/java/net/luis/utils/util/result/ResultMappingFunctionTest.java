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
 * Test class for {@link ResultMappingFunction}.<br>
 *
 * @author Luis-St
 */
class ResultMappingFunctionTest {
	
	@Test
	void identityReturnsInput() {
		ResultMappingFunction<String, String> identity = ResultMappingFunction.identity();
		
		Result<String> successInput = Result.success("test");
		Result<String> successOutput = identity.apply(successInput);
		assertTrue(successOutput.isSuccess());
		assertEquals("test", successOutput.resultOrThrow());
		
		Result<String> errorInput = Result.error("error message");
		Result<String> errorOutput = identity.apply(errorInput);
		assertTrue(errorOutput.isError());
		assertEquals("error message", errorOutput.errorOrThrow());
	}
	
	@Test
	void directNullThrows() {
		assertThrows(NullPointerException.class, () -> ResultMappingFunction.direct(null));
	}
	
	@Test
	void directMapsSuccessValue() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.direct(String::length);
		
		Result<String> input = Result.success("hello");
		Result<Integer> output = function.apply(input);
		
		assertTrue(output.isSuccess());
		assertEquals(5, output.resultOrThrow());
	}
	
	@Test
	void directPropagatesError() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.direct(String::length);
		
		Result<String> input = Result.error("original error");
		Result<Integer> output = function.apply(input);
		
		assertTrue(output.isError());
		assertEquals("original error", output.errorOrThrow());
	}
	
	@Test
	void throwableNullThrows() {
		assertThrows(NullPointerException.class, () -> ResultMappingFunction.throwable(null));
	}
	
	@Test
	void throwableSuccessfulExecution() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.throwable(Integer::parseInt);
		
		Result<String> input = Result.success("42");
		Result<Integer> output = function.apply(input);
		
		assertTrue(output.isSuccess());
		assertEquals(42, output.resultOrThrow());
	}
	
	@Test
	void throwableExceptionReturnsError() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.throwable(Integer::parseInt);
		
		Result<String> input = Result.success("not a number");
		Result<Integer> output = function.apply(input);
		
		assertTrue(output.isError());
		assertNotNull(output.errorOrThrow());
	}
	
	@Test
	void throwablePropagatesOriginalError() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.throwable(Integer::parseInt);
		
		Result<String> input = Result.error("original error");
		Result<Integer> output = function.apply(input);
		
		assertTrue(output.isError());
		assertEquals("original error", output.errorOrThrow());
	}
	
	@Test
	void throwableWithNullMessage() {
		ResultMappingFunction<String, String> function = ResultMappingFunction.throwable(s -> {
			throw new RuntimeException((String) null);
		});
		
		Result<String> input = Result.success("test");
		Result<String> output = function.apply(input);
		
		assertTrue(output.isError());
		assertEquals("Unknown error, no message provided", output.errorOrThrow());
	}
	
	@Test
	void applyReturnsResult() {
		ResultMappingFunction<Integer, Integer> doubler = result -> {
			if (result.isError()) {
				return Result.error(result.errorOrThrow());
			}
			return Result.success(result.resultOrThrow() * 2);
		};
		
		Result<Integer> input = Result.success(5);
		Result<Integer> output = doubler.apply(input);
		
		assertTrue(output.isSuccess());
		assertEquals(10, output.resultOrThrow());
	}
}
