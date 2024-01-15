package net.luis.utils.exception;

import net.luis.utils.collection.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

/**
 * Thrown when a requested item does not exist in a collection like object.<br>
 * Library equivalent of {@link NoSuchElementException}.<br>
 *
 * @see Registry
 * @see NoSuchElementException
 *
 * @author Luis-St
 */
public class NoSuchItemException extends NoSuchElementException {
	
	/**
	 * Constructs a new {@link NoSuchItemException} with no details.<br>
	 */
	public NoSuchItemException() {
		super();
	}
	
	/**
	 * Constructs a new {@link NoSuchItemException} with the specified message.<br>
	 * @param message The message of the exception
	 */
	public NoSuchItemException(@NotNull String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@link NoSuchItemException} with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public NoSuchItemException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@link NoSuchItemException} with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public NoSuchItemException(@NotNull Throwable cause) {
		super(cause);
	}
}
