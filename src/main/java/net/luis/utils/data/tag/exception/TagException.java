package net.luis.utils.data.tag.exception;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;

/**
 *
 * @author Luis-st
 *
 */

public class TagException extends RuntimeException {
	
	@Serial
	private static final long serialVersionUID = 6187701756812507195L;
	
	public TagException() {
		super();
	}
	
	public TagException(@NotNull String message) {
		super(message);
	}
	
	public TagException(@NotNull Throwable cause) {
		super(cause);
	}
	
	public TagException(@NotNull String message, @NotNull Throwable cause) {
		super(message, cause);
	}
	
}
