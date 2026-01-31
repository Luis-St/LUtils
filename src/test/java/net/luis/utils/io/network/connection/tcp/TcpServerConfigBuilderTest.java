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

import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TcpServerConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class TcpServerConfigBuilderTest {
	
	@Test
	void builderDefaultValues() {
		TcpServerConfig config = TcpServerConfig.builder().build();
		
		assertEquals(50, config.backlog());
		assertEquals(8192, config.clientBufferSize());
		assertEquals(Duration.ZERO, config.clientReadTimeout());
		assertTrue(config.tcpNoDelay());
		assertTrue(config.keepAlive());
		assertNotNull(config.executorStrategy());
		assertNull(config.onClientConnect());
		assertNull(config.onClientDisconnect());
		assertNull(config.onMessage());
		assertNull(config.onError());
	}
	
	@Test
	void backlogWithValidValue() {
		TcpServerConfig config = TcpServerConfig.builder()
			.backlog(100)
			.build();
		assertEquals(100, config.backlog());
	}
	
	@Test
	void clientBufferSizeWithValidValue() {
		TcpServerConfig config = TcpServerConfig.builder()
			.clientBufferSize(4096)
			.build();
		assertEquals(4096, config.clientBufferSize());
	}
	
	@Test
	void clientReadTimeoutWithValidDuration() {
		TcpServerConfig config = TcpServerConfig.builder()
			.clientReadTimeout(Duration.ofSeconds(30))
			.build();
		assertEquals(Duration.ofSeconds(30), config.clientReadTimeout());
	}
	
	@Test
	void clientReadTimeoutWithZero() {
		TcpServerConfig config = TcpServerConfig.builder()
			.clientReadTimeout(Duration.ZERO)
			.build();
		assertEquals(Duration.ZERO, config.clientReadTimeout());
	}
	
	@Test
	void tcpNoDelayTrue() {
		TcpServerConfig config = TcpServerConfig.builder()
			.tcpNoDelay(true)
			.build();
		assertTrue(config.tcpNoDelay());
	}
	
	@Test
	void tcpNoDelayFalse() {
		TcpServerConfig config = TcpServerConfig.builder()
			.tcpNoDelay(false)
			.build();
		assertFalse(config.tcpNoDelay());
	}
	
	@Test
	void keepAliveTrue() {
		TcpServerConfig config = TcpServerConfig.builder()
			.keepAlive(true)
			.build();
		assertTrue(config.keepAlive());
	}
	
	@Test
	void keepAliveFalse() {
		TcpServerConfig config = TcpServerConfig.builder()
			.keepAlive(false)
			.build();
		assertFalse(config.keepAlive());
	}
	
	@Test
	void executorStrategyWithVirtualThreads() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.virtualThreads();
		TcpServerConfig config = TcpServerConfig.builder()
			.executorStrategy(strategy)
			.build();
		assertSame(strategy, config.executorStrategy());
	}
	
	@Test
	void executorStrategyWithFixedPool() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(10);
		TcpServerConfig config = TcpServerConfig.builder()
			.executorStrategy(strategy)
			.build();
		assertSame(strategy, config.executorStrategy());
	}
	
	@Test
	void onClientConnectWithHandler() {
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientConnect(event -> {})
			.build();
		assertNotNull(config.onClientConnect());
	}
	
	@Test
	void onClientConnectWithNull() {
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientConnect(null)
			.build();
		assertNull(config.onClientConnect());
	}
	
	@Test
	void onClientDisconnectWithHandler() {
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientDisconnect(event -> {})
			.build();
		assertNotNull(config.onClientDisconnect());
	}
	
	@Test
	void onClientDisconnectWithNull() {
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientDisconnect(null)
			.build();
		assertNull(config.onClientDisconnect());
	}
	
	@Test
	void onMessageWithHandler() {
		TcpServerConfig config = TcpServerConfig.builder()
			.onMessage((server, conn, data) -> {})
			.build();
		assertNotNull(config.onMessage());
	}
	
	@Test
	void onMessageWithNull() {
		TcpServerConfig config = TcpServerConfig.builder()
			.onMessage(null)
			.build();
		assertNull(config.onMessage());
	}
	
	@Test
	void onErrorWithHandler() {
		TcpServerConfig config = TcpServerConfig.builder()
			.onError((type, msg, cause) -> {})
			.build();
		assertNotNull(config.onError());
	}
	
	@Test
	void onErrorWithNull() {
		TcpServerConfig config = TcpServerConfig.builder()
			.onError(null)
			.build();
		assertNull(config.onError());
	}
	
	@Test
	void methodChainingConsistency() {
		TcpServerConfigBuilder builder = TcpServerConfig.builder();
		assertSame(builder, builder.backlog(100));
		assertSame(builder, builder.clientBufferSize(4096));
		assertSame(builder, builder.clientReadTimeout(Duration.ofSeconds(30)));
		assertSame(builder, builder.tcpNoDelay(true));
		assertSame(builder, builder.keepAlive(true));
		assertSame(builder, builder.executorStrategy(ClientExecutorStrategy.virtualThreads()));
		assertSame(builder, builder.onClientConnect(event -> {}));
		assertSame(builder, builder.onClientDisconnect(event -> {}));
		assertSame(builder, builder.onMessage((server, conn, data) -> {}));
		assertSame(builder, builder.onError((type, msg, cause) -> {}));
	}
	
	@Test
	void builderSetsAllValues() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(5);
		TcpServerConfig config = TcpServerConfig.builder()
			.backlog(200)
			.clientBufferSize(16384)
			.clientReadTimeout(Duration.ofSeconds(60))
			.tcpNoDelay(false)
			.keepAlive(false)
			.executorStrategy(strategy)
			.onClientConnect(event -> {})
			.onClientDisconnect(event -> {})
			.onMessage((server, conn, data) -> {})
			.onError((type, msg, cause) -> {})
			.build();
		
		assertEquals(200, config.backlog());
		assertEquals(16384, config.clientBufferSize());
		assertEquals(Duration.ofSeconds(60), config.clientReadTimeout());
		assertFalse(config.tcpNoDelay());
		assertFalse(config.keepAlive());
		assertSame(strategy, config.executorStrategy());
		assertNotNull(config.onClientConnect());
		assertNotNull(config.onClientDisconnect());
		assertNotNull(config.onMessage());
		assertNotNull(config.onError());
	}
	
	@Test
	void builderReuseAfterBuild() {
		TcpServerConfigBuilder builder = TcpServerConfig.builder()
			.backlog(100);
		
		TcpServerConfig first = builder.build();
		assertEquals(100, first.backlog());
		
		builder.backlog(200);
		TcpServerConfig second = builder.build();
		assertEquals(200, second.backlog());
		
		assertEquals(100, first.backlog());
	}
	
	@Test
	void builderMultipleBuilds() {
		TcpServerConfigBuilder builder = TcpServerConfig.builder();
		
		TcpServerConfig config1 = builder.build();
		TcpServerConfig config2 = builder.build();
		
		assertEquals(config1, config2);
		assertNotSame(config1, config2);
	}
	
	@Test
	void builderOverwriteValues() {
		TcpServerConfig config = TcpServerConfig.builder()
			.backlog(100)
			.backlog(200)
			.build();
		
		assertEquals(200, config.backlog());
	}
	
	@Test
	void buildMatchesDefaultConstant() {
		TcpServerConfig fromBuilder = TcpServerConfig.builder().build();
		TcpServerConfig defaultConfig = TcpServerConfig.DEFAULT;
		
		assertEquals(defaultConfig.backlog(), fromBuilder.backlog());
		assertEquals(defaultConfig.clientBufferSize(), fromBuilder.clientBufferSize());
		assertEquals(defaultConfig.clientReadTimeout(), fromBuilder.clientReadTimeout());
		assertEquals(defaultConfig.tcpNoDelay(), fromBuilder.tcpNoDelay());
		assertEquals(defaultConfig.keepAlive(), fromBuilder.keepAlive());
		assertEquals(defaultConfig.onClientConnect(), fromBuilder.onClientConnect());
		assertEquals(defaultConfig.onClientDisconnect(), fromBuilder.onClientDisconnect());
		assertEquals(defaultConfig.onMessage(), fromBuilder.onMessage());
		assertEquals(defaultConfig.onError(), fromBuilder.onError());
	}
}
