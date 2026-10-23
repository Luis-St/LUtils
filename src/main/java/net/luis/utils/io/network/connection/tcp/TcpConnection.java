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

import net.luis.utils.io.network.Endpoint;
import net.luis.utils.io.network.connection.*;
import net.luis.utils.io.network.connection.exception.*;
import net.luis.utils.io.network.connection.ssl.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.time.Duration;
import java.util.Objects;

/**
 * A plain TCP connection.<br>
 * <p>
 *     A client opens a connection with {@link #connectTo(Endpoint)}, a {@link TcpServer} accepts connections and hands them to its handler.<br>
 *     Both sides use the same type with the same operations.
 * </p>
 * <p>
 *     A connection can be upgraded to TLS while it is open, which is how protocols such as {@code STARTTLS} switch to a secure channel.<br>
 *     The client side calls {@link #upgrade(SslUpgradeConfig)}, the server side calls {@link #upgrade(SslServerConfig)}.<br>
 *     Both return a new {@link SslConnection} that owns the socket from then on, while this connection becomes inactive.<br>
 *     The upgraded connection keeps the framing, the maximum message size and the read timeout of this connection.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * try (TcpConnection connection = TcpConnection.connectTo(endpoint)) {
 *     connection.send("STARTTLS".getBytes());
 *     if (isPositiveReply(connection.receive())) {
 *         try (SslConnection secure = connection.upgrade()) {
 *             secure.send("Hello".getBytes());
 *         }
 *     }
 * }
 * }</pre>
 *
 * @see TcpClientConfig
 * @see TcpServer
 * @see SslConnection
 *
 * @author Luis-St
 */
public final class TcpConnection extends Connection {
	
	/**
	 * The endpoint that names the peer for server name indication and hostname verification of a client side upgrade.<br>
	 */
	private final Endpoint peer;
	
	/**
	 * Constructs a new TCP connection over the given connected socket.<br>
	 * The remote address of the socket names the peer of a client side upgrade.<br>
	 *
	 * @param socket The connected socket
	 * @param framing The framing that delimits messages on the stream
	 * @param maxMessageSize The maximum length of a message in bytes
	 * @param readTimeout The read timeout, or null if a receive waits forever
	 * @throws NullPointerException If the socket or the framing is null
	 * @throws IllegalArgumentException If the socket is not connected, the max message size is less than 1 or the read timeout is not positive
	 * @throws NetworkConnectionException If the read timeout cannot be applied to the socket
	 */
	public TcpConnection(@NonNull Socket socket, @NonNull Framing framing, int maxMessageSize, @Nullable Duration readTimeout) throws NetworkConnectionException {
		this(socket, null, framing, maxMessageSize, readTimeout);
	}
	
	/**
	 * Constructs a new TCP connection over the given connected socket.<br>
	 *
	 * @param socket The connected socket
	 * @param peer The endpoint the socket was connected to, or null to use the remote address of the socket
	 * @param framing The framing that delimits messages on the stream
	 * @param maxMessageSize The maximum length of a message in bytes
	 * @param readTimeout The read timeout, or null if a receive waits forever
	 * @throws NullPointerException If the socket or the framing is null
	 * @throws IllegalArgumentException If the socket is not connected, the max message size is less than 1 or the read timeout is not positive
	 * @throws NetworkConnectionException If the read timeout cannot be applied to the socket
	 */
	private TcpConnection(@NonNull Socket socket, @Nullable Endpoint peer, @NonNull Framing framing, int maxMessageSize, @Nullable Duration readTimeout) throws NetworkConnectionException {
		super(socket, framing, maxMessageSize, readTimeout);
		this.peer = peer != null ? peer : this.remoteEndpoint().orElseThrow();
	}
	
	/**
	 * Opens a TCP connection to the given endpoint with the default configuration.<br>
	 *
	 * @param endpoint The endpoint to connect to
	 * @return The open connection
	 * @throws NullPointerException If the endpoint is null
	 * @throws NetworkTimeoutException If connecting times out
	 * @throws NetworkConnectionException If connecting fails
	 */
	public static @NonNull TcpConnection connectTo(@NonNull Endpoint endpoint) throws NetworkConnectionException {
		return connectTo(endpoint, TcpClientConfig.DEFAULT);
	}
	
	/**
	 * Opens a TCP connection to the given endpoint with the given configuration.<br>
	 *
	 * @param endpoint The endpoint to connect to
	 * @param config The configuration of the connection
	 * @return The open connection
	 * @throws NullPointerException If the endpoint or the configuration is null
	 * @throws NetworkTimeoutException If connecting times out
	 * @throws NetworkConnectionException If connecting fails
	 */
	public static @NonNull TcpConnection connectTo(@NonNull Endpoint endpoint, @NonNull TcpClientConfig config) throws NetworkConnectionException {
		Socket socket = NetworkUtils.connectSocket(endpoint, config);
		try {
			return new TcpConnection(socket, endpoint, config.framing(), config.maxMessageSize(), config.readTimeout());
		} catch (NetworkConnectionException e) {
			NetworkUtils.closeQuietly(socket);
			throw e;
		}
	}
	
	/**
	 * Upgrades this connection to TLS as the client side, using the default upgrade configuration.<br>
	 *
	 * @return The secured connection
	 * @throws NetworkConnectionException If this connection is not active, unread input is pending or the TLS handshake fails
	 * @see #upgrade(SslUpgradeConfig)
	 */
	public @NonNull SslConnection upgrade() throws NetworkConnectionException {
		return this.upgrade(SslUpgradeConfig.DEFAULT);
	}
	
	/**
	 * Upgrades this connection to TLS as the client side.<br>
	 * <p>
	 *     TLS is layered over the socket of this connection and the handshake is performed before this method returns.<br>
	 *     The endpoint this connection was opened with names the server for server name indication and hostname verification.<br>
	 *     The upgrade is rejected while unread input is pending, because those bytes would be lost or misread as handshake data.
	 * </p>
	 * <p>
	 *     On success the returned connection owns the socket and this connection becomes inactive.<br>
	 *     If the TLS settings cannot be applied or the handshake fails, the socket is closed, because a partially performed handshake leaves the plain stream unusable.<br>
	 *     If the upgrade is rejected before TLS is layered, for example because unread input is pending, this connection keeps the socket and the caller decides whether to close it.
	 * </p>
	 *
	 * @param config The TLS settings of the upgrade
	 * @return The secured connection
	 * @throws NullPointerException If the configuration is null
	 * @throws NetworkConnectionException If this connection is not active, unread input is pending or the TLS handshake fails
	 */
	public @NonNull SslConnection upgrade(@NonNull SslUpgradeConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(config, "Config must not be null");
		
		return this.handOver(() -> {
			if (this.streams().hasPendingInput()) {
				throw new NetworkConnectionException("Unread plaintext is pending, it would be lost or misread as handshake data", NetworkErrorType.PROTOCOL_ERROR, this.peer);
			}
			
			SSLSocket sslSocket = SslSockets.layerClient(this.socket(), this.peer, config);
			return new SslConnection(sslSocket, this.framing(), this.maxMessageSize(), this.readTimeout().orElse(null));
		});
	}
	
	/**
	 * Upgrades this connection to TLS as the server side.<br>
	 * <p>
	 *     TLS is layered over the socket of this connection in server mode and the handshake is performed before this method returns.<br>
	 *     Input that was already buffered by this connection is passed to the TLS layer, so a handshake that arrived early is not lost.<br>
	 *     Only the TLS settings of the configuration are used, the transport settings of this connection are kept.
	 * </p>
	 * <p>
	 *     On success the returned connection owns the socket and this connection becomes inactive.<br>
	 *     If the TLS settings cannot be applied or the handshake fails, the socket is closed, because a partially performed handshake leaves the plain stream unusable.<br>
	 *     If the upgrade fails before TLS is layered, this connection keeps the socket and the caller decides whether to close it.
	 * </p>
	 *
	 * @param config The configuration that provides the TLS settings of the upgrade
	 * @return The secured connection
	 * @throws NullPointerException If the configuration is null
	 * @throws NetworkConnectionException If this connection is not active or the TLS handshake fails
	 */
	public @NonNull SslConnection upgrade(@NonNull SslServerConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(config, "Config must not be null");
		
		return this.handOver(() -> {
			byte[] consumed;
			try {
				InputStream input = this.streams().input();
				consumed = input.readNBytes(this.streams().hasPendingInput() ? input.available() : 0);
			} catch (IOException e) {
				throw NetworkUtils.mapIoFailure(e, "Failed to read pending input", this.peer);
			}
			
			SSLSocket sslSocket = SslSockets.layerServer(this.socket(), consumed, config);
			return new SslConnection(sslSocket, this.framing(), this.maxMessageSize(), this.readTimeout().orElse(null));
		});
	}
	
	/**
	 * Returns whether this connection was upgraded to TLS.<br>
	 * An upgraded connection is no longer active, the connection returned by the upgrade is used instead.<br>
	 *
	 * @return True if this connection was upgraded
	 */
	public boolean isUpgraded() {
		return this.isReleased();
	}
}
