package net.luis.utils.io.codec.decoder;

import org.jetbrains.annotations.Nullable;

/**
 * Thrown when an error occurs during decoding an object.<br>
 *
 * @see Decoder
 *
 * @author Luis-St
 */
public class DecoderException extends RuntimeException {
	
	/**
	 * Constructs a new decoder exception with no details.<br>
	 */
	public DecoderException() {}
	
	/**
	 * Constructs a new decoder exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public DecoderException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new decoder exception with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public DecoderException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new decoder exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public DecoderException(@Nullable Throwable cause) {
		super(cause);
	}
}
