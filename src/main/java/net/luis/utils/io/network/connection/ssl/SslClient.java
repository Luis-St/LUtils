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
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.exception.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * A blocking SSL/TLS client for establishing secure connections to remote servers.<br>
 * This class provides a simple blocking API for TLS communication,<br>
 * mirroring the plain TCP client but performing a TLS handshake and optional hostname verification on connect.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IpEndpoint server = new IpEndpoint(Ipv4Address.LOOPBACK, 8443);
 * SslClientConfig config = SslClientConfig.builder()
 *     .connectTimeout(Duration.ofSeconds(10))
 *     .verifyHostname(true)
 *     .onConnect(event -> System.out.println("Secure connection established!"))
 *     .build();
 *
 * try (SslClient client = new SslClient(config)) {
 *     client.connect(server);
 *     client.send("Hello, Server!".getBytes());
 *     byte[] response = client.receive();
 *     System.out.println("Response: " + new String(response));
 * }
 * }</pre>
 *
 * @see SslClientConfig
 *
 * @author Luis-St
 */
public final class SslClient implements NetworkClient {
	
	/**
	 * The configuration for this client.<br>
	 */
	private final SslClientConfig config;
	/**
	 * The underlying SSL socket for communication.<br>
	 */
	private volatile SSLSocket socket;
	/**
	 * Whether this client is currently connected.<br>
	 */
	private volatile boolean connected;
	
	/**
	 * Constructs a new SSL client with default configuration.<br>
	 */
	public SslClient() {
		this(SslClientConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new SSL client with the specified configuration.<br>
	 *
	 * @param config The client configuration
	 * @throws NullPointerException If config is null
	 */
	public SslClient(@NonNull SslClientConfig config) {
		this.config = Objects.requireNonNull(config, "Config must not be null");
	}
	
	/**
	 * Creates a new SSL client with default configuration and connects it to the specified remote endpoint.<br>
	 *
	 * @param endpoint The remote endpoint to connect to
	 * @return The connected client
	 * @throws NullPointerException If endpoint is null
	 * @throws NetworkConnectionException If connection or the TLS handshake fails
	 * @throws NetworkTimeoutException If connection times out
	 */
	public static @NonNull SslClient connectTo(@NonNull IpEndpoint endpoint) throws NetworkConnectionException {
		return connectTo(endpoint, SslClientConfig.DEFAULT);
	}
	
	/**
	 * Creates a new SSL client with the specified configuration and connects it to the specified remote endpoint.<br>
	 * If the connection fails, the client is closed before the exception is propagated.<br>
	 *
	 * @param endpoint The remote endpoint to connect to
	 * @param config The client configuration
	 * @return The connected client
	 * @throws NullPointerException If endpoint or config is null
	 * @throws NetworkConnectionException If connection or the TLS handshake fails
	 * @throws NetworkTimeoutException If connection times out
	 */
	public static @NonNull SslClient connectTo(@NonNull IpEndpoint endpoint, @NonNull SslClientConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		
		SslClient client = new SslClient(config);
		try {
			client.connect(endpoint);
			return client;
		} catch (NetworkConnectionException e) {
			client.close();
			throw e;
		}
	}
	
	/**
	 * Connects to the specified remote endpoint and performs the TLS handshake.<br>
	 *
	 * @param endpoint The remote endpoint to connect to
	 * @throws NullPointerException If endpoint is null
	 * @throws NetworkConnectionException If connection or the TLS handshake fails
	 * @throws NetworkTimeoutException If connection times out
	 */
	public void connect(@NonNull IpEndpoint endpoint) throws NetworkConnectionException {
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		if (this.connected) {
			throw new NetworkConnectionException("Client is already connected", NetworkErrorType.ALREADY_CONNECTED, endpoint);
		}
		
		try {
			SSLContext context = this.config.resolveSslContext();
			SSLSocket sslSocket = (SSLSocket) context.getSocketFactory().createSocket();
			this.socket = sslSocket;
			
			sslSocket.setTcpNoDelay(this.config.tcpNoDelay());
			sslSocket.setKeepAlive(this.config.keepAlive());
			if (!this.config.readTimeout().isZero()) {
				sslSocket.setSoTimeout((int) this.config.readTimeout().toMillis());
			}
			if (!this.config.enabledProtocols().isEmpty()) {
				sslSocket.setEnabledProtocols(this.config.enabledProtocols().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
			}
			if (!this.config.enabledCipherSuites().isEmpty()) {
				sslSocket.setEnabledCipherSuites(this.config.enabledCipherSuites().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
			}
			if (this.config.verifyHostname()) {
				SSLParameters parameters = sslSocket.getSSLParameters();
				parameters.setEndpointIdentificationAlgorithm("HTTPS");
				sslSocket.setSSLParameters(parameters);
			}
			
			sslSocket.connect(endpoint.toInetSocketAddress(), (int) this.config.connectTimeout().toMillis());
			sslSocket.startHandshake();
			this.connected = true;
			
			if (this.config.onConnect() != null) {
				this.config.onConnect().handle(null, this.localEndpoint().orElse(endpoint), endpoint, Instant.now());
			}
		} catch (SocketTimeoutException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.CONNECTION_TIMEOUT, "Connection timed out to " + endpoint, e);
			throw new NetworkTimeoutException("Connection timed out to " + endpoint, NetworkErrorType.CONNECTION_TIMEOUT, this.config.connectTimeout(), endpoint);
		} catch (SSLHandshakeException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.HANDSHAKE_FAILED, "SSL handshake failed with " + endpoint, e);
			throw new NetworkConnectionException("SSL handshake failed with " + endpoint, e, NetworkErrorType.HANDSHAKE_FAILED, endpoint);
		} catch (ConnectException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.CONNECTION_REFUSED, "Connection refused by " + endpoint, e);
			throw new NetworkConnectionException("Connection refused by " + endpoint, e, NetworkErrorType.CONNECTION_REFUSED, endpoint);
		} catch (NoRouteToHostException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.HOST_UNREACHABLE, "Host unreachable: " + endpoint, e);
			throw new NetworkConnectionException("Host unreachable: " + endpoint, e, NetworkErrorType.HOST_UNREACHABLE, endpoint);
		} catch (NoSuchAlgorithmException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.CONNECTION_FAILED, "Failed to initialize SSL context", e);
			throw new NetworkConnectionException("Failed to initialize SSL context", e, NetworkErrorType.CONNECTION_FAILED, endpoint);
		} catch (IOException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.CONNECTION_FAILED, "Failed to connect to " + endpoint, e);
			throw new NetworkConnectionException("Failed to connect to " + endpoint, e, NetworkErrorType.CONNECTION_FAILED, endpoint);
		}
	}
	
	/**
	 * Returns the TLS session for this connection.<br>
	 * The session provides details such as the negotiated protocol, cipher suite, and peer certificates.<br>
	 *
	 * @return The SSL session
	 * @throws NetworkConnectionException If the client is not connected
	 */
	public @NonNull SSLSession getSession() throws NetworkConnectionException {
		this.ensureConnected();
		return this.socket.getSession();
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
			NetworkUtils.writeFrame(out, data);
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
			byte[] data = NetworkUtils.readFrame(in, maxBytes);
			
			if (data == null) {
				this.handleDisconnect();
				return ArrayUtils.EMPTY_BYTE_ARRAY;
			}
			
			return data;
		} catch (FrameTooLargeException e) {
			throw new NetworkConnectionException(e.getMessage(), e, NetworkErrorType.MESSAGE_TOO_LARGE);
		} catch (EOFException e) {
			this.handleDisconnect();
			throw new NetworkConnectionException("Connection reset while receiving data", e, NetworkErrorType.CONNECTION_RESET);
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
					this.config.onDisconnect().handle(null, local, remote, Instant.now());
				}
			} catch (Exception _) {}
		}
		this.connected = false;
	}
	//endregion
}
