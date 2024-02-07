package net.luis.utils.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlreadyInitializedExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new AlreadyInitializedException((String) null));
		assertDoesNotThrow(() -> new AlreadyInitializedException((Throwable) null));
		assertDoesNotThrow(() -> new AlreadyInitializedException(null, null));
	}
}