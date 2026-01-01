/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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
 * Test class for {@link Lazy}.<br>
 *
 * @author Luis-St
 */
class LazyTest {
	
	@Test
	void constructorWithoutParameters() {
		assertDoesNotThrow(() -> new Lazy<>());
		
		Lazy<String> lazy = new Lazy<>();
		assertFalse(lazy.isInstantiated());
	}
	
	@Test
	void constructorWithValue() {
		assertDoesNotThrow(() -> new Lazy<>(10));
		assertDoesNotThrow(() -> new Lazy<>((Object) null));
		
		Lazy<Integer> lazy = new Lazy<>(10);
		assertTrue(lazy.isInstantiated());
		assertEquals(Integer.valueOf(10), lazy.get());
		
		Lazy<String> lazyNull = new Lazy<>((String) null);
		assertTrue(lazyNull.isInstantiated());
		assertNull(lazyNull.get());
	}
	
	@Test
	void constructorWithAction() {
		AtomicInteger counter = new AtomicInteger(0);
		
		assertDoesNotThrow(() -> new Lazy<>(v -> counter.incrementAndGet()));
		assertThrows(NullPointerException.class, () -> new Lazy<>(null));
		
		Lazy<String> lazy = new Lazy<>(v -> counter.incrementAndGet());
		assertFalse(lazy.isInstantiated());
		assertEquals(0, counter.get());
	}
	
	@Test
	void constructorWithValueAndAction() {
		AtomicInteger counter = new AtomicInteger(0);
		
		assertDoesNotThrow(() -> new Lazy<>(10, v -> counter.incrementAndGet()));
		assertDoesNotThrow(() -> new Lazy<>(null, v -> counter.incrementAndGet()));
		assertThrows(NullPointerException.class, () -> new Lazy<>(10, null));
		
		Lazy<Integer> lazy = new Lazy<>(10, v -> counter.incrementAndGet());
		assertTrue(lazy.isInstantiated());
		assertEquals(Integer.valueOf(10), lazy.get());
		assertEquals(0, counter.get());
	}
	
	@Test
	void getWhenNotInitialized() {
		Lazy<String> lazy = new Lazy<>();
		assertThrows(NotInitializedException.class, lazy::get);
	}
	
	@Test
	void getWhenInitialized() {
		Lazy<Integer> lazy = new Lazy<>(42);
		assertDoesNotThrow(lazy::get);
		assertEquals(Integer.valueOf(42), lazy.get());
	}
	
	@Test
	void getWithNullValue() {
		Lazy<String> lazy = new Lazy<>((String) null);
		assertDoesNotThrow(lazy::get);
		assertNull(lazy.get());
	}
	
	@Test
	void setWhenNotInitialized() {
		Lazy<String> lazy = new Lazy<>();
		assertDoesNotThrow(() -> lazy.set("value"));
		
		assertTrue(lazy.isInstantiated());
		assertEquals("value", lazy.get());
	}
	
	@Test
	void setWhenAlreadyInitialized() {
		Lazy<Integer> lazy = new Lazy<>(10);
		assertThrows(AlreadyInitializedException.class, () -> lazy.set(20));
		
		assertEquals(Integer.valueOf(10), lazy.get());
	}
	
	@Test
	void setWithNullValue() {
		Lazy<String> lazy = new Lazy<>();
		assertDoesNotThrow(() -> lazy.set(null));
		
		assertTrue(lazy.isInstantiated());
		assertNull(lazy.get());
	}
	
	@Test
	void setTriggersAction() {
		AtomicInteger counter = new AtomicInteger(0);
		Lazy<String> lazy = new Lazy<>(v -> {
			if ("test".equals(v)) {
				counter.incrementAndGet();
			}
		});
		
		lazy.set("test");
		assertEquals(1, counter.get());
		
		lazy = new Lazy<>(v -> {
			if (v == null) {
				counter.incrementAndGet();
			}
		});
		lazy.set(null);
		assertEquals(2, counter.get());
	}
	
	@Test
	void isInstantiatedInitially() {
		Lazy<String> lazy = new Lazy<>();
		assertFalse(lazy.isInstantiated());
		
		Lazy<Integer> lazyWithValue = new Lazy<>(10);
		assertTrue(lazyWithValue.isInstantiated());
		
		Lazy<String> lazyWithAction = new Lazy<>(v -> {});
		assertFalse(lazyWithAction.isInstantiated());
	}
	
	@Test
	void isInstantiatedAfterSet() {
		Lazy<String> lazy = new Lazy<>();
		assertFalse(lazy.isInstantiated());
		
		lazy.set("value");
		assertTrue(lazy.isInstantiated());
	}
	
	@Test
	void isInstantiatedWithNullValue() {
		Lazy<String> lazy = new Lazy<>((String) null);
		assertTrue(lazy.isInstantiated());
		
		Lazy<String> lazySet = new Lazy<>();
		lazySet.set(null);
		assertTrue(lazySet.isInstantiated());
	}
	
	@Test
	void hashCodeConsistency() {
		Lazy<String> lazy1 = new Lazy<>("same");
		Lazy<String> lazy2 = new Lazy<>("same");
		
		assertEquals(lazy1.hashCode(), lazy2.hashCode());
		
		Lazy<String> lazyUninitialized1 = new Lazy<>();
		Lazy<String> lazyUninitialized2 = new Lazy<>();
		
		assertEquals(lazyUninitialized1.hashCode(), lazyUninitialized2.hashCode());
	}
}
