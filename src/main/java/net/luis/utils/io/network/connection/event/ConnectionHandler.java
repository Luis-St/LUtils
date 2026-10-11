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

import net.luis.utils.io.network.connection.Connection;
import net.luis.utils.io.network.connection.NetworkServer;
import org.jspecify.annotations.NonNull;

/**
 * Handler that takes over a whole client connection.<br>
 * This functional interface is used on servers that hand the connection to the user instead of driving a read loop themselves.<br>
 * <p>
 *     Unlike {@link MessageEventHandler}, which is called once per received message,<br>
 *     this handler is called once per connection and owns it for its entire lifetime.<br>
 *     The server does not read from the connection while the handler runs,<br>
 *     so the handler is free to read and write the streams of the connection directly and to consume exactly as much as the protocol requires.<br>
 *     This is what stream oriented protocols such as FTP, SMTP, or line based command protocols need,<br>
 *     because they interleave reads and writes in an order only the protocol knows.
 * </p>
 * <p>
 *     Which wrapper is put on the streams is up to the handler, a reader,<br>
 *     a writer, a data stream, or a parser of its own.<br>
 *     It has to be created once and then reused,<br>
 *     because a buffered wrapper reads ahead and a second one built over the same connection would wait for data the first one already holds.<br>
 *     The streams themselves never change, so a wrapper created at the start of the handler stays valid for the whole session,<br>
 *     and one that has to be reachable from elsewhere can be kept in {@link Connection#context()}.<br>
 *     Wrappers must not be closed, the server closes the connection.
 * </p>
 * <p>
 *     The server still owns the thread the handler runs on, registers the connection, reports errors, and closes the connection.<br>
 *     The handler therefore runs on the virtual thread the server assigned to the client and simply returns when the conversation is over.
 * </p>
 * <p>
 *     Because the handler owns the read side, framing does not apply to the raw streams.<br>
 *     It still applies to {@link Connection#send(byte[])} and {@link Connection#receive()},<br>
 *     so a handler that mixes both has to keep the configured framing in mind.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * ConnectionHandler<TcpServer, TcpConnection> handler = (server, connection) -> {
 *     // Created once, reused for the whole session
 *     BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.US_ASCII));
 *     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.US_ASCII));
 *
 *     writer.write("220 Service ready\r\n");
 *     writer.flush();
 *
 *     String line;
 *     while ((line = reader.readLine()) != null) {
 *         if (line.startsWith("QUIT")) {
 *             writer.write("221 Goodbye\r\n");
 *             writer.flush();
 *             break;
 *         }
 *         writer.write("502 Command not implemented\r\n");
 *         writer.flush();
 *     }
 * };
 *
 * TcpServerConfig config = TcpServerConfig.builder()
 *     .framing(false)
 *     .onConnection(handler)
 *     .build();
 * }</pre>
 *
 * @see MessageEventHandler
 *
 * @author Luis-St
 *
 * @param <S> The server type (TcpServer or SslServer)
 * @param <C> The connection type (TcpConnection for TCP, SslConnection for SSL)
 */
@FunctionalInterface
public interface ConnectionHandler<S extends NetworkServer, C extends Connection> {
	
	/**
	 * Called once for every accepted client, after the connection has been established.<br>
	 * The connection is closed by the server as soon as this method returns or throws.<br>
	 *
	 * @param server The server the client connected to
	 * @param connection The connection to the client, owned by this handler until it returns
	 * @throws Exception If the handler fails, the failure is reported to the configured error handler
	 */
	void handle(@NonNull S server, @NonNull C connection) throws Exception;
}
