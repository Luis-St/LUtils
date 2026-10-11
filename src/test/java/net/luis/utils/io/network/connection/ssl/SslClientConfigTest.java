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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslClientConfig}.<br>
 *
 * @author Luis-St
 */
class SslClientConfigTest {
	
	private static SSLContext context;
	
	@BeforeAll
	static void setUp() throws Exception {
		context = SSLContext.getDefault();
	}
	
	@Test
	void defaultConfig() {
		SslClientConfig config = SslClientConfig.DEFAULT;
		
		assertEquals(Duration.ofSeconds(30), config.connectTimeout());
		assertEquals(Duration.ZERO, config.readTimeout());
		assertEquals(Duration.ZERO, config.writeTimeout());
		assertEquals(8192, config.bufferSize());
		assertTrue(config.tcpNoDelay());
		assertTrue(config.keepAlive());
		assertNull(config.sslContext());
		assertTrue(config.enabledProtocols().isEmpty());
		assertTrue(config.enabledCipherSuites().isEmpty());
		assertTrue(config.verifyHostname());
		assertNull(config.onConnect());
		assertNull(config.onDisconnect());
		assertNull(config.onError());
	}
	
	@Test
	void constructWithNullConnectTimeoutThrows() {
		assertThrows(NullPointerException.class, () -> new SslClientConfig(null, Duration.ZERO, Duration.ZERO, 8192, true, true, true, null, List.of(), List.of(), true, null, null, null));
	}
	
	@Test
	void constructWithNullReadTimeoutThrows() {
		assertThrows(NullPointerException.class, () -> new SslClientConfig(Duration.ofSeconds(30), null, Duration.ZERO, 8192, true, true, true, null, List.of(), List.of(), true, null, null, null));
	}
	
	@Test
	void constructWithNullWriteTimeoutThrows() {
		assertThrows(NullPointerException.class, () -> new SslClientConfig(Duration.ofSeconds(30), Duration.ZERO, null, 8192, true, true, true, null, List.of(), List.of(), true, null, null, null));
	}
	
	@Test
	void constructWithNullEnabledProtocolsThrows() {
		assertThrows(NullPointerException.class, () -> new SslClientConfig(Duration.ofSeconds(30), Duration.ZERO, Duration.ZERO, 8192, true, true, true, null, null, List.of(), true, null, null, null));
	}
	
	@Test
	void constructWithNullEnabledCipherSuitesThrows() {
		assertThrows(NullPointerException.class, () -> new SslClientConfig(Duration.ofSeconds(30), Duration.ZERO, Duration.ZERO, 8192, true, true, true, null, List.of(), null, true, null, null, null));
	}
	
	@Test
	void constructWithInvalidBufferSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SslClientConfig(Duration.ofSeconds(30), Duration.ZERO, Duration.ZERO, 0, true, true, true, null, List.of(), List.of(), true, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> new SslClientConfig(Duration.ofSeconds(30), Duration.ZERO, Duration.ZERO, -1, true, true, true, null, List.of(), List.of(), true, null, null, null));
	}
	
	@Test
	void constructWithNullSslContextIsAllowed() {
		SslClientConfig config = new SslClientConfig(Duration.ofSeconds(30), Duration.ZERO, Duration.ZERO, 8192, true, true, true, null, List.of(), List.of(), true, null, null, null);
		assertNull(config.sslContext());
	}
	
	@Test
	void constructWithNullProtocolElementThrows() {
		List<TlsProtocol> protocols = new ArrayList<>();
		protocols.add(null);
		
		assertThrows(NullPointerException.class, () -> new SslClientConfig(Duration.ofSeconds(30), Duration.ZERO, Duration.ZERO, 8192, true, true, true, null, protocols, List.of(), true, null, null, null));
	}
	
	@Test
	void constructCopiesProtocolsDefensively() {
		List<TlsProtocol> protocols = new ArrayList<>(List.of(TlsProtocol.TLS_V1_3));
		SslClientConfig config = new SslClientConfig(Duration.ofSeconds(30), Duration.ZERO, Duration.ZERO, 8192, true, true, true, null, protocols, List.of(), true, null, null, null);
		
		protocols.add(TlsProtocol.TLS_V1_2);
		assertEquals(1, config.enabledProtocols().size());
		assertThrows(UnsupportedOperationException.class, () -> config.enabledProtocols().add(TlsProtocol.TLS_V1_1));
	}
	
	@Test
	void constructCopiesCipherSuitesDefensively() {
		List<String> ciphers = new ArrayList<>(List.of("TLS_AES_256_GCM_SHA384"));
		SslClientConfig config = new SslClientConfig(Duration.ofSeconds(30), Duration.ZERO, Duration.ZERO, 8192, true, true, true, null, List.of(), ciphers, true, null, null, null);
		
		ciphers.add("TLS_AES_128_GCM_SHA256");
		assertEquals(1, config.enabledCipherSuites().size());
		assertThrows(UnsupportedOperationException.class, () -> config.enabledCipherSuites().add("x"));
	}
	
	@Test
	void resolveSslContextReturnsConfiguredContext() throws Exception {
		SslClientConfig config = SslClientConfig.builder().sslContext(context).build();
		assertSame(context, config.resolveSslContext());
	}
	
	@Test
	void resolveSslContextReturnsDefaultWhenNull() throws Exception {
		SslClientConfig config = SslClientConfig.DEFAULT;
		assertNotNull(config.resolveSslContext());
	}
	
	@Test
	void builder() {
		SslClientConfig config = SslClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(10))
			.readTimeout(Duration.ofSeconds(30))
			.writeTimeout(Duration.ofSeconds(15))
			.bufferSize(16384)
			.tcpNoDelay(false)
			.keepAlive(false)
			.sslContext(context)
			.enabledProtocols(List.of(TlsProtocol.TLS_V1_3))
			.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384"))
			.verifyHostname(false)
			.build();
		
		assertEquals(Duration.ofSeconds(10), config.connectTimeout());
		assertEquals(Duration.ofSeconds(30), config.readTimeout());
		assertEquals(Duration.ofSeconds(15), config.writeTimeout());
		assertEquals(16384, config.bufferSize());
		assertFalse(config.tcpNoDelay());
		assertFalse(config.keepAlive());
		assertSame(context, config.sslContext());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.enabledProtocols());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
		assertFalse(config.verifyHostname());
	}
	
	@Test
	void builderWithHandlers() {
		SslClientConfig config = SslClientConfig.builder()
			.onConnect((connection, local, remote, timestamp) -> {})
			.onDisconnect((connection, local, remote, timestamp) -> {})
			.onError((connection, type, msg, cause) -> {})
			.build();
		
		assertNotNull(config.onConnect());
		assertNotNull(config.onDisconnect());
		assertNotNull(config.onError());
	}
	
	@Test
	void framingIsEnabledByDefault() {
		assertTrue(SslClientConfig.builder().build().framing());
	}
	
	@Test
	void framingCanBeDisabled() {
		assertFalse(SslClientConfig.builder().framing(false).build().framing());
		assertTrue(SslClientConfig.builder().framing(false).framing(true).build().framing());
	}
	
}
