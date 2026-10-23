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

import net.luis.utils.io.network.connection.Framing;
import net.luis.utils.io.network.connection.tcp.TcpClientConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslClientConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class SslClientConfigBuilderTest {
	
	private static SSLContext context;
	
	@BeforeAll
	static void setUp() throws Exception {
		context = SslTestContext.clientContext();
	}
	
	@Test
	void builderStartsWithDefaults() {
		assertEquals(SslClientConfig.DEFAULT, SslClientConfig.builder().build());
	}
	
	@Test
	void connectTimeoutWithNull() {
		assertThrows(NullPointerException.class, () -> SslClientConfig.builder().connectTimeout(null));
	}
	
	@Test
	void readTimeoutWithNull() {
		assertThrows(NullPointerException.class, () -> SslClientConfig.builder().readTimeout(null));
	}
	
	@Test
	void framingWithNull() {
		assertThrows(NullPointerException.class, () -> SslClientConfig.builder().framing(null));
	}
	
	@Test
	void enabledProtocolsWithNull() {
		assertThrows(NullPointerException.class, () -> SslClientConfig.builder().enabledProtocols(null));
	}
	
	@Test
	void enabledCipherSuitesWithNull() {
		assertThrows(NullPointerException.class, () -> SslClientConfig.builder().enabledCipherSuites(null));
	}
	
	@Test
	void buildWithInvalidMaxMessageSize() {
		SslClientConfigBuilder builder = assertDoesNotThrow(() -> SslClientConfig.builder().maxMessageSize(0));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithZeroConnectTimeout() {
		SslClientConfigBuilder builder = assertDoesNotThrow(() -> SslClientConfig.builder().connectTimeout(Duration.ZERO));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithNullProtocolElement() {
		SslClientConfigBuilder builder = assertDoesNotThrow(() -> SslClientConfig.builder().enabledProtocols(Arrays.asList(TlsProtocol.TLS_V1_3, null)));
		
		assertThrows(NullPointerException.class, builder::build);
	}
	
	@Test
	void noReadTimeoutClearsTimeout() {
		SslClientConfig config = SslClientConfig.builder().readTimeout(Duration.ofSeconds(5)).noReadTimeout().build();
		
		assertNull(config.transport().readTimeout());
	}
	
	@Test
	void sslContextWithNullUsesDefault() {
		SslClientConfig config = SslClientConfig.builder().sslContext(context).sslContext(null).build();
		
		assertNull(config.tls().sslContext());
	}
	
	@Test
	void connectTimeoutSetsTransport() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		
		assertSame(builder, builder.connectTimeout(Duration.ofSeconds(2)));
		assertEquals(Duration.ofSeconds(2), builder.build().transport().connectTimeout());
	}
	
	@Test
	void readTimeoutSetsTransport() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		
		assertSame(builder, builder.readTimeout(Duration.ofSeconds(3)));
		assertEquals(Duration.ofSeconds(3), builder.build().transport().readTimeout());
	}
	
	@Test
	void maxMessageSizeSetsTransport() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		
		assertSame(builder, builder.maxMessageSize(256));
		assertEquals(256, builder.build().transport().maxMessageSize());
	}
	
	@Test
	void framingSetsTransport() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		
		assertSame(builder, builder.framing(Framing.lines()));
		assertEquals(Framing.lines(), builder.build().transport().framing());
	}
	
	@Test
	void tcpNoDelaySetsTransport() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		
		assertSame(builder, builder.tcpNoDelay(false));
		assertFalse(builder.build().transport().tcpNoDelay());
	}
	
	@Test
	void keepAliveSetsTransport() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		
		assertSame(builder, builder.keepAlive(false));
		assertFalse(builder.build().transport().keepAlive());
	}
	
	@Test
	void sslContextSetsTls() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		
		assertSame(builder, builder.sslContext(context));
		assertSame(context, builder.build().tls().sslContext());
	}
	
	@Test
	void enabledProtocolsSetsTls() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		
		assertSame(builder, builder.enabledProtocols(List.of(TlsProtocol.TLS_V1_2)));
		assertEquals(List.of(TlsProtocol.TLS_V1_2), builder.build().tls().enabledProtocols());
	}
	
	@Test
	void enabledCipherSuitesSetsTls() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		
		assertSame(builder, builder.enabledCipherSuites(List.of("TLS_AES_128_GCM_SHA256")));
		assertEquals(List.of("TLS_AES_128_GCM_SHA256"), builder.build().tls().enabledCipherSuites());
	}
	
	@Test
	void verifyHostnameSetsTls() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		
		assertSame(builder, builder.verifyHostname(false));
		assertFalse(builder.build().tls().verifyHostname());
	}
	
	@Test
	void buildWithAllValues() {
		SslClientConfig config = SslClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(1))
			.readTimeout(Duration.ofSeconds(2))
			.maxMessageSize(100)
			.framing(Framing.none())
			.tcpNoDelay(false)
			.keepAlive(false)
			.sslContext(context)
			.enabledProtocols(List.of(TlsProtocol.TLS_V1_3))
			.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384"))
			.verifyHostname(false)
			.build();
		
		TcpClientConfig transport = TcpClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(1))
			.readTimeout(Duration.ofSeconds(2))
			.maxMessageSize(100)
			.framing(Framing.none())
			.tcpNoDelay(false)
			.keepAlive(false)
			.build();
		SslUpgradeConfig tls = SslUpgradeConfig.builder()
			.sslContext(context)
			.enabledProtocols(List.of(TlsProtocol.TLS_V1_3))
			.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384"))
			.verifyHostname(false)
			.build();
		assertEquals(new SslClientConfig(transport, tls), config);
	}
	
	@Test
	void builderReuseAfterBuild() {
		SslClientConfigBuilder builder = SslClientConfig.builder().maxMessageSize(100);
		SslClientConfig first = builder.build();
		
		builder.maxMessageSize(200).verifyHostname(false);
		SslClientConfig second = builder.build();
		
		assertEquals(100, first.transport().maxMessageSize());
		assertTrue(first.tls().verifyHostname());
		assertEquals(200, second.transport().maxMessageSize());
		assertFalse(second.tls().verifyHostname());
	}
	
	@Test
	void methodChainingConsistency() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		
		assertSame(builder, builder.connectTimeout(Duration.ofSeconds(1)));
		assertSame(builder, builder.readTimeout(Duration.ofSeconds(1)));
		assertSame(builder, builder.noReadTimeout());
		assertSame(builder, builder.maxMessageSize(10));
		assertSame(builder, builder.framing(Framing.lines()));
		assertSame(builder, builder.tcpNoDelay(true));
		assertSame(builder, builder.keepAlive(true));
		assertSame(builder, builder.sslContext(null));
		assertSame(builder, builder.enabledProtocols(List.of()));
		assertSame(builder, builder.enabledCipherSuites(List.of()));
		assertSame(builder, builder.verifyHostname(true));
	}
	
	@Test
	void builtListsAreImmutable() {
		List<TlsProtocol> protocols = new ArrayList<>(List.of(TlsProtocol.TLS_V1_3));
		SslClientConfig config = SslClientConfig.builder().enabledProtocols(protocols).build();
		
		protocols.add(TlsProtocol.TLS_V1_2);
		
		assertThrows(UnsupportedOperationException.class, () -> config.tls().enabledProtocols().add(TlsProtocol.TLS_V1_1));
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.tls().enabledProtocols());
	}
}
