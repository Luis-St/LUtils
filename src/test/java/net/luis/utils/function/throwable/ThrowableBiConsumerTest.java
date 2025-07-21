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
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test case for {@link ThrowableBiConsumer}.<br>
 *
 * @author Luis-St
 */
class ThrowableBiConsumerTest {
	
	@Test
	void caughtConvertsSuccessfulConsumer() {
		ThrowableBiConsumer<String, Integer, Exception> consumer = (s, i) -> {
			assertEquals("test", s);
			assertEquals(42, i);
		};
		BiConsumer<String, Integer> caught = ThrowableBiConsumer.caught(consumer);
		
		assertDoesNotThrow(() -> caught.accept("test", 42));
	}
	
	@Test
	void caughtWrapsExceptionInRuntimeException() {
		ThrowableBiConsumer<String, String, Exception> consumer = (a, b) -> {
			throw new Exception("Test exception");
		};
		BiConsumer<String, String> caught = ThrowableBiConsumer.caught(consumer);
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> caught.accept("a", "b"));
		assertInstanceOf(Exception.class, exception.getCause());
		assertEquals("Test exception", exception.getCause().getMessage());
	}
	
	@Test
	void caughtWithNullConsumer() {
		assertThrows(NullPointerException.class, () -> ThrowableBiConsumer.caught(null));
	}
	
	@Test
	void caughtWithNullParameters() {
		ThrowableBiConsumer<String, String, Exception> consumer = (a, b) -> {
			assertNull(a);
			assertNull(b);
		};
		BiConsumer<String, String> caught = ThrowableBiConsumer.caught(consumer);
		
		assertDoesNotThrow(() -> caught.accept(null, null));
	}
	
	@Test
	void caughtWithDifferentParameterTypes() {
		ThrowableBiConsumer<String, Integer, Exception> consumer = (s, i) -> {
			assertEquals("hello", s);
			assertEquals(5, i);
		};
		BiConsumer<String, Integer> caught = ThrowableBiConsumer.caught(consumer);
		
		assertDoesNotThrow(() -> caught.accept("hello", 5));
	}
	
	@Test
	void acceptWithSuccessfulExecution() {
		ThrowableBiConsumer<String, String, Exception> consumer = (a, b) -> {
			assertEquals("first", a);
			assertEquals("second", b);
		};
		
		assertDoesNotThrow(() -> consumer.accept("first", "second"));
	}
	
	@Test
	void acceptWithException() {
		ThrowableBiConsumer<String, String, Exception> consumer = (a, b) -> {
			if ("fail".equals(a)) {
				throw new Exception("Test exception");
			}
		};
		
		assertDoesNotThrow(() -> consumer.accept("success", "test"));
		
		Exception exception = assertThrows(Exception.class, () -> consumer.accept("fail", "test"));
		assertEquals("Test exception", exception.getMessage());
	}
	
	@Test
	void acceptWithNullParameters() {
		ThrowableBiConsumer<String, String, Exception> consumer = (a, b) -> {
			if (a == null || b == null) {
				throw new Exception("Null parameters not allowed");
			}
		};
		
		assertDoesNotThrow(() -> consumer.accept("valid", "valid"));
		assertThrows(Exception.class, () -> consumer.accept(null, "valid"));
		assertThrows(Exception.class, () -> consumer.accept("valid", null));
		assertThrows(Exception.class, () -> consumer.accept(null, null));
	}
	
	@Test
	void acceptWithDifferentTypes() {
		ThrowableBiConsumer<Integer, Boolean, Exception> consumer = (i, b) -> {
			assertEquals(42, i);
			assertTrue(b);
		};
		
		assertDoesNotThrow(() -> consumer.accept(42, true));
	}
	
	@Test
	void andThenExecutesBothOperations() {
		AtomicInteger counter = new AtomicInteger(0);
		
		ThrowableBiConsumer<String, String, Exception> first = (a, b) -> counter.incrementAndGet();
		ThrowableBiConsumer<String, String, Exception> second = (a, b) -> counter.incrementAndGet();
		
		ThrowableBiConsumer<String, String, Exception> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("test", "test"));
		
		assertEquals(2, counter.get());
	}
	
	@Test
	void andThenExecutesInCorrectOrder() {
		StringBuilder result = new StringBuilder();
		
		ThrowableBiConsumer<String, String, Exception> first = (a, b) -> result.append("first");
		ThrowableBiConsumer<String, String, Exception> second = (a, b) -> result.append("second");
		
		ThrowableBiConsumer<String, String, Exception> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("test", "test"));
		
		assertEquals("firstsecond", result.toString());
	}
	
	@Test
	void andThenWithMultipleChaining() {
		AtomicInteger counter = new AtomicInteger(0);
		
		ThrowableBiConsumer<String, String, Exception> first = (a, b) -> counter.incrementAndGet();
		ThrowableBiConsumer<String, String, Exception> second = (a, b) -> counter.incrementAndGet();
		ThrowableBiConsumer<String, String, Exception> third = (a, b) -> counter.incrementAndGet();
		
		ThrowableBiConsumer<String, String, Exception> combined = first.andThen(second).andThen(third);
		assertDoesNotThrow(() -> combined.accept("test", "test"));
		
		assertEquals(3, counter.get());
	}
	
	@Test
	void andThenWithNullAfterConsumer() {
		ThrowableBiConsumer<String, String, Exception> consumer = (a, b) -> {};
		
		assertThrows(NullPointerException.class, () -> consumer.andThen(null));
	}
	
	@Test
	void andThenPassesCorrectParameters() {
		ThrowableBiConsumer<String, String, Exception> first = (a, b) -> {
			assertEquals("test1", a);
			assertEquals("test2", b);
		};
		
		ThrowableBiConsumer<String, String, Exception> second = (a, b) -> {
			assertEquals("test1", a);
			assertEquals("test2", b);
		};
		
		ThrowableBiConsumer<String, String, Exception> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("test1", "test2"));
	}
	
	@Test
	void andThenWithExceptionInFirstConsumer() {
		ThrowableBiConsumer<String, String, Exception> first = (a, b) -> {
			throw new Exception("First consumer failed");
		};
		
		ThrowableBiConsumer<String, String, Exception> second = (a, b) -> {
			fail("Second consumer should not be called");
		};
		
		ThrowableBiConsumer<String, String, Exception> combined = first.andThen(second);
		Exception exception = assertThrows(Exception.class, () -> combined.accept("test", "test"));
		assertEquals("First consumer failed", exception.getMessage());
	}
	
	@Test
	void andThenWithExceptionInSecondConsumer() {
		AtomicInteger counter = new AtomicInteger(0);
		
		ThrowableBiConsumer<String, String, Exception> first = (a, b) -> counter.incrementAndGet();
		
		ThrowableBiConsumer<String, String, Exception> second = (a, b) -> {
			throw new Exception("Second consumer failed");
		};
		
		ThrowableBiConsumer<String, String, Exception> combined = first.andThen(second);
		Exception exception = assertThrows(Exception.class, () -> combined.accept("test", "test"));
		assertEquals("Second consumer failed", exception.getMessage());
		assertEquals(1, counter.get());
	}
}
