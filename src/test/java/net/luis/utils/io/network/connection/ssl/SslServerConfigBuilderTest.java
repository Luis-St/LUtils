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

import net.luis.utils.io.network.connection.ErrorHandler;
import net.luis.utils.io.network.connection.Framing;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import net.luis.utils.io.network.connection.tcp.TcpServerConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslServerConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class SslServerConfigBuilderTest {
	
	private static SSLContext context;
	
	@BeforeAll
	static void setUp() throws Exception {
		context = SslTestContext.serverContext();
	}
	
	@Test
	void builderStartsWithDefaults() {
		SslServerConfig config = SslServerConfig.builder(context).build();
		
		assertEquals(TcpServerConfig.DEFAULT, config.transport());
		assertSame(context, config.sslContext());
		assertTrue(config.enabledProtocols().isEmpty());
		assertTrue(config.enabledCipherSuites().isEmpty());
		assertEquals(SslClientAuth.NONE, config.clientAuth());
	}
	
	@Test
	void builderWithNullSslContext() {
		assertThrows(NullPointerException.class, () -> SslServerConfig.builder(null));
	}
	
	@Test
	void framingWithNull() {
		assertThrows(NullPointerException.class, () -> SslServerConfig.builder(context).framing(null));
	}
	
	@Test
	void clientReadTimeoutWithNull() {
		assertThrows(NullPointerException.class, () -> SslServerConfig.builder(context).clientReadTimeout(null));
	}
	
	@Test
	void executorStrategyWithNull() {
		assertThrows(NullPointerException.class, () -> SslServerConfig.builder(context).executorStrategy(null));
	}
	
	@Test
	void enabledProtocolsWithNull() {
		assertThrows(NullPointerException.class, () -> SslServerConfig.builder(context).enabledProtocols(null));
	}
	
	@Test
	void enabledCipherSuitesWithNull() {
		assertThrows(NullPointerException.class, () -> SslServerConfig.builder(context).enabledCipherSuites(null));
	}
	
	@Test
	void clientAuthWithNull() {
		assertThrows(NullPointerException.class, () -> SslServerConfig.builder(context).clientAuth(null));
	}
	
	@Test
	void buildWithInvalidBacklog() {
		SslServerConfigBuilder builder = assertDoesNotThrow(() -> SslServerConfig.builder(context).backlog(0));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithInvalidMaxMessageSize() {
		SslServerConfigBuilder builder = assertDoesNotThrow(() -> SslServerConfig.builder(context).maxMessageSize(0));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void noClientReadTimeoutClearsTimeout() {
		SslServerConfig config = SslServerConfig.builder(context).clientReadTimeout(Duration.ofSeconds(5)).noClientReadTimeout().build();
		
		assertNull(config.transport().clientReadTimeout());
	}
	
	@Test
	void onErrorWithNullClearsHandler() {
		SslServerConfig config = SslServerConfig.builder(context).onError((client, error) -> {}).onError(null).build();
		
		assertNull(config.transport().onError());
	}
	
	@Test
	void backlogSetsTransport() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		
		assertSame(builder, builder.backlog(5));
		assertEquals(5, builder.build().transport().backlog());
	}
	
	@Test
	void maxMessageSizeSetsTransport() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		
		assertSame(builder, builder.maxMessageSize(256));
		assertEquals(256, builder.build().transport().maxMessageSize());
	}
	
	@Test
	void framingSetsTransport() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		
		assertSame(builder, builder.framing(Framing.lines()));
		assertEquals(Framing.lines(), builder.build().transport().framing());
	}
	
	@Test
	void clientReadTimeoutSetsTransport() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		
		assertSame(builder, builder.clientReadTimeout(Duration.ofSeconds(4)));
		assertEquals(Duration.ofSeconds(4), builder.build().transport().clientReadTimeout());
	}
	
	@Test
	void tcpNoDelaySetsTransport() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		
		assertSame(builder, builder.tcpNoDelay(false));
		assertFalse(builder.build().transport().tcpNoDelay());
	}
	
	@Test
	void keepAliveSetsTransport() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		
		assertSame(builder, builder.keepAlive(false));
		assertFalse(builder.build().transport().keepAlive());
	}
	
	@Test
	void executorStrategySetsTransport() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(2);
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		
		assertSame(builder, builder.executorStrategy(strategy));
		assertSame(strategy, builder.build().transport().executorStrategy());
	}
	
	@Test
	void onErrorSetsTransport() {
		ErrorHandler handler = (client, error) -> {};
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		
		assertSame(builder, builder.onError(handler));
		assertSame(handler, builder.build().transport().onError());
	}
	
	@Test
	void enabledProtocolsSetsValue() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		
		assertSame(builder, builder.enabledProtocols(List.of(TlsProtocol.TLS_V1_2, TlsProtocol.TLS_V1_3)));
		assertEquals(List.of(TlsProtocol.TLS_V1_2, TlsProtocol.TLS_V1_3), builder.build().enabledProtocols());
	}
	
	@Test
	void enabledCipherSuitesSetsValue() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		
		assertSame(builder, builder.enabledCipherSuites(List.of("TLS_AES_128_GCM_SHA256")));
		assertEquals(List.of("TLS_AES_128_GCM_SHA256"), builder.build().enabledCipherSuites());
	}
	
	@Test
	void clientAuthSetsValue() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		
		for (SslClientAuth clientAuth : SslClientAuth.values()) {
			assertSame(builder, builder.clientAuth(clientAuth));
			assertEquals(clientAuth, builder.build().clientAuth());
		}
	}
	
	@Test
	void buildWithAllValues() {
		ErrorHandler handler = (client, error) -> {};
		ClientExecutorStrategy strategy = ClientExecutorStrategy.cachedPool();
		SslServerConfig config = SslServerConfig.builder(context)
			.backlog(7)
			.maxMessageSize(100)
			.framing(Framing.none())
			.clientReadTimeout(Duration.ofSeconds(2))
			.tcpNoDelay(false)
			.keepAlive(false)
			.executorStrategy(strategy)
			.onError(handler)
			.enabledProtocols(List.of(TlsProtocol.TLS_V1_3))
			.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384"))
			.clientAuth(SslClientAuth.REQUESTED)
			.build();
		
		TcpServerConfig transport = new TcpServerConfig(7, 100, Framing.none(), Duration.ofSeconds(2), false, false, strategy, handler);
		assertEquals(new SslServerConfig(transport, context, List.of(TlsProtocol.TLS_V1_3), List.of("TLS_AES_256_GCM_SHA384"), SslClientAuth.REQUESTED), config);
	}
	
	@Test
	void builderReuseAfterBuild() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context).backlog(3);
		SslServerConfig first = builder.build();
		
		builder.backlog(9).clientAuth(SslClientAuth.REQUIRED);
		SslServerConfig second = builder.build();
		
		assertEquals(3, first.transport().backlog());
		assertEquals(SslClientAuth.NONE, first.clientAuth());
		assertEquals(9, second.transport().backlog());
		assertEquals(SslClientAuth.REQUIRED, second.clientAuth());
	}
	
	@Test
	void methodChainingConsistency() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		
		assertSame(builder, builder.backlog(1));
		assertSame(builder, builder.maxMessageSize(10));
		assertSame(builder, builder.framing(Framing.lines()));
		assertSame(builder, builder.clientReadTimeout(Duration.ofSeconds(1)));
		assertSame(builder, builder.noClientReadTimeout());
		assertSame(builder, builder.tcpNoDelay(true));
		assertSame(builder, builder.keepAlive(true));
		assertSame(builder, builder.executorStrategy(ClientExecutorStrategy.virtualThreads()));
		assertSame(builder, builder.onError(null));
		assertSame(builder, builder.enabledProtocols(List.of()));
		assertSame(builder, builder.enabledCipherSuites(List.of()));
		assertSame(builder, builder.clientAuth(SslClientAuth.NONE));
	}
}
