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

import net.luis.utils.crypto.*;
import net.luis.utils.crypto.algorithm.NativeSignatureAlgorithm;
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import net.luis.utils.crypto.exception.*;
import net.luis.utils.crypto.util.PemDocument;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.security.auth.x500.X500Principal;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CertificateRequests}.<br>
 *
 * @author Luis-St
 */
class CertificateRequestsTest {
	
	private static final DistinguishedName SUBJECT = DistinguishedName.ofCommonName("api.example.com");
	private static final List<SubjectAltName> NAMES = List.of(new SubjectAltName.Dns("api.example.com"));
	private static final CertificateSpec SPEC = CertificateSpec.of(Instant.now().minus(Duration.ofMinutes(5)), Duration.ofDays(30));
	
	private static KeyPair ed25519;
	private static KeyPair other;
	private static KeyPair caPair;
	private static X509Certificate ca;
	private static X509Certificate leaf;
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		ed25519 = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		other = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		caPair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		ca = Certificates.builder().subject(DistinguishedName.ofCommonName("Root")).selfSigned(caPair).validity(Instant.now().minus(Duration.ofMinutes(5)), Duration.ofDays(365)).ca(0).build(SignatureAlgorithm.ED25519);
		leaf = Certificates.builder().subject(DistinguishedName.ofCommonName("Leaf")).selfSigned(other).validity(Instant.now().minus(Duration.ofMinutes(5)), Duration.ofDays(365)).build(SignatureAlgorithm.ED25519);
	}
	
	private static CertificateRequest validRequest() {
		return CertificateRequests.parse(CertificateRequests.create(SUBJECT, ed25519, SignatureAlgorithm.ED25519, NAMES));
	}
	
	private static byte[] foreignRequest(SubjectPublicKeyInfo info, KeyPair signer) throws Exception {
		return new PKCS10CertificationRequestBuilder(new X500Name("CN=api.example.com"), info).build(new JcaContentSignerBuilder("Ed25519").build(signer.getPrivate())).getEncoded();
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = CertificateRequests.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(CertificateRequests.class.getModifiers()));
		
		Constructor<CertificateRequests> constructor = CertificateRequests.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void createWithNullSubject() {
		assertEquals("Subject must not be null", assertThrows(NullPointerException.class, () -> CertificateRequests.create(null, ed25519, SignatureAlgorithm.ED25519, NAMES)).getMessage());
	}
	
	@Test
	void createWithNullKeyPair() {
		assertEquals("Key pair must not be null", assertThrows(NullPointerException.class, () -> CertificateRequests.create(SUBJECT, null, SignatureAlgorithm.ED25519, NAMES)).getMessage());
	}
	
	@Test
	void createWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> CertificateRequests.create(SUBJECT, ed25519, null, NAMES)).getMessage());
	}
	
	@Test
	void createWithNullSubjectAltNames() {
		assertEquals("Subject alternative names must not be null", assertThrows(NullPointerException.class, () -> CertificateRequests.create(SUBJECT, ed25519, SignatureAlgorithm.ED25519, null)).getMessage());
	}
	
	@Test
	void createWithSubjectAltNamesHoldingNull() {
		List<SubjectAltName> names = Arrays.asList(new SubjectAltName.Dns("api.example.com"), null);
		assertThrows(NullPointerException.class, () -> CertificateRequests.create(SUBJECT, ed25519, SignatureAlgorithm.ED25519, names));
	}
	
	@Test
	void createWithHybridAlgorithm() {
		assertThrows(UnsupportedAlgorithmException.class, () -> CertificateRequests.create(SUBJECT, ed25519, SignatureAlgorithm.ED25519_ML_DSA_65, NAMES));
	}
	
	@Test
	void createWithUnknownAlgorithm() {
		NativeSignatureAlgorithm unknown = new NativeSignatureAlgorithm("Unknown", "Unknown", "Unknown", null, 0, false, false);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CertificateRequests.create(SUBJECT, ed25519, unknown, NAMES));
		assertEquals("Unknown signature type requested: Unknown", exception.getMessage());
	}
	
	@Test
	void createWithAlgorithmNotMatchingKey() {
		CryptoException exception = assertThrows(CryptoException.class, () -> CertificateRequests.create(SUBJECT, ed25519, SignatureAlgorithm.ECDSA_P256_SHA_256, NAMES));
		assertFalse(exception instanceof UnsupportedAlgorithmException);
	}
	
	@Test
	void parseBytesWithNull() {
		assertEquals("Encoded request must not be null", assertThrows(NullPointerException.class, () -> CertificateRequests.parse((byte[]) null)).getMessage());
	}
	
	@Test
	void parseBytesWithGarbage() {
		assertEquals("Malformed certificate request", assertThrows(MalformedDataException.class, () -> CertificateRequests.parse(new byte[] { 1, 2, 3, 4 })).getMessage());
	}
	
	@Test
	void parseBytesWithEmptyArray() {
		assertThrows(MalformedDataException.class, () -> CertificateRequests.parse(new byte[0]));
	}
	
	@Test
	void parseBytesWithUnreadableKey() throws Exception {
		byte[] der = foreignRequest(new SubjectPublicKeyInfo(new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.3.4")), new byte[32]), ed25519);
		assertThrows(MalformedDataException.class, () -> CertificateRequests.parse(der));
	}
	
	@Test
	void parseDocumentWithNull() {
		assertEquals("Document must not be null", assertThrows(NullPointerException.class, () -> CertificateRequests.parse((PemDocument) null)).getMessage());
	}
	
	@Test
	void parseDocumentWithWrongLabel() {
		PemDocument document = new PemDocument(Pems.CERTIFICATE, CertificateRequests.create(SUBJECT, ed25519, SignatureAlgorithm.ED25519, NAMES));
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CertificateRequests.parse(document));
		assertEquals("Expected a 'CERTIFICATE REQUEST' PEM document, got 'CERTIFICATE'", exception.getMessage());
	}
	
	@Test
	void parseDocumentWithMalformedContent() {
		PemDocument document = new PemDocument(Pems.CERTIFICATE_REQUEST, new byte[] { 1, 2, 3, 4 });
		assertEquals("Malformed certificate request", assertThrows(MalformedDataException.class, () -> CertificateRequests.parse(document)).getMessage());
	}
	
	@Test
	void signWithNullRequest() {
		assertEquals("Request must not be null", assertThrows(NullPointerException.class, () -> CertificateRequests.sign(null, ca, caPair.getPrivate(), SignatureAlgorithm.ED25519, SPEC)).getMessage());
	}
	
	@Test
	void signWithNullCa() {
		CertificateRequest request = validRequest();
		assertEquals("Certificate authority must not be null", assertThrows(NullPointerException.class, () -> CertificateRequests.sign(request, null, caPair.getPrivate(), SignatureAlgorithm.ED25519, SPEC)).getMessage());
	}
	
	@Test
	void signWithNullCaKey() {
		CertificateRequest request = validRequest();
		assertEquals("Certificate authority key must not be null", assertThrows(NullPointerException.class, () -> CertificateRequests.sign(request, ca, null, SignatureAlgorithm.ED25519, SPEC)).getMessage());
	}
	
	@Test
	void signWithNullAlgorithm() {
		CertificateRequest request = validRequest();
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> CertificateRequests.sign(request, ca, caPair.getPrivate(), null, SPEC)).getMessage());
	}
	
	@Test
	void signWithNullSpec() {
		CertificateRequest request = validRequest();
		assertEquals("Spec must not be null", assertThrows(NullPointerException.class, () -> CertificateRequests.sign(request, ca, caPair.getPrivate(), SignatureAlgorithm.ED25519, null)).getMessage());
	}
	
	@Test
	void signWithHybridAlgorithm() {
		CertificateRequest request = validRequest();
		assertThrows(UnsupportedAlgorithmException.class, () -> CertificateRequests.sign(request, ca, caPair.getPrivate(), SignatureAlgorithm.ED25519_ML_DSA_65, SPEC));
	}
	
	@Test
	void signWithNonAuthorityIssuer() {
		CertificateRequest request = validRequest();
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> CertificateRequests.sign(request, leaf, other.getPrivate(), SignatureAlgorithm.ED25519, SPEC));
		assertEquals("Issuer CN=Leaf is not a certificate authority", exception.getMessage());
	}
	
	@Test
	void signWithInvalidRequestSignature() throws Exception {
		CertificateRequest request = CertificateRequests.parse(foreignRequest(SubjectPublicKeyInfo.getInstance(ed25519.getPublic().getEncoded()), other));
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> CertificateRequests.sign(request, ca, caPair.getPrivate(), SignatureAlgorithm.ED25519, SPEC));
		assertEquals("Signature verification failed for the certificate request", exception.getMessage());
	}
	
	@Test
	void signWithUncheckableRequestSignature() throws Exception {
		CertificationRequest structure = CertificationRequest.getInstance(CertificateRequests.create(SUBJECT, ed25519, SignatureAlgorithm.ED25519, NAMES));
		CertificationRequest rewrapped = new CertificationRequest(
			structure.getCertificationRequestInfo(), new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.3.4")), structure.getSignature()
		);
		CertificateRequest request = CertificateRequests.parse(rewrapped.getEncoded());
		
		CryptoException exception = assertThrows(CryptoException.class, () -> CertificateRequests.sign(request, ca, caPair.getPrivate(), SignatureAlgorithm.ED25519, SPEC));
		assertFalse(exception instanceof AuthenticationException);
	}
	
	@Test
	void createWithoutSubjectAltNames() {
		CertificateRequest request = CertificateRequests.parse(CertificateRequests.create(SUBJECT, ed25519, SignatureAlgorithm.ED25519, List.of()));
		assertTrue(request.subjectAltNames().isEmpty());
		assertEquals(0, request.request().getAttributes(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest).length);
		assertTrue(request.verify());
	}
	
	@Test
	void createWithSubjectAltNames() {
		CertificateRequest request = validRequest();
		assertEquals(NAMES, request.subjectAltNames());
		assertEquals(1, request.request().getAttributes(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest).length);
		assertTrue(request.verify());
	}
	
	@Test
	void parseDocumentWithRequestLabel() {
		byte[] der = CertificateRequests.create(SUBJECT, ed25519, SignatureAlgorithm.ED25519, NAMES);
		CertificateRequest request = CertificateRequests.parse(new PemDocument(Pems.CERTIFICATE_REQUEST, der));
		assertArrayEquals(der, request.encoded());
		assertEquals(SUBJECT.toX500Principal(), request.subject());
		assertEquals(NAMES, request.subjectAltNames());
	}
	
	@Test
	void signValidRequest() throws Exception {
		CertificateRequest request = validRequest();
		X509Certificate certificate = CertificateRequests.sign(request, ca, caPair.getPrivate(), SignatureAlgorithm.ED25519, SPEC);
		
		assertEquals(request.subject(), certificate.getSubjectX500Principal());
		assertArrayEquals(ed25519.getPublic().getEncoded(), certificate.getPublicKey().getEncoded());
		assertEquals(ca.getSubjectX500Principal(), certificate.getIssuerX500Principal());
		assertDoesNotThrow(() -> certificate.verify(ca.getPublicKey()));
		assertEquals(-1, certificate.getBasicConstraints());
	}
	
	@Test
	void createProducesVerifiableRequest() {
		CertificateRequest request = validRequest();
		assertTrue(request.verify());
		assertEquals(new X500Principal("CN=api.example.com"), request.subject());
		assertArrayEquals(ed25519.getPublic().getEncoded(), request.publicKey().getEncoded());
	}
	
	@Test
	void parseBytesKeepsEncoding() {
		byte[] der = CertificateRequests.create(SUBJECT, ed25519, SignatureAlgorithm.ED25519, NAMES);
		assertArrayEquals(der, CertificateRequests.parse(der).encoded());
	}
	
	@Test
	void signDoesNotCopyRequestedNames() {
		CertificateRequest request = validRequest();
		X509Certificate certificate = CertificateRequests.sign(request, ca, caPair.getPrivate(), SignatureAlgorithm.ED25519, SPEC);
		assertEquals(NAMES, request.subjectAltNames());
		assertTrue(Certificates.subjectAltNames(certificate).isEmpty());
	}
	
	@Test
	void signUsesNamesFromSpec() {
		List<SubjectAltName> names = List.of(new SubjectAltName.Dns("other.example.com"));
		X509Certificate certificate = CertificateRequests.sign(validRequest(), ca, caPair.getPrivate(), SignatureAlgorithm.ED25519, SPEC.withSubjectAltNames(names));
		assertEquals(names, Certificates.subjectAltNames(certificate));
	}
	
	@Test
	void createWithAllNameKinds() {
		DistinguishedName subject = new DistinguishedName("api.example.com", "Example", "IT", "Berlin", "Berlin", "DE");
		List<SubjectAltName> names = List.of(
			new SubjectAltName.Dns("api.example.com"), new SubjectAltName.Ip(Ipv4Address.fromOctets(10, 0, 0, 1)),
			new SubjectAltName.Uri(URI.create("spiffe://example.com/api")), new SubjectAltName.Email("admin@example.com")
		);
		
		CertificateRequest request = CertificateRequests.parse(CertificateRequests.create(subject, ed25519, SignatureAlgorithm.ED25519, names));
		assertEquals(names, request.subjectAltNames());
		assertEquals(subject.toX500Principal(), request.subject());
		assertTrue(request.verify());
	}
	
	@Test
	void createWithEcdsaAndSignWithEd25519() {
		KeyPair pair = Signatures.generateKeyPair(SignatureAlgorithm.ECDSA_P256_SHA_256);
		CertificateRequest request = CertificateRequests.parse(CertificateRequests.create(SUBJECT, pair, SignatureAlgorithm.ECDSA_P256_SHA_256, NAMES));
		CertificateSpec spec = SPEC.withSubjectAltNames(request.subjectAltNames());
		
		X509Certificate certificate = CertificateRequests.sign(request, ca, caPair.getPrivate(), SignatureAlgorithm.ED25519, spec);
		assertArrayEquals(pair.getPublic().getEncoded(), certificate.getPublicKey().getEncoded());
		assertEquals(NAMES, Certificates.subjectAltNames(certificate));
		assertEquals(spec.notBefore().getEpochSecond(), certificate.getNotBefore().toInstant().getEpochSecond());
		assertEquals(spec.notAfter().getEpochSecond(), certificate.getNotAfter().toInstant().getEpochSecond());
	}
	
	@Test
	void pemRoundTrip() {
		byte[] der = CertificateRequests.create(SUBJECT, ed25519, SignatureAlgorithm.ED25519, NAMES);
		PemDocument document = Pems.decode(Pems.encode(Pems.CERTIFICATE_REQUEST, der));
		
		CertificateRequest request = CertificateRequests.parse(document);
		assertEquals(SUBJECT.toX500Principal(), request.subject());
		assertEquals(NAMES, request.subjectAltNames());
		assertTrue(request.verify());
	}
}
