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
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.exception.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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
public final class SslClient implements NetworkClient<byte[]> {
	
	/**
	 * The configuration for this client.<br>
	 */
	private final SslClientConfig config;
	/**
	 * The reusable scratch buffer for unframed read operations.<br>
	 * Allocated lazily and grown on demand, and unused while framing is enabled.<br>
	 */
	private byte @Nullable [] readBuffer;
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
	public static @NonNull SslClient connectTo(@NonNull Endpoint endpoint) throws NetworkConnectionException {
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
	public static @NonNull SslClient connectTo(@NonNull Endpoint endpoint, @NonNull SslClientConfig config) throws NetworkConnectionException {
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
	 * Layers TLS over an already connected socket and returns a client that owns the secured connection.<br>
	 * <p>
	 *     The socket is wrapped in an {@link SSLSocket} that is configured from the given configuration, afterwards the TLS handshake is performed.<br>
	 *     This is how protocols that negotiate TLS on an established connection, such as {@code STARTTLS}, switch to a secure channel without opening a new connection.
	 * </p>
	 * <p>
	 *     The given endpoint names the peer for server name indication and hostname verification, so it has to be the endpoint the socket was connected to.<br>
	 *     A {@link HostEndpoint} contributes its hostname, an {@link IpEndpoint} its literal address.
	 * </p>
	 * <p>
	 *     On success the returned client owns the socket, so closing the client closes the underlying socket.<br>
	 *     If the upgrade fails, ownership stays with the caller, which is responsible for closing the socket.<br>
	 *     The connect handler of the configuration is not called, because the connection itself was established before the upgrade.
	 * </p>
	 *
	 * @param socket The connected plaintext socket to upgrade
	 * @param endpoint The remote endpoint the socket is connected to
	 * @param config The configuration for the secured connection
	 * @return A client for the secured connection
	 * @throws NullPointerException If socket, endpoint, or config is null
	 * @throws NetworkConnectionException If the socket is not connected or the TLS handshake fails
	 */
	public static @NonNull SslClient upgrade(@NonNull Socket socket, @NonNull Endpoint endpoint, @NonNull SslClientConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(socket, "Socket must not be null");
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		if (socket.isClosed() || !socket.isConnected()) {
			throw new NetworkConnectionException("Socket is not connected", NetworkErrorType.NOT_CONNECTED, endpoint);
		}
		
		SslClient client = new SslClient(config);
		try {
			SSLContext context = config.resolveSslContext();
			SSLSocket sslSocket = (SSLSocket) context.getSocketFactory().createSocket(socket, resolvePeerHost(endpoint), endpoint.port(), true);
			client.socket = sslSocket;
			client.configureSocket(sslSocket);
			
			sslSocket.startHandshake();
			client.connected = true;
			return client;
		} catch (NoSuchAlgorithmException e) {
			NetworkUtils.handleError(config.onError(), NetworkErrorType.CONNECTION_FAILED, "Failed to initialize SSL context", e);
			throw new NetworkConnectionException("Failed to initialize SSL context", e, NetworkErrorType.CONNECTION_FAILED, endpoint);
		} catch (SSLHandshakeException e) {
			NetworkUtils.handleError(config.onError(), NetworkErrorType.HANDSHAKE_FAILED, "SSL handshake failed during upgrade", e);
			throw new NetworkConnectionException("SSL handshake failed during upgrade", e, NetworkErrorType.HANDSHAKE_FAILED, endpoint);
		} catch (IOException e) {
			NetworkUtils.handleError(config.onError(), NetworkErrorType.IO_ERROR, "Failed to upgrade connection to TLS", e);
			throw new NetworkConnectionException("Failed to upgrade connection to TLS", e, NetworkErrorType.IO_ERROR, endpoint);
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
	public void connect(@NonNull Endpoint endpoint) throws NetworkConnectionException {
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		if (this.connected) {
			throw new NetworkConnectionException("Client is already connected", NetworkErrorType.ALREADY_CONNECTED, endpoint);
		}
		
		try {
			SSLContext context = this.config.resolveSslContext();
			SSLSocket sslSocket = (SSLSocket) context.getSocketFactory().createSocket();
			this.socket = sslSocket;
			
			this.configureSocket(sslSocket);
			
			sslSocket.connect(endpoint.toInetSocketAddress(), (int) this.config.connectTimeout().toMillis());
			sslSocket.startHandshake();
			this.connected = true;
			
			if (this.config.onConnect() != null) {
				IpEndpoint local = this.localEndpoint().orElse(null);
				IpEndpoint remote = this.remoteEndpoint().orElse(null);
				
				this.config.onConnect().handle(null, local != null ? local : endpoint, remote != null ? remote : endpoint, Instant.now());
			}
		} catch (NoSuchAlgorithmException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.CONNECTION_FAILED, "Failed to initialize SSL context", e);
			throw new NetworkConnectionException("Failed to initialize SSL context", e, NetworkErrorType.CONNECTION_FAILED, endpoint);
		} catch (IOException e) {
			throw NetworkUtils.mapConnectFailure(e, endpoint, this.config.connectTimeout(), this.config.onError());
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
	
	@Override
	public @NonNull Optional<IpEndpoint> remoteEndpoint() {
		if (!this.isActive()) {
			return Optional.empty();
		}
		
		InetSocketAddress address = (InetSocketAddress) this.socket.getRemoteSocketAddress();
		return Optional.of(IpEndpoint.from(address));
	}
	
	@Override
	public void send(byte @NonNull [] data) throws NetworkConnectionException {
		Objects.requireNonNull(data, "Data must not be null");
		NetworkUtils.validateMessageSize(data, this.config.bufferSize(), null);
		this.ensureConnected();
		
		NetworkUtils.writeAll(this.socket, data, this.config.framing(), this.config.onError(), null, this::handleDisconnect);
	}
	
	@Override
	public byte @NonNull [] receive() throws NetworkConnectionException {
		return this.receive(this.config.bufferSize());
	}
	
	@Override
	public byte @NonNull [] receive(int maxBytes) throws NetworkConnectionException {
		if (maxBytes < 1) {
			throw new IllegalArgumentException("Max bytes must be at least 1: " + maxBytes);
		}
		this.ensureConnected();
		
		if (!this.config.framing()) {
			this.readBuffer = NetworkUtils.resizeBuffer(this.readBuffer, maxBytes);
		}
		
		return NetworkUtils.readAvailable(this.socket, this.readBuffer, maxBytes, this.config.framing(), this.config.readTimeout(), this.config.onError(), null, this::handleDisconnect);
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
	 * Applies the configured socket options and TLS parameters to the given socket.<br>
	 * This is done before the handshake, because the enabled protocols, cipher suites and endpoint identification
	 * algorithm have to be known when the handshake starts.<br>
	 *
	 * @param sslSocket The socket to configure
	 * @throws SocketException If a socket option could not be applied
	 */
	private void configureSocket(@NonNull SSLSocket sslSocket) throws SocketException {
		sslSocket.setTcpNoDelay(this.config.tcpNoDelay());
		sslSocket.setKeepAlive(this.config.keepAlive());
		if (!this.config.readTimeout().isZero()) {
			sslSocket.setSoTimeout((int) this.config.readTimeout().toMillis());
		}
		if (!this.config.enabledProtocols().isEmpty()) {
			sslSocket.setEnabledProtocols(TlsProtocol.toProtocolNames(this.config.enabledProtocols()));
		}
		if (!this.config.enabledCipherSuites().isEmpty()) {
			sslSocket.setEnabledCipherSuites(this.config.enabledCipherSuites().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
		}
		if (this.config.verifyHostname()) {
			SSLParameters parameters = sslSocket.getSSLParameters();
			parameters.setEndpointIdentificationAlgorithm("HTTPS");
			sslSocket.setSSLParameters(parameters);
		}
	}
	
	/**
	 * Determines the peer host name to use for server name indication and hostname verification.<br>
	 *
	 * @param endpoint The remote endpoint
	 * @return The hostname of a host endpoint, or the literal address of an ip endpoint
	 */
	private static @NonNull String resolvePeerHost(@NonNull Endpoint endpoint) {
		return switch (endpoint) {
			case HostEndpoint hostEndpoint -> hostEndpoint.hostname();
			case IpEndpoint ipEndpoint -> ipEndpoint.address().toString();
		};
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
