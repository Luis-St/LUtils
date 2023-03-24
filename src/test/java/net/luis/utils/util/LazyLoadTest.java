package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class LazyLoadTest {
	
	@Test
	void isLoaded() {
		LazyLoad<String> lazyLoad = new LazyLoad<>(() -> "Hello World!");
		assertFalse(lazyLoad.isLoaded());
		lazyLoad.load();
		assertTrue(lazyLoad.isLoaded());
	}
	
	@Test
	void get() {
		assertEquals("Hello World!", new LazyLoad<>(() -> "Hello World!").get());
	}
}