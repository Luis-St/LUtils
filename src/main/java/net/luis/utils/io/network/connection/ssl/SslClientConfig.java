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

import net.luis.utils.io.network.connection.tcp.TcpClientConfig;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Configuration options for connections opened by {@link SslConnection#connectTo}.<br>
 * <p>
 *     A secured connection is a plain connection with TLS layered over it,
 *     so this record combines the transport settings of a {@link TcpClientConfig} with the TLS settings of an {@link SslUpgradeConfig}.<br>
 *     The builder exposes the options of both parts directly.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SslClientConfig config = SslClientConfig.builder()
 *     .connectTimeout(Duration.ofSeconds(10))
 *     .enabledProtocols(List.of(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_2))
 *     .verifyHostname(true)
 *     .build();
 *
 * try (SslConnection connection = SslConnection.connectTo(serverEndpoint, config)) {
 *     connection.send("Hello".getBytes());
 * }
 * }</pre>
 *
 * @see SslClientConfigBuilder
 * @see SslConnection
 *
 * @author Luis-St
 *
 * @param transport The transport settings of the connection
 * @param tls The TLS settings of the connection
 */
public record SslClientConfig(
	@NonNull TcpClientConfig transport,
	@NonNull SslUpgradeConfig tls
) {
	
	/**
	 * Default configuration for secured connections.<br>
	 * It combines {@link TcpClientConfig#DEFAULT} with {@link SslUpgradeConfig#DEFAULT}.<br>
	 */
	public static final SslClientConfig DEFAULT = new SslClientConfig(TcpClientConfig.DEFAULT, SslUpgradeConfig.DEFAULT);
	
	/**
	 * Constructs a new SSL client configuration.<br>
	 *
	 * @param transport The transport settings of the connection
	 * @param tls The TLS settings of the connection
	 * @throws NullPointerException If the transport or the TLS settings are null
	 */
	public SslClientConfig {
		Objects.requireNonNull(transport, "Transport config must not be null");
		Objects.requireNonNull(tls, "Tls config must not be null");
	}
	
	/**
	 * Creates a new builder for constructing an SSL client configuration.<br>
	 * @return A new builder with default values
	 */
	public static @NonNull SslClientConfigBuilder builder() {
		return new SslClientConfigBuilder();
	}
}
