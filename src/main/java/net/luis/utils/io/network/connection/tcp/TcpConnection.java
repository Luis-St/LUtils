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
import net.luis.utils.io.network.connection.Connection;
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.exception.*;
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
 *     Example usage in a message handler:
 * </p>
 * <pre>{@code
 * TcpServerConfig config = TcpServerConfig.builder()
 *     .onMessage((server, connection, data) -> {
 *         System.out.println("From " + connection.remoteEndpoint() + ": " + new String(data));
 *         connection.send("Response".getBytes());
 *     })
 *     .build();
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
	 * The reusable scratch buffer for read operations.<br>
	 * Allocated lazily and grown on demand, so that repeated receives do not allocate a new buffer each time.<br>
	 */
	private byte @Nullable [] readBuffer;
	
	/**
	 * Constructs a new TCP connection wrapping the given socket.<br>
	 *
	 * @param socket The client socket
	 * @param bufferSize The buffer size for read operations
	 * @param readTimeout The read timeout
	 * @throws NullPointerException If socket or read timeout is null
	 */
	TcpConnection(@NonNull Socket socket, int bufferSize, @NonNull Duration readTimeout) {
		this.socket = Objects.requireNonNull(socket, "Socket must not be null");
		this.bufferSize = bufferSize;
		this.readTimeout = Objects.requireNonNull(readTimeout, "Read timeout must not be null");
	}
	
	@Override
	public boolean isActive() {
		return !this.socket.isClosed() && this.socket.isConnected();
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
		
		NetworkUtils.writeAll(this.socket, data, null, this.remoteEndpoint(), null);
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
		
		this.readBuffer = NetworkUtils.resizeBuffer(this.readBuffer, maxBytes);
		return NetworkUtils.readAvailable(this.socket, this.readBuffer, maxBytes, this.readTimeout, null, this.remoteEndpoint(), null);
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
