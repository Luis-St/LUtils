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

import net.luis.utils.io.network.connection.ErrorHandler;
import net.luis.utils.io.network.connection.Framing;
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
	
	@Test
	void constructWithAllValues() {
		ErrorHandler onError = (client, error) -> {};
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(2);
		TcpServerConfig config = new TcpServerConfig(10, 1024, Framing.lines(), Duration.ofSeconds(3), false, false, strategy, onError);
		
		assertEquals(10, config.backlog());
		assertEquals(1024, config.maxMessageSize());
		assertEquals(Framing.lines(), config.framing());
		assertEquals(Duration.ofSeconds(3), config.clientReadTimeout());
		assertFalse(config.tcpNoDelay());
		assertFalse(config.keepAlive());
		assertSame(strategy, config.executorStrategy());
		assertSame(onError, config.onError());
	}
	
	@Test
	void constructWithNullOptionalValues() {
		TcpServerConfig config = assertDoesNotThrow(() -> new TcpServerConfig(10, 1024, Framing.lines(), null, true, true, STRATEGY, null));
		
		assertNull(config.clientReadTimeout());
		assertNull(config.onError());
	}
	
	@Test
	void constructWithNullFraming() {
		assertThrows(NullPointerException.class, () -> new TcpServerConfig(10, 1024, null, null, true, true, STRATEGY, null));
	}
	
	@Test
	void constructWithNullExecutorStrategy() {
		assertThrows(NullPointerException.class, () -> new TcpServerConfig(10, 1024, Framing.lines(), null, true, true, null, null));
	}
	
	@Test
	void constructWithInvalidClientReadTimeout() {
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(10, 1024, Framing.lines(), Duration.ZERO, true, true, STRATEGY, null));
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(10, 1024, Framing.lines(), Duration.ofSeconds(-1), true, true, STRATEGY, null));
	}
	
	@Test
	void constructWithInvalidBacklog() {
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(0, 1024, Framing.lines(), null, true, true, STRATEGY, null));
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(-1, 1024, Framing.lines(), null, true, true, STRATEGY, null));
	}
	
	@Test
	void constructWithInvalidMaxMessageSize() {
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(10, 0, Framing.lines(), null, true, true, STRATEGY, null));
		assertThrows(IllegalArgumentException.class, () -> new TcpServerConfig(10, -1, Framing.lines(), null, true, true, STRATEGY, null));
	}
	
	@Test
	void constructWithBoundaryValues() {
		TcpServerConfig config = assertDoesNotThrow(() -> new TcpServerConfig(1, 1, Framing.lines(), null, true, true, STRATEGY, null));
		
		assertEquals(1, config.backlog());
		assertEquals(1, config.maxMessageSize());
	}
	
	@Test
	void defaultValues() {
		TcpServerConfig config = TcpServerConfig.DEFAULT;
		
		assertEquals(50, config.backlog());
		assertEquals(8192, config.maxMessageSize());
		assertEquals(Framing.lengthPrefixed(), config.framing());
		assertNull(config.clientReadTimeout());
		assertTrue(config.tcpNoDelay());
		assertTrue(config.keepAlive());
		assertInstanceOf(VirtualThreadStrategy.class, config.executorStrategy());
		assertNull(config.onError());
	}
	
	@Test
	void builderReturnsNewInstanceEachCall() {
		assertNotSame(TcpServerConfig.builder(), TcpServerConfig.builder());
	}
	
	@Test
	void builderWithoutChangesBuildsDefault() {
		assertEquals(TcpServerConfig.DEFAULT, TcpServerConfig.builder().build());
	}
	
	@Test
	void equalityAndHashCode() {
		ErrorHandler onError = (client, error) -> {};
		ErrorHandler otherOnError = (client, error) -> {};
		TcpServerConfig first = new TcpServerConfig(10, 1024, Framing.lines(), null, true, true, STRATEGY, onError);
		TcpServerConfig second = new TcpServerConfig(10, 1024, Framing.lines(), null, true, true, STRATEGY, onError);
		TcpServerConfig other = new TcpServerConfig(10, 1024, Framing.lines(), null, true, true, STRATEGY, otherOnError);
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, other);
	}
}
