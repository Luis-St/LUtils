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

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LazyLoad}.<br>
 *
 * @author Luis-St
 */
class LazyLoadTest {
	
	@Test
	void constructorRejectsNullSupplier() {
		assertThrows(NullPointerException.class, () -> new LazyLoad<>(null));
	}
	
	@Test
	void constructorAcceptsValidSupplier() {
		assertDoesNotThrow(() -> new LazyLoad<>(() -> "Hello World!"));
		assertDoesNotThrow(() -> new LazyLoad<>(() -> null));
		assertDoesNotThrow(() -> new LazyLoad<>(() -> 42));
	}
	
	@Test
	void loadExecutesSupplier() {
		AtomicInteger counter = new AtomicInteger(0);
		LazyLoad<String> lazyLoad = new LazyLoad<>(() -> {
			counter.incrementAndGet();
			return "loaded";
		});
		
		assertEquals(0, counter.get());
		lazyLoad.load();
		assertEquals(1, counter.get());
		assertEquals("loaded", lazyLoad.get());
	}
	
	@Test
	void loadOnlyExecutesSupplierOnce() {
		AtomicInteger counter = new AtomicInteger(0);
		LazyLoad<String> lazyLoad = new LazyLoad<>(() -> {
			counter.incrementAndGet();
			return "loaded";
		});
		
		lazyLoad.load();
		lazyLoad.load();
		lazyLoad.load();
		assertEquals(1, counter.get());
	}
	
	@Test
	void isLoadedReturnsFalseInitially() {
		LazyLoad<String> lazyLoad = new LazyLoad<>(() -> "Hello World!");
		assertFalse(lazyLoad.isLoaded());
	}
	
	@Test
	void isLoadedReturnsTrueAfterLoad() {
		LazyLoad<String> lazyLoad = new LazyLoad<>(() -> "Hello World!");
		lazyLoad.load();
		assertTrue(lazyLoad.isLoaded());
	}
	
	@Test
	void isLoadedReturnsTrueAfterGet() {
		LazyLoad<String> lazyLoad = new LazyLoad<>(() -> "Hello World!");
		lazyLoad.get();
		assertTrue(lazyLoad.isLoaded());
	}
	
	@Test
	void getReturnsSuppliedValue() {
		assertEquals("Hello World!", new LazyLoad<>(() -> "Hello World!").get());
		assertEquals(42, new LazyLoad<>(() -> 42).get());
		assertNull(new LazyLoad<>(() -> null).get());
	}
	
	@Test
	void getLoadsValueOnFirstCall() {
		AtomicInteger counter = new AtomicInteger(0);
		LazyLoad<String> lazyLoad = new LazyLoad<>(() -> {
			counter.incrementAndGet();
			return "loaded";
		});
		
		assertFalse(lazyLoad.isLoaded());
		String result = lazyLoad.get();
		assertTrue(lazyLoad.isLoaded());
		assertEquals("loaded", result);
		assertEquals(1, counter.get());
	}
	
	@Test
	void getReturnsSameValueOnMultipleCalls() {
		LazyLoad<String> lazyLoad = new LazyLoad<>(() -> "consistent");
		
		String first = lazyLoad.get();
		String second = lazyLoad.get();
		String third = lazyLoad.get();
		
		assertEquals(first, second);
		assertEquals(second, third);
	}
	
	@Test
	void equalsComparesValues() {
		LazyLoad<String> loaded1 = new LazyLoad<>(() -> "same");
		LazyLoad<String> loaded2 = new LazyLoad<>(() -> "same");
		LazyLoad<String> different = new LazyLoad<>(() -> "different");
		LazyLoad<String> nullValue = new LazyLoad<>(() -> null);
		
		loaded1.load();
		loaded2.load();
		different.load();
		nullValue.load();
		
		assertEquals(loaded1, loaded2);
		assertNotEquals(loaded1, different);
		assertNotEquals(loaded1, nullValue);
		assertNotEquals(loaded1, null);
		assertNotEquals(loaded1, "not a LazyLoad");
	}
	
	@Test
	void equalsComparesUnloadedValues() {
		LazyLoad<String> unloaded1 = new LazyLoad<>(() -> "test");
		LazyLoad<String> unloaded2 = new LazyLoad<>(() -> "test");
		
		assertEquals(unloaded1, unloaded2);
	}
	
	@Test
	void hashCodeIsSameForEqualObjects() {
		Supplier<String> supplier = () -> "test";
		LazyLoad<String> lazyLoad1 = new LazyLoad<>(supplier);
		LazyLoad<String> lazyLoad2 = new LazyLoad<>(supplier);
		
		lazyLoad1.load();
		lazyLoad2.load();
		
		assertEquals(lazyLoad1.hashCode(), lazyLoad2.hashCode());
	}
	
	@Test
	void toStringShowsValue() {
		LazyLoad<String> unloaded = new LazyLoad<>(() -> "test");
		assertEquals("lazy (null)", unloaded.toString());
		
		unloaded.load();
		assertEquals("lazy (test)", unloaded.toString());
		
		LazyLoad<Integer> withNumber = new LazyLoad<>(() -> 42);
		withNumber.load();
		assertEquals("lazy (42)", withNumber.toString());
		
		LazyLoad<String> withNull = new LazyLoad<>(() -> null);
		withNull.load();
		assertEquals("lazy (null)", withNull.toString());
	}
}
