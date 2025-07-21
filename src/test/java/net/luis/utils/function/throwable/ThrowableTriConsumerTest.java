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

import net.luis.utils.function.TriConsumer;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThrowableTriConsumer}.<br>
 *
 * @author Luis-St
 */
class ThrowableTriConsumerTest {
	
	@Test
	void caughtConvertsSuccessfulConsumer() {
		ThrowableTriConsumer<String, Integer, Boolean, Exception> consumer = (s, i, b) -> {
			assertEquals("test", s);
			assertEquals(42, i);
			assertTrue(b);
		};
		TriConsumer<String, Integer, Boolean> caught = ThrowableTriConsumer.caught(consumer);
		
		assertDoesNotThrow(() -> caught.accept("test", 42, true));
	}
	
	@Test
	void caughtWrapsExceptionInRuntimeException() {
		ThrowableTriConsumer<String, String, String, Exception> consumer = (a, b, c) -> {
			throw new Exception("Test exception");
		};
		TriConsumer<String, String, String> caught = ThrowableTriConsumer.caught(consumer);
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> caught.accept("a", "b", "c"));
		assertInstanceOf(Exception.class, exception.getCause());
		assertEquals("Test exception", exception.getCause().getMessage());
	}
	
	@Test
	void caughtWithNullConsumer() {
		assertThrows(NullPointerException.class, () -> ThrowableTriConsumer.caught(null));
	}
	
	@Test
	void caughtWithNullParameters() {
		ThrowableTriConsumer<String, String, String, Exception> consumer = (a, b, c) -> {
			assertNull(a);
			assertNull(b);
			assertNull(c);
		};
		TriConsumer<String, String, String> caught = ThrowableTriConsumer.caught(consumer);
		
		assertDoesNotThrow(() -> caught.accept(null, null, null));
	}
	
	@Test
	void caughtWithDifferentParameterTypes() {
		ThrowableTriConsumer<String, Integer, Boolean, Exception> consumer = (s, i, b) -> {
			assertEquals("hello", s);
			assertEquals(5, i);
			assertTrue(b);
		};
		TriConsumer<String, Integer, Boolean> caught = ThrowableTriConsumer.caught(consumer);
		
		assertDoesNotThrow(() -> caught.accept("hello", 5, true));
	}
	
	@Test
	void acceptWithSuccessfulExecution() {
		ThrowableTriConsumer<String, String, String, Exception> consumer = (a, b, c) -> {
			assertEquals("first", a);
			assertEquals("second", b);
			assertEquals("third", c);
		};
		
		assertDoesNotThrow(() -> consumer.accept("first", "second", "third"));
	}
	
	@Test
	void acceptWithException() {
		ThrowableTriConsumer<String, String, String, Exception> consumer = (a, b, c) -> {
			if ("fail".equals(a)) {
				throw new Exception("Test exception");
			}
		};
		
		assertDoesNotThrow(() -> consumer.accept("success", "test", "test"));
		
		Exception exception = assertThrows(Exception.class, () -> consumer.accept("fail", "test", "test"));
		assertEquals("Test exception", exception.getMessage());
	}
	
	@Test
	void acceptWithNullParameters() {
		ThrowableTriConsumer<String, String, String, Exception> consumer = (a, b, c) -> {
			if (a == null || b == null || c == null) {
				throw new Exception("Null parameters not allowed");
			}
		};
		
		assertDoesNotThrow(() -> consumer.accept("valid", "valid", "valid"));
		assertThrows(Exception.class, () -> consumer.accept(null, "valid", "valid"));
		assertThrows(Exception.class, () -> consumer.accept("valid", null, "valid"));
		assertThrows(Exception.class, () -> consumer.accept("valid", "valid", null));
	}
	
	@Test
	void acceptWithDifferentTypes() {
		ThrowableTriConsumer<Integer, String, Boolean, Exception> consumer = (i, s, b) -> {
			assertEquals(42, i);
			assertEquals("test", s);
			assertTrue(b);
		};
		
		assertDoesNotThrow(() -> consumer.accept(42, "test", true));
	}
	
	@Test
	void andThenExecutesBothOperations() {
		AtomicInteger counter = new AtomicInteger(0);
		
		ThrowableTriConsumer<String, String, String, Exception> first = (a, b, c) -> counter.incrementAndGet();
		ThrowableTriConsumer<String, String, String, Exception> second = (a, b, c) -> counter.incrementAndGet();
		
		ThrowableTriConsumer<String, String, String, Exception> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("test", "test", "test"));
		
		assertEquals(2, counter.get());
	}
	
	@Test
	void andThenExecutesInCorrectOrder() {
		StringBuilder result = new StringBuilder();
		
		ThrowableTriConsumer<String, String, String, Exception> first = (a, b, c) -> result.append("first");
		ThrowableTriConsumer<String, String, String, Exception> second = (a, b, c) -> result.append("second");
		
		ThrowableTriConsumer<String, String, String, Exception> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("test", "test", "test"));
		
		assertEquals("firstsecond", result.toString());
	}
	
	@Test
	void andThenWithMultipleChaining() {
		AtomicInteger counter = new AtomicInteger(0);
		
		ThrowableTriConsumer<String, String, String, Exception> first = (a, b, c) -> counter.incrementAndGet();
		ThrowableTriConsumer<String, String, String, Exception> second = (a, b, c) -> counter.incrementAndGet();
		ThrowableTriConsumer<String, String, String, Exception> third = (a, b, c) -> counter.incrementAndGet();
		
		ThrowableTriConsumer<String, String, String, Exception> combined = first.andThen(second).andThen(third);
		assertDoesNotThrow(() -> combined.accept("test", "test", "test"));
		
		assertEquals(3, counter.get());
	}
	
	@Test
	void andThenWithNullAfterConsumer() {
		ThrowableTriConsumer<String, String, String, Exception> consumer = (a, b, c) -> {};
		
		assertThrows(NullPointerException.class, () -> consumer.andThen(null));
	}
	
	@Test
	void andThenPassesCorrectParameters() {
		ThrowableTriConsumer<String, String, String, Exception> first = (a, b, c) -> {
			assertEquals("test1", a);
			assertEquals("test2", b);
			assertEquals("test3", c);
		};
		
		ThrowableTriConsumer<String, String, String, Exception> second = (a, b, c) -> {
			assertEquals("test1", a);
			assertEquals("test2", b);
			assertEquals("test3", c);
		};
		
		ThrowableTriConsumer<String, String, String, Exception> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("test1", "test2", "test3"));
	}
	
	@Test
	void andThenWithExceptionInFirstConsumer() {
		ThrowableTriConsumer<String, String, String, Exception> first = (a, b, c) -> {
			throw new Exception("First consumer failed");
		};
		
		ThrowableTriConsumer<String, String, String, Exception> second = (a, b, c) -> {
			fail("Second consumer should not be called");
		};
		
		ThrowableTriConsumer<String, String, String, Exception> combined = first.andThen(second);
		Exception exception = assertThrows(Exception.class, () -> combined.accept("test", "test", "test"));
		assertEquals("First consumer failed", exception.getMessage());
	}
	
	@Test
	void andThenWithExceptionInSecondConsumer() {
		AtomicInteger counter = new AtomicInteger(0);
		
		ThrowableTriConsumer<String, String, String, Exception> first = (a, b, c) -> counter.incrementAndGet();
		
		ThrowableTriConsumer<String, String, String, Exception> second = (a, b, c) -> {
			throw new Exception("Second consumer failed");
		};
		
		ThrowableTriConsumer<String, String, String, Exception> combined = first.andThen(second);
		Exception exception = assertThrows(Exception.class, () -> combined.accept("test", "test", "test"));
		assertEquals("Second consumer failed", exception.getMessage());
		assertEquals(1, counter.get());
	}
}
