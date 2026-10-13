/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.crypto.cert;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.x500.*;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.security.auth.x500.X500Principal;
import java.util.*;

/**
 * The distinguished name of a certificate subject or issuer, reduced to the attributes a certificate usually carries.<br>
 * <p>
 *     Every component is optional, but at least one must be present.<br>
 *     The name is written from the most significant attribute to the least significant one, so the country comes first and the common name last.<br>
 *     This is the order in which the attributes appear in the encoded certificate, and the reverse of the order in which RFC 2253 prints them.
 * </p>
 * <p>
 *     A name is deliberately never derived from a certificate that was not built by this library.<br>
 *     A certificate from a public authority may carry attributes this record does not model, and dropping them silently would produce a different name.<br>
 *     Such names stay {@link X500Principal}s.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * DistinguishedName name = new DistinguishedName("api.example.com", "Example", null, null, null, "DE");
 *
 * // The same name, parsed from its RFC 2253 form
 * DistinguishedName parsed = DistinguishedName.of("CN=api.example.com,O=Example,C=DE");
 * }</pre>
 *
 * @author Luis-St
 *
 * @param commonName The common name, or null if absent
 * @param organization The organization, or null if absent
 * @param organizationalUnit The organizational unit, or null if absent
 * @param locality The locality, or null if absent
 * @param state The state or province, or null if absent
 * @param country The two letter country code, or null if absent
 */
public record DistinguishedName(
	@Nullable String commonName,
	@Nullable String organization,
	@Nullable String organizationalUnit,
	@Nullable String locality,
	@Nullable String state,
	@Nullable String country
) {
	
	/**
	 * Constructs a new distinguished name.<br>
	 * @throws IllegalArgumentException If every component is null, if a component is blank, or if the country is not two letters
	 */
	public DistinguishedName {
		List<String> components = Arrays.asList(commonName, organization, organizationalUnit, locality, state, country);
		if (components.stream().allMatch(Objects::isNull)) {
			throw new IllegalArgumentException("A distinguished name needs at least one component");
		}
		if (components.stream().filter(Objects::nonNull).anyMatch(String::isBlank)) {
			throw new IllegalArgumentException("A distinguished name component must not be blank");
		}
		if (country != null && !country.matches("[A-Za-z]{2}")) {
			throw new IllegalArgumentException("Country must be a two letter code, was '" + country + "'");
		}
	}
	
	/**
	 * Creates a distinguished name holding only the given common name.<br>
	 *
	 * @param commonName The common name
	 * @return The created distinguished name
	 * @throws NullPointerException If the common name is null
	 * @throws IllegalArgumentException If the common name is blank
	 */
	public static @NonNull DistinguishedName ofCommonName(@NonNull String commonName) {
		Objects.requireNonNull(commonName, "Common name must not be null");
		return new DistinguishedName(commonName, null, null, null, null, null);
	}
	
	/**
	 * Parses a distinguished name from its RFC 2253 form.<br>
	 * <p>
	 *     The name must only carry attributes this record models, each at most once and never in a multi-valued relative name.<br>
	 *     Anything else is rejected rather than dropped, so the parsed name is always the name that was written.
	 * </p>
	 *
	 * @param rfc2253 The name to parse, for example {@code CN=api.example.com,O=Example,C=DE}
	 * @return The parsed distinguished name
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is malformed or carries an attribute this record does not model
	 */
	public static @NonNull DistinguishedName of(@NonNull String rfc2253) {
		Objects.requireNonNull(rfc2253, "Name must not be null");
		return of(X500Name.getInstance(new X500Principal(rfc2253).getEncoded()));
	}
	
	/**
	 * Converts the given encoded name into a distinguished name.<br>
	 *
	 * @param name The name to convert
	 * @return The converted distinguished name
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name carries an attribute this record does not model
	 */
	static @NonNull DistinguishedName of(@NonNull X500Name name) {
		Objects.requireNonNull(name, "Name must not be null");
		
		Map<ASN1ObjectIdentifier, String> values = new HashMap<>();
		for (RDN rdn : name.getRDNs()) {
			if (rdn.isMultiValued()) {
				throw new IllegalArgumentException("Multi-valued relative distinguished names are not supported");
			}
			
			AttributeTypeAndValue attribute = rdn.getFirst();
			if (!(attribute.getValue() instanceof ASN1String value)) {
				throw new IllegalArgumentException("Attribute " + attribute.getType() + " does not hold a string");
			}
			if (!List.of(BCStyle.CN, BCStyle.O, BCStyle.OU, BCStyle.L, BCStyle.ST, BCStyle.C).contains(attribute.getType())) {
				throw new IllegalArgumentException("Attribute " + attribute.getType() + " is not supported");
			}
			if (values.put(attribute.getType(), value.getString()) != null) {
				throw new IllegalArgumentException("Attribute " + attribute.getType() + " appears more than once");
			}
		}
		return new DistinguishedName(values.get(BCStyle.CN), values.get(BCStyle.O), values.get(BCStyle.OU), values.get(BCStyle.L), values.get(BCStyle.ST), values.get(BCStyle.C));
	}
	
	/**
	 * Converts this name into a principal.<br>
	 * @return The principal naming the same entity
	 */
	public @NonNull X500Principal toX500Principal() {
		return X509Support.principal(this.toX500Name());
	}
	
	/**
	 * Converts this name into its encoded form, most significant attribute first.<br>
	 * @return The encoded name
	 */
	@NonNull X500Name toX500Name() {
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		if (this.country != null) {
			builder.addRDN(BCStyle.C, this.country.toUpperCase(Locale.ROOT));
		}
		if (this.state != null) {
			builder.addRDN(BCStyle.ST, this.state);
		}
		if (this.locality != null) {
			builder.addRDN(BCStyle.L, this.locality);
		}
		if (this.organization != null) {
			builder.addRDN(BCStyle.O, this.organization);
		}
		if (this.organizationalUnit != null) {
			builder.addRDN(BCStyle.OU, this.organizationalUnit);
		}
		if (this.commonName != null) {
			builder.addRDN(BCStyle.CN, this.commonName);
		}
		return builder.build();
	}
}
