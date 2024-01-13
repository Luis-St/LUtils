package net.luis.utils.exception;

import org.jetbrains.annotations.NotNull;

public class NoSuchItemException extends RuntimeException {
	
	public NoSuchItemException() {
		super();
	}
	
	public NoSuchItemException(@NotNull String message) {
		super(message);
	}
	
	public NoSuchItemException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
	public NoSuchItemException(@NotNull Throwable cause) {
		super(cause);
	}
	
}
