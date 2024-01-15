package net.luis.utils.exception;

import net.luis.utils.util.LazyInitialisation;
import org.jetbrains.annotations.NotNull;

/**
 * Thrown when an access or action is attempted on an object that has not been initialised<br>
 * but requires initialisation before it can be used.<br>
 *
 * @see LazyInitialisation
 *
 * @author Luis-St
 */
public class NotInitialisedException extends RuntimeException {
	
	/**
	 * Constructs a new {@link NotInitialisedException} with no details.<br>
	 */
	public NotInitialisedException() {
		super();
	}
	
	/**
	 * Constructs a new {@link NotInitialisedException} with the specified message.<br>
	 * @param message The message of the exception
	 */
	public NotInitialisedException(@NotNull String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@link NotInitialisedException} with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public NotInitialisedException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@link NotInitialisedException} with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public NotInitialisedException(@NotNull Throwable cause) {
		super(cause);
	}
}
