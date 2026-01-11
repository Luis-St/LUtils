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

package net.luis.utils.io.network.address.ipv6;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents the scope field of an IPv6 multicast address.<br>
 * The scope defines the extent to which a multicast packet should be forwarded,<br>
 * from interface-local (never leaves the node) to global (routable across the Internet).<br>
 * <p>
 *     The scope is encoded in bits 12-15 of an IPv6 multicast address (second nibble of the second octet).<br>
 *     For example, in ff02::1 (all-nodes link-local), the scope value is 2 (link-local).
 * </p>
 * <p>
 *     Scope values are defined by IANA and RFC 7346:
 * </p>
 * <ul>
 *     <li>0x1 - Interface-Local: confined to a single interface</li>
 *     <li>0x2 - Link-Local: not forwarded by routers</li>
 *     <li>0x4 - Admin-Local: administratively configured scope</li>
 *     <li>0x5 - Site-Local: confined to a site</li>
 *     <li>0x8 - Organization-Local: confined to an organization</li>
 *     <li>0xE - Global: Internet-wide scope</li>
 * </ul>
 * <pre>{@code
 * Ipv6Address multicast = IpAddresses.parseIpv6("ff02::1");
 * Ipv6MulticastScope scope = multicast.getMulticastScope();
 *
 * if (scope == Ipv6MulticastScope.LINK_LOCAL) {
 *     System.out.println("Link-local multicast, won't be routed");
 * }
 * }</pre>
 *
 * @author Luis-St
 */
public enum Ipv6MulticastScope {

	/**
	 * Reserved scope value (0x0).<br>
	 */
	RESERVED_0(0x0, "Reserved"),
	/**
	 * Interface-Local scope (0x1).<br>
	 * Packets are confined to a single interface and never leave the node.<br>
	 */
	INTERFACE_LOCAL(0x1, "Interface-Local scope"),
	/**
	 * Link-Local scope (0x2).<br>
	 * Packets are not forwarded by routers and stay on the local link.<br>
	 */
	LINK_LOCAL(0x2, "Link-Local scope"),
	/**
	 * Realm-Local scope (0x3).<br>
	 * Scope is defined by local policy.<br>
	 */
	REALM_LOCAL(0x3, "Realm-Local scope"),
	/**
	 * Admin-Local scope (0x4).<br>
	 * The smallest administratively configured scope.<br>
	 */
	ADMIN_LOCAL(0x4, "Admin-Local scope"),
	/**
	 * Site-Local scope (0x5).<br>
	 * Packets are confined to a site.<br>
	 */
	SITE_LOCAL(0x5, "Site-Local scope"),
	/**
	 * Unassigned scope value (0x6).<br>
	 */
	UNASSIGNED_6(0x6, "Unassigned"),
	/**
	 * Unassigned scope value (0x7).<br>
	 */
	UNASSIGNED_7(0x7, "Unassigned"),
	/**
	 * Organization-Local scope (0x8).<br>
	 * Packets are confined to an organization.<br>
	 */
	ORGANIZATION_LOCAL(0x8, "Organization-Local scope"),
	/**
	 * Unassigned scope value (0x9).<br>
	 */
	UNASSIGNED_9(0x9, "Unassigned"),
	/**
	 * Unassigned scope value (0xA).<br>
	 */
	UNASSIGNED_A(0xA, "Unassigned"),
	/**
	 * Unassigned scope value (0xB).<br>
	 */
	UNASSIGNED_B(0xB, "Unassigned"),
	/**
	 * Unassigned scope value (0xC).<br>
	 */
	UNASSIGNED_C(0xC, "Unassigned"),
	/**
	 * Unassigned scope value (0xD).<br>
	 */
	UNASSIGNED_D(0xD, "Unassigned"),
	/**
	 * Global scope (0xE).<br>
	 * Packets are routable across the Internet.<br>
	 */
	GLOBAL(0xE, "Global scope"),
	/**
	 * Reserved scope value (0xF).<br>
	 */
	RESERVED_F(0xF, "Reserved"),
	/**
	 * Unknown scope value.<br>
	 * Used when the scope value is not in the valid range (0x0-0xF).<br>
	 */
	UNKNOWN(-1, "Unknown scope");

	/**
	 * The numeric value of this scope (0x0-0xF or -1 for unknown).<br>
	 */
	private final int value;
	/**
	 * A human-readable description of this scope.<br>
	 */
	private final String description;

	/**
	 * Constructs a new multicast scope with the given value and description.<br>
	 *
	 * @param value The numeric value of the scope
	 * @param description The human-readable description
	 */
	Ipv6MulticastScope(int value, @NonNull String description) {
		this.value = value;
		this.description = Objects.requireNonNull(description, "Description must not be null");
	}

	/**
	 * Returns the multicast scope for the given numeric value.<br>
	 *
	 * @param value The scope value (0x0-0xF)
	 * @return The corresponding scope, or {@link #UNKNOWN} if the value is out of range
	 */
	public static @NonNull Ipv6MulticastScope fromValue(int value) {
		for (Ipv6MulticastScope scope : values()) {
			if (scope.value == value) {
				return scope;
			}
		}
		return UNKNOWN;
	}

	/**
	 * Returns the numeric value of this scope.<br>
	 * @return The scope value (0x0-0xF or -1 for unknown)
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * Returns the human-readable description of this scope.<br>
	 * @return The scope description
	 */
	public @NonNull String getDescription() {
		return this.description;
	}

	/**
	 * Checks whether this scope is an assigned (non-reserved, non-unassigned) scope.<br>
	 * @return {@code true} if this scope is assigned, {@code false} if it is reserved, unassigned, or unknown
	 */
	public boolean isAssigned() {
		return switch (this) {
			case RESERVED_0, RESERVED_F, UNKNOWN, UNASSIGNED_6, UNASSIGNED_7, UNASSIGNED_9, UNASSIGNED_A, UNASSIGNED_B, UNASSIGNED_C, UNASSIGNED_D -> false;
			default -> true;
		};
	}

	/**
	 * Checks whether this scope is a well-known scope commonly used in practice.<br>
	 * @return {@code true} if this is one of the well-known scopes ({@link #INTERFACE_LOCAL}, {@link #LINK_LOCAL}, {@link #SITE_LOCAL}, {@link #ORGANIZATION_LOCAL}, or {@link #GLOBAL})
	 */
	public boolean isWellKnown() {
		return this == INTERFACE_LOCAL || this == LINK_LOCAL || this == SITE_LOCAL || this == ORGANIZATION_LOCAL || this == GLOBAL;
	}
}
