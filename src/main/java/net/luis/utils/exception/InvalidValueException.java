package net.luis.utils.exception;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public class InvalidValueException extends RuntimeException {
	
	public InvalidValueException() {
		super();
	}
	
	public InvalidValueException(@NotNull String message) {
		super(message);
	}
	
	public InvalidValueException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
	public InvalidValueException(@NotNull Throwable cause) {
		super(cause);
	}
}
