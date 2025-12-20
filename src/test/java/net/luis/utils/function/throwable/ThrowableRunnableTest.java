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

package net.luis.utils.function.throwable;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThrowableRunnable}.<br>
 *
 * @author Luis-St
 */
class ThrowableRunnableTest {

	@Test
	void caughtConvertsSuccessfulRunnable() {
		AtomicInteger counter = new AtomicInteger(0);
		ThrowableRunnable<Exception> runnable = counter::incrementAndGet;
		Runnable caught = ThrowableRunnable.caught(runnable);

		assertDoesNotThrow(caught::run);
		assertEquals(1, counter.get());
	}

	@Test
	void caughtWrapsExceptionInRuntimeException() {
		ThrowableRunnable<Exception> runnable = () -> {
			throw new Exception("Test exception");
		};
		Runnable caught = ThrowableRunnable.caught(runnable);

		RuntimeException exception = assertThrows(RuntimeException.class, caught::run);
		assertInstanceOf(Exception.class, exception.getCause());
		assertEquals("Test exception", exception.getCause().getMessage());
	}

	@Test
	void caughtWithNullRunnable() {
		assertThrows(NullPointerException.class, () -> ThrowableRunnable.caught(null));
	}

	@Test
	void caughtWithMultipleExecutions() {
		AtomicInteger counter = new AtomicInteger(0);
		ThrowableRunnable<Exception> runnable = counter::incrementAndGet;
		Runnable caught = ThrowableRunnable.caught(runnable);

		assertDoesNotThrow(caught::run);
		assertDoesNotThrow(caught::run);
		assertDoesNotThrow(caught::run);
		assertEquals(3, counter.get());
	}

	@Test
	void caughtWithSideEffects() {
		StringBuilder result = new StringBuilder();
		ThrowableRunnable<Exception> runnable = () -> result.append("executed");
		Runnable caught = ThrowableRunnable.caught(runnable);

		assertDoesNotThrow(caught::run);
		assertEquals("executed", result.toString());
	}

	@Test
	void runWithSuccessfulExecution() {
		AtomicInteger counter = new AtomicInteger(0);
		ThrowableRunnable<Exception> runnable = counter::incrementAndGet;

		assertDoesNotThrow(runnable::run);
		assertEquals(1, counter.get());
	}

	@Test
	void runWithException() {
		ThrowableRunnable<Exception> runnable = () -> {
			throw new Exception("Test exception");
		};

		Exception exception = assertThrows(Exception.class, runnable::run);
		assertEquals("Test exception", exception.getMessage());
	}

	@Test
	void runWithSideEffects() {
		StringBuilder result = new StringBuilder();
		ThrowableRunnable<Exception> runnable = () -> result.append("test");

		assertDoesNotThrow(runnable::run);
		assertEquals("test", result.toString());
	}

	@Test
	void runWithMultipleExecutions() {
		AtomicInteger counter = new AtomicInteger(0);
		ThrowableRunnable<Exception> runnable = counter::incrementAndGet;

		assertDoesNotThrow(runnable::run);
		assertDoesNotThrow(runnable::run);
		assertEquals(2, counter.get());
	}

	@Test
	void andThenExecutesBothOperations() {
		AtomicInteger counter = new AtomicInteger(0);

		ThrowableRunnable<Exception> first = counter::incrementAndGet;
		ThrowableRunnable<Exception> second = counter::incrementAndGet;

		ThrowableRunnable<Exception> combined = first.andThen(second);
		assertDoesNotThrow(combined::run);

		assertEquals(2, counter.get());
	}

	@Test
	void andThenExecutesInCorrectOrder() {
		StringBuilder result = new StringBuilder();

		ThrowableRunnable<Exception> first = () -> result.append("first");
		ThrowableRunnable<Exception> second = () -> result.append("second");

		ThrowableRunnable<Exception> combined = first.andThen(second);
		assertDoesNotThrow(combined::run);

		assertEquals("firstsecond", result.toString());
	}

	@Test
	void andThenWithMultipleChaining() {
		AtomicInteger counter = new AtomicInteger(0);

		ThrowableRunnable<Exception> first = counter::incrementAndGet;
		ThrowableRunnable<Exception> second = counter::incrementAndGet;
		ThrowableRunnable<Exception> third = counter::incrementAndGet;

		ThrowableRunnable<Exception> combined = first.andThen(second).andThen(third);
		assertDoesNotThrow(combined::run);

		assertEquals(3, counter.get());
	}

	@Test
	void andThenWithNullAfterRunnable() {
		ThrowableRunnable<Exception> runnable = () -> {};

		assertThrows(NullPointerException.class, () -> runnable.andThen(null));
	}

	@Test
	void andThenWithExceptionInFirstRunnable() {
		ThrowableRunnable<Exception> first = () -> {
			throw new Exception("First runnable failed");
		};

		ThrowableRunnable<Exception> second = () -> {
			fail("Second runnable should not be called");
		};

		ThrowableRunnable<Exception> combined = first.andThen(second);
		Exception exception = assertThrows(Exception.class, combined::run);
		assertEquals("First runnable failed", exception.getMessage());
	}

	@Test
	void andThenWithExceptionInSecondRunnable() {
		AtomicInteger counter = new AtomicInteger(0);

		ThrowableRunnable<Exception> first = counter::incrementAndGet;

		ThrowableRunnable<Exception> second = () -> {
			throw new Exception("Second runnable failed");
		};

		ThrowableRunnable<Exception> combined = first.andThen(second);
		Exception exception = assertThrows(Exception.class, combined::run);
		assertEquals("Second runnable failed", exception.getMessage());
		assertEquals(1, counter.get());
	}

	@Test
	void andThenWithBothOperationsSuccessful() {
		StringBuilder result = new StringBuilder();

		ThrowableRunnable<Exception> first = () -> result.append("A");
		ThrowableRunnable<Exception> second = () -> result.append("B");

		ThrowableRunnable<Exception> combined = first.andThen(second);
		assertDoesNotThrow(combined::run);

		assertEquals("AB", result.toString());
	}

	@Test
	void andThenWithComplexChaining() {
		StringBuilder result = new StringBuilder();

		ThrowableRunnable<Exception> a = () -> result.append("A");
		ThrowableRunnable<Exception> b = () -> result.append("B");
		ThrowableRunnable<Exception> c = () -> result.append("C");
		ThrowableRunnable<Exception> d = () -> result.append("D");

		ThrowableRunnable<Exception> combined = a.andThen(b).andThen(c).andThen(d);
		assertDoesNotThrow(combined::run);

		assertEquals("ABCD", result.toString());
	}
}
