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
import net.luis.utils.io.network.connection.Connection;
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.context.ConnectionContext;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
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
	 * The reusable scratch buffer for read operations.<br>
	 * Allocated lazily and grown on demand, so that repeated receives do not allocate a new buffer each time.<br>
	 */
	private byte @Nullable [] readBuffer;
	
	/**
	 * Constructs a new SSL connection wrapping the given socket.<br>
	 *
	 * @param socket The client SSL socket
	 * @param bufferSize The buffer size for read operations
	 * @param readTimeout The read timeout
	 * @throws NullPointerException If socket or read timeout is null
	 */
	SslConnection(@NonNull SSLSocket socket, int bufferSize, boolean framing, @NonNull Duration readTimeout) {
		this.socket = Objects.requireNonNull(socket, "Socket must not be null");
		this.bufferSize = bufferSize;
		this.framing = framing;
		this.readTimeout = Objects.requireNonNull(readTimeout, "Read timeout must not be null");
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
		
		return NetworkUtils.readAvailable(this.socket, this.readBuffer, maxBytes, this.framing, this.readTimeout, null, this.remoteEndpoint(), null);
	}
	
	@Override
	public @NonNull InputStream getInputStream() throws NetworkConnectionException {
		if (!this.isActive()) {
			throw new NetworkConnectionException("Connection is closed", NetworkErrorType.SOCKET_CLOSED);
		}
		
		try {
			return this.socket.getInputStream();
		} catch (IOException e) {
			throw new NetworkConnectionException("Failed to get input stream", e, NetworkErrorType.IO_ERROR);
		}
	}
	
	@Override
	public @NonNull OutputStream getOutputStream() throws NetworkConnectionException {
		if (!this.isActive()) {
			throw new NetworkConnectionException("Connection is closed", NetworkErrorType.SOCKET_CLOSED);
		}
		
		try {
			return this.socket.getOutputStream();
		} catch (IOException e) {
			throw new NetworkConnectionException("Failed to get output stream", e, NetworkErrorType.IO_ERROR);
		}
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
