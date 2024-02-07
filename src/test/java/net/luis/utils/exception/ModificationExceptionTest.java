package net.luis.utils.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModificationExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new ModificationException((String) null));
		assertDoesNotThrow(() -> new ModificationException((Throwable) null));
		assertDoesNotThrow(() -> new ModificationException(null, null));
	}
}