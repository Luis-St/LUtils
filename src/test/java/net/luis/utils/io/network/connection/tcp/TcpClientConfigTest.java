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

import java.time.Duration;

/**
 * Test class for {@link TcpClientConfig}.<br>
 *
 * @author Luis-St
 */
class TcpClientConfigTest {

	@Test
	void defaultConfig() {
		TcpClientConfig config = TcpClientConfig.DEFAULT;

		assertEquals(Duration.ofSeconds(30), config.connectTimeout());
		assertEquals(Duration.ZERO, config.readTimeout());
		assertEquals(Duration.ZERO, config.writeTimeout());
		assertEquals(8192, config.bufferSize());
		assertTrue(config.tcpNoDelay());
		assertTrue(config.keepAlive());
		assertNull(config.onConnect());
		assertNull(config.onDisconnect());
		assertNull(config.onError());
	}

	@Test
	void builder() {
		TcpClientConfig config = TcpClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(10))
			.readTimeout(Duration.ofSeconds(30))
			.writeTimeout(Duration.ofSeconds(15))
			.bufferSize(16384)
			.tcpNoDelay(false)
			.keepAlive(false)
			.build();

		assertEquals(Duration.ofSeconds(10), config.connectTimeout());
		assertEquals(Duration.ofSeconds(30), config.readTimeout());
		assertEquals(Duration.ofSeconds(15), config.writeTimeout());
		assertEquals(16384, config.bufferSize());
		assertFalse(config.tcpNoDelay());
		assertFalse(config.keepAlive());
	}

	@Test
	void builderWithHandlers() {
		boolean[] connectCalled = { false };
		boolean[] disconnectCalled = { false };
		boolean[] errorCalled = { false };

		TcpClientConfig config = TcpClientConfig.builder()
			.onConnect(event -> connectCalled[0] = true)
			.onDisconnect(event -> disconnectCalled[0] = true)
			.onError((type, msg, cause) -> errorCalled[0] = true)
			.build();

		assertNotNull(config.onConnect());
		assertNotNull(config.onDisconnect());
		assertNotNull(config.onError());
	}

	@Test
	void constructWithNullConnectTimeoutThrows() {
		assertThrows(NullPointerException.class, () -> new TcpClientConfig(
			null, Duration.ZERO, Duration.ZERO, 8192, true, true, null, null, null
		));
	}

	@Test
	void constructWithNullReadTimeoutThrows() {
		assertThrows(NullPointerException.class, () -> new TcpClientConfig(
			Duration.ofSeconds(30), null, Duration.ZERO, 8192, true, true, null, null, null
		));
	}

	@Test
	void constructWithNullWriteTimeoutThrows() {
		assertThrows(NullPointerException.class, () -> new TcpClientConfig(
			Duration.ofSeconds(30), Duration.ZERO, null, 8192, true, true, null, null, null
		));
	}

	@Test
	void constructWithInvalidBufferSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new TcpClientConfig(
			Duration.ofSeconds(30), Duration.ZERO, Duration.ZERO, 0, true, true, null, null, null
		));
		assertThrows(IllegalArgumentException.class, () -> new TcpClientConfig(
			Duration.ofSeconds(30), Duration.ZERO, Duration.ZERO, -1, true, true, null, null, null
		));
	}
}
