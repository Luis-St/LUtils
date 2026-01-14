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

import net.luis.utils.io.network.connection.event.*;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

/**
 * Builder class for constructing TCP server configuration.<br>
 * Provides a fluent API for setting individual configuration options.<br>
 * <p>
 *     All options default to values matching {@link TcpServerConfig#DEFAULT}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TcpServerConfig config = TcpServerConfig.builder()
 *     .backlog(100)
 *     .clientBufferSize(4096)
 *     .executorStrategy(ClientExecutorStrategy.fixedPool(10))
 *     .onMessage((server, conn, data) -> {
 *         System.out.println("Received: " + new String(data));
 *         conn.send("OK".getBytes());
 *     })
 *     .build();
 * }</pre>
 *
 * @see TcpServerConfig
 *
 * @author Luis-St
 */
public final class TcpServerConfigBuilder {
	
	private int backlog = 50;
	private int clientBufferSize = 8192;
	private @NonNull Duration clientReadTimeout = Duration.ZERO;
	private boolean tcpNoDelay = true;
	private boolean keepAlive = true;
	private @NonNull ClientExecutorStrategy executorStrategy = ClientExecutorStrategy.virtualThreads();
	private @Nullable ConnectionEventHandler onClientConnect;
	private @Nullable ConnectionEventHandler onClientDisconnect;
	private @Nullable MessageEventHandler<TcpServer, TcpConnection> onMessage;
	private @Nullable ErrorEventHandler onError;
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	TcpServerConfigBuilder() {}
	
	/**
	 * Sets the maximum number of pending connections in the queue.<br>
	 *
	 * @param backlog The backlog size (must be at least 1)
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder backlog(int backlog) {
		this.backlog = backlog;
		return this;
	}
	
	/**
	 * Sets the buffer size for each client connection in bytes.<br>
	 *
	 * @param clientBufferSize The buffer size (must be at least 1)
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder clientBufferSize(int clientBufferSize) {
		this.clientBufferSize = clientBufferSize;
		return this;
	}
	
	/**
	 * Sets the read timeout for client connections.<br>
	 * Use {@link Duration#ZERO} for infinite timeout.<br>
	 *
	 * @param clientReadTimeout The read timeout
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder clientReadTimeout(@NonNull Duration clientReadTimeout) {
		this.clientReadTimeout = clientReadTimeout;
		return this;
	}
	
	/**
	 * Sets whether to disable Nagle's algorithm for client connections.<br>
	 *
	 * @param tcpNoDelay True to disable Nagle's algorithm
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder tcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
		return this;
	}
	
	/**
	 * Sets whether to enable TCP keep-alive for client connections.<br>
	 *
	 * @param keepAlive True to enable keep-alive
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder keepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}
	
	/**
	 * Sets the executor strategy for handling concurrent client connections.<br>
	 *
	 * @param executorStrategy The executor strategy
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder executorStrategy(@NonNull ClientExecutorStrategy executorStrategy) {
		this.executorStrategy = executorStrategy;
		return this;
	}
	
	/**
	 * Sets the handler called when a client connects.<br>
	 *
	 * @param onClientConnect The connection handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder onClientConnect(@Nullable ConnectionEventHandler onClientConnect) {
		this.onClientConnect = onClientConnect;
		return this;
	}
	
	/**
	 * Sets the handler called when a client disconnects.<br>
	 *
	 * @param onClientDisconnect The disconnection handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder onClientDisconnect(@Nullable ConnectionEventHandler onClientDisconnect) {
		this.onClientDisconnect = onClientDisconnect;
		return this;
	}
	
	/**
	 * Sets the handler called when a message is received from a client.<br>
	 *
	 * @param onMessage The message handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder onMessage(@Nullable MessageEventHandler<TcpServer, TcpConnection> onMessage) {
		this.onMessage = onMessage;
		return this;
	}
	
	/**
	 * Sets the error event handler.<br>
	 *
	 * @param onError The error handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull TcpServerConfigBuilder onError(@Nullable ErrorEventHandler onError) {
		this.onError = onError;
		return this;
	}
	
	/**
	 * Builds a new TCP server configuration with the configured values.<br>
	 * @return A new configuration instance
	 */
	public @NonNull TcpServerConfig build() {
		return new TcpServerConfig(this.backlog, this.clientBufferSize, this.clientReadTimeout, this.tcpNoDelay, this.keepAlive, this.executorStrategy, this.onClientConnect, this.onClientDisconnect, this.onMessage, this.onError);
	}
}
