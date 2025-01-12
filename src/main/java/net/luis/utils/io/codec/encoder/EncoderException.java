package net.luis.utils.io.codec.encoder;

import org.jetbrains.annotations.Nullable;

/**
 * Thrown when an error occurs during encoding an object.<br>
 *
 * @see Encoder
 *
 * @author Luis-St
 */
public class EncoderException extends RuntimeException {
	
	/**
	 * Constructs a new encoder exception with no details.<br>
	 */
	public EncoderException() {}
	
	/**
	 * Constructs a new encoder exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public EncoderException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new encoder exception with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public EncoderException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new encoder exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public EncoderException(@Nullable Throwable cause) {
		super(cause);
	}
}
