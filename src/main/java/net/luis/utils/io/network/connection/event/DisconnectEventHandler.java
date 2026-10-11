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

import net.luis.utils.io.network.Endpoint;
import net.luis.utils.io.network.connection.Connection;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 * Handler for connection termination events.<br>
 * This functional interface is used to handle disconnection events for both TCP/SSL clients and servers.<br>
 * <p>
 *     The {@code connection} is only available where the connection is already wrapped in a {@link Connection}
 *     at the time the event fires (e.g. server-side {@code onClientDisconnect}). Client-side {@code onDisconnect}
 *     handlers do not have a {@link Connection} instance to offer and receive {@code null} instead. Where present,
 *     the connection is still active at the time the handler runs and is closed only after the handler returns,
 *     so it can be used to send a final message (e.g. a graceful shutdown notice) before the connection is torn
 *     down. Calling {@link Connection#receive()} from the handler should be avoided, since it blocks synchronously
 *     and the peer is not guaranteed to send anything further.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * DisconnectEventHandler onDisconnect = (connection, localEndpoint, remoteEndpoint, timestamp) -> {
 *     System.out.println("Disconnected from " + remoteEndpoint + " at " + timestamp);
 * };
 *
 * TcpClientConfig config = TcpClientConfig.builder()
 *     .onDisconnect(onDisconnect)
 *     .build();
 * }</pre>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface DisconnectEventHandler {
	
	/**
	 * Called when a disconnect event occurs.<br>
	 *
	 * @param connection The connection that was closed, or null if not available
	 * @param localEndpoint The local endpoint of the connection
	 * @param remoteEndpoint The remote endpoint of the connection
	 * @param timestamp When the event occurred
	 */
	void handle(@Nullable Connection connection, @NonNull Endpoint localEndpoint, @NonNull Endpoint remoteEndpoint, @NonNull Instant timestamp);
}
