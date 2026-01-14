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

import java.util.Objects;

/**
 * Configuration options for UDP servers.<br>
 * This record provides settings for UDP server behavior such as buffer sizes, executor strategy, and event handlers.
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * UdpServerConfig config = UdpServerConfig.builder()
 *     .executorStrategy(ClientExecutorStrategy.virtualThreads())
 *     .onMessage((server, datagram, data) -> {
 *         server.send(datagram.endpoint(), "Pong".getBytes());
 *     })
 *     .build();
 *
 * try (UdpServer server = new UdpServer(bindEndpoint, config)) {
 *     server.start();
 * }
 * }</pre>
 *
 * @see UdpServerConfigBuilder
 * @see UdpServer
 *
 * @author Luis-St
 *
 * @param bufferSize Size of the receive buffer in bytes
 * @param broadcast Whether to allow receiving broadcast packets
 * @param reuseAddress Whether to allow address reuse (SO_REUSEADDR)
 * @param executorStrategy How to handle concurrent datagram processing
 * @param onMessage Handler called when a datagram is received
 * @param onError Handler called when an error occurs
 */
public record UdpServerConfig(
	int bufferSize,
	boolean broadcast,
	boolean reuseAddress,
	@NonNull ClientExecutorStrategy executorStrategy,
	@Nullable MessageEventHandler<UdpServer, UdpDatagram> onMessage,
	@Nullable ErrorEventHandler onError
) {

	/**
	 * Default configuration for UDP servers.<br>
	 * <ul>
	 *     <li>{@link #bufferSize} = {@code 65535} (max UDP payload)</li>
	 *     <li>{@link #broadcast} = {@code false}</li>
	 *     <li>{@link #reuseAddress} = {@code false}</li>
	 *     <li>{@link #executorStrategy} = {@code virtualThreads()}</li>
	 *     <li>{@link #onMessage} = {@code null}</li>
	 *     <li>{@link #onError} = {@code null}</li>
	 * </ul>
	 */
	public static final UdpServerConfig DEFAULT = new UdpServerConfig(
		65535,
		false,
		false,
		ClientExecutorStrategy.virtualThreads(),
		null,
		null
	);

	/**
	 * Constructs a new UDP server configuration.<br>
	 *
	 * @param bufferSize Size of the receive buffer in bytes
	 * @param broadcast Whether to allow broadcast packets
	 * @param reuseAddress Whether to allow address reuse
	 * @param executorStrategy How to handle concurrent datagram processing
	 * @param onMessage Handler called when a datagram is received
	 * @param onError Handler called when an error occurs
	 * @throws NullPointerException If executorStrategy is null
	 * @throws IllegalArgumentException If bufferSize is less than 1
	 */
	public UdpServerConfig {
		Objects.requireNonNull(executorStrategy, "Executor strategy must not be null");
		if (bufferSize < 1) {
			throw new IllegalArgumentException("Buffer size must be at least 1: " + bufferSize);
		}
	}

	/**
	 * Creates a new builder for constructing UDP server configuration.<br>
	 *
	 * @return A new builder with default values
	 */
	public static @NonNull UdpServerConfigBuilder builder() {
		return new UdpServerConfigBuilder();
	}
}
