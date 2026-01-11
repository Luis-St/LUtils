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

import net.luis.utils.io.network.address.exception.IpParseErrorType;
import net.luis.utils.io.network.address.exception.IpParseException;
import net.luis.utils.io.network.address.format.IpParseOptions;
import net.luis.utils.io.network.address.ipv4.*;
import net.luis.utils.io.network.address.ipv6.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.*;
import java.util.Objects;
import java.util.Optional;

/**
 * Factory and utility methods for creating and parsing IP addresses and networks.<br>
 * This class serves as the main entry point for the IP address library, providing methods to parse, validate, and create IP addresses in various formats.
 * <p>
 *     Supported formats:
 * </p>
 * <ul>
 *     <li>IPv4 dotted-decimal: {@code "192.168.1.1"}</li>
 *     <li>IPv6 colon-hexadecimal: {@code "2001:db8::1"}</li>
 *     <li>IPv6 with zone ID: {@code "fe80::1%eth0"}</li>
 *     <li>IPv4-mapped IPv6: {@code "::ffff:192.168.1.1"}</li>
 *     <li>CIDR notation: {@code "192.168.1.0/24"} or {@code "2001:db8::/32"}</li>
 * </ul>
 * <pre>{@code
 * // Parse addresses
 * Ipv4Address ipv4 = IpAddresses.parseIpv4("192.168.1.1");
 * Ipv6Address ipv6 = IpAddresses.parseIpv6("2001:db8::1");
 *
 * // Auto-detect version
 * IpAddress<?> address = IpAddresses.parse("192.168.1.1");
 *
 * // Parse networks
 * Ipv4Network network = IpAddresses.parseIpv4Network("192.168.0.0/16");
 *
 * // Use well-known networks
 * if (IpAddresses.IPV4_PRIVATE_CLASS_C.contains(ipv4)) {
 *     System.out.println("Private address");
 * }
 *
 * // Convert from java.net
 * InetAddress inet = InetAddress.getByName("8.8.8.8");
 * IpAddress<?> converted = IpAddresses.from(inet);
 * }</pre>
 *
 * @author Luis-St
 */
public final class IpAddresses {

	/**
	 * The IPv4 loopback network (127.0.0.0/8).<br>
	 * This network is reserved for loopback addresses, with 127.0.0.1 being the most commonly used loopback address.<br>
	 */
	public static final Ipv4Network IPV4_LOOPBACK_NETWORK = Ipv4Network.parse("127.0.0.0/8");
	/**
	 * The IPv4 link-local network (169.254.0.0/16).<br>
	 * This network is used for automatic private IP addressing (APIPA) when no DHCP server is available.<br>
	 */
	public static final Ipv4Network IPV4_LINK_LOCAL_NETWORK = Ipv4Network.parse("169.254.0.0/16");
	/**
	 * The IPv4 private Class A network (10.0.0.0/8).<br>
	 * This is the largest private address block, commonly used in large enterprise networks.<br>
	 */
	public static final Ipv4Network IPV4_PRIVATE_CLASS_A = Ipv4Network.parse("10.0.0.0/8");
	/**
	 * The IPv4 private Class B network (172.16.0.0/12).<br>
	 * This private address block covers 172.16.0.0 through 172.31.255.255.<br>
	 */
	public static final Ipv4Network IPV4_PRIVATE_CLASS_B = Ipv4Network.parse("172.16.0.0/12");
	/**
	 * The IPv4 private Class C network (192.168.0.0/16).<br>
	 * This is the most commonly used private address block for home and small office networks.<br>
	 */
	public static final Ipv4Network IPV4_PRIVATE_CLASS_C = Ipv4Network.parse("192.168.0.0/16");
	/**
	 * The IPv4 shared address space network (100.64.0.0/10).<br>
	 * This address block is designated for use in carrier-grade NAT (CGN) environments as defined in RFC 6598.<br>
	 */
	public static final Ipv4Network IPV4_SHARED_ADDRESS_SPACE = Ipv4Network.parse("100.64.0.0/10");

	/**
	 * The IPv6 loopback network (::1/128).<br>
	 * The IPv6 loopback address is a single address (::1) used for localhost communication.<br>
	 */
	public static final Ipv6Network IPV6_LOOPBACK_NETWORK = Ipv6Network.of(Ipv6Address.LOOPBACK, 128);
	/**
	 * The IPv6 link-local network (fe80::/10).<br>
	 * Link-local addresses are used for communication within a single network segment and are not routable beyond the local link.<br>
	 */
	public static final Ipv6Network IPV6_LINK_LOCAL_NETWORK = Ipv6Network.parse("fe80::/10");
	/**
	 * The IPv6 unique local address network (fc00::/7).<br>
	 * Unique local addresses (ULA) are similar to IPv4 private addresses and are intended for local communications within a site or organization.<br>
	 */
	public static final Ipv6Network IPV6_UNIQUE_LOCAL_NETWORK = Ipv6Network.parse("fc00::/7");
	/**
	 * The IPv6 documentation network (2001:db8::/32).<br>
	 * This address block is reserved for use in documentation and example code, and should never appear in production networks.<br>
	 */
	public static final Ipv6Network IPV6_DOCUMENTATION_NETWORK = Ipv6Network.parse("2001:db8::/32");
	/**
	 * The IPv6 multicast network (ff00::/8).<br>
	 * All IPv6 multicast addresses begin with ff, with the following bits indicating flags and scope.<br>
	 */
	public static final Ipv6Network IPV6_MULTICAST_NETWORK = Ipv6Network.parse("ff00::/8");

	/**
	 * Private constructor to prevent instantiation.
	 */
	private IpAddresses() {}

	/**
	 * Parses an IPv4 address from the given string using default parse options.<br>
	 * The address must be in dotted-decimal notation (e.g., "192.168.1.1").<br>
	 *
	 * @param address The address string to parse
	 * @return The parsed IPv4 address
	 * @throws NullPointerException If the address is null
	 * @throws IpParseException If the address cannot be parsed
	 */
	public static @NonNull Ipv4Address parseIpv4(@NonNull String address) {
		return parseIpv4(address, IpParseOptions.DEFAULT);
	}

	/**
	 * Parses an IPv4 address from the given string using the specified parse options.<br>
	 * The address must be in dotted-decimal notation (e.g., "192.168.1.1").<br>
	 *
	 * @param address The address string to parse
	 * @param options The parse options to use
	 * @return The parsed IPv4 address
	 * @throws NullPointerException If the address or options is null
	 * @throws IpParseException If the address cannot be parsed
	 */
	public static @NonNull Ipv4Address parseIpv4(@NonNull String address, @NonNull IpParseOptions options) {
		return tryParseIpv4(address, options).orElseThrow(() ->
			new IpParseException("Invalid IPv4 address: " + address, IpParseErrorType.INVALID_FORMAT, address)
		);
	}

	/**
	 * Attempts to parse an IPv4 address from the given string using default parse options.<br>
	 * The address must be in dotted-decimal notation (e.g., "192.168.1.1").<br>
	 *
	 * @param address The address string to parse
	 * @return An optional containing the parsed address, or empty if parsing fails
	 * @throws NullPointerException If the address is null
	 */
	public static @NonNull Optional<Ipv4Address> tryParseIpv4(@NonNull String address) {
		return tryParseIpv4(address, IpParseOptions.DEFAULT);
	}

	/**
	 * Attempts to parse an IPv4 address from the given string using the specified parse options.<br>
	 * The address must be in dotted-decimal notation (e.g., "192.168.1.1").<br>
	 *
	 * @param address The address string to parse
	 * @param options The parse options to use
	 * @return An optional containing the parsed address, or empty if parsing fails
	 * @throws NullPointerException If the address or options is null
	 */
	public static @NonNull Optional<Ipv4Address> tryParseIpv4(@NonNull String address, @NonNull IpParseOptions options) {
		Objects.requireNonNull(address, "Address must not be null");
		Objects.requireNonNull(options, "Options must not be null");

		String addressToParse = address.trim();
		if (options.allowPortSuffix()) {
			int colonIndex = addressToParse.lastIndexOf(':');
			if (colonIndex != -1) {
				String portPart = addressToParse.substring(colonIndex + 1);
				if (isValidPort(portPart)) {
					addressToParse = addressToParse.substring(0, colonIndex);
				}
			}
		}

		if (options.allowDecimalNotation() && !addressToParse.contains(".")) {
			return tryParseIpv4Decimal(addressToParse);
		}
		return tryParseIpv4DottedDecimal(addressToParse, options);
	}

	/**
	 * Parses an IPv4 address from a decimal integer representation.<br>
	 *
	 * @param address The decimal string to parse
	 * @return An optional containing the parsed address, or empty if parsing fails
	 */
	private static @NonNull Optional<Ipv4Address> tryParseIpv4Decimal(@NonNull String address) {
		try {
			long value = Long.parseLong(address);
			if (value < 0 || value > 0xFFFFFFFFL) {
				return Optional.empty();
			}
			return Optional.of(Ipv4Address.fromValue((int) value));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	/**
	 * Parses an IPv4 address from dotted-decimal notation.<br>
	 *
	 * @param address The address string to parse
	 * @param options The parse options to use
	 * @return An optional containing the parsed address, or empty if parsing fails
	 */
	private static @NonNull Optional<Ipv4Address> tryParseIpv4DottedDecimal(@NonNull String address, @NonNull IpParseOptions options) {
		String[] parts = address.split("\\.", -1);
		if (parts.length != 4) {
			return Optional.empty();
		}

		int value = 0;
		for (int i = 0; i < 4; i++) {
			String part = parts[i];
			if (part.isEmpty()) {
				return Optional.empty();
			}

			int octet;
			try {
				if (part.length() > 1 && part.charAt(0) == '0') {
					if (options.allowOctalNotation()) {
						octet = Integer.parseInt(part, 8);
					} else if (options.allowLeadingZeros()) {
						octet = Integer.parseInt(part);
					} else {
						return Optional.empty();
					}
				} else {
					octet = Integer.parseInt(part);
				}
			} catch (NumberFormatException e) {
				return Optional.empty();
			}

			if (octet < 0 || octet > 255) {
				return Optional.empty();
			}
			value = (value << 8) | octet;
		}
		return Optional.of(Ipv4Address.fromValue(value));
	}

	/**
	 * Parses an IPv6 address from the given string using default parse options.<br>
	 * The address can be in full or compressed colon-hexadecimal notation (e.g., "2001:db8::1" or "2001:0db8:0000:0000:0000:0000:0000:0001").<br>
	 *
	 * @param address The address string to parse
	 * @return The parsed IPv6 address
	 * @throws NullPointerException If the address is null
	 * @throws IpParseException If the address cannot be parsed
	 */
	public static @NonNull Ipv6Address parseIpv6(@NonNull String address) {
		return parseIpv6(address, IpParseOptions.DEFAULT);
	}

	/**
	 * Parses an IPv6 address from the given string using the specified parse options.<br>
	 * The address can be in full or compressed colon-hexadecimal notation (e.g., "2001:db8::1" or "2001:0db8:0000:0000:0000:0000:0000:0001").<br>
	 *
	 * @param address The address string to parse
	 * @param options The parse options to use
	 * @return The parsed IPv6 address
	 * @throws NullPointerException If the address or options is null
	 * @throws IpParseException If the address cannot be parsed
	 */
	public static @NonNull Ipv6Address parseIpv6(@NonNull String address, @NonNull IpParseOptions options) {
		return tryParseIpv6(address, options).orElseThrow(() ->
			new IpParseException("Invalid IPv6 address: " + address, IpParseErrorType.INVALID_FORMAT, address)
		);
	}

	/**
	 * Attempts to parse an IPv6 address from the given string using default parse options.<br>
	 * The address can be in full or compressed colon-hexadecimal notation (e.g., "2001:db8::1" or "2001:0db8:0000:0000:0000:0000:0000:0001").<br>
	 *
	 * @param address The address string to parse
	 * @return An optional containing the parsed address, or empty if parsing fails
	 * @throws NullPointerException If the address is null
	 */
	public static @NonNull Optional<Ipv6Address> tryParseIpv6(@NonNull String address) {
		return tryParseIpv6(address, IpParseOptions.DEFAULT);
	}

	/**
	 * Attempts to parse an IPv6 address from the given string using the specified parse options.<br>
	 * The address can be in full or compressed colon-hexadecimal notation (e.g., "2001:db8::1" or "2001:0db8:0000:0000:0000:0000:0000:0001").<br>
	 *
	 * @param address The address string to parse
	 * @param options The parse options to use
	 * @return An optional containing the parsed address, or empty if parsing fails
	 * @throws NullPointerException If the address or options is null
	 */
	public static @NonNull Optional<Ipv6Address> tryParseIpv6(@NonNull String address, @NonNull IpParseOptions options) {
		Objects.requireNonNull(address, "Address must not be null");
		Objects.requireNonNull(options, "Options must not be null");

		String addressToParse = address.trim();

		if (options.allowPortSuffix() && addressToParse.startsWith("[")) {
			int closeBracket = addressToParse.indexOf(']');
			if (closeBracket != -1) {
				addressToParse = addressToParse.substring(1, closeBracket);
			}
		}

		String zoneId = null;
		if (options.allowZoneId()) {
			int zoneIndex = addressToParse.indexOf('%');
			if (zoneIndex != -1) {
				zoneId = addressToParse.substring(zoneIndex + 1);
				addressToParse = addressToParse.substring(0, zoneIndex);
				if (zoneId.isEmpty()) {
					return Optional.empty();
				}
			}
		} else {
			if (addressToParse.contains("%")) {
				return Optional.empty();
			}
		}

		int lastColonIndex = addressToParse.lastIndexOf(':');
		if (lastColonIndex != -1 && options.allowMixedNotation()) {
			String possibleIpv4 = addressToParse.substring(lastColonIndex + 1);
			if (possibleIpv4.contains(".")) {
				return tryParseIpv6MixedNotation(addressToParse, zoneId, options);
			}
		}

		if (!options.allowMixedCase()) {
			String lower = addressToParse.toLowerCase();
			String upper = addressToParse.toUpperCase();
			if (!addressToParse.equals(lower) && !addressToParse.equals(upper)) {
				return Optional.empty();
			}
		}

		int doubleColonIndex = addressToParse.indexOf("::");
		if (doubleColonIndex != -1 && !options.allowEmptySegments()) {
			return Optional.empty();
		}
		return tryParseIpv6Standard(addressToParse, zoneId, options);
	}

	/**
	 * Parses a standard IPv6 address (without mixed IPv4 notation).<br>
	 *
	 * @param address The address string to parse
	 * @param zoneId The zone ID, or null if not present
	 * @param options The parse options to use
	 * @return An optional containing the parsed address, or empty if parsing fails
	 * @throws NullPointerException If the address or options is null
	 */
	@SuppressWarnings("DuplicatedCode")
	private static @NonNull Optional<Ipv6Address> tryParseIpv6Standard(@NonNull String address, @Nullable String zoneId, @NonNull IpParseOptions options) {
		Objects.requireNonNull(address, "Address must not be null");
		Objects.requireNonNull(options, "Options must not be null");
		
		int doubleColonIndex = address.indexOf("::");
		long highBits = 0;
		long lowBits = 0;

		if (doubleColonIndex == -1) {
			String[] groups = address.split(":", -1);
			if (groups.length != 8) {
				return Optional.empty();
			}

			for (int i = 0; i < 8; i++) {
				int value = parseHextet(groups[i], options);
				if (value < 0) {
					return Optional.empty();
				}
				if (i < 4) {
					highBits = (highBits << 16) | value;
				} else {
					lowBits = (lowBits << 16) | value;
				}
			}
		} else {
			if (address.indexOf("::", doubleColonIndex + 2) != -1) {
				return Optional.empty();
			}

			String leftPart = address.substring(0, doubleColonIndex);
			String rightPart = address.substring(doubleColonIndex + 2);

			String[] leftGroups = leftPart.isEmpty() ? new String[0] : leftPart.split(":", -1);
			String[] rightGroups = rightPart.isEmpty() ? new String[0] : rightPart.split(":", -1);

			int totalGroups = leftGroups.length + rightGroups.length;
			if (totalGroups > 7) {
				return Optional.empty(); // Too many groups
			}

			int zerosNeeded = 8 - totalGroups;
			int[] hextets = new int[8];

			for (int i = 0; i < leftGroups.length; i++) {
				int value = parseHextet(leftGroups[i], options);
				if (value < 0) {
					return Optional.empty();
				}
				hextets[i] = value;
			}

			for (int i = 0; i < rightGroups.length; i++) {
				int value = parseHextet(rightGroups[i], options);
				if (value < 0) {
					return Optional.empty();
				}
				hextets[leftGroups.length + zerosNeeded + i] = value;
			}

			for (int i = 0; i < 4; i++) {
				highBits = (highBits << 16) | hextets[i];
			}
			for (int i = 4; i < 8; i++) {
				lowBits = (lowBits << 16) | hextets[i];
			}
		}
		return Optional.of(new Ipv6Address(highBits, lowBits, zoneId));
	}

	/**
	 * Parses an IPv6 address in mixed notation (::ffff:192.168.1.1).
	 *
	 * @param address The address string to parse
	 * @param zoneId The zone ID, or null if not present
	 * @param options The parse options to use
	 * @return An optional containing the parsed address, or empty if parsing fails
	 * @throws NullPointerException If the address or options is null
	 */
	@SuppressWarnings("DuplicatedCode")
	private static @NonNull Optional<Ipv6Address> tryParseIpv6MixedNotation(@NonNull String address, @Nullable String zoneId, @NonNull IpParseOptions options) {
		Objects.requireNonNull(address, "Address must not be null");
		Objects.requireNonNull(options, "Options must not be null");
		
		int lastColonIndex = address.lastIndexOf(':');
		String ipv6Part = address.substring(0, lastColonIndex);
		String ipv4Part = address.substring(lastColonIndex + 1);

		Optional<Ipv4Address> ipv4Optional = tryParseIpv4DottedDecimal(ipv4Part, options);
		if (ipv4Optional.isEmpty()) {
			return Optional.empty();
		}
		Ipv4Address ipv4 = ipv4Optional.get();

		int doubleColonIndex = ipv6Part.indexOf("::");
		int[] hextets = new int[6];
		if (doubleColonIndex == -1) {
			String[] groups = ipv6Part.split(":", -1);
			if (groups.length != 6) {
				return Optional.empty();
			}
			for (int i = 0; i < 6; i++) {
				int value = parseHextet(groups[i], options);
				if (value < 0) {
					return Optional.empty();
				}
				hextets[i] = value;
			}
		} else {
			if (ipv6Part.indexOf("::", doubleColonIndex + 2) != -1) {
				return Optional.empty();
			}

			String leftPart = ipv6Part.substring(0, doubleColonIndex);
			String rightPart = ipv6Part.substring(doubleColonIndex + 2);

			String[] leftGroups = leftPart.isEmpty() ? ArrayUtils.EMPTY_STRING_ARRAY : leftPart.split(":", -1);
			String[] rightGroups = rightPart.isEmpty() ? ArrayUtils.EMPTY_STRING_ARRAY : rightPart.split(":", -1);
			int totalGroups = leftGroups.length + rightGroups.length;
			if (totalGroups > 5) {
				return Optional.empty();
			}

			int zerosNeeded = 6 - totalGroups;
			for (int i = 0; i < leftGroups.length; i++) {
				int value = parseHextet(leftGroups[i], options);
				if (value < 0) {
					return Optional.empty();
				}
				hextets[i] = value;
			}

			for (int i = 0; i < rightGroups.length; i++) {
				int value = parseHextet(rightGroups[i], options);
				if (value < 0) {
					return Optional.empty();
				}
				hextets[leftGroups.length + zerosNeeded + i] = value;
			}
		}

		long highBits = ((long) hextets[0] << 48) | ((long) hextets[1] << 32) | ((long) hextets[2] << 16) | hextets[3];
		long lowBits = ((long) hextets[4] << 48) | ((long) hextets[5] << 32) | (ipv4.toUnsignedLong() & 0xFFFFFFFFL);
		return Optional.of(new Ipv6Address(highBits, lowBits, zoneId));
	}

	/**
	 * Parses a hextet (16-bit group) from a string.<br>
	 *
	 * @param hextet The hextet string to parse
	 * @param options The parse options to use
	 * @return The parsed value (0-65535), or -1 if invalid
	 */
	private static int parseHextet(@NonNull String hextet, @NonNull IpParseOptions options) {
		if (hextet.isEmpty() || hextet.length() > 4) {
			return -1;
		}

		for (int i = 0; i < hextet.length(); i++) {
			char c = hextet.charAt(i);
			if (!isHexDigit(c)) {
				return -1;
			}
		}

		try {
			int value = Integer.parseInt(hextet, 16);
			if (value < 0 || value > 0xFFFF) {
				return -1;
			}
			return value;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Checks if a character is a hexadecimal digit.<br>
	 *
	 * @param c The character to check
	 * @return {@code true} if the character is a hex digit, {@code false} otherwise
	 */
	private static boolean isHexDigit(char c) {
		return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
	}

	/**
	 * Parses an IP address from the given string, auto-detecting the version.<br>
	 * This method first attempts to parse as IPv4, then as IPv6.<br>
	 *
	 * @param address The address string to parse
	 * @return The parsed IP address
	 * @throws NullPointerException If the address is null
	 * @throws IpParseException If the address cannot be parsed
	 */
	public static @NonNull IpAddress<?> parse(@NonNull String address) {
		return parse(address, IpParseOptions.DEFAULT);
	}

	/**
	 * Parses an IP address from the given string using the specified options, auto-detecting the version.<br>
	 * This method first attempts to parse as IPv4, then as IPv6.<br>
	 *
	 * @param address The address string to parse
	 * @param options The parse options to use
	 * @return The parsed IP address
	 * @throws NullPointerException If the address or options is null
	 * @throws IpParseException If the address cannot be parsed
	 */
	public static @NonNull IpAddress<?> parse(@NonNull String address, @NonNull IpParseOptions options) {
		return tryParse(address, options).orElseThrow(() ->
			new IpParseException("Invalid IP address: " + address, IpParseErrorType.INVALID_FORMAT, address)
		);
	}

	/**
	 * Attempts to parse an IP address from the given string, auto-detecting the version.<br>
	 * This method first attempts to parse as IPv4, then as IPv6.<br>
	 *
	 * @param address The address string to parse
	 * @return An optional containing the parsed address, or empty if parsing fails
	 * @throws NullPointerException If the address is null
	 */
	public static @NonNull Optional<? extends IpAddress<?>> tryParse(@NonNull String address) {
		return tryParse(address, IpParseOptions.DEFAULT);
	}

	/**
	 * Attempts to parse an IP address from the given string using the specified options, auto-detecting the version.<br>
	 * This method first attempts to parse as IPv4, then as IPv6.<br>
	 *
	 * @param address The address string to parse
	 * @param options The parse options to use
	 * @return An optional containing the parsed address, or empty if parsing fails
	 * @throws NullPointerException If the address or options is null
	 */
	public static @NonNull Optional<? extends IpAddress<?>> tryParse(@NonNull String address, @NonNull IpParseOptions options) {
		Objects.requireNonNull(address, "Address must not be null");
		Objects.requireNonNull(options, "Options must not be null");

		if (address.contains(":")) {
			Optional<Ipv6Address> ipv6 = tryParseIpv6(address, options);
			if (ipv6.isPresent()) {
				return ipv6;
			}
		}

		Optional<Ipv4Address> ipv4 = tryParseIpv4(address, options);
		if (ipv4.isPresent()) {
			return ipv4;
		}

		if (!address.contains(":")) {
			return tryParseIpv6(address, options);
		}
		return Optional.empty();
	}

	/**
	 * Checks if the given string is a valid IPv4 address.<br>
	 *
	 * @param address The address string to validate
	 * @return {@code true} if the address is a valid IPv4 address, {@code false} otherwise
	 * @throws NullPointerException If the address is null
	 */
	public static boolean isValidIpv4(@NonNull String address) {
		Objects.requireNonNull(address, "Address must not be null");
		return tryParseIpv4(address).isPresent();
	}

	/**
	 * Checks if the given string is a valid IPv6 address.<br>
	 *
	 * @param address The address string to validate
	 * @return {@code true} if the address is a valid IPv6 address, {@code false} otherwise
	 * @throws NullPointerException If the address is null
	 */
	public static boolean isValidIpv6(@NonNull String address) {
		Objects.requireNonNull(address, "Address must not be null");
		return tryParseIpv6(address).isPresent();
	}

	/**
	 * Checks if the given string is a valid IP address (either IPv4 or IPv6).<br>
	 *
	 * @param address The address string to validate
	 * @return {@code true} if the address is a valid IP address, {@code false} otherwise
	 * @throws NullPointerException If the address is null
	 */
	public static boolean isValid(@NonNull String address) {
		Objects.requireNonNull(address, "Address must not be null");
		return tryParse(address).isPresent();
	}

	/**
	 * Parses an IPv4 network from CIDR notation (e.g., "192.168.1.0/24").<br>
	 *
	 * @param cidr The CIDR notation string to parse
	 * @return The parsed IPv4 network
	 * @throws NullPointerException If the CIDR string is null
	 * @throws IpParseException If the CIDR string cannot be parsed
	 */
	public static @NonNull Ipv4Network parseIpv4Network(@NonNull String cidr) {
		return tryParseIpv4Network(cidr).orElseThrow(() ->
			new IpParseException("Invalid IPv4 CIDR notation: " + cidr, IpParseErrorType.INVALID_FORMAT, cidr)
		);
	}

	/**
	 * Attempts to parse an IPv4 network from CIDR notation (e.g., "192.168.1.0/24").<br>
	 *
	 * @param cidr The CIDR notation string to parse
	 * @return An optional containing the parsed network, or empty if parsing fails
	 * @throws NullPointerException If the CIDR string is null
	 */
	public static @NonNull Optional<Ipv4Network> tryParseIpv4Network(@NonNull String cidr) {
		Objects.requireNonNull(cidr, "CIDR must not be null");
		return Ipv4Network.tryParse(cidr);
	}

	/**
	 * Parses an IPv6 network from CIDR notation (e.g., "2001:db8::/32").<br>
	 *
	 * @param cidr The CIDR notation string to parse
	 * @return The parsed IPv6 network
	 * @throws NullPointerException If the CIDR string is null
	 * @throws IpParseException If the CIDR string cannot be parsed
	 */
	public static @NonNull Ipv6Network parseIpv6Network(@NonNull String cidr) {
		return tryParseIpv6Network(cidr).orElseThrow(() ->
			new IpParseException("Invalid IPv6 CIDR notation: " + cidr, IpParseErrorType.INVALID_FORMAT, cidr)
		);
	}

	/**
	 * Attempts to parse an IPv6 network from CIDR notation (e.g., "2001:db8::/32").<br>
	 *
	 * @param cidr The CIDR notation string to parse
	 * @return An optional containing the parsed network, or empty if parsing fails
	 * @throws NullPointerException If the CIDR string is null
	 */
	public static @NonNull Optional<Ipv6Network> tryParseIpv6Network(@NonNull String cidr) {
		Objects.requireNonNull(cidr, "CIDR must not be null");
		return Ipv6Network.tryParse(cidr);
	}

	/**
	 * Creates an IP address from a {@link InetAddress}.<br>
	 * This method returns an {@link Ipv4Address} for {@link Inet4Address} and an {@link Ipv6Address} for {@link Inet6Address}.
	 *
	 * @param address The internet address to convert
	 * @return The corresponding IP address
	 * @throws NullPointerException If the address is null
	 * @throws IllegalArgumentException If the address type is unknown
	 */
	public static @NonNull IpAddress<?> from(@NonNull InetAddress address) {
		Objects.requireNonNull(address, "Address must not be null");
		if (address instanceof Inet4Address inet4) {
			return from(inet4);
		} else if (address instanceof Inet6Address inet6) {
			return from(inet6);
		}
		throw new IllegalArgumentException("Unknown InetAddress type: " + address.getClass().getName());
	}

	/**
	 * Creates an IPv4 address from a {@link Inet4Address}.<br>
	 *
	 * @param address The internet address v4 to convert
	 * @return The corresponding IPv4 address
	 * @throws NullPointerException If the address is null
	 */
	public static @NonNull Ipv4Address from(@NonNull Inet4Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		return Ipv4Address.from(address);
	}

	/**
	 * Creates an IPv6 address from a {@link java.net.Inet6Address}.<br>
	 *
	 * @param address The internet v6 address to convert
	 * @return The corresponding IPv6 address
	 * @throws NullPointerException If the address is null
	 */
	public static @NonNull Ipv6Address from(@NonNull Inet6Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		return Ipv6Address.from(address);
	}

	/**
	 * Creates an IPv4 address range from the start to end addresses (inclusive).<br>
	 *
	 * @param start The first address in the range
	 * @param end The last address in the range
	 * @return A new IPv4 range
	 * @throws NullPointerException If start or end is null
	 * @throws IllegalArgumentException If start is greater than end
	 */
	public static @NonNull Ipv4Range ipv4Range(@NonNull Ipv4Address start, @NonNull Ipv4Address end) {
		return Ipv4Range.of(start, end);
	}

	/**
	 * Creates an IPv6 address range from the start to end addresses (inclusive).<br>
	 *
	 * @param start The first address in the range
	 * @param end The last address in the range
	 * @return A new IPv6 range
	 * @throws NullPointerException If start or end is null
	 * @throws IllegalArgumentException If start is greater than end
	 */
	public static @NonNull Ipv6Range ipv6Range(@NonNull Ipv6Address start, @NonNull Ipv6Address end) {
		return Ipv6Range.of(start, end);
	}

	/**
	 * Checks if a string represents a valid port number (0-65535).
	 *
	 * @param port The string to check
	 * @return {@code true} if the string is a valid port number, {@code false} otherwise
	 */
	private static boolean isValidPort(@NonNull String port) {
		if (port.isEmpty()) {
			return false;
		}
		try {
			int portNum = Integer.parseInt(port);
			return portNum >= 0 && portNum <= 65535;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
