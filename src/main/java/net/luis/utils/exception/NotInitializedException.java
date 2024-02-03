package net.luis.utils.exception;

import net.luis.utils.util.LazyInitialization;
import org.jetbrains.annotations.NotNull;

/**
 * Thrown when an access or action is attempted on an object that has not been initialized<br>
 * but requires initialization before it can be used.<br>
 *
 * @see LazyInitialization
 *
 * @author Luis-St
 */
public class NotInitializedException extends RuntimeException {
	
	/**
	 * Constructs a new {@link NotInitializedException} with no details.<br>
	 */
	public NotInitializedException() {
		super();
	}
	
	/**
	 * Constructs a new {@link NotInitializedException} with the specified message.<br>
	 * @param message The message of the exception
	 */
	public NotInitializedException(@NotNull String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@link NotInitializedException} with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public NotInitializedException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@link NotInitializedException} with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public NotInitializedException(@NotNull Throwable cause) {
		super(cause);
	}
}