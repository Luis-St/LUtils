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
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThrowableConsumer}.<br>
 *
 * @author Luis-St
 */
class ThrowableConsumerTest {
	
	@Test
	void caughtConvertsSuccessfulConsumer() {
		ThrowableConsumer<String, Exception> consumer = s -> {
			assertEquals("test", s);
		};
		Consumer<String> caught = ThrowableConsumer.caught(consumer);
		
		assertDoesNotThrow(() -> caught.accept("test"));
	}
	
	@Test
	void caughtWrapsExceptionInRuntimeException() {
		ThrowableConsumer<String, Exception> consumer = s -> {
			throw new Exception("Test exception");
		};
		Consumer<String> caught = ThrowableConsumer.caught(consumer);
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> caught.accept("test"));
		assertInstanceOf(Exception.class, exception.getCause());
		assertEquals("Test exception", exception.getCause().getMessage());
	}
	
	@Test
	void caughtWithNullConsumer() {
		assertThrows(NullPointerException.class, () -> ThrowableConsumer.caught(null));
	}
	
	@Test
	void caughtWithNullParameter() {
		ThrowableConsumer<String, Exception> consumer = s -> {
			assertNull(s);
		};
		Consumer<String> caught = ThrowableConsumer.caught(consumer);
		
		assertDoesNotThrow(() -> caught.accept(null));
	}
	
	@Test
	void caughtWithDifferentParameterTypes() {
		ThrowableConsumer<Integer, Exception> consumer = i -> {
			assertEquals(42, i);
		};
		Consumer<Integer> caught = ThrowableConsumer.caught(consumer);
		
		assertDoesNotThrow(() -> caught.accept(42));
	}
	
	@Test
	void acceptWithSuccessfulExecution() {
		ThrowableConsumer<String, Exception> consumer = s -> {
			assertEquals("test", s);
		};
		
		assertDoesNotThrow(() -> consumer.accept("test"));
	}
	
	@Test
	void acceptWithException() {
		ThrowableConsumer<String, Exception> consumer = s -> {
			if ("fail".equals(s)) {
				throw new Exception("Test exception");
			}
		};
		
		assertDoesNotThrow(() -> consumer.accept("success"));
		
		Exception exception = assertThrows(Exception.class, () -> consumer.accept("fail"));
		assertEquals("Test exception", exception.getMessage());
	}
	
	@Test
	void acceptWithNullParameter() {
		ThrowableConsumer<String, Exception> consumer = s -> {
			if (s == null) {
				throw new Exception("Null parameter not allowed");
			}
		};
		
		assertDoesNotThrow(() -> consumer.accept("valid"));
		assertThrows(Exception.class, () -> consumer.accept(null));
	}
	
	@Test
	void acceptWithDifferentTypes() {
		ThrowableConsumer<Boolean, Exception> consumer = b -> {
			assertTrue(b);
		};
		
		assertDoesNotThrow(() -> consumer.accept(true));
	}
	
	@Test
	void andThenExecutesBothOperations() {
		AtomicInteger counter = new AtomicInteger(0);
		
		ThrowableConsumer<String, Exception> first = s -> counter.incrementAndGet();
		ThrowableConsumer<String, Exception> second = s -> counter.incrementAndGet();
		
		ThrowableConsumer<String, Exception> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("test"));
		
		assertEquals(2, counter.get());
	}
	
	@Test
	void andThenExecutesInCorrectOrder() {
		StringBuilder result = new StringBuilder();
		
		ThrowableConsumer<String, Exception> first = s -> result.append("first");
		ThrowableConsumer<String, Exception> second = s -> result.append("second");
		
		ThrowableConsumer<String, Exception> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("test"));
		
		assertEquals("firstsecond", result.toString());
	}
	
	@Test
	void andThenWithMultipleChaining() {
		AtomicInteger counter = new AtomicInteger(0);
		
		ThrowableConsumer<String, Exception> first = s -> counter.incrementAndGet();
		ThrowableConsumer<String, Exception> second = s -> counter.incrementAndGet();
		ThrowableConsumer<String, Exception> third = s -> counter.incrementAndGet();
		
		ThrowableConsumer<String, Exception> combined = first.andThen(second).andThen(third);
		assertDoesNotThrow(() -> combined.accept("test"));
		
		assertEquals(3, counter.get());
	}
	
	@Test
	void andThenWithNullAfterConsumer() {
		ThrowableConsumer<String, Exception> consumer = s -> {};
		
		assertThrows(NullPointerException.class, () -> consumer.andThen(null));
	}
	
	@Test
	void andThenPassesCorrectParameter() {
		ThrowableConsumer<String, Exception> first = s -> {
			assertEquals("test", s);
		};
		
		ThrowableConsumer<String, Exception> second = s -> {
			assertEquals("test", s);
		};
		
		ThrowableConsumer<String, Exception> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("test"));
	}
	
	@Test
	void andThenWithExceptionInFirstConsumer() {
		ThrowableConsumer<String, Exception> first = s -> {
			throw new Exception("First consumer failed");
		};
		
		ThrowableConsumer<String, Exception> second = s -> {
			fail("Second consumer should not be called");
		};
		
		ThrowableConsumer<String, Exception> combined = first.andThen(second);
		Exception exception = assertThrows(Exception.class, () -> combined.accept("test"));
		assertEquals("First consumer failed", exception.getMessage());
	}
	
	@Test
	void andThenWithExceptionInSecondConsumer() {
		AtomicInteger counter = new AtomicInteger(0);
		
		ThrowableConsumer<String, Exception> first = s -> counter.incrementAndGet();
		
		ThrowableConsumer<String, Exception> second = s -> {
			throw new Exception("Second consumer failed");
		};
		
		ThrowableConsumer<String, Exception> combined = first.andThen(second);
		Exception exception = assertThrows(Exception.class, () -> combined.accept("test"));
		assertEquals("Second consumer failed", exception.getMessage());
		assertEquals(1, counter.get());
	}
}
