package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Result}
 *
 * @author Luis-St
 */
class ResultTest {
	
	@Test
	void createSuccess() {
		assertNotNull(Result.success(100));
		assertDoesNotThrow(() -> Result.success(100));
		assertDoesNotThrow(() -> Result.success(null));
	}
	
	@Test
	void createError() {
		assertNotNull(Result.error("Error"));
		assertDoesNotThrow(() -> Result.error("Error"));
		assertThrows(NullPointerException.class, () -> Result.error(null));
	}
	
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
		assertEquals(Optional.of(100), Result.success(100).result());
		assertEquals(Optional.empty(), Result.error("Error").result());
	}
	
	@Test
	void orThrow() {
		assertDoesNotThrow(() -> Result.success(100).orThrow());
		assertDoesNotThrow(() -> Result.success(100).orThrow(RuntimeException::new));
		assertThrows(IllegalStateException.class, () -> Result.error("Error").orThrow());
		assertThrows(NullPointerException.class, () -> Result.error("Error").orThrow(null));
		assertThrows(NoSuchElementException.class, () -> Result.error("Error").orThrow(NoSuchElementException::new));
	}
	
	@Test
	void isError() {
		assertFalse(Result.success(100).isError());
		assertTrue(Result.error("Error").isError());
	}
	
	@Test
	void error() {
		assertEquals(Optional.empty(), Result.success(100).error());
		assertEquals(Optional.of("Error"), Result.error("Error").error());
	}
}