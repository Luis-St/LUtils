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
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.event.ConnectionEvent;
import net.luis.utils.io.network.connection.exception.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.Optional;

/**
 * A blocking TCP client for establishing connections to remote servers.<br>
 * This class provides a simple blocking API for TCP communication.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IpEndpoint server = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
 * TcpClientConfig config = TcpClientConfig.builder()
 *     .connectTimeout(Duration.ofSeconds(10))
 *     .onConnect(event -> System.out.println("Connected!"))
 *     .build();
 *
 * try (TcpClient client = new TcpClient(config)) {
 *     client.connect(server);
 *     client.send("Hello, Server!".getBytes());
 *     byte[] response = client.receive();
 *     System.out.println("Response: " + new String(response));
 * }
 * }</pre>
 *
 * @see TcpClientConfig
 *
 * @author Luis-St
 */
public final class TcpClient implements NetworkClient {
	
	/**
	 * The configuration for this client.<br>
	 */
	private final TcpClientConfig config;
	/**
	 * The underlying socket for communication.<br>
	 */
	private volatile Socket socket;
	/**
	 * Whether this client is currently connected.<br>
	 */
	private volatile boolean connected;
	
	/**
	 * Constructs a new TCP client with default configuration.<br>
	 */
	public TcpClient() {
		this(TcpClientConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new TCP client with the specified configuration.<br>
	 *
	 * @param config The client configuration
	 * @throws NullPointerException If config is null
	 */
	public TcpClient(@NonNull TcpClientConfig config) {
		this.config = Objects.requireNonNull(config, "Config must not be null");
	}
	
	/**
	 * Connects to the specified remote endpoint.<br>
	 *
	 * @param endpoint The remote endpoint to connect to
	 * @throws NullPointerException If endpoint is null
	 * @throws NetworkConnectionException If connection fails
	 * @throws NetworkTimeoutException If connection times out
	 */
	public void connect(@NonNull IpEndpoint endpoint) throws NetworkConnectionException {
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		if (this.connected) {
			throw new NetworkConnectionException("Client is already connected", NetworkErrorType.ALREADY_CONNECTED, endpoint);
		}
		
		try {
			this.socket = new Socket();
			this.socket.setTcpNoDelay(this.config.tcpNoDelay());
			this.socket.setKeepAlive(this.config.keepAlive());
			
			if (!this.config.readTimeout().isZero()) {
				this.socket.setSoTimeout((int) this.config.readTimeout().toMillis());
			}
			
			this.socket.connect(endpoint.toInetSocketAddress(), (int) this.config.connectTimeout().toMillis());
			this.connected = true;
			
			if (this.config.onConnect() != null) {
				ConnectionEvent event = ConnectionEvent.now(this.localEndpoint().orElse(endpoint), endpoint);
				this.config.onConnect().handle(event);
			}
		} catch (SocketTimeoutException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.CONNECTION_TIMEOUT, "Connection timed out to " + endpoint, e);
			throw new NetworkTimeoutException("Connection timed out to " + endpoint, NetworkErrorType.CONNECTION_TIMEOUT, this.config.connectTimeout(), endpoint);
		} catch (ConnectException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.CONNECTION_REFUSED, "Connection refused by " + endpoint, e);
			throw new NetworkConnectionException("Connection refused by " + endpoint, e, NetworkErrorType.CONNECTION_REFUSED, endpoint);
		} catch (NoRouteToHostException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.HOST_UNREACHABLE, "Host unreachable: " + endpoint, e);
			throw new NetworkConnectionException("Host unreachable: " + endpoint, e, NetworkErrorType.HOST_UNREACHABLE, endpoint);
		} catch (IOException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.CONNECTION_FAILED, "Failed to connect to " + endpoint, e);
			throw new NetworkConnectionException("Failed to connect to " + endpoint, e, NetworkErrorType.CONNECTION_FAILED, endpoint);
		}
	}
	
	/**
	 * Sends data to the connected server.<br>
	 *
	 * @param data The data to send
	 * @throws NullPointerException If data is null
	 * @throws NetworkConnectionException If sending fails or data exceeds buffer size
	 */
	public void send(byte @NonNull [] data) throws NetworkConnectionException {
		Objects.requireNonNull(data, "Data must not be null");
		this.validateMessageSize(data);
		this.ensureConnected();
		
		try {
			OutputStream out = this.socket.getOutputStream();
			out.write(data);
			out.flush();
		} catch (SocketException e) {
			this.handleDisconnect();
			throw new NetworkConnectionException("Connection reset", e, NetworkErrorType.CONNECTION_RESET);
		} catch (IOException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Failed to send data", e);
			throw new NetworkConnectionException("Failed to send data", e, NetworkErrorType.IO_ERROR);
		}
	}
	
	/**
	 * Receives data from the connected server (blocking).<br>
	 * Uses the buffer size from the configuration.<br>
	 *
	 * @return The received data, or an empty array if the connection was closed
	 * @throws NetworkConnectionException If receiving fails
	 * @throws NetworkTimeoutException If the receive times out
	 */
	public byte @NonNull [] receive() throws NetworkConnectionException {
		return this.receive(this.config.bufferSize());
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
		this.ensureConnected();
		
		try {
			InputStream in = this.socket.getInputStream();
			byte[] buffer = new byte[maxBytes];
			int bytesRead = in.read(buffer);
			
			if (bytesRead == -1) {
				this.handleDisconnect();
				return ArrayUtils.EMPTY_BYTE_ARRAY;
			}
			
			byte[] data = new byte[bytesRead];
			System.arraycopy(buffer, 0, data, 0, bytesRead);
			return data;
		} catch (SocketTimeoutException e) {
			throw new NetworkTimeoutException("Read timed out", NetworkErrorType.READ_TIMEOUT, this.config.readTimeout());
		} catch (SocketException e) {
			this.handleDisconnect();
			throw new NetworkConnectionException("Connection reset", e, NetworkErrorType.CONNECTION_RESET);
		} catch (IOException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Failed to receive data", e);
			throw new NetworkConnectionException("Failed to receive data", e, NetworkErrorType.IO_ERROR);
		}
	}
	
	/**
	 * Returns the input stream for advanced reading.<br>
	 *
	 * @return The input stream
	 * @throws NetworkConnectionException If the stream cannot be obtained
	 */
	public @NonNull InputStream getInputStream() throws NetworkConnectionException {
		this.ensureConnected();
		
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
		this.ensureConnected();
		
		try {
			return this.socket.getOutputStream();
		} catch (IOException e) {
			throw new NetworkConnectionException("Failed to get output stream", e, NetworkErrorType.IO_ERROR);
		}
	}
	
	@Override
	public boolean isActive() {
		return this.connected && this.socket != null && !this.socket.isClosed() && this.socket.isConnected();
	}
	
	@Override
	public @NonNull Optional<IpEndpoint> localEndpoint() {
		if (!this.isActive()) {
			return Optional.empty();
		}
		
		InetSocketAddress address = (InetSocketAddress) this.socket.getLocalSocketAddress();
		return Optional.of(IpEndpoint.from(address));
	}
	
	/**
	 * Returns the remote endpoint this client is connected to.<br>
	 * @return The remote endpoint, or empty if not connected
	 */
	public @NonNull Optional<IpEndpoint> remoteEndpoint() {
		if (!this.isActive()) {
			return Optional.empty();
		}
		
		InetSocketAddress address = (InetSocketAddress) this.socket.getRemoteSocketAddress();
		return Optional.of(IpEndpoint.from(address));
	}
	
	@Override
	public void close() {
		if (this.socket != null && !this.socket.isClosed()) {
			this.handleDisconnect();
			
			try {
				this.socket.close();
			} catch (IOException _) {}
		}
		this.connected = false;
	}
	
	//region Helper methods
	
	/**
	 * Validates that the message size does not exceed the configured buffer size.<br>
	 *
	 * @param data The data to validate
	 * @throws NetworkConnectionException If the data exceeds the buffer size
	 */
	private void validateMessageSize(byte @NonNull [] data) throws NetworkConnectionException {
		if (data.length > this.config.bufferSize()) {
			throw new NetworkConnectionException("Message size " + data.length + " exceeds buffer size " + this.config.bufferSize(), NetworkErrorType.MESSAGE_TOO_LARGE);
		}
	}
	
	/**
	 * Ensures that the client is connected before performing operations.<br>
	 * @throws NetworkConnectionException If the client is not connected
	 */
	private void ensureConnected() throws NetworkConnectionException {
		if (!this.connected || this.socket == null || this.socket.isClosed()) {
			throw new NetworkConnectionException("Client is not connected", NetworkErrorType.NOT_CONNECTED);
		}
	}
	
	/**
	 * Handles the disconnection event by notifying the configured handler.<br>
	 * This method is called when the connection is closed or reset.<br>
	 */
	private void handleDisconnect() {
		if (this.connected && this.config.onDisconnect() != null) {
			try {
				IpEndpoint local = this.localEndpoint().orElse(null);
				IpEndpoint remote = this.remoteEndpoint().orElse(null);
				if (local != null && remote != null) {
					ConnectionEvent event = ConnectionEvent.now(local, remote);
					this.config.onDisconnect().handle(event);
				}
			} catch (Exception _) {}
		}
		this.connected = false;
	}
	//endregion
}
