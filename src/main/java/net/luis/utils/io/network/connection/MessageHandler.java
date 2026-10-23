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

/**
 * Handles a single message received on a connection.<br>
 * <p>
 *     A message handler is turned into a {@link ConnectionHandler} by {@link ConnectionHandler#perMessage(MessageHandler)},
 *     which runs the receive loop and calls this handler for every received message.<br>
 *     This keeps simple request and response servers to a single lambda.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TcpServer server = TcpServer.startOn(endpoint, ConnectionHandler.perMessage((srv, connection, data) -> {
 *     connection.send(data);
 * }));
 * }</pre>
 *
 * @see ConnectionHandler
 *
 * @author Luis-St
 *
 * @param <S> The type of the server that accepted the connection
 * @param <C> The type of the connection
 */
@FunctionalInterface
public interface MessageHandler<S extends NetworkServer, C extends Connection> {
	
	/**
	 * Handles a message received on the given connection.<br>
	 *
	 * @param server The server that accepted the connection
	 * @param connection The connection the message was received on
	 * @param data The received message
	 * @throws Exception If handling the message fails, which ends the connection
	 */
	void handle(@NonNull S server, @NonNull C connection, byte @NonNull [] data) throws Exception;
}
