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

package net.luis.utils.io.network.mail;

import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import net.luis.utils.io.network.connection.ssl.SslClientConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Configuration options for an {@link SmtpClient}.<br>
 * This record bundles the transport security mode, authentication strategy,<br>
 * and the reused {@link SslClientConfig} that supplies all TLS parameters<br>
 * (SSL context, protocols, cipher suites, timeouts, and hostname verification).<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SmtpClientConfig config = SmtpClientConfig.builder()
 *     .security(SmtpSecurity.STARTTLS)
 *     .auth(new SmtpAuth.Login("user@example.com", password))
 *     .ehloHostname("client.example.com")
 *     .build();
 *
 * try (SmtpClient client = new SmtpClient(config)) {
 *     client.connect("smtp.example.com", 587);
 *     client.send(message);
 * }
 * }</pre>
 *
 * @see SmtpClientConfigBuilder
 * @see SmtpClient
 *
 * @author Luis-St
 *
 * @param security The transport security mode
 * @param auth The authentication strategy
 * @param ehloHostname The hostname announced in the EHLO command, or null to resolve the local host
 * @param tlsConfig The SSL/TLS configuration reused for secure transport
 * @param defaultCharset The character set used to encode SMTP commands and authentication tokens
 * @param bufferSize The size of the read/write buffers in bytes
 * @param onError The handler called when an error occurs, or null
 */
public record SmtpClientConfig(
	@NonNull SmtpSecurity security,
	@NonNull SmtpAuth auth,
	@Nullable String ehloHostname,
	@NonNull SslClientConfig tlsConfig,
	@NonNull Charset defaultCharset,
	int bufferSize,
	@Nullable ErrorEventHandler onError
) {
	
	/**
	 * Default configuration for SMTP clients.<br>
	 * <ul>
	 *     <li>{@link #security} = {@link SmtpSecurity#STARTTLS}</li>
	 *     <li>{@link #auth} = {@link SmtpAuth.None}</li>
	 *     <li>{@link #ehloHostname} = {@code null} (resolve local host)</li>
	 *     <li>{@link #tlsConfig} = {@link SslClientConfig#DEFAULT}</li>
	 *     <li>{@link #defaultCharset} = {@code UTF-8}</li>
	 *     <li>{@link #bufferSize} = {@code 8192}</li>
	 *     <li>{@link #onError} = {@code null}</li>
	 * </ul>
	 */
	public static final SmtpClientConfig DEFAULT = new SmtpClientConfig(SmtpSecurity.STARTTLS, new SmtpAuth.None(), null, SslClientConfig.DEFAULT, StandardCharsets.UTF_8, 8192, null);
	
	/**
	 * Constructs a new SMTP client configuration.<br>
	 *
	 * @param security The transport security mode
	 * @param auth The authentication strategy
	 * @param ehloHostname The hostname announced in the EHLO command, or null to resolve the local host
	 * @param tlsConfig The SSL/TLS configuration reused for secure transport
	 * @param defaultCharset The character set used to encode SMTP commands and authentication tokens
	 * @param bufferSize The size of the read/write buffers in bytes
	 * @param onError The handler called when an error occurs, or null
	 * @throws NullPointerException If security, auth, tlsConfig, or defaultCharset is null
	 * @throws IllegalArgumentException If bufferSize is less than 1 or the EHLO hostname contains whitespace
	 */
	public SmtpClientConfig {
		Objects.requireNonNull(security, "Security must not be null");
		Objects.requireNonNull(auth, "Auth must not be null");
		Objects.requireNonNull(tlsConfig, "Tls config must not be null");
		Objects.requireNonNull(defaultCharset, "Default charset must not be null");
		
		if (bufferSize < 1) {
			throw new IllegalArgumentException("Buffer size must be at least 1: " + bufferSize);
		}
		
		if (ehloHostname != null) {
			for (int i = 0; i < ehloHostname.length(); i++) {
				if (ehloHostname.charAt(i) <= ' ') {
					throw new IllegalArgumentException("Ehlo hostname must not contain whitespace: " + ehloHostname);
				}
			}
		}
	}
	
	/**
	 * Creates a new builder for constructing SMTP client configuration.<br>
	 * @return A new builder with default values
	 */
	public static @NonNull SmtpClientConfigBuilder builder() {
		return new SmtpClientConfigBuilder();
	}
}
