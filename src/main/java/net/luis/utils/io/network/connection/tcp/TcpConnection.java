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
import net.luis.utils.io.network.connection.*;
import net.luis.utils.io.network.connection.context.ConnectionContext;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.ssl.SslConnection;
import net.luis.utils.io.network.connection.ssl.SslServerConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.Objects;

/**
 * Represents an active TCP connection from a client to a server.<br>
 * This class wraps a client socket and provides convenient send/receive operations.<br>
 * <p>
 *     Instances of this class are created by {@link TcpServer} when clients connect and are passed to the message handler for processing.
 * </p>
 * <p>
 *     A connection can be switched to TLS on the server side through {@link #upgrade(SslServerConfig)}, which yields an {@link SslConnection} sharing the same socket.
 * </p>
 * <p>
 *     Example usage in a message handler:
 * </p>
 * <pre>{@code
 * TcpServerConfig config = TcpServerConfig.builder().onMessage((server, connection, data) -> {
 *     System.out.println("From " + connection.remoteEndpoint() + ": " + new String(data));
 *     connection.send("Response".getBytes());
 * }).build();
 * }</pre>
 *
 * @see TcpServer
 * @see TcpServerConfig
 *
 * @author Luis-St
 */
public final class TcpConnection implements Connection {
	
	/**
	 * The underlying client socket.<br>
	 */
	private final Socket socket;
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
	 * Whether this connection was upgraded to TLS, after which it no longer counts as a plain TCP client.<br>
	 */
	private volatile boolean upgraded;
	
	/**
	 * Constructs a new TCP connection wrapping the given socket.<br>
	 *
	 * @param socket The client socket
	 * @param bufferSize The buffer size for read operations
	 * @param framing Whether messages are framed with a length prefix on the wire
	 * @param readTimeout The read timeout
	 * @throws NullPointerException If socket or read timeout is null
	 */
	TcpConnection(@NonNull Socket socket, int bufferSize, boolean framing, @NonNull Duration readTimeout) {
		this.socket = Objects.requireNonNull(socket, "Socket must not be null");
		this.streams = new SocketStreams(socket);
		this.bufferSize = bufferSize;
		this.framing = framing;
		this.readTimeout = Objects.requireNonNull(readTimeout, "Read timeout must not be null");
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
	
	@Override
	public boolean isActive() {
		return !this.socket.isClosed() && this.socket.isConnected();
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
	
	/**
	 * Layers TLS over this connection in server mode and returns the secured connection.<br>
	 * <p>
	 *     Input that was already read ahead, such as a peeked first byte of the handshake, is passed on to the secured connection.<br>
	 *     The TLS handshake is not started here, it is performed by the server that serves the secured connection or implicitly on its first read or write.
	 * </p>
	 * <p>
	 *     Both connections share the underlying socket, so closing either of them closes it.<br>
	 *     This connection must not be used to send or receive data afterward.<br>
	 *     It is no longer counted or broadcast to by the {@link TcpServer} that accepted it.
	 * </p>
	 *
	 * @param config The configuration of the secured connection
	 * @return The secured connection
	 * @throws NullPointerException If config is null
	 * @throws NetworkConnectionException If the connection is closed or cannot be layered with TLS
	 * @see SslConnection#upgrade(Socket, byte[], SslServerConfig)
	 */
	public @NonNull SslConnection upgrade(@NonNull SslServerConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(config, "Config must not be null");
		this.ensureActive();
		
		InputStream input = this.streams.input();
		byte[] consumed;
		try {
			consumed = input.readNBytes(input.available());
		} catch (IOException e) {
			throw new NetworkConnectionException("Failed to read pending input", e, NetworkErrorType.IO_ERROR, this.remoteEndpoint());
		}
		
		SslConnection secured = SslConnection.upgrade(this.socket, consumed, config);
		this.upgraded = true;
		return secured;
	}
	
	/**
	 * Returns whether this connection was upgraded to TLS through {@link #upgrade(SslServerConfig)}.<br>
	 * @return True if this connection was upgraded, false otherwise
	 */
	boolean isUpgraded() {
		return this.upgraded;
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
