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

package net.luis.utils.io.network;

import net.luis.utils.io.network.address.HostnameResolver;
import org.jspecify.annotations.NonNull;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a network endpoint consisting of a DNS hostname and a port number.<br>
 * This record is the counterpart to {@link IpEndpoint} for targets that are named rather than addressed.<br>
 * <p>
 *     The hostname is not resolved when the endpoint is constructed, so an instance may name a host that does not exist.<br>
 *     Resolution happens when the endpoint is used, either explicitly through {@link #resolve()}<br>
 *     or implicitly through {@link #toInetSocketAddress()}.
 * </p>
 * <p>
 *     Keeping the hostname rather than an address matters for TLS, because the name is required for
 *     server name indication and for verifying the certificate presented by the server.<br>
 *     Connecting to a host by its resolved address instead would send no server name indication and<br>
 *     verify the certificate against the address, which almost never matches.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * HostEndpoint endpoint = new HostEndpoint("smtp.example.com", 587);
 *
 * // Resolve explicitly, for example to inspect the address
 * Optional<IpEndpoint> resolved = endpoint.resolve();
 *
 * // Or hand it to a client, which resolves it while connecting
 * SslClient client = SslClient.connectTo(endpoint);
 * }</pre>
 *
 * @see Endpoint
 * @see IpEndpoint
 *
 * @author Luis-St
 *
 * @param hostname The hostname of the endpoint
 * @param port The port number (must be between 0 and 65535 inclusive)
 */
public record HostEndpoint(@NonNull String hostname, int port) implements Endpoint {
	
	/**
	 * The maximum length of a hostname in characters, as specified by RFC 1035.<br>
	 */
	public static final int MAX_HOSTNAME_LENGTH = 253;
	
	/**
	 * Constructs a new host endpoint with the specified hostname and port.<br>
	 *
	 * @param hostname The hostname of the endpoint
	 * @param port The port number (must be between 0 and 65535 inclusive)
	 * @throws NullPointerException If hostname is null
	 * @throws IllegalArgumentException If hostname is blank, longer than {@link #MAX_HOSTNAME_LENGTH} characters, contains whitespace, or if port is not between 0 and 65535
	 */
	public HostEndpoint {
		Objects.requireNonNull(hostname, "Hostname must not be null");
		if (hostname.isBlank()) {
			throw new IllegalArgumentException("Hostname must not be blank");
		}
		if (hostname.length() > MAX_HOSTNAME_LENGTH) {
			throw new IllegalArgumentException("Hostname must not be longer than " + MAX_HOSTNAME_LENGTH + " characters: " + hostname.length());
		}
		
		for (int i = 0; i < hostname.length(); i++) {
			if (Character.isWhitespace(hostname.charAt(i))) {
				throw new IllegalArgumentException("Hostname must not contain whitespace: " + hostname);
			}
		}
		
		if (port < MIN_PORT || port > MAX_PORT) {
			throw new IllegalArgumentException("Port must be between " + MIN_PORT + " and " + MAX_PORT + ": " + port);
		}
	}
	
	/**
	 * Converts this endpoint to a {@link InetSocketAddress}, resolving the hostname.<br>
	 * <p>
	 *     The returned socket address keeps the hostname, so a socket connected to it reports the name<br>
	 *     through {@link InetSocketAddress#getHostString()} and TLS can use it for server name indication<br>
	 *     and certificate verification.
	 * </p>
	 * <p>
	 *     If the hostname cannot be resolved, an unresolved socket address is returned rather than an exception being thrown,<br>
	 *     matching the behavior of {@link InetSocketAddress#InetSocketAddress(String, int)}.<br>
	 *     Connecting to an unresolved socket address fails with an {@link UnknownHostException}.
	 * </p>
	 *
	 * @return An {@link InetSocketAddress} representing this endpoint
	 */
	@Override
	public @NonNull InetSocketAddress toInetSocketAddress() {
		return new InetSocketAddress(this.hostname, this.port);
	}
	
	/**
	 * Resolves the hostname of this endpoint through the system name service.<br>
	 * <p>
	 *     If the hostname maps to more than one address, the first resolved address is used.
	 * </p>
	 *
	 * @return The resolved endpoint, or empty if the hostname could not be resolved
	 */
	@Override
	public @NonNull Optional<IpEndpoint> resolve() {
		return HostnameResolver.resolve(this.hostname).map(address -> new IpEndpoint(address, this.port));
	}
	
	/**
	 * Returns a string representation of this endpoint.<br>
	 * The format is {@code hostname:port} (e.g., "smtp.example.com:587").
	 *
	 * @return A string representation of this endpoint
	 */
	@Override
	public @NonNull String toString() {
		return this.hostname + ":" + this.port;
	}
}
