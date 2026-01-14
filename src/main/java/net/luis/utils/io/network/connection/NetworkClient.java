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
import net.luis.utils.io.network.connection.tcp.TcpClient;
import net.luis.utils.io.network.connection.udp.UdpClient;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * Base interface for network clients.<br>
 * This sealed interface provides common operations for both TCP and UDP clients.
 * <p>
 *     This interface is sealed and permits only {@link TcpClient} and {@link UdpClient}<br>
 *     as implementations, ensuring type safety when working with network clients polymorphically.
 * </p>
 * <p>
 *     All implementations support try-with-resources through the {@link AutoCloseable} interface.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * NetworkClient client = ...; // TcpClient or UdpClient
 * try (client) {
 *     if (client.isActive()) {
 *         System.out.println("Local: " + client.localEndpoint());
 *     }
 * }
 *
 * // Pattern matching
 * switch (client) {
 *     case TcpClient tcp -> tcp.connect(endpoint);
 *     case UdpClient udp -> udp.bind(endpoint);
 * }
 * }</pre>
 *
 * @see TcpClient
 * @see UdpClient
 *
 * @author Luis-St
 */
public sealed interface NetworkClient extends AutoCloseable permits TcpClient, UdpClient {
	
	/**
	 * Returns whether this client is currently active (connected or bound).<br>
	 * <p>
	 *     For TCP clients, this means connected to a server.<br>
	 *     For UDP clients, this means bound to a local endpoint.
	 * </p>
	 *
	 * @return True if the client is active
	 */
	boolean isActive();
	
	/**
	 * Returns the local endpoint this client is bound to, if any.<br>
	 * @return The local endpoint, or empty if not bound
	 */
	@NonNull Optional<IpEndpoint> localEndpoint();
	
	@Override
	void close();
}
