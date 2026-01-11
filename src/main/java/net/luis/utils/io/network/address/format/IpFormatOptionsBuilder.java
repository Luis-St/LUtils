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
 * Builder class for constructing ip format options instances with custom configuration.<br>
 * Provides a fluent API for setting individual formatting options.<br>
 * <p>
 *     All options default to values matching {@link IpFormatOptions#DEFAULT}:
 * </p>
 * <ul>
 *     <li>{@code uppercase} - false</li>
 *     <li>{@code expandZeros} - false</li>
 *     <li>{@code padHextets} - false</li>
 *     <li>{@code ipv4Mapped} - true</li>
 *     <li>{@code includeZoneId} - true</li>
 *     <li>{@code includePrefixLength} - false</li>
 * </ul>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IpFormatOptions options = IpFormatOptions.builder()
 *    .uppercase(true)
 *    .expandZeros(true)
 *    .includePrefixLength(true)
 *    .build();
 * }</pre>
 *
 * @see IpFormatOptions
 *
 * @author Luis-St
 */
public final class IpFormatOptionsBuilder {
	
	/**
	 * The uppercase option.<br>
	 * When enabled, hex digits A-F are uppercase.<br>
	 */
	private boolean uppercase;
	/**
	 * The expand zeros option.<br>
	 * When enabled, all zero hextets are shown explicitly instead of using :: compression.<br>
	 */
	private boolean expandZeros;
	/**
	 * The pad hextets option.<br>
	 * When enabled, each hextet is padded to four characters with leading zeros.<br>
	 */
	private boolean padHextets;
	/**
	 * The ipv4 mapped option.<br>
	 * When enabled, IPv4-mapped IPv6 addresses are shown in dotted-decimal format.<br>
	 */
	private boolean ipv4Mapped = true;
	/**
	 * The include zone id option.<br>
	 * When enabled, zone identifiers are included in the output for link-local addresses.<br>
	 */
	private boolean includeZoneId = true;
	/**
	 * The include prefix length option.<br>
	 * When enabled, network addresses include CIDR notation with the prefix length.<br>
	 */
	private boolean includePrefixLength;
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	IpFormatOptionsBuilder() {}
	
	/**
	 * Sets whether to use uppercase hex characters for IPv6 addresses.<br>
	 * <p>
	 *     When enabled, hex digits A-F are uppercase:
	 * </p>
	 * <pre>{@code
	 * // uppercase = false: "2001:db8::1"
	 * // uppercase = true:  "2001:DB8::1"
	 * }</pre>
	 *
	 * @param uppercase {@code true} to use uppercase hex characters, {@code false} for lowercase
	 * @return This builder for method chaining
	 */
	public @NonNull IpFormatOptionsBuilder uppercase(boolean uppercase) {
		this.uppercase = uppercase;
		return this;
	}
	
	/**
	 * Sets whether to expand all zero groups instead of using :: compression.<br>
	 * <p>
	 *     When enabled, all zero hextets are shown explicitly:
	 * </p>
	 * <pre>{@code
	 * // expandZeros = false: "2001:db8::1"
	 * // expandZeros = true:  "2001:db8:0:0:0:0:0:1"
	 * }</pre>
	 *
	 * @param expandZeros {@code true} to show all zero groups, {@code false} to use :: compression
	 * @return This builder for method chaining
	 */
	public @NonNull IpFormatOptionsBuilder expandZeros(boolean expandZeros) {
		this.expandZeros = expandZeros;
		return this;
	}
	
	/**
	 * Sets whether to pad IPv6 hextets with leading zeros.<br>
	 * <p>
	 *     When enabled, each hextet is padded to four characters:
	 * </p>
	 * <pre>{@code
	 * // padHextets = false: "2001:db8::1"
	 * // padHextets = true:  "2001:0db8::0001"
	 * }</pre>
	 *
	 * @param padHextets {@code true} to pad hextets with leading zeros, {@code false} for minimal representation
	 * @return This builder for method chaining
	 */
	public @NonNull IpFormatOptionsBuilder padHextets(boolean padHextets) {
		this.padHextets = padHextets;
		return this;
	}
	
	/**
	 * Sets whether to display IPv4-mapped IPv6 addresses in dotted-decimal format.<br>
	 * <p>
	 *     When enabled, the last 32 bits are shown as an IPv4 address:
	 * </p>
	 * <pre>{@code
	 * // ipv4Mapped = false: "::ffff:c0a8:0101"
	 * // ipv4Mapped = true:  "::ffff:192.168.1.1"
	 * }</pre>
	 *
	 * @param ipv4Mapped {@code true} to use dotted-decimal format for IPv4-mapped addresses
	 * @return This builder for method chaining
	 */
	public @NonNull IpFormatOptionsBuilder ipv4Mapped(boolean ipv4Mapped) {
		this.ipv4Mapped = ipv4Mapped;
		return this;
	}
	
	/**
	 * Sets whether to include zone identifiers in the output.<br>
	 * <p>
	 *     Zone identifiers are used with link-local addresses to specify the network interface:
	 * </p>
	 * <pre>{@code
	 * // includeZoneId = false: "fe80::1"
	 * // includeZoneId = true:  "fe80::1%eth0"
	 * }</pre>
	 *
	 * @param includeZoneId {@code true} to include zone identifiers, {@code false} to omit them
	 * @return This builder for method chaining
	 */
	public @NonNull IpFormatOptionsBuilder includeZoneId(boolean includeZoneId) {
		this.includeZoneId = includeZoneId;
		return this;
	}
	
	/**
	 * Sets whether to include the prefix length for network addresses.<br>
	 * <p>
	 *     When enabled, network addresses include CIDR notation:
	 * </p>
	 * <pre>{@code
	 * // includePrefixLength = false: "192.168.1.0"
	 * // includePrefixLength = true:  "192.168.1.0/24"
	 * }</pre>
	 *
	 * @param includePrefixLength {@code true} to include prefix length, {@code false} to omit it
	 * @return This builder for method chaining
	 */
	public @NonNull IpFormatOptionsBuilder includePrefixLength(boolean includePrefixLength) {
		this.includePrefixLength = includePrefixLength;
		return this;
	}
	
	/**
	 * Builds a new ip format options instance with the configured values.<br>
	 * @return A new ip format options instance
	 */
	public @NonNull IpFormatOptions build() {
		return new IpFormatOptions(
			this.uppercase,
			this.expandZeros,
			this.padHextets,
			this.ipv4Mapped,
			this.includeZoneId,
			this.includePrefixLength
		);
	}
}
