package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class LazyInstantiationTest {
	
	@Test
	void get() {
		assertThrows(NullPointerException.class, new LazyInstantiation<>()::get);
		assertDoesNotThrow(new LazyInstantiation<>(10)::get);
		assertEquals(new LazyInstantiation<>(10).get(), 10);
	}
	
	@Test
	void set() {
		LazyInstantiation<Integer> lazy = new LazyInstantiation<>();
		assertDoesNotThrow(() -> lazy.set(10));
		assertThrows(ConcurrentModificationException.class, () -> lazy.set(10));
	}
	
	@Test
	void isInstantiated() {
		LazyInstantiation<Integer> lazy = new LazyInstantiation<>();
		assertFalse(lazy.isInstantiated());
		lazy.set(10);
		assertTrue(lazy.isInstantiated());
	}
	
	@Test
	void whenInstantiated() {
		LazyInstantiation<Integer> lazy = new LazyInstantiation<>();
		assertThrows(NullPointerException.class, () -> lazy.whenInstantiated(null));
		CompletableFuture<Integer> future = assertDoesNotThrow(() -> lazy.whenInstantiated((value) -> {
			assertEquals(value, 10);
		}));
		assertFalse(future.isDone());
		lazy.set(10);
		assertTrue(future.isDone());
	}
}