package net.luis.utils.data.tag.exception;

import java.nio.file.Path;

import net.luis.utils.data.tag.Tag;

/**
 *
 * @author Luis-st
 *
 */

public class SaveTagException extends TagException {
	
	private static final long serialVersionUID = 1058513685709077997L;
	
	public SaveTagException() {
		super();
	}
	
	public SaveTagException(String message) {
		super(message);
	}
	
	public SaveTagException(Throwable cause) {
		super(cause);
	}
	
	public SaveTagException(Tag tag, Path path) {
		super("Tag of type " + tag.getType().getVisitorName() + " cannot be stored in file " + path.toString());
	}
	
	public SaveTagException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
