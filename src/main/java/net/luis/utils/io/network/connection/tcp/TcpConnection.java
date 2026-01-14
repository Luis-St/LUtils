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
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.exception.NetworkTimeoutException;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.time.Duration;
import java.util.Objects;

/**
 * Represents an active TCP connection from a client to a server.<br>
 * This class wraps a client socket and provides convenient send/receive operations.<br>
 * <p>
 *     Instances of this class are created by {@link TcpServer} when clients connect
 *     and are passed to the message handler for processing.
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
public final class TcpConnection implements AutoCloseable {

	private final @NonNull Socket socket;
	private final int bufferSize;
	private final @NonNull Duration readTimeout;

	/**
	 * Constructs a new TCP connection wrapping the given socket.<br>
	 *
	 * @param socket The client socket
	 * @param bufferSize The buffer size for read operations
	 * @param readTimeout The read timeout
	 * @throws NullPointerException If socket or readTimeout is null
	 */
	TcpConnection(@NonNull Socket socket, int bufferSize, @NonNull Duration readTimeout) {
		this.socket = Objects.requireNonNull(socket, "Socket must not be null");
		this.bufferSize = bufferSize;
		this.readTimeout = Objects.requireNonNull(readTimeout, "Read timeout must not be null");
	}

	/**
	 * Returns the remote endpoint of this connection.<br>
	 * @return The remote endpoint
	 */
	public @NonNull IpEndpoint remoteEndpoint() {
		InetSocketAddress address = (InetSocketAddress) this.socket.getRemoteSocketAddress();
		return this.createEndpoint(address);
	}

	/**
	 * Returns the local endpoint of this connection.<br>
	 * @return The local endpoint
	 */
	public @NonNull IpEndpoint localEndpoint() {
		InetSocketAddress address = (InetSocketAddress) this.socket.getLocalSocketAddress();
		return this.createEndpoint(address);
	}

	/**
	 * Sends data to the connected client.<br>
	 *
	 * @param data The data to send
	 * @throws NullPointerException If data is null
	 * @throws NetworkConnectionException If sending fails
	 */
	public void send(byte @NonNull [] data) throws NetworkConnectionException {
		Objects.requireNonNull(data, "Data must not be null");

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
				return ArrayUtils.EMPTY_BYTE_ARRAY; // Connection closed
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
			} catch (IOException ignored) {
				// Ignore close errors
			}
		}
	}

	private @NonNull IpEndpoint createEndpoint(@NonNull InetSocketAddress address) {
		IpAddress<?> ipAddress;
		if (address.getAddress() instanceof Inet4Address inet4) {
			ipAddress = Ipv4Address.from(inet4);
		} else {
			ipAddress = Ipv6Address.from((Inet6Address) address.getAddress());
		}
		return new IpEndpoint(ipAddress, address.getPort());
	}
}
