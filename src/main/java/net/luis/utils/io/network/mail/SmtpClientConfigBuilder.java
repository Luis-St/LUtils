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
 * Builder class for constructing SMTP client configuration.<br>
 * Provides a fluent API for setting individual configuration options.<br>
 * <p>
 *     All options default to values matching {@link SmtpClientConfig#DEFAULT}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SmtpClientConfig config = SmtpClientConfig.builder()
 *     .security(SmtpSecurity.IMPLICIT_TLS)
 *     .auth(new SmtpAuth.OAuth("user@example.com", token))
 *     .tlsConfig(SslClientConfig.builder().verifyHostname(true).build())
 *     .build();
 * }</pre>
 *
 * @see SmtpClientConfig
 *
 * @author Luis-St
 */
public final class SmtpClientConfigBuilder {
	
	/**
	 * The transport security mode.<br>
	 */
	private SmtpSecurity security = SmtpSecurity.STARTTLS;
	/**
	 * The authentication strategy.<br>
	 */
	private SmtpAuth auth = new SmtpAuth.None();
	/**
	 * The hostname announced in the EHLO command, or null to resolve the local host.<br>
	 */
	private @Nullable String ehloHostname;
	/**
	 * The SSL/TLS configuration reused for secure transport.<br>
	 */
	private SslClientConfig tlsConfig = SslClientConfig.DEFAULT;
	/**
	 * The character set used to encode SMTP commands and authentication tokens.<br>
	 */
	private Charset defaultCharset = StandardCharsets.UTF_8;
	/**
	 * The size of the read/write buffers in bytes.<br>
	 */
	private int bufferSize = 8192;
	/**
	 * The handler called when an error occurs, or null.<br>
	 */
	private @Nullable ErrorEventHandler onError;
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	SmtpClientConfigBuilder() {}
	
	/**
	 * Sets the transport security mode.<br>
	 *
	 * @param security The transport security mode
	 * @return This builder for method chaining
	 * @throws NullPointerException If the security mode is null
	 */
	public @NonNull SmtpClientConfigBuilder security(@NonNull SmtpSecurity security) {
		this.security = Objects.requireNonNull(security, "Security must not be null");
		return this;
	}
	
	/**
	 * Sets the authentication strategy.<br>
	 *
	 * @param auth The authentication strategy
	 * @return This builder for method chaining
	 * @throws NullPointerException If the authentication strategy is null
	 */
	public @NonNull SmtpClientConfigBuilder auth(@NonNull SmtpAuth auth) {
		this.auth = Objects.requireNonNull(auth, "Auth must not be null");
		return this;
	}
	
	/**
	 * Sets the hostname announced in the EHLO command.<br>
	 * If null, the local canonical host name is resolved, falling back to {@code localhost}.<br>
	 *
	 * @param ehloHostname The EHLO hostname, or null to resolve the local host
	 * @return This builder for method chaining
	 */
	public @NonNull SmtpClientConfigBuilder ehloHostname(@Nullable String ehloHostname) {
		this.ehloHostname = ehloHostname;
		return this;
	}
	
	/**
	 * Sets the SSL/TLS configuration reused for secure transport.<br>
	 *
	 * @param tlsConfig The SSL/TLS configuration
	 * @return This builder for method chaining
	 * @throws NullPointerException If the TLS configuration is null
	 */
	public @NonNull SmtpClientConfigBuilder tlsConfig(@NonNull SslClientConfig tlsConfig) {
		this.tlsConfig = Objects.requireNonNull(tlsConfig, "Tls config must not be null");
		return this;
	}
	
	/**
	 * Sets the character set used to encode SMTP commands and authentication tokens.<br>
	 *
	 * @param defaultCharset The character set
	 * @return This builder for method chaining
	 * @throws NullPointerException If the character set is null
	 */
	public @NonNull SmtpClientConfigBuilder defaultCharset(@NonNull Charset defaultCharset) {
		this.defaultCharset = Objects.requireNonNull(defaultCharset, "Default charset must not be null");
		return this;
	}
	
	/**
	 * Sets the size of the read/write buffers in bytes.<br>
	 *
	 * @param bufferSize The buffer size (must be at least 1)
	 * @return This builder for method chaining
	 */
	public @NonNull SmtpClientConfigBuilder bufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
		return this;
	}
	
	/**
	 * Sets the error event handler.<br>
	 *
	 * @param onError The error handler, or null to disable
	 * @return This builder for method chaining
	 */
	public @NonNull SmtpClientConfigBuilder onError(@Nullable ErrorEventHandler onError) {
		this.onError = onError;
		return this;
	}
	
	/**
	 * Builds a new SMTP client configuration with the configured values.<br>
	 * @return A new configuration instance
	 */
	public @NonNull SmtpClientConfig build() {
		return new SmtpClientConfig(this.security, this.auth, this.ehloHostname, this.tlsConfig, this.defaultCharset, this.bufferSize, this.onError);
	}
}
