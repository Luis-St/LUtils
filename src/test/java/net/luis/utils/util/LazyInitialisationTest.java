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
class LazyInitialisationTest {
	
	@Test
	void get() {
		assertThrows(NotInitialisedException.class, new LazyInitialisation<>()::get);
		assertDoesNotThrow(new LazyInitialisation<>(10)::get);
		assertEquals(10, new LazyInitialisation<>(10).get());
	}
	
	@Test
	void set() {
		LazyInitialisation<Integer> lazy = new LazyInitialisation<>();
		assertDoesNotThrow(() -> lazy.set(10));
		assertThrows(AlreadyInitialisedException.class, () -> lazy.set(10));
	}
	
	@Test
	void isInstantiated() {
		LazyInitialisation<Integer> lazy = new LazyInitialisation<>();
		assertFalse(lazy.isInstantiated());
		lazy.set(10);
		assertTrue(lazy.isInstantiated());
	}
	
	@Test
	void whenInstantiated() {
		LazyInitialisation<Integer> lazy = new LazyInitialisation<>();
		assertThrows(NullPointerException.class, () -> lazy.onInitialisation(null));
		CompletableFuture<Integer> future = assertDoesNotThrow(() -> lazy.onInitialisation((value) -> assertEquals(10, value)));
		assertFalse(future.isDone());
		lazy.set(10);
		assertTrue(future.isDone());
	}
}