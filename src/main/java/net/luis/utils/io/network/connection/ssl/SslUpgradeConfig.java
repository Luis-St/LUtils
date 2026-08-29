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

import net.luis.utils.io.network.connection.tcp.TcpClient;
import net.luis.utils.io.network.connection.tcp.TcpClientConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.util.List;
import java.util.Objects;

/**
 * Configuration options for upgrading an established plaintext connection to TLS.<br>
 * This record holds only the TLS specific options, everything else is taken from the transport configuration of the connection that is upgraded.<br>
 * <p>
 *     An upgrade configuration is combined with a {@link TcpClientConfig} through {@link #toClientConfig(TcpClientConfig)},
 *     which yields the {@link SslClientConfig} that describes the secured connection.<br>
 *     The transport settings such as timeouts, buffer size, framing,<br>
 *     socket options and event handlers are carried over unchanged, while the TLS settings are taken from this configuration.
 * </p>
 * <p>
 *     This is the configuration type accepted by {@link TcpClient#upgrade(SslUpgradeConfig)},<br>
 *     which layers TLS over an already connected client and hands the secured connection over as an {@link SslClient}.<br>
 *     Protocols such as {@code STARTTLS} use this to switch to a secure channel after the connection was established.
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
 * try (TcpClient client = TcpClient.connectTo(server)) {
 *     client.send("STARTTLS".getBytes());
 *     if (isPositiveReply(client.receive())) {
 *         try (SslClient secure = client.upgrade(upgrade)) {
 *             secure.send("Hello, Server!".getBytes());
 *         }
 *     }
 * }
 * }</pre>
 *
 * @see SslUpgradeConfigBuilder
 * @see SslClientConfig
 * @see TcpClient#upgrade(SslUpgradeConfig)
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
	 * Combines this upgrade configuration with the given TCP client configuration into an SSL client configuration.<br>
	 * <p>
	 *     The connect timeout, read timeout, write timeout, buffer size, framing flag and socket options are taken from the given configuration, together with its event handlers.<br>
	 *     The SSL context, enabled protocols, enabled cipher suites and hostname verification are taken from this configuration.
	 * </p>
	 *
	 * @param config The TCP client configuration to take the transport settings from
	 * @return A new SSL client configuration combining both configurations
	 * @throws NullPointerException If config is null
	 */
	public @NonNull SslClientConfig toClientConfig(@NonNull TcpClientConfig config) {
		Objects.requireNonNull(config, "Config must not be null");
		
		return new SslClientConfig(
			config.connectTimeout(),
			config.readTimeout(),
			config.writeTimeout(),
			config.bufferSize(),
			config.framing(),
			config.tcpNoDelay(),
			config.keepAlive(),
			this.sslContext,
			this.enabledProtocols,
			this.enabledCipherSuites,
			this.verifyHostname,
			config.onConnect(),
			config.onDisconnect(),
			config.onError()
		);
	}
}
