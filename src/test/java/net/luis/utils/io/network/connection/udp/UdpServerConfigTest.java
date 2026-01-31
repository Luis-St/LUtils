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

import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import net.luis.utils.io.network.connection.executor.VirtualThreadStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UdpServerConfig}.<br>
 *
 * @author Luis-St
 */
class UdpServerConfigTest {
	
	@Test
	void defaultConfig() {
		UdpServerConfig config = UdpServerConfig.DEFAULT;
		
		assertEquals(65535, config.bufferSize());
		assertFalse(config.broadcast());
		assertFalse(config.reuseAddress());
		assertInstanceOf(VirtualThreadStrategy.class, config.executorStrategy());
		assertNull(config.onMessage());
		assertNull(config.onError());
	}
	
	@Test
	void constructWithNullExecutorStrategyThrows() {
		assertThrows(NullPointerException.class, () -> new UdpServerConfig(65535, false, false, null, null, null));
	}
	
	@Test
	void constructWithInvalidBufferSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new UdpServerConfig(0, false, false, ClientExecutorStrategy.virtualThreads(), null, null));
		assertThrows(IllegalArgumentException.class, () -> new UdpServerConfig(-1, false, false, ClientExecutorStrategy.virtualThreads(), null, null));
	}
	
	@Test
	void builder() {
		UdpServerConfig config = UdpServerConfig.builder()
			.bufferSize(1024)
			.broadcast(true)
			.reuseAddress(true)
			.executorStrategy(ClientExecutorStrategy.fixedPool(4))
			.build();
		
		assertEquals(1024, config.bufferSize());
		assertTrue(config.broadcast());
		assertTrue(config.reuseAddress());
	}
	
	@Test
	void builderWithHandlers() {
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {})
			.onError((type, msg, cause) -> {})
			.build();
		
		assertNotNull(config.onMessage());
		assertNotNull(config.onError());
	}
}
