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
	void builderStartsWithDefaults() {
		assertEquals(TcpServerConfig.DEFAULT, TcpServerConfig.builder().build());
	}
	
	@Test
	void framingWithNull() {
		assertThrows(NullPointerException.class, () -> TcpServerConfig.builder().framing(null));
	}
	
	@Test
	void clientReadTimeoutWithNull() {
		assertThrows(NullPointerException.class, () -> TcpServerConfig.builder().clientReadTimeout(null));
	}
	
	@Test
	void executorStrategyWithNull() {
		assertThrows(NullPointerException.class, () -> TcpServerConfig.builder().executorStrategy(null));
	}
	
	@Test
	void buildWithInvalidBacklog() {
		TcpServerConfigBuilder builder = assertDoesNotThrow(() -> TcpServerConfig.builder().backlog(0));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithInvalidMaxMessageSize() {
		TcpServerConfigBuilder builder = assertDoesNotThrow(() -> TcpServerConfig.builder().maxMessageSize(0));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithZeroClientReadTimeout() {
		TcpServerConfigBuilder builder = assertDoesNotThrow(() -> TcpServerConfig.builder().clientReadTimeout(Duration.ZERO));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void noClientReadTimeoutClearsTimeout() {
		TcpServerConfig config = TcpServerConfig.builder().clientReadTimeout(Duration.ofSeconds(5)).noClientReadTimeout().build();
		
		assertNull(config.clientReadTimeout());
	}
	
	@Test
	void onErrorWithNullClearsHandler() {
		TcpServerConfig config = TcpServerConfig.builder().onError((client, error) -> {}).onError(null).build();
		
		assertNull(config.onError());
	}
	
	@Test
	void backlogSetsValue() {
		TcpServerConfigBuilder builder = TcpServerConfig.builder();
		
		assertSame(builder, builder.backlog(5));
		assertEquals(5, builder.build().backlog());
	}
	
	@Test
	void maxMessageSizeSetsValue() {
		TcpServerConfigBuilder builder = TcpServerConfig.builder();
		
		assertSame(builder, builder.maxMessageSize(256));
		assertEquals(256, builder.build().maxMessageSize());
	}
	
	@Test
	void framingSetsValue() {
		TcpServerConfigBuilder builder = TcpServerConfig.builder();
		
		assertSame(builder, builder.framing(Framing.lines()));
		assertEquals(Framing.lines(), builder.build().framing());
	}
	
	@Test
	void clientReadTimeoutSetsValue() {
		TcpServerConfigBuilder builder = TcpServerConfig.builder();
		
		assertSame(builder, builder.clientReadTimeout(Duration.ofSeconds(4)));
		assertEquals(Duration.ofSeconds(4), builder.build().clientReadTimeout());
	}
	
	@Test
	void tcpNoDelaySetsValue() {
		TcpServerConfigBuilder builder = TcpServerConfig.builder();
		
		assertSame(builder, builder.tcpNoDelay(false));
		assertFalse(builder.build().tcpNoDelay());
	}
	
	@Test
	void keepAliveSetsValue() {
		TcpServerConfigBuilder builder = TcpServerConfig.builder();
		
		assertSame(builder, builder.keepAlive(false));
		assertFalse(builder.build().keepAlive());
	}
	
	@Test
	void executorStrategySetsValue() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(2);
		TcpServerConfigBuilder builder = TcpServerConfig.builder();
		
		assertSame(builder, builder.executorStrategy(strategy));
		assertSame(strategy, builder.build().executorStrategy());
	}
	
	@Test
	void onErrorSetsValue() {
		ErrorHandler handler = (client, error) -> {};
		TcpServerConfigBuilder builder = TcpServerConfig.builder();
		
		assertSame(builder, builder.onError(handler));
		assertSame(handler, builder.build().onError());
	}
	
	@Test
	void buildWithAllValues() {
		ErrorHandler handler = (client, error) -> {};
		ClientExecutorStrategy strategy = ClientExecutorStrategy.cachedPool();
		TcpServerConfig config = TcpServerConfig.builder()
			.backlog(7)
			.maxMessageSize(100)
			.framing(Framing.none())
			.clientReadTimeout(Duration.ofSeconds(2))
			.tcpNoDelay(false)
			.keepAlive(false)
			.executorStrategy(strategy)
			.onError(handler)
			.build();
		
		assertEquals(new TcpServerConfig(7, 100, Framing.none(), Duration.ofSeconds(2), false, false, strategy, handler), config);
	}
	
	@Test
	void builderReuseAfterBuild() {
		TcpServerConfigBuilder builder = TcpServerConfig.builder().backlog(3);
		TcpServerConfig first = builder.build();
		
		builder.backlog(9);
		TcpServerConfig second = builder.build();
		
		assertEquals(3, first.backlog());
		assertEquals(9, second.backlog());
	}
	
	@Test
	void methodChainingConsistency() {
		TcpServerConfigBuilder builder = TcpServerConfig.builder();
		
		assertSame(builder, builder.backlog(1));
		assertSame(builder, builder.maxMessageSize(10));
		assertSame(builder, builder.framing(Framing.lines()));
		assertSame(builder, builder.clientReadTimeout(Duration.ofSeconds(1)));
		assertSame(builder, builder.noClientReadTimeout());
		assertSame(builder, builder.tcpNoDelay(true));
		assertSame(builder, builder.keepAlive(true));
		assertSame(builder, builder.executorStrategy(ClientExecutorStrategy.virtualThreads()));
		assertSame(builder, builder.onError(null));
	}
}
