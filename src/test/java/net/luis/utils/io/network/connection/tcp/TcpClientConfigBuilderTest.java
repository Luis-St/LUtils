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
	void builderDefaultValues() {
		TcpClientConfig config = TcpClientConfig.builder().build();
		
		assertEquals(Duration.ofSeconds(30), config.connectTimeout());
		assertEquals(Duration.ZERO, config.readTimeout());
		assertEquals(Duration.ZERO, config.writeTimeout());
		assertEquals(8192, config.bufferSize());
		assertTrue(config.tcpNoDelay());
		assertTrue(config.keepAlive());
		assertNull(config.onConnect());
		assertNull(config.onDisconnect());
		assertNull(config.onError());
	}
	
	@Test
	void connectTimeoutWithValidDuration() {
		TcpClientConfig config = TcpClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(10))
			.build();
		assertEquals(Duration.ofSeconds(10), config.connectTimeout());
	}
	
	@Test
	void connectTimeoutWithNullThrows() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder();
		assertThrows(NullPointerException.class, () -> builder.connectTimeout(null));
	}
	
	@Test
	void readTimeoutWithValidDuration() {
		TcpClientConfig config = TcpClientConfig.builder()
			.readTimeout(Duration.ofSeconds(5))
			.build();
		assertEquals(Duration.ofSeconds(5), config.readTimeout());
	}
	
	@Test
	void readTimeoutWithNullThrows() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder();
		assertThrows(NullPointerException.class, () -> builder.readTimeout(null));
	}
	
	@Test
	void readTimeoutWithZero() {
		TcpClientConfig config = TcpClientConfig.builder()
			.readTimeout(Duration.ZERO)
			.build();
		assertEquals(Duration.ZERO, config.readTimeout());
	}
	
	@Test
	void writeTimeoutWithValidDuration() {
		TcpClientConfig config = TcpClientConfig.builder()
			.writeTimeout(Duration.ofSeconds(5))
			.build();
		assertEquals(Duration.ofSeconds(5), config.writeTimeout());
	}
	
	@Test
	void writeTimeoutWithNullThrows() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder();
		assertThrows(NullPointerException.class, () -> builder.writeTimeout(null));
	}
	
	@Test
	void writeTimeoutWithZero() {
		TcpClientConfig config = TcpClientConfig.builder()
			.writeTimeout(Duration.ZERO)
			.build();
		assertEquals(Duration.ZERO, config.writeTimeout());
	}
	
	@Test
	void bufferSizeWithValidValue() {
		TcpClientConfig config = TcpClientConfig.builder()
			.bufferSize(4096)
			.build();
		assertEquals(4096, config.bufferSize());
	}
	
	@Test
	void tcpNoDelayTrue() {
		TcpClientConfig config = TcpClientConfig.builder()
			.tcpNoDelay(true)
			.build();
		assertTrue(config.tcpNoDelay());
	}
	
	@Test
	void tcpNoDelayFalse() {
		TcpClientConfig config = TcpClientConfig.builder()
			.tcpNoDelay(false)
			.build();
		assertFalse(config.tcpNoDelay());
	}
	
	@Test
	void keepAliveTrue() {
		TcpClientConfig config = TcpClientConfig.builder()
			.keepAlive(true)
			.build();
		assertTrue(config.keepAlive());
	}
	
	@Test
	void keepAliveFalse() {
		TcpClientConfig config = TcpClientConfig.builder()
			.keepAlive(false)
			.build();
		assertFalse(config.keepAlive());
	}
	
	@Test
	void onConnectWithHandler() {
		TcpClientConfig config = TcpClientConfig.builder()
			.onConnect(event -> {})
			.build();
		assertNotNull(config.onConnect());
	}
	
	@Test
	void onConnectWithNull() {
		TcpClientConfig config = TcpClientConfig.builder()
			.onConnect(null)
			.build();
		assertNull(config.onConnect());
	}
	
	@Test
	void onDisconnectWithHandler() {
		TcpClientConfig config = TcpClientConfig.builder()
			.onDisconnect(event -> {})
			.build();
		assertNotNull(config.onDisconnect());
	}
	
	@Test
	void onDisconnectWithNull() {
		TcpClientConfig config = TcpClientConfig.builder()
			.onDisconnect(null)
			.build();
		assertNull(config.onDisconnect());
	}
	
	@Test
	void onErrorWithHandler() {
		TcpClientConfig config = TcpClientConfig.builder()
			.onError((type, msg, cause) -> {})
			.build();
		assertNotNull(config.onError());
	}
	
	@Test
	void onErrorWithNull() {
		TcpClientConfig config = TcpClientConfig.builder()
			.onError(null)
			.build();
		assertNull(config.onError());
	}
	
	@Test
	void methodChainingConsistency() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder();
		assertSame(builder, builder.connectTimeout(Duration.ofSeconds(10)));
		assertSame(builder, builder.readTimeout(Duration.ofSeconds(5)));
		assertSame(builder, builder.writeTimeout(Duration.ofSeconds(5)));
		assertSame(builder, builder.bufferSize(4096));
		assertSame(builder, builder.tcpNoDelay(true));
		assertSame(builder, builder.keepAlive(true));
		assertSame(builder, builder.onConnect(event -> {}));
		assertSame(builder, builder.onDisconnect(event -> {}));
		assertSame(builder, builder.onError((type, msg, cause) -> {}));
	}
	
	@Test
	void builderSetsAllValues() {
		TcpClientConfig config = TcpClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(15))
			.readTimeout(Duration.ofSeconds(10))
			.writeTimeout(Duration.ofSeconds(5))
			.bufferSize(16384)
			.tcpNoDelay(false)
			.keepAlive(false)
			.onConnect(event -> {})
			.onDisconnect(event -> {})
			.onError((type, msg, cause) -> {})
			.build();
		
		assertEquals(Duration.ofSeconds(15), config.connectTimeout());
		assertEquals(Duration.ofSeconds(10), config.readTimeout());
		assertEquals(Duration.ofSeconds(5), config.writeTimeout());
		assertEquals(16384, config.bufferSize());
		assertFalse(config.tcpNoDelay());
		assertFalse(config.keepAlive());
		assertNotNull(config.onConnect());
		assertNotNull(config.onDisconnect());
		assertNotNull(config.onError());
	}
	
	@Test
	void builderReuseAfterBuild() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(10));
		
		TcpClientConfig first = builder.build();
		assertEquals(Duration.ofSeconds(10), first.connectTimeout());
		
		builder.connectTimeout(Duration.ofSeconds(20));
		TcpClientConfig second = builder.build();
		assertEquals(Duration.ofSeconds(20), second.connectTimeout());
		
		assertEquals(Duration.ofSeconds(10), first.connectTimeout());
	}
	
	@Test
	void builderMultipleBuilds() {
		TcpClientConfigBuilder builder = TcpClientConfig.builder();
		
		TcpClientConfig config1 = builder.build();
		TcpClientConfig config2 = builder.build();
		
		assertEquals(config1, config2);
		assertNotSame(config1, config2);
	}
	
	@Test
	void builderOverwriteValues() {
		TcpClientConfig config = TcpClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(10))
			.connectTimeout(Duration.ofSeconds(20))
			.build();
		
		assertEquals(Duration.ofSeconds(20), config.connectTimeout());
	}
	
	@Test
	void buildMatchesDefaultConstant() {
		TcpClientConfig fromBuilder = TcpClientConfig.builder().build();
		TcpClientConfig defaultConfig = TcpClientConfig.DEFAULT;
		
		assertEquals(defaultConfig.connectTimeout(), fromBuilder.connectTimeout());
		assertEquals(defaultConfig.readTimeout(), fromBuilder.readTimeout());
		assertEquals(defaultConfig.writeTimeout(), fromBuilder.writeTimeout());
		assertEquals(defaultConfig.bufferSize(), fromBuilder.bufferSize());
		assertEquals(defaultConfig.tcpNoDelay(), fromBuilder.tcpNoDelay());
		assertEquals(defaultConfig.keepAlive(), fromBuilder.keepAlive());
		assertEquals(defaultConfig.onConnect(), fromBuilder.onConnect());
		assertEquals(defaultConfig.onDisconnect(), fromBuilder.onDisconnect());
		assertEquals(defaultConfig.onError(), fromBuilder.onError());
	}
}
