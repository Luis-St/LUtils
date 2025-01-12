package net.luis.utils.io.codec.decoder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DecoderException}.<br>
 *
 * @author Luis-St
 */
class DecoderExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new DecoderException((String) null));
		assertDoesNotThrow(() -> new DecoderException((Throwable) null));
		assertDoesNotThrow(() -> new DecoderException(null, null));
	}
}
