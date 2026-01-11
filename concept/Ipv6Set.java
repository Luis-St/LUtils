package net.luis.ip;

import java.util.Optional;

/**
 * An efficient set of IPv6 addresses.
 * Implementations must be immutable and thread-safe.
 */
public interface Ipv6Set extends IpSet<Ipv6Address, Ipv6Range, Ipv6Network, Ipv6Set> {

    /**
     * Creates an empty IPv6 set.
     */
    static Ipv6Set empty() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv6 set from the given networks.
     *
     * @param networks the networks to include
     * @return a new set containing the networks
     */
    static Ipv6Set of(Ipv6Network... networks) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv6 set from the given ranges.
     *
     * @param ranges the ranges to include
     * @return a new set containing the ranges
     */
    static Ipv6Set of(Ipv6Range... ranges) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv6 set from the given addresses.
     *
     * @param addresses the addresses to include
     * @return a new set containing the addresses
     */
    static Ipv6Set of(Ipv6Address... addresses) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Checks if this set contains only IPv4-mapped addresses.
     *
     * @return true if all addresses are IPv4-mapped
     */
    boolean isIpv4MappedOnly();

    /**
     * Converts this IPv6 set to an IPv4 set if it contains only IPv4-mapped addresses.
     *
     * @return the equivalent IPv4 set, or empty if not all addresses are IPv4-mapped
     */
    Optional<Ipv4Set> toIpv4Set();
}
