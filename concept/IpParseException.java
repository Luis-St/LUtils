package net.luis.ip;

/**
 * Exception thrown when parsing an IP address, network, or range fails.
 */
public class IpParseException extends IllegalArgumentException {

    private final String input;
    private final int position;
    private final ParseErrorType errorType;

    /**
     * Creates a new parse exception.
     *
     * @param message the error message
     * @param input the input string that failed to parse
     */
    public IpParseException(String message, String input) {
        this(message, input, -1, ParseErrorType.UNKNOWN);
    }

    /**
     * Creates a new parse exception with position information.
     *
     * @param message the error message
     * @param input the input string that failed to parse
     * @param position the position in the input where the error occurred
     * @param errorType the type of parse error
     */
    public IpParseException(String message, String input, int position, ParseErrorType errorType) {
        super(formatMessage(message, input, position));
        this.input = input;
        this.position = position;
        this.errorType = errorType;
    }

    private static String formatMessage(String message, String input, int position) {
        if (position >= 0 && position < input.length()) {
            return String.format("%s at position %d in '%s'", message, position, input);
        }
        return String.format("%s in '%s'", message, input);
    }

    /**
     * Returns the input string that failed to parse.
     */
    public String getInput() {
        return input;
    }

    /**
     * Returns the position in the input where the error occurred, or -1 if unknown.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns the type of parse error.
     */
    public ParseErrorType getErrorType() {
        return errorType;
    }

    /**
     * Types of parsing errors.
     */
    public enum ParseErrorType {
        /**
         * The input string is null or empty.
         */
        EMPTY_INPUT,

        /**
         * An octet or hextet value is out of range.
         */
        VALUE_OUT_OF_RANGE,

        /**
         * The address has too many or too few segments.
         */
        INVALID_SEGMENT_COUNT,

        /**
         * An invalid character was encountered.
         */
        INVALID_CHARACTER,

        /**
         * The prefix length is invalid or out of range.
         */
        INVALID_PREFIX_LENGTH,

        /**
         * Multiple "::" compressions in IPv6 address.
         */
        MULTIPLE_COMPRESSIONS,

        /**
         * Invalid subnet mask (non-contiguous bits).
         */
        INVALID_SUBNET_MASK,

        /**
         * Start address is greater than end address in a range.
         */
        INVALID_RANGE,

        /**
         * Leading zeros detected in strict mode.
         */
        LEADING_ZEROS,

        /**
         * Generic or unknown parse error.
         */
        UNKNOWN
    }
}
