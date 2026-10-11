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

import net.luis.utils.crypto.Hashes;
import net.luis.utils.crypto.Signatures;
import net.luis.utils.crypto.algorithm.HashAlgorithm;
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CertificatePins}.<br>
 *
 * @author Luis-St
 */
class CertificatePinsTest {
	
	private static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.ECDSA_P256_SHA_256;
	private static final HashAlgorithm SHA_256 = HashAlgorithm.SHA_256;
	private static final String SERVER_AUTH_TYPE = "ECDHE_ECDSA";
	private static final String CLIENT_AUTH_TYPE = "ECDSA";
	private static final String NO_PIN = "No certificate of the chain carries a pinned public key";
	
	private static CertificateAuthority authority;
	private static X509Certificate root;
	private static X509Certificate otherRoot;
	private static KeyPair serverPair;
	private static X509Certificate server;
	private static X509Certificate client;
	private static X509Certificate foreignServer;
	private static X509TrustManager delegate;
	
	@BeforeAll
	static void setUp() {
		authority = CertificateAuthority.create(DistinguishedName.ofCommonName("Pin Root"), ALGORITHM, Duration.ofDays(365));
		root = authority.certificate();
		CertificateAuthority otherAuthority = CertificateAuthority.create(DistinguishedName.ofCommonName("Other Pin Root"), ALGORITHM, Duration.ofDays(365));
		otherRoot = otherAuthority.certificate();
		serverPair = Signatures.generateKeyPair(ALGORITHM);
		server = authority.issueServer(DistinguishedName.ofCommonName("server"), serverPair.getPublic(), Duration.ofDays(30), new SubjectAltName.Dns("server.example"));
		client = authority.issueClient(DistinguishedName.ofCommonName("client"), Signatures.generateKeyPair(ALGORITHM).getPublic(), Duration.ofDays(30));
		foreignServer = otherAuthority.issueServer(DistinguishedName.ofCommonName("server"), serverPair.getPublic(), Duration.ofDays(30), new SubjectAltName.Dns("server.example"));
		delegate = TrustStore.of(root).trustManager();
	}
	
	private static CertificatePins pin(X509Certificate @NonNull ... certificates) {
		return CertificatePins.pinSpki(SHA_256, Arrays.stream(certificates).map(certificate -> CertificatePins.spki(SHA_256, certificate)).toArray(byte[][]::new));
	}
	
	@Test
	void pinSpkiSinglePin() {
		byte[] digest = CertificatePins.spki(SHA_256, server);
		CertificatePins pins = CertificatePins.pinSpki(SHA_256, digest);
		assertEquals(SHA_256, pins.algorithm());
		assertEquals(1, pins.pins().size());
		assertArrayEquals(digest, pins.pins().getFirst());
	}
	
	@Test
	void pinSpkiWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> CertificatePins.pinSpki(null, new byte[32])).getMessage());
	}
	
	@Test
	void pinSpkiWithNullPins() {
		assertEquals("Pins must not be null", assertThrows(NullPointerException.class, () -> CertificatePins.pinSpki(SHA_256, (byte[][]) null)).getMessage());
	}
	
	@Test
	void pinSpkiWithNoPin() {
		assertEquals("A pin set needs at least one pin", assertThrows(IllegalArgumentException.class, () -> CertificatePins.pinSpki(SHA_256)).getMessage());
	}
	
	@Test
	void pinSpkiWithNullPin() {
		assertEquals("Pin must not be null", assertThrows(NullPointerException.class, () -> CertificatePins.pinSpki(SHA_256, (byte[]) null)).getMessage());
		assertEquals("Pin must not be null", assertThrows(NullPointerException.class, () -> CertificatePins.pinSpki(SHA_256, new byte[32], null)).getMessage());
	}
	
	@Test
	void pinSpkiWithWrongLength() {
		assertEquals("A SHA-256 pin must be 32 bytes long, was 31", assertThrows(IllegalArgumentException.class, () -> CertificatePins.pinSpki(SHA_256, new byte[31])).getMessage());
		assertEquals("A SHA-256 pin must be 32 bytes long, was 64", assertThrows(IllegalArgumentException.class, () -> CertificatePins.pinSpki(SHA_256, new byte[32], new byte[64])).getMessage());
	}
	
	@Test
	void spkiWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> CertificatePins.spki(null, server)).getMessage());
	}
	
	@Test
	void spkiWithNullCertificate() {
		assertEquals("Certificate must not be null", assertThrows(NullPointerException.class, () -> CertificatePins.spki(SHA_256, null)).getMessage());
	}
	
	@Test
	void matchesWithNullChain() {
		assertEquals("Chain must not be null", assertThrows(NullPointerException.class, () -> pin(server).matches(null)).getMessage());
	}
	
	@Test
	void matchesWithNullElement() {
		List<X509Certificate> chain = Arrays.asList(null, server);
		assertThrows(NullPointerException.class, () -> pin(server).matches(chain));
	}
	
	@Test
	void trustManagerWithNullDelegate() {
		assertEquals("Delegate must not be null", assertThrows(NullPointerException.class, () -> pin(server).trustManager(null)).getMessage());
	}
	
	@Test
	void matchesEmptyChain() {
		assertFalse(pin(server).matches(List.of()));
	}
	
	@Test
	void matchesFirstCertificate() {
		assertTrue(pin(server).matches(List.of(server, root)));
	}
	
	@Test
	void matchesLaterCertificate() {
		assertTrue(pin(root).matches(List.of(server, client, root)));
	}
	
	@Test
	void matchesNoCertificate() {
		assertFalse(pin(otherRoot).matches(List.of(server, client, root)));
	}
	
	@Test
	void checkServerTrustedWithPinnedLeaf() {
		X509TrustManager manager = pin(server).trustManager(delegate);
		assertDoesNotThrow(() -> manager.checkServerTrusted(new X509Certificate[] { server }, SERVER_AUTH_TYPE));
	}
	
	@Test
	void checkServerTrustedWithPinnedAnchor() {
		X509TrustManager manager = pin(root).trustManager(delegate);
		assertDoesNotThrow(() -> manager.checkServerTrusted(new X509Certificate[] { server }, SERVER_AUTH_TYPE));
	}
	
	@Test
	void checkServerTrustedWithoutPin() {
		X509TrustManager manager = pin(otherRoot).trustManager(delegate);
		X509Certificate[] chain = { server };
		assertEquals(NO_PIN, assertThrows(CertificateException.class, () -> manager.checkServerTrusted(chain, SERVER_AUTH_TYPE)).getMessage());
	}
	
	@Test
	void checkServerTrustedIgnoresUnrelatedIssuer() {
		X509TrustManager manager = pin(otherRoot).trustManager(TrustStore.of(root, otherRoot).trustManager());
		X509Certificate[] chain = { server };
		assertEquals(NO_PIN, assertThrows(CertificateException.class, () -> manager.checkServerTrusted(chain, SERVER_AUTH_TYPE)).getMessage());
	}
	
	@Test
	void checkServerTrustedDelegateRejects() {
		X509TrustManager manager = pin(server).trustManager(delegate);
		X509Certificate[] chain = { foreignServer };
		assertTrue(pin(server).matches(List.of(foreignServer)));
		CertificateException exception = assertThrows(CertificateException.class, () -> manager.checkServerTrusted(chain, SERVER_AUTH_TYPE));
		assertNotEquals(NO_PIN, exception.getMessage());
	}
	
	@Test
	void checkServerTrustedWithSocket() {
		X509ExtendedTrustManager pinned = (X509ExtendedTrustManager) pin(server).trustManager(delegate);
		X509ExtendedTrustManager unpinned = (X509ExtendedTrustManager) pin(otherRoot).trustManager(delegate);
		X509Certificate[] chain = { server };
		assertDoesNotThrow(() -> pinned.checkServerTrusted(chain, SERVER_AUTH_TYPE, (Socket) null));
		assertEquals(NO_PIN, assertThrows(CertificateException.class, () -> unpinned.checkServerTrusted(chain, SERVER_AUTH_TYPE, (Socket) null)).getMessage());
	}
	
	@Test
	void checkServerTrustedWithEngine() {
		X509ExtendedTrustManager pinned = (X509ExtendedTrustManager) pin(server).trustManager(delegate);
		X509ExtendedTrustManager unpinned = (X509ExtendedTrustManager) pin(otherRoot).trustManager(delegate);
		X509Certificate[] chain = { server };
		assertDoesNotThrow(() -> pinned.checkServerTrusted(chain, SERVER_AUTH_TYPE, (SSLEngine) null));
		assertEquals(NO_PIN, assertThrows(CertificateException.class, () -> unpinned.checkServerTrusted(chain, SERVER_AUTH_TYPE, (SSLEngine) null)).getMessage());
	}
	
	@Test
	void checkServerTrustedWithPlainDelegate() {
		PlainTrustManager plain = new PlainTrustManager(delegate);
		X509ExtendedTrustManager pinned = (X509ExtendedTrustManager) pin(server).trustManager(plain);
		X509ExtendedTrustManager unpinned = (X509ExtendedTrustManager) pin(otherRoot).trustManager(plain);
		X509Certificate[] chain = { server };
		assertDoesNotThrow(() -> pinned.checkServerTrusted(chain, SERVER_AUTH_TYPE, (Socket) null));
		assertDoesNotThrow(() -> pinned.checkServerTrusted(chain, SERVER_AUTH_TYPE, (SSLEngine) null));
		assertEquals(NO_PIN, assertThrows(CertificateException.class, () -> unpinned.checkServerTrusted(chain, SERVER_AUTH_TYPE, (Socket) null)).getMessage());
		assertEquals(NO_PIN, assertThrows(CertificateException.class, () -> unpinned.checkServerTrusted(chain, SERVER_AUTH_TYPE, (SSLEngine) null)).getMessage());
	}
	
	@Test
	void checkClientTrustedWithPinnedLeaf() {
		X509TrustManager pinned = pin(client).trustManager(delegate);
		X509TrustManager unpinned = pin(server).trustManager(delegate);
		X509Certificate[] chain = { client };
		assertDoesNotThrow(() -> pinned.checkClientTrusted(chain, CLIENT_AUTH_TYPE));
		assertEquals(NO_PIN, assertThrows(CertificateException.class, () -> unpinned.checkClientTrusted(chain, CLIENT_AUTH_TYPE)).getMessage());
	}
	
	@Test
	void checkClientTrustedWithSocket() {
		X509ExtendedTrustManager pinned = (X509ExtendedTrustManager) pin(root).trustManager(delegate);
		X509ExtendedTrustManager unpinned = (X509ExtendedTrustManager) pin(server).trustManager(delegate);
		X509Certificate[] chain = { client };
		assertDoesNotThrow(() -> pinned.checkClientTrusted(chain, CLIENT_AUTH_TYPE, (Socket) null));
		assertEquals(NO_PIN, assertThrows(CertificateException.class, () -> unpinned.checkClientTrusted(chain, CLIENT_AUTH_TYPE, (Socket) null)).getMessage());
	}
	
	@Test
	void checkClientTrustedWithEngine() {
		X509ExtendedTrustManager pinned = (X509ExtendedTrustManager) pin(client).trustManager(delegate);
		X509ExtendedTrustManager unpinned = (X509ExtendedTrustManager) pin(server).trustManager(delegate);
		X509Certificate[] chain = { client };
		assertDoesNotThrow(() -> pinned.checkClientTrusted(chain, CLIENT_AUTH_TYPE, (SSLEngine) null));
		assertEquals(NO_PIN, assertThrows(CertificateException.class, () -> unpinned.checkClientTrusted(chain, CLIENT_AUTH_TYPE, (SSLEngine) null)).getMessage());
	}
	
	@Test
	void checkClientTrustedWithPlainDelegate() {
		PlainTrustManager plain = new PlainTrustManager(delegate);
		X509ExtendedTrustManager pinned = (X509ExtendedTrustManager) pin(client).trustManager(plain);
		X509ExtendedTrustManager unpinned = (X509ExtendedTrustManager) pin(server).trustManager(plain);
		X509Certificate[] chain = { client };
		assertDoesNotThrow(() -> pinned.checkClientTrusted(chain, CLIENT_AUTH_TYPE, (Socket) null));
		assertDoesNotThrow(() -> pinned.checkClientTrusted(chain, CLIENT_AUTH_TYPE, (SSLEngine) null));
		assertEquals(NO_PIN, assertThrows(CertificateException.class, () -> unpinned.checkClientTrusted(chain, CLIENT_AUTH_TYPE, (Socket) null)).getMessage());
		assertEquals(NO_PIN, assertThrows(CertificateException.class, () -> unpinned.checkClientTrusted(chain, CLIENT_AUTH_TYPE, (SSLEngine) null)).getMessage());
	}
	
	@Test
	void checkServerTrustedWithEmptyChain() {
		X509TrustManager manager = pin(server).trustManager(new AcceptingTrustManager(new X509Certificate[] { root }));
		X509Certificate[] chain = {};
		assertEquals(NO_PIN, assertThrows(CertificateException.class, () -> manager.checkServerTrusted(chain, SERVER_AUTH_TYPE)).getMessage());
	}
	
	@Test
	void spkiDigestOfPublicKey() {
		byte[] digest = CertificatePins.spki(SHA_256, server);
		assertEquals(32, digest.length);
		assertArrayEquals(Hashes.hash(SHA_256, server.getPublicKey().getEncoded()), digest);
		assertArrayEquals(Hashes.hash(SHA_256, serverPair.getPublic().getEncoded()), digest);
	}
	
	@Test
	void spkiWithOtherAlgorithm() {
		byte[] sha512 = CertificatePins.spki(HashAlgorithm.SHA_512, server);
		assertEquals(64, sha512.length);
		assertFalse(Arrays.equals(CertificatePins.spki(SHA_256, server), Arrays.copyOf(sha512, 32)));
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_512, server.getPublicKey().getEncoded()), sha512);
	}
	
	@Test
	void algorithmReturnsGiven() {
		CertificatePins pins = CertificatePins.pinSpki(HashAlgorithm.SHA_384, CertificatePins.spki(HashAlgorithm.SHA_384, root));
		assertEquals(HashAlgorithm.SHA_384, pins.algorithm());
		assertTrue(pins.matches(List.of(root)));
	}
	
	@Test
	void trustManagerIsExtended() {
		X509TrustManager manager = pin(server).trustManager(delegate);
		assertInstanceOf(X509ExtendedTrustManager.class, manager);
		assertArrayEquals(delegate.getAcceptedIssuers(), manager.getAcceptedIssuers());
		assertNotSame(delegate, manager);
	}
	
	@Test
	void toStringNamesAlgorithmAndCount() {
		assertEquals("CertificatePins[algorithm=SHA_256, pins=2]", pin(server, root).toString());
		assertEquals("CertificatePins[algorithm=SHA_256, pins=1]", pin(server).toString());
	}
	
	@Test
	void pinSpkiCopiesInput() {
		byte[] digest = CertificatePins.spki(SHA_256, server);
		CertificatePins pins = CertificatePins.pinSpki(SHA_256, digest);
		Arrays.fill(digest, (byte) 0);
		assertTrue(pins.matches(List.of(server)));
		assertArrayEquals(CertificatePins.spki(SHA_256, server), pins.pins().getFirst());
	}
	
	@Test
	void pinsReturnsCopies() {
		CertificatePins pins = pin(server);
		byte[] expected = CertificatePins.spki(SHA_256, server);
		pins.pins().getFirst()[0] ^= 1;
		assertArrayEquals(expected, pins.pins().getFirst());
		assertNotSame(pins.pins().getFirst(), pins.pins().getFirst());
		assertThrows(UnsupportedOperationException.class, () -> pins.pins().add(new byte[32]));
		assertTrue(pins.matches(List.of(server)));
	}
	
	@Test
	void spkiSurvivesRenewal() {
		CertificateSpec spec = CertificateSpec.of(Instant.now(), Duration.ofDays(90));
		X509Certificate renewed = authority.issue(DistinguishedName.ofCommonName("renewed"), serverPair.getPublic(), spec);
		assertArrayEquals(CertificatePins.spki(SHA_256, server), CertificatePins.spki(SHA_256, renewed));
		assertFalse(Arrays.equals(CertificatePins.spki(SHA_256, server), CertificatePins.spki(SHA_256, client)));
		assertTrue(pin(server).matches(List.of(renewed)));
	}
	
	@Test
	void matchesWithBackupPin() {
		CertificatePins pins = pin(otherRoot, client);
		assertTrue(pins.matches(List.of(client)));
		assertTrue(pins.matches(List.of(server, client)));
		assertFalse(pins.matches(List.of(server, root)));
		assertEquals(2, pins.pins().size());
	}
	
	private record PlainTrustManager(@NonNull X509TrustManager delegate) implements X509TrustManager {
		
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			this.delegate.checkClientTrusted(chain, authType);
		}
		
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			this.delegate.checkServerTrusted(chain, authType);
		}
		
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return this.delegate.getAcceptedIssuers();
		}
	}
	
	private record AcceptingTrustManager(X509Certificate @NonNull [] issuers) implements X509TrustManager {
		
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) {}
		
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) {}
		
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return this.issuers.clone();
		}
	}
}
