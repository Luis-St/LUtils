package net.luis.utils.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StructuredConcurrencyException}.<br>
 *
 * @author Luis-St
 */
class StructuredConcurrencyExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new StructuredConcurrencyException((String) null));
		assertDoesNotThrow(() -> new StructuredConcurrencyException((Throwable) null));
		assertDoesNotThrow(() -> new StructuredConcurrencyException(null, null));
	}
}
