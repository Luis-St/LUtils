package net.luis.ip;

import java.util.List;
import java.util.Optional;

/**
 * Factory and utility methods for creating and parsing IP addresses, networks, and ranges.
 * This class provides the main entry points for the IP address library.
 */
public interface IpAddresses {

    // ==================== IPv4 Address Parsing ====================

    /**
     * Parses an IPv4 address from a string.
     *
     * @param address the address string (e.g., "192.168.1.1")
     * @return the parsed address
     * @throws IllegalArgumentException if the string is not a valid IPv4 address
     */
    static Ipv4Address parseIpv4(String address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Attempts to parse an IPv4 address from a string.
     *
     * @param address the address string
     * @return the parsed address, or empty if invalid
     */
    static Optional<Ipv4Address> tryParseIpv4(String address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv4 address from four octets.
     *
     * @param a first octet (0-255)
     * @param b second octet (0-255)
     * @param c third octet (0-255)
     * @param d fourth octet (0-255)
     * @return the address
     * @throws IllegalArgumentException if any octet is out of range
     */
    static Ipv4Address ipv4(int a, int b, int c, int d) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv4 address from a 32-bit integer value.
     *
     * @param value the address as an unsigned 32-bit value
     * @return the address
     */
    static Ipv4Address ipv4(long value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv4 address from raw bytes.
     *
     * @param bytes the address bytes (must be exactly 4 bytes)
     * @return the address
     * @throws IllegalArgumentException if bytes length is not 4
     */
    static Ipv4Address ipv4(byte[] bytes) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // ==================== IPv6 Address Parsing ====================

    /**
     * Parses an IPv6 address from a string.
     *
     * @param address the address string (e.g., "2001:db8::1" or "::ffff:192.168.1.1")
     * @return the parsed address
     * @throws IllegalArgumentException if the string is not a valid IPv6 address
     */
    static Ipv6Address parseIpv6(String address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Attempts to parse an IPv6 address from a string.
     *
     * @param address the address string
     * @return the parsed address, or empty if invalid
     */
    static Optional<Ipv6Address> tryParseIpv6(String address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv6 address from eight hextets.
     *
     * @param h0 first hextet (0-65535)
     * @param h1 second hextet
     * @param h2 third hextet
     * @param h3 fourth hextet
     * @param h4 fifth hextet
     * @param h5 sixth hextet
     * @param h6 seventh hextet
     * @param h7 eighth hextet
     * @return the address
     * @throws IllegalArgumentException if any hextet is out of range
     */
    static Ipv6Address ipv6(int h0, int h1, int h2, int h3, int h4, int h5, int h6, int h7) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv6 address from high and low 64-bit values.
     *
     * @param highBits the high 64 bits
     * @param lowBits the low 64 bits
     * @return the address
     */
    static Ipv6Address ipv6(long highBits, long lowBits) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv6 address from raw bytes.
     *
     * @param bytes the address bytes (must be exactly 16 bytes)
     * @return the address
     * @throws IllegalArgumentException if bytes length is not 16
     */
    static Ipv6Address ipv6(byte[] bytes) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // ==================== Generic Address Parsing ====================

    /**
     * Parses an IP address from a string (auto-detects IPv4 or IPv6).
     *
     * @param address the address string
     * @return the parsed address (either Ipv4Address or Ipv6Address)
     * @throws IllegalArgumentException if the string is not a valid IP address
     */
    static IpAddress<?> parse(String address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Attempts to parse an IP address from a string.
     *
     * @param address the address string
     * @return the parsed address, or empty if invalid
     */
    static Optional<? extends IpAddress<?>> tryParse(String address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // ==================== Network Parsing ====================

    /**
     * Parses an IPv4 network in CIDR notation.
     *
     * @param cidr the CIDR string (e.g., "192.168.1.0/24")
     * @return the parsed network
     * @throws IllegalArgumentException if the string is not valid CIDR notation
     */
    static Ipv4Network parseIpv4Network(String cidr) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv4 network from an address and prefix length.
     *
     * @param address the network address
     * @param prefixLength the prefix length (0-32)
     * @return the network
     * @throws IllegalArgumentException if prefix length is out of range
     */
    static Ipv4Network ipv4Network(Ipv4Address address, int prefixLength) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv4 network from an address and subnet mask.
     *
     * @param address the network address
     * @param mask the subnet mask
     * @return the network
     */
    static Ipv4Network ipv4Network(Ipv4Address address, Ipv4SubnetMask mask) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Parses an IPv6 network in CIDR notation.
     *
     * @param cidr the CIDR string (e.g., "2001:db8::/32")
     * @return the parsed network
     * @throws IllegalArgumentException if the string is not valid CIDR notation
     */
    static Ipv6Network parseIpv6Network(String cidr) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv6 network from an address and prefix length.
     *
     * @param address the network address
     * @param prefixLength the prefix length (0-128)
     * @return the network
     * @throws IllegalArgumentException if prefix length is out of range
     */
    static Ipv6Network ipv6Network(Ipv6Address address, int prefixLength) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // ==================== Range Creation ====================

    /**
     * Creates an IPv4 range from start and end addresses.
     *
     * @param start the first address (inclusive)
     * @param end the last address (inclusive)
     * @return the range
     * @throws IllegalArgumentException if start is greater than end
     */
    static Ipv4Range ipv4Range(Ipv4Address start, Ipv4Address end) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Parses an IPv4 range from a string (supports "start-end" and CIDR notation).
     *
     * @param range the range string
     * @return the parsed range
     * @throws IllegalArgumentException if the string is not a valid range
     */
    static Ipv4Range parseIpv4Range(String range) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Creates an IPv6 range from start and end addresses.
     *
     * @param start the first address (inclusive)
     * @param end the last address (inclusive)
     * @return the range
     * @throws IllegalArgumentException if start is greater than end
     */
    static Ipv6Range ipv6Range(Ipv6Address start, Ipv6Address end) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Parses an IPv6 range from a string (supports "start-end" and CIDR notation).
     *
     * @param range the range string
     * @return the parsed range
     * @throws IllegalArgumentException if the string is not a valid range
     */
    static Ipv6Range parseIpv6Range(String range) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // ==================== Subnet Mask ====================

    /**
     * Creates a subnet mask from a prefix length.
     *
     * @param prefixLength the prefix length (0-32)
     * @return the subnet mask
     */
    static Ipv4SubnetMask subnetMask(int prefixLength) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Parses a subnet mask from dotted decimal notation.
     *
     * @param mask the mask string (e.g., "255.255.255.0")
     * @return the subnet mask
     * @throws IllegalArgumentException if the string is not a valid subnet mask
     */
    static Ipv4SubnetMask parseSubnetMask(String mask) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // ==================== Aggregation Utilities ====================

    /**
     * Aggregates a list of IPv4 networks into the minimal equivalent set.
     *
     * @param networks the networks to aggregate
     * @return the aggregated networks
     */
    static List<Ipv4Network> aggregateIpv4(List<Ipv4Network> networks) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Aggregates a list of IPv6 networks into the minimal equivalent set.
     *
     * @param networks the networks to aggregate
     * @return the aggregated networks
     */
    static List<Ipv6Network> aggregateIpv6(List<Ipv6Network> networks) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // ==================== Well-Known Addresses ====================

    /**
     * Returns the IPv4 loopback address (127.0.0.1).
     */
    static Ipv4Address ipv4Loopback() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns the IPv4 broadcast address (255.255.255.255).
     */
    static Ipv4Address ipv4Broadcast() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns the IPv4 unspecified address (0.0.0.0).
     */
    static Ipv4Address ipv4Any() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns the IPv6 loopback address (::1).
     */
    static Ipv6Address ipv6Loopback() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Returns the IPv6 unspecified address (::).
     */
    static Ipv6Address ipv6Any() {
        throw new UnsupportedOperationException("Not implemented");
    }

    // ==================== Validation ====================

    /**
     * Checks if a string is a valid IPv4 address.
     *
     * @param address the string to check
     * @return true if valid
     */
    static boolean isValidIpv4(String address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Checks if a string is a valid IPv6 address.
     *
     * @param address the string to check
     * @return true if valid
     */
    static boolean isValidIpv6(String address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Checks if a string is a valid IP address (IPv4 or IPv6).
     *
     * @param address the string to check
     * @return true if valid
     */
    static boolean isValid(String address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Checks if a string is valid CIDR notation.
     *
     * @param cidr the string to check
     * @return true if valid
     */
    static boolean isValidCidr(String cidr) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
