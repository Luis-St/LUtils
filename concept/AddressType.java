package net.luis.ip;

/**
 * Classification of IP address types.
 */
public enum AddressType {

    /**
     * Unspecified address (0.0.0.0 or ::).
     */
    UNSPECIFIED,

    /**
     * Loopback address (127.0.0.0/8 or ::1).
     */
    LOOPBACK,

    /**
     * Private/site-local unicast address (RFC 1918 for IPv4, deprecated for IPv6).
     */
    PRIVATE,

    /**
     * Link-local unicast address (169.254.0.0/16 or fe80::/10).
     */
    LINK_LOCAL,

    /**
     * Unique local address (IPv6 fc00::/7 only).
     */
    UNIQUE_LOCAL,

    /**
     * Multicast address (224.0.0.0/4 or ff00::/8).
     */
    MULTICAST,

    /**
     * Broadcast address (IPv4 only, 255.255.255.255).
     */
    BROADCAST,

    /**
     * Documentation/example address (192.0.2.0/24, 2001:db8::/32, etc.).
     */
    DOCUMENTATION,

    /**
     * Reserved for future use (IPv4 240.0.0.0/4).
     */
    RESERVED,

    /**
     * Shared address space for carrier-grade NAT (100.64.0.0/10).
     */
    SHARED,

    /**
     * Global unicast address (publicly routable).
     */
    GLOBAL_UNICAST,

    /**
     * IPv4-mapped IPv6 address (::ffff:0:0/96).
     */
    IPV4_MAPPED,

    /**
     * 6to4 transition address (2002::/16).
     */
    TRANSITION_6TO4,

    /**
     * Teredo transition address (2001:0000::/32).
     */
    TRANSITION_TEREDO,

    /**
     * Unknown or unclassified address type.
     */
    UNKNOWN
}
