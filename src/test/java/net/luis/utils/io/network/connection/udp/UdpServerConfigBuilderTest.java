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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UdpServerConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class UdpServerConfigBuilderTest {
	
	@Test
	void builderDefaultValues() {
		UdpServerConfig config = UdpServerConfig.builder().build();
		
		assertEquals(65535, config.bufferSize());
		assertFalse(config.broadcast());
		assertFalse(config.reuseAddress());
		assertNotNull(config.executorStrategy());
		assertNull(config.onMessage());
		assertNull(config.onError());
	}
	
	@Test
	void bufferSizeWithValidValue() {
		UdpServerConfig config = UdpServerConfig.builder()
			.bufferSize(1024)
			.build();
		assertEquals(1024, config.bufferSize());
	}
	
	@Test
	void broadcastTrue() {
		UdpServerConfig config = UdpServerConfig.builder()
			.broadcast(true)
			.build();
		assertTrue(config.broadcast());
	}
	
	@Test
	void broadcastFalse() {
		UdpServerConfig config = UdpServerConfig.builder()
			.broadcast(false)
			.build();
		assertFalse(config.broadcast());
	}
	
	@Test
	void reuseAddressTrue() {
		UdpServerConfig config = UdpServerConfig.builder()
			.reuseAddress(true)
			.build();
		assertTrue(config.reuseAddress());
	}
	
	@Test
	void reuseAddressFalse() {
		UdpServerConfig config = UdpServerConfig.builder()
			.reuseAddress(false)
			.build();
		assertFalse(config.reuseAddress());
	}
	
	@Test
	void executorStrategyWithVirtualThreads() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.virtualThreads();
		UdpServerConfig config = UdpServerConfig.builder()
			.executorStrategy(strategy)
			.build();
		assertSame(strategy, config.executorStrategy());
	}
	
	@Test
	void executorStrategyWithFixedPool() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(10);
		UdpServerConfig config = UdpServerConfig.builder()
			.executorStrategy(strategy)
			.build();
		assertSame(strategy, config.executorStrategy());
	}
	
	@Test
	void executorStrategyWithNullThrows() {
		UdpServerConfigBuilder builder = UdpServerConfig.builder();
		assertThrows(NullPointerException.class, () -> builder.executorStrategy(null));
	}
	
	@Test
	void onMessageWithHandler() {
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {})
			.build();
		assertNotNull(config.onMessage());
	}
	
	@Test
	void onMessageWithNull() {
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage(null)
			.build();
		assertNull(config.onMessage());
	}
	
	@Test
	void onErrorWithHandler() {
		UdpServerConfig config = UdpServerConfig.builder()
			.onError((type, msg, cause) -> {})
			.build();
		assertNotNull(config.onError());
	}
	
	@Test
	void onErrorWithNull() {
		UdpServerConfig config = UdpServerConfig.builder()
			.onError(null)
			.build();
		assertNull(config.onError());
	}
	
	@Test
	void methodChainingConsistency() {
		UdpServerConfigBuilder builder = UdpServerConfig.builder();
		assertSame(builder, builder.bufferSize(1024));
		assertSame(builder, builder.broadcast(true));
		assertSame(builder, builder.reuseAddress(true));
		assertSame(builder, builder.executorStrategy(ClientExecutorStrategy.virtualThreads()));
		assertSame(builder, builder.onMessage((server, datagram, data) -> {}));
		assertSame(builder, builder.onError((type, msg, cause) -> {}));
	}
	
	@Test
	void builderSetsAllValues() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(5);
		UdpServerConfig config = UdpServerConfig.builder()
			.bufferSize(2048)
			.broadcast(true)
			.reuseAddress(true)
			.executorStrategy(strategy)
			.onMessage((server, datagram, data) -> {})
			.onError((type, msg, cause) -> {})
			.build();
		
		assertEquals(2048, config.bufferSize());
		assertTrue(config.broadcast());
		assertTrue(config.reuseAddress());
		assertSame(strategy, config.executorStrategy());
		assertNotNull(config.onMessage());
		assertNotNull(config.onError());
	}
	
	@Test
	void builderReuseAfterBuild() {
		UdpServerConfigBuilder builder = UdpServerConfig.builder()
			.bufferSize(1024);
		
		UdpServerConfig first = builder.build();
		assertEquals(1024, first.bufferSize());
		
		builder.bufferSize(2048);
		UdpServerConfig second = builder.build();
		assertEquals(2048, second.bufferSize());
		
		assertEquals(1024, first.bufferSize());
	}
	
	@Test
	void builderMultipleBuilds() {
		UdpServerConfigBuilder builder = UdpServerConfig.builder();
		
		UdpServerConfig config1 = builder.build();
		UdpServerConfig config2 = builder.build();
		
		assertEquals(config1, config2);
		assertNotSame(config1, config2);
	}
	
	@Test
	void builderOverwriteValues() {
		UdpServerConfig config = UdpServerConfig.builder()
			.bufferSize(1024)
			.bufferSize(2048)
			.build();
		
		assertEquals(2048, config.bufferSize());
	}
	
	@Test
	void buildMatchesDefaultConstant() {
		UdpServerConfig fromBuilder = UdpServerConfig.builder().build();
		UdpServerConfig defaultConfig = UdpServerConfig.DEFAULT;
		
		assertEquals(defaultConfig.bufferSize(), fromBuilder.bufferSize());
		assertEquals(defaultConfig.broadcast(), fromBuilder.broadcast());
		assertEquals(defaultConfig.reuseAddress(), fromBuilder.reuseAddress());
		assertEquals(defaultConfig.onMessage(), fromBuilder.onMessage());
		assertEquals(defaultConfig.onError(), fromBuilder.onError());
	}
}
