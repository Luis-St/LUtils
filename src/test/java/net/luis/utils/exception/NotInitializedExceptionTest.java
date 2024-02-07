package net.luis.utils.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotInitializedExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new NotInitializedException((String) null));
		assertDoesNotThrow(() -> new NotInitializedException((Throwable) null));
		assertDoesNotThrow(() -> new NotInitializedException(null, null));
	}
}