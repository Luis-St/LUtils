package net.luis.utils.exception;

import net.luis.utils.util.unsafe.reflection.ReflectionHelper;
import org.jetbrains.annotations.Nullable;

/**
 * Thrown when an error occurs during reflection.<br>
 *
 * @see ReflectionHelper
 *
 * @author Luis-St
 */
public class ReflectionException extends RuntimeException {
	
	/**
	 * Constructs a new {@link ReflectionException} with no details.<br>
	 */
	public ReflectionException() {}
	
	/**
	 * Constructs a new {@link ReflectionException} with the specified message.<br>
	 * @param message The message of the exception
	 */
	public ReflectionException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new {@link ReflectionException} with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public ReflectionException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@link ReflectionException} with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public ReflectionException(@Nullable Throwable cause) {
		super(cause);
	}
}
