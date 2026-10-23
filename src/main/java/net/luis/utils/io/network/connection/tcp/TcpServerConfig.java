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

import net.luis.utils.io.network.connection.*;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * Configuration options for stream servers.<br>
 * <p>
 *     This record holds the transport settings of a {@link TcpServer} and the transport part of an SSL server configuration.<br>
 *     The only callback is the error handler, which receives the failures that occur on the threads of the server.<br>
 *     What happens on a connection is decided by the {@link ConnectionHandler} the server is created with.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TcpServerConfig config = TcpServerConfig.builder()
 *     .backlog(100)
 *     .clientReadTimeout(Duration.ofMinutes(5))
 *     .executorStrategy(ClientExecutorStrategy.fixedPool(10))
 *     .onError((client, error) -> error.printStackTrace())
 *     .build();
 * }</pre>
 *
 * @see TcpServerConfigBuilder
 * @see TcpServer
 *
 * @author Luis-St
 *
 * @param backlog The maximum number of pending connections in the queue
 * @param maxMessageSize The maximum length of a message in bytes
 * @param framing The framing that delimits messages on the stream
 * @param clientReadTimeout The maximum time a receive on a connection waits for data, or null to wait forever
 * @param tcpNoDelay Whether to disable Nagle's algorithm for the connections
 * @param keepAlive Whether to enable TCP keep-alive for the connections
 * @param executorStrategy The strategy that creates the executor the connections are served on
 * @param onError The handler for failures on the threads of the server, or null to use the uncaught exception handler
 */
public record TcpServerConfig(
	int backlog,
	int maxMessageSize,
	@NonNull Framing framing,
	@Nullable Duration clientReadTimeout,
	boolean tcpNoDelay,
	boolean keepAlive,
	@NonNull ClientExecutorStrategy executorStrategy,
	@Nullable ErrorHandler onError
) {
	
	/**
	 * Default configuration for stream servers.<br>
	 * <ul>
	 *     <li>{@link #backlog} = {@code 50}</li>
	 *     <li>{@link #maxMessageSize} = {@code 8192}</li>
	 *     <li>{@link #framing} = {@link Framing#lengthPrefixed()}</li>
	 *     <li>{@link #clientReadTimeout} = {@code null} (wait forever)</li>
	 *     <li>{@link #tcpNoDelay} = {@code true}</li>
	 *     <li>{@link #keepAlive} = {@code true}</li>
	 *     <li>{@link #executorStrategy} = {@link ClientExecutorStrategy#virtualThreads()}</li>
	 *     <li>{@link #onError} = {@code null} (uncaught exception handler)</li>
	 * </ul>
	 */
	public static final TcpServerConfig DEFAULT = new TcpServerConfig(50, 8192, Framing.lengthPrefixed(), null, true, true, ClientExecutorStrategy.virtualThreads(), null);
	
	/**
	 * Constructs a new stream server configuration.<br>
	 *
	 * @param backlog The maximum number of pending connections in the queue
	 * @param maxMessageSize The maximum length of a message in bytes
	 * @param framing The framing that delimits messages on the stream
	 * @param clientReadTimeout The maximum time a receive on a connection waits for data, or null to wait forever
	 * @param tcpNoDelay Whether to disable Nagle's algorithm for the connections
	 * @param keepAlive Whether to enable TCP keep-alive for the connections
	 * @param executorStrategy The strategy that creates the executor the connections are served on
	 * @param onError The handler for failures on the threads of the server, or null to use the uncaught exception handler
	 * @throws NullPointerException If the framing or the executor strategy is null
	 * @throws IllegalArgumentException If the backlog or the max message size is less than 1 or the client read timeout is not positive
	 */
	public TcpServerConfig {
		Objects.requireNonNull(framing, "Framing must not be null");
		Objects.requireNonNull(executorStrategy, "Executor strategy must not be null");
		NetworkUtils.validateTimeout(clientReadTimeout, "Client read timeout");
		if (backlog < 1) {
			throw new IllegalArgumentException("Backlog must be at least 1: " + backlog);
		}
		if (maxMessageSize < 1) {
			throw new IllegalArgumentException("Max message size must be at least 1: " + maxMessageSize);
		}
	}
	
	/**
	 * Creates a new builder for constructing a stream server configuration.<br>
	 * @return A new builder with default values
	 */
	public static @NonNull TcpServerConfigBuilder builder() {
		return new TcpServerConfigBuilder();
	}
}
