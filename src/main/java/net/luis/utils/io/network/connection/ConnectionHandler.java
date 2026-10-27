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

import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Handles a single connection accepted by a stream server.<br>
 * <p>
 *     The handler is called once per connection on the thread the server assigned to it and owns the connection until it returns.<br>
 *     It is written as plain blocking code.<br>
 *     Whatever has to happen when a client connects goes before the receive loop,
 *     whatever has to happen when it disconnects goes after it or into a finally block.<br>
 *     State that belongs to one connection is kept in local variables.
 * </p>
 * <p>
 *     The server closes the connection once the handler returns.<br>
 *     An exception thrown by the handler ends the connection and is reported to the error handler of the server.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * ConnectionHandler<TcpServer, TcpConnection> handler = (server, connection) -> {
 *     System.out.println("Connected: " + connection.remoteEndpoint());
 *     Optional<byte[]> message;
 *     while ((message = connection.receive()).isPresent()) {
 *         connection.send(message.get());
 *     }
 *     System.out.println("Disconnected: " + connection.remoteEndpoint());
 * };
 * }</pre>
 *
 * @see MessageHandler
 * @see StreamServer
 *
 * @author Luis-St
 *
 * @param <S> The type of the server that accepted the connection
 * @param <C> The type of the connection
 */
@FunctionalInterface
public interface ConnectionHandler<S extends NetworkServer, C extends Connection> {
	
	/**
	 * Creates a connection handler that receives messages until the connection ends and passes each one to the given message handler.<br>
	 * <p>
	 *     The returned handler returns once the remote side closed the connection.<br>
	 *     An exception thrown by the message handler ends the connection.
	 * </p>
	 *
	 * @param handler The handler called for every received message
	 * @return The connection handler
	 * @param <S> The type of the server that accepted the connection
	 * @param <C> The type of the connection
	 * @throws NullPointerException If the handler is null
	 */
	static <S extends NetworkServer, C extends Connection> @NonNull ConnectionHandler<S, C> perMessage(@NonNull MessageHandler<? super S, ? super C> handler) {
		Objects.requireNonNull(handler, "Handler must not be null");
		return (server, connection) -> {
			Optional<byte[]> message;
			while ((message = connection.receive()).isPresent()) {
				handler.handle(server, connection, message.get());
			}
		};
	}
	
	/**
	 * Handles the given connection until the communication with the client is finished.<br>
	 *
	 * @param server The server that accepted the connection
	 * @param connection The connection to handle
	 * @throws Exception If handling the connection fails
	 */
	void handle(@NonNull S server, @NonNull C connection) throws Exception;
}
