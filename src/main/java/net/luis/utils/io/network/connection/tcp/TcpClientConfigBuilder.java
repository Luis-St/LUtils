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

/**
 * Builder class for constructing TCP client configuration.<br>
 * Provides a fluent API for setting individual configuration options.<br>
 * <p>
 *     All options default to values matching {@link TcpClientConfig#DEFAULT}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TcpClientConfig config = TcpClientConfig.builder()
 *     .connectTimeout(Duration.ofSeconds(10))
 *     .readTimeout(Duration.ofSeconds(30))
 *     .tcpNoDelay(true)
 *     .onConnect(event -> System.out.println("Connected to " + event.remoteEndpoint()))
 *     .onDisconnect(event -> System.out.println("Disconnected"))
 *     .build();
 * }</pre>
 *
 * @see TcpClientConfig
 *
 * @author Luis-St
 */
public final class TcpClientConfigBuilder {
	
	private @NonNull Duration connectTimeout = Duration.ofSeconds(30);
	private @NonNull Duration readTimeout = Duration.ZERO;
	private @NonNull Duration writeTimeout = Duration.ZERO;
	private int bufferSize = 8192;
	private boolean tcpNoDelay = true;
	private boolean keepAlive = true;
	private @Nullable ConnectionEventHandler onConnect;
	private @Nullable ConnectionEventHandler onDisconnect;
	private @Nullable ErrorEventHandler onError;
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	TcpClientConfigBuilder() {}
	
	/**
	 * Sets the maximum time to wait for connection establishment.<br>
	 *
	 * @param connectTimeout The connect timeout
	 * @return This builder for method chaining
	 */
	public @NonNull TcpClientConfigBuilder connectTimeout(@NonNull Duration connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}
	
	/**
	 * Sets the maximum time to wait for read operations.<br>
	 * Use {@link Duration#ZERO} for infinite timeout.<br>
	 *
	 * @param readTimeout The read timeout
	 * @return This builder for method chaining
	 */
	public @NonNull TcpClientConfigBuilder readTimeout(@NonNull Duration readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}
	
	/**
	 * Sets the maximum time to wait for write operations.<br>
	 * Use {@link Duration#ZERO} for infinite timeout.<br>
	 *
	 * @param writeTimeout The write timeout
	 * @return This builder for method chaining
	 */
	public @NonNull TcpClientConfigBuilder writeTimeout(@NonNull Duration writeTimeout) {
		this.writeTimeout = writeTimeout;
		return this;
	}
	
	/**
	 * Sets the size of the read/write buffers in bytes.<br>
	 *
	 * @param bufferSize The buffer size (must be at least 1)
	 * @return This builder for method chaining
	 */
	public @NonNull TcpClientConfigBuilder bufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
		return this;
	}
	
	/**
	 * Sets whether to disable Nagle's algorithm (TCP_NODELAY).<br>
	 * When enabled, small packets are sent immediately without waiting.<br>
	 *
	 * @param tcpNoDelay True to disable Nagle's algorithm
	 * @return This builder for method chaining
	 */
	public @NonNull TcpClientConfigBuilder tcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
		return this;
	}
	
	/**
	 * Sets whether to enable TCP keep-alive (SO_KEEPALIVE).<br>
	 *
	 * @param keepAlive True to enable keep-alive
	 * @return This builder for method chaining
	 */
	public @NonNull TcpClientConfigBuilder keepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}
	
	/**
	 * Sets the handler called when connection is established.<br>
	 *
	 * @param onConnect The connection handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull TcpClientConfigBuilder onConnect(@Nullable ConnectionEventHandler onConnect) {
		this.onConnect = onConnect;
		return this;
	}
	
	/**
	 * Sets the handler called when connection is closed.<br>
	 *
	 * @param onDisconnect The disconnection handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull TcpClientConfigBuilder onDisconnect(@Nullable ConnectionEventHandler onDisconnect) {
		this.onDisconnect = onDisconnect;
		return this;
	}
	
	/**
	 * Sets the error event handler.<br>
	 *
	 * @param onError The error handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull TcpClientConfigBuilder onError(@Nullable ErrorEventHandler onError) {
		this.onError = onError;
		return this;
	}
	
	/**
	 * Builds a new TCP client configuration with the configured values.<br>
	 * @return A new configuration instance
	 */
	public @NonNull TcpClientConfig build() {
		return new TcpClientConfig(this.connectTimeout, this.readTimeout, this.writeTimeout, this.bufferSize, this.tcpNoDelay, this.keepAlive, this.onConnect, this.onDisconnect, this.onError);
	}
}
