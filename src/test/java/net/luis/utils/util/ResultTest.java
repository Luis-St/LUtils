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

package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Result}
 *
 * @author Luis-St
 */
class ResultTest {
	
	@Test
	void successCreatesSuccessfulResult() {
		Result<Integer> result = Result.success(100);
		assertNotNull(result);
		assertTrue(result.isSuccess());
		assertFalse(result.isError());
	}
	
	@Test
	void successAcceptsNullValue() {
		Result<String> result = Result.success(null);
		assertNotNull(result);
		assertTrue(result.isSuccess());
		assertNull(result.orThrow());
	}
	
	@Test
	void errorCreatesFailedResult() {
		Result<Integer> result = Result.error("Something went wrong");
		assertNotNull(result);
		assertFalse(result.isSuccess());
		assertTrue(result.isError());
	}
	
	@Test
	void errorRejectsNullMessage() {
		assertThrows(NullPointerException.class, () -> Result.error(null));
	}
	
	@Test
	void getReturnsUnderlyingEither() {
		Result<Integer> success = Result.success(42);
		Result<Integer> error = Result.error("failed");
		
		assertEquals(Either.left(42), success.get());
		assertEquals(Either.right("failed"), error.get());
	}
	
	@Test
	void isSuccessIdentifiesSuccessfulResults() {
		assertTrue(Result.success(100).isSuccess());
		assertTrue(Result.success(null).isSuccess());
		assertFalse(Result.error("error").isSuccess());
	}
	
	@Test
	void isErrorIdentifiesFailedResults() {
		assertFalse(Result.success(100).isError());
		assertFalse(Result.success(null).isError());
		assertTrue(Result.error("error").isError());
	}
	
	@Test
	void resultReturnsOptionalOfValue() {
		assertEquals(Optional.of(100), Result.success(100).result());
		assertEquals(Optional.empty(), Result.<Integer>success(null).result());
		assertEquals(Optional.empty(), Result.<Integer>error("failed").result());
	}
	
	@Test
	void errorReturnsOptionalOfErrorMessage() {
		assertEquals(Optional.empty(), Result.success(100).error());
		assertEquals(Optional.of("failed"), Result.error("failed").error());
	}
	
	@Test
	void orThrowReturnsValueForSuccess() {
		assertEquals(100, Result.success(100).orThrow());
		assertNull(Result.<String>success(null).orThrow());
	}
	
	@Test
	void orThrowThrowsForError() {
		Result<Integer> error = Result.error("Something failed");
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, error::orThrow);
		assertTrue(exception.getMessage().contains("Something failed"));
	}
	
	@Test
	void orThrowWithSupplierReturnsValueForSuccess() {
		assertEquals(100, Result.success(100).orThrow(RuntimeException::new));
		assertNull(Result.<String>success(null).orThrow(RuntimeException::new));
	}
	
	@Test
	void orThrowWithSupplierThrowsCustomException() {
		Result<Integer> error = Result.error("Custom error");
		
		assertThrows(NoSuchElementException.class, () -> error.orThrow(NoSuchElementException::new));
		assertThrows(IllegalArgumentException.class, () -> error.orThrow(IllegalArgumentException::new));
	}
	
	@Test
	void orThrowWithSupplierRejectsNullSupplier() {
		Result<Integer> error = Result.error("failed");
		assertThrows(NullPointerException.class, () -> error.orThrow(null));
	}
	
	@Test
	void errorOrThrowReturnsErrorMessage() {
		assertEquals("failed", Result.error("failed").errorOrThrow());
	}
	
	@Test
	void errorOrThrowThrowsForSuccess() {
		assertThrows(IllegalStateException.class, () -> Result.success(100).errorOrThrow());
	}
	
	@Test
	void mapTransformsSuccessValue() {
		Result<Integer> success = Result.success(100);
		Result<String> mapped = success.map(Object::toString);
		
		assertTrue(mapped.isSuccess());
		assertEquals("100", mapped.orThrow());
	}
	
	@Test
	void mapDoesNotAffectErrorResult() {
		Result<Integer> error = Result.error("failed");
		Result<String> mapped = error.map(Object::toString);
		
		assertTrue(mapped.isError());
		assertEquals("failed", mapped.errorOrThrow());
	}
	
	@Test
	void mapRejectsNullMapper() {
		assertThrows(NullPointerException.class, () -> Result.success(100).map(null));
	}
	
	@Test
	void mapHandlesNullValues() {
		Result<String> nullSuccess = Result.success(null);
		Result<String> mapped = nullSuccess.map(value -> value == null ? "was null" : value);
		
		assertTrue(mapped.isSuccess());
		assertEquals("was null", mapped.orThrow());
	}
	
	@Test
	void flatMapChainsSuccessfulOperations() {
		Result<Integer> success = Result.success(100);
		Result<String> chained = success.flatMap(value -> Result.success(value.toString()));
		
		assertTrue(chained.isSuccess());
		assertEquals("100", chained.orThrow());
	}
	
	@Test
	void flatMapStopsOnFirstError() {
		Result<Integer> success = Result.success(100);
		Result<String> chained = success.flatMap(value -> Result.error("chain failed"));
		
		assertTrue(chained.isError());
		assertEquals("chain failed", chained.errorOrThrow());
	}
	
	@Test
	void flatMapDoesNotAffectErrorResult() {
		Result<Integer> error = Result.error("original error");
		Result<String> chained = error.flatMap(value -> Result.success("should not execute"));
		
		assertTrue(chained.isError());
		assertEquals("original error", chained.errorOrThrow());
	}
	
	@Test
	void flatMapRejectsNullMapper() {
		assertThrows(NullPointerException.class, () -> Result.success(100).flatMap(null));
	}
	
	@Test
	void orElseReturnsFallbackForError() {
		assertEquals(200, Result.<Integer>error("failed").orElse(200));
		assertEquals(100, Result.success(100).orElse(200));
	}
	
	@Test
	void orElseRejectsNullFallback() {
		assertThrows(NullPointerException.class, () -> Result.success(100).orElse(null));
	}
	
	@Test
	void orElseGetExecutesSupplierForError() {
		AtomicInteger counter = new AtomicInteger(0);
		
		Integer result = Result.<Integer>error("failed").orElseGet(() -> {
			counter.incrementAndGet();
			return 200;
		});
		
		assertEquals(200, result);
		assertEquals(1, counter.get());
	}
	
	@Test
	void orElseGetDoesNotExecuteSupplierForSuccess() {
		AtomicInteger counter = new AtomicInteger(0);
		
		Integer result = Result.success(100).orElseGet(() -> {
			counter.incrementAndGet();
			return 200;
		});
		
		assertEquals(100, result);
		assertEquals(0, counter.get());
	}
	
	@Test
	void orElseGetRejectsNullSupplier() {
		assertThrows(NullPointerException.class, () -> Result.error("failed").orElseGet(null));
	}
	
	@Test
	void equalsComparesUnderlyingEither() {
		Result<Integer> success1 = Result.success(100);
		Result<Integer> success2 = Result.success(100);
		Result<Integer> differentSuccess = Result.success(200);
		Result<Integer> error1 = Result.error("failed");
		Result<Integer> error2 = Result.error("failed");
		Result<Integer> differentError = Result.error("other error");
		
		assertEquals(success1, success2);
		assertNotEquals(success1, differentSuccess);
		assertEquals(error1, error2);
		assertNotEquals(error1, differentError);
		assertNotEquals(success1, error1);
		assertNotEquals(success1, null);
		assertNotEquals(success1, "not a result");
	}
	
	@Test
	void hashCodeIsSameForEqualObjects() {
		Result<Integer> result1 = Result.success(100);
		Result<Integer> result2 = Result.success(100);
		
		assertEquals(result1.hashCode(), result2.hashCode());
	}
	
	@Test
	void toStringShowsValue() {
		assertEquals("100", Result.success(100).toString());
		assertEquals("null", Result.<String>success(null).toString());
		assertEquals("failed", Result.error("failed").toString());
		assertEquals("Something went wrong", Result.error("Something went wrong").toString());
	}
}
