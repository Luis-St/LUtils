package net.luis.ip;

import java.util.Optional;

/**
 * Represents an arbitrary range of IPv6 addresses.
 * Implementations must be immutable.
 */
public interface Ipv6Range extends IpRange<Ipv6Address, Ipv6Range, Ipv6Network> {

    /**
     * If this range represents IPv4-mapped addresses only, extracts the IPv4 range.
     *
     * @return the IPv4 range, or empty if not entirely IPv4-mapped
     */
    Optional<Ipv4Range> toIpv4Range();

    /**
     * Checks if this entire range consists of IPv4-mapped addresses.
     */
    boolean isIpv4MappedRange();
}
