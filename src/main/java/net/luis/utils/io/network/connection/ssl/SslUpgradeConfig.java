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

import net.luis.utils.io.network.connection.tcp.TcpConnection;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

/**
 * The TLS settings of the client side of a secured connection.<br>
 * <p>
 *     This record holds only TLS specific options, the transport settings are taken from the connection that is secured.<br>
 *     It is used by {@link TcpConnection#upgrade(SslUpgradeConfig)} to upgrade an open plain connection,
 *     which is how protocols such as {@code STARTTLS} switch to a secure channel,
 *     and it is the TLS part of an {@link SslClientConfig}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SslUpgradeConfig upgrade = SslUpgradeConfig.builder()
 *     .enabledProtocols(List.of(TlsProtocol.TLS_V1_3))
 *     .verifyHostname(true)
 *     .build();
 *
 * try (TcpConnection connection = TcpConnection.connectTo(server)) {
 *     connection.send("STARTTLS".getBytes());
 *     if (isPositiveReply(connection.receive())) {
 *         try (SslConnection secure = connection.upgrade(upgrade)) {
 *             secure.send("Hello, Server!".getBytes());
 *         }
 *     }
 * }
 * }</pre>
 *
 * @see SslUpgradeConfigBuilder
 * @see SslClientConfig
 * @see TcpConnection#upgrade(SslUpgradeConfig)
 *
 * @author Luis-St
 *
 * @param sslContext The SSL context to use, or null to use the JVM default ({@link SSLContext#getDefault()})
 * @param enabledProtocols The TLS protocols to enable (empty for the socket default), e.g. {@link TlsProtocol#TLS_V1_3}
 * @param enabledCipherSuites The cipher suites to enable (empty for the socket default)
 * @param verifyHostname Whether to verify the server hostname against its certificate (HTTPS endpoint identification)
 */
public record SslUpgradeConfig(
	@Nullable SSLContext sslContext,
	@NonNull List<TlsProtocol> enabledProtocols,
	@NonNull List<String> enabledCipherSuites,
	boolean verifyHostname
) {
	
	/**
	 * Default configuration for TLS upgrades.<br>
	 * <ul>
	 *     <li>{@link #sslContext} = {@code null} (JVM default)</li>
	 *     <li>{@link #enabledProtocols} = {@code []} (socket default)</li>
	 *     <li>{@link #enabledCipherSuites} = {@code []} (socket default)</li>
	 *     <li>{@link #verifyHostname} = {@code true}</li>
	 * </ul>
	 */
	public static final SslUpgradeConfig DEFAULT = new SslUpgradeConfig(null, List.of(), List.of(), true);
	
	/**
	 * Constructs a new TLS upgrade configuration.<br>
	 * The protocol and cipher suite lists are copied defensively into immutable lists.<br>
	 *
	 * @param sslContext The SSL context to use, or null for the JVM default
	 * @param enabledProtocols The TLS protocols to enable
	 * @param enabledCipherSuites The cipher suites to enable
	 * @param verifyHostname Whether to verify the server hostname
	 * @throws NullPointerException If enabled protocols or enabled cipher suites is null, or if enabled protocols contains null
	 */
	public SslUpgradeConfig {
		Objects.requireNonNull(enabledProtocols, "Enabled protocols must not be null");
		Objects.requireNonNull(enabledCipherSuites, "Enabled cipher suites must not be null");
		
		enabledProtocols = List.copyOf(enabledProtocols);
		enabledCipherSuites = List.copyOf(enabledCipherSuites);
	}
	
	/**
	 * Creates a new builder for constructing a TLS upgrade configuration.<br>
	 * @return A new builder with default values
	 */
	public static @NonNull SslUpgradeConfigBuilder builder() {
		return new SslUpgradeConfigBuilder();
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
