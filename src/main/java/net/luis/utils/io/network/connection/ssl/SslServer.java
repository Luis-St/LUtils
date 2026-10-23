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

package net.luis.utils.io.network.connection.ssl;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.ConnectionHandler;
import net.luis.utils.io.network.connection.StreamServer;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.tcp.TcpServerConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;

import javax.net.ssl.SSLSocket;
import java.net.Socket;
import java.util.Objects;

/**
 * A server that accepts TLS secured connections.<br>
 * <p>
 *     Every accepted connection is secured with the TLS settings of the configuration before it is handed to the {@link ConnectionHandler}.<br>
 *     The handshake is performed on the thread that serves the connection, so a slow or failing handshake does not block other connections.<br>
 *     A failed handshake is reported to the error handler of the configuration and the handler is not called.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IpEndpoint bindAddress = new IpEndpoint(Ipv4Address.ANY, 8443);
 * SslServerConfig config = SslServerConfig.builder(sslContext)
 *     .clientAuth(SslClientAuth.REQUESTED)
 *     .build();
 *
 * try (SslServer server = SslServer.startOn(bindAddress, config, ConnectionHandler.perMessage((srv, connection, data) -> connection.send(data)))) {
 *     // Server runs until it is stopped or closed
 * }
 * }</pre>
 *
 * @see SslServerConfig
 * @see SslConnection
 * @see ConnectionHandler
 *
 * @author Luis-St
 */
public final class SslServer extends StreamServer<SslServer, SslConnection> {
	
	/**
	 * The configuration of this server.<br>
	 */
	private final SslServerConfig config;
	
	/**
	 * Constructs a new SSL server with the given configuration.<br>
	 * The server is not started.<br>
	 *
	 * @param bindEndpoint The endpoint the server binds to
	 * @param config The configuration of the server
	 * @param handler The handler every accepted connection is handed to
	 * @throws NullPointerException If the bind endpoint, the configuration or the handler is null
	 */
	public SslServer(@NonNull IpEndpoint bindEndpoint, @NonNull SslServerConfig config, @NonNull ConnectionHandler<SslServer, SslConnection> handler) {
		super(bindEndpoint, Objects.requireNonNull(config, "Config must not be null").transport(), handler);
		this.config = config;
	}
	
	/**
	 * Creates a new SSL server with the given configuration and starts it.<br>
	 *
	 * @param bindEndpoint The endpoint the server binds to
	 * @param config The configuration of the server
	 * @param handler The handler every accepted connection is handed to
	 * @return The started server
	 * @throws NullPointerException If the bind endpoint, the configuration or the handler is null
	 * @throws NetworkConnectionException If the server cannot be bound or started
	 */
	public static @NonNull SslServer startOn(@NonNull IpEndpoint bindEndpoint, @NonNull SslServerConfig config, @NonNull ConnectionHandler<SslServer, SslConnection> handler) throws NetworkConnectionException {
		SslServer server = new SslServer(bindEndpoint, config, handler);
		server.start();
		return server;
	}
	
	/**
	 * Returns the configuration of this server.<br>
	 * @return The configuration
	 */
	public @NonNull SslServerConfig config() {
		return this.config;
	}
	
	@Override
	protected @NonNull SslServer self() {
		return this;
	}
	
	@Override
	protected @NonNull SslConnection open(@NonNull Socket socket) throws NetworkConnectionException {
		Objects.requireNonNull(socket, "Socket must not be null");
		
		TcpServerConfig transport = this.config.transport();
		SSLSocket sslSocket = SslSockets.layerServer(socket, ArrayUtils.EMPTY_BYTE_ARRAY, this.config);
		return new SslConnection(sslSocket, transport.framing(), transport.maxMessageSize(), transport.clientReadTimeout());
	}
}
