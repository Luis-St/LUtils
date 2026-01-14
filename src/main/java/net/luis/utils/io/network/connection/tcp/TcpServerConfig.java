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
import net.luis.utils.io.network.connection.event.MessageEventHandler;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * Configuration options for TCP servers.<br>
 * This record provides settings for TCP server behavior such as backlog, client settings, and event handlers.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TcpServerConfig config = TcpServerConfig.builder()
 *     .backlog(100)
 *     .executorStrategy(ClientExecutorStrategy.virtualThreads())
 *     .onClientConnect(event -> System.out.println("Client connected: " + event.remoteEndpoint()))
 *     .onMessage((server, conn, data) -> {
 *         conn.send(("Echo: " + new String(data)).getBytes());
 *     })
 *     .build();
 *
 * try (TcpServer server = new TcpServer(bindEndpoint, config)) {
 *     server.start();
 * }
 * }</pre>
 *
 * @see TcpServerConfigBuilder
 * @see TcpServer
 *
 * @author Luis-St
 *
 * @param backlog Maximum number of pending connections in the queue
 * @param clientBufferSize Buffer size for each client connection in bytes
 * @param clientReadTimeout Read timeout for client connections (Duration.ZERO for infinite)
 * @param tcpNoDelay Whether to disable Nagle's algorithm for client connections
 * @param keepAlive Whether to enable TCP keep-alive for client connections
 * @param executorStrategy How to handle concurrent client connections
 * @param onClientConnect Handler called when a client connects
 * @param onClientDisconnect Handler called when a client disconnects
 * @param onMessage Handler called when a message is received from a client
 * @param onError Handler called when an error occurs
 */
public record TcpServerConfig(
	int backlog,
	int clientBufferSize,
	@NonNull Duration clientReadTimeout,
	boolean tcpNoDelay,
	boolean keepAlive,
	@NonNull ClientExecutorStrategy executorStrategy,
	@Nullable ConnectionEventHandler onClientConnect,
	@Nullable ConnectionEventHandler onClientDisconnect,
	@Nullable MessageEventHandler<TcpServer, TcpConnection> onMessage,
	@Nullable ErrorEventHandler onError
) {

	/**
	 * Default configuration for TCP servers.<br>
	 * <ul>
	 *     <li>{@link #backlog} = {@code 50}</li>
	 *     <li>{@link #clientBufferSize} = {@code 8192}</li>
	 *     <li>{@link #clientReadTimeout} = {@code Duration.ZERO} (infinite)</li>
	 *     <li>{@link #tcpNoDelay} = {@code true}</li>
	 *     <li>{@link #keepAlive} = {@code true}</li>
	 *     <li>{@link #executorStrategy} = {@code virtualThreads()}</li>
	 *     <li>All handlers = {@code null}</li>
	 * </ul>
	 */
	public static final TcpServerConfig DEFAULT = new TcpServerConfig(
		50,
		8192,
		Duration.ZERO,
		true,
		true,
		ClientExecutorStrategy.virtualThreads(),
		null,
		null,
		null,
		null
	);

	/**
	 * Constructs a new TCP server configuration.<br>
	 *
	 * @param backlog Maximum number of pending connections
	 * @param clientBufferSize Buffer size for client connections
	 * @param clientReadTimeout Read timeout for client connections
	 * @param tcpNoDelay Whether to disable Nagle's algorithm
	 * @param keepAlive Whether to enable TCP keep-alive
	 * @param executorStrategy How to handle concurrent clients
	 * @param onClientConnect Handler for client connections
	 * @param onClientDisconnect Handler for client disconnections
	 * @param onMessage Handler for incoming messages
	 * @param onError Handler for errors
	 * @throws NullPointerException If clientReadTimeout or executorStrategy is null
	 * @throws IllegalArgumentException If backlog or clientBufferSize is less than 1
	 */
	public TcpServerConfig {
		Objects.requireNonNull(clientReadTimeout, "Client read timeout must not be null");
		Objects.requireNonNull(executorStrategy, "Executor strategy must not be null");
		if (backlog < 1) {
			throw new IllegalArgumentException("Backlog must be at least 1: " + backlog);
		}
		if (clientBufferSize < 1) {
			throw new IllegalArgumentException("Client buffer size must be at least 1: " + clientBufferSize);
		}
	}

	/**
	 * Creates a new builder for constructing TCP server configuration.<br>
	 * @return A new builder with default values
	 */
	public static @NonNull TcpServerConfigBuilder builder() {
		return new TcpServerConfigBuilder();
	}
}
