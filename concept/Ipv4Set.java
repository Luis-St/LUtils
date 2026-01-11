package net.luis.ip;

/**
 * An efficient set of IPv4 addresses.
 * Implementations must be immutable and thread-safe.
 */
public interface Ipv4Set extends IpSet<Ipv4Address, Ipv4Range, Ipv4Network, Ipv4Set> {

    /**
     * Creates an empty IPv4 set.
     */
    static Ipv4Set empty() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv4 set from the given networks.
     *
     * @param networks the networks to include
     * @return a new set containing the networks
     */
    static Ipv4Set of(Ipv4Network... networks) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv4 set from the given ranges.
     *
     * @param ranges the ranges to include
     * @return a new set containing the ranges
     */
    static Ipv4Set of(Ipv4Range... ranges) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv4 set from the given addresses.
     *
     * @param addresses the addresses to include
     * @return a new set containing the addresses
     */
    static Ipv4Set of(Ipv4Address... addresses) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Converts this IPv4 set to an IPv6 set using IPv4-mapped addresses.
     *
     * @return the equivalent IPv6 set
     */
    Ipv6Set toIpv4MappedIpv6Set();
}
