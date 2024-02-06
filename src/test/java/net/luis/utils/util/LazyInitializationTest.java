package net.luis.utils.util;

import net.luis.utils.exception.AlreadyInitializedException;
import net.luis.utils.exception.NotInitializedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LazyInitialization}.<br>
 *
 * @author Luis-St
 */
class LazyInitializationTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new LazyInitialization<>());
		assertDoesNotThrow(() -> new LazyInitialization<>(10));
		assertDoesNotThrow(() -> new LazyInitialization<>((Object) null));
		assertDoesNotThrow(() -> new LazyInitialization<>((v) -> {}));
		assertThrows(NullPointerException.class, () -> new LazyInitialization<>(null));
		assertDoesNotThrow(() -> new LazyInitialization<>(10, (v) -> {}));
		assertDoesNotThrow(() -> new LazyInitialization<>(null, (v) -> {}));
		assertThrows(NullPointerException.class, () -> new LazyInitialization<>(null, null));
	}
	
	@Test
	void get() {
		assertThrows(NotInitializedException.class, new LazyInitialization<>()::get);
		assertDoesNotThrow(new LazyInitialization<>(10)::get);
		assertEquals(10, new LazyInitialization<>(10).get());
		assertNull(new LazyInitialization<>((Object) null).get());
	}
	
	@Test
	void set() {
		LazyInitialization<Integer> lazy = new LazyInitialization<>();
		assertDoesNotThrow(() -> lazy.set(10));
		assertThrows(AlreadyInitializedException.class, () -> lazy.set(10));
	}
	
	@Test
	void isInstantiated() {
		LazyInitialization<Integer> lazy = new LazyInitialization<>();
		assertFalse(lazy.isInstantiated());
		lazy.set(10);
		assertTrue(lazy.isInstantiated());
	}
}