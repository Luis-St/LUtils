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
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UdpServerConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class UdpServerConfigBuilderTest {
	
	@Test
	void builderStartsWithDefaults() {
		assertEquals(UdpServerConfig.DEFAULT, UdpServerConfig.builder().build());
	}
	
	@Test
	void receiveTimeoutWithNull() {
		assertThrows(NullPointerException.class, () -> UdpServerConfig.builder().receiveTimeout(null));
	}
	
	@Test
	void executorStrategyWithNull() {
		assertThrows(NullPointerException.class, () -> UdpServerConfig.builder().executorStrategy(null));
	}
	
	@Test
	void buildWithInvalidMaxDatagramSize() {
		UdpServerConfigBuilder builder = assertDoesNotThrow(() -> UdpServerConfig.builder().maxDatagramSize(0));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithZeroReceiveTimeout() {
		UdpServerConfigBuilder builder = assertDoesNotThrow(() -> UdpServerConfig.builder().receiveTimeout(Duration.ZERO));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void noReceiveTimeoutClearsTimeout() {
		UdpServerConfig config = UdpServerConfig.builder().receiveTimeout(Duration.ofSeconds(5)).noReceiveTimeout().build();
		
		assertNull(config.peer().receiveTimeout());
	}
	
	@Test
	void onErrorWithNullClearsHandler() {
		UdpServerConfig config = UdpServerConfig.builder().onError((client, error) -> {}).onError(null).build();
		
		assertNull(config.onError());
	}
	
	@Test
	void receiveTimeoutSetsPeer() {
		UdpServerConfigBuilder builder = UdpServerConfig.builder();
		
		assertSame(builder, builder.receiveTimeout(Duration.ofSeconds(3)));
		assertEquals(Duration.ofSeconds(3), builder.build().peer().receiveTimeout());
	}
	
	@Test
	void maxDatagramSizeSetsPeer() {
		UdpServerConfigBuilder builder = UdpServerConfig.builder();
		
		assertSame(builder, builder.maxDatagramSize(512));
		assertEquals(512, builder.build().peer().maxDatagramSize());
	}
	
	@Test
	void broadcastSetsPeer() {
		UdpServerConfigBuilder builder = UdpServerConfig.builder();
		
		assertSame(builder, builder.broadcast(true));
		assertTrue(builder.build().peer().broadcast());
	}
	
	@Test
	void reuseAddressSetsPeer() {
		UdpServerConfigBuilder builder = UdpServerConfig.builder();
		
		assertSame(builder, builder.reuseAddress(true));
		assertTrue(builder.build().peer().reuseAddress());
	}
	
	@Test
	void executorStrategySetsValue() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(2);
		UdpServerConfigBuilder builder = UdpServerConfig.builder();
		
		assertSame(builder, builder.executorStrategy(strategy));
		assertSame(strategy, builder.build().executorStrategy());
	}
	
	@Test
	void onErrorSetsValue() {
		ErrorHandler handler = (client, error) -> {};
		UdpServerConfigBuilder builder = UdpServerConfig.builder();
		
		assertSame(builder, builder.onError(handler));
		assertSame(handler, builder.build().onError());
	}
	
	@Test
	void buildWithAllValues() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.cachedPool();
		ErrorHandler handler = (client, error) -> {};
		UdpServerConfig config = UdpServerConfig.builder()
			.receiveTimeout(Duration.ofSeconds(1))
			.maxDatagramSize(100)
			.broadcast(true)
			.reuseAddress(true)
			.executorStrategy(strategy)
			.onError(handler)
			.build();
		
		assertEquals(new UdpServerConfig(new UdpPeerConfig(Duration.ofSeconds(1), 100, true, true), strategy, handler), config);
	}
	
	@Test
	void builderReuseAfterBuild() {
		UdpServerConfigBuilder builder = UdpServerConfig.builder().maxDatagramSize(100);
		UdpServerConfig first = builder.build();
		
		builder.maxDatagramSize(200);
		UdpServerConfig second = builder.build();
		
		assertEquals(100, first.peer().maxDatagramSize());
		assertEquals(200, second.peer().maxDatagramSize());
	}
	
	@Test
	void methodChainingConsistency() {
		UdpServerConfigBuilder builder = UdpServerConfig.builder();
		
		assertSame(builder, builder.receiveTimeout(Duration.ofSeconds(1)));
		assertSame(builder, builder.noReceiveTimeout());
		assertSame(builder, builder.maxDatagramSize(10));
		assertSame(builder, builder.broadcast(false));
		assertSame(builder, builder.reuseAddress(false));
		assertSame(builder, builder.executorStrategy(ClientExecutorStrategy.virtualThreads()));
		assertSame(builder, builder.onError(null));
	}
}
