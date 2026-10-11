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

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.*;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.junit.jupiter.api.Test;

import javax.security.auth.x500.X500Principal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DistinguishedName}.<br>
 *
 * @author Luis-St
 */
class DistinguishedNameTest {
	
	private static final DistinguishedName FULL = new DistinguishedName("api.example.com", "Example", "Platform", "Berlin", "Berlin State", "DE");
	
	@Test
	void constructWithAllComponents() {
		assertEquals("api.example.com", FULL.commonName());
		assertEquals("Example", FULL.organization());
		assertEquals("Platform", FULL.organizationalUnit());
		assertEquals("Berlin", FULL.locality());
		assertEquals("Berlin State", FULL.state());
		assertEquals("DE", FULL.country());
	}
	
	@Test
	void constructWithSingleComponent() {
		DistinguishedName name = new DistinguishedName(null, "Example", null, null, null, null);
		assertNull(name.commonName());
		assertEquals("Example", name.organization());
		assertNull(name.organizationalUnit());
		assertNull(name.locality());
		assertNull(name.state());
		assertNull(name.country());
	}
	
	@Test
	void constructWithAllNull() {
		assertEquals("A distinguished name needs at least one component", assertThrows(IllegalArgumentException.class, () -> new DistinguishedName(null, null, null, null, null, null)).getMessage());
	}
	
	@Test
	void constructWithBlankComponent() {
		assertThrows(IllegalArgumentException.class, () -> new DistinguishedName(" ", "Example", null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> new DistinguishedName("api.example.com", null, null, "\t", null, null));
		assertThrows(IllegalArgumentException.class, () -> new DistinguishedName(null, null, null, null, "", "DE"));
		assertThrows(IllegalArgumentException.class, () -> new DistinguishedName(null, null, null, null, null, " "));
	}
	
	@Test
	void constructWithInvalidCountry() {
		assertThrows(IllegalArgumentException.class, () -> new DistinguishedName("api.example.com", null, null, null, null, "DEU"));
		assertThrows(IllegalArgumentException.class, () -> new DistinguishedName("api.example.com", null, null, null, null, "D"));
		assertThrows(IllegalArgumentException.class, () -> new DistinguishedName("api.example.com", null, null, null, null, "1A"));
		assertThrows(IllegalArgumentException.class, () -> new DistinguishedName("api.example.com", null, null, null, null, "D3"));
	}
	
	@Test
	void ofCommonNameWithNull() {
		assertEquals("Common name must not be null", assertThrows(NullPointerException.class, () -> DistinguishedName.ofCommonName(null)).getMessage());
	}
	
	@Test
	void ofCommonNameWithBlank() {
		assertThrows(IllegalArgumentException.class, () -> DistinguishedName.ofCommonName(""));
		assertThrows(IllegalArgumentException.class, () -> DistinguishedName.ofCommonName("   "));
	}
	
	@Test
	void ofStringWithNull() {
		assertEquals("Name must not be null", assertThrows(NullPointerException.class, () -> DistinguishedName.of((String) null)).getMessage());
	}
	
	@Test
	void ofStringMalformed() {
		assertThrows(IllegalArgumentException.class, () -> DistinguishedName.of("not a distinguished name"));
		assertThrows(IllegalArgumentException.class, () -> DistinguishedName.of("CN"));
	}
	
	@Test
	void ofStringWithUnsupportedAttribute() {
		assertThrows(IllegalArgumentException.class, () -> DistinguishedName.of("DC=example,CN=api.example.com"));
		assertThrows(IllegalArgumentException.class, () -> DistinguishedName.of("STREET=Main Street,CN=api.example.com"));
	}
	
	@Test
	void ofStringWithDuplicateAttribute() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> DistinguishedName.of("CN=first,CN=second"));
		assertTrue(exception.getMessage().contains("more than once"));
	}
	
	@Test
	void ofStringEmpty() {
		assertThrows(IllegalArgumentException.class, () -> DistinguishedName.of(""));
	}
	
	@Test
	void ofX500NameWithNull() {
		assertEquals("Name must not be null", assertThrows(NullPointerException.class, () -> DistinguishedName.of((X500Name) null)).getMessage());
	}
	
	@Test
	void ofX500NameMultiValued() {
		RDN rdn = new RDN(new AttributeTypeAndValue[] { new AttributeTypeAndValue(BCStyle.CN, new DERUTF8String("api")), new AttributeTypeAndValue(BCStyle.O, new DERUTF8String("Example")) });
		X500Name name = new X500Name(new RDN[] { rdn });
		assertEquals("Multi-valued relative distinguished names are not supported", assertThrows(IllegalArgumentException.class, () -> DistinguishedName.of(name)).getMessage());
		assertThrows(IllegalArgumentException.class, () -> DistinguishedName.of("CN=api+O=Example"));
	}
	
	@Test
	void ofX500NameNonStringValue() {
		X500Name name = new X500Name(new RDN[] { new RDN(BCStyle.CN, new ASN1Integer(42)) });
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> DistinguishedName.of(name));
		assertTrue(exception.getMessage().contains("does not hold a string"));
	}
	
	@Test
	void constructWithoutCountry() {
		DistinguishedName name = assertDoesNotThrow(() -> new DistinguishedName("api.example.com", "Example", null, null, null, null));
		assertNull(name.country());
		assertEquals("api.example.com", name.commonName());
	}
	
	@Test
	void constructWithLowerCaseCountry() {
		DistinguishedName name = new DistinguishedName("api.example.com", null, null, null, null, "de");
		assertEquals("de", name.country());
		assertEquals("Ab", new DistinguishedName(null, null, null, null, null, "Ab").country());
	}
	
	@Test
	void ofStringSingleValuedSupported() {
		DistinguishedName name = DistinguishedName.of("CN=api.example.com,OU=Platform,O=Example,L=Berlin,ST=Berlin State,C=DE");
		assertEquals(FULL, name);
		assertEquals("Platform", name.organizationalUnit());
		assertEquals("Berlin State", name.state());
	}
	
	@Test
	void ofX500NameWithoutRdns() {
		assertThrows(IllegalArgumentException.class, () -> DistinguishedName.of(new X500Name(new RDN[0])));
	}
	
	@Test
	void toX500NameWithOnlyCommonName() {
		RDN[] rdns = DistinguishedName.ofCommonName("api.example.com").toX500Name().getRDNs();
		assertEquals(1, rdns.length);
		assertEquals(BCStyle.CN, rdns[0].getFirst().getType());
		assertEquals("api.example.com", rdns[0].getFirst().getValue().toString());
	}
	
	@Test
	void toX500NameWithOnlyCountry() {
		RDN[] rdns = new DistinguishedName(null, null, null, null, null, "DE").toX500Name().getRDNs();
		assertEquals(1, rdns.length);
		assertEquals(BCStyle.C, rdns[0].getFirst().getType());
		assertEquals("DE", rdns[0].getFirst().getValue().toString());
	}
	
	@Test
	void toX500NameUpperCasesCountry() {
		DistinguishedName name = new DistinguishedName("api.example.com", null, null, null, null, "de");
		assertEquals("CN=api.example.com,C=DE", name.toX500Principal().getName());
		assertEquals("DE", name.toX500Name().getRDNs(BCStyle.C)[0].getFirst().getValue().toString());
	}
	
	@Test
	void ofCommonNameSetsOnlyCommonName() {
		DistinguishedName name = DistinguishedName.ofCommonName("api.example.com");
		assertEquals("api.example.com", name.commonName());
		assertNull(name.organization());
		assertNull(name.country());
		assertEquals(new DistinguishedName("api.example.com", null, null, null, null, null), name);
	}
	
	@Test
	void ofStringParsesExample() {
		DistinguishedName name = DistinguishedName.of("CN=api.example.com,O=Example,C=DE");
		assertEquals(new DistinguishedName("api.example.com", "Example", null, null, null, "DE"), name);
		assertNull(name.organizationalUnit());
	}
	
	@Test
	void toX500PrincipalPrintsRfc2253() {
		X500Principal principal = new DistinguishedName("api.example.com", "Example", null, null, null, "DE").toX500Principal();
		assertEquals("CN=api.example.com,O=Example,C=DE", principal.getName());
		assertEquals(new X500Principal("CN=api.example.com,O=Example,C=DE"), principal);
	}
	
	@Test
	void equalNamesAreEqual() {
		DistinguishedName first = new DistinguishedName("api.example.com", "Example", null, null, null, "DE");
		DistinguishedName second = new DistinguishedName("api.example.com", "Example", null, null, null, "DE");
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, DistinguishedName.ofCommonName("api.example.com"));
	}
	
	@Test
	void toX500NameOrderMostSignificantFirst() {
		RDN[] rdns = FULL.toX500Name().getRDNs();
		List<?> types = Arrays.stream(rdns).map(rdn -> rdn.getFirst().getType()).toList();
		assertEquals(List.of(BCStyle.C, BCStyle.ST, BCStyle.L, BCStyle.O, BCStyle.OU, BCStyle.CN), types);
		assertEquals("CN=api.example.com,OU=Platform,O=Example,L=Berlin,ST=Berlin State,C=DE", FULL.toX500Principal().getName());
	}
	
	@Test
	void roundTripThroughPrincipal() {
		assertEquals(FULL, DistinguishedName.of(FULL.toX500Principal().getName()));
		DistinguishedName partial = new DistinguishedName(null, "Example", "Platform", null, null, null);
		assertEquals(partial, DistinguishedName.of(partial.toX500Principal().getName()));
	}
	
	@Test
	void roundTripThroughX500Name() {
		assertEquals(FULL, DistinguishedName.of(FULL.toX500Name()));
		DistinguishedName partial = new DistinguishedName(null, null, null, "Berlin", "Berlin State", null);
		assertEquals(partial, DistinguishedName.of(partial.toX500Name()));
	}
	
	@Test
	void roundTripWithLowerCaseCountryNormalizes() {
		DistinguishedName original = new DistinguishedName("api.example.com", null, null, null, null, "de");
		DistinguishedName parsed = DistinguishedName.of(original.toX500Principal().getName());
		assertEquals("DE", parsed.country());
		assertEquals(original.commonName(), parsed.commonName());
		assertNotEquals(original, parsed);
	}
	
	@Test
	void ofStringWithEscapedCharacters() {
		DistinguishedName name = DistinguishedName.of("CN=Doe\\, John,O=A\\+B Inc.");
		assertEquals("Doe, John", name.commonName());
		assertEquals("A+B Inc.", name.organization());
		assertEquals(name, DistinguishedName.of(name.toX500Principal().getName()));
	}
}
