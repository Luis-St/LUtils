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

package net.luis.utils.io.network.connection.event;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.Connection;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

/**
 * Event context for connection termination.<br>
 * <p>
 *     The {@link #connection} is only available where the connection is already wrapped in a {@link Connection}
 *     at the time the event fires (e.g. server-side {@code onClientDisconnect}). Client-side {@code onDisconnect}
 *     handlers do not have a {@link Connection} instance to offer and receive {@code null} instead. Where present,
 *     the connection is typically already inactive by the time the handler runs.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * DisconnectEventHandler handler = event -> {
 *     System.out.println("Disconnected from " + event.remoteEndpoint() + " at " + event.timestamp());
 * };
 * }</pre>
 *
 * @see DisconnectEventHandler
 *
 * @author Luis-St
 *
 * @param connection The connection that was closed, or null if not available
 * @param localEndpoint The local endpoint of the connection
 * @param remoteEndpoint The remote endpoint of the connection
 * @param timestamp When the event occurred
 */
public record DisconnectEvent(
	@Nullable Connection connection,
	@NonNull IpEndpoint localEndpoint,
	@NonNull IpEndpoint remoteEndpoint,
	@NonNull Instant timestamp
) {
	
	/**
	 * Constructs a new disconnect event.<br>
	 *
	 * @param connection The connection that was closed, or null if not available
	 * @param localEndpoint The local endpoint of the connection
	 * @param remoteEndpoint The remote endpoint of the connection
	 * @param timestamp When the event occurred
	 * @throws NullPointerException If the local endpoint, remote endpoint, or timestamp is null
	 */
	public DisconnectEvent {
		Objects.requireNonNull(localEndpoint, "Local endpoint must not be null");
		Objects.requireNonNull(remoteEndpoint, "Remote endpoint must not be null");
		Objects.requireNonNull(timestamp, "Timestamp must not be null");
	}
	
	/**
	 * Creates a disconnect event with the current timestamp.<br>
	 *
	 * @param connection The connection that was closed, or null if not available
	 * @param localEndpoint The local endpoint of the connection
	 * @param remoteEndpoint The remote endpoint of the connection
	 * @return A new disconnect event with the current timestamp
	 * @throws NullPointerException If the local endpoint or remote endpoint is null
	 */
	public static @NonNull DisconnectEvent now(@Nullable Connection connection, @NonNull IpEndpoint localEndpoint, @NonNull IpEndpoint remoteEndpoint) {
		return new DisconnectEvent(connection, localEndpoint, remoteEndpoint, Instant.now());
	}
}
