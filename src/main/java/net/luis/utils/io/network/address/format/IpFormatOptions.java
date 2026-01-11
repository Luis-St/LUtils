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

package net.luis.utils.io.network.address.format;

import org.jspecify.annotations.NonNull;

/**
 * Configuration options for formatting IP addresses to strings.<br>
 * This record controls how IP addresses are converted to their string representation, supporting various output formats for different use cases.<br>
 * <p>
 *     Common formatting scenarios:
 * </p>
 * <pre>{@code
 * Ipv6Address address = IpAddresses.parseIpv6("2001:db8::1");
 *
 * // Default: "2001:db8::1"
 * address.format(IpFormatOptions.DEFAULT);
 *
 * // Expanded: "2001:0db8:0000:0000:0000:0000:0000:0001"
 * address.format(IpFormatOptions.EXPANDED);
 *
 * // Uppercase: "2001:DB8::1"
 * address.format(IpFormatOptions.builder().uppercase(true).build());
 * }</pre>
 *
 * @see IpFormatOptionsBuilder
 *
 * @author Luis-St
 *
 * @param uppercase Use uppercase hex characters for IPv6 addresses (e.g., "2001:DB8::1" instead of "2001:db8::1")
 * @param expandZeros Don't use :: compression for IPv6 addresses, showing all zero groups explicitly
 * @param padHextets Pad IPv6 hextets with leading zeros to four characters (e.g., "2001:0db8" instead of "2001:db8")
 * @param ipv4Mapped Show IPv4-mapped IPv6 addresses in dotted-decimal format (e.g., "::ffff:192.168.1.1" instead of "::ffff:c0a8:0101")
 * @param includeZoneId Include zone identifier in IPv6 output for link-local addresses (e.g., "fe80::1%eth0")
 * @param includePrefixLength Include prefix length for network addresses (e.g., "192.168.1.0/24" or "2001:db8::/32")
 */
public record IpFormatOptions(
	boolean uppercase,
	boolean expandZeros,
	boolean padHextets,
	boolean ipv4Mapped,
	boolean includeZoneId,
	boolean includePrefixLength
) {

	/**
	 * Default formatting options.<br>
	 * Uses lowercase hex characters, enables :: compression, no padding, and includes zone identifiers when present.<br>
	 * <p>
	 *     Example output: "2001:db8::1", "fe80::1%eth0"
	 * </p>
	 */
	public static final IpFormatOptions DEFAULT = new IpFormatOptions(false, false, false, true, true, false);

	/**
	 * Expanded formatting options.<br>
	 * Disables :: compression and pads all hextets with leading zeros for a fully expanded representation.<br>
	 * <p>
	 *     Example output: "2001:0db8:0000:0000:0000:0000:0000:0001"
	 * </p>
	 */
	public static final IpFormatOptions EXPANDED = new IpFormatOptions(false, true, true, false, true, false);

	/**
	 * Compact formatting options.<br>
	 * Uses maximum compression with :: notation and excludes zone identifiers for the most compact representation possible.<br>
	 * <p>
	 *     Example output: "2001:db8::1"
	 * </p>
	 */
	public static final IpFormatOptions COMPACT = new IpFormatOptions(false, false, false, true, false, false);
	
	/**
	 * Constructs a new ip format options instance with the specified settings.<br>
	 *
	 * @param uppercase Use uppercase hex characters for IPv6 addresses (e.g., "2001:DB8::1" instead of "2001:db8::1")
	 * @param expandZeros Don't use :: compression for IPv6 addresses, showing all zero groups explicitly
	 * @param padHextets Pad IPv6 hextets with leading zeros to four characters (e.g., "2001:0db8" instead of "2001:db8")
	 * @param ipv4Mapped Show IPv4-mapped IPv6 addresses in dotted-decimal format (e.g., "::ffff:192.168.1.1" instead of "::ffff:c0a8:0101")
	 * @param includeZoneId Include zone identifier in IPv6 output for link-local addresses (e.g., "fe80::1%eth0")
	 * @param includePrefixLength Include prefix length for network addresses (e.g., "192.168.1.0/24" or "2001:db8::/32")
	 */
	public IpFormatOptions {}
	
	/**
	 * Creates a new builder for constructing custom ip format options.<br>
	 * <p>
	 *     The builder starts with default values matching {@link #DEFAULT}.
	 * </p>
	 * <pre>{@code
	 * IpFormatOptions options = IpFormatOptions.builder()
	 *     .uppercase(true)
	 *     .padHextets(true)
	 *     .build();
	 * }</pre>
	 *
	 * @return A new builder instance
	 * @see IpFormatOptionsBuilder
	 */
	public static @NonNull IpFormatOptionsBuilder builder() {
		return new IpFormatOptionsBuilder();
	}
}
