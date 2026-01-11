package net.luis.utils.io.network.address.format;

import org.jspecify.annotations.NonNull;

/**
 * Builder class for constructing ip parse options instances.<br>
 * Provides a fluent API for setting individual formatting options.<br>
 * <p>
 *     All options default to values matching {@link IpParseOptions#DEFAULT}:
 * </p>
 * <ul>
 *     <li>{@link #allowLeadingZeros} = {@code false}</li>
 *     <li>{@link #allowOctalNotation} = {@code false}</li>
 *     <li>{@link #allowDecimalNotation} = {@code false}</li>
 *     <li>{@link #allowMixedCase} = {@code true}</li>
 *     <li>{@link #allowEmptySegments} = {@code true}</li>
 *     <li>{@link #allowMixedNotation} = {@code true}</li>
 *     <li>{@link #allowZoneId} = {@code true}</li>
 *     <li>{@link #allowPortSuffix} = {@code false}</li>
 *     <li>{@link #normalize} = {@code false}</li>
 * </ul>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IpParseOptions options = IpParseOptions.builder()
 *     .allowLeadingZeros(true)
 *     .allowMixedCase(true)
 *     .normalize(true)
 *     .build();
 * }</pre>
 *
 * @see IpParseOptions
 *
 * @author Luis-St
 */
public final class IpParseOptionsBuilder {
	
	/**
	 * The allow leading zeros option.<br>
	 * When enabled, leading zeros in IPv4 octets are permitted without being treated as octal.<br>
	 */
	private boolean allowLeadingZeros;
	/**
	 * The allow octal notation option.<br>
	 * When enabled, leading zeros in IPv4 octets are interpreted as octal notation.<br>
	 */
	private boolean allowOctalNotation;
	/**
	 * The allow decimal notation option.<br>
	 * When enabled, IPv4 addresses in decimal notation are permitted.<br>
	 */
	private boolean allowDecimalNotation;
	/**
	 * The allow mixed case option.<br>
	 * When enabled, mixed case hexadecimal characters in IPv6 addresses are permitted.<br>
	 */
	private boolean allowMixedCase = true;
	/**
	 * The allow empty segments option.<br>
	 * When enabled, the double-colon (::) shorthand in IPv6 addresses is permitted.<br>
	 */
	private boolean allowEmptySegments = true;
	/**
	 * The allow mixed notation option.<br>
	 * When enabled, IPv4-mapped IPv6 addresses are permitted.<br>
	 */
	private boolean allowMixedNotation = true;
	/**
	 * The allow zone id option.<br>
	 * When enabled, zone identifiers in IPv6 link-local addresses are permitted.<br>
	 */
	private boolean allowZoneId = true;
	/**
	 * The allow port suffix option.<br>
	 * When enabled, port suffix notation in addresses is permitted.<br>
	 */
	private boolean allowPortSuffix;
	/**
	 * The normalize option.<br>
	 * When enabled, parsed addresses are normalized to their canonical form.<br>
	 */
	private boolean normalize;
	
	/**
	 * Constructs a new builder with default values.<br>
	 */
	IpParseOptionsBuilder() {}
	
	/**
	 * Sets whether to allow leading zeros in IPv4 octets without treating them as octal.<br>
	 * When enabled, "192.168.001.001" is parsed as "192.168.1.1".<br>
	 *
	 * @param allowLeadingZeros {@code true} to allow leading zeros, {@code false} otherwise
	 * @return This builder for method chaining
	 */
	public @NonNull IpParseOptionsBuilder allowLeadingZeros(boolean allowLeadingZeros) {
		this.allowLeadingZeros = allowLeadingZeros;
		return this;
	}
	
	/**
	 * Sets whether to interpret leading zeros as octal notation.<br>
	 * When enabled, "010" is interpreted as decimal 8.<br>
	 *
	 * @param allowOctalNotation {@code true} to interpret leading zeros as octal, {@code false} otherwise
	 * @return This builder for method chaining
	 */
	public @NonNull IpParseOptionsBuilder allowOctalNotation(boolean allowOctalNotation) {
		this.allowOctalNotation = allowOctalNotation;
		return this;
	}
	
	/**
	 * Sets whether to allow IPv4 addresses in decimal notation.<br>
	 * When enabled, "3232235777" is parsed as "192.168.1.1".<br>
	 *
	 * @param allowDecimalNotation {@code true} to allow decimal notation, {@code false} otherwise
	 * @return This builder for method chaining
	 */
	public @NonNull IpParseOptionsBuilder allowDecimalNotation(boolean allowDecimalNotation) {
		this.allowDecimalNotation = allowDecimalNotation;
		return this;
	}
	
	/**
	 * Sets whether to allow mixed case hexadecimal characters in IPv6 addresses.<br>
	 * When enabled, "2001:DB8::1" and "2001:db8::1" are both valid.<br>
	 *
	 * @param allowMixedCase {@code true} to allow mixed case, {@code false} otherwise
	 * @return This builder for method chaining
	 */
	public @NonNull IpParseOptionsBuilder allowMixedCase(boolean allowMixedCase) {
		this.allowMixedCase = allowMixedCase;
		return this;
	}
	
	/**
	 * Sets whether to allow the double-colon (::) shorthand in IPv6 addresses.<br>
	 * When enabled, "2001:db8::1" is valid as shorthand for "2001:db8:0:0:0:0:0:1".<br>
	 *
	 * @param allowEmptySegments {@code true} to allow empty segments, {@code false} otherwise
	 * @return This builder for method chaining
	 */
	public @NonNull IpParseOptionsBuilder allowEmptySegments(boolean allowEmptySegments) {
		this.allowEmptySegments = allowEmptySegments;
		return this;
	}
	
	/**
	 * Sets whether to allow IPv4-mapped IPv6 addresses.<br>
	 * When enabled, "::ffff:192.168.1.1" is a valid IPv6 representation.<br>
	 *
	 * @param allowMixedNotation {@code true} to allow mixed notation, {@code false} otherwise
	 * @return This builder for method chaining
	 */
	public @NonNull IpParseOptionsBuilder allowMixedNotation(boolean allowMixedNotation) {
		this.allowMixedNotation = allowMixedNotation;
		return this;
	}
	
	/**
	 * Sets whether to allow zone identifiers in IPv6 link-local addresses.<br>
	 * When enabled, "fe80::1%eth0" includes the network interface identifier.<br>
	 *
	 * @param allowZoneId {@code true} to allow zone identifiers, {@code false} otherwise
	 * @return This builder for method chaining
	 */
	public @NonNull IpParseOptionsBuilder allowZoneId(boolean allowZoneId) {
		this.allowZoneId = allowZoneId;
		return this;
	}
	
	/**
	 * Sets whether to allow port suffix notation in addresses.<br>
	 * When enabled, "192.168.1.1:8080" or "[::1]:8080" are parsed, with the port being available separately.<br>
	 *
	 * @param allowPortSuffix {@code true} to allow port suffix, {@code false} otherwise
	 * @return This builder for method chaining
	 */
	public @NonNull IpParseOptionsBuilder allowPortSuffix(boolean allowPortSuffix) {
		this.allowPortSuffix = allowPortSuffix;
		return this;
	}
	
	/**
	 * Sets whether to normalize the parsed address to its canonical form.<br>
	 * When enabled, addresses are converted to their standard representation (e.g., removing leading zeros, expanding IPv6 shorthand).<br>
	 *
	 * @param normalize {@code true} to normalize addresses, {@code false} otherwise
	 * @return This builder for method chaining
	 */
	public @NonNull IpParseOptionsBuilder normalize(boolean normalize) {
		this.normalize = normalize;
		return this;
	}
	
	/**
	 * Builds a new ip parse options instance with the configured values.
	 * @return A new ip parse options instance
	 */
	public @NonNull IpParseOptions build() {
		return new IpParseOptions(
			this.allowLeadingZeros,
			this.allowOctalNotation,
			this.allowDecimalNotation,
			this.allowMixedCase,
			this.allowEmptySegments,
			this.allowMixedNotation,
			this.allowZoneId,
			this.allowPortSuffix,
			this.normalize
		);
	}
}
