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

import net.luis.utils.io.network.Endpoint;
import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.*;
import net.luis.utils.io.network.connection.exception.*;
import net.luis.utils.io.network.connection.ssl.SslClient;
import net.luis.utils.io.network.connection.ssl.SslUpgradeConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Instant;
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
public final class TcpClient implements NetworkClient<byte[]> {
	
	/**
	 * The configuration for this client.<br>
	 */
	private final TcpClientConfig config;
	/**
	 * The reusable scratch buffer for unframed read operations.<br>
	 * Allocated lazily and grown on demand, and unused while framing is enabled.<br>
	 */
	private byte @Nullable [] readBuffer;
	/**
	 * The underlying socket for communication.<br>
	 */
	private volatile Socket socket;
	/**
	 * The streams of the current socket, so that every caller gets the same instance.<br>
	 * Replaced whenever a new connection is established.<br>
	 */
	private volatile @Nullable SocketStreams streams;
	/**
	 * The endpoint this client was connected to, or null if it was never connected.<br>
	 * It is kept because the peer name is required to upgrade the connection to TLS.<br>
	 */
	private volatile @Nullable Endpoint endpoint;
	/**
	 * Whether this client is currently connected.<br>
	 */
	private volatile boolean connected;
	/**
	 * Whether the connection of this client was handed over to an SSL client by an upgrade.<br>
	 */
	private volatile boolean upgraded;
	
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
	 * Creates a new TCP client with default configuration and connects it to the specified remote endpoint.<br>
	 *
	 * @param endpoint The remote endpoint to connect to
	 * @return The connected client
	 * @throws NullPointerException If endpoint is null
	 * @throws NetworkConnectionException If connection fails
	 * @throws NetworkTimeoutException If connection times out
	 */
	public static @NonNull TcpClient connectTo(@NonNull Endpoint endpoint) throws NetworkConnectionException {
		return connectTo(endpoint, TcpClientConfig.DEFAULT);
	}
	
	/**
	 * Creates a new TCP client with the specified configuration and connects it to the specified remote endpoint.<br>
	 * If the connection fails, the client is closed before the exception is propagated.<br>
	 *
	 * @param endpoint The remote endpoint to connect to
	 * @param config The client configuration
	 * @return The connected client
	 * @throws NullPointerException If endpoint or config is null
	 * @throws NetworkConnectionException If connection fails
	 * @throws NetworkTimeoutException If connection times out
	 */
	public static @NonNull TcpClient connectTo(@NonNull Endpoint endpoint, @NonNull TcpClientConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		
		TcpClient client = new TcpClient(config);
		try {
			client.connect(endpoint);
			return client;
		} catch (NetworkConnectionException e) {
			client.close();
			throw e;
		}
	}
	
	/**
	 * Connects to the specified remote endpoint.<br>
	 *
	 * @param endpoint The remote endpoint to connect to
	 * @throws NullPointerException If endpoint is null
	 * @throws NetworkConnectionException If connection fails
	 * @throws NetworkTimeoutException If connection times out
	 */
	public void connect(@NonNull Endpoint endpoint) throws NetworkConnectionException {
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		if (this.connected) {
			throw new NetworkConnectionException("Client is already connected", NetworkErrorType.ALREADY_CONNECTED, endpoint);
		}
		
		try {
			this.socket = new Socket();
			this.streams = new SocketStreams(this.socket);
			this.endpoint = endpoint;
			this.upgraded = false;
			this.socket.setTcpNoDelay(this.config.tcpNoDelay());
			this.socket.setKeepAlive(this.config.keepAlive());
			
			if (!this.config.readTimeout().isZero()) {
				this.socket.setSoTimeout((int) this.config.readTimeout().toMillis());
			}
			
			this.socket.connect(endpoint.toInetSocketAddress(), (int) this.config.connectTimeout().toMillis());
			this.connected = true;
			
			if (this.config.onConnect() != null) {
				IpEndpoint local = this.localEndpoint().orElse(null);
				IpEndpoint remote = this.remoteEndpoint().orElse(null);
				
				this.config.onConnect().handle(null, local != null ? local : endpoint, remote != null ? remote : endpoint, Instant.now());
			}
		} catch (IOException e) {
			throw NetworkUtils.mapConnectFailure(e, endpoint, this.config.connectTimeout(), this.config.onError());
		}
	}
	
	/**
	 * Upgrades this connection to TLS using the default upgrade configuration.<br>
	 *
	 * @return A secure client that owns the upgraded connection
	 * @throws NetworkConnectionException If the client is not connected or the TLS handshake fails
	 * @see #upgrade(SslUpgradeConfig)
	 */
	public @NonNull SslClient upgrade() throws NetworkConnectionException {
		return this.upgrade(SslUpgradeConfig.DEFAULT);
	}
	
	/**
	 * Upgrades this connection to TLS and returns a secure client for it.<br>
	 * <p>
	 *     The given upgrade configuration is combined with the configuration of this client into the configuration of the returned client, see {@link SslUpgradeConfig#toClientConfig(TcpClientConfig)}.<br>
	 *     All transport settings such as timeouts, buffer size, framing, socket options and event handlers are therefore carried over, so that the secure connection behaves exactly like the plaintext connection it replaces.
	 * </p>
	 * <p>
	 *     This is the client side of protocols that negotiate TLS on an existing connection, such as {@code STARTTLS}.<br>
	 *     The negotiation itself is not part of this method, the upgrade is performed once the peer has agreed to it.
	 * </p>
	 * <p>
	 *     The peer must not have sent anything beyond its agreement, because the secure client reads the raw socket and
	 *     neither sees what a reader of this client has already buffered nor expects plaintext before the handshake.<br>
	 *     Pending input is therefore rejected rather than silently dropped.
	 * </p>
	 * <p>
	 *     On success the returned client owns the connection and this client is left inactive, so closing it becomes a no operation and the secure client has to be closed instead.<br>
	 *     The connect handler is not called again, because the connection was already established.<br>
	 *     If the upgrade fails, this client keeps the connection and has to be closed as usual.
	 * </p>
	 *
	 * @param upgradeConfig The TLS options to upgrade with
	 * @return A secure client that owns the upgraded connection
	 * @throws NullPointerException If the upgrade config is null
	 * @throws NetworkConnectionException If the client is not connected, unread plaintext is still pending, or the TLS handshake fails
	 */
	public @NonNull SslClient upgrade(@NonNull SslUpgradeConfig upgradeConfig) throws NetworkConnectionException {
		Objects.requireNonNull(upgradeConfig, "Upgrade config must not be null");
		this.ensureConnected();
		
		Endpoint endpoint = this.endpoint;
		if (endpoint == null) {
			throw new NetworkConnectionException("Client is not connected", NetworkErrorType.NOT_CONNECTED);
		}
		if (this.streams().hasPendingInput()) {
			throw new NetworkConnectionException("Unread plaintext is pending, it would be lost or misread as handshake data", NetworkErrorType.IO_ERROR, endpoint);
		}
		
		SslClient client = SslClient.upgrade(this.socket, endpoint, upgradeConfig.toClientConfig(this.config));
		this.streams = null;
		this.upgraded = true;
		this.connected = false;
		return client;
	}
	
	/**
	 * Checks whether the connection of this client was handed over to a secure client by {@link #upgrade(SslUpgradeConfig)}.<br>
	 * An upgraded client is no longer active and closing it does not close the upgraded connection.<br>
	 *
	 * @return True if this client was upgraded, false otherwise
	 */
	public boolean isUpgraded() {
		return this.upgraded;
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
		
		InputStream in;
		try {
			in = this.streams().input();
		} catch (NetworkConnectionException e) {
			this.handleDisconnect();
			throw e;
		}
		
		return NetworkUtils.readMessage(in, this.readBuffer, maxBytes, this.config.framing(), this.config.readTimeout(), this.config.onError(), null, this::handleDisconnect);
	}
	
	/**
	 * Returns the input stream for advanced reading.<br>
	 * The stream is buffered and the same instance is returned on every call, so it can be stored and read across calls.<br>
	 * <p>
	 *     {@link #receive()} reads through this same buffer, so the two can be mixed without skipping bytes.
	 * </p>
	 *
	 * @return The input stream
	 * @throws NetworkConnectionException If the client is not connected or the stream cannot be obtained
	 */
	public @NonNull InputStream getInputStream() throws NetworkConnectionException {
		this.ensureConnected();
		return this.streams().input();
	}
	
	/**
	 * Returns the output stream for advanced writing.<br>
	 * The stream is not buffered and the same instance is returned on every call, so everything written to it goes to the peer immediately.<br>
	 *
	 * @return The output stream
	 * @throws NetworkConnectionException If the client is not connected or the stream cannot be obtained
	 */
	public @NonNull OutputStream getOutputStream() throws NetworkConnectionException {
		this.ensureConnected();
		return this.streams().output();
	}
	
	/**
	 * Closes this client and its underlying socket.<br>
	 * If the connection was handed over to a secure client by an upgrade, nothing is closed, because the secure client owns the connection.<br>
	 */
	@Override
	public void close() {
		if (!this.upgraded && this.socket != null && !this.socket.isClosed()) {
			this.handleDisconnect();
			
			try {
				this.socket.close();
			} catch (IOException _) {}
		}
		this.connected = false;
	}
	
	//region Helper methods
	
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
	 * Returns the streams of the current connection.<br>
	 *
	 * @return The streams
	 * @throws NetworkConnectionException If the client is not connected
	 */
	private @NonNull SocketStreams streams() throws NetworkConnectionException {
		SocketStreams streams = this.streams;
		if (streams == null) {
			throw new NetworkConnectionException("Client is not connected", NetworkErrorType.NOT_CONNECTED);
		}
		return streams;
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
