package net.luis.utils.io.data.json.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonSyntaxException}.<br>
 *
 * @author Luis-St
 */
class JsonSyntaxExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new JsonSyntaxException((String) null));
		assertDoesNotThrow(() -> new JsonSyntaxException((Throwable) null));
		assertDoesNotThrow(() -> new JsonSyntaxException(null, null));
	}
}