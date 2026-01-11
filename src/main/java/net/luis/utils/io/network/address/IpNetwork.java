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

import net.luis.utils.io.network.address.ipv4.Ipv4Network;
import net.luis.utils.io.network.address.ipv6.Ipv6Network;
import org.jspecify.annotations.NonNull;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a network defined by CIDR notation (address/prefix).<br>
 * This sealed interface provides operations for working with IP network blocks, including containment checks, subnet calculations, and address iteration.<br>
 * <p>
 *     A network consists of a network address and a prefix length that defines the number of bits in the network portion.<br>
 *     For example, 192.168.1.0/24 represents a network where the first 24 bits are fixed and the last 8 bits can vary (256 addresses total).
 * </p>
 * <p>
 *     <b>Warning:</b> For large networks (e.g., /8 for IPv4, /64 for IPv6), iterating over all addresses may consume significant memory and time.<br>
 *     Consider using {@link #addressStream()} with a limit, or check {@link #size()} before iterating.<br>
 *     For example, a /24 network contains 256 addresses, while a /8 contains over 16 million.
 * </p>
 *
 * @see IpAddress
 * @see IpRange
 * @see Ipv4Network
 * @see Ipv6Network
 *
 * @author Luis-St
 *
 * @param <A> The address type (Ipv4Address or Ipv6Address)
 * @param <N> The network type (self-referential for type safety)
 */
public sealed interface IpNetwork<A extends IpAddress<A>, N extends IpNetwork<A, N>> extends Iterable<A>, Comparable<N> permits Ipv4Network, Ipv6Network {

	/**
	 * Returns the network address, which is the first address in the network.<br>
	 * For example, in 192.168.1.0/24, the network address is 192.168.1.0.<br>
	 *
	 * @return The network address
	 */
	@NonNull A networkAddress();

	/**
	 * Returns the broadcast address, which is the last address in the network.<br>
	 * For example, in 192.168.1.0/24, the broadcast address is 192.168.1.255.<br>
	 *
	 * @return The broadcast address
	 * @throws UnsupportedOperationException If the address type does not support broadcast addresses (e.g., IPv6)
	 */
	@NonNull A broadcastAddress();

	/**
	 * Returns the CIDR prefix length of this network.<br>
	 * <p>
	 *     The prefix length specifies how many bits of the address are fixed (the network portion).<br>
	 *     For example, /24 means the first 24 bits are the network portion.
	 * </p>
	 *
	 * @return The prefix length (0 to address bit length)
	 */
	int prefixLength();

	/**
	 * Returns the total number of addresses in this network.<br>
	 * <p>
	 *     This is calculated as 2^(bitLength - prefixLength).<br>
	 *     For example, a /24 IPv4 network contains 2^(32-24) = 256 addresses.
	 * </p>
	 *
	 * @return The number of addresses in the network
	 */
	@NonNull BigInteger size();

	/**
	 * Checks if the given address is contained within this network.<br>
	 *
	 * @param address The address to check
	 * @return {@code true} if the address is within this network, {@code false} otherwise
	 * @throws NullPointerException If the address is null
	 */
	boolean contains(@NonNull A address);

	/**
	 * Checks if this network completely contains another network.<br>
	 * A network contains another if the other network's prefix is longer (or equal) and the other network's address falls within this network's range.<br>
	 *
	 * @param other The other network to check
	 * @return {@code true} if this network contains the other network, {@code false} otherwise
	 * @throws NullPointerException If the other network is null
	 */
	boolean contains(@NonNull N other);

	/**
	 * Checks if this network overlaps with another network.<br>
	 * Two networks overlap if they share at least one common address.<br>
	 *
	 * @param other The other network to check
	 * @return {@code true} if the networks overlap, {@code false} otherwise
	 * @throws NullPointerException If the other network is null
	 */
	boolean overlaps(@NonNull N other);

	/**
	 * Splits this network into two subnets with prefix length + 1.<br>
	 * For example, splitting 192.168.0.0/24 produces:<br>
	 * <ul>
	 *     <li>192.168.0.0/25</li>
	 *     <li>192.168.0.128/25</li>
	 * </ul>
	 *
	 * @return A list containing exactly two subnets
	 * @throws IllegalStateException If the network cannot be split (prefix is already at maximum)
	 */
	@NonNull List<N> split();

	/**
	 * Returns the supernet (parent network) with prefix length - 1.<br>
	 * For example, the supernet of 192.168.1.0/24 is 192.168.0.0/23.<br>
	 *
	 * @return An optional containing the supernet, or empty if prefix is 0
	 */
	@NonNull Optional<N> supernet();

	/**
	 * Returns all subnets of this network with the specified prefix length.<br>
	 *
	 * @param newPrefixLength The prefix length for the subnets (must be greater than current prefix)
	 * @return A list of all subnets with the given prefix length
	 * @throws IllegalArgumentException If the new prefix length is not greater than the current prefix length or exceeds the maximum prefix length for the address type
	 */
	@NonNull List<N> subnets(int newPrefixLength);

	/**
	 * Returns an iterator over all addresses in this network.<br>
	 * <p>
	 *     <b>Warning:</b> For large networks, this may iterate over millions or billions of addresses.<br>
	 *     Check {@link #size()} before iterating, or use {@link #addressStream()} with a limit.
	 * </p>
	 *
	 * @return An iterator over all addresses in the network
	 */
	@Override
	@NonNull Iterator<A> iterator();

	/**
	 * Returns a stream of all addresses in this network.<br>
	 * <p>
	 *     <b>Warning:</b> For large networks, consider using {@code .limit()} to avoid processing an excessive number of addresses.
	 * </p>
	 *
	 * @return A stream of all addresses in the network
	 */
	@NonNull Stream<A> addressStream();

	/**
	 * Returns an iterator over usable host addresses in this network.<br>
	 * <p>
	 *     For IPv4 networks with prefix &lt; 31, this excludes the network and broadcast addresses.<br>
	 *     For /31 and /32 networks (and all IPv6 networks), all addresses are considered usable hosts.
	 * </p>
	 *
	 * @return An iterator over usable host addresses
	 * @throws UnsupportedOperationException If the address type does not support broadcast addresses (e.g., IPv6)
	 */
	@NonNull Iterator<A> hostIterator();

	/**
	 * Returns a stream of usable host addresses in this network.<br>
	 * <p>
	 *     For IPv4 networks with prefix &lt; 31, this excludes the network and broadcast addresses.<br>
	 *     For /31 and /32 networks (and all IPv6 networks), all addresses are considered usable hosts.
	 * </p>
	 *
	 * @return A stream of usable host addresses
	 * @throws UnsupportedOperationException If the address type does not support broadcast addresses (e.g., IPv6)
	 */
	@NonNull Stream<A> hostStream();

	/**
	 * Returns the number of usable host addresses in this network.<br>
	 * <p>
	 *     For IPv4 networks with prefix &lt; 31, this is {@link #size()} - 2 (excluding network and broadcast addresses).<br>
	 *     For /31 and /32 networks (and all IPv6 networks), this equals {@link #size()}.
	 * </p>
	 *
	 * @return The number of usable host addresses
	 * @throws UnsupportedOperationException If the address type does not support broadcast addresses (e.g., IPv6)
	 */
	@NonNull BigInteger hostCount();

	/**
	 * Checks if this network is in canonical form.<br>
	 * <p>
	 *     A network is canonical if no host bits are set (i.e., the address equals the network address).<br>
	 *     For example, 192.168.1.0/24 is canonical, but 192.168.1.100/24 is not.
	 * </p>
	 *
	 * @return {@code true} if the network is in canonical form, {@code false} otherwise
	 */
	boolean isCanonical();

	/**
	 * Returns this network in canonical form with all host bits cleared.<br>
	 * <p>
	 *     If already canonical, returns this network.<br>
	 *     For example, 192.168.1.100/24 becomes 192.168.1.0/24.
	 * </p>
	 *
	 * @return The canonical form of this network
	 */
	@NonNull N toCanonical();

	/**
	 * Returns the CIDR notation representation of this network.<br>
	 * The format is "address/prefix", e.g., "192.168.1.0/24" or "2001:db8::/32".<br>
	 *
	 * @return The CIDR notation string
	 */
	@NonNull String toCidrNotation();
}
