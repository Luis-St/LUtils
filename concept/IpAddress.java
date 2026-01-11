package net.luis.ip;

import java.util.Optional;

/**
 * Base interface for all IP addresses (IPv4 and IPv6).
 * Implementations must be immutable and thread-safe.
 *
 * @param <T> the concrete IP address type for self-referential operations
 */
public interface IpAddress<T extends IpAddress<T>> extends Comparable<T> {

    /**
     * Returns the IP version (4 or 6).
     */
    int version();

    /**
     * Returns the number of bits in this address type (32 for IPv4, 128 for IPv6).
     */
    int bitLength();

    /**
     * Returns the raw bytes of this address in network byte order (big-endian).
     */
    byte[] toBytes();

    /**
     * Returns the bit at the specified index (0 = most significant bit).
     *
     * @param index the bit index (0 to bitLength() - 1)
     * @return true if the bit is set, false otherwise
     * @throws IndexOutOfBoundsException if index is out of range
     */
    boolean getBit(int index);

    /**
     * Returns a new address with the bit at the specified index set to the given value.
     *
     * @param index the bit index (0 to bitLength() - 1)
     * @param value the bit value to set
     * @return a new address with the modified bit
     */
    T withBit(int index, boolean value);

    /**
     * Returns the next address in sequence.
     *
     * @return the next address, or empty if this is the maximum address
     */
    Optional<T> next();

    /**
     * Returns the previous address in sequence.
     *
     * @return the previous address, or empty if this is the minimum address
     */
    Optional<T> previous();

    /**
     * Adds the given offset to this address.
     *
     * @param offset the offset to add (can be negative)
     * @return the resulting address, or empty if overflow/underflow occurs
     */
    Optional<T> add(long offset);

    /**
     * Calculates the distance from this address to another.
     * Returns a positive value if other is greater, negative if smaller.
     *
     * @param other the other address
     * @return the distance, or empty if the distance exceeds Long.MAX_VALUE
     */
    Optional<Long> distanceTo(T other);

    /**
     * Returns the number of leading bits this address shares with another.
     *
     * @param other the other address
     * @return the common prefix length
     */
    int commonPrefixLength(T other);

    /**
     * Checks if this address is the unspecified/any address (0.0.0.0 or ::).
     */
    boolean isUnspecified();

    /**
     * Checks if this address is a loopback address.
     */
    boolean isLoopback();

    /**
     * Checks if this address is a multicast address.
     */
    boolean isMulticast();

    /**
     * Checks if this address is a link-local address.
     */
    boolean isLinkLocal();

    /**
     * Checks if this address is a private/site-local address.
     */
    boolean isPrivate();

    /**
     * Checks if this address is reserved for documentation purposes.
     */
    boolean isDocumentation();

    /**
     * Checks if this address is globally routable.
     */
    boolean isGlobalUnicast();

    /**
     * Returns the address type classification.
     */
    AddressType getAddressType();

    /**
     * Returns the canonical string representation of this address.
     */
    @Override
    String toString();

    /**
     * Returns the string representation with configurable formatting options.
     *
     * @param options the formatting options
     * @return the formatted string
     */
    String toString(FormatOptions options);

    /**
     * Returns the reverse DNS pointer name for this address.
     * (e.g., "1.0.168.192.in-addr.arpa" or "...ip6.arpa")
     */
    String toReverseDnsName();

    /**
     * Returns the minimum address for this address type.
     */
    T minValue();

    /**
     * Returns the maximum address for this address type.
     */
    T maxValue();
}
