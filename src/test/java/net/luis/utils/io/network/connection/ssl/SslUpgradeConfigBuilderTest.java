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

import net.luis.utils.io.network.connection.tcp.TcpClientConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslUpgradeConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class SslUpgradeConfigBuilderTest {
	
	private static SSLContext context;
	
	@BeforeAll
	static void setUp() throws Exception {
		context = SSLContext.getDefault();
	}
	
	@Test
	void builderDefaultValues() {
		SslUpgradeConfig config = SslUpgradeConfig.builder().build();
		
		assertNull(config.sslContext());
		assertTrue(config.enabledProtocols().isEmpty());
		assertTrue(config.enabledCipherSuites().isEmpty());
		assertTrue(config.verifyHostname());
	}
	
	@Test
	void enabledProtocolsWithNullThrows() {
		SslUpgradeConfigBuilder builder = SslUpgradeConfig.builder();
		
		assertThrows(NullPointerException.class, () -> builder.enabledProtocols(null));
	}
	
	@Test
	void enabledCipherSuitesWithNullThrows() {
		SslUpgradeConfigBuilder builder = SslUpgradeConfig.builder();
		
		assertThrows(NullPointerException.class, () -> builder.enabledCipherSuites(null));
	}
	
	@Test
	void buildWithNullProtocolElementThrows() {
		List<TlsProtocol> protocols = new ArrayList<>();
		protocols.add(null);
		SslUpgradeConfigBuilder builder = assertDoesNotThrow(() -> SslUpgradeConfig.builder().enabledProtocols(protocols));
		
		assertThrows(NullPointerException.class, builder::build);
	}
	
	@Test
	void sslContextWithValidContext() {
		SslUpgradeConfig config = SslUpgradeConfig.builder().sslContext(context).build();
		
		assertSame(context, config.sslContext());
	}
	
	@Test
	void sslContextWithNullIsAllowed() {
		SslUpgradeConfig config = SslUpgradeConfig.builder().sslContext(context).sslContext(null).build();
		
		assertNull(config.sslContext());
	}
	
	@Test
	void enabledProtocolsWithValidList() {
		SslUpgradeConfig config = SslUpgradeConfig.builder().enabledProtocols(List.of(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_2)).build();
		
		assertEquals(List.of(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_2), config.enabledProtocols());
	}
	
	@Test
	void enabledProtocolsWithEmptyList() {
		SslUpgradeConfig config = SslUpgradeConfig.builder().enabledProtocols(List.of()).build();
		
		assertTrue(config.enabledProtocols().isEmpty());
	}
	
	@Test
	void enabledCipherSuitesWithValidList() {
		SslUpgradeConfig config = SslUpgradeConfig.builder().enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384", "TLS_AES_128_GCM_SHA256")).build();
		
		assertEquals(List.of("TLS_AES_256_GCM_SHA384", "TLS_AES_128_GCM_SHA256"), config.enabledCipherSuites());
	}
	
	@Test
	void enabledCipherSuitesWithEmptyList() {
		SslUpgradeConfig config = SslUpgradeConfig.builder().enabledCipherSuites(List.of()).build();
		
		assertTrue(config.enabledCipherSuites().isEmpty());
	}
	
	@Test
	void verifyHostnameDisabled() {
		SslUpgradeConfig config = SslUpgradeConfig.builder().verifyHostname(false).build();
		
		assertFalse(config.verifyHostname());
	}
	
	@Test
	void verifyHostnameEnabled() {
		SslUpgradeConfig config = SslUpgradeConfig.builder().verifyHostname(false).verifyHostname(true).build();
		
		assertTrue(config.verifyHostname());
	}
	
	@Test
	void methodChainingConsistency() {
		SslUpgradeConfigBuilder builder = SslUpgradeConfig.builder();
		
		assertSame(builder, builder.sslContext(context));
		assertSame(builder, builder.enabledProtocols(List.of(TlsProtocol.TLS_V1_3)));
		assertSame(builder, builder.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384")));
		assertSame(builder, builder.verifyHostname(false));
	}
	
	@Test
	void builderWithAllValues() {
		SslUpgradeConfig config = SslUpgradeConfig.builder()
			.sslContext(context)
			.enabledProtocols(List.of(TlsProtocol.TLS_V1_3))
			.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384"))
			.verifyHostname(false)
			.build();
		
		assertSame(context, config.sslContext());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.enabledProtocols());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
		assertFalse(config.verifyHostname());
	}
	
	@Test
	void builderOverwriteValues() {
		SslUpgradeConfig config = SslUpgradeConfig.builder()
			.enabledProtocols(List.of(TlsProtocol.TLS_V1_2))
			.enabledProtocols(List.of(TlsProtocol.TLS_V1_3))
			.build();
		
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.enabledProtocols());
	}
	
	@Test
	void buildMatchesDefaultConstant() {
		assertEquals(SslUpgradeConfig.DEFAULT, SslUpgradeConfig.builder().build());
	}
	
	@Test
	void builderMultipleBuilds() {
		SslUpgradeConfigBuilder builder = SslUpgradeConfig.builder().sslContext(context).enabledProtocols(List.of(TlsProtocol.TLS_V1_3));
		
		SslUpgradeConfig first = builder.build();
		SslUpgradeConfig second = builder.build();
		
		assertEquals(first, second);
		assertNotSame(first, second);
	}
	
	@Test
	void builderReuseAfterBuild() {
		SslUpgradeConfigBuilder builder = SslUpgradeConfig.builder().enabledProtocols(List.of(TlsProtocol.TLS_V1_2));
		
		SslUpgradeConfig first = builder.build();
		assertEquals(List.of(TlsProtocol.TLS_V1_2), first.enabledProtocols());
		
		builder.enabledProtocols(List.of(TlsProtocol.TLS_V1_3));
		SslUpgradeConfig second = builder.build();
		
		assertEquals(List.of(TlsProtocol.TLS_V1_3), second.enabledProtocols());
		assertEquals(List.of(TlsProtocol.TLS_V1_2), first.enabledProtocols());
	}
	
	@Test
	void buildIsolatesFromMutableSourceList() {
		List<TlsProtocol> protocols = new ArrayList<>(List.of(TlsProtocol.TLS_V1_3));
		SslUpgradeConfigBuilder builder = SslUpgradeConfig.builder().enabledProtocols(protocols);
		
		protocols.add(TlsProtocol.TLS_V1_2);
		SslUpgradeConfig config = builder.build();
		protocols.add(TlsProtocol.TLS_V1_1);
		
		assertEquals(List.of(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_2), config.enabledProtocols());
		assertThrows(UnsupportedOperationException.class, () -> config.enabledProtocols().add(TlsProtocol.TLS_V1_1));
	}
	
	@Test
	void buildFeedsToClientConfig() {
		SslUpgradeConfig config = SslUpgradeConfig.builder()
			.sslContext(context)
			.enabledProtocols(List.of(TlsProtocol.TLS_V1_3))
			.verifyHostname(false)
			.build();
		TcpClientConfig base = TcpClientConfig.builder().bufferSize(1024).readTimeout(Duration.ofSeconds(5)).build();
		
		SslClientConfig result = config.toClientConfig(base);
		
		assertSame(context, result.sslContext());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), result.enabledProtocols());
		assertFalse(result.verifyHostname());
		assertEquals(1024, result.bufferSize());
		assertEquals(Duration.ofSeconds(5), result.readTimeout());
	}
}
