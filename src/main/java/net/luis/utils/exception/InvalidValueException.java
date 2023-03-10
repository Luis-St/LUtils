package net.luis.utils.exception;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;

/**
 *
 * @author Luis-st
 *
 */

public class InvalidValueException extends RuntimeException {
	
	@Serial
	private static final long serialVersionUID = -214146787383637863L;
	
	public InvalidValueException(@NotNull String message) {
		super(message);
	}
	
	public InvalidValueException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
}
