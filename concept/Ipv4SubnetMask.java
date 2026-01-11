package net.luis.ip;

/**
 * Represents an IPv4 subnet mask.
 * A valid subnet mask has all 1-bits contiguous from the most significant bit.
 * Implementations must be immutable.
 */
public interface Ipv4SubnetMask {

    /**
     * Returns the prefix length (number of 1-bits).
     */
    int prefixLength();

    /**
     * Returns the mask as a 32-bit value.
     */
    long toInt();

    /**
     * Returns the mask as an IPv4 address representation.
     */
    Ipv4Address toAddress();

    /**
     * Returns the raw bytes of this mask.
     */
    byte[] toBytes();

    /**
     * Returns the wildcard (inverse) mask.
     */
    Ipv4SubnetMask toWildcard();

    /**
     * Checks if this is a wildcard mask (inverse of a subnet mask).
     */
    boolean isWildcard();

    /**
     * Returns the number of host addresses for this mask.
     * Calculated as 2^(32 - prefixLength).
     */
    long hostCount();

    /**
     * Returns the dotted decimal notation (e.g., "255.255.255.0").
     */
    @Override
    String toString();

    /**
     * Checks if this is a valid contiguous subnet mask.
     */
    boolean isValid();
}
