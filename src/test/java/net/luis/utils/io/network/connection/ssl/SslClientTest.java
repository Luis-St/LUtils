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

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.Test;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslClient}.<br>
 *
 * @author Luis-St
 */
class SslClientTest {
	
	private static final IpEndpoint ENDPOINT = new IpEndpoint(Ipv4Address.LOOPBACK, 8443);
	
	@Test
	void upgradeWithNullSocketThrows() {
		assertThrows(NullPointerException.class, () -> SslClient.upgrade(null, ENDPOINT, SslClientConfig.DEFAULT));
	}
	
	@Test
	void upgradeWithNullEndpointThrows() throws Exception {
		try (Socket socket = new Socket()) {
			assertThrows(NullPointerException.class, () -> SslClient.upgrade(socket, null, SslClientConfig.DEFAULT));
		}
	}
	
	@Test
	void upgradeWithNullConfigThrows() throws Exception {
		try (Socket socket = new Socket()) {
			assertThrows(NullPointerException.class, () -> SslClient.upgrade(socket, ENDPOINT, null));
		}
	}
	
	@Test
	void upgradeUnconnectedSocketThrows() throws Exception {
		try (Socket socket = new Socket()) {
			assertFalse(socket.isConnected());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, ENDPOINT, SslClientConfig.DEFAULT));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
			assertEquals(ENDPOINT, exception.endpoint());
		}
	}
	
	@Test
	void upgradeClosedSocketThrows() throws Exception {
		Socket socket = new Socket();
		socket.close();
		assertTrue(socket.isClosed());
		
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, ENDPOINT, SslClientConfig.DEFAULT));
		assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
	}
}
