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
import net.luis.utils.io.network.connection.tcp.TcpServer;
import net.luis.utils.io.network.connection.udp.UdpServer;
import org.jspecify.annotations.NonNull;

/**
 * Base interface for network servers.<br>
 * This sealed interface provides common operations for both TCP and UDP servers.<br>
 * <p>
 *     This interface is sealed and permits only {@link TcpServer} and {@link UdpServer}<br>
 *     as implementations, ensuring type safety when working with network servers polymorphically.
 * </p>
 * <p>
 *     All implementations support try-with-resources through the {@link AutoCloseable} interface.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * NetworkServer server = ...; // TcpServer or UdpServer
 * try (server) {
 *     server.start();
 *     System.out.println("Server running on: " + server.boundEndpoint());
 *     // Wait for shutdown signal
 * }
 *
 * // Pattern matching
 * switch (server) {
 *     case TcpServer tcp -> System.out.println("Clients: " + tcp.getClientCount());
 *     case UdpServer udp -> System.out.println("UDP server");
 * }
 * }</pre>
 *
 * @see TcpServer
 * @see UdpServer
 *
 * @author Luis-St
 */
public sealed interface NetworkServer extends AutoCloseable permits TcpServer, UdpServer {
	
	/**
	 * Returns whether this server is currently running.<br>
	 * @return True if the server is running
	 */
	boolean isRunning();
	
	/**
	 * Returns the endpoint this server is bound to.<br>
	 * @return The bound endpoint
	 */
	@NonNull IpEndpoint boundEndpoint();
	
	/**
	 * Starts the server and begins accepting connections/datagrams.<br>
	 * This method returns immediately; the server runs in background threads.
	 */
	void start();
	
	/**
	 * Stops the server gracefully.<br>
	 * This method blocks until all active operations complete or timeout.
	 */
	void stop();
	
	@Override
	void close();
}
