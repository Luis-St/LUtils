package net.luis.utils.exception;

import org.jetbrains.annotations.NotNull;

public class AlreadyInitialisedException extends RuntimeException {
	
	public AlreadyInitialisedException() {
		super();
	}
	
	public AlreadyInitialisedException(@NotNull String message) {
		super(message);
	}
	
	public AlreadyInitialisedException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
	public AlreadyInitialisedException(@NotNull Throwable cause) {
		super(cause);
	}
}
