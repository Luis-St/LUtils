package net.luis.ip;

import java.util.Optional;

/**
 * Represents an IPv4 address (32-bit).
 * Implementations must be immutable.
 */
public interface Ipv4Address extends IpAddress<Ipv4Address> {

    /**
     * The number of bits in an IPv4 address.
     */
    int BIT_LENGTH = 32;

    /**
     * The number of octets in an IPv4 address.
     */
    int OCTET_COUNT = 4;

    @Override
    default int version() {
        return 4;
    }

    @Override
    default int bitLength() {
        return BIT_LENGTH;
    }

    /**
     * Returns the address as a 32-bit unsigned integer value.
     */
    long toInt();

    /**
     * Returns the octet at the specified index (0-3, where 0 is the most significant).
     *
     * @param index the octet index (0-3)
     * @return the octet value (0-255)
     * @throws IndexOutOfBoundsException if index is out of range
     */
    int getOctet(int index);

    /**
     * Returns a new address with the specified octet changed.
     *
     * @param index the octet index (0-3)
     * @param value the new octet value (0-255)
     * @return a new address with the modified octet
     * @throws IllegalArgumentException if value is out of range
     */
    Ipv4Address withOctet(int index, int value);

    /**
     * Returns the first octet (most significant byte).
     */
    default int firstOctet() {
        return getOctet(0);
    }

    /**
     * Returns the second octet.
     */
    default int secondOctet() {
        return getOctet(1);
    }

    /**
     * Returns the third octet.
     */
    default int thirdOctet() {
        return getOctet(2);
    }

    /**
     * Returns the fourth octet (least significant byte).
     */
    default int fourthOctet() {
        return getOctet(3);
    }

    /**
     * Checks if this is a broadcast address (255.255.255.255).
     */
    boolean isBroadcast();

    /**
     * Checks if this address is in the shared address space (100.64.0.0/10, RFC 6598).
     * Used for carrier-grade NAT.
     */
    boolean isSharedAddressSpace();

    /**
     * Checks if this address is in the reserved range (240.0.0.0/4).
     */
    boolean isReserved();

    /**
     * Converts this IPv4 address to an IPv4-mapped IPv6 address (::ffff:x.x.x.x).
     */
    Ipv6Address toIpv4MappedIpv6();

    /**
     * Converts this IPv4 address to an IPv4-compatible IPv6 address (::x.x.x.x).
     * @deprecated IPv4-compatible addresses are deprecated in RFC 4291
     */
    @Deprecated
    Ipv6Address toIpv4CompatibleIpv6();

    /**
     * Applies the given subnet mask to this address (bitwise AND).
     *
     * @param mask the subnet mask
     * @return the network address
     */
    Ipv4Address applyMask(Ipv4SubnetMask mask);

    /**
     * Applies the given wildcard mask to this address (bitwise OR with inverted mask).
     *
     * @param mask the wildcard mask
     * @return the broadcast address for the network
     */
    Ipv4Address applyWildcard(Ipv4SubnetMask mask);
}
