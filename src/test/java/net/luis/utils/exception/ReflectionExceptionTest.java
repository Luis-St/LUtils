package net.luis.utils.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new ReflectionException((String) null));
		assertDoesNotThrow(() -> new ReflectionException((Throwable) null));
		assertDoesNotThrow(() -> new ReflectionException(null, null));
	}
}
