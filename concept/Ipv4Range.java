package net.luis.ip;

/**
 * Represents an arbitrary range of IPv4 addresses.
 * Implementations must be immutable.
 */
public interface Ipv4Range extends IpRange<Ipv4Address, Ipv4Range, Ipv4Network> {

    /**
     * Converts this IPv4 range to an IPv4-mapped IPv6 range.
     */
    Ipv6Range toIpv4MappedIpv6Range();
}
