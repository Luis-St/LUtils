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

import net.luis.utils.io.network.connection.ErrorHandler;
import net.luis.utils.io.network.connection.Framing;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import net.luis.utils.io.network.connection.tcp.TcpServerConfig;
import net.luis.utils.io.network.connection.tcp.TcpServerConfigBuilder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Builder class for constructing an SSL server configuration.<br>
 * Provides a fluent API for the transport and the TLS options of an SSL server.<br>
 * <p>
 *     The {@link SSLContext} is required and must be supplied when the builder is created.<br>
 *     The transport options default to the values of {@link TcpServerConfig#DEFAULT},
 *     the TLS options default to the socket defaults without client authentication.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SslServerConfig config = SslServerConfig.builder(sslContext)
 *     .backlog(100)
 *     .clientAuth(SslClientAuth.REQUESTED)
 *     .executorStrategy(ClientExecutorStrategy.fixedPool(10))
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
	 * The builder of the transport settings.<br>
	 */
	private final TcpServerConfigBuilder transport = TcpServerConfig.builder();
	/**
	 * The TLS protocols to enable.<br>
	 */
	private List<TlsProtocol> enabledProtocols = List.of();
	/**
	 * The cipher suites to enable.<br>
	 */
	private List<String> enabledCipherSuites = List.of();
	/**
	 * How the server handles client certificate authentication.<br>
	 */
	private SslClientAuth clientAuth = SslClientAuth.NONE;
	
	/**
	 * Constructs a new builder with the required SSL context and default values.<br>
	 *
	 * @param sslContext The SSL context holding the server certificate and private key
	 * @throws NullPointerException If the SSL context is null
	 */
	SslServerConfigBuilder(@NonNull SSLContext sslContext) {
		this.sslContext = Objects.requireNonNull(sslContext, "SSL context must not be null");
	}
	
	/**
	 * Sets the maximum number of pending connections in the queue.<br>
	 *
	 * @param backlog The backlog size, which must be at least 1
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder backlog(int backlog) {
		this.transport.backlog(backlog);
		return this;
	}
	
	/**
	 * Sets the maximum length of a message in bytes.<br>
	 *
	 * @param maxMessageSize The max message size, which must be at least 1
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder maxMessageSize(int maxMessageSize) {
		this.transport.maxMessageSize(maxMessageSize);
		return this;
	}
	
	/**
	 * Sets the framing that delimits messages on the stream.<br>
	 *
	 * @param framing The framing
	 * @return This builder for method chaining
	 * @throws NullPointerException If the framing is null
	 */
	public @NonNull SslServerConfigBuilder framing(@NonNull Framing framing) {
		this.transport.framing(framing);
		return this;
	}
	
	/**
	 * Sets the maximum time a receive or the handshake on a connection waits for data.<br>
	 *
	 * @param clientReadTimeout The read timeout, which must be positive
	 * @return This builder for method chaining
	 * @throws NullPointerException If the read timeout is null
	 */
	public @NonNull SslServerConfigBuilder clientReadTimeout(@NonNull Duration clientReadTimeout) {
		this.transport.clientReadTimeout(clientReadTimeout);
		return this;
	}
	
	/**
	 * Removes the read timeout of the connections, so that a receive waits forever.<br>
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder noClientReadTimeout() {
		this.transport.noClientReadTimeout();
		return this;
	}
	
	/**
	 * Sets whether to disable Nagle's algorithm for the connections.<br>
	 *
	 * @param tcpNoDelay True to disable Nagle's algorithm
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder tcpNoDelay(boolean tcpNoDelay) {
		this.transport.tcpNoDelay(tcpNoDelay);
		return this;
	}
	
	/**
	 * Sets whether to enable TCP keep-alive for the connections.<br>
	 *
	 * @param keepAlive True to enable keep-alive
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder keepAlive(boolean keepAlive) {
		this.transport.keepAlive(keepAlive);
		return this;
	}
	
	/**
	 * Sets the strategy that creates the executor the connections are served on.<br>
	 *
	 * @param executorStrategy The executor strategy
	 * @return This builder for method chaining
	 * @throws NullPointerException If the executor strategy is null
	 */
	public @NonNull SslServerConfigBuilder executorStrategy(@NonNull ClientExecutorStrategy executorStrategy) {
		this.transport.executorStrategy(executorStrategy);
		return this;
	}
	
	/**
	 * Sets the handler for failures on the threads of the server, including failed handshakes.<br>
	 *
	 * @param onError The error handler, or null to use the uncaught exception handler
	 * @return This builder for method chaining
	 */
	public @NonNull SslServerConfigBuilder onError(@Nullable ErrorHandler onError) {
		this.transport.onError(onError);
		return this;
	}
	
	/**
	 * Sets the TLS protocols to enable.<br>
	 * An empty list uses the socket default.<br>
	 *
	 * @param enabledProtocols The protocols to enable
	 * @return This builder for method chaining
	 * @throws NullPointerException If the enabled protocols list is null
	 */
	public @NonNull SslServerConfigBuilder enabledProtocols(@NonNull List<TlsProtocol> enabledProtocols) {
		this.enabledProtocols = Objects.requireNonNull(enabledProtocols, "Enabled protocols must not be null");
		return this;
	}
	
	/**
	 * Sets the cipher suites to enable.<br>
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
	 * Builds a new SSL server configuration with the configured values.<br>
	 *
	 * @return A new configuration instance
	 * @throws IllegalArgumentException If the backlog or the max message size is less than 1 or the client read timeout is not positive
	 */
	public @NonNull SslServerConfig build() {
		return new SslServerConfig(this.transport.build(), this.sslContext, this.enabledProtocols, this.enabledCipherSuites, this.clientAuth);
	}
}
