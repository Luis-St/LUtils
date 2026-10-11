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

import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.IpAddresses;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SubjectAltName}.<br>
 *
 * @author Luis-St
 */
class SubjectAltNameTest {
	
	private static final IpAddress<?> IPV4 = IpAddresses.parse("192.0.2.1");
	private static final IpAddress<?> IPV6 = IpAddresses.parse("2001:db8::1");
	
	@Test
	void constructDns() {
		SubjectAltName.Dns dns = new SubjectAltName.Dns("api.example.com");
		assertEquals("api.example.com", dns.name());
		assertInstanceOf(SubjectAltName.class, dns);
	}
	
	@Test
	void constructDnsWithNullName() {
		assertEquals("Name must not be null", assertThrows(NullPointerException.class, () -> new SubjectAltName.Dns(null)).getMessage());
	}
	
	@Test
	void constructIp() {
		SubjectAltName.Ip ip = new SubjectAltName.Ip(IPV4);
		assertSame(IPV4, ip.address());
		assertInstanceOf(SubjectAltName.class, ip);
	}
	
	@Test
	void constructIpWithNullAddress() {
		assertEquals("Address must not be null", assertThrows(NullPointerException.class, () -> new SubjectAltName.Ip(null)).getMessage());
	}
	
	@Test
	void constructUri() {
		URI uri = URI.create("spiffe://example.org/service");
		SubjectAltName.Uri name = new SubjectAltName.Uri(uri);
		assertSame(uri, name.uri());
		assertInstanceOf(SubjectAltName.class, name);
	}
	
	@Test
	void constructUriWithNullUri() {
		assertEquals("Uri must not be null", assertThrows(NullPointerException.class, () -> new SubjectAltName.Uri(null)).getMessage());
	}
	
	@Test
	void constructEmail() {
		SubjectAltName.Email email = new SubjectAltName.Email("admin@example.com");
		assertEquals("admin@example.com", email.address());
		assertInstanceOf(SubjectAltName.class, email);
	}
	
	@Test
	void constructEmailWithNullAddress() {
		assertEquals("Address must not be null", assertThrows(NullPointerException.class, () -> new SubjectAltName.Email(null)).getMessage());
	}
	
	@Test
	void constructDnsWithEmptyName() {
		assertEquals("Domain name must not be blank", assertThrows(IllegalArgumentException.class, () -> new SubjectAltName.Dns("")).getMessage());
	}
	
	@Test
	void constructDnsWithBlankName() {
		assertEquals("Domain name must not be blank", assertThrows(IllegalArgumentException.class, () -> new SubjectAltName.Dns(" \t ")).getMessage());
	}
	
	@Test
	void constructDnsWithNonAsciiName() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new SubjectAltName.Dns("bücher.example"));
		assertTrue(exception.getMessage().startsWith("Domain name must be ascii"));
		assertTrue(exception.getMessage().contains("bücher.example"));
	}
	
	@Test
	void constructUriWithRelativeUri() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new SubjectAltName.Uri(URI.create("relative/path")));
		assertTrue(exception.getMessage().startsWith("Uri must be absolute"));
		assertTrue(exception.getMessage().contains("relative/path"));
	}
	
	@Test
	void constructEmailWithBlankAddress() {
		assertEquals("Email address must not be blank", assertThrows(IllegalArgumentException.class, () -> new SubjectAltName.Email("  ")).getMessage());
	}
	
	@Test
	void constructEmailWithNonAsciiAddress() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new SubjectAltName.Email("jürgen@example.com"));
		assertTrue(exception.getMessage().startsWith("Email address must be ascii"));
	}
	
	@Test
	void constructEmailWithoutAtSign() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new SubjectAltName.Email("admin.example.com"));
		assertTrue(exception.getMessage().startsWith("Email address must hold a local part and a domain"));
		assertTrue(exception.getMessage().contains("admin.example.com"));
	}
	
	@Test
	void constructEmailWithLeadingAtSign() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new SubjectAltName.Email("@example.com"));
		assertTrue(exception.getMessage().contains("@example.com"));
	}
	
	@Test
	void constructDnsWithWildcard() {
		assertEquals("*.example.com", new SubjectAltName.Dns("*.example.com").name());
	}
	
	@Test
	void constructIpWithIpv6() {
		SubjectAltName.Ip ip = new SubjectAltName.Ip(IPV6);
		assertSame(IPV6, ip.address());
		assertEquals(16, ip.address().toBytes().length);
	}
	
	@Test
	void constructUriWithOpaqueUri() {
		URI uri = URI.create("urn:example:test");
		assertTrue(uri.isOpaque());
		assertEquals(uri, new SubjectAltName.Uri(uri).uri());
	}
	
	@Test
	void constructEmailWithAtSignAfterFirstCharacter() {
		assertEquals("a@example.com", new SubjectAltName.Email("a@example.com").address());
	}
	
	@Test
	void dnsEquality() {
		SubjectAltName.Dns first = new SubjectAltName.Dns("example.com");
		SubjectAltName.Dns second = new SubjectAltName.Dns("example.com");
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new SubjectAltName.Dns("example.org"));
	}
	
	@Test
	void ipEquality() {
		SubjectAltName.Ip first = new SubjectAltName.Ip(IPV4);
		SubjectAltName.Ip second = new SubjectAltName.Ip(IpAddresses.parse("192.0.2.1"));
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new SubjectAltName.Ip(IPV6));
	}
	
	@Test
	void uriEquality() {
		SubjectAltName.Uri first = new SubjectAltName.Uri(URI.create("https://example.com"));
		SubjectAltName.Uri second = new SubjectAltName.Uri(URI.create("https://example.com"));
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new SubjectAltName.Uri(URI.create("https://example.org")));
	}
	
	@Test
	void emailEquality() {
		SubjectAltName.Email first = new SubjectAltName.Email("admin@example.com");
		SubjectAltName.Email second = new SubjectAltName.Email("admin@example.com");
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new SubjectAltName.Email("user@example.com"));
	}
	
	@Test
	void constructEmailWithoutDomain() {
		assertEquals("user@", new SubjectAltName.Email("user@").address());
	}
	
	@Test
	void sealedInterfacePermitsFourKinds() {
		Class<?>[] permitted = SubjectAltName.class.getPermittedSubclasses();
		assertTrue(SubjectAltName.class.isSealed());
		assertEquals(4, permitted.length);
		assertEquals(Set.of(SubjectAltName.Dns.class, SubjectAltName.Ip.class, SubjectAltName.Uri.class, SubjectAltName.Email.class), Set.of(permitted));
	}
	
	@Test
	void differentKindsAreNotEqual() {
		SubjectAltName dns = new SubjectAltName.Dns("example.com");
		SubjectAltName uri = new SubjectAltName.Uri(URI.create("http://example.com"));
		assertNotEquals(dns, uri);
		assertNotEquals(uri, dns);
	}
	
	@Test
	void exhaustiveSwitchOverKinds() {
		List<SubjectAltName> names = List.of(
			new SubjectAltName.Dns("example.com"),
			new SubjectAltName.Ip(IPV4),
			new SubjectAltName.Uri(URI.create("https://example.com")),
			new SubjectAltName.Email("admin@example.com")
		);
		List<String> kinds = names.stream().map(name -> switch (name) {
			case SubjectAltName.Dns _ -> "dns";
			case SubjectAltName.Ip _ -> "ip";
			case SubjectAltName.Uri _ -> "uri";
			case SubjectAltName.Email _ -> "email";
		}).toList();
		assertEquals(List.of("dns", "ip", "uri", "email"), kinds);
	}
	
	@Test
	void constructEmailWithMultipleAtSigns() {
		assertEquals("a@b@example.com", new SubjectAltName.Email("a@b@example.com").address());
	}
}
