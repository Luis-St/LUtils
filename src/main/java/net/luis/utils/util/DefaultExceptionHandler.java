package net.luis.utils.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
	
	private static final Logger LOGGER = LogManager.getLogger(DefaultExceptionHandler.class);
	
	@Override
	public void uncaughtException(@NotNull Thread thread, Throwable throwable) {
		LOGGER.error("Error in thread {}: {}", thread.getName(), throwable);
	}
}
