package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

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
		assertEquals(Result.success(100).result().orElseThrow(), 100);
		assertThrows(NoSuchElementException.class, () -> Result.error("Error").result().orElseThrow());
	}
	
	@Test
	void orThrow() {
		assertDoesNotThrow(() -> Result.success(100).orThrow());
		assertDoesNotThrow(() -> Result.success(100).orThrow((error) -> System.out.println("Error of result:" + error)));
		assertThrows(NoSuchElementException.class, () -> Result.error("Error").orThrow());
		assertThrows(NoSuchElementException.class, () -> Result.error("Error").orThrow((error) -> {}));
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
		assertEquals(Result.error("Error").error().orElseThrow(), "Error");
		assertThrows(NoSuchElementException.class, () -> Result.success(100).error().orElseThrow());
	}
}