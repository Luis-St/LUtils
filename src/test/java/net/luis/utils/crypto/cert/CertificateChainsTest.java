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

import net.luis.utils.crypto.Signatures;
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.net.URI;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CertificateChains}.<br>
 *
 * @author Luis-St
 */
class CertificateChainsTest {
	
	private static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.ECDSA_P256_SHA_256;
	private static final RevocationPolicy NONE = new RevocationPolicy.None();
	
	private static CertificateAuthority authority;
	private static X509Certificate root;
	private static X509Certificate leaf;
	private static X509Certificate otherRoot;
	private static X509Certificate deepRoot;
	private static X509Certificate intermediate;
	private static X509Certificate deepLeaf;
	
	@BeforeAll
	static void setUp() {
		authority = CertificateAuthority.create(DistinguishedName.ofCommonName("Chain Root"), ALGORITHM, Duration.ofDays(365));
		root = authority.certificate();
		leaf = issueServer(authority, "leaf.example");
		otherRoot = CertificateAuthority.create(DistinguishedName.ofCommonName("Other Root"), ALGORITHM, Duration.ofDays(365)).certificate();
		
		KeyPair rootPair = Signatures.generateKeyPair(ALGORITHM);
		deepRoot = Certificates.builder().subject(DistinguishedName.ofCommonName("Deep Root")).selfSigned(rootPair).validity(Instant.now().minus(Duration.ofMinutes(5)), Duration.ofDays(365))
			.keyUsage(Set.of(KeyUsage.KEY_CERT_SIGN, KeyUsage.CRL_SIGN)).ca(1).build(ALGORITHM);
		KeyPair intermediatePair = Signatures.generateKeyPair(ALGORITHM);
		intermediate = Certificates.builder().subject(DistinguishedName.ofCommonName("Deep Intermediate")).subjectKey(intermediatePair.getPublic()).issuer(deepRoot, rootPair.getPrivate())
			.validity(Instant.now().minus(Duration.ofMinutes(5)), Duration.ofDays(180)).keyUsage(Set.of(KeyUsage.KEY_CERT_SIGN, KeyUsage.CRL_SIGN)).ca(0).build(ALGORITHM);
		deepLeaf = issueServer(CertificateAuthority.of(intermediate, intermediatePair.getPrivate(), ALGORITHM, BigInteger.ONE), "deep.example");
	}
	
	private static X509Certificate issueServer(CertificateAuthority issuer, String name) {
		return issuer.issueServer(DistinguishedName.ofCommonName(name), Signatures.generateKeyPair(ALGORITHM).getPublic(), Duration.ofDays(30), new SubjectAltName.Dns(name));
	}
	
	@Test
	void validateWithNullChain() {
		assertEquals("Chain must not be null", assertThrows(NullPointerException.class, () -> CertificateChains.validate(null, TrustStore.of(root), Instant.now(), NONE)).getMessage());
	}
	
	@Test
	void validateWithNullTrustStore() {
		assertEquals("Trust store must not be null", assertThrows(NullPointerException.class, () -> CertificateChains.validate(List.of(leaf), null, Instant.now(), NONE)).getMessage());
	}
	
	@Test
	void validateWithNullInstant() {
		assertEquals("Instant must not be null", assertThrows(NullPointerException.class, () -> CertificateChains.validate(List.of(leaf), TrustStore.of(root), null, NONE)).getMessage());
	}
	
	@Test
	void validateWithNullPolicy() {
		assertEquals("Policy must not be null", assertThrows(NullPointerException.class, () -> CertificateChains.validate(List.of(leaf), TrustStore.of(root), Instant.now(), null)).getMessage());
	}
	
	@Test
	void validateWithNullElement() {
		List<X509Certificate> chain = Arrays.asList(leaf, null);
		assertThrows(NullPointerException.class, () -> CertificateChains.validate(chain, TrustStore.of(root), Instant.now(), NONE));
	}
	
	@Test
	void validateWithEmptyChain() {
		assertEquals("Chain must not be empty", assertThrows(IllegalArgumentException.class, () -> CertificateChains.validate(List.of(), TrustStore.of(root), Instant.now(), NONE)).getMessage());
	}
	
	@Test
	void buildWithNullLeaf() {
		assertEquals("Leaf must not be null", assertThrows(NullPointerException.class, () -> CertificateChains.build(null, List.of(), TrustStore.of(root))).getMessage());
	}
	
	@Test
	void buildWithNullPool() {
		assertEquals("Pool must not be null", assertThrows(NullPointerException.class, () -> CertificateChains.build(leaf, null, TrustStore.of(root))).getMessage());
	}
	
	@Test
	void buildWithNullTrustStore() {
		assertEquals("Trust store must not be null", assertThrows(NullPointerException.class, () -> CertificateChains.build(leaf, List.of(), null)).getMessage());
	}
	
	@Test
	void buildWithNullInPool() {
		List<X509Certificate> pool = Arrays.asList(intermediate, null);
		assertThrows(NullPointerException.class, () -> CertificateChains.build(deepLeaf, pool, TrustStore.of(deepRoot)));
	}
	
	@Test
	void validateLeafIssuedByAnchor() {
		ChainValidationResult result = CertificateChains.validate(List.of(leaf), TrustStore.of(root), Instant.now(), NONE);
		assertTrue(result.isValid());
		assertEquals(new ChainValidationResult.Valid(root), result);
	}
	
	@Test
	void validateWithAnchorIncluded() {
		ChainValidationResult withAnchor = CertificateChains.validate(List.of(leaf, root), TrustStore.of(root), Instant.now(), NONE);
		ChainValidationResult withoutAnchor = CertificateChains.validate(List.of(leaf), TrustStore.of(root), Instant.now(), NONE);
		assertEquals(new ChainValidationResult.Valid(root), withAnchor);
		assertEquals(withoutAnchor, withAnchor);
	}
	
	@Test
	void validateLeafIsAnchor() {
		ChainValidationResult result = CertificateChains.validate(List.of(leaf), TrustStore.of(leaf), Instant.now(), NONE);
		assertTrue(result.isValid());
		assertEquals(new ChainValidationResult.Valid(leaf), result);
	}
	
	@Test
	void validateLeafIsAnchorOutsideValidity() {
		Instant at = Instant.now().plus(Duration.ofDays(400));
		ChainValidationResult result = CertificateChains.validate(List.of(leaf, root), TrustStore.of(root, leaf), at, NONE);
		ChainValidationResult.Invalid invalid = assertInstanceOf(ChainValidationResult.Invalid.class, result);
		assertEquals(0, invalid.index());
		assertEquals("Certificate is not valid at " + at, invalid.reason());
		assertFalse(result.isValid());
	}
	
	@Test
	void validateWithUntrustedRoot() {
		ChainValidationResult result = CertificateChains.validate(List.of(leaf), TrustStore.of(otherRoot), Instant.now(), NONE);
		ChainValidationResult.Invalid invalid = assertInstanceOf(ChainValidationResult.Invalid.class, result);
		assertFalse(result.isValid());
		assertFalse(invalid.reason().isBlank());
		assertTrue(invalid.index() >= -1);
	}
	
	@Test
	void validateExpiredLeaf() {
		ChainValidationResult result = CertificateChains.validate(List.of(leaf), TrustStore.of(root), Instant.now().plus(Duration.ofDays(60)), NONE);
		ChainValidationResult.Invalid invalid = assertInstanceOf(ChainValidationResult.Invalid.class, result);
		assertEquals(0, invalid.index());
		assertFalse(invalid.reason().isBlank());
	}
	
	@Test
	void validateWithCrlNotRevoked() {
		CertificateAuthority issuer = CertificateAuthority.create(DistinguishedName.ofCommonName("Crl Root"), ALGORITHM, Duration.ofDays(365));
		X509Certificate certificate = issueServer(issuer, "good.example");
		ChainValidationResult result = CertificateChains.validate(List.of(certificate), TrustStore.of(issuer.certificate()), Instant.now(), new RevocationPolicy.Crl(List.of(issuer.crl())));
		assertEquals(new ChainValidationResult.Valid(issuer.certificate()), result);
	}
	
	@Test
	void validateWithCrlRevoked() {
		CertificateAuthority issuer = CertificateAuthority.create(DistinguishedName.ofCommonName("Revoking Root"), ALGORITHM, Duration.ofDays(365));
		X509Certificate certificate = issueServer(issuer, "revoked.example");
		assertTrue(issuer.revoke(certificate.getSerialNumber()));
		ChainValidationResult result = CertificateChains.validate(List.of(certificate), TrustStore.of(issuer.certificate()), Instant.now(), new RevocationPolicy.Crl(List.of(issuer.crl())));
		ChainValidationResult.Invalid invalid = assertInstanceOf(ChainValidationResult.Invalid.class, result);
		assertEquals(0, invalid.index());
	}
	
	@Test
	void validateWithCrlMissing() {
		CertificateAuthority unrelated = CertificateAuthority.create(DistinguishedName.ofCommonName("Unrelated Root"), ALGORITHM, Duration.ofDays(365));
		ChainValidationResult result = CertificateChains.validate(List.of(leaf), TrustStore.of(root), Instant.now(), new RevocationPolicy.Crl(List.of(unrelated.crl())));
		assertInstanceOf(ChainValidationResult.Invalid.class, result);
		assertFalse(result.isValid());
	}
	
	@Test
	void validateWithOcspSoftFailWithoutResponder() {
		ChainValidationResult result = CertificateChains.validate(List.of(leaf), TrustStore.of(root), Instant.now(), new RevocationPolicy.Ocsp(null, true));
		ChainValidationResult.Invalid invalid = assertInstanceOf(ChainValidationResult.Invalid.class, result);
		assertEquals("Certificate does not specify OCSP responder", invalid.reason());
		assertEquals(0, invalid.index());
	}
	
	@Test
	void validateWithOcspHardFail() {
		ChainValidationResult result = CertificateChains.validate(List.of(leaf), TrustStore.of(root), Instant.now(), new RevocationPolicy.Ocsp(null, false));
		ChainValidationResult.Invalid invalid = assertInstanceOf(ChainValidationResult.Invalid.class, result);
		assertFalse(invalid.reason().isBlank());
	}
	
	@Test
	void validateWithOcspResponder() {
		RevocationPolicy policy = new RevocationPolicy.Ocsp(URI.create("http://127.0.0.1:1/ocsp"), true);
		ChainValidationResult result = CertificateChains.validate(List.of(leaf), TrustStore.of(root), Instant.now(), policy);
		assertEquals(new ChainValidationResult.Valid(root), result);
	}
	
	@Test
	void buildLeafIsAnchor() {
		Optional<List<X509Certificate>> chain = CertificateChains.build(leaf, List.of(intermediate), TrustStore.of(leaf));
		assertTrue(chain.isPresent());
		assertEquals(List.of(leaf), chain.get());
	}
	
	@Test
	void buildLeafIssuedByAnchor() {
		Optional<List<X509Certificate>> chain = CertificateChains.build(leaf, List.of(), TrustStore.of(root));
		assertTrue(chain.isPresent());
		assertEquals(List.of(leaf), chain.get());
		assertFalse(chain.get().contains(root));
	}
	
	@Test
	void buildWithMissingIntermediate() {
		assertTrue(CertificateChains.build(deepLeaf, List.of(), TrustStore.of(deepRoot)).isEmpty());
		assertTrue(CertificateChains.build(leaf, List.of(), TrustStore.of(otherRoot)).isEmpty());
	}
	
	@Test
	void validateClientCertificate() {
		X509Certificate client = authority.issueClient(DistinguishedName.ofCommonName("client"), Signatures.generateKeyPair(ALGORITHM).getPublic(), Duration.ofDays(30));
		ChainValidationResult result = CertificateChains.validate(List.of(client), TrustStore.of(root), Instant.now(), NONE);
		assertEquals(new ChainValidationResult.Valid(root), result);
	}
	
	@Test
	void validateThreeLevelChain() {
		ChainValidationResult result = CertificateChains.validate(List.of(deepLeaf, intermediate), TrustStore.of(deepRoot), Instant.now(), NONE);
		assertTrue(result.isValid());
		assertEquals(new ChainValidationResult.Valid(deepRoot), result);
	}
	
	@Test
	void validateIntermediateOrderMatters() {
		ChainValidationResult result = CertificateChains.validate(List.of(intermediate, deepLeaf), TrustStore.of(deepRoot), Instant.now(), NONE);
		assertInstanceOf(ChainValidationResult.Invalid.class, result);
		assertFalse(result.isValid());
	}
	
	@Test
	void buildThreeLevelChain() {
		Optional<List<X509Certificate>> chain = CertificateChains.build(deepLeaf, List.of(intermediate), TrustStore.of(deepRoot));
		assertTrue(chain.isPresent());
		assertEquals(List.of(deepLeaf, intermediate), chain.get());
	}
	
	@Test
	void buildFromUnorderedPool() {
		List<X509Certificate> pool = List.of(otherRoot, leaf, intermediate, deepLeaf);
		Optional<List<X509Certificate>> chain = CertificateChains.build(deepLeaf, pool, TrustStore.of(deepRoot));
		assertTrue(chain.isPresent());
		assertEquals(2, chain.get().size());
		assertEquals(List.of(deepLeaf, intermediate), chain.get());
	}
	
	@Test
	void buildExpiredChain() {
		CertificateSpec spec = CertificateSpec.of(Instant.now().minus(Duration.ofDays(10)), Duration.ofDays(1));
		X509Certificate expired = authority.issue(DistinguishedName.ofCommonName("expired"), Signatures.generateKeyPair(ALGORITHM).getPublic(), spec);
		assertTrue(CertificateChains.build(expired, List.of(), TrustStore.of(root)).isEmpty());
		assertTrue(CertificateChains.build(leaf, List.of(), TrustStore.of(root)).isPresent());
	}
	
	@Test
	void buildResultValidates() {
		List<X509Certificate> chain = CertificateChains.build(deepLeaf, List.of(intermediate, otherRoot), TrustStore.of(deepRoot)).orElseThrow();
		ChainValidationResult result = CertificateChains.validate(chain, TrustStore.of(deepRoot), Instant.now(), NONE);
		assertEquals(new ChainValidationResult.Valid(deepRoot), result);
	}
	
	@Test
	void validateWithStoreOfSeveralAnchors() {
		TrustStore store = TrustStore.of(otherRoot, root, deepRoot);
		assertEquals(new ChainValidationResult.Valid(root), CertificateChains.validate(List.of(leaf), store, Instant.now(), NONE));
		assertEquals(new ChainValidationResult.Valid(deepRoot), CertificateChains.validate(List.of(deepLeaf, intermediate), store, Instant.now(), NONE));
		assertEquals(new ChainValidationResult.Valid(deepRoot), CertificateChains.validate(List.of(deepLeaf, intermediate, deepRoot), store, Instant.now(), NONE));
	}
}
