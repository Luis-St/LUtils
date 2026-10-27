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
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.security.cert.X509CRL;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link RevocationPolicy}.<br>
 *
 * @author Luis-St
 */
class RevocationPolicyTest {
	
	private static final URI RESPONDER = URI.create("http://ocsp.example.com");
	
	private static X509CRL first;
	private static X509CRL second;
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		first = CertificateAuthority.create(DistinguishedName.ofCommonName("First"), SignatureAlgorithm.ED25519, Duration.ofDays(1)).crl();
		second = CertificateAuthority.create(DistinguishedName.ofCommonName("Second"), SignatureAlgorithm.ED25519, Duration.ofDays(1)).crl();
	}
	
	@Test
	void constructNone() {
		RevocationPolicy.None none = new RevocationPolicy.None();
		assertInstanceOf(RevocationPolicy.class, none);
		assertEquals(new RevocationPolicy.None(), none);
		assertEquals(new RevocationPolicy.None().hashCode(), none.hashCode());
	}
	
	@Test
	void constructCrlWithList() {
		RevocationPolicy.Crl crl = new RevocationPolicy.Crl(List.of(first, second));
		assertEquals(List.of(first, second), crl.crls());
		assertSame(first, crl.crls().get(0));
	}
	
	@Test
	void constructCrlWithCollection() {
		Collection<X509CRL> crls = Set.of(first);
		RevocationPolicy.Crl crl = new RevocationPolicy.Crl(crls);
		assertEquals(1, crl.crls().size());
		assertSame(first, crl.crls().getFirst());
	}
	
	@Test
	void constructCrlWithNullList() {
		assertEquals("Revocation lists must not be null", assertThrows(NullPointerException.class, () -> new RevocationPolicy.Crl(null)).getMessage());
	}
	
	@Test
	void constructCrlWithNullCollection() {
		assertEquals("Revocation lists must not be null", assertThrows(NullPointerException.class, () -> new RevocationPolicy.Crl((Collection<X509CRL>) null)).getMessage());
	}
	
	@Test
	void constructOcspWithResponder() {
		RevocationPolicy.Ocsp ocsp = new RevocationPolicy.Ocsp(RESPONDER, true);
		assertEquals(RESPONDER, ocsp.responder());
		assertTrue(ocsp.softFail());
	}
	
	@Test
	void constructOcspWithNullResponder() {
		RevocationPolicy.Ocsp ocsp = assertDoesNotThrow(() -> new RevocationPolicy.Ocsp(null, false));
		assertNull(ocsp.responder());
		assertFalse(ocsp.softFail());
	}
	
	@Test
	void constructCrlWithListHoldingNull() {
		List<X509CRL> crls = Arrays.asList(first, null);
		assertThrows(NullPointerException.class, () -> new RevocationPolicy.Crl(crls));
	}
	
	@Test
	void constructCrlWithCollectionHoldingNull() {
		Collection<X509CRL> crls = new HashSet<>(Arrays.asList(first, null));
		assertThrows(NullPointerException.class, () -> new RevocationPolicy.Crl(crls));
	}
	
	@Test
	void crlListIsUnmodifiable() {
		RevocationPolicy.Crl crl = new RevocationPolicy.Crl(List.of(first));
		assertThrows(UnsupportedOperationException.class, () -> crl.crls().add(second));
		assertThrows(UnsupportedOperationException.class, () -> crl.crls().clear());
		assertEquals(1, crl.crls().size());
	}
	
	@Test
	void crlCopiesList() {
		List<X509CRL> source = new ArrayList<>(List.of(first));
		RevocationPolicy.Crl crl = new RevocationPolicy.Crl(source);
		source.add(second);
		source.remove(first);
		assertEquals(List.of(first), crl.crls());
	}
	
	@Test
	void crlCopiesCollection() {
		Collection<X509CRL> source = new LinkedHashSet<>(List.of(first));
		RevocationPolicy.Crl crl = new RevocationPolicy.Crl(source);
		source.add(second);
		assertEquals(List.of(first), crl.crls());
	}
	
	@Test
	void constructCrlWithEmptyList() {
		RevocationPolicy.Crl crl = new RevocationPolicy.Crl(List.of());
		assertTrue(crl.crls().isEmpty());
		assertEquals(new RevocationPolicy.Crl(Set.of()), crl);
	}
	
	@Test
	void ocspSoftFailTrueAndFalse() {
		RevocationPolicy.Ocsp soft = new RevocationPolicy.Ocsp(RESPONDER, true);
		RevocationPolicy.Ocsp hard = new RevocationPolicy.Ocsp(RESPONDER, false);
		assertTrue(soft.softFail());
		assertFalse(hard.softFail());
		assertNotEquals(soft, hard);
	}
	
	@Test
	void crlEquality() {
		RevocationPolicy.Crl crl = new RevocationPolicy.Crl(List.of(first, second));
		RevocationPolicy.Crl same = new RevocationPolicy.Crl(new ArrayList<>(List.of(first, second)));
		assertEquals(crl, same);
		assertEquals(crl.hashCode(), same.hashCode());
		assertNotEquals(crl, new RevocationPolicy.Crl(List.of(second, first)));
		assertNotEquals(crl, new RevocationPolicy.Crl(List.of(first)));
	}
	
	@Test
	void ocspEquality() {
		RevocationPolicy.Ocsp ocsp = new RevocationPolicy.Ocsp(RESPONDER, false);
		assertEquals(new RevocationPolicy.Ocsp(URI.create("http://ocsp.example.com"), false), ocsp);
		assertNotEquals(new RevocationPolicy.Ocsp(URI.create("http://other.example.com"), false), ocsp);
		assertNotEquals(new RevocationPolicy.Ocsp(null, false), ocsp);
	}
	
	@Test
	void permittedSubclasses() {
		assertTrue(RevocationPolicy.class.isSealed());
		Set<Class<?>> permitted = Set.of(RevocationPolicy.class.getPermittedSubclasses());
		assertEquals(Set.of(RevocationPolicy.None.class, RevocationPolicy.Crl.class, RevocationPolicy.Ocsp.class), permitted);
	}
	
	@Test
	void switchOverPolicies() {
		List<RevocationPolicy> policies = List.of(new RevocationPolicy.None(), new RevocationPolicy.Crl(List.of(first)), new RevocationPolicy.Ocsp(RESPONDER, true));
		List<String> described = policies.stream().map(policy -> switch (policy) {
			case RevocationPolicy.None none -> "none";
			case RevocationPolicy.Crl(List<X509CRL> crls) -> "crl:" + crls.size();
			case RevocationPolicy.Ocsp(URI responder, boolean softFail) -> "ocsp:" + responder.getHost() + ":" + softFail;
		}).toList();
		assertEquals(List.of("none", "crl:1", "ocsp:ocsp.example.com:true"), described);
	}
}
