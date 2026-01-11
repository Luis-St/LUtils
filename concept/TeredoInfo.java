package net.luis.ip;

/**
 * Information extracted from a Teredo IPv6 address.
 * Teredo addresses encode the server address, client port, and client address.
 * 
 * Format: 2001:0000:SSSS:SSSS:FFFF:PPPP:CCCC:CCCC
 * - 2001:0000 - Teredo prefix
 * - SSSS:SSSS - Teredo server IPv4 address
 * - FFFF - Flags (includes cone NAT indicator)
 * - PPPP - Obfuscated UDP port (XORed with 0xFFFF)
 * - CCCC:CCCC - Obfuscated client IPv4 address (XORed with 0xFFFFFFFF)
 *
 * @param serverAddress the Teredo server's IPv4 address
 * @param clientAddress the client's public IPv4 address (after NAT)
 * @param clientPort the client's mapped UDP port
 * @param flags the Teredo flags field
 * @param isConeNat whether the client is behind a cone NAT
 */
public record TeredoInfo(
    Ipv4Address serverAddress,
    Ipv4Address clientAddress,
    int clientPort,
    int flags,
    boolean isConeNat
) {

    /**
     * The Teredo prefix (2001:0000::/32).
     */
    public static final String TEREDO_PREFIX = "2001:0000::/32";

    /**
     * The default Teredo UDP port.
     */
    public static final int DEFAULT_TEREDO_PORT = 3544;

    /**
     * Flag bit indicating cone NAT.
     */
    public static final int CONE_NAT_FLAG = 0x8000;

    /**
     * Returns true if this Teredo tunnel uses the default port.
     */
    public boolean usesDefaultPort() {
        return clientPort == DEFAULT_TEREDO_PORT;
    }
}
