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

import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.io.network.address.IpNetwork;
import net.luis.utils.io.network.address.ipv4.Ipv4Network;
import org.jspecify.annotations.NonNull;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents an IPv6 network defined by CIDR notation (address/prefix).<br>
 * An IPv6 network is a contiguous block of addresses where the first {@code prefixLength} bits identify the network and the remaining bits identify hosts.<br>
 * <p>
 *     Unlike IPv4, IPv6 doesn't use the concept of broadcast addresses.<br>
 *     The standard subnet size is /64, which allows for 2^64 addresses per subnet (more than all IPv4 addresses squared).
 * </p>
 * <p>
 *     <b>Warning:</b> IPv6 networks can be astronomically large.<br>
 *     A /64 network contains 18 quintillion addresses.<br>
 *     Never attempt to iterate all addresses in a network larger than /120 or so.<br>
 *     Always check {@link #size()} first.
 * </p>
 * <pre>{@code
 * // Create a /64 network
 * Ipv6Network network = Ipv6Network.parse("2001:db8::/64");
 *
 * // Check containment
 * Ipv6Address ip = IpAddresses.parseIpv6("2001:db8::1");
 * if (network.contains(ip)) {
 *     System.out.println("Address is in network");
 * }
 *
 * // Split into two /65 subnets
 * List<Ipv6Network> subnets = network.split();
 * }</pre>
 *
 * @see IpNetwork
 * @see Ipv4Network
 *
 * @author Luis-St
 *
 * @param networkAddress The network address
 * @param prefixLength The CIDR prefix length (0-128)
 */
public record Ipv6Network(@NonNull Ipv6Address networkAddress, int prefixLength) implements IpNetwork<Ipv6Address, Ipv6Network> {
	
	/**
	 * Constructs a new IPv6 network with the given network address and prefix length.<br>
	 *
	 * @param networkAddress The network address (first address in the network)
	 * @param prefixLength The CIDR prefix length (0-128)
	 * @throws NullPointerException If the network address is null
	 * @throws IllegalArgumentException If the prefix length is not between 0 and 128
	 */
	public Ipv6Network {
		Objects.requireNonNull(networkAddress, "Network address must not be null");
		if (prefixLength < 0 || prefixLength > 128) {
			throw new IllegalArgumentException("Prefix length must be between 0 and 128, got: " + prefixLength);
		}
	}
	
	/**
	 * Creates a new IPv6 network from an address and prefix length.<br>
	 * The network address is calculated by applying the prefix mask to the given address.<br>
	 *
	 * @param address The IPv6 address within the network
	 * @param prefixLength The CIDR prefix length (0-128)
	 * @return A new IPv6 network in canonical form
	 * @throws NullPointerException If the address is null
	 * @throws IllegalArgumentException If the prefix length is not between 0 and 128
	 */
	public static @NonNull Ipv6Network of(@NonNull Ipv6Address address, int prefixLength) {
		Objects.requireNonNull(address, "Address must not be null");
		if (prefixLength < 0 || prefixLength > 128) {
			throw new IllegalArgumentException("Prefix length must be between 0 and 128, got: " + prefixLength);
		}
		Ipv6Address networkAddr = applyPrefixMask(address, prefixLength);
		return new Ipv6Network(networkAddr, prefixLength);
	}
	
	/**
	 * Parses an IPv6 network from CIDR notation (e.g., "2001:db8::/32").<br>
	 *
	 * @param cidr The CIDR notation string
	 * @return A new IPv6 network parsed from the string
	 * @throws NullPointerException If the CIDR string is null
	 * @throws IllegalArgumentException If the CIDR string is invalid
	 */
	public static @NonNull Ipv6Network parse(@NonNull String cidr) {
		Objects.requireNonNull(cidr, "CIDR must not be null");
		return tryParse(cidr).orElseThrow(() -> new IllegalArgumentException("Invalid CIDR notation: " + cidr));
	}
	
	/**
	 * Attempts to parse an IPv6 network from CIDR notation (e.g., "2001:db8::/32").<br>
	 *
	 * @param cidr The CIDR notation string
	 * @return An optional containing the parsed network, or empty if parsing fails
	 * @throws NullPointerException If the CIDR string is null
	 */
	@SuppressWarnings("DuplicatedCode")
	public static @NonNull Optional<Ipv6Network> tryParse(@NonNull String cidr) {
		Objects.requireNonNull(cidr, "CIDR must not be null");
		int slashIndex = cidr.indexOf('/');
		if (slashIndex == -1) {
			return Optional.empty();
		}
		
		String addressPart = cidr.substring(0, slashIndex);
		String prefixPart = cidr.substring(slashIndex + 1);
		try {
			int prefixLength = Integer.parseInt(prefixPart);
			if (prefixLength < 0 || prefixLength > 128) {
				return Optional.empty();
			}
			
			Optional<Ipv6Address> address = IpAddresses.tryParseIpv6(addressPart);
			if (address.isEmpty()) {
				return Optional.empty();
			}
			return Optional.of(of(address.orElseThrow(), prefixLength));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Applies a prefix mask to an IPv6 address, clearing host bits.<br>
	 *
	 * @param address The address to mask
	 * @param prefixLength The prefix length
	 * @return The masked address (network address)
	 */
	private static @NonNull Ipv6Address applyPrefixMask(@NonNull Ipv6Address address, int prefixLength) {
		Objects.requireNonNull(address, "Address must not be null");
		if (prefixLength == 0) {
			return new Ipv6Address(0L, 0L, null);
		}
		if (prefixLength == 128) {
			return address.withoutZoneId();
		}
		
		long highBits = address.highBits();
		long lowBits = address.lowBits();
		
		if (prefixLength <= 64) {
			int hostBits = 64 - prefixLength;
			long highMask = -1L << hostBits;
			highBits &= highMask;
			lowBits = 0L;
		} else {
			int hostBits = 128 - prefixLength;
			long lowMask = -1L << hostBits;
			lowBits &= lowMask;
		}
		return new Ipv6Address(highBits, lowBits, null);
	}
	
	@Override
	public @NonNull Ipv6Address broadcastAddress() {
		throw new UnsupportedOperationException("IPv6 does not have broadcast addresses");
	}
	
	@Override
	public @NonNull BigInteger size() {
		int hostBits = 128 - this.prefixLength;
		return BigInteger.ONE.shiftLeft(hostBits);
	}
	
	@Override
	public boolean contains(@NonNull Ipv6Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		
		Ipv6Address maskedAddress = applyPrefixMask(address, this.prefixLength);
		return maskedAddress.highBits() == this.networkAddress.highBits() && maskedAddress.lowBits() == this.networkAddress.lowBits();
	}
	
	@Override
	public boolean contains(@NonNull Ipv6Network other) {
		Objects.requireNonNull(other, "Other network must not be null");
		
		if (this.prefixLength > other.prefixLength) {
			return false;
		}
		return this.contains(other.networkAddress);
	}
	
	@Override
	public boolean overlaps(@NonNull Ipv6Network other) {
		Objects.requireNonNull(other, "Other network must not be null");
		return this.contains(other.networkAddress) || other.contains(this.networkAddress);
	}
	
	@Override
	public @NonNull List<Ipv6Network> split() {
		if (this.prefixLength >= 128) {
			throw new IllegalStateException("Cannot split a /" + this.prefixLength + " network");
		}
		int newPrefixLength = this.prefixLength + 1;
		Ipv6Network first = new Ipv6Network(this.networkAddress, newPrefixLength);
		
		Ipv6Address secondNetworkAddr = setBitAt(this.networkAddress, this.prefixLength);
		Ipv6Network second = new Ipv6Network(secondNetworkAddr, newPrefixLength);
		return List.of(first, second);
	}
	
	/**
	 * Sets a specific bit in an IPv6 address.<br>
	 *
	 * @param address The address to modify
	 * @param bitIndex The bit index (0 is MSB)
	 * @return A new address with the bit set
	 */
	private static @NonNull Ipv6Address setBitAt(@NonNull Ipv6Address address, int bitIndex) {
		long highBits = address.highBits();
		long lowBits = address.lowBits();
		
		if (bitIndex < 64) {
			highBits |= (1L << (63 - bitIndex));
		} else {
			lowBits |= (1L << (127 - bitIndex));
		}
		return new Ipv6Address(highBits, lowBits, null);
	}
	
	@Override
	public @NonNull Optional<Ipv6Network> supernet() {
		if (this.prefixLength == 0) {
			return Optional.empty();
		}
		return Optional.of(of(this.networkAddress, this.prefixLength - 1));
	}
	
	@Override
	@SuppressWarnings("ExtractMethodRecommender")
	public @NonNull List<Ipv6Network> subnets(int newPrefixLength) {
		if (newPrefixLength <= this.prefixLength) {
			throw new IllegalArgumentException("New prefix length must be greater than current prefix length: " + this.prefixLength + ", got: " + newPrefixLength);
		}
		if (newPrefixLength > 128) {
			throw new IllegalArgumentException("Prefix length cannot exceed 128, got: " + newPrefixLength);
		}
		
		int prefixDifference = newPrefixLength - this.prefixLength;
		if (prefixDifference > 20) {
			throw new IllegalArgumentException("Cannot generate more than 2^20 subnets. Requested prefix difference: " + prefixDifference);
		}
		
		int subnetCount = 1 << prefixDifference;
		List<Ipv6Network> result = new ArrayList<>(subnetCount);
		
		BigInteger baseValue = this.networkAddress.toBigInteger();
		BigInteger increment = BigInteger.ONE.shiftLeft(128 - newPrefixLength);
		for (int i = 0; i < subnetCount; i++) {
			BigInteger subnetValue = baseValue.add(increment.multiply(BigInteger.valueOf(i)));
			Ipv6Address subnetAddr = bigIntegerToIpv6Address(subnetValue);
			result.add(new Ipv6Network(subnetAddr, newPrefixLength));
		}
		return result;
	}
	
	/**
	 * Converts a BigInteger to an Ipv6Address.<br>
	 *
	 * @param value The BigInteger value
	 * @return The corresponding Ipv6Address
	 */
	private static @NonNull Ipv6Address bigIntegerToIpv6Address(@NonNull BigInteger value) {
		byte[] bytes = value.toByteArray();
		byte[] addressBytes = new byte[16];
		
		int srcOffset = Math.max(0, bytes.length - 16);
		int destOffset = Math.max(0, 16 - bytes.length);
		int length = Math.min(bytes.length, 16);
		
		System.arraycopy(bytes, srcOffset, addressBytes, destOffset, length);
		long highBits = 0;
		long lowBits = 0;
		for (int i = 0; i < 8; i++) {
			highBits = (highBits << 8) | (addressBytes[i] & 0xFF);
		}
		for (int i = 8; i < 16; i++) {
			lowBits = (lowBits << 8) | (addressBytes[i] & 0xFF);
		}
		return new Ipv6Address(highBits, lowBits, null);
	}
	
	@Override
	public @NonNull Iterator<Ipv6Address> iterator() {
		return new Ipv6NetworkIterator(this.networkAddress, this.lastAddress());
	}
	
	/**
	 * Returns the last address in this network.<br>
	 * This is the highest address within the network range, calculated by setting all host bits to 1.<br>
	 *
	 * @return The last address in the network
	 */
	public @NonNull Ipv6Address lastAddress() {
		if (this.prefixLength == 128) {
			return this.networkAddress;
		}
		if (this.prefixLength == 0) {
			return Ipv6Address.MAX;
		}
		
		long highBits = this.networkAddress.highBits();
		long lowBits = this.networkAddress.lowBits();
		
		if (this.prefixLength <= 64) {
			int hostBits = 64 - this.prefixLength;
			long hostMask = (1L << hostBits) - 1;
			highBits |= hostMask;
			lowBits = -1L;
		} else {
			int hostBits = 128 - this.prefixLength;
			long hostMask = (1L << hostBits) - 1;
			lowBits |= hostMask;
		}
		return new Ipv6Address(highBits, lowBits, null);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public @NonNull Stream<Ipv6Address> addressStream() {
		BigInteger size = this.size();
		long streamSize = size.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0 ? Long.MAX_VALUE : size.longValueExact();
		
		Spliterator<Ipv6Address> spliterator = Spliterators.spliterator(
			this.iterator(),
			streamSize,
			Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.IMMUTABLE
		);
		return StreamSupport.stream(spliterator, false);
	}
	
	@Override
	public @NonNull Iterator<Ipv6Address> hostIterator() {
		throw new UnsupportedOperationException("IPv6 does not have reserved host addresses, all addresses are usable hosts");
	}
	
	@Override
	public @NonNull Stream<Ipv6Address> hostStream() {
		throw new UnsupportedOperationException("IPv6 does not have reserved host addresses, all addresses are usable hosts");
	}
	
	@Override
	public @NonNull BigInteger hostCount() {
		throw new UnsupportedOperationException("IPv6 does not have reserved host addresses, all addresses are usable hosts");
	}
	
	@Override
	public boolean isCanonical() {
		Ipv6Address canonical = applyPrefixMask(this.networkAddress, this.prefixLength);
		return canonical.highBits() == this.networkAddress.highBits() && canonical.lowBits() == this.networkAddress.lowBits();
	}
	
	@Override
	public @NonNull Ipv6Network toCanonical() {
		if (this.isCanonical()) {
			return this;
		}
		return of(this.networkAddress, this.prefixLength);
	}
	
	@Override
	public @NonNull String toCidrNotation() {
		return formatIpv6Address(this.networkAddress) + "/" + this.prefixLength;
	}
	
	/**
	 * Converts this network to an equivalent range covering all addresses in the network.<br>
	 * The range starts at the network address and ends at the last address.<br>
	 *
	 * @return A new IPv6 range covering all addresses in this network
	 */
	public @NonNull Ipv6Range toRange() {
		return Ipv6Range.fromNetwork(this);
	}
	
	/**
	 * Formats an IPv6 address in compressed notation.<br>
	 *
	 * @param address The address to format
	 * @return The formatted string
	 * @throws NullPointerException If the address is null
	 */
	private static @NonNull String formatIpv6Address(@NonNull Ipv6Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		int[] hextets = new int[8];
		for (int i = 0; i < 8; i++) {
			hextets[i] = address.getHextet(i);
		}
		
		int bestStart = -1;
		int bestLength = 0;
		int currentStart = -1;
		int currentLength = 0;
		for (int i = 0; i < 8; i++) {
			if (hextets[i] == 0) {
				if (currentStart == -1) {
					currentStart = i;
					currentLength = 1;
				} else {
					currentLength++;
				}
			} else {
				if (currentLength > bestLength && currentLength > 1) {
					bestStart = currentStart;
					bestLength = currentLength;
				}
				currentStart = -1;
				currentLength = 0;
			}
		}
		if (currentLength > bestLength && currentLength > 1) {
			bestStart = currentStart;
			bestLength = currentLength;
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			if (bestStart != -1 && i >= bestStart && i < bestStart + bestLength) {
				if (i == bestStart) {
					sb.append("::");
				}
				continue;
			}
			if (i > 0 && !(bestStart != -1 && i == bestStart + bestLength)) {
				sb.append(':');
			}
			sb.append(Integer.toHexString(hextets[i]));
		}
		return sb.toString();
	}
	
	@Override
	public int compareTo(@NonNull Ipv6Network other) {
		int addressCompare = this.networkAddress.compareTo(other.networkAddress);
		
		if (addressCompare != 0) {
			return addressCompare;
		}
		return Integer.compare(this.prefixLength, other.prefixLength);
	}
	
	/**
	 * Checks if this network is a standard /64 subnet.<br>
	 * A /64 is the standard prefix length for IPv6 subnets and is required for SLAAC (Stateless Address Autoconfiguration) to work.<br>
	 *
	 * @return {@code true} if this is a /64 network, {@code false} otherwise
	 */
	public boolean isStandardSubnet() {
		return this.prefixLength == 64;
	}
	
	/**
	 * Checks if this network represents a single host (/128).<br>
	 * @return {@code true} if this is a /128 network, {@code false} otherwise
	 */
	public boolean isSingleHost() {
		return this.prefixLength == 128;
	}
	
	@Override
	public @NonNull String toString() {
		return this.toCidrNotation();
	}
	
	//region Inner classes
	
	/**
	 * Iterator implementation for iterating over addresses in an IPv6 network.<br>
	 *
	 * @author Luis-St
	 */
	private static final class Ipv6NetworkIterator implements Iterator<Ipv6Address> {
		
		/**
		 * The end address of the iteration (inclusive).<br>
		 */
		private final Ipv6Address endAddress;
		/**
		 * The current address in the iteration.<br>
		 */
		private Ipv6Address currentAddress;
		/**
		 * Indicates if there are more addresses to iterate.<br>
		 */
		private boolean hasNext;
		
		/**
		 * Creates a new iterator from the start address to the end address (inclusive).<br>
		 *
		 * @param startAddress The first address to iterate
		 * @param endAddress The last address to iterate (inclusive)
		 * @throws NullPointerException If either address is null
		 */
		Ipv6NetworkIterator(@NonNull Ipv6Address startAddress, @NonNull Ipv6Address endAddress) {
			this.currentAddress = Objects.requireNonNull(startAddress, "Start address must not be null");
			this.endAddress = Objects.requireNonNull(endAddress, "End address must not be null");
			this.hasNext = startAddress.compareTo(endAddress) <= 0;
		}
		
		@Override
		public boolean hasNext() {
			return this.hasNext;
		}
		
		@Override
		public @NonNull Ipv6Address next() {
			if (!this.hasNext) {
				throw new NoSuchElementException("No more addresses in network");
			}
			
			Ipv6Address result = this.currentAddress;
			if (this.currentAddress.compareTo(this.endAddress) >= 0) {
				this.hasNext = false;
			} else {
				this.currentAddress = this.currentAddress.next().orElseThrow();
			}
			return result;
		}
	}
	//endregion
}
