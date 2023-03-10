package net.luis.utils.data.tag.exception;

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
	
	public TagException(String message) {
		super(message);
	}
	
	public TagException(Throwable cause) {
		super(cause);
	}
	
	public TagException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
