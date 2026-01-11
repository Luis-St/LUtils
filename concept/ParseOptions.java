package net.luis.ip;

/**
 * Configuration options for parsing IP addresses.
 * Controls strictness and normalization behavior.
 */
public interface ParseOptions {

    /**
     * Returns the default (strict) parsing options.
     */
    static ParseOptions strict() {
        return builder().build();
    }

    /**
     * Returns lenient parsing options that accept non-canonical formats.
     */
    static ParseOptions lenient() {
        return builder()
            .allowLeadingZeros(true)
            .allowOctalNotation(true)
            .allowDecimalNotation(true)
            .allowMixedCase(true)
            .allowEmptySegments(true)
            .build();
    }

    /**
     * Creates a new builder for parse options.
     */
    static Builder builder() {
        return new Builder();
    }

    /**
     * Whether to allow leading zeros in octets (e.g., "192.168.001.001").
     * In strict mode, leading zeros are rejected as they could indicate octal.
     */
    boolean allowLeadingZeros();

    /**
     * Whether to interpret leading zeros as octal notation (e.g., "0300" = 192).
     * Only applies if allowLeadingZeros is true.
     */
    boolean allowOctalNotation();

    /**
     * Whether to allow full decimal notation for IPv4 (e.g., "3232235777" for 192.168.1.1).
     */
    boolean allowDecimalNotation();

    /**
     * Whether to allow mixed case in IPv6 addresses.
     * Strict mode may require lowercase only.
     */
    boolean allowMixedCase();

    /**
     * Whether to allow empty segments in IPv6 (multiple consecutive colons beyond "::").
     */
    boolean allowEmptySegments();

    /**
     * Whether to allow IPv4-style notation in the last 32 bits of IPv6 addresses.
     * Example: ::ffff:192.168.1.1
     */
    boolean allowMixedNotation();

    /**
     * Whether to allow zone identifiers in IPv6 addresses (e.g., "fe80::1%eth0").
     */
    boolean allowZoneId();

    /**
     * Whether to allow port suffixes (e.g., "192.168.1.1:8080" or "[::1]:8080").
     * If allowed, the port is stripped and ignored.
     */
    boolean allowPortSuffix();

    /**
     * Whether to normalize the result to canonical form.
     * If true, the parsed address will be in canonical format regardless of input.
     */
    boolean normalize();

    /**
     * Builder for constructing ParseOptions instances.
     */
    class Builder {
        private boolean allowLeadingZeros = false;
        private boolean allowOctalNotation = false;
        private boolean allowDecimalNotation = false;
        private boolean allowMixedCase = true;
        private boolean allowEmptySegments = false;
        private boolean allowMixedNotation = true;
        private boolean allowZoneId = false;
        private boolean allowPortSuffix = false;
        private boolean normalize = true;

        Builder() {}

        public Builder allowLeadingZeros(boolean allow) {
            this.allowLeadingZeros = allow;
            return this;
        }

        public Builder allowOctalNotation(boolean allow) {
            this.allowOctalNotation = allow;
            return this;
        }

        public Builder allowDecimalNotation(boolean allow) {
            this.allowDecimalNotation = allow;
            return this;
        }

        public Builder allowMixedCase(boolean allow) {
            this.allowMixedCase = allow;
            return this;
        }

        public Builder allowEmptySegments(boolean allow) {
            this.allowEmptySegments = allow;
            return this;
        }

        public Builder allowMixedNotation(boolean allow) {
            this.allowMixedNotation = allow;
            return this;
        }

        public Builder allowZoneId(boolean allow) {
            this.allowZoneId = allow;
            return this;
        }

        public Builder allowPortSuffix(boolean allow) {
            this.allowPortSuffix = allow;
            return this;
        }

        public Builder normalize(boolean normalize) {
            this.normalize = normalize;
            return this;
        }

        public ParseOptions build() {
            return new ParseOptionsImpl(
                allowLeadingZeros, allowOctalNotation, allowDecimalNotation,
                allowMixedCase, allowEmptySegments, allowMixedNotation,
                allowZoneId, allowPortSuffix, normalize
            );
        }
    }
}

/**
 * Default implementation of ParseOptions.
 */
record ParseOptionsImpl(
    boolean allowLeadingZeros,
    boolean allowOctalNotation,
    boolean allowDecimalNotation,
    boolean allowMixedCase,
    boolean allowEmptySegments,
    boolean allowMixedNotation,
    boolean allowZoneId,
    boolean allowPortSuffix,
    boolean normalize
) implements ParseOptions {}
