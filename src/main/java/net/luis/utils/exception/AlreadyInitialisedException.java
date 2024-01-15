package net.luis.utils.exception;

import net.luis.utils.util.LazyInitialisation;
import org.jetbrains.annotations.NotNull;

/**
 * Thrown when an initialisation is attempted on an object that has already been initialised.<br>
 *
 * @see LazyInitialisation
 *
 * @author Luis-St
 */
public class AlreadyInitialisedException extends RuntimeException {
	
	/**
	 * Constructs a new {@link AlreadyInitialisedException} with no details.<br>
	 */
	public AlreadyInitialisedException() {
		super();
	}
	
	/**
	 * Constructs a new {@link AlreadyInitialisedException} with the specified message.<br>
	 * @param message The message of the exception
	 */
	public AlreadyInitialisedException(@NotNull String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@link AlreadyInitialisedException} with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public AlreadyInitialisedException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@link AlreadyInitialisedException} with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public AlreadyInitialisedException(@NotNull Throwable cause) {
		super(cause);
	}
}
