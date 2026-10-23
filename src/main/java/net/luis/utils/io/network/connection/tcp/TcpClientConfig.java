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
import net.luis.utils.io.network.connection.NetworkUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * Configuration options for connections opened by {@link TcpConnection#connectTo}.<br>
 * This record only holds transport settings, what happens on the connection is decided by the code that uses it.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TcpClientConfig config = TcpClientConfig.builder()
 *     .connectTimeout(Duration.ofSeconds(10))
 *     .readTimeout(Duration.ofSeconds(30))
 *     .framing(Framing.lines())
 *     .build();
 *
 * try (TcpConnection connection = TcpConnection.connectTo(endpoint, config)) {
 *     connection.send("Hello".getBytes());
 * }
 * }</pre>
 *
 * @see TcpClientConfigBuilder
 * @see TcpConnection
 *
 * @author Luis-St
 *
 * @param connectTimeout The maximum time to wait for the connection to be established
 * @param readTimeout The maximum time a receive waits for data, or null to wait forever
 * @param maxMessageSize The maximum length of a message in bytes
 * @param framing The framing that delimits messages on the stream
 * @param tcpNoDelay Whether to disable Nagle's algorithm (TCP_NODELAY)
 * @param keepAlive Whether to enable TCP keep-alive (SO_KEEPALIVE)
 */
public record TcpClientConfig(
	@NonNull Duration connectTimeout,
	@Nullable Duration readTimeout,
	int maxMessageSize,
	@NonNull Framing framing,
	boolean tcpNoDelay,
	boolean keepAlive
) {
	
	/**
	 * Default configuration for TCP connections.<br>
	 * <ul>
	 *     <li>{@link #connectTimeout} = {@code 30 seconds}</li>
	 *     <li>{@link #readTimeout} = {@code null} (wait forever)</li>
	 *     <li>{@link #maxMessageSize} = {@code 8192}</li>
	 *     <li>{@link #framing} = {@link Framing#lengthPrefixed()}</li>
	 *     <li>{@link #tcpNoDelay} = {@code true}</li>
	 *     <li>{@link #keepAlive} = {@code true}</li>
	 * </ul>
	 */
	public static final TcpClientConfig DEFAULT = new TcpClientConfig(Duration.ofSeconds(30), null, 8192, Framing.lengthPrefixed(), true, true);
	
	/**
	 * Constructs a new TCP client configuration.<br>
	 *
	 * @param connectTimeout The maximum time to wait for the connection to be established
	 * @param readTimeout The maximum time a receive waits for data, or null to wait forever
	 * @param maxMessageSize The maximum length of a message in bytes
	 * @param framing The framing that delimits messages on the stream
	 * @param tcpNoDelay Whether to disable Nagle's algorithm
	 * @param keepAlive Whether to enable TCP keep-alive
	 * @throws NullPointerException If the connect timeout or the framing is null
	 * @throws IllegalArgumentException If a timeout is not positive or the max message size is less than 1
	 */
	public TcpClientConfig {
		Objects.requireNonNull(connectTimeout, "Connect timeout must not be null");
		Objects.requireNonNull(framing, "Framing must not be null");
		NetworkUtils.validateTimeout(connectTimeout, "Connect timeout");
		NetworkUtils.validateTimeout(readTimeout, "Read timeout");
		if (maxMessageSize < 1) {
			throw new IllegalArgumentException("Max message size must be at least 1: " + maxMessageSize);
		}
	}
	
	/**
	 * Creates a new builder for constructing a TCP client configuration.<br>
	 * @return A new builder with default values
	 */
	public static @NonNull TcpClientConfigBuilder builder() {
		return new TcpClientConfigBuilder();
	}
	
	/**
	 * Returns a copy of this configuration that uses the given framing.<br>
	 *
	 * @param framing The framing of the copy
	 * @return The copy with the given framing
	 * @throws NullPointerException If the framing is null
	 */
	public @NonNull TcpClientConfig withFraming(@NonNull Framing framing) {
		return new TcpClientConfig(this.connectTimeout, this.readTimeout, this.maxMessageSize, framing, this.tcpNoDelay, this.keepAlive);
	}
	
	/**
	 * Returns a copy of this configuration that uses the given maximum message size.<br>
	 *
	 * @param maxMessageSize The maximum message size of the copy
	 * @return The copy with the given maximum message size
	 * @throws IllegalArgumentException If the max message size is less than 1
	 */
	public @NonNull TcpClientConfig withMaxMessageSize(int maxMessageSize) {
		return new TcpClientConfig(this.connectTimeout, this.readTimeout, maxMessageSize, this.framing, this.tcpNoDelay, this.keepAlive);
	}
}
