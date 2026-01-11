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

import net.luis.utils.io.network.address.IpSet;
import org.jspecify.annotations.NonNull;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;

/**
 * Represents an immutable set of IPv6 addresses.<br>
 * This class provides efficient set operations for IPv6 address collections.<br>
 * <p>
 *     <b>Note:</b> Zone identifiers are stripped from addresses when added to a set, as they are not relevant for set membership.
 * </p>
 * <p>
 *     <b>Warning:</b> The IPv6 address space is enormous (2^128 addresses).<br>
 *     Be careful with operations like {@link #complement()} on small sets, as the result could theoretically contain a vast number of addresses.<br>
 *     The set stores ranges efficiently, but avoid iterating over large ranges.
 * </p>
 * <pre>{@code
 * Ipv6Set allowed = Ipv6Set.of(
 *     Ipv6Network.parse("2001:db8::/32"),
 *     Ipv6Network.parse("2001:db9::/32")
 * );
 *
 * Ipv6Set blocked = Ipv6Set.of(Ipv6Network.parse("2001:db8:1::/48"));
 *
 * Ipv6Set effective = allowed.difference(blocked);
 * }</pre>
 *
 * @author Luis-St
 */
public final class Ipv6Set implements IpSet<Ipv6Address, Ipv6Range, Ipv6Network, Ipv6Set> {

	/**
	 * An empty IPv6 set containing no addresses.<br>
	 */
	private static final Ipv6Set EMPTY = new Ipv6Set(List.of());

	/**
	 * A set containing all possible IPv6 addresses (:: to ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff).<br>
	 */
	private static final Ipv6Set ALL = new Ipv6Set(List.of(
		Ipv6Range.of(Ipv6Address.UNSPECIFIED, new Ipv6Address(-1L, -1L, null))
	));

	/**
	 * The internal list of non-overlapping, sorted ranges representing this set.
	 */
	private final List<Ipv6Range> ranges;

	/**
	 * Constructs a new Ipv6Set from the given list of ranges.<br>
	 * The ranges are assumed to be already normalized (non-overlapping and sorted).<br>
	 *
	 * @param ranges The normalized list of ranges
	 * @throws NullPointerException If ranges is null
	 */
	private Ipv6Set(@NonNull List<Ipv6Range> ranges) {
		Objects.requireNonNull(ranges, "Ranges must not be null");
		this.ranges = List.copyOf(ranges);
	}

	/**
	 * Returns an empty IPv6 set containing no addresses.<br>
	 * @return An empty IPv6 set
	 */
	public static @NonNull Ipv6Set empty() {
		return EMPTY;
	}

	/**
	 * Creates an IPv6 set containing the specified addresses.<br>
	 * Zone identifiers are stripped from all addresses.<br>
	 *
	 * @param addresses The addresses to include in the set
	 * @return A new IPv6 set containing the specified addresses
	 * @throws NullPointerException If addresses is null or contains null elements
	 */
	public static @NonNull Ipv6Set of(@NonNull Ipv6Address @NonNull ... addresses) {
		Objects.requireNonNull(addresses, "Addresses must not be null");
		if (addresses.length == 0) {
			return EMPTY;
		}
		
		List<Ipv6Range> rangeList = new ArrayList<>(addresses.length);
		for (Ipv6Address address : addresses) {
			Objects.requireNonNull(address, "Address must not be null");
			Ipv6Address stripped = address.withoutZoneId();
			rangeList.add(Ipv6Range.of(stripped, stripped));
		}
		return new Ipv6Set(normalize(rangeList));
	}

	/**
	 * Creates an IPv6 set containing all addresses in the specified networks.<br>
	 *
	 * @param networks The networks to include in the set
	 * @return A new IPv6 set containing all addresses in the specified networks
	 * @throws NullPointerException If networks is null or contains null elements
	 */
	public static @NonNull Ipv6Set of(@NonNull Ipv6Network @NonNull ... networks) {
		Objects.requireNonNull(networks, "Networks must not be null");
		if (networks.length == 0) {
			return EMPTY;
		}
		
		List<Ipv6Range> rangeList = new ArrayList<>(networks.length);
		for (Ipv6Network network : networks) {
			Objects.requireNonNull(network, "Network must not be null");
			rangeList.add(Ipv6Range.of(network.networkAddress(), network.broadcastAddress()));
		}
		return new Ipv6Set(normalize(rangeList));
	}

	/**
	 * Creates an IPv6 set containing all addresses in the specified ranges.<br>
	 *
	 * @param ranges The ranges to include in the set
	 * @return A new IPv6 set containing all addresses in the specified ranges
	 * @throws NullPointerException If ranges is null or contains null elements
	 */
	public static @NonNull Ipv6Set of(@NonNull Ipv6Range @NonNull ... ranges) {
		Objects.requireNonNull(ranges, "Ranges must not be null");
		if (ranges.length == 0) {
			return EMPTY;
		}
		
		List<Ipv6Range> rangeList = new ArrayList<>(ranges.length);
		for (Ipv6Range range : ranges) {
			Objects.requireNonNull(range, "Range must not be null");
			rangeList.add(range);
		}
		return new Ipv6Set(normalize(rangeList));
	}

	/**
	 * Returns a set containing all possible IPv6 addresses (:: to ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff).<br>
	 * @return A set containing all IPv6 addresses
	 */
	public static @NonNull Ipv6Set all() {
		return ALL;
	}

	/**
	 * Normalizes a list of ranges by sorting them and merging overlapping/adjacent ranges.<br>
	 *
	 * @param ranges The list of ranges to normalize
	 * @return A new list of non-overlapping, sorted ranges
	 */
	private static @NonNull List<Ipv6Range> normalize(@NonNull List<Ipv6Range> ranges) {
		if (ranges.isEmpty()) {
			return List.of();
		}
		if (ranges.size() == 1) {
			return List.of(ranges.getFirst());
		}

		List<Ipv6Range> sorted = new ArrayList<>(ranges);
		sorted.sort(Comparator.comparing(Ipv6Range::start));

		List<Ipv6Range> result = new ArrayList<>();
		Ipv6Range current = sorted.getFirst();
		for (int i = 1; i < sorted.size(); i++) {
			Ipv6Range next = sorted.get(i);
			Optional<Ipv6Range> merged = current.merge(next);
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
		for (Ipv6Range range : this.ranges) {
			total = total.add(range.size());
		}
		return total;
	}

	@Override
	public boolean containsAddress(@NonNull Ipv6Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		Ipv6Address stripped = address.withoutZoneId();
		
		for (Ipv6Range range : this.ranges) {
			if (range.containsAddress(stripped)) {
				return true;
			}
			
			if (range.start().compareTo(stripped) > 0) {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean containsNetwork(@NonNull Ipv6Network network) {
		Objects.requireNonNull(network, "Network must not be null");
		return this.containsRange(Ipv6Range.of(network.networkAddress(), network.broadcastAddress()));
	}

	@Override
	public boolean containsRange(@NonNull Ipv6Range range) {
		Objects.requireNonNull(range, "Range must not be null");
		
		for (Ipv6Range r : this.ranges) {
			if (r.containsRange(range)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(@NonNull Ipv6Set other) {
		Objects.requireNonNull(other, "Other set must not be null");
		
		for (Ipv6Range range : other.ranges) {
			if (!this.containsRange(range)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public @NonNull Ipv6Set union(@NonNull Ipv6Set other) {
		Objects.requireNonNull(other, "Other set must not be null");
		if (this.isEmpty()) {
			return other;
		}
		if (other.isEmpty()) {
			return this;
		}

		List<Ipv6Range> combined = new ArrayList<>(this.ranges.size() + other.ranges.size());
		combined.addAll(this.ranges);
		combined.addAll(other.ranges);
		return new Ipv6Set(normalize(combined));
	}

	@Override
	public @NonNull Ipv6Set intersection(@NonNull Ipv6Set other) {
		Objects.requireNonNull(other, "Other set must not be null");
		if (this.isEmpty() || other.isEmpty()) {
			return EMPTY;
		}

		List<Ipv6Range> result = new ArrayList<>();
		for (Ipv6Range thisRange : this.ranges) {
			for (Ipv6Range otherRange : other.ranges) {
				Optional<Ipv6Range> inter = thisRange.intersection(otherRange);
				inter.ifPresent(result::add);
			}
		}

		if (result.isEmpty()) {
			return EMPTY;
		}
		return new Ipv6Set(normalize(result));
	}

	@Override
	public @NonNull Ipv6Set difference(@NonNull Ipv6Set other) {
		Objects.requireNonNull(other, "Other set must not be null");
		if (this.isEmpty() || other.isEmpty()) {
			return this;
		}

		List<Ipv6Range> current = new ArrayList<>(this.ranges);
		for (Ipv6Range toRemove : other.ranges) {
			List<Ipv6Range> next = new ArrayList<>();
			for (Ipv6Range range : current) {
				next.addAll(range.difference(toRemove));
			}
			current = next;
		}

		if (current.isEmpty()) {
			return EMPTY;
		}
		return new Ipv6Set(normalize(current));
	}

	@Override
	public @NonNull Ipv6Set symmetricDifference(@NonNull Ipv6Set other) {
		Objects.requireNonNull(other, "Other set must not be null");
		return this.difference(other).union(other.difference(this));
	}

	@Override
	public @NonNull Ipv6Set complement() {
		if (this.isEmpty()) {
			return ALL;
		}
		return ALL.difference(this);
	}

	@Override
	public @NonNull Ipv6Set addAddress(@NonNull Ipv6Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		return this.union(Ipv6Set.of(address));
	}

	@Override
	public @NonNull Ipv6Set addNetwork(@NonNull Ipv6Network network) {
		Objects.requireNonNull(network, "Network must not be null");
		return this.union(Ipv6Set.of(network));
	}

	@Override
	public @NonNull Ipv6Set addRange(@NonNull Ipv6Range range) {
		Objects.requireNonNull(range, "Range must not be null");
		return this.union(Ipv6Set.of(range));
	}

	@Override
	public @NonNull Ipv6Set removeAddress(@NonNull Ipv6Address address) {
		Objects.requireNonNull(address, "Address must not be null");
		return this.difference(Ipv6Set.of(address));
	}

	@Override
	public @NonNull Ipv6Set removeNetwork(@NonNull Ipv6Network network) {
		Objects.requireNonNull(network, "Network must not be null");
		return this.difference(Ipv6Set.of(network));
	}

	@Override
	public @NonNull Ipv6Set removeRange(@NonNull Ipv6Range range) {
		Objects.requireNonNull(range, "Range must not be null");
		return this.difference(Ipv6Set.of(range));
	}

	@Override
	public @NonNull List<Ipv6Range> toRanges() {
		return this.ranges;
	}

	@Override
	public @NonNull List<Ipv6Network> toNetworks() {
		if (this.isEmpty()) {
			return List.of();
		}

		List<Ipv6Network> result = new ArrayList<>();
		for (Ipv6Range range : this.ranges) {
			Ipv6Address start = range.start();
			Ipv6Address end = range.end();

			int prefixLength = calculateMinimalPrefixLength(start, end);
			Ipv6Network network = Ipv6Network.of(start, prefixLength);
			result.add(network);
		}
		return result;
	}

	@Override
	public @NonNull List<Ipv6Network> toExactNetworks() {
		if (this.isEmpty()) {
			return List.of();
		}

		List<Ipv6Network> result = new ArrayList<>();
		for (Ipv6Range range : this.ranges) {
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
	private static int calculateMinimalPrefixLength(@NonNull Ipv6Address start, @NonNull Ipv6Address end) {
		long highXor = start.highBits() ^ end.highBits();
		long lowXor = start.lowBits() ^ end.lowBits();

		if (highXor == 0 && lowXor == 0) {
			return 128;
		}
		
		if (highXor == 0) {
			return 64 + Long.numberOfLeadingZeros(lowXor);
		} else {
			return Long.numberOfLeadingZeros(highXor);
		}
	}

	@Override
	public @NonNull Iterator<Ipv6Range> rangeIterator() {
		return this.ranges.iterator();
	}

	@Override
	public @NonNull Stream<Ipv6Range> rangeStream() {
		return this.ranges.stream();
	}

	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Ipv6Set ipv6Set)) return false;
		
		return this.ranges.equals(ipv6Set.ranges);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.ranges);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public @NonNull String toString() {
		if (this.isEmpty()) {
			return "Ipv6Set[]";
		}
		
		StringBuilder sb = new StringBuilder("Ipv6Set[");
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
