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

import net.luis.utils.io.network.*;
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.exception.*;
import net.luis.utils.io.network.connection.tcp.TcpConnection;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Objects;

/**
 * Utility methods that layer TLS over connected plain sockets.<br>
 * <p>
 *     Every TLS connection of this package is created by layering TLS over a plain socket,
 *     no matter whether it was opened by connecting, accepted by a server or upgraded from a {@link TcpConnection}.<br>
 *     This keeps one code path for the TLS settings and the handshake.
 * </p>
 * <p>
 *     The layered socket owns the plain socket, closing it closes the plain socket as well.<br>
 *     If configuring the layered socket or the handshake fails, the layered socket is closed and the plain socket with it.<br>
 *     A partially performed handshake leaves the plain stream unusable, so the plain socket cannot be handed back.<br>
 *     If layering fails before the layered socket is created, the plain socket stays open and the caller decides whether to close it.
 * </p>
 *
 * @author Luis-St
 */
public final class SslSockets {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private SslSockets() {}
	
	/**
	 * Layers TLS over the given connected socket as the client side and performs the handshake.<br>
	 * <p>
	 *     The given endpoint names the server for server name indication and hostname verification.<br>
	 *     A {@link HostEndpoint} contributes its hostname, an {@link IpEndpoint} its literal address.
	 * </p>
	 * <p>
	 *     If the TLS settings cannot be applied or the handshake fails, the given socket is closed.
	 * </p>
	 *
	 * @param socket The connected plain socket
	 * @param peer The endpoint the socket is connected to
	 * @param config The TLS settings
	 * @return The layered socket after a successful handshake
	 * @throws NullPointerException If the socket, the peer or the configuration is null
	 * @throws NetworkConnectionException If the socket is not connected, the SSL context cannot be created or the handshake fails
	 */
	public static @NonNull SSLSocket layerClient(@NonNull Socket socket, @NonNull Endpoint peer, @NonNull SslUpgradeConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(socket, "Socket must not be null");
		Objects.requireNonNull(peer, "Peer must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		ensureConnected(socket, peer);
		
		SSLContext context;
		try {
			context = config.resolveSslContext();
		} catch (NoSuchAlgorithmException e) {
			throw new NetworkConnectionException("Failed to initialize SSL context", e, NetworkErrorType.CONNECTION_FAILED, peer);
		}
		
		SSLSocket sslSocket = null;
		try {
			sslSocket = (SSLSocket) context.getSocketFactory().createSocket(socket, peerHost(peer), peer.port(), true);
			if (!config.enabledProtocols().isEmpty()) {
				sslSocket.setEnabledProtocols(TlsProtocol.toProtocolNames(config.enabledProtocols()));
			}
			if (!config.enabledCipherSuites().isEmpty()) {
				sslSocket.setEnabledCipherSuites(config.enabledCipherSuites().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
			}
			if (config.verifyHostname()) {
				SSLParameters parameters = sslSocket.getSSLParameters();
				parameters.setEndpointIdentificationAlgorithm("HTTPS");
				sslSocket.setSSLParameters(parameters);
			}
			
			handshake(sslSocket, peer);
			return sslSocket;
		} catch (NetworkConnectionException e) {
			NetworkUtils.closeQuietly(sslSocket);
			throw e;
		} catch (IOException | IllegalArgumentException e) {
			NetworkUtils.closeQuietly(sslSocket);
			throw new NetworkConnectionException("Failed to layer TLS over the connection to " + peer, e, NetworkErrorType.HANDSHAKE_FAILED, peer);
		}
	}
	
	/**
	 * Layers TLS over the given connected socket as the server side and performs the handshake.<br>
	 * <p>
	 *     The given consumed bytes are input that was already read from the socket,
	 *     they are processed before any further input of the socket.<br>
	 *     Only the TLS settings of the configuration are used.<br>
	 *     If the TLS settings cannot be applied or the handshake fails, the given socket is closed.
	 * </p>
	 *
	 * @param socket The connected plain socket
	 * @param consumed The input that was already read from the socket
	 * @param config The configuration that provides the TLS settings
	 * @return The layered socket after a successful handshake
	 * @throws NullPointerException If the socket, the consumed bytes or the configuration is null
	 * @throws NetworkConnectionException If the socket is not connected or the handshake fails
	 */
	public static @NonNull SSLSocket layerServer(@NonNull Socket socket, byte @NonNull [] consumed, @NonNull SslServerConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(socket, "Socket must not be null");
		Objects.requireNonNull(consumed, "Consumed data must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		
		ensureConnected(socket, null);
		if (!(socket.getRemoteSocketAddress() instanceof InetSocketAddress remoteAddress)) {
			throw new NetworkConnectionException("Socket is not connected to an internet address", NetworkErrorType.NOT_CONNECTED);
		}
		IpEndpoint peer = IpEndpoint.from(remoteAddress);
		
		SSLSocket sslSocket = null;
		try {
			sslSocket = (SSLSocket) config.sslContext().getSocketFactory().createSocket(socket, new ByteArrayInputStream(consumed), true);
			if (!config.enabledProtocols().isEmpty()) {
				sslSocket.setEnabledProtocols(TlsProtocol.toProtocolNames(config.enabledProtocols()));
			}
			if (!config.enabledCipherSuites().isEmpty()) {
				sslSocket.setEnabledCipherSuites(config.enabledCipherSuites().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
			}
			switch (config.clientAuth()) {
				case NONE -> {}
				case REQUESTED -> sslSocket.setWantClientAuth(true);
				case REQUIRED -> sslSocket.setNeedClientAuth(true);
			}
			
			handshake(sslSocket, peer);
			return sslSocket;
		} catch (NetworkConnectionException e) {
			NetworkUtils.closeQuietly(sslSocket);
			throw e;
		} catch (IOException | IllegalArgumentException e) {
			NetworkUtils.closeQuietly(sslSocket);
			throw new NetworkConnectionException("Failed to layer TLS over the connection from " + peer, e, NetworkErrorType.HANDSHAKE_FAILED, peer);
		}
	}
	
	/**
	 * Ensures that the given socket is connected.<br>
	 *
	 * @param socket The socket to check
	 * @param peer The endpoint to attach to the thrown exception, or null if it is not known
	 * @throws NullPointerException If the socket is null
	 * @throws NetworkConnectionException If the socket is closed or not connected
	 */
	private static void ensureConnected(@NonNull Socket socket, @Nullable Endpoint peer) throws NetworkConnectionException {
		Objects.requireNonNull(socket, "Socket must not be null");
		if (socket.isClosed() || !socket.isConnected()) {
			throw new NetworkConnectionException("Socket is not connected", NetworkErrorType.NOT_CONNECTED, peer);
		}
	}
	
	/**
	 * Performs the TLS handshake on the given socket and maps its failures to network exceptions.<br>
	 *
	 * @param socket The layered socket
	 * @param peer The endpoint to attach to thrown exceptions
	 * @throws NullPointerException If the socket or the peer is null
	 * @throws NetworkTimeoutException If the handshake times out
	 * @throws NetworkConnectionException If the handshake fails
	 */
	private static void handshake(@NonNull SSLSocket socket, @NonNull Endpoint peer) throws NetworkConnectionException {
		Objects.requireNonNull(socket, "Socket must not be null");
		Objects.requireNonNull(peer, "Peer must not be null");
		
		try {
			socket.startHandshake();
		} catch (SocketTimeoutException e) {
			int timeout = 0;
			try {
				timeout = socket.getSoTimeout();
			} catch (IOException _) {}
			throw new NetworkTimeoutException("TLS handshake timed out with " + peer, e, NetworkErrorType.HANDSHAKE_FAILED, Duration.ofMillis(timeout), peer);
		} catch (IOException e) {
			throw new NetworkConnectionException("TLS handshake failed with " + peer, e, NetworkErrorType.HANDSHAKE_FAILED, peer);
		}
	}
	
	/**
	 * Determines the peer host name used for server name indication and hostname verification.<br>
	 *
	 * @param peer The remote endpoint
	 * @return The hostname of a host endpoint, or the literal address of an ip endpoint
	 * @throws NullPointerException If the peer is null
	 */
	private static @NonNull String peerHost(@NonNull Endpoint peer) {
		Objects.requireNonNull(peer, "Peer must not be null");
		return switch (peer) {
			case HostEndpoint hostEndpoint -> hostEndpoint.hostname();
			case IpEndpoint ipEndpoint -> ipEndpoint.address().toString();
		};
	}
}
