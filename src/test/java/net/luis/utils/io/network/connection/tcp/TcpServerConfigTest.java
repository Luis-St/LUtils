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

package net.luis.utils.io.network.connection.tcp;

import net.luis.utils.io.network.connection.event.ConnectionHandler;
import net.luis.utils.io.network.connection.event.MessageEventHandler;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import net.luis.utils.io.network.connection.executor.VirtualThreadStrategy;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TcpServerConfig}.<br>
 *
 * @author Luis-St
 */
class TcpServerConfigTest {
	
	private static final ClientExecutorStrategy STRATEGY = ClientExecutorStrategy.virtualThreads();
	
	private static TcpServerConfig withHandlers(MessageEventHandler<TcpServer, TcpConnection> onMessage, ConnectionHandler<TcpServer, TcpConnection> onConnection) {
		return new TcpServerConfig(50, 8192, true, Duration.ZERO, true, true, STRATEGY, null, null, onMessage, onConnection, null);
	}
	
	@Test
	void defaultConfig() {
		TcpServerConfig config = TcpServerConfig.DEFAULT;
		
		assertEquals(50, config.backlog());
		assertEquals(8192, config.clientBufferSize());
		assertEquals(Duration.ZERO, config.clientReadTimeout());
		assertTrue(config.tcpNoDelay());
		assertTrue(config.keepAlive());
		assertInstanceOf(VirtualThreadStrategy.class, config.executorStrategy());
		assertNull(config.onClientConnect());
		assertNull(config.onClientDisconnect());
		assertNull(config.onMessage());
		assertNull(config.onConnection());
		assertNull(config.onError());
	}
	
	@Test
	void constructWithNullClientReadTimeoutThrows() {
		assertThrows(NullPointerException.class, () -> new TcpServerConfig(50, 8192, true, null, true, true, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
	}
	
	@Test
	void constructWithNullExecutorStrategyThrows() {
		assertThrows(NullPointerException.class, () -> new TcpServerConfig(50, 8192, true, Duration.ZERO, true, true, null, null, null, null, null, null));
	}
	
	@Test
	void constructWithInvalidBacklogThrows() {
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(0, 8192, true, Duration.ZERO, true, true, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(-1, 8192, true, Duration.ZERO, true, true, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
	}
	
	@Test
	void constructWithInvalidClientBufferSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(50, 0, true, Duration.ZERO, true, true, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(50, -1, true, Duration.ZERO, true, true, ClientExecutorStrategy.virtualThreads(), null, null, null, null, null));
	}
	
	@Test
	void builder() {
		TcpServerConfig config = TcpServerConfig.builder()
			.backlog(100)
			.clientBufferSize(16384)
			.clientReadTimeout(Duration.ofSeconds(60))
			.tcpNoDelay(false)
			.keepAlive(false)
			.executorStrategy(ClientExecutorStrategy.fixedPool(8))
			.build();
		
		assertEquals(100, config.backlog());
		assertEquals(16384, config.clientBufferSize());
		assertEquals(Duration.ofSeconds(60), config.clientReadTimeout());
		assertFalse(config.tcpNoDelay());
		assertFalse(config.keepAlive());
	}
	
	@Test
	void builderWithHandlers() {
		TcpServerConfig config = TcpServerConfig.builder()
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
		assertTrue(TcpServerConfig.builder().build().framing());
	}
	
	@Test
	void framingCanBeDisabled() {
		assertFalse(TcpServerConfig.builder().framing(false).build().framing());
		assertTrue(TcpServerConfig.builder().framing(false).framing(true).build().framing());
	}
	
	@Test
	void constructWithConnectionHandler() {
		ConnectionHandler<TcpServer, TcpConnection> handler = (server, connection) -> {};
		
		TcpServerConfig config = withHandlers(null, handler);
		
		assertSame(handler, config.onConnection());
		assertNull(config.onMessage());
		assertEquals(50, config.backlog());
		assertEquals(8192, config.clientBufferSize());
	}
	
	@Test
	void constructWithNullConnectionHandler() {
		TcpServerConfig config = assertDoesNotThrow(() -> withHandlers(null, null));
		
		assertNull(config.onConnection());
	}
	
	@Test
	void constructWithBothMessageAndConnectionHandler() {
		MessageEventHandler<TcpServer, TcpConnection> onMessage = (server, connection, data) -> {};
		ConnectionHandler<TcpServer, TcpConnection> onConnection = (server, connection) -> {};
		
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> withHandlers(onMessage, onConnection));
		assertTrue(exception.getMessage().contains("must not be set at the same time"));
	}
	
	@Test
	void constructWithOnlyMessageHandler() {
		MessageEventHandler<TcpServer, TcpConnection> onMessage = (server, connection, data) -> {};
		
		TcpServerConfig config = assertDoesNotThrow(() -> withHandlers(onMessage, null));
		
		assertSame(onMessage, config.onMessage());
		assertNull(config.onConnection());
	}
	
	@Test
	void constructWithOnlyConnectionHandler() {
		ConnectionHandler<TcpServer, TcpConnection> onConnection = (server, connection) -> {};
		
		TcpServerConfig config = assertDoesNotThrow(() -> withHandlers(null, onConnection));
		
		assertSame(onConnection, config.onConnection());
		assertNull(config.onMessage());
	}
	
	@Test
	void constructWithNeitherHandler() {
		TcpServerConfig config = assertDoesNotThrow(() -> withHandlers(null, null));
		
		assertNull(config.onMessage());
		assertNull(config.onConnection());
	}
	
	@Test
	void constructValidatesSizeBeforeHandlerConflict() {
		MessageEventHandler<TcpServer, TcpConnection> onMessage = (server, connection, data) -> {};
		ConnectionHandler<TcpServer, TcpConnection> onConnection = (server, connection) -> {};
		
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new TcpServerConfig(50, 0, true, Duration.ZERO, true, true, STRATEGY, null, null, onMessage, onConnection, null)
		);
		assertTrue(exception.getMessage().contains("Client buffer size"));
		assertFalse(exception.getMessage().contains("must not be set at the same time"));
	}
	
	@Test
	void connectionHandlerIsReturnedUnchanged() {
		ConnectionHandler<TcpServer, TcpConnection> handler = (server, connection) -> {};
		
		assertSame(handler, withHandlers(null, handler).onConnection());
	}
	
	@Test
	void equalityIncludesConnectionHandler() {
		ConnectionHandler<TcpServer, TcpConnection> first = (server, connection) -> {};
		ConnectionHandler<TcpServer, TcpConnection> second = (server, connection) -> {};
		
		TcpServerConfig config = withHandlers(null, first);
		
		assertNotEquals(config, withHandlers(null, second));
		assertEquals(config, config);
	}
	
	@Test
	void equalityWithSameConnectionHandler() {
		ConnectionHandler<TcpServer, TcpConnection> handler = (server, connection) -> {};
		
		TcpServerConfig first = withHandlers(null, handler);
		TcpServerConfig second = withHandlers(null, handler);
		
		assertEquals(first, second);
		assertEquals(second, first);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toStringContainsConnectionHandler() {
		ConnectionHandler<TcpServer, TcpConnection> handler = (server, connection) -> {};
		
		assertTrue(withHandlers(null, handler).toString().contains("onConnection"));
	}
	
}
