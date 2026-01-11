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

import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Network;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a MAC (Media Access Control) address, also known as EUI-48.<br>
 * A MAC address is a 48-bit identifier assigned to network interfaces for communication at the data link layer.<br>
 * It is typically displayed as six groups of two hexadecimal digits separated by colons or dashes.<br>
 * <p>
 *     The first bit (I/G bit) indicates unicast (0) or multicast (1).<br>
 *     The second bit (U/L bit) indicates universal (0) or local (1) administration.<br>
 *     The first three octets form the OUI (Organizationally Unique Identifier), assigned by IEEE to manufacturers.
 * </p>
 * <p>
 *     MAC addresses are closely related to IPv6 through the EUI-64 format,<br>
 *     which generates an interface identifier by inserting 0xFFFE in the middle
 *     of the MAC address and flipping the U/L bit.
 * </p>
 * <pre>{@code
 * MacAddress mac = MacAddresses.parse("00:1A:2B:3C:4D:5E");
 *
 * // Check address properties
 * if (mac.isUnicast() && mac.isUniversal()) {
 *     System.out.println("Universal unicast address");
 * }
 *
 * // Generate IPv6 link-local address
 * Ipv6Address linkLocal = mac.toLinkLocalIpv6();
 * // Result: fe80::21a:2bff:fe3c:4d5e
 *
 * // Various string formats
 * System.out.println(mac.toColonString());  // 00:1a:2b:3c:4d:5e
 * System.out.println(mac.toDashString());   // 00-1A-2B-3C-4D-5E
 * System.out.println(mac.toCiscoString());  // 001a.2b3c.4d5e
 * }</pre>
 *
 * @author Luis-St
 *
 * @param value The 48-bit MAC address stored in the lower 48 bits of a long
 */
public record MacAddress(long value) implements Comparable<MacAddress> {

	/**
	 * The number of bits in a MAC address.<br>
	 */
	public static final int BIT_LENGTH = 48;

	/**
	 * The number of octets in a MAC address.<br>
	 */
	public static final int OCTET_COUNT = 6;

	/**
	 * The broadcast MAC address (FF:FF:FF:FF:FF:FF).<br>
	 */
	public static final MacAddress BROADCAST = new MacAddress(0xFFFF_FFFF_FFFFL);

	/**
	 * The zero MAC address (00:00:00:00:00:00).<br>
	 */
	public static final MacAddress ZERO = new MacAddress(0L);

	/**
	 * Compact constructor that masks the value to ensure only the lower 48 bits are used.<br>
	 * @param value The 48-bit MAC address value
	 */
	public MacAddress {
		value = value & 0xFFFF_FFFF_FFFFL;
	}

	/**
	 * Creates a MAC address from a 48-bit value.<br>
	 *
	 * @param value The address as an unsigned 48-bit value (stored in long)
	 * @return The MAC address
	 */
	public static @NonNull MacAddress of(long value) {
		return new MacAddress(value);
	}

	/**
	 * Returns the MAC address as raw bytes in network byte order.<br>
	 * @return A 6-byte array representing the MAC address
	 */
	public byte @NonNull [] toBytes() {
		byte[] bytes = new byte[OCTET_COUNT];
		for (int i = 0; i < OCTET_COUNT; i++) {
			bytes[i] = (byte) ((this.value >>> (40 - i * 8)) & 0xFF);
		}
		return bytes;
	}

	/**
	 * Returns the MAC address as a 48-bit value stored in a long.<br>
	 * This is equivalent to the record component accessor.<br>
	 *
	 * @return The 48-bit MAC address value
	 */
	public long toLong() {
		return this.value;
	}

	/**
	 * Returns the octet at the specified index.<br>
	 * Index 0 is the most significant octet (leftmost in standard notation).<br>
	 *
	 * @param index The octet index (0-5)
	 * @return The octet value (0-255)
	 * @throws IndexOutOfBoundsException If the index is out of range
	 */
	public int getOctet(int index) {
		if (index < 0 || index >= OCTET_COUNT) {
			throw new IndexOutOfBoundsException("Octet index out of range: " + index);
		}
		return (int) ((this.value >>> (40 - index * 8)) & 0xFF);
	}

	/**
	 * Returns a new address with the specified octet changed.<br>
	 *
	 * @param index The octet index (0-5)
	 * @param value The new octet value (0-255)
	 * @return A new address with the modified octet
	 * @throws IndexOutOfBoundsException If the index is out of range
	 * @throws IllegalArgumentException If the value is out of range
	 */
	public @NonNull MacAddress withOctet(int index, int value) {
		if (index < 0 || index >= OCTET_COUNT) {
			throw new IndexOutOfBoundsException("Octet index out of range: " + index);
		}
		if (value < 0 || value > 0xFF) {
			throw new IllegalArgumentException("Octet value out of range: " + value);
		}
		
		int shift = 40 - index * 8;
		long mask = 0xFFL << shift;
		long newValue = (this.value & ~mask) | ((long) value << shift);
		return new MacAddress(newValue);
	}

	/**
	 * Returns the OUI (Organizationally Unique Identifier) bytes.<br>
	 * The OUI is the first 3 octets, assigned to vendors by IEEE.<br>
	 *
	 * @return A 3-byte array containing the OUI
	 */
	public byte @NonNull [] getOui() {
		byte[] oui = new byte[3];
		oui[0] = (byte) ((this.value >>> 40) & 0xFF);
		oui[1] = (byte) ((this.value >>> 32) & 0xFF);
		oui[2] = (byte) ((this.value >>> 24) & 0xFF);
		return oui;
	}

	/**
	 * Returns the OUI as a colon-separated string (e.g., "00:1A:2B").<br>
	 * @return The OUI string in uppercase colon-delimited format
	 */
	public @NonNull String getOuiString() {
		return String.format("%02X:%02X:%02X",
			(this.value >>> 40) & 0xFF,
			(this.value >>> 32) & 0xFF,
			(this.value >>> 24) & 0xFF);
	}

	/**
	 * Checks if this is a unicast address.<br>
	 * The I/G bit (bit 0 of the first octet) is 0 for unicast addresses.<br>
	 *
	 * @return {@code true} if this is a unicast address, {@code false} otherwise
	 */
	public boolean isUnicast() {
		return ((this.value >>> 40) & 0x01) == 0;
	}

	/**
	 * Checks if this is a multicast address.<br>
	 * The I/G bit (bit 0 of the first octet) is 1 for multicast addresses.<br>
	 *
	 * @return {@code true} if this is a multicast address, {@code false} otherwise
	 */
	public boolean isMulticast() {
		return ((this.value >>> 40) & 0x01) == 1;
	}

	/**
	 * Checks if this is a universally administered address.<br>
	 * The U/L bit (bit 1 of the first octet) is 0 for universally administered addresses.<br>
	 *
	 * @return {@code true} if this is a universally administered address, {@code false} otherwise
	 */
	public boolean isUniversal() {
		return ((this.value >>> 40) & 0x02) == 0;
	}

	/**
	 * Checks if this is a locally administered address.<br>
	 * The U/L bit (bit 1 of the first octet) is 1 for locally administered addresses.<br>
	 *
	 * @return {@code true} if this is a locally administered address, {@code false} otherwise
	 */
	public boolean isLocal() {
		return ((this.value >>> 40) & 0x02) == 0x02;
	}

	/**
	 * Checks if this is the broadcast address (FF:FF:FF:FF:FF:FF).<br>
	 * @return {@code true} if this is the broadcast address, {@code false} otherwise
	 */
	public boolean isBroadcast() {
		return this.value == 0xFFFF_FFFF_FFFFL;
	}

	/**
	 * Converts this MAC address to a Modified EUI-64 identifier.<br>
	 * The algorithm:<br>
	 * <ol>
	 *     <li>Split the MAC into two 24-bit halves</li>
	 *     <li>Insert 0xFFFE in the middle</li>
	 *     <li>Flip the U/L bit (bit 1 of the first octet)</li>
	 * </ol>
	 *
	 * @return The 8-byte Modified EUI-64 identifier
	 */
	public byte @NonNull [] toModifiedEui64() {
		byte[] eui64 = new byte[8];
		
		eui64[0] = (byte) (((this.value >>> 40) & 0xFF) ^ 0x02);
		eui64[1] = (byte) ((this.value >>> 32) & 0xFF);
		eui64[2] = (byte) ((this.value >>> 24) & 0xFF);
		
		eui64[3] = (byte) 0xFF;
		eui64[4] = (byte) 0xFE;
		
		eui64[5] = (byte) ((this.value >>> 16) & 0xFF);
		eui64[6] = (byte) ((this.value >>> 8) & 0xFF);
		eui64[7] = (byte) (this.value & 0xFF);
		return eui64;
	}

	/**
	 * Generates an IPv6 link-local address from this MAC using Modified EUI-64.<br>
	 * Format: fe80::xxxx:xxff:fexx:xxxx<br>
	 *
	 * @return The link-local IPv6 address
	 */
	public @NonNull Ipv6Address toLinkLocalIpv6() {
		byte[] eui64 = this.toModifiedEui64();
		
		long lowBits = 0;
		for (int i = 0; i < 8; i++) {
			lowBits = (lowBits << 8) | (eui64[i] & 0xFF);
		}
		return new Ipv6Address(0xFE80_0000_0000_0000L, lowBits);
	}

	/**
	 * Generates an IPv6 address by combining a /64 prefix with this MAC's EUI-64.<br>
	 * The prefix must be a /64 network.<br>
	 *
	 * @param prefix The /64 network prefix
	 * @return The full IPv6 address
	 * @throws NullPointerException If prefix is null
	 * @throws IllegalArgumentException If prefix is not /64
	 */
	public @NonNull Ipv6Address toIpv6(@NonNull Ipv6Network prefix) {
		Objects.requireNonNull(prefix, "Prefix must not be null");
		if (prefix.prefixLength() != 64) {
			throw new IllegalArgumentException("Prefix must be /64, got /" + prefix.prefixLength());
		}
		
		byte[] eui64 = this.toModifiedEui64();
		long lowBits = 0;
		for (int i = 0; i < 8; i++) {
			lowBits = (lowBits << 8) | (eui64[i] & 0xFF);
		}
		return new Ipv6Address(prefix.networkAddress().highBits(), lowBits);
	}

	/**
	 * Returns a new address with the U/L bit set (locally administered).<br>
	 * @return A new locally administered MAC address
	 */
	public @NonNull MacAddress toLocallyAdministered() {
		return new MacAddress(this.value | (0x02L << 40));
	}

	/**
	 * Returns a new address with the U/L bit cleared (universally administered).<br>
	 * @return A new universally administered MAC address
	 */
	public @NonNull MacAddress toUniversallyAdministered() {
		return new MacAddress(this.value & ~(0x02L << 40));
	}
	
	@Override
	public int compareTo(@NonNull MacAddress other) {
		return Long.compareUnsigned(this.value, other.value);
	}
	
	/**
	 * Returns the colon-delimited string representation in lowercase (e.g., "00:1a:2b:3c:4d:5e").<br>
	 * @return The MAC address in colon-delimited lowercase format
	 */
	public @NonNull String toColonString() {
		return String.format("%02x:%02x:%02x:%02x:%02x:%02x",
			(this.value >>> 40) & 0xFF,
			(this.value >>> 32) & 0xFF,
			(this.value >>> 24) & 0xFF,
			(this.value >>> 16) & 0xFF,
			(this.value >>> 8) & 0xFF,
			this.value & 0xFF);
	}
	
	/**
	 * Returns the dash-delimited string representation in uppercase (e.g., "00-1A-2B-3C-4D-5E").<br>
	 * @return The MAC address in dash-delimited uppercase format
	 */
	public @NonNull String toDashString() {
		return String.format("%02X-%02X-%02X-%02X-%02X-%02X",
			(this.value >>> 40) & 0xFF,
			(this.value >>> 32) & 0xFF,
			(this.value >>> 24) & 0xFF,
			(this.value >>> 16) & 0xFF,
			(this.value >>> 8) & 0xFF,
			this.value & 0xFF);
	}
	
	/**
	 * Returns the Cisco-style dotted string representation in lowercase (e.g., "001a.2b3c.4d5e").<br>
	 * @return The MAC address in Cisco dot notation
	 */
	public @NonNull String toCiscoString() {
		return String.format("%04x.%04x.%04x",
			(this.value >>> 32) & 0xFFFF,
			(this.value >>> 16) & 0xFFFF,
			this.value & 0xFFFF);
	}
	
	/**
	 * Returns the bare hexadecimal string representation in lowercase (e.g., "001a2b3c4d5e").<br>
	 * @return The MAC address as a bare hexadecimal string
	 */
	public @NonNull String toBareString() {
		return String.format("%012x", this.value);
	}
	
	/**
	 * Returns the canonical string representation (colon-delimited, lowercase).<br>
	 * @return The MAC address in canonical format
	 */
	@Override
	public @NonNull String toString() {
		return this.toColonString();
	}
}
