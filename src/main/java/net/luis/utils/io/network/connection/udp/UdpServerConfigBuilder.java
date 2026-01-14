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

import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import net.luis.utils.io.network.connection.event.MessageEventHandler;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Builder class for constructing UDP server configuration.<br>
 * Provides a fluent API for setting individual configuration options.<br>
 * <p>
 *     All options default to values matching {@link UdpServerConfig#DEFAULT}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * UdpServerConfig config = UdpServerConfig.builder()
 *     .bufferSize(1024)
 *     .executorStrategy(ClientExecutorStrategy.fixedPool(10))
 *     .onMessage((server, datagram, data) -> {
 *         System.out.println("Received from " + datagram.endpoint());
 *     })
 *     .build();
 * }</pre>
 *
 * @see UdpServerConfig
 *
 * @author Luis-St
 */
public final class UdpServerConfigBuilder {
	
	private int bufferSize = 65535;
	private boolean broadcast;
	private boolean reuseAddress;
	private @NonNull ClientExecutorStrategy executorStrategy = ClientExecutorStrategy.virtualThreads();
	private @Nullable MessageEventHandler<UdpServer, UdpDatagram> onMessage;
	private @Nullable ErrorEventHandler onError;
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	UdpServerConfigBuilder() {}
	
	/**
	 * Sets the size of the receive buffer in bytes.<br>
	 *
	 * @param bufferSize The buffer size (must be at least 1)
	 * @return This builder for method chaining
	 */
	public @NonNull UdpServerConfigBuilder bufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
		return this;
	}
	
	/**
	 * Sets whether to allow receiving broadcast packets.<br>
	 *
	 * @param broadcast True to enable broadcast
	 * @return This builder for method chaining
	 */
	public @NonNull UdpServerConfigBuilder broadcast(boolean broadcast) {
		this.broadcast = broadcast;
		return this;
	}
	
	/**
	 * Sets whether to allow address reuse (SO_REUSEADDR).<br>
	 *
	 * @param reuseAddress True to enable address reuse
	 * @return This builder for method chaining
	 */
	public @NonNull UdpServerConfigBuilder reuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
		return this;
	}
	
	/**
	 * Sets the executor strategy for handling concurrent datagram processing.<br>
	 *
	 * @param executorStrategy The executor strategy
	 * @return This builder for method chaining
	 */
	public @NonNull UdpServerConfigBuilder executorStrategy(@NonNull ClientExecutorStrategy executorStrategy) {
		this.executorStrategy = executorStrategy;
		return this;
	}
	
	/**
	 * Sets the message event handler called when a datagram is received.<br>
	 *
	 * @param onMessage The message handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull UdpServerConfigBuilder onMessage(@Nullable MessageEventHandler<UdpServer, UdpDatagram> onMessage) {
		this.onMessage = onMessage;
		return this;
	}
	
	/**
	 * Sets the error event handler.<br>
	 *
	 * @param onError The error handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull UdpServerConfigBuilder onError(@Nullable ErrorEventHandler onError) {
		this.onError = onError;
		return this;
	}
	
	/**
	 * Builds a new UDP server configuration with the configured values.<br>
	 * @return A new configuration instance
	 */
	public @NonNull UdpServerConfig build() {
		return new UdpServerConfig(this.bufferSize, this.broadcast, this.reuseAddress, this.executorStrategy, this.onMessage, this.onError);
	}
}
