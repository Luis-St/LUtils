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
import net.luis.utils.crypto.algorithm.HashAlgorithm;
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.key.KeyId;
import net.luis.utils.crypto.util.PemDocument;
import net.luis.utils.io.network.address.IpAddresses;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.net.URI;
import java.security.KeyPair;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Certificates}.<br>
 *
 * @author Luis-St
 */
class CertificatesTest {
	
	private static final DistinguishedName ROOT_NAME = new DistinguishedName("LUtils Test Root", "LUtils", null, null, null, "DE");
	private static final List<SubjectAltName> NAMES = List.of(
		new SubjectAltName.Dns("api.example.com"),
		new SubjectAltName.Ip(IpAddresses.parse("192.0.2.1")),
		new SubjectAltName.Ip(IpAddresses.parse("2001:db8::1")),
		new SubjectAltName.Uri(URI.create("spiffe://example.com/service")),
		new SubjectAltName.Email("admin@example.com")
	);
	
	private static KeyPair rootPair;
	private static KeyPair leafPair;
	private static X509Certificate root;
	private static X509Certificate leaf;
	private static X509CRL crl;
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		rootPair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		leafPair = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		root = Certificates.builder().subject(ROOT_NAME).selfSigned(rootPair).validity(Instant.now().minus(Duration.ofHours(1)), Duration.ofDays(30)).ca(0).build(SignatureAlgorithm.ED25519);
		leaf = Certificates.builder().subject(DistinguishedName.ofCommonName("api.example.com")).subjectKey(leafPair.getPublic()).issuer(root, rootPair.getPrivate())
			.validity(Instant.now().minus(Duration.ofHours(1)), Duration.ofDays(7)).subjectAltName(NAMES.toArray(SubjectAltName[]::new)).build(SignatureAlgorithm.ED25519);
		crl = CertificateAuthority.create(ROOT_NAME, SignatureAlgorithm.ED25519, Duration.ofDays(1)).crl();
	}
	
	private static X509Certificate issueLeaf(KeyPair pair, DistinguishedName subject) {
		return Certificates.builder().subject(subject).subjectKey(pair.getPublic()).issuer(root, rootPair.getPrivate())
			.validity(Instant.now().minus(Duration.ofHours(1)), Duration.ofDays(1)).build(SignatureAlgorithm.ED25519);
	}
	
	@Test
	void builderReturnsNewBuilder() {
		CertificateBuilder first = Certificates.builder();
		CertificateBuilder second = Certificates.builder();
		assertNotNull(first);
		assertNotNull(second);
		assertNotSame(first, second);
	}
	
	@Test
	void parseWithNullBytes() {
		assertEquals("Encoded certificate must not be null", assertThrows(NullPointerException.class, () -> Certificates.parse((byte[]) null)).getMessage());
	}
	
	@Test
	void parseMalformedBytes() {
		assertThrows(MalformedDataException.class, () -> Certificates.parse(new byte[] { 1, 2, 3, 4, 5 }));
		assertThrows(MalformedDataException.class, () -> Certificates.parse(new byte[0]));
	}
	
	@Test
	void parseWithNullDocument() {
		assertEquals("Document must not be null", assertThrows(NullPointerException.class, () -> Certificates.parse((PemDocument) null)).getMessage());
	}
	
	@Test
	void parseDocumentWithWrongLabel() throws Exception {
		PemDocument document = new PemDocument(Pems.PUBLIC_KEY, leaf.getEncoded());
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Certificates.parse(document));
		assertTrue(exception.getMessage().contains(Pems.PUBLIC_KEY));
	}
	
	@Test
	void parseDocumentWithMalformedContent() {
		PemDocument document = new PemDocument(Pems.CERTIFICATE, new byte[] { 1, 2, 3 });
		assertThrows(MalformedDataException.class, () -> Certificates.parse(document));
	}
	
	@Test
	void parseChainWithNull() {
		assertEquals("Pem must not be null", assertThrows(NullPointerException.class, () -> Certificates.parseChain(null)).getMessage());
	}
	
	@Test
	void parseChainWithOtherLabel() {
		String pem = Pems.encode(leaf) + Pems.encode(leafPair.getPrivate());
		assertThrows(MalformedDataException.class, () -> Certificates.parseChain(pem));
	}
	
	@Test
	void tryParseWithNull() {
		assertEquals("Encoded certificate must not be null", assertThrows(NullPointerException.class, () -> Certificates.tryParse(null)).getMessage());
	}
	
	@Test
	void parseCrlWithNullBytes() {
		assertEquals("Encoded revocation list must not be null", assertThrows(NullPointerException.class, () -> Certificates.parseCrl((byte[]) null)).getMessage());
	}
	
	@Test
	void parseCrlMalformedBytes() {
		assertThrows(MalformedDataException.class, () -> Certificates.parseCrl(new byte[] { 1, 2, 3, 4, 5 }));
		assertThrows(MalformedDataException.class, () -> Certificates.parseCrl(new byte[0]));
	}
	
	@Test
	void parseCrlWithNullDocument() {
		assertEquals("Document must not be null", assertThrows(NullPointerException.class, () -> Certificates.parseCrl((PemDocument) null)).getMessage());
	}
	
	@Test
	void parseCrlDocumentWithWrongLabel() throws Exception {
		PemDocument document = new PemDocument(Pems.CERTIFICATE, crl.getEncoded());
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Certificates.parseCrl(document));
		assertTrue(exception.getMessage().contains(Pems.X509_CRL));
	}
	
	@Test
	void fingerprintWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> Certificates.fingerprint(null, leaf)).getMessage());
	}
	
	@Test
	void fingerprintWithNullCertificate() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> Certificates.fingerprint(HashAlgorithm.SHA_256, null)).getMessage());
	}
	
	@Test
	void idWithNull() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> Certificates.id(null)).getMessage());
	}
	
	@Test
	void isValidAtWithNullCertificate() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> Certificates.isValidAt(null, Instant.now())).getMessage());
	}
	
	@Test
	void isValidAtWithNullInstant() {
		assertEquals("Instant must not be null", assertThrows(NullPointerException.class, () -> Certificates.isValidAt(leaf, null)).getMessage());
	}
	
	@Test
	void isSelfSignedWithNull() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> Certificates.isSelfSigned(null)).getMessage());
	}
	
	@Test
	void subjectAltNamesWithNull() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> Certificates.subjectAltNames(null)).getMessage());
	}
	
	@Test
	void parseValidCertificate() throws Exception {
		X509Certificate parsed = Certificates.parse(leaf.getEncoded());
		assertEquals(leaf, parsed);
		assertEquals(leaf.getSubjectX500Principal(), parsed.getSubjectX500Principal());
		assertEquals(leaf.getSerialNumber(), parsed.getSerialNumber());
	}
	
	@Test
	void parseValidDocument() throws Exception {
		X509Certificate parsed = Certificates.parse(new PemDocument(Pems.CERTIFICATE, root.getEncoded()));
		assertEquals(root, parsed);
		assertEquals(root.getPublicKey(), parsed.getPublicKey());
	}
	
	@Test
	void tryParseValidCertificate() throws Exception {
		Optional<X509Certificate> parsed = Certificates.tryParse(leaf.getEncoded());
		assertTrue(parsed.isPresent());
		assertEquals(leaf, parsed.get());
	}
	
	@Test
	void tryParseMalformedBytes() {
		assertTrue(Certificates.tryParse(new byte[] { 1, 2, 3, 4, 5 }).isEmpty());
		assertTrue(Certificates.tryParse(new byte[0]).isEmpty());
	}
	
	@Test
	void parseCrlValidList() throws Exception {
		X509CRL parsed = Certificates.parseCrl(crl.getEncoded());
		assertEquals(crl, parsed);
		assertEquals(crl.getIssuerX500Principal(), parsed.getIssuerX500Principal());
	}
	
	@Test
	void parseCrlValidDocument() throws Exception {
		X509CRL parsed = Certificates.parseCrl(new PemDocument(Pems.X509_CRL, crl.getEncoded()));
		assertEquals(crl, parsed);
		assertEquals(crl.getNextUpdate(), parsed.getNextUpdate());
	}
	
	@Test
	void parseChainWithoutDocuments() {
		assertTrue(Certificates.parseChain("").isEmpty());
		assertTrue(Certificates.parseChain("no pem document in here").isEmpty());
	}
	
	@Test
	void isValidAtBeforeNotBefore() {
		Instant notBefore = leaf.getNotBefore().toInstant();
		assertFalse(Certificates.isValidAt(leaf, notBefore.minusMillis(1)));
		assertFalse(Certificates.isValidAt(leaf, notBefore.minus(Duration.ofDays(365))));
	}
	
	@Test
	void isValidAtNotBefore() {
		assertTrue(Certificates.isValidAt(leaf, leaf.getNotBefore().toInstant()));
	}
	
	@Test
	void isValidAtNotAfter() {
		assertTrue(Certificates.isValidAt(leaf, leaf.getNotAfter().toInstant()));
	}
	
	@Test
	void isValidAtAfterNotAfter() {
		Instant notAfter = leaf.getNotAfter().toInstant();
		assertFalse(Certificates.isValidAt(leaf, notAfter.plusMillis(1)));
		assertFalse(Certificates.isValidAt(leaf, notAfter.plus(Duration.ofDays(365))));
	}
	
	@Test
	void isSelfSignedWithDifferentIssuer() {
		assertNotEquals(leaf.getSubjectX500Principal(), leaf.getIssuerX500Principal());
		assertFalse(Certificates.isSelfSigned(leaf));
	}
	
	@Test
	void isSelfSignedRootCertificate() {
		assertEquals(root.getSubjectX500Principal(), root.getIssuerX500Principal());
		assertTrue(Certificates.isSelfSigned(root));
	}
	
	@Test
	void isSelfSignedWithForgedIssuerName() {
		X509Certificate forged = issueLeaf(leafPair, ROOT_NAME);
		assertEquals(forged.getSubjectX500Principal(), forged.getIssuerX500Principal());
		assertFalse(Certificates.isSelfSigned(forged));
	}
	
	@Test
	void subjectAltNamesWithoutExtension() {
		assertTrue(Certificates.subjectAltNames(root).isEmpty());
		assertTrue(Certificates.subjectAltNames(issueLeaf(leafPair, DistinguishedName.ofCommonName("plain"))).isEmpty());
	}
	
	@Test
	void subjectAltNamesSkipsUnmodeledKinds() throws Exception {
		Instant now = Instant.now();
		X509v3CertificateBuilder builder = new X509v3CertificateBuilder(
			new X500Name("CN=Issuer"), BigInteger.TEN, Date.from(now), Date.from(now.plus(Duration.ofDays(1))), new X500Name("CN=Subject"), SubjectPublicKeyInfo.getInstance(leafPair.getPublic().getEncoded())
		);
		GeneralNames names = new GeneralNames(new GeneralName[] { new GeneralName(new X500Name("CN=Directory")), new GeneralName(GeneralName.dNSName, "api.example.com") });
		builder.addExtension(Extension.subjectAlternativeName, false, names);
		X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(builder.build(new JcaContentSignerBuilder("Ed25519").build(leafPair.getPrivate())));
		
		assertEquals(List.of(new SubjectAltName.Dns("api.example.com")), Certificates.subjectAltNames(certificate));
	}
	
	@Test
	void fingerprintMatchesHashOfEncoding() throws Exception {
		byte[] fingerprint = Certificates.fingerprint(HashAlgorithm.SHA_256, leaf);
		assertEquals(32, fingerprint.length);
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, leaf.getEncoded()), fingerprint);
		assertArrayEquals(fingerprint, Certificates.fingerprint(HashAlgorithm.SHA_256, leaf));
	}
	
	@Test
	void fingerprintDiffersByAlgorithm() {
		byte[] sha256 = Certificates.fingerprint(HashAlgorithm.SHA_256, leaf);
		byte[] sha384 = Certificates.fingerprint(HashAlgorithm.SHA_384, leaf);
		assertEquals(48, sha384.length);
		assertFalse(Arrays.equals(sha256, Arrays.copyOf(sha384, 32)));
		assertFalse(Arrays.equals(sha256, Certificates.fingerprint(HashAlgorithm.SHA_256, root)));
	}
	
	@Test
	void idMatchesKeyIdOfPublicKey() {
		assertEquals(KeyId.of(leafPair.getPublic()), Certificates.id(leaf));
		assertEquals(KeyId.of(rootPair.getPublic()), Certificates.id(root));
	}
	
	@Test
	void isValidAtWithinValidity() {
		assertTrue(Certificates.isValidAt(leaf, Instant.now()));
		assertTrue(Certificates.isValidAt(root, Instant.now().plus(Duration.ofDays(10))));
	}
	
	@Test
	void parseChainSingleCertificate() {
		List<X509Certificate> chain = Certificates.parseChain(Pems.encode(leaf));
		assertEquals(1, chain.size());
		assertEquals(leaf, chain.getFirst());
	}
	
	@Test
	void parseChainKeepsOrder() {
		List<X509Certificate> chain = Certificates.parseChain(Pems.encodeChain(List.of(leaf, root)));
		assertEquals(2, chain.size());
		assertEquals(leaf, chain.getFirst());
		assertEquals(root, chain.get(1));
		assertThrows(UnsupportedOperationException.class, () -> chain.add(root));
	}
	
	@Test
	void idSharedByCertificatesOfSameKey() {
		X509Certificate other = issueLeaf(leafPair, DistinguishedName.ofCommonName("other.example.com"));
		assertNotEquals(leaf, other);
		assertEquals(Certificates.id(leaf), Certificates.id(other));
		assertNotEquals(Certificates.id(leaf), Certificates.id(root));
	}
	
	@Test
	void subjectAltNamesInWrittenOrder() {
		List<SubjectAltName> names = Certificates.subjectAltNames(leaf);
		assertEquals(NAMES, names);
		assertThrows(UnsupportedOperationException.class, () -> names.add(new SubjectAltName.Dns("other.example.com")));
	}
	
	@Test
	void parseRoundTripThroughPem() {
		X509Certificate parsed = Certificates.parse(Pems.decode(Pems.encode(leaf)));
		assertEquals(leaf, parsed);
		assertArrayEquals(Certificates.fingerprint(HashAlgorithm.SHA_256, leaf), Certificates.fingerprint(HashAlgorithm.SHA_256, parsed));
		assertEquals(NAMES, Certificates.subjectAltNames(parsed));
	}
}
