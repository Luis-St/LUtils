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

import net.luis.utils.io.network.connection.Framing;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * Builder class for constructing a TCP client configuration.<br>
 * Provides a fluent API for setting individual configuration options.<br>
 * <p>
 *     All options default to the values of {@link TcpClientConfig#DEFAULT}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TcpClientConfig config = TcpClientConfig.builder()
 *     .connectTimeout(Duration.ofSeconds(10))
 *     .maxMessageSize(65536)
 *     .build();
 * }</pre>
 *
 * @see TcpClientConfig
 *
 * @author Luis-St
 */
public final class TcpClientConfigBuilder {
	
	/**
	 * The maximum time to wait for the connection to be established.<br>
	 */
	private Duration connectTimeout = TcpClientConfig.DEFAULT.connectTimeout();
	/**
	 * The maximum time a receive waits for data, or null to wait forever.<br>
	 */
	private @Nullable Duration readTimeout = TcpClientConfig.DEFAULT.readTimeout();
	/**
	 * The maximum length of a message in bytes.<br>
	 */
	private int maxMessageSize = TcpClientConfig.DEFAULT.maxMessageSize();
	/**
	 * The framing that delimits messages on the stream.<br>
	 */
	private Framing framing = TcpClientConfig.DEFAULT.framing();
	/**
	 * Whether to disable Nagle's algorithm.<br>
	 */
	private boolean tcpNoDelay = TcpClientConfig.DEFAULT.tcpNoDelay();
	/**
	 * Whether to enable TCP keep-alive.<br>
	 */
	private boolean keepAlive = TcpClientConfig.DEFAULT.keepAlive();
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	TcpClientConfigBuilder() {}
	
	/**
	 * Sets the maximum time to wait for the connection to be established.<br>
	 *
	 * @param connectTimeout The connect timeout, which must be positive
	 * @return This builder for method chaining
	 * @throws NullPointerException If the connect timeout is null
	 */
	public @NonNull TcpClientConfigBuilder connectTimeout(@NonNull Duration connectTimeout) {
		this.connectTimeout = Objects.requireNonNull(connectTimeout, "Connect timeout must not be null");
		return this;
	}
	
	/**
	 * Sets the maximum time a receive waits for data.<br>
	 *
	 * @param readTimeout The read timeout, which must be positive
	 * @return This builder for method chaining
	 * @throws NullPointerException If the read timeout is null
	 */
	public @NonNull TcpClientConfigBuilder readTimeout(@NonNull Duration readTimeout) {
		this.readTimeout = Objects.requireNonNull(readTimeout, "Read timeout must not be null");
		return this;
	}
	
	/**
	 * Removes the read timeout, so that a receive waits forever.<br>
	 * @return This builder for method chaining
	 */
	public @NonNull TcpClientConfigBuilder noReadTimeout() {
		this.readTimeout = null;
		return this;
	}
	
	/**
	 * Sets the maximum length of a message in bytes.<br>
	 *
	 * @param maxMessageSize The max message size, which must be at least 1
	 * @return This builder for method chaining
	 */
	public @NonNull TcpClientConfigBuilder maxMessageSize(int maxMessageSize) {
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
	public @NonNull TcpClientConfigBuilder framing(@NonNull Framing framing) {
		this.framing = Objects.requireNonNull(framing, "Framing must not be null");
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
	 * Builds a new TCP client configuration with the configured values.<br>
	 *
	 * @return A new configuration instance
	 * @throws IllegalArgumentException If a timeout is not positive or the max message size is less than 1
	 */
	public @NonNull TcpClientConfig build() {
		return new TcpClientConfig(this.connectTimeout, this.readTimeout, this.maxMessageSize, this.framing, this.tcpNoDelay, this.keepAlive);
	}
}
