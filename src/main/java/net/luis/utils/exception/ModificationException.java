package net.luis.utils.exception;

import net.luis.utils.collection.Registry;
import org.jetbrains.annotations.NotNull;

/**
 * Thrown when a method is called on an object which will modify it,<br>
 * but the object is not modifiable at the moment or at all.<br>
 *
 * @see Registry
 *
 * @author Luis-St
 */
public class ModificationException extends RuntimeException {
	
	/**
	 * Constructs a new {@link ModificationException} with no details.<br>
	 */
	public ModificationException() {
		super();
	}
	
	/**
	 * Constructs a new {@link ModificationException} with the specified message.<br>
	 * @param message The message of the exception
	 */
	public ModificationException(@NotNull String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@link ModificationException} with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public ModificationException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@link ModificationException} with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public ModificationException(@NotNull Throwable cause) {
		super(cause);
	}
}
