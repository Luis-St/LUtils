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

import net.luis.utils.io.network.connection.ErrorHandler;
import net.luis.utils.io.network.connection.Framing;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * Builder class for constructing a stream server configuration.<br>
 * Provides a fluent API for setting individual configuration options.<br>
 * <p>
 *     All options default to the values of {@link TcpServerConfig#DEFAULT}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TcpServerConfig config = TcpServerConfig.builder()
 *     .backlog(100)
 *     .maxMessageSize(4096)
 *     .executorStrategy(ClientExecutorStrategy.fixedPool(10))
 *     .build();
 * }</pre>
 *
 * @see TcpServerConfig
 *
 * @author Luis-St
 */
public final class TcpServerConfigBuilder {
	
	/**
	 * The maximum number of pending connections in the queue.<br>
	 */
	private int backlog = TcpServerConfig.DEFAULT.backlog();
	/**
	 * The maximum length of a message in bytes.<br>
	 */
	private int maxMessageSize = TcpServerConfig.DEFAULT.maxMessageSize();
	/**
	 * The framing that delimits messages on the stream.<br>
	 */
	private Framing framing = TcpServerConfig.DEFAULT.framing();
	/**
	 * The maximum time a receive on a connection waits for data, or null to wait forever.<br>
	 */
	private @Nullable Duration clientReadTimeout = TcpServerConfig.DEFAULT.clientReadTimeout();
	/**
	 * Whether to disable Nagle's algorithm for the connections.<br>
	 */
	private boolean tcpNoDelay = TcpServerConfig.DEFAULT.tcpNoDelay();
	/**
	 * Whether to enable TCP keep-alive for the connections.<br>
	 */
	private boolean keepAlive = TcpServerConfig.DEFAULT.keepAlive();
	/**
	 * The strategy that creates the executor the connections are served on.<br>
	 */
	private ClientExecutorStrategy executorStrategy = TcpServerConfig.DEFAULT.executorStrategy();
	/**
	 * The handler for failures on the threads of the server.<br>
	 */
	private @Nullable ErrorHandler onError = TcpServerConfig.DEFAULT.onError();
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	TcpServerConfigBuilder() {}
	
	/**
	 * Sets the maximum number of pending connections in the queue.<br>
	 *
	 * @param backlog The backlog size, which must be at least 1
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder backlog(int backlog) {
		this.backlog = backlog;
		return this;
	}
	
	/**
	 * Sets the maximum length of a message in bytes.<br>
	 *
	 * @param maxMessageSize The max message size, which must be at least 1
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder maxMessageSize(int maxMessageSize) {
		this.maxMessageSize = maxMessageSize;
		return this;
	}
	
	/**
	 * Sets the framing that delimits messages on the stream.<br>
	 * Both peers have to use the same framing.<br>
	 *
	 * @param framing The framing
	 * @return This builder for method chaining
	 * @throws NullPointerException If the framing is null
	 */
	public @NonNull TcpServerConfigBuilder framing(@NonNull Framing framing) {
		this.framing = Objects.requireNonNull(framing, "Framing must not be null");
		return this;
	}
	
	/**
	 * Sets the maximum time a receive on a connection waits for data.<br>
	 *
	 * @param clientReadTimeout The read timeout, which must be positive
	 * @return This builder for method chaining
	 * @throws NullPointerException If the read timeout is null
	 */
	public @NonNull TcpServerConfigBuilder clientReadTimeout(@NonNull Duration clientReadTimeout) {
		this.clientReadTimeout = Objects.requireNonNull(clientReadTimeout, "Client read timeout must not be null");
		return this;
	}
	
	/**
	 * Removes the read timeout of the connections, so that a receive waits forever.<br>
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder noClientReadTimeout() {
		this.clientReadTimeout = null;
		return this;
	}
	
	/**
	 * Sets whether to disable Nagle's algorithm for the connections.<br>
	 *
	 * @param tcpNoDelay True to disable Nagle's algorithm
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder tcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
		return this;
	}
	
	/**
	 * Sets whether to enable TCP keep-alive for the connections.<br>
	 *
	 * @param keepAlive True to enable keep-alive
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder keepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}
	
	/**
	 * Sets the strategy that creates the executor the connections are served on.<br>
	 *
	 * @param executorStrategy The executor strategy
	 * @return This builder for method chaining
	 * @throws NullPointerException If the executor strategy is null
	 */
	public @NonNull TcpServerConfigBuilder executorStrategy(@NonNull ClientExecutorStrategy executorStrategy) {
		this.executorStrategy = Objects.requireNonNull(executorStrategy, "Executor strategy must not be null");
		return this;
	}
	
	/**
	 * Sets the handler for failures on the threads of the server.<br>
	 *
	 * @param onError The error handler, or null to use the uncaught exception handler
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder onError(@Nullable ErrorHandler onError) {
		this.onError = onError;
		return this;
	}
	
	/**
	 * Builds a new stream server configuration with the configured values.<br>
	 *
	 * @return A new configuration instance
	 * @throws IllegalArgumentException If the backlog or the max message size is less than 1 or the client read timeout is not positive
	 */
	public @NonNull TcpServerConfig build() {
		return new TcpServerConfig(this.backlog, this.maxMessageSize, this.framing, this.clientReadTimeout, this.tcpNoDelay, this.keepAlive, this.executorStrategy, this.onError);
	}
}
