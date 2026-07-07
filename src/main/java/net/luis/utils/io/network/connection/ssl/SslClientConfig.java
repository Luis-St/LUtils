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
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Configuration options for SSL/TLS clients.<br>
 * This record extends the plain TCP settings with TLS-specific options such as the {@link SSLContext},<br>
 * the enabled protocols and cipher suites, and hostname verification.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SslClientConfig config = SslClientConfig.builder()
 *     .connectTimeout(Duration.ofSeconds(10))
 *     .enabledProtocols(List.of("TLSv1.3", "TLSv1.2"))
 *     .verifyHostname(true)
 *     .onConnect(event -> System.out.println("Secure connection established!"))
 *     .build();
 *
 * try (SslClient client = new SslClient(config)) {
 *     client.connect(serverEndpoint);
 * }
 * }</pre>
 *
 * @see SslClientConfigBuilder
 * @see SslClient
 *
 * @author Luis-St
 *
 * @param connectTimeout Maximum time to wait for connection establishment
 * @param readTimeout Maximum time to wait for read operations (Duration.ZERO for infinite)
 * @param writeTimeout Maximum time to wait for write operations (Duration.ZERO for infinite)
 * @param bufferSize Size of the read/write buffers in bytes
 * @param tcpNoDelay Whether to disable Nagle's algorithm (TCP_NODELAY)
 * @param keepAlive Whether to enable TCP keep-alive (SO_KEEPALIVE)
 * @param sslContext The SSL context to use, or null to use the JVM default ({@link SSLContext#getDefault()})
 * @param enabledProtocols The TLS protocols to enable (empty for the socket default), e.g. {@code "TLSv1.3"}
 * @param enabledCipherSuites The cipher suites to enable (empty for the socket default)
 * @param verifyHostname Whether to verify the server hostname against its certificate (HTTPS endpoint identification)
 * @param onConnect Handler called when the secure connection is established
 * @param onDisconnect Handler called when the connection is closed
 * @param onError Handler called when an error occurs
 */
public record SslClientConfig(
	@NonNull Duration connectTimeout,
	@NonNull Duration readTimeout,
	@NonNull Duration writeTimeout,
	int bufferSize,
	boolean tcpNoDelay,
	boolean keepAlive,
	@Nullable SSLContext sslContext,
	@NonNull List<String> enabledProtocols,
	@NonNull List<String> enabledCipherSuites,
	boolean verifyHostname,
	@Nullable ConnectionEventHandler onConnect,
	@Nullable ConnectionEventHandler onDisconnect,
	@Nullable ErrorEventHandler onError
) {
	
	/**
	 * Default configuration for SSL clients.<br>
	 * <ul>
	 *     <li>{@link #connectTimeout} = {@code 30 seconds}</li>
	 *     <li>{@link #readTimeout} = {@code Duration.ZERO} (infinite)</li>
	 *     <li>{@link #writeTimeout} = {@code Duration.ZERO} (infinite)</li>
	 *     <li>{@link #bufferSize} = {@code 8192}</li>
	 *     <li>{@link #tcpNoDelay} = {@code true}</li>
	 *     <li>{@link #keepAlive} = {@code true}</li>
	 *     <li>{@link #sslContext} = {@code null} (JVM default)</li>
	 *     <li>{@link #enabledProtocols} = {@code []} (socket default)</li>
	 *     <li>{@link #enabledCipherSuites} = {@code []} (socket default)</li>
	 *     <li>{@link #verifyHostname} = {@code true}</li>
	 *     <li>All handlers = {@code null}</li>
	 * </ul>
	 */
	public static final SslClientConfig DEFAULT = new SslClientConfig(Duration.ofSeconds(30), Duration.ZERO, Duration.ZERO, 8192, true, true, null, List.of(), List.of(), true, null, null, null);
	
	/**
	 * Constructs a new SSL client configuration.<br>
	 * The protocol and cipher suite lists are copied defensively into immutable lists.<br>
	 *
	 * @param connectTimeout Maximum time to wait for connection establishment
	 * @param readTimeout Maximum time to wait for read operations
	 * @param writeTimeout Maximum time to wait for write operations
	 * @param bufferSize Size of the read/write buffers in bytes
	 * @param tcpNoDelay Whether to disable Nagle's algorithm
	 * @param keepAlive Whether to enable TCP keep-alive
	 * @param sslContext The SSL context to use, or null for the JVM default
	 * @param enabledProtocols The TLS protocols to enable
	 * @param enabledCipherSuites The cipher suites to enable
	 * @param verifyHostname Whether to verify the server hostname
	 * @param onConnect Handler called when the connection is established
	 * @param onDisconnect Handler called when the connection is closed
	 * @param onError Handler called when an error occurs
	 * @throws NullPointerException If connect timeout, read timeout, write timeout, enabled protocols, or enabled cipher suites is null
	 * @throws IllegalArgumentException If bufferSize is less than 1
	 */
	public SslClientConfig {
		Objects.requireNonNull(connectTimeout, "Connect timeout must not be null");
		Objects.requireNonNull(readTimeout, "Read timeout must not be null");
		Objects.requireNonNull(writeTimeout, "Write timeout must not be null");
		Objects.requireNonNull(enabledProtocols, "Enabled protocols must not be null");
		Objects.requireNonNull(enabledCipherSuites, "Enabled cipher suites must not be null");
		
		if (bufferSize < 1) {
			throw new IllegalArgumentException("Buffer size must be at least 1: " + bufferSize);
		}
		
		enabledProtocols = List.copyOf(enabledProtocols);
		enabledCipherSuites = List.copyOf(enabledCipherSuites);
	}
	
	/**
	 * Creates a new builder for constructing SSL client configuration.<br>
	 * @return A new builder with default values
	 */
	public static @NonNull SslClientConfigBuilder builder() {
		return new SslClientConfigBuilder();
	}
	
	/**
	 * Resolves the SSL context to use for this configuration.<br>
	 * If no context was configured, the JVM default context is returned.<br>
	 *
	 * @return The configured SSL context, or the JVM default if none was set
	 * @throws NoSuchAlgorithmException If the default SSL context cannot be created
	 */
	public @NonNull SSLContext resolveSslContext() throws NoSuchAlgorithmException {
		return this.sslContext != null ? this.sslContext : SSLContext.getDefault();
	}
}
