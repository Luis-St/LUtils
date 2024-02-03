package net.luis.utils.exception;

import net.luis.utils.util.LazyInitialization;
import org.jetbrains.annotations.NotNull;

/**
 * Thrown when an initialization is attempted on an object that has already been initialized.<br>
 *
 * @see LazyInitialization
 *
 * @author Luis-St
 */
public class AlreadyInitializedException extends RuntimeException {
	
	/**
	 * Constructs a new {@link AlreadyInitializedException} with no details.<br>
	 */
	public AlreadyInitializedException() {
		super();
	}
	
	/**
	 * Constructs a new {@link AlreadyInitializedException} with the specified message.<br>
	 * @param message The message of the exception
	 */
	public AlreadyInitializedException(@NotNull String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@link AlreadyInitializedException} with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public AlreadyInitializedException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@link AlreadyInitializedException} with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public AlreadyInitializedException(@NotNull Throwable cause) {
		super(cause);
	}
}
