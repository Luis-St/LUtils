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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * Builder class for constructing a UDP peer configuration.<br>
 * Provides a fluent API for setting individual configuration options.<br>
 * <p>
 *     All options default to the values of {@link UdpPeerConfig#DEFAULT}.
 * </p>
 *
 * @see UdpPeerConfig
 *
 * @author Luis-St
 */
public final class UdpPeerConfigBuilder {
	
	/**
	 * The maximum time a receive waits for a datagram, or null to wait forever.<br>
	 */
	private @Nullable Duration receiveTimeout = UdpPeerConfig.DEFAULT.receiveTimeout();
	/**
	 * The maximum size of a datagram in bytes.<br>
	 */
	private int maxDatagramSize = UdpPeerConfig.DEFAULT.maxDatagramSize();
	/**
	 * Whether sending to broadcast addresses is allowed.<br>
	 */
	private boolean broadcast = UdpPeerConfig.DEFAULT.broadcast();
	/**
	 * Whether the local address may be bound by several sockets.<br>
	 */
	private boolean reuseAddress = UdpPeerConfig.DEFAULT.reuseAddress();
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	UdpPeerConfigBuilder() {}
	
	/**
	 * Sets the maximum time a receive waits for a datagram.<br>
	 *
	 * @param receiveTimeout The receive timeout, which must be positive
	 * @return This builder for method chaining
	 * @throws NullPointerException If the receive timeout is null
	 */
	public @NonNull UdpPeerConfigBuilder receiveTimeout(@NonNull Duration receiveTimeout) {
		this.receiveTimeout = Objects.requireNonNull(receiveTimeout, "Receive timeout must not be null");
		return this;
	}
	
	/**
	 * Removes the receive timeout, so that a receive waits forever.<br>
	 * @return This builder for method chaining
	 */
	public @NonNull UdpPeerConfigBuilder noReceiveTimeout() {
		this.receiveTimeout = null;
		return this;
	}
	
	/**
	 * Sets the maximum size of a datagram in bytes.<br>
	 * Larger outgoing datagrams are rejected, larger incoming datagrams are truncated.<br>
	 *
	 * @param maxDatagramSize The max datagram size, which must be at least 1
	 * @return This builder for method chaining
	 */
	public @NonNull UdpPeerConfigBuilder maxDatagramSize(int maxDatagramSize) {
		this.maxDatagramSize = maxDatagramSize;
		return this;
	}
	
	/**
	 * Sets whether sending to broadcast addresses is allowed (SO_BROADCAST).<br>
	 *
	 * @param broadcast True to allow broadcasts
	 * @return This builder for method chaining
	 */
	public @NonNull UdpPeerConfigBuilder broadcast(boolean broadcast) {
		this.broadcast = broadcast;
		return this;
	}
	
	/**
	 * Sets whether the local address may be bound by several sockets (SO_REUSEADDR).<br>
	 *
	 * @param reuseAddress True to allow address reuse
	 * @return This builder for method chaining
	 */
	public @NonNull UdpPeerConfigBuilder reuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
		return this;
	}
	
	/**
	 * Builds a new UDP peer configuration with the configured values.<br>
	 *
	 * @return A new configuration instance
	 * @throws IllegalArgumentException If the receive timeout is not positive or the max datagram size is less than 1
	 */
	public @NonNull UdpPeerConfig build() {
		return new UdpPeerConfig(this.receiveTimeout, this.maxDatagramSize, this.broadcast, this.reuseAddress);
	}
}
