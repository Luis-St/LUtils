package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("DataFlowIssue")
class ResultTest {
	
	@Test
	void get() {
		assertEquals(Result.success(100).get(), Either.left(100));
		assertEquals(Result.error("Error").get(), Either.right("Error"));
	}
	
	@Test
	void isSuccess() {
		assertTrue(Result.success(100).isSuccess());
		assertFalse(Result.error("Error").isSuccess());
	}
	
	@Test
	void result() {
		assertTrue(Result.success(100).result().isPresent());
		assertTrue(Result.error("Error").result().isEmpty());
		assertEquals(100, Result.success(100).result().orElseThrow());
		assertThrows(NoSuchElementException.class, () -> Result.error("Error").result().orElseThrow());
	}
	
	@Test
	void orThrow() {
		assertDoesNotThrow(() -> Result.success(100).orThrow());
		assertDoesNotThrow(() -> Result.success(100).orThrow(RuntimeException::new));
		assertThrows(NoSuchElementException.class, () -> Result.error("Error").orThrow());
		assertThrows(NullPointerException.class, () -> Result.error("Error").orThrow(null));
		assertThrows(RuntimeException.class, () -> Result.error("Error").orThrow(RuntimeException::new));
	}
	
	@Test
	void isError() {
		assertFalse(Result.success(100).isError());
		assertTrue(Result.error("Error").isError());
	}
	
	@Test
	void error() {
		assertTrue(Result.success(100).error().isEmpty());
		assertTrue(Result.error("Error").error().isPresent());
		assertEquals("Error", Result.error("Error").error().orElseThrow());
		assertThrows(NoSuchElementException.class, () -> Result.success(100).error().orElseThrow());
	}
}