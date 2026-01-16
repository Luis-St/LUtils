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
 * Configuration options for parsing IP addresses from strings.<br>
 * This record provides fine-grained control over how IP address strings are interpreted during parsing, allowing for strict RFC compliance or lenient parsing of various formats.<br>
 * <p>
 *     Common parsing scenarios:
 * </p>
 * <pre>{@code
 * // Default parsing
 * Ipv4Address ipv4 = IpAddresses.parseIpv4("192.168.1.1");
 *
 * // Lenient: "192.168.001.001" -> "192.168.1.1"
 * IpAddresses.parseIpv4("192.168.001.001", IpParseOptions.LENIENT);
 *
 * // Custom options via builder
 * IpAddresses.parseIpv4("192.168.1.1:8080", IpParseOptions.builder().allowPortSuffix(true).build());
 * }</pre>
 *
 * @see IpParseOptionsBuilder
 *
 * @author Luis-St
 *
 * @param allowLeadingZeros Allow leading zeros in IPv4 octets without treating them as octal (e.g., "192.168.001.001" parses as "192.168.1.1")
 * @param allowOctalNotation Interpret leading zeros as octal notation (e.g., "010" is interpreted as decimal 8)
 * @param allowDecimalNotation Allow IPv4 addresses in decimal notation (e.g., "3232235777" parses as "192.168.1.1")
 * @param allowMixedCase Allow mixed case hexadecimal characters in IPv6 addresses (e.g., "2001:DB8::1" and "2001:db8::1")
 * @param allowEmptySegments Allow the double-colon (::) shorthand in IPv6 addresses (e.g., "2001:db8::1" for "2001:db8:0:0:0:0:0:1")
 * @param allowMixedNotation Allow IPv4-mapped IPv6 addresses (e.g., "::ffff:192.168.1.1")
 * @param allowZoneId Allow zone identifiers in IPv6 link-local addresses (e.g., "fe80::1%eth0")
 * @param allowPortSuffix Allow port suffix notation in addresses (e.g., "192.168.1.1:8080" or "[::1]:8080")
 * @param normalize Normalize the parsed address to its canonical form
 */
public record IpParseOptions(
	boolean allowLeadingZeros,
	boolean allowOctalNotation,
	boolean allowDecimalNotation,
	boolean allowMixedCase,
	boolean allowEmptySegments,
	boolean allowMixedNotation,
	boolean allowZoneId,
	boolean allowPortSuffix,
	boolean normalize
) {
	
	/**
	 * Default parsing options with sensible defaults for common use cases.<br>
	 * <p>
	 *     Configuration:
	 * </p>
	 * <ul>
	 *     <li>{@link #allowLeadingZeros} = {@code false}</li>
	 *     <li>{@link #allowOctalNotation} = {@code false}</li>
	 *     <li>{@link #allowDecimalNotation} = {@code false}</li>
	 *     <li>{@link #allowMixedCase} = {@code true}</li>
	 *     <li>{@link #allowEmptySegments} = {@code true}</li>
	 *     <li>{@link #allowMixedNotation} = {@code true}</li>
	 *     <li>{@link #allowZoneId} = {@code true}</li>
	 *     <li>{@link #allowPortSuffix} = {@code false}</li>
	 *     <li>{@link #normalize} = {@code false}</li>
	 * </ul>
	 */
	public static final IpParseOptions DEFAULT = new IpParseOptions(false, false, false, true, true, true, true, false, false);
	
	/**
	 * Strict parsing options for RFC-compliant address parsing with normalization.<br>
	 * <p>
	 *     Configuration:
	 * </p>
	 * <ul>
	 *     <li>{@link #allowLeadingZeros} = {@code false}</li>
	 *     <li>{@link #allowOctalNotation} = {@code false}</li>
	 *     <li>{@link #allowDecimalNotation} = {@code false}</li>
	 *     <li>{@link #allowMixedCase} = {@code false}</li>
	 *     <li>{@link #allowEmptySegments} = {@code false}</li>
	 *     <li>{@link #allowMixedNotation} = {@code false}</li>
	 *     <li>{@link #allowZoneId} = {@code false}</li>
	 *     <li>{@link #allowPortSuffix} = {@code false}</li>
	 *     <li>{@link #normalize} = {@code true}</li>
	 * </ul>
	 */
	public static final IpParseOptions STRICT = new IpParseOptions(false, false, false, false, false, false, false, false, true);
	
	/**
	 * Lenient parsing options that accept all supported formats.<br>
	 * <p>
	 *     Configuration:
	 * </p>
	 * <ul>
	 *     <li>{@link #allowLeadingZeros} = {@code true}</li>
	 *     <li>{@link #allowOctalNotation} = {@code false}</li>
	 *     <li>{@link #allowDecimalNotation} = {@code true}</li>
	 *     <li>{@link #allowMixedCase} = {@code true}</li>
	 *     <li>{@link #allowEmptySegments} = {@code true}</li>
	 *     <li>{@link #allowMixedNotation} = {@code true}</li>
	 *     <li>{@link #allowZoneId} = {@code true}</li>
	 *     <li>{@link #allowPortSuffix} = {@code true}</li>
	 *     <li>{@link #normalize} = {@code true}</li>
	 * </ul>
	 */
	public static final IpParseOptions LENIENT = new IpParseOptions(true, false, true, true, true, true, true, true, true);
	
	/**
	 * Constructs a new ip parse options instance.<br>
	 *
	 * @param allowLeadingZeros Allow leading zeros in IPv4 octets without treating them as octal (e.g., "192.168.001.001" parses as "192.168.1.1")
	 * @param allowOctalNotation Interpret leading zeros as octal notation (e.g., "010" is interpreted as decimal 8)
	 * @param allowDecimalNotation Allow IPv4 addresses in decimal notation (e.g., "3232235777" parses as "192.168.1.1")
	 * @param allowMixedCase Allow mixed case hexadecimal characters in IPv6 addresses (e.g., "2001:DB8::1" and "2001:db8::1")
	 * @param allowEmptySegments Allow the double-colon (::) shorthand in IPv6 addresses (e.g., "2001:db8::1" for "2001:db8:0:0:0:0:0:1")
	 * @param allowMixedNotation Allow IPv4-mapped IPv6 addresses (e.g., "::ffff:192.168.1.1")
	 * @param allowZoneId Allow zone identifiers in IPv6 link-local addresses (e.g., "fe80::1%eth0")
	 * @param allowPortSuffix Allow port suffix notation in addresses (e.g., "192.168.1.1:8080" or "[::1]:8080")
	 * @param normalize Normalize the parsed address to its canonical form
	 * @throws IllegalArgumentException If both leading zeros and octal notation are allowed simultaneously
	 */
	public IpParseOptions {
		if (allowLeadingZeros && allowOctalNotation) {
			throw new IllegalArgumentException("Cannot allow both leading zeros and octal notation simultaneously.");
		}
	}
	
	/**
	 * Creates a new builder for constructing ip parse options instances.<br>
	 * @return A new {@link IpParseOptionsBuilder} instance with default values
	 */
	public static @NonNull IpParseOptionsBuilder builder() {
		return new IpParseOptionsBuilder();
	}
}
