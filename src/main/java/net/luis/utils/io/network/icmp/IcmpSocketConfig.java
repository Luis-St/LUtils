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

package net.luis.utils.io.network.icmp;

import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * Configuration options for ICMP sockets.<br>
 * This record provides settings for the socket type, timeouts, buffer size, time to live and error handling.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IcmpSocketConfig config = IcmpSocketConfig.builder()
 *     .socketType(IcmpSocketType.RAW)
 *     .receiveTimeout(Duration.ofSeconds(1))
 *     .timeToLive(32)
 *     .build();
 *
 * try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4, config)) {
 *     // ...
 * }
 * }</pre>
 *
 * @see IcmpSocketConfigBuilder
 * @see IcmpSocket
 *
 * @author Luis-St
 *
 * @param socketType The kind of operating system socket to open
 * @param receiveTimeout Maximum time to wait for receive operations (Duration.ZERO for infinite)
 * @param bufferSize Size of the receive buffer in bytes
 * @param timeToLive Time to live or hop limit of outgoing packets (0 for the system default)
 * @param onError Handler called when an error occurs
 */
public record IcmpSocketConfig(
	@NonNull IcmpSocketType socketType,
	@NonNull Duration receiveTimeout,
	int bufferSize,
	int timeToLive,
	@Nullable ErrorEventHandler onError
) {
	
	/**
	 * Default configuration for ICMP sockets.<br>
	 * <ul>
	 *     <li>{@link #socketType} = {@link IcmpSocketType#DATAGRAM}</li>
	 *     <li>{@link #receiveTimeout} = {@code Duration.ZERO} (infinite)</li>
	 *     <li>{@link #bufferSize} = {@code 65535} (max IP packet size)</li>
	 *     <li>{@link #timeToLive} = {@code 0} (system default)</li>
	 *     <li>{@link #onError} = {@code null}</li>
	 * </ul>
	 */
	public static final IcmpSocketConfig DEFAULT = new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ZERO, 65535, 0, null);
	
	/**
	 * Constructs a new ICMP socket configuration.<br>
	 *
	 * @param socketType The kind of operating system socket to open
	 * @param receiveTimeout Maximum time to wait for receive operations
	 * @param bufferSize Size of the receive buffer in bytes
	 * @param timeToLive Time to live or hop limit of outgoing packets
	 * @param onError Handler called when an error occurs
	 * @throws NullPointerException If the socket type or receive timeout is null
	 * @throws IllegalArgumentException If the receive timeout is negative, the buffer size is less than the ICMP header size or the time to live is not between 0 and 255
	 */
	public IcmpSocketConfig {
		Objects.requireNonNull(socketType, "Socket type must not be null");
		Objects.requireNonNull(receiveTimeout, "Receive timeout must not be null");
		
		if (receiveTimeout.isNegative()) {
			throw new IllegalArgumentException("Receive timeout must not be negative: " + receiveTimeout);
		}
		if (bufferSize < IcmpPacket.HEADER_SIZE) {
			throw new IllegalArgumentException("Buffer size must be at least " + IcmpPacket.HEADER_SIZE + ": " + bufferSize);
		}
		if (timeToLive < 0 || timeToLive > 255) {
			throw new IllegalArgumentException("Time to live must be between 0 and 255: " + timeToLive);
		}
	}
	
	/**
	 * Creates a new builder for constructing ICMP socket configuration.<br>
	 * @return A new builder with default values
	 */
	public static @NonNull IcmpSocketConfigBuilder builder() {
		return new IcmpSocketConfigBuilder();
	}
}
