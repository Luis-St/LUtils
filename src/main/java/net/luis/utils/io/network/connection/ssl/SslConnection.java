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
import net.luis.utils.io.network.connection.*;
import net.luis.utils.io.network.connection.context.ConnectionContext;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.Objects;

/**
 * Represents an active SSL/TLS connection from a client to an {@link SslServer}.<br>
 * This class wraps an {@link SSLSocket} and provides convenient send/receive operations.<br>
 * <p>
 *     Instances of this class are created by {@link SslServer} when clients connect and are passed to the message handler for processing.
 * </p>
 * <p>
 *     Example usage in a message handler:
 * </p>
 * <pre>{@code
 * SslServerConfig config = SslServerConfig.builder(sslContext)
 *     .onMessage((server, connection, data) -> {
 *         System.out.println("From " + connection.remoteEndpoint() + ": " + new String(data));
 *         connection.send("Response".getBytes());
 *     })
 *     .build();
 * }</pre>
 *
 * @see SslServer
 * @see SslServerConfig
 *
 * @author Luis-St
 */
public final class SslConnection implements Connection {
	
	/**
	 * The underlying client SSL socket.<br>
	 */
	private final SSLSocket socket;
	/**
	 * The buffer size for read operations.<br>
	 */
	private final int bufferSize;
	/**
	 * The read timeout for blocking operations.<br>
	 */
	private final Duration readTimeout;
	/**
	 * Whether messages are framed with a length prefix on the wire.<br>
	 */
	private final boolean framing;
	/**
	 * The reusable scratch buffer for unframed read operations.<br>
	 * Allocated lazily and grown on demand, and unused while framing is enabled.<br>
	 * The context storing user data attached to this connection.<br>
	 */
	private final ConnectionContext context = new ConnectionContext();
	/**
	 * The streams of the socket, so that every caller gets the same instance.<br>
	 */
	private final SocketStreams streams;
	/**
	 * The reusable scratch buffer for read operations.<br>
	 * Allocated lazily and grown on demand, so that repeated receives do not allocate a new buffer each time.<br>
	 */
	private byte @Nullable [] readBuffer;
	
	/**
	 * Constructs a new SSL connection wrapping the given socket.<br>
	 *
	 * @param socket The client SSL socket
	 * @param bufferSize The buffer size for read operations
	 * @param framing Whether messages are framed with a length prefix on the wire
	 * @param readTimeout The read timeout
	 * @throws NullPointerException If socket or read timeout is null
	 */
	SslConnection(@NonNull SSLSocket socket, int bufferSize, boolean framing, @NonNull Duration readTimeout) {
		this.socket = Objects.requireNonNull(socket, "Socket must not be null");
		this.streams = new SocketStreams(socket);
		this.bufferSize = bufferSize;
		this.framing = framing;
		this.readTimeout = Objects.requireNonNull(readTimeout, "Read timeout must not be null");
	}
	
	/**
	 * Layers TLS over an already connected plaintext socket in server mode and returns the secured connection.<br>
	 * <p>
	 *     This is how a server that accepted a connection as plaintext switches it to TLS, for example after it detected a TLS handshake on a port shared with plaintext.<br>
	 *     The given consumed bytes are the input that was already read from the socket, they are processed before any further input of the socket.<br>
	 *     The enabled protocols, the enabled cipher suites, the client authentication mode and the client socket options are taken from the given configuration.<br>
	 *     The TLS handshake is not started here, it is performed by the server that serves the connection or implicitly on the first read or write.
	 * </p>
	 * <p>
	 *     The returned connection owns the socket, so closing it closes the underlying socket.<br>
	 *     If the upgrade fails, ownership stays with the caller, which is responsible for closing the socket.
	 * </p>
	 *
	 * @param socket The connected plaintext socket to upgrade
	 * @param consumed The input that was already read from the socket
	 * @param config The configuration of the secured connection
	 * @return The secured connection
	 * @throws NullPointerException If socket, consumed, or config is null
	 * @throws NetworkConnectionException If the socket is not connected or cannot be layered with TLS
	 * @see SslServer#serve(SslConnection)
	 */
	public static @NonNull SslConnection upgrade(@NonNull Socket socket, byte @NonNull [] consumed, @NonNull SslServerConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(socket, "Socket must not be null");
		Objects.requireNonNull(consumed, "Consumed data must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		if (socket.isClosed() || !socket.isConnected()) {
			throw new NetworkConnectionException("Socket is not connected", NetworkErrorType.NOT_CONNECTED);
		}
		
		IpEndpoint endpoint = IpEndpoint.from((InetSocketAddress) socket.getRemoteSocketAddress());
		try {
			SSLSocket sslSocket = (SSLSocket) config.sslContext().getSocketFactory().createSocket(socket, new ByteArrayInputStream(consumed), true);
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
			
			sslSocket.setTcpNoDelay(config.tcpNoDelay());
			sslSocket.setKeepAlive(config.keepAlive());
			sslSocket.setSoTimeout((int) Math.min(Integer.MAX_VALUE, config.clientReadTimeout().toMillis()));
			return new SslConnection(sslSocket, config.clientBufferSize(), config.framing(), config.clientReadTimeout());
		} catch (IOException e) {
			throw new NetworkConnectionException("Failed to upgrade connection to TLS", e, NetworkErrorType.IO_ERROR, endpoint);
		}
	}
	
	/**
	 * Ensures that this connection is still active before its streams are handed out.<br>
	 * @throws NetworkConnectionException If the connection is closed
	 */
	private void ensureActive() throws NetworkConnectionException {
		if (!this.isActive()) {
			throw new NetworkConnectionException("Connection is closed", NetworkErrorType.SOCKET_CLOSED);
		}
	}
	
	/**
	 * Performs the TLS handshake on this connection (blocking).<br>
	 * This method is called by the server before any data is exchanged to ensure the secure
	 * channel is established and to surface handshake failures early.<br>
	 *
	 * @throws NetworkConnectionException If the handshake fails
	 */
	void startHandshake() throws NetworkConnectionException {
		try {
			this.socket.startHandshake();
		} catch (SSLHandshakeException e) {
			throw new NetworkConnectionException("SSL handshake failed", e, NetworkErrorType.HANDSHAKE_FAILED, this.remoteEndpoint());
		} catch (IOException e) {
			throw new NetworkConnectionException("Failed during SSL handshake", e, NetworkErrorType.IO_ERROR, this.remoteEndpoint());
		}
	}
	
	@Override
	public boolean isActive() {
		return !this.socket.isClosed() && this.socket.isConnected();
	}
	
	/**
	 * Returns the TLS session associated with this connection.<br>
	 * The session provides details such as the negotiated protocol, cipher suite, and peer certificates.<br>
	 *
	 * @return The SSL session
	 */
	public @NonNull SSLSession getSession() {
		return this.socket.getSession();
	}
	
	@Override
	public @NonNull ConnectionContext context() {
		return this.context;
	}
	
	@Override
	public @NonNull IpEndpoint remoteEndpoint() {
		return IpEndpoint.from((InetSocketAddress) this.socket.getRemoteSocketAddress());
	}
	
	@Override
	public @NonNull IpEndpoint localEndpoint() {
		return IpEndpoint.from((InetSocketAddress) this.socket.getLocalSocketAddress());
	}
	
	@Override
	public void send(byte @NonNull [] data) throws NetworkConnectionException {
		Objects.requireNonNull(data, "Data must not be null");
		NetworkUtils.validateMessageSize(data, this.bufferSize, this.remoteEndpoint());
		if (!this.isActive()) {
			throw new NetworkConnectionException("Connection is closed", NetworkErrorType.SOCKET_CLOSED, this.remoteEndpoint());
		}
		
		NetworkUtils.writeAll(this.socket, data, this.framing, null, this.remoteEndpoint(), null);
	}
	
	@Override
	public byte @NonNull [] receive() throws NetworkConnectionException {
		return this.receive(this.bufferSize);
	}
	
	@Override
	public byte @NonNull [] receive(int maxBytes) throws NetworkConnectionException {
		if (maxBytes < 1) {
			throw new IllegalArgumentException("Max bytes must be at least 1: " + maxBytes);
		}
		if (!this.isActive()) {
			throw new NetworkConnectionException("Connection is closed", NetworkErrorType.SOCKET_CLOSED, this.remoteEndpoint());
		}
		
		if (!this.framing) {
			this.readBuffer = NetworkUtils.resizeBuffer(this.readBuffer, maxBytes);
		}
		return NetworkUtils.readMessage(this.streams.input(), this.readBuffer, maxBytes, this.framing, this.readTimeout, null, this.remoteEndpoint(), null);
	}
	
	@Override
	public @NonNull InputStream getInputStream() throws NetworkConnectionException {
		this.ensureActive();
		return this.streams.input();
	}
	
	@Override
	public @NonNull OutputStream getOutputStream() throws NetworkConnectionException {
		this.ensureActive();
		return this.streams.output();
	}
	
	@Override
	public void close() {
		if (!this.socket.isClosed()) {
			try {
				this.socket.close();
			} catch (IOException _) {}
		}
	}
}
