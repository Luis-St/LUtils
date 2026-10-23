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

package net.luis.utils.io.network.connection;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.udp.UdpServer;
import org.jspecify.annotations.NonNull;

import java.time.Duration;

/**
 * Base interface for network servers.<br>
 * <p>
 *     A server is a loop that receives from the network and feeds a handler.<br>
 *     The stream servers accept connections and hand every {@link Connection} to a {@link ConnectionHandler},
 *     the {@link UdpServer} receives datagrams and hands each one to its handler.
 * </p>
 * <p>
 *     All implementations support try-with-resources through the {@link AutoCloseable} interface,
 *     closing a server stops it immediately.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * try (NetworkServer server = TcpServer.startOn(endpoint, ConnectionHandler.perMessage((srv, connection, data) -> connection.send(data)))) {
 *     // Server runs until it is stopped or closed
 * }
 * }</pre>
 *
 * @see StreamServer
 * @see UdpServer
 *
 * @author Luis-St
 */
public sealed interface NetworkServer extends AutoCloseable permits StreamServer, UdpServer {
	
	/**
	 * Returns whether the server is currently running.<br>
	 * @return True if the server is running
	 */
	boolean isRunning();
	
	/**
	 * Returns the endpoint this server is bound to.<br>
	 * While the server is running, the endpoint is observed from the socket, so an ephemeral port is resolved to the actual port.<br>
	 *
	 * @return The bound endpoint, or the configured bind endpoint if the server is not running
	 */
	@NonNull IpEndpoint boundEndpoint();
	
	/**
	 * Starts the server.<br>
	 * Calling this method on a running server has no effect.<br>
	 *
	 * @throws NetworkConnectionException If the server cannot be bound or started
	 */
	void start() throws NetworkConnectionException;
	
	/**
	 * Stops the server immediately.<br>
	 * All handlers are interrupted by closing their sockets.<br>
	 * Calling this method on a server that is not running has no effect.<br>
	 */
	void stop();
	
	/**
	 * Stops the server gracefully.<br>
	 * <p>
	 *     The server stops receiving new connections or datagrams right away,
	 *     afterward it waits up to the given grace period for the running handlers to finish.<br>
	 *     Handlers that are still running when the grace period has elapsed are interrupted by closing their sockets.<br>
	 *     A handler that blocks in a receive only finishes in time if its remote side closes the connection.
	 * </p>
	 *
	 * @param gracePeriod The maximum time to wait for running handlers
	 * @throws NullPointerException If the grace period is null
	 * @throws IllegalArgumentException If the grace period is negative
	 */
	void stop(@NonNull Duration gracePeriod);
	
	/**
	 * Stops the server immediately.<br>
	 * @see #stop()
	 */
	@Override
	void close();
}
