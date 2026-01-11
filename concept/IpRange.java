package net.luis.ip;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents an arbitrary range of IP addresses (not necessarily CIDR-aligned).
 * Implementations must be immutable.
 *
 * @param <A> the address type
 * @param <R> the concrete range type for self-referential operations
 * @param <N> the corresponding network type
 */
public interface IpRange<A extends IpAddress<A>, R extends IpRange<A, R, N>, N extends IpNetwork<A, N>>
        extends Comparable<R>, Iterable<A> {

    /**
     * Returns the first (lowest) address in this range.
     */
    A start();

    /**
     * Returns the last (highest) address in this range.
     */
    A end();

    /**
     * Returns the total number of addresses in this range.
     * Returns -1 if the count exceeds Long.MAX_VALUE.
     */
    long size();

    /**
     * Checks if the given address is contained within this range.
     *
     * @param address the address to check
     * @return true if the address is within this range
     */
    boolean contains(A address);

    /**
     * Checks if this range fully contains another range.
     *
     * @param other the other range
     * @return true if this range contains the other
     */
    boolean contains(R other);

    /**
     * Checks if this range fully contains the given network.
     *
     * @param network the network to check
     * @return true if this range contains the network
     */
    boolean contains(N network);

    /**
     * Checks if this range overlaps with another range.
     *
     * @param other the other range
     * @return true if there is any overlap
     */
    boolean overlaps(R other);

    /**
     * Checks if this range is adjacent to another (no gap, no overlap).
     *
     * @param other the other range
     * @return true if the ranges are adjacent
     */
    boolean isAdjacentTo(R other);

    /**
     * Checks if this range can be represented as a single CIDR network.
     */
    boolean isCidrAligned();

    /**
     * Returns the intersection of this range with another.
     *
     * @param other the other range
     * @return the intersection, or empty if no overlap
     */
    Optional<R> intersection(R other);

    /**
     * Returns the union of this range with another.
     * Only succeeds if the ranges overlap or are adjacent.
     *
     * @param other the other range
     * @return the merged range, or empty if there would be a gap
     */
    Optional<R> union(R other);

    /**
     * Subtracts another range from this range.
     *
     * @param other the range to subtract
     * @return list of remaining ranges (0, 1, or 2 ranges)
     */
    List<R> subtract(R other);

    /**
     * Converts this range to the minimal list of CIDR networks that cover exactly this range.
     *
     * @return list of CIDR networks
     */
    List<N> toCidrNetworks();

    /**
     * Returns a stream of all addresses in this range.
     */
    Stream<A> addressStream();

    /**
     * Returns an iterator over all addresses in this range.
     */
    @Override
    Iterator<A> iterator();

    /**
     * Returns the string representation (e.g., "192.168.1.0-192.168.1.255").
     */
    @Override
    String toString();
}
