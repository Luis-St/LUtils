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

import net.luis.utils.io.network.connection.NetworkServer;
import org.jspecify.annotations.NonNull;

/**
 * Handler for incoming messages/datagrams.<br>
 * This functional interface is used to handle incoming data on servers.
 * <p>
 *     For TCP servers, the context parameter is a {@code TcpConnection} that can be used to send replies.<br>
 *     For UDP servers, the context parameter is a {@code UdpDatagram} containing the source endpoint.
 * </p>
 * <p>
 *     Example usage for TCP:
 * </p>
 * <pre>{@code
 * MessageEventHandler<TcpServer, TcpConnection> handler = (server, connection, data) -> {
 *     System.out.println("Received from " + connection.remoteEndpoint());
 *     connection.send("Echo: ".getBytes());
 *     connection.send(data);
 * };
 * }</pre>
 * <p>
 *     Example usage for UDP:
 * </p>
 * <pre>{@code
 * MessageEventHandler<UdpServer, UdpDatagram> handler = (server, datagram, data) -> {
 *     System.out.println("Received from " + datagram.endpoint());
 *     server.send(datagram.endpoint(), "Pong".getBytes());
 * };
 * }</pre>
 *
 * @param <S> The server type (TcpServer or UdpServer)
 * @param <C> The context type (TcpConnection for TCP, UdpDatagram for UDP)
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface MessageEventHandler<S extends NetworkServer, C> {

	/**
	 * Called when a message is received.<br>
	 *
	 * @param server The server that received the message
	 * @param context The context for replying (connection for TCP, datagram for UDP)
	 * @param data The raw message data
	 */
	void handle(@NonNull S server, @NonNull C context, byte @NonNull [] data);
}
