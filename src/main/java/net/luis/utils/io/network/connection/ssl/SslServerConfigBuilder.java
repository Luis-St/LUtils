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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Builder class for constructing SSL server configuration.<br>
 * Provides a fluent API for setting individual configuration options.<br>
 * <p>
 *     The {@link SSLContext} is required and must be supplied when the builder is created.<br>
 *     All other options default to sensible values (see {@link SslServer}).
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SslServerConfigBuilder config = SslServerConfigBuilder.builder(sslContext)
 *     .backlog(100)
 *     .clientBufferSize(4096)
 *     .clientAuth(SSLClientAuth.REQUESTED)
 *     .executorStrategy(ClientExecutorStrategy.fixedPool(10))
 *     .onMessage((server, conn, data) -> conn.send("OK".getBytes()))
 *     .build();
 * }</pre>
 *
 * @see SslServerConfig
 *
 * @author Luis-St
 */
public final class SslServerConfigBuilder {
	
	/**
	 * The SSL context holding the server certificate and private key.<br>
	 */
	private final SSLContext sslContext;
	/**
	 * The maximum number of pending connections in the queue.<br>
	 */
	private int backlog = 50;
	/**
	 * The buffer size for each client connection in bytes.<br>
	 */
	private int clientBufferSize = 8192;
	/**
	 * Whether messages are framed with a length prefix on the wire.<br>
	 * Enabled by default, so that every send is received as exactly one message.<br>
	 */
	private boolean framing = true;
	/**
	 * The read timeout for client connections.<br>
	 */
	private Duration clientReadTimeout = Duration.ZERO;
	/**
	 * Whether to disable Nagle's algorithm for client connections.<br>
	 */
	private boolean tcpNoDelay = true;
	/**
	 * Whether to enable TCP keep-alive for client connections.<br>
	 */
	private boolean keepAlive = true;
	/**
	 * The TLS protocols to enable.<br>
	 */
	private List<TlsProtocol> enabledProtocols = List.of();
	/**
	 * The cipher suites to enable.<br>
	 */
	private List<String> enabledCipherSuites = List.of();
	/**
	 * How to handle client certificate authentication.<br>
	 */
	private SslClientAuth clientAuth = SslClientAuth.NONE;
	/**
	 * The executor strategy for handling concurrent client connections.<br>
	 */
	private ClientExecutorStrategy executorStrategy = ClientExecutorStrategy.virtualThreads();
	/**
	 * The handler called when a client connects.<br>
	 */
	private @Nullable ConnectEventHandler onClientConnect;
	/**
	 * The handler called when a client disconnects.<br>
	 */
	private @Nullable DisconnectEventHandler onClientDisconnect;
	/**
	 * The handler called when a message is received from a client.<br>
	 */
	private @Nullable MessageEventHandler<SslServer, SslConnection> onMessage;
	/**
	 * The handler that takes over the whole connection instead of the built-in read loop.<br>
	 */
	private @Nullable ConnectionHandler<SslServer, SslConnection> onConnection;
	/**
	 * The handler called when an error occurs.<br>
	 */
	private @Nullable ErrorEventHandler onError;
	
	/**
	 * Constructs a new builder with the required SSL context and default values.<br>
	 *
	 * @param sslContext The SSL context holding the server certificate and private key
	 * @throws NullPointerException If sslContext is null
	 */
	SslServerConfigBuilder(@NonNull SSLContext sslContext) {
		this.sslContext = Objects.requireNonNull(sslContext, "SSL context must not be null");
	}
	
	/**
	 * Sets the maximum number of pending connections in the queue.<br>
	 *
	 * @param backlog The backlog size (must be at least 1)
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder backlog(int backlog) {
		this.backlog = backlog;
		return this;
	}
	
	/**
	 * Sets whether messages are framed with a length prefix on the wire.<br>
	 * <p>
	 *     With framing enabled, each send is written as one length-prefixed frame and each receive returns exactly that message,
	 *     regardless of how TCP fragments or coalesces the stream. This is the default and is required for message oriented protocols.
	 * </p>
	 * <p>
	 *     Disabling it restores the raw byte stream, where a read returns whatever is currently available. This is only useful when
	 *     talking to a peer that does not understand the frame header, or when the payload carries its own delimiters. Both peers
	 *     have to agree, since a framed peer and an unframed peer cannot interoperate.
	 * </p>
	 *
	 * @param framing Whether to frame messages with a length prefix
	 * @return This builder
	 */
	public @NonNull SslServerConfigBuilder framing(boolean framing) {
		this.framing = framing;
		return this;
	}
	
	/**
	 * Sets the buffer size for each client connection in bytes.<br>
	 *
	 * @param clientBufferSize The buffer size (must be at least 1)
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder clientBufferSize(int clientBufferSize) {
		this.clientBufferSize = clientBufferSize;
		return this;
	}
	
	/**
	 * Sets the read timeout for client connections.<br>
	 * Use {@link Duration#ZERO} for infinite timeout.<br>
	 *
	 * @param clientReadTimeout The read timeout
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder clientReadTimeout(@NonNull Duration clientReadTimeout) {
		this.clientReadTimeout = clientReadTimeout;
		return this;
	}
	
	/**
	 * Sets whether to disable Nagle's algorithm for client connections.<br>
	 *
	 * @param tcpNoDelay True to disable Nagle's algorithm
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder tcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
		return this;
	}
	
	/**
	 * Sets whether to enable TCP keep-alive for client connections.<br>
	 *
	 * @param keepAlive True to enable keep-alive
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder keepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}
	
	/**
	 * Sets the TLS protocols to enable on the server socket.<br>
	 * An empty list uses the socket default.<br>
	 *
	 * @param enabledProtocols The protocols to enable, e.g. {@code List.of(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_2)}
	 * @return This builder for method chaining
	 * @throws NullPointerException If the enabled protocols list is null
	 */
	public @NonNull SslServerConfigBuilder enabledProtocols(@NonNull List<TlsProtocol> enabledProtocols) {
		this.enabledProtocols = Objects.requireNonNull(enabledProtocols, "Enabled protocols must not be null");
		return this;
	}
	
	/**
	 * Sets the cipher suites to enable on the server socket.<br>
	 * An empty list uses the socket default.<br>
	 *
	 * @param enabledCipherSuites The cipher suites to enable
	 * @return This builder for method chaining
	 * @throws NullPointerException If the enabled cipher suites list is null
	 */
	public @NonNull SslServerConfigBuilder enabledCipherSuites(@NonNull List<String> enabledCipherSuites) {
		this.enabledCipherSuites = Objects.requireNonNull(enabledCipherSuites, "Enabled cipher suites must not be null");
		return this;
	}
	
	/**
	 * Sets how the server handles client certificate authentication (mutual TLS).<br>
	 *
	 * @param clientAuth The client authentication mode
	 * @return This builder for method chaining
	 * @throws NullPointerException If the client auth mode is null
	 */
	public @NonNull SslServerConfigBuilder clientAuth(@NonNull SslClientAuth clientAuth) {
		this.clientAuth = Objects.requireNonNull(clientAuth, "Client auth must not be null");
		return this;
	}
	
	/**
	 * Sets the executor strategy for handling concurrent client connections.<br>
	 *
	 * @param executorStrategy The executor strategy
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder executorStrategy(@NonNull ClientExecutorStrategy executorStrategy) {
		this.executorStrategy = executorStrategy;
		return this;
	}
	
	/**
	 * Sets the handler called when a client connects.<br>
	 *
	 * @param onClientConnect The connection handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder onClientConnect(@Nullable ConnectEventHandler onClientConnect) {
		this.onClientConnect = onClientConnect;
		return this;
	}
	
	/**
	 * Sets the handler called when a client disconnects.<br>
	 *
	 * @param onClientDisconnect The disconnection handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder onClientDisconnect(@Nullable DisconnectEventHandler onClientDisconnect) {
		this.onClientDisconnect = onClientDisconnect;
		return this;
	}
	
	/**
	 * Sets the handler called when a message is received from a client.<br>
	 *
	 * @param onMessage The message handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder onMessage(@Nullable MessageEventHandler<SslServer, SslConnection> onMessage) {
		this.onMessage = onMessage;
		return this;
	}
	
	/**
	 * Sets the handler that takes over the whole connection.<br>
	 * <p>
	 *     The handler is called once per client after the TLS handshake has completed, on the thread the server assigned to it,
	 *     and owns the connection until it returns.<br>
	 *     While it runs, the server does not read from the connection, so the handler can read from {@link SslConnection#getInputStream()}
	 *     and write to {@link SslConnection#getOutputStream()} exactly as its protocol requires.
	 * </p>
	 * <p>
	 *     This replaces the built-in read loop, so it cannot be combined with {@link #onMessage(MessageEventHandler)}.<br>
	 *     Setting both makes {@link #build()} fail.
	 * </p>
	 *
	 * @param onConnection The connection handler, or null to use the built-in read loop
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder onConnection(@Nullable ConnectionHandler<SslServer, SslConnection> onConnection) {
		this.onConnection = onConnection;
		return this;
	}
	
	/**
	 * Sets the error event handler.<br>
	 *
	 * @param onError The error handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder onError(@Nullable ErrorEventHandler onError) {
		this.onError = onError;
		return this;
	}
	
	/**
	 * Builds a new SSL server configuration with the configured values.<br>
	 *
	 * @return A new configuration instance
	 * @throws IllegalArgumentException If both a message handler and a connection handler are set
	 */
	public @NonNull SslServerConfig build() {
		return new SslServerConfig(
			this.backlog, this.clientBufferSize, this.framing, this.clientReadTimeout, this.tcpNoDelay, this.keepAlive, this.sslContext, this.enabledProtocols, this.enabledCipherSuites, this.clientAuth, this.executorStrategy,
			this.onClientConnect, this.onClientDisconnect, this.onMessage, this.onConnection, this.onError
		);
	}
}
