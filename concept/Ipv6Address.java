package net.luis.ip;

import java.util.Optional;

/**
 * Represents an IPv6 address (128-bit).
 * Implementations must be immutable.
 */
public interface Ipv6Address extends IpAddress<Ipv6Address> {

    /**
     * The number of bits in an IPv6 address.
     */
    int BIT_LENGTH = 128;

    /**
     * The number of hextets (16-bit groups) in an IPv6 address.
     */
    int HEXTET_COUNT = 8;

    @Override
    default int version() {
        return 6;
    }

    @Override
    default int bitLength() {
        return BIT_LENGTH;
    }

    /**
     * Returns the hextet at the specified index (0-7, where 0 is the most significant).
     *
     * @param index the hextet index (0-7)
     * @return the hextet value (0-65535)
     * @throws IndexOutOfBoundsException if index is out of range
     */
    int getHextet(int index);

    /**
     * Returns a new address with the specified hextet changed.
     *
     * @param index the hextet index (0-7)
     * @param value the new hextet value (0-65535)
     * @return a new address with the modified hextet
     * @throws IllegalArgumentException if value is out of range
     */
    Ipv6Address withHextet(int index, int value);

    /**
     * Returns the high 64 bits of this address (network identifier portion in many allocations).
     */
    long highBits();

    /**
     * Returns the low 64 bits of this address (interface identifier portion in many allocations).
     */
    long lowBits();

    /**
     * Checks if this is an IPv4-mapped IPv6 address (::ffff:x.x.x.x).
     */
    boolean isIpv4Mapped();

    /**
     * Checks if this is an IPv4-compatible IPv6 address (::x.x.x.x).
     * @deprecated IPv4-compatible addresses are deprecated in RFC 4291
     */
    @Deprecated
    boolean isIpv4Compatible();

    /**
     * Checks if this is a 6to4 address (2002::/16).
     */
    boolean is6to4();

    /**
     * Checks if this is a Teredo address (2001:0000::/32).
     */
    boolean isTeredo();

    /**
     * Checks if this is an ISATAP address.
     */
    boolean isIsatap();

    /**
     * Checks if this is a unique local address (fc00::/7).
     */
    boolean isUniqueLocal();

    /**
     * Checks if this is an interface-local multicast address.
     */
    boolean isInterfaceLocalMulticast();

    /**
     * Checks if this is a link-local multicast address.
     */
    boolean isLinkLocalMulticast();

    /**
     * Checks if this is a site-local multicast address.
     */
    boolean isSiteLocalMulticast();

    /**
     * Checks if this is an organization-local multicast address.
     */
    boolean isOrganizationLocalMulticast();

    /**
     * Checks if this is a global multicast address.
     */
    boolean isGlobalMulticast();

    /**
     * Checks if this address appears to be generated using EUI-64 format.
     */
    boolean isEui64();

    /**
     * Checks if this address appears to be a privacy/temporary address (RFC 4941).
     */
    boolean isPrivacyAddress();

    /**
     * Returns the multicast scope if this is a multicast address.
     *
     * @return the scope, or empty if not a multicast address
     */
    Optional<Ipv6MulticastScope> getMulticastScope();

    /**
     * Returns the embedded IPv4 address if this is an IPv4-mapped or IPv4-compatible address.
     *
     * @return the embedded IPv4 address, or empty if not applicable
     */
    Optional<Ipv4Address> toIpv4();

    /**
     * Extracts the embedded IPv4 address from a 6to4 address.
     *
     * @return the embedded IPv4 address, or empty if not a 6to4 address
     */
    Optional<Ipv4Address> extract6to4Address();

    /**
     * Extracts the Teredo server and client addresses.
     *
     * @return the Teredo info, or empty if not a Teredo address
     */
    Optional<TeredoInfo> extractTeredoInfo();

    /**
     * Returns the solicited-node multicast address for this unicast address.
     * Format: ff02::1:ffXX:XXXX where XX:XXXX are the last 24 bits of this address.
     */
    Ipv6Address toSolicitedNodeMulticast();

    /**
     * Returns the string representation in fully expanded form (no :: compression).
     */
    String toExpandedString();

    /**
     * Returns the string representation in compressed form (with :: for longest zero run).
     */
    String toCompressedString();

    /**
     * Returns the string representation in mixed notation for IPv4-mapped addresses.
     * Example: ::ffff:192.168.1.1
     */
    String toMixedString();
}
