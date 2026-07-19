package net.luis.utils.logging.formatter.pattern;

import org.jspecify.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class LogPatternException extends IllegalArgumentException {
	
	public LogPatternException() {	}
	
	public LogPatternException(@Nullable String message) {
		super(message);
	}
	
	public LogPatternException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	public LogPatternException(@Nullable Throwable cause) {
		super(cause);
	}
}
