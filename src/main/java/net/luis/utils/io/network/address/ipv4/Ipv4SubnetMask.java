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

package net.luis.utils.io.network.address.ipv4;

import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an IPv4 subnet mask or wildcard mask.<br>
 * A subnet mask defines the boundary between the network and host portions of an IPv4 address.<br>
 * Valid subnet masks consist of contiguous 1-bits followed by contiguous 0-bits.<br>
 * <p>
 *     Subnet masks can also be represented as wildcard masks (inverse masks), commonly used in access control lists.<br>
 *     A wildcard mask is the bitwise inverse of a subnet mask.<br>
 *     For example, subnet mask 255.255.255.0 corresponds to wildcard mask 0.0.0.255.<br>
 * </p>
 * <p>
 *     This property is informational and depends on how the mask was created.<br>
 *     Use {@link #toWildcard()} or {@link #toSubnetMask()} to convert between representations.
 * </p>
 * <pre>{@code
 * // Create from prefix length
 * Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
 * System.out.println(mask);  // 255.255.255.0
 *
 * // Apply to an address to get network address
 * Ipv4Address ip = IpAddresses.parseIpv4("192.168.1.100");
 * Ipv4Address network = mask.applyTo(ip);  // 192.168.1.0
 *
 * // Work with wildcard masks
 * Ipv4SubnetMask wildcard = mask.toWildcard();
 * System.out.println(wildcard);  // 0.0.0.255
 * }</pre>
 *
 * @param value The 32-bit mask value
 * @param isWildcard True if this mask represents a wildcard (inverse) mask
 *
 * @see Ipv4Address
 *
 * @author Luis-St
 */
public record Ipv4SubnetMask(int value, boolean isWildcard) {
	
	/**
	 * Subnet mask with prefix length 0 (0.0.0.0).<br>
	 */
	public static final Ipv4SubnetMask MASK_0 = fromPrefixLength(0);
	
	/**
	 * Subnet mask with prefix length 8 (255.0.0.0).<br>
	 */
	public static final Ipv4SubnetMask MASK_8 = fromPrefixLength(8);
	
	/**
	 * Subnet mask with prefix length 16 (255.255.0.0).<br>
	 */
	public static final Ipv4SubnetMask MASK_16 = fromPrefixLength(16);
	
	/**
	 * Subnet mask with prefix length 24 (255.255.255.0).<br>
	 */
	public static final Ipv4SubnetMask MASK_24 = fromPrefixLength(24);
	
	/**
	 * Subnet mask with prefix length 32 (255.255.255.255).<br>
	 */
	public static final Ipv4SubnetMask MASK_32 = fromPrefixLength(32);
	
	/**
	 * Constructs a new IPv4 subnet mask with the given value and wildcard flag.<br>
	 *
	 * @param value The 32-bit mask value
	 * @param isWildcard True if this mask represents a wildcard (inverse) mask
	 * @throws IllegalArgumentException If the mask is not contiguous (has holes)
	 */
	public Ipv4SubnetMask {
		int maskToCheck = isWildcard ? ~value : value;
		if (!isContiguousMask(maskToCheck)) {
			throw new IllegalArgumentException("Subnet mask must be contiguous (no holes): " + toDottedDecimalInternal(value));
		}
	}
	
	/**
	 * Creates a subnet mask from the given prefix length.<br>
	 *
	 * @param prefixLength The prefix length (0-32)
	 * @return A new subnet mask with the specified prefix length
	 * @throws IllegalArgumentException If the prefix length is not in the range 0-32
	 */
	public static @NonNull Ipv4SubnetMask fromPrefixLength(int prefixLength) {
		if (prefixLength < 0 || prefixLength > 32) {
			throw new IllegalArgumentException("Prefix length must be between 0 and 32, got: " + prefixLength);
		}
		
		int mask = prefixLength == 0 ? 0 : (0xFFFFFFFF << (32 - prefixLength));
		return new Ipv4SubnetMask(mask, false);
	}
	
	/**
	 * Parses a subnet mask from dotted decimal notation (e.g., "255.255.255.0").<br>
	 *
	 * @param mask The mask string in dotted decimal notation
	 * @return A new subnet mask parsed from the string
	 * @throws NullPointerException If mask is null
	 * @throws IllegalArgumentException If the mask string is invalid or the mask is not contiguous
	 */
	public static @NonNull Ipv4SubnetMask parse(@NonNull String mask) {
		Objects.requireNonNull(mask, "Mask must not be null");
		return tryParse(mask).orElseThrow(() -> new IllegalArgumentException("Invalid subnet mask: " + mask));
	}
	
	/**
	 * Attempts to parse a subnet mask from dotted decimal notation (e.g., "255.255.255.0").<br>
	 *
	 * @param mask The mask string in dotted decimal notation
	 * @return An optional containing the parsed subnet mask, or empty if parsing fails
	 * @throws NullPointerException If mask is null
	 */
	public static @NonNull Optional<Ipv4SubnetMask> tryParse(@NonNull String mask) {
		Objects.requireNonNull(mask, "Mask must not be null");
		
		try {
			int value = parseOctets(mask);
			if (!isContiguousMask(value)) {
				return Optional.empty();
			}
			
			return Optional.of(new Ipv4SubnetMask(value, false));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Creates a subnet mask from a wildcard mask string (e.g., "0.0.0.255").<br>
	 *
	 * @param wildcard The wildcard mask string in dotted decimal notation
	 * @return A new subnet mask created from the wildcard
	 * @throws NullPointerException If wildcard is null
	 * @throws IllegalArgumentException If the wildcard string is invalid or the resulting mask is not contiguous
	 */
	public static @NonNull Ipv4SubnetMask fromWildcard(@NonNull String wildcard) {
		Objects.requireNonNull(wildcard, "Wildcard must not be null");
		return new Ipv4SubnetMask(parseOctets(wildcard), true);
	}
	
	/**
	 * Creates a subnet mask from a wildcard mask value.<br>
	 *
	 * @param value The 32-bit wildcard mask value
	 * @return A new subnet mask created from the wildcard value
	 * @throws IllegalArgumentException If the resulting mask is not contiguous
	 */
	public static @NonNull Ipv4SubnetMask fromWildcard(int value) {
		return new Ipv4SubnetMask(value, true);
	}
	
	//region Static helper methods
	
	/**
	 * Parses octets from a dotted decimal string.<br>
	 *
	 * @param dottedDecimal The string in dotted decimal notation
	 * @return The 32-bit integer value
	 * @throws NullPointerException If the string is null
	 * @throws IllegalArgumentException If the string is not a valid dotted decimal format
	 */
	private static int parseOctets(@NonNull String dottedDecimal) {
		Objects.requireNonNull(dottedDecimal, "Dotted decimal string must not be null");
		String[] parts = dottedDecimal.split("\\.");
		if (parts.length != 4) {
			throw new IllegalArgumentException("Invalid dotted decimal format: " + dottedDecimal);
		}
		
		int result = 0;
		for (int i = 0; i < 4; i++) {
			int octet;
			
			try {
				octet = Integer.parseInt(parts[i]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid octet value: " + parts[i]);
			}
			
			if (octet < 0 || octet > 255) {
				throw new IllegalArgumentException("Octet value out of range (0-255): " + octet);
			}
			result = (result << 8) | octet;
		}
		return result;
	}
	
	/**
	 * Checks if a mask value represents a contiguous subnet mask.<br>
	 * A contiguous subnet mask has all 1-bits followed by all 0-bits.<br>
	 * <p>
	 *     A mask is contiguous if {@code (inverted_mask + 1) & inverted_mask == 0}.<br>
	 * </p>
	 *
	 * @param mask The mask value to check
	 * @return True if the mask is contiguous, false otherwise
	 */
	private static boolean isContiguousMask(int mask) {
		if (mask == 0) {
			return true;
		}
		
		int inverted = ~mask;
		int check = inverted + 1;
		return (check & inverted) == 0;
	}
	
	/**
	 * Converts a mask value to dotted decimal string.<br>
	 *
	 * @param value The 32-bit mask value
	 * @return The dotted decimal string representation
	 */
	private static @NonNull String toDottedDecimalInternal(int value) {
		return ((value >>> 24) & 0xFF) + "." + ((value >>> 16) & 0xFF) + "." + ((value >>> 8) & 0xFF) + "." + (value & 0xFF);
	}
	//endregion
	
	/**
	 * Converts this mask to its equivalent prefix length (CIDR notation).<br>
	 * For example, 255.255.255.0 returns 24.<br>
	 *
	 * @return The prefix length (0-32)
	 */
	public int toPrefixLength() {
		int maskValue = this.isWildcard ? ~this.value : this.value;
		return Integer.bitCount(maskValue);
	}
	
	/**
	 * Converts this subnet mask to its equivalent wildcard mask.<br>
	 * <p>
	 *     The wildcard mask is the bitwise inverse of the subnet mask.<br>
	 *     If this mask is already a wildcard mask, it is returned as-is.
	 * </p>
	 *
	 * @return A wildcard mask equivalent to this subnet mask
	 */
	public @NonNull Ipv4SubnetMask toWildcard() {
		if (this.isWildcard) {
			return this;
		}
		return new Ipv4SubnetMask(~this.value, true);
	}
	
	/**
	 * Converts this wildcard mask to its equivalent subnet mask.<br>
	 * If this mask is already a subnet mask (not wildcard), it is returned as-is.<br>
	 *
	 * @return A subnet mask equivalent to this wildcard mask
	 */
	public @NonNull Ipv4SubnetMask toSubnetMask() {
		if (!this.isWildcard) {
			return this;
		}
		return new Ipv4SubnetMask(~this.value, false);
	}
	
	/**
	 * Returns the number of host addresses available with this subnet mask.<br>
	 * <p>
	 *     For typical subnets, this is 2^(32-prefix) - 2 (excluding network and broadcast addresses).<br>
	 *     For /31 and /32 subnets, this returns 2 and 1 respectively, as per RFC 3021.
	 * </p>
	 *
	 * @return The number of usable host addresses
	 */
	public long hostCount() {
		int prefixLength = this.toPrefixLength();
		int hostBits = 32 - prefixLength;
		
		return switch (hostBits) {
			case 0 -> 1L;
			case 1 -> 2L;
			default -> (1L << hostBits) - 2;
		};
	}
	
	/**
	 * Applies this mask to an address to get the network address.<br>
	 * This performs a bitwise AND operation between the address and the mask.<br>
	 *
	 * @param address The IP address to apply the mask to
	 * @return The network address resulting from applying the mask
	 * @throws NullPointerException If address is null
	 */
	public @NonNull Ipv4Address applyTo(@NonNull Ipv4Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		
		int maskValue = this.isWildcard ? ~this.value : this.value;
		int networkValue = address.value() & maskValue;
		return Ipv4Address.fromValue(networkValue);
	}
	
	/**
	 * Extracts the host portion of an address using this mask.<br>
	 * This performs a bitwise AND operation between the address and the inverted mask.<br>
	 *
	 * @param address The IP address to extract the host portion from
	 * @return The host portion of the address
	 * @throws NullPointerException If address is null
	 */
	public @NonNull Ipv4Address getHostPart(@NonNull Ipv4Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		
		int maskValue = this.isWildcard ? ~this.value : this.value;
		int hostValue = address.value() & ~maskValue;
		return Ipv4Address.fromValue(hostValue);
	}
	
	/**
	 * Returns the byte representation of this mask in network byte order (big-endian).<br>
	 * @return A new 4-byte array containing the mask value
	 */
	public byte @NonNull [] toBytes() {
		return new byte[] {
			(byte) ((this.value >>> 24) & 0xFF),
			(byte) ((this.value >>> 16) & 0xFF),
			(byte) ((this.value >>> 8) & 0xFF),
			(byte) (this.value & 0xFF)
		};
	}
	
	/**
	 * Returns the dotted decimal representation of this mask (e.g., "255.255.255.0").<br>
	 * @return The dotted decimal string representation
	 */
	public @NonNull String toDottedDecimal() {
		return toDottedDecimalInternal(this.value);
	}
	
	/**
	 * Returns the octet value at the specified index.<br>
	 *
	 * @param index The octet index (0-3, where 0 is the most significant octet)
	 * @return The octet value (0-255)
	 * @throws IndexOutOfBoundsException If the index is not in the range 0-3
	 */
	public int getOctet(int index) {
		if (index < 0 || index > 3) {
			throw new IndexOutOfBoundsException("Octet index must be between 0 and 3, got: " + index);
		}
		return (this.value >>> (24 - (index * 8))) & 0xFF;
	}
	
	@Override
	public @NonNull String toString() {
		return this.toDottedDecimal();
	}
}
