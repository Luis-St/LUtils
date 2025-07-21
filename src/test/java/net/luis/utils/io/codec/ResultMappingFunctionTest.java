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
 * Test class for {@link ResultMappingFunction}.<br>
 *
 * @author Luis-St
 */
class ResultMappingFunctionTest {
	
	@Test
	void directNullChecks() {
		assertThrows(NullPointerException.class, () -> ResultMappingFunction.direct(null));
	}
	
	@Test
	void directWithSuccessfulResult() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.direct(Integer::parseInt);
		
		Result<Integer> result = function.apply(Result.success("42"));
		assertTrue(result.isSuccess());
		assertEquals(42, result.orThrow());
	}
	
	@Test
	void directWithErrorResult() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.direct(Integer::parseInt);
		
		Result<Integer> result = function.apply(Result.error("parse error"));
		assertTrue(result.isError());
		assertEquals("parse error", result.errorOrThrow());
	}
	
	@Test
	void directWithFunctionThrowingException() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.direct(s -> {
			if ("invalid".equals(s)) {
				throw new NumberFormatException("Invalid number");
			}
			return Integer.parseInt(s);
		});
		
		assertThrows(NumberFormatException.class, () -> function.apply(Result.success("invalid")));
	}
	
	@Test
	void directWithNullResult() {
		ResultMappingFunction<String, String> function = ResultMappingFunction.direct(s -> null);
		
		Result<String> result = function.apply(Result.success("test"));
		assertTrue(result.isSuccess());
		assertNull(result.orThrow());
	}
	
	@Test
	void throwableNullChecks() {
		assertThrows(NullPointerException.class, () -> ResultMappingFunction.throwable(null));
	}
	
	@Test
	void throwableWithSuccessfulResult() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.throwable(Integer::parseInt);
		
		Result<Integer> result = function.apply(Result.success("42"));
		assertTrue(result.isSuccess());
		assertEquals(42, result.orThrow());
	}
	
	@Test
	void throwableWithErrorResult() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.throwable(Integer::parseInt);
		
		Result<Integer> result = function.apply(Result.error("parse error"));
		assertTrue(result.isError());
		assertEquals("parse error", result.errorOrThrow());
	}
	
	@Test
	void throwableWithFunctionThrowingException() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.throwable(s -> {
			throw new NumberFormatException("Invalid number: " + s);
		});
		
		Result<Integer> result = function.apply(Result.success("invalid"));
		assertTrue(result.isError());
		assertEquals("Invalid number: invalid", result.errorOrThrow());
	}
	
	@Test
	void throwableWithFunctionThrowingNullPointerException() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.throwable(s -> {
			throw new NullPointerException();
		});
		
		Result<Integer> result = function.apply(Result.success("test"));
		assertTrue(result.isError());
		assertEquals("Unknown error, no message provided", result.errorOrThrow());
	}
	
	@Test
	void throwableWithNullResult() {
		ResultMappingFunction<String, String> function = ResultMappingFunction.throwable(s -> null);
		
		Result<String> result = function.apply(Result.success("test"));
		assertTrue(result.isSuccess());
		assertNull(result.orThrow());
	}
	
	@Test
	void applyNullChecks() {
		ResultMappingFunction<String, Integer> function = ResultMappingFunction.direct(Integer::parseInt);
		
		assertThrows(NullPointerException.class, () -> function.apply(null));
	}
	
	@Test
	void composingResultMappingFunctions() {
		ResultMappingFunction<String, Integer> parseInt = ResultMappingFunction.direct(Integer::parseInt);
		ResultMappingFunction<Integer, String> toString = ResultMappingFunction.direct(Object::toString);
		
		Result<String> result = toString.apply(parseInt.apply(Result.success("42")));
		assertTrue(result.isSuccess());
		assertEquals("42", result.orThrow());
	}
	
	@Test
	void composingWithErrorPropagation() {
		ResultMappingFunction<String, Integer> parseInt = ResultMappingFunction.direct(Integer::parseInt);
		ResultMappingFunction<Integer, String> toString = ResultMappingFunction.direct(Object::toString);
		
		Result<String> result = toString.apply(parseInt.apply(Result.error("initial error")));
		assertTrue(result.isError());
		assertEquals("initial error", result.errorOrThrow());
	}
}
