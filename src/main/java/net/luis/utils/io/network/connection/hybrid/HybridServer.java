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

package net.luis.utils.io.network.connection.hybrid;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.ssl.*;
import net.luis.utils.io.network.connection.tcp.*;
import net.luis.utils.io.network.connection.udp.UdpServer;
import net.luis.utils.io.network.connection.udp.UdpServerConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A server that serves UDP, TCP and SSL/TLS clients on a single endpoint at the same time.<br>
 * <p>
 *     Each protocol is optional and configured with the configuration of its own server.<br>
 *     The clients are served by a regular {@link UdpServer}, {@link TcpServer} and {@link SslServer}.<br>
 *     The handlers of each configuration therefore receive the server of their protocol.<br>
 *     These servers can be retrieved through {@link #udpServer()}, {@link #tcpServer()} and {@link #sslServer()} as well.
 * </p>
 * <p>
 *     UDP and TCP are separate protocols, so the UDP server and the TCP server are bound to the same port side by side.<br>
 *     SSL/TLS runs on top of TCP, so if both plain TCP and SSL/TLS are configured, the TCP server accepts every connection and the protocol is detected per connection.<br>
 *     A TLS connection always starts with a handshake record, whose first byte is {@code 0x16}.<br>
 *     The first byte of every connection is therefore peeked through the buffered input stream of the connection.<br>
 *     A connection starting with a handshake record is upgraded with {@link TcpConnection#upgrade(SslServerConfig)} and served by the SSL server through {@link SslServer#serve(SslConnection)}.<br>
 *     Every other connection is served as plain TCP.<br>
 *     A client that sends nothing within the detection timeout is served as plain TCP as well.<br>
 *     Protocols in which the server speaks first therefore keep working, delayed by the detection timeout.<br>
 *     A plain TCP protocol whose first byte is {@code 0x16} cannot be served next to SSL/TLS.<br>
 *     With framing enabled this never happens, because a frame starting with {@code 0x16} would be longer than any accepted message.
 * </p>
 * <p>
 *     On a shared port the SSL server is not started itself, it only serves the upgraded connections.<br>
 *     These run on the threads of the TCP server, so the executor strategy and the backlog of the SSL configuration are not used.<br>
 *     The backlog of the shared port is the larger backlog of both configurations.
 * </p>
 * <p>
 *     If the configured port is {@code 0}, the port chosen for TCP is used for UDP as well, so all protocols share the same endpoint.<br>
 *     If one of the protocols cannot be started, the others are stopped again and the failure is reported to the error handler of its configuration.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IpEndpoint bindAddress = new IpEndpoint(Ipv4Address.ANY, 8080);
 * UdpServerConfig udpConfig = UdpServerConfig.builder()
 *     .onMessage((server, datagram, data) -> server.send(datagram.endpoint(), data))
 *     .build();
 * TcpServerConfig tcpConfig = TcpServerConfig.builder()
 *     .onMessage((server, connection, data) -> connection.send(data))
 *     .build();
 * SslServerConfig sslConfig = SslServerConfig.builder(sslContext)
 *     .onMessage((server, connection, data) -> connection.send(data))
 *     .build();
 *
 * try (HybridServer server = HybridServer.create(bindAddress, udpConfig, tcpConfig, sslConfig)) {
 *     server.start();
 *     // Server runs until stopped or closed
 * }
 * }</pre>
 *
 * @see UdpServer
 * @see TcpServer
 * @see SslServer
 *
 * @author Luis-St
 */
@SuppressWarnings("DataFlowIssue")
public final class HybridServer implements NetworkServer {
	
	/**
	 * The first byte of a TLS handshake record, which starts every TLS connection.<br>
	 */
	private static final int TLS_HANDSHAKE_RECORD = 0x16;
	/**
	 * The interval in which a connection is checked for its first byte during protocol detection.<br>
	 */
	private static final Duration DETECTION_POLL_INTERVAL = Duration.ofMillis(5);
	/**
	 * The default time to wait for the first byte of a connection before it is served as plain TCP.<br>
	 */
	public static final Duration DEFAULT_DETECTION_TIMEOUT = Duration.ofSeconds(1);
	/**
	 * The endpoint to bind the server to.<br>
	 */
	private final IpEndpoint bindEndpoint;
	/**
	 * The configuration of the UDP server, or null if UDP is not served.<br>
	 */
	private final @Nullable UdpServerConfig udpConfig;
	/**
	 * The configuration of the TCP server, or null if plain TCP is not served.<br>
	 */
	private final @Nullable TcpServerConfig tcpConfig;
	/**
	 * The configuration of the SSL server, or null if SSL/TLS is not served.<br>
	 */
	private final @Nullable SslServerConfig sslConfig;
	/**
	 * The time to wait for the first byte of a connection before it is served as plain TCP.<br>
	 */
	private final Duration detectionTimeout;
	/**
	 * Whether the server is currently running.<br>
	 */
	private final AtomicBoolean running = new AtomicBoolean(false);
	/**
	 * The server for UDP clients, or null if UDP is not served or the server was not started yet.<br>
	 */
	private volatile @Nullable UdpServer udpServer;
	/**
	 * The server for TCP clients, or null if TCP is not served or the server was not started yet.<br>
	 * On a shared port it accepts the SSL/TLS clients as well.<br>
	 */
	private volatile @Nullable TcpServer tcpServer;
	/**
	 * The server for SSL/TLS clients, or null if SSL/TLS is not served or the server was not started yet.<br>
	 */
	private volatile @Nullable SslServer sslServer;
	
	/**
	 * Constructs a new hybrid server with the specified bind endpoint, configurations and detection timeout.<br>
	 *
	 * @param bindEndpoint The endpoint to bind to
	 * @param udpConfig The configuration of the UDP server, or null if UDP is not served
	 * @param tcpConfig The configuration of the TCP server, or null if plain TCP is not served
	 * @param sslConfig The configuration of the SSL server, or null if SSL/TLS is not served
	 * @param detectionTimeout The time to wait for the first byte of a connection before it is served as plain TCP
	 * @throws NullPointerException If bind endpoint or detection timeout is null
	 * @throws IllegalArgumentException If no configuration is given or the detection timeout is not positive
	 */
	private HybridServer(@NonNull IpEndpoint bindEndpoint, @Nullable UdpServerConfig udpConfig, @Nullable TcpServerConfig tcpConfig, @Nullable SslServerConfig sslConfig, @NonNull Duration detectionTimeout) {
		this.bindEndpoint = Objects.requireNonNull(bindEndpoint, "Bind endpoint must not be null");
		this.detectionTimeout = Objects.requireNonNull(detectionTimeout, "Detection timeout must not be null");
		if (udpConfig == null && tcpConfig == null && sslConfig == null) {
			throw new IllegalArgumentException("At least one of the UDP, TCP and SSL configurations must be set");
		}
		if (detectionTimeout.isNegative() || detectionTimeout.isZero()) {
			throw new IllegalArgumentException("Detection timeout must be positive: " + detectionTimeout);
		}
		
		this.udpConfig = udpConfig;
		this.tcpConfig = tcpConfig;
		this.sslConfig = sslConfig;
	}
	
	/**
	 * Creates a new hybrid server that serves only UDP clients.<br>
	 *
	 * @param endpoint The endpoint to bind to
	 * @param config The configuration of the UDP server
	 * @return The created server
	 * @throws NullPointerException If endpoint or config is null
	 */
	public static @NonNull HybridServer createUdp(@NonNull IpEndpoint endpoint, @NonNull UdpServerConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		return create(endpoint, config, null, null);
	}
	
	/**
	 * Creates a new hybrid server that serves only plain TCP clients.<br>
	 *
	 * @param endpoint The endpoint to bind to
	 * @param config The configuration of the TCP server
	 * @return The created server
	 * @throws NullPointerException If endpoint or config is null
	 */
	public static @NonNull HybridServer createTcp(@NonNull IpEndpoint endpoint, @NonNull TcpServerConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		return create(endpoint, null, config, null);
	}
	
	/**
	 * Creates a new hybrid server that serves only SSL/TLS clients.<br>
	 *
	 * @param endpoint The endpoint to bind to
	 * @param config The configuration of the SSL server
	 * @return The created server
	 * @throws NullPointerException If endpoint or config is null
	 */
	public static @NonNull HybridServer createSsl(@NonNull IpEndpoint endpoint, @NonNull SslServerConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		return create(endpoint, null, null, config);
	}
	
	/**
	 * Creates a new hybrid server that serves every protocol whose configuration is given on the same endpoint.<br>
	 * The {@link #DEFAULT_DETECTION_TIMEOUT default detection timeout} is used to tell plain TCP and SSL/TLS clients apart.<br>
	 *
	 * @param endpoint The endpoint to bind to
	 * @param udpConfig The configuration of the UDP server, or null if UDP is not served
	 * @param tcpConfig The configuration of the TCP server, or null if plain TCP is not served
	 * @param sslConfig The configuration of the SSL server, or null if SSL/TLS is not served
	 * @return The created server
	 * @throws NullPointerException If endpoint is null
	 * @throws IllegalArgumentException If no configuration is given
	 */
	public static @NonNull HybridServer create(@NonNull IpEndpoint endpoint, @Nullable UdpServerConfig udpConfig, @Nullable TcpServerConfig tcpConfig, @Nullable SslServerConfig sslConfig) {
		return create(endpoint, udpConfig, tcpConfig, sslConfig, DEFAULT_DETECTION_TIMEOUT);
	}
	
	/**
	 * Creates a new hybrid server that serves every protocol whose configuration is given on the same endpoint.<br>
	 * <p>
	 *     The detection timeout is only used if both plain TCP and SSL/TLS are served.<br>
	 *     It is the time to wait for the first byte of a connection before the connection is served as plain TCP.
	 * </p>
	 *
	 * @param endpoint The endpoint to bind to
	 * @param udpConfig The configuration of the UDP server, or null if UDP is not served
	 * @param tcpConfig The configuration of the TCP server, or null if plain TCP is not served
	 * @param sslConfig The configuration of the SSL server, or null if SSL/TLS is not served
	 * @param detectionTimeout The time to wait for the first byte of a connection
	 * @return The created server
	 * @throws NullPointerException If endpoint or detection timeout is null
	 * @throws IllegalArgumentException If no configuration is given or the detection timeout is not positive
	 */
	public static @NonNull HybridServer create(@NonNull IpEndpoint endpoint, @Nullable UdpServerConfig udpConfig, @Nullable TcpServerConfig tcpConfig, @Nullable SslServerConfig sslConfig, @NonNull Duration detectionTimeout) {
		return new HybridServer(endpoint, udpConfig, tcpConfig, sslConfig, detectionTimeout);
	}
	
	/**
	 * Returns the server that serves the UDP clients.<br>
	 * @return The UDP server, or an empty optional if UDP is not served or the server was not started yet
	 */
	public @NonNull Optional<UdpServer> udpServer() {
		return Optional.ofNullable(this.udpServer);
	}
	
	/**
	 * Returns the server that serves the plain TCP clients.<br>
	 * On a shared port this server accepts the SSL/TLS clients as well, but only the plain TCP clients stay registered with it.<br>
	 *
	 * @return The TCP server, or an empty optional if plain TCP is not served or the server was not started yet
	 */
	public @NonNull Optional<TcpServer> tcpServer() {
		return Optional.ofNullable(this.tcpServer);
	}
	
	/**
	 * Returns the server that serves the SSL/TLS clients.<br>
	 * @return The SSL server, or an empty optional if SSL/TLS is not served or the server was not started yet
	 */
	public @NonNull Optional<SslServer> sslServer() {
		return Optional.ofNullable(this.sslServer);
	}
	
	@Override
	public boolean isRunning() {
		if (!this.running.get()) {
			return false;
		}
		
		if (this.udpConfig != null && (this.udpServer == null || !this.udpServer.isRunning())) {
			return false;
		}
		if (this.tcpConfig != null) {
			return this.tcpServer != null && this.tcpServer.isRunning();
		}
		return this.sslConfig == null || (this.sslServer != null && this.sslServer.isRunning());
	}
	
	@Override
	public @NonNull IpEndpoint boundEndpoint() {
		if (this.tcpServer != null) {
			return this.tcpServer.boundEndpoint();
		}
		if (this.sslServer != null) {
			return this.sslServer.boundEndpoint();
		}
		if (this.udpServer != null) {
			return this.udpServer.boundEndpoint();
		}
		return this.bindEndpoint;
	}
	
	@Override
	public void start() {
		if (this.running.getAndSet(true)) {
			return;
		}
		
		if (!this.startStreamServers() || !this.startUdpServer()) {
			this.stop();
		}
	}
	
	@Override
	public void stop() {
		if (!this.running.getAndSet(false)) {
			return;
		}
		
		if (this.sslServer != null) {
			this.sslServer.stop();
		}
		if (this.tcpServer != null) {
			this.tcpServer.stop();
		}
		if (this.udpServer != null) {
			this.udpServer.stop();
		}
	}
	
	@Override
	public void close() {
		this.stop();
	}
	
	//region Helper methods
	
	/**
	 * Starts the servers for plain TCP and SSL/TLS, whichever of them is configured.<br>
	 * If both are configured, the TCP server accepts the clients of both and the SSL server only serves the upgraded connections.<br>
	 *
	 * @return True if every configured server was started, false otherwise
	 */
	private boolean startStreamServers() {
		if (this.tcpConfig != null && this.sslConfig != null) {
			this.sslServer = new SslServer(this.bindEndpoint, this.sslConfig);
			
			this.tcpServer = new TcpServer(this.bindEndpoint, this.createSharedConfig(this.tcpConfig, this.sslConfig));
			this.tcpServer.start();
			return this.tcpServer.isRunning();
		}
		
		if (this.tcpConfig != null) {
			this.tcpServer = new TcpServer(this.bindEndpoint, this.tcpConfig);
			this.tcpServer.start();
			return this.tcpServer.isRunning();
		}
		if (this.sslConfig != null) {
			this.sslServer = new SslServer(this.bindEndpoint, this.sslConfig);
			this.sslServer.start();
			return this.sslServer.isRunning();
		}
		return true;
	}
	
	/**
	 * Starts the server for UDP if it is configured.<br>
	 * The server is bound to the port of the TCP or SSL server if one is running, so that a configured port of {@code 0} results in one shared port.<br>
	 *
	 * @return True if the server was started or UDP is not configured, false otherwise
	 */
	private boolean startUdpServer() {
		if (this.udpConfig == null) {
			return true;
		}
		
		IpEndpoint endpoint = this.tcpConfig != null || this.sslConfig != null ? this.boundEndpoint() : this.bindEndpoint;
		this.udpServer = new UdpServer(endpoint, this.udpConfig);
		this.udpServer.start();
		return this.udpServer.isRunning();
	}
	
	/**
	 * Creates the configuration of the TCP server that accepts the clients of both plain TCP and SSL/TLS on a shared port.<br>
	 * <p>
	 *     The configuration is the given TCP configuration with the larger backlog of both configurations.<br>
	 *     Its event handlers are replaced by a connection handler that detects the protocol of each connection.<br>
	 *     The handlers of the TCP configuration are called by that connection handler, because they must only be called for plain TCP clients.
	 * </p>
	 *
	 * @param tcpConfig The configuration of the TCP server
	 * @param sslConfig The configuration of the SSL server
	 * @return The configuration of the shared TCP server
	 * @throws NullPointerException If tcp config or ssl config is null
	 */
	private @NonNull TcpServerConfig createSharedConfig(@NonNull TcpServerConfig tcpConfig, @NonNull SslServerConfig sslConfig) {
		Objects.requireNonNull(tcpConfig, "TCP config must not be null");
		Objects.requireNonNull(sslConfig, "SSL config must not be null");
		
		return new TcpServerConfig(
			Math.max(tcpConfig.backlog(), sslConfig.backlog()), tcpConfig.clientBufferSize(), tcpConfig.framing(), tcpConfig.clientReadTimeout(), tcpConfig.tcpNoDelay(), tcpConfig.keepAlive(), tcpConfig.executorStrategy(), null, null, null, this::handleSharedConnection, tcpConfig.onError()
		);
	}
	
	/**
	 * Detects the protocol of a connection accepted on the shared port and serves it accordingly.<br>
	 * <p>
	 *     The first byte of the connection is peeked within the detection timeout.<br>
	 *     If it starts a TLS handshake record, the connection is upgraded and served by the SSL server.<br>
	 *     Otherwise it is served as plain TCP with the handlers of the TCP configuration.
	 * </p>
	 *
	 * @param server The TCP server that accepted the connection
	 * @param connection The accepted connection
	 * @throws NullPointerException If server or connection is null
	 * @throws Exception If serving the connection fails, the failure is reported by the TCP server
	 */
	private void handleSharedConnection(@NonNull TcpServer server, @NonNull TcpConnection connection) throws Exception {
		Objects.requireNonNull(server, "Server must not be null");
		Objects.requireNonNull(connection, "Connection must not be null");
		
		OptionalInt firstByte = this.peekFirstByte(connection);
		if (firstByte.isPresent() && firstByte.getAsInt() == TLS_HANDSHAKE_RECORD && this.sslServer != null && this.sslConfig != null) {
			this.sslServer.serve(connection.upgrade(this.sslConfig));
		} else {
			this.servePlain(server, connection, Objects.requireNonNull(this.tcpConfig, "TCP config must not be null"));
		}
	}
	
	/**
	 * Returns the first byte of the given connection without consuming it, waiting at most the detection timeout for it to arrive.<br>
	 * <p>
	 *     The connection is polled for available input instead of blocking in a read, because the read timeout of the connection cannot be changed from here.<br>
	 *     Once input is available, the first byte is read and pushed back through the mark of the buffered input stream of the connection.<br>
	 *     The byte is therefore read again by whoever reads the connection next.
	 * </p>
	 *
	 * @param connection The connection to peek into
	 * @return The first byte, or an empty optional if no input arrived within the detection timeout, the connection was closed, or the thread was interrupted
	 * @throws NullPointerException If connection is null
	 * @throws NetworkConnectionException If the connection is closed before it is checked
	 * @throws IOException If reading from the connection fails
	 */
	private @NonNull OptionalInt peekFirstByte(@NonNull TcpConnection connection) throws NetworkConnectionException, IOException {
		Objects.requireNonNull(connection, "Connection must not be null");
		
		InputStream input = connection.getInputStream();
		long deadline = System.nanoTime() + this.detectionTimeout.toNanos();
		while (input.available() == 0) {
			if (System.nanoTime() - deadline >= 0 || !connection.isActive()) {
				return OptionalInt.empty();
			}
			
			try {
				Thread.sleep(DETECTION_POLL_INTERVAL);
			} catch (InterruptedException _) {
				Thread.currentThread().interrupt();
				return OptionalInt.empty();
			}
		}
		
		input.mark(1);
		int firstByte = input.read();
		if (firstByte == -1) {
			return OptionalInt.empty();
		}
		
		input.reset();
		return OptionalInt.of(firstByte);
	}
	
	/**
	 * Serves a connection accepted on the shared port as plain TCP with the handlers of the given configuration.<br>
	 * The connection is handled the same way the TCP server handles its connections, the TCP server closes it once this method returns.<br>
	 *
	 * @param server The TCP server that accepted the connection
	 * @param connection The accepted connection
	 * @param config The configuration of the TCP server
	 * @throws NullPointerException If server, connection, or config is null
	 * @throws Exception If the connection handler or receiving fails, the failure is reported by the TCP server
	 */
	private void servePlain(@NonNull TcpServer server, @NonNull TcpConnection connection, @NonNull TcpServerConfig config) throws Exception {
		Objects.requireNonNull(server, "Server must not be null");
		Objects.requireNonNull(connection, "Connection must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		
		if (config.onClientConnect() != null) {
			config.onClientConnect().handle(connection, connection.localEndpoint(), connection.remoteEndpoint(), Instant.now());
		}
		
		try {
			if (config.onConnection() != null) {
				config.onConnection().handle(server, connection);
				return;
			}
			
			while (server.isRunning() && connection.isActive()) {
				byte[] data = connection.receive();
				if (data.length == 0) {
					break;
				}
				
				if (config.onMessage() != null) {
					try {
						config.onMessage().handle(server, connection, data);
					} catch (Exception e) {
						NetworkUtils.handleError(config.onError(), connection, NetworkErrorType.IO_ERROR, "Error in message handler", e);
					}
				}
			}
		} finally {
			if (config.onClientDisconnect() != null && connection.isActive()) {
				try {
					config.onClientDisconnect().handle(connection, connection.localEndpoint(), connection.remoteEndpoint(), Instant.now());
				} catch (Exception _) {}
			}
		}
	}
	//endregion
}
