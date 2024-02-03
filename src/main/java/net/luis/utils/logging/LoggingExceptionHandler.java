package net.luis.utils.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of {@link Thread.UncaughtExceptionHandler} that logs uncaught exceptions.<br>
 *
 * @author Luis-St
 */
public class LoggingExceptionHandler implements Thread.UncaughtExceptionHandler {
	
	/**
	 * The logger for this class.<br>
	 */
	private static final Logger LOGGER = LogManager.getLogger(LoggingExceptionHandler.class);
	
	@Override
	public void uncaughtException(@NotNull Thread thread, @Nullable Throwable throwable) {
		LOGGER.error("Error in thread {}: {}", thread.getName(), throwable);
	}
}
