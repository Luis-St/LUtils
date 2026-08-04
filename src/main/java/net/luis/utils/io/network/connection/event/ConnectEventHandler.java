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
 * Handler for connection establishment events.<br>
 * This functional interface is used to handle connection events for both TCP/SSL clients and servers.<br>
 * <p>
 *     The {@code connection} is only available where the connection is already wrapped in a {@link Connection}
 *     at the time the event fires (e.g. server-side {@code onClientConnect}). Client-side {@code onConnect}
 *     handlers do not have a {@link Connection} instance to offer and receive {@code null} instead.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * ConnectEventHandler onConnect = (connection, localEndpoint, remoteEndpoint, timestamp) -> {
 *     System.out.println("Connected to " + remoteEndpoint + " at " + timestamp);
 * };
 *
 * TcpClientConfig config = TcpClientConfig.builder()
 *     .onConnect(onConnect)
 *     .build();
 * }</pre>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface ConnectEventHandler {
	
	/**
	 * Called when a connect event occurs.<br>
	 *
	 * @param connection The connection that was established, or null if not available
	 * @param localEndpoint The local endpoint of the connection
	 * @param remoteEndpoint The remote endpoint of the connection
	 * @param timestamp When the event occurred
	 */
	void handle(@Nullable Connection connection, @NonNull Endpoint localEndpoint, @NonNull Endpoint remoteEndpoint, @NonNull Instant timestamp);
}
