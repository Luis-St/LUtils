package net.luis.utils.logging;

import org.jetbrains.annotations.NotNull;

/**
 * The possible logging types.
 *
 * @author Luis-St
 */
public enum LoggingType {
	/**
	 * The logging will be printed to the console.<br>
	 */
	CONSOLE,
	/**
	 * The logging will be printed to a file.<br>
	 */
	FILE;
	
	@Override
	public @NotNull String toString() {
		return this.name().toLowerCase();
	}
}
