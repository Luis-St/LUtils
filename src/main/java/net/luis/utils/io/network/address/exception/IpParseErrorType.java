package net.luis.utils.io.network.address.exception;

/**
 * Enumeration of possible error types that can occur during IP address parsing.<br>
 * Each error type represents a specific category of parsing failure.
 *
 * @author Luis-St
 */
public enum IpParseErrorType {
	
	/**
	 * The input string does not conform to the expected IP address format.<br>
	 * This includes issues like wrong number of octets/hextets, missing delimiters,
	 * or invalid characters in the address.
	 */
	INVALID_FORMAT,
	
	/**
	 * An octet value in an IPv4 address is outside the valid range of 0-255.<br>
	 * For example, "256.0.0.1" would trigger this error.
	 */
	INVALID_OCTET_VALUE,
	
	/**
	 * A hextet value in an IPv6 address is outside the valid range of 0-FFFF.<br>
	 * For example, "10000::1" would trigger this error.
	 */
	INVALID_HEXTET_VALUE,
	
	/**
	 * The prefix length (CIDR notation) is outside the valid range.<br>
	 * For IPv4, valid range is 0-32. For IPv6, valid range is 0-128.
	 */
	INVALID_PREFIX_LENGTH,
	
	/**
	 * The zone ID in an IPv6 address is invalid or malformed.<br>
	 * Zone IDs are used for link-local addresses (e.g., "fe80::1%eth0").
	 */
	INVALID_ZONE_ID,
	
	/**
	 * The input string is null or empty.<br>
	 * A valid IP address requires at least some input to parse.
	 */
	EMPTY_INPUT,
	
	/**
	 * An unknown or unclassified error occurred during parsing.<br>
	 * This is used when the error does not fit any other category.
	 */
	UNKNOWN
}
