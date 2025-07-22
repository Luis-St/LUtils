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

import net.luis.utils.function.QuadConsumer;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThrowableQuadConsumer}.<br>
 *
 * @author Luis-St
 */
class ThrowableQuadConsumerTest {
	
	@Test
	void caughtConvertsSuccessfulConsumer() {
		ThrowableQuadConsumer<String, Integer, Boolean, Double, Exception> consumer = (s, i, b, d) -> {
			assertEquals("test", s);
			assertEquals(42, i);
			assertTrue(b);
			assertEquals(3.14, d, 0.001);
		};
		QuadConsumer<String, Integer, Boolean, Double> caught = ThrowableQuadConsumer.caught(consumer);
		
		assertDoesNotThrow(() -> caught.accept("test", 42, true, 3.14));
	}
	
	@Test
	void caughtWrapsExceptionInRuntimeException() {
		ThrowableQuadConsumer<String, String, String, String, Exception> consumer = (a, b, c, d) -> {
			throw new Exception("Test exception");
		};
		QuadConsumer<String, String, String, String> caught = ThrowableQuadConsumer.caught(consumer);
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> caught.accept("a", "b", "c", "d"));
		assertInstanceOf(Exception.class, exception.getCause());
		assertEquals("Test exception", exception.getCause().getMessage());
	}
	
	@Test
	void caughtWithNullConsumer() {
		assertThrows(NullPointerException.class, () -> ThrowableQuadConsumer.caught(null));
	}
	
	@Test
	void caughtWithNullParameters() {
		ThrowableQuadConsumer<String, String, String, String, Exception> consumer = (a, b, c, d) -> {
			assertNull(a);
			assertNull(b);
			assertNull(c);
			assertNull(d);
		};
		QuadConsumer<String, String, String, String> caught = ThrowableQuadConsumer.caught(consumer);
		
		assertDoesNotThrow(() -> caught.accept(null, null, null, null));
	}
	
	@Test
	void caughtWithDifferentParameterTypes() {
		ThrowableQuadConsumer<String, Integer, Boolean, Double, Exception> consumer = (s, i, b, d) -> {
			assertEquals("hello", s);
			assertEquals(5, i);
			assertTrue(b);
			assertEquals(2.71, d, 0.001);
		};
		QuadConsumer<String, Integer, Boolean, Double> caught = ThrowableQuadConsumer.caught(consumer);
		
		assertDoesNotThrow(() -> caught.accept("hello", 5, true, 2.71));
	}
	
	@Test
	void acceptWithSuccessfulExecution() {
		ThrowableQuadConsumer<String, String, String, String, Exception> consumer = (a, b, c, d) -> {
			assertEquals("first", a);
			assertEquals("second", b);
			assertEquals("third", c);
			assertEquals("fourth", d);
		};
		
		assertDoesNotThrow(() -> consumer.accept("first", "second", "third", "fourth"));
	}
	
	@Test
	void acceptWithException() {
		ThrowableQuadConsumer<String, String, String, String, Exception> consumer = (a, b, c, d) -> {
			if ("fail".equals(a)) {
				throw new Exception("Test exception");
			}
		};
		
		assertDoesNotThrow(() -> consumer.accept("success", "test", "test", "test"));
		
		Exception exception = assertThrows(Exception.class, () -> consumer.accept("fail", "test", "test", "test"));
		assertEquals("Test exception", exception.getMessage());
	}
	
	@Test
	void acceptWithNullParameters() {
		ThrowableQuadConsumer<String, String, String, String, Exception> consumer = (a, b, c, d) -> {
			if (a == null || b == null || c == null || d == null) {
				throw new Exception("Null parameters not allowed");
			}
		};
		
		assertDoesNotThrow(() -> consumer.accept("valid", "valid", "valid", "valid"));
		assertThrows(Exception.class, () -> consumer.accept(null, "valid", "valid", "valid"));
		assertThrows(Exception.class, () -> consumer.accept("valid", null, "valid", "valid"));
		assertThrows(Exception.class, () -> consumer.accept("valid", "valid", null, "valid"));
		assertThrows(Exception.class, () -> consumer.accept("valid", "valid", "valid", null));
	}
	
	@Test
	void acceptWithDifferentTypes() {
		ThrowableQuadConsumer<Integer, String, Boolean, Double, Exception> consumer = (i, s, b, d) -> {
			assertEquals(42, i);
			assertEquals("test", s);
			assertTrue(b);
			assertEquals(3.14, d, 0.001);
		};
		
		assertDoesNotThrow(() -> consumer.accept(42, "test", true, 3.14));
	}
	
	@Test
	void andThenExecutesBothOperations() {
		AtomicInteger counter = new AtomicInteger(0);
		
		ThrowableQuadConsumer<String, String, String, String, Exception> first = (a, b, c, d) -> counter.incrementAndGet();
		ThrowableQuadConsumer<String, String, String, String, Exception> second = (a, b, c, d) -> counter.incrementAndGet();
		
		ThrowableQuadConsumer<String, String, String, String, Exception> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("test", "test", "test", "test"));
		
		assertEquals(2, counter.get());
	}
	
	@Test
	void andThenExecutesInCorrectOrder() {
		StringBuilder result = new StringBuilder();
		
		ThrowableQuadConsumer<String, String, String, String, Exception> first = (a, b, c, d) -> result.append("first");
		ThrowableQuadConsumer<String, String, String, String, Exception> second = (a, b, c, d) -> result.append("second");
		
		ThrowableQuadConsumer<String, String, String, String, Exception> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("test", "test", "test", "test"));
		
		assertEquals("firstsecond", result.toString());
	}
	
	@Test
	void andThenWithMultipleChaining() {
		AtomicInteger counter = new AtomicInteger(0);
		
		ThrowableQuadConsumer<String, String, String, String, Exception> first = (a, b, c, d) -> counter.incrementAndGet();
		ThrowableQuadConsumer<String, String, String, String, Exception> second = (a, b, c, d) -> counter.incrementAndGet();
		ThrowableQuadConsumer<String, String, String, String, Exception> third = (a, b, c, d) -> counter.incrementAndGet();
		
		ThrowableQuadConsumer<String, String, String, String, Exception> combined = first.andThen(second).andThen(third);
		assertDoesNotThrow(() -> combined.accept("test", "test", "test", "test"));
		
		assertEquals(3, counter.get());
	}
	
	@Test
	void andThenWithNullAfterConsumer() {
		ThrowableQuadConsumer<String, String, String, String, Exception> consumer = (a, b, c, d) -> {};
		
		assertThrows(NullPointerException.class, () -> consumer.andThen(null));
	}
	
	@Test
	void andThenPassesCorrectParameters() {
		ThrowableQuadConsumer<String, String, String, String, Exception> first = (a, b, c, d) -> {
			assertEquals("test1", a);
			assertEquals("test2", b);
			assertEquals("test3", c);
			assertEquals("test4", d);
		};
		
		ThrowableQuadConsumer<String, String, String, String, Exception> second = (a, b, c, d) -> {
			assertEquals("test1", a);
			assertEquals("test2", b);
			assertEquals("test3", c);
			assertEquals("test4", d);
		};
		
		ThrowableQuadConsumer<String, String, String, String, Exception> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("test1", "test2", "test3", "test4"));
	}
	
	@Test
	void andThenWithExceptionInFirstConsumer() {
		ThrowableQuadConsumer<String, String, String, String, Exception> first = (a, b, c, d) -> {
			throw new Exception("First consumer failed");
		};
		
		ThrowableQuadConsumer<String, String, String, String, Exception> second = (a, b, c, d) -> {
			fail("Second consumer should not be called");
		};
		
		ThrowableQuadConsumer<String, String, String, String, Exception> combined = first.andThen(second);
		Exception exception = assertThrows(Exception.class, () -> combined.accept("test", "test", "test", "test"));
		assertEquals("First consumer failed", exception.getMessage());
	}
	
	@Test
	void andThenWithExceptionInSecondConsumer() {
		AtomicInteger counter = new AtomicInteger(0);
		
		ThrowableQuadConsumer<String, String, String, String, Exception> first = (a, b, c, d) -> counter.incrementAndGet();
		
		ThrowableQuadConsumer<String, String, String, String, Exception> second = (a, b, c, d) -> {
			throw new Exception("Second consumer failed");
		};
		
		ThrowableQuadConsumer<String, String, String, String, Exception> combined = first.andThen(second);
		Exception exception = assertThrows(Exception.class, () -> combined.accept("test", "test", "test", "test"));
		assertEquals("Second consumer failed", exception.getMessage());
		assertEquals(1, counter.get());
	}
}
