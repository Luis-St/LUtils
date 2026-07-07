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

package net.luis.utils.io.network.connection.ssl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslClientConfigBuilder}.<br>
 *
 * @author Luis-St
 */
class SslClientConfigBuilderTest {
	
	private static SSLContext context;
	
	@BeforeAll
	static void setUp() throws Exception {
		context = SSLContext.getDefault();
	}
	
	@Test
	void builderDefaultValues() {
		SslClientConfig config = SslClientConfig.builder().build();
		
		assertEquals(Duration.ofSeconds(30), config.connectTimeout());
		assertEquals(Duration.ZERO, config.readTimeout());
		assertEquals(Duration.ZERO, config.writeTimeout());
		assertEquals(8192, config.bufferSize());
		assertTrue(config.tcpNoDelay());
		assertTrue(config.keepAlive());
		assertNull(config.sslContext());
		assertTrue(config.enabledProtocols().isEmpty());
		assertTrue(config.enabledCipherSuites().isEmpty());
		assertTrue(config.verifyHostname());
		assertNull(config.onConnect());
		assertNull(config.onDisconnect());
		assertNull(config.onError());
	}
	
	@Test
	void connectTimeoutWithValidDuration() {
		SslClientConfig config = SslClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(10))
			.build();
		assertEquals(Duration.ofSeconds(10), config.connectTimeout());
	}
	
	@Test
	void connectTimeoutWithNullThrows() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		assertThrows(NullPointerException.class, () -> builder.connectTimeout(null));
	}
	
	@Test
	void readTimeoutWithValidDuration() {
		SslClientConfig config = SslClientConfig.builder()
			.readTimeout(Duration.ofSeconds(5))
			.build();
		assertEquals(Duration.ofSeconds(5), config.readTimeout());
	}
	
	@Test
	void readTimeoutWithNullThrows() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		assertThrows(NullPointerException.class, () -> builder.readTimeout(null));
	}
	
	@Test
	void writeTimeoutWithValidDuration() {
		SslClientConfig config = SslClientConfig.builder()
			.writeTimeout(Duration.ofSeconds(5))
			.build();
		assertEquals(Duration.ofSeconds(5), config.writeTimeout());
	}
	
	@Test
	void writeTimeoutWithNullThrows() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		assertThrows(NullPointerException.class, () -> builder.writeTimeout(null));
	}
	
	@Test
	void bufferSizeWithValidValue() {
		SslClientConfig config = SslClientConfig.builder()
			.bufferSize(4096)
			.build();
		assertEquals(4096, config.bufferSize());
	}
	
	@Test
	void tcpNoDelayFalse() {
		SslClientConfig config = SslClientConfig.builder()
			.tcpNoDelay(false)
			.build();
		assertFalse(config.tcpNoDelay());
	}
	
	@Test
	void keepAliveFalse() {
		SslClientConfig config = SslClientConfig.builder()
			.keepAlive(false)
			.build();
		assertFalse(config.keepAlive());
	}
	
	@Test
	void sslContextWithValue() {
		SslClientConfig config = SslClientConfig.builder()
			.sslContext(context)
			.build();
		assertSame(context, config.sslContext());
	}
	
	@Test
	void sslContextWithNull() {
		SslClientConfig config = SslClientConfig.builder()
			.sslContext(null)
			.build();
		assertNull(config.sslContext());
	}
	
	@Test
	void enabledProtocolsWithValue() {
		SslClientConfig config = SslClientConfig.builder()
			.enabledProtocols(List.of("TLSv1.3", "TLSv1.2"))
			.build();
		assertEquals(List.of("TLSv1.3", "TLSv1.2"), config.enabledProtocols());
	}
	
	@Test
	void enabledProtocolsWithNullThrows() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		assertThrows(NullPointerException.class, () -> builder.enabledProtocols(null));
	}
	
	@Test
	void enabledCipherSuitesWithValue() {
		SslClientConfig config = SslClientConfig.builder()
			.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384"))
			.build();
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
	}
	
	@Test
	void enabledCipherSuitesWithNullThrows() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		assertThrows(NullPointerException.class, () -> builder.enabledCipherSuites(null));
	}
	
	@Test
	void verifyHostnameTrue() {
		SslClientConfig config = SslClientConfig.builder()
			.verifyHostname(true)
			.build();
		assertTrue(config.verifyHostname());
	}
	
	@Test
	void verifyHostnameFalse() {
		SslClientConfig config = SslClientConfig.builder()
			.verifyHostname(false)
			.build();
		assertFalse(config.verifyHostname());
	}
	
	@Test
	void onConnectWithHandler() {
		SslClientConfig config = SslClientConfig.builder()
			.onConnect(event -> {})
			.build();
		assertNotNull(config.onConnect());
	}
	
	@Test
	void onConnectWithNull() {
		SslClientConfig config = SslClientConfig.builder()
			.onConnect(null)
			.build();
		assertNull(config.onConnect());
	}
	
	@Test
	void onDisconnectWithHandler() {
		SslClientConfig config = SslClientConfig.builder()
			.onDisconnect(event -> {})
			.build();
		assertNotNull(config.onDisconnect());
	}
	
	@Test
	void onErrorWithHandler() {
		SslClientConfig config = SslClientConfig.builder()
			.onError((type, msg, cause) -> {})
			.build();
		assertNotNull(config.onError());
	}
	
	@Test
	void methodChainingConsistency() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		assertSame(builder, builder.connectTimeout(Duration.ofSeconds(10)));
		assertSame(builder, builder.readTimeout(Duration.ofSeconds(5)));
		assertSame(builder, builder.writeTimeout(Duration.ofSeconds(5)));
		assertSame(builder, builder.bufferSize(4096));
		assertSame(builder, builder.tcpNoDelay(true));
		assertSame(builder, builder.keepAlive(true));
		assertSame(builder, builder.sslContext(context));
		assertSame(builder, builder.enabledProtocols(List.of("TLSv1.3")));
		assertSame(builder, builder.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384")));
		assertSame(builder, builder.verifyHostname(true));
		assertSame(builder, builder.onConnect(event -> {}));
		assertSame(builder, builder.onDisconnect(event -> {}));
		assertSame(builder, builder.onError((type, msg, cause) -> {}));
	}
	
	@Test
	void builderSetsAllValues() {
		SslClientConfig config = SslClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(15))
			.readTimeout(Duration.ofSeconds(10))
			.writeTimeout(Duration.ofSeconds(5))
			.bufferSize(16384)
			.tcpNoDelay(false)
			.keepAlive(false)
			.sslContext(context)
			.enabledProtocols(List.of("TLSv1.3"))
			.enabledCipherSuites(List.of("TLS_AES_256_GCM_SHA384"))
			.verifyHostname(false)
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
		assertSame(context, config.sslContext());
		assertEquals(List.of("TLSv1.3"), config.enabledProtocols());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
		assertFalse(config.verifyHostname());
		assertNotNull(config.onConnect());
		assertNotNull(config.onDisconnect());
		assertNotNull(config.onError());
	}
	
	@Test
	void builderReuseAfterBuild() {
		SslClientConfigBuilder builder = SslClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(10));
		
		SslClientConfig first = builder.build();
		assertEquals(Duration.ofSeconds(10), first.connectTimeout());
		
		builder.connectTimeout(Duration.ofSeconds(20));
		SslClientConfig second = builder.build();
		assertEquals(Duration.ofSeconds(20), second.connectTimeout());
		
		assertEquals(Duration.ofSeconds(10), first.connectTimeout());
	}
	
	@Test
	void builderMultipleBuilds() {
		SslClientConfigBuilder builder = SslClientConfig.builder();
		
		SslClientConfig config1 = builder.build();
		SslClientConfig config2 = builder.build();
		
		assertEquals(config1, config2);
		assertNotSame(config1, config2);
	}
	
	@Test
	void builderOverwriteValues() {
		SslClientConfig config = SslClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(10))
			.connectTimeout(Duration.ofSeconds(20))
			.build();
		
		assertEquals(Duration.ofSeconds(20), config.connectTimeout());
	}
	
	@Test
	void buildMatchesDefaultConstant() {
		SslClientConfig fromBuilder = SslClientConfig.builder().build();
		SslClientConfig defaultConfig = SslClientConfig.DEFAULT;
		
		assertEquals(defaultConfig.connectTimeout(), fromBuilder.connectTimeout());
		assertEquals(defaultConfig.readTimeout(), fromBuilder.readTimeout());
		assertEquals(defaultConfig.writeTimeout(), fromBuilder.writeTimeout());
		assertEquals(defaultConfig.bufferSize(), fromBuilder.bufferSize());
		assertEquals(defaultConfig.tcpNoDelay(), fromBuilder.tcpNoDelay());
		assertEquals(defaultConfig.keepAlive(), fromBuilder.keepAlive());
		assertEquals(defaultConfig.sslContext(), fromBuilder.sslContext());
		assertEquals(defaultConfig.enabledProtocols(), fromBuilder.enabledProtocols());
		assertEquals(defaultConfig.enabledCipherSuites(), fromBuilder.enabledCipherSuites());
		assertEquals(defaultConfig.verifyHostname(), fromBuilder.verifyHostname());
		assertEquals(defaultConfig.onConnect(), fromBuilder.onConnect());
		assertEquals(defaultConfig.onDisconnect(), fromBuilder.onDisconnect());
		assertEquals(defaultConfig.onError(), fromBuilder.onError());
	}
}
