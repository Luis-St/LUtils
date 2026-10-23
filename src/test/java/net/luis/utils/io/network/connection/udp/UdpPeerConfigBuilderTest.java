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
 * Test class for {@link UdpPeerConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class UdpPeerConfigBuilderTest {
	
	@Test
	void builderStartsWithDefaults() {
		assertEquals(UdpPeerConfig.DEFAULT, UdpPeerConfig.builder().build());
	}
	
	@Test
	void receiveTimeoutWithNull() {
		assertThrows(NullPointerException.class, () -> UdpPeerConfig.builder().receiveTimeout(null));
	}
	
	@Test
	void buildWithInvalidMaxDatagramSize() {
		UdpPeerConfigBuilder builder = assertDoesNotThrow(() -> UdpPeerConfig.builder().maxDatagramSize(0));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithZeroReceiveTimeout() {
		UdpPeerConfigBuilder builder = assertDoesNotThrow(() -> UdpPeerConfig.builder().receiveTimeout(Duration.ZERO));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void noReceiveTimeoutClearsTimeout() {
		UdpPeerConfig config = UdpPeerConfig.builder().receiveTimeout(Duration.ofSeconds(5)).noReceiveTimeout().build();
		
		assertNull(config.receiveTimeout());
	}
	
	@Test
	void receiveTimeoutSetsValue() {
		UdpPeerConfigBuilder builder = UdpPeerConfig.builder();
		
		assertSame(builder, builder.receiveTimeout(Duration.ofSeconds(3)));
		assertEquals(Duration.ofSeconds(3), builder.build().receiveTimeout());
	}
	
	@Test
	void maxDatagramSizeSetsValue() {
		UdpPeerConfigBuilder builder = UdpPeerConfig.builder();
		
		assertSame(builder, builder.maxDatagramSize(512));
		assertEquals(512, builder.build().maxDatagramSize());
	}
	
	@Test
	void broadcastSetsValue() {
		UdpPeerConfigBuilder builder = UdpPeerConfig.builder();
		
		assertSame(builder, builder.broadcast(true));
		assertTrue(builder.build().broadcast());
	}
	
	@Test
	void reuseAddressSetsValue() {
		UdpPeerConfigBuilder builder = UdpPeerConfig.builder();
		
		assertSame(builder, builder.reuseAddress(true));
		assertTrue(builder.build().reuseAddress());
	}
	
	@Test
	void buildWithAllValues() {
		UdpPeerConfig config = UdpPeerConfig.builder().receiveTimeout(Duration.ofSeconds(1)).maxDatagramSize(100).broadcast(true).reuseAddress(true).build();
		
		assertEquals(new UdpPeerConfig(Duration.ofSeconds(1), 100, true, true), config);
	}
	
	@Test
	void builderReuseAfterBuild() {
		UdpPeerConfigBuilder builder = UdpPeerConfig.builder().maxDatagramSize(100);
		UdpPeerConfig first = builder.build();
		
		builder.maxDatagramSize(200);
		UdpPeerConfig second = builder.build();
		
		assertEquals(100, first.maxDatagramSize());
		assertEquals(200, second.maxDatagramSize());
	}
	
	@Test
	void methodChainingConsistency() {
		UdpPeerConfigBuilder builder = UdpPeerConfig.builder();
		
		assertSame(builder, builder.receiveTimeout(Duration.ofSeconds(1)));
		assertSame(builder, builder.noReceiveTimeout());
		assertSame(builder, builder.maxDatagramSize(10));
		assertSame(builder, builder.broadcast(false));
		assertSame(builder, builder.reuseAddress(false));
	}
}
