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

package net.luis.utils.io.network.address;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Classification of IP address types based on their purpose and routing behavior.<br>
 * This enum categorizes IP addresses according to IANA special-purpose address registries and relevant RFCs.<br>
 * <p>
 *     Address type classification is useful for:
 * </p>
 * <ul>
 *     <li>Filtering addresses for security purposes</li>
 *     <li>Determining if an address is publicly routable</li>
 *     <li>Identifying special-purpose addresses</li>
 * </ul>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * Ipv4Address address = IpAddresses.parseIpv4("192.168.1.1");
 * AddressType type = address.getAddressType();
 *
 * if (type == AddressType.PRIVATE) {
 *     System.out.println("This is a private address");
 * }
 *
 * if (!type.isRoutable()) {
 *     System.out.println("This address is not globally routable");
 * }
 * }</pre>
 *
 * @author Luis-St
 */
public enum AddressType {
	
	/**
	 * Unspecified address (0.0.0.0 for IPv4 or :: for IPv6).<br>
	 * Used to indicate the absence of an address or to bind to all available interfaces.<br>
	 */
	UNSPECIFIED("0.0.0.0 or ::", false),
	/**
	 * Loopback address (127.0.0.0/8 for IPv4 or ::1 for IPv6).<br>
	 * Used for communication within the local host.<br>
	 */
	LOOPBACK("127.0.0.0/8 or ::1", false),
	/**
	 * Private address as defined by RFC 1918 for IPv4 (10/8, 172.16/12, 192.168/16)
	 * or Unique Local Address (ULA) for IPv6 (fc00::/7).<br>
	 * Used for internal network communication and not routable on the public internet.<br>
	 */
	PRIVATE("RFC 1918 private addresses (10/8, 172.16/12, 192.168/16) or IPv6 ULA (fc00::/7)", false),
	/**
	 * Link-local address (169.254.0.0/16 for IPv4 or fe80::/10 for IPv6).<br>
	 * Used for communication on a single network segment when no other address is available.<br>
	 */
	LINK_LOCAL("169.254.0.0/16 or fe80::/10", false),
	/**
	 * Multicast address (224.0.0.0/4 for IPv4 or ff00::/8 for IPv6).<br>
	 * Used for one-to-many communication.<br>
	 */
	MULTICAST("224.0.0.0/4 or ff00::/8", false),
	/**
	 * Broadcast address (255.255.255.255 for IPv4 only).<br>
	 * Used for one-to-all communication on a network segment.<br>
	 */
	BROADCAST("255.255.255.255 (IPv4 only)", false),
	/**
	 * Documentation address (192.0.2.0/24, 198.51.100.0/24, 203.0.113.0/24 for IPv4 or 2001:db8::/32 for IPv6).<br>
	 * Reserved for use in documentation and examples.<br>
	 */
	DOCUMENTATION("192.0.2.0/24, 198.51.100.0/24, 203.0.113.0/24 or 2001:db8::/32", false),
	/**
	 * Shared address space as defined by RFC 6598 (100.64.0.0/10).<br>
	 * Used for carrier-grade NAT deployments.<br>
	 */
	SHARED_ADDRESS_SPACE("100.64.0.0/10 (RFC 6598, carrier-grade NAT)", false),
	/**
	 * Global unicast address.<br>
	 * Globally routable unicast addresses used for communication across the internet.<br>
	 */
	GLOBAL_UNICAST("Globally routable unicast addresses", true),
	/**
	 * Reserved address.<br>
	 * Reserved for future use or special purposes.<br>
	 */
	RESERVED("Reserved for future use or special purposes", false);
	
	/**
	 * The description of this address type.<br>
	 */
	private final String description;
	/**
	 * Whether this address type is globally routable.<br>
	 */
	private final boolean routable;
	
	/**
	 * Constructs a new address type with the given description and routability.<br>
	 *
	 * @param description The description of this address type
	 * @param routable Whether this address type is globally routable
	 * @throws NullPointerException If the description is null
	 */
	AddressType(@NonNull String description, boolean routable) {
		this.description = Objects.requireNonNull(description, "Description must not be null");
		this.routable = routable;
	}
	
	/**
	 * Returns the description of this address type.<br>
	 * @return The description
	 */
	public @NonNull String description() {
		return this.description;
	}
	
	/**
	 * Returns whether this address type is globally routable.<br>
	 * Only {@link #GLOBAL_UNICAST} addresses are globally routable.<br>
	 *
	 * @return {@code true} if this address type is globally routable, {@code false} otherwise
	 */
	public boolean isRoutable() {
		return this.routable;
	}
}
