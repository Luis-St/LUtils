package net.luis.ip;

/**
 * IPv6 multicast address scopes as defined in RFC 4291.
 * The scope is encoded in bits 12-15 of the multicast address.
 */
public enum Ipv6MulticastScope {

    /**
     * Reserved scope value (0x0).
     */
    RESERVED_0(0x0),

    /**
     * Interface-local scope (0x1).
     * Packets are not forwarded beyond the originating interface.
     */
    INTERFACE_LOCAL(0x1),

    /**
     * Link-local scope (0x2).
     * Packets are not forwarded beyond the local link.
     */
    LINK_LOCAL(0x2),

    /**
     * Realm-local scope (0x3).
     * Defined by local administration, larger than link but smaller than admin.
     */
    REALM_LOCAL(0x3),

    /**
     * Admin-local scope (0x4).
     * Smallest administratively configured scope.
     */
    ADMIN_LOCAL(0x4),

    /**
     * Site-local scope (0x5).
     * Packets are not forwarded beyond the local site.
     */
    SITE_LOCAL(0x5),

    /**
     * Organization-local scope (0x8).
     * Packets are not forwarded beyond the organization.
     */
    ORGANIZATION_LOCAL(0x8),

    /**
     * Global scope (0xE).
     * No scope restrictions.
     */
    GLOBAL(0xE),

    /**
     * Reserved scope value (0xF).
     */
    RESERVED_F(0xF),

    /**
     * Unknown or unassigned scope value.
     */
    UNKNOWN(-1);

    private final int value;

    Ipv6MulticastScope(int value) {
        this.value = value;
    }

    /**
     * Returns the numeric scope value.
     */
    public int value() {
        return value;
    }

    /**
     * Returns the scope for the given numeric value.
     *
     * @param value the scope value (0-15)
     * @return the corresponding scope enum
     */
    public static Ipv6MulticastScope fromValue(int value) {
        for (Ipv6MulticastScope scope : values()) {
            if (scope.value == value) {
                return scope;
            }
        }
        return UNKNOWN;
    }
}
