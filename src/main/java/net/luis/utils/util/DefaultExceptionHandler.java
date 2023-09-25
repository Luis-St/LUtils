package net.luis.utils.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
	
	private static final Logger LOGGER = LogManager.getLogger(DefaultExceptionHandler.class);
	
	@Override
	public void uncaughtException(@NotNull Thread thread, @Nullable Throwable throwable) {
		LOGGER.error("Error in thread {}: {}", thread.getName(), throwable);
	}
}
