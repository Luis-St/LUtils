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
 * Test class for {@link QuadConsumer}.<br>
 *
 * @author Luis-St
 */
class QuadConsumerTest {
	
	@Test
	void acceptWithCorrectParameters() {
		QuadConsumer<String, String, String, String> consumer = (a, b, c, d) -> {
			assertEquals("a", a);
			assertEquals("b", b);
			assertEquals("c", c);
			assertEquals("d", d);
		};
		
		assertDoesNotThrow(() -> consumer.accept("a", "b", "c", "d"));
	}
	
	@Test
	void acceptWithNullParameters() {
		QuadConsumer<String, String, String, String> consumer = (a, b, c, d) -> {
			assertNull(a);
			assertNull(b);
			assertNull(c);
			assertNull(d);
		};
		
		assertDoesNotThrow(() -> consumer.accept(null, null, null, null));
	}
	
	@Test
	void acceptWithMixedParameters() {
		QuadConsumer<Object, Object, Object, Object> consumer = (a, b, c, d) -> {
			assertEquals("string", a);
			assertEquals(42, b);
			assertNull(c);
			assertTrue((Boolean) d);
		};
		
		assertDoesNotThrow(() -> consumer.accept("string", 42, null, true));
	}
	
	@Test
	void acceptWithDifferentTypes() {
		QuadConsumer<Integer, String, Boolean, Double> consumer = (i, s, b, d) -> {
			assertEquals(1, i);
			assertEquals("test", s);
			assertTrue(b);
			assertEquals(3.14, d, 0.001);
		};
		
		assertDoesNotThrow(() -> consumer.accept(1, "test", true, 3.14));
	}
	
	@Test
	void andThenExecutesBothOperations() {
		AtomicInteger counter = new AtomicInteger(0);
		
		QuadConsumer<String, String, String, String> first = (a, b, c, d) -> counter.incrementAndGet();
		QuadConsumer<String, String, String, String> second = (a, b, c, d) -> counter.incrementAndGet();
		
		QuadConsumer<String, String, String, String> combined = first.andThen(second);
		combined.accept("a", "b", "c", "d");
		
		assertEquals(2, counter.get());
	}
	
	@Test
	void andThenExecutesInCorrectOrder() {
		StringBuilder result = new StringBuilder();
		
		QuadConsumer<String, String, String, String> first = (a, b, c, d) -> result.append("first");
		QuadConsumer<String, String, String, String> second = (a, b, c, d) -> result.append("second");
		
		QuadConsumer<String, String, String, String> combined = first.andThen(second);
		combined.accept("a", "b", "c", "d");
		
		assertEquals("firstsecond", result.toString());
	}
	
	@Test
	void andThenWithMultipleChaining() {
		AtomicInteger counter = new AtomicInteger(0);
		
		QuadConsumer<String, String, String, String> first = (a, b, c, d) -> counter.incrementAndGet();
		QuadConsumer<String, String, String, String> second = (a, b, c, d) -> counter.incrementAndGet();
		QuadConsumer<String, String, String, String> third = (a, b, c, d) -> counter.incrementAndGet();
		
		QuadConsumer<String, String, String, String> combined = first.andThen(second).andThen(third);
		combined.accept("a", "b", "c", "d");
		
		assertEquals(3, counter.get());
	}
	
	@Test
	void andThenWithNullAfterConsumer() {
		QuadConsumer<String, String, String, String> consumer = (a, b, c, d) -> {};
		
		assertThrows(NullPointerException.class, () -> consumer.andThen(null));
	}
	
	@Test
	void andThenPassesCorrectParameters() {
		QuadConsumer<String, String, String, String> first = (a, b, c, d) -> {
			assertEquals("a", a);
			assertEquals("b", b);
			assertEquals("c", c);
			assertEquals("d", d);
		};
		
		QuadConsumer<String, String, String, String> second = (a, b, c, d) -> {
			assertEquals("a", a);
			assertEquals("b", b);
			assertEquals("c", c);
			assertEquals("d", d);
		};
		
		QuadConsumer<String, String, String, String> combined = first.andThen(second);
		assertDoesNotThrow(() -> combined.accept("a", "b", "c", "d"));
	}
	
	@Test
	void andThenWithExceptionInFirstConsumer() {
		QuadConsumer<String, String, String, String> first = (a, b, c, d) -> {
			throw new RuntimeException("First consumer failed");
		};
		
		QuadConsumer<String, String, String, String> second = (a, b, c, d) -> {
			fail("Second consumer should not be called");
		};
		
		QuadConsumer<String, String, String, String> combined = first.andThen(second);
		assertThrows(RuntimeException.class, () -> combined.accept("a", "b", "c", "d"));
	}
	
	@Test
	void andThenWithExceptionInSecondConsumer() {
		AtomicInteger counter = new AtomicInteger(0);
		
		QuadConsumer<String, String, String, String> first = (a, b, c, d) -> counter.incrementAndGet();
		
		QuadConsumer<String, String, String, String> second = (a, b, c, d) -> {
			throw new RuntimeException("Second consumer failed");
		};
		
		QuadConsumer<String, String, String, String> combined = first.andThen(second);
		assertThrows(RuntimeException.class, () -> combined.accept("a", "b", "c", "d"));
		assertEquals(1, counter.get());
	}
}
