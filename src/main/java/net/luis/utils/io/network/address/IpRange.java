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

import net.luis.utils.io.network.address.ipv4.Ipv4Range;
import net.luis.utils.io.network.address.ipv6.Ipv6Range;
import org.jspecify.annotations.NonNull;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a contiguous range of IP addresses from a start to an end address.<br>
 * Unlike {@link IpNetwork}, a range does not need to align to CIDR boundaries.
 * <p>
 *     Ranges are useful for representing arbitrary address spans such as DHCP pools, allocation blocks,<br>
 *     or firewall rules that don't fit neatly into CIDR notation.
 * </p>
 * <p>
 *     <b>Warning:</b> For large ranges, iterating over all addresses may consume significant memory and time.<br>
 *     Check {@link #size()} before iterating or use{@link #addressStream()} with a limit.
 * </p>
 * <pre>{@code
 * Ipv4Range range = IpAddresses.ipv4Range(
 *     IpAddresses.parseIpv4("192.168.1.100"),
 *     IpAddresses.parseIpv4("192.168.1.200")
 * );
 *
 * // Check containment
 * if (range.containsAddress(IpAddresses.parseIpv4("192.168.1.150"))) {
 *     System.out.println("Address is in range");
 * }
 *
 * // Convert to CIDR networks for firewall rules
 * List<Ipv4Network> networks = range.toCidrNetworks();
 * }</pre>
 *
 * @see IpAddress
 * @see IpNetwork
 * @see Ipv4Range
 * @see Ipv6Range
 *
 * @author Luis-St
 *
 * @param <A> The address type
 * @param <R> The range type (self-referential)
 * @param <N> The network type for CIDR decomposition
 */
public sealed interface IpRange<A extends IpAddress<A>, R extends IpRange<A, R, N>, N extends IpNetwork<A, N>> extends Iterable<A>, Comparable<R> permits Ipv4Range, Ipv6Range {
	
	/**
	 * Returns the first address in this range (inclusive).<br>
	 * @return The start address of this range
	 */
	@NonNull A start();
	
	/**
	 * Returns the last address in this range (inclusive).<br>
	 * @return The end address of this range
	 */
	@NonNull A end();
	
	/**
	 * Returns the number of addresses in this range.<br>
	 * @return The number of addresses as a BigInteger
	 */
	@NonNull BigInteger size();
	
	/**
	 * Checks if this range contains the specified address.<br>
	 *
	 * @param address The address to check
	 * @return {@code true} if the address is within this range, {@code false} otherwise
	 * @throws NullPointerException If the address is null
	 */
	boolean containsAddress(@NonNull A address);
	
	/**
	 * Checks if this range completely contains another range.<br>
	 *
	 * @param other The other range to check
	 * @return {@code true} if this range contains all addresses of the other range, {@code false} otherwise
	 * @throws NullPointerException If the other range is null
	 */
	boolean containsRange(@NonNull R other);
	
	/**
	 * Checks if this range overlaps with another range.<br>
	 *
	 * @param other The other range to check
	 * @return {@code true} if there is at least one common address, {@code false} otherwise
	 * @throws NullPointerException If the other range is null
	 */
	boolean overlaps(@NonNull R other);
	
	/**
	 * Returns the intersection of this range with another range.<br>
	 *
	 * @param other The other range to intersect with
	 * @return An optional containing the intersection range, or empty if the ranges do not overlap
	 * @throws NullPointerException If the other range is null
	 */
	@NonNull Optional<R> intersection(@NonNull R other);
	
	/**
	 * Returns the difference between this range and another range.<br>
	 * The result contains all addresses that are in this range but not in the other range.<br>
	 *
	 * @param other The range to subtract from this range
	 * @return A list of 0, 1, or 2 ranges representing the difference
	 * @throws NullPointerException If the other range is null
	 */
	@NonNull List<R> difference(@NonNull R other);
	
	/**
	 * Attempts to merge this range with another range if they are contiguous or overlapping.<br>
	 *
	 * @param other The other range to merge with
	 * @return An optional containing the merged range, or empty if the ranges cannot be merged
	 * @throws NullPointerException If the other range is null
	 */
	@NonNull Optional<R> merge(@NonNull R other);
	
	/**
	 * Returns an iterator over all addresses in this range.<br>
	 * <p>
	 *     <b>Warning:</b> For large ranges, this may consume significant memory and time.<br>
	 *     Check {@link #size()} before iterating or use {@link #addressStream()} with a limit.
	 * </p>
	 *
	 * @return An iterator over all addresses in this range
	 */
	@Override
	@NonNull Iterator<A> iterator();
	
	/**
	 * Returns a stream of all addresses in this range.<br>
	 * <p>
	 *     <b>Warning:</b> For large ranges, this may consume significant memory and time.<br>
	 *     Check {@link #size()} before streaming or use a limit on the stream.
	 * </p>
	 *
	 * @return A stream of all addresses in this range
	 */
	@NonNull Stream<A> addressStream();
	
	/**
	 * Decomposes this range into a minimal set of CIDR networks that cover all addresses.<br>
	 * @return A list of CIDR networks that together cover exactly this range
	 */
	@NonNull List<N> toCidrNetworks();
	
	/**
	 * Checks if this range contains only a single address.<br>
	 * @return {@code true} if start equals end, {@code false} otherwise
	 */
	boolean isSingleAddress();
	
	/**
	 * Checks if this range can be represented as a single CIDR network.<br>
	 * @return {@code true} if this range aligns to CIDR boundaries, {@code false} otherwise
	 */
	boolean isNetwork();
	
	/**
	 * Converts this range to a network if it can be represented as a single CIDR block.<br>
	 * @return An optional containing the network, or empty if the range does not align to CIDR boundaries
	 */
	@NonNull Optional<N> toNetwork();
}
