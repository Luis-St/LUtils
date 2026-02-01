package net.luis.utils.io.codec.constraint.config.validator;

import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.Nullable;

/**
 * Thrown when a constraint matcher fails to match a constraint.<br>
 *
 * @see TypeProvider
 *
 * @author Luis-St
 */
public class ConstraintViolateException extends RuntimeException {
	
	/**
	 * Constructs a new constraint matcher failure exception with no message.<br>
	 */
	public ConstraintViolateException() {}
	
	/**
	 * Constructs a new constraint matcher failure exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public ConstraintViolateException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new constraint matcher failure exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public ConstraintViolateException(@Nullable Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructs a new constraint matcher failure exception with the specified message and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public ConstraintViolateException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
}
