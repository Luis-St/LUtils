package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LazyLoad}.<br>
 *
 * @author Luis-St
 */
class LazyLoadTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new LazyLoad<>(null));
		assertDoesNotThrow(() -> new LazyLoad<>(() -> "Hello World!"));
	}
	
	@Test
	void load() {
		LazyLoad<String> lazyLoad = new LazyLoad<>(() -> "Hello World!");
		assertDoesNotThrow(lazyLoad::load);
		assertEquals("Hello World!", lazyLoad.get());
	}
	
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
		assertNull(new LazyLoad<>(() -> null).get());
	}
}