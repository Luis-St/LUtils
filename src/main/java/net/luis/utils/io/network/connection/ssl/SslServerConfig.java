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

import net.luis.utils.io.network.connection.event.*;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import net.luis.utils.io.network.connection.tcp.TcpServerConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Configuration options for SSL/TLS servers.<br>
 * This record extends the plain TCP server settings with TLS-specific options such as the required
 * {@link SSLContext}, the enabled protocols and cipher suites, and the client certificate authentication mode.<br>
 * <p>
 *     Unlike {@link TcpServerConfig}, there is no shared default instance because a server always requires an {@link SSLContext} holding its certificate and private key.<br>
 *     Use {@link #builder(SSLContext)} to construct a configuration.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SslServerConfig config = SslServerConfig.builder(sslContext)
 *     .backlog(100)
 *     .clientAuth(SSLClientAuth.REQUIRED)
 *     .executorStrategy(ClientExecutorStrategy.virtualThreads())
 *     .onMessage((server, conn, data) -> conn.send(("Echo: " + new String(data)).getBytes()))
 *     .build();
 *
 * try (SslServer server = new SslServer(bindEndpoint, config)) {
 *     server.start();
 * }
 * }</pre>
 *
 * @see SslServerConfigBuilder
 * @see SslServer
 *
 * @author Luis-St
 *
 * @param backlog Maximum number of pending connections in the queue
 * @param clientBufferSize Buffer size for each client connection in bytes
 * @param framing Whether messages are framed with a length prefix on the wire, so that each receive returns exactly one send
 * @param clientReadTimeout Read timeout for client connections (Duration.ZERO for infinite)
 * @param tcpNoDelay Whether to disable Nagle's algorithm for client connections
 * @param keepAlive Whether to enable TCP keep-alive for client connections
 * @param sslContext The SSL context holding the server certificate and private key (required)
 * @param enabledProtocols The TLS protocols to enable (empty for the socket default)
 * @param enabledCipherSuites The cipher suites to enable (empty for the socket default)
 * @param clientAuth How to handle client certificate authentication (mutual TLS)
 * @param executorStrategy How to handle concurrent client connections
 * @param onClientConnect Handler called when a client connects (after a successful handshake)
 * @param onClientDisconnect Handler called when a client disconnects
 * @param onMessage Handler called when a message is received from a client
 * @param onConnection Handler that takes over the whole connection instead of the built-in read loop
 * @param onError Handler called when an error occurs
 */
public record SslServerConfig(
	int backlog,
	int clientBufferSize,
	boolean framing,
	@NonNull Duration clientReadTimeout,
	boolean tcpNoDelay,
	boolean keepAlive,
	@NonNull SSLContext sslContext,
	@NonNull List<TlsProtocol> enabledProtocols,
	@NonNull List<String> enabledCipherSuites,
	@NonNull SslClientAuth clientAuth,
	@NonNull ClientExecutorStrategy executorStrategy,
	@Nullable ConnectEventHandler onClientConnect,
	@Nullable DisconnectEventHandler onClientDisconnect,
	@Nullable MessageEventHandler<SslServer, SslConnection> onMessage,
	@Nullable ConnectionHandler<SslServer, SslConnection> onConnection,
	@Nullable ErrorEventHandler onError
) {
	
	/**
	 * Constructs a new SSL server configuration.<br>
	 * The protocol and cipher suite lists are copied defensively into immutable lists.<br>
	 *
	 * @param backlog Maximum number of pending connections
	 * @param clientBufferSize Buffer size for client connections
	 * @param framing Whether messages are framed with a length prefix on the wire, so that each receive returns exactly one send
	 * @param clientReadTimeout Read timeout for client connections
	 * @param tcpNoDelay Whether to disable Nagle's algorithm
	 * @param keepAlive Whether to enable TCP keep-alive
	 * @param sslContext The SSL context holding the server certificate and private key
	 * @param enabledProtocols The TLS protocols to enable
	 * @param enabledCipherSuites The cipher suites to enable
	 * @param clientAuth How to handle client certificate authentication
	 * @param executorStrategy How to handle concurrent clients
	 * @param onClientConnect Handler for client connections
	 * @param onClientDisconnect Handler for client disconnections
	 * @param onMessage Handler for incoming messages
	 * @param onConnection Handler that takes over the whole connection
	 * @param onError Handler for errors
	 * @throws NullPointerException If clientReadTimeout, sslContext, enabledProtocols, enabledCipherSuites, clientAuth, or executorStrategy is null, or if enabledProtocols contains null
	 * @throws IllegalArgumentException If backlog or clientBufferSize is less than 1, or if both onMessage and onConnection are set
	 */
	public SslServerConfig {
		Objects.requireNonNull(clientReadTimeout, "Client read timeout must not be null");
		Objects.requireNonNull(sslContext, "SSL context must not be null");
		Objects.requireNonNull(enabledProtocols, "Enabled protocols must not be null");
		Objects.requireNonNull(enabledCipherSuites, "Enabled cipher suites must not be null");
		Objects.requireNonNull(clientAuth, "Client auth must not be null");
		Objects.requireNonNull(executorStrategy, "Executor strategy must not be null");
		
		if (backlog < 1) {
			throw new IllegalArgumentException("Backlog must be at least 1: " + backlog);
		}
		if (clientBufferSize < 1) {
			throw new IllegalArgumentException("Client buffer size must be at least 1: " + clientBufferSize);
		}
		if (onMessage != null && onConnection != null) {
			throw new IllegalArgumentException("Message handler and connection handler must not be set at the same time");
		}
		
		enabledProtocols = List.copyOf(enabledProtocols);
		enabledCipherSuites = List.copyOf(enabledCipherSuites);
	}
	
	/**
	 * Creates a new builder for constructing SSL server configuration.<br>
	 *
	 * @param sslContext The SSL context holding the server certificate and private key
	 * @return A new builder with default values
	 * @throws NullPointerException If sslContext is null
	 */
	public static @NonNull SslServerConfigBuilder builder(@NonNull SSLContext sslContext) {
		return new SslServerConfigBuilder(sslContext);
	}
}
