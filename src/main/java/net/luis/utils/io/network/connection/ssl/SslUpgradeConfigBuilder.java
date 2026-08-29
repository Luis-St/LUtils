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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.util.List;
import java.util.Objects;

/**
 * Builder class for constructing a TLS upgrade configuration.<br>
 * Provides a fluent API for setting individual configuration options.<br>
 * <p>
 *     All options default to values matching {@link SslUpgradeConfig#DEFAULT}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SslUpgradeConfig upgrade = SslUpgradeConfig.builder()
 *     .sslContext(myContext)
 *     .enabledProtocols(List.of("TLSv1.3"))
 *     .verifyHostname(true)
 *     .build();
 * }</pre>
 *
 * @see SslUpgradeConfig
 *
 * @author Luis-St
 */
public final class SslUpgradeConfigBuilder {
	
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
	 * Constructs a new builder with default values.<br>
	 */
	SslUpgradeConfigBuilder() {}
	
	/**
	 * Sets the SSL context to use for layering TLS over the existing connection.<br>
	 * If null, the JVM default context ({@link SSLContext#getDefault()}) is used.<br>
	 *
	 * @param sslContext The SSL context, or null to use the JVM default
	 * @return This builder for method chaining
	 */
	public @NonNull SslUpgradeConfigBuilder sslContext(@Nullable SSLContext sslContext) {
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
	public @NonNull SslUpgradeConfigBuilder enabledProtocols(@NonNull List<String> enabledProtocols) {
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
	public @NonNull SslUpgradeConfigBuilder enabledCipherSuites(@NonNull List<String> enabledCipherSuites) {
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
	public @NonNull SslUpgradeConfigBuilder verifyHostname(boolean verifyHostname) {
		this.verifyHostname = verifyHostname;
		return this;
	}
	
	/**
	 * Builds a new TLS upgrade configuration with the configured values.<br>
	 * @return A new configuration instance
	 */
	public @NonNull SslUpgradeConfig build() {
		return new SslUpgradeConfig(this.sslContext, this.enabledProtocols, this.enabledCipherSuites, this.verifyHostname);
	}
}
