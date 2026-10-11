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
 * Builder class for constructing ICMP socket configuration.<br>
 * Provides a fluent API for setting individual configuration options.<br>
 * <p>
 *     All options default to values matching {@link IcmpSocketConfig#DEFAULT}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IcmpSocketConfig config = IcmpSocketConfig.builder()
 *     .socketType(IcmpSocketType.DATAGRAM)
 *     .receiveTimeout(Duration.ofSeconds(1))
 *     .timeToLive(64)
 *     .onError((connection, type, msg, cause) -> System.err.println(msg))
 *     .build();
 * }</pre>
 *
 * @see IcmpSocketConfig
 *
 * @author Luis-St
 */
public final class IcmpSocketConfigBuilder {
	
	/**
	 * The kind of operating system socket to open.<br>
	 */
	private IcmpSocketType socketType = IcmpSocketType.DATAGRAM;
	/**
	 * The maximum time to wait for receive operations.<br>
	 */
	private Duration receiveTimeout = Duration.ZERO;
	/**
	 * The size of the receive buffer in bytes.<br>
	 */
	private int bufferSize = 65535;
	/**
	 * The time to live or hop limit of outgoing packets.<br>
	 */
	private int timeToLive;
	/**
	 * The handler called when an error occurs.<br>
	 */
	private @Nullable ErrorEventHandler onError;
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	IcmpSocketConfigBuilder() {}
	
	/**
	 * Sets the kind of operating system socket to open.<br>
	 *
	 * @param socketType The socket type
	 * @return This builder for method chaining
	 * @throws NullPointerException If the socket type is null
	 */
	public @NonNull IcmpSocketConfigBuilder socketType(@NonNull IcmpSocketType socketType) {
		this.socketType = Objects.requireNonNull(socketType, "Socket type must not be null");
		return this;
	}
	
	/**
	 * Sets the maximum time to wait for receive operations.<br>
	 * Use {@link Duration#ZERO} for infinite timeout.<br>
	 *
	 * @param receiveTimeout The receive timeout
	 * @return This builder for method chaining
	 * @throws NullPointerException If the receive timeout is null
	 */
	public @NonNull IcmpSocketConfigBuilder receiveTimeout(@NonNull Duration receiveTimeout) {
		this.receiveTimeout = Objects.requireNonNull(receiveTimeout, "Receive timeout must not be null");
		return this;
	}
	
	/**
	 * Sets the size of the receive buffer in bytes.<br>
	 *
	 * @param bufferSize The buffer size (must be at least the ICMP header size)
	 * @return This builder for method chaining
	 */
	public @NonNull IcmpSocketConfigBuilder bufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
		return this;
	}
	
	/**
	 * Sets the time to live or hop limit of outgoing packets.<br>
	 * Use 0 to keep the system default.<br>
	 *
	 * @param timeToLive The time to live (must be between 0 and 255)
	 * @return This builder for method chaining
	 */
	public @NonNull IcmpSocketConfigBuilder timeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
		return this;
	}
	
	/**
	 * Sets the error event handler.<br>
	 *
	 * @param onError The error handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull IcmpSocketConfigBuilder onError(@Nullable ErrorEventHandler onError) {
		this.onError = onError;
		return this;
	}
	
	/**
	 * Builds a new ICMP socket configuration with the configured values.<br>
	 * @return A new configuration instance
	 */
	public @NonNull IcmpSocketConfig build() {
		return new IcmpSocketConfig(this.socketType, this.receiveTimeout, this.bufferSize, this.timeToLive, this.onError);
	}
}
