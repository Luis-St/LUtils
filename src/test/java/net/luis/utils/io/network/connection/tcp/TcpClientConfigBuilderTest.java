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

import net.luis.utils.io.network.connection.Framing;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TcpClientConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class TcpClientConfigBuilderTest {
	
	@Test
	void builderStartsWithDefaults() {
		assertEquals(TcpClientConfig.DEFAULT, TcpClientConfig.builder().build());
	}
	
	@Test
	void connectTimeoutWithNull() {
		assertThrows(NullPointerException.class, () -> TcpClientConfig.builder().connectTimeout(null));
	}
	
	@Test
	void readTimeoutWithNull() {
		assertThrows(NullPointerException.class, () -> TcpClientConfig.builder().readTimeout(null));
		assertNull(TcpClientConfig.builder().readTimeout(Duration.ofSeconds(1)).noReadTimeout().build().readTimeout());
	}
	
	@Test
	void framingWithNull() {
		assertThrows(NullPointerException.class, () -> TcpClientConfig.builder().framing(null));
	}
	
	@Test
	void buildWithInvalidMaxMessageSize() {
		TcpClientConfigBuilder builder = assertDoesNotThrow(() -> TcpClientConfig.builder().maxMessageSize(0));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithZeroConnectTimeout() {
		TcpClientConfigBuilder builder = assertDoesNotThrow(() -> TcpClientConfig.builder().connectTimeout(Duration.ZERO));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithNegativeReadTimeout() {
		TcpClientConfigBuilder builder = assertDoesNotThrow(() -> TcpClientConfig.builder().readTimeout(Duration.ofSeconds(-1)));
		
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void noReadTimeoutClearsTimeout() {
		TcpClientConfig config = TcpClientConfig.builder().readTimeout(Duration.ofSeconds(5)).noReadTimeout().build();
		
		assertNull(config.readTimeout());
	}
	
	@Test
	void readTimeoutAfterNoReadTimeout() {
		TcpClientConfig config = TcpClientConfig.builder().noReadTimeout().readTimeout(Duration.ofSeconds(3)).build();
		
		assertEquals(Duration.ofSeconds(3), config.readTimeout());
	}
	
	@Test
	void connectTimeoutSetsValue() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder();
		
		assertSame(builder, builder.connectTimeout(Duration.ofSeconds(2)));
		assertEquals(Duration.ofSeconds(2), builder.build().connectTimeout());
	}
	
	@Test
	void readTimeoutSetsValue() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder();
		
		assertSame(builder, builder.readTimeout(Duration.ofSeconds(7)));
		assertEquals(Duration.ofSeconds(7), builder.build().readTimeout());
	}
	
	@Test
	void maxMessageSizeSetsValue() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder();
		
		assertSame(builder, builder.maxMessageSize(256));
		assertEquals(256, builder.build().maxMessageSize());
	}
	
	@Test
	void framingSetsValue() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder();
		
		assertSame(builder, builder.framing(Framing.lines()));
		assertEquals(Framing.lines(), builder.build().framing());
	}
	
	@Test
	void tcpNoDelaySetsValue() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder();
		
		assertSame(builder, builder.tcpNoDelay(false));
		assertFalse(builder.build().tcpNoDelay());
	}
	
	@Test
	void keepAliveSetsValue() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder();
		
		assertSame(builder, builder.keepAlive(false));
		assertFalse(builder.build().keepAlive());
	}
	
	@Test
	void buildWithAllValues() {
		TcpClientConfig config = TcpClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(1))
			.readTimeout(Duration.ofSeconds(2))
			.maxMessageSize(100)
			.framing(Framing.none())
			.tcpNoDelay(false)
			.keepAlive(false)
			.build();
		
		assertEquals(new TcpClientConfig(Duration.ofSeconds(1), Duration.ofSeconds(2), 100, Framing.none(), false, false), config);
	}
	
	@Test
	void builderReuseAfterBuild() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder().maxMessageSize(100);
		TcpClientConfig first = builder.build();
		
		builder.maxMessageSize(200);
		TcpClientConfig second = builder.build();
		
		assertEquals(100, first.maxMessageSize());
		assertEquals(200, second.maxMessageSize());
	}
	
	@Test
	void methodChainingConsistency() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder();
		
		assertSame(builder, builder.connectTimeout(Duration.ofSeconds(1)));
		assertSame(builder, builder.readTimeout(Duration.ofSeconds(1)));
		assertSame(builder, builder.noReadTimeout());
		assertSame(builder, builder.maxMessageSize(10));
		assertSame(builder, builder.framing(Framing.lines()));
		assertSame(builder, builder.tcpNoDelay(true));
		assertSame(builder, builder.keepAlive(true));
	}
}
