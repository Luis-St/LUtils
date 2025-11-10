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

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Success}.<br>
 *
 * @author Luis-St
 */
class SuccessTest {
	
	@Test
	void isSuccess() {
		Result<Integer> success = Result.success(100);
		assertTrue(success.isSuccess());
	}
	
	@Test
	void isSuccessWithNullValue() {
		Result<String> success = Result.success(null);
		assertTrue(success.isSuccess());
	}
	
	@Test
	void isError() {
		Result<Integer> success = Result.success(100);
		assertFalse(success.isError());
	}
	
	@Test
	void isPartial() {
		Result<Integer> success = Result.success(100);
		assertFalse(success.isPartial());
	}
	
	@Test
	void hasValue() {
		Result<Integer> success = Result.success(100);
		assertTrue(success.hasValue());
	}
	
	@Test
	void hasValueWithNullValue() {
		Result<String> success = Result.success(null);
		assertTrue(success.hasValue());
	}
	
	@Test
	void hasError() {
		Result<Integer> success = Result.success(100);
		assertFalse(success.hasError());
	}
	
	@Test
	void result() {
		Result<Integer> success = Result.success(100);
		assertEquals(Optional.of(100), success.result());
	}
	
	@Test
	void resultWithNullValue() {
		Result<String> success = Result.success(null);
		assertEquals(Optional.empty(), success.result());
	}
	
	@Test
	void resultOrThrow() {
		Result<Integer> success = Result.success(100);
		assertEquals(100, success.resultOrThrow());
	}
	
	@Test
	void resultOrThrowWithNullValue() {
		Result<String> success = Result.success(null);
		assertNull(success.resultOrThrow());
	}
	
	@Test
	void resultOrThrowWithExceptionSupplier() {
		Result<Integer> success = Result.success(100);
		assertEquals(100, success.resultOrThrow(RuntimeException::new));
	}
	
	@Test
	void resultOrThrowWithExceptionSupplierAndNullValue() {
		Result<String> success = Result.success(null);
		assertNull(success.resultOrThrow(RuntimeException::new));
	}
	
	@Test
	void error() {
		Result<Integer> success = Result.success(100);
		assertEquals(Optional.empty(), success.error());
	}
	
	@Test
	void errorOrThrow() {
		Result<Integer> success = Result.success(100);
		assertThrows(IllegalStateException.class, success::errorOrThrow);
	}
	
	@Test
	void map() {
		Result<Integer> success = Result.success(100);
		Result<String> mapped = success.map(Object::toString);
		
		assertTrue(mapped.isSuccess());
		assertEquals("100", mapped.resultOrThrow());
	}
	
	@Test
	void mapWithNullValue() {
		Result<String> success = Result.success(null);
		Result<String> mapped = success.map(value -> value == null ? "was null" : value);
		
		assertTrue(mapped.isSuccess());
		assertEquals("was null", mapped.resultOrThrow());
	}
	
	@Test
	void mapRejectsNullMapper() {
		Result<Integer> success = Result.success(100);
		assertThrows(NullPointerException.class, () -> success.map(null));
	}
	
	@Test
	void flatMap() {
		Result<Integer> success = Result.success(100);
		Result<String> mapped = success.flatMap(value -> Result.success(value.toString()));
		
		assertTrue(mapped.isSuccess());
		assertEquals("100", mapped.resultOrThrow());
	}
	
	@Test
	void flatMapWithError() {
		Result<Integer> success = Result.success(100);
		Result<String> mapped = success.flatMap(value -> Result.error("chain failed"));
		
		assertTrue(mapped.isError());
		assertEquals("chain failed", mapped.errorOrThrow());
	}
	
	@Test
	void flatMapRejectsNullMapper() {
		Result<Integer> success = Result.success(100);
		assertThrows(NullPointerException.class, () -> success.flatMap(null));
	}
	
	@Test
	void orElse() {
		Result<Integer> success = Result.success(100);
		assertEquals(100, success.orElse(200));
	}
	
	@Test
	void orElseRejectsNullFallback() {
		Result<Integer> success = Result.success(100);
		assertThrows(NullPointerException.class, () -> success.orElse(null));
	}
	
	@Test
	void orElseGet() {
		AtomicInteger counter = new AtomicInteger(0);
		
		Result<Integer> success = Result.success(100);
		Integer result = success.orElseGet(() -> {
			counter.incrementAndGet();
			return 200;
		});
		
		assertEquals(100, result);
		assertEquals(0, counter.get());
	}
	
	@Test
	void orElseGetRejectsNullSupplier() {
		Result<Integer> success = Result.success(100);
		assertThrows(NullPointerException.class, () -> success.orElseGet(null));
	}
	
	@Test
	void equals() {
		Result<Integer> success1 = Result.success(100);
		Result<Integer> success2 = Result.success(100);
		Result<Integer> differentSuccess = Result.success(200);
		
		assertEquals(success1, success2);
		assertNotEquals(success1, differentSuccess);
		assertNotEquals(success1, Result.error("error"));
		assertNotEquals(success1, Result.partial(100, "warning"));
	}
	
	@Test
	void hashCodeTest() {
		Result<Integer> success1 = Result.success(100);
		Result<Integer> success2 = Result.success(100);
		
		assertEquals(success1.hashCode(), success2.hashCode());
	}
	
	@Test
	void toStringTest() {
		Result<Integer> success = Result.success(100);
		assertEquals("Success[100]", success.toString());
	}
	
	@Test
	void toStringWithNullValue() {
		Result<String> success = Result.success(null);
		assertEquals("Success[null]", success.toString());
	}
}
