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

import java.util.Objects;

/**
 * Configuration options for UDP servers.<br>
 * <p>
 *     This record combines the settings of the peer the server receives on with the settings of the receive loop.<br>
 *     The only callback is the error handler, which receives the failures that occur on the threads of the server.<br>
 *     What happens with a datagram is decided by the {@link DatagramHandler} the server is created with.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * UdpServerConfig config = UdpServerConfig.builder()
 *     .maxDatagramSize(1024)
 *     .executorStrategy(ClientExecutorStrategy.fixedPool(4))
 *     .build();
 * }</pre>
 *
 * @see UdpServerConfigBuilder
 * @see UdpServer
 *
 * @author Luis-St
 *
 * @param peer The settings of the peer the server receives on
 * @param executorStrategy The strategy that creates the executor the datagrams are handled on
 * @param onError The handler for failures on the threads of the server, or null to use the uncaught exception handler
 */
public record UdpServerConfig(
	@NonNull UdpPeerConfig peer,
	@NonNull ClientExecutorStrategy executorStrategy,
	@Nullable ErrorHandler onError
) {
	
	/**
	 * Default configuration for UDP servers.<br>
	 * <ul>
	 *     <li>{@link #peer} = {@link UdpPeerConfig#DEFAULT}</li>
	 *     <li>{@link #executorStrategy} = {@link ClientExecutorStrategy#virtualThreads()}</li>
	 *     <li>{@link #onError} = {@code null} (uncaught exception handler)</li>
	 * </ul>
	 */
	public static final UdpServerConfig DEFAULT = new UdpServerConfig(UdpPeerConfig.DEFAULT, ClientExecutorStrategy.virtualThreads(), null);
	
	/**
	 * Constructs a new UDP server configuration.<br>
	 *
	 * @param peer The settings of the peer the server receives on
	 * @param executorStrategy The strategy that creates the executor the datagrams are handled on
	 * @param onError The handler for failures on the threads of the server, or null to use the uncaught exception handler
	 * @throws NullPointerException If the peer settings or the executor strategy is null
	 */
	public UdpServerConfig {
		Objects.requireNonNull(peer, "Peer config must not be null");
		Objects.requireNonNull(executorStrategy, "Executor strategy must not be null");
	}
	
	/**
	 * Creates a new builder for constructing a UDP server configuration.<br>
	 * @return A new builder with default values
	 */
	public static @NonNull UdpServerConfigBuilder builder() {
		return new UdpServerConfigBuilder();
	}
}
