package net.luis.utils.io.codec.encoder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EncoderException}.<br>
 *
 * @author Luis-St
 */
class EncoderExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new EncoderException((String) null));
		assertDoesNotThrow(() -> new EncoderException((Throwable) null));
		assertDoesNotThrow(() -> new EncoderException(null, null));
	}
}
