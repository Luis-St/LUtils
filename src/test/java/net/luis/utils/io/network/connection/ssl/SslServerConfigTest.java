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

import net.luis.utils.io.network.connection.tcp.TcpServerConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslServerConfig}.<br>
 *
 * @author Luis-St
 */
class SslServerConfigTest {
	
	private static SSLContext context;
	
	@BeforeAll
	static void setUp() throws Exception {
		context = SslTestContext.serverContext();
	}
	
	@Test
	void constructWithAllValues() {
		TcpServerConfig transport = TcpServerConfig.builder().backlog(5).build();
		SslServerConfig config = new SslServerConfig(transport, context, List.of(TlsProtocol.TLS_V1_3), List.of("TLS_AES_256_GCM_SHA384"), SslClientAuth.REQUIRED);
		
		assertSame(transport, config.transport());
		assertSame(context, config.sslContext());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.enabledProtocols());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
		assertEquals(SslClientAuth.REQUIRED, config.clientAuth());
	}
	
	@Test
	void constructWithNullTransport() {
		assertThrows(NullPointerException.class, () -> new SslServerConfig(null, context, List.of(), List.of(), SslClientAuth.NONE));
	}
	
	@Test
	void constructWithNullSslContext() {
		assertThrows(NullPointerException.class, () -> new SslServerConfig(TcpServerConfig.DEFAULT, null, List.of(), List.of(), SslClientAuth.NONE));
	}
	
	@Test
	void constructWithNullEnabledProtocols() {
		assertThrows(NullPointerException.class, () -> new SslServerConfig(TcpServerConfig.DEFAULT, context, null, List.of(), SslClientAuth.NONE));
	}
	
	@Test
	void constructWithNullEnabledCipherSuites() {
		assertThrows(NullPointerException.class, () -> new SslServerConfig(TcpServerConfig.DEFAULT, context, List.of(), null, SslClientAuth.NONE));
	}
	
	@Test
	void constructWithNullClientAuth() {
		assertThrows(NullPointerException.class, () -> new SslServerConfig(TcpServerConfig.DEFAULT, context, List.of(), List.of(), null));
	}
	
	@Test
	void constructWithNullProtocolElement() {
		List<TlsProtocol> protocols = new ArrayList<>();
		protocols.add(null);
		
		assertThrows(NullPointerException.class, () -> new SslServerConfig(TcpServerConfig.DEFAULT, context, protocols, List.of(), SslClientAuth.NONE));
	}
	
	@Test
	void constructWithNullCipherSuiteElement() {
		List<String> cipherSuites = new ArrayList<>();
		cipherSuites.add(null);
		
		assertThrows(NullPointerException.class, () -> new SslServerConfig(TcpServerConfig.DEFAULT, context, List.of(), cipherSuites, SslClientAuth.NONE));
	}
	
	@Test
	void constructCopiesProtocolsDefensively() {
		List<TlsProtocol> protocols = new ArrayList<>(List.of(TlsProtocol.TLS_V1_3));
		SslServerConfig config = new SslServerConfig(TcpServerConfig.DEFAULT, context, protocols, List.of(), SslClientAuth.NONE);
		
		protocols.add(TlsProtocol.TLS_V1_2);
		
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.enabledProtocols());
		assertThrows(UnsupportedOperationException.class, () -> config.enabledProtocols().add(TlsProtocol.TLS_V1_1));
	}
	
	@Test
	void constructCopiesCipherSuitesDefensively() {
		List<String> cipherSuites = new ArrayList<>(List.of("TLS_AES_256_GCM_SHA384"));
		SslServerConfig config = new SslServerConfig(TcpServerConfig.DEFAULT, context, List.of(), cipherSuites, SslClientAuth.NONE);
		
		cipherSuites.add("TLS_AES_128_GCM_SHA256");
		
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
		assertThrows(UnsupportedOperationException.class, () -> config.enabledCipherSuites().add("TLS_AES_128_GCM_SHA256"));
	}
	
	@Test
	void constructWithEmptyLists() {
		SslServerConfig config = new SslServerConfig(TcpServerConfig.DEFAULT, context, List.of(), List.of(), SslClientAuth.NONE);
		
		assertTrue(config.enabledProtocols().isEmpty());
		assertTrue(config.enabledCipherSuites().isEmpty());
	}
	
	@Test
	void builderWithNullSslContext() {
		assertThrows(NullPointerException.class, () -> SslServerConfig.builder(null));
	}
	
	@Test
	void builderReturnsNewInstanceEachCall() {
		assertNotSame(SslServerConfig.builder(context), SslServerConfig.builder(context));
	}
	
	@Test
	void equalityAndHashCode() {
		SslServerConfig first = new SslServerConfig(TcpServerConfig.DEFAULT, context, List.of(TlsProtocol.TLS_V1_3), List.of(), SslClientAuth.NONE);
		SslServerConfig second = new SslServerConfig(TcpServerConfig.DEFAULT, context, List.of(TlsProtocol.TLS_V1_3), List.of(), SslClientAuth.NONE);
		SslServerConfig otherClientAuth = new SslServerConfig(TcpServerConfig.DEFAULT, context, List.of(TlsProtocol.TLS_V1_3), List.of(), SslClientAuth.REQUESTED);
		SslServerConfig otherTransport = new SslServerConfig(TcpServerConfig.builder().backlog(3).build(), context, List.of(TlsProtocol.TLS_V1_3), List.of(), SslClientAuth.NONE);
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, otherClientAuth);
		assertNotEquals(first, otherTransport);
	}
}
