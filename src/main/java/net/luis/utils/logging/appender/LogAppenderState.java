package net.luis.utils.logging.appender;

/**
 *
 * @author Luis-St
 *
 */

public enum LogAppenderState {
	
	ENABLED,
	DISABLED;
	
	private LogAppenderState() {}
	
	public boolean isEnabled() {
		return this == ENABLED;
	}
	
	public boolean isDisabled() {
		return this == DISABLED;
	}
}
