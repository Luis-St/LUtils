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

import net.luis.utils.io.network.connection.ErrorHandler;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import net.luis.utils.io.network.connection.executor.VirtualThreadStrategy;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UdpServerConfig}.<br>
 *
 * @author Luis-St
 */
class UdpServerConfigTest {
	
	@Test
	void constructWithAllValues() {
		UdpPeerConfig peer = new UdpPeerConfig(Duration.ofSeconds(1), 512, true, true);
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(2);
		ErrorHandler onError = (client, error) -> {};
		
		UdpServerConfig config = new UdpServerConfig(peer, strategy, onError);
		
		assertSame(peer, config.peer());
		assertSame(strategy, config.executorStrategy());
		assertSame(onError, config.onError());
	}
	
	@Test
	void constructWithNullErrorHandler() {
		UdpServerConfig config = assertDoesNotThrow(() -> new UdpServerConfig(UdpPeerConfig.DEFAULT, ClientExecutorStrategy.virtualThreads(), null));
		
		assertNull(config.onError());
	}
	
	@Test
	void constructWithNullPeer() {
		assertThrows(NullPointerException.class, () -> new UdpServerConfig(null, ClientExecutorStrategy.virtualThreads(), null));
	}
	
	@Test
	void constructWithNullExecutorStrategy() {
		assertThrows(NullPointerException.class, () -> new UdpServerConfig(UdpPeerConfig.DEFAULT, null, null));
	}
	
	@Test
	void defaultValues() {
		UdpServerConfig config = UdpServerConfig.DEFAULT;
		
		assertSame(UdpPeerConfig.DEFAULT, config.peer());
		assertInstanceOf(VirtualThreadStrategy.class, config.executorStrategy());
		assertNull(config.onError());
	}
	
	@Test
	void builderReturnsNewInstanceEachCall() {
		assertNotSame(UdpServerConfig.builder(), UdpServerConfig.builder());
	}
	
	@Test
	void builderWithoutChangesBuildsDefault() {
		assertEquals(UdpServerConfig.DEFAULT, UdpServerConfig.builder().build());
	}
	
	@Test
	void equalityAndHashCode() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.virtualThreads();
		UdpServerConfig first = new UdpServerConfig(new UdpPeerConfig(null, 512, false, false), strategy, null);
		UdpServerConfig second = new UdpServerConfig(new UdpPeerConfig(null, 512, false, false), strategy, null);
		UdpServerConfig other = new UdpServerConfig(new UdpPeerConfig(null, 1024, false, false), strategy, null);
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, other);
	}
}
