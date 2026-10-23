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

import net.luis.utils.io.network.connection.NetworkUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

/**
 * Configuration options for UDP peers.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * UdpPeerConfig config = UdpPeerConfig.builder()
 *     .receiveTimeout(Duration.ofSeconds(5))
 *     .maxDatagramSize(1024)
 *     .broadcast(true)
 *     .build();
 *
 * try (UdpPeer peer = UdpPeer.ephemeral(config)) {
 *     peer.send(target, "Hello".getBytes());
 * }
 * }</pre>
 *
 * @see UdpPeerConfigBuilder
 * @see UdpPeer
 *
 * @author Luis-St
 *
 * @param receiveTimeout The maximum time a receive waits for a datagram, or null to wait forever
 * @param maxDatagramSize The maximum size of a datagram in bytes, larger received datagrams are truncated
 * @param broadcast Whether sending to broadcast addresses is allowed (SO_BROADCAST)
 * @param reuseAddress Whether the local address may be bound by several sockets (SO_REUSEADDR)
 */
public record UdpPeerConfig(
	@Nullable Duration receiveTimeout,
	int maxDatagramSize,
	boolean broadcast,
	boolean reuseAddress
) {
	
	/**
	 * Default configuration for UDP peers.<br>
	 * <ul>
	 *     <li>{@link #receiveTimeout} = {@code null} (wait forever)</li>
	 *     <li>{@link #maxDatagramSize} = {@code 65507} (max UDP payload over IPv4)</li>
	 *     <li>{@link #broadcast} = {@code false}</li>
	 *     <li>{@link #reuseAddress} = {@code false}</li>
	 * </ul>
	 */
	public static final UdpPeerConfig DEFAULT = new UdpPeerConfig(null, 65507, false, false);
	
	/**
	 * Constructs a new UDP peer configuration.<br>
	 *
	 * @param receiveTimeout The maximum time a receive waits for a datagram, or null to wait forever
	 * @param maxDatagramSize The maximum size of a datagram in bytes
	 * @param broadcast Whether sending to broadcast addresses is allowed
	 * @param reuseAddress Whether the local address may be bound by several sockets
	 * @throws IllegalArgumentException If the receive timeout is not positive or the max datagram size is less than 1
	 */
	public UdpPeerConfig {
		NetworkUtils.validateTimeout(receiveTimeout, "Receive timeout");
		if (maxDatagramSize < 1) {
			throw new IllegalArgumentException("Max datagram size must be at least 1: " + maxDatagramSize);
		}
	}
	
	/**
	 * Creates a new builder for constructing a UDP peer configuration.<br>
	 * @return A new builder with default values
	 */
	public static @NonNull UdpPeerConfigBuilder builder() {
		return new UdpPeerConfigBuilder();
	}
}
