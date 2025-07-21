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

package net.luis.utils.function;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TriConsumer}.<br>
 *
 * @author Luis-St
 */
class TriConsumerTest {
	
	@Test
	void acceptWithCorrectParameters() {
		TriConsumer<String, String, String> consumer = (a, b, c) -> {
			assertEquals("a", a);
			assertEquals("b", b);
			assertEquals("c", c);
		};
		
		assertDoesNotThrow(() -> consumer.accept("a", "b", "c"));
	}
	
	@Test
	void acceptWithNullParameters() {
		TriConsumer<String, String, String> consumer = (a, b, c) -> {
			assertNull(a);
			assertNull(b);
			assertNull(c);
		};
		
		assertDoesNotThrow(() -> consumer.accept(null, null, null));
	}
	
	@Test
	void acceptWithMixedParameters() {
		TriConsumer<Object, Object, Object> consumer = (a, b, c) -> {
			assertEquals("string", a);
			assertEquals(42, b);
			assertNull(c);
		};
		
		assertDoesNotThrow(() -> consumer.accept("string", 42, null));
	}
	
	@Test
	void acceptWithDifferentTypes() {
		TriConsumer<Integer, String, Boolean> consumer = (i, s, b) -> {
			assertEquals(1, i);
			assertEquals("test", s);
			assertTrue(b);
		};
		
		assertDoesNotThrow(() -> consumer.accept(1, "test", true));
	}
	
	@Test
	void andThenExecutesBothOperations() {
		AtomicInteger counter = new AtomicInteger(0);
		
		TriConsumer<String, String, String> first = (a, b, c) -> counter.incrementAndGet();
		TriConsumer<String, String, String> second = (a, b, c) -> counter.incrementAndGet();
		
		TriConsumer<String, String, String> combined = first.andThen(second);
		combined.accept("a", "b", "c");
		
		assertEquals(2, counter.get());
	}
	
	@Test
	void andThenExecutesInCorrectOrder() {
		StringBuilder result = new StringBuilder();
		
		TriConsumer<String, String, String> first = (a, b, c) -> result.append("first");
		TriConsumer<String, String, String> second = (a, b, c) -> result.append("second");
		
		TriConsumer<String, String, String> combined = first.andThen(second);
		combined.accept("a", "b", "c");
		
		assertEquals("firstsecond", result.toString());
	}
	
	@Test
	void andThenWithMultipleChaining() {
		AtomicInteger counter = new AtomicInteger(0);
		
		TriConsumer<String, String, String> first = (a, b, c) -> counter.incrementAndGet();
		TriConsumer<String, String, String> second = (a, b, c) -> counter.incrementAndGet();
		TriConsumer<String, String, String> third = (a, b, c) -> counter.incrementAndGet();
		
		TriConsumer<String, String, String> combined = first.andThen(second).andThen(third);
		combined.accept("a", "b", "c");
		
		assertEquals(3, counter.get());
	}
	
	@Test
	void andThenWithNullAfterConsumer() {
		TriConsumer<String, String, String> consumer = (a, b, c) -> {};
		
		assertThrows(NullPointerException.class, () -> consumer.andThen(null));
	}
	
	@Test
	void andThenPassesCorrectParameters() {
		TriConsumer<String, String, String> first = (a, b, c) -> {
			assertEquals("a", a);
			assertEquals("b", b);
			assertEquals("c", c);
		};
		
		TriConsumer<String, String, String> second = (a, b, c) -> {
			assertEquals("a", a);
			assertEquals("b", b);
			assertEquals("c", c);
		};
		
		TriConsumer<String, String, String> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("a", "b", "c"));
	}
	
	@Test
	void andThenWithExceptionInFirstConsumer() {
		TriConsumer<String, String, String> first = (a, b, c) -> {
			throw new RuntimeException("First consumer failed");
		};
		
		TriConsumer<String, String, String> second = (a, b, c) -> {
			fail("Second consumer should not be called");
		};
		
		TriConsumer<String, String, String> combined = first.andThen(second);
		assertThrows(RuntimeException.class, () -> combined.accept("a", "b", "c"));
	}
	
	@Test
	void andThenWithExceptionInSecondConsumer() {
		AtomicInteger counter = new AtomicInteger(0);
		
		TriConsumer<String, String, String> first = (a, b, c) -> counter.incrementAndGet();
		
		TriConsumer<String, String, String> second = (a, b, c) -> {
			throw new RuntimeException("Second consumer failed");
		};
		
		TriConsumer<String, String, String> combined = first.andThen(second);
		assertThrows(RuntimeException.class, () -> combined.accept("a", "b", "c"));
		assertEquals(1, counter.get());
	}
}
