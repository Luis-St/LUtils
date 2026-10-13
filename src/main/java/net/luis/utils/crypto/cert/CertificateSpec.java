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

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * What an issued certificate says about its subject, apart from the subject's name and key.<br>
 * <p>
 *     The spec is separate from the name and the key because those come from different places.<br>
 *     A {@link CertificateBuilder} takes them directly, a signed certificate request brings them along, and the spec is what the issuer decides on top of either.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * CertificateSpec spec = CertificateSpec.of(Instant.now(), Duration.ofDays(90))
 *     .withSubjectAltNames(new SubjectAltName.Dns("api.example.com"))
 *     .withKeyUsage(Set.of(KeyUsage.DIGITAL_SIGNATURE))
 *     .withExtendedKeyUsage(Set.of(ExtendedKeyUsage.SERVER_AUTH));
 * }</pre>
 *
 * @author Luis-St
 *
 * @param serial The serial number, or null to draw a random positive 128 bit number when the certificate is issued
 * @param notBefore The first instant the certificate is valid at
 * @param validity How long the certificate is valid for, starting at not before
 * @param subjectAltNames The subject alternative names, empty to leave the extension out
 * @param keyUsage The key usages, empty to leave the extension out
 * @param extendedKeyUsage The extended key usages, empty to leave the extension out
 * @param pathLength The maximum number of intermediate authorities below this one, or null if the certificate is not an authority
 */
public record CertificateSpec(
	@Nullable BigInteger serial,
	@NonNull Instant notBefore,
	@NonNull Duration validity,
	@NonNull @Unmodifiable List<SubjectAltName> subjectAltNames,
	@NonNull @Unmodifiable Set<KeyUsage> keyUsage,
	@NonNull @Unmodifiable Set<ExtendedKeyUsage> extendedKeyUsage,
	@Nullable Integer pathLength
) {
	
	/**
	 * The largest serial number RFC 5280 permits, which is 20 octets with a clear sign bit.<br>
	 */
	private static final int MAX_SERIAL_BITS = 159;
	
	/**
	 * Constructs a new certificate spec.<br>
	 * @throws NullPointerException If the not before instant, the validity, or one of the collections is null or holds null
	 * @throws IllegalArgumentException If the serial is not positive or longer than 20 octets, if the validity is not positive, or if the path length is negative
	 */
	public CertificateSpec {
		Objects.requireNonNull(notBefore, "Not before must not be null");
		Objects.requireNonNull(validity, "Validity must not be null");
		Objects.requireNonNull(subjectAltNames, "Subject alternative names must not be null");
		Objects.requireNonNull(keyUsage, "Key usage must not be null");
		Objects.requireNonNull(extendedKeyUsage, "Extended key usage must not be null");
		if (serial != null && (serial.signum() <= 0 || serial.bitLength() > MAX_SERIAL_BITS)) {
			throw new IllegalArgumentException("Serial must be positive and at most 20 octets long, was " + serial);
		}
		if (validity.isNegative() || validity.isZero()) {
			throw new IllegalArgumentException("Validity must be positive, was " + validity);
		}
		if (pathLength != null && pathLength < 0) {
			throw new IllegalArgumentException("Path length must not be negative, was " + pathLength);
		}
		
		subjectAltNames = List.copyOf(subjectAltNames);
		keyUsage = Collections.unmodifiableSet(keyUsage.isEmpty() ? EnumSet.noneOf(KeyUsage.class) : EnumSet.copyOf(keyUsage));
		extendedKeyUsage = Collections.unmodifiableSet(extendedKeyUsage.isEmpty() ? EnumSet.noneOf(ExtendedKeyUsage.class) : EnumSet.copyOf(extendedKeyUsage));
	}
	
	/**
	 * Creates a spec for an end entity certificate with a random serial and no extensions besides the key identifiers.<br>
	 *
	 * @param notBefore The first instant the certificate is valid at
	 * @param validity How long the certificate is valid for
	 * @return The created spec
	 * @throws NullPointerException If the not before instant or the validity is null
	 * @throws IllegalArgumentException If the validity is not positive
	 */
	public static @NonNull CertificateSpec of(@NonNull Instant notBefore, @NonNull Duration validity) {
		return new CertificateSpec(null, notBefore, validity, List.of(), Set.of(), Set.of(), null);
	}
	
	/**
	 * Returns a copy of this spec with the given serial number.<br>
	 *
	 * @param serial The serial number
	 * @return The copied spec
	 * @throws NullPointerException If the serial is null
	 * @throws IllegalArgumentException If the serial is not positive or longer than 20 octets
	 */
	public @NonNull CertificateSpec withSerial(@NonNull BigInteger serial) {
		Objects.requireNonNull(serial, "Serial must not be null");
		return new CertificateSpec(serial, this.notBefore, this.validity, this.subjectAltNames, this.keyUsage, this.extendedKeyUsage, this.pathLength);
	}
	
	/**
	 * Returns a copy of this spec with the given subject alternative names, replacing any it had.<br>
	 *
	 * @param names The subject alternative names
	 * @return The copied spec
	 * @throws NullPointerException If the names are null or hold null
	 */
	public @NonNull CertificateSpec withSubjectAltNames(SubjectAltName @NonNull ... names) {
		Objects.requireNonNull(names, "Names must not be null");
		return this.withSubjectAltNames(List.of(names));
	}
	
	/**
	 * Returns a copy of this spec with the given subject alternative names, replacing any it had.<br>
	 *
	 * @param names The subject alternative names
	 * @return The copied spec
	 * @throws NullPointerException If the names are null or hold null
	 */
	public @NonNull CertificateSpec withSubjectAltNames(@NonNull List<SubjectAltName> names) {
		Objects.requireNonNull(names, "Names must not be null");
		return new CertificateSpec(this.serial, this.notBefore, this.validity, names, this.keyUsage, this.extendedKeyUsage, this.pathLength);
	}
	
	/**
	 * Returns a copy of this spec with the given key usages, replacing any it had.<br>
	 *
	 * @param usages The key usages
	 * @return The copied spec
	 * @throws NullPointerException If the usages are null or hold null
	 */
	public @NonNull CertificateSpec withKeyUsage(@NonNull Set<KeyUsage> usages) {
		Objects.requireNonNull(usages, "Usages must not be null");
		return new CertificateSpec(this.serial, this.notBefore, this.validity, this.subjectAltNames, usages, this.extendedKeyUsage, this.pathLength);
	}
	
	/**
	 * Returns a copy of this spec with the given extended key usages, replacing any it had.<br>
	 *
	 * @param usages The extended key usages
	 * @return The copied spec
	 * @throws NullPointerException If the usages are null or hold null
	 */
	public @NonNull CertificateSpec withExtendedKeyUsage(@NonNull Set<ExtendedKeyUsage> usages) {
		Objects.requireNonNull(usages, "Usages must not be null");
		return new CertificateSpec(this.serial, this.notBefore, this.validity, this.subjectAltNames, this.keyUsage, usages, this.pathLength);
	}
	
	/**
	 * Returns a copy of this spec that makes the certificate an authority.<br>
	 *
	 * @param pathLength The maximum number of intermediate authorities below this one, zero if it may only issue end entity certificates
	 * @return The copied spec
	 * @throws IllegalArgumentException If the path length is negative
	 */
	public @NonNull CertificateSpec withCa(int pathLength) {
		return new CertificateSpec(this.serial, this.notBefore, this.validity, this.subjectAltNames, this.keyUsage, this.extendedKeyUsage, pathLength);
	}
	
	/**
	 * Returns the last instant the certificate is valid at.<br>
	 * @return The not after instant
	 */
	public @NonNull Instant notAfter() {
		return this.notBefore.plus(this.validity);
	}
	
	/**
	 * Returns whether the certificate is an authority.<br>
	 * @return True if the spec carries a path length
	 */
	public boolean isCa() {
		return this.pathLength != null;
	}
}
