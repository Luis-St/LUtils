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
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.Objects;

/**
 * Event context for connection lifecycle events.<br>
 * This record contains information about a connection event such as connect or disconnect.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * ConnectionEventHandler handler = event -> {
 *     System.out.println("Connection from " + event.remoteEndpoint() + " at " + event.timestamp());
 * };
 * }</pre>
 *
 * @see ConnectionEventHandler
 *
 * @author Luis-St
 *
 * @param localEndpoint The local endpoint of the connection
 * @param remoteEndpoint The remote endpoint of the connection
 * @param timestamp When the event occurred
 */
public record ConnectionEvent(
	@NonNull IpEndpoint localEndpoint,
	@NonNull IpEndpoint remoteEndpoint,
	@NonNull Instant timestamp
) {
	
	/**
	 * Constructs a new connection event.<br>
	 *
	 * @param localEndpoint The local endpoint of the connection
	 * @param remoteEndpoint The remote endpoint of the connection
	 * @param timestamp When the event occurred
	 * @throws NullPointerException If any parameter is null
	 */
	public ConnectionEvent {
		Objects.requireNonNull(localEndpoint, "Local endpoint must not be null");
		Objects.requireNonNull(remoteEndpoint, "Remote endpoint must not be null");
		Objects.requireNonNull(timestamp, "Timestamp must not be null");
	}
	
	/**
	 * Creates a connection event with the current timestamp.<br>
	 *
	 * @param localEndpoint The local endpoint of the connection
	 * @param remoteEndpoint The remote endpoint of the connection
	 * @return A new connection event with the current timestamp
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull ConnectionEvent now(@NonNull IpEndpoint localEndpoint, @NonNull IpEndpoint remoteEndpoint) {
		return new ConnectionEvent(localEndpoint, remoteEndpoint, Instant.now());
	}
}
