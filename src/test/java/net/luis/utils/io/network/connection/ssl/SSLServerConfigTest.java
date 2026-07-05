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
import net.luis.utils.io.network.connection.executor.VirtualThreadStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SSLServerConfig}.<br>
 *
 * @author Luis-St
 */
class SSLServerConfigTest {
	
	private static SSLContext context;
	
	@BeforeAll
	static void setUp() throws Exception {
		context = SSLContext.getDefault();
	}
	
	@Test
	void constructWithNullClientReadTimeoutThrows() {
		assertThrows(NullPointerException.class, () -> new SSLServerConfig(50, 8192, null, true, true, context, List.of(), List.of(), SSLClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null));
	}
	
	@Test
	void constructWithNullSslContextThrows() {
		assertThrows(NullPointerException.class, () -> new SSLServerConfig(50, 8192, Duration.ZERO, true, true, null, List.of(), List.of(), SSLClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null));
	}
	
	@Test
	void constructWithNullEnabledProtocolsThrows() {
		assertThrows(NullPointerException.class, () -> new SSLServerConfig(50, 8192, Duration.ZERO, true, true, context, null, List.of(), SSLClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null));
	}
	
	@Test
	void constructWithNullEnabledCipherSuitesThrows() {
		assertThrows(NullPointerException.class, () -> new SSLServerConfig(50, 8192, Duration.ZERO, true, true, context, List.of(), null, SSLClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null));
	}
	
	@Test
	void constructWithNullClientAuthThrows() {
		assertThrows(NullPointerException.class, () -> new SSLServerConfig(50, 8192, Duration.ZERO, true, true, context, List.of(), List.of(), null, ClientExecutorStrategy.virtualThreads(), null, null, null, null));
	}
	
	@Test
	void constructWithNullExecutorStrategyThrows() {
		assertThrows(NullPointerException.class, () -> new SSLServerConfig(50, 8192, Duration.ZERO, true, true, context, List.of(), List.of(), SSLClientAuth.NONE, null, null, null, null, null));
	}
	
	@Test
	void constructWithInvalidBacklogThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SSLServerConfig(0, 8192, Duration.ZERO, true, true, context, List.of(), List.of(), SSLClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> new SSLServerConfig(-1, 8192, Duration.ZERO, true, true, context, List.of(), List.of(), SSLClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null));
	}
	
	@Test
	void constructWithInvalidClientBufferSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SSLServerConfig(50, 0, Duration.ZERO, true, true, context, List.of(), List.of(), SSLClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> new SSLServerConfig(50, -1, Duration.ZERO, true, true, context, List.of(), List.of(), SSLClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null));
	}
	
	@Test
	void constructCopiesProtocolsDefensively() {
		List<String> protocols = new ArrayList<>(List.of("TLSv1.3"));
		SSLServerConfig config = new SSLServerConfig(50, 8192, Duration.ZERO, true, true, context, protocols, List.of(), SSLClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null);
		
		protocols.add("TLSv1.2");
		assertEquals(1, config.enabledProtocols().size());
		assertThrows(UnsupportedOperationException.class, () -> config.enabledProtocols().add("x"));
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
		assertInstanceOf(VirtualThreadStrategy.class, config.executorStrategy());
		assertNull(config.onClientConnect());
		assertNull(config.onClientDisconnect());
		assertNull(config.onMessage());
		assertNull(config.onError());
	}
	
	@Test
	void builderWithNullSslContextThrows() {
		assertThrows(NullPointerException.class, () -> SSLServerConfig.builder(null));
	}
	
	@Test
	void builder() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.backlog(100)
			.clientBufferSize(16384)
			.clientReadTimeout(Duration.ofSeconds(60))
			.tcpNoDelay(false)
			.keepAlive(false)
			.enabledProtocols(List.of("TLSv1.3"))
			.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384"))
			.clientAuth(SSLClientAuth.REQUIRED)
			.executorStrategy(ClientExecutorStrategy.fixedPool(8))
			.build();
		
		assertEquals(100, config.backlog());
		assertEquals(16384, config.clientBufferSize());
		assertEquals(Duration.ofSeconds(60), config.clientReadTimeout());
		assertFalse(config.tcpNoDelay());
		assertFalse(config.keepAlive());
		assertEquals(List.of("TLSv1.3"), config.enabledProtocols());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
		assertEquals(SSLClientAuth.REQUIRED, config.clientAuth());
	}
	
	@Test
	void builderWithHandlers() {
		SSLServerConfig config = SSLServerConfig.builder(context)
			.onClientConnect(event -> {})
			.onClientDisconnect(event -> {})
			.onMessage((server, conn, data) -> {})
			.onError((type, msg, cause) -> {})
			.build();
		
		assertNotNull(config.onClientConnect());
		assertNotNull(config.onClientDisconnect());
		assertNotNull(config.onMessage());
		assertNotNull(config.onError());
	}
}
