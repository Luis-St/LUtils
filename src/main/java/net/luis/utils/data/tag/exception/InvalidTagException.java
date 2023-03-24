package net.luis.utils.data.tag.exception;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;

/**
 *
 * @author Luis-St
 *
 */

public class InvalidTagException extends TagException {
	
	@Serial
	private static final long serialVersionUID = -8636790467022682132L;
	
	public InvalidTagException(int id) {
		super("Invalid tag ID: " + id);
	}
	
	public InvalidTagException(int id, @NotNull Throwable cause) {
		super("Invalid tag ID: " + id, cause);
	}
	
}