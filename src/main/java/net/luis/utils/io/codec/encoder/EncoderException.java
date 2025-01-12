package net.luis.utils.io.codec.encoder;

import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class EncoderException extends RuntimeException {
	
	public EncoderException() {}
	
	public EncoderException(@Nullable String message) {
		super(message);
	}
	
	public EncoderException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	public EncoderException(@Nullable Throwable cause) {
		super(cause);
	}
}
