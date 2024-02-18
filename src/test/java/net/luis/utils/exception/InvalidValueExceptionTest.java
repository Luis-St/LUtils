package net.luis.utils.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the class {@link InvalidValueException}.<br>
 *
 * @author Luis-St
 */
class InvalidValueExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new InvalidValueException((String) null));
		assertDoesNotThrow(() -> new InvalidValueException((Throwable) null));
		assertDoesNotThrow(() -> new InvalidValueException(null, null));
	}
}