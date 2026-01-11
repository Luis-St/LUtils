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

import net.luis.utils.io.network.address.AddressType;
import net.luis.utils.io.network.address.IpAddress;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigInteger;
import java.net.*;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Represents an IPv4 (Internet Protocol version 4) address.<br>
 * An IPv4 address is a 32-bit identifier, typically displayed as four decimal numbers separated by dots (dotted-decimal notation), such as "192.168.1.1".<br>
 * <p>
 *     This is an immutable value type implemented as a Java record.<br>
 *     The address is stored internally as a 32-bit signed integer, but represents an unsigned 32-bit value.<br>
 *     Use {@link #toUnsignedLong()} for arithmetic operations.
 * </p>
 * <pre>{@code
 * // Create from dotted-decimal notation
 * Ipv4Address address = IpAddresses.parseIpv4("192.168.1.1");
 *
 * // Access octets
 * int firstOctet = address.getOctet(0);  // 192
 *
 * // Check address type
 * if (address.isPrivate()) {
 *     System.out.println("Private address in RFC 1918 space");
 * }
 *
 * // Iterate to next address
 * Optional<Ipv4Address> next = address.next();  // 192.168.1.2
 * }</pre>
 *
 * @see IpAddress
 * @see Ipv4Network
 * @see Ipv4SubnetMask
 *
 * @author Luis-St
 *
 * @param value The 32-bit address value stored as a signed int (unsigned semantics)
 */
public record Ipv4Address(int value) implements IpAddress<Ipv4Address> {

	/**
	 * The bit length of an IPv4 address.<br>
	 */
	public static final int BIT_LENGTH = 32;

	/**
	 * The number of octets in an IPv4 address.<br>
	 */
	public static final int OCTET_COUNT = 4;

	/**
	 * The unspecified IPv4 address (0.0.0.0).<br>
	 */
	public static final Ipv4Address UNSPECIFIED = new Ipv4Address(0);

	/**
	 * The loopback IPv4 address (127.0.0.1).<br>
	 */
	public static final Ipv4Address LOOPBACK = new Ipv4Address(0x7F000001);

	/**
	 * The broadcast IPv4 address (255.255.255.255).<br>
	 */
	public static final Ipv4Address BROADCAST = new Ipv4Address(0xFFFFFFFF);
	
	/**
	 * Constructs an ipv4 address.<br>
	 * @param value The 32-bit address value stored as a signed int (unsigned semantics)
	 */
	public Ipv4Address {}
	
	/**
	 * Creates an ipv4 address from an ipv4 address.<br>
	 *
	 * @param address The ipv4 address to convert
	 * @return An ipv4 address representing the same address
	 * @throws NullPointerException If address is null
	 */
	public static @NonNull Ipv4Address from(@NonNull Inet4Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		
		byte[] bytes = address.getAddress();
		int value = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
		return new Ipv4Address(value);
	}
	
	/**
	 * Creates an ipv4 address from a 32-bit integer value.<br>
	 *
	 * @param value The 32-bit integer value representing the address
	 * @return An ipv4 address with the specified value
	 */
	public static @NonNull Ipv4Address fromValue(int value) {
		return new Ipv4Address(value);
	}
	
	/**
	 * Creates an ipv4 address from an unsigned long value.<br>
	 *
	 * @param value The unsigned long value representing the address (0 to 4294967295)
	 * @return An ipv4 address with the specified value
	 * @throws IllegalArgumentException If the value is not between 0 and 4294967295
	 */
	public static @NonNull Ipv4Address fromUnsignedLong(long value) {
		if (value < 0 || value > 0xFFFFFFFFL) {
			throw new IllegalArgumentException("Value must be between 0 and 4294967295: " + value);
		}
		
		return new Ipv4Address((int) value);
	}
	
	/**
	 * Creates an ipv4 address from a byte array.<br>
	 *
	 * @param bytes The byte array representing the address
	 * @return An ipv4 address with the specified bytes
	 * @throws NullPointerException If the byte array is null
	 * @throws IllegalArgumentException If the byte array is not exactly 4 bytes long
	 */
	public static @NonNull Ipv4Address fromBytes(byte @NonNull [] bytes) {
		Objects.requireNonNull(bytes, "Byte array must not be null");
		if (bytes.length != 4) {
			throw new IllegalArgumentException("Byte array must be exactly 4 bytes long");
		}
		
		int value = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
		return new Ipv4Address(value);
	}
	
	/**
	 * Creates an ipv4 address from four octets.<br>
	 * Each octet must be between 0 and 255.<br>
	 *
	 * @param octet1 The first octet (most significant)
	 * @param octet2 The second octet
	 * @param octet3 The third octet
	 * @param octet4 The fourth octet (least significant)
	 * @return An ipv4 address with the specified octets
	 * @throws IllegalArgumentException If any octet is not between 0 and 255
	 */
	public static @NonNull Ipv4Address fromOctets(int octet1, int octet2, int octet3, int octet4) {
		if (octet1 < 0 || octet1 > 255) {
			throw new IllegalArgumentException("First octet must be between 0 and 255: " + octet1);
		}
		if (octet2 < 0 || octet2 > 255) {
			throw new IllegalArgumentException("Second octet must be between 0 and 255: " + octet2);
		}
		if (octet3 < 0 || octet3 > 255) {
			throw new IllegalArgumentException("Third octet must be between 0 and 255: " + octet3);
		}
		if (octet4 < 0 || octet4 > 255) {
			throw new IllegalArgumentException("Fourth octet must be between 0 and 255: " + octet4);
		}
		
		int value = (octet1 << 24) | (octet2 << 16) | (octet3 << 8) | octet4;
		return new Ipv4Address(value);
	}
	
	@Override
	public int version() {
		return 4;
	}
	
	@Override
	public int bitLength() {
		return BIT_LENGTH;
	}
	
	@Override
	public byte @NonNull [] toBytes() {
		return new byte[] {
			(byte) ((this.value >>> 24) & 0xFF),
			(byte) ((this.value >>> 16) & 0xFF),
			(byte) ((this.value >>> 8) & 0xFF),
			(byte) (this.value & 0xFF)
		};
	}
	@Override
	public @NonNull BigInteger toBigInteger() {
		return BigInteger.valueOf(this.toUnsignedLong());
	}
	
	@Override
	public boolean getBit(int index) {
		if (index < 0 || index >= BIT_LENGTH) {
			throw new IndexOutOfBoundsException("Bit index out of range: " + index);
		}
		
		int bitPosition = BIT_LENGTH - 1 - index;
		return ((this.value >>> bitPosition) & 1) == 1;
	}
	
	@Override
	public @NonNull Ipv4Address withBit(int index, boolean bitValue) {
		if (index < 0 || index >= BIT_LENGTH) {
			throw new IndexOutOfBoundsException("Bit index out of range: " + index);
		}
		
		int bitPosition = BIT_LENGTH - 1 - index;
		int newValue;
		if (bitValue) {
			newValue = this.value | (1 << bitPosition);
		} else {
			newValue = this.value & ~(1 << bitPosition);
		}
		return new Ipv4Address(newValue);
	}
	
	@Override
	public @NonNull Optional<Ipv4Address> next() {
		if (this.value == 0xFFFFFFFF) {
			return Optional.empty();
		}
		return Optional.of(new Ipv4Address(this.value + 1));
	}
	
	@Override
	public @NonNull Optional<Ipv4Address> previous() {
		if (this.value == 0) {
			return Optional.empty();
		}
		return Optional.of(new Ipv4Address(this.value - 1));
	}
	
	@Override
	public @NonNull BigInteger distanceTo(@NonNull Ipv4Address other) {
		Objects.requireNonNull(other, "Other address must not be null");
		return other.toBigInteger().subtract(this.toBigInteger());
	}
	
	@Override
	public boolean isUnspecified() {
		return this.value == 0;
	}
	
	@Override
	public boolean isLoopback() {
		return this.getOctet(0) == 127;
	}
	
	@Override
	public boolean isMulticast() {
		int firstOctet = this.getOctet(0);
		return firstOctet >= 224 && firstOctet <= 239;
	}
	
	@Override
	public boolean isLinkLocal() {
		return this.getOctet(0) == 169 && this.getOctet(1) == 254;
	}
	
	@Override
	public boolean isPrivate() {
		int firstOctet = this.getOctet(0);
		int secondOctet = this.getOctet(1);

		// 10.0.0.0/8
		if (firstOctet == 10) {
			return true;
		}

		// 172.16.0.0/12 (172.16.x.x - 172.31.x.x)
		if (firstOctet == 172 && secondOctet >= 16 && secondOctet <= 31) {
			return true;
		}

		// 192.168.0.0/16
		return firstOctet == 192 && secondOctet == 168;
	}
	
	@Override
	public boolean isDocumentation() {
		int firstOctet = this.getOctet(0);
		int secondOctet = this.getOctet(1);
		int thirdOctet = this.getOctet(2);

		// 192.0.2.0/24 (TEST-NET-1)
		if (firstOctet == 192 && secondOctet == 0 && thirdOctet == 2) {
			return true;
		}

		// 198.51.100.0/24 (TEST-NET-2)
		if (firstOctet == 198 && secondOctet == 51 && thirdOctet == 100) {
			return true;
		}

		// 203.0.113.0/24 (TEST-NET-3)
		return firstOctet == 203 && secondOctet == 0 && thirdOctet == 113;
	}

	/**
	 * Checks if this is the broadcast address (255.255.255.255).<br>
	 * @return {@code true} if this is the broadcast address, {@code false} otherwise
	 */
	public boolean isBroadcast() {
		return this.value == 0xFFFFFFFF;
	}

	/**
	 * Checks if this is a shared address space address (100.64.0.0/10).<br>
	 * @return {@code true} if this is a shared address space address, {@code false} otherwise
	 */
	public boolean isSharedAddressSpace() {
		int firstOctet = this.getOctet(0);
		int secondOctet = this.getOctet(1);
		
		// 100.64.0.0/10 means 100.64.x.x - 100.127.x.x
		return firstOctet == 100 && secondOctet >= 64 && secondOctet <= 127;
	}
	
	@Override
	public boolean isGlobalUnicast() {
		return !this.isUnspecified() && !this.isLoopback() && !this.isPrivate() && !this.isLinkLocal() && !this.isMulticast() && !this.isDocumentation() && !this.isBroadcast() && !this.isSharedAddressSpace() && !this.isReserved();
	}

	/**
	 * Checks if this is a reserved address.<br>
	 * This includes addresses in reserved ranges that are not classified elsewhere.<br>
	 *
	 * @return {@code true} if this is a reserved address, {@code false} otherwise
	 */
	public boolean isReserved() {
		int firstOctet = this.getOctet(0);

		// 0.0.0.0/8 (except 0.0.0.0 which is unspecified)
		if (firstOctet == 0 && this.value != 0) {
			return true;
		}

		// 240.0.0.0/4 (reserved for future use, except broadcast)
		return firstOctet >= 240 && firstOctet <= 255 && this.value != 0xFFFFFFFF;
	}
	
	@Override
	public @NonNull AddressType getAddressType() {
		if (this.isUnspecified()) {
			return AddressType.UNSPECIFIED;
		}
		if (this.isLoopback()) {
			return AddressType.LOOPBACK;
		}
		if (this.isPrivate()) {
			return AddressType.PRIVATE;
		}
		if (this.isLinkLocal()) {
			return AddressType.LINK_LOCAL;
		}
		if (this.isMulticast()) {
			return AddressType.MULTICAST;
		}
		if (this.isBroadcast()) {
			return AddressType.BROADCAST;
		}
		if (this.isDocumentation()) {
			return AddressType.DOCUMENTATION;
		}
		if (this.isSharedAddressSpace()) {
			return AddressType.SHARED_ADDRESS_SPACE;
		}
		if (this.isReserved()) {
			return AddressType.RESERVED;
		}
		return AddressType.GLOBAL_UNICAST;
	}
	
	@Override
	public @NonNull String toReverseDnsName() {
		return this.getOctet(3) + "." + this.getOctet(2) + "." + this.getOctet(1) + "." + this.getOctet(0) + ".in-addr.arpa";
	}
	
	@Override
	public @NonNull InetAddress toInetAddress() {
		return this.toInet4Address();
	}

	/**
	 * Converts this IP address to a {@link Inet4Address}.<br>
	 * @return An ipv4 address representing this IP address
	 */
	public @NonNull Inet4Address toInet4Address() {
		try {
			return (Inet4Address) InetAddress.getByAddress(this.toBytes());
		} catch (UnknownHostException e) {
			throw new AssertionError("Failed to create Inet4Address from valid bytes", e);
		}
	}
	
	@Override
	public @NonNull InetSocketAddress toSocketAddress(int port) {
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("Port must be between 0 and 65535: " + port);
		}
		return new InetSocketAddress(this.toInetAddress(), port);
	}
	
	@Override
	public @NonNull Ipv4Address minValue() {
		return UNSPECIFIED;
	}
	
	@Override
	public @NonNull Ipv4Address maxValue() {
		return BROADCAST;
	}
	
	@Override
	public int compareTo(@NonNull Ipv4Address other) {
		return Long.compare(this.toUnsignedLong(), other.toUnsignedLong());
	}

	/**
	 * Returns the value of the octet at the specified index.<br>
	 * Index 0 is the most significant octet.<br>
	 *
	 * @param index The octet index (0-3)
	 * @return The octet value (0-255)
	 * @throws IndexOutOfBoundsException If the index is not between 0 and 3
	 */
	public int getOctet(int index) {
		if (index < 0 || index >= OCTET_COUNT) {
			throw new IndexOutOfBoundsException("Octet index out of range: " + index);
		}
		
		int shift = (3 - index) * 8;
		return (this.value >>> shift) & 0xFF;
	}

	/**
	 * Returns a new address with the octet at the specified index changed to the given value.<br>
	 *
	 * @param index The octet index (0-3)
	 * @param octetValue The new value for the octet (0-255)
	 * @return A new address with the octet at the specified index changed
	 * @throws IndexOutOfBoundsException If the index is not between 0 and 3
	 * @throws IllegalArgumentException If the octet value is not between 0 and 255
	 */
	public @NonNull Ipv4Address withOctet(int index, int octetValue) {
		if (index < 0 || index >= OCTET_COUNT) {
			throw new IndexOutOfBoundsException("Octet index out of range: " + index);
		}
		if (octetValue < 0 || octetValue > 255) {
			throw new IllegalArgumentException("Octet value out of range: " + octetValue);
		}
		
		int shift = (3 - index) * 8;
		int mask = ~(0xFF << shift);
		int newValue = (this.value & mask) | (octetValue << shift);
		return new Ipv4Address(newValue);
	}

	/**
	 * Returns the unsigned long representation of this IPv4 address.<br>
	 * This method treats the 32-bit signed integer as an unsigned value.<br>
	 *
	 * @return The unsigned long value (0 to 4294967295)
	 */
	public long toUnsignedLong() {
		return Integer.toUnsignedLong(this.value);
	}

	/**
	 * Returns a string representation of this IPv4 address in dotted-decimal notation.
	 *
	 * @return The address in dotted-decimal notation (e.g., "192.168.1.1")
	 */
	@Override
	public @NonNull String toString() {
		return this.getOctet(0) + "." + this.getOctet(1) + "." + this.getOctet(2) + "." + this.getOctet(3);
	}
}
