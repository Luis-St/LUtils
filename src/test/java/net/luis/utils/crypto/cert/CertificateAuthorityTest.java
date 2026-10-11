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
import net.luis.utils.crypto.exception.AuthenticationException;
import net.luis.utils.crypto.exception.UnsupportedAlgorithmException;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.SignatureException;
import java.security.cert.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CertificateAuthority}.<br>
 *
 * @author Luis-St
 */
class CertificateAuthorityTest {
	
	private static final DistinguishedName ROOT_NAME = DistinguishedName.ofCommonName("Test Root");
	private static final DistinguishedName SUBJECT = DistinguishedName.ofCommonName("subject");
	private static final Duration VALIDITY = Duration.ofDays(30);
	private static final SubjectAltName DNS = new SubjectAltName.Dns("api.example.com");
	private static final String SERVER_AUTH = "1.3.6.1.5.5.7.3.1";
	private static final String CLIENT_AUTH = "1.3.6.1.5.5.7.3.2";
	private static final String CRL_NUMBER = "2.5.29.20";
	
	private static KeyPair subjectPair;
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		subjectPair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
	}
	
	private static CertificateAuthority newAuthority() {
		return CertificateAuthority.create(ROOT_NAME, SignatureAlgorithm.ED25519, Duration.ofDays(365));
	}
	
	private static CertificateSpec spec() {
		return CertificateSpec.of(Instant.now(), VALIDITY);
	}
	
	private static BigInteger crlNumber(X509CRL crl) {
		return ASN1Integer.getInstance(ASN1OctetString.getInstance(crl.getExtensionValue(CRL_NUMBER)).getOctets()).getValue();
	}
	
	@Test
	void createAuthority() throws Exception {
		CertificateAuthority ca = newAuthority();
		X509Certificate root = ca.certificate();
		assertTrue(Certificates.isSelfSigned(root));
		assertEquals(0, root.getBasicConstraints());
		assertTrue(root.getKeyUsage()[0]);
		assertTrue(root.getKeyUsage()[5]);
		assertTrue(root.getKeyUsage()[6]);
		assertEquals(ROOT_NAME.toX500Principal(), root.getSubjectX500Principal());
		assertEquals(SignatureAlgorithm.ED25519, ca.algorithm());
		assertEquals(BigInteger.ONE, ca.nextSerial());
		assertDoesNotThrow(() -> root.checkValidity());
	}
	
	@Test
	void createWithNullSubject() {
		assertEquals("Subject must not be null", assertThrows(NullPointerException.class, () -> CertificateAuthority.create(null, SignatureAlgorithm.ED25519, VALIDITY)).getMessage());
	}
	
	@Test
	void createWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> CertificateAuthority.create(ROOT_NAME, null, VALIDITY)).getMessage());
	}
	
	@Test
	void createWithNullValidity() {
		assertEquals("Validity must not be null", assertThrows(NullPointerException.class, () -> CertificateAuthority.create(ROOT_NAME, SignatureAlgorithm.ED25519, null)).getMessage());
	}
	
	@Test
	void ofRestoresAuthority() {
		KeyPair pair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		X509Certificate root = Certificates.builder().subject(ROOT_NAME).selfSigned(pair).validity(Instant.now(), VALIDITY).ca(0).build(SignatureAlgorithm.ED25519);
		CertificateAuthority restored = CertificateAuthority.of(root, pair.getPrivate(), SignatureAlgorithm.ED25519, BigInteger.valueOf(42));
		assertSame(root, restored.certificate());
		assertEquals(SignatureAlgorithm.ED25519, restored.algorithm());
		assertEquals(BigInteger.valueOf(42), restored.nextSerial());
	}
	
	@Test
	void ofWithNullCertificate() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> CertificateAuthority.of(null, subjectPair.getPrivate(), SignatureAlgorithm.ED25519, BigInteger.ONE)).getMessage());
	}
	
	@Test
	void ofWithNullKey() {
		X509Certificate root = newAuthority().certificate();
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> CertificateAuthority.of(root, null, SignatureAlgorithm.ED25519, BigInteger.ONE)).getMessage());
	}
	
	@Test
	void ofWithNullAlgorithm() {
		X509Certificate root = newAuthority().certificate();
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> CertificateAuthority.of(root, subjectPair.getPrivate(), null, BigInteger.ONE)).getMessage());
	}
	
	@Test
	void ofWithNullSerial() {
		X509Certificate root = newAuthority().certificate();
		assertEquals("Next serial must not be null", assertThrows(NullPointerException.class, () -> CertificateAuthority.of(root, subjectPair.getPrivate(), SignatureAlgorithm.ED25519, null)).getMessage());
	}
	
	@Test
	void createWithHybridAlgorithm() {
		assertThrows(UnsupportedAlgorithmException.class, () -> CertificateAuthority.create(ROOT_NAME, SignatureAlgorithm.ED25519_ML_DSA_65, VALIDITY));
	}
	
	@Test
	void createWithZeroValidity() {
		assertThrows(IllegalArgumentException.class, () -> CertificateAuthority.create(ROOT_NAME, SignatureAlgorithm.ED25519, Duration.ZERO));
	}
	
	@Test
	void createWithNegativeValidity() {
		assertThrows(IllegalArgumentException.class, () -> CertificateAuthority.create(ROOT_NAME, SignatureAlgorithm.ED25519, Duration.ofDays(-1)));
	}
	
	@Test
	void ofWithHybridAlgorithm() {
		X509Certificate root = newAuthority().certificate();
		assertThrows(UnsupportedAlgorithmException.class, () -> CertificateAuthority.of(root, subjectPair.getPrivate(), SignatureAlgorithm.ED25519_ML_DSA_65, BigInteger.ONE));
	}
	
	@Test
	void ofWithEndEntityCertificate() {
		X509Certificate leaf = newAuthority().issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		assertEquals(-1, leaf.getBasicConstraints());
		assertThrows(IllegalArgumentException.class, () -> CertificateAuthority.of(leaf, subjectPair.getPrivate(), SignatureAlgorithm.ED25519, BigInteger.ONE));
	}
	
	@Test
	void ofWithZeroSerial() {
		X509Certificate root = newAuthority().certificate();
		assertThrows(IllegalArgumentException.class, () -> CertificateAuthority.of(root, subjectPair.getPrivate(), SignatureAlgorithm.ED25519, BigInteger.ZERO));
	}
	
	@Test
	void ofWithNegativeSerial() {
		X509Certificate root = newAuthority().certificate();
		assertThrows(IllegalArgumentException.class, () -> CertificateAuthority.of(root, subjectPair.getPrivate(), SignatureAlgorithm.ED25519, BigInteger.valueOf(-5)));
	}
	
	@Test
	void ofWithKeyNotMatchingCertificate() {
		X509Certificate root = newAuthority().certificate();
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CertificateAuthority.of(root, subjectPair.getPrivate(), SignatureAlgorithm.ED25519, BigInteger.ONE));
		assertEquals("Key does not belong to certificate CN=Test Root", exception.getMessage());
	}
	
	@Test
	void issueServerWithNullSubject() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Subject must not be null", assertThrows(NullPointerException.class, () -> ca.issueServer(null, subjectPair.getPublic(), VALIDITY, DNS)).getMessage());
		assertEquals(BigInteger.ONE, ca.nextSerial());
	}
	
	@Test
	void issueServerWithNullKey() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> ca.issueServer(SUBJECT, null, VALIDITY, DNS)).getMessage());
		assertEquals(BigInteger.ONE, ca.nextSerial());
	}
	
	@Test
	void issueServerWithNullValidity() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Validity must not be null", assertThrows(NullPointerException.class, () -> ca.issueServer(SUBJECT, subjectPair.getPublic(), null, DNS)).getMessage());
	}
	
	@Test
	void issueServerWithNullNames() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Names must not be null", assertThrows(NullPointerException.class, () -> ca.issueServer(SUBJECT, subjectPair.getPublic(), VALIDITY, (SubjectAltName[]) null)).getMessage());
	}
	
	@Test
	void issueServerWithNullName() {
		CertificateAuthority ca = newAuthority();
		assertThrows(NullPointerException.class, () -> ca.issueServer(SUBJECT, subjectPair.getPublic(), VALIDITY, DNS, null));
		assertEquals(BigInteger.ONE, ca.nextSerial());
	}
	
	@Test
	void issueServerWithoutNames() {
		CertificateAuthority ca = newAuthority();
		assertThrows(IllegalArgumentException.class, () -> ca.issueServer(SUBJECT, subjectPair.getPublic(), VALIDITY));
		assertEquals(BigInteger.ONE, ca.nextSerial());
	}
	
	@Test
	void issueServerWithZeroValidity() {
		CertificateAuthority ca = newAuthority();
		assertThrows(IllegalArgumentException.class, () -> ca.issueServer(SUBJECT, subjectPair.getPublic(), Duration.ZERO, DNS));
		assertEquals(BigInteger.ONE, ca.nextSerial());
	}
	
	@Test
	void issueClientWithNullSubject() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Subject must not be null", assertThrows(NullPointerException.class, () -> ca.issueClient(null, subjectPair.getPublic(), VALIDITY)).getMessage());
	}
	
	@Test
	void issueClientWithNullKey() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> ca.issueClient(SUBJECT, null, VALIDITY)).getMessage());
	}
	
	@Test
	void issueClientWithNullValidity() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Validity must not be null", assertThrows(NullPointerException.class, () -> ca.issueClient(SUBJECT, subjectPair.getPublic(), null)).getMessage());
	}
	
	@Test
	void issueClientWithNullNames() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Names must not be null", assertThrows(NullPointerException.class, () -> ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY, (SubjectAltName[]) null)).getMessage());
	}
	
	@Test
	void issueClientWithNegativeValidity() {
		CertificateAuthority ca = newAuthority();
		assertThrows(IllegalArgumentException.class, () -> ca.issueClient(SUBJECT, subjectPair.getPublic(), Duration.ofSeconds(-1)));
	}
	
	@Test
	void issueWithNullSubject() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Subject must not be null", assertThrows(NullPointerException.class, () -> ca.issue(null, subjectPair.getPublic(), spec())).getMessage());
	}
	
	@Test
	void issueWithNullKey() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> ca.issue(SUBJECT, null, spec())).getMessage());
	}
	
	@Test
	void issueWithNullSpec() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Spec must not be null", assertThrows(NullPointerException.class, () -> ca.issue(SUBJECT, subjectPair.getPublic(), null)).getMessage());
		assertEquals(BigInteger.ONE, ca.nextSerial());
	}
	
	@Test
	void signWithNullRequest() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Request must not be null", assertThrows(NullPointerException.class, () -> ca.sign(null, spec())).getMessage());
	}
	
	@Test
	void signWithNullSpec() {
		CertificateAuthority ca = newAuthority();
		CertificateRequest request = CertificateRequests.parse(CertificateRequests.create(SUBJECT, subjectPair, SignatureAlgorithm.ED25519, List.of()));
		assertEquals("Spec must not be null", assertThrows(NullPointerException.class, () -> ca.sign(request, null)).getMessage());
	}
	
	@Test
	void signWithTamperedRequest() {
		CertificateAuthority ca = newAuthority();
		byte[] der = CertificateRequests.create(SUBJECT, subjectPair, SignatureAlgorithm.ED25519, List.of());
		der[der.length - 1] ^= 0x01;
		CertificateRequest request = CertificateRequests.parse(der);
		
		assertThrows(AuthenticationException.class, () -> ca.sign(request, spec()));
		assertEquals(BigInteger.ONE, ca.nextSerial());
	}
	
	@Test
	void revokeWithNullSerial() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Serial must not be null", assertThrows(NullPointerException.class, () -> ca.revoke(null, RevocationReason.KEY_COMPROMISE)).getMessage());
	}
	
	@Test
	void revokeWithNullReason() {
		CertificateAuthority ca = newAuthority();
		ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		assertEquals("Reason must not be null", assertThrows(NullPointerException.class, () -> ca.revoke(BigInteger.ONE, null)).getMessage());
		assertFalse(ca.isRevoked(BigInteger.ONE));
	}
	
	@Test
	void revokeWithoutReasonWithNullSerial() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Serial must not be null", assertThrows(NullPointerException.class, () -> ca.revoke(null)).getMessage());
	}
	
	@Test
	void isRevokedWithNullSerial() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Serial must not be null", assertThrows(NullPointerException.class, () -> ca.isRevoked(null)).getMessage());
	}
	
	@Test
	void crlWithNullValidity() {
		CertificateAuthority ca = newAuthority();
		assertEquals("Validity must not be null", assertThrows(NullPointerException.class, () -> ca.crl(null)).getMessage());
	}
	
	@Test
	void crlWithZeroValidity() {
		CertificateAuthority ca = newAuthority();
		assertThrows(IllegalArgumentException.class, () -> ca.crl(Duration.ZERO));
		assertEquals(BigInteger.ONE, crlNumber(ca.crl()));
	}
	
	@Test
	void crlWithNegativeValidity() {
		CertificateAuthority ca = newAuthority();
		assertThrows(IllegalArgumentException.class, () -> ca.crl(Duration.ofDays(-1)));
		assertEquals(BigInteger.ONE, crlNumber(ca.crl()));
	}
	
	@Test
	void ofWithCaCertificate() {
		X509Certificate root = Certificates.builder().subject(ROOT_NAME).selfSigned(subjectPair).validity(Instant.now(), VALIDITY).ca(0).build(SignatureAlgorithm.ED25519);
		CertificateAuthority restored = CertificateAuthority.of(root, subjectPair.getPrivate(), SignatureAlgorithm.ED25519, BigInteger.TEN);
		X509Certificate issued = restored.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		assertEquals(BigInteger.TEN, issued.getSerialNumber());
		assertEquals(BigInteger.valueOf(11), restored.nextSerial());
	}
	
	@Test
	void revokeZeroSerial() {
		CertificateAuthority ca = newAuthority();
		ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		assertThrows(IllegalArgumentException.class, () -> ca.revoke(BigInteger.ZERO, RevocationReason.KEY_COMPROMISE));
		assertFalse(ca.isRevoked(BigInteger.ZERO));
	}
	
	@Test
	void revokeNegativeSerial() {
		CertificateAuthority ca = newAuthority();
		ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		assertThrows(IllegalArgumentException.class, () -> ca.revoke(BigInteger.valueOf(-1), RevocationReason.KEY_COMPROMISE));
	}
	
	@Test
	void revokeNextSerial() {
		CertificateAuthority ca = newAuthority();
		assertThrows(IllegalArgumentException.class, () -> ca.revoke(BigInteger.ONE, RevocationReason.KEY_COMPROMISE));
		ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		assertThrows(IllegalArgumentException.class, () -> ca.revoke(BigInteger.TWO, RevocationReason.KEY_COMPROMISE));
	}
	
	@Test
	void revokeSerialAboveNextSerial() {
		CertificateAuthority ca = newAuthority();
		ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		assertThrows(IllegalArgumentException.class, () -> ca.revoke(BigInteger.valueOf(100), RevocationReason.KEY_COMPROMISE));
		assertFalse(ca.isRevoked(BigInteger.valueOf(100)));
	}
	
	@Test
	void revokeIssuedSerial() {
		CertificateAuthority ca = newAuthority();
		X509Certificate issued = ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		assertFalse(ca.isRevoked(issued.getSerialNumber()));
		assertTrue(ca.revoke(issued.getSerialNumber(), RevocationReason.KEY_COMPROMISE));
		assertTrue(ca.isRevoked(issued.getSerialNumber()));
	}
	
	@Test
	void revokeAlreadyRevokedSerial() {
		CertificateAuthority ca = newAuthority();
		BigInteger serial = ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY).getSerialNumber();
		assertTrue(ca.revoke(serial, RevocationReason.KEY_COMPROMISE));
		assertFalse(ca.revoke(serial, RevocationReason.SUPERSEDED));
		assertFalse(ca.revoke(serial));
		
		X509CRLEntry entry = ca.crl().getRevokedCertificate(serial);
		assertEquals(CRLReason.KEY_COMPROMISE, entry.getRevocationReason());
	}
	
	@Test
	void revokeWithoutReasonUsesUnspecified() {
		CertificateAuthority ca = newAuthority();
		BigInteger serial = ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY).getSerialNumber();
		assertTrue(ca.revoke(serial));
		assertTrue(ca.isRevoked(serial));
		
		X509CRLEntry entry = ca.crl().getRevokedCertificate(serial);
		assertNotNull(entry);
		assertNull(entry.getRevocationReason());
	}
	
	@Test
	void isRevokedReturnsFalseForNotRevoked() {
		CertificateAuthority ca = newAuthority();
		ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		assertFalse(ca.isRevoked(BigInteger.ONE));
		assertFalse(ca.isRevoked(BigInteger.valueOf(999)));
		assertFalse(ca.isRevoked(BigInteger.ZERO));
	}
	
	@Test
	void crlWithoutRevocations() {
		CertificateAuthority ca = newAuthority();
		ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		X509CRL crl = ca.crl();
		assertNull(crl.getRevokedCertificates());
		assertEquals(ca.certificate().getSubjectX500Principal(), crl.getIssuerX500Principal());
	}
	
	@Test
	void crlWithUnspecifiedReasonOmitsReason() {
		CertificateAuthority ca = newAuthority();
		BigInteger serial = ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY).getSerialNumber();
		ca.revoke(serial, RevocationReason.UNSPECIFIED);
		
		X509CRLEntry entry = ca.crl().getRevokedCertificate(serial);
		assertNotNull(entry);
		assertNull(entry.getRevocationReason());
	}
	
	@Test
	void crlWithReasonCarriesReason() {
		CertificateAuthority ca = newAuthority();
		BigInteger serial = ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY).getSerialNumber();
		ca.revoke(serial, RevocationReason.KEY_COMPROMISE);
		
		X509CRLEntry entry = ca.crl().getRevokedCertificate(serial);
		assertNotNull(entry);
		assertEquals(CRLReason.KEY_COMPROMISE, entry.getRevocationReason());
	}
	
	@Test
	void issueServerWithName() throws Exception {
		CertificateAuthority ca = newAuthority();
		X509Certificate server = ca.issueServer(SUBJECT, subjectPair.getPublic(), VALIDITY, DNS);
		assertEquals(List.of(SERVER_AUTH), server.getExtendedKeyUsage());
		assertTrue(server.getKeyUsage()[0]);
		assertFalse(server.getKeyUsage()[5]);
		assertEquals(-1, server.getBasicConstraints());
		assertEquals(List.of(DNS), Certificates.subjectAltNames(server));
		assertEquals(SUBJECT.toX500Principal(), server.getSubjectX500Principal());
	}
	
	@Test
	void issueClientWithoutNames() throws Exception {
		CertificateAuthority ca = newAuthority();
		X509Certificate client = ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		assertEquals(List.of(CLIENT_AUTH), client.getExtendedKeyUsage());
		assertTrue(client.getKeyUsage()[0]);
		assertEquals(-1, client.getBasicConstraints());
		assertTrue(Certificates.subjectAltNames(client).isEmpty());
		assertEquals(subjectPair.getPublic(), client.getPublicKey());
	}
	
	@Test
	void certificateAndAlgorithmAccessors() {
		CertificateAuthority ca = CertificateAuthority.create(ROOT_NAME, SignatureAlgorithm.ECDSA_P256_SHA_256, VALIDITY);
		assertSame(ca.certificate(), ca.certificate());
		assertEquals(SignatureAlgorithm.ECDSA_P256_SHA_256, ca.algorithm());
		assertEquals("EC", ca.certificate().getPublicKey().getAlgorithm());
	}
	
	@Test
	void issueAdvancesSerial() {
		CertificateAuthority ca = newAuthority();
		assertEquals(BigInteger.ONE, ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY).getSerialNumber());
		assertEquals(BigInteger.TWO, ca.issueServer(SUBJECT, subjectPair.getPublic(), VALIDITY, DNS).getSerialNumber());
		assertEquals(BigInteger.valueOf(3), ca.issue(SUBJECT, subjectPair.getPublic(), spec()).getSerialNumber());
		assertEquals(BigInteger.valueOf(4), ca.nextSerial());
	}
	
	@Test
	void issueIgnoresSpecSerial() {
		CertificateAuthority ca = newAuthority();
		X509Certificate issued = ca.issue(SUBJECT, subjectPair.getPublic(), spec().withSerial(BigInteger.valueOf(777)));
		assertEquals(BigInteger.ONE, issued.getSerialNumber());
		assertEquals(BigInteger.TWO, ca.nextSerial());
	}
	
	@Test
	void issuedCertificateVerifiesAgainstRoot() throws Exception {
		CertificateAuthority ca = newAuthority();
		X509Certificate issued = ca.issue(SUBJECT, subjectPair.getPublic(), spec().withKeyUsage(Set.of(KeyUsage.KEY_ENCIPHERMENT)));
		assertDoesNotThrow(() -> issued.verify(ca.certificate().getPublicKey()));
		assertEquals(ca.certificate().getSubjectX500Principal(), issued.getIssuerX500Principal());
		assertTrue(issued.getKeyUsage()[2]);
		assertNull(issued.getExtendedKeyUsage());
	}
	
	@Test
	void signValidRequest() {
		CertificateAuthority ca = newAuthority();
		CertificateRequest request = CertificateRequests.parse(CertificateRequests.create(SUBJECT, subjectPair, SignatureAlgorithm.ED25519, List.of(DNS)));
		X509Certificate issued = ca.sign(request, spec().withSerial(BigInteger.valueOf(500)));
		
		assertEquals(SUBJECT.toX500Principal(), issued.getSubjectX500Principal());
		assertEquals(subjectPair.getPublic(), issued.getPublicKey());
		assertTrue(Certificates.subjectAltNames(issued).isEmpty());
		assertEquals(BigInteger.ONE, issued.getSerialNumber());
		assertDoesNotThrow(() -> issued.verify(ca.certificate().getPublicKey()));
	}
	
	@Test
	void crlDefaultValidityIsSevenDays() {
		CertificateAuthority ca = newAuthority();
		X509CRL crl = ca.crl();
		assertEquals(Duration.ofDays(7), Duration.between(crl.getThisUpdate().toInstant(), crl.getNextUpdate().toInstant()));
		
		X509CRL custom = ca.crl(Duration.ofHours(1));
		assertEquals(Duration.ofHours(1), Duration.between(custom.getThisUpdate().toInstant(), custom.getNextUpdate().toInstant()));
	}
	
	@Test
	void crlIsSignedByRoot() {
		CertificateAuthority ca = newAuthority();
		X509CRL crl = ca.crl();
		assertDoesNotThrow(() -> crl.verify(ca.certificate().getPublicKey()));
		assertEquals(ca.certificate().getSubjectX500Principal(), crl.getIssuerX500Principal());
		assertNotNull(crl.getExtensionValue("2.5.29.35"));
	}
	
	@Test
	void crlNumberIncreases() {
		CertificateAuthority ca = newAuthority();
		assertEquals(BigInteger.ONE, crlNumber(ca.crl()));
		assertEquals(BigInteger.TWO, crlNumber(ca.crl(Duration.ofDays(1))));
		assertEquals(BigInteger.valueOf(3), crlNumber(ca.crl()));
	}
	
	@Test
	void crlListsRevocationsInOrder() {
		CertificateAuthority ca = newAuthority();
		for (int i = 0; i < 4; i++) {
			ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		}
		ca.revoke(BigInteger.valueOf(3), RevocationReason.SUPERSEDED);
		ca.revoke(BigInteger.ONE);
		ca.revoke(BigInteger.valueOf(4), RevocationReason.CA_COMPROMISE);
		
		X509CRL crl = ca.crl();
		assertEquals(3, crl.getRevokedCertificates().size());
		assertEquals(CRLReason.SUPERSEDED, crl.getRevokedCertificate(BigInteger.valueOf(3)).getRevocationReason());
		assertNull(crl.getRevokedCertificate(BigInteger.ONE).getRevocationReason());
		assertEquals(CRLReason.CA_COMPROMISE, crl.getRevokedCertificate(BigInteger.valueOf(4)).getRevocationReason());
		assertNull(crl.getRevokedCertificate(BigInteger.TWO));
	}
	
	@Test
	void restoredAuthorityContinuesSerials() {
		KeyPair rootPair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		X509Certificate root = Certificates.builder().subject(ROOT_NAME).selfSigned(rootPair).validity(Instant.now(), VALIDITY).ca(0).build(SignatureAlgorithm.ED25519);
		CertificateAuthority first = CertificateAuthority.of(root, rootPair.getPrivate(), SignatureAlgorithm.ED25519, BigInteger.ONE);
		X509Certificate old = first.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		
		CertificateAuthority second = CertificateAuthority.of(root, rootPair.getPrivate(), SignatureAlgorithm.ED25519, first.nextSerial());
		X509Certificate fresh = second.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY);
		assertEquals(BigInteger.TWO, fresh.getSerialNumber());
		assertDoesNotThrow(() -> old.verify(second.certificate().getPublicKey()));
		assertDoesNotThrow(() -> fresh.verify(root.getPublicKey()));
		assertFalse(second.isRevoked(BigInteger.ONE));
	}
	
	@Test
	void intermediateAuthorityIssuesLeaf() {
		CertificateAuthority root = newAuthority();
		KeyPair intermediatePair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		CertificateSpec caSpec = spec().withCa(0).withKeyUsage(Set.of(KeyUsage.KEY_CERT_SIGN, KeyUsage.CRL_SIGN));
		X509Certificate intermediateCertificate = root.issue(DistinguishedName.ofCommonName("Intermediate"), intermediatePair.getPublic(), caSpec);
		assertEquals(0, intermediateCertificate.getBasicConstraints());
		
		CertificateAuthority intermediate = CertificateAuthority.of(intermediateCertificate, intermediatePair.getPrivate(), SignatureAlgorithm.ED25519, BigInteger.ONE);
		X509Certificate leaf = intermediate.issueServer(SUBJECT, subjectPair.getPublic(), VALIDITY, DNS);
		assertDoesNotThrow(() -> leaf.verify(intermediatePair.getPublic()));
		assertEquals(intermediateCertificate.getSubjectX500Principal(), leaf.getIssuerX500Principal());
		assertThrows(SignatureException.class, () -> leaf.verify(root.certificate().getPublicKey()));
	}
	
	@Test
	void concurrentIssueYieldsDistinctSerials() throws Exception {
		CertificateAuthority ca = newAuthority();
		List<Callable<BigInteger>> tasks = new ArrayList<>();
		for (int i = 0; i < 16; i++) {
			tasks.add(() -> ca.issueClient(SUBJECT, subjectPair.getPublic(), VALIDITY).getSerialNumber());
		}
		
		TreeSet<BigInteger> serials = new TreeSet<>();
		try (ExecutorService executor = Executors.newFixedThreadPool(4)) {
			for (Future<BigInteger> future : executor.invokeAll(tasks)) {
				serials.add(future.get());
			}
		}
		assertEquals(16, serials.size());
		assertEquals(BigInteger.ONE, serials.first());
		assertEquals(BigInteger.valueOf(16), serials.last());
		assertEquals(BigInteger.valueOf(17), ca.nextSerial());
	}
}
