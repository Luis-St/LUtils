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

import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.ArrayList;
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
		context = SSLContext.getDefault();
	}
	
	@Test
	void builderDefaultValues() {
		SslServerConfig config = SslServerConfig.builder(context).build();
		
		assertEquals(50, config.backlog());
		assertEquals(8192, config.clientBufferSize());
		assertEquals(Duration.ZERO, config.clientReadTimeout());
		assertTrue(config.framing());
		assertTrue(config.tcpNoDelay());
		assertTrue(config.keepAlive());
		assertSame(context, config.sslContext());
		assertTrue(config.enabledProtocols().isEmpty());
		assertTrue(config.enabledCipherSuites().isEmpty());
		assertEquals(SslClientAuth.NONE, config.clientAuth());
		assertNotNull(config.executorStrategy());
		assertNull(config.onClientConnect());
		assertNull(config.onClientDisconnect());
		assertNull(config.onMessage());
		assertNull(config.onError());
	}
	
	@Test
	void backlogWithValidValue() {
		SslServerConfig config = SslServerConfig.builder(context)
			.backlog(100)
			.build();
		assertEquals(100, config.backlog());
	}
	
	@Test
	void clientBufferSizeWithValidValue() {
		SslServerConfig config = SslServerConfig.builder(context)
			.clientBufferSize(4096)
			.build();
		assertEquals(4096, config.clientBufferSize());
	}
	
	@Test
	void framingTrue() {
		SslServerConfig config = SslServerConfig.builder(context)
			.framing(true)
			.build();
		assertTrue(config.framing());
	}
	
	@Test
	void framingFalse() {
		SslServerConfig config = SslServerConfig.builder(context)
			.framing(false)
			.build();
		assertFalse(config.framing());
	}
	
	@Test
	void framingDefaultsToEnabled() {
		SslServerConfig config = SslServerConfig.builder(context).build();
		assertTrue(config.framing());
	}
	
	@Test
	void framingReturnsSameBuilder() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		assertSame(builder, builder.framing(false));
	}
	
	@Test
	void framingSetMultipleTimes() {
		SslServerConfig config = SslServerConfig.builder(context)
			.framing(false)
			.framing(true)
			.framing(false)
			.build();
		assertFalse(config.framing());
	}
	
	@Test
	void framingSurvivesBuilderReuse() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context)
			.framing(false);
		
		SslServerConfig first = builder.build();
		assertFalse(first.framing());
		
		builder.framing(true);
		SslServerConfig second = builder.build();
		assertTrue(second.framing());
		
		assertFalse(first.framing());
	}
	
	@Test
	void framingCombinedWithOtherOptions() {
		SslServerConfig config = SslServerConfig.builder(context)
			.backlog(64)
			.clientBufferSize(4096)
			.framing(false)
			.tcpNoDelay(true)
			.keepAlive(false)
			.build();
		
		assertEquals(64, config.backlog());
		assertEquals(4096, config.clientBufferSize());
		assertFalse(config.framing());
		assertTrue(config.tcpNoDelay());
		assertFalse(config.keepAlive());
	}
	
	@Test
	void clientReadTimeoutWithValidDuration() {
		SslServerConfig config = SslServerConfig.builder(context)
			.clientReadTimeout(Duration.ofSeconds(30))
			.build();
		assertEquals(Duration.ofSeconds(30), config.clientReadTimeout());
	}
	
	@Test
	void tcpNoDelayFalse() {
		SslServerConfig config = SslServerConfig.builder(context)
			.tcpNoDelay(false)
			.build();
		assertFalse(config.tcpNoDelay());
	}
	
	@Test
	void keepAliveFalse() {
		SslServerConfig config = SslServerConfig.builder(context)
			.keepAlive(false)
			.build();
		assertFalse(config.keepAlive());
	}
	
	@Test
	void enabledProtocolsWithValue() {
		SslServerConfig config = SslServerConfig.builder(context)
			.enabledProtocols(List.of(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_2))
			.build();
		assertEquals(List.of(TlsProtocol.TLS_V1_3, TlsProtocol.TLS_V1_2), config.enabledProtocols());
	}
	
	@Test
	void enabledProtocolsWithNullThrows() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		assertThrows(NullPointerException.class, () -> builder.enabledProtocols(null));
	}
	
	@Test
	void buildWithNullProtocolElementThrows() {
		List<TlsProtocol> protocols = new ArrayList<>();
		protocols.add(null);
		SslServerConfigBuilder builder = assertDoesNotThrow(() -> SslServerConfig.builder(context).enabledProtocols(protocols));
		
		assertThrows(NullPointerException.class, builder::build);
	}
	
	@Test
	void enabledCipherSuitesWithValue() {
		SslServerConfig config = SslServerConfig.builder(context)
			.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384"))
			.build();
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
	}
	
	@Test
	void enabledCipherSuitesWithNullThrows() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		assertThrows(NullPointerException.class, () -> builder.enabledCipherSuites(null));
	}
	
	@Test
	void clientAuthWithValue() {
		SslServerConfig config = SslServerConfig.builder(context)
			.clientAuth(SslClientAuth.REQUESTED)
			.build();
		assertEquals(SslClientAuth.REQUESTED, config.clientAuth());
	}
	
	@Test
	void clientAuthWithNullThrows() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		assertThrows(NullPointerException.class, () -> builder.clientAuth(null));
	}
	
	@Test
	void executorStrategyWithFixedPool() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(10);
		SslServerConfig config = SslServerConfig.builder(context)
			.executorStrategy(strategy)
			.build();
		assertSame(strategy, config.executorStrategy());
	}
	
	@Test
	void onClientConnectWithHandler() {
		SslServerConfig config = SslServerConfig.builder(context)
			.onClientConnect((connection, local, remote, timestamp) -> {})
			.build();
		assertNotNull(config.onClientConnect());
	}
	
	@Test
	void onClientConnectWithNull() {
		SslServerConfig config = SslServerConfig.builder(context)
			.onClientConnect(null)
			.build();
		assertNull(config.onClientConnect());
	}
	
	@Test
	void onClientDisconnectWithHandler() {
		SslServerConfig config = SslServerConfig.builder(context)
			.onClientDisconnect((connection, local, remote, timestamp) -> {})
			.build();
		assertNotNull(config.onClientDisconnect());
	}
	
	@Test
	void onMessageWithHandler() {
		SslServerConfig config = SslServerConfig.builder(context)
			.onMessage((server, conn, data) -> {})
			.build();
		assertNotNull(config.onMessage());
	}
	
	@Test
	void onMessageWithNull() {
		SslServerConfig config = SslServerConfig.builder(context)
			.onMessage(null)
			.build();
		assertNull(config.onMessage());
	}
	
	@Test
	void onErrorWithHandler() {
		SslServerConfig config = SslServerConfig.builder(context)
			.onError((connection, type, msg, cause) -> {})
			.build();
		assertNotNull(config.onError());
	}
	
	@Test
	void methodChainingConsistency() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		assertSame(builder, builder.backlog(100));
		assertSame(builder, builder.clientBufferSize(4096));
		assertSame(builder, builder.clientReadTimeout(Duration.ofSeconds(30)));
		assertSame(builder, builder.framing(false));
		assertSame(builder, builder.tcpNoDelay(true));
		assertSame(builder, builder.keepAlive(true));
		assertSame(builder, builder.enabledProtocols(List.of(TlsProtocol.TLS_V1_3)));
		assertSame(builder, builder.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384")));
		assertSame(builder, builder.clientAuth(SslClientAuth.NONE));
		assertSame(builder, builder.executorStrategy(ClientExecutorStrategy.virtualThreads()));
		assertSame(builder, builder.onClientConnect((connection, local, remote, timestamp) -> {}));
		assertSame(builder, builder.onClientDisconnect((connection, local, remote, timestamp) -> {}));
		assertSame(builder, builder.onMessage((server, conn, data) -> {}));
		assertSame(builder, builder.onError((connection, type, msg, cause) -> {}));
	}
	
	@Test
	void builderSetsAllValues() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(5);
		SslServerConfig config = SslServerConfig.builder(context)
			.backlog(200)
			.clientBufferSize(16384)
			.framing(false)
			.clientReadTimeout(Duration.ofSeconds(60))
			.tcpNoDelay(false)
			.keepAlive(false)
			.enabledProtocols(List.of(TlsProtocol.TLS_V1_3))
			.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384"))
			.clientAuth(SslClientAuth.REQUIRED)
			.executorStrategy(strategy)
			.onClientConnect((connection, local, remote, timestamp) -> {})
			.onClientDisconnect((connection, local, remote, timestamp) -> {})
			.onMessage((server, conn, data) -> {})
			.onError((connection, type, msg, cause) -> {})
			.build();
		
		assertEquals(200, config.backlog());
		assertEquals(16384, config.clientBufferSize());
		assertEquals(Duration.ofSeconds(60), config.clientReadTimeout());
		assertFalse(config.framing());
		assertFalse(config.tcpNoDelay());
		assertFalse(config.keepAlive());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.enabledProtocols());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
		assertEquals(SslClientAuth.REQUIRED, config.clientAuth());
		assertSame(strategy, config.executorStrategy());
		assertNotNull(config.onClientConnect());
		assertNotNull(config.onClientDisconnect());
		assertNotNull(config.onMessage());
		assertNotNull(config.onError());
	}
	
	@Test
	void builderReuseAfterBuild() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context)
			.backlog(100);
		
		SslServerConfig first = builder.build();
		assertEquals(100, first.backlog());
		
		builder.backlog(200);
		SslServerConfig second = builder.build();
		assertEquals(200, second.backlog());
		
		assertEquals(100, first.backlog());
	}
	
	@Test
	void builderMultipleBuilds() {
		SslServerConfigBuilder builder = SslServerConfig.builder(context);
		
		SslServerConfig config1 = builder.build();
		SslServerConfig config2 = builder.build();
		
		assertEquals(config1, config2);
		assertNotSame(config1, config2);
	}
	
	@Test
	void builderOverwriteValues() {
		SslServerConfig config = SslServerConfig.builder(context)
			.backlog(100)
			.backlog(200)
			.build();
		
		assertEquals(200, config.backlog());
	}
}
