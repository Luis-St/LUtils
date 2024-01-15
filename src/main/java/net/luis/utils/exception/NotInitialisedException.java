package net.luis.utils.exception;

import org.jetbrains.annotations.NotNull;

/**
 * @author Luis-St
 */
public class NotInitialisedException extends RuntimeException {
	
	public NotInitialisedException() {
		super();
	}
	
	public NotInitialisedException(@NotNull String message) {
		super(message);
	}
	
	public NotInitialisedException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
	public NotInitialisedException(@NotNull Throwable cause) {
		super(cause);
	}
}
