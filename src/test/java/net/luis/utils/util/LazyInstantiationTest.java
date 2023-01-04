package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-st
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
}