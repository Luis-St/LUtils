package net.luis.utils.io.codec.decoder;

import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class DecoderException extends RuntimeException {
	
	public DecoderException() {}
	
	public DecoderException(@Nullable String message) {
		super(message);
	}
	
	public DecoderException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	public DecoderException(@Nullable Throwable cause) {
		super(cause);
	}
}
