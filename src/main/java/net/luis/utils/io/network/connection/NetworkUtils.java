/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.network.connection;

import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods for network client and server implementations.<br>
 * This class provides common helper methods used across TCP and UDP implementations.<br>
 *
 * @author Luis-St
 */
public final class NetworkUtils {
	
	/**
	 * The default timeout in seconds for executor shutdown.<br>
	 */
	private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private NetworkUtils() {}
	
	/**
	 * Handles an error by notifying the configured error handler if present.<br>
	 * This method safely invokes the error handler without throwing exceptions.<br>
	 *
	 * @param handler The error handler to notify, or null if no handler is configured
	 * @param errorType The type of error that occurred
	 * @param message A human-readable error message
	 * @param cause The underlying exception
	 * @throws NullPointerException If the error type, message, or cause is null
	 */
	public static void handleError(@Nullable ErrorEventHandler handler, @NonNull NetworkErrorType errorType, @NonNull String message, @NonNull Throwable cause) {
		Objects.requireNonNull(errorType, "Error type must not be null");
		Objects.requireNonNull(message, "Message must not be null");
		Objects.requireNonNull(cause, "Cause must not be null");
		
		if (handler != null) {
			handler.handle(errorType, message, cause);
		}
	}
	
	/**
	 * Shuts down an executor service gracefully.<br>
	 * This method attempts a graceful shutdown, waiting up to 5 seconds for tasks to complete.<br>
	 * If tasks don't complete in time, it forces an immediate shutdown.<br>
	 * <p>
	 *     This method only shuts down the executor if {@code ownsExecutor} is {@code true},<br>
	 *     indicating that the caller is responsible for managing the executor's lifecycle.
	 * </p>
	 *
	 * @param executor The executor service to shut down, or null if no executor is configured
	 * @param ownsExecutor Whether the caller owns the executor and should shut it down
	 */
	public static void shutdownExecutor(@Nullable ExecutorService executor, boolean ownsExecutor) {
		if (executor != null && ownsExecutor) {
			executor.shutdown();
			
			try {
				if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
					executor.shutdownNow();
				}
			} catch (InterruptedException e) {
				executor.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}
}
