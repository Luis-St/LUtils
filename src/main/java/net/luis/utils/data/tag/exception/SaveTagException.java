package net.luis.utils.data.tag.exception;

import net.luis.utils.data.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.nio.file.Path;

/**
 *
 * @author Luis-St
 *
 */

public class SaveTagException extends TagException {
	
	@Serial
	private static final long serialVersionUID = 1058513685709077997L;
	
	public SaveTagException() {
		super();
	}
	
	public SaveTagException(@NotNull String message) {
		super(message);
	}
	
	public SaveTagException(@NotNull Throwable cause) {
		super(cause);
	}
	
	public SaveTagException(@NotNull Tag tag, @NotNull Path path, @NotNull Throwable cause) {
		super("Tag of type " + tag.getType().getVisitorName() + " cannot be stored in file " + path, cause);
	}
	
}
