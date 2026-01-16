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

package net.luis.utils.io.network.connection.udp;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UdpClientConfig}.<br>
 *
 * @author Luis-St
 */
class UdpClientConfigTest {
	
	@Test
	void defaultConfig() {
		UdpClientConfig config = UdpClientConfig.DEFAULT;
		
		assertEquals(Duration.ZERO, config.receiveTimeout());
		assertEquals(65535, config.bufferSize());
		assertFalse(config.broadcast());
		assertFalse(config.reuseAddress());
		assertNull(config.onError());
	}
	
	@Test
	void constructWithNullReceiveTimeoutThrows() {
		assertThrows(NullPointerException.class, () -> new UdpClientConfig(null, 65535, false, false, null));
	}
	
	@Test
	void constructWithInvalidBufferSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new UdpClientConfig(Duration.ZERO, 0, false, false, null));
		assertThrows(IllegalArgumentException.class, () -> new UdpClientConfig(Duration.ZERO, -1, false, false, null));
	}
	
	@Test
	void builder() {
		UdpClientConfig config = UdpClientConfig.builder()
			.receiveTimeout(Duration.ofSeconds(5))
			.bufferSize(1024)
			.broadcast(true)
			.reuseAddress(true)
			.build();
		
		assertEquals(Duration.ofSeconds(5), config.receiveTimeout());
		assertEquals(1024, config.bufferSize());
		assertTrue(config.broadcast());
		assertTrue(config.reuseAddress());
	}
	
	@Test
	void builderWithErrorHandler() {
		UdpClientConfig config = UdpClientConfig.builder()
			.onError((type, msg, cause) -> {})
			.build();
		
		assertNotNull(config.onError());
	}
}
