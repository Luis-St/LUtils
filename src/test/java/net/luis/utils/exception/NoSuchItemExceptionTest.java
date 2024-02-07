package net.luis.utils.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoSuchItemExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new NoSuchItemException((String) null));
		assertDoesNotThrow(() -> new NoSuchItemException((Throwable) null));
		assertDoesNotThrow(() -> new NoSuchItemException(null, null));
	}
}