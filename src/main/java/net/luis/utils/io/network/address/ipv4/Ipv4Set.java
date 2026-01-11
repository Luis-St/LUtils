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

import net.luis.utils.io.network.address.IpSet;
import net.luis.utils.io.network.address.ipv6.Ipv6Set;
import org.jspecify.annotations.NonNull;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;

/**
 * Represents an immutable set of IPv4 addresses.<br>
 * This class provides efficient set operations for IP address collections, useful for access control lists, address allocation, and network planning.<br>
 * <p>
 *     Sets are immutable; all modification operations return new instances.<br>
 *     Internally, sets are represented as a minimal collection of non-overlapping,<br>
 *     sorted ranges for efficient storage and operations.
 * </p>
 * <pre>{@code
 * // Create a set from networks
 * Ipv4Set allowed = Ipv4Set.of(
 *     Ipv4Network.parse("10.0.0.0/8"),
 *     Ipv4Network.parse("192.168.0.0/16")
 * );
 *
 * // Subtract a specific subnet
 * Ipv4Set blocked = Ipv4Set.of(Ipv4Network.parse("10.0.0.0/24"));
 *
 * Ipv4Set effective = allowed.difference(blocked);
 *
 * // Check if an address is allowed
 * if (effective.contains(IpAddresses.parseIpv4("10.0.1.1"))) {
 *     System.out.println("Address is allowed");
 * }
 * }</pre>
 *
 * @see IpSet
 * @see Ipv6Set
 *
 * @author Luis-St
 */
public final class Ipv4Set implements IpSet<Ipv4Address, Ipv4Range, Ipv4Network, Ipv4Set> {

	/**
	 * An empty IPv4 set containing no addresses.<br>
	 */
	private static final Ipv4Set EMPTY = new Ipv4Set(List.of());

	/**
	 * A set containing all possible IPv4 addresses (0.0.0.0 - 255.255.255.255).<br>
	 */
	private static final Ipv4Set ALL = new Ipv4Set(List.of(
		Ipv4Range.of(Ipv4Address.UNSPECIFIED, Ipv4Address.BROADCAST)
	));

	/**
	 * The internal list of non-overlapping, sorted ranges representing this set.<br>
	 */
	private final List<Ipv4Range> ranges;

	/**
	 * Constructs a new Ipv4Set from the given list of ranges.<br>
	 * The ranges are assumed to be already normalized (non-overlapping and sorted).<br>
	 *
	 * @param ranges The normalized list of ranges
	 * @throws NullPointerException If ranges is null
	 */
	private Ipv4Set(@NonNull List<Ipv4Range> ranges) {
		Objects.requireNonNull(ranges, "Ranges must not be null");
		this.ranges = List.copyOf(ranges);
	}

	/**
	 * Returns an empty IPv4 set containing no addresses.<br>
	 * @return An empty IPv4 set
	 */
	public static @NonNull Ipv4Set empty() {
		return EMPTY;
	}

	/**
	 * Creates an IPv4 set containing the specified addresses.<br>
	 *
	 * @param addresses The addresses to include in the set
	 * @return A new IPv4 set containing the specified addresses
	 * @throws NullPointerException If addresses is null or contains null elements
	 */
	public static @NonNull Ipv4Set of(Ipv4Address @NonNull ... addresses) {
		Objects.requireNonNull(addresses, "Addresses must not be null");
		if (addresses.length == 0) {
			return EMPTY;
		}
		
		List<Ipv4Range> rangeList = new ArrayList<>(addresses.length);
		for (Ipv4Address address : addresses) {
			Objects.requireNonNull(address, "Address must not be null");
			rangeList.add(Ipv4Range.of(address, address));
		}
		return new Ipv4Set(normalize(rangeList));
	}

	/**
	 * Creates an IPv4 set containing all addresses in the specified networks.<br>
	 *
	 * @param networks The networks to include in the set
	 * @return A new IPv4 set containing all addresses in the specified networks
	 * @throws NullPointerException If networks is null or contains null elements
	 */
	public static @NonNull Ipv4Set of(Ipv4Network @NonNull ... networks) {
		Objects.requireNonNull(networks, "Networks must not be null");
		if (networks.length == 0) {
			return EMPTY;
		}
		List<Ipv4Range> rangeList = new ArrayList<>(networks.length);
		for (Ipv4Network network : networks) {
			Objects.requireNonNull(network, "Network must not be null");
			rangeList.add(Ipv4Range.of(network.networkAddress(), network.broadcastAddress()));
		}
		return new Ipv4Set(normalize(rangeList));
	}

	/**
	 * Creates an IPv4 set containing all addresses in the specified ranges.<br>
	 *
	 * @param ranges The ranges to include in the set
	 * @return A new IPv4 set containing all addresses in the specified ranges
	 * @throws NullPointerException If ranges is null or contains null elements
	 */
	public static @NonNull Ipv4Set of(Ipv4Range @NonNull ... ranges) {
		Objects.requireNonNull(ranges, "Ranges must not be null");
		if (ranges.length == 0) {
			return EMPTY;
		}
		
		List<Ipv4Range> rangeList = new ArrayList<>(ranges.length);
		for (Ipv4Range range : ranges) {
			Objects.requireNonNull(range, "Range must not be null");
			rangeList.add(range);
		}
		return new Ipv4Set(normalize(rangeList));
	}

	/**
	 * Returns a set containing all possible IPv4 addresses (0.0.0.0 - 255.255.255.255).<br>
	 * @return A set containing all IPv4 addresses
	 */
	public static @NonNull Ipv4Set all() {
		return ALL;
	}

	/**
	 * Normalizes a list of ranges by sorting them and merging overlapping/adjacent ranges.<br>
	 *
	 * @param ranges The list of ranges to normalize
	 * @return A new list of non-overlapping, sorted ranges
	 */
	private static @NonNull List<Ipv4Range> normalize(@NonNull List<Ipv4Range> ranges) {
		if (ranges.isEmpty()) {
			return List.of();
		}
		if (ranges.size() == 1) {
			return List.of(ranges.get(0));
		}

		List<Ipv4Range> sorted = new ArrayList<>(ranges);
		sorted.sort(Comparator.comparing(Ipv4Range::start));

		List<Ipv4Range> result = new ArrayList<>();
		Ipv4Range current = sorted.getFirst();
		for (int i = 1; i < sorted.size(); i++) {
			Ipv4Range next = sorted.get(i);
			Optional<Ipv4Range> merged = current.merge(next);
			
			if (merged.isPresent()) {
				current = merged.get();
			} else {
				result.add(current);
				current = next;
			}
		}
		result.add(current);
		return result;
	}

	@Override
	public boolean isEmpty() {
		return this.ranges.isEmpty();
	}

	@Override
	public @NonNull BigInteger size() {
		BigInteger total = BigInteger.ZERO;
		for (Ipv4Range range : this.ranges) {
			total = total.add(range.size());
		}
		return total;
	}

	@Override
	public boolean containsAddress(@NonNull Ipv4Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		
		for (Ipv4Range range : this.ranges) {
			if (range.containsAddress(address)) {
				return true;
			}
			
			if (range.start().compareTo(address) > 0) {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean containsNetwork(@NonNull Ipv4Network network) {
		Objects.requireNonNull(network, "Network must not be null");
		return this.containsRange(Ipv4Range.of(network.networkAddress(), network.broadcastAddress()));
	}

	@Override
	public boolean containsRange(@NonNull Ipv4Range range) {
		Objects.requireNonNull(range, "Range must not be null");
		
		for (Ipv4Range r : this.ranges) {
			if (r.containsRange(range)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(@NonNull Ipv4Set other) {
		Objects.requireNonNull(other, "Other set must not be null");
		
		for (Ipv4Range range : other.ranges) {
			if (!this.containsRange(range)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public @NonNull Ipv4Set union(@NonNull Ipv4Set other) {
		Objects.requireNonNull(other, "Other set must not be null");
		if (this.isEmpty()) {
			return other;
		}
		if (other.isEmpty()) {
			return this;
		}

		List<Ipv4Range> combined = new ArrayList<>(this.ranges.size() + other.ranges.size());
		combined.addAll(this.ranges);
		combined.addAll(other.ranges);
		return new Ipv4Set(normalize(combined));
	}

	@Override
	public @NonNull Ipv4Set intersection(@NonNull Ipv4Set other) {
		Objects.requireNonNull(other, "Other set must not be null");
		if (this.isEmpty() || other.isEmpty()) {
			return EMPTY;
		}

		List<Ipv4Range> result = new ArrayList<>();
		for (Ipv4Range thisRange : this.ranges) {
			for (Ipv4Range otherRange : other.ranges) {
				Optional<Ipv4Range> inter = thisRange.intersection(otherRange);
				inter.ifPresent(result::add);
			}
		}

		if (result.isEmpty()) {
			return EMPTY;
		}
		return new Ipv4Set(normalize(result));
	}

	@Override
	public @NonNull Ipv4Set difference(@NonNull Ipv4Set other) {
		Objects.requireNonNull(other, "Other set must not be null");
		if (this.isEmpty() || other.isEmpty()) {
			return this;
		}

		List<Ipv4Range> current = new ArrayList<>(this.ranges);

		for (Ipv4Range toRemove : other.ranges) {
			List<Ipv4Range> next = new ArrayList<>();
			for (Ipv4Range range : current) {
				next.addAll(range.difference(toRemove));
			}
			current = next;
		}

		if (current.isEmpty()) {
			return EMPTY;
		}
		return new Ipv4Set(normalize(current));
	}

	@Override
	public @NonNull Ipv4Set symmetricDifference(@NonNull Ipv4Set other) {
		Objects.requireNonNull(other, "Other set must not be null");
		return this.difference(other).union(other.difference(this));
	}

	@Override
	public @NonNull Ipv4Set complement() {
		if (this.isEmpty()) {
			return ALL;
		}
		return ALL.difference(this);
	}

	@Override
	public @NonNull Ipv4Set addAddress(@NonNull Ipv4Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		return this.union(Ipv4Set.of(address));
	}

	@Override
	public @NonNull Ipv4Set addNetwork(@NonNull Ipv4Network network) {
		Objects.requireNonNull(network, "Network must not be null");
		return this.union(Ipv4Set.of(network));
	}

	@Override
	public @NonNull Ipv4Set addRange(@NonNull Ipv4Range range) {
		Objects.requireNonNull(range, "Range must not be null");
		return this.union(Ipv4Set.of(range));
	}

	@Override
	public @NonNull Ipv4Set removeAddress(@NonNull Ipv4Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		return this.difference(Ipv4Set.of(address));
	}

	@Override
	public @NonNull Ipv4Set removeNetwork(@NonNull Ipv4Network network) {
		Objects.requireNonNull(network, "Network must not be null");
		return this.difference(Ipv4Set.of(network));
	}

	@Override
	public @NonNull Ipv4Set removeRange(@NonNull Ipv4Range range) {
		Objects.requireNonNull(range, "Range must not be null");
		return this.difference(Ipv4Set.of(range));
	}

	@Override
	public @NonNull List<Ipv4Range> toRanges() {
		return this.ranges;
	}

	@Override
	public @NonNull List<Ipv4Network> toNetworks() {
		if (this.isEmpty()) {
			return List.of();
		}

		List<Ipv4Network> result = new ArrayList<>();
		for (Ipv4Range range : this.ranges) {
			Ipv4Address start = range.start();
			Ipv4Address end = range.end();

			int prefixLength = calculateMinimalPrefixLength(start, end);
			Ipv4Network network = Ipv4Network.of(start, prefixLength);
			result.add(network);
		}
		return result;
	}

	@Override
	public @NonNull List<Ipv4Network> toExactNetworks() {
		if (this.isEmpty()) {
			return List.of();
		}

		List<Ipv4Network> result = new ArrayList<>();
		for (Ipv4Range range : this.ranges) {
			result.addAll(range.toCidrNetworks());
		}
		return result;
	}

	/**
	 * Calculates the minimal prefix length for a network that can contain both addresses.<br>
	 *
	 * @param start The start address
	 * @param end The end address
	 * @return The minimal prefix length
	 */
	private static int calculateMinimalPrefixLength(@NonNull Ipv4Address start, @NonNull Ipv4Address end) {
		int xor = start.value() ^ end.value();
		if (xor == 0) {
			return 32;
		}
		
		return Integer.numberOfLeadingZeros(xor);
	}

	@Override
	public @NonNull Iterator<Ipv4Range> rangeIterator() {
		return this.ranges.iterator();
	}

	@Override
	public @NonNull Stream<Ipv4Range> rangeStream() {
		return this.ranges.stream();
	}

	//region Object methods
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Ipv4Set ipv4Set)) return false;
		
		return this.ranges.equals(ipv4Set.ranges);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.ranges);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public @NonNull String toString() {
		if (this.isEmpty()) {
			return "Ipv4Set[]";
		}
		
		StringBuilder sb = new StringBuilder("Ipv4Set[");
		for (int i = 0; i < this.ranges.size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(this.ranges.get(i));
		}
		sb.append("]");
		return sb.toString();
	}
	//endregion
}
