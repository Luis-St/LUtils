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

package net.luis.utils.io.network.connection.tcp;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.ConnectionHandler;
import net.luis.utils.io.network.connection.StreamServer;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import org.jspecify.annotations.NonNull;

import java.net.Socket;
import java.util.Objects;

/**
 * A server that accepts plain TCP connections.<br>
 * <p>
 *     Every accepted connection is handed to the {@link ConnectionHandler} of the server on its own thread.<br>
 *     The handler can upgrade a connection to TLS with {@link TcpConnection#upgrade(net.luis.utils.io.network.connection.ssl.SslServerConfig)},
 *     which is how a server implements {@code STARTTLS}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IpEndpoint bindAddress = new IpEndpoint(Ipv4Address.ANY, 8080);
 * try (TcpServer server = TcpServer.startOn(bindAddress, ConnectionHandler.perMessage((srv, connection, data) -> connection.send(data)))) {
 *     // Server runs until it is stopped or closed
 * }
 * }</pre>
 *
 * @see TcpServerConfig
 * @see TcpConnection
 * @see ConnectionHandler
 *
 * @author Luis-St
 */
public final class TcpServer extends StreamServer<TcpServer, TcpConnection> {
	
	/**
	 * The configuration of this server.<br>
	 */
	private final TcpServerConfig config;
	
	/**
	 * Constructs a new TCP server with the default configuration.<br>
	 * The server is not started.<br>
	 *
	 * @param bindEndpoint The endpoint the server binds to
	 * @param handler The handler every accepted connection is handed to
	 * @throws NullPointerException If the bind endpoint or the handler is null
	 */
	public TcpServer(@NonNull IpEndpoint bindEndpoint, @NonNull ConnectionHandler<TcpServer, TcpConnection> handler) {
		this(bindEndpoint, TcpServerConfig.DEFAULT, handler);
	}
	
	/**
	 * Constructs a new TCP server with the given configuration.<br>
	 * The server is not started.<br>
	 *
	 * @param bindEndpoint The endpoint the server binds to
	 * @param config The configuration of the server
	 * @param handler The handler every accepted connection is handed to
	 * @throws NullPointerException If the bind endpoint, the configuration or the handler is null
	 */
	public TcpServer(@NonNull IpEndpoint bindEndpoint, @NonNull TcpServerConfig config, @NonNull ConnectionHandler<TcpServer, TcpConnection> handler) {
		super(bindEndpoint, config, handler);
		this.config = config;
	}
	
	/**
	 * Creates a new TCP server with the default configuration and starts it.<br>
	 *
	 * @param bindEndpoint The endpoint the server binds to
	 * @param handler The handler every accepted connection is handed to
	 * @return The started server
	 * @throws NullPointerException If the bind endpoint or the handler is null
	 * @throws NetworkConnectionException If the server cannot be bound or started
	 */
	public static @NonNull TcpServer startOn(@NonNull IpEndpoint bindEndpoint, @NonNull ConnectionHandler<TcpServer, TcpConnection> handler) throws NetworkConnectionException {
		return startOn(bindEndpoint, TcpServerConfig.DEFAULT, handler);
	}
	
	/**
	 * Creates a new TCP server with the given configuration and starts it.<br>
	 *
	 * @param bindEndpoint The endpoint the server binds to
	 * @param config The configuration of the server
	 * @param handler The handler every accepted connection is handed to
	 * @return The started server
	 * @throws NullPointerException If the bind endpoint, the configuration or the handler is null
	 * @throws NetworkConnectionException If the server cannot be bound or started
	 */
	public static @NonNull TcpServer startOn(@NonNull IpEndpoint bindEndpoint, @NonNull TcpServerConfig config, @NonNull ConnectionHandler<TcpServer, TcpConnection> handler) throws NetworkConnectionException {
		TcpServer server = new TcpServer(bindEndpoint, config, handler);
		server.start();
		return server;
	}
	
	/**
	 * Returns the configuration of this server.<br>
	 * @return The configuration
	 */
	public @NonNull TcpServerConfig config() {
		return this.config;
	}
	
	@Override
	protected @NonNull TcpServer self() {
		return this;
	}
	
	@Override
	protected @NonNull TcpConnection open(@NonNull Socket socket) throws NetworkConnectionException {
		Objects.requireNonNull(socket, "Socket must not be null");
		return new TcpConnection(socket, this.config.framing(), this.config.maxMessageSize(), this.config.clientReadTimeout());
	}
}
