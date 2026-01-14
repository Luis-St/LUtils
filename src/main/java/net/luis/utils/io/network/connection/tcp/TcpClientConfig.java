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

package net.luis.utils.io.network.connection.tcp;

import net.luis.utils.io.network.connection.event.ConnectionEventHandler;
import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * Configuration options for TCP clients.<br>
 * This record provides settings for TCP socket behavior such as timeouts, buffer sizes, and event handlers.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TcpClientConfig config = TcpClientConfig.builder()
 *     .connectTimeout(Duration.ofSeconds(10))
 *     .readTimeout(Duration.ofSeconds(30))
 *     .tcpNoDelay(true)
 *     .onConnect(event -> System.out.println("Connected!"))
 *     .build();
 *
 * try (TcpClient client = new TcpClient(config)) {
 *     client.connect(serverEndpoint);
 * }
 * }</pre>
 *
 * @see TcpClientConfigBuilder
 * @see TcpClient
 *
 * @author Luis-St
 *
 * @param connectTimeout Maximum time to wait for connection establishment
 * @param readTimeout Maximum time to wait for read operations (Duration.ZERO for infinite)
 * @param writeTimeout Maximum time to wait for write operations (Duration.ZERO for infinite)
 * @param bufferSize Size of the read/write buffers in bytes
 * @param tcpNoDelay Whether to disable Nagle's algorithm (TCP_NODELAY)
 * @param keepAlive Whether to enable TCP keep-alive (SO_KEEPALIVE)
 * @param onConnect Handler called when connection is established
 * @param onDisconnect Handler called when connection is closed
 * @param onError Handler called when an error occurs
 */
public record TcpClientConfig(
	@NonNull Duration connectTimeout,
	@NonNull Duration readTimeout,
	@NonNull Duration writeTimeout,
	int bufferSize,
	boolean tcpNoDelay,
	boolean keepAlive,
	@Nullable ConnectionEventHandler onConnect,
	@Nullable ConnectionEventHandler onDisconnect,
	@Nullable ErrorEventHandler onError
) {
	
	/**
	 * Default configuration for TCP clients.<br>
	 * <ul>
	 *     <li>{@link #connectTimeout} = {@code 30 seconds}</li>
	 *     <li>{@link #readTimeout} = {@code Duration.ZERO} (infinite)</li>
	 *     <li>{@link #writeTimeout} = {@code Duration.ZERO} (infinite)</li>
	 *     <li>{@link #bufferSize} = {@code 8192}</li>
	 *     <li>{@link #tcpNoDelay} = {@code true}</li>
	 *     <li>{@link #keepAlive} = {@code true}</li>
	 *     <li>All handlers = {@code null}</li>
	 * </ul>
	 */
	public static final TcpClientConfig DEFAULT = new TcpClientConfig(Duration.ofSeconds(30), Duration.ZERO, Duration.ZERO, 8192, true, true, null, null, null);
	
	/**
	 * Constructs a new TCP client configuration.<br>
	 *
	 * @param connectTimeout Maximum time to wait for connection establishment
	 * @param readTimeout Maximum time to wait for read operations
	 * @param writeTimeout Maximum time to wait for write operations
	 * @param bufferSize Size of the read/write buffers in bytes
	 * @param tcpNoDelay Whether to disable Nagle's algorithm
	 * @param keepAlive Whether to enable TCP keep-alive
	 * @param onConnect Handler called when connection is established
	 * @param onDisconnect Handler called when connection is closed
	 * @param onError Handler called when an error occurs
	 * @throws NullPointerException If connectTimeout, readTimeout, or writeTimeout is null
	 * @throws IllegalArgumentException If bufferSize is less than 1
	 */
	public TcpClientConfig {
		Objects.requireNonNull(connectTimeout, "Connect timeout must not be null");
		Objects.requireNonNull(readTimeout, "Read timeout must not be null");
		Objects.requireNonNull(writeTimeout, "Write timeout must not be null");
		
		if (bufferSize < 1) {
			throw new IllegalArgumentException("Buffer size must be at least 1: " + bufferSize);
		}
	}
	
	/**
	 * Creates a new builder for constructing TCP client configuration.<br>
	 * @return A new builder with default values
	 */
	public static @NonNull TcpClientConfigBuilder builder() {
		return new TcpClientConfigBuilder();
	}
}
