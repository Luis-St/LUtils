package net.luis.utils.util;

import net.luis.utils.exception.AlreadyInitializedException;
import net.luis.utils.exception.NotInitializedException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("DataFlowIssue")
class LazyInitializationTest {
	
	@Test
	void get() {
		assertThrows(NotInitializedException.class, new LazyInitialization<>()::get);
		assertDoesNotThrow(new LazyInitialization<>(10)::get);
		assertEquals(10, new LazyInitialization<>(10).get());
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