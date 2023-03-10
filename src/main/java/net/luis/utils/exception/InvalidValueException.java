package net.luis.utils.exception;

import java.io.Serial;

/**
 *
 * @author Luis-st
 *
 */

public class InvalidValueException extends RuntimeException {
	
	@Serial
	private static final long serialVersionUID = -214146787383637863L;
	
	public InvalidValueException(String message) {
		super(message);
	}
	
	public InvalidValueException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
