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

import net.luis.utils.io.network.connection.event.ConnectionEventHandler;
import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Builder class for constructing SSL client configuration.<br>
 * Provides a fluent API for setting individual configuration options.<br>
 * <p>
 *     All options default to values matching {@link SSLClientConfig#DEFAULT}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SSLClientConfig config = SSLClientConfig.builder()
 *     .connectTimeout(Duration.ofSeconds(10))
 *     .sslContext(myContext)
 *     .enabledProtocols(List.of("TLSv1.3"))
 *     .verifyHostname(true)
 *     .onConnect(event -> System.out.println("Connected to " + event.remoteEndpoint()))
 *     .build();
 * }</pre>
 *
 * @see SSLClientConfig
 *
 * @author Luis-St
 */
public final class SSLClientConfigBuilder {
	
	/**
	 * The maximum time to wait for connection establishment.<br>
	 */
	private Duration connectTimeout = Duration.ofSeconds(30);
	/**
	 * The maximum time to wait for read operations.<br>
	 */
	private Duration readTimeout = Duration.ZERO;
	/**
	 * The maximum time to wait for write operations.<br>
	 */
	private Duration writeTimeout = Duration.ZERO;
	/**
	 * The size of the read/write buffers in bytes.<br>
	 */
	private int bufferSize = 8192;
	/**
	 * Whether to disable Nagle's algorithm (TCP_NODELAY).<br>
	 */
	private boolean tcpNoDelay = true;
	/**
	 * Whether to enable TCP keep-alive (SO_KEEPALIVE).<br>
	 */
	private boolean keepAlive = true;
	/**
	 * The SSL context to use, or null for the JVM default.<br>
	 */
	private @Nullable SSLContext sslContext;
	/**
	 * The TLS protocols to enable.<br>
	 */
	private List<String> enabledProtocols = List.of();
	/**
	 * The cipher suites to enable.<br>
	 */
	private List<String> enabledCipherSuites = List.of();
	/**
	 * Whether to verify the server hostname against its certificate.<br>
	 */
	private boolean verifyHostname = true;
	/**
	 * The handler called when the connection is established.<br>
	 */
	private @Nullable ConnectionEventHandler onConnect;
	/**
	 * The handler called when the connection is closed.<br>
	 */
	private @Nullable ConnectionEventHandler onDisconnect;
	/**
	 * The handler called when an error occurs.<br>
	 */
	private @Nullable ErrorEventHandler onError;
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	SSLClientConfigBuilder() {}
	
	/**
	 * Sets the maximum time to wait for connection establishment.<br>
	 *
	 * @param connectTimeout The connect timeout
	 * @return This builder for method chaining
	 * @throws NullPointerException If the connect timeout is null
	 */
	public @NonNull SSLClientConfigBuilder connectTimeout(@NonNull Duration connectTimeout) {
		this.connectTimeout = Objects.requireNonNull(connectTimeout, "Connect timeout must not be null");
		return this;
	}
	
	/**
	 * Sets the maximum time to wait for read operations.<br>
	 * Use {@link Duration#ZERO} for infinite timeout.<br>
	 *
	 * @param readTimeout The read timeout
	 * @return This builder for method chaining
	 * @throws NullPointerException If the read timeout is null
	 */
	public @NonNull SSLClientConfigBuilder readTimeout(@NonNull Duration readTimeout) {
		this.readTimeout = Objects.requireNonNull(readTimeout, "Read timeout must not be null");
		return this;
	}
	
	/**
	 * Sets the maximum time to wait for write operations.<br>
	 * Use {@link Duration#ZERO} for infinite timeout.<br>
	 *
	 * @param writeTimeout The write timeout
	 * @return This builder for method chaining
	 * @throws NullPointerException If the write timeout is null
	 */
	public @NonNull SSLClientConfigBuilder writeTimeout(@NonNull Duration writeTimeout) {
		this.writeTimeout = Objects.requireNonNull(writeTimeout, "Write timeout must not be null");
		return this;
	}
	
	/**
	 * Sets the size of the read/write buffers in bytes.<br>
	 *
	 * @param bufferSize The buffer size (must be at least 1)
	 * @return This builder for method chaining
	 */
	public @NonNull SSLClientConfigBuilder bufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
		return this;
	}
	
	/**
	 * Sets whether to disable Nagle's algorithm (TCP_NODELAY).<br>
	 * When enabled, small packets are sent immediately without waiting.<br>
	 *
	 * @param tcpNoDelay True to disable Nagle's algorithm
	 * @return This builder for method chaining
	 */
	public @NonNull SSLClientConfigBuilder tcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
		return this;
	}
	
	/**
	 * Sets whether to enable TCP keep-alive (SO_KEEPALIVE).<br>
	 *
	 * @param keepAlive True to enable keep-alive
	 * @return This builder for method chaining
	 */
	public @NonNull SSLClientConfigBuilder keepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}
	
	/**
	 * Sets the SSL context to use for creating secure sockets.<br>
	 * If null, the JVM default context ({@link SSLContext#getDefault()}) is used.<br>
	 *
	 * @param sslContext The SSL context, or null to use the JVM default
	 * @return This builder for method chaining
	 */
	public @NonNull SSLClientConfigBuilder sslContext(@Nullable SSLContext sslContext) {
		this.sslContext = sslContext;
		return this;
	}
	
	/**
	 * Sets the TLS protocols to enable on the socket.<br>
	 * An empty list uses the socket default.<br>
	 *
	 * @param enabledProtocols The protocols to enable, e.g. {@code List.of("TLSv1.3", "TLSv1.2")}
	 * @return This builder for method chaining
	 * @throws NullPointerException If the enabled protocols list is null
	 */
	public @NonNull SSLClientConfigBuilder enabledProtocols(@NonNull List<String> enabledProtocols) {
		this.enabledProtocols = Objects.requireNonNull(enabledProtocols, "Enabled protocols must not be null");
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
	public @NonNull SSLClientConfigBuilder enabledCipherSuites(@NonNull List<String> enabledCipherSuites) {
		this.enabledCipherSuites = Objects.requireNonNull(enabledCipherSuites, "Enabled cipher suites must not be null");
		return this;
	}
	
	/**
	 * Sets whether to verify the server hostname against its certificate.<br>
	 * When enabled, HTTPS endpoint identification is applied, rejecting certificates that do not match the target host.<br>
	 *
	 * @param verifyHostname True to enable hostname verification
	 * @return This builder for method chaining
	 */
	public @NonNull SSLClientConfigBuilder verifyHostname(boolean verifyHostname) {
		this.verifyHostname = verifyHostname;
		return this;
	}
	
	/**
	 * Sets the handler called when the connection is established.<br>
	 *
	 * @param onConnect The connection handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull SSLClientConfigBuilder onConnect(@Nullable ConnectionEventHandler onConnect) {
		this.onConnect = onConnect;
		return this;
	}
	
	/**
	 * Sets the handler called when the connection is closed.<br>
	 *
	 * @param onDisconnect The disconnection handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull SSLClientConfigBuilder onDisconnect(@Nullable ConnectionEventHandler onDisconnect) {
		this.onDisconnect = onDisconnect;
		return this;
	}
	
	/**
	 * Sets the error event handler.<br>
	 *
	 * @param onError The error handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull SSLClientConfigBuilder onError(@Nullable ErrorEventHandler onError) {
		this.onError = onError;
		return this;
	}
	
	/**
	 * Builds a new SSL client configuration with the configured values.<br>
	 * @return A new configuration instance
	 */
	public @NonNull SSLClientConfig build() {
		return new SSLClientConfig(this.connectTimeout, this.readTimeout, this.writeTimeout, this.bufferSize, this.tcpNoDelay, this.keepAlive, this.sslContext, this.enabledProtocols, this.enabledCipherSuites, this.verifyHostname, this.onConnect, this.onDisconnect, this.onError);
	}
}
