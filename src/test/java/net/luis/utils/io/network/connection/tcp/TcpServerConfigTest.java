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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import net.luis.utils.io.network.connection.executor.VirtualThreadStrategy;

import java.time.Duration;

/**
 * Test class for {@link TcpServerConfig}.<br>
 *
 * @author Luis-St
 */
class TcpServerConfigTest {

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
		assertNull(config.onError());
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
		boolean[] connectCalled = { false };
		boolean[] disconnectCalled = { false };
		boolean[] messageCalled = { false };
		boolean[] errorCalled = { false };

		TcpServerConfig config = TcpServerConfig.builder()
			.onClientConnect(event -> connectCalled[0] = true)
			.onClientDisconnect(event -> disconnectCalled[0] = true)
			.onMessage((server, conn, data) -> messageCalled[0] = true)
			.onError((type, msg, cause) -> errorCalled[0] = true)
			.build();

		assertNotNull(config.onClientConnect());
		assertNotNull(config.onClientDisconnect());
		assertNotNull(config.onMessage());
		assertNotNull(config.onError());
	}

	@Test
	void constructWithNullClientReadTimeoutThrows() {
		assertThrows(NullPointerException.class, () -> new TcpServerConfig(
			50, 8192, null, true, true, ClientExecutorStrategy.virtualThreads(), null, null, null, null
		));
	}

	@Test
	void constructWithNullExecutorStrategyThrows() {
		assertThrows(NullPointerException.class, () -> new TcpServerConfig(
			50, 8192, Duration.ZERO, true, true, null, null, null, null, null
		));
	}

	@Test
	void constructWithInvalidBacklogThrows() {
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(
			0, 8192, Duration.ZERO, true, true, ClientExecutorStrategy.virtualThreads(), null, null, null, null
		));
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(
			-1, 8192, Duration.ZERO, true, true, ClientExecutorStrategy.virtualThreads(), null, null, null, null
		));
	}

	@Test
	void constructWithInvalidClientBufferSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(
			50, 0, Duration.ZERO, true, true, ClientExecutorStrategy.virtualThreads(), null, null, null, null
		));
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(
			50, -1, Duration.ZERO, true, true, ClientExecutorStrategy.virtualThreads(), null, null, null, null
		));
	}
}
