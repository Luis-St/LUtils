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

package net.luis.utils.util;

import net.luis.utils.exception.AlreadyInitializedException;
import net.luis.utils.exception.NotInitializedException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LazyInitialization}.<br>
 *
 * @author Luis-St
 */
class LazyInitializationTest {
	
	@Test
	void constructorWithoutParameters() {
		assertDoesNotThrow(() -> new LazyInitialization<>());
		
		LazyInitialization<String> lazy = new LazyInitialization<>();
		assertFalse(lazy.isInstantiated());
	}
	
	@Test
	void constructorWithValue() {
		assertDoesNotThrow(() -> new LazyInitialization<>(10));
		assertDoesNotThrow(() -> new LazyInitialization<>((Object) null));
		
		LazyInitialization<Integer> lazy = new LazyInitialization<>(10);
		assertTrue(lazy.isInstantiated());
		assertEquals(Integer.valueOf(10), lazy.get());
		
		LazyInitialization<String> lazyNull = new LazyInitialization<>((String) null);
		assertTrue(lazyNull.isInstantiated());
		assertNull(lazyNull.get());
	}
	
	@Test
	void constructorWithAction() {
		AtomicInteger counter = new AtomicInteger(0);
		
		assertDoesNotThrow(() -> new LazyInitialization<>(v -> counter.incrementAndGet()));
		assertThrows(NullPointerException.class, () -> new LazyInitialization<>(null));
		
		LazyInitialization<String> lazy = new LazyInitialization<>(v -> counter.incrementAndGet());
		assertFalse(lazy.isInstantiated());
		assertEquals(0, counter.get());
	}
	
	@Test
	void constructorWithValueAndAction() {
		AtomicInteger counter = new AtomicInteger(0);
		
		assertDoesNotThrow(() -> new LazyInitialization<>(10, v -> counter.incrementAndGet()));
		assertDoesNotThrow(() -> new LazyInitialization<>(null, v -> counter.incrementAndGet()));
		assertThrows(NullPointerException.class, () -> new LazyInitialization<>(10, null));
		
		LazyInitialization<Integer> lazy = new LazyInitialization<>(10, v -> counter.incrementAndGet());
		assertTrue(lazy.isInstantiated());
		assertEquals(Integer.valueOf(10), lazy.get());
		assertEquals(0, counter.get());
	}
	
	@Test
	void getWhenNotInitialized() {
		LazyInitialization<String> lazy = new LazyInitialization<>();
		assertThrows(NotInitializedException.class, lazy::get);
	}
	
	@Test
	void getWhenInitialized() {
		LazyInitialization<Integer> lazy = new LazyInitialization<>(42);
		assertDoesNotThrow(lazy::get);
		assertEquals(Integer.valueOf(42), lazy.get());
	}
	
	@Test
	void getWithNullValue() {
		LazyInitialization<String> lazy = new LazyInitialization<>((String) null);
		assertDoesNotThrow(lazy::get);
		assertNull(lazy.get());
	}
	
	@Test
	void setWhenNotInitialized() {
		LazyInitialization<String> lazy = new LazyInitialization<>();
		assertDoesNotThrow(() -> lazy.set("value"));
		
		assertTrue(lazy.isInstantiated());
		assertEquals("value", lazy.get());
	}
	
	@Test
	void setWhenAlreadyInitialized() {
		LazyInitialization<Integer> lazy = new LazyInitialization<>(10);
		assertThrows(AlreadyInitializedException.class, () -> lazy.set(20));
		
		assertEquals(Integer.valueOf(10), lazy.get());
	}
	
	@Test
	void setWithNullValue() {
		LazyInitialization<String> lazy = new LazyInitialization<>();
		assertDoesNotThrow(() -> lazy.set(null));
		
		assertTrue(lazy.isInstantiated());
		assertNull(lazy.get());
	}
	
	@Test
	void setTriggersAction() {
		AtomicInteger counter = new AtomicInteger(0);
		LazyInitialization<String> lazy = new LazyInitialization<>(v -> {
			if ("test".equals(v)) {
				counter.incrementAndGet();
			}
		});
		
		lazy.set("test");
		assertEquals(1, counter.get());
		
		lazy = new LazyInitialization<>(v -> {
			if (v == null) {
				counter.incrementAndGet();
			}
		});
		lazy.set(null);
		assertEquals(2, counter.get());
	}
	
	@Test
	void isInstantiatedInitially() {
		LazyInitialization<String> lazy = new LazyInitialization<>();
		assertFalse(lazy.isInstantiated());
		
		LazyInitialization<Integer> lazyWithValue = new LazyInitialization<>(10);
		assertTrue(lazyWithValue.isInstantiated());
		
		LazyInitialization<String> lazyWithAction = new LazyInitialization<>(v -> {});
		assertFalse(lazyWithAction.isInstantiated());
	}
	
	@Test
	void isInstantiatedAfterSet() {
		LazyInitialization<String> lazy = new LazyInitialization<>();
		assertFalse(lazy.isInstantiated());
		
		lazy.set("value");
		assertTrue(lazy.isInstantiated());
	}
	
	@Test
	void isInstantiatedWithNullValue() {
		LazyInitialization<String> lazy = new LazyInitialization<>((String) null);
		assertTrue(lazy.isInstantiated());
		
		LazyInitialization<String> lazySet = new LazyInitialization<>();
		lazySet.set(null);
		assertTrue(lazySet.isInstantiated());
	}
	
	@Test
	void hashCodeConsistency() {
		LazyInitialization<String> lazy1 = new LazyInitialization<>("same");
		LazyInitialization<String> lazy2 = new LazyInitialization<>("same");
		
		assertEquals(lazy1.hashCode(), lazy2.hashCode());
		
		LazyInitialization<String> lazyUninitialized1 = new LazyInitialization<>();
		LazyInitialization<String> lazyUninitialized2 = new LazyInitialization<>();
		
		assertEquals(lazyUninitialized1.hashCode(), lazyUninitialized2.hashCode());
	}
}
