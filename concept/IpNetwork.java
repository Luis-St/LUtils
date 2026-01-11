package net.luis.ip;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents an IP network in CIDR notation (address + prefix length).
 * Implementations must be immutable.
 *
 * @param <A> the address type
 * @param <N> the concrete network type for self-referential operations
 */
public interface IpNetwork<A extends IpAddress<A>, N extends IpNetwork<A, N>> extends Comparable<N>, Iterable<A> {

    /**
     * Returns the network address (the base address with host bits zeroed).
     */
    A networkAddress();

    /**
     * Returns the prefix length (number of network bits).
     */
    int prefixLength();

    /**
     * Returns the number of host bits (bitLength - prefixLength).
     */
    default int hostBits() {
        return networkAddress().bitLength() - prefixLength();
    }

    /**
     * Returns the total number of addresses in this network.
     * Returns -1 if the count exceeds Long.MAX_VALUE.
     */
    long size();

    /**
     * Returns the first usable host address in this network.
     * For networks with only 1 or 2 addresses, returns the network address.
     */
    A firstHost();

    /**
     * Returns the last usable host address in this network.
     * For networks with only 1 or 2 addresses, returns the last address.
     */
    A lastHost();

    /**
     * Returns the last address in this network (broadcast for IPv4, or just the last address for IPv6).
     */
    A lastAddress();

    /**
     * Checks if the given address is contained within this network.
     *
     * @param address the address to check
     * @return true if the address is within this network
     */
    boolean contains(A address);

    /**
     * Checks if this network fully contains another network.
     *
     * @param other the other network
     * @return true if this network contains the other
     */
    boolean contains(N other);

    /**
     * Checks if this network overlaps with another network.
     *
     * @param other the other network
     * @return true if there is any overlap
     */
    boolean overlaps(N other);

    /**
     * Checks if this network is adjacent to another (no gap, no overlap).
     *
     * @param other the other network
     * @return true if the networks are adjacent
     */
    boolean isAdjacentTo(N other);

    /**
     * Returns the supernet containing this network with the given prefix length.
     *
     * @param newPrefixLength the new prefix length (must be less than current)
     * @return the supernet
     * @throws IllegalArgumentException if newPrefixLength >= current prefix length
     */
    N supernet(int newPrefixLength);

    /**
     * Splits this network into two equal subnets.
     *
     * @return list of two subnets, or empty if prefix length is already maximum
     */
    Optional<List<N>> split();

    /**
     * Splits this network into subnets of the given prefix length.
     *
     * @param newPrefixLength the prefix length for subnets
     * @return list of subnets
     * @throws IllegalArgumentException if newPrefixLength <= current prefix length
     */
    List<N> subnets(int newPrefixLength);

    /**
     * Returns a stream of all subnets of the given prefix length.
     *
     * @param newPrefixLength the prefix length for subnets
     * @return stream of subnets
     */
    Stream<N> subnetStream(int newPrefixLength);

    /**
     * Excludes another network from this network, returning the remaining parts.
     *
     * @param excluded the network to exclude
     * @return list of networks representing this minus the excluded network
     */
    List<N> exclude(N excluded);

    /**
     * Attempts to merge this network with another into a single supernet.
     * Only succeeds if the two networks are adjacent and can form a valid CIDR block.
     *
     * @param other the other network
     * @return the merged network, or empty if merge is not possible
     */
    Optional<N> merge(N other);

    /**
     * Returns a stream of all addresses in this network.
     */
    Stream<A> addressStream();

    /**
     * Returns an iterator over all addresses in this network.
     */
    @Override
    Iterator<A> iterator();

    /**
     * Returns the CIDR notation string (e.g., "192.168.1.0/24").
     */
    @Override
    String toString();

    /**
     * Checks if this is a host route (/32 for IPv4, /128 for IPv6).
     */
    boolean isHostRoute();

    /**
     * Checks if this is the default route (0.0.0.0/0 or ::/0).
     */
    boolean isDefaultRoute();
}
