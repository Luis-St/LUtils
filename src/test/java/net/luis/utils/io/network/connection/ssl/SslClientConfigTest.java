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

import net.luis.utils.io.network.connection.tcp.TcpClientConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslClientConfig}.<br>
 *
 * @author Luis-St
 */
class SslClientConfigTest {
	
	@Test
	void constructWithBothParts() {
		TcpClientConfig transport = TcpClientConfig.builder().maxMessageSize(100).build();
		SslUpgradeConfig tls = SslUpgradeConfig.builder().verifyHostname(false).build();
		
		SslClientConfig config = new SslClientConfig(transport, tls);
		
		assertSame(transport, config.transport());
		assertSame(tls, config.tls());
	}
	
	@Test
	void constructWithNullTransport() {
		assertThrows(NullPointerException.class, () -> new SslClientConfig(null, SslUpgradeConfig.DEFAULT));
	}
	
	@Test
	void constructWithNullTls() {
		assertThrows(NullPointerException.class, () -> new SslClientConfig(TcpClientConfig.DEFAULT, null));
	}
	
	@Test
	void defaultValues() {
		assertSame(TcpClientConfig.DEFAULT, SslClientConfig.DEFAULT.transport());
		assertSame(SslUpgradeConfig.DEFAULT, SslClientConfig.DEFAULT.tls());
	}
	
	@Test
	void builderReturnsNewInstanceEachCall() {
		assertNotSame(SslClientConfig.builder(), SslClientConfig.builder());
	}
	
	@Test
	void builderWithoutChangesBuildsDefault() {
		assertEquals(SslClientConfig.DEFAULT, SslClientConfig.builder().build());
	}
	
	@Test
	void equalityAndHashCode() {
		SslClientConfig first = new SslClientConfig(TcpClientConfig.builder().maxMessageSize(100).build(), new SslUpgradeConfig(null, List.of(TlsProtocol.TLS_V1_3), List.of(), true));
		SslClientConfig second = new SslClientConfig(TcpClientConfig.builder().maxMessageSize(100).build(), new SslUpgradeConfig(null, List.of(TlsProtocol.TLS_V1_3), List.of(), true));
		SslClientConfig otherTransport = new SslClientConfig(TcpClientConfig.builder().maxMessageSize(200).build(), new SslUpgradeConfig(null, List.of(TlsProtocol.TLS_V1_3), List.of(), true));
		SslClientConfig otherTls = new SslClientConfig(TcpClientConfig.builder().maxMessageSize(100).build(), new SslUpgradeConfig(null, List.of(TlsProtocol.TLS_V1_3), List.of(), false));
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, otherTransport);
		assertNotEquals(first, otherTls);
	}
}
