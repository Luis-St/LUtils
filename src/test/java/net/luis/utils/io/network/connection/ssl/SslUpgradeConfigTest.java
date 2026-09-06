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

import net.luis.utils.io.network.connection.event.*;
import net.luis.utils.io.network.connection.tcp.TcpClientConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslUpgradeConfig}.<br>
 *
 * @author Luis-St
 */
class SslUpgradeConfigTest {
	
	private static SSLContext context;
	
	@BeforeAll
	static void setUp() throws Exception {
		context = SSLContext.getDefault();
	}
	
	@Test
	void constructWithAllValues() {
		SslUpgradeConfig config = new SslUpgradeConfig(context, List.of(TlsProtocol.TLS_V1_3), List.of("TLS_AES_256_GCM_SHA384"), false);
		
		assertSame(context, config.sslContext());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.enabledProtocols());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
		assertFalse(config.verifyHostname());
	}
	
	@Test
	void constructWithNullSslContextIsAllowed() {
		SslUpgradeConfig config = assertDoesNotThrow(() -> new SslUpgradeConfig(null, List.of(TlsProtocol.TLS_V1_3), List.of(), true));
		
		assertNull(config.sslContext());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.enabledProtocols());
		assertTrue(config.verifyHostname());
	}
	
	@Test
	void constructWithNullEnabledProtocolsThrows() {
		assertThrows(NullPointerException.class, () -> new SslUpgradeConfig(null, null, List.of(), true));
	}
	
	@Test
	void constructWithNullEnabledCipherSuitesThrows() {
		assertThrows(NullPointerException.class, () -> new SslUpgradeConfig(null, List.of(), null, true));
	}
	
	@Test
	void constructWithNullProtocolElementThrows() {
		List<TlsProtocol> protocols = new ArrayList<>();
		protocols.add(null);
		
		assertThrows(NullPointerException.class, () -> new SslUpgradeConfig(null, protocols, List.of(), true));
	}
	
	@Test
	void constructWithNullCipherSuiteElementThrows() {
		List<String> cipherSuites = new ArrayList<>();
		cipherSuites.add(null);
		
		assertThrows(NullPointerException.class, () -> new SslUpgradeConfig(null, List.of(), cipherSuites, true));
	}
	
	@Test
	void toClientConfigWithNullConfigThrows() {
		assertThrows(NullPointerException.class, () -> SslUpgradeConfig.DEFAULT.toClientConfig(null));
	}
	
	@Test
	void defaultConfigValues() {
		SslUpgradeConfig config = SslUpgradeConfig.DEFAULT;
		
		assertNull(config.sslContext());
		assertTrue(config.enabledProtocols().isEmpty());
		assertTrue(config.enabledCipherSuites().isEmpty());
		assertTrue(config.verifyHostname());
	}
	
	@Test
	void constructCopiesProtocolsDefensively() {
		List<TlsProtocol> protocols = new ArrayList<>(List.of(TlsProtocol.TLS_V1_3));
		SslUpgradeConfig config = new SslUpgradeConfig(null, protocols, List.of(), true);
		
		protocols.add(TlsProtocol.TLS_V1_2);
		
		assertEquals(1, config.enabledProtocols().size());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), config.enabledProtocols());
		assertThrows(UnsupportedOperationException.class, () -> config.enabledProtocols().add(TlsProtocol.TLS_V1_1));
	}
	
	@Test
	void constructCopiesCipherSuitesDefensively() {
		List<String> cipherSuites = new ArrayList<>(List.of("TLS_AES_256_GCM_SHA384"));
		SslUpgradeConfig config = new SslUpgradeConfig(null, List.of(), cipherSuites, true);
		
		cipherSuites.add("TLS_AES_128_GCM_SHA256");
		
		assertEquals(1, config.enabledCipherSuites().size());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), config.enabledCipherSuites());
		assertThrows(UnsupportedOperationException.class, () -> config.enabledCipherSuites().add("TLS_AES_128_GCM_SHA256"));
	}
	
	@Test
	void builderReturnsNewInstanceEachCall() {
		SslUpgradeConfigBuilder first = SslUpgradeConfig.builder();
		SslUpgradeConfigBuilder second = SslUpgradeConfig.builder();
		
		assertNotNull(first);
		assertNotNull(second);
		assertNotSame(first, second);
	}
	
	@Test
	void toClientConfigTakesTransportSettingsFromTcpConfig() {
		TcpClientConfig base = TcpClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(10))
			.readTimeout(Duration.ofSeconds(5))
			.writeTimeout(Duration.ofSeconds(15))
			.bufferSize(16384)
			.framing(false)
			.tcpNoDelay(false)
			.keepAlive(false)
			.build();
		
		SslClientConfig result = SslUpgradeConfig.DEFAULT.toClientConfig(base);
		
		assertEquals(Duration.ofSeconds(10), result.connectTimeout());
		assertEquals(Duration.ofSeconds(5), result.readTimeout());
		assertEquals(Duration.ofSeconds(15), result.writeTimeout());
		assertEquals(16384, result.bufferSize());
		assertFalse(result.framing());
		assertFalse(result.tcpNoDelay());
		assertFalse(result.keepAlive());
	}
	
	@Test
	void toClientConfigTakesTlsSettingsFromUpgradeConfig() {
		SslUpgradeConfig upgradeConfig = new SslUpgradeConfig(context, List.of(TlsProtocol.TLS_V1_3), List.of("TLS_AES_256_GCM_SHA384"), false);
		
		SslClientConfig result = upgradeConfig.toClientConfig(TcpClientConfig.DEFAULT);
		
		assertSame(context, result.sslContext());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), result.enabledProtocols());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), result.enabledCipherSuites());
		assertFalse(result.verifyHostname());
	}
	
	@Test
	void toClientConfigCarriesHandlers() {
		ConnectEventHandler onConnect = (connection, local, remote, timestamp) -> {};
		DisconnectEventHandler onDisconnect = (connection, local, remote, timestamp) -> {};
		ErrorEventHandler onError = (connection, errorType, message, cause) -> {};
		TcpClientConfig base = TcpClientConfig.builder().onConnect(onConnect).onDisconnect(onDisconnect).onError(onError).build();
		
		SslClientConfig result = SslUpgradeConfig.DEFAULT.toClientConfig(base);
		
		assertSame(onConnect, result.onConnect());
		assertSame(onDisconnect, result.onDisconnect());
		assertSame(onError, result.onError());
	}
	
	@Test
	void toClientConfigWithoutHandlers() {
		SslClientConfig result = SslUpgradeConfig.DEFAULT.toClientConfig(TcpClientConfig.DEFAULT);
		
		assertNull(result.onConnect());
		assertNull(result.onDisconnect());
		assertNull(result.onError());
	}
	
	@Test
	void toClientConfigCarriesFramingEnabled() {
		TcpClientConfig base = TcpClientConfig.builder().framing(true).build();
		
		assertTrue(SslUpgradeConfig.DEFAULT.toClientConfig(base).framing());
	}
	
	@Test
	void toClientConfigCarriesFramingDisabled() {
		TcpClientConfig base = TcpClientConfig.builder().framing(false).build();
		
		assertFalse(SslUpgradeConfig.DEFAULT.toClientConfig(base).framing());
	}
	
	@Test
	void toClientConfigCarriesVerifyHostnameEnabled() {
		SslUpgradeConfig upgradeConfig = new SslUpgradeConfig(null, List.of(), List.of(), true);
		
		assertTrue(upgradeConfig.toClientConfig(TcpClientConfig.DEFAULT).verifyHostname());
	}
	
	@Test
	void toClientConfigCarriesVerifyHostnameDisabled() {
		SslUpgradeConfig upgradeConfig = new SslUpgradeConfig(null, List.of(), List.of(), false);
		
		assertFalse(upgradeConfig.toClientConfig(TcpClientConfig.DEFAULT).verifyHostname());
	}
	
	@Test
	void toClientConfigOfDefaultsMatchesSslClientConfigDefault() {
		assertEquals(SslClientConfig.DEFAULT, SslUpgradeConfig.DEFAULT.toClientConfig(TcpClientConfig.DEFAULT));
	}
	
	@Test
	void toClientConfigResolvesConfiguredSslContext() throws Exception {
		SslUpgradeConfig upgradeConfig = new SslUpgradeConfig(context, List.of(), List.of(), true);
		
		SslClientConfig result = upgradeConfig.toClientConfig(TcpClientConfig.DEFAULT);
		
		assertSame(context, result.resolveSslContext());
	}
	
	@Test
	void toClientConfigResolvesDefaultSslContextWhenNull() throws Exception {
		SslClientConfig result = SslUpgradeConfig.DEFAULT.toClientConfig(TcpClientConfig.DEFAULT);
		
		assertNull(result.sslContext());
		assertNotNull(result.resolveSslContext());
	}
	
	@Test
	void toClientConfigReturnsNewInstanceEachCall() {
		SslClientConfig first = SslUpgradeConfig.DEFAULT.toClientConfig(TcpClientConfig.DEFAULT);
		SslClientConfig second = SslUpgradeConfig.DEFAULT.toClientConfig(TcpClientConfig.DEFAULT);
		
		assertEquals(first, second);
		assertNotSame(first, second);
	}
	
	@Test
	void toClientConfigKeepsListsImmutable() {
		SslUpgradeConfig upgradeConfig = new SslUpgradeConfig(null, List.of(TlsProtocol.TLS_V1_3), List.of("TLS_AES_256_GCM_SHA384"), true);
		
		SslClientConfig result = upgradeConfig.toClientConfig(TcpClientConfig.DEFAULT);
		
		assertEquals(List.of(TlsProtocol.TLS_V1_3), result.enabledProtocols());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), result.enabledCipherSuites());
		assertThrows(UnsupportedOperationException.class, () -> result.enabledProtocols().add(TlsProtocol.TLS_V1_2));
		assertThrows(UnsupportedOperationException.class, () -> result.enabledCipherSuites().add("TLS_AES_128_GCM_SHA256"));
	}
	
	@Test
	void toClientConfigIsIndependentOfSourceListMutation() {
		List<TlsProtocol> protocols = new ArrayList<>(List.of(TlsProtocol.TLS_V1_3));
		SslUpgradeConfig upgradeConfig = new SslUpgradeConfig(null, protocols, List.of(), true);
		
		protocols.add(TlsProtocol.TLS_V1_2);
		SslClientConfig result = upgradeConfig.toClientConfig(TcpClientConfig.DEFAULT);
		
		assertEquals(1, result.enabledProtocols().size());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), result.enabledProtocols());
	}
	
	@Test
	void toClientConfigAppliedToMultipleBaseConfigs() {
		SslUpgradeConfig upgradeConfig = new SslUpgradeConfig(context, List.of(TlsProtocol.TLS_V1_3), List.of(), false);
		TcpClientConfig first = TcpClientConfig.builder().framing(false).bufferSize(1024).build();
		TcpClientConfig second = TcpClientConfig.builder().framing(true).bufferSize(8192).build();
		
		SslClientConfig firstResult = upgradeConfig.toClientConfig(first);
		SslClientConfig secondResult = upgradeConfig.toClientConfig(second);
		
		assertFalse(firstResult.framing());
		assertEquals(1024, firstResult.bufferSize());
		assertTrue(secondResult.framing());
		assertEquals(8192, secondResult.bufferSize());
		assertSame(firstResult.sslContext(), secondResult.sslContext());
		assertEquals(firstResult.enabledProtocols(), secondResult.enabledProtocols());
	}
	
	@Test
	void toClientConfigWithFullyCustomisedInputs() {
		ConnectEventHandler onConnect = (connection, local, remote, timestamp) -> {};
		DisconnectEventHandler onDisconnect = (connection, local, remote, timestamp) -> {};
		ErrorEventHandler onError = (connection, errorType, message, cause) -> {};
		TcpClientConfig base = TcpClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(10))
			.readTimeout(Duration.ofSeconds(5))
			.writeTimeout(Duration.ofSeconds(15))
			.bufferSize(16384)
			.framing(false)
			.tcpNoDelay(false)
			.keepAlive(false)
			.onConnect(onConnect)
			.onDisconnect(onDisconnect)
			.onError(onError)
			.build();
		SslUpgradeConfig upgradeConfig = new SslUpgradeConfig(context, List.of(TlsProtocol.TLS_V1_3), List.of("TLS_AES_256_GCM_SHA384"), false);
		
		SslClientConfig result = upgradeConfig.toClientConfig(base);
		
		assertEquals(List.of(Duration.ofSeconds(10), Duration.ofSeconds(5), Duration.ofSeconds(15)), List.of(result.connectTimeout(), result.readTimeout(), result.writeTimeout()));
		assertEquals(16384, result.bufferSize());
		assertEquals(List.of(false, false, false), List.of(result.framing(), result.tcpNoDelay(), result.keepAlive()));
		assertSame(context, result.sslContext());
		assertEquals(List.of(TlsProtocol.TLS_V1_3), result.enabledProtocols());
		assertEquals(List.of("TLS_AES_256_GCM_SHA384"), result.enabledCipherSuites());
		assertFalse(result.verifyHostname());
		assertEquals(List.of(onConnect, onDisconnect, onError), List.of(result.onConnect(), result.onDisconnect(), result.onError()));
	}
}
