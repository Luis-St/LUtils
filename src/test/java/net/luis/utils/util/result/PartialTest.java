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
 * Test class for {@link Partial}.<br>
 *
 * @author Luis-St
 */
class PartialTest {
	
	@Test
	void isSuccess() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertFalse(partial.isSuccess());
	}
	
	@Test
	void isError() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertFalse(partial.isError());
	}
	
	@Test
	void isPartial() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertTrue(partial.isPartial());
	}
	
	@Test
	void hasValue() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertTrue(partial.hasValue());
	}
	
	@Test
	void hasValueWithNullValue() {
		Result<String> partial = Result.partial(null, "warning");
		assertTrue(partial.hasValue());
	}
	
	@Test
	void hasError() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertTrue(partial.hasError());
	}
	
	@Test
	void result() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertEquals(Optional.of(50), partial.result());
	}
	
	@Test
	void resultWithNullValue() {
		Result<String> partial = Result.partial(null, "warning");
		assertEquals(Optional.empty(), partial.result());
	}
	
	@Test
	void resultOrThrow() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertEquals(50, partial.resultOrThrow());
	}
	
	@Test
	void resultOrThrowWithNullValue() {
		Result<String> partial = Result.partial(null, "warning");
		assertNull(partial.resultOrThrow());
	}
	
	@Test
	void resultOrThrowWithExceptionSupplier() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertEquals(50, partial.resultOrThrow(RuntimeException::new));
	}
	
	@Test
	void resultOrThrowWithExceptionSupplierRejectsNullSupplier() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertThrows(NullPointerException.class, () -> partial.resultOrThrow(null));
	}
	
	@Test
	void error() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertEquals(Optional.of("warning"), partial.error());
	}
	
	@Test
	void errorOrThrow() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertEquals("warning", partial.errorOrThrow());
	}
	
	@Test
	void map() {
		Result<Integer> partial = Result.partial(50, "incomplete");
		Result<String> mapped = partial.map(Object::toString);
		
		assertTrue(mapped.isPartial());
		assertEquals("50", mapped.resultOrThrow());
		assertEquals("incomplete", mapped.errorOrThrow());
	}
	
	@Test
	void mapRejectsNullMapper() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertThrows(NullPointerException.class, () -> partial.map(null));
	}
	
	@Test
	void flatMapWithSuccess() {
		Result<Integer> partial = Result.partial(50, "original warning");
		Result<String> mapped = partial.flatMap(value -> Result.success(value.toString()));
		
		assertTrue(mapped.isPartial());
		assertEquals("50", mapped.resultOrThrow());
		assertEquals("original warning", mapped.errorOrThrow());
	}
	
	@Test
	void flatMapWithError() {
		Result<Integer> partial = Result.partial(50, "warning");
		Result<String> mapped = partial.flatMap(value -> Result.error("chain failed"));
		
		assertTrue(mapped.isError());
		assertEquals("warning; chain failed", mapped.errorOrThrow());
	}
	
	@Test
	void flatMapWithPartial() {
		Result<Integer> partial = Result.partial(50, "first warning");
		Result<String> mapped = partial.flatMap(value -> Result.partial(value.toString(), "second warning"));
		
		assertTrue(mapped.isPartial());
		assertEquals("50", mapped.resultOrThrow());
		assertEquals("Mapping of partial result failed\n - first warning\n - second warning", mapped.errorOrThrow());
	}
	
	@Test
	void flatMapRejectsNullMapper() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertThrows(NullPointerException.class, () -> partial.flatMap(null));
	}
	
	@Test
	void orElse() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertEquals(50, partial.orElse(200));
	}
	
	@Test
	void orElseRejectsNullFallback() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertThrows(NullPointerException.class, () -> partial.orElse(null));
	}
	
	@Test
	void orElseGet() {
		AtomicInteger counter = new AtomicInteger(0);
		
		Result<Integer> partial = Result.partial(50, "warning");
		Integer result = partial.orElseGet(() -> {
			counter.incrementAndGet();
			return 200;
		});
		
		assertEquals(50, result);
		assertEquals(0, counter.get());
	}
	
	@Test
	void orElseGetRejectsNullSupplier() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertThrows(NullPointerException.class, () -> partial.orElseGet(null));
	}
	
	@Test
	void equals() {
		Result<Integer> partial1 = Result.partial(50, "warning");
		Result<Integer> partial2 = Result.partial(50, "warning");
		Result<Integer> differentPartial = Result.partial(50, "different warning");
		
		assertEquals(partial1, partial2);
		assertNotEquals(partial1, differentPartial);
		assertNotEquals(partial1, Result.success(50));
		assertNotEquals(partial1, Result.error("warning"));
	}
	
	@Test
	void hashCodeTest() {
		Result<Integer> partial1 = Result.partial(50, "warning");
		Result<Integer> partial2 = Result.partial(50, "warning");
		
		assertEquals(partial1.hashCode(), partial2.hashCode());
	}
	
	@Test
	void toStringTest() {
		Result<Integer> partial = Result.partial(50, "warning");
		assertEquals("Partial[value=50, error=warning]", partial.toString());
	}
	
	@Test
	void toStringWithNullValue() {
		Result<String> partial = Result.partial(null, "no value");
		assertEquals("Partial[value=null, error=no value]", partial.toString());
	}
}
