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
 * Test class for {@link UdpPeerConfig}.<br>
 *
 * @author Luis-St
 */
class UdpPeerConfigTest {
	
	@Test
	void constructWithAllValues() {
		UdpPeerConfig config = new UdpPeerConfig(Duration.ofSeconds(2), 1024, true, true);
		
		assertEquals(Duration.ofSeconds(2), config.receiveTimeout());
		assertEquals(1024, config.maxDatagramSize());
		assertTrue(config.broadcast());
		assertTrue(config.reuseAddress());
	}
	
	@Test
	void constructWithNullReceiveTimeout() {
		UdpPeerConfig config = assertDoesNotThrow(() -> new UdpPeerConfig(null, 1024, false, false));
		
		assertNull(config.receiveTimeout());
	}
	
	@Test
	void constructWithInvalidReceiveTimeout() {
		assertThrows(IllegalArgumentException.class, () -> new UdpPeerConfig(Duration.ZERO, 1024, false, false));
		assertThrows(IllegalArgumentException.class, () -> new UdpPeerConfig(Duration.ofSeconds(-1), 1024, false, false));
	}
	
	@Test
	void constructWithInvalidMaxDatagramSize() {
		assertThrows(IllegalArgumentException.class, () -> new UdpPeerConfig(null, 0, false, false));
		assertThrows(IllegalArgumentException.class, () -> new UdpPeerConfig(null, -1, false, false));
	}
	
	@Test
	void constructWithMaxDatagramSizeOne() {
		UdpPeerConfig config = assertDoesNotThrow(() -> new UdpPeerConfig(null, 1, false, false));
		
		assertEquals(1, config.maxDatagramSize());
	}
	
	@Test
	void defaultValues() {
		UdpPeerConfig config = UdpPeerConfig.DEFAULT;
		
		assertNull(config.receiveTimeout());
		assertEquals(65507, config.maxDatagramSize());
		assertFalse(config.broadcast());
		assertFalse(config.reuseAddress());
	}
	
	@Test
	void builderReturnsNewInstanceEachCall() {
		assertNotSame(UdpPeerConfig.builder(), UdpPeerConfig.builder());
	}
	
	@Test
	void builderWithoutChangesBuildsDefault() {
		assertEquals(UdpPeerConfig.DEFAULT, UdpPeerConfig.builder().build());
	}
	
	@Test
	void equalityAndHashCode() {
		UdpPeerConfig first = new UdpPeerConfig(Duration.ofSeconds(1), 512, false, true);
		UdpPeerConfig second = new UdpPeerConfig(Duration.ofSeconds(1), 512, false, true);
		UdpPeerConfig other = new UdpPeerConfig(Duration.ofSeconds(1), 512, true, true);
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, other);
	}
}
