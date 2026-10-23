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

import net.luis.utils.io.network.Endpoint;
import net.luis.utils.io.network.connection.exception.*;
import net.luis.utils.io.network.connection.tcp.TcpClientConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import java.io.Closeable;
import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods for the network clients and servers.<br>
 * This class provides the helper methods shared by the TCP, SSL and UDP implementations.<br>
 *
 * @author Luis-St
 */
public final class NetworkUtils {
	
	/**
	 * The maximum time in seconds to wait for an owned executor to terminate.<br>
	 */
	private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;
	
	/**
	 * The maximum time in milliseconds a closing ssl socket waits for the close notification of its peer.<br>
	 */
	private static final int SSL_CLOSE_TIMEOUT_MILLIS = 1000;
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private NetworkUtils() {}
	
	/**
	 * Validates that the given data does not exceed the given maximum message size.<br>
	 *
	 * @param data The data to validate
	 * @param maxMessageSize The maximum allowed size in bytes
	 * @param endpoint The endpoint to attach to the thrown exception, or null if not available
	 * @throws NullPointerException If the data is null
	 * @throws NetworkConnectionException If the data exceeds the maximum message size
	 */
	public static void validateMessageSize(byte @NonNull [] data, int maxMessageSize, @Nullable Endpoint endpoint) throws NetworkConnectionException {
		Objects.requireNonNull(data, "Data must not be null");
		if (data.length > maxMessageSize) {
			throw new NetworkConnectionException("Message size " + data.length + " exceeds max message size " + maxMessageSize, NetworkErrorType.MESSAGE_TOO_LARGE, endpoint);
		}
	}
	
	/**
	 * Validates an optional timeout.<br>
	 * A timeout is either null, which means waiting forever, or positive.<br>
	 *
	 * @param timeout The timeout to validate, or null for no timeout
	 * @param name The name of the timeout used in the exception message
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the timeout is zero or negative
	 */
	public static void validateTimeout(@Nullable Duration timeout, @NonNull String name) {
		Objects.requireNonNull(name, "Name must not be null");
		if (timeout != null && (timeout.isZero() || timeout.isNegative())) {
			throw new IllegalArgumentException(name + " must be positive, use null to wait forever: " + timeout);
		}
	}
	
	/**
	 * Validates the grace period of a graceful server stop.<br>
	 *
	 * @param gracePeriod The grace period to validate
	 * @throws NullPointerException If the grace period is null
	 * @throws IllegalArgumentException If the grace period is negative
	 */
	public static void validateGracePeriod(@NonNull Duration gracePeriod) {
		Objects.requireNonNull(gracePeriod, "Grace period must not be null");
		if (gracePeriod.isNegative()) {
			throw new IllegalArgumentException("Grace period must not be negative: " + gracePeriod);
		}
	}
	
	/**
	 * Converts an optional timeout into the milliseconds expected by the socket timeout options.<br>
	 * No timeout is converted to zero, which the socket options interpret as waiting forever.<br>
	 * A positive timeout is converted to at least one millisecond and at most {@link Integer#MAX_VALUE} milliseconds.<br>
	 *
	 * @param timeout The timeout, or null for no timeout
	 * @return The timeout in milliseconds
	 * @throws IllegalArgumentException If the timeout is zero or negative
	 */
	public static int toTimeoutMillis(@Nullable Duration timeout) {
		validateTimeout(timeout, "Timeout");
		if (timeout == null) {
			return 0;
		}
		return Math.clamp(timeout.toMillis(), 1, Integer.MAX_VALUE);
	}
	
	/**
	 * Opens a plain socket and connects it to the given endpoint using the given configuration.<br>
	 * The socket options and the read timeout of the configuration are applied before the socket is returned.<br>
	 *
	 * @param endpoint The endpoint to connect to
	 * @param config The configuration of the connection
	 * @return The connected socket
	 * @throws NullPointerException If the endpoint or the configuration is null
	 * @throws NetworkTimeoutException If connecting times out
	 * @throws NetworkConnectionException If connecting fails
	 */
	public static @NonNull Socket connectSocket(@NonNull Endpoint endpoint, @NonNull TcpClientConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		
		Socket socket = new Socket();
		try {
			socket.setTcpNoDelay(config.tcpNoDelay());
			socket.setKeepAlive(config.keepAlive());
			socket.setSoTimeout(toTimeoutMillis(config.readTimeout()));
			socket.connect(endpoint.toInetSocketAddress(), toTimeoutMillis(config.connectTimeout()));
			return socket;
		} catch (IOException e) {
			closeQuietly(socket);
			throw mapConnectFailure(e, endpoint, config.connectTimeout());
		}
	}
	
	/**
	 * Maps a failure raised while establishing a connection to the matching network exception.<br>
	 * The returned exception is meant to be thrown by the caller.<br>
	 *
	 * @param cause The failure that was caught
	 * @param endpoint The endpoint that was being connected to
	 * @param connectTimeout The configured connect timeout, reported on a timeout
	 * @return The mapped exception to throw
	 * @throws NullPointerException If the cause, the endpoint or the connect timeout is null
	 */
	public static @NonNull NetworkConnectionException mapConnectFailure(@NonNull IOException cause, @NonNull Endpoint endpoint, @NonNull Duration connectTimeout) {
		Objects.requireNonNull(cause, "Cause must not be null");
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		Objects.requireNonNull(connectTimeout, "Connect timeout must not be null");
		
		return switch (cause) {
			case NetworkConnectionException e -> e;
			case SocketTimeoutException e -> new NetworkTimeoutException("Connection timed out to " + endpoint, e, NetworkErrorType.CONNECTION_TIMEOUT, connectTimeout, endpoint);
			case SSLHandshakeException e -> new NetworkConnectionException("SSL handshake failed with " + endpoint, e, NetworkErrorType.HANDSHAKE_FAILED, endpoint);
			case ConnectException e -> new NetworkConnectionException("Connection refused by " + endpoint, e, NetworkErrorType.CONNECTION_REFUSED, endpoint);
			case NoRouteToHostException e -> new NetworkConnectionException("Host unreachable: " + endpoint, e, NetworkErrorType.HOST_UNREACHABLE, endpoint);
			case UnknownHostException e -> new NetworkConnectionException("Unknown host: " + endpoint, e, NetworkErrorType.HOST_UNREACHABLE, endpoint);
			default -> new NetworkConnectionException("Failed to connect to " + endpoint, cause, NetworkErrorType.CONNECTION_FAILED, endpoint);
		};
	}
	
	/**
	 * Maps a failure raised while sending or receiving to the matching network exception.<br>
	 * The returned exception is meant to be thrown by the caller.<br>
	 * <p>
	 *     A {@link BindException} is mapped by {@link #mapBindFailure(BindException, String, Endpoint)}.<br>
	 *     The JDK reports a send that the operating system rejects for missing rights as one,
	 *     for example a broadcast datagram on a socket that does not allow broadcasts.
	 * </p>
	 *
	 * @param cause The failure that was caught
	 * @param message The message of the mapped exception
	 * @param endpoint The endpoint to attach to the mapped exception, or null if not available
	 * @return The mapped exception to throw
	 * @throws NullPointerException If the cause or the message is null
	 */
	public static @NonNull NetworkConnectionException mapIoFailure(@NonNull IOException cause, @NonNull String message, @Nullable Endpoint endpoint) {
		Objects.requireNonNull(cause, "Cause must not be null");
		Objects.requireNonNull(message, "Message must not be null");
		
		return switch (cause) {
			case NetworkConnectionException e -> e;
			case BindException e -> mapBindFailure(e, message, endpoint);
			case SocketException e -> new NetworkConnectionException(message + ", connection reset", e, NetworkErrorType.CONNECTION_RESET, endpoint);
			default -> new NetworkConnectionException(message, cause, NetworkErrorType.IO_ERROR, endpoint);
		};
	}
	
	/**
	 * Maps a failure of the operating system to use a local address to the matching network exception.<br>
	 * The returned exception is meant to be thrown by the caller.<br>
	 * <p>
	 *     The address may be in use, not available on this host or not permitted for the process.<br>
	 *     The JDK reports all of them as {@link BindException} and only its message tells them apart.<br>
	 *     A failure whose cause cannot be determined is mapped to {@link NetworkErrorType#ADDRESS_IN_USE}.
	 * </p>
	 *
	 * @param cause The failure that was caught
	 * @param message The message of the mapped exception
	 * @param endpoint The endpoint to attach to the mapped exception, or null if not available
	 * @return The mapped exception to throw
	 * @throws NullPointerException If the cause or the message is null
	 */
	public static @NonNull NetworkConnectionException mapBindFailure(@NonNull BindException cause, @NonNull String message, @Nullable Endpoint endpoint) {
		Objects.requireNonNull(cause, "Cause must not be null");
		Objects.requireNonNull(message, "Message must not be null");
		
		return switch (bindErrorType(cause)) {
			case ADDRESS_NOT_AVAILABLE -> new NetworkConnectionException(message + ", address not available", cause, NetworkErrorType.ADDRESS_NOT_AVAILABLE, endpoint);
			case PERMISSION_DENIED -> new NetworkConnectionException(message + ", permission denied", cause, NetworkErrorType.PERMISSION_DENIED, endpoint);
			default -> new NetworkConnectionException(message + ", address already in use", cause, NetworkErrorType.ADDRESS_IN_USE, endpoint);
		};
	}
	
	/**
	 * Determines the error type of the given bind failure from its message.<br>
	 * <p>
	 *     This is the only place that inspects an exception message, because the JDK maps the error numbers
	 *     {@code EADDRINUSE}, {@code EADDRNOTAVAIL} and {@code EACCES} to the same exception type.<br>
	 *     The messages are the ones the JDK uses on Linux, macOS and Windows.
	 * </p>
	 *
	 * @param cause The bind failure
	 * @return The error type, which is {@link NetworkErrorType#ADDRESS_IN_USE} if the message matches no other cause
	 * @throws NullPointerException If the cause is null
	 */
	private static @NonNull NetworkErrorType bindErrorType(@NonNull BindException cause) {
		Objects.requireNonNull(cause, "Cause must not be null");
		
		String message = Objects.requireNonNullElse(cause.getMessage(), "").toLowerCase(Locale.ROOT);
		if (message.contains("assign requested address")) {
			return NetworkErrorType.ADDRESS_NOT_AVAILABLE;
		} else if (message.contains("permission denied")) {
			return NetworkErrorType.PERMISSION_DENIED;
		}
		return NetworkErrorType.ADDRESS_IN_USE;
	}
	
	/**
	 * Reports an error that cannot be thrown to a caller.<br>
	 * The error is passed to the given error handler, or to the uncaught exception handler of the current thread if no error handler is given.<br>
	 *
	 * @param handler The error handler, or null to use the uncaught exception handler
	 * @param client The client the error occurred on, or null if it is not related to a client
	 * @param error The error to report
	 * @throws NullPointerException If the error is null
	 */
	public static void reportError(@Nullable ErrorHandler handler, @Nullable NetworkClient<?> client, @NonNull Throwable error) {
		Objects.requireNonNull(error, "Error must not be null");
		
		if (handler != null) {
			handler.handle(client, error);
			return;
		}
		
		Thread thread = Thread.currentThread();
		thread.getUncaughtExceptionHandler().uncaughtException(thread, error);
	}
	
	/**
	 * Closes the given resource and ignores any failure.<br>
	 * <p>
	 *     An ssl socket reads its input until the peer answers with a close notification when it is closed.<br>
	 *     Its read timeout is limited beforehand, so a peer that never answers cannot block the close forever.<br>
	 * </p>
	 *
	 * @param closeable The resource to close, or null to do nothing
	 */
	public static void closeQuietly(@Nullable Closeable closeable) {
		if (closeable instanceof SSLSocket sslSocket && !sslSocket.isClosed()) {
			try {
				int timeout = sslSocket.getSoTimeout();
				if (timeout == 0 || timeout > SSL_CLOSE_TIMEOUT_MILLIS) {
					sslSocket.setSoTimeout(SSL_CLOSE_TIMEOUT_MILLIS);
				}
			} catch (SocketException _) {}
		}
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException _) {}
		}
	}
	
	/**
	 * Shuts down an executor service gracefully.<br>
	 * <p>
	 *     The executor is given up to 5 seconds to finish its tasks, afterward it is shut down forcefully.<br>
	 *     The executor is only shut down if it is owned by the caller.
	 * </p>
	 *
	 * @param executor The executor service to shut down, or null if no executor was created
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
