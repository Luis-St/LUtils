package net.luis.utils.exception;

import org.jetbrains.annotations.NotNull;

/**
 * @author Luis-St
 */
public class NoSuchItemException extends NoSuchElementException {
	
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
