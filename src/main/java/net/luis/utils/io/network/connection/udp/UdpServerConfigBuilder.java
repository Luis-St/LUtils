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

package net.luis.utils.io.network.connection.udp;

import net.luis.utils.io.network.connection.ErrorHandler;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * Builder class for constructing a UDP server configuration.<br>
 * Provides a fluent API for the peer and the receive loop options of a UDP server.<br>
 * <p>
 *     All options default to the values of {@link UdpServerConfig#DEFAULT}.
 * </p>
 *
 * @see UdpServerConfig
 *
 * @author Luis-St
 */
public final class UdpServerConfigBuilder {
	
	/**
	 * The builder of the peer settings.<br>
	 */
	private final UdpPeerConfigBuilder peer = UdpPeerConfig.builder();
	/**
	 * The strategy that creates the executor the datagrams are handled on.<br>
	 */
	private ClientExecutorStrategy executorStrategy = UdpServerConfig.DEFAULT.executorStrategy();
	/**
	 * The handler for failures on the threads of the server.<br>
	 */
	private @Nullable ErrorHandler onError = UdpServerConfig.DEFAULT.onError();
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	UdpServerConfigBuilder() {}
	
	/**
	 * Sets the maximum time a receive of the server waits for a datagram.<br>
	 * A timeout does not stop the server, the receive loop simply waits again.<br>
	 *
	 * @param receiveTimeout The receive timeout, which must be positive
	 * @return This builder for method chaining
	 * @throws NullPointerException If the receive timeout is null
	 */
	public @NonNull UdpServerConfigBuilder receiveTimeout(@NonNull Duration receiveTimeout) {
		this.peer.receiveTimeout(receiveTimeout);
		return this;
	}
	
	/**
	 * Removes the receive timeout, so that a receive waits forever.<br>
	 * @return This builder for method chaining
	 */
	public @NonNull UdpServerConfigBuilder noReceiveTimeout() {
		this.peer.noReceiveTimeout();
		return this;
	}
	
	/**
	 * Sets the maximum size of a datagram in bytes.<br>
	 *
	 * @param maxDatagramSize The max datagram size, which must be at least 1
	 * @return This builder for method chaining
	 */
	public @NonNull UdpServerConfigBuilder maxDatagramSize(int maxDatagramSize) {
		this.peer.maxDatagramSize(maxDatagramSize);
		return this;
	}
	
	/**
	 * Sets whether sending to broadcast addresses is allowed (SO_BROADCAST).<br>
	 *
	 * @param broadcast True to allow broadcasts
	 * @return This builder for method chaining
	 */
	public @NonNull UdpServerConfigBuilder broadcast(boolean broadcast) {
		this.peer.broadcast(broadcast);
		return this;
	}
	
	/**
	 * Sets whether the local address may be bound by several sockets (SO_REUSEADDR).<br>
	 *
	 * @param reuseAddress True to allow address reuse
	 * @return This builder for method chaining
	 */
	public @NonNull UdpServerConfigBuilder reuseAddress(boolean reuseAddress) {
		this.peer.reuseAddress(reuseAddress);
		return this;
	}
	
	/**
	 * Sets the strategy that creates the executor the datagrams are handled on.<br>
	 *
	 * @param executorStrategy The executor strategy
	 * @return This builder for method chaining
	 * @throws NullPointerException If the executor strategy is null
	 */
	public @NonNull UdpServerConfigBuilder executorStrategy(@NonNull ClientExecutorStrategy executorStrategy) {
		this.executorStrategy = Objects.requireNonNull(executorStrategy, "Executor strategy must not be null");
		return this;
	}
	
	/**
	 * Sets the handler for failures on the threads of the server.<br>
	 *
	 * @param onError The error handler, or null to use the uncaught exception handler
	 * @return This builder for method chaining
	 */
	public @NonNull UdpServerConfigBuilder onError(@Nullable ErrorHandler onError) {
		this.onError = onError;
		return this;
	}
	
	/**
	 * Builds a new UDP server configuration with the configured values.<br>
	 *
	 * @return A new configuration instance
	 * @throws IllegalArgumentException If the receive timeout is not positive or the max datagram size is less than 1
	 */
	public @NonNull UdpServerConfig build() {
		return new UdpServerConfig(this.peer.build(), this.executorStrategy, this.onError);
	}
}
