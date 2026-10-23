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
import net.luis.utils.io.network.connection.tcp.TcpServerConfig;
import org.jspecify.annotations.NonNull;

import javax.net.ssl.SSLContext;
import java.util.List;
import java.util.Objects;

/**
 * Configuration options for SSL servers.<br>
 * <p>
 *     This record combines the transport settings of a {@link TcpServerConfig} with the TLS settings of the server side.<br>
 *     The TLS settings are also used by {@link TcpConnection#upgrade(SslServerConfig)}, which secures a plain connection on the server side.<br>
 *     The builder exposes the options of both parts directly.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SslServerConfig config = SslServerConfig.builder(sslContext)
 *     .backlog(100)
 *     .enabledProtocols(List.of(TlsProtocol.TLS_V1_3))
 *     .clientAuth(SslClientAuth.REQUIRED)
 *     .build();
 * }</pre>
 *
 * @see SslServerConfigBuilder
 * @see SslServer
 *
 * @author Luis-St
 *
 * @param transport The transport settings of the server
 * @param sslContext The SSL context holding the server certificate and private key
 * @param enabledProtocols The TLS protocols to enable (empty for the socket default)
 * @param enabledCipherSuites The cipher suites to enable (empty for the socket default)
 * @param clientAuth How the server handles client certificate authentication
 */
public record SslServerConfig(
	@NonNull TcpServerConfig transport,
	@NonNull SSLContext sslContext,
	@NonNull List<TlsProtocol> enabledProtocols,
	@NonNull List<String> enabledCipherSuites,
	@NonNull SslClientAuth clientAuth
) {
	
	/**
	 * Constructs a new SSL server configuration.<br>
	 * The protocol and cipher suite lists are copied into immutable lists.<br>
	 *
	 * @param transport The transport settings of the server
	 * @param sslContext The SSL context holding the server certificate and private key
	 * @param enabledProtocols The TLS protocols to enable
	 * @param enabledCipherSuites The cipher suites to enable
	 * @param clientAuth How the server handles client certificate authentication
	 * @throws NullPointerException If any parameter is null or a list contains null
	 */
	public SslServerConfig {
		Objects.requireNonNull(transport, "Transport config must not be null");
		Objects.requireNonNull(sslContext, "SSL context must not be null");
		Objects.requireNonNull(enabledProtocols, "Enabled protocols must not be null");
		Objects.requireNonNull(enabledCipherSuites, "Enabled cipher suites must not be null");
		Objects.requireNonNull(clientAuth, "Client auth must not be null");
		
		enabledProtocols = List.copyOf(enabledProtocols);
		enabledCipherSuites = List.copyOf(enabledCipherSuites);
	}
	
	/**
	 * Creates a new builder for constructing an SSL server configuration.<br>
	 *
	 * @param sslContext The SSL context holding the server certificate and private key
	 * @return A new builder with default values
	 * @throws NullPointerException If the SSL context is null
	 */
	public static @NonNull SslServerConfigBuilder builder(@NonNull SSLContext sslContext) {
		return new SslServerConfigBuilder(sslContext);
	}
}
