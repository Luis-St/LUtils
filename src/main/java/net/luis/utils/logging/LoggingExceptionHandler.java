/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
	
	/**
	 * Constructs a new logging exception handler.<br>
	 */
	public LoggingExceptionHandler() {}
	
	@Override
	public void uncaughtException(@NotNull Thread thread, @Nullable Throwable throwable) {
		LOGGER.error("Error in thread {}: {}", thread.getName(), throwable);
	}
}
