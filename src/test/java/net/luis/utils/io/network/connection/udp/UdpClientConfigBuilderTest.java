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
 * Test class for {@link UdpClientConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class UdpClientConfigBuilderTest {
	
	@Test
	void builderDefaultValues() {
		UdpClientConfig config = UdpClientConfig.builder().build();
		
		assertEquals(Duration.ZERO, config.receiveTimeout());
		assertEquals(65535, config.bufferSize());
		assertFalse(config.broadcast());
		assertFalse(config.reuseAddress());
		assertNull(config.onError());
	}
	
	@Test
	void receiveTimeoutWithValidDuration() {
		UdpClientConfig config = UdpClientConfig.builder()
			.receiveTimeout(Duration.ofSeconds(5))
			.build();
		assertEquals(Duration.ofSeconds(5), config.receiveTimeout());
	}
	
	@Test
	void receiveTimeoutWithNullThrows() {
		UdpClientConfigBuilder builder = UdpClientConfig.builder();
		assertThrows(NullPointerException.class, () -> builder.receiveTimeout(null));
	}
	
	@Test
	void receiveTimeoutWithZero() {
		UdpClientConfig config = UdpClientConfig.builder()
			.receiveTimeout(Duration.ZERO)
			.build();
		assertEquals(Duration.ZERO, config.receiveTimeout());
	}
	
	@Test
	void bufferSizeWithValidValue() {
		UdpClientConfig config = UdpClientConfig.builder()
			.bufferSize(1024)
			.build();
		assertEquals(1024, config.bufferSize());
	}
	
	@Test
	void broadcastTrue() {
		UdpClientConfig config = UdpClientConfig.builder()
			.broadcast(true)
			.build();
		assertTrue(config.broadcast());
	}
	
	@Test
	void broadcastFalse() {
		UdpClientConfig config = UdpClientConfig.builder()
			.broadcast(false)
			.build();
		assertFalse(config.broadcast());
	}
	
	@Test
	void reuseAddressTrue() {
		UdpClientConfig config = UdpClientConfig.builder()
			.reuseAddress(true)
			.build();
		assertTrue(config.reuseAddress());
	}
	
	@Test
	void reuseAddressFalse() {
		UdpClientConfig config = UdpClientConfig.builder()
			.reuseAddress(false)
			.build();
		assertFalse(config.reuseAddress());
	}
	
	@Test
	void onErrorWithHandler() {
		UdpClientConfig config = UdpClientConfig.builder()
			.onError((type, msg, cause) -> {})
			.build();
		assertNotNull(config.onError());
	}
	
	@Test
	void onErrorWithNull() {
		UdpClientConfig config = UdpClientConfig.builder()
			.onError(null)
			.build();
		assertNull(config.onError());
	}
	
	@Test
	void methodChainingConsistency() {
		UdpClientConfigBuilder builder = UdpClientConfig.builder();
		assertSame(builder, builder.receiveTimeout(Duration.ofSeconds(5)));
		assertSame(builder, builder.bufferSize(1024));
		assertSame(builder, builder.broadcast(true));
		assertSame(builder, builder.reuseAddress(true));
		assertSame(builder, builder.onError((type, msg, cause) -> {}));
	}
	
	@Test
	void builderSetsAllValues() {
		UdpClientConfig config = UdpClientConfig.builder()
			.receiveTimeout(Duration.ofSeconds(10))
			.bufferSize(2048)
			.broadcast(true)
			.reuseAddress(true)
			.onError((type, msg, cause) -> {})
			.build();
		
		assertEquals(Duration.ofSeconds(10), config.receiveTimeout());
		assertEquals(2048, config.bufferSize());
		assertTrue(config.broadcast());
		assertTrue(config.reuseAddress());
		assertNotNull(config.onError());
	}
	
	@Test
	void builderReuseAfterBuild() {
		UdpClientConfigBuilder builder = UdpClientConfig.builder()
			.bufferSize(1024);
		
		UdpClientConfig first = builder.build();
		assertEquals(1024, first.bufferSize());
		
		builder.bufferSize(2048);
		UdpClientConfig second = builder.build();
		assertEquals(2048, second.bufferSize());
		
		assertEquals(1024, first.bufferSize());
	}
	
	@Test
	void builderMultipleBuilds() {
		UdpClientConfigBuilder builder = UdpClientConfig.builder();
		
		UdpClientConfig config1 = builder.build();
		UdpClientConfig config2 = builder.build();
		
		assertEquals(config1, config2);
		assertNotSame(config1, config2);
	}
	
	@Test
	void builderOverwriteValues() {
		UdpClientConfig config = UdpClientConfig.builder()
			.bufferSize(1024)
			.bufferSize(2048)
			.build();
		
		assertEquals(2048, config.bufferSize());
	}
	
	@Test
	void buildMatchesDefaultConstant() {
		UdpClientConfig fromBuilder = UdpClientConfig.builder().build();
		UdpClientConfig defaultConfig = UdpClientConfig.DEFAULT;
		
		assertEquals(defaultConfig.receiveTimeout(), fromBuilder.receiveTimeout());
		assertEquals(defaultConfig.bufferSize(), fromBuilder.bufferSize());
		assertEquals(defaultConfig.broadcast(), fromBuilder.broadcast());
		assertEquals(defaultConfig.reuseAddress(), fromBuilder.reuseAddress());
		assertEquals(defaultConfig.onError(), fromBuilder.onError());
	}
}
