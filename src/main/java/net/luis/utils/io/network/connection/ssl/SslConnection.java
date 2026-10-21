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

import net.luis.utils.io.network.Endpoint;
import net.luis.utils.io.network.connection.*;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkTimeoutException;
import net.luis.utils.io.network.connection.tcp.TcpClientConfig;
import net.luis.utils.io.network.connection.tcp.TcpConnection;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.Objects;

/**
 * A TLS secured connection.<br>
 * <p>
 *     A client opens a secured connection with {@link #connectTo(Endpoint)} or upgrades a plain one with {@link TcpConnection#upgrade(SslUpgradeConfig)},
 *     an {@link SslServer} accepts secured connections and hands them to its handler.<br>
 *     The TLS handshake has always completed when a secured connection is handed out.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * try (SslConnection connection = SslConnection.connectTo(new HostEndpoint("example.com", 8443))) {
 *     System.out.println("Protocol: " + connection.getSession().getProtocol());
 *     connection.send("Hello".getBytes());
 * }
 * }</pre>
 *
 * @see SslClientConfig
 * @see SslServer
 *
 * @author Luis-St
 */
public final class SslConnection extends Connection {
	
	/**
	 * The underlying SSL socket.<br>
	 */
	private final SSLSocket sslSocket;
	
	/**
	 * Constructs a new secured connection over the given SSL socket.<br>
	 * <p>
	 *     The constructor does not perform the handshake.<br>
	 *     If the handshake of the socket has not been performed yet, it is performed on the first read or write.<br>
	 *     The connections created by this package are only constructed after their handshake has completed.
	 * </p>
	 *
	 * @param socket The connected SSL socket
	 * @param framing The framing that delimits messages on the stream
	 * @param maxMessageSize The maximum length of a message in bytes
	 * @param readTimeout The read timeout, or null if a receive waits forever
	 * @throws NullPointerException If the socket or the framing is null
	 * @throws IllegalArgumentException If the socket is not connected, the max message size is less than 1 or the read timeout is not positive
	 * @throws NetworkConnectionException If the read timeout cannot be applied to the socket
	 */
	public SslConnection(@NonNull SSLSocket socket, @NonNull Framing framing, int maxMessageSize, @Nullable Duration readTimeout) throws NetworkConnectionException {
		super(socket, framing, maxMessageSize, readTimeout);
		this.sslSocket = socket;
	}
	
	/**
	 * Opens a secured connection to the given endpoint with the default configuration.<br>
	 *
	 * @param endpoint The endpoint to connect to
	 * @return The open connection
	 * @throws NullPointerException If the endpoint is null
	 * @throws NetworkTimeoutException If connecting or the handshake times out
	 * @throws NetworkConnectionException If connecting or the handshake fails
	 */
	public static @NonNull SslConnection connectTo(@NonNull Endpoint endpoint) throws NetworkConnectionException {
		return connectTo(endpoint, SslClientConfig.DEFAULT);
	}
	
	/**
	 * Opens a secured connection to the given endpoint with the given configuration.<br>
	 * A plain connection is opened with the transport settings and secured with the TLS settings of the configuration.<br>
	 *
	 * @param endpoint The endpoint to connect to
	 * @param config The configuration of the connection
	 * @return The open connection
	 * @throws NullPointerException If the endpoint or the configuration is null
	 * @throws NetworkTimeoutException If connecting or the handshake times out
	 * @throws NetworkConnectionException If connecting or the handshake fails
	 */
	public static @NonNull SslConnection connectTo(@NonNull Endpoint endpoint, @NonNull SslClientConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		
		TcpClientConfig transport = config.transport();
		Socket socket = NetworkUtils.connectSocket(endpoint, transport);
		try {
			SSLSocket sslSocket = SslSockets.layerClient(socket, endpoint, config.tls());
			return new SslConnection(sslSocket, transport.framing(), transport.maxMessageSize(), transport.readTimeout());
		} catch (NetworkConnectionException e) {
			NetworkUtils.closeQuietly(socket);
			throw e;
		}
	}
	
	/**
	 * Returns the TLS session of this connection.<br>
	 * The session provides details such as the negotiated protocol, the cipher suite and the peer certificates.<br>
	 *
	 * @return The SSL session
	 */
	public @NonNull SSLSession getSession() {
		return this.sslSocket.getSession();
	}
}
