package net.luis.utils.exception;

import org.jetbrains.annotations.Nullable;

/**
 * Thrown to indicate that the value of a system property is invalid.<br>
 * <p>
 *     The exception message should contain the name of the system property and the invalid value.<br>
 *     The message may also contain a description of the expected value.<br>
 * </p>
 *
 * @author Luis-St
 */
public class InvalidValueException extends RuntimeException {
	
	/**
	 * Constructs a new {@link InvalidValueException} with no details.<br>
	 */
	public InvalidValueException() {}
	
	/**
	 * Constructs a new {@link InvalidValueException} with the specified message.<br>
	 * @param message The message of the exception
	 */
	public InvalidValueException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@link InvalidValueException} with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public InvalidValueException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@link InvalidValueException} with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public InvalidValueException(@Nullable Throwable cause) {
		super(cause);
	}
	
}
