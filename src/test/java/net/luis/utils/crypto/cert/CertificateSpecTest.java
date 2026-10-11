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

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CertificateSpec}.<br>
 *
 * @author Luis-St
 */
class CertificateSpecTest {
	
	private static final Instant NOT_BEFORE = Instant.parse("2026-01-01T00:00:00Z");
	private static final Duration VALIDITY = Duration.ofDays(90);
	private static final SubjectAltName DNS = new SubjectAltName.Dns("api.example.com");
	private static final SubjectAltName EMAIL = new SubjectAltName.Email("admin@example.com");
	private static final BigInteger MAX_SERIAL = BigInteger.ONE.shiftLeft(159).subtract(BigInteger.ONE);
	
	private static CertificateSpec spec(BigInteger serial, Integer pathLength) {
		return new CertificateSpec(serial, NOT_BEFORE, VALIDITY, List.of(), Set.of(), Set.of(), pathLength);
	}
	
	@Test
	void constructWithAllComponents() {
		CertificateSpec spec = new CertificateSpec(BigInteger.TEN, NOT_BEFORE, VALIDITY, List.of(DNS), Set.of(KeyUsage.DIGITAL_SIGNATURE), Set.of(ExtendedKeyUsage.SERVER_AUTH), 1);
		assertEquals(BigInteger.TEN, spec.serial());
		assertEquals(NOT_BEFORE, spec.notBefore());
		assertEquals(VALIDITY, spec.validity());
		assertEquals(List.of(DNS), spec.subjectAltNames());
		assertEquals(Set.of(KeyUsage.DIGITAL_SIGNATURE), spec.keyUsage());
		assertEquals(Set.of(ExtendedKeyUsage.SERVER_AUTH), spec.extendedKeyUsage());
		assertEquals(1, spec.pathLength());
	}
	
	@Test
	void constructWithNullSerialAndPathLength() {
		CertificateSpec spec = spec(null, null);
		assertNull(spec.serial());
		assertNull(spec.pathLength());
	}
	
	@Test
	void constructWithNullNotBefore() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new CertificateSpec(null, null, VALIDITY, List.of(), Set.of(), Set.of(), null));
		assertEquals("Not before must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNullValidity() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new CertificateSpec(null, NOT_BEFORE, null, List.of(), Set.of(), Set.of(), null));
		assertEquals("Validity must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNullSubjectAltNames() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new CertificateSpec(null, NOT_BEFORE, VALIDITY, null, Set.of(), Set.of(), null));
		assertEquals("Subject alternative names must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNullKeyUsage() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new CertificateSpec(null, NOT_BEFORE, VALIDITY, List.of(), null, Set.of(), null));
		assertEquals("Key usage must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNullExtendedKeyUsage() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new CertificateSpec(null, NOT_BEFORE, VALIDITY, List.of(), Set.of(), null, null));
		assertEquals("Extended key usage must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithZeroSerial() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> spec(BigInteger.ZERO, null));
		assertEquals("Serial must be positive and at most 20 octets long, was 0", exception.getMessage());
	}
	
	@Test
	void constructWithNegativeSerial() {
		assertThrows(IllegalArgumentException.class, () -> spec(BigInteger.valueOf(-1), null));
		assertThrows(IllegalArgumentException.class, () -> spec(MAX_SERIAL.negate(), null));
	}
	
	@Test
	void constructWithTooLongSerial() {
		BigInteger serial = BigInteger.ONE.shiftLeft(159);
		assertEquals(160, serial.bitLength());
		assertThrows(IllegalArgumentException.class, () -> spec(serial, null));
	}
	
	@Test
	void constructWithZeroValidity() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new CertificateSpec(null, NOT_BEFORE, Duration.ZERO, List.of(), Set.of(), Set.of(), null));
		assertEquals("Validity must be positive, was PT0S", exception.getMessage());
	}
	
	@Test
	void constructWithNegativeValidity() {
		assertThrows(IllegalArgumentException.class, () -> new CertificateSpec(null, NOT_BEFORE, Duration.ofNanos(-1), List.of(), Set.of(), Set.of(), null));
		assertThrows(IllegalArgumentException.class, () -> new CertificateSpec(null, NOT_BEFORE, Duration.ofDays(-1), List.of(), Set.of(), Set.of(), null));
	}
	
	@Test
	void constructWithNegativePathLength() {
		assertEquals("Path length must not be negative, was -1", assertThrows(IllegalArgumentException.class, () -> spec(null, -1)).getMessage());
	}
	
	@Test
	void constructWithNullSubjectAltNameElement() {
		List<SubjectAltName> names = Arrays.asList(DNS, null);
		assertThrows(NullPointerException.class, () -> new CertificateSpec(null, NOT_BEFORE, VALIDITY, names, Set.of(), Set.of(), null));
	}
	
	@Test
	void constructWithNullKeyUsageElement() {
		Set<KeyUsage> usages = new HashSet<>(Arrays.asList(KeyUsage.CRL_SIGN, null));
		assertThrows(NullPointerException.class, () -> new CertificateSpec(null, NOT_BEFORE, VALIDITY, List.of(), usages, Set.of(), null));
	}
	
	@Test
	void constructWithNullExtendedKeyUsageElement() {
		Set<ExtendedKeyUsage> usages = new HashSet<>(Arrays.asList(ExtendedKeyUsage.CODE_SIGNING, null));
		assertThrows(NullPointerException.class, () -> new CertificateSpec(null, NOT_BEFORE, VALIDITY, List.of(), Set.of(), usages, null));
	}
	
	@Test
	void ofWithNullNotBefore() {
		assertEquals("Not before must not be null", assertThrows(NullPointerException.class, () -> CertificateSpec.of(null, VALIDITY)).getMessage());
	}
	
	@Test
	void ofWithNullValidity() {
		assertEquals("Validity must not be null", assertThrows(NullPointerException.class, () -> CertificateSpec.of(NOT_BEFORE, null)).getMessage());
	}
	
	@Test
	void ofWithNonPositiveValidity() {
		assertThrows(IllegalArgumentException.class, () -> CertificateSpec.of(NOT_BEFORE, Duration.ZERO));
		assertThrows(IllegalArgumentException.class, () -> CertificateSpec.of(NOT_BEFORE, Duration.ofSeconds(-1)));
	}
	
	@Test
	void withSerialWithNull() {
		assertEquals("Serial must not be null", assertThrows(NullPointerException.class, () -> CertificateSpec.of(NOT_BEFORE, VALIDITY).withSerial(null)).getMessage());
	}
	
	@Test
	void withSerialWithInvalidSerial() {
		CertificateSpec spec = CertificateSpec.of(NOT_BEFORE, VALIDITY);
		assertThrows(IllegalArgumentException.class, () -> spec.withSerial(BigInteger.ZERO));
		assertThrows(IllegalArgumentException.class, () -> spec.withSerial(BigInteger.ONE.shiftLeft(159)));
	}
	
	@Test
	void withSubjectAltNamesWithNullArray() {
		CertificateSpec spec = CertificateSpec.of(NOT_BEFORE, VALIDITY);
		assertEquals("Names must not be null", assertThrows(NullPointerException.class, () -> spec.withSubjectAltNames((SubjectAltName[]) null)).getMessage());
	}
	
	@Test
	void withSubjectAltNamesWithNullArrayElement() {
		CertificateSpec spec = CertificateSpec.of(NOT_BEFORE, VALIDITY);
		assertThrows(NullPointerException.class, () -> spec.withSubjectAltNames(DNS, null));
	}
	
	@Test
	void withSubjectAltNamesWithNullList() {
		CertificateSpec spec = CertificateSpec.of(NOT_BEFORE, VALIDITY);
		assertEquals("Names must not be null", assertThrows(NullPointerException.class, () -> spec.withSubjectAltNames((List<SubjectAltName>) null)).getMessage());
	}
	
	@Test
	void withSubjectAltNamesWithNullListElement() {
		CertificateSpec spec = CertificateSpec.of(NOT_BEFORE, VALIDITY);
		assertThrows(NullPointerException.class, () -> spec.withSubjectAltNames(Arrays.asList(DNS, null)));
	}
	
	@Test
	void withKeyUsageWithNull() {
		assertEquals("Usages must not be null", assertThrows(NullPointerException.class, () -> CertificateSpec.of(NOT_BEFORE, VALIDITY).withKeyUsage(null)).getMessage());
	}
	
	@Test
	void withExtendedKeyUsageWithNull() {
		assertEquals("Usages must not be null", assertThrows(NullPointerException.class, () -> CertificateSpec.of(NOT_BEFORE, VALIDITY).withExtendedKeyUsage(null)).getMessage());
	}
	
	@Test
	void withCaWithNegativePathLength() {
		assertEquals("Path length must not be negative, was -2", assertThrows(IllegalArgumentException.class, () -> CertificateSpec.of(NOT_BEFORE, VALIDITY).withCa(-2)).getMessage());
	}
	
	@Test
	void constructWithSmallestSerial() {
		assertEquals(BigInteger.ONE, spec(BigInteger.ONE, null).serial());
	}
	
	@Test
	void constructWithLargestSerial() {
		assertEquals(159, MAX_SERIAL.bitLength());
		assertEquals(MAX_SERIAL, spec(MAX_SERIAL, null).serial());
	}
	
	@Test
	void constructWithSmallestValidity() {
		CertificateSpec spec = CertificateSpec.of(NOT_BEFORE, Duration.ofNanos(1));
		assertEquals(Duration.ofNanos(1), spec.validity());
	}
	
	@Test
	void constructWithZeroPathLength() {
		CertificateSpec spec = spec(null, 0);
		assertEquals(0, spec.pathLength());
		assertTrue(spec.isCa());
	}
	
	@Test
	void constructWithEmptyCollections() {
		CertificateSpec spec = spec(null, null);
		assertTrue(spec.subjectAltNames().isEmpty());
		assertTrue(spec.keyUsage().isEmpty());
		assertTrue(spec.extendedKeyUsage().isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> spec.keyUsage().add(KeyUsage.CRL_SIGN));
		assertThrows(UnsupportedOperationException.class, () -> spec.extendedKeyUsage().add(ExtendedKeyUsage.SERVER_AUTH));
	}
	
	@Test
	void constructWithNonEmptyCollections() {
		Set<KeyUsage> usages = new LinkedHashSet<>(List.of(KeyUsage.CRL_SIGN, KeyUsage.DIGITAL_SIGNATURE, KeyUsage.KEY_CERT_SIGN));
		Set<ExtendedKeyUsage> extendedUsages = new LinkedHashSet<>(List.of(ExtendedKeyUsage.IPSEC_IKE, ExtendedKeyUsage.SERVER_AUTH));
		CertificateSpec spec = new CertificateSpec(null, NOT_BEFORE, VALIDITY, List.of(EMAIL, DNS), usages, extendedUsages, null);
		assertEquals(List.of(EMAIL, DNS), spec.subjectAltNames());
		assertEquals(List.of(KeyUsage.DIGITAL_SIGNATURE, KeyUsage.KEY_CERT_SIGN, KeyUsage.CRL_SIGN), List.copyOf(spec.keyUsage()));
		assertEquals(List.of(ExtendedKeyUsage.SERVER_AUTH, ExtendedKeyUsage.IPSEC_IKE), List.copyOf(spec.extendedKeyUsage()));
	}
	
	@Test
	void isCaWithPathLength() {
		assertTrue(spec(null, 3).isCa());
	}
	
	@Test
	void isCaWithoutPathLength() {
		assertFalse(spec(null, null).isCa());
	}
	
	@Test
	void ofDefaults() {
		CertificateSpec spec = CertificateSpec.of(NOT_BEFORE, VALIDITY);
		assertNull(spec.serial());
		assertEquals(NOT_BEFORE, spec.notBefore());
		assertEquals(VALIDITY, spec.validity());
		assertTrue(spec.subjectAltNames().isEmpty());
		assertTrue(spec.keyUsage().isEmpty());
		assertTrue(spec.extendedKeyUsage().isEmpty());
		assertNull(spec.pathLength());
		assertFalse(spec.isCa());
	}
	
	@Test
	void notAfterAddsValidity() {
		assertEquals(Instant.parse("2026-04-01T00:00:00Z"), CertificateSpec.of(NOT_BEFORE, VALIDITY).notAfter());
	}
	
	@Test
	void withSerialCopies() {
		CertificateSpec original = CertificateSpec.of(NOT_BEFORE, VALIDITY).withKeyUsage(Set.of(KeyUsage.DIGITAL_SIGNATURE));
		CertificateSpec copy = original.withSerial(BigInteger.TEN);
		assertNotSame(original, copy);
		assertEquals(BigInteger.TEN, copy.serial());
		assertNull(original.serial());
		assertEquals(original.keyUsage(), copy.keyUsage());
		assertEquals(original.notBefore(), copy.notBefore());
	}
	
	@Test
	void withSubjectAltNamesArrayCopies() {
		CertificateSpec original = CertificateSpec.of(NOT_BEFORE, VALIDITY).withSubjectAltNames(DNS);
		CertificateSpec copy = original.withSubjectAltNames(EMAIL, DNS);
		assertEquals(List.of(EMAIL, DNS), copy.subjectAltNames());
		assertEquals(List.of(DNS), original.subjectAltNames());
	}
	
	@Test
	void withSubjectAltNamesListCopies() {
		CertificateSpec original = CertificateSpec.of(NOT_BEFORE, VALIDITY).withSubjectAltNames(List.of(DNS, EMAIL));
		assertEquals(List.of(DNS, EMAIL), original.subjectAltNames());
		CertificateSpec cleared = original.withSubjectAltNames(List.of());
		assertTrue(cleared.subjectAltNames().isEmpty());
		assertEquals(List.of(DNS, EMAIL), original.subjectAltNames());
	}
	
	@Test
	void withKeyUsageCopies() {
		CertificateSpec original = CertificateSpec.of(NOT_BEFORE, VALIDITY).withKeyUsage(Set.of(KeyUsage.KEY_AGREEMENT));
		CertificateSpec copy = original.withKeyUsage(Set.of(KeyUsage.KEY_CERT_SIGN, KeyUsage.CRL_SIGN));
		assertEquals(Set.of(KeyUsage.KEY_CERT_SIGN, KeyUsage.CRL_SIGN), copy.keyUsage());
		assertEquals(Set.of(KeyUsage.KEY_AGREEMENT), original.keyUsage());
	}
	
	@Test
	void withExtendedKeyUsageCopies() {
		CertificateSpec original = CertificateSpec.of(NOT_BEFORE, VALIDITY);
		CertificateSpec copy = original.withExtendedKeyUsage(Set.of(ExtendedKeyUsage.OCSP_SIGNING));
		assertEquals(Set.of(ExtendedKeyUsage.OCSP_SIGNING), copy.extendedKeyUsage());
		assertTrue(original.extendedKeyUsage().isEmpty());
	}
	
	@Test
	void withCaCopies() {
		CertificateSpec original = CertificateSpec.of(NOT_BEFORE, VALIDITY);
		CertificateSpec copy = original.withCa(2);
		assertEquals(2, copy.pathLength());
		assertTrue(copy.isCa());
		assertFalse(original.isCa());
	}
	
	@Test
	void collectionsCopiedDefensively() {
		List<SubjectAltName> names = new ArrayList<>(List.of(DNS));
		Set<KeyUsage> usages = EnumSet.of(KeyUsage.DIGITAL_SIGNATURE);
		Set<ExtendedKeyUsage> extendedUsages = EnumSet.of(ExtendedKeyUsage.CLIENT_AUTH);
		CertificateSpec spec = new CertificateSpec(null, NOT_BEFORE, VALIDITY, names, usages, extendedUsages, null);
		names.add(EMAIL);
		usages.add(KeyUsage.CRL_SIGN);
		extendedUsages.clear();
		assertEquals(List.of(DNS), spec.subjectAltNames());
		assertEquals(Set.of(KeyUsage.DIGITAL_SIGNATURE), spec.keyUsage());
		assertEquals(Set.of(ExtendedKeyUsage.CLIENT_AUTH), spec.extendedKeyUsage());
	}
	
	@Test
	void collectionsAreUnmodifiable() {
		CertificateSpec spec = new CertificateSpec(null, NOT_BEFORE, VALIDITY, List.of(DNS), Set.of(KeyUsage.DIGITAL_SIGNATURE), Set.of(ExtendedKeyUsage.SERVER_AUTH), null);
		assertThrows(UnsupportedOperationException.class, () -> spec.subjectAltNames().add(EMAIL));
		assertThrows(UnsupportedOperationException.class, () -> spec.keyUsage().add(KeyUsage.CRL_SIGN));
		assertThrows(UnsupportedOperationException.class, () -> spec.extendedKeyUsage().add(ExtendedKeyUsage.CODE_SIGNING));
		assertThrows(UnsupportedOperationException.class, () -> spec.keyUsage().clear());
	}
	
	@Test
	void chainedWithersKeepEveryComponent() {
		CertificateSpec chained = CertificateSpec.of(NOT_BEFORE, VALIDITY).withSerial(BigInteger.TWO).withSubjectAltNames(DNS, EMAIL)
			.withKeyUsage(Set.of(KeyUsage.KEY_CERT_SIGN)).withExtendedKeyUsage(Set.of(ExtendedKeyUsage.TIME_STAMPING)).withCa(0);
		CertificateSpec direct = new CertificateSpec(BigInteger.TWO, NOT_BEFORE, VALIDITY, List.of(DNS, EMAIL), Set.of(KeyUsage.KEY_CERT_SIGN), Set.of(ExtendedKeyUsage.TIME_STAMPING), 0);
		assertEquals(direct, chained);
		assertEquals(direct.hashCode(), chained.hashCode());
		assertTrue(chained.isCa());
	}
	
	@Test
	void equalityIgnoresSetImplementation() {
		CertificateSpec fromSetOf = new CertificateSpec(null, NOT_BEFORE, VALIDITY, List.of(), Set.of(KeyUsage.DIGITAL_SIGNATURE, KeyUsage.KEY_AGREEMENT), Set.of(ExtendedKeyUsage.SERVER_AUTH), null);
		CertificateSpec fromEnumSet = new CertificateSpec(null, NOT_BEFORE, VALIDITY, new ArrayList<>(), EnumSet.of(KeyUsage.KEY_AGREEMENT, KeyUsage.DIGITAL_SIGNATURE), EnumSet.of(ExtendedKeyUsage.SERVER_AUTH), null);
		assertEquals(fromSetOf, fromEnumSet);
		assertEquals(fromSetOf.hashCode(), fromEnumSet.hashCode());
		assertNotEquals(fromSetOf, fromSetOf.withCa(0));
	}
	
	@Test
	void notAfterWithFractionalValidity() {
		Duration validity = Duration.ofSeconds(1, 500);
		assertEquals(Instant.parse("2026-01-01T00:00:01.000000500Z"), CertificateSpec.of(NOT_BEFORE, validity).notAfter());
	}
}
