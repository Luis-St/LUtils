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

package net.luis.utils.io.network.connection.ssl;

import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TlsProtocol}.<br>
 *
 * @author Luis-St
 */
class TlsProtocolTest {
	
	@Test
	void valuesContainsAllProtocols() {
		assertEquals(6, TlsProtocol.values().length);
		assertArrayEquals(new TlsProtocol[] {
			TlsProtocol.SSL_V3, TlsProtocol.TLS_V1, TlsProtocol.TLS_V1_1, TlsProtocol.TLS_V1_2, TlsProtocol.TLS_V1_3, TlsProtocol.SSL_V2_HELLO
		}, TlsProtocol.values());
	}
	
	@Test
	void valueOfReturnsMatchingConstant() {
		assertEquals(TlsProtocol.SSL_V3, TlsProtocol.valueOf("SSL_V3"));
		assertEquals(TlsProtocol.TLS_V1_2, TlsProtocol.valueOf("TLS_V1_2"));
		assertEquals(TlsProtocol.TLS_V1_3, TlsProtocol.valueOf("TLS_V1_3"));
		assertEquals(TlsProtocol.SSL_V2_HELLO, TlsProtocol.valueOf("SSL_V2_HELLO"));
	}
	
	@Test
	void valueOfWithUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> TlsProtocol.valueOf("TLSv1.3"));
		assertThrows(IllegalArgumentException.class, () -> TlsProtocol.valueOf("TLS_V1_4"));
	}
	
	@Test
	void valueOfWithNullThrows() {
		assertThrows(NullPointerException.class, () -> TlsProtocol.valueOf(null));
	}
	
	@Test
	void toProtocolNamesWithNullListThrows() {
		assertThrows(NullPointerException.class, () -> TlsProtocol.toProtocolNames(null));
	}
	
	@Test
	void toProtocolNamesWithNullElementThrows() {
		List<TlsProtocol> protocols = Arrays.asList(TlsProtocol.TLS_V1_3, null);
		
		NullPointerException exception = assertThrows(NullPointerException.class, () -> TlsProtocol.toProtocolNames(protocols));
		assertEquals("Protocols must not contain null", exception.getMessage());
	}
	
	@Test
	void toProtocolNamesWithOnlyNullElementThrows() {
		List<TlsProtocol> protocols = Collections.singletonList(null);
		
		NullPointerException exception = assertThrows(NullPointerException.class, () -> TlsProtocol.toProtocolNames(protocols));
		assertEquals("Protocols must not contain null", exception.getMessage());
	}
	
	@Test
	void byNameWithNullReturnsEmpty() {
		assertTrue(TlsProtocol.byName(null).isEmpty());
	}
	
	@Test
	void byNameWithExactNameReturnsConstant() {
		assertEquals(Optional.of(TlsProtocol.SSL_V3), TlsProtocol.byName("SSLv3"));
		assertEquals(Optional.of(TlsProtocol.TLS_V1), TlsProtocol.byName("TLSv1"));
		assertEquals(Optional.of(TlsProtocol.TLS_V1_1), TlsProtocol.byName("TLSv1.1"));
		assertEquals(Optional.of(TlsProtocol.TLS_V1_2), TlsProtocol.byName("TLSv1.2"));
		assertEquals(Optional.of(TlsProtocol.TLS_V1_3), TlsProtocol.byName("TLSv1.3"));
		assertEquals(Optional.of(TlsProtocol.SSL_V2_HELLO), TlsProtocol.byName("SSLv2Hello"));
	}
	
	@Test
	void byNameWithDifferentCaseReturnsConstant() {
		assertEquals(Optional.of(TlsProtocol.TLS_V1_3), TlsProtocol.byName("tlsv1.3"));
		assertEquals(Optional.of(TlsProtocol.TLS_V1_2), TlsProtocol.byName("TLSV1.2"));
		assertEquals(Optional.of(TlsProtocol.SSL_V2_HELLO), TlsProtocol.byName("sslv2hello"));
		assertEquals(Optional.of(TlsProtocol.SSL_V3), TlsProtocol.byName("SsLv3"));
	}
	
	@Test
	void byNameWithUnknownNameReturnsEmpty() {
		assertTrue(TlsProtocol.byName("TLSv1.4").isEmpty());
		assertTrue(TlsProtocol.byName("NO-SUCH-PROTOCOL").isEmpty());
		assertTrue(TlsProtocol.byName("DTLSv1.2").isEmpty());
	}
	
	@Test
	void byNameWithConstantNameReturnsEmpty() {
		assertTrue(TlsProtocol.byName("TLS_V1_3").isEmpty());
		assertTrue(TlsProtocol.byName("SSL_V2_HELLO").isEmpty());
	}
	
	@Test
	void byNameWithEmptyStringReturnsEmpty() {
		assertTrue(TlsProtocol.byName("").isEmpty());
		assertTrue(TlsProtocol.byName("   ").isEmpty());
	}
	
	@Test
	void toProtocolNamesWithEmptyListReturnsEmptyArray() {
		String[] names = TlsProtocol.toProtocolNames(List.of());
		
		assertNotNull(names);
		assertEquals(0, names.length);
	}
	
	@Test
	void toProtocolNamesWithSingleProtocol() {
		assertArrayEquals(new String[] { "TLSv1.3" }, TlsProtocol.toProtocolNames(List.of(TlsProtocol.TLS_V1_3)));
		assertArrayEquals(new String[] { "SSLv2Hello" }, TlsProtocol.toProtocolNames(List.of(TlsProtocol.SSL_V2_HELLO)));
	}
	
	@Test
	void toProtocolNamesWithMultipleProtocolsPreservesOrder() {
		List<TlsProtocol> protocols = List.of(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_2, TlsProtocol.TLS_V1_1);
		assertArrayEquals(new String[] { "TLSv1.3", "TLSv1.2", "TLSv1.1" }, TlsProtocol.toProtocolNames(protocols));
		
		List<TlsProtocol> reversed = List.of(TlsProtocol.TLS_V1_1, TlsProtocol.TLS_V1_2, TlsProtocol.TLS_V1_3);
		assertArrayEquals(new String[] { "TLSv1.1", "TLSv1.2", "TLSv1.3" }, TlsProtocol.toProtocolNames(reversed));
	}
	
	@Test
	void isDeprecatedFalseForSecureProtocols() {
		assertFalse(TlsProtocol.TLS_V1_2.isDeprecated());
		assertFalse(TlsProtocol.TLS_V1_3.isDeprecated());
	}
	
	@Test
	void isDeprecatedTrueForLegacyProtocols() {
		assertTrue(TlsProtocol.SSL_V3.isDeprecated());
		assertTrue(TlsProtocol.TLS_V1.isDeprecated());
		assertTrue(TlsProtocol.TLS_V1_1.isDeprecated());
	}
	
	@Test
	void isDeprecatedTrueForSslV2Hello() {
		assertTrue(TlsProtocol.SSL_V2_HELLO.isDeprecated());
	}
	
	@Test
	void protocolNameReturnsJsseName() {
		assertEquals("SSLv3", TlsProtocol.SSL_V3.protocolName());
		assertEquals("TLSv1", TlsProtocol.TLS_V1.protocolName());
		assertEquals("TLSv1.1", TlsProtocol.TLS_V1_1.protocolName());
		assertEquals("TLSv1.2", TlsProtocol.TLS_V1_2.protocolName());
		assertEquals("TLSv1.3", TlsProtocol.TLS_V1_3.protocolName());
		assertEquals("SSLv2Hello", TlsProtocol.SSL_V2_HELLO.protocolName());
	}
	
	@Test
	void toStringReturnsProtocolName() {
		for (TlsProtocol protocol : TlsProtocol.values()) {
			assertEquals(protocol.protocolName(), protocol.toString());
		}
		assertEquals("TLSv1.3", TlsProtocol.TLS_V1_3.toString());
	}
	
	@Test
	void toStringDiffersFromEnumName() {
		assertNotEquals(TlsProtocol.TLS_V1_3.name(), TlsProtocol.TLS_V1_3.toString());
		assertNotEquals(TlsProtocol.SSL_V2_HELLO.name(), TlsProtocol.SSL_V2_HELLO.toString());
		assertEquals("TLS_V1_3", TlsProtocol.TLS_V1_3.name());
		assertEquals("TLSv1.3", TlsProtocol.TLS_V1_3.toString());
	}
	
	@Test
	void byNameRoundTripsProtocolName() {
		for (TlsProtocol protocol : TlsProtocol.values()) {
			Optional<TlsProtocol> resolved = TlsProtocol.byName(protocol.protocolName());
			assertTrue(resolved.isPresent());
			assertSame(protocol, resolved.orElseThrow());
		}
	}
	
	@Test
	void protocolNamesAreUnique() {
		Set<String> names = new HashSet<>();
		for (TlsProtocol protocol : TlsProtocol.values()) {
			assertTrue(names.add(protocol.protocolName()));
		}
		assertEquals(6, names.size());
	}
	
	@Test
	void valuesOrderedByProtocolVersion() {
		assertTrue(TlsProtocol.SSL_V3.ordinal() < TlsProtocol.TLS_V1.ordinal());
		assertTrue(TlsProtocol.TLS_V1.ordinal() < TlsProtocol.TLS_V1_1.ordinal());
		assertTrue(TlsProtocol.TLS_V1_1.ordinal() < TlsProtocol.TLS_V1_2.ordinal());
		assertTrue(TlsProtocol.TLS_V1_2.ordinal() < TlsProtocol.TLS_V1_3.ordinal());
		assertTrue(TlsProtocol.TLS_V1_3.ordinal() < TlsProtocol.SSL_V2_HELLO.ordinal());
	}
	
	@Test
	void toProtocolNamesWithDuplicatesKeepsDuplicates() {
		String[] names = TlsProtocol.toProtocolNames(List.of(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_3));
		
		assertEquals(2, names.length);
		assertArrayEquals(new String[] { "TLSv1.3", "TLSv1.3" }, names);
	}
	
	@Test
	void toProtocolNamesAcceptedBySslSocket() throws Exception {
		try (SSLSocket socket = (SSLSocket) SSLContext.getDefault().getSocketFactory().createSocket()) {
			List<String> supported = List.of(socket.getSupportedProtocols());
			for (TlsProtocol protocol : TlsProtocol.values()) {
				assertTrue(supported.contains(protocol.protocolName()), protocol.protocolName());
			}
			
			assertDoesNotThrow(() -> socket.setEnabledProtocols(TlsProtocol.toProtocolNames(List.of(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_2))));
			List<String> enabled = List.of(socket.getEnabledProtocols());
			assertTrue(enabled.contains("TLSv1.3"));
			assertTrue(enabled.contains("TLSv1.2"));
		}
	}
	
	@Test
	void byNameCoversEveryJvmSupportedProtocol() throws Exception {
		String[] supported = SSLContext.getDefault().getSupportedSSLParameters().getProtocols();
		
		assertNotEquals(0, supported.length);
		for (String name : supported) {
			assertTrue(TlsProtocol.byName(name).isPresent(), name);
		}
	}
	
	@Test
	void toProtocolNamesReturnsIndependentArray() {
		List<TlsProtocol> protocols = List.of(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_2);
		
		String[] first = TlsProtocol.toProtocolNames(protocols);
		String[] second = TlsProtocol.toProtocolNames(protocols);
		assertNotSame(first, second);
		
		first[0] = "mutated";
		assertArrayEquals(new String[] { "TLSv1.3", "TLSv1.2" }, second);
		assertArrayEquals(new String[] { "TLSv1.3", "TLSv1.2" }, TlsProtocol.toProtocolNames(protocols));
	}
	
	@Test
	void toProtocolNamesFromMutableList() {
		List<TlsProtocol> protocols = new ArrayList<>(List.of(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_2));
		String[] names = TlsProtocol.toProtocolNames(protocols);
		assertArrayEquals(new String[] { "TLSv1.3", "TLSv1.2" }, names);
		
		protocols.add(TlsProtocol.TLS_V1_1);
		assertArrayEquals(new String[] { "TLSv1.3", "TLSv1.2" }, names);
		assertArrayEquals(new String[] { "TLSv1.3", "TLSv1.2", "TLSv1.1" }, TlsProtocol.toProtocolNames(protocols));
		assertArrayEquals(names, TlsProtocol.toProtocolNames(Arrays.asList(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_2)));
	}
	
	@Test
	void toProtocolNamesForAllProtocols() {
		TlsProtocol[] values = TlsProtocol.values();
		String[] names = TlsProtocol.toProtocolNames(List.of(values));
		
		assertEquals(6, names.length);
		for (int i = 0; i < values.length; i++) {
			assertEquals(values[i].protocolName(), names[i]);
		}
	}
}
