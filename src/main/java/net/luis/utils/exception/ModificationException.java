package net.luis.utils.exception;

import org.jetbrains.annotations.NotNull;

public class ModificationException extends RuntimeException {
	
	public ModificationException() {
		super();
	}
	
	public ModificationException(@NotNull String message) {
		super(message);
	}
	
	public ModificationException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
	public ModificationException(@NotNull Throwable cause) {
		super(cause);
	}
}
