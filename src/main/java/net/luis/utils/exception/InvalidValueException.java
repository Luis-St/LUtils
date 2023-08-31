package net.luis.utils.exception;

import java.io.Serial;

/**
 *
 * @author Luis-St
 *
 */

public class InvalidValueException extends RuntimeException {
	
	@Serial
	private static final long serialVersionUID = -214146787383637863L;
	
	public InvalidValueException() {
		super();
	}
	
	public InvalidValueException(String message) {
		super(message);
	}
	
	public InvalidValueException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidValueException(Throwable cause) {
		super(cause);
	}
}
