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

import net.luis.utils.io.network.address.ipv4.Ipv4Set;
import net.luis.utils.io.network.address.ipv6.Ipv6Set;
import org.jspecify.annotations.NonNull;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a set of IP addresses that can contain multiple disjoint ranges and networks.<br>
 * This interface provides efficient set operations for IP address collections, useful for access control lists, address allocation, and network planning.<br>
 * <p>
 *     Sets are immutable; all modification operations return new instances.<br>
 *     Internally, sets are represented as a minimal collection of non-overlapping ranges for efficient storage and operations.
 * </p>
 * <pre>{@code
 * // Create a set from multiple networks
 * Ipv4Set allowed = Ipv4Set.of(
 *     IpAddresses.parseIpv4Network("10.0.0.0/8"),
 *     IpAddresses.parseIpv4Network("192.168.0.0/16")
 * );
 *
 * // Subtract a specific subnet
 * Ipv4Set blocked = Ipv4Set.of(
 *     IpAddresses.parseIpv4Network("10.0.0.0/24")
 * );
 *
 * Ipv4Set effective = allowed.difference(blocked);
 *
 * // Check if an address is allowed
 * if (effective.containsAddress(IpAddresses.parseIpv4("10.0.1.1"))) {
 *     System.out.println("Address is allowed");
 * }
 * }</pre>
 *
 * @author Luis-St
 *
 * @param <A> The address type
 * @param <R> The range type
 * @param <N> The network type
 * @param <S> The set type (self-referential)
 */
public sealed interface IpSet<A extends IpAddress<A>, R extends IpRange<A, R, N>, N extends IpNetwork<A, N>, S extends IpSet<A, R, N, S>> permits Ipv4Set, Ipv6Set {

	/**
	 * Checks if this set is empty.<br>
	 * @return {@code true} if this set contains no addresses, {@code false} otherwise
	 */
	boolean isEmpty();

	/**
	 * Returns the total number of addresses in this set.<br>
	 * @return The total number of addresses
	 */
	@NonNull BigInteger size();

	/**
	 * Checks if this set contains the specified address.<br>
	 *
	 * @param address The address to check
	 * @return {@code true} if this set contains the address, {@code false} otherwise
	 * @throws NullPointerException If the address is null
	 */
	boolean containsAddress(@NonNull A address);

	/**
	 * Checks if this set contains all addresses in the specified network.<br>
	 *
	 * @param network The network to check
	 * @return {@code true} if this set contains all addresses in the network, {@code false} otherwise
	 * @throws NullPointerException If the network is null
	 */
	boolean containsNetwork(@NonNull N network);

	/**
	 * Checks if this set contains all addresses in the specified range.<br>
	 *
	 * @param range The range to check
	 * @return {@code true} if this set contains all addresses in the range, {@code false} otherwise
	 * @throws NullPointerException If the range is null
	 */
	boolean containsRange(@NonNull R range);

	/**
	 * Checks if this set contains all addresses in the specified other set.<br>
	 *
	 * @param other The other set to check
	 * @return {@code true} if this set contains all addresses in the other set, {@code false} otherwise
	 * @throws NullPointerException If the other set is null
	 */
	boolean containsAll(@NonNull S other);

	/**
	 * Returns a new set containing all addresses in this set or the other set.<br>
	 *
	 * @param other The other set to union with
	 * @return A new set representing the union of both sets
	 * @throws NullPointerException If the other set is null
	 */
	@NonNull S union(@NonNull S other);

	/**
	 * Returns a new set containing only addresses present in both this set and the other set.<br>
	 *
	 * @param other The other set to intersect with
	 * @return A new set representing the intersection of both sets
	 * @throws NullPointerException If the other set is null
	 */
	@NonNull S intersection(@NonNull S other);

	/**
	 * Returns a new set containing addresses in this set but not in the other set.<br>
	 *
	 * @param other The other set to subtract
	 * @return A new set representing this set minus the other set
	 * @throws NullPointerException If the other set is null
	 */
	@NonNull S difference(@NonNull S other);

	/**
	 * Returns a new set containing addresses in either set but not in both.<br>
	 * <p>
	 *     This is equivalent to (A &#8746; B) - (A &#8745; B).
	 * </p>
	 *
	 * @param other The other set
	 * @return A new set representing the symmetric difference of both sets
	 * @throws NullPointerException If the other set is null
	 */
	@NonNull S symmetricDifference(@NonNull S other);

	/**
	 * Returns a new set containing all addresses not in this set.<br>
	 * @return A new set representing the complement of this set
	 */
	@NonNull S complement();

	/**
	 * Returns a new set with the specified address added.<br>
	 *
	 * @param address The address to add
	 * @return A new set containing all addresses in this set plus the specified address
	 * @throws NullPointerException If the address is null
	 */
	@NonNull S addAddress(@NonNull A address);

	/**
	 * Returns a new set with all addresses in the specified network added.<br>
	 *
	 * @param network The network to add
	 * @return A new set containing all addresses in this set plus all addresses in the network
	 * @throws NullPointerException If the network is null
	 */
	@NonNull S addNetwork(@NonNull N network);

	/**
	 * Returns a new set with all addresses in the specified range added.<br>
	 *
	 * @param range The range to add
	 * @return A new set containing all addresses in this set plus all addresses in the range
	 * @throws NullPointerException If the range is null
	 */
	@NonNull S addRange(@NonNull R range);

	/**
	 * Returns a new set with the specified address removed.<br>
	 *
	 * @param address The address to remove
	 * @return A new set containing all addresses in this set except the specified address
	 * @throws NullPointerException If the address is null
	 */
	@NonNull S removeAddress(@NonNull A address);

	/**
	 * Returns a new set with all addresses in the specified network removed.<br>
	 *
	 * @param network The network to remove
	 * @return A new set containing all addresses in this set except those in the network
	 * @throws NullPointerException If the network is null
	 */
	@NonNull S removeNetwork(@NonNull N network);

	/**
	 * Returns a new set with all addresses in the specified range removed.<br>
	 *
	 * @param range The range to remove
	 * @return A new set containing all addresses in this set except those in the range
	 * @throws NullPointerException If the range is null
	 */
	@NonNull S removeRange(@NonNull R range);

	/**
	 * Returns a minimal list of ranges that cover all addresses in this set.<br>
	 * <p>
	 *     The returned ranges are non-overlapping and sorted.
	 * </p>
	 *
	 * @return A minimal list of ranges representing this set
	 */
	@NonNull List<R> toRanges();

	/**
	 * Returns a minimal list of CIDR networks that cover all addresses in this set.<br>
	 * <p>
	 *     Note: The networks may cover addresses not in this set if exact CIDR representation is not possible.
	 * </p>
	 *
	 * @return A minimal list of CIDR networks covering this set
	 */
	@NonNull List<N> toNetworks();

	/**
	 * Returns a list of CIDR networks that exactly cover all addresses in this set.<br>
	 * <p>
	 *     Unlike {@link #toNetworks()}, this method guarantees exact coverage using multiple CIDR networks if necessary.
	 * </p>
	 *
	 * @return A list of CIDR networks exactly representing this set
	 */
	@NonNull List<N> toExactNetworks();

	/**
	 * Returns an iterator over the ranges in this set.<br>
	 * @return An iterator over the ranges
	 */
	@NonNull Iterator<R> rangeIterator();

	/**
	 * Returns a stream of the ranges in this set.<br>
	 * @return A stream of the ranges
	 */
	@NonNull Stream<R> rangeStream();
}
