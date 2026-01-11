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

import net.luis.utils.io.network.address.AddressType;
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.mac.MacAddress;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigInteger;
import java.net.*;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents an IPv6 (Internet Protocol version 6) address.<br>
 * An IPv6 address is a 128-bit identifier, typically displayed as eight groups of four hexadecimal digits separated by colons, such as "2001:db8::1".<br>
 * <p>
 *     The address is stored as two 64-bit long values: {@code highBits} for the network prefix portion and {@code lowBits} for the interface identifier.<br>
 *     An optional {@code zoneId} supports link-local address scoping.
 * </p>
 * <pre>{@code
 * // Parse various formats
 * Ipv6Address address = IpAddresses.parseIpv6("2001:db8::1");
 * Ipv6Address linkLocal = IpAddresses.parseIpv6("fe80::1%eth0");
 *
 * // Check address properties
 * if (address.isGlobalUnicast()) {
 *     System.out.println("Globally routable");
 * }
 *
 * // Work with zone IDs
 * Optional<String> zone = linkLocal.zoneId();
 * Ipv6Address withoutZone = linkLocal.withoutZoneId();
 *
 * // Extract embedded IPv4
 * if (address.isIpv4Mapped()) {
 *     Optional<Ipv4Address> ipv4 = address.toIpv4();
 * }
 * }</pre>
 *
 *
 * @see IpAddress
 * @see Ipv4Address
 * @see TeredoInfo
 *
 * @author Luis-St
 *
 * @param highBits The upper 64 bits of the address (network prefix)
 * @param lowBits The lower 64 bits of the address (interface identifier)
 * @param zoneId Optional zone identifier for link-local addresses
 */
public record Ipv6Address(long highBits, long lowBits, @Nullable String zoneId) implements IpAddress<Ipv6Address> {
	
	/**
	 * The number of bits in an IPv6 address.<br>
	 */
	public static final int BIT_LENGTH = 128;
	/**
	 * The number of hextets (16-bit groups) in an IPv6 address.<br>
	 */
	public static final int HEXTET_COUNT = 8;
	/**
	 * The unspecified address (::), representing the absence of an address.<br>
	 */
	public static final Ipv6Address UNSPECIFIED = new Ipv6Address(0L, 0L, null);
	/**
	 * The loopback address (::1), used for localhost communication.<br>
	 */
	public static final Ipv6Address LOOPBACK = new Ipv6Address(0L, 1L, null);
	/**
	 * The maximum IPv6 address (ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff).<br>
	 * All 128 bits are set to 1.<br>
	 */
	public static final Ipv6Address MAX = new Ipv6Address(-1L, -1L, null);

	/**
	 * Constructs a new IPv6 address without a zone identifier.
	 *
	 * @param highBits The upper 64 bits of the address (network prefix)
	 * @param lowBits The lower 64 bits of the address (interface identifier)
	 */
	public Ipv6Address(long highBits, long lowBits) {
		this(highBits, lowBits, null);
	}
	
	/**
	 * Creates an ipv6 address from an internet v6 address.<br>
	 *
	 * @param address The internet v6 address to convert
	 * @return The corresponding ipv6 address
	 * @throws NullPointerException If address is null
	 */
	public static @NonNull Ipv6Address from(@NonNull Inet6Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		
		Ipv6Address ipv6Address = fromBytes(address.getAddress());
		NetworkInterface scopedInterface = address.getScopedInterface();
		if (scopedInterface != null) {
			return ipv6Address.withZoneId(scopedInterface.getName());
		} else if (address.getScopeId() != 0) {
			return ipv6Address.withZoneId(String.valueOf(address.getScopeId()));
		}
		return ipv6Address;
	}
	
	/**
	 * Creates an ipv6 address from the given high and low bits.<br>
	 *
	 * @param highBits The high 64 bits of the address
	 * @param lowBits The low 64 bits of the address
	 * @return The corresponding ipv6 address
	 */
	public static @NonNull Ipv6Address fromBits(long highBits, long lowBits) {
		return new Ipv6Address(highBits, lowBits);
	}
	
	/**
	 * Creates an ipv6 address from a byte array.<br>
	 * The byte array must be exactly 16 bytes long.<br>
	 *
	 * @param bytes The byte array representing the ipv6 address
	 * @return The corresponding ipv6 address
	 * @throws NullPointerException If the byte array is null
	 * @throws IllegalArgumentException If the byte array is not 16 bytes long
	 */
	public static @NonNull Ipv6Address fromBytes(byte @NonNull [] bytes) {
		Objects.requireNonNull(bytes, "Byte array must not be null");
		if (bytes.length != 16) {
			throw new IllegalArgumentException("Byte array must be exactly 16 bytes long");
		}
		
		long highBits = 0;
		for (int i = 0; i < 8; i++) {
			highBits = (highBits << 8) | (bytes[i] & 0xFF);
		}
		
		long lowBits = 0;
		for (int i = 8; i < 16; i++) {
			lowBits = (lowBits << 8) | (bytes[i] & 0xFF);
		}
		return new Ipv6Address(highBits, lowBits);
	}
	
	@Override
	public int version() {
		return 6;
	}
	
	@Override
	public int bitLength() {
		return BIT_LENGTH;
	}
	
	@Override
	public byte @NonNull [] toBytes() {
		byte[] bytes = new byte[16];
		for (int i = 0; i < 8; i++) {
			bytes[i] = (byte) (this.highBits >>> (56 - i * 8));
		}
		for (int i = 0; i < 8; i++) {
			bytes[8 + i] = (byte) (this.lowBits >>> (56 - i * 8));
		}
		return bytes;
	}
	
	@Override
	public @NonNull BigInteger toBigInteger() {
		byte[] bytes = new byte[17];
		bytes[0] = 0;
		
		byte[] addressBytes = this.toBytes();
		System.arraycopy(addressBytes, 0, bytes, 1, 16);
		return new BigInteger(bytes);
	}
	
	@Override
	public boolean getBit(int index) {
		if (index < 0 || index >= BIT_LENGTH) {
			throw new IndexOutOfBoundsException("Bit index out of range: " + index);
		}
		
		if (index < 64) {
			return ((this.highBits >>> (63 - index)) & 1L) == 1L;
		} else {
			return ((this.lowBits >>> (127 - index)) & 1L) == 1L;
		}
	}
	
	@Override
	public @NonNull Ipv6Address withBit(int index, boolean value) {
		if (index < 0 || index >= BIT_LENGTH) {
			throw new IndexOutOfBoundsException("Bit index out of range: " + index);
		}
		long newHighBits = this.highBits;
		long newLowBits = this.lowBits;
		
		if (index < 64) {
			long mask = 1L << (63 - index);
			if (value) {
				newHighBits |= mask;
			} else {
				newHighBits &= ~mask;
			}
		} else {
			long mask = 1L << (127 - index);
			if (value) {
				newLowBits |= mask;
			} else {
				newLowBits &= ~mask;
			}
		}
		return new Ipv6Address(newHighBits, newLowBits, this.zoneId);
	}
	
	@Override
	public @NonNull Optional<Ipv6Address> next() {
		if (this.highBits == -1L && this.lowBits == -1L) {
			return Optional.empty();
		}
		
		long newLowBits = this.lowBits + 1;
		long newHighBits = this.highBits;
		if (newLowBits == 0) {
			newHighBits++;
		}
		return Optional.of(new Ipv6Address(newHighBits, newLowBits, this.zoneId));
	}
	
	@Override
	public @NonNull Optional<Ipv6Address> previous() {
		if (this.highBits == 0L && this.lowBits == 0L) {
			return Optional.empty();
		}
		
		long newLowBits = this.lowBits - 1;
		long newHighBits = this.highBits;
		if (this.lowBits == 0) {
			newHighBits--;
		}
		return Optional.of(new Ipv6Address(newHighBits, newLowBits, this.zoneId));
	}
	
	@Override
	public @NonNull BigInteger distanceTo(@NonNull Ipv6Address other) {
		Objects.requireNonNull(other, "Other address must not be null");
		return other.toBigInteger().subtract(this.toBigInteger());
	}
	
	@Override
	public boolean isUnspecified() {
		return this.highBits == 0L && this.lowBits == 0L;
	}
	
	@Override
	public boolean isLoopback() {
		return this.highBits == 0L && this.lowBits == 1L;
	}
	
	@Override
	public boolean isMulticast() {
		// ff00::/8 - first 8 bits are 11111111
		return (this.highBits >>> 56) == 0xFFL;
	}
	
	@Override
	public boolean isLinkLocal() {
		// fe80::/10 - first 10 bits are 1111111010
		return (this.highBits >>> 54) == 0x3FAL; // 0b1111111010
	}
	
	@Override
	public boolean isPrivate() {
		// fc00::/7 - Unique Local Addresses, first 7 bits are 1111110
		return (this.highBits >>> 57) == 0x7EL; // 0b1111110
	}
	
	/**
	 * Checks if this address is a Unique Local Address (ULA).<br>
	 * This is an alias for {@link #isPrivate()} for IPv6.
	 *
	 * @return {@code true} if this is a ULA address, {@code false} otherwise
	 */
	public boolean isUniqueLocal() {
		return this.isPrivate();
	}
	
	@Override
	public boolean isDocumentation() {
		// 2001:db8::/32
		return (this.highBits >>> 32) == 0x20010DB8L;
	}
	
	@Override
	public boolean isGlobalUnicast() {
		// 2000::/3 (first 3 bits are 001) minus reserved ranges
		if ((this.highBits >>> 61) != 0x1L) { // 0b001
			return false;
		}
		// Exclude documentation range
		if (this.isDocumentation()) {
			return false;
		}
		// Exclude Teredo (2001:0000::/32)
		return (this.highBits >>> 32) != 0x20010000L;
	}
	
	@Override
	public @NonNull AddressType getAddressType() {
		if (this.isUnspecified()) {
			return AddressType.UNSPECIFIED;
		}
		if (this.isLoopback()) {
			return AddressType.LOOPBACK;
		}
		if (this.isMulticast()) {
			return AddressType.MULTICAST;
		}
		if (this.isLinkLocal()) {
			return AddressType.LINK_LOCAL;
		}
		if (this.isPrivate()) {
			return AddressType.PRIVATE;
		}
		if (this.isDocumentation()) {
			return AddressType.DOCUMENTATION;
		}
		if (this.isGlobalUnicast()) {
			return AddressType.GLOBAL_UNICAST;
		}
		return AddressType.RESERVED;
	}
	
	@Override
	public @NonNull String toReverseDnsName() {
		StringBuilder sb = new StringBuilder();
		byte[] bytes = this.toBytes();
		
		for (int i = bytes.length - 1; i >= 0; i--) {
			int b = bytes[i] & 0xFF;
			sb.append(Integer.toHexString(b & 0x0F)).append('.');
			sb.append(Integer.toHexString((b >> 4) & 0x0F)).append('.');
		}
		
		sb.append("ip6.arpa");
		return sb.toString();
	}
	
	@Override
	public @NonNull InetAddress toInetAddress() {
		return this.toInet6Address();
	}
	
	@Override
	public @NonNull InetSocketAddress toSocketAddress(int port) {
		if (port < 0 || port > 65535) {
			throw new IllegalArgumentException("Port out of range: " + port);
		}
		return new InetSocketAddress(this.toInetAddress(), port);
	}
	
	@Override
	public @NonNull Ipv6Address minValue() {
		return UNSPECIFIED;
	}
	
	@Override
	public @NonNull Ipv6Address maxValue() {
		return new Ipv6Address(-1L, -1L, null);
	}
	
	@Override
	public int compareTo(@NonNull Ipv6Address other) {
		int highCompare = Long.compareUnsigned(this.highBits, other.highBits);
		if (highCompare != 0) {
			return highCompare;
		}
		return Long.compareUnsigned(this.lowBits, other.lowBits);
	}
	
	/**
	 * Returns the zone identifier as an {@link Optional}.<br>
	 * The zone ID is used to distinguish link-local addresses on different interfaces.<br>
	 *
	 * @return An {@link Optional} containing the zone ID, or empty if not present
	 */
	public @NonNull Optional<String> getZoneId() {
		return Optional.ofNullable(this.zoneId);
	}
	
	/**
	 * Returns a new address with the specified zone identifier.<br>
	 *
	 * @param zoneId The zone identifier to set
	 * @return A new address with the specified zone ID
	 * @throws NullPointerException If zoneId is null
	 */
	public @NonNull Ipv6Address withZoneId(@NonNull String zoneId) {
		Objects.requireNonNull(zoneId, "Zone ID must not be null");
		return new Ipv6Address(this.highBits, this.lowBits, zoneId);
	}
	
	/**
	 * Returns a new address without any zone identifier.<br>
	 * @return A new address without a zone ID
	 */
	public @NonNull Ipv6Address withoutZoneId() {
		if (this.zoneId == null) {
			return this;
		}
		return new Ipv6Address(this.highBits, this.lowBits, null);
	}
	
	/**
	 * Returns the hextet (16-bit group) at the specified index.<br>
	 * Index 0 is the most significant hextet (leftmost in standard notation).<br>
	 *
	 * @param index The hextet index (0-7)
	 * @return The hextet value (0-65535)
	 * @throws IndexOutOfBoundsException If the index is out of range
	 */
	public int getHextet(int index) {
		if (index < 0 || index >= HEXTET_COUNT) {
			throw new IndexOutOfBoundsException("Hextet index out of range: " + index);
		}
		if (index < 4) {
			return (int) ((this.highBits >>> (48 - index * 16)) & 0xFFFFL);
		} else {
			return (int) ((this.lowBits >>> (48 - (index - 4) * 16)) & 0xFFFFL);
		}
	}
	
	/**
	 * Returns a new address with the specified hextet changed.<br>
	 *
	 * @param index The hextet index (0-7)
	 * @param value The new hextet value (0-65535)
	 * @return A new address with the modified hextet
	 * @throws IndexOutOfBoundsException If the index is out of range
	 * @throws IllegalArgumentException If the value is out of range
	 */
	public @NonNull Ipv6Address withHextet(int index, int value) {
		if (index < 0 || index >= HEXTET_COUNT) {
			throw new IndexOutOfBoundsException("Hextet index out of range: " + index);
		}
		if (value < 0 || value > 0xFFFF) {
			throw new IllegalArgumentException("Hextet value out of range: " + value);
		}
		
		long newHighBits = this.highBits;
		long newLowBits = this.lowBits;
		if (index < 4) {
			int shift = 48 - index * 16;
			newHighBits = (newHighBits & ~(0xFFFFL << shift)) | ((long) value << shift);
		} else {
			int shift = 48 - (index - 4) * 16;
			newLowBits = (newLowBits & ~(0xFFFFL << shift)) | ((long) value << shift);
		}
		return new Ipv6Address(newHighBits, newLowBits, this.zoneId);
	}
	
	/**
	 * Checks if this is an IPv4-mapped IPv6 address (::ffff:x.x.x.x, highBits is 0, lowBits starts with 0x0000FFFF).<br>
	 * IPv4-mapped addresses have the format ::ffff:0:0/96 with the IPv4 address in the last 32 bits.<br>
	 *
	 * @return {@code true} if this is an IPv4-mapped address, {@code false} otherwise
	 */
	public boolean isIpv4Mapped() {
		return this.highBits == 0L && (this.lowBits >>> 32) == 0xFFFFL;
	}
	
	/**
	 * Checks if this is an IPv4-compatible IPv6 address (::x.x.x.x).<br>
	 * IPv4-compatible addresses are deprecated in RFC 4291.<br>
	 *
	 * @return {@code true} if this is an IPv4-compatible address, {@code false} otherwise
	 * @deprecated IPv4-compatible addresses are deprecated
	 */
	@Deprecated
	public boolean isIpv4Compatible() {
		// ::x.x.x.x - highBits is 0, lowBits has 32-bit IPv4 address
		// Must not be ::0 (unspecified) or ::1 (loopback) or ::ffff:x.x.x.x (mapped)
		return this.highBits == 0L && (this.lowBits >>> 32) == 0L && this.lowBits > 1L;
	}
	
	/**
	 * Checks if this is an IPv4-translatable IPv6 address (64:ff9b::/96, well-known prefix for NAT64).<br>
	 * These addresses are used by NAT64 for IPv4/IPv6 translation.<br>
	 *
	 * @return {@code true} if this is an IPv4-translatable address, {@code false} otherwise
	 */
	public boolean isIpv4Translatable() {
		return this.highBits == 0x0064FF9B00000000L && (this.lowBits >>> 32) == 0L;
	}
	
	/**
	 * Extracts the embedded IPv4 address from IPv4-mapped, IPv4-compatible,or IPv4-translatable addresses.<br>
	 * @return An {@link Optional} containing the extracted IPv4 address, or empty if not applicable
	 */
	public @NonNull Optional<Ipv4Address> toIpv4() {
		if (this.isIpv4Mapped() || this.isIpv4Compatible() || this.isIpv4Translatable()) {
			int ipv4Value = (int) this.lowBits;
			return Optional.of(new Ipv4Address(ipv4Value));
		}
		return Optional.empty();
	}
	
	/**
	 * Checks if this address has an EUI-64 interface identifier.<br>
	 * EUI-64 addresses have 0xFFFE in bits 24-39 of the interface identifier (positions 88-103 of the full address).<br>
	 *
	 * @return {@code true} if this address uses EUI-64, {@code false} otherwise
	 */
	public boolean isEui64() {
		return ((this.lowBits >>> 24) & 0xFFFFL) == 0xFFFEL;
	}
	
	/**
	 * Extracts the MAC address from an EUI-64 interface identifier.<br>
	 * This reverses the EUI-64 process by removing the 0xFFFE bytes and
	 * inverting the universal/local bit.
	 *
	 * @return An {@link Optional} containing the extracted MAC address, or empty if not EUI-64
	 */
	public @NonNull Optional<MacAddress> extractMacAddress() {
		if (!this.isEui64()) {
			return Optional.empty();
		}
		
		long highMac = (this.lowBits >>> 40) & 0xFFFFFFL;
		long lowMac = this.lowBits & 0xFFFFFFL;
		highMac ^= 0x020000L;
		long macValue = (highMac << 24) | lowMac;
		return Optional.of(MacAddress.of(macValue));
	}
	
	/**
	 * Checks if this is a solicited-node multicast address (ff02::1:ffxx:xxxx).<br>
	 * Solicited-node addresses are used in neighbor discovery.<br>
	 *
	 * @return {@code true} if this is a solicited-node multicast address, {@code false} otherwise
	 */
	public boolean isSolicitedNodeMulticast() {
		return this.highBits == 0xFF02000000000000L && (this.lowBits >>> 24) == 0x0000000001FFL;
	}
	
	/**
	 * Returns the multicast scope of this address.<br>
	 * The scope is encoded in bits 8-11 of the address (second nibble of the second byte).<br>
	 *
	 * @return The multicast scope of this address
	 * @throws IllegalStateException If this is not a multicast address
	 */
	public @NonNull Ipv6MulticastScope getMulticastScope() {
		if (!this.isMulticast()) {
			throw new IllegalStateException("Not a multicast address");
		}
		int scopeValue = (int) ((this.highBits >>> 48) & 0x0FL);
		return Ipv6MulticastScope.fromValue(scopeValue);
	}
	
	/**
	 * Generates the solicited-node multicast address for this unicast address.<br>
	 * The solicited-node address is formed by taking ff02::1:ff00:0/104 and appending the last 24 bits of the unicast address.<br>
	 *
	 * @return The solicited-node multicast address
	 */
	public @NonNull Ipv6Address toSolicitedNodeMulticast() {
		// ff02:0:0:0:0:1:ffXX:XXXX where XX:XXXX are the last 24 bits of this address
		long last24Bits = this.lowBits & 0xFFFFFFL;
		return new Ipv6Address(0xFF02000000000000L, 0x00000001FF000000L | last24Bits, null);
	}
	
	/**
	 * Checks if this is a Teredo address (2001:0000::/32).<br>
	 * Teredo is a transition technology for providing IPv6 connectivity through IPv4 NATs.<br>
	 *
	 * @return {@code true} if this is a Teredo address, {@code false} otherwise
	 */
	public boolean isTeredo() {
		return (this.highBits >>> 32) == 0x20010000L;
	}
	
	/**
	 * Extracts Teredo information from this address.<br>
	 * Returns the server IPv4 address, client IPv4 address, and UDP port.<br>
	 * <ul>
	 *     <li>Teredo format: 2001:0000:SSSS:SSSS:FLAGS:PORT:CCCC:CCCC</li>
	 *     <li>Server IPv4: bits 32-63 of highBits</li>
	 *     <li>Flags: bits 48-63 of lowBits</li>
	 *     <li>Port: bits 32-47 of lowBits (obfuscated - XOR with 0xFFFF)</li>
	 *     <li>Client IPv4: bits 0-31 of lowBits (obfuscated - XOR with 0xFFFFFFFF)</li>
	 * </ul>
	 *
	 * @return An {@link Optional} containing the Teredo info, or empty if not a Teredo address
	 */
	public @NonNull Optional<TeredoInfo> getTeredoInfo() {
		if (!this.isTeredo()) {
			return Optional.empty();
		}
		
		int serverIpv4 = (int) this.highBits;
		int flags = (int) (this.lowBits >>> 48);
		int port = (int) ((this.lowBits >>> 32) & 0xFFFF) ^ 0xFFFF;
		int clientIpv4 = ~((int) this.lowBits);
		boolean isConeNat = (flags & TeredoInfo.CONE_NAT_FLAG) != 0;
		
		return Optional.of(new TeredoInfo(
			new Ipv4Address(serverIpv4),
			new Ipv4Address(clientIpv4),
			port,
			flags,
			isConeNat
		));
	}
	
	/**
	 * Converts this address to an internet v6 address.<br>
	 * @return An internet v6 address representing this address
	 */
	public @NonNull Inet6Address toInet6Address() {
		try {
			NetworkInterface scopedInterface = null;
			if (this.zoneId != null) {
				try {
					scopedInterface = NetworkInterface.getByName(this.zoneId);
				} catch (SocketException _) {}
			}
			return Inet6Address.getByAddress(null, this.toBytes(), scopedInterface);
		} catch (UnknownHostException e) {
			throw new IllegalStateException("Failed to create internet v6 address", e);
		}
	}
}
