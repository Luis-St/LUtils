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
import net.luis.utils.io.network.connection.exception.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
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
 * SSLServerConfig config = SSLServerConfig.builder(sslContext)
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
public final class SslConnection implements AutoCloseable {
	
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
	 * Constructs a new SSL connection wrapping the given socket.<br>
	 *
	 * @param socket The client SSL socket
	 * @param bufferSize The buffer size for read operations
	 * @param readTimeout The read timeout
	 * @throws NullPointerException If socket or read timeout is null
	 */
	SslConnection(@NonNull SSLSocket socket, int bufferSize, @NonNull Duration readTimeout) {
		this.socket = Objects.requireNonNull(socket, "Socket must not be null");
		this.bufferSize = bufferSize;
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
	
	/**
	 * Returns the TLS session associated with this connection.<br>
	 * The session provides details such as the negotiated protocol, cipher suite, and peer certificates.<br>
	 *
	 * @return The SSL session
	 */
	public @NonNull SSLSession getSession() {
		return this.socket.getSession();
	}
	
	/**
	 * Returns the remote endpoint of this connection.<br>
	 * @return The remote endpoint
	 */
	public @NonNull IpEndpoint remoteEndpoint() {
		InetSocketAddress address = (InetSocketAddress) this.socket.getRemoteSocketAddress();
		return IpEndpoint.from(address);
	}
	
	/**
	 * Returns the local endpoint of this connection.<br>
	 * @return The local endpoint
	 */
	public @NonNull IpEndpoint localEndpoint() {
		InetSocketAddress address = (InetSocketAddress) this.socket.getLocalSocketAddress();
		return IpEndpoint.from(address);
	}
	
	/**
	 * Sends data to the connected client.<br>
	 *
	 * @param data The data to send
	 * @throws NullPointerException If data is null
	 * @throws NetworkConnectionException If sending fails or data exceeds buffer size
	 */
	public void send(byte @NonNull [] data) throws NetworkConnectionException {
		Objects.requireNonNull(data, "Data must not be null");
		this.validateMessageSize(data);
		if (!this.isActive()) {
			throw new NetworkConnectionException("Connection is closed", NetworkErrorType.SOCKET_CLOSED, this.remoteEndpoint());
		}
		
		try {
			OutputStream out = this.socket.getOutputStream();
			out.write(data);
			out.flush();
		} catch (SocketException e) {
			throw new NetworkConnectionException("Connection reset", e, NetworkErrorType.CONNECTION_RESET, this.remoteEndpoint());
		} catch (IOException e) {
			throw new NetworkConnectionException("Failed to send data", e, NetworkErrorType.IO_ERROR, this.remoteEndpoint());
		}
	}
	
	/**
	 * Receives data from the connected client (blocking).<br>
	 * Uses the configured buffer size.<br>
	 *
	 * @return The received data, or an empty array if the connection was closed
	 * @throws NetworkConnectionException If receiving fails
	 * @throws NetworkTimeoutException If the receive times out
	 */
	public byte @NonNull [] receive() throws NetworkConnectionException {
		return this.receive(this.bufferSize);
	}
	
	/**
	 * Receives data with a custom buffer size (blocking).<br>
	 *
	 * @param maxBytes The maximum number of bytes to receive
	 * @return The received data, or an empty array if the connection was closed
	 * @throws IllegalArgumentException If maxBytes is less than 1
	 * @throws NetworkConnectionException If receiving fails
	 * @throws NetworkTimeoutException If the receive times out
	 */
	public byte @NonNull [] receive(int maxBytes) throws NetworkConnectionException {
		if (maxBytes < 1) {
			throw new IllegalArgumentException("Max bytes must be at least 1: " + maxBytes);
		}
		
		if (!this.isActive()) {
			throw new NetworkConnectionException("Connection is closed", NetworkErrorType.SOCKET_CLOSED, this.remoteEndpoint());
		}
		
		try {
			InputStream in = this.socket.getInputStream();
			byte[] buffer = new byte[maxBytes];
			int bytesRead = in.read(buffer);
			
			if (bytesRead == -1) {
				return ArrayUtils.EMPTY_BYTE_ARRAY;
			}
			
			byte[] data = new byte[bytesRead];
			System.arraycopy(buffer, 0, data, 0, bytesRead);
			return data;
		} catch (SocketTimeoutException e) {
			throw new NetworkTimeoutException("Read timed out", NetworkErrorType.READ_TIMEOUT, this.readTimeout, this.remoteEndpoint());
		} catch (SocketException e) {
			throw new NetworkConnectionException("Connection reset", e, NetworkErrorType.CONNECTION_RESET, this.remoteEndpoint());
		} catch (IOException e) {
			throw new NetworkConnectionException("Failed to receive data", e, NetworkErrorType.IO_ERROR, this.remoteEndpoint());
		}
	}
	
	/**
	 * Returns the input stream for advanced reading.<br>
	 *
	 * @return The input stream
	 * @throws NetworkConnectionException If the stream cannot be obtained
	 */
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
	
	/**
	 * Returns the output stream for advanced writing.<br>
	 *
	 * @return The output stream
	 * @throws NetworkConnectionException If the stream cannot be obtained
	 */
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
	
	/**
	 * Returns whether this connection is still active.<br>
	 * @return True if the connection is active
	 */
	public boolean isActive() {
		return !this.socket.isClosed() && this.socket.isConnected();
	}
	
	@Override
	public void close() {
		if (!this.socket.isClosed()) {
			try {
				this.socket.close();
			} catch (IOException _) {}
		}
	}
	
	//region Helper methods
	
	/**
	 * Validates that the message size does not exceed the configured buffer size.<br>
	 *
	 * @param data The data to validate
	 * @throws NetworkConnectionException If the data exceeds the buffer size
	 */
	private void validateMessageSize(byte @NonNull [] data) throws NetworkConnectionException {
		if (data.length > this.bufferSize) {
			throw new NetworkConnectionException("Message size " + data.length + " exceeds buffer size " + this.bufferSize, NetworkErrorType.MESSAGE_TOO_LARGE, this.remoteEndpoint());
		}
	}
	//endregion
}
