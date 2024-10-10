package net.luis.utils.io.data.json.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonArrayIndexOutOfBoundsException}.<br>
 *
 * @author Luis-St
 */
class JsonArrayIndexOutOfBoundsExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new JsonArrayIndexOutOfBoundsException((String) null));
	}
}