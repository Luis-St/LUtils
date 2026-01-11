package net.luis.ip;

/**
 * Options for formatting IP addresses as strings.
 * Use the builder pattern to construct instances.
 */
public interface FormatOptions {

    /**
     * Returns the default formatting options.
     */
    static FormatOptions defaults() {
        return builder().build();
    }

    /**
     * Creates a new builder for format options.
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Whether to use uppercase letters for hexadecimal digits (IPv6).
     */
    boolean uppercase();

    /**
     * Whether to compress zero runs in IPv6 addresses using "::".
     */
    boolean compressZeros();

    /**
     * Whether to use mixed notation for IPv4-mapped IPv6 addresses (::ffff:192.168.1.1).
     */
    boolean mixedNotation();

    /**
     * Whether to pad IPv4 octets with leading zeros (e.g., 192.168.001.001).
     */
    boolean padOctets();

    /**
     * Whether to pad IPv6 hextets with leading zeros (e.g., 2001:0db8:0000:...).
     */
    boolean padHextets();

    /**
     * Whether to include the prefix length when formatting networks.
     */
    boolean includePrefixLength();

    /**
     * The separator to use between octets for IPv4 (default is '.').
     */
    char ipv4Separator();

    /**
     * The separator to use between hextets for IPv6 (default is ':').
     */
    char ipv6Separator();

    /**
     * Builder for constructing FormatOptions instances.
     */
    class Builder {
        private boolean uppercase = false;
        private boolean compressZeros = true;
        private boolean mixedNotation = false;
        private boolean padOctets = false;
        private boolean padHextets = false;
        private boolean includePrefixLength = true;
        private char ipv4Separator = '.';
        private char ipv6Separator = ':';

        Builder() {}

        public Builder uppercase(boolean uppercase) {
            this.uppercase = uppercase;
            return this;
        }

        public Builder compressZeros(boolean compress) {
            this.compressZeros = compress;
            return this;
        }

        public Builder mixedNotation(boolean mixed) {
            this.mixedNotation = mixed;
            return this;
        }

        public Builder padOctets(boolean pad) {
            this.padOctets = pad;
            return this;
        }

        public Builder padHextets(boolean pad) {
            this.padHextets = pad;
            return this;
        }

        public Builder includePrefixLength(boolean include) {
            this.includePrefixLength = include;
            return this;
        }

        public Builder ipv4Separator(char separator) {
            this.ipv4Separator = separator;
            return this;
        }

        public Builder ipv6Separator(char separator) {
            this.ipv6Separator = separator;
            return this;
        }

        public FormatOptions build() {
            return new FormatOptionsImpl(
                uppercase, compressZeros, mixedNotation,
                padOctets, padHextets, includePrefixLength,
                ipv4Separator, ipv6Separator
            );
        }
    }
}

/**
 * Default implementation of FormatOptions.
 */
record FormatOptionsImpl(
    boolean uppercase,
    boolean compressZeros,
    boolean mixedNotation,
    boolean padOctets,
    boolean padHextets,
    boolean includePrefixLength,
    char ipv4Separator,
    char ipv6Separator
) implements FormatOptions {}
