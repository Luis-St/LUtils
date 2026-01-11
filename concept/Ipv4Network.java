package net.luis.ip;

/**
 * Represents an IPv4 network in CIDR notation.
 * Implementations must be immutable.
 */
public interface Ipv4Network extends IpNetwork<Ipv4Address, Ipv4Network> {

    /**
     * Maximum prefix length for IPv4 (32).
     */
    int MAX_PREFIX_LENGTH = 32;

    /**
     * Returns the subnet mask for this network.
     */
    Ipv4SubnetMask subnetMask();

    /**
     * Returns the wildcard mask (inverse of subnet mask) for this network.
     */
    Ipv4SubnetMask wildcardMask();

    /**
     * Returns the broadcast address for this network.
     */
    Ipv4Address broadcastAddress();

    /**
     * Returns the number of usable host addresses.
     * For /31 networks (point-to-point links), returns 2.
     * For /32 networks (host routes), returns 1.
     * For all others, returns size - 2 (excluding network and broadcast).
     */
    long usableHosts();

    /**
     * Checks if this network is a point-to-point link (/31).
     */
    default boolean isPointToPoint() {
        return prefixLength() == 31;
    }

    /**
     * Checks if this is one of the RFC 1918 private address ranges.
     * (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16)
     */
    boolean isPrivate();

    /**
     * Converts this IPv4 network to its IPv4-mapped IPv6 equivalent.
     */
    Ipv6Network toIpv4MappedIpv6Network();
}
