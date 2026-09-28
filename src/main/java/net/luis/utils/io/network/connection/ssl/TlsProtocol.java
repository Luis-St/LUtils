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

import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.util.*;

/**
 * The TLS and SSL protocol versions that can be enabled on a secured socket.<br>
 * Each constant maps to the protocol name the JSSE provider expects in {@link SSLSocket#setEnabledProtocols(String[])} and {@link SSLServerSocket#setEnabledProtocols(String[])}.<br>
 * <p>
 *     Using this enum instead of raw protocol names moves a misspelled protocol from a handshake time failure to a compile time error.<br>
 *     The set of protocols a socket actually supports still depends on the running JVM and its security policy,
 *     so enabling a constant that the provider does not offer fails when the socket is configured.
 * </p>
 * <p>
 *     Only {@link #TLS_V1_2} and {@link #TLS_V1_3} are considered secure.<br>
 *     Every other constant exists for interoperability with legacy peers and is reported as deprecated by {@link #isDeprecated()}.<br>
 *     Modern JVMs disable those protocols by default, so enabling them may additionally require a change to the {@code jdk.tls.disabledAlgorithms} security property.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * SslClientConfig config = SslClientConfig.builder()
 *     .enabledProtocols(List.of(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_2))
 *     .build();
 * }</pre>
 *
 * @see SslClientConfig
 * @see SslServerConfig
 * @see SslUpgradeConfig
 *
 * @author Luis-St
 */
public enum TlsProtocol {
	
	/**
	 * The SSL 3.0 protocol.<br>
	 * This protocol is broken by the POODLE attack and must not be used for new connections.<br>
	 */
	SSL_V3("SSLv3"),
	/**
	 * The TLS 1.0 protocol.<br>
	 * This protocol is deprecated by RFC 8996 and must not be used for new connections.<br>
	 */
	TLS_V1("TLSv1"),
	/**
	 * The TLS 1.1 protocol.<br>
	 * This protocol is deprecated by RFC 8996 and must not be used for new connections.<br>
	 */
	TLS_V1_1("TLSv1.1"),
	/**
	 * The TLS 1.2 protocol.<br>
	 * This is the oldest protocol version that is still considered secure.<br>
	 */
	TLS_V1_2("TLSv1.2"),
	/**
	 * The TLS 1.3 protocol.<br>
	 * This is the newest protocol version and should be preferred whenever the peer supports it.<br>
	 */
	TLS_V1_3("TLSv1.3"),
	/**
	 * The SSLv2 compatibility hello.<br>
	 * This is not a protocol version of its own but a pseudo protocol that allows the initial hello message to be sent in the SSL 2.0 format,
	 * so that a real protocol version can be negotiated with very old peers.<br>
	 * It only has an effect when it is enabled together with at least one real protocol version.<br>
	 */
	SSL_V2_HELLO("SSLv2Hello");
	
	/**
	 * The protocol name expected by the JSSE provider.<br>
	 */
	private final String protocolName;
	
	/**
	 * Constructs a new protocol with the given JSSE protocol name.<br>
	 * @param protocolName The protocol name expected by the JSSE provider
	 */
	TlsProtocol(@NonNull String protocolName) {
		this.protocolName = Objects.requireNonNull(protocolName, "Protocol name must not be null");
	}
	
	/**
	 * Returns the protocol for the given JSSE protocol name.<br>
	 * The name is matched case insensitively, so both {@code "TLSv1.3"} and {@code "tlsv1.3"} resolve to {@link #TLS_V1_3}.<br>
	 *
	 * @param protocolName The protocol name to look up, or null
	 * @return An optional containing the matching protocol, or an empty optional if the name is null or unknown
	 */
	public static @NonNull Optional<TlsProtocol> byName(@Nullable String protocolName) {
		if (protocolName == null) {
			return Optional.empty();
		}
		for (TlsProtocol protocol : values()) {
			if (protocol.protocolName.equalsIgnoreCase(protocolName)) {
				return Optional.of(protocol);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Converts the given protocols into the protocol name array expected by the JSSE socket setters.<br>
	 * The order of the given list is preserved.<br>
	 *
	 * @param protocols The protocols to convert
	 * @return An array holding the protocol name of each given protocol, empty if the given list is empty
	 * @throws NullPointerException If the protocol list is null or contains null
	 */
	public static String @NonNull [] toProtocolNames(@NonNull List<TlsProtocol> protocols) {
		Objects.requireNonNull(protocols, "Protocols must not be null");
		if (protocols.isEmpty()) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}
		
		String[] names = new String[protocols.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = Objects.requireNonNull(protocols.get(i), "Protocols must not contain null").protocolName;
		}
		return names;
	}
	
	/**
	 * Returns the protocol name expected by the JSSE provider.<br>
	 * @return The protocol name, for example {@code "TLSv1.3"}
	 */
	public @NonNull String protocolName() {
		return this.protocolName;
	}
	
	/**
	 * Checks whether this protocol is deprecated and should no longer be enabled.<br>
	 * Only {@link #TLS_V1_2} and {@link #TLS_V1_3} are not deprecated.<br>
	 * {@link #SSL_V2_HELLO} counts as deprecated because it only serves peers that predate TLS 1.2.<br>
	 *
	 * @return {@code true} if this protocol is deprecated, {@code false} otherwise
	 */
	public boolean isDeprecated() {
		return switch (this) {
			case TLS_V1_2, TLS_V1_3 -> false;
			default -> true;
		};
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return this.protocolName;
	}
	//endregion
}
