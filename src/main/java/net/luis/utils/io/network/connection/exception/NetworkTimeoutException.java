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

package net.luis.utils.io.network.connection.exception;

import net.luis.utils.io.network.IpEndpoint;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * Thrown when a network operation times out.<br>
 * This exception extends {@link NetworkConnectionException} and provides
 * additional information about the timeout duration that was exceeded.<br>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * try {
 *     client.connect(endpoint);
 * } catch (NetworkTimeoutException e) {
 *     System.out.println("Connection timed out after: " + e.timeout());
 * }
 * }</pre>
 *
 * @see NetworkConnectionException
 * @see NetworkErrorType#CONNECTION_TIMEOUT
 * @see NetworkErrorType#READ_TIMEOUT
 * @see NetworkErrorType#WRITE_TIMEOUT
 *
 * @author Luis-St
 */
public class NetworkTimeoutException extends NetworkConnectionException {

	/**
	 * The timeout duration that was exceeded.<br>
	 */
	private final @NonNull Duration timeout;

	/**
	 * Constructs a new network timeout exception with the specified error type and timeout.<br>
	 *
	 * @param errorType The type of timeout error
	 * @param timeout The timeout duration that was exceeded
	 * @throws NullPointerException If timeout is null
	 */
	public NetworkTimeoutException(@Nullable NetworkErrorType errorType, @NonNull Duration timeout) {
		super(errorType);
		this.timeout = Objects.requireNonNull(timeout, "Timeout must not be null");
	}

	/**
	 * Constructs a new network timeout exception with the specified error type, timeout, and endpoint.<br>
	 *
	 * @param errorType The type of timeout error
	 * @param timeout The timeout duration that was exceeded
	 * @param endpoint The endpoint involved in the failed operation
	 * @throws NullPointerException If timeout is null
	 */
	public NetworkTimeoutException(@Nullable NetworkErrorType errorType, @NonNull Duration timeout, @Nullable IpEndpoint endpoint) {
		super(errorType, endpoint);
		this.timeout = Objects.requireNonNull(timeout, "Timeout must not be null");
	}

	/**
	 * Constructs a new network timeout exception with the specified message, error type, and timeout.<br>
	 *
	 * @param message The message of the exception
	 * @param errorType The type of timeout error
	 * @param timeout The timeout duration that was exceeded
	 * @throws NullPointerException If timeout is null
	 */
	public NetworkTimeoutException(@Nullable String message, @Nullable NetworkErrorType errorType, @NonNull Duration timeout) {
		super(message, errorType);
		this.timeout = Objects.requireNonNull(timeout, "Timeout must not be null");
	}

	/**
	 * Constructs a new network timeout exception with the specified message, error type, timeout, and endpoint.<br>
	 *
	 * @param message The message of the exception
	 * @param errorType The type of timeout error
	 * @param timeout The timeout duration that was exceeded
	 * @param endpoint The endpoint involved in the failed operation
	 * @throws NullPointerException If timeout is null
	 */
	public NetworkTimeoutException(@Nullable String message, @Nullable NetworkErrorType errorType, @NonNull Duration timeout, @Nullable IpEndpoint endpoint) {
		super(message, errorType, endpoint);
		this.timeout = Objects.requireNonNull(timeout, "Timeout must not be null");
	}

	/**
	 * Constructs a new network timeout exception with all details.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 * @param errorType The type of timeout error
	 * @param timeout The timeout duration that was exceeded
	 * @param endpoint The endpoint involved in the failed operation
	 * @throws NullPointerException If timeout is null
	 */
	public NetworkTimeoutException(@Nullable String message, @Nullable Throwable cause, @Nullable NetworkErrorType errorType, @NonNull Duration timeout, @Nullable IpEndpoint endpoint) {
		super(message, cause, errorType, endpoint);
		this.timeout = Objects.requireNonNull(timeout, "Timeout must not be null");
	}

	/**
	 * Returns the timeout duration that was exceeded.<br>
	 *
	 * @return The timeout duration, never null
	 */
	public @NonNull Duration timeout() {
		return this.timeout;
	}
}
