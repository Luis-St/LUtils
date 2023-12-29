package net.luis.utils.util;

import net.luis.utils.exception.AlreadyInitialisedException;
import net.luis.utils.exception.NotInitialisedException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("DataFlowIssue")
class LazyInstantiationTest {
	
	@Test
	void get() {
		assertThrows(NotInitialisedException.class, new LazyInstantiation<>()::get);
		assertDoesNotThrow(new LazyInstantiation<>(10)::get);
		assertEquals(10, new LazyInstantiation<>(10).get());
	}
	
	@Test
	void set() {
		LazyInstantiation<Integer> lazy = new LazyInstantiation<>();
		assertDoesNotThrow(() -> lazy.set(10));
		assertThrows(AlreadyInitialisedException.class, () -> lazy.set(10));
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
		CompletableFuture<Integer> future = assertDoesNotThrow(() -> lazy.whenInstantiated((value) -> assertEquals(10, value)));
		assertFalse(future.isDone());
		lazy.set(10);
		assertTrue(future.isDone());
	}
}