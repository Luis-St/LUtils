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

import net.luis.utils.io.network.Endpoint;
import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkTimeoutException;
import net.luis.utils.io.network.connection.ssl.SslClient;
import net.luis.utils.io.network.connection.tcp.TcpClient;
import net.luis.utils.io.network.connection.udp.UdpClient;
import net.luis.utils.io.network.connection.udp.UdpDatagram;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * Base interface for network clients.<br>
 * This sealed interface provides common operations for TCP, SSL, and UDP clients.
 * <p>
 *     This interface is sealed and permits only {@link TcpClient}, {@link SslClient}, and {@link UdpClient}<br>
 *     as implementations, ensuring type safety when working with network clients polymorphically.
 * </p>
 * <p>
 *     All implementations support try-with-resources through the {@link AutoCloseable} interface.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * NetworkClient<byte[]> client = ...; // TcpClient or SslClient
 * try (client) {
 *     client.send("Hello".getBytes());
 *     byte[] response = client.receive();
 * }
 *
 * // Pattern matching, for the operations that stay protocol specific
 * switch (client) {
 *     case TcpClient tcp -> tcp.connect(endpoint);
 *     case UdpClient udp -> udp.bind(endpoint);
 * }
 * }</pre>
 *
 * @see TcpClient
 * @see SslClient
 * @see UdpClient
 *
 * @author Luis-St
 *
 * @param <M> The type of message this client sends and receives, which is {@code byte[]} for the stream based clients and {@link UdpDatagram} for the datagram based client
 */
public sealed interface NetworkClient<M> extends AutoCloseable permits TcpClient, SslClient, UdpClient {
	
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
	 * <p>
	 *     The returned endpoint is observed from the underlying socket, so implementations return<br>
	 *     an {@link IpEndpoint} with a literal address rather than the endpoint originally passed in.
	 * </p>
	 *
	 * @return The local endpoint, or empty if not bound
	 */
	@NonNull Optional<? extends Endpoint> localEndpoint();
	
	/**
	 * Returns the remote endpoint this client is connected to, if any.<br>
	 * Connectionless clients such as {@link UdpClient} have no single remote peer and always return empty.
	 *
	 * @return The remote endpoint, or empty if not connected
	 */
	@NonNull Optional<? extends Endpoint> remoteEndpoint();
	
	/**
	 * Sends a message to the remote peer.<br>
	 * Stream based clients send the raw bytes, while datagram based clients send one datagram that carries its own destination.
	 *
	 * @param message The message to send
	 * @throws NullPointerException If the message is null
	 * @throws NetworkConnectionException If sending fails or the message exceeds the buffer size
	 */
	void send(@NonNull M message) throws NetworkConnectionException;
	
	/**
	 * Receives a message from the remote peer (blocking).<br>
	 * Uses the buffer size from the configuration.<br>
	 *
	 * @return The received message
	 * @throws NetworkConnectionException If receiving fails
	 * @throws NetworkTimeoutException If the receive times out
	 */
	@NonNull M receive() throws NetworkConnectionException;
	
	/**
	 * Receives a message with a custom buffer size (blocking).<br>
	 *
	 * @param maxBytes The maximum number of bytes to receive
	 * @return The received message
	 * @throws IllegalArgumentException If maxBytes is less than 1
	 * @throws NetworkConnectionException If receiving fails
	 * @throws NetworkTimeoutException If the receive times out
	 */
	@NonNull M receive(int maxBytes) throws NetworkConnectionException;
	
	@Override
	void close();
}
