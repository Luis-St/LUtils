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
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a network endpoint consisting of a host part and a port number.<br>
 * This sealed interface is implemented by the two ways a host can be named.<br>
 * <p>
 *     An {@link IpEndpoint} identifies its host by a literal IP address and is therefore always resolved.<br>
 *     A {@link HostEndpoint} identifies its host by a DNS hostname, which is resolved when the endpoint is used.
 * </p>
 * <p>
 *     Endpoints observed from a live socket, such as the local and remote endpoint of a connection,
 *     are always {@link IpEndpoint}s because the underlying socket reports literal addresses.<br>
 *     A {@link HostEndpoint} is only produced when a hostname was supplied by the caller, which makes it<br>
 *     the relevant type for connection targets where the name matters for TLS server name indication and certificate verification.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * Endpoint byName = new HostEndpoint("example.com", 443);
 * Endpoint byAddress = IpAddresses.parseIpv4("192.168.1.1").toEndpoint(8080);
 *
 * // Both can be used wherever a connection target is expected
 * SslClient client = SslClient.connectTo(byName);
 *
 * // The host part can be recovered by pattern matching
 * String host = switch (byName) {
 *     case HostEndpoint hostEndpoint -> hostEndpoint.hostname();
 *     case IpEndpoint ipEndpoint -> ipEndpoint.address().toString();
 * };
 * }</pre>
 *
 * @see IpEndpoint
 * @see HostEndpoint
 *
 * @author Luis-St
 */
public sealed interface Endpoint permits IpEndpoint, HostEndpoint {
	
	/**
	 * The minimum valid port number.<br>
	 */
	int MIN_PORT = 0;
	/**
	 * The maximum valid port number.<br>
	 */
	int MAX_PORT = 65535;
	
	/**
	 * Creates an endpoint from a {@link InetSocketAddress}.<br>
	 * <p>
	 *     An unresolved socket address is converted to a {@link HostEndpoint} because no address is available,<br>
	 *     any other socket address is converted to an {@link IpEndpoint}.
	 * </p>
	 * <p>
	 *     The hostname of a resolved socket address is intentionally discarded,<br>
	 *     because recovering it may require a reverse lookup.<br>
	 *     Use {@link HostnameResolver} if the name is required.
	 * </p>
	 *
	 * @param address The socket address to convert
	 * @return A new endpoint representing the given socket address
	 * @throws NullPointerException If address is null
	 */
	static @NonNull Endpoint from(@NonNull InetSocketAddress address) {
		Objects.requireNonNull(address, "Address must not be null");
		if (address.isUnresolved()) {
			return new HostEndpoint(address.getHostString(), address.getPort());
		}
		return IpEndpoint.from(address);
	}
	
	/**
	 * Returns the port number of this endpoint.<br>
	 * @return The port number, between {@link #MIN_PORT} and {@link #MAX_PORT} inclusive
	 */
	int port();
	
	/**
	 * Converts this endpoint to a {@link InetSocketAddress}.<br>
	 * This method provides interoperability with the standard Java networking API.<br>
	 * <p>
	 *     The returned socket address retains the host part of this endpoint,<br>
	 *     so a socket connected to it reports the original hostname through {@link InetSocketAddress#getHostString()}.
	 * </p>
	 *
	 * @return An {@link InetSocketAddress} representing this endpoint
	 */
	@NonNull InetSocketAddress toInetSocketAddress();
	
	/**
	 * Resolves this endpoint to an endpoint with a literal IP address.<br>
	 * <p>
	 *     An {@link IpEndpoint} is already resolved and is returned unchanged,<br>
	 *     a {@link HostEndpoint} is resolved through the system name service.
	 * </p>
	 *
	 * @return The resolved endpoint, or empty if the hostname could not be resolved
	 */
	@NonNull Optional<IpEndpoint> resolve();
}
