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

import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.IpAddresses;
import org.jspecify.annotations.NonNull;

import java.net.*;
import java.util.Objects;

/**
 * Represents a network endpoint consisting of an IP address and a port number.<br>
 * This record provides a convenient way to bundle an IP address with its associated port,
 * commonly used for specifying connection targets or server bindings.<br>
 * <p>
 *     The port number must be in the valid range of 0 to 65535 inclusive.<br>
 *     The IP address can be either IPv4 or IPv6.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * // Create an endpoint from an IP address
 * Ipv4Address address = IpAddresses.parseIpv4("192.168.1.1");
 * IpEndpoint endpoint = new IpEndpoint(address, 8080);
 *
 * // Or use the convenience method on IpAddress
 * IpEndpoint endpoint = address.toEndpoint(8080);
 *
 * // Convert to Java's InetSocketAddress for use with standard networking APIs
 * InetSocketAddress socketAddress = endpoint.toInetSocketAddress();
 * }</pre>
 *
 * @see IpAddress
 * @see InetSocketAddress
 *
 * @author Luis-St
 *
 * @param address The IP address of the endpoint
 * @param port The port number (must be between 0 and 65535 inclusive)
 */
public record IpEndpoint(@NonNull IpAddress<?> address, int port) {
	
	/**
	 * The minimum valid port number.<br>
	 */
	public static final int MIN_PORT = 0;
	/**
	 * The maximum valid port number.<br>
	 */
	public static final int MAX_PORT = 65535;
	
	/**
	 * Constructs a new IP endpoint with the specified address and port.<br>
	 *
	 * @param address The IP address of the endpoint
	 * @param port The port number (must be between 0 and 65535 inclusive)
	 * @throws NullPointerException If address is null
	 * @throws IllegalArgumentException If port is not between 0 and 65535
	 */
	public IpEndpoint {
		Objects.requireNonNull(address, "Address must not be null");
		if (port < MIN_PORT || port > MAX_PORT) {
			throw new IllegalArgumentException("Port must be between " + MIN_PORT + " and " + MAX_PORT + ": " + port);
		}
	}
	
	/**
	 * Creates an  from a {@link InetSocketAddress}.<br>
	 * This method provides a convenient way to convert from the standard Java networking API to an .<br>
	 * <p>
	 *     The method automatically detects whether the address is IPv4 or IPv6 and creates the appropriate {@link IpAddress} type.
	 * </p>
	 *
	 * @param address The socket address to convert
	 * @return A new  representing the given socket address
	 * @throws NullPointerException If address is null
	 */
	public static @NonNull IpEndpoint from(@NonNull InetSocketAddress address) {
		Objects.requireNonNull(address, "Address must not be null");
		return new IpEndpoint(IpAddresses.from(address.getAddress()), address.getPort());
	}
	
	/**
	 * Converts this endpoint to a {@link InetSocketAddress}.<br>
	 * This method provides interoperability with the standard Java networking API.
	 * <p>
	 *     The returned socket address can be used with classes such as
	 *     {@link Socket#connect(SocketAddress)} and
	 *     {@link ServerSocket#bind(SocketAddress)}.
	 * </p>
	 *
	 * @return An {@link InetSocketAddress} representing this endpoint
	 */
	public @NonNull InetSocketAddress toInetSocketAddress() {
		return this.address.toSocketAddress(this.port);
	}
	
	/**
	 * Returns a string representation of this endpoint.<br>
	 * For IPv4 addresses, the format is {@code address:port} (e.g., "192.168.1.1:8080").<br>
	 * For IPv6 addresses, the format is {@code [address]:port} (e.g., "[2001:db8::1]:8080").
	 *
	 * @return A string representation of this endpoint
	 */
	@Override
	public @NonNull String toString() {
		if (this.address.version() == 6) {
			return "[" + this.address + "]:" + this.port;
		}
		return this.address + ":" + this.port;
	}
}
