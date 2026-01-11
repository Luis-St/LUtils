package net.luis.ip;

/**
 * Represents an IPv6 network in CIDR notation.
 * Implementations must be immutable.
 */
public interface Ipv6Network extends IpNetwork<Ipv6Address, Ipv6Network> {

    /**
     * Maximum prefix length for IPv6 (128).
     */
    int MAX_PREFIX_LENGTH = 128;

    /**
     * Common prefix length for a /64 subnet (standard allocation for end sites).
     */
    int STANDARD_SUBNET_PREFIX = 64;

    /**
     * Returns the prefix as raw bytes.
     */
    byte[] prefixBytes();

    /**
     * Checks if this is a /64 network (standard subnet size).
     */
    default boolean isStandardSubnet() {
        return prefixLength() == STANDARD_SUBNET_PREFIX;
    }

    /**
     * Checks if this network is in the unique local address range (fc00::/7).
     */
    boolean isUniqueLocal();

    /**
     * Checks if this network is in the link-local range (fe80::/10).
     */
    boolean isLinkLocal();

    /**
     * Checks if this network is in the multicast range (ff00::/8).
     */
    boolean isMulticast();

    /**
     * Checks if this network is in the 6to4 range (2002::/16).
     */
    boolean is6to4();

    /**
     * Checks if this network is in the Teredo range (2001:0000::/32).
     */
    boolean isTeredo();

    /**
     * Checks if this network is in the documentation range.
     * (2001:db8::/32)
     */
    boolean isDocumentation();

    /**
     * Returns the network in compressed string notation.
     */
    String toCompressedString();

    /**
     * Returns the network in expanded string notation.
     */
    String toExpandedString();
}
