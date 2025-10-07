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

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Error}.<br>
 *
 * @author Luis-St
 */
class ErrorTest {

	@Test
	void isSuccess() {
		Result<Integer> error = Result.error("failed");
		assertFalse(error.isSuccess());
	}

	@Test
	void isError() {
		Result<Integer> error = Result.error("failed");
		assertTrue(error.isError());
	}

	@Test
	void isPartial() {
		Result<Integer> error = Result.error("failed");
		assertFalse(error.isPartial());
	}

	@Test
	void hasValue() {
		Result<Integer> error = Result.error("failed");
		assertFalse(error.hasValue());
	}

	@Test
	void hasError() {
		Result<Integer> error = Result.error("failed");
		assertTrue(error.hasError());
	}

	@Test
	void result() {
		Result<Integer> error = Result.error("failed");
		assertEquals(Optional.empty(), error.result());
	}

	@Test
	void resultOrThrow() {
		Result<Integer> error = Result.error("Something failed");

		IllegalStateException exception = assertThrows(IllegalStateException.class, error::resultOrThrow);
		assertTrue(exception.getMessage().contains("Something failed"));
	}

	@Test
	void resultOrThrowWithExceptionSupplier() {
		Result<Integer> error = Result.error("Custom error");

		assertThrows(NoSuchElementException.class, () -> error.resultOrThrow(NoSuchElementException::new));
		assertThrows(IllegalArgumentException.class, () -> error.resultOrThrow(IllegalArgumentException::new));
	}

	@Test
	void resultOrThrowWithExceptionSupplierRejectsNullSupplier() {
		Result<Integer> error = Result.error("failed");
		assertThrows(NullPointerException.class, () -> error.resultOrThrow(null));
	}

	@Test
	void error() {
		Result<Integer> error = Result.error("failed");
		assertEquals(Optional.of("failed"), error.error());
	}

	@Test
	void errorOrThrow() {
		Result<Integer> error = Result.error("failed");
		assertEquals("failed", error.errorOrThrow());
	}

	@Test
	void map() {
		Result<Integer> error = Result.error("failed");
		Result<String> mapped = error.map(Object::toString);

		assertTrue(mapped.isError());
		assertEquals("failed", mapped.errorOrThrow());
	}

	@Test
	void mapRejectsNullMapper() {
		Result<Integer> error = Result.error("failed");
		assertThrows(NullPointerException.class, () -> error.map(null));
	}

	@Test
	void flatMap() {
		Result<Integer> error = Result.error("original error");
		Result<String> mapped = error.flatMap(value -> Result.success("should not execute"));

		assertTrue(mapped.isError());
		assertEquals("original error", mapped.errorOrThrow());
	}

	@Test
	void flatMapRejectsNullMapper() {
		Result<Integer> error = Result.error("failed");
		assertThrows(NullPointerException.class, () -> error.flatMap(null));
	}

	@Test
	void orElse() {
		Result<Integer> error = Result.error("failed");
		assertEquals(200, error.orElse(200));
	}

	@Test
	void orElseRejectsNullFallback() {
		Result<Integer> error = Result.error("failed");
		assertThrows(NullPointerException.class, () -> error.orElse(null));
	}

	@Test
	void orElseGet() {
		AtomicInteger counter = new AtomicInteger(0);

		Result<Integer> error = Result.error("failed");
		Integer result = error.orElseGet(() -> {
			counter.incrementAndGet();
			return 200;
		});

		assertEquals(200, result);
		assertEquals(1, counter.get());
	}

	@Test
	void orElseGetRejectsNullSupplier() {
		Result<Integer> error = Result.error("failed");
		assertThrows(NullPointerException.class, () -> error.orElseGet(null));
	}

	@Test
	void equals() {
		Result<Integer> error1 = Result.error("failed");
		Result<Integer> error2 = Result.error("failed");
		Result<Integer> differentError = Result.error("other error");

		assertEquals(error1, error2);
		assertNotEquals(error1, differentError);
		assertNotEquals(error1, Result.success(100));
		assertNotEquals(error1, Result.partial(100, "failed"));
	}

	@Test
	void hashCodeTest() {
		Result<Integer> error1 = Result.error("failed");
		Result<Integer> error2 = Result.error("failed");

		assertEquals(error1.hashCode(), error2.hashCode());
	}

	@Test
	void toStringTest() {
		Result<Integer> error = Result.error("failed");
		assertEquals("Error[failed]", error.toString());
	}

	@Test
	void toStringWithLongMessage() {
		Result<Integer> error = Result.error("Something went wrong");
		assertEquals("Error[Something went wrong]", error.toString());
	}
}
