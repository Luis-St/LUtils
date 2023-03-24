package net.luis.utils.data.tag.exception;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.nio.file.Path;

/**
 *
 * @author Luis-St
 *
 */

public class LoadTagException extends TagException {
	
	@Serial
	private static final long serialVersionUID = 739609791447382568L;
	
	public LoadTagException() {
		super();
	}
	
	public LoadTagException(@NotNull String message) {
		super(message);
	}
	
	public LoadTagException(@NotNull Throwable cause) {
		super(cause);
	}
	
	public LoadTagException(@NotNull Path path, @NotNull Throwable cause) {
		super("Tag cannot be loaded from file " + path, cause);
	}
	
}
