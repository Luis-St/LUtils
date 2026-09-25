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

package net.luis.utils.io.network.icmp;

import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IcmpSocketConfig}.<br>
 *
 * @author Luis-St
 */
class IcmpSocketConfigTest {
	
	private static final ErrorEventHandler HANDLER = (connection, type, message, cause) -> {};
	
	@Test
	void constructWithValidValues() {
		IcmpSocketConfig config = new IcmpSocketConfig(IcmpSocketType.RAW, Duration.ofSeconds(1), 1500, 64, HANDLER);
		assertEquals(IcmpSocketType.RAW, config.socketType());
		assertEquals(Duration.ofSeconds(1), config.receiveTimeout());
		assertEquals(1500, config.bufferSize());
		assertEquals(64, config.timeToLive());
		assertSame(HANDLER, config.onError());
	}
	
	@Test
	void constructWithNullErrorHandler() {
		IcmpSocketConfig config = assertDoesNotThrow(() -> new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ZERO, 1500, 0, null));
		assertNull(config.onError());
	}
	
	@Test
	void constructWithNullSocketType() {
		assertThrows(NullPointerException.class, () -> new IcmpSocketConfig(null, Duration.ZERO, 1500, 0, null));
	}
	
	@Test
	void constructWithNullReceiveTimeout() {
		assertThrows(NullPointerException.class, () -> new IcmpSocketConfig(IcmpSocketType.DATAGRAM, null, 1500, 0, null));
	}
	
	@Test
	void constructWithNegativeReceiveTimeout() {
		assertThrows(IllegalArgumentException.class, () -> new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ofMillis(-1), 1500, 0, null));
	}
	
	@Test
	void constructWithBufferSizeBelowHeaderSize() {
		assertThrows(IllegalArgumentException.class, () -> new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ZERO, IcmpPacket.HEADER_SIZE - 1, 0, null));
	}
	
	@Test
	void constructWithNegativeTimeToLive() {
		assertThrows(IllegalArgumentException.class, () -> new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ZERO, 1500, -1, null));
	}
	
	@Test
	void constructWithTimeToLiveAboveMax() {
		assertThrows(IllegalArgumentException.class, () -> new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ZERO, 1500, 256, null));
	}
	
	@Test
	void constructWithZeroReceiveTimeout() {
		IcmpSocketConfig config = assertDoesNotThrow(() -> new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ZERO, 1500, 0, null));
		assertTrue(config.receiveTimeout().isZero());
	}
	
	@Test
	void constructWithBufferSizeAtHeaderSize() {
		assertEquals(8, new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ZERO, 8, 0, null).bufferSize());
	}
	
	@Test
	void constructWithMinTimeToLiveBoundary() {
		assertEquals(0, new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ZERO, 1500, 0, null).timeToLive());
	}
	
	@Test
	void constructWithMaxTimeToLiveBoundary() {
		assertEquals(255, new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ZERO, 1500, 255, null).timeToLive());
	}
	
	@Test
	void defaultConfigValues() {
		IcmpSocketConfig config = IcmpSocketConfig.DEFAULT;
		assertEquals(IcmpSocketType.DATAGRAM, config.socketType());
		assertEquals(Duration.ZERO, config.receiveTimeout());
		assertEquals(65535, config.bufferSize());
		assertEquals(0, config.timeToLive());
		assertNull(config.onError());
	}
	
	@Test
	void builderReturnsNewInstanceEachCall() {
		IcmpSocketConfigBuilder first = IcmpSocketConfig.builder();
		assertNotNull(first);
		assertNotSame(first, IcmpSocketConfig.builder());
	}
	
	@Test
	void builderWithoutChangesBuildsDefault() {
		assertEquals(IcmpSocketConfig.DEFAULT, IcmpSocketConfig.builder().build());
	}
	
	@Test
	void constructWithLargeValues() {
		IcmpSocketConfig config = assertDoesNotThrow(() -> new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ofDays(1), Integer.MAX_VALUE, 255, null));
		assertEquals(IcmpSocketType.DATAGRAM, config.socketType());
		assertEquals(Duration.ofDays(1), config.receiveTimeout());
		assertEquals(Integer.MAX_VALUE, config.bufferSize());
		assertEquals(255, config.timeToLive());
		assertNull(config.onError());
	}
	
	@Test
	void validationOrderReportsReceiveTimeoutFirst() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ofMillis(-1), 0, -1, null));
		assertTrue(e.getMessage().startsWith("Receive timeout"));
	}
	
	@Test
	void equalConfigsWithSameValues() {
		IcmpSocketConfig first = new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ofSeconds(1), 1500, 64, HANDLER);
		IcmpSocketConfig second = new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ofSeconds(1), 1500, 64, HANDLER);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new IcmpSocketConfig(IcmpSocketType.DATAGRAM, Duration.ofSeconds(1), 1500, 32, HANDLER));
	}
}
