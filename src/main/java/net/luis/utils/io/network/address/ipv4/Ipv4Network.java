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

import net.luis.utils.io.network.address.IpAddresses;
import net.luis.utils.io.network.address.IpNetwork;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents an IPv4 network defined by CIDR notation (address/prefix).<br>
 * An IPv4 network is a contiguous block of addresses where the first {@code prefixLength}<br>
 * bits identify the network and the remaining bits identify hosts within that network.<br>
 * <p>
 *     For example, the network 192.168.1.0/24 contains 256 addresses from 192.168.1.0 (network address) to 192.168.1.255 (broadcast address).<br>
 *     The usable host addresses are 192.168.1.1 through 192.168.1.254 (254 hosts).
 * </p>
 * <p>
 *     <b>Warning:</b> For large networks (e.g., /8 containing 16+ million addresses),<br>
 *     iterating over all addresses may consume significant memory and time.<br>
 *     Use {@link #size()} to check the network size before iterating.
 * </p>
 * <pre>{@code
 * // Create a /24 network
 * Ipv4Network network = Ipv4Network.parse("192.168.1.0/24");
 *
 * // Check containment
 * Ipv4Address ip = IpAddresses.parseIpv4("192.168.1.100");
 * if (network.contains(ip)) {
 *     System.out.println("Address is in network");
 * }
 *
 * // Get subnet information
 * System.out.println("Network: " + network.networkAddress());   // 192.168.1.0
 * System.out.println("Broadcast: " + network.broadcastAddress()); // 192.168.1.255
 * System.out.println("Mask: " + network.subnetMask());          // 255.255.255.0
 * System.out.println("Hosts: " + network.hostCount());          // 254
 *
 * // Split into two /25 subnets
 * List<Ipv4Network> subnets = network.split();
 * }</pre>
 *
 * @see Ipv4Address
 * @see Ipv4SubnetMask
 * @see Ipv4Range
 *
 * @author Luis-St
 *
 * @param networkAddress The network address (first address in the network)
 * @param prefixLength The CIDR prefix length (0-32)
 */
public record Ipv4Network(@NonNull Ipv4Address networkAddress, int prefixLength) implements IpNetwork<Ipv4Address, Ipv4Network> {

	/**
	 * Constructs a new IPv4 network with the given network address and prefix length.<br>
	 *
	 * @param networkAddress The network address (first address in the network)
	 * @param prefixLength The CIDR prefix length (0-32)
	 * @throws NullPointerException If the network address is null
	 * @throws IllegalArgumentException If the prefix length is not between 0 and 32
	 */
	public Ipv4Network {
		Objects.requireNonNull(networkAddress, "Network address must not be null");
		
		if (prefixLength < 0 || prefixLength > 32) {
			throw new IllegalArgumentException("Prefix length must be between 0 and 32, got: " + prefixLength);
		}
	}

	/**
	 * Creates a new IPv4 network from an address and prefix length.<br>
	 * The network address is calculated by applying the prefix mask to the given address.<br>
	 *
	 * @param address The IPv4 address within the network
	 * @param prefixLength The CIDR prefix length (0-32)
	 * @return A new IPv4 network in canonical form
	 * @throws NullPointerException If the address is null
	 * @throws IllegalArgumentException If the prefix length is not between 0 and 32
	 */
	public static @NonNull Ipv4Network of(@NonNull Ipv4Address address, int prefixLength) {
		Objects.requireNonNull(address, "Address must not be null");
		if (prefixLength < 0 || prefixLength > 32) {
			throw new IllegalArgumentException("Prefix length must be between 0 and 32, got: " + prefixLength);
		}
		
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(prefixLength);
		Ipv4Address networkAddr = mask.applyTo(address);
		return new Ipv4Network(networkAddr, prefixLength);
	}

	/**
	 * Creates a new IPv4 network from an address and subnet mask.<br>
	 * The network address is calculated by applying the subnet mask to the given address.<br>
	 *
	 * @param address The IPv4 address within the network
	 * @param mask The subnet mask defining the network portion
	 * @return A new IPv4 network in canonical form
	 * @throws NullPointerException If the address or mask is null
	 */
	public static @NonNull Ipv4Network of(@NonNull Ipv4Address address, @NonNull Ipv4SubnetMask mask) {
		Objects.requireNonNull(address, "Address must not be null");
		Objects.requireNonNull(mask, "Mask must not be null");
		
		Ipv4Address networkAddr = mask.applyTo(address);
		return new Ipv4Network(networkAddr, mask.toPrefixLength());
	}

	/**
	 * Parses an IPv4 network from CIDR notation (e.g., "192.168.1.0/24").<br>
	 *
	 * @param cidr The CIDR notation string
	 * @return A new IPv4 network parsed from the string
	 * @throws NullPointerException If the CIDR string is null
	 * @throws IllegalArgumentException If the CIDR string is invalid
	 */
	public static @NonNull Ipv4Network parse(@NonNull String cidr) {
		Objects.requireNonNull(cidr, "CIDR must not be null");
		return tryParse(cidr).orElseThrow(() -> new IllegalArgumentException("Invalid CIDR notation: " + cidr));
	}

	/**
	 * Attempts to parse an IPv4 network from CIDR notation (e.g., "192.168.1.0/24").<br>
	 *
	 * @param cidr The CIDR notation string
	 * @return An optional containing the parsed network, or empty if parsing fails
	 * @throws NullPointerException If the CIDR string is null
	 */
	@SuppressWarnings("DuplicatedCode")
	public static @NonNull Optional<Ipv4Network> tryParse(@NonNull String cidr) {
		Objects.requireNonNull(cidr, "CIDR must not be null");
		int slashIndex = cidr.indexOf('/');
		if (slashIndex == -1) {
			return Optional.empty();
		}
		
		String addressPart = cidr.substring(0, slashIndex);
		String prefixPart = cidr.substring(slashIndex + 1);
		try {
			int prefixLength = Integer.parseInt(prefixPart);
			if (prefixLength < 0 || prefixLength > 32) {
				return Optional.empty();
			}
			
			Optional<Ipv4Address> address = IpAddresses.tryParseIpv4(addressPart);
			if (address.isEmpty()) {
				return Optional.empty();
			}
			return Optional.of(of(address.orElseThrow(), prefixLength));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}
	
	@Override
	public @NonNull Ipv4Address broadcastAddress() {
		int hostBits = 32 - this.prefixLength;
		if (hostBits == 0) {
			return this.networkAddress;
		}
		
		int hostMask = (1 << hostBits) - 1;
		int broadcastValue = this.networkAddress.value() | hostMask;
		return Ipv4Address.fromValue(broadcastValue);
	}
	
	@Override
	public @NonNull BigInteger size() {
		int hostBits = 32 - this.prefixLength;
		return BigInteger.ONE.shiftLeft(hostBits);
	}
	
	@Override
	public boolean contains(@NonNull Ipv4Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		
		Ipv4SubnetMask mask = this.subnetMask();
		Ipv4Address maskedAddress = mask.applyTo(address);
		return maskedAddress.equals(this.networkAddress);
	}
	
	@Override
	public boolean contains(@NonNull Ipv4Network other) {
		Objects.requireNonNull(other, "Other network must not be null");
		
		if (this.prefixLength > other.prefixLength) {
			return false;
		}
		return this.contains(other.networkAddress);
	}
	
	@Override
	public boolean overlaps(@NonNull Ipv4Network other) {
		Objects.requireNonNull(other, "Other network must not be null");
		return this.contains(other.networkAddress) || other.contains(this.networkAddress);
	}

	@Override
	public @NonNull List<Ipv4Network> split() {
		if (this.prefixLength >= 32) {
			throw new IllegalStateException("Cannot split a /" + this.prefixLength + " network");
		}
		int newPrefixLength = this.prefixLength + 1;
		Ipv4Network first = new Ipv4Network(this.networkAddress, newPrefixLength);

		int hostBits = 32 - newPrefixLength;
		int secondNetworkValue = this.networkAddress.value() | (1 << hostBits);
		Ipv4Network second = new Ipv4Network(Ipv4Address.fromValue(secondNetworkValue), newPrefixLength);
		return List.of(first, second);
	}
	
	@Override
	public @NonNull Optional<Ipv4Network> supernet() {
		if (this.prefixLength == 0) {
			return Optional.empty();
		}
		return Optional.of(of(this.networkAddress, this.prefixLength - 1));
	}
	
	@Override
	public @NonNull List<Ipv4Network> subnets(int newPrefixLength) {
		if (newPrefixLength <= this.prefixLength) {
			throw new IllegalArgumentException("New prefix length must be greater than current prefix length: " + this.prefixLength + ", got: " + newPrefixLength);
		}
		if (newPrefixLength > 32) {
			throw new IllegalArgumentException("Prefix length cannot exceed 32, got: " + newPrefixLength);
		}

		int subnetCount = 1 << (newPrefixLength - this.prefixLength);
		int hostBitsInNewSubnet = 32 - newPrefixLength;

		List<Ipv4Network> result = new ArrayList<>(subnetCount);
		int baseValue = this.networkAddress.value();

		for (int i = 0; i < subnetCount; i++) {
			int subnetValue = baseValue + (i << hostBitsInNewSubnet);
			result.add(new Ipv4Network(Ipv4Address.fromValue(subnetValue), newPrefixLength));
		}
		return result;
	}

	@Override
	public @NonNull Iterator<Ipv4Address> iterator() {
		return new Ipv4NetworkIterator(this.networkAddress, this.broadcastAddress());
	}

	@Override
	public @NonNull Stream<Ipv4Address> addressStream() {
		Spliterator<Ipv4Address> spliterator = Spliterators.spliterator(
			this.iterator(),
			this.size().longValueExact(),
			Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.IMMUTABLE
		);
		return StreamSupport.stream(spliterator, false);
	}

	@Override
	public @NonNull Iterator<Ipv4Address> hostIterator() {
		if (this.prefixLength >= 31) {
			return this.iterator();
		}
		
		Ipv4Address firstHost = this.networkAddress.next().orElse(this.networkAddress);
		Ipv4Address lastHost = this.broadcastAddress().previous().orElse(this.broadcastAddress());
		return new Ipv4NetworkIterator(firstHost, lastHost);
	}

	@Override
	public @NonNull Stream<Ipv4Address> hostStream() {
		long hostCount = this.hostCount().longValueExact();
		
		Spliterator<Ipv4Address> spliterator = Spliterators.spliterator(
			this.hostIterator(),
			hostCount,
			Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.IMMUTABLE
		);
		return StreamSupport.stream(spliterator, false);
	}

	@Override
	public @NonNull BigInteger hostCount() {
		if (this.prefixLength >= 31) {
			return this.size();
		}
		return this.size().subtract(BigInteger.TWO);
	}

	@Override
	public boolean isCanonical() {
		Ipv4SubnetMask mask = this.subnetMask();
		Ipv4Address canonical = mask.applyTo(this.networkAddress);
		return canonical.equals(this.networkAddress);
	}

	@Override
	public @NonNull Ipv4Network toCanonical() {
		if (this.isCanonical()) {
			return this;
		}
		return of(this.networkAddress, this.prefixLength);
	}

	@Override
	public @NonNull String toCidrNotation() {
		return this.networkAddress.toString() + "/" + this.prefixLength;
	}
	
	@Override
	public int compareTo(@NonNull Ipv4Network other) {
		int addressCompare = this.networkAddress.compareTo(other.networkAddress);
		if (addressCompare != 0) {
			return addressCompare;
		}
		return Integer.compare(this.prefixLength, other.prefixLength);
	}

	/**
	 * Returns the subnet mask for this network.<br>
	 * The subnet mask has the first {@code prefixLength} bits set to 1.<br>
	 *
	 * @return The subnet mask for this network
	 */
	public @NonNull Ipv4SubnetMask subnetMask() {
		return Ipv4SubnetMask.fromPrefixLength(this.prefixLength);
	}

	/**
	 * Returns the wildcard mask for this network.<br>
	 * The wildcard mask is the inverse of the subnet mask.<br>
	 *
	 * @return The wildcard mask for this network
	 */
	public @NonNull Ipv4SubnetMask wildcardMask() {
		return this.subnetMask().toWildcard();
	}

	/**
	 * Returns the first usable host address in this network.<br>
	 * <p>
	 *     For /31 networks (point-to-point links), returns the network address.<br>
	 *     For /32 networks (single host), returns the only address.<br>
	 *     For other networks, returns the address after the network address.
	 * </p>
	 *
	 * @return An optional containing the first usable host address, or empty if the network has no usable hosts
	 */
	public @NonNull Optional<Ipv4Address> firstHost() {
		if (this.prefixLength == 32) {
			return Optional.of(this.networkAddress);
		}
		if (this.prefixLength == 31) {
			return Optional.of(this.networkAddress);
		}
		return this.networkAddress.next();
	}

	/**
	 * Returns the last usable host address in this network.<br>
	 * <p>
	 *     For /31 networks (point-to-point links), returns the broadcast address.<br>
	 *     For /32 networks (single host), returns the only address.<br>
	 *     For other networks, returns the address before the broadcast address.
	 * </p>
	 *
	 * @return An optional containing the last usable host address, or empty if the network has no usable hosts
	 */
	public @NonNull Optional<Ipv4Address> lastHost() {
		if (this.prefixLength == 32) {
			return Optional.of(this.networkAddress);
		}
		if (this.prefixLength == 31) {
			return Optional.of(this.broadcastAddress());
		}
		return this.broadcastAddress().previous();
	}

	@Override
	public @NonNull String toString() {
		return this.toCidrNotation();
	}

	//region Inner class
	
	/**
	 * Iterator implementation for iterating over addresses in an IPv4 network.<br>
	 *
	 * @author Luis-St
	 */
	private static final class Ipv4NetworkIterator implements Iterator<Ipv4Address> {
		
		/**
		 * The last address to iterate (inclusive).<br>
		 */
		private final Ipv4Address endAddress;
		/**
		 * The current address in the iteration.<br>
		 */
		private Ipv4Address currentAddress;
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
		Ipv4NetworkIterator(@NonNull Ipv4Address startAddress, @NonNull Ipv4Address endAddress) {
			this.currentAddress = Objects.requireNonNull(startAddress, "Start address must not be null");
			this.endAddress = Objects.requireNonNull(endAddress, "End address must not be null");
			this.hasNext = startAddress.compareTo(endAddress) <= 0;
		}

		@Override
		public boolean hasNext() {
			return this.hasNext;
		}

		@Override
		public @NonNull Ipv4Address next() {
			if (!this.hasNext) {
				throw new NoSuchElementException("No more addresses in network");
			}
			
			Ipv4Address result = this.currentAddress;
			if (this.currentAddress.equals(this.endAddress)) {
				this.hasNext = false;
			} else {
				this.currentAddress = this.currentAddress.next().orElseThrow();
			}
			return result;
		}
	}
	//endregion
}
