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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslUpgradeConfig}.<br>
 *
 * @author Luis-St
 */
class SslUpgradeConfigTest {
	
	private static SSLContext context;
	
	@BeforeAll
	static void setUp() throws Exception {
		context = SSLContext.getDefault();
	}
	
	@Test
	void constructWithAllValues() {
		SslUpgradeConfig config = new SslUpgradeConfig(context, List.of(TlsProtocol.TLS_V1_3), List.of("TLS_AES_256_GCM_SHA384"), false);
		
		assertSame(context, config.sslContext());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.enabledProtocols());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
		assertFalse(config.verifyHostname());
	}
	
	@Test
	void constructWithNullSslContextIsAllowed() {
		SslUpgradeConfig config = assertDoesNotThrow(() -> new SslUpgradeConfig(null, List.of(TlsProtocol.TLS_V1_3), List.of(), true));
		
		assertNull(config.sslContext());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.enabledProtocols());
		assertTrue(config.verifyHostname());
	}
	
	@Test
	void constructWithNullEnabledProtocolsThrows() {
		assertThrows(NullPointerException.class, () -> new SslUpgradeConfig(null, null, List.of(), true));
	}
	
	@Test
	void constructWithNullEnabledCipherSuitesThrows() {
		assertThrows(NullPointerException.class, () -> new SslUpgradeConfig(null, List.of(), null, true));
	}
	
	@Test
	void constructWithNullProtocolElementThrows() {
		List<TlsProtocol> protocols = new ArrayList<>();
		protocols.add(null);
		
		assertThrows(NullPointerException.class, () -> new SslUpgradeConfig(null, protocols, List.of(), true));
	}
	
	@Test
	void constructWithNullCipherSuiteElementThrows() {
		List<String> cipherSuites = new ArrayList<>();
		cipherSuites.add(null);
		
		assertThrows(NullPointerException.class, () -> new SslUpgradeConfig(null, List.of(), cipherSuites, true));
	}
	
	@Test
	void defaultConfigValues() {
		SslUpgradeConfig config = SslUpgradeConfig.DEFAULT;
		
		assertNull(config.sslContext());
		assertTrue(config.enabledProtocols().isEmpty());
		assertTrue(config.enabledCipherSuites().isEmpty());
		assertTrue(config.verifyHostname());
	}
	
	@Test
	void constructCopiesProtocolsDefensively() {
		List<TlsProtocol> protocols = new ArrayList<>(List.of(TlsProtocol.TLS_V1_3));
		SslUpgradeConfig config = new SslUpgradeConfig(null, protocols, List.of(), true);
		
		protocols.add(TlsProtocol.TLS_V1_2);
		
		assertEquals(1, config.enabledProtocols().size());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.enabledProtocols());
		assertThrows(UnsupportedOperationException.class, () -> config.enabledProtocols().add(TlsProtocol.TLS_V1_1));
	}
	
	@Test
	void constructCopiesCipherSuitesDefensively() {
		List<String> cipherSuites = new ArrayList<>(List.of("TLS_AES_256_GCM_SHA384"));
		SslUpgradeConfig config = new SslUpgradeConfig(null, List.of(), cipherSuites, true);
		
		cipherSuites.add("TLS_AES_128_GCM_SHA256");
		
		assertEquals(1, config.enabledCipherSuites().size());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
		assertThrows(UnsupportedOperationException.class, () -> config.enabledCipherSuites().add("TLS_AES_128_GCM_SHA256"));
	}
	
	@Test
	void builderReturnsNewInstanceEachCall() {
		SslUpgradeConfigBuilder first = SslUpgradeConfig.builder();
		SslUpgradeConfigBuilder second = SslUpgradeConfig.builder();
		
		assertNotNull(first);
		assertNotNull(second);
		assertNotSame(first, second);
	}
	
	@Test
	void resolveSslContextReturnsConfiguredContext() throws Exception {
		SSLContext configured = SslTestContext.clientContext();
		SslUpgradeConfig config = new SslUpgradeConfig(configured, List.of(), List.of(), true);
		
		assertSame(configured, config.resolveSslContext());
		assertNotSame(SSLContext.getDefault(), config.resolveSslContext());
	}
	
	@Test
	void resolveSslContextReturnsDefaultWhenNull() throws Exception {
		assertSame(SSLContext.getDefault(), SslUpgradeConfig.DEFAULT.resolveSslContext());
	}
	
	@Test
	void resolveSslContextDoesNotChangeComponent() throws Exception {
		SslUpgradeConfig.DEFAULT.resolveSslContext();
		
		assertNull(SslUpgradeConfig.DEFAULT.sslContext());
	}
	
	@Test
	void equalityAndHashCode() {
		SslUpgradeConfig first = new SslUpgradeConfig(context, List.of(TlsProtocol.TLS_V1_3), List.of("TLS_AES_256_GCM_SHA384"), true);
		SslUpgradeConfig second = new SslUpgradeConfig(context, List.of(TlsProtocol.TLS_V1_3), List.of("TLS_AES_256_GCM_SHA384"), true);
		SslUpgradeConfig other = new SslUpgradeConfig(context, List.of(TlsProtocol.TLS_V1_3), List.of("TLS_AES_256_GCM_SHA384"), false);
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, other);
	}
}
