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

import net.luis.utils.crypto.Pems;
import net.luis.utils.crypto.Signatures;
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import net.luis.utils.crypto.exception.MalformedDataException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.*;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TrustStore}.<br>
 *
 * @author Luis-St
 */
class TrustStoreTest {
	
	private static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.ECDSA_P256_SHA_256;
	private static final String AUTH_TYPE = "ECDHE_ECDSA";
	private static CertificateAuthority authorityA;
	private static CertificateAuthority authorityB;
	private static X509Certificate rootA;
	private static X509Certificate rootB;
	private static X509Certificate rootC;
	@TempDir
	static Path directory;
	
	@BeforeAll
	static void setUp() {
		authorityA = CertificateAuthority.create(DistinguishedName.ofCommonName("Store Root A"), ALGORITHM, Duration.ofDays(365));
		authorityB = CertificateAuthority.create(DistinguishedName.ofCommonName("Store Root B"), ALGORITHM, Duration.ofDays(365));
		rootA = authorityA.certificate();
		rootB = authorityB.certificate();
		rootC = CertificateAuthority.create(DistinguishedName.ofCommonName("Store Root C"), ALGORITHM, Duration.ofDays(365)).certificate();
	}
	
	private static X509Certificate issueServer(CertificateAuthority issuer, String name) {
		return issuer.issueServer(DistinguishedName.ofCommonName(name), Signatures.generateKeyPair(ALGORITHM).getPublic(), Duration.ofDays(30), new SubjectAltName.Dns(name));
	}
	
	private static Path write(String name, String content) throws IOException {
		return Files.writeString(directory.resolve(name), content, StandardCharsets.US_ASCII);
	}
	
	@Test
	void ofSingleCertificate() {
		TrustStore store = TrustStore.of(rootA);
		assertEquals(List.of(rootA), store.certificates());
		assertTrue(store.contains(rootA));
		assertFalse(store.isEmpty());
	}
	
	@Test
	void ofCollection() {
		TrustStore store = TrustStore.of(List.of(rootB, rootA, rootC));
		assertEquals(List.of(rootB, rootA, rootC), store.certificates());
		assertFalse(store.isEmpty());
	}
	
	@Test
	void fromPemWithSingleCertificate() throws IOException {
		Path file = write("single.pem", Pems.encode(rootA));
		TrustStore store = TrustStore.fromPem(file);
		assertEquals(List.of(rootA), store.certificates());
		assertTrue(store.contains(rootA));
	}
	
	@Test
	void systemStore() {
		TrustStore store = TrustStore.system();
		assertNotNull(store);
		assertFalse(store.isEmpty());
		assertTrue(store.certificates().stream().allMatch(store::contains));
		assertFalse(store.contains(rootA));
	}
	
	@Test
	void ofWithNullArray() {
		assertEquals("Certificates must not be null", assertThrows(NullPointerException.class, () -> TrustStore.of((X509Certificate[]) null)).getMessage());
	}
	
	@Test
	void ofWithNullElement() {
		assertThrows(NullPointerException.class, () -> TrustStore.of(rootA, null));
	}
	
	@Test
	void ofWithNoCertificate() {
		assertEquals("A trust store needs at least one anchor", assertThrows(IllegalArgumentException.class, () -> TrustStore.of(new X509Certificate[0])).getMessage());
	}
	
	@Test
	void ofWithNullCollection() {
		assertEquals("Certificates must not be null", assertThrows(NullPointerException.class, () -> TrustStore.of((Collection<X509Certificate>) null)).getMessage());
	}
	
	@Test
	void ofWithEmptyCollection() {
		assertEquals("A trust store needs at least one anchor", assertThrows(IllegalArgumentException.class, () -> TrustStore.of(List.of())).getMessage());
	}
	
	@Test
	void ofCollectionWithNullElement() {
		List<X509Certificate> certificates = Arrays.asList(rootA, null);
		assertThrows(NullPointerException.class, () -> TrustStore.of(certificates));
	}
	
	@Test
	void fromPemWithNullFile() {
		assertEquals("File must not be null", assertThrows(NullPointerException.class, () -> TrustStore.fromPem(null)).getMessage());
	}
	
	@Test
	void fromPemWithMissingFile() {
		Path file = directory.resolve("missing.pem");
		assertThrows(UncheckedIOException.class, () -> TrustStore.fromPem(file));
	}
	
	@Test
	void fromPemWithoutCertificate() throws IOException {
		Path file = write("empty.pem", "no pem document in here");
		assertEquals("No certificate in " + file, assertThrows(MalformedDataException.class, () -> TrustStore.fromPem(file)).getMessage());
	}
	
	@Test
	void fromPemWithOtherDocument() throws IOException {
		Path file = write("key.pem", Pems.encode(rootA.getPublicKey()));
		assertThrows(MalformedDataException.class, () -> TrustStore.fromPem(file));
	}
	
	@Test
	void containsWithNull() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> TrustStore.of(rootA).contains(null)).getMessage());
	}
	
	@Test
	void unionWithNull() {
		assertEquals("Other trust store must not be null", assertThrows(NullPointerException.class, () -> TrustStore.of(rootA).union(null)).getMessage());
	}
	
	@Test
	void ofRemovesDuplicates() {
		TrustStore fromArray = TrustStore.of(rootA, rootB, rootA);
		TrustStore fromCollection = TrustStore.of(List.of(rootB, rootB, rootA));
		assertEquals(List.of(rootA, rootB), fromArray.certificates());
		assertEquals(List.of(rootB, rootA), fromCollection.certificates());
	}
	
	@Test
	void containsAbsentCertificate() {
		TrustStore store = TrustStore.of(rootA, rootB);
		assertTrue(store.contains(rootB));
		assertFalse(store.contains(rootC));
	}
	
	@Test
	void certificatesAreUnmodifiable() {
		TrustStore store = TrustStore.of(rootA);
		assertThrows(UnsupportedOperationException.class, () -> store.certificates().add(rootB));
		assertEquals(List.of(rootA), store.certificates());
	}
	
	@Test
	void trustManagerAcceptsIssuedChain() {
		X509TrustManager manager = TrustStore.of(rootA).trustManager();
		X509Certificate[] chain = { issueServer(authorityA, "a.example") };
		assertDoesNotThrow(() -> manager.checkServerTrusted(chain, AUTH_TYPE));
	}
	
	@Test
	void trustManagerRejectsForeignChain() {
		X509TrustManager manager = TrustStore.of(rootA).trustManager();
		X509Certificate[] chain = { issueServer(authorityB, "b.example") };
		assertThrows(CertificateException.class, () -> manager.checkServerTrusted(chain, AUTH_TYPE));
	}
	
	@Test
	void unionOfDisjointStores() {
		TrustStore union = TrustStore.of(rootA).union(TrustStore.of(rootB, rootC));
		assertEquals(List.of(rootA, rootB, rootC), union.certificates());
		assertTrue(union.contains(rootC));
	}
	
	@Test
	void anchorsMatchCertificates() {
		Set<TrustAnchor> anchors = TrustStore.of(rootB, rootA).anchors();
		List<X509Certificate> trusted = anchors.stream().map(TrustAnchor::getTrustedCert).toList();
		assertEquals(2, anchors.size());
		assertEquals(List.of(rootB, rootA), trusted);
		assertTrue(anchors.stream().allMatch(anchor -> anchor.getNameConstraints() == null));
	}
	
	@Test
	void trustManagerAcceptedIssuers() {
		X509TrustManager manager = TrustStore.of(rootA, rootB).trustManager();
		Set<X509Certificate> issuers = Set.of(manager.getAcceptedIssuers());
		assertEquals(Set.of(rootA, rootB), issuers);
	}
	
	@Test
	void toStringNamesAnchorCount() {
		assertEquals("TrustStore[anchors=2]", TrustStore.of(rootA, rootB).toString());
		assertEquals("TrustStore[anchors=1]", TrustStore.of(rootA, rootA).toString());
	}
	
	@Test
	void fromPemWithSeveralCertificates() throws IOException {
		Path file = write("chain.pem", Pems.encodeChain(List.of(rootC, rootA, rootB)));
		TrustStore store = TrustStore.fromPem(file);
		assertEquals(List.of(rootC, rootA, rootB), store.certificates());
		assertEquals("TrustStore[anchors=3]", store.toString());
	}
	
	@Test
	void unionOfOverlappingStores() {
		TrustStore union = TrustStore.of(rootA, rootB).union(TrustStore.of(rootB, rootC));
		assertEquals(List.of(rootA, rootB, rootC), union.certificates());
		assertEquals(3, union.anchors().size());
	}
	
	@Test
	void unionLeavesOriginalsUnchanged() {
		TrustStore first = TrustStore.of(rootA);
		TrustStore second = TrustStore.of(rootB);
		TrustStore union = first.union(second);
		assertEquals(List.of(rootA), first.certificates());
		assertEquals(List.of(rootB), second.certificates());
		assertNotSame(first, union);
		assertEquals(2, union.certificates().size());
	}
	
	@Test
	void trustManagerWithSeveralAnchors() {
		X509TrustManager manager = TrustStore.of(rootA, rootB, rootC).trustManager();
		X509Certificate[] chainA = { issueServer(authorityA, "a.example") };
		X509Certificate[] chainB = { issueServer(authorityB, "b.example") };
		assertDoesNotThrow(() -> manager.checkServerTrusted(chainA, AUTH_TYPE));
		assertDoesNotThrow(() -> manager.checkServerTrusted(chainB, AUTH_TYPE));
		assertEquals(3, manager.getAcceptedIssuers().length);
	}
}
