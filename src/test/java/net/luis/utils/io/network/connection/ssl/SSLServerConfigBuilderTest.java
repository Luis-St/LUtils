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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SSLServerConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class SSLServerConfigBuilderTest {
	
	private static SSLContext context;
	
	@BeforeAll
	static void setUp() throws Exception {
		context = SSLContext.getDefault();
	}
	
	@Test
	void builderDefaultValues() {
		SSLServerConfig config = SSLServerConfig.builder(context).build();
		
		assertEquals(50, config.backlog());
		assertEquals(8192, config.clientBufferSize());
		assertEquals(Duration.ZERO, config.clientReadTimeout());
		assertTrue(config.tcpNoDelay());
		assertTrue(config.keepAlive());
		assertSame(context, config.sslContext());
		assertTrue(config.enabledProtocols().isEmpty());
		assertTrue(config.enabledCipherSuites().isEmpty());
		assertEquals(SSLClientAuth.NONE, config.clientAuth());
		assertNotNull(config.executorStrategy());
		assertNull(config.onClientConnect());
		assertNull(config.onClientDisconnect());
		assertNull(config.onMessage());
		assertNull(config.onError());
	}
	
	@Test
	void backlogWithValidValue() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.backlog(100)
			.build();
		assertEquals(100, config.backlog());
	}
	
	@Test
	void clientBufferSizeWithValidValue() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.clientBufferSize(4096)
			.build();
		assertEquals(4096, config.clientBufferSize());
	}
	
	@Test
	void clientReadTimeoutWithValidDuration() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.clientReadTimeout(Duration.ofSeconds(30))
			.build();
		assertEquals(Duration.ofSeconds(30), config.clientReadTimeout());
	}
	
	@Test
	void tcpNoDelayFalse() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.tcpNoDelay(false)
			.build();
		assertFalse(config.tcpNoDelay());
	}
	
	@Test
	void keepAliveFalse() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.keepAlive(false)
			.build();
		assertFalse(config.keepAlive());
	}
	
	@Test
	void enabledProtocolsWithValue() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.enabledProtocols(List.of("TLSv1.3", "TLSv1.2"))
			.build();
		assertEquals(List.of("TLSv1.3", "TLSv1.2"), config.enabledProtocols());
	}
	
	@Test
	void enabledProtocolsWithNullThrows() {
		SSLServerConfigBuilder builder = SSLServerConfig.builder(context);
		assertThrows(NullPointerException.class, () -> builder.enabledProtocols(null));
	}
	
	@Test
	void enabledCipherSuitesWithValue() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384"))
			.build();
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
	}
	
	@Test
	void enabledCipherSuitesWithNullThrows() {
		SSLServerConfigBuilder builder = SSLServerConfig.builder(context);
		assertThrows(NullPointerException.class, () -> builder.enabledCipherSuites(null));
	}
	
	@Test
	void clientAuthWithValue() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.clientAuth(SSLClientAuth.REQUESTED)
			.build();
		assertEquals(SSLClientAuth.REQUESTED, config.clientAuth());
	}
	
	@Test
	void clientAuthWithNullThrows() {
		SSLServerConfigBuilder builder = SSLServerConfig.builder(context);
		assertThrows(NullPointerException.class, () -> builder.clientAuth(null));
	}
	
	@Test
	void executorStrategyWithFixedPool() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(10);
		SSLServerConfig config = SSLServerConfig.builder(context)
			.executorStrategy(strategy)
			.build();
		assertSame(strategy, config.executorStrategy());
	}
	
	@Test
	void onClientConnectWithHandler() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.onClientConnect(event -> {})
			.build();
		assertNotNull(config.onClientConnect());
	}
	
	@Test
	void onClientConnectWithNull() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.onClientConnect(null)
			.build();
		assertNull(config.onClientConnect());
	}
	
	@Test
	void onClientDisconnectWithHandler() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.onClientDisconnect(event -> {})
			.build();
		assertNotNull(config.onClientDisconnect());
	}
	
	@Test
	void onMessageWithHandler() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.onMessage((server, conn, data) -> {})
			.build();
		assertNotNull(config.onMessage());
	}
	
	@Test
	void onMessageWithNull() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.onMessage(null)
			.build();
		assertNull(config.onMessage());
	}
	
	@Test
	void onErrorWithHandler() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.onError((type, msg, cause) -> {})
			.build();
		assertNotNull(config.onError());
	}
	
	@Test
	void methodChainingConsistency() {
		SSLServerConfigBuilder builder = SSLServerConfig.builder(context);
		assertSame(builder, builder.backlog(100));
		assertSame(builder, builder.clientBufferSize(4096));
		assertSame(builder, builder.clientReadTimeout(Duration.ofSeconds(30)));
		assertSame(builder, builder.tcpNoDelay(true));
		assertSame(builder, builder.keepAlive(true));
		assertSame(builder, builder.enabledProtocols(List.of("TLSv1.3")));
		assertSame(builder, builder.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384")));
		assertSame(builder, builder.clientAuth(SSLClientAuth.NONE));
		assertSame(builder, builder.executorStrategy(ClientExecutorStrategy.virtualThreads()));
		assertSame(builder, builder.onClientConnect(event -> {}));
		assertSame(builder, builder.onClientDisconnect(event -> {}));
		assertSame(builder, builder.onMessage((server, conn, data) -> {}));
		assertSame(builder, builder.onError((type, msg, cause) -> {}));
	}
	
	@Test
	void builderSetsAllValues() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(5);
		SSLServerConfig config = SSLServerConfig.builder(context)
			.backlog(200)
			.clientBufferSize(16384)
			.clientReadTimeout(Duration.ofSeconds(60))
			.tcpNoDelay(false)
			.keepAlive(false)
			.enabledProtocols(List.of("TLSv1.3"))
			.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384"))
			.clientAuth(SSLClientAuth.REQUIRED)
			.executorStrategy(strategy)
			.onClientConnect(event -> {})
			.onClientDisconnect(event -> {})
			.onMessage((server, conn, data) -> {})
			.onError((type, msg, cause) -> {})
			.build();
		
		assertEquals(200, config.backlog());
		assertEquals(16384, config.clientBufferSize());
		assertEquals(Duration.ofSeconds(60), config.clientReadTimeout());
		assertFalse(config.tcpNoDelay());
		assertFalse(config.keepAlive());
		assertEquals(List.of("TLSv1.3"), config.enabledProtocols());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
		assertEquals(SSLClientAuth.REQUIRED, config.clientAuth());
		assertSame(strategy, config.executorStrategy());
		assertNotNull(config.onClientConnect());
		assertNotNull(config.onClientDisconnect());
		assertNotNull(config.onMessage());
		assertNotNull(config.onError());
	}
	
	@Test
	void builderReuseAfterBuild() {
		SSLServerConfigBuilder builder = SSLServerConfig.builder(context)
			.backlog(100);
		
		SSLServerConfig first = builder.build();
		assertEquals(100, first.backlog());
		
		builder.backlog(200);
		SSLServerConfig second = builder.build();
		assertEquals(200, second.backlog());
		
		assertEquals(100, first.backlog());
	}
	
	@Test
	void builderMultipleBuilds() {
		SSLServerConfigBuilder builder = SSLServerConfig.builder(context);
		
		SSLServerConfig config1 = builder.build();
		SSLServerConfig config2 = builder.build();
		
		assertEquals(config1, config2);
		assertNotSame(config1, config2);
	}
	
	@Test
	void builderOverwriteValues() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.backlog(100)
			.backlog(200)
			.build();
		
		assertEquals(200, config.backlog());
	}
}
