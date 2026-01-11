package net.luis.ip;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * An efficient set of IP addresses, optimized for storing large collections
 * of addresses and performing set operations without expanding to individual addresses.
 * Implementations may use interval trees, tries, or other compressed representations.
 *
 * @param <A> the address type
 * @param <R> the range type
 * @param <N> the network type
 * @param <S> the concrete set type for self-referential operations
 */
public interface IpSet<
        A extends IpAddress<A>,
        R extends IpRange<A, R, N>,
        N extends IpNetwork<A, N>,
        S extends IpSet<A, R, N, S>
> extends Iterable<R> {

    /**
     * Checks if this set is empty.
     */
    boolean isEmpty();

    /**
     * Returns the total number of addresses in this set.
     * Returns -1 if the count exceeds Long.MAX_VALUE.
     */
    long size();

    /**
     * Returns the number of contiguous ranges in this set.
     */
    int rangeCount();

    /**
     * Checks if the given address is contained in this set.
     *
     * @param address the address to check
     * @return true if the address is in this set
     */
    boolean contains(A address);

    /**
     * Checks if this set fully contains the given range.
     *
     * @param range the range to check
     * @return true if the entire range is in this set
     */
    boolean contains(R range);

    /**
     * Checks if this set fully contains the given network.
     *
     * @param network the network to check
     * @return true if the entire network is in this set
     */
    boolean contains(N network);

    /**
     * Checks if this set overlaps with the given range.
     *
     * @param range the range to check
     * @return true if any address is in both this set and the range
     */
    boolean overlaps(R range);

    /**
     * Checks if this set overlaps with another set.
     *
     * @param other the other set
     * @return true if any address is in both sets
     */
    boolean overlaps(S other);

    /**
     * Adds an address to this set.
     *
     * @param address the address to add
     * @return a new set with the address added
     */
    S add(A address);

    /**
     * Adds a range to this set.
     *
     * @param range the range to add
     * @return a new set with the range added
     */
    S add(R range);

    /**
     * Adds a network to this set.
     *
     * @param network the network to add
     * @return a new set with the network added
     */
    S add(N network);

    /**
     * Adds all addresses from another set to this set.
     *
     * @param other the other set
     * @return a new set with all addresses from both sets
     */
    S addAll(S other);

    /**
     * Removes an address from this set.
     *
     * @param address the address to remove
     * @return a new set with the address removed
     */
    S remove(A address);

    /**
     * Removes a range from this set.
     *
     * @param range the range to remove
     * @return a new set with the range removed
     */
    S remove(R range);

    /**
     * Removes a network from this set.
     *
     * @param network the network to remove
     * @return a new set with the network removed
     */
    S remove(N network);

    /**
     * Removes all addresses in another set from this set.
     *
     * @param other the other set
     * @return a new set with those addresses removed
     */
    S removeAll(S other);

    /**
     * Returns the union of this set and another set.
     *
     * @param other the other set
     * @return a new set containing all addresses from both sets
     */
    S union(S other);

    /**
     * Returns the intersection of this set and another set.
     *
     * @param other the other set
     * @return a new set containing only addresses in both sets
     */
    S intersection(S other);

    /**
     * Returns the difference of this set and another set (this - other).
     *
     * @param other the other set
     * @return a new set containing addresses in this but not in other
     */
    S difference(S other);

    /**
     * Returns the symmetric difference of this set and another set.
     *
     * @param other the other set
     * @return a new set containing addresses in exactly one of the sets
     */
    S symmetricDifference(S other);

    /**
     * Returns the complement of this set (all addresses not in this set).
     *
     * @return the complement set
     */
    S complement();

    /**
     * Returns the ranges in this set as an immutable list.
     */
    List<R> toRanges();

    /**
     * Converts this set to the minimal list of CIDR networks.
     */
    List<N> toCidrNetworks();

    /**
     * Returns a stream of all ranges in this set.
     */
    Stream<R> rangeStream();

    /**
     * Returns a stream of all addresses in this set.
     * Warning: This may produce a very large number of elements.
     */
    Stream<A> addressStream();

    /**
     * Returns an iterator over the ranges in this set.
     */
    @Override
    Iterator<R> iterator();

    /**
     * Returns the smallest range that contains all addresses in this set.
     *
     * @return the bounding range, or null if this set is empty
     */
    R boundingRange();

    /**
     * Clears all addresses from this set.
     *
     * @return an empty set
     */
    S clear();
}
