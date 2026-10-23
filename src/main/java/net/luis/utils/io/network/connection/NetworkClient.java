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
import net.luis.utils.io.network.connection.exception.NetworkTimeoutException;
import net.luis.utils.io.network.connection.udp.UdpDatagram;
import net.luis.utils.io.network.connection.udp.UdpPeer;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * Base interface for everything that sends and receives messages over the network.<br>
 * <p>
 *     A network client is a live endpoint of a communication.<br>
 *     The stream based {@link Connection connections} are network clients of bytes,
 *     no matter whether they were opened by connecting to a server or accepted by a {@link NetworkServer}.<br>
 *     The datagram based {@link UdpPeer} is a network client of {@link UdpDatagram datagrams},
 *     and a {@link CodecClient} is a network client of the values its codec encodes.
 * </p>
 * <p>
 *     Code that is written against this interface therefore works on both sides of a communication and for every transport.<br>
 *     All implementations support try-with-resources through the {@link AutoCloseable} interface.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * try (NetworkClient<byte[]> client = TcpConnection.connectTo(endpoint)) {
 *     client.send("Hello".getBytes());
 *     Optional<byte[]> response = client.receive();
 * }
 * }</pre>
 *
 * @see Connection
 * @see UdpPeer
 * @see CodecClient
 *
 * @author Luis-St
 *
 * @param <M> The type of message this client sends and receives
 */
public sealed interface NetworkClient<M> extends AutoCloseable permits Connection, UdpPeer, CodecClient {
	
	/**
	 * Returns whether this client is currently active.<br>
	 * <p>
	 *     A connection is active while it is open and has not been handed over to an upgraded connection.<br>
	 *     A peer is active while its socket is open.<br>
	 *     A close by the remote side is only noticed by the next receive, which then returns an empty optional.
	 * </p>
	 *
	 * @return True if the client is active
	 */
	boolean isActive();
	
	/**
	 * Returns the local endpoint this client is bound to.<br>
	 * @return The local endpoint, or an empty optional if the client is not bound
	 */
	@NonNull Optional<IpEndpoint> localEndpoint();
	
	/**
	 * Returns the remote endpoint this client communicates with.<br>
	 * A peer that is not connected to a single remote endpoint returns an empty optional.<br>
	 *
	 * @return The remote endpoint, or an empty optional if there is no single remote endpoint
	 */
	@NonNull Optional<IpEndpoint> remoteEndpoint();
	
	/**
	 * Sends a message to the remote side.<br>
	 * Sending is thread safe, concurrent sends are written one after the other and never interleave.<br>
	 *
	 * @param message The message to send
	 * @throws NullPointerException If the message is null
	 * @throws NetworkConnectionException If the client is not active, the message is too large or sending fails
	 */
	void send(@NonNull M message) throws NetworkConnectionException;
	
	/**
	 * Receives the next message from the remote side (blocking).<br>
	 * <p>
	 *     An empty optional means that no further message will arrive.<br>
	 *     This is the case when the remote side closed the connection or when the client was closed locally.<br>
	 *     A connection that was handed over to an upgraded connection rejects the receive instead.
	 * </p>
	 *
	 * @return The received message, or an empty optional if the communication has ended
	 * @throws NetworkTimeoutException If no message arrived within the configured timeout
	 * @throws NetworkConnectionException If the connection was handed over, the message is too large or receiving fails
	 */
	@NonNull Optional<M> receive() throws NetworkConnectionException;
	
	@Override
	void close();
}
