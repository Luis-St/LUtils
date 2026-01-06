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

package net.luis.utils.io.codec.constraint_new.core;

/**
 * Enumeration of IP address types based on their network classification.<br>
 * <p>
 *     IP addresses can be categorized into different types based on their scope, reachability, and intended use.<br>
 *     These classifications apply to both IPv4 and IPv6 addresses, though the specific ranges differ.
 * </p>
 *
 * @author Luis-St
 *
 */

public enum IpAddressType {

	/**
	 * Public (globally routable) IP address.<br>
	 * <p>
	 *     IP addresses that are routable on the public internet.<br>
	 *     These addresses are assigned by regional internet registries and are globally unique.
	 * </p>
	 */
	PUBLIC,

	/**
	 * Private (non-routable) IP address.<br>
	 * <p>
	 *     IP addresses reserved for use within private networks (RFC 1918 for IPv4).<br>
	 *     Common ranges include 10.0.0.0/8, 172.16.0.0/12, and 192.168.0.0/16.
	 * </p>
	 */
	PRIVATE,

	/**
	 * Loopback IP address.<br>
	 * <p>
	 *     IP addresses used for communication within the same host.<br>
	 *     IPv4 loopback is 127.0.0.0/8 (commonly 127.0.0.1), IPv6 loopback is ::1.
	 * </p>
	 */
	LOOPBACK,

	/**
	 * Link-local IP address.<br>
	 * <p>
	 *     IP addresses used for communication within a local network segment without a router.<br>
	 *     IPv4 link-local is 169.254.0.0/16, IPv6 link-local is fe80::/10.
	 * </p>
	 */
	LINK_LOCAL,

	/**
	 * Multicast IP address.<br>
	 * <p>
	 *     IP addresses used for one-to-many communication.<br>
	 *     IPv4 multicast is 224.0.0.0/4, IPv6 multicast is ff00::/8.
	 * </p>
	 */
	MULTICAST,

	/**
	 * Broadcast IP address.<br>
	 * <p>
	 *     IP addresses used for one-to-all communication within a network.<br>
	 *     IPv4 broadcast is typically 255.255.255.255 or the last address in a subnet. IPv6 does not support broadcast.
	 * </p>
	 */
	BROADCAST,

	/**
	 * Unspecified IP address.<br>
	 * <p>
	 *     The all-zeros address indicating no specific address.<br>
	 *     IPv4 unspecified is 0.0.0.0, IPv6 unspecified is ::.
	 * </p>
	 */
	UNSPECIFIED
}
