package net.luis.utils.io.data.json.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonTypeException}.<br>
 *
 * @author Luis-St
 */
class JsonTypeExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new JsonTypeException((String) null));
		assertDoesNotThrow(() -> new JsonTypeException((Throwable) null));
		assertDoesNotThrow(() -> new JsonTypeException(null, null));
	}
}