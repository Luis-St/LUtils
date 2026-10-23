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

import net.luis.utils.io.network.connection.Framing;
import net.luis.utils.io.network.connection.tcp.TcpClientConfig;
import net.luis.utils.io.network.connection.tcp.TcpClientConfigBuilder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.List;

/**
 * Builder class for constructing an SSL client configuration.<br>
 * Provides a fluent API for the transport and the TLS options of a secured connection.<br>
 * <p>
 *     All options default to the values of {@link SslClientConfig#DEFAULT}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SslClientConfig config = SslClientConfig.builder()
 *     .connectTimeout(Duration.ofSeconds(10))
 *     .sslContext(myContext)
 *     .enabledProtocols(List.of(TlsProtocol.TLS_V1_3))
 *     .build();
 * }</pre>
 *
 * @see SslClientConfig
 *
 * @author Luis-St
 */
public final class SslClientConfigBuilder {
	
	/**
	 * The builder of the transport settings.<br>
	 */
	private final TcpClientConfigBuilder transport = TcpClientConfig.builder();
	/**
	 * The builder of the TLS settings.<br>
	 */
	private final SslUpgradeConfigBuilder tls = SslUpgradeConfig.builder();
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	SslClientConfigBuilder() {}
	
	/**
	 * Sets the maximum time to wait for the connection to be established.<br>
	 *
	 * @param connectTimeout The connect timeout, which must be positive
	 * @return This builder for method chaining
	 * @throws NullPointerException If the connect timeout is null
	 */
	public @NonNull SslClientConfigBuilder connectTimeout(@NonNull Duration connectTimeout) {
		this.transport.connectTimeout(connectTimeout);
		return this;
	}
	
	/**
	 * Sets the maximum time a receive or the handshake waits for data.<br>
	 *
	 * @param readTimeout The read timeout, which must be positive
	 * @return This builder for method chaining
	 * @throws NullPointerException If the read timeout is null
	 */
	public @NonNull SslClientConfigBuilder readTimeout(@NonNull Duration readTimeout) {
		this.transport.readTimeout(readTimeout);
		return this;
	}
	
	/**
	 * Removes the read timeout, so that a receive waits forever.<br>
	 * @return This builder for method chaining
	 */
	public @NonNull SslClientConfigBuilder noReadTimeout() {
		this.transport.noReadTimeout();
		return this;
	}
	
	/**
	 * Sets the maximum length of a message in bytes.<br>
	 *
	 * @param maxMessageSize The max message size, which must be at least 1
	 * @return This builder for method chaining
	 */
	public @NonNull SslClientConfigBuilder maxMessageSize(int maxMessageSize) {
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
	public @NonNull SslClientConfigBuilder framing(@NonNull Framing framing) {
		this.transport.framing(framing);
		return this;
	}
	
	/**
	 * Sets whether to disable Nagle's algorithm (TCP_NODELAY).<br>
	 *
	 * @param tcpNoDelay True to disable Nagle's algorithm
	 * @return This builder for method chaining
	 */
	public @NonNull SslClientConfigBuilder tcpNoDelay(boolean tcpNoDelay) {
		this.transport.tcpNoDelay(tcpNoDelay);
		return this;
	}
	
	/**
	 * Sets whether to enable TCP keep-alive (SO_KEEPALIVE).<br>
	 *
	 * @param keepAlive True to enable keep-alive
	 * @return This builder for method chaining
	 */
	public @NonNull SslClientConfigBuilder keepAlive(boolean keepAlive) {
		this.transport.keepAlive(keepAlive);
		return this;
	}
	
	/**
	 * Sets the SSL context to use for creating secure sockets.<br>
	 *
	 * @param sslContext The SSL context, or null to use the JVM default
	 * @return This builder for method chaining
	 */
	public @NonNull SslClientConfigBuilder sslContext(@Nullable SSLContext sslContext) {
		this.tls.sslContext(sslContext);
		return this;
	}
	
	/**
	 * Sets the TLS protocols to enable on the socket.<br>
	 * An empty list uses the socket default.<br>
	 *
	 * @param enabledProtocols The protocols to enable
	 * @return This builder for method chaining
	 * @throws NullPointerException If the enabled protocols list is null
	 */
	public @NonNull SslClientConfigBuilder enabledProtocols(@NonNull List<TlsProtocol> enabledProtocols) {
		this.tls.enabledProtocols(enabledProtocols);
		return this;
	}
	
	/**
	 * Sets the cipher suites to enable on the socket.<br>
	 * An empty list uses the socket default.<br>
	 *
	 * @param enabledCipherSuites The cipher suites to enable
	 * @return This builder for method chaining
	 * @throws NullPointerException If the enabled cipher suites list is null
	 */
	public @NonNull SslClientConfigBuilder enabledCipherSuites(@NonNull List<String> enabledCipherSuites) {
		this.tls.enabledCipherSuites(enabledCipherSuites);
		return this;
	}
	
	/**
	 * Sets whether to verify the server hostname against its certificate.<br>
	 *
	 * @param verifyHostname True to enable hostname verification
	 * @return This builder for method chaining
	 */
	public @NonNull SslClientConfigBuilder verifyHostname(boolean verifyHostname) {
		this.tls.verifyHostname(verifyHostname);
		return this;
	}
	
	/**
	 * Builds a new SSL client configuration with the configured values.<br>
	 *
	 * @return A new configuration instance
	 * @throws IllegalArgumentException If a timeout is not positive or the max message size is less than 1
	 */
	public @NonNull SslClientConfig build() {
		return new SslClientConfig(this.transport.build(), this.tls.build());
	}
}
