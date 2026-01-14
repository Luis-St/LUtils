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

/**
 * Builder class for constructing UDP client configuration.<br>
 * Provides a fluent API for setting individual configuration options.<br>
 * <p>
 *     All options default to values matching {@link UdpClientConfig#DEFAULT}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * UdpClientConfig config = UdpClientConfig.builder()
 *     .receiveTimeout(Duration.ofSeconds(5))
 *     .bufferSize(1024)
 *     .broadcast(true)
 *     .onError((type, msg, cause) -> System.err.println(msg))
 *     .build();
 * }</pre>
 *
 * @see UdpClientConfig
 *
 * @author Luis-St
 */
public final class UdpClientConfigBuilder {
	
	private @NonNull Duration receiveTimeout = Duration.ZERO;
	private int bufferSize = 65535;
	private boolean broadcast;
	private boolean reuseAddress;
	private @Nullable ErrorEventHandler onError;
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	UdpClientConfigBuilder() {}
	
	/**
	 * Sets the maximum time to wait for receive operations.<br>
	 * Use {@link Duration#ZERO} for infinite timeout.<br>
	 *
	 * @param receiveTimeout The receive timeout
	 * @return This builder for method chaining
	 */
	public @NonNull UdpClientConfigBuilder receiveTimeout(@NonNull Duration receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
		return this;
	}
	
	/**
	 * Sets the size of the receive buffer in bytes.<br>
	 *
	 * @param bufferSize The buffer size (must be at least 1)
	 * @return This builder for method chaining
	 */
	public @NonNull UdpClientConfigBuilder bufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
		return this;
	}
	
	/**
	 * Sets whether to allow sending/receiving broadcast packets.<br>
	 *
	 * @param broadcast True to enable broadcast
	 * @return This builder for method chaining
	 */
	public @NonNull UdpClientConfigBuilder broadcast(boolean broadcast) {
		this.broadcast = broadcast;
		return this;
	}
	
	/**
	 * Sets whether to allow address reuse (SO_REUSEADDR).<br>
	 *
	 * @param reuseAddress True to enable address reuse
	 * @return This builder for method chaining
	 */
	public @NonNull UdpClientConfigBuilder reuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
		return this;
	}
	
	/**
	 * Sets the error event handler.<br>
	 *
	 * @param onError The error handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull UdpClientConfigBuilder onError(@Nullable ErrorEventHandler onError) {
		this.onError = onError;
		return this;
	}
	
	/**
	 * Builds a new UDP client configuration with the configured values.<br>
	 * @return A new configuration instance
	 */
	public @NonNull UdpClientConfig build() {
		return new UdpClientConfig(this.receiveTimeout, this.bufferSize, this.broadcast, this.reuseAddress, this.onError);
	}
}
