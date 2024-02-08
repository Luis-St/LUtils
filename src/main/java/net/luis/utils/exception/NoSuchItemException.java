package net.luis.utils.exception;

import net.luis.utils.collection.Registry;
import org.jetbrains.annotations.Nullable;

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
	public NoSuchItemException() {}
	
	/**
	 * Constructs a new {@link NoSuchItemException} with the specified message.<br>
	 * @param message The message of the exception
	 */
	public NoSuchItemException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@link NoSuchItemException} with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public NoSuchItemException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@link NoSuchItemException} with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public NoSuchItemException(@Nullable Throwable cause) {
		super(cause);
	}
}
