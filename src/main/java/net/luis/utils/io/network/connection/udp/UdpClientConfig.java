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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;

/**
 * Configuration options for UDP clients.<br>
 * This record provides settings for UDP socket behavior such as timeouts, buffer sizes, and event handlers.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * UdpClientConfig config = UdpClientConfig.builder()
 *     .receiveTimeout(Duration.ofSeconds(5))
 *     .bufferSize(1024)
 *     .broadcast(true)
 *     .build();
 *
 * try (UdpClient client = new UdpClient(config)) {
 *     // ...
 * }
 * }</pre>
 *
 * @see UdpClientConfigBuilder
 * @see UdpClient
 *
 * @author Luis-St
 *
 * @param receiveTimeout Maximum time to wait for receive operations (Duration.ZERO for infinite)
 * @param bufferSize Size of the receive buffer in bytes
 * @param broadcast Whether to allow sending/receiving broadcast packets
 * @param reuseAddress Whether to allow address reuse (SO_REUSEADDR)
 * @param onError Handler called when an error occurs
 */
public record UdpClientConfig(
	@NonNull Duration receiveTimeout,
	int bufferSize,
	boolean broadcast,
	boolean reuseAddress,
	@Nullable ErrorEventHandler onError
) {

	/**
	 * Default configuration for UDP clients.<br>
	 * <ul>
	 *     <li>{@link #receiveTimeout} = {@code Duration.ZERO} (infinite)</li>
	 *     <li>{@link #bufferSize} = {@code 65535} (max UDP payload)</li>
	 *     <li>{@link #broadcast} = {@code false}</li>
	 *     <li>{@link #reuseAddress} = {@code false}</li>
	 *     <li>{@link #onError} = {@code null}</li>
	 * </ul>
	 */
	public static final UdpClientConfig DEFAULT = new UdpClientConfig(
		Duration.ZERO,
		65535,
		false,
		false,
		null
	);

	/**
	 * Constructs a new UDP client configuration.<br>
	 *
	 * @param receiveTimeout Maximum time to wait for receive operations
	 * @param bufferSize Size of the receive buffer in bytes
	 * @param broadcast Whether to allow broadcast packets
	 * @param reuseAddress Whether to allow address reuse
	 * @param onError Handler called when an error occurs
	 * @throws NullPointerException If receiveTimeout is null
	 * @throws IllegalArgumentException If bufferSize is less than 1
	 */
	public UdpClientConfig {
		Objects.requireNonNull(receiveTimeout, "Receive timeout must not be null");
		if (bufferSize < 1) {
			throw new IllegalArgumentException("Buffer size must be at least 1: " + bufferSize);
		}
	}

	/**
	 * Creates a new builder for constructing UDP client configuration.<br>
	 * @return A new builder with default values
	 */
	public static @NonNull UdpClientConfigBuilder builder() {
		return new UdpClientConfigBuilder();
	}
}
