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

package net.luis.utils.util.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Result}.<br>
 *
 * @author Luis-St
 */
class ResultTest {
	
	@Test
	void successWithoutValueCreatesSuccessfulResult() {
		Result<Integer> result = Result.success();
		assertNotNull(result);
		assertTrue(result.isSuccess());
		assertFalse(result.isError());
		assertFalse(result.isPartial());
	}
	
	@Test
	void successCreatesSuccessfulResult() {
		Result<Integer> result = Result.success(100);
		assertNotNull(result);
		assertTrue(result.isSuccess());
		assertFalse(result.isError());
		assertFalse(result.isPartial());
	}
	
	@Test
	void successAcceptsNullValue() {
		Result<String> result = Result.success(null);
		assertNotNull(result);
		assertTrue(result.isSuccess());
		assertNull(result.resultOrThrow());
	}
	
	@Test
	void errorCreatesFailedResult() {
		Result<Integer> result = Result.error("Something went wrong");
		assertNotNull(result);
		assertFalse(result.isSuccess());
		assertTrue(result.isError());
		assertFalse(result.isPartial());
	}
	
	@Test
	void errorRejectsNullMessage() {
		assertThrows(NullPointerException.class, () -> Result.error(null));
	}
	
	@Test
	void partialCreatesPartialResult() {
		Result<Integer> result = Result.partial(50, "Incomplete data");
		assertNotNull(result);
		assertFalse(result.isSuccess());
		assertFalse(result.isError());
		assertTrue(result.isPartial());
	}
	
	@Test
	void partialAcceptsNullValue() {
		Result<String> result = Result.partial(null, "No value but error");
		assertNotNull(result);
		assertTrue(result.isPartial());
		assertNull(result.resultOrThrow());
		assertEquals("No value but error", result.errorOrThrow());
	}
	
	@Test
	void partialRejectsNullErrorMessage() {
		assertThrows(NullPointerException.class, () -> Result.partial(100, null));
	}
	
	@Test
	void successHasValueButNoError() {
		Result<Integer> result = Result.success(100);
		assertTrue(result.hasValue());
		assertFalse(result.hasError());
	}
	
	@Test
	void errorHasNoValueButHasError() {
		Result<Integer> result = Result.error("failed");
		assertFalse(result.hasValue());
		assertTrue(result.hasError());
	}
	
	@Test
	void partialHasValueAndError() {
		Result<Integer> result = Result.partial(50, "warning");
		assertTrue(result.hasValue());
		assertTrue(result.hasError());
	}
	
	@Test
	void successErrorAndPartialAreMutuallyExclusive() {
		Result<Integer> success = Result.success(100);
		Result<Integer> error = Result.error("failed");
		Result<Integer> partial = Result.partial(50, "warning");
		
		assertTrue(success.isSuccess() && !success.isError() && !success.isPartial());
		assertTrue(error.isError() && !error.isSuccess() && !error.isPartial());
		assertTrue(partial.isPartial() && !partial.isSuccess() && !partial.isError());
	}
}
