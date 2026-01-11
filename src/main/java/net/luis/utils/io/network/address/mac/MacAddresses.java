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

package net.luis.utils.io.network.address.mac;

import net.luis.utils.io.network.address.exception.IpParseErrorType;
import net.luis.utils.io.network.address.exception.IpParseException;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Factory and utility methods for creating and parsing MAC addresses.<br>
 * This class provides various methods for constructing {@link MacAddress} instances from different representations and formats.<br>
 * <p>
 *     Supported parsing formats:
 * </p>
 * <ul>
 *     <li>Colon-separated: {@code "00:1A:2B:3C:4D:5E"}</li>
 *     <li>Dash-separated: {@code "00-1A-2B-3C-4D-5E"}</li>
 *     <li>Cisco dot notation: {@code "001A.2B3C.4D5E"}</li>
 *     <li>Bare hexadecimal: {@code "001A2B3C4D5E"}</li>
 * </ul>
 * <p>
 *     All formats accept both uppercase and lowercase hexadecimal characters.
 * </p>
 * <pre>{@code
 * // Parse various formats
 * MacAddress mac1 = MacAddresses.parse("00:1A:2B:3C:4D:5E");
 * MacAddress mac2 = MacAddresses.parse("00-1A-2B-3C-4D-5E");
 * MacAddress mac3 = MacAddresses.parse("001A.2B3C.4D5E");
 *
 * // Create from octets
 * MacAddress mac4 = MacAddresses.of(0x00, 0x1A, 0x2B, 0x3C, 0x4D, 0x5E);
 *
 * // Extract MAC from EUI-64 IPv6 address
 * Ipv6Address ipv6 = IpAddresses.parseIpv6("fe80::21a:2bff:fe3c:4d5e");
 * Optional<MacAddress> extracted = MacAddresses.fromEui64(ipv6);
 *
 * // Generate random locally-administered MAC
 * MacAddress randomMac = MacAddresses.random();
 * }</pre>
 *
 * @author Luis-St
 */
public final class MacAddresses {

	/**
	 * Pattern for colon-separated MAC address format (e.g., "00:1A:2B:3C:4D:5E").<br>
	 */
	private static final Pattern COLON_PATTERN = Pattern.compile("^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$");

	/**
	 * Pattern for dash-separated MAC address format (e.g., "00-1A-2B-3C-4D-5E").<br>
	 */
	private static final Pattern DASH_PATTERN = Pattern.compile("^([0-9A-Fa-f]{2}-){5}[0-9A-Fa-f]{2}$");

	/**
	 * Pattern for Cisco dot notation MAC address format (e.g., "001A.2B3C.4D5E").<br>
	 */
	private static final Pattern CISCO_PATTERN = Pattern.compile("^[0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4}$");

	/**
	 * Pattern for bare hexadecimal MAC address format (e.g., "001A2B3C4D5E").<br>
	 */
	private static final Pattern BARE_PATTERN = Pattern.compile("^[0-9A-Fa-f]{12}$");

	/**
	 * The number of octets in a MAC address.<br>
	 */
	private static final int OCTET_COUNT = 6;

	/**
	 * Secure random instance for generating random MAC addresses.<br>
	 */
	private static final SecureRandom RANDOM = new SecureRandom();

	/**
	 * Cached broadcast MAC address (FF:FF:FF:FF:FF:FF).<br>
	 */
	private static final MacAddress BROADCAST = MacAddress.of(0xFFFFFFFFFFFFL);

	/**
	 * Cached zero MAC address (00:00:00:00:00:00).<br>
	 */
	private static final MacAddress ZERO = MacAddress.of(0L);

	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private MacAddresses() {}

	/**
	 * Parses a MAC address from a string representation.<br>
	 * This method supports multiple formats:
	 * <ul>
	 *     <li>Colon-separated: {@code "00:1A:2B:3C:4D:5E"}</li>
	 *     <li>Dash-separated: {@code "00-1A-2B-3C-4D-5E"}</li>
	 *     <li>Cisco dot notation: {@code "001A.2B3C.4D5E"}</li>
	 *     <li>Bare hexadecimal: {@code "001A2B3C4D5E"}</li>
	 * </ul>
	 *
	 * @param address The string representation of the MAC address
	 * @return The parsed {@link MacAddress}
	 * @throws NullPointerException If address is null
	 * @throws IpParseException If the address cannot be parsed
	 */
	public static @NonNull MacAddress parse(@NonNull String address) {
		Objects.requireNonNull(address, "Address must not be null");

		if (address.isEmpty()) {
			throw new IpParseException("MAC address cannot be empty", IpParseErrorType.EMPTY_INPUT, address);
		}

		String normalizedHex = normalizeToHex(address);
		if (normalizedHex == null) {
			throw new IpParseException("Invalid MAC address format: " + address, IpParseErrorType.INVALID_FORMAT, address);
		}

		try {
			long value = Long.parseLong(normalizedHex, 16);
			return MacAddress.of(value);
		} catch (NumberFormatException e) {
			throw new IpParseException("Invalid MAC address format: " + address, e, IpParseErrorType.INVALID_FORMAT, address);
		}
	}

	/**
	 * Attempts to parse a MAC address from a string representation.<br>
	 * This method supports the same formats as {@link #parse(String)} but returns
	 * an empty optional instead of throwing an exception on failure.<br>
	 *
	 * @param address The string representation of the MAC address
	 * @return An optional containing the parsed {@link MacAddress}, or empty if parsing fails
	 */
	public static @NonNull Optional<MacAddress> tryParse(@Nullable String address) {
		if (address == null || address.isEmpty()) {
			return Optional.empty();
		}

		String normalizedHex = normalizeToHex(address);
		if (normalizedHex == null) {
			return Optional.empty();
		}

		try {
			long value = Long.parseLong(normalizedHex, 16);
			return Optional.of(MacAddress.of(value));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	/**
	 * Checks if the given string is a valid MAC address.<br>
	 * This method supports the same formats as {@link #parse(String)}.<br>
	 *
	 * @param address The string to check
	 * @return {@code true} if the string is a valid MAC address, {@code false} otherwise
	 */
	public static boolean isValid(@Nullable String address) {
		if (address == null || address.isEmpty()) {
			return false;
		}
		return normalizeToHex(address) != null;
	}

	/**
	 * Normalizes a MAC address string to bare hexadecimal format.<br>
	 * Returns null if the format is not recognized.<br>
	 *
	 * @param address The MAC address string to normalize
	 * @return The normalized hexadecimal string, or null if invalid
	 * @throws NullPointerException If address is null
	 */
	private static @Nullable String normalizeToHex(@NonNull String address) {
		Objects.requireNonNull(address, "Address must not be null");
		if (COLON_PATTERN.matcher(address).matches()) {
			return address.replace(":", "");
		}

		if (DASH_PATTERN.matcher(address).matches()) {
			return address.replace("-", "");
		}

		if (CISCO_PATTERN.matcher(address).matches()) {
			return address.replace(".", "");
		}

		if (BARE_PATTERN.matcher(address).matches()) {
			return address;
		}
		return null;
	}

	/**
	 * Creates a MAC address from a 6-byte array.<br>
	 *
	 * @param bytes The byte array containing the MAC address (must be exactly 6 bytes)
	 * @return The {@link MacAddress} created from the bytes
	 * @throws NullPointerException If bytes is null
	 * @throws IllegalArgumentException If the byte array is not exactly 6 bytes
	 */
	public static @NonNull MacAddress of(byte @NonNull [] bytes) {
		Objects.requireNonNull(bytes, "Bytes must not be null");
		if (bytes.length != OCTET_COUNT) {
			throw new IllegalArgumentException("MAC address must be exactly 6 bytes, got: " + bytes.length);
		}

		long value = 0;
		for (int i = 0; i < OCTET_COUNT; i++) {
			value = (value << 8) | (bytes[i] & 0xFF);
		}
		return MacAddress.of(value);
	}

	/**
	 * Creates a MAC address from a 48-bit value stored in a long.<br>
	 * Only the lower 48 bits of the long are used.<br>
	 *
	 * @param value The 48-bit MAC address value
	 * @return The {@link MacAddress} created from the value
	 */
	public static @NonNull MacAddress of(long value) {
		return MacAddress.of(value & 0xFFFFFFFFFFFFL);
	}

	/**
	 * Creates a MAC address from six individual octets.<br>
	 * Each octet must be in the range 0-255.<br>
	 *
	 * @param o0 The first octet (most significant)
	 * @param o1 The second octet
	 * @param o2 The third octet
	 * @param o3 The fourth octet
	 * @param o4 The fifth octet
	 * @param o5 The sixth octet (least significant)
	 * @return The {@link MacAddress} created from the octets
	 * @throws IllegalArgumentException If any octet is outside the range 0-255
	 */
	public static @NonNull MacAddress of(int o0, int o1, int o2, int o3, int o4, int o5) {
		validateOctet(o0, 0);
		validateOctet(o1, 1);
		validateOctet(o2, 2);
		validateOctet(o3, 3);
		validateOctet(o4, 4);
		validateOctet(o5, 5);

		long value = ((long) (o0 & 0xFF) << 40)
			| ((long) (o1 & 0xFF) << 32)
			| ((long) (o2 & 0xFF) << 24)
			| ((long) (o3 & 0xFF) << 16)
			| ((long) (o4 & 0xFF) << 8)
			| (o5 & 0xFF);
		return MacAddress.of(value);
	}

	/**
	 * Validates that an octet value is within the valid range (0-255).<br>
	 *
	 * @param value The octet value to validate
	 * @param index The index of the octet (for error message)
	 * @throws IllegalArgumentException If the value is outside the range 0-255
	 */
	private static void validateOctet(int value, int index) {
		if (value < 0 || value > 255) {
			throw new IllegalArgumentException("Octet " + index + " out of range (0-255): " + value);
		}
	}

	/**
	 * Returns the broadcast MAC address (FF:FF:FF:FF:FF:FF).<br>
	 * The broadcast address is used to send frames to all devices on the network.
	 *
	 * @return The broadcast MAC address
	 */
	public static @NonNull MacAddress broadcast() {
		return BROADCAST;
	}

	/**
	 * Returns the zero MAC address (00:00:00:00:00:00).<br>
	 * The zero address is often used as a placeholder or to indicate an unspecified address.
	 *
	 * @return The zero MAC address
	 */
	public static @NonNull MacAddress zero() {
		return ZERO;
	}

	/**
	 * Extracts a MAC address from an EUI-64 encoded IPv6 address.<br>
	 * EUI-64 is a method of creating a 64-bit interface identifier from a 48-bit MAC address
	 * by inserting 0xFFFE in the middle and inverting the universal/local bit.<br>
	 * <p>
	 *     This method reverses that process to extract the original MAC address.<br>
	 *     The IPv6 address must have an EUI-64 format interface identifier (0xFFFE at positions 24-39 of the lower 64 bits).
	 * </p>
	 *
	 * @param address The IPv6 address potentially containing an EUI-64 interface identifier
	 * @return An optional containing the extracted MAC address, or empty if not EUI-64
	 * @throws NullPointerException If address is null
	 */
	public static @NonNull Optional<MacAddress> fromEui64(@NonNull Ipv6Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		return address.extractMacAddress();
	}

	/**
	 * Generates a random locally-administered unicast MAC address.<br>
	 * The locally-administered bit (bit 1 of the first octet) is set to 1, and the multicast bit (bit 0 of the first octet) is set to 0.<br>
	 * <p>
	 *     Locally-administered addresses can be used for virtual machines,<br>
	 *     containers, or other software-defined network interfaces without
	 *     risk of conflicting with manufacturer-assigned addresses.
	 * </p>
	 *
	 * @return A random locally-administered unicast MAC address
	 */
	public static @NonNull MacAddress random() {
		byte[] bytes = new byte[OCTET_COUNT];
		RANDOM.nextBytes(bytes);

		bytes[0] = (byte) ((bytes[0] | 0x02) & 0xFE);
		return of(bytes);
	}

	/**
	 * Generates a random locally-administered multicast MAC address.<br>
	 * The locally-administered bit (bit 1 of the first octet) is set to 1,<br>
	 * and the multicast bit (bit 0 of the first octet) is set to 1.<br>
	 *
	 * @return A random locally-administered multicast MAC address
	 */
	public static @NonNull MacAddress randomMulticast() {
		byte[] bytes = new byte[OCTET_COUNT];
		RANDOM.nextBytes(bytes);

		bytes[0] = (byte) (bytes[0] | 0x03);
		return of(bytes);
	}
}
