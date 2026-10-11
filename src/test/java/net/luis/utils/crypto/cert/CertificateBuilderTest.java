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

import net.luis.utils.crypto.Providers;
import net.luis.utils.crypto.Signatures;
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.exception.UnsupportedAlgorithmException;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CertificateBuilder}.<br>
 *
 * @author Luis-St
 */
class CertificateBuilderTest {
	
	private static final Instant NOT_BEFORE = Instant.parse("2026-01-01T00:00:00Z");
	private static final Duration VALIDITY = Duration.ofDays(30);
	private static final DistinguishedName ROOT_NAME = DistinguishedName.ofCommonName("Test Root");
	private static final DistinguishedName LEAF_NAME = DistinguishedName.ofCommonName("Test Leaf");
	
	private static KeyPair rootPair;
	private static KeyPair leafPair;
	private static KeyPair ecPair;
	private static X509Certificate root;
	private static X509Certificate leaf;
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		rootPair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		leafPair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		ecPair = Signatures.generateKeyPair(SignatureAlgorithm.ECDSA_P256_SHA_256);
		root = Certificates.builder().subject(ROOT_NAME).selfSigned(rootPair).validity(NOT_BEFORE, VALIDITY).ca(1).build(SignatureAlgorithm.ED25519);
		leaf = Certificates.builder().subject(LEAF_NAME).subjectKey(leafPair.getPublic()).issuer(root, rootPair.getPrivate()).validity(NOT_BEFORE, VALIDITY).build(SignatureAlgorithm.ED25519);
	}
	
	private static CertificateBuilder selfSignedBuilder(KeyPair pair) {
		return Certificates.builder().subject(LEAF_NAME).selfSigned(pair).validity(NOT_BEFORE, VALIDITY);
	}
	
	private static byte[] subjectKeyId(X509Certificate certificate) {
		return SubjectKeyIdentifier.fromExtensions(X509Support.holder(certificate).getExtensions()).getKeyIdentifier();
	}
	
	private static byte[] authorityKeyId(X509Certificate certificate) {
		return AuthorityKeyIdentifier.fromExtensions(X509Support.holder(certificate).getExtensions()).getKeyIdentifier();
	}
	
	@Test
	void constructEmptyBuilder() {
		CertificateBuilder builder = new CertificateBuilder();
		assertEquals("Validity must be set", assertThrows(IllegalStateException.class, builder::spec).getMessage());
		assertEquals("Subject must be set", assertThrows(IllegalStateException.class, () -> builder.build(SignatureAlgorithm.ED25519)).getMessage());
	}
	
	@Test
	void constructThroughCertificates() {
		CertificateBuilder first = Certificates.builder();
		CertificateBuilder second = Certificates.builder();
		assertNotNull(first);
		assertNotNull(second);
		assertNotSame(first, second);
	}
	
	@Test
	void subjectWithNull() {
		assertEquals("Subject must not be null", assertThrows(NullPointerException.class, () -> Certificates.builder().subject(null)).getMessage());
	}
	
	@Test
	void subjectKeyWithNull() {
		assertEquals("Subject key must not be null", assertThrows(NullPointerException.class, () -> Certificates.builder().subjectKey(null)).getMessage());
	}
	
	@Test
	void issuerWithNullCertificate() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> Certificates.builder().issuer(null, rootPair.getPrivate())).getMessage());
	}
	
	@Test
	void issuerWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> Certificates.builder().issuer(root, null)).getMessage());
	}
	
	@Test
	void issuerWithEndEntityCertificate() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Certificates.builder().issuer(leaf, leafPair.getPrivate()));
		assertTrue(exception.getMessage().contains("CN=Test Leaf"));
		assertTrue(exception.getMessage().endsWith("is not a certificate authority"));
	}
	
	@Test
	void selfSignedWithNull() {
		assertEquals("Key pair must not be null", assertThrows(NullPointerException.class, () -> Certificates.builder().selfSigned(null)).getMessage());
	}
	
	@Test
	void serialWithNull() {
		assertEquals("Serial must not be null", assertThrows(NullPointerException.class, () -> Certificates.builder().serial(null)).getMessage());
	}
	
	@Test
	void validityWithNullNotBefore() {
		assertEquals("Not before must not be null", assertThrows(NullPointerException.class, () -> Certificates.builder().validity(null, VALIDITY)).getMessage());
	}
	
	@Test
	void validityWithNullValidity() {
		assertEquals("Validity must not be null", assertThrows(NullPointerException.class, () -> Certificates.builder().validity(NOT_BEFORE, null)).getMessage());
	}
	
	@Test
	void subjectAltNameWithNullArray() {
		assertEquals("Names must not be null", assertThrows(NullPointerException.class, () -> Certificates.builder().subjectAltName((SubjectAltName[]) null)).getMessage());
	}
	
	@Test
	void subjectAltNameWithNullElement() {
		assertThrows(NullPointerException.class, () -> Certificates.builder().subjectAltName(new SubjectAltName.Dns("a.example.com"), null));
	}
	
	@Test
	void keyUsageWithNull() {
		assertEquals("Usages must not be null", assertThrows(NullPointerException.class, () -> Certificates.builder().keyUsage(null)).getMessage());
	}
	
	@Test
	void keyUsageWithNullElement() {
		Set<KeyUsage> usages = new HashSet<>();
		usages.add(KeyUsage.DIGITAL_SIGNATURE);
		usages.add(null);
		assertThrows(NullPointerException.class, () -> Certificates.builder().keyUsage(usages));
	}
	
	@Test
	void extendedKeyUsageWithNull() {
		assertEquals("Usages must not be null", assertThrows(NullPointerException.class, () -> Certificates.builder().extendedKeyUsage(null)).getMessage());
	}
	
	@Test
	void extendedKeyUsageWithNullElement() {
		Set<ExtendedKeyUsage> usages = new HashSet<>();
		usages.add(ExtendedKeyUsage.SERVER_AUTH);
		usages.add(null);
		assertThrows(NullPointerException.class, () -> Certificates.builder().extendedKeyUsage(usages));
	}
	
	@Test
	void caWithNegativePathLength() {
		assertEquals("Path length must not be negative, was -1", assertThrows(IllegalArgumentException.class, () -> Certificates.builder().ca(-1)).getMessage());
	}
	
	@Test
	void specWithoutValidity() {
		CertificateBuilder builder = Certificates.builder().subject(LEAF_NAME).selfSigned(leafPair).serial(BigInteger.ONE);
		assertEquals("Validity must be set", assertThrows(IllegalStateException.class, builder::spec).getMessage());
	}
	
	@Test
	void specWithInvalidSerial() {
		CertificateBuilder builder = Certificates.builder().validity(NOT_BEFORE, VALIDITY);
		assertThrows(IllegalArgumentException.class, () -> builder.serial(BigInteger.ZERO).spec());
		assertThrows(IllegalArgumentException.class, () -> builder.serial(BigInteger.valueOf(-5)).spec());
		assertThrows(IllegalArgumentException.class, () -> builder.serial(BigInteger.ONE.shiftLeft(159)).spec());
	}
	
	@Test
	void specWithNonPositiveValidity() {
		assertThrows(IllegalArgumentException.class, () -> Certificates.builder().validity(NOT_BEFORE, Duration.ZERO).spec());
		assertThrows(IllegalArgumentException.class, () -> Certificates.builder().validity(NOT_BEFORE, Duration.ofDays(-1)).spec());
	}
	
	@Test
	void buildWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> selfSignedBuilder(leafPair).build(null)).getMessage());
	}
	
	@Test
	void buildWithHybridAlgorithm() {
		assertThrows(UnsupportedAlgorithmException.class, () -> Certificates.builder().build(SignatureAlgorithm.ED25519_ML_DSA_65));
		assertThrows(UnsupportedAlgorithmException.class, () -> selfSignedBuilder(leafPair).build(SignatureAlgorithm.ED25519_ML_DSA_65));
	}
	
	@Test
	void buildWithoutSubject() {
		CertificateBuilder builder = Certificates.builder().selfSigned(leafPair).validity(NOT_BEFORE, VALIDITY);
		assertEquals("Subject must be set", assertThrows(IllegalStateException.class, () -> builder.build(SignatureAlgorithm.ED25519)).getMessage());
	}
	
	@Test
	void buildWithoutSubjectKey() {
		CertificateBuilder builder = Certificates.builder().subject(LEAF_NAME).validity(NOT_BEFORE, VALIDITY);
		assertEquals("Subject key must be set", assertThrows(IllegalStateException.class, () -> builder.build(SignatureAlgorithm.ED25519)).getMessage());
	}
	
	@Test
	void buildWithoutIssuer() {
		CertificateBuilder builder = Certificates.builder().subject(LEAF_NAME).subjectKey(leafPair.getPublic()).validity(NOT_BEFORE, VALIDITY);
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> builder.build(SignatureAlgorithm.ED25519));
		assertEquals("Issuer must be set, either through issuer or through selfSigned", exception.getMessage());
	}
	
	@Test
	void buildWithoutValidity() {
		CertificateBuilder builder = Certificates.builder().subject(LEAF_NAME).selfSigned(leafPair);
		assertEquals("Validity must be set", assertThrows(IllegalStateException.class, () -> builder.build(SignatureAlgorithm.ED25519)).getMessage());
	}
	
	@Test
	void buildWithInvalidSerial() {
		CertificateBuilder builder = selfSignedBuilder(leafPair).serial(BigInteger.ZERO);
		assertThrows(IllegalArgumentException.class, () -> builder.build(SignatureAlgorithm.ED25519));
	}
	
	@Test
	void buildWithMismatchedKey() {
		CertificateBuilder builder = selfSignedBuilder(ecPair);
		assertThrows(CryptoException.class, () -> builder.build(SignatureAlgorithm.ED25519));
	}
	
	@Test
	void issuerWithCertificateAuthority() {
		CertificateBuilder builder = Certificates.builder();
		assertEquals(1, root.getBasicConstraints());
		assertSame(builder, builder.issuer(root, rootPair.getPrivate()));
	}
	
	@Test
	void issuerWithPathLengthZeroAuthority() throws Exception {
		KeyPair authorityPair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		X509Certificate authority = Certificates.builder().subject(ROOT_NAME).selfSigned(authorityPair).validity(NOT_BEFORE, VALIDITY).ca(0).build(SignatureAlgorithm.ED25519);
		assertEquals(0, authority.getBasicConstraints());
		
		X509Certificate issued = Certificates.builder().subject(LEAF_NAME).subjectKey(leafPair.getPublic()).issuer(authority, authorityPair.getPrivate()).validity(NOT_BEFORE, VALIDITY).build(SignatureAlgorithm.ED25519);
		assertEquals(authority.getSubjectX500Principal(), issued.getIssuerX500Principal());
		assertDoesNotThrow(() -> issued.verify(authorityPair.getPublic()));
	}
	
	@Test
	void caWithZeroPathLength() {
		CertificateSpec spec = Certificates.builder().validity(NOT_BEFORE, VALIDITY).ca(0).spec();
		assertEquals(0, spec.pathLength());
		assertTrue(spec.isCa());
	}
	
	@Test
	void buildSelfSigned() {
		X509Certificate certificate = selfSignedBuilder(leafPair).build(SignatureAlgorithm.ED25519);
		assertEquals(certificate.getSubjectX500Principal(), certificate.getIssuerX500Principal());
		assertEquals("CN=Test Leaf", certificate.getSubjectX500Principal().getName());
		assertTrue(Certificates.isSelfSigned(certificate));
		assertEquals(leafPair.getPublic(), certificate.getPublicKey());
		assertArrayEquals(subjectKeyId(certificate), authorityKeyId(certificate));
	}
	
	@Test
	void buildIssuedByAuthority() {
		assertEquals(root.getSubjectX500Principal(), leaf.getIssuerX500Principal());
		assertEquals("CN=Test Leaf", leaf.getSubjectX500Principal().getName());
		assertDoesNotThrow(() -> leaf.verify(rootPair.getPublic()));
		assertFalse(Certificates.isSelfSigned(leaf));
		assertEquals(leafPair.getPublic(), leaf.getPublicKey());
		assertArrayEquals(subjectKeyId(root), authorityKeyId(leaf));
	}
	
	@Test
	void issuerReplacesSelfSigned() {
		KeyPair pair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		X509Certificate certificate = selfSignedBuilder(pair).issuer(root, rootPair.getPrivate()).build(SignatureAlgorithm.ED25519);
		assertEquals(root.getSubjectX500Principal(), certificate.getIssuerX500Principal());
		assertEquals(pair.getPublic(), certificate.getPublicKey());
		assertDoesNotThrow(() -> certificate.verify(rootPair.getPublic()));
		assertFalse(Certificates.isSelfSigned(certificate));
	}
	
	@Test
	void selfSignedReplacesIssuer() {
		KeyPair pair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		CertificateBuilder builder = Certificates.builder().subject(LEAF_NAME).subjectKey(leafPair.getPublic()).issuer(root, rootPair.getPrivate()).validity(NOT_BEFORE, VALIDITY);
		X509Certificate certificate = builder.selfSigned(pair).build(SignatureAlgorithm.ED25519);
		assertEquals(certificate.getSubjectX500Principal(), certificate.getIssuerX500Principal());
		assertEquals(pair.getPublic(), certificate.getPublicKey());
		assertTrue(Certificates.isSelfSigned(certificate));
	}
	
	@Test
	void buildWithoutSerialDrawsRandom() {
		CertificateBuilder builder = selfSignedBuilder(leafPair);
		BigInteger first = builder.build(SignatureAlgorithm.ED25519).getSerialNumber();
		BigInteger second = builder.build(SignatureAlgorithm.ED25519).getSerialNumber();
		assertEquals(1, first.signum());
		assertTrue(first.bitLength() <= 128);
		assertEquals(1, second.signum());
		assertNotEquals(first, second);
	}
	
	@Test
	void buildWithSerial() {
		BigInteger serial = BigInteger.valueOf(123456789L);
		X509Certificate certificate = selfSignedBuilder(leafPair).serial(serial).build(SignatureAlgorithm.ED25519);
		assertEquals(serial, certificate.getSerialNumber());
	}
	
	@Test
	void buildWithoutCa() {
		X509Certificate certificate = selfSignedBuilder(leafPair).build(SignatureAlgorithm.ED25519);
		assertEquals(-1, certificate.getBasicConstraints());
		assertEquals(-1, leaf.getBasicConstraints());
	}
	
	@Test
	void buildWithCa() {
		X509Certificate certificate = selfSignedBuilder(leafPair).ca(3).build(SignatureAlgorithm.ED25519);
		assertEquals(3, certificate.getBasicConstraints());
		assertEquals(1, root.getBasicConstraints());
	}
	
	@Test
	void buildWithoutExtensions() throws Exception {
		X509Certificate certificate = selfSignedBuilder(leafPair).build(SignatureAlgorithm.ED25519);
		assertNull(certificate.getKeyUsage());
		assertNull(certificate.getExtendedKeyUsage());
		assertNull(certificate.getSubjectAlternativeNames());
		assertTrue(Certificates.subjectAltNames(certificate).isEmpty());
	}
	
	@Test
	void specWithDefaults() {
		CertificateSpec spec = Certificates.builder().validity(NOT_BEFORE, VALIDITY).spec();
		assertNull(spec.serial());
		assertTrue(spec.subjectAltNames().isEmpty());
		assertTrue(spec.keyUsage().isEmpty());
		assertTrue(spec.extendedKeyUsage().isEmpty());
		assertNull(spec.pathLength());
		assertFalse(spec.isCa());
	}
	
	@Test
	void methodChainingConsistency() {
		CertificateBuilder builder = Certificates.builder();
		assertSame(builder, builder.subject(LEAF_NAME));
		assertSame(builder, builder.subjectKey(leafPair.getPublic()));
		assertSame(builder, builder.issuer(root, rootPair.getPrivate()));
		assertSame(builder, builder.selfSigned(leafPair));
		assertSame(builder, builder.serial(BigInteger.TEN));
		assertSame(builder, builder.validity(NOT_BEFORE, VALIDITY));
		assertSame(builder, builder.subjectAltName(new SubjectAltName.Dns("a.example.com")));
		assertSame(builder, builder.keyUsage(Set.of(KeyUsage.DIGITAL_SIGNATURE)));
		assertSame(builder, builder.extendedKeyUsage(Set.of(ExtendedKeyUsage.SERVER_AUTH)));
		assertSame(builder, builder.ca(0));
	}
	
	@Test
	void specReflectsBuilderState() {
		SubjectAltName name = new SubjectAltName.Dns("a.example.com");
		CertificateSpec spec = Certificates.builder().serial(BigInteger.TEN).validity(NOT_BEFORE, VALIDITY).subjectAltName(name)
			.keyUsage(Set.of(KeyUsage.KEY_CERT_SIGN)).extendedKeyUsage(Set.of(ExtendedKeyUsage.CODE_SIGNING)).ca(2).spec();
		assertEquals(BigInteger.TEN, spec.serial());
		assertEquals(NOT_BEFORE, spec.notBefore());
		assertEquals(VALIDITY, spec.validity());
		assertEquals(List.of(name), spec.subjectAltNames());
		assertEquals(Set.of(KeyUsage.KEY_CERT_SIGN), spec.keyUsage());
		assertEquals(Set.of(ExtendedKeyUsage.CODE_SIGNING), spec.extendedKeyUsage());
		assertEquals(2, spec.pathLength());
	}
	
	@Test
	void buildWithKeyUsage() {
		X509Certificate certificate = selfSignedBuilder(leafPair).keyUsage(Set.of(KeyUsage.DIGITAL_SIGNATURE, KeyUsage.KEY_ENCIPHERMENT)).build(SignatureAlgorithm.ED25519);
		boolean[] bits = certificate.getKeyUsage();
		assertNotNull(bits);
		assertTrue(bits[0]);
		assertFalse(bits[1]);
		assertTrue(bits[2]);
		for (int i = 3; i < bits.length; i++) {
			assertFalse(bits[i]);
		}
	}
	
	@Test
	void buildWithExtendedKeyUsage() throws Exception {
		X509Certificate certificate = selfSignedBuilder(leafPair).extendedKeyUsage(Set.of(ExtendedKeyUsage.SERVER_AUTH, ExtendedKeyUsage.CLIENT_AUTH)).build(SignatureAlgorithm.ED25519);
		List<String> oids = certificate.getExtendedKeyUsage();
		assertEquals(2, oids.size());
		assertTrue(oids.contains(ExtendedKeyUsage.SERVER_AUTH.oid()));
		assertTrue(oids.contains(ExtendedKeyUsage.CLIENT_AUTH.oid()));
	}
	
	@Test
	void buildWithSubjectAltNames() {
		SubjectAltName dns = new SubjectAltName.Dns("api.example.com");
		SubjectAltName email = new SubjectAltName.Email("admin@example.com");
		X509Certificate certificate = selfSignedBuilder(leafPair).subjectAltName(dns, email).build(SignatureAlgorithm.ED25519);
		assertEquals(List.of(dns, email), Certificates.subjectAltNames(certificate));
	}
	
	@Test
	void buildValidityPeriod() {
		X509Certificate certificate = selfSignedBuilder(leafPair).build(SignatureAlgorithm.ED25519);
		assertEquals(NOT_BEFORE, certificate.getNotBefore().toInstant());
		assertEquals(NOT_BEFORE.plus(VALIDITY), certificate.getNotAfter().toInstant());
		assertTrue(Certificates.isValidAt(certificate, NOT_BEFORE));
		assertFalse(Certificates.isValidAt(certificate, NOT_BEFORE.minusSeconds(1)));
	}
	
	@Test
	void buildWithEcdsa() {
		X509Certificate certificate = selfSignedBuilder(ecPair).build(SignatureAlgorithm.ECDSA_P256_SHA_256);
		assertEquals("SHA256withECDSA", certificate.getSigAlgName());
		assertEquals(ecPair.getPublic(), certificate.getPublicKey());
		assertTrue(Certificates.isSelfSigned(certificate));
	}
	
	@Test
	void buildChainOfThree() {
		KeyPair intermediatePair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		KeyPair endPair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		DistinguishedName intermediateName = DistinguishedName.ofCommonName("Test Intermediate");
		X509Certificate intermediate = Certificates.builder().subject(intermediateName).subjectKey(intermediatePair.getPublic()).issuer(root, rootPair.getPrivate())
			.validity(NOT_BEFORE, VALIDITY).keyUsage(Set.of(KeyUsage.KEY_CERT_SIGN, KeyUsage.CRL_SIGN)).ca(0).build(SignatureAlgorithm.ED25519);
		X509Certificate end = Certificates.builder().subject(LEAF_NAME).subjectKey(endPair.getPublic()).issuer(intermediate, intermediatePair.getPrivate())
			.validity(NOT_BEFORE, VALIDITY).build(SignatureAlgorithm.ED25519);
		
		assertDoesNotThrow(() -> intermediate.verify(rootPair.getPublic()));
		assertDoesNotThrow(() -> end.verify(intermediatePair.getPublic()));
		assertEquals(intermediate.getSubjectX500Principal(), end.getIssuerX500Principal());
		assertEquals(0, intermediate.getBasicConstraints());
		assertEquals(-1, end.getBasicConstraints());
		assertArrayEquals(subjectKeyId(intermediate), authorityKeyId(end));
	}
	
	@Test
	void buildWithMlDsa() {
		KeyPair pair = Signatures.generateKeyPair(SignatureAlgorithm.ML_DSA_44);
		X509Certificate certificate = selfSignedBuilder(pair).ca(0).build(SignatureAlgorithm.ML_DSA_44);
		assertEquals(pair.getPublic().getEncoded().length, certificate.getPublicKey().getEncoded().length);
		assertTrue(Certificates.isSelfSigned(certificate));
		assertEquals(0, certificate.getBasicConstraints());
	}
	
	@Test
	void setterReplacesEarlierValue() {
		SubjectAltName later = new SubjectAltName.Dns("b.example.com");
		CertificateSpec spec = Certificates.builder().serial(BigInteger.ONE).serial(BigInteger.TWO).validity(NOT_BEFORE, VALIDITY).validity(NOT_BEFORE.plusSeconds(60), Duration.ofDays(1))
			.subjectAltName(new SubjectAltName.Dns("a.example.com")).subjectAltName(later).keyUsage(Set.of(KeyUsage.CRL_SIGN)).keyUsage(Set.of(KeyUsage.KEY_AGREEMENT))
			.extendedKeyUsage(Set.of(ExtendedKeyUsage.SERVER_AUTH)).extendedKeyUsage(Set.of(ExtendedKeyUsage.TIME_STAMPING)).ca(5).ca(1).spec();
		assertEquals(BigInteger.TWO, spec.serial());
		assertEquals(NOT_BEFORE.plusSeconds(60), spec.notBefore());
		assertEquals(Duration.ofDays(1), spec.validity());
		assertEquals(List.of(later), spec.subjectAltNames());
		assertEquals(Set.of(KeyUsage.KEY_AGREEMENT), spec.keyUsage());
		assertEquals(Set.of(ExtendedKeyUsage.TIME_STAMPING), spec.extendedKeyUsage());
		assertEquals(1, spec.pathLength());
	}
	
	@Test
	void keyUsageCopiedDefensively() {
		Set<KeyUsage> usages = new HashSet<>(Set.of(KeyUsage.DIGITAL_SIGNATURE));
		Set<ExtendedKeyUsage> extendedUsages = new HashSet<>(Set.of(ExtendedKeyUsage.CLIENT_AUTH));
		CertificateBuilder builder = Certificates.builder().validity(NOT_BEFORE, VALIDITY).keyUsage(usages).extendedKeyUsage(extendedUsages);
		usages.add(KeyUsage.CRL_SIGN);
		extendedUsages.clear();
		
		CertificateSpec spec = builder.spec();
		assertEquals(Set.of(KeyUsage.DIGITAL_SIGNATURE), spec.keyUsage());
		assertEquals(Set.of(ExtendedKeyUsage.CLIENT_AUTH), spec.extendedKeyUsage());
	}
	
	@Test
	void builderReuseAfterBuild() {
		CertificateBuilder builder = selfSignedBuilder(leafPair);
		X509Certificate first = builder.build(SignatureAlgorithm.ED25519);
		assertEquals("CN=Test Leaf", first.getSubjectX500Principal().getName());
		
		X509Certificate second = builder.subject(DistinguishedName.ofCommonName("Other")).build(SignatureAlgorithm.ED25519);
		assertEquals("CN=Other", second.getSubjectX500Principal().getName());
		assertEquals("CN=Test Leaf", first.getSubjectX500Principal().getName());
		assertNotEquals(first.getSerialNumber(), second.getSerialNumber());
	}
	
	@Test
	void buildWithAllKeyUsages() {
		X509Certificate certificate = selfSignedBuilder(leafPair).keyUsage(EnumSet.allOf(KeyUsage.class)).build(SignatureAlgorithm.ED25519);
		boolean[] bits = certificate.getKeyUsage();
		assertNotNull(bits);
		assertTrue(bits.length >= 9);
		for (KeyUsage usage : KeyUsage.values()) {
			assertTrue(bits[usage.bit()], usage.name());
		}
	}
}
