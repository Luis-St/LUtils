package net.luis.utils.data.tag.exception;

import java.nio.file.Path;

/**
 *
 * @author Luis-st
 *
 */

public class LoadTagException extends TagException {
	
	private static final long serialVersionUID = 739609791447382568L;
	
	public LoadTagException() {
		super();
	}
	
	public LoadTagException(String message) {
		super(message);
	}
	
	public LoadTagException(Throwable cause) {
		super(cause);
	}
	
	public LoadTagException(Path path, Throwable cause) {
		super("Tag cannot be loaded from file " + path.toString(), cause);
	}
	
}
