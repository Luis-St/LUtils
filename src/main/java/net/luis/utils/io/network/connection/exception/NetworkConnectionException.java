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

import java.io.IOException;

/**
 * Thrown when a network connection operation fails.<br>
 * This exception provides detailed information about the failure including<br>
 * the error type and optionally the endpoint involved.<br>
 * <p>
 *     The {@link NetworkErrorType} enum categorizes the specific error, allowing
 *     callers to handle different error conditions appropriately. For example:
 * </p>
 * <pre>{@code
 * try {
 *     client.connect(endpoint);
 * } catch (NetworkConnectionException e) {
 *     if (e.errorType() == NetworkErrorType.CONNECTION_REFUSED) {
 *         System.out.println("Server not available at: " + e.endpoint());
 *     }
 * }
 * }</pre>
 *
 * @see NetworkErrorType
 *
 * @author Luis-St
 */
public class NetworkConnectionException extends IOException {

	/**
	 * The type of error that caused this exception.<br>
	 */
	private final @NonNull NetworkErrorType errorType;

	/**
	 * The endpoint involved in the failed operation, or null if not available.<br>
	 */
	private final @Nullable IpEndpoint endpoint;

	/**
	 * Constructs a new network connection exception with no details.<br>
	 * The error type defaults to {@link NetworkErrorType#UNKNOWN} and the endpoint is null.<br>
	 */
	public NetworkConnectionException() {
		this.errorType = NetworkErrorType.UNKNOWN;
		this.endpoint = null;
	}

	/**
	 * Constructs a new network connection exception with the specified message.<br>
	 * The error type defaults to {@link NetworkErrorType#UNKNOWN} and the endpoint is null.<br>
	 *
	 * @param message The message of the exception
	 */
	public NetworkConnectionException(@Nullable String message) {
		super(message);
		this.errorType = NetworkErrorType.UNKNOWN;
		this.endpoint = null;
	}

	/**
	 * Constructs a new network connection exception with the specified message and cause.<br>
	 * The error type defaults to {@link NetworkErrorType#UNKNOWN} and the endpoint is null.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public NetworkConnectionException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
		this.errorType = NetworkErrorType.UNKNOWN;
		this.endpoint = null;
	}

	/**
	 * Constructs a new network connection exception with the specified cause.<br>
	 * The error type defaults to {@link NetworkErrorType#UNKNOWN} and the endpoint is null.<br>
	 *
	 * @param cause The cause of the exception
	 */
	public NetworkConnectionException(@Nullable Throwable cause) {
		super(cause);
		this.errorType = NetworkErrorType.UNKNOWN;
		this.endpoint = null;
	}

	/**
	 * Constructs a new network connection exception with the specified error type.<br>
	 * The endpoint is null and no message is set.<br>
	 * If the provided error type is null, it defaults to {@link NetworkErrorType#UNKNOWN}.<br>
	 *
	 * @param errorType The type of error that caused this exception
	 */
	public NetworkConnectionException(@Nullable NetworkErrorType errorType) {
		this.errorType = errorType != null ? errorType : NetworkErrorType.UNKNOWN;
		this.endpoint = null;
	}

	/**
	 * Constructs a new network connection exception with the specified error type and endpoint.<br>
	 * No message is set, if the provided error type is null, it defaults to {@link NetworkErrorType#UNKNOWN}.<br>
	 *
	 * @param errorType The type of error that caused this exception
	 * @param endpoint The endpoint involved in the failed operation
	 */
	public NetworkConnectionException(@Nullable NetworkErrorType errorType, @Nullable IpEndpoint endpoint) {
		this.errorType = errorType != null ? errorType : NetworkErrorType.UNKNOWN;
		this.endpoint = endpoint;
	}

	/**
	 * Constructs a new network connection exception with the specified message and error type.<br>
	 * The endpoint is null, if the provided error type is null, it defaults to {@link NetworkErrorType#UNKNOWN}.<br>
	 *
	 * @param message The message of the exception
	 * @param errorType The type of error that caused this exception
	 */
	public NetworkConnectionException(@Nullable String message, @Nullable NetworkErrorType errorType) {
		super(message);
		this.errorType = errorType != null ? errorType : NetworkErrorType.UNKNOWN;
		this.endpoint = null;
	}

	/**
	 * Constructs a new network connection exception with the specified message, error type, and endpoint.<br>
	 * If the provided error type is null, it defaults to {@link NetworkErrorType#UNKNOWN}.<br>
	 *
	 * @param message The message of the exception
	 * @param errorType The type of error that caused this exception
	 * @param endpoint The endpoint involved in the failed operation
	 */
	public NetworkConnectionException(@Nullable String message, @Nullable NetworkErrorType errorType, @Nullable IpEndpoint endpoint) {
		super(message);
		this.errorType = errorType != null ? errorType : NetworkErrorType.UNKNOWN;
		this.endpoint = endpoint;
	}

	/**
	 * Constructs a new network connection exception with the specified message, cause, and error type.<br>
	 * The endpoint is null, if the provided error type is null, it defaults to {@link NetworkErrorType#UNKNOWN}.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 * @param errorType The type of error that caused this exception
	 */
	public NetworkConnectionException(@Nullable String message, @Nullable Throwable cause, @Nullable NetworkErrorType errorType) {
		super(message, cause);
		this.errorType = errorType != null ? errorType : NetworkErrorType.UNKNOWN;
		this.endpoint = null;
	}

	/**
	 * Constructs a new network connection exception with the specified message, cause, error type, and endpoint.<br>
	 * If the provided error type is null, it defaults to {@link NetworkErrorType#UNKNOWN}.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 * @param errorType The type of error that caused this exception
	 * @param endpoint The endpoint involved in the failed operation
	 */
	public NetworkConnectionException(@Nullable String message, @Nullable Throwable cause, @Nullable NetworkErrorType errorType, @Nullable IpEndpoint endpoint) {
		super(message, cause);
		this.errorType = errorType != null ? errorType : NetworkErrorType.UNKNOWN;
		this.endpoint = endpoint;
	}

	/**
	 * Returns the type of error that caused this exception.<br>
	 * @return The error type, never null
	 */
	public @NonNull NetworkErrorType errorType() {
		return this.errorType;
	}

	/**
	 * Returns the endpoint involved in the failed operation.<br>
	 * @return The endpoint, or null if not available
	 */
	public @Nullable IpEndpoint endpoint() {
		return this.endpoint;
	}
}
