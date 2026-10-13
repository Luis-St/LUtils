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

import net.luis.utils.io.network.connection.event.ConnectionHandler;
import net.luis.utils.io.network.connection.event.MessageEventHandler;
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
 * Test class for {@link SslServerConfig}.<br>
 *
 * @author Luis-St
 */
class SslServerConfigTest {
	
	private static final ClientExecutorStrategy STRATEGY = ClientExecutorStrategy.virtualThreads();
	
	private static SSLContext context;
	
	@BeforeAll
	static void setUp() throws Exception {
		context = SSLContext.getDefault();
	}
	
	private static SslServerConfig withHandlers(MessageEventHandler<SslServer, SslConnection> onMessage, ConnectionHandler<SslServer, SslConnection> onConnection) {
		return new SslServerConfig(50, 8192, true, Duration.ZERO, true, true, context, List.of(), List.of(), SslClientAuth.NONE, STRATEGY, null, null, onMessage, onConnection, null);
	}
	
	@Test
	void constructWithNullClientReadTimeoutThrows() {
		assertThrows(NullPointerException.class, () -> new SslServerConfig(50, 8192, true, null, true, true, context, List.of(), List.of(), SslClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
	}
	
	@Test
	void constructWithNullSslContextThrows() {
		assertThrows(NullPointerException.class, () -> new SslServerConfig(50, 8192, true, Duration.ZERO, true, true, null, List.of(), List.of(), SslClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
	}
	
	@Test
	void constructWithNullEnabledProtocolsThrows() {
		assertThrows(NullPointerException.class, () -> new SslServerConfig(50, 8192, true, Duration.ZERO, true, true, context, null, List.of(), SslClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
	}
	
	@Test
	void constructWithNullEnabledCipherSuitesThrows() {
		assertThrows(NullPointerException.class, () -> new SslServerConfig(50, 8192, true, Duration.ZERO, true, true, context, List.of(), null, SslClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
	}
	
	@Test
	void constructWithNullClientAuthThrows() {
		assertThrows(NullPointerException.class, () -> new SslServerConfig(50, 8192, true, Duration.ZERO, true, true, context, List.of(), List.of(), null, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
	}
	
	@Test
	void constructWithNullExecutorStrategyThrows() {
		assertThrows(NullPointerException.class, () -> new SslServerConfig(50, 8192, true, Duration.ZERO, true, true, context, List.of(), List.of(), SslClientAuth.NONE, null, null, null, null, null, null));
	}
	
	@Test
	void constructWithInvalidBacklogThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SslServerConfig(0, 8192, true, Duration.ZERO, true, true, context, List.of(), List.of(), SslClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> new SslServerConfig(-1, 8192, true, Duration.ZERO, true, true, context, List.of(), List.of(), SslClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
	}
	
	@Test
	void constructWithInvalidClientBufferSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SslServerConfig(50, 0, true, Duration.ZERO, true, true, context, List.of(), List.of(), SslClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> new SslServerConfig(50, -1, true, Duration.ZERO, true, true, context, List.of(), List.of(), SslClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
	}
	
	@Test
	void constructWithNullProtocolElementThrows() {
		List<TlsProtocol> protocols = new ArrayList<>();
		protocols.add(null);
		
		assertThrows(NullPointerException.class, () -> new SslServerConfig(50, 8192, true, Duration.ZERO, true, true, context, protocols, List.of(), SslClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
	}
	
	@Test
	void constructCopiesProtocolsDefensively() {
		List<TlsProtocol> protocols = new ArrayList<>(List.of(TlsProtocol.TLS_V1_3));
		SslServerConfig config = new SslServerConfig(50, 8192, true, Duration.ZERO, true, true, context, protocols, List.of(), SslClientAuth.NONE, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null);
		
		protocols.add(TlsProtocol.TLS_V1_2);
		assertEquals(1, config.enabledProtocols().size());
		assertThrows(UnsupportedOperationException.class, () -> config.enabledProtocols().add(TlsProtocol.TLS_V1_2));
	}
	
	@Test
	void builderDefaultValues() {
		SslServerConfig config = SslServerConfig.builder(context).build();
		
		assertEquals(50, config.backlog());
		assertEquals(8192, config.clientBufferSize());
		assertEquals(Duration.ZERO, config.clientReadTimeout());
		assertTrue(config.tcpNoDelay());
		assertTrue(config.keepAlive());
		assertSame(context, config.sslContext());
		assertTrue(config.enabledProtocols().isEmpty());
		assertTrue(config.enabledCipherSuites().isEmpty());
		assertEquals(SslClientAuth.NONE, config.clientAuth());
		assertInstanceOf(VirtualThreadStrategy.class, config.executorStrategy());
		assertNull(config.onClientConnect());
		assertNull(config.onClientDisconnect());
		assertNull(config.onMessage());
		assertNull(config.onConnection());
		assertNull(config.onError());
	}
	
	@Test
	void builderWithNullSslContextThrows() {
		assertThrows(NullPointerException.class, () -> SslServerConfig.builder(null));
	}
	
	@Test
	void builder() {
		SslServerConfig config = SslServerConfig.builder(context)
			.backlog(100)
			.clientBufferSize(16384)
			.clientReadTimeout(Duration.ofSeconds(60))
			.tcpNoDelay(false)
			.keepAlive(false)
			.enabledProtocols(List.of(TlsProtocol.TLS_V1_3))
			.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384"))
			.clientAuth(SslClientAuth.REQUIRED)
			.executorStrategy(ClientExecutorStrategy.fixedPool(8))
			.build();
		
		assertEquals(100, config.backlog());
		assertEquals(16384, config.clientBufferSize());
		assertEquals(Duration.ofSeconds(60), config.clientReadTimeout());
		assertFalse(config.tcpNoDelay());
		assertFalse(config.keepAlive());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.enabledProtocols());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
		assertEquals(SslClientAuth.REQUIRED, config.clientAuth());
	}
	
	@Test
	void builderWithHandlers() {
		SslServerConfig config = SslServerConfig.builder(context)
			.onClientConnect((connection, local, remote, timestamp) -> {})
			.onClientDisconnect((connection, local, remote, timestamp) -> {})
			.onMessage((server, conn, data) -> {})
			.onError((connection, type, msg, cause) -> {})
			.build();
		
		assertNotNull(config.onClientConnect());
		assertNotNull(config.onClientDisconnect());
		assertNotNull(config.onMessage());
		assertNotNull(config.onError());
	}
	
	@Test
	void framingIsEnabledByDefault() {
		assertTrue(SslServerConfig.builder(context).build().framing());
	}
	
	@Test
	void framingCanBeDisabled() {
		assertFalse(SslServerConfig.builder(context).framing(false).build().framing());
		assertTrue(SslServerConfig.builder(context).framing(false).framing(true).build().framing());
	}
	
	@Test
	void constructWithConnectionHandler() {
		ConnectionHandler<SslServer, SslConnection> handler = (server, connection) -> {};
		
		SslServerConfig config = withHandlers(null, handler);
		
		assertSame(handler, config.onConnection());
		assertNull(config.onMessage());
		assertEquals(50, config.backlog());
		assertSame(context, config.sslContext());
	}
	
	@Test
	void constructWithNullConnectionHandler() {
		SslServerConfig config = assertDoesNotThrow(() -> withHandlers(null, null));
		
		assertNull(config.onConnection());
	}
	
	@Test
	void constructWithBothMessageAndConnectionHandler() {
		MessageEventHandler<SslServer, SslConnection> onMessage = (server, connection, data) -> {};
		ConnectionHandler<SslServer, SslConnection> onConnection = (server, connection) -> {};
		
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> withHandlers(onMessage, onConnection));
		assertTrue(exception.getMessage().contains("must not be set at the same time"));
	}
	
	@Test
	void constructWithOnlyMessageHandler() {
		MessageEventHandler<SslServer, SslConnection> onMessage = (server, connection, data) -> {};
		
		SslServerConfig config = assertDoesNotThrow(() -> withHandlers(onMessage, null));
		
		assertSame(onMessage, config.onMessage());
		assertNull(config.onConnection());
	}
	
	@Test
	void constructWithOnlyConnectionHandler() {
		ConnectionHandler<SslServer, SslConnection> onConnection = (server, connection) -> {};
		
		SslServerConfig config = assertDoesNotThrow(() -> withHandlers(null, onConnection));
		
		assertSame(onConnection, config.onConnection());
		assertNull(config.onMessage());
	}
	
	@Test
	void constructWithNeitherHandler() {
		SslServerConfig config = assertDoesNotThrow(() -> withHandlers(null, null));
		
		assertNull(config.onMessage());
		assertNull(config.onConnection());
	}
	
	@Test
	void constructValidatesSizeBeforeHandlerConflict() {
		MessageEventHandler<SslServer, SslConnection> onMessage = (server, connection, data) -> {};
		ConnectionHandler<SslServer, SslConnection> onConnection = (server, connection) -> {};
		
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new SslServerConfig(50, 0, true, Duration.ZERO, true, true, context, List.of(), List.of(), SslClientAuth.NONE, STRATEGY, null, null, onMessage, onConnection, null)
		);
		assertTrue(exception.getMessage().contains("Client buffer size"));
		assertFalse(exception.getMessage().contains("must not be set at the same time"));
	}
	
	@Test
	void constructRejectsHandlerConflictBeforeCopyingProtocols() {
		MessageEventHandler<SslServer, SslConnection> onMessage = (server, connection, data) -> {};
		ConnectionHandler<SslServer, SslConnection> onConnection = (server, connection) -> {};
		List<TlsProtocol> protocols = new ArrayList<>();
		protocols.add(null);
		
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new SslServerConfig(50, 8192, true, Duration.ZERO, true, true, context, protocols, List.of(), SslClientAuth.NONE, STRATEGY, null, null, onMessage, onConnection, null)
		);
		assertTrue(exception.getMessage().contains("must not be set at the same time"));
	}
	
	@Test
	void connectionHandlerIsReturnedUnchanged() {
		ConnectionHandler<SslServer, SslConnection> handler = (server, connection) -> {};
		
		assertSame(handler, withHandlers(null, handler).onConnection());
	}
	
	@Test
	void equalityIncludesConnectionHandler() {
		ConnectionHandler<SslServer, SslConnection> first = (server, connection) -> {};
		ConnectionHandler<SslServer, SslConnection> second = (server, connection) -> {};
		
		SslServerConfig config = withHandlers(null, first);
		
		assertNotEquals(config, withHandlers(null, second));
		assertEquals(config, config);
	}
	
	@Test
	void equalityWithSameConnectionHandler() {
		ConnectionHandler<SslServer, SslConnection> handler = (server, connection) -> {};
		
		SslServerConfig first = withHandlers(null, handler);
		SslServerConfig second = withHandlers(null, handler);
		
		assertEquals(first, second);
		assertEquals(second, first);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toStringContainsConnectionHandler() {
		ConnectionHandler<SslServer, SslConnection> handler = (server, connection) -> {};
		
		assertTrue(withHandlers(null, handler).toString().contains("onConnection"));
	}
	
}
