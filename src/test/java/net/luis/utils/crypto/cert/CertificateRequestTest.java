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
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.security.auth.x500.X500Principal;
import java.net.URI;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CertificateRequest}.<br>
 *
 * @author Luis-St
 */
class CertificateRequestTest {
	
	private static final X500Name SUBJECT = new X500Name("CN=api.example.com");
	
	private static KeyPair ed25519;
	private static KeyPair other;
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		ed25519 = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		other = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
	}
	
	private static PKCS10CertificationRequestBuilder builder(X500Name subject, KeyPair pair) {
		return new PKCS10CertificationRequestBuilder(subject, SubjectPublicKeyInfo.getInstance(pair.getPublic().getEncoded()));
	}
	
	private static PKCS10CertificationRequest sign(PKCS10CertificationRequestBuilder builder, PrivateKey key) throws Exception {
		return builder.build(new JcaContentSignerBuilder("Ed25519").build(key));
	}
	
	private static Extensions sanExtensions(GeneralName... names) throws Exception {
		return new Extensions(new Extension(Extension.subjectAlternativeName, false, new GeneralNames(names).getEncoded()));
	}
	
	private static PKCS10CertificationRequest requestWithNames(GeneralName... names) throws Exception {
		return sign(builder(SUBJECT, ed25519).addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, sanExtensions(names)), ed25519.getPrivate());
	}
	
	@Test
	void constructFromRequest() throws Exception {
		CertificateRequest request = new CertificateRequest(sign(builder(SUBJECT, ed25519), ed25519.getPrivate()));
		assertEquals(new X500Principal("CN=api.example.com"), request.subject());
		assertArrayEquals(ed25519.getPublic().getEncoded(), request.publicKey().getEncoded());
		assertTrue(request.subjectAltNames().isEmpty());
	}
	
	@Test
	void constructWithNullRequest() {
		assertEquals("Request must not be null", assertThrows(NullPointerException.class, () -> new CertificateRequest(null)).getMessage());
	}
	
	@Test
	void constructWithUnknownKeyAlgorithm() throws Exception {
		SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.3.4")), new byte[32]);
		PKCS10CertificationRequest request = sign(new PKCS10CertificationRequestBuilder(SUBJECT, info), ed25519.getPrivate());
		assertThrows(MalformedDataException.class, () -> new CertificateRequest(request));
	}
	
	@Test
	void constructWithMalformedKey() throws Exception {
		SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519), new byte[3]);
		PKCS10CertificationRequest request = sign(new PKCS10CertificationRequestBuilder(SUBJECT, info), ed25519.getPrivate());
		assertThrows(MalformedDataException.class, () -> new CertificateRequest(request));
	}
	
	@Test
	void constructWithTwoExtensionRequestAttributes() throws Exception {
		PKCS10CertificationRequestBuilder builder = builder(SUBJECT, ed25519)
			.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, sanExtensions(new GeneralName(GeneralName.dNSName, "a.example.com")))
			.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, sanExtensions(new GeneralName(GeneralName.dNSName, "b.example.com")));
		PKCS10CertificationRequest request = sign(builder, ed25519.getPrivate());
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> new CertificateRequest(request));
		assertEquals("A certificate request must ask for extensions at most once", exception.getMessage());
	}
	
	@Test
	void constructWithExtensionRequestHoldingTwoValues() throws Exception {
		ASN1Encodable[] values = {
			sanExtensions(new GeneralName(GeneralName.dNSName, "a.example.com")), sanExtensions(new GeneralName(GeneralName.dNSName, "b.example.com"))
		};
		PKCS10CertificationRequest request = sign(builder(SUBJECT, ed25519).addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, values), ed25519.getPrivate());
		
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> new CertificateRequest(request));
		assertEquals("A certificate request must ask for extensions at most once", exception.getMessage());
	}
	
	@Test
	void verifyWithUnknownSignatureAlgorithm() throws Exception {
		PKCS10CertificationRequest signed = sign(builder(SUBJECT, ed25519), ed25519.getPrivate());
		CertificationRequest structure = signed.toASN1Structure();
		CertificationRequest rewrapped = new CertificationRequest(
			structure.getCertificationRequestInfo(), new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.3.4")), structure.getSignature()
		);
		
		CertificateRequest request = new CertificateRequest(new PKCS10CertificationRequest(rewrapped));
		assertThrows(CryptoException.class, request::verify);
	}
	
	@Test
	void subjectAltNamesWithoutExtensionRequest() throws Exception {
		CertificateRequest request = new CertificateRequest(sign(builder(SUBJECT, ed25519), ed25519.getPrivate()));
		assertEquals(List.of(), request.subjectAltNames());
	}
	
	@Test
	void subjectAltNamesWithSingleExtensionRequest() throws Exception {
		CertificateRequest request = new CertificateRequest(requestWithNames(new GeneralName(GeneralName.dNSName, "api.example.com")));
		assertEquals(List.of(new SubjectAltName.Dns("api.example.com")), request.subjectAltNames());
	}
	
	@Test
	void subjectAltNamesWithoutSubjectAltNameExtension() throws Exception {
		Extensions extensions = new Extensions(new Extension(Extension.basicConstraints, true, new BasicConstraints(false).getEncoded()));
		PKCS10CertificationRequest parsed = sign(builder(SUBJECT, ed25519).addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extensions), ed25519.getPrivate());
		
		CertificateRequest request = new CertificateRequest(parsed);
		assertTrue(request.subjectAltNames().isEmpty());
		assertTrue(request.verify());
	}
	
	@Test
	void subjectAltNamesSkipUnmodeledKinds() throws Exception {
		CertificateRequest request = new CertificateRequest(requestWithNames(
			new GeneralName(new X500Name("CN=directory")), new GeneralName(GeneralName.dNSName, "api.example.com")
		));
		assertEquals(List.of(new SubjectAltName.Dns("api.example.com")), request.subjectAltNames());
	}
	
	@Test
	void verifyValidSignature() throws Exception {
		CertificateRequest request = new CertificateRequest(sign(builder(SUBJECT, ed25519), ed25519.getPrivate()));
		assertTrue(request.verify());
	}
	
	@Test
	void verifySignatureOfDifferentKey() throws Exception {
		CertificateRequest request = new CertificateRequest(sign(builder(SUBJECT, ed25519), other.getPrivate()));
		assertFalse(request.verify());
		assertArrayEquals(ed25519.getPublic().getEncoded(), request.publicKey().getEncoded());
	}
	
	@Test
	void subjectKeepsAllAttributes() throws Exception {
		X500Name subject = new X500Name("CN=a,SERIALNUMBER=42");
		CertificateRequest request = new CertificateRequest(sign(builder(subject, ed25519), ed25519.getPrivate()));
		assertArrayEquals(subject.getEncoded(ASN1Encoding.DER), request.subject().getEncoded());
	}
	
	@Test
	void subjectAltNamesAreUnmodifiable() throws Exception {
		CertificateRequest request = new CertificateRequest(requestWithNames(new GeneralName(GeneralName.dNSName, "api.example.com")));
		assertThrows(UnsupportedOperationException.class, () -> request.subjectAltNames().add(new SubjectAltName.Dns("other.example.com")));
	}
	
	@Test
	void encodedMatchesInput() throws Exception {
		PKCS10CertificationRequest parsed = sign(builder(SUBJECT, ed25519), ed25519.getPrivate());
		CertificateRequest request = new CertificateRequest(parsed);
		assertArrayEquals(parsed.getEncoded(), request.encoded());
		assertArrayEquals(request.encoded(), request.encoded());
	}
	
	@Test
	void requestReturnsParsedRequest() throws Exception {
		CertificateRequest request = new CertificateRequest(sign(builder(SUBJECT, ed25519), ed25519.getPrivate()));
		assertNotNull(request.request());
		assertArrayEquals(request.encoded(), request.request().getEncoded());
	}
	
	@Test
	void toStringContainsSubjectAndKey() throws Exception {
		CertificateRequest request = new CertificateRequest(sign(builder(SUBJECT, ed25519), ed25519.getPrivate()));
		assertEquals("CertificateRequest[subject=CN=api.example.com, key=" + request.publicKey().getAlgorithm() + "]", request.toString());
	}
	
	@Test
	void subjectAltNamesKeepOrderOfAllKinds() throws Exception {
		CertificateRequest request = new CertificateRequest(requestWithNames(
			new GeneralName(GeneralName.dNSName, "api.example.com"), new GeneralName(GeneralName.iPAddress, "127.0.0.1"),
			new GeneralName(GeneralName.uniformResourceIdentifier, "spiffe://example.com/api"), new GeneralName(GeneralName.rfc822Name, "admin@example.com")
		));
		List<SubjectAltName> expected = List.of(
			new SubjectAltName.Dns("api.example.com"), new SubjectAltName.Ip(Ipv4Address.fromOctets(127, 0, 0, 1)),
			new SubjectAltName.Uri(URI.create("spiffe://example.com/api")), new SubjectAltName.Email("admin@example.com")
		);
		assertEquals(expected, request.subjectAltNames());
	}
	
	@Test
	void verifyEcdsaRequest() {
		KeyPair pair = Signatures.generateKeyPair(SignatureAlgorithm.ECDSA_P256_SHA_256);
		byte[] der = CertificateRequests.create(DistinguishedName.ofCommonName("api.example.com"), pair, SignatureAlgorithm.ECDSA_P256_SHA_256, List.of());
		
		CertificateRequest request = CertificateRequests.parse(der);
		assertTrue(request.verify());
		assertEquals("EC", request.publicKey().getAlgorithm());
		assertArrayEquals(der, request.encoded());
	}
}
